// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { test } from 'node:test';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { test } from 'node:test';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import assert from 'node:assert/strict';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import assert from 'node:assert/strict';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import { TimerEngine, PREPARATION_MS } from '../js/timer.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { TimerEngine, PREPARATION_MS } from '../js/timer.js';
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import { newWorkout } from '../js/model.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { newWorkout } from '../js/model.js';
// DOC L0005: setup: Prepara o verifica il caso di regressione «setup»; le asserzioni nel corpo specificano l’esito atteso.
function setup(skipPreparation = true) {
  // DOC L0006: now: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di setup.
  let now = 0; const w = newWorkout(); Object.assign(w.blocks[0].exercises[0], { workSec: 2, repetitions: 2, restBetweenRepsSec: 1 });
  // DOC L0007: t: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const t = new TimerEngine(w, () => now, () => new Date(now).toISOString()); t.start();
  // DOC L0008: advance: Incrementa indice e visita di fase; alla fine produce il risultato senza aggiungere un recupero finale.
  const advance = ms => { now += ms; t.tick(); };
  // DOC L0009: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di setup.
  if (skipPreparation) advance(PREPARATION_MS);
  // DOC L0010: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di setup.
  return { t, advance };
// DOC L0011: Chiude il costrutto aperto alla riga 5.
}
// DOC L0012: Esegue il passaggio esplicito «test('preparazione di 20 secondi prima di ogni workout, esclusa dal lavoro e dalle statistiche', () => {» nel flusso: Prove di regressione in timer.test.js.
test('preparazione di 20 secondi prima di ogni workout, esclusa dal lavoro e dalle statistiche', () => {
  // DOC L0013: Esegue il passaggio esplicito «const { t, advance } = setup(false);» nel flusso: Prove di regressione in timer.test.js.
  const { t, advance } = setup(false);
  // DOC L0014: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(t.currentPhase.type, 'PREPARE'); assert.equal(t.remainingMs, 20000); assert.equal(t.nextPhase.type, 'WORK');
  // DOC L0015: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  advance(19999); assert.equal(t.index, -1); assert.equal(t.remainingMs, 1); assert.equal(t.elapsedMs, 0); assert.equal(t.workMs, 0);
  // DOC L0016: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  advance(1); assert.equal(t.index, 0); assert.equal(t.remainingMs, 2000); assert.equal(t.elapsedMs, 0);
  // DOC L0017: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  advance(500); assert.equal(t.workMs, 500);
// DOC L0018: Chiude il costrutto aperto alla riga 12.
});
// DOC L0019: Esegue il passaggio esplicito «test('pausa, salto e interruzione durante la preparazione', () => {» nel flusso: Prove di regressione in timer.test.js.
test('pausa, salto e interruzione durante la preparazione', () => {
  // DOC L0020: first: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const first = setup(false); first.advance(5000); first.t.pause(); first.advance(30000);
  // DOC L0021: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(first.t.remainingMs, 15000); first.t.resume(); first.advance(15000); assert.equal(first.t.index, 0);
  // DOC L0022: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  first.t.previous(); assert.equal(first.t.index, 0); // Preparation is not replayed once work has started.
  // DOC L0023: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. skipped: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const skipped = setup(false); skipped.t.skip(); assert.equal(skipped.t.index, 0); assert.equal(skipped.t.elapsedMs, 0);
  // DOC L0024: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. stopped: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const stopped = setup(false); stopped.advance(1000); stopped.t.stop(); assert.equal(stopped.t.result.elapsedMs, 0); assert.equal(stopped.t.result.completedPhases, 0); assert.equal(stopped.t.result.status, 'INTERRUPTED');
// DOC L0025: Chiude il costrutto aperto alla riga 19.
});
// DOC L0026: Esegue il passaggio esplicito «test('ritardo di polling attraversa preparazione e allenamento senza alterare i totali', () => {» nel flusso: Prove di regressione in timer.test.js.
test('ritardo di polling attraversa preparazione e allenamento senza alterare i totali', () => {
  // DOC L0027: Esegue il passaggio esplicito «const { t, advance } = setup(false); advance(25000);» nel flusso: Prove di regressione in timer.test.js.
  const { t, advance } = setup(false); advance(25000);
  // DOC L0028: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(t.result.elapsedMs, 5000); assert.equal(t.result.workMs, 4000); assert.equal(t.result.plannedMs, 5000); assert.equal(t.result.completedPhases, 3);
// DOC L0029: Chiude il costrutto aperto alla riga 26.
});
// DOC L0030: Esegue il passaggio esplicito «test('deadline recupera ritardo di polling e scheda nascosta', () => {» nel flusso: Prove di regressione in timer.test.js.
test('deadline recupera ritardo di polling e scheda nascosta', () => {
  // DOC L0031: Esegue il passaggio esplicito «const { t, advance } = setup(); advance(99000);» nel flusso: Prove di regressione in timer.test.js.
  const { t, advance } = setup(); advance(99000);
  // DOC L0032: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(t.state, 'finished'); assert.equal(t.result.elapsedMs, 5000); assert.equal(t.result.workMs, 4000); assert.equal(t.result.status, 'COMPLETED'); assert.equal(t.result.completedWorkIntervals, 2);
  // DOC L0033: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(t.result.completedPhases, 3); assert.equal(t.result.plannedPhases, 3);
// DOC L0034: Chiude il costrutto aperto alla riga 30.
});
// DOC L0035: Esegue il passaggio esplicito «test('pausa non conta tempo e ripresa mantiene residuo', () => {» nel flusso: Prove di regressione in timer.test.js.
test('pausa non conta tempo e ripresa mantiene residuo', () => {
  // DOC L0036: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  const { t, advance } = setup(); advance(700); t.pause(); advance(10000); assert.equal(t.remainingMs, 1300); t.resume(); advance(1300); assert.equal(t.index, 1); assert.equal(t.elapsedMs, 2000); assert.equal(t.pauses, 1);
// DOC L0037: Chiude il costrutto aperto alla riga 35.
});
// DOC L0038: Esegue il passaggio esplicito «test('skip non accredita tempo o completamento', () => {» nel flusso: Prove di regressione in timer.test.js.
test('skip non accredita tempo o completamento', () => {
  // DOC L0039: Esegue il passaggio esplicito «const { t, advance } = setup(); advance(500); t.skip(); t.skip(); advance(2000);» nel flusso: Prove di regressione in timer.test.js.
  const { t, advance } = setup(); advance(500); t.skip(); t.skip(); advance(2000);
  // DOC L0040: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(t.result.workMs, 2500); assert.equal(t.result.completedWorkIntervals, 1); assert.equal(t.result.status, 'INTERRUPTED');
// DOC L0041: Chiude il costrutto aperto alla riga 38.
});
// DOC L0042: Esegue il passaggio esplicito «test('previous ripete il lavoro ma non duplica intervalli', () => {» nel flusso: Prove di regressione in timer.test.js.
test('previous ripete il lavoro ma non duplica intervalli', () => {
  // DOC L0043: Esegue il passaggio esplicito «const { t, advance } = setup(); advance(2000); t.previous(); advance(5000);» nel flusso: Prove di regressione in timer.test.js.
  const { t, advance } = setup(); advance(2000); t.previous(); advance(5000);
  // DOC L0044: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(t.result.workMs, 6000); assert.equal(t.result.completedWorkIntervals, 2); assert.equal(t.result.elapsedMs, 7000);
// DOC L0045: Chiude il costrutto aperto alla riga 42.
});
// DOC L0046: Esegue il passaggio esplicito «test('stop idempotente e pausa sul confine finale', () => {» nel flusso: Prove di regressione in timer.test.js.
test('stop idempotente e pausa sul confine finale', () => {
  // DOC L0047: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. first: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const { t, advance } = setup(); advance(1000); const first = t.stop(); assert.equal(t.stop(), first); assert.equal(first.status, 'INTERRUPTED');
  // DOC L0048: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. other: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const other = setup(); other.advance(5000); other.t.pause(); assert.equal(other.t.result.status, 'COMPLETED');
// DOC L0049: Chiude il costrutto aperto alla riga 46.
});
