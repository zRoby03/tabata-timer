import { test } from 'node:test';
import assert from 'node:assert/strict';
import { TimerEngine, PREPARATION_MS } from '../js/timer.js';
import { newWorkout } from '../js/model.js';
function setup(skipPreparation = true) {
  let now = 0; const w = newWorkout(); Object.assign(w.blocks[0].exercises[0], { workSec: 5, repetitions: 2, restBetweenRepsSec: 5 });
  const t = new TimerEngine(w, () => now, () => new Date(now).toISOString()); t.start();
  const advance = ms => { now += ms; t.tick(); };
  if (skipPreparation) advance(PREPARATION_MS);
  return { t, advance };
}
test('preparazione di 20 secondi esclusa da lavoro e statistiche', () => {
  const { t, advance } = setup(false);
  assert.equal(t.currentPhase.type, 'PREPARE'); assert.equal(t.nextPhase.type, 'WORK');
  advance(19999); assert.equal(t.index, -1); assert.equal(t.remainingMs, 1); assert.equal(t.elapsedMs, 0);
  advance(1); assert.equal(t.remainingMs, 5000); assert.equal(t.elapsedMs, 0);
  advance(500); assert.equal(t.workMs, 500);
});
test('pausa, salto e interruzione durante la preparazione', () => {
  const first = setup(false); first.advance(5000); first.t.pause(); first.advance(30000);
  assert.equal(first.t.remainingMs, 15000); first.t.resume(); first.advance(15000); assert.equal(first.t.index, 0);
  first.t.previous(); assert.equal(first.t.index, 0);
  const skipped = setup(false); skipped.t.skip(); assert.equal(skipped.t.index, 0); assert.equal(skipped.t.elapsedMs, 0);
  const stopped = setup(false); stopped.advance(1000); stopped.t.stop(); assert.equal(stopped.t.result.elapsedMs, 0); assert.equal(stopped.t.result.status, 'INTERRUPTED');
});
test('ritardo attraversa preparazione e allenamento senza alterare i totali', () => {
  const { t, advance } = setup(false); advance(35000);
  assert.equal(t.result.elapsedMs, 15000); assert.equal(t.result.workMs, 10000); assert.equal(t.result.plannedMs, 15000);
});
test('deadline recupera ritardo di polling e scheda nascosta', () => {
  const { t, advance } = setup(); advance(99000);
  assert.equal(t.state, 'finished'); assert.equal(t.result.elapsedMs, 15000); assert.equal(t.result.workMs, 10000); assert.equal(t.result.status, 'COMPLETED');
  assert.equal(t.result.completedWorkIntervals, 2); assert.equal(t.result.completedPhases, 3);
});
test('pausa non conta tempo e ripresa mantiene residuo', () => {
  const { t, advance } = setup(); advance(700); t.pause(); advance(10000); assert.equal(t.remainingMs, 4300); t.resume(); advance(4300); assert.equal(t.index, 1); assert.equal(t.elapsedMs, 5000); assert.equal(t.pauses, 1);
});
test('skip non accredita tempo o completamento', () => {
  const { t, advance } = setup(); advance(500); t.skip(); t.skip(); advance(5000);
  assert.equal(t.result.workMs, 5500); assert.equal(t.result.completedWorkIntervals, 1); assert.equal(t.result.status, 'INTERRUPTED');
});
test('previous ripete il lavoro ma non duplica intervalli', () => {
  const { t, advance } = setup(); advance(5000); t.previous(); advance(15000);
  assert.equal(t.result.workMs, 15000); assert.equal(t.result.completedWorkIntervals, 2); assert.equal(t.result.elapsedMs, 20000);
});
test('stop idempotente e pausa sul confine finale', () => {
  const { t, advance } = setup(); advance(1000); const first = t.stop(); assert.equal(t.stop(), first); assert.equal(first.status, 'INTERRUPTED');
  const other = setup(); other.advance(15000); other.t.pause(); assert.equal(other.t.result.status, 'COMPLETED');
});
