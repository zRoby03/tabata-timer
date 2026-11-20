export const MODES = {
  WORKOUT: { title: 'Tabata Workout', subtitle: 'Allenamento a intervalli', collection: 'Allenamenti', noun: 'allenamento', focus: 'Lavoro', rest: 'Recupero', rounds: 'Giri', final: 'Defaticamento' },
  STUDIO: { title: 'Tabata Studio', subtitle: 'Timer Pomodoro per studio e lavoro', collection: 'Programmi di studio', noun: 'programma di studio', focus: 'Concentrazione', rest: 'Pausa', rounds: 'Sessioni', final: 'Pausa finale' }
};
export const modeOf = value => value?.mode ?? 'WORKOUT';
export const isStudio = value => modeOf(value) === 'STUDIO';
export const unitFactor = value => isStudio(value) ? 60 : 1;
export const modeData = (data, mode) => ({ workouts: data.workouts.filter(w => modeOf(w) === mode), sessions: data.sessions.filter(s => modeOf(s) === mode) });
export function loadMode(storage) { try { return storage?.getItem('tabata:mode') === 'STUDIO' ? 'STUDIO' : 'WORKOUT'; } catch { return 'WORKOUT'; } }
export function saveMode(storage, mode) { try { storage?.setItem('tabata:mode', mode); } catch { /* The current visit still works when storage is unavailable. */ } }
