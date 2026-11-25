import { isStudio, modeOf, MODES } from './modes.js';
import { TRANSITION_SOUNDS, TransitionCues, cueVolume } from './transitions.js';
import { CountdownCues, hornSample, COUNTDOWN_SOUNDS } from './countdown.js';
import { integer, LIMITS, STUDIO_MIN_SECONDS, uid, WORKOUT_MIN_SECONDS } from './model.js';
import { compileTimeline, plannedMs } from './timeline.js';
const AUDIO_SOUNDS = { ...COUNTDOWN_SOUNDS, ...TRANSITION_SOUNDS };
export const PREPARATION_MS = 20000;
const PREPARATION = Object.freeze({ type: 'PREPARE', blockId: null, exerciseId: null, durationMs: PREPARATION_MS, label: 'Preparazione' });

// The clock supplies an absolute monotonic timestamp; polling never decrements a counter.
export class TimerEngine {
  constructor(workout, now = () => performance.now(), wall = () => new Date().toISOString(), breakSeconds = null) {
    this.workout = structuredClone(workout); this.standaloneBreak = breakSeconds !== null;
    this.phases = this.standaloneBreak ? [standaloneBreakPhase(workout, breakSeconds)] : compileTimeline(workout);
    this.now = now; this.wall = wall;
    this.index = this.standaloneBreak ? 0 : -1;
    this.remainingMs = this.standaloneBreak ? this.phases[0].durationMs : PREPARATION_MS;
    this.state = 'idle'; this.phaseRun = 0;
    this.elapsedMs = 0; this.workMs = 0; this.pauses = 0; this.completed = new Set(); this.completedPhases = new Set(); this.result = null;
    this.lastWorkIndex = this.phases.findLastIndex(p => p.type === 'WORK');
    this.workIntervals = this.phases.filter(p => p.type === 'WORK').length;
    this.naturalEndings = {}; this.finalOverrunMs = 0; this.manualStop = false;
  }
  get currentPhase() { return this.index < 0 ? PREPARATION : this.phases[this.index]; }
  get nextPhase() { return this.phases[this.index + 1]; }
  start() {
    if (this.state !== 'idle') return;
    this.id = uid(); this.startedAt = this.wall(); this.last = this.now(); this.state = 'running';
  }
  tick() {
    if (this.state !== 'running') return;
    const current = this.now(); let delta = Math.max(0, current - this.last); this.last = current;
    while (delta >= this.remainingMs && this.state === 'running') {
      this.consume(this.remainingMs); delta -= this.remainingMs;
      if (this.index >= 0) {
        this.completedPhases.add(this.index);
        if (this.currentPhase.type === 'WORK') this.completed.add(this.index);
      }
      this.advance(true);
      if (this.state === 'finished') this.finalOverrunMs = delta;
    }
    if (this.state === 'running') { this.consume(delta); this.remainingMs -= delta; }
  }
  consume(ms) {
    if (this.index < 0) return; // The lead-in is outside the planned workout and its statistics.
    this.elapsedMs += ms; if (this.currentPhase.type === 'WORK') this.workMs += ms;
  }
  advance(natural = false) {
    if (natural && this.standaloneBreak) this.naturalEndings.break = this.elapsedMs;
    if (natural && this.index === this.lastWorkIndex) this.naturalEndings.work ??= this.elapsedMs;
    if (natural && this.currentPhase?.type === 'COOLDOWN') this.naturalEndings.cooldown ??= this.elapsedMs;
    this.index++; this.phaseRun++;
    if (this.index >= this.phases.length) {
      const completed = this.standaloneBreak ? natural : this.completed.size === this.workIntervals;
      this.finish(completed ? 'COMPLETED' : 'INTERRUPTED');
    }
    else this.remainingMs = this.phases[this.index].durationMs;
  }
  pause() { this.tick(); if (this.state === 'running') { this.state = 'paused'; this.pauses++; } }
  resume() { if (this.state === 'paused') { this.last = this.now(); this.state = 'running'; } }
  skip() { this.tick(); if (['running', 'paused'].includes(this.state)) this.advance(); }
  previous() {
    this.tick();
    if (!['running', 'paused'].includes(this.state)) return;
    if (this.index >= 0) this.index = Math.max(0, this.index - 1);
    this.phaseRun++; this.remainingMs = this.currentPhase.durationMs; this.last = this.now();
  }
  stop() { this.tick(); if (['running', 'paused'].includes(this.state)) { this.manualStop = true; this.finish('INTERRUPTED'); } return this.result; }
  finish(status) {
    if (this.result) return this.result;
    this.state = 'finished'; this.remainingMs = 0;
    this.result = { id: this.id, mode: modeOf(this.workout), workoutId: this.workout.id, workoutTitle: this.workout.title, startedAt: this.startedAt, endedAt: this.wall(), plannedMs: plannedMs(this.phases), elapsedMs: Math.round(this.elapsedMs), workMs: Math.round(this.workMs), completedWorkIntervals: this.completed.size, plannedWorkIntervals: this.workIntervals, completedPhases: this.completedPhases.size, plannedPhases: this.phases.length, pauses: this.pauses, status };
    return this.result;
  }
}

