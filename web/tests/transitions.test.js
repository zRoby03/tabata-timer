import { test } from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { TransitionCues, TRANSITION_SOUNDS } from '../js/transitions.js';
import { TimerEngine } from '../js/timer.js';
import { newWorkout } from '../js/model.js';
function setup(cooldown = 4) {
  let clock = 0; const w = newWorkout(); w.cooldownSec = cooldown;
  Object.assign(w.blocks[0].exercises[0], { workSec: 4, repetitions: 2, restBetweenRepsSec: 4 });
  const timer = new TimerEngine(w, () => clock), cues = new TransitionCues(); timer.start();
  return { timer, cues, advance(ms) { clock += ms; timer.tick(); } };
}
test('fischietto all’inizio del lavoro, senza ripetizione dopo pausa', () => {
  const { timer, cues, advance } = setup(); assert.equal(cues.read(timer), null);
  advance(20000); assert.equal(cues.read(timer), 'whistle'); assert.equal(cues.read(timer), null);
  timer.pause(); assert.equal(cues.read(timer), null); advance(5000); timer.resume(); assert.equal(cues.read(timer), null);
});
test('sospiro all’inizio del recupero e fischietto al turno successivo', () => {
  const { timer, cues, advance } = setup(); advance(20000); cues.read(timer);
  advance(4000); assert.equal(cues.read(timer), 'sigh'); assert.equal(cues.read(timer), null);
  advance(4000); assert.equal(cues.read(timer), 'whistle');
});
test('tre ding all’inizio del defaticamento, una sola volta anche tornando indietro', () => {
  const { timer, cues, advance } = setup(); advance(32000); assert.equal(cues.read(timer), 'finish');
  timer.previous(); assert.equal(cues.read(timer), 'whistle'); timer.skip(); assert.equal(cues.read(timer), null);
  advance(4000); assert.equal(cues.read(timer), null);
});
test('senza defaticamento campanella alla fine; Termina manuale non suona', () => {
  const natural = setup(0); natural.advance(32000); assert.equal(natural.cues.read(natural.timer), 'finish'); assert.equal(natural.cues.read(natural.timer), null);
  const stopped = setup(); stopped.advance(20000); stopped.cues.read(stopped.timer); stopped.timer.stop(); assert.equal(stopped.cues.read(stopped.timer), null);
});
test('cambio turno in pausa: segnale solo alla ripresa; precedente è una nuova visita', () => {
  const { timer, cues, advance } = setup(); advance(20000); cues.read(timer); timer.pause(); timer.skip();
  assert.equal(cues.read(timer), null); timer.resume(); assert.equal(cues.read(timer), 'sigh');
  timer.previous(); assert.equal(cues.read(timer), 'whistle');
});
test('ritardi non producono fischi arretrati; una nuova sessione ripristina i segnali', () => {
  const { timer, cues, advance } = setup(); advance(22000); assert.equal(cues.read(timer), null);
  advance(2000); assert.equal(cues.read(timer), 'sigh');
  const other = setup(); other.advance(20000); assert.equal(cues.read(other.timer), 'whistle');
});
for (const [key, duration, expectedNotes] of [['whistle', .5, 1], ['sigh', .5, null], ['finish', .9, 3]]) test(`campione ${key}: durata, pause, livelli e identità Android/web`, () => {
  const web = readFileSync(new URL(`../audio/${TRANSITION_SOUNDS[key]}`, import.meta.url));
  const android = readFileSync(new URL(`../../android/app/src/main/res/raw/${TRANSITION_SOUNDS[key].replaceAll('-', '_')}`, import.meta.url));
  assert.deepEqual(web, android); assert.equal(web.readUInt32LE(24), 24000);
  const pcm = Array.from({ length: (web.length - 44) / 2 }, (_, i) => web.readInt16LE(44 + i * 2));
  assert.equal(pcm.length / 24000, duration); assert.ok(Math.max(...pcm.map(Math.abs)) < 32767);
  if (key === 'whistle') assert.equal(Math.max(...pcm.map(Math.abs)), 13200, '11000 * 120% con gain 1');
  let notes = 0, wasLoud = false;
  for (let offset = 0; offset < pcm.length; offset += 120) {
    const window = pcm.slice(offset, offset + 120);
    const loud = Math.sqrt(window.reduce((sum, n) => sum + n * n, 0) / window.length) > 500;
    if (loud && !wasLoud) notes++;
    wasLoud = loud;
  }
  if (expectedNotes !== null) assert.equal(notes, expectedNotes);
  if (key === 'sigh') assert.deepEqual(web, readFileSync(new URL('../../assets/audio/sigh-reference.wav', import.meta.url)));
});

test('fischio da arbitro meno acuto: tono dominante sotto 2100 Hz e attacco morbido', () => {
  const wav = readFileSync(new URL('../audio/work-start.wav', import.meta.url));
  const rate = wav.readUInt32LE(24), count = (wav.length - 44) / 2;
  const pcm = Array.from({ length: count }, (_, i) => wav.readInt16LE(44 + i * 2));
  const central = pcm.slice(Math.round(.05 * rate), Math.round(.45 * rate));
  const positiveCrossings = central.slice(1).filter((v, i) => central[i] <= 0 && v > 0).length;
  const frequency = positiveCrossings / (central.length / rate);
  assert.ok(frequency > 1400 && frequency < 2100, `Dominante ${frequency} Hz`);
  const rms = values => Math.sqrt(values.reduce((sum, x) => sum + x * x, 0) / values.length);
  assert.ok(rms(pcm.slice(0, Math.round(.005 * rate))) < rms(central) * .4);
  assert.ok(rms(pcm.slice(-Math.round(.01 * rate))) < rms(central) * .3);
});
