import { test } from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { compileTimeline, plannedMs } from '../js/timeline.js';
const cases = JSON.parse(readFileSync(new URL('../../fixtures/timeline-cases.json', import.meta.url)));
for (const c of cases) test(c.name, () => {
  const phases = compileTimeline(c.workout);
  assert.equal(plannedMs(phases), c.expectedMs);
  assert.deepEqual(phases.map(p => p.type), c.expectedTypes);
  assert.ok(phases.every(p => p.durationMs > 0));
});
test('nessuna pausa finale e durate zero saltate', () => {
  const w = structuredClone(cases[0].workout);
  w.blocks[0].cycles = 1; w.blocks[0].exercises[0].repetitions = 1;
  assert.deepEqual(compileTimeline(w).map(p => p.type), ['WORK']);
  w.blocks[0].exercises[0].repetitions = 3; w.blocks[0].exercises[0].restBetweenRepsSec = 0;
  assert.equal(compileTimeline(w).length, 3);
});
for (const [name, change] of [
  ['versione', w => w.schemaVersion = 2], ['array vuoto', w => w.blocks = []],
  ['frazione', w => w.warmupSec = 0.5], ['negativo', w => w.cooldownSec = -1],
  ['NaN', w => w.warmupSec = NaN], ['titolo', w => w.title = ' '],
  ['ID duplicato', w => w.blocks[0].id = w.id], ['lavoro zero', w => w.blocks[0].exercises[0].workSec = 0],
  ['ripetizioni', w => w.blocks[0].cycles = 101], ['colore', w => w.color = 'url(x)'],
  ['limite fasi', w => { w.blocks[0].cycles = 100; w.blocks[0].exercises[0].repetitions = 100; }],
  ['limite durata', w => { w.blocks[0].cycles = 100; w.blocks[0].exercises[0].workSec = 3600; }]
]) test(`rifiuta ${name}`, () => {
  const w = structuredClone(cases[0].workout); change(w); assert.throws(() => compileTimeline(w));
});
test('schema malformato', () => { for (const w of [null, [], {}, 7]) assert.throws(() => compileTimeline(w)); });