function standaloneBreakPhase(workout, seconds) {
  const studio = isStudio(workout), label = MODES[modeOf(workout)].rest;
  if (studio) integer(seconds / STUDIO_MIN_SECONDS, label, 1, LIMITS.seconds / STUDIO_MIN_SECONDS);
  else integer(seconds, label, WORKOUT_MIN_SECONDS);
  return { type: 'REST_REP', blockId: null, exerciseId: null, durationMs: seconds * 1000, label };
}

export class TimerCuePlayback {
  constructor(signals) {
    this.signals = signals;
    this.countdown = new CountdownCues();
    this.transitions = new TransitionCues();
    this.timer = null;
    this.visit = -1;
    this.state = 'idle';
  }
  update(timer) {
    if (timer !== this.timer || timer?.phaseRun !== this.visit || (timer?.state !== this.state && timer?.state !== 'running')) {
      this.signals.stop();
      this.timer = timer;
      this.visit = timer?.phaseRun;
    }
    this.state = timer?.state;
    const marker = this.transitions.read(timer);
    const second = this.countdown.read(timer);
    if (marker) this.signals.play(marker);
    else if (second !== null) this.signals.horn(second, timer.currentPhase?.type);
  }
}

export class TimerSignals {
  constructor() { this.audio = null; this.buffers = {}; this.sources = new Set(); this.lock = null; this.sound = true; this.vibration = false; this.keepAwake = false; }
  async unlock() {
    if (!this.sound) return true;
    try {
      const Audio = globalThis.AudioContext || globalThis.webkitAudioContext;
      if (!Audio) return false;
      this.audio ??= new Audio(); await this.audio.resume();
      if (Object.keys(AUDIO_SOUNDS).some(key => !this.buffers[key])) {
        const loaded = await Promise.all(Object.entries(AUDIO_SOUNDS).map(async ([key, file]) => {
          const response = await fetch(new URL('../audio/' + file, import.meta.url));
          if (!response.ok) throw new Error('Suono non disponibile');
          return [key, await this.audio.decodeAudioData(await response.arrayBuffer())];
        }));
        this.buffers = Object.fromEntries(loaded);
      }
      return this.audio.state === 'running';
    } catch { return false; }
  }
  horn(second, phase = 'PREPARE') {
    const key = hornSample(second, phase); if (key) this.play(key);
  }
  play(key) {
    const buffer = this.buffers[key];
    if (this.sound && buffer && this.audio?.state === 'running') {
      this.stop();
      const source = this.audio.createBufferSource(), gain = this.audio.createGain();
      source.buffer = buffer;
      source.playbackRate.value = 1;
      gain.gain.value = cueVolume(key);
      source.connect(gain).connect(this.audio.destination);
      source.onended = () => { this.sources.delete(source); source.disconnect(); gain.disconnect(); };
      this.sources.add(source);
      source.start();
    }
    if (this.vibration) navigator.vibrate?.(100);
  }
  stop() { for (const source of this.sources) source.stop(); this.sources.clear(); }
  async wake() {
    if (!this.keepAwake || this.lock || document.visibilityState !== 'visible') return;
    if (!navigator.wakeLock) throw new Error('Schermo acceso non supportato in questo browser.');
    this.lock = await navigator.wakeLock.request('screen');
    this.lock.addEventListener('release', () => { this.lock = null; });
  }
  async release() { if (this.lock) { await this.lock.release(); this.lock = null; } }
}
