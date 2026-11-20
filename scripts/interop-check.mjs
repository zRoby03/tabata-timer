// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { readFileSync } from 'node:fs';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { readFileSync } from 'node:fs';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import assert from 'node:assert/strict';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import assert from 'node:assert/strict';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import { parseBackup } from '../web/js/io.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { parseBackup } from '../web/js/io.js';
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import { compileTimeline, plannedMs } from '../web/js/timeline.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { compileTimeline, plannedMs } from '../web/js/timeline.js';
// DOC L0005: web: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const web = parseBackup(readFileSync('fixtures/backup-web-v1.json', 'utf8'));
// DOC L0006: android: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const android = parseBackup(readFileSync('android/app/build/interop/android-export-v1.json', 'utf8'));
// DOC L0007: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert.deepEqual(android.workouts, web.workouts);
// DOC L0008: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert.deepEqual(android.sessions, web.sessions);
// DOC L0009: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert.equal(plannedMs(compileTimeline(android.workouts[0])), 220000);
// DOC L0010: Esegue il passaggio esplicito «console.log('Web → Kotlin → Web: workout, sessione e timeline 220 s identici.');» nel flusso: Verifica dei contratti JSON comuni tra piattaforme.
console.log('Web → Kotlin → Web: workout, sessione e timeline 220 s identici.');
// DOC L0011: studioWeb: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const studioWeb = parseBackup(readFileSync('fixtures/backup-modes-v1.json', 'utf8'));
// DOC L0012: studioAndroid: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const studioAndroid = parseBackup(readFileSync('android/app/build/interop/android-export-modes-v1.json', 'utf8'));
// DOC L0013: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert.deepEqual(studioAndroid.workouts, studioWeb.workouts);
// DOC L0014: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert.deepEqual(studioAndroid.sessions, studioWeb.sessions);
// DOC L0015: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert.equal(plannedMs(compileTimeline(studioAndroid.workouts[0])), 14100000);
// DOC L0016: Esegue il passaggio esplicito «console.log('Studio Web → Kotlin → Web: archivio precedente conservato; nuova esecuzione senza pausa lunga (235 minuti).');» nel flusso: Verifica dei contratti JSON comuni tra piattaforme.
console.log('Studio Web → Kotlin → Web: archivio precedente conservato; nuova esecuzione senza pausa lunga (235 minuti).');

// DOC L0018: simpleWeb: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const simpleWeb = parseBackup(readFileSync('fixtures/backup-simple-v1.json', 'utf8'));
// DOC L0019: simpleAndroid: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const simpleAndroid = parseBackup(readFileSync('android/app/build/interop/android-export-simple-v1.json', 'utf8'));
// DOC L0020: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert.deepEqual(simpleAndroid.workouts, simpleWeb.workouts);
// DOC L0021: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert.deepEqual(simpleAndroid.sessions, simpleWeb.sessions);
// DOC L0022: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert.equal(plannedMs(compileTimeline(simpleAndroid.workouts[0])), 83000);
// DOC L0023: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert.equal(plannedMs(compileTimeline(simpleAndroid.workouts[1])), 660000);
// DOC L0024: Esegue il passaggio esplicito «console.log('Nuovo Studio piatto e recupero comune Workout: JSON e durate identici Web → Kotlin → Web.');» nel flusso: Verifica dei contratti JSON comuni tra piattaforme.
console.log('Nuovo Studio piatto e recupero comune Workout: JSON e durate identici Web → Kotlin → Web.');
