import { t } from './i18n.js';
import { MODES, isStudio } from './modes.js';
export const SCHEMA_VERSION = 1;
export const LIMITS = Object.freeze({ blocks: 50, exercises: 100, repeats: 100, seconds: 3600, phases: 10000, totalMs: 86400000 });
export const WORKOUT_MIN_SECONDS = 5;
export const COOLDOWN_MIN_SECONDS = 10;
export const STUDIO_MIN_SECONDS = 60;
export const uid = () => globalThis.crypto.randomUUID();
export const clone = value => structuredClone(value);
export const formatTime = ms => {
  const s = Math.ceil(Math.max(0, ms) / 1000);
  return s >= 3600 ? `${Math.floor(s / 3600)}:${String(Math.floor(s / 60) % 60).padStart(2, '0')}:${String(s % 60).padStart(2, '0')}` : `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}`;
};
export function object(value, path) {
  if (!value || typeof value !== 'object' || Array.isArray(value)) throw new Error(`${path}: oggetto richiesto`);
}
export function text(value, path, max = 120, empty = false) {
  if (typeof value !== 'string' || value.length > max || (!empty && !value.trim())) throw new Error(`${path}: testo non valido (max ${max})`);
}
export function integer(value, path, min = 0, max = LIMITS.seconds) {
  if (!Number.isSafeInteger(value) || value < min || value > max) throw new Error(`${path}: intero tra ${min} e ${max}`);
}
export function id(value, path) {
  if (typeof value !== 'string' || !/^[A-Za-z0-9_-]{1,128}$/.test(value)) throw new Error(`${path}: ID non valido`);
}
export function validateWorkout(w) {
  object(w, 'workout');
  if (w.schemaVersion !== 1) throw new Error('Versione allenamento non supportata');
  if (w.mode !== undefined && !Object.hasOwn(MODES, w.mode)) throw new Error('Modalità non valida');
  if (w.timingVersion !== undefined && w.timingVersion !== 2) throw new Error('Versione tempi non supportata');
  if (w.longBreakSec !== undefined) integer(w.longBreakSec, 'Pausa lunga');
  if (w.longBreakEvery !== undefined) integer(w.longBreakEvery, 'Frequenza pausa lunga', 2, 100);
  id(w.id, 'workout.id'); text(w.title, 'Titolo'); text(w.description, 'Descrizione', 2000, true);
  if (w.color !== undefined && !/^#[0-9a-f]{6}$/i.test(w.color)) throw new Error('Colore non valido');
  const seconds = (value, name, min = 0) => { integer(value, name, min); if (isStudio(w) && value % 60 !== 0) throw new Error(`${name}: Studio usa minuti interi (massimo 60)`); };
  seconds(w.warmupSec, 'Riscaldamento'); seconds(w.cooldownSec, 'Defaticamento');
  if (isStudio(w) && w.activities !== undefined) {
    seconds(w.restBetweenActivitiesSec, 'Pausa tra attività');
    if (!Array.isArray(w.activities) || !w.activities.length || w.activities.length > 5000) throw new Error('Serve almeno un’attività (massimo 5000)');
    const ids = new Set([w.id]);
    for (const a of w.activities) {
      object(a, 'Attività'); id(a.id, 'activity.id');
      if (ids.has(a.id)) throw new Error('ID attività duplicato');
      ids.add(a.id); text(a.title, 'Nome attività');
      seconds(a.concentrationSec, t("Concentrazione"), 60); integer(a.sessions, 'Sessioni', 1, 100); seconds(a.pauseSec, 'Pausa');
    }
    return w;
  }
  if (isStudio(w)) seconds(w.longBreakSec ?? 0, 'Pausa lunga');
  if (!Array.isArray(w.blocks) || !w.blocks.length || w.blocks.length > LIMITS.blocks) throw new Error('Servono da 1 a 50 blocchi');
  const ids = new Set([w.id]);
  const unique = (value, path) => { id(value, path); if (ids.has(value)) throw new Error('ID duplicato nell’allenamento'); ids.add(value); };
  for (const b of w.blocks) {
    object(b, 'Blocco'); unique(b.id, 'block.id'); text(b.title, 'Titolo blocco');
    integer(b.cycles, 'Cicli', 1, LIMITS.repeats); seconds(b.restBetweenCyclesSec, 'Recupero cicli'); seconds(b.restAfterBlockSec, 'Recupero blocchi');
    if (b.restBetweenExercisesSec !== undefined) seconds(b.restBetweenExercisesSec, 'Recupero tra esercizi');
    if (!Array.isArray(b.exercises) || !b.exercises.length || b.exercises.length > LIMITS.exercises) throw new Error('Servono da 1 a 100 esercizi per blocco');
    for (const e of b.exercises) {
      object(e, 'Esercizio'); unique(e.id, 'exercise.id'); text(e.title, 'Titolo esercizio');
      integer(e.repetitions, 'Ripetizioni', 1, LIMITS.repeats); seconds(e.workSec, 'Lavoro', 1); seconds(e.restBetweenRepsSec, 'Recupero ripetizioni'); seconds(e.restAfterExerciseSec, 'Recupero esercizi');
    }
  }
  if (!isStudio(w) && w.timingVersion === 2) {
    const minimum = (value, min, label, optional = false) => {
      if ((optional && value === 0) || value >= min) return;
      throw new Error(t('{0}: minimo {1}', label, min));
    };
    minimum(w.cooldownSec, 10, t('Defaticamento'), true);
    w.blocks.forEach((b, bi) => {
      if (bi < w.blocks.length - 1) minimum(b.restAfterBlockSec, 5, t('Recupero tra blocchi'));
      b.exercises.forEach(e => { minimum(e.workSec, 5, t('Lavoro')); minimum(e.restBetweenRepsSec, 5, t('Recupero'), true); });
    });
  }
  return w;
}
export const newExercise = (mode = 'WORKOUT') => ({ id: uid(), title: mode === 'STUDIO' ? t("Concentrazione") : t("Nuovo esercizio"), repetitions: 1, workSec: mode === 'STUDIO' ? 1500 : 30, restBetweenRepsSec: mode === 'STUDIO' ? 300 : 10, restAfterExerciseSec: mode === 'STUDIO' ? 300 : 15 });
export const newStudyActivity = () => ({ id: uid(), title: t("Concentrazione"), concentrationSec: 1500, sessions: 4, pauseSec: 300 });
export const newBlock = () => ({ id: uid(), title: t("Nuovo blocco"), cycles: 1, restBetweenCyclesSec: 60, restAfterBlockSec: 60, restBetweenExercisesSec: 15, exercises: [newExercise()] });
export const newWorkout = (mode = 'WORKOUT') => ({ schemaVersion: 1, mode, id: uid(), title: mode === 'STUDIO' ? t("Il mio studio") : t("Il mio allenamento"), description: '', color: '#9153b7', warmupSec: 0, cooldownSec: 0, blocks: mode === 'STUDIO' ? [] : [newBlock()], ...(mode === 'STUDIO' ? { activities: [newStudyActivity()], restBetweenActivitiesSec: 300 } : { timingVersion: 2 }) });

// Editing rules are stricter than archive validation, where zero can mean an omitted phase.
export function validateDurationInputs(w) {
  if (isStudio(w)) {
    const minutes = (value, label) => integer(value / STUDIO_MIN_SECONDS, label, 1, LIMITS.seconds / STUDIO_MIN_SECONDS);
    for (const activity of w.activities) {
      minutes(activity.concentrationSec, 'Concentrazione');
      minutes(activity.pauseSec, 'Pausa');
    }
    minutes(w.restBetweenActivitiesSec, 'Pausa tra attività');
    return;
  }
  if (w.cooldownSec !== 0) integer(w.cooldownSec, 'Durata defaticamento', COOLDOWN_MIN_SECONDS);
  w.blocks.forEach((block, index) => {
    if (index < w.blocks.length - 1) integer(block.restAfterBlockSec, 'Recupero tra blocchi', WORKOUT_MIN_SECONDS);
    for (const exercise of block.exercises) {
      integer(exercise.workSec, 'Lavoro', WORKOUT_MIN_SECONDS);
      integer(exercise.restBetweenRepsSec, 'Recupero', WORKOUT_MIN_SECONDS);
    }
  });
}

export function prepareWorkoutEdit(w) {
  const draft = normalizeWorkout(w);
  let adjusted = false;
  const minimum = (value, min) => {
    if (value >= min) return value;
    adjusted = true;
    return min;
  };
  if (isStudio(draft)) {
    draft.restBetweenActivitiesSec = minimum(draft.restBetweenActivitiesSec, STUDIO_MIN_SECONDS);
    for (const activity of draft.activities) activity.pauseSec = minimum(activity.pauseSec, STUDIO_MIN_SECONDS);
  } else {
    for (const block of draft.blocks) {
      for (const exercise of block.exercises) exercise.restBetweenRepsSec = minimum(exercise.restBetweenRepsSec, WORKOUT_MIN_SECONDS);
    }
  }
  const notice = adjusted
    ? 'Le pause a zero sono impostate al minimo nella copia da modificare. Il programma salvato si aggiorna quando salvi.'
    : normalizationNotice(w);
  return { draft, notice };
}

// Reading/previewing old records never rewrites the archive. Saving the edited copy adopts the new form.
export function normalizeWorkout(w) {
  const copy = clone(w);
  if (!isStudio(copy)) {
    if (copy.timingVersion !== 2) {
      const active = (value, min) => value > 0 ? Math.max(min, value) : value;
      copy.cooldownSec = active(copy.cooldownSec, 10);
      copy.blocks.forEach((b, bi) => {
        if (bi < copy.blocks.length - 1) b.restAfterBlockSec = Math.max(5, b.restAfterBlockSec);
        b.exercises.forEach(e => { e.workSec = Math.max(5, e.workSec); e.restBetweenRepsSec = active(e.restBetweenRepsSec, 5); });
      });
      copy.timingVersion = 2;
    }
    return copy;
  }
  if (copy.activities === undefined) {
    const activities = [], boundaries = [], ids = new Set([copy.id]);
    copy.blocks.forEach((b, bi) => {
      for (let c = 0; c < b.cycles; c++) {
        b.exercises.forEach((e, ei) => {
          let id = `legacy-${copy.id.slice(0, 80)}-${activities.length + 1}`;
          while (ids.has(id)) id += '_x';
          if (activities.length >= 5000) throw new Error('Troppe attività nel programma precedente');
          ids.add(id); activities.push({ id, title: e.title, concentrationSec: e.workSec, sessions: e.repetitions, pauseSec: e.restBetweenRepsSec });
          if (ei < b.exercises.length - 1) boundaries.push(e.restAfterExerciseSec);
        });
        if (c < b.cycles - 1) boundaries.push(b.restBetweenCyclesSec);
      }
      if (bi < copy.blocks.length - 1) boundaries.push(b.restAfterBlockSec);
    });
    copy.activities = activities; copy.restBetweenActivitiesSec = boundaries[0] ?? 300;
  }
  copy.blocks = []; copy.warmupSec = 0; copy.cooldownSec = 0;
  delete copy.longBreakSec; delete copy.longBreakEvery;
  return copy;
}
export function normalizationNotice(w) {
  if (isStudio(w) && w.activities === undefined) return 'Programma precedente: le serie diventano attività; pause lunghe e finali sono escluse. La pausa tra attività usa il primo valore precedente. L’originale resta nell’archivio fino al salvataggio.';
  if (!isStudio(w) && w.timingVersion !== 2 && (JSON.stringify(normalizeWorkout(w), (key, value) => key === 'timingVersion' ? undefined : value) !== JSON.stringify(w) || w.blocks.some(b => b.cycles > 1 || b.exercises.length > 1))) return 'Allenamento precedente: uso il recupero personale anche tra esercizi e cicli; tra blocchi lo sostituisce il recupero del blocco. Applico i nuovi minimi alla sola copia di esecuzione. Controlla la durata in anteprima. L’originale resta nell’archivio fino al salvataggio.';
  return '';
}
export function duplicateWorkout(w) {
  const copy = normalizeWorkout(w); copy.id = uid(); copy.title = t("{0} (copia)", w.title.slice(0, 110));
  if (isStudio(copy)) { copy.activities.forEach(a => { a.id = uid(); }); return copy; }
  copy.blocks.forEach(b => { b.id = uid(); b.exercises.forEach(e => { e.id = uid(); }); });
  return copy;
}
export function presets(mode = 'WORKOUT') {
  if (mode === 'STUDIO') return [[25, 5], [50, 10]].map(([focus, rest]) => {
    const w = newWorkout('STUDIO'); w.title = `Studio ${focus}/${rest}`;
    Object.assign(w.activities[0], { concentrationSec: focus * 60, pauseSec: rest * 60 });
    return w;
  });
  const classic = newWorkout(); classic.title = t("Tabata classico"); classic.description = t("Otto intervalli di lavoro. Nessun recupero dopo l’ultimo.");
  Object.assign(classic.blocks[0], { title: 'Tabata', restBetweenCyclesSec: 0, restAfterBlockSec: 0 });
  Object.assign(classic.blocks[0].exercises[0], { title: t("Esercizio libero"), repetitions: 8, workSec: 20, restBetweenRepsSec: 10, restAfterExerciseSec: 0 });
  const circuit = newWorkout(); circuit.title = t("Circuito completo"); circuit.color = '#b54079'; circuit.description = t("Tre movimenti, due giri. Personalizza il tuo ritmo."); circuit.warmupSec = 0; circuit.cooldownSec = 60;
  circuit.blocks[0].title = t("Circuito"); circuit.blocks[0].cycles = 2;
  circuit.blocks[0].exercises = ['Squat', 'Push-up', 'Plank'].map(title => ({ ...newExercise(), title }));
  return [classic, circuit];
}
