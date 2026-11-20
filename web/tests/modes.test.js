// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { test } from 'node:test';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { test } from 'node:test';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import assert from 'node:assert/strict';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import assert from 'node:assert/strict';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import { readFileSync } from 'node:fs';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { readFileSync } from 'node:fs';
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import { newStudyActivity, newWorkout, newExercise, normalizeWorkout, normalizationNotice, duplicateWorkout } from '../js/model.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { newStudyActivity, newWorkout, newExercise, normalizeWorkout, normalizationNotice, duplicateWorkout } from '../js/model.js';
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import { newQuickWorkout } from '../js/quick.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { newQuickWorkout } from '../js/quick.js';
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import { compileTimeline, plannedMs } from '../js/timeline.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { compileTimeline, plannedMs } from '../js/timeline.js';
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import { TimerEngine } from '../js/timer.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { TimerEngine } from '../js/timer.js';
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import { TransitionCues } from '../js/transitions.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { TransitionCues } from '../js/transitions.js';
// DOC L0009: Rende disponibili le dipendenze esplicite di questa riga: import { CountdownCues, hornSample } from '../js/countdown.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { CountdownCues, hornSample } from '../js/countdown.js';
// DOC L0010: Rende disponibili le dipendenze esplicite di questa riga: import { modeOf, modeData, loadMode, saveMode } from '../js/modes.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { modeOf, modeData, loadMode, saveMode } from '../js/modes.js';
// DOC L0011: Rende disponibili le dipendenze esplicite di questa riga: import { parseBackup, makeBackup } from '../js/io.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { parseBackup, makeBackup } from '../js/io.js';
// DOC L0012: Rende disponibili le dipendenze esplicite di questa riga: import { LocalRepository } from '../js/storage.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { LocalRepository } from '../js/storage.js';
// DOC L0013: Rende disponibili le dipendenze esplicite di questa riga: import { aggregate } from '../js/stats.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { aggregate } from '../js/stats.js';

// DOC L0015: Esegue il passaggio esplicito «test('Studio usa minuti, quattro sessioni e nessuna pausa finale', () => {» nel flusso: Prove di regressione in modes.test.js.
test('Studio usa minuti, quattro sessioni e nessuna pausa finale', () => {
  // DOC L0016: w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const w = newQuickWorkout('STUDIO'), phases = compileTimeline(w);
  // DOC L0017: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(plannedMs(phases), 115 * 60000);
  // DOC L0018: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(phases.filter(p => p.type === 'WORK').length, 4);
  // DOC L0019: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(phases.at(-1).type, 'WORK');
  // DOC L0020: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.throws(() => { w.activities[0].concentrationSec = 61; compileTimeline(w); });
// DOC L0021: Chiude il costrutto aperto alla riga 15.
});
// DOC L0022: Esegue il passaggio esplicito «test('Studio piatto: pause solo tra sessioni e tra attività, mai finali', () => {» nel flusso: Prove di regressione in modes.test.js.
test('Studio piatto: pause solo tra sessioni e tra attività, mai finali', () => {
  // DOC L0023: w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const w = newQuickWorkout('STUDIO');
  // DOC L0024: Esegue il passaggio esplicito «Object.assign(w.activities[0], { concentrationSec: 60, sessions: 2, pauseSec: 120 });» nel flusso: Prove di regressione in modes.test.js.
  Object.assign(w.activities[0], { concentrationSec: 60, sessions: 2, pauseSec: 120 });
  // DOC L0025: Esegue il passaggio esplicito «w.activities.push({ ...newStudyActivity(), concentrationSec: 180, sessions: 1, pauseSec: 3600 });» nel flusso: Prove di regressione in modes.test.js.
  w.activities.push({ ...newStudyActivity(), concentrationSec: 180, sessions: 1, pauseSec: 3600 });
  // DOC L0026: Esegue il passaggio esplicito «w.restBetweenActivitiesSec = 240;» nel flusso: Prove di regressione in modes.test.js.
  w.restBetweenActivitiesSec = 240;
  // DOC L0027: phases: sequenza cronologica compilata dal programma.
  const phases = compileTimeline(w);
  // DOC L0028: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  assert.deepEqual(phases.map(p => p.type), ['WORK', 'REST_REP', 'WORK', 'REST_ACTIVITY', 'WORK']);
  // DOC L0029: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  assert.deepEqual(phases.map(p => p.durationMs), [60, 120, 60, 240, 180].map(s => s * 1000));
  // DOC L0030: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(phases[1].label, 'Pausa tra sessioni'); assert.equal(phases[3].label, 'Pausa tra attività');
  // DOC L0031: now: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. timer: motore corrente, opzionale quando nessuna sessione è stata avviata.
  let now = 0; const timer = new TimerEngine(w, () => now); timer.start(); now = 20000 + plannedMs(phases); timer.tick();
  // DOC L0032: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(timer.result.workMs, 300000); assert.equal(timer.result.plannedMs, 660000); assert.equal(timer.result.plannedWorkIntervals, 3);
  // DOC L0033: Esegue il passaggio esplicito «w.restBetweenActivitiesSec = 0; w.activities[0].pauseSec = 0;» nel flusso: Prove di regressione in modes.test.js.
  w.restBetweenActivitiesSec = 0; w.activities[0].pauseSec = 0;
  // DOC L0034: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(compileTimeline(w).length, 3);
// DOC L0035: Chiude il costrutto aperto alla riga 22.
});

