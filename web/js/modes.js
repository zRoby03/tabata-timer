// DOC L0001: MODES: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
export const MODES = {
  // DOC L0002: Esegue il passaggio esplicito «WORKOUT: { title: 'Tabata Workout', subtitle: 'Allenamento a intervalli', collection: 'Allenamenti', noun: 'allenamento', focus: 'Lavoro', rest: 'Recupero', rounds: 'Giri', final:» nel flusso: Separazione Workout/Studio e conversione delle unità.
  WORKOUT: { title: 'Tabata Workout', subtitle: 'Allenamento a intervalli', collection: 'Allenamenti', noun: 'allenamento', focus: 'Lavoro', rest: 'Recupero', rounds: 'Giri', final: 'Defaticamento' },
  // DOC L0003: Esegue il passaggio esplicito «STUDIO: { title: 'Tabata Studio', subtitle: 'Timer Pomodoro per studio e lavoro', collection: 'Programmi di studio', noun: 'programma di studio', focus: 'Concentrazione', rest: 'Pa» nel flusso: Separazione Workout/Studio e conversione delle unità.
  STUDIO: { title: 'Tabata Studio', subtitle: 'Timer Pomodoro per studio e lavoro', collection: 'Programmi di studio', noun: 'programma di studio', focus: 'Concentrazione', rest: 'Pausa', rounds: 'Sessioni', final: 'Pausa finale' }
// DOC L0004: Chiude il costrutto aperto alla riga 1.
};
// DOC L0005: modeOf: Legge la modalità; i dati privi del campo mode appartengono a Workout.
export const modeOf = value => value?.mode ?? 'WORKOUT';
// DOC L0006: isStudio: Determina se il programma appartiene alla modalità Studio.
export const isStudio = value => modeOf(value) === 'STUDIO';
// DOC L0007: unitFactor: Restituisce 60 per i minuti Studio e 1 per i secondi Workout.
export const unitFactor = value => isStudio(value) ? 60 : 1;
// DOC L0008: modeData: Crea una vista dell’archivio contenente soltanto programmi e sessioni della modalità richiesta.
export const modeData = (data, mode) => ({ workouts: data.workouts.filter(w => modeOf(w) === mode), sessions: data.sessions.filter(s => modeOf(s) === mode) });
// DOC L0009: loadMode: Legge l’ultima modalità dal browser con fallback a Workout anche se lo storage è negato.
export function loadMode(storage) { try { return storage?.getItem('tabata:mode') === 'STUDIO' ? 'STUDIO' : 'WORKOUT'; } catch { return 'WORKOUT'; } }
// DOC L0010: saveMode: Ricorda l’ultima modalità senza rendere inutilizzabile il timer se lo storage fallisce.
export function saveMode(storage, mode) { try { storage?.setItem('tabata:mode', mode); } catch { /* The current visit still works when storage is unavailable. */ } }
