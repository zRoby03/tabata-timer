// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { test } from 'node:test';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { test } from 'node:test';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import assert from 'node:assert/strict';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import assert from 'node:assert/strict';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import { newQuickWorkout } from '../js/quick.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { newQuickWorkout } from '../js/quick.js';
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import { compileTimeline, plannedMs } from '../js/timeline.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { compileTimeline, plannedMs } from '../js/timeline.js';

// DOC L0006: Esegue il passaggio esplicito «test('timer rapido: otto lavori e sette recuperi, senza recupero finale', () => {» nel flusso: Prove di regressione in quick.test.js.
test('timer rapido: otto lavori e sette recuperi, senza recupero finale', () => {
  // DOC L0007: phases: sequenza cronologica compilata dal programma.
  const phases = compileTimeline(newQuickWorkout());
  // DOC L0008: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(plannedMs(phases), 230000);
  // DOC L0009: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(phases.filter(p => p.type === 'WORK').length, 8);
  // DOC L0010: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(phases.at(-1).type, 'WORK');
// DOC L0011: Chiude il costrutto aperto alla riga 6.
});

// DOC L0013: Esegue il passaggio esplicito «test('opzioni rapide riusano cicli e recuperi del modello condiviso', () => {» nel flusso: Prove di regressione in quick.test.js.
test('opzioni rapide riusano cicli e recuperi del modello condiviso', () => {
  // DOC L0014: w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const w = newQuickWorkout();
  // DOC L0015: Esegue il passaggio esplicito «w.blocks[0].cycles = 2;» nel flusso: Prove di regressione in quick.test.js.
  w.blocks[0].cycles = 2;
  // DOC L0016: Esegue il passaggio esplicito «w.blocks[0].exercises[0].repetitions = 3;» nel flusso: Prove di regressione in quick.test.js.
  w.blocks[0].exercises[0].repetitions = 3;
  // DOC L0017: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(plannedMs(compileTimeline(w)), 220000);
// DOC L0018: Chiude il costrutto aperto alla riga 13.
});
