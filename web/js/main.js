import { t, phaseLabel, setLanguage, translateDocument } from './i18n.js';
import { MODES, isStudio, modeData, loadMode, saveMode } from './modes.js';
import { loadAppearance, saveAppearance, applyAppearance } from './appearance.js';
import { prepareWorkoutEdit, validateDurationInputs, normalizationNotice, newStudyActivity, newWorkout, newBlock, newExercise, duplicateWorkout, presets, clone, uid, formatTime } from './model.js';
import { compileEditedTimeline, plannedMs } from './timeline.js';
import { LocalRepository, STORAGE_KEY, updateArchive } from './storage.js';
import { editorView, preview } from './editor.js';
import { TimerEngine, TimerSignals, TimerCuePlayback } from './timer.js';
import { acquireTimer, releaseTimer } from './timer-owner.js';
import { completion } from './stats.js';
import { workoutsView, historyView, statsView, settingsView } from './views.js';
import { parseBackup, makeBackup, sessionsCsv, download, MAX_BACKUP_BYTES } from './io.js';
import { escapeHtml as h, dateLabel } from './dom.js';
import { newQuickWorkout, quickView, quickPresets } from './quick.js';
import { helpView } from './help.js';

const main = document.querySelector('main'), notice = document.querySelector('#notice');
function announce(message) { notice.textContent = t(message); notice.hidden = false; }
let browserStorage; try { browserStorage = window.localStorage; } catch { announce(t("Salvataggio browser non disponibile. Modalità temporanea attiva: esporta un backup prima di chiudere.")); }
let appearance = loadAppearance(browserStorage, announce); applyAppearance(appearance);
const repo = new LocalRepository(browserStorage, announce); repo.load(parseBackup);
if (!repo.data.workouts.length && !repo.persistent) repo.data.workouts = presets();
let route = 'home', draft = null, draftNotice = '', dirty = false, timer = null, savedSession = null, pendingImport = null, filterDays = 30, filterWorkout = '', pendingSave = null;
const signals = new TimerSignals();
const playback = new TimerCuePlayback(signals);
const soundCountdown = () => playback.update(timer);
const writeArchive = update => updateArchive(repo, parseBackup, update);
let starting = false;
const cleanUrl = new URL(location.href);
let mode = cleanUrl.searchParams.get('mode')?.toUpperCase();
if (cleanUrl.searchParams.has('v') || cleanUrl.searchParams.has('mode')) {
  cleanUrl.searchParams.delete('v'); cleanUrl.searchParams.delete('mode');
  history.replaceState(null, '', cleanUrl);
}
if (!Object.hasOwn(MODES, mode)) mode = loadMode(browserStorage);
else saveMode(browserStorage, mode);
const quickByMode = { WORKOUT: newQuickWorkout(), STUDIO: newQuickWorkout('STUDIO') };
let quick = quickByMode[mode];
const labels = () => MODES[mode];
const active = () => timer && ['running', 'paused'].includes(timer.state);