// DOC L0037: Esegue il passaggio esplicito «test('vecchio Studio: conversione esplicita, originale e cronologia intatti', () => {» nel flusso: Prove di regressione in modes.test.js.
test('vecchio Studio: conversione esplicita, originale e cronologia intatti', () => {
  // DOC L0038: backup: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const backup = parseBackup(readFileSync(new URL('../../fixtures/backup-modes-v1.json', import.meta.url), 'utf8'));
  // DOC L0039: original: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Lavora su una copia per mantenere distinta la bozza dal valore precedente.
  const original = structuredClone(backup); const w = backup.workouts[0];
  // DOC L0040: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.match(normalizationNotice(w), /precedente/);
  // DOC L0041: normalized: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const normalized = normalizeWorkout(w);
  // DOC L0042: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(normalized.activities.length, 1); assert.equal(normalized.blocks.length, 0);
  // DOC L0043: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(normalized.longBreakSec, undefined); assert.equal(normalized.cooldownSec, 0);
  // DOC L0044: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(plannedMs(compileTimeline(w)), 235 * 60000);
  // DOC L0045: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.deepEqual(backup, original); assert.deepEqual(parseBackup(makeBackup(backup)).workouts, original.workouts);
  // DOC L0046: legacy: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Lavora su una copia per mantenere distinta la bozza dal valore precedente.
  const legacy = structuredClone(w); legacy.blocks[0].cycles = 2; legacy.blocks[0].exercises[0].repetitions = 1;
  // DOC L0047: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(normalizeWorkout(legacy).activities.length, 2);
  // DOC L0048: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  assert.equal(new Set(duplicateWorkout(legacy).activities.map(a => a.id)).size, 2);
// DOC L0049: Chiude il costrutto aperto alla riga 37.
});

