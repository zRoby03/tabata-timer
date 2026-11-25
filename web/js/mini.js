import { t, translateDocument } from './i18n.js';
import { APPEARANCE_KEY, applyAppearance, loadAppearance } from './appearance.js';
import { loadMode, MODES } from './modes.js';
import { formatTime, STUDIO_MIN_SECONDS, WORKOUT_MIN_SECONDS } from './model.js';
import { newQuickWorkout } from './quick.js';
import { TimerEngine, TimerCuePlayback, TimerSignals } from './timer.js';
import { acquireTimer, releaseTimer } from './timer-owner.js';
import { LocalRepository, updateArchive } from './storage.js';
import { parseBackup } from './io.js';

const PREFS = 'tabata:mini-preferences';
const MAX_ROUNDS = 20;
const root = document.querySelector('.mini');
const readout = root.querySelector('.mini-readout');
const phase = root.querySelector('.mini-phase');
const modeButtons = [...root.querySelectorAll('[data-mode]')];
const phaseButtons = [...root.querySelectorAll('[data-kind]')];
const actions = Object.fromEntries([...root.querySelectorAll('[data-action]')].map(button => [button.dataset.action, button]));
const time = root.querySelector('.mini-time');
const before = document.querySelector('#mini-before'), after = document.querySelector('#mini-after');
const status = document.querySelector('#mini-status'), roundsText = document.querySelector('#mini-rounds');
const message = document.querySelector('#mini-message');
let storage;
try { storage = localStorage; } catch { /* Temporary controls still work. */ }
const repo = new LocalRepository(storage, announce);
repo.load(parseBackup);
const signals = new TimerSignals(), playback = new TimerCuePlayback(signals);
let timer = null, starting = false, ticker = null, settled = null;
const presets = { WORKOUT: { focus: 20, break: 10, rounds: 8 }, STUDIO: { focus: 1500, break: 300, rounds: 4 } };
function normalize(prefs) {
  const values = {};
  for (const [mode, defaults] of Object.entries(presets)) {
    const candidate = prefs?.values?.[mode];
    const { minimum, step } = limits(mode);
    const duration = value => Number.isInteger(value) && value >= minimum && value <= 3600 && value % step === 0;
    values[mode] = {
      focus: duration(candidate?.focus) ? candidate.focus : defaults.focus,
      break: duration(candidate?.break) ? candidate.break : defaults.break,
      rounds: Number.isInteger(candidate?.rounds) && candidate.rounds >= 1 && candidate.rounds <= MAX_ROUNDS ? candidate.rounds : defaults.rounds
    };
  }
  return { mode: Object.hasOwn(presets, prefs?.mode) ? prefs.mode : loadMode(storage), kind: prefs?.kind === 'break' ? 'break' : 'focus', values };
}
function limits(mode) {
  return mode === 'STUDIO' ? { minimum: STUDIO_MIN_SECONDS, step: STUDIO_MIN_SECONDS } : { minimum: WORKOUT_MIN_SECONDS, step: 1 };
}
let preferences;
try { preferences = normalize(JSON.parse(storage?.getItem(PREFS) || 'null')); }
catch { preferences = normalize(null); }
function remember() {
  try { storage?.setItem(PREFS, JSON.stringify(preferences)); } catch { /* Temporary controls still work. */ }
}
function appearance() { applyAppearance({ ...loadAppearance(storage), theme: 'dark' }); }
function announce(text) { message.textContent = t(text); message.hidden = false; }
function clearMessage() { message.hidden = true; message.textContent = ''; }
function active() { return timer && ['running', 'paused'].includes(timer.state); }
function makeWorkout() {
  const { mode, values } = preferences, setup = values[mode];
  const workout = newQuickWorkout(mode);
  workout.title = t(preferences.kind === 'break' ? 'Pausa singola' : 'Timer rapido');
  if (mode === 'STUDIO') Object.assign(workout.activities[0], { concentrationSec: setup.focus, pauseSec: setup.break, sessions: setup.rounds });
  else Object.assign(workout.blocks[0].exercises[0], { workSec: setup.focus, restBetweenRepsSec: setup.break, repetitions: setup.rounds });
  return workout;
}
function syncLayout() {
  const origin = readout.getBoundingClientRect().left;
  const bounds = phase.getBoundingClientRect();
  const center = bounds.left - origin + bounds.width / 2;
  const marks = phaseButtons.map((button, i) => {
    const character = i === 0 ? 0 : button.firstChild.length - 1;
    const range = document.createRange();
    range.setStart(button.firstChild, character);
    range.setEnd(button.firstChild, character + 1);
    const letter = range.getBoundingClientRect();
    return letter.left - origin + letter.width / 2;
  });
  const room = Math.min(center - marks[0], marks[1] - center) * 2 - actions.less.offsetWidth - 6;
  readout.style.setProperty('--mini-center', center + 'px');
  readout.style.setProperty('--mini-minus', marks[0] + 'px');
  readout.style.setProperty('--mini-plus', marks[1] + 'px');
  readout.style.setProperty('--mini-clock-room', Math.max(0, room) + 'px');
}
function render() {
  const locked = active() || starting, paused = timer?.state === 'paused';
  const running = timer?.state === 'running', setup = preferences.values[preferences.mode];
  const stage = active() ? timer.currentPhase : null;
  const kind = stage ? (stage.type.startsWith('REST') ? 'break' : 'focus') : preferences.kind;
  const duration = setup[preferences.kind], clock = formatTime(active() ? timer.remainingMs : duration * 1000);
  const cut = clock.lastIndexOf(':');
  before.textContent = clock.slice(0, cut);
  after.textContent = clock.slice(cut + 1);
  time.dataset.long = String(clock.length > 5);
  root.dataset.paused = String(paused);
  actions.stop.hidden = !paused;
  actions.play.disabled = starting;
  actions.play.dataset.running = String(running);
  actions.play.setAttribute('aria-label', t(running ? 'Pausa (timer)' : paused ? 'Riprendi' : 'Avvia'));
  const label = paused ? 'In pausa' : starting ? 'Avvio del timer…' : !stage ? ''
    : stage.type === 'PREPARE' ? 'Preparazione' : kind === 'focus' ? MODES[preferences.mode].focus : MODES[preferences.mode].rest;
  if (status.textContent !== t(label)) status.textContent = t(label);
  const progress = active() && !timer.standaloneBreak ? `${timer.completed.size + (stage?.type === 'WORK' ? 1 : 0)}/${timer.workIntervals}` : String(setup.rounds);
  if (roundsText.textContent !== progress) roundsText.textContent = progress;
  roundsText.dataset.long = String(progress.length > 4);
  for (const button of modeButtons) {
    button.disabled = locked;
    button.setAttribute('aria-pressed', String(button.dataset.mode === preferences.mode));
  }
  for (const button of phaseButtons) {
    button.disabled = locked;
    button.setAttribute('aria-pressed', String(button.dataset.kind === kind));
  }
  actions.less.disabled = locked || duration === limits(preferences.mode).minimum;
  actions.more.disabled = locked || duration === 3600;
  actions['round-less'].disabled = locked || setup.rounds === 1;
  actions['round-more'].disabled = locked || setup.rounds === MAX_ROUNDS;
}