function navigate(next) {
  if (next === 'home' && active()) next = 'timer';
  if (route === 'editor' && next !== 'editor' && dirty) {
    if (!confirm(t("Uscire e scartare le modifiche non salvate?"))) return;
    dirty = false; draft = null;
  }
  if (next === 'editor' && !draft) draft = newWorkout(mode);
  route = next; render(); window.scrollTo(0, 0); main.focus({ preventScroll: true });
}
function render() {
  translateDocument();
  const archive = modeData(repo.data, mode);
  document.title = "Tabata Timer";
  document.body.dataset.mode = mode.toLowerCase();
  const modeSwitch = document.querySelector('#mode-switch');
  modeSwitch.setAttribute('aria-checked', String(mode === 'STUDIO'));
  modeSwitch.disabled = active();
  document.querySelector('[data-route=workouts]').textContent = t(labels().collection);
  document.querySelector('#save-title').textContent = t("Salva {0}", t(labels().noun));
  document.querySelector('#save-name-label').textContent = mode === 'STUDIO' ? t("Nome del programma") : t("Nome dell’allenamento");
  document.querySelector('#save-description').textContent = t("Lo ritroverai in {0} anche dopo aver chiuso il browser. Il salvataggio vale per questo browser e questo indirizzo.", t(labels().collection));
  const tab = ['home', 'timer'].includes(route) ? 'home' : ['editor', 'workouts'].includes(route) ? 'workouts' : ['history', 'stats'].includes(route) ? 'history' : '';
  document.querySelectorAll('[data-route]').forEach(b => b.setAttribute('aria-current', b.dataset.route === tab ? 'page' : 'false'));
  document.body.dataset.page = route;
  main.innerHTML = route === 'home' ? quickView(quick, repo.persistent) : route === 'workouts' ? workoutsView(archive, repo.persistent, mode) : route === 'editor' ? editorView(draft, repo.data.workouts.some(w => w.id === draft.id), draftNotice) : route === 'timer' ? timerView() : route === 'history' ? historyView(archive.sessions, repo.persistent, mode) : route === 'stats' ? statsView(archive, filterDays, filterWorkout, mode) : route === 'help' ? helpView(mode) : settingsView(repo.persistent, appearance);
  if (route === 'editor') updateEditorSave();
  if (route === 'timer') paintTimer();
}
function updateEditorSave() {
  const form = main.querySelector('#workout-form');
  form.querySelector('[type="submit"]').disabled = form.matches(':invalid');
}
function timerView() {
  if (!timer) return quickView(quick, repo.persistent);
  return `<section class="card timer-panel"><p class="timer-workout-name">${h(timer.workout.title)}</p><h1 id="timer-status">${t("Pronto")}</h1><div class="timer-display"><div class="timer-digits" id="timer-digits" role="timer" aria-label="${t("Tempo rimanente")}" aria-live="off">0:00</div></div><div class="timer-phase" id="timer-phase" aria-live="polite"></div><progress id="timer-progress" class="timer-progress" max="${timer.phases.length}" value="0" aria-label="${t("Avanzamento fasi")}"></progress><p id="timer-position" class="muted"></p><div class="row timer-main-controls"><button class="primary" data-action="pause" id="pause-button">${t("Pausa (timer)")}</button><button data-action="skip">${t("Salta →")}</button></div><div class="timer-next" id="timer-next"></div><details class="timer-options"><summary>${t("Altri controlli")}</summary><div class="row"><button data-action="previous">${t("← Precedente")}</button><button data-action="fullscreen">${t("Schermo intero")}</button></div><div class="timer-settings"><label class="check"><input type="checkbox" id="sound" ${signals.sound ? 'checked' : ''}>${t("Suoni")}</label><label class="check"><input type="checkbox" id="vibration" ${signals.vibration ? 'checked' : ''}>${t("Vibrazione")}</label><label class="check"><input type="checkbox" id="awake" ${signals.keepAwake ? 'checked' : ''}>${t("Schermo acceso")}</label></div><p class="timer-warning">${t(isStudio(timer.workout) ? "Bip brevi sul 3, 2, 1. Fischietto all’inizio del lavoro, sospiro all’inizio del recupero e tre ding alla fine dell’allenamento, prima dell’eventuale defaticamento." : "Un ding avvia il lavoro; due ding un nuovo ciclo o blocco. Due sospiri annunciano il recupero tra blocchi. Tre ding concludono lavoro e defaticamento; durante il defaticamento il timer resta silenzioso.")}</p><p class="timer-warning">${t("Il timer recupera il tempo trascorso quando torni alla pagina. Audio e vibrazione possono non funzionare in background, a schermo bloccato o con browser sospeso.")}</p></details><button data-action="stop" class="text-button danger">${t("Termina")}</button><p class="timer-warning">${t("Mantieni la pagina visibile: gli avvisi in background non sono garantiti.")}</p><div id="timer-result" aria-live="polite"></div></section>`;
}
function paintTimer() {
  if (!timer || route !== 'timer') return;
  document.querySelector('#mode-switch').disabled = active();
  const done = timer.state === 'finished', p = timer.currentPhase;
  document.querySelector('#timer-status').textContent = done ? (timer.result.status === 'COMPLETED' ? (isStudio(timer.workout) ? t("Studio completato.") : t("Allenamento completato.")) : t("Sessione interrotta.")) : timer.state === 'paused' ? t("In pausa") : p.type === 'PREPARE' ? t("Preparazione") : p.type === 'WORK' ? t(labels().focus) : p.type === 'WARMUP' ? t("Riscaldamento") : p.type === 'COOLDOWN' ? t(labels().final) : p.type === 'REST_LONG' ? t("Pausa lunga") : t(labels().rest);
  const digits = document.querySelector('#timer-digits');
  const remaining = formatTime(timer.remainingMs);
  if (digits.textContent !== remaining) {
    digits.textContent = remaining;
    digits.dataset.long = String(remaining.length > 5);
  }
  const phaseText = done ? t("Sessione registrata") : timer.index < 0 ? t("Mettiti in posizione. Si parte tra poco.") : phaseLabel(p);
  const phaseNode = document.querySelector('#timer-phase'); if (phaseNode.textContent !== phaseText) phaseNode.textContent = phaseText;
  document.querySelector('#timer-position').textContent = done ? t("{0}/{1} intervalli completati", timer.result.completedWorkIntervals, timer.result.plannedWorkIntervals) : timer.index < 0 ? t("20 secondi per prepararti") : t("Fase {0} di {1} · Tempo attivo {2}", timer.index + 1, timer.phases.length, formatTime(timer.elapsedMs));
  document.querySelector('#timer-progress').value = Math.max(0, Math.min(timer.index, timer.phases.length));
  document.querySelector('#timer-next').textContent = timer.nextPhase ? t("Poi: {0} · {1}", phaseLabel(timer.nextPhase), formatTime(timer.nextPhase.durationMs)) : t("Ultima fase. Il tuo traguardo è qui.");
  for (const action of ['pause', 'skip', 'previous', 'stop']) document.querySelector(`[data-action="${action}"]`).disabled = done;
  document.querySelector('#pause-button').textContent = timer.state === 'paused' ? t("Riprendi") : t("Pausa (timer)");
  const resultNode = document.querySelector('#timer-result');
  if (done && resultNode.dataset.sessionId !== timer.result.id) {
    resultNode.dataset.sessionId = timer.result.id;
    resultNode.innerHTML = `<p><strong>${t("{0}% completato", completion(timer.result).toFixed(0))}</strong> ${t("· {0} di {1} · {2} pause", formatTime(timer.workMs), isStudio(timer.workout) ? t("concentrazione effettiva") : t("lavoro effettivo"), timer.pauses)}</p><button data-action="history">${t("Vedi attività")}</button><button data-action="home">${t("Nuovo timer")}</button>`;
  }
}
async function start(workout) {
  if (active()) { announce(t("C’è già una sessione attiva. Termina prima quella corrente.")); navigate('timer'); return; }
  if (starting) return;
  if (dirty && !confirm(t("Avviare scartando le modifiche non salvate?"))) return;
  const prepared = new TimerEngine(workout);
  starting = true;
  try {
    if (!await acquireTimer()) { announce(t("Timer aperto in un’altra finestra.")); return; }
  } finally { starting = false; }
  const migration = normalizationNotice(workout); if (migration) announce(migration);
  signals.stop(); timer = prepared; savedSession = null; dirty = false; timer.start();
  navigate('timer');
  if (!await signals.unlock()) announce(t("Suoni non disponibili in questo browser. Il timer continua senza audio."));
  try { await signals.wake(); } catch (e) { announce(e.message); }
}
function settle() {
  if (timer?.result && savedSession !== timer.result.id) {
    const finished = timer;
    savedSession = finished.result.id;
    signals.release().catch(() => {});
    writeArchive(() => repo.addSession(finished.result)).then(saved => {
      if (saved) announce(repo.persistent ? t("Sessione salvata in questo browser.") : t("Attività registrata per questa visita. Attiva il salvataggio o esporta una copia JSON per conservarla."));
    }).catch(error => announce(error.message)).finally(() => {
      if (timer === finished) releaseTimer(finished.manualStop ? 0 : 2000);
    });
  }
}
function mutateEditor(action, button) {
  const bi = Number(button.dataset.bi), ei = Number(button.dataset.ei), kind = action.startsWith('exercise') ? 'exercise' : 'block';
  if (action === 'block-add') draft.blocks.push(newBlock(mode));
  else if (action === 'exercise-add') draft.blocks[bi].exercises.push(newExercise(mode));
  else {
    const list = kind === 'block' ? draft.blocks : draft.blocks[bi].exercises, i = kind === 'block' ? bi : ei;
    if (action.endsWith('delete')) { if (list.length === 1) { announce(t("Mantieni almeno un blocco e un esercizio.")); return; } if (!confirm(t("Eliminare questo elemento?"))) return; list.splice(i, 1); }
    else if (action.endsWith('copy')) { const copy = clone(list[i]); copy.id = uid(); if (kind === 'block') copy.exercises.forEach(e => { e.id = uid(); }); list.splice(i + 1, 0, copy); }
    else { const to = i + (action.endsWith('up') ? -1 : 1); if (to < 0 || to >= list.length) return; [list[i], list[to]] = [list[to], list[i]]; }
  }
  dirty = true; render();
}
document.addEventListener('click', async event => {
  const button = event.target.closest('button'); if (!button) return;
  if (button.dataset.route) { navigate(button.dataset.route); return; }
  const action = button.dataset.action; if (!action) return;
  const workout = repo.data.workouts.find(w => w.id === button.dataset.id);
  try {
    if (action.startsWith('activity-')) {
      const target = button.dataset.quick === 'true' ? quick : draft;
      if (action === 'activity-add') target.activities.push(newStudyActivity());
      else if (target.activities.length > 1) target.activities.splice(Number(button.dataset.index), 1);
      if (target === draft) dirty = true; render(); return;
    }
    if (action.startsWith('block-') || action.startsWith('exercise-')) { mutateEditor(action, button); return; }
    if (['home', 'workouts', 'history', 'stats', 'settings', 'help'].includes(action)) { navigate(action); return; }
    switch (action) {
      case 'mini': {
        const url = new URL('./mini.html', location.href);
        if (navigator.locks) {
          const opened = window.open(url, 'tabata-mini-timer', 'popup,width=370,height=252');
          if (opened) { opened.focus(); break; }
        }
        if (active()) { announce(t("Termina la sessione in corso prima di aprire il mini timer.")); break; }
        location.assign(url); break;
      }
      case 'language': { notice.hidden = true; notice.textContent = ''; const saved = setLanguage(button.dataset.value); render(); if (!saved) announce(t('Preferenza applicata per questa visita. Il browser non ha potuto salvarla.')); break; }
      case 'mode': {
        if (active()) { announce(t("Termina la sessione prima di cambiare modalità.")); return; }
        if (dirty && !confirm(t("Scartare le modifiche non salvate e cambiare modalità?"))) return;
        notice.hidden = true; notice.textContent = '';
        mode = mode === 'WORKOUT' ? 'STUDIO' : 'WORKOUT'; quick = quickByMode[mode]; saveMode(browserStorage, mode);
        timer = null; draft = null; dirty = false; filterWorkout = ''; route = 'home'; render(); break;
      }
      case 'style':
      case 'theme':
      case 'palette': {
        appearance = { ...appearance, [action]: button.dataset.value };
        applyAppearance(appearance); saveAppearance(browserStorage, appearance, announce);
        render(); main.querySelector(`[data-action="${action}"][data-value="${button.dataset.value}"]`)?.focus();
        break;
      }
      case 'quick-preset': {
        const p = quickPresets(mode)[Number(button.dataset.preset)];
        if (!p) return;
        quick.title = p.name;
        if (mode === 'STUDIO') Object.assign(quick.activities[0], { concentrationSec: p.workSec, pauseSec: p.restSec });
        else Object.assign(quick.blocks[0].exercises[0], { workSec: p.workSec, restBetweenRepsSec: p.restSec });
        render(); break;
      }
      case 'quick-save': {
        compileEditedTimeline(quick);
        pendingSave = duplicateWorkout(quick); pendingSave.title = quick.title;
        document.querySelector('#save-name').value = quick.title;
        document.querySelector('#save-error').hidden = true;
        document.querySelector('#save-dialog').showModal();
        document.querySelector('#save-name').select();
        break;
      }
      case 'new': if (dirty && !confirm(t("Scartare le modifiche?"))) return; draft = newWorkout(mode); draftNotice = ''; dirty = false; navigate('editor'); break;
      case 'edit': {
        if (dirty && !confirm(t("Scartare le modifiche?"))) return;
        const edit = prepareWorkoutEdit(workout);
        draft = edit.draft; draftNotice = edit.notice; dirty = false; navigate('editor'); break;
      }
      case 'copy': { const saved = await writeArchive(() => repo.upsertWorkout(duplicateWorkout(workout))); render(); if (saved) announce(t("Allenamento duplicato.")); break; }
      case 'delete': if (!confirm(t("Eliminare “{0}”? Le sessioni restano in cronologia.", workout.title))) return; await writeArchive(() => { repo.data.workouts = repo.data.workouts.filter(w => w.id !== workout.id); repo.save(); }); render(); break;
      case 'presets': await writeArchive(() => presets(mode).forEach(w => repo.upsertWorkout(w))); render(); break;
      case 'start': await start(workout); break;
      case 'pause': if (timer?.state === 'paused') { await signals.unlock(); timer.resume(); } else { timer?.pause(); signals.stop(); } soundCountdown(); settle(); paintTimer(); break;
      case 'skip': signals.stop(); timer?.skip(); soundCountdown(); settle(); paintTimer(); break;
      case 'previous': signals.stop(); timer?.previous(); soundCountdown(); settle(); paintTimer(); break;
      case 'stop': if (active() && confirm(t("Terminare e registrare la sessione come interrotta?"))) { signals.stop(); timer.stop(); soundCountdown(); settle(); paintTimer(); } break;
      case 'fullscreen': if (document.fullscreenElement) await document.exitFullscreen(); else if (main.querySelector('.timer-panel').requestFullscreen) await main.querySelector('.timer-panel').requestFullscreen(); else announce(t("Schermo intero non supportato: puoi usare il timer nella pagina.")); break;
      case 'persist': if (await writeArchive(() => repo.enable())) announce(t("Allenamenti e attività salvati in questo browser.")); render(); break;
      case 'temporary': if (confirm(t("Rimuovere il salvataggio da questo browser e continuare solo per questa visita? Esporta prima una copia JSON per conservarlo."))) { await writeArchive(() => repo.disable()); render(); } break;
      case 'export-json': download(makeBackup(repo.data), 'tabata-backup-v1.json', 'application/json'); break;
      case 'export-csv': download(sessionsCsv(modeData(repo.data, mode).sessions), 'tabata-sessioni.csv', 'text/csv;charset=utf-8'); break;
      case 'import': if (active()) { announce(t("Termina la sessione prima di importare.")); return; } document.querySelector('#import-file').click(); break;
      case 'clear': if (active()) { announce(t("Termina la sessione prima di cancellare i dati.")); return; } if (confirm(t("Cancellare tutti gli allenamenti e tutte le attività? Questa operazione non può essere annullata."))) { if (!await writeArchive(() => repo.disable())) return; repo.data = { workouts: [], sessions: [] }; draft = null; dirty = false; timer = null; render(); announce(t("Dati cancellati. Modalità temporanea attiva.")); } break;
      case 'session-delete': if (confirm(t("Eliminare questa sessione?"))) { await writeArchive(() => { repo.data.sessions = repo.data.sessions.filter(s => s.id !== button.dataset.id); repo.save(); }); render(); } break;
    }
  } catch (e) { announce(e.message); }
});
function formValue(input, key) {
  if (input.type !== 'number') return input.value;
  const value = input.valueAsNumber * Number(input.dataset.unitFactor || 1);
  if (key === 'cooldownSec') {
    if (input.disabled) return 0;
    if (value === 0) return NaN;
  }
  return value;
}
main.addEventListener('input', event => {
  let input = event.target;
  if (input.hasAttribute('data-cooldown-toggle')) {
    const enabled = input.checked;
    const field = input.closest('.cooldown-fields').querySelector('[data-cooldown-duration]');
    field.hidden = !enabled;
    input = field.querySelector('input');
    input.disabled = !enabled;
    input.value = enabled ? input.min : '';
  }
  if (input.dataset.quickPath) {
    const path = input.dataset.quickPath.split('.'), key = path.pop(); let target = quick;
    path.forEach(p => { target = target[p]; });
    target[key] = formValue(input, key);
    const error = document.querySelector('#quick-error');
    const total = document.querySelector('#quick-total');
    try {
      const duration = formatTime(plannedMs(compileEditedTimeline(quick)));
      total.textContent = duration;
      total.dataset.long = String(duration.length > 5);
      document.querySelector('#quick-start-button').disabled = false; document.querySelector('[data-action=quick-save]').disabled = false; error.hidden = true;
    } catch (e) {
      total.textContent = '—'; total.dataset.long = 'false';
      error.textContent = t(e.message); error.hidden = false;
      document.querySelector('#quick-start-button').disabled = true; document.querySelector('[data-action=quick-save]').disabled = true;
    }
    document.querySelectorAll('[data-preset]').forEach(b => { const p = quickPresets(mode)[Number(b.dataset.preset)], e = mode === 'STUDIO' ? { workSec: quick.activities[0].concentrationSec, restBetweenRepsSec: quick.activities[0].pauseSec } : quick.blocks[0].exercises[0]; b.setAttribute('aria-pressed', String(e.workSec === p.workSec && e.restBetweenRepsSec === p.restSec)); });
    return;
  }
  if (!input.dataset.path) return;
  const path = input.dataset.path.split('.'), key = path.pop(); let target = draft;
  path.forEach(p => { target = target[p]; }); target[key] = formValue(input, key); dirty = true;
  document.querySelector('#preview').innerHTML = preview(draft);
  updateEditorSave();
});
main.addEventListener('submit', async event => {
  event.preventDefault();
  try {
    if (event.target.id === 'quick-form') {
      validateDurationInputs(quick);
      start(quick).catch(e => announce(e.message));
      return;
    }
    compileEditedTimeline(draft);
    const candidate = clone(draft);
    const saved = await writeArchive(() => repo.saveWorkoutLocally(candidate));
    if (saved) { dirty = false; announce(t("Allenamento salvato sul dispositivo.")); navigate('workouts'); }
  } catch (e) { announce(e.message); }
});
main.addEventListener('change', async event => {
  const input = event.target;
  if (input.id === 'sound') { signals.sound = input.checked; if (!signals.sound) signals.stop(); if (signals.sound) await signals.unlock(); }
  if (input.id === 'vibration') { signals.vibration = input.checked; if (!navigator.vibrate && input.checked) announce(t("Vibrazione non supportata in questo browser.")); }
  if (input.id === 'awake') { signals.keepAwake = input.checked; try { if (input.checked && active()) await signals.wake(); else await signals.release(); } catch (e) { announce(t("Schermo acceso non disponibile: {0}", e.message)); } }
  if (input.id === 'filter-days') { filterDays = Number(input.value); render(); }
  if (input.id === 'filter-workout') { filterWorkout = input.value; render(); }
});
document.querySelector('#save-form').addEventListener('submit', async event => {
  event.preventDefault();
  if (!pendingSave) return;
  const error = document.querySelector('#save-error');
  try {
    pendingSave.title = document.querySelector('#save-name').value.trim();
    compileEditedTimeline(pendingSave);
    const candidate = clone(pendingSave);
    if (!await writeArchive(() => repo.saveWorkoutLocally(candidate))) {
      error.textContent = t("Salvataggio non riuscito. L’allenamento resta disponibile per questa visita. Puoi riprovare oppure chiudere e creare una copia JSON dalle impostazioni.");
      error.hidden = false; return;
    }
    quick.title = candidate.title; pendingSave = null;
    document.querySelector('#save-dialog').close();
    navigate('workouts'); announce(t("Salvato sul dispositivo. Lo trovi in {0}.", t(labels().collection)));
  } catch (e) { error.textContent = t(e.message); error.hidden = false; }
});
document.querySelector('#cancel-save').addEventListener('click', () => { pendingSave = null; document.querySelector('#save-dialog').close(); });
document.querySelector('#save-dialog').addEventListener('cancel', () => { pendingSave = null; });