// DOC L0051: Esegue il passaggio esplicito «test('Workout: recupero unico di blocco, solo tra esercizi e ripetizioni conservate', () => {» nel flusso: Prove di regressione in modes.test.js.
test('Workout: recupero unico di blocco, solo tra esercizi e ripetizioni conservate', () => {
  // DOC L0052: w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const w = newWorkout(), b = w.blocks[0]; b.restBetweenExercisesSec = 7;
  // DOC L0053: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  b.exercises = [1, 2, 3].map(n => ({ ...newExercise(), title: `E${n}`, repetitions: 2, workSec: 10, restBetweenRepsSec: 3, restAfterExerciseSec: 99 }));
  // DOC L0054: phases: sequenza cronologica compilata dal programma.
  const phases = compileTimeline(w);
  // DOC L0055: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  assert.deepEqual(phases.filter(p => p.type === 'REST_EXERCISE').map(p => p.durationMs), [7000, 7000]);
  // DOC L0056: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(phases.filter(p => p.type === 'REST_REP').length, 3); assert.equal(phases.at(-1).type, 'WORK');
  // DOC L0057: Esegue il passaggio esplicito «delete b.restBetweenExercisesSec; b.exercises[1].restAfterExerciseSec = 20;» nel flusso: Prove di regressione in modes.test.js.
  delete b.restBetweenExercisesSec; b.exercises[1].restAfterExerciseSec = 20;
  // DOC L0058: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.match(normalizationNotice(w), /primo valore/); assert.equal(normalizeWorkout(w).blocks[0].restBetweenExercisesSec, 99);
// DOC L0059: Chiude il costrutto aperto alla riga 51.
});

// DOC L0061: Esegue il passaggio esplicito «test('nuovi campi Studio: valori interi, identità e limiti controllati', () => {» nel flusso: Prove di regressione in modes.test.js.
test('nuovi campi Studio: valori interi, identità e limiti controllati', () => {
  // DOC L0062: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  for (const change of [w => w.activities = [], w => w.activities[0].sessions = 0, w => w.activities[0].title = '', w => w.restBetweenActivitiesSec = 61, w => w.activities.push({ ...w.activities[0] })]) {
    // DOC L0063: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    const w = newQuickWorkout('STUDIO'); change(w); assert.throws(() => compileTimeline(w));
  // DOC L0064: Chiude il costrutto aperto alla riga 62.
  }
// DOC L0065: Chiude il costrutto aperto alla riga 61.
});

