import { isStudio } from './modes.js';
export const TRANSITION_SOUNDS = { whistle: 'work-start.wav', sigh: 'rest-start.wav', finish: 'workout-finish.wav', ding1: 'workout-ding-1.wav', ding2: 'workout-ding-2.wav', ding3: 'workout-ding-3.wav', 'double-sigh': 'rest-double.wav' };
export const cueVolume = key => key === 'whistle' ? 1 : ['sigh', 'double-sigh'].includes(key) ? 0.85 : 0.65;
export const CUE_DURATION_MS = { whistle: 500, sigh: 500, finish: 900, ding1: 1050, ding2: 1485, ding3: 1920, 'double-sigh': 1200 };
export const cueDurationMs = key => CUE_DURATION_MS[key] ?? 120;
export function phaseStartCue(phase, studio = false) {
  if (phase?.type === 'WORK') return studio ? 'whistle' : phase.entryCue ?? 'ding1';
  if (phase?.type.startsWith('REST_')) return !studio && phase.type === 'REST_BLOCK' ? 'double-sigh' : 'sigh';
  return null;
}
export class TransitionCues {
  constructor() { this.engine = null; this.visit = -1; this.finished = false; this.announced = new Set(); }
  read(engine) {
    if (!engine) return null;
    if (engine !== this.engine) { this.engine = engine; this.visit = -1; this.finished = false; this.announced.clear(); }
    if (engine.standaloneBreak && engine.state === 'finished') {
      if (this.finished || engine.naturalEndings.break === undefined || engine.finalOverrunMs > 500) return null;
      this.finished = true;
      return isStudio(engine.workout) ? 'finish' : 'ding3';
    }
    if (isStudio(engine.workout)) {
      if (engine.state === 'finished') {
        if (engine.index >= engine.phases.length && !this.finished) { this.finished = true; return 'finish'; }
        return null;
      }
    } else {
      if (engine.manualStop || !['running', 'finished'].includes(engine.state)) return null;
      for (const ending of ['work', 'cooldown']) {
        const at = engine.naturalEndings[ending];
        if (at === undefined || this.announced.has(ending)) continue;
        this.announced.add(ending);
        const age = engine.elapsedMs - at + (engine.state === 'finished' ? engine.finalOverrunMs : 0);
        if (age <= 500) return 'ding3';
      }
    }
    if (engine.state !== 'running' || engine.phaseRun === this.visit) return null;
    this.visit = engine.phaseRun;
    const phase = engine.currentPhase;
    if (!phase || phase.durationMs - engine.remainingMs > 500) return null;
    return phaseStartCue(phase, isStudio(engine.workout));
  }
}