document.querySelector('#import-file').addEventListener('change', async event => {
  const file = event.target.files[0]; event.target.value = ''; if (!file) return;
  try {
    if (file.size > MAX_BACKUP_BYTES) throw new Error(t("File troppo grande: massimo 5 MiB"));
    const bytes = await file.arrayBuffer();
    let raw;
    try { raw = new TextDecoder("utf-8", { fatal: true }).decode(bytes); }
    catch { throw new Error("Il file non è un JSON UTF-8 valido."); }
    if (active() || document.querySelector("#import-dialog").open) return;
    pendingImport = parseBackup(raw);
    document.querySelector('#import-preview').textContent = t("{0} allenamenti · {1} sessioni · esportato il {2}", pendingImport.workouts.length, pendingImport.sessions.length, dateLabel(pendingImport.exportedAt));
    document.querySelector('#import-dialog').showModal();
  } catch (e) { announce(t("Importazione rifiutata: {0}", t(e.message))); }
});
document.querySelector('#confirm-import').addEventListener('click', async () => {
  if (!pendingImport || active()) return;
  const backup = { workouts: pendingImport.workouts, sessions: pendingImport.sessions };
  pendingImport = null;
  const saved = await writeArchive(() => repo.replace(backup));
  document.querySelector('#import-dialog').close();
  if (!saved) { announce(t("Importazione non salvata. L’archivio precedente è rimasto invariato.")); return; }
  dirty = false; draft = null; timer = null;
  navigate('workouts');
  announce(repo.persistent ? t("Importazione completata e salvata sul dispositivo.") : t("Importazione completata per questa visita. Attiva il salvataggio per conservarla."));
});
document.querySelector('#cancel-import').addEventListener('click', () => { pendingImport = null; document.querySelector('#import-dialog').close(); });
document.querySelector('#import-dialog').addEventListener('cancel', () => { pendingImport = null; });
document.querySelector('.brand').addEventListener('click', event => { event.preventDefault(); navigate('home'); });
window.addEventListener('storage', event => {
  if (event.key !== STORAGE_KEY) return;
  repo.load(parseBackup);
  if (['workouts', 'history', 'stats', 'settings'].includes(route)) render();
});
window.addEventListener('pagehide', event => {
  if (event.persisted && active()) timer.stop();
  signals.stop();
  releaseTimer();
});
document.addEventListener('visibilitychange', () => { timer?.tick(); soundCountdown(); settle(); if (document.visibilityState === 'visible') { paintTimer(); if (active()) signals.wake().catch(e => announce(e.message)); } });
window.addEventListener('beforeunload', event => { if (active() || dirty) { event.preventDefault(); event.returnValue = ''; } });
setInterval(() => { timer?.tick(); soundCountdown(); settle(); if (route === 'timer') paintTimer(); }, 100);
if ('serviceWorker' in navigator) navigator.serviceWorker.register('./sw.js').catch(() => announce(t("Cache offline non disponibile. La pagina aperta funziona; per ricaricarla serve il server locale.")));
render();