function tick() {
  if (!timer) return;
  timer.tick();
  playback.update(timer);
  if (timer.result && timer !== settled) {
    const finished = timer;
    settled = finished;
    clearInterval(ticker);
    announce(finished.result.status === 'COMPLETED' ? 'Sessione completata' : 'Sessione interrotta.');
    const record = finished.standaloneBreak ? Promise.resolve() : updateArchive(repo, parseBackup, () => repo.addSession(finished.result));
    record.catch(error => announce(error.message)).finally(() => {
      if (timer === finished) releaseTimer(finished.manualStop ? 0 : 2000);
    });
  }
  render();
}
async function play() {
  if (starting) return;
  clearMessage();
  if (active()) {
    if (timer.state === 'running') {
      timer.pause();
      clearInterval(ticker);
    } else {
      await signals.unlock();
      timer.resume();
      ticker = setInterval(tick, 100);
    }
    tick();
    return;
  }
  const workout = makeWorkout();
  const breakSeconds = preferences.kind === 'break' ? preferences.values[preferences.mode].break : null;
  const next = new TimerEngine(workout, undefined, undefined, breakSeconds);
  starting = true;
  render();
  const audioReady = signals.unlock();
  try {
    if (!await acquireTimer()) { announce('Timer aperto in un’altra finestra.'); return; }
    if (!await audioReady) announce('Suoni non disponibili in questo browser. Il timer continua senza audio.');
    signals.stop();
    timer = next;
    timer.start();
    ticker = setInterval(tick, 100);
    tick();
  } finally { starting = false; render(); }
}

root.addEventListener('click', event => {
  const button = event.target.closest('button');
  if (!button || button.disabled) return;
  const { action, mode, kind } = button.dataset;
  if (action === 'play') { play().catch(error => announce(error.message)); return; }
  if (action === 'stop') {
    if (timer?.state === 'paused') { timer.stop(); tick(); actions.play.focus(); }
    return;
  }
  if (active() || starting) return;
  clearMessage();
  if (mode) preferences.mode = mode;
  else if (kind) preferences.kind = kind;
  else {
    const setup = preferences.values[preferences.mode], { minimum, step } = limits(preferences.mode);
    if (action === 'round-more' || action === 'round-less') setup.rounds = Math.min(MAX_ROUNDS, Math.max(1, setup.rounds + (action === 'round-more' ? 1 : -1)));
    else if (action === 'more' || action === 'less') setup[preferences.kind] = Math.min(3600, Math.max(minimum, setup[preferences.kind] + (action === 'more' ? step : -step)));
  }
  remember();
  render();
});
window.addEventListener('storage', event => { if (event.key === APPEARANCE_KEY) appearance(); });
window.addEventListener('pagehide', event => {
  if (event.persisted && active()) timer.stop();
  signals.stop();
  releaseTimer();
});
document.addEventListener('visibilitychange', () => { if (active()) tick(); });
appearance();
translateDocument();
render();
const observer = new ResizeObserver(syncLayout);
observer.observe(phase);
observer.observe(readout);
document.fonts.ready.then(syncLayout);
if ('serviceWorker' in navigator) navigator.serviceWorker.register('./sw.js').catch(() => {});
