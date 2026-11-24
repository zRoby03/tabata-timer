// Read only the current second: delayed polling never produces a burst of old cues.
export class CountdownCues {
  constructor() { this.engine = null; this.visit = -1; this.played = new Set(); }
  read(engine) {
    if (!engine) return null;
    if (engine !== this.engine || engine.phaseRun !== this.visit) {
      this.engine = engine; this.visit = engine.phaseRun; this.played.clear();
    }
    if (engine.state !== 'running') return null;
    if (engine.currentPhase?.type === 'COOLDOWN') return null;
    const second = Math.ceil(engine.remainingMs / 1000);
    if (second < 1 || second > 3 || this.played.has(second)) return null;
    this.played.add(second);
    return second;
  }
}

// One short beep on each of 3, 2, 1; silence separates the cues.
export const COUNTDOWN_SOUNDS = Object.fromEntries(['work', 'rest'].flatMap(kind =>
  [3, 2, 1].map(second => [`${kind}-${second}`, `countdown-${kind}-${second}.wav`])
));
export function hornSample(second, phase = 'PREPARE') {
  if (![3, 2, 1].includes(second)) return null;
  const kind = phase === 'WORK' ? 'work' : 'rest';
  return `${kind}-${second}`;
}
