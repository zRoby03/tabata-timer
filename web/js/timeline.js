import { validateWorkout, normalizeWorkout, LIMITS } from './model.js';
import { isStudio } from './modes.js';
export function compileTimeline(workout) {
  validateWorkout(workout);
  workout = normalizeWorkout(workout); validateWorkout(workout);
  const phases = []; let total = 0;
  const studio = isStudio(workout);
  const add = (type, sec, label, blockId = null, exerciseId = null) => {
    if (!sec) return;
    total += sec * 1000;
    if (phases.length >= LIMITS.phases || total > LIMITS.totalMs) throw new Error('Allenamento troppo lungo: massimo 10.000 fasi e 24 ore');
    phases.push({ type, blockId, exerciseId, durationMs: sec * 1000, label });
  };
  if (studio) {
    workout.activities.forEach((a, i) => {
      for (let session = 0; session < a.sessions; session++) {
        add('WORK', a.concentrationSec, `Attività ${i + 1} · ${a.title} · sessione ${session + 1}/${a.sessions}`, null, a.id);
        if (session < a.sessions - 1) add('REST_REP', a.pauseSec, 'Pausa tra sessioni', null, a.id);
      }
      if (i < workout.activities.length - 1) add('REST_ACTIVITY', workout.restBetweenActivitiesSec, 'Pausa tra attività', null, a.id);
    });
    return phases;
  }
  add('WARMUP', workout.warmupSec, 'Riscaldamento');
  workout.blocks.forEach((b, bi) => {
    for (let c = 0; c < b.cycles; c++) {
      b.exercises.forEach((e, ei) => {
        for (let r = 0; r < e.repetitions; r++) {
          add('WORK', e.workSec, `${e.title} · giro ${c + 1}/${b.cycles} · ${r + 1}/${e.repetitions}`, b.id, e.id);
          if (r < e.repetitions - 1) add('REST_REP', e.restBetweenRepsSec, 'Recupero ripetizioni', b.id, e.id);
        }
        if (ei < b.exercises.length - 1) add('REST_EXERCISE', b.restBetweenExercisesSec, 'Recupero tra esercizi', b.id, e.id);
      });
      if (c < b.cycles - 1) add('REST_CYCLE', b.restBetweenCyclesSec, 'Recupero cicli', b.id);
    }
    if (bi < workout.blocks.length - 1) add('REST_BLOCK', b.restAfterBlockSec, 'Recupero blocchi', b.id);
  });
  add('COOLDOWN', workout.cooldownSec, 'Defaticamento');
  return phases;
}
export const plannedMs = phases => phases.reduce((sum, p) => sum + p.durationMs, 0);
