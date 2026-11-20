// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { t } from './i18n.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { t } from './i18n.js';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import { MODES, isStudio } from './modes.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { MODES, isStudio } from './modes.js';
// DOC L0003: SCHEMA_VERSION: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
export const SCHEMA_VERSION = 1;
// DOC L0004: LIMITS: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
export const LIMITS = Object.freeze({ blocks: 50, exercises: 100, repeats: 100, seconds: 3600, phases: 10000, totalMs: 86400000 });
// DOC L0005: uid: Genera un UUID per distinguere programmi, elementi e sessioni senza collisioni volontarie.
export const uid = () => globalThis.crypto.randomUUID();
// DOC L0006: clone: Copia il valore strutturato per poterlo modificare senza alterare l’originale salvato.
export const clone = value => structuredClone(value);
// DOC L0007: formatTime: Converte millisecondi in m:ss o h:mm:ss, arrotondando il residuo verso l’alto e impedendo valori negativi.
export const formatTime = ms => {
  // DOC L0008: s: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di formatTime.
  const s = Math.ceil(Math.max(0, ms) / 1000);
  // DOC L0009: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di formatTime.
  return s >= 3600 ? `${Math.floor(s / 3600)}:${String(Math.floor(s / 60) % 60).padStart(2, '0')}:${String(s % 60).padStart(2, '0')}` : `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}`;
// DOC L0010: Chiude il costrutto aperto alla riga 7.
};
// DOC L0011: object: Rifiuta null, array e valori primitivi quando lo schema richiede un oggetto.
export function object(value, path) {
  // DOC L0012: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di object.
  if (!value || typeof value !== 'object' || Array.isArray(value)) throw new Error(`${path}: oggetto richiesto`);
// DOC L0013: Chiude il costrutto aperto alla riga 11.
}
// DOC L0014: text: Controlla tipo, lunghezza e presenza del testo richiesto dallo schema.
export function text(value, path, max = 120, empty = false) {
  // DOC L0015: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di text.
  if (typeof value !== 'string' || value.length > max || (!empty && !value.trim())) throw new Error(`${path}: testo non valido (max ${max})`);
// DOC L0016: Chiude il costrutto aperto alla riga 14.
}
// DOC L0017: integer: Accetta solo interi sicuri entro i limiti ammessi dal campo.
export function integer(value, path, min = 0, max = LIMITS.seconds) {
  // DOC L0018: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di integer.
  if (!Number.isSafeInteger(value) || value < min || value > max) throw new Error(`${path}: intero tra ${min} e ${max}`);
// DOC L0019: Chiude il costrutto aperto alla riga 17.
}
// DOC L0020: id: Valida il formato dell’identificativo usato nel backup.
export function id(value, path) {
  // DOC L0021: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di id.
  if (typeof value !== 'string' || !/^[A-Za-z0-9_-]{1,128}$/.test(value)) throw new Error(`${path}: ID non valido`);
// DOC L0022: Chiude il costrutto aperto alla riga 20.
}
// DOC L0023: validateWorkout: Controlla schema, modalità, durate, titoli, quantità e unicità degli ID prima di compilare o salvare il programma.
export function validateWorkout(w) {
  // DOC L0024: Esegue il passaggio esplicito «object(w, 'workout');» nel flusso: Modello, limiti, validazione e compatibilità degli allenamenti. Nel contesto di validateWorkout.
  object(w, 'workout');
  // DOC L0025: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di validateWorkout.
  if (w.schemaVersion !== 1) throw new Error('Versione allenamento non supportata');
  // DOC L0026: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di validateWorkout.
  if (w.mode !== undefined && !Object.hasOwn(MODES, w.mode)) throw new Error('Modalità non valida');
  // DOC L0027: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di validateWorkout.
  if (w.longBreakSec !== undefined) integer(w.longBreakSec, 'Pausa lunga');
  // DOC L0028: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di validateWorkout.
  if (w.longBreakEvery !== undefined) integer(w.longBreakEvery, 'Frequenza pausa lunga', 2, 100);
  // DOC L0029: Esegue il passaggio esplicito «id(w.id, 'workout.id'); text(w.title, 'Titolo'); text(w.description, 'Descrizione', 2000, true);» nel flusso: Modello, limiti, validazione e compatibilità degli allenamenti. Nel contesto di validateWorkout.
  id(w.id, 'workout.id'); text(w.title, 'Titolo'); text(w.description, 'Descrizione', 2000, true);
  // DOC L0030: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di validateWorkout.
  if (w.color !== undefined && !/^#[0-9a-f]{6}$/i.test(w.color)) throw new Error('Colore non valido');
  // DOC L0031: seconds: Valida una durata in secondi e richiede multipli di 60 quando il programma appartiene a Studio.
  const seconds = (value, name, min = 0) => { integer(value, name, min); if (isStudio(w) && value % 60 !== 0) throw new Error(`${name}: Studio usa minuti interi (massimo 60)`); };
  // DOC L0032: Esegue il passaggio esplicito «seconds(w.warmupSec, 'Riscaldamento'); seconds(w.cooldownSec, 'Defaticamento');» nel flusso: Modello, limiti, validazione e compatibilità degli allenamenti. Nel contesto di validateWorkout.
  seconds(w.warmupSec, 'Riscaldamento'); seconds(w.cooldownSec, 'Defaticamento');
  // DOC L0033: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di validateWorkout.
  if (isStudio(w) && w.activities !== undefined) {
    // DOC L0034: Esegue il passaggio esplicito «seconds(w.restBetweenActivitiesSec, 'Pausa tra attività');» nel flusso: Modello, limiti, validazione e compatibilità degli allenamenti. Nel contesto di validateWorkout.
    seconds(w.restBetweenActivitiesSec, 'Pausa tra attività');
    // DOC L0035: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di validateWorkout.
    if (!Array.isArray(w.activities) || !w.activities.length || w.activities.length > 5000) throw new Error('Serve almeno un’attività (massimo 5000)');
    // DOC L0036: ids: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di validateWorkout.
    const ids = new Set([w.id]);
    // DOC L0037: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di validateWorkout.
    for (const a of w.activities) {
      // DOC L0038: Esegue il passaggio esplicito «object(a, 'Attività'); id(a.id, 'activity.id');» nel flusso: Modello, limiti, validazione e compatibilità degli allenamenti. Nel contesto di validateWorkout.
      object(a, 'Attività'); id(a.id, 'activity.id');
      // DOC L0039: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di validateWorkout.
      if (ids.has(a.id)) throw new Error('ID attività duplicato');
      // DOC L0040: Esegue il passaggio esplicito «ids.add(a.id); text(a.title, 'Nome attività');» nel flusso: Modello, limiti, validazione e compatibilità degli allenamenti. Nel contesto di validateWorkout.
      ids.add(a.id); text(a.title, 'Nome attività');
      // DOC L0041: Esegue il passaggio esplicito «seconds(a.concentrationSec, t("Concentrazione"), 60); integer(a.sessions, 'Sessioni', 1, 100); seconds(a.pauseSec, 'Pausa');» nel flusso: Modello, limiti, validazione e compatibilità degli allenamenti. Nel contesto di validateWorkout.
      seconds(a.concentrationSec, t("Concentrazione"), 60); integer(a.sessions, 'Sessioni', 1, 100); seconds(a.pauseSec, 'Pausa');
    // DOC L0042: Chiude il costrutto aperto alla riga 37.
    }
    // DOC L0043: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di validateWorkout.
    return w;
  // DOC L0044: Chiude il costrutto aperto alla riga 33.
  }
  // DOC L0045: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di validateWorkout.
  if (isStudio(w)) seconds(w.longBreakSec ?? 0, 'Pausa lunga');
  // DOC L0046: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di validateWorkout.
  if (!Array.isArray(w.blocks) || !w.blocks.length || w.blocks.length > LIMITS.blocks) throw new Error('Servono da 1 a 50 blocchi');
  // DOC L0047: ids: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di validateWorkout.
  const ids = new Set([w.id]);
  // DOC L0048: unique: Valida l’ID e lo inserisce nell’insieme locale per rilevare duplicati nello stesso programma.
  const unique = (value, path) => { id(value, path); if (ids.has(value)) throw new Error('ID duplicato nell’allenamento'); ids.add(value); };
  // DOC L0049: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di validateWorkout.
  for (const b of w.blocks) {
    // DOC L0050: Esegue il passaggio esplicito «object(b, 'Blocco'); unique(b.id, 'block.id'); text(b.title, 'Titolo blocco');» nel flusso: Modello, limiti, validazione e compatibilità degli allenamenti. Nel contesto di validateWorkout.
    object(b, 'Blocco'); unique(b.id, 'block.id'); text(b.title, 'Titolo blocco');
    // DOC L0051: Esegue il passaggio esplicito «integer(b.cycles, 'Cicli', 1, LIMITS.repeats); seconds(b.restBetweenCyclesSec, 'Recupero cicli'); seconds(b.restAfterBlockSec, 'Recupero blocchi');» nel flusso: Modello, limiti, validazione e compatibilità degli allenamenti. Nel contesto di validateWorkout.
    integer(b.cycles, 'Cicli', 1, LIMITS.repeats); seconds(b.restBetweenCyclesSec, 'Recupero cicli'); seconds(b.restAfterBlockSec, 'Recupero blocchi');
    // DOC L0052: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di validateWorkout.
    if (b.restBetweenExercisesSec !== undefined) seconds(b.restBetweenExercisesSec, 'Recupero tra esercizi');
    // DOC L0053: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di validateWorkout.
    if (!Array.isArray(b.exercises) || !b.exercises.length || b.exercises.length > LIMITS.exercises) throw new Error('Servono da 1 a 100 esercizi per blocco');
    // DOC L0054: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di validateWorkout.
    for (const e of b.exercises) {
      // DOC L0055: Esegue il passaggio esplicito «object(e, 'Esercizio'); unique(e.id, 'exercise.id'); text(e.title, 'Titolo esercizio');» nel flusso: Modello, limiti, validazione e compatibilità degli allenamenti. Nel contesto di validateWorkout.
      object(e, 'Esercizio'); unique(e.id, 'exercise.id'); text(e.title, 'Titolo esercizio');
      // DOC L0056: Esegue il passaggio esplicito «integer(e.repetitions, 'Ripetizioni', 1, LIMITS.repeats); seconds(e.workSec, 'Lavoro', 1); seconds(e.restBetweenRepsSec, 'Recupero ripetizioni'); seconds(e.restAfterExerciseSec, 'R» nel flusso: Modello, limiti, validazione e compatibilità degli allenamenti. Nel contesto di validateWorkout.
      integer(e.repetitions, 'Ripetizioni', 1, LIMITS.repeats); seconds(e.workSec, 'Lavoro', 1); seconds(e.restBetweenRepsSec, 'Recupero ripetizioni'); seconds(e.restAfterExerciseSec, 'Recupero esercizi');
    // DOC L0057: Chiude il costrutto aperto alla riga 54.
    }
  // DOC L0058: Chiude il costrutto aperto alla riga 49.
  }
  // DOC L0059: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di validateWorkout.
  return w;
// DOC L0060: Chiude il costrutto aperto alla riga 23.
}
// DOC L0061: newExercise: Crea un esercizio con identificativo nuovo e tempi iniziali coerenti con la modalità.
export const newExercise = (mode = 'WORKOUT') => ({ id: uid(), title: mode === 'STUDIO' ? t("Concentrazione") : t("Nuovo esercizio"), repetitions: 1, workSec: mode === 'STUDIO' ? 1500 : 30, restBetweenRepsSec: mode === 'STUDIO' ? 300 : 10, restAfterExerciseSec: mode === 'STUDIO' ? 300 : 15 });
// DOC L0062: newStudyActivity: Crea un’attività Studio con 25 minuti di concentrazione, quattro sessioni e pausa di cinque minuti.
export const newStudyActivity = () => ({ id: uid(), title: t("Concentrazione"), concentrationSec: 1500, sessions: 4, pauseSec: 300 });
// DOC L0063: newBlock: Crea un blocco Workout con un esercizio e il recupero comune tra esercizi.
export const newBlock = () => ({ id: uid(), title: t("Nuovo blocco"), cycles: 1, restBetweenCyclesSec: 60, restAfterBlockSec: 60, restBetweenExercisesSec: 15, exercises: [newExercise()] });
// DOC L0064: newWorkout: Crea un programma nuovo della modalità richiesta, con blocchi Workout oppure attività Studio.
export const newWorkout = (mode = 'WORKOUT') => ({ schemaVersion: 1, mode, id: uid(), title: mode === 'STUDIO' ? t("Il mio studio") : t("Il mio allenamento"), description: '', color: '#9153b7', warmupSec: 0, cooldownSec: 0, blocks: mode === 'STUDIO' ? [] : [newBlock()], ...(mode === 'STUDIO' ? { activities: [newStudyActivity()], restBetweenActivitiesSec: 300 } : {}) });

// Reading/previewing old records never rewrites the archive. Saving the edited copy adopts the new form.
// DOC L0067: normalizeWorkout: Restituisce una copia compatibile con il modello semplice: non effettua scritture nell’archivio.
export function normalizeWorkout(w) {
  // DOC L0068: copy: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di normalizeWorkout.
  const copy = clone(w);
  // DOC L0069: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di normalizeWorkout.
  if (!isStudio(copy)) {
    // DOC L0070: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    copy.blocks.forEach(b => { b.restBetweenExercisesSec ??= b.exercises[0]?.restAfterExerciseSec ?? 15; });
    // DOC L0071: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di normalizeWorkout.
    return copy;
  // DOC L0072: Chiude il costrutto aperto alla riga 69.
  }
  // DOC L0073: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di normalizeWorkout.
  if (copy.activities === undefined) {
    // DOC L0074: activities: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di normalizeWorkout.
    const activities = [], boundaries = [], ids = new Set([copy.id]);
    // DOC L0075: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    copy.blocks.forEach((b, bi) => {
      // DOC L0076: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
      for (let c = 0; c < b.cycles; c++) {
        // DOC L0077: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
        b.exercises.forEach((e, ei) => {
          // DOC L0078: id: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
          let id = `legacy-${copy.id.slice(0, 80)}-${activities.length + 1}`;
          // DOC L0079: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
          while (ids.has(id)) id += '_x';
          // DOC L0080: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
          if (activities.length >= 5000) throw new Error('Troppe attività nel programma precedente');
          // DOC L0081: Esegue il passaggio esplicito «ids.add(id); activities.push({ id, title: e.title, concentrationSec: e.workSec, sessions: e.repetitions, pauseSec: e.restBetweenRepsSec });» nel flusso: Modello, limiti, validazione e compatibilità degli allenamenti.
          ids.add(id); activities.push({ id, title: e.title, concentrationSec: e.workSec, sessions: e.repetitions, pauseSec: e.restBetweenRepsSec });
          // DOC L0082: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
          if (ei < b.exercises.length - 1) boundaries.push(e.restAfterExerciseSec);
        // DOC L0083: Chiude il costrutto aperto alla riga 77.
        });
        // DOC L0084: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
        if (c < b.cycles - 1) boundaries.push(b.restBetweenCyclesSec);
      // DOC L0085: Chiude il costrutto aperto alla riga 76.
      }
      // DOC L0086: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
      if (bi < copy.blocks.length - 1) boundaries.push(b.restAfterBlockSec);
    // DOC L0087: Chiude il costrutto aperto alla riga 75.
    });
    // DOC L0088: Esegue il passaggio esplicito «copy.activities = activities; copy.restBetweenActivitiesSec = boundaries[0] ?? 300;» nel flusso: Modello, limiti, validazione e compatibilità degli allenamenti. Nel contesto di normalizeWorkout.
    copy.activities = activities; copy.restBetweenActivitiesSec = boundaries[0] ?? 300;
  // DOC L0089: Chiude il costrutto aperto alla riga 73.
  }
  // DOC L0090: Esegue il passaggio esplicito «copy.blocks = []; copy.warmupSec = 0; copy.cooldownSec = 0;» nel flusso: Modello, limiti, validazione e compatibilità degli allenamenti. Nel contesto di normalizeWorkout.
  copy.blocks = []; copy.warmupSec = 0; copy.cooldownSec = 0;
  // DOC L0091: Esegue il passaggio esplicito «delete copy.longBreakSec; delete copy.longBreakEvery;» nel flusso: Modello, limiti, validazione e compatibilità degli allenamenti. Nel contesto di normalizeWorkout.
  delete copy.longBreakSec; delete copy.longBreakEvery;
  // DOC L0092: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di normalizeWorkout.
  return copy;
