// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { object, text, id, integer } from './model.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { object, text, id, integer } from './model.js';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import { compileTimeline } from './timeline.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { compileTimeline } from './timeline.js';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import { MODES, modeOf } from './modes.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { MODES, modeOf } from './modes.js';
// DOC L0004: MAX_BACKUP_BYTES: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
export const MAX_BACKUP_BYTES = 5 * 1024 * 1024;
// DOC L0005: timestamp: Controlla la forma UTC della data e verifica che sia un istante realmente valido.
function timestamp(value, path) {
  // DOC L0006: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di timestamp.
  if (typeof value !== 'string' || !/^\d{4}-\d\d-\d\dT\d\d:\d\d:\d\d(?:\.\d{1,3})?Z$/.test(value) || !Number.isFinite(Date.parse(value)) || new Date(value).toISOString().slice(0, 19) !== value.slice(0, 19)) throw new Error(`${path}: data UTC non valida`);
// DOC L0007: Chiude il costrutto aperto alla riga 5.
}
// DOC L0008: validateSession: Rifiuta sessioni con date, identificativi, stato o metriche incoerenti.
export function validateSession(s) {
  // DOC L0009: Esegue il passaggio esplicito «object(s, 'Sessione'); id(s.id, 'session.id'); id(s.workoutId, 'workoutId');» nel flusso: Importazione, esportazione JSON e CSV sul web. Nel contesto di validateSession.
  object(s, 'Sessione'); id(s.id, 'session.id'); id(s.workoutId, 'workoutId');
  // DOC L0010: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di validateSession.
  if (s.mode !== undefined && !Object.hasOwn(MODES, s.mode)) throw new Error('Modalità sessione non valida');
  // DOC L0011: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di validateSession.
  if (s.workoutTitle !== undefined) text(s.workoutTitle, 'Nome storico', 120, true);
  // DOC L0012: Esegue il passaggio esplicito «timestamp(s.startedAt, 'Inizio'); timestamp(s.endedAt, 'Fine');» nel flusso: Importazione, esportazione JSON e CSV sul web. Nel contesto di validateSession.
  timestamp(s.startedAt, 'Inizio'); timestamp(s.endedAt, 'Fine');
  // DOC L0013: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di validateSession.
  if (Date.parse(s.endedAt) < Date.parse(s.startedAt)) throw new Error('Fine precedente all’inizio');
  // DOC L0014: Esegue il passaggio esplicito «integer(s.plannedMs, 'Pianificato', 1, 86400000); integer(s.elapsedMs, 'Effettivo', 0, Number.MAX_SAFE_INTEGER); integer(s.workMs, 'Lavoro', 0, s.elapsedMs);» nel flusso: Importazione, esportazione JSON e CSV sul web. Nel contesto di validateSession.
  integer(s.plannedMs, 'Pianificato', 1, 86400000); integer(s.elapsedMs, 'Effettivo', 0, Number.MAX_SAFE_INTEGER); integer(s.workMs, 'Lavoro', 0, s.elapsedMs);
  // DOC L0015: Esegue il passaggio esplicito «integer(s.plannedWorkIntervals, 'Intervalli pianificati', 1, 10000); integer(s.completedWorkIntervals, 'Intervalli completati', 0, s.plannedWorkIntervals); integer(s.pauses, 'Pause» nel flusso: Importazione, esportazione JSON e CSV sul web. Nel contesto di validateSession.
  integer(s.plannedWorkIntervals, 'Intervalli pianificati', 1, 10000); integer(s.completedWorkIntervals, 'Intervalli completati', 0, s.plannedWorkIntervals); integer(s.pauses, 'Pause', 0, 2147483647);
  // DOC L0016: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di validateSession.
  if (s.completedPhases !== undefined || s.plannedPhases !== undefined) { integer(s.plannedPhases, 'Fasi pianificate', s.plannedWorkIntervals, 10000); integer(s.completedPhases, 'Fasi completate', s.completedWorkIntervals, s.plannedPhases); }
  // DOC L0017: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di validateSession.
  if (!['COMPLETED', 'INTERRUPTED'].includes(s.status) || (s.status === 'COMPLETED' && s.completedWorkIntervals !== s.plannedWorkIntervals)) throw new Error('Stato sessione non valido');
  // DOC L0018: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di validateSession.
  return s;
// DOC L0019: Chiude il costrutto aperto alla riga 8.
}
// DOC L0020: parseBackup: Valida dimensione, JSON, versione, programmi e sessioni prima di proporre la sostituzione dell’archivio.
export function parseBackup(raw) {
  // DOC L0021: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di parseBackup.
  if (typeof raw !== 'string' || new TextEncoder().encode(raw).length > MAX_BACKUP_BYTES) throw new Error('Backup troppo grande: massimo 5 MiB');
  // DOC L0022: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. value: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di parseBackup.
  let value; try { value = JSON.parse(raw); } catch { throw new Error('JSON non valido'); }
  // DOC L0023: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di parseBackup.
  object(value, 'Backup'); if (value.schemaVersion !== 1) throw new Error('Versione backup non supportata'); timestamp(value.exportedAt, 'Esportazione');
  // DOC L0024: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di parseBackup.
  if (!Array.isArray(value.workouts) || value.workouts.length > 500 || !Array.isArray(value.sessions) || value.sessions.length > 10000) throw new Error('Collezioni backup non valide o troppo grandi');
  // DOC L0025: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di parseBackup.
  for (const [items, validate] of [[value.workouts, compileTimeline], [value.sessions, validateSession]]) {
    // DOC L0026: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. ids: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. item: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di parseBackup.
    const ids = new Set(); for (const item of items) { validate(item); if (ids.has(item.id)) throw new Error('ID duplicato nel backup'); ids.add(item.id); }
  // DOC L0027: Chiude il costrutto aperto alla riga 25.
  }
  // DOC L0028: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di parseBackup.
  return value;
// DOC L0029: Chiude il costrutto aperto alla riga 20.
}
// DOC L0030: makeBackup: Serializza programmi e sessioni con versione e data di esportazione del backup.
export const makeBackup = data => JSON.stringify({ schemaVersion: 1, exportedAt: new Date().toISOString(), workouts: data.workouts, sessions: data.sessions }, null, 2);
// DOC L0031: sessionsCsv: Esporta le sessioni in CSV con intestazione, virgolette e neutralizzazione delle formule.
export function sessionsCsv(sessions) {
  // DOC L0032: fields: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di sessionsCsv.
  const fields = ['id', 'mode', 'workoutId', 'workoutTitle', 'startedAt', 'endedAt', 'status', 'plannedMs', 'elapsedMs', 'workMs', 'completedWorkIntervals', 'plannedWorkIntervals', 'completedPhases', 'plannedPhases', 'pauses'];
  // Apostrophe guards spreadsheet formula injection when users choose to open CSV in a spreadsheet.
  // DOC L0034: cell: Prepara una cella CSV, raddoppiando le virgolette e proteggendo i prefissi interpretabili come formule.
  const cell = value => `"${String(value ?? '').replace(/^[=+@\-\t\r]/, "'$&").replaceAll('"', '""')}"`;
  // DOC L0035: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Restituisce il valore calcolato o conclude il percorso della funzione.
  return '\uFEFF' + [fields.map(cell).join(','), ...sessions.map(s => fields.map(f => cell(f === 'mode' ? modeOf(s) : s[f])).join(','))].join('\r\n');
// DOC L0036: Chiude il costrutto aperto alla riga 31.
}
// DOC L0037: download: Crea un file Blob scaricabile nel browser e rilascia il suo URL temporaneo.
export function download(content, filename, type) {
  // DOC L0038: url: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const url = URL.createObjectURL(new Blob([content], { type })), a = document.createElement('a'); a.href = url; a.download = filename; a.click(); setTimeout(() => URL.revokeObjectURL(url), 1000);
// DOC L0039: Chiude il costrutto aperto alla riga 37.
}
