// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { validateWorkout, normalizeWorkout, LIMITS } from './model.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { validateWorkout, normalizeWorkout, LIMITS } from './model.js';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import { isStudio } from './modes.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { isStudio } from './modes.js';
// DOC L0003: compileTimeline: Espande il programma in fasi cronologiche, saltando durate zero e pause dopo l’ultimo elemento.
export function compileTimeline(workout) {
  // DOC L0004: Esegue il passaggio esplicito «validateWorkout(workout);» nel flusso: Compilazione del programma in fasi elementari. Nel contesto di compileTimeline.
  validateWorkout(workout);
  // DOC L0005: Esegue il passaggio esplicito «workout = normalizeWorkout(workout); validateWorkout(workout);» nel flusso: Compilazione del programma in fasi elementari. Nel contesto di compileTimeline.
  workout = normalizeWorkout(workout); validateWorkout(workout);
  // DOC L0006: phases: sequenza cronologica compilata dal programma. total: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di compileTimeline.
  const phases = []; let total = 0;
  // DOC L0007: studio: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di compileTimeline.
  const studio = isStudio(workout);
  // DOC L0008: add: Aggiunge una fase non vuota, converte secondi in millisecondi e verifica i limiti di quantità e durata complessiva.
  const add = (type, sec, label, blockId = null, exerciseId = null) => {
    // DOC L0009: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di add.
    if (!sec) return;
    // DOC L0010: Esegue il passaggio esplicito «total += sec * 1000;» nel flusso: Compilazione del programma in fasi elementari. Nel contesto di add.
    total += sec * 1000;
    // DOC L0011: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di add.
    if (phases.length >= LIMITS.phases || total > LIMITS.totalMs) throw new Error('Allenamento troppo lungo: massimo 10.000 fasi e 24 ore');
    // DOC L0012: Esegue il passaggio esplicito «phases.push({ type, blockId, exerciseId, durationMs: sec * 1000, label });» nel flusso: Compilazione del programma in fasi elementari. Nel contesto di add.
    phases.push({ type, blockId, exerciseId, durationMs: sec * 1000, label });
  // DOC L0013: Chiude il costrutto aperto alla riga 8.
  };
  // DOC L0014: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di compileTimeline.
  if (studio) {
    // DOC L0015: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    workout.activities.forEach((a, i) => {
      // DOC L0016: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
      for (let session = 0; session < a.sessions; session++) {
        // DOC L0017: Esegue il passaggio esplicito «add('WORK', a.concentrationSec, `Attività ${i + 1} · ${a.title} · sessione ${session + 1}/${a.sessions}`, null, a.id);» nel flusso: Compilazione del programma in fasi elementari.
        add('WORK', a.concentrationSec, `Attività ${i + 1} · ${a.title} · sessione ${session + 1}/${a.sessions}`, null, a.id);
        // DOC L0018: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
        if (session < a.sessions - 1) add('REST_REP', a.pauseSec, 'Pausa tra sessioni', null, a.id);
      // DOC L0019: Chiude il costrutto aperto alla riga 16.
      }
      // DOC L0020: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
      if (i < workout.activities.length - 1) add('REST_ACTIVITY', workout.restBetweenActivitiesSec, 'Pausa tra attività', null, a.id);
    // DOC L0021: Chiude il costrutto aperto alla riga 15.
    });
    // DOC L0022: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di compileTimeline.
    return phases;
  // DOC L0023: Chiude il costrutto aperto alla riga 14.
  }
  // DOC L0024: Esegue il passaggio esplicito «add('WARMUP', workout.warmupSec, 'Riscaldamento');» nel flusso: Compilazione del programma in fasi elementari. Nel contesto di compileTimeline.
  add('WARMUP', workout.warmupSec, 'Riscaldamento');
  // DOC L0025: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  workout.blocks.forEach((b, bi) => {
    // DOC L0026: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    for (let c = 0; c < b.cycles; c++) {
      // DOC L0027: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
      b.exercises.forEach((e, ei) => {
        // DOC L0028: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
        for (let r = 0; r < e.repetitions; r++) {
          // DOC L0029: Esegue il passaggio esplicito «add('WORK', e.workSec, `${e.title} · giro ${c + 1}/${b.cycles} · ${r + 1}/${e.repetitions}`, b.id, e.id);» nel flusso: Compilazione del programma in fasi elementari.
          add('WORK', e.workSec, `${e.title} · giro ${c + 1}/${b.cycles} · ${r + 1}/${e.repetitions}`, b.id, e.id);
          // DOC L0030: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
          if (r < e.repetitions - 1) add('REST_REP', e.restBetweenRepsSec, 'Recupero ripetizioni', b.id, e.id);
        // DOC L0031: Chiude il costrutto aperto alla riga 28.
        }
        // DOC L0032: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
        if (ei < b.exercises.length - 1) add('REST_EXERCISE', b.restBetweenExercisesSec, 'Recupero tra esercizi', b.id, e.id);
      // DOC L0033: Chiude il costrutto aperto alla riga 27.
      });
      // DOC L0034: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
      if (c < b.cycles - 1) add('REST_CYCLE', b.restBetweenCyclesSec, 'Recupero cicli', b.id);
    // DOC L0035: Chiude il costrutto aperto alla riga 26.
    }
    // DOC L0036: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
    if (bi < workout.blocks.length - 1) add('REST_BLOCK', b.restAfterBlockSec, 'Recupero blocchi', b.id);
  // DOC L0037: Chiude il costrutto aperto alla riga 25.
  });
  // DOC L0038: Esegue il passaggio esplicito «add('COOLDOWN', workout.cooldownSec, 'Defaticamento');» nel flusso: Compilazione del programma in fasi elementari. Nel contesto di compileTimeline.
  add('COOLDOWN', workout.cooldownSec, 'Defaticamento');
  // DOC L0039: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di compileTimeline.
  return phases;
// DOC L0040: Chiude il costrutto aperto alla riga 3.
}
// DOC L0041: plannedMs: Somma le durate della timeline; i 20 secondi di preparazione restano fuori da questo totale.
export const plannedMs = phases => phases.reduce((sum, p) => sum + p.durationMs, 0);
