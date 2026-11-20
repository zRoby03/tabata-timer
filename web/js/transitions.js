export const TRANSITION_SOUNDS = { whistle: 'work-start.wav', sigh: 'rest-start.wav', finish: 'workout-finish.wav' };

// Whistle PCM is pre-amplified by 1.2 so SoundPool and Web Audio share the same safe 120% level.
export const cueVolume = key => key === 'whistle' ? 1 : key === 'sigh' ? 0.85 : 0.65;

// Observe the current phase only; never replay a backlog of phase-start sounds.
export class TransitionCues {
  constructor() { this.engine = null; this.visit = -1; this.finished = false; }
  read(engine) {
    if (!engine) return null;
    if (engine !== this.engine) { this.engine = engine; this.visit = -1; this.finished = false; }
    if (engine.state === 'finished') {
      if (engine.index >= engine.phases.length && !this.finished) { this.finished = true; return 'finish'; }
      return null;
    }
    if (engine.state !== 'running' || engine.phaseRun === this.visit) return null;
    this.visit = engine.phaseRun;
    const phase = engine.currentPhase;
    if (phase?.type === 'COOLDOWN' && !this.finished) { this.finished = true; return 'finish'; }
    if (!phase || phase.durationMs - engine.remainingMs > 500) return null;
    if (phase.type === 'WORK') return 'whistle';
    return phase.type.startsWith('REST_') ? 'sigh' : null;
  }
}
