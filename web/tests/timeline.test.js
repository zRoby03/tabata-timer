// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { test } from 'node:test';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { test } from 'node:test';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import assert from 'node:assert/strict';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import assert from 'node:assert/strict';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import { readFileSync } from 'node:fs';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { readFileSync } from 'node:fs';
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import { compileTimeline, plannedMs } from '../js/timeline.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { compileTimeline, plannedMs } from '../js/timeline.js';
// DOC L0005: cases: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const cases = JSON.parse(readFileSync(new URL('../../fixtures/timeline-cases.json', import.meta.url)));
// DOC L0006: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
for (const c of cases) test(c.name, () => {
  // DOC L0007: phases: sequenza cronologica compilata dal programma.
  const phases = compileTimeline(c.workout);
  // DOC L0008: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(plannedMs(phases), c.expectedMs);
  // DOC L0009: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  assert.deepEqual(phases.map(p => p.type), c.expectedTypes);
  // DOC L0010: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.ok(phases.every(p => p.durationMs > 0));
// DOC L0011: Chiude il costrutto aperto alla riga 6.
});
// DOC L0012: Esegue il passaggio esplicito «test('nessuna pausa finale e durate zero saltate', () => {» nel flusso: Prove di regressione in timeline.test.js.
test('nessuna pausa finale e durate zero saltate', () => {
  // DOC L0013: w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Lavora su una copia per mantenere distinta la bozza dal valore precedente.
  const w = structuredClone(cases[0].workout);
  // DOC L0014: Esegue il passaggio esplicito «w.blocks[0].cycles = 1; w.blocks[0].exercises[0].repetitions = 1;» nel flusso: Prove di regressione in timeline.test.js.
  w.blocks[0].cycles = 1; w.blocks[0].exercises[0].repetitions = 1;
  // DOC L0015: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  assert.deepEqual(compileTimeline(w).map(p => p.type), ['WORK']);
  // DOC L0016: Esegue il passaggio esplicito «w.blocks[0].exercises[0].repetitions = 3; w.blocks[0].exercises[0].restBetweenRepsSec = 0;» nel flusso: Prove di regressione in timeline.test.js.
  w.blocks[0].exercises[0].repetitions = 3; w.blocks[0].exercises[0].restBetweenRepsSec = 0;
  // DOC L0017: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(compileTimeline(w).length, 3);
// DOC L0018: Chiude il costrutto aperto alla riga 12.
});
// DOC L0019: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
for (const [name, change] of [
  // DOC L0020: Esegue il passaggio esplicito «['versione', w => w.schemaVersion = 2], ['array vuoto', w => w.blocks = []],» nel flusso: Prove di regressione in timeline.test.js.
  ['versione', w => w.schemaVersion = 2], ['array vuoto', w => w.blocks = []],
  // DOC L0021: Esegue il passaggio esplicito «['frazione', w => w.warmupSec = 0.5], ['negativo', w => w.cooldownSec = -1],» nel flusso: Prove di regressione in timeline.test.js.
  ['frazione', w => w.warmupSec = 0.5], ['negativo', w => w.cooldownSec = -1],
  // DOC L0022: Esegue il passaggio esplicito «['NaN', w => w.warmupSec = NaN], ['titolo', w => w.title = ' '],» nel flusso: Prove di regressione in timeline.test.js.
  ['NaN', w => w.warmupSec = NaN], ['titolo', w => w.title = ' '],
  // DOC L0023: Esegue il passaggio esplicito «['ID duplicato', w => w.blocks[0].id = w.id], ['lavoro zero', w => w.blocks[0].exercises[0].workSec = 0],» nel flusso: Prove di regressione in timeline.test.js.
  ['ID duplicato', w => w.blocks[0].id = w.id], ['lavoro zero', w => w.blocks[0].exercises[0].workSec = 0],
  // DOC L0024: Esegue il passaggio esplicito «['ripetizioni', w => w.blocks[0].cycles = 101], ['colore', w => w.color = 'url(x)'],» nel flusso: Prove di regressione in timeline.test.js.
  ['ripetizioni', w => w.blocks[0].cycles = 101], ['colore', w => w.color = 'url(x)'],
  // DOC L0025: Esegue il passaggio esplicito «['limite fasi', w => { w.blocks[0].cycles = 100; w.blocks[0].exercises[0].repetitions = 100; }],» nel flusso: Prove di regressione in timeline.test.js.
  ['limite fasi', w => { w.blocks[0].cycles = 100; w.blocks[0].exercises[0].repetitions = 100; }],
  // DOC L0026: Esegue il passaggio esplicito «['limite durata', w => { w.blocks[0].cycles = 100; w.blocks[0].exercises[0].workSec = 3600; }]» nel flusso: Prove di regressione in timeline.test.js.
  ['limite durata', w => { w.blocks[0].cycles = 100; w.blocks[0].exercises[0].workSec = 3600; }]
// DOC L0027: Esegue il passaggio esplicito «]) test(`rifiuta ${name}`, () => {» nel flusso: Prove di regressione in timeline.test.js.
]) test(`rifiuta ${name}`, () => {
  // DOC L0028: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Lavora su una copia per mantenere distinta la bozza dal valore precedente.
  const w = structuredClone(cases[0].workout); change(w); assert.throws(() => compileTimeline(w));
// DOC L0029: Chiude il costrutto aperto alla riga 27.
});
// DOC L0030: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
test('schema malformato', () => { for (const w of [null, [], {}, 7]) assert.throws(() => compileTimeline(w)); });
