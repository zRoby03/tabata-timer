// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { test } from 'node:test';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { test } from 'node:test';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import assert from 'node:assert/strict';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import assert from 'node:assert/strict';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import { LocalRepository, STORAGE_KEY } from '../js/storage.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { LocalRepository, STORAGE_KEY } from '../js/storage.js';
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import { parseBackup, makeBackup, sessionsCsv } from '../js/io.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { parseBackup, makeBackup, sessionsCsv } from '../js/io.js';
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import { aggregate } from '../js/stats.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { aggregate } from '../js/stats.js';
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import { newWorkout } from '../js/model.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { newWorkout } from '../js/model.js';
// DOC L0007: session: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const session = { id: 's1', workoutId: 'w', workoutTitle: '=SUM(A1)', startedAt: '2026-11-16T10:00:00.000Z', endedAt: '2026-11-16T10:01:00.000Z', plannedMs: 60000, elapsedMs: 40000, workMs: 20000, completedWorkIntervals: 1, plannedWorkIntervals: 2, pauses: 1, status: 'INTERRUPTED' };
// DOC L0008: memory: Prepara o verifica il caso di regressione «memory»; le asserzioni nel corpo specificano l’esito atteso. getItem: Prepara o verifica il caso di regressione «get Item»; le asserzioni nel corpo specificano l’esito atteso. setItem: Prepara o verifica il caso di regressione «set Item»; le asserzioni nel corpo specificano l’esito atteso. removeItem: Prepara o verifica il caso di regressione «remove Item»; le asserzioni nel corpo specificano l’esito atteso.
function memory() { const map = new Map(); return { writes: 0, getItem: k => map.get(k), setItem(k, v) { this.writes++; map.set(k, v); }, removeItem(k) { this.writes++; map.delete(k); } }; }
// DOC L0009: Esegue il passaggio esplicito «test('temporanea zero scritture, persistenza e reload', () => {» nel flusso: Prove di regressione in storage-io-stats.test.js.
test('temporanea zero scritture, persistenza e reload', () => {
  // DOC L0010: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. storage: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const storage = memory(), repo = new LocalRepository(storage); repo.upsertWorkout(newWorkout()); repo.addSession(session); assert.equal(storage.writes, 0);
  // DOC L0011: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(repo.enable(), true); const reloaded = new LocalRepository(storage); reloaded.load(parseBackup); assert.equal(reloaded.data.sessions.length, 1); assert.equal(reloaded.persistent, true);
  // DOC L0012: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Legge il valore locale e applica i controlli/fallback riportati nell’espressione.
  reloaded.disable(); const before = storage.writes; reloaded.addSession({ ...session, id: 's2' }); assert.equal(storage.writes, before); assert.equal(storage.getItem(STORAGE_KEY), undefined);
// DOC L0013: Chiude il costrutto aperto alla riga 9.
});
// DOC L0014: Esegue il passaggio esplicito «test('salva sul dispositivo attiva la persistenza e conserva il nome al reload', () => {» nel flusso: Prove di regressione in storage-io-stats.test.js.
test('salva sul dispositivo attiva la persistenza e conserva il nome al reload', () => {
  // DOC L0015: storage: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const storage = memory(), repo = new LocalRepository(storage), w = newWorkout();
  // DOC L0016: Esegue il passaggio esplicito «w.title = 'La mia pausa attiva';» nel flusso: Prove di regressione in storage-io-stats.test.js.
  w.title = 'La mia pausa attiva';
  // DOC L0017: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(repo.saveWorkoutLocally(w), true);
  // DOC L0018: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(storage.writes, 1); assert.equal(repo.persistent, true);
  // DOC L0019: reopened: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const reopened = new LocalRepository(storage); reopened.load(parseBackup);
  // DOC L0020: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(reopened.data.workouts[0].title, 'La mia pausa attiva');
// DOC L0021: Chiude il costrutto aperto alla riga 14.
});
// DOC L0022: Esegue il passaggio esplicito «test('salvataggio locale fallito conserva RAM e snapshot senza duplicare un nuovo tentativo', () => {» nel flusso: Prove di regressione in storage-io-stats.test.js.
test('salvataggio locale fallito conserva RAM e snapshot senza duplicare un nuovo tentativo', () => {
  // DOC L0023: storage: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const storage = memory(), repo = new LocalRepository(storage), w = newWorkout();
  // DOC L0024: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti.
  storage.setItem = () => { throw new Error('quota'); };
  // DOC L0025: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(repo.saveWorkoutLocally(w), false); assert.equal(repo.persistent, false);
  // DOC L0026: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(repo.saveWorkoutLocally(w), false); assert.equal(repo.data.workouts.length, 1);
// DOC L0027: Chiude il costrutto aperto alla riga 22.
});
// DOC L0028: Esegue il passaggio esplicito «test('un errore non disattiva una persistenza già scelta', () => {» nel flusso: Prove di regressione in storage-io-stats.test.js.
test('un errore non disattiva una persistenza già scelta', () => {
  // DOC L0029: storage: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const storage = memory(), repo = new LocalRepository(storage); repo.enable();
  // DOC L0030: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Legge il valore locale e applica i controlli/fallback riportati nell’espressione. snapshot: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const snapshot = storage.getItem(STORAGE_KEY); storage.setItem = () => { throw new Error('quota'); };
  // DOC L0031: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(repo.saveWorkoutLocally(newWorkout()), false);
  // DOC L0032: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Legge il valore locale e applica i controlli/fallback riportati nell’espressione.
  assert.equal(repo.persistent, true); assert.equal(storage.getItem(STORAGE_KEY), snapshot);
// DOC L0033: Chiude il costrutto aperto alla riga 28.
});
// DOC L0034: Esegue il passaggio esplicito «test('quota e storage indisponibile preservano RAM e snapshot', () => {» nel flusso: Prove di regressione in storage-io-stats.test.js.
test('quota e storage indisponibile preservano RAM e snapshot', () => {
  // DOC L0035: Legge il valore locale e applica i controlli/fallback riportati nell’espressione. warnings: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. old: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const warnings = [], storage = memory(), repo = new LocalRepository(storage, m => warnings.push(m)); repo.upsertWorkout(newWorkout()); repo.enable(); const old = storage.getItem(STORAGE_KEY);
  // DOC L0036: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Legge il valore locale e applica i controlli/fallback riportati nell’espressione.
  storage.setItem = () => { throw new Error('quota'); }; repo.addSession(session); assert.equal(repo.data.sessions.length, 1); assert.equal(storage.getItem(STORAGE_KEY), old); assert.equal(warnings.length, 1);
  // DOC L0037: getItem: Prepara o verifica il caso di regressione «get Item»; le asserzioni nel corpo specificano l’esito atteso.
  const denied = new LocalRepository({ getItem() { throw new Error('denied'); } }, m => warnings.push(m)); denied.load(parseBackup); assert.equal(denied.persistent, false);
// DOC L0038: Chiude il costrutto aperto alla riga 34.
});
// DOC L0039: Esegue il passaggio esplicito «test('corruzione non cancellata e sessioni non duplicate', () => {» nel flusso: Prove di regressione in storage-io-stats.test.js.
test('corruzione non cancellata e sessioni non duplicate', () => {
  // DOC L0040: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Scrive la preferenza o la copia locale prevista, senza inviarla a un server. Legge il valore locale e applica i controlli/fallback riportati nell’espressione. storage: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. repo: repository browser che coordina memoria, validazione e persistenza locale.
  const storage = memory(); storage.setItem(STORAGE_KEY, 'invalid'); const repo = new LocalRepository(storage); repo.load(parseBackup); assert.equal(storage.getItem(STORAGE_KEY), 'invalid'); repo.addSession(session); repo.addSession(session); assert.equal(repo.data.sessions.length, 1);
// DOC L0041: Chiude il costrutto aperto alla riga 39.
});
// DOC L0042: Esegue il passaggio esplicito «test('JSON roundtrip, CSV neutralizza formule', () => {» nel flusso: Prove di regressione in storage-io-stats.test.js.
test('JSON roundtrip, CSV neutralizza formule', () => {
  // DOC L0043: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. raw: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const raw = makeBackup({ workouts: [newWorkout()], sessions: [session] }); assert.equal(parseBackup(raw).sessions[0].id, 's1'); assert.match(sessionsCsv([session]), /'=SUM/);
// DOC L0044: Chiude il costrutto aperto alla riga 42.
});
// DOC L0045: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
for (const [name, change] of [
  // DOC L0046: Esegue il passaggio esplicito «['versione', x => x.schemaVersion = 2], ['duplicate', x => x.sessions.push(session)], ['date', x => x.sessions[0].startedAt = '2026-02-30T00:00:00Z'],» nel flusso: Prove di regressione in storage-io-stats.test.js.
  ['versione', x => x.schemaVersion = 2], ['duplicate', x => x.sessions.push(session)], ['date', x => x.sessions[0].startedAt = '2026-02-30T00:00:00Z'],
  // DOC L0047: Esegue il passaggio esplicito «['lavoro', x => x.sessions[0].workMs = 999999], ['intervalli', x => x.sessions[0].completedWorkIntervals = 3], ['stato', x => x.sessions[0].status = 'COMPLETED']» nel flusso: Prove di regressione in storage-io-stats.test.js.
  ['lavoro', x => x.sessions[0].workMs = 999999], ['intervalli', x => x.sessions[0].completedWorkIntervals = 3], ['stato', x => x.sessions[0].status = 'COMPLETED']
// DOC L0048: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
]) test(`backup rifiuta ${name}`, () => { const value = JSON.parse(makeBackup({ workouts: [], sessions: [session] })); change(value); assert.throws(() => parseBackup(JSON.stringify(value))); });
// DOC L0049: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
test('backup troppo grande o JSON malformato', () => { assert.throws(() => parseBackup(' '.repeat(5 * 1024 * 1024 + 1))); assert.throws(() => parseBackup('{')); });
// DOC L0050: Esegue il passaggio esplicito «test('statistiche settimana UTC, filtro workout e media', () => {» nel flusso: Prove di regressione in storage-io-stats.test.js.
test('statistiche settimana UTC, filtro workout e media', () => {
  // DOC L0051: now: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. all: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const now = new Date('2026-11-17T12:00:00Z'); const all = aggregate([session, { ...session, id: 's2', workoutId: 'other', workMs: 40000 }], 7, '', now);
  // DOC L0052: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(all.count, 2); assert.equal(all.completion, 50); assert.equal(all.workMs, 60000); assert.equal(all.weeks.find(w => w.week === '2026-11-16').count, 2);
  // DOC L0053: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(aggregate([session], 7, 'other', now).count, 0); assert.equal(aggregate([], 7, '', now).completion, 0);
// DOC L0054: Chiude il costrutto aperto alla riga 50.
});
