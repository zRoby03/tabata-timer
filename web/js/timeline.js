import { validateWorkout, normalizeWorkout, LIMITS } from './model.js';
import { isStudio } from './modes.js';
export function compileTimeline(original) {
  validateWorkout(original);
  const w = normalizeWorkout(original); validateWorkout(w);
  const phases = []; let total = 0;
  const add = (type, sec, labelKey, labelArgs = [], metadata = {}) => {
    if (!sec) return;
    total += sec * 1000;
    if (phases.length >= LIMITS.phases || total > LIMITS.totalMs) throw new Error('Allenamento troppo lungo: massimo 10.000 fasi e 24 ore');
    const label = labelKey.replace(/\{(\d+)\}/g, (_, i) => String(labelArgs[i]));
    phases.push({ type, blockId: null, exerciseId: null, durationMs: sec * 1000, label, labelKey, labelArgs, ...metadata });
  };
  if (isStudio(w)) {
    w.activities.forEach((a, i) => {
      const metadata = { exerciseId: a.id, activityIndex: i };
      for (let session = 0; session < a.sessions; session++) {
        add('WORK', a.concentrationSec, 'Attività {0} · {1} · sessione {2}/{3}', [i + 1, a.title, session + 1, a.sessions], metadata);
        if (session < a.sessions - 1) add('REST_REP', a.pauseSec, 'Pausa {0} — tra sessioni', [a.title], metadata);
      }
      if (i < w.activities.length - 1) add('REST_ACTIVITY', w.restBetweenActivitiesSec, 'Pausa {0} → {1} — tra attività', [a.title, w.activities[i + 1].title], metadata);
    });
    return phases;
  }
  add('WARMUP', w.warmupSec, 'Riscaldamento');
  w.blocks.forEach((b, bi) => {
    for (let c = 0; c < b.cycles; c++) {
      b.exercises.forEach((e, ei) => {
        const metadata = { blockId: b.id, exerciseId: e.id, blockIndex: bi, blockTitle: b.title, cycleIndex: c, cycles: b.cycles, exerciseTitle: e.title };
        for (let r = 0; r < e.repetitions; r++) {
          const entryCue = ei === 0 && r === 0 && (bi > 0 || c > 0) ? 'ding2' : 'ding1';
          add('WORK', e.workSec, '{0} · giro {1}/{2} · {3}/{4}', [e.title, c + 1, b.cycles, r + 1, e.repetitions], { ...metadata, repetitionIndex: r, entryCue });
          const nextRep = r < e.repetitions - 1, nextExercise = ei < b.exercises.length - 1, nextCycle = c < b.cycles - 1;
          if (nextRep || nextExercise || nextCycle) {
            add(nextRep ? 'REST_REP' : nextExercise ? 'REST_EXERCISE' : 'REST_REPEAT', e.restBetweenRepsSec, 'Recupero {0}', [e.title], metadata);
          } else if (bi < w.blocks.length - 1) {
            add('REST_BLOCK', b.restAfterBlockSec, 'Recupero Blocco {0} → {1}', [bi + 1, bi + 2], metadata);
          }
        }
      });
    }
  });
  add('COOLDOWN', w.cooldownSec, 'Defaticamento');
  return phases;
}
export const plannedMs = phases => phases.reduce((sum, p) => sum + p.durationMs, 0);
