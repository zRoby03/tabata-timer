import { TRANSITION_SOUNDS } from '../js/transitions.js';
import { test } from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { CountdownCues, hornSample, COUNTDOWN_SOUNDS } from '../js/countdown.js';
import { TimerEngine, TimerSignals } from '../js/timer.js';
import { newWorkout } from '../js/model.js';

function setup() {
  let clock = 0; const w = newWorkout(); w.blocks[0].exercises[0].workSec = 4;
  const t = new TimerEngine(w, () => clock); t.start();
  return { t, cues: new CountdownCues(), advance: ms => { clock += ms; t.tick(); } };
}
test('tre, due, uno una sola volta e nessun suono durante pausa o dopo stop', () => {
  const { t, cues, advance } = setup(); assert.equal(cues.read(t), null);
  advance(17000); assert.equal(cues.read(t), 3); assert.equal(cues.read(t), null);
  t.pause(); advance(5000); assert.equal(cues.read(t), null); t.resume(); assert.equal(cues.read(t), null);
  advance(1000); assert.equal(cues.read(t), 2); advance(1000); assert.equal(cues.read(t), 1);
  t.stop(); assert.equal(cues.read(t), null);
});
test('nessuna raffica di segnali arretrati; nuovo giro e precedente ripristinano i segnali', () => {
  const { t, cues, advance } = setup(); advance(19000); assert.equal(cues.read(t), 1); assert.equal(cues.read(t), null);
  advance(2000); assert.equal(t.index, 0); assert.equal(cues.read(t), 3);
  advance(1000); assert.equal(cues.read(t), 2);
  t.previous(); assert.equal(cues.read(t), null); advance(1000); assert.equal(cues.read(t), 3);
  const next = setup(); next.advance(17000); assert.equal(cues.read(next.t), 3);
});
test('modalità silenziosa non crea sorgenti audio', () => {
  const signals = new TimerSignals(); signals.sound = false;
  signals.audio = { state: 'running', createBufferSource() { throw new Error('Audio creato in modalità silenziosa'); } }; signals.buffers['rest-3'] = {};
  signals.horn(3);
});
test('sei campioni identici tra web e Android, ciascuno breve e seguito da silenzio prima del secondo successivo', () => {
  assert.equal(Object.keys(COUNTDOWN_SOUNDS).length, 6);
  for (const [key, file] of Object.entries(COUNTDOWN_SOUNDS)) {
    const web = readFileSync(new URL(`../audio/${file}`, import.meta.url));
    const android = readFileSync(new URL(`../../android/app/src/main/res/raw/countdown_${key.replace('-', '_')}.wav`, import.meta.url));
    assert.deepEqual(web, android); assert.equal(web.toString('ascii', 0, 4), 'RIFF');
    assert.equal(web.readUInt32LE(24), 22050); assert.equal(web.readUInt16LE(22), 1);
    assert.equal(web.length - 44, 2646 * 2);
    assert.ok((web.length - 44) / 2 / 22050 < .15);
  }
});
test('conto 3-2-1: tre note basse nel lavoro e tre alte in ogni recupero', () => {
  assert.deepEqual([3, 2, 1].map(n => hornSample(n, 'WORK')), ['work-3', 'work-2', 'work-1']);
  for (const phase of ['REST_REP', 'REST_EXERCISE', 'REST_CYCLE', 'REST_BLOCK', 'COOLDOWN', 'WARMUP'])
    assert.deepEqual([3, 2, 1].map(n => hornSample(n, phase)), ['rest-3', 'rest-2', 'rest-1']);
});
test('preparazione con una nota al secondo e nessun quarto suono allo zero', () => {
  assert.deepEqual([3, 2, 1].map(n => hornSample(n, 'PREPARE')), ['rest-3', 'rest-2', 'rest-1']);
  for (const phase of ['WORK', 'COOLDOWN', 'PREPARE']) for (const n of [0, 4, -1]) assert.equal(hornSample(n, phase), null);
});
test('ogni WAV contiene una sola nota pulita, senza frasi ripetute o saturazione', () => {
  const frequencies = { 'work-3': 330, 'work-2': 370, 'work-1': 440, 'rest-3': 660, 'rest-2': 740, 'rest-1': 880 };
  for (const [key, frequency] of Object.entries(frequencies)) {
    const wave = readFileSync(new URL(`../audio/${COUNTDOWN_SOUNDS[key]}`, import.meta.url));
    const pcm = Array.from({ length: (wave.length - 44) / 2 }, (_, i) => wave.readInt16LE(44 + i * 2));
    assert.ok(Math.max(...pcm.map(Math.abs)) < 32767);
    let notes = 0, wasLoud = false, crossings = 0;
    for (let i = 1; i < pcm.length; i++) if (pcm[i - 1] <= 0 && pcm[i] > 0) crossings++;
    for (let offset = 0; offset < pcm.length; offset += 110) {
      const window = pcm.slice(offset, offset + 110);
      const loud = Math.sqrt(window.reduce((sum, n) => sum + n * n, 0) / window.length) > 500;
      if (loud && !wasLoud) notes++;
      wasLoud = loud;
    }
    assert.equal(notes, 1, key); assert.ok(Math.abs(crossings / (pcm.length / 22050) - frequency) <= 12, key);
    const attack = pcm.slice(0, 110);
    assert.ok(Math.sqrt(attack.reduce((sum, n) => sum + n * n, 0) / attack.length) > 2000, `Attacco immediato: ${key}`);
  }
});
function audioSetup() {
  const signals = new TimerSignals(), sources = [];
  signals.buffers = Object.fromEntries(Object.keys({ ...COUNTDOWN_SOUNDS, ...TRANSITION_SOUNDS }).map(name => [name, { name }]));
  signals.audio = {
    state: 'running', destination: {},
    createGain() { return { gain: {}, connect() {}, disconnect() {} }; },
    createBufferSource() {
      const source = { playbackRate: {}, connect(gain) { this.gain = gain; return gain; }, disconnect() {}, start() {}, stop() { this.stopped = true; this.onended(); } };
      sources.push(source); return source;
    }
  };
  return { signals, sources };
}
test('una sola voce alla volta, durata dei campioni conservata e arresto della nota precedente', () => {
  const { signals, sources } = audioSetup(); signals.horn(3, 'WORK'); signals.horn(2, 'COOLDOWN');
  assert.equal(sources[0].buffer.name, 'work-3'); assert.equal(sources[1].buffer.name, 'rest-2');
  assert.equal(sources[0].stopped, true);
  assert.equal(sources[0].playbackRate.value, 1); assert.equal(sources[1].playbackRate.value, 1);
});
test('pausa e disattivazione audio interrompono subito la nota in corso', () => {
  const { signals, sources } = audioSetup(); signals.horn(3, 'WORK'); signals.stop(); signals.stop();
  assert.equal(sources[0].stopped, true); assert.equal(signals.sources.size, 0);
});

test('suoni di cambio turno rispettano modalità silenziosa e selezione del file', () => {
  const { signals, sources } = audioSetup(); signals.sound = false;
  for (const key of ['whistle', 'sigh', 'finish']) signals.play(key);
  assert.equal(sources.length, 0); signals.sound = true; signals.play('finish'); assert.equal(sources[0].buffer.name, 'finish');
});

test('fischietto PCM 120% con gain 1, sospiro 85%, bip e campanella 65%', () => {
  const { signals, sources } = audioSetup();
  for (const key of ['whistle', 'sigh', 'finish', 'work-3', 'rest-3']) signals.play(key);
  assert.deepEqual(sources.map(s => s.gain.gain.value), [1, .85, .65, .65, .65]);
});
