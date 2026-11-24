import { test } from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { TransitionCues, TRANSITION_SOUNDS } from '../js/transitions.js';
import { TimerEngine } from '../js/timer.js';
import { newWorkout } from '../js/model.js';
function setup(cooldown = 10) {
  let clock = 0; const w = newWorkout(); w.cooldownSec = cooldown;
  Object.assign(w.blocks[0].exercises[0], { workSec: 5, repetitions: 2, restBetweenRepsSec: 5 });
  const timer = new TimerEngine(w, () => clock), cues = new TransitionCues(); timer.start();
  return { timer, cues, advance(ms) { clock += ms; timer.tick(); } };
}
test('un ding avvia il lavoro senza ripetersi dopo pausa', () => {
  const {timer,cues,advance}=setup(); assert.equal(cues.read(timer),null);
  advance(20000); assert.equal(cues.read(timer),'ding1'); assert.equal(cues.read(timer),null);
  timer.pause(); assert.equal(cues.read(timer),null);advance(5000);timer.resume();assert.equal(cues.read(timer),null);
});
test('sospiro al recupero e ding singolo al lavoro ordinario successivo',()=>{
  const {timer,cues,advance}=setup();advance(20000);cues.read(timer);
  advance(5000);assert.equal(cues.read(timer),'sigh');assert.equal(cues.read(timer),null);
  advance(5000);assert.equal(cues.read(timer),'ding1');
});
test('stesso triplo ding a fine lavoro e fine defaticamento, ciascuno una volta',()=>{
  const {timer,cues,advance}=setup();advance(35000);assert.equal(cues.read(timer),'ding3');assert.equal(cues.read(timer),null);
  timer.previous();assert.equal(cues.read(timer),'ding1');timer.skip();assert.equal(cues.read(timer),null);
  advance(10000);assert.equal(cues.read(timer),'ding3');assert.equal(cues.read(timer),null);
});
test('senza defaticamento un solo triplo ding; Termina e salto finale non celebrano',()=>{
  const natural=setup(0);natural.advance(35000);assert.equal(natural.cues.read(natural.timer),'ding3');assert.equal(natural.cues.read(natural.timer),null);
  const stopped=setup();stopped.advance(20000);stopped.timer.stop();assert.equal(stopped.cues.read(stopped.timer),null);
  const skipped=setup(0);skipped.advance(30000);skipped.timer.skip();assert.equal(skipped.cues.read(skipped.timer),null);
});
test('cambio turno in pausa attende ripresa; precedente crea nuova visita',()=>{
  const {timer,cues,advance}=setup();advance(20000);cues.read(timer);timer.pause();timer.skip();
  assert.equal(cues.read(timer),null);timer.resume();assert.equal(cues.read(timer),'sigh');
  timer.previous();assert.equal(cues.read(timer),'ding1');
});
test('polling tardivo non recupera avvisi scaduti, compresi i finali',()=>{
  const {timer,cues,advance}=setup();advance(22000);assert.equal(cues.read(timer),null);
  advance(3000);assert.equal(cues.read(timer),'sigh');
  const late=setup();late.advance(55000);assert.equal(late.cues.read(late.timer),null);
  const other=setup();other.advance(20000);assert.equal(cues.read(other.timer),'ding1');
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
