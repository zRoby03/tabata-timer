import { test } from 'node:test';
import assert from 'node:assert/strict';
import { newQuickWorkout } from '../js/quick.js';
import { compileTimeline, plannedMs } from '../js/timeline.js';

test('timer rapido: otto lavori e sette recuperi, senza recupero finale', () => {
  const phases = compileTimeline(newQuickWorkout());
  assert.equal(plannedMs(phases), 230000);
  assert.equal(phases.filter(p => p.type === 'WORK').length, 8);
  assert.equal(phases.at(-1).type, 'WORK');
});

test('opzioni rapide riusano cicli e recuperi del modello condiviso', () => {
  const w = newQuickWorkout();
  w.blocks[0].cycles = 2;
  w.blocks[0].exercises[0].repetitions = 3;
  assert.equal(plannedMs(compileTimeline(w)), 170000);
});