// DOC L0067: Esegue il passaggio esplicito «test('preparazione Studio: 20 secondi, tre bip alti e fischio all’avvio', () => {» nel flusso: Prove di regressione in modes.test.js.
test('preparazione Studio: 20 secondi, tre bip alti e fischio all’avvio', () => {
  // DOC L0068: clock: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. timer: motore corrente, opzionale quando nessuna sessione è stata avviata.
  let clock = 0; const timer = new TimerEngine(newQuickWorkout('STUDIO'), () => clock);
  // DOC L0069: countdown: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const countdown = new CountdownCues(), transitions = new TransitionCues(); timer.start();
  // DOC L0070: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(timer.remainingMs, 20000);
  // DOC L0071: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  for (const second of [3, 2, 1]) { clock = 20000 - second * 1000; timer.tick(); assert.equal(hornSample(countdown.read(timer), timer.currentPhase.type), `rest-${second}`); }
  // DOC L0072: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  clock = 20000; timer.tick(); assert.equal(transitions.read(timer), 'whistle'); assert.equal(timer.elapsedMs, 0);
  // DOC L0073: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  clock += 1500000; timer.tick(); assert.equal(transitions.read(timer), 'sigh');
  // DOC L0074: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  timer.stop(); assert.equal(timer.result.mode, 'STUDIO'); assert.equal(timer.result.workMs, 1500000);
// DOC L0075: Chiude il costrutto aperto alla riga 67.
});
// DOC L0076: Esegue il passaggio esplicito «test('archivio legacy rimane Workout; salvataggio, attività e statistiche separati', () => {» nel flusso: Prove di regressione in modes.test.js.
test('archivio legacy rimane Workout; salvataggio, attività e statistiche separati', () => {
  // DOC L0077: legacy: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const legacy = parseBackup(readFileSync(new URL('../../fixtures/backup-web-v1.json', import.meta.url), 'utf8'));
  // DOC L0078: w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. s: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const w = newQuickWorkout('STUDIO'); const s = { ...legacy.sessions[0], id: 'studio-session', workoutId: w.id, mode: 'STUDIO', workMs: 30000 };
  // DOC L0079: getItem: Prepara o verifica il caso di regressione «get Item»; le asserzioni nel corpo specificano l’esito atteso. setItem: Prepara o verifica il caso di regressione «set Item»; le asserzioni nel corpo specificano l’esito atteso.
  const values = new Map(), storage = { getItem: k => values.get(k), setItem: (k, v) => values.set(k, v) };
  // DOC L0080: repo: repository browser che coordina memoria, validazione e persistenza locale.
  const repo = new LocalRepository(storage); repo.data = legacy; repo.saveWorkoutLocally(w); repo.addSession(s);
  // DOC L0081: reopened: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const reopened = new LocalRepository(storage); reopened.load(parseBackup);
  // DOC L0082: studio: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const studio = modeData(reopened.data, 'STUDIO'), workout = modeData(reopened.data, 'WORKOUT');
  // DOC L0083: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(studio.workouts.length, 1); assert.equal(studio.sessions.length, 1);
  // DOC L0084: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(workout.workouts.length, legacy.workouts.length - 1); assert.equal(workout.sessions.length, legacy.sessions.length - 1);
  // DOC L0085: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(modeOf(workout.workouts[0]), 'WORKOUT');
  // DOC L0086: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(aggregate(studio.sessions, 36500).workMs, 30000);
  // DOC L0087: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.deepEqual(parseBackup(makeBackup(reopened.data)).workouts, reopened.data.workouts);
// DOC L0088: Chiude il costrutto aperto alla riga 76.
});
// DOC L0089: Esegue il passaggio esplicito «test('modalità ricordata separatamente dai dati; storage negato non blocca', () => {» nel flusso: Prove di regressione in modes.test.js.
test('modalità ricordata separatamente dai dati; storage negato non blocca', () => {
  // DOC L0090: getItem: Prepara o verifica il caso di regressione «get Item»; le asserzioni nel corpo specificano l’esito atteso. setItem: Prepara o verifica il caso di regressione «set Item»; le asserzioni nel corpo specificano l’esito atteso.
  const values = new Map(), storage = { getItem: k => values.get(k), setItem: (k, v) => values.set(k, v) };
  // DOC L0091: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  saveMode(storage, 'STUDIO'); assert.equal(loadMode(storage), 'STUDIO'); assert.equal(values.size, 1);
  // DOC L0092: getItem: Prepara o verifica il caso di regressione «get Item»; le asserzioni nel corpo specificano l’esito atteso. setItem: Prepara o verifica il caso di regressione «set Item»; le asserzioni nel corpo specificano l’esito atteso.
  const denied = { getItem() { throw Error(); }, setItem() { throw Error(); } };
  // DOC L0093: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.doesNotThrow(() => saveMode(denied, 'STUDIO')); assert.equal(loadMode(denied), 'WORKOUT');
// DOC L0094: Chiude il costrutto aperto alla riga 89.
});
// DOC L0095: Esegue il passaggio esplicito «test('backup rifiuta modalità sconosciute per programmi e attività', () => {» nel flusso: Prove di regressione in modes.test.js.
test('backup rifiuta modalità sconosciute per programmi e attività', () => {
  // DOC L0096: data: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const data = parseBackup(readFileSync(new URL('../../fixtures/backup-web-v1.json', import.meta.url), 'utf8'));
  // DOC L0097: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  data.workouts[0].mode = 'OTHER'; assert.throws(() => parseBackup(makeBackup(data)));
  // DOC L0098: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  delete data.workouts[0].mode; data.sessions[0].mode = 'OTHER'; assert.throws(() => parseBackup(makeBackup(data)));
// DOC L0099: Chiude il costrutto aperto alla riga 95.
});