// DOC L0093: Chiude il costrutto aperto alla riga 67.
}
// DOC L0094: normalizationNotice: Spiega all’utente la conversione dei campi precedenti e quando l’originale verrà sostituito.
export function normalizationNotice(w) {
  // DOC L0095: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di normalizationNotice.
  if (isStudio(w) && w.activities === undefined) return 'Programma precedente: le serie diventano attività; pause lunghe e finali sono escluse. La pausa tra attività usa il primo valore precedente. L’originale resta nell’archivio fino al salvataggio.';
  // DOC L0096: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Restituisce il valore calcolato o conclude il percorso della funzione.
  if (!isStudio(w) && w.blocks.some(b => b.restBetweenExercisesSec === undefined && new Set(b.exercises.slice(0, -1).map(e => e.restAfterExerciseSec)).size > 1)) return 'Allenamento precedente: il recupero tra esercizi è ora unico per blocco e usa il primo valore precedente. L’originale resta nell’archivio fino al salvataggio.';
  // DOC L0097: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di normalizationNotice.
  return '';
// DOC L0098: Chiude il costrutto aperto alla riga 94.
}
// DOC L0099: duplicateWorkout: Duplica il programma normalizzato assegnando ID nuovi anche ai suoi elementi.
export function duplicateWorkout(w) {
  // DOC L0100: copy: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di duplicateWorkout.
  const copy = normalizeWorkout(w); copy.id = uid(); copy.title = t("{0} (copia)", w.title.slice(0, 110));
  // DOC L0101: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Restituisce il valore calcolato o conclude il percorso della funzione.
  if (isStudio(copy)) { copy.activities.forEach(a => { a.id = uid(); }); return copy; }
  // DOC L0102: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  copy.blocks.forEach(b => { b.id = uid(); b.exercises.forEach(e => { e.id = uid(); }); });
  // DOC L0103: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di duplicateWorkout.
  return copy;
// DOC L0104: Chiude il costrutto aperto alla riga 99.
}
// DOC L0105: presets: Costruisce programmi pronti distinti per modalità con durate effettive espresse internamente in secondi.
export function presets(mode = 'WORKOUT') {
  // DOC L0106: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Restituisce il valore calcolato o conclude il percorso della funzione.
  if (mode === 'STUDIO') return [[25, 5], [50, 10]].map(([focus, rest]) => {
    // DOC L0107: w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    const w = newWorkout('STUDIO'); w.title = `Studio ${focus}/${rest}`;
    // DOC L0108: Esegue il passaggio esplicito «Object.assign(w.activities[0], { concentrationSec: focus * 60, pauseSec: rest * 60 });» nel flusso: Modello, limiti, validazione e compatibilità degli allenamenti.
    Object.assign(w.activities[0], { concentrationSec: focus * 60, pauseSec: rest * 60 });
    // DOC L0109: Restituisce il valore calcolato o conclude il percorso della funzione.
    return w;
  // DOC L0110: Chiude il costrutto aperto alla riga 106.
  });
  // DOC L0111: classic: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di presets.
  const classic = newWorkout(); classic.title = t("Tabata classico"); classic.description = t("Otto intervalli di lavoro. Nessun recupero dopo l’ultimo.");
  // DOC L0112: Esegue il passaggio esplicito «Object.assign(classic.blocks[0], { title: 'Tabata', restBetweenCyclesSec: 0, restAfterBlockSec: 0 });» nel flusso: Modello, limiti, validazione e compatibilità degli allenamenti. Nel contesto di presets.
  Object.assign(classic.blocks[0], { title: 'Tabata', restBetweenCyclesSec: 0, restAfterBlockSec: 0 });
  // DOC L0113: Esegue il passaggio esplicito «Object.assign(classic.blocks[0].exercises[0], { title: t("Esercizio libero"), repetitions: 8, workSec: 20, restBetweenRepsSec: 10, restAfterExerciseSec: 0 });» nel flusso: Modello, limiti, validazione e compatibilità degli allenamenti. Nel contesto di presets.
  Object.assign(classic.blocks[0].exercises[0], { title: t("Esercizio libero"), repetitions: 8, workSec: 20, restBetweenRepsSec: 10, restAfterExerciseSec: 0 });
  // DOC L0114: circuit: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di presets.
  const circuit = newWorkout(); circuit.title = t("Circuito completo"); circuit.color = '#b54079'; circuit.description = t("Tre movimenti, due giri. Personalizza il tuo ritmo."); circuit.warmupSec = 0; circuit.cooldownSec = 60;
  // DOC L0115: Esegue il passaggio esplicito «circuit.blocks[0].title = t("Circuito"); circuit.blocks[0].cycles = 2;» nel flusso: Modello, limiti, validazione e compatibilità degli allenamenti. Nel contesto di presets.
  circuit.blocks[0].title = t("Circuito"); circuit.blocks[0].cycles = 2;
  // DOC L0116: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  circuit.blocks[0].exercises = ['Squat', 'Push-up', 'Plank'].map(title => ({ ...newExercise(), title }));
  // DOC L0117: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di presets.
  return [classic, circuit];
// DOC L0118: Chiude il costrutto aperto alla riga 105.
}
