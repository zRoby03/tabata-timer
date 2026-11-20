// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { TRANSITION_SOUNDS } from '../js/transitions.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { TRANSITION_SOUNDS } from '../js/transitions.js';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import { test } from 'node:test';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { test } from 'node:test';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import assert from 'node:assert/strict';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import assert from 'node:assert/strict';
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import { readFileSync } from 'node:fs';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { readFileSync } from 'node:fs';
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import { CountdownCues, hornSample, COUNTDOWN_SOUNDS } from '../js/countdown.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { CountdownCues, hornSample, COUNTDOWN_SOUNDS } from '../js/countdown.js';
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import { TimerEngine, TimerSignals } from '../js/timer.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { TimerEngine, TimerSignals } from '../js/timer.js';
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import { newWorkout } from '../js/model.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { newWorkout } from '../js/model.js';

// DOC L0009: setup: Prepara o verifica il caso di regressione «setup»; le asserzioni nel corpo specificano l’esito atteso.
function setup() {
  // DOC L0010: clock: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di setup.
  let clock = 0; const w = newWorkout(); w.blocks[0].exercises[0].workSec = 4;
  // DOC L0011: t: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const t = new TimerEngine(w, () => clock); t.start();
  // DOC L0012: advance: Incrementa indice e visita di fase; alla fine produce il risultato senza aggiungere un recupero finale.
  return { t, cues: new CountdownCues(), advance: ms => { clock += ms; t.tick(); } };
// DOC L0013: Chiude il costrutto aperto alla riga 9.
}
// DOC L0014: Esegue il passaggio esplicito «test('tre, due, uno una sola volta e nessun suono durante pausa o dopo stop', () => {» nel flusso: Prove di regressione in countdown.test.js.
test('tre, due, uno una sola volta e nessun suono durante pausa o dopo stop', () => {
  // DOC L0015: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  const { t, cues, advance } = setup(); assert.equal(cues.read(t), null);
  // DOC L0016: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  advance(17000); assert.equal(cues.read(t), 3); assert.equal(cues.read(t), null);
  // DOC L0017: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  t.pause(); advance(5000); assert.equal(cues.read(t), null); t.resume(); assert.equal(cues.read(t), null);
  // DOC L0018: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  advance(1000); assert.equal(cues.read(t), 2); advance(1000); assert.equal(cues.read(t), 1);
  // DOC L0019: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  t.stop(); assert.equal(cues.read(t), null);
// DOC L0020: Chiude il costrutto aperto alla riga 14.
});
// DOC L0021: Esegue il passaggio esplicito «test('nessuna raffica di segnali arretrati; nuovo giro e precedente ripristinano i segnali', () => {» nel flusso: Prove di regressione in countdown.test.js.
test('nessuna raffica di segnali arretrati; nuovo giro e precedente ripristinano i segnali', () => {
  // DOC L0022: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  const { t, cues, advance } = setup(); advance(19000); assert.equal(cues.read(t), 1); assert.equal(cues.read(t), null);
  // DOC L0023: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  advance(2000); assert.equal(t.index, 0); assert.equal(cues.read(t), 3);
  // DOC L0024: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  advance(1000); assert.equal(cues.read(t), 2);
  // DOC L0025: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  t.previous(); assert.equal(cues.read(t), null); advance(1000); assert.equal(cues.read(t), 3);
  // DOC L0026: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. next: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const next = setup(); next.advance(17000); assert.equal(cues.read(next.t), 3);
// DOC L0027: Chiude il costrutto aperto alla riga 21.
});
// DOC L0028: Esegue il passaggio esplicito «test('modalità silenziosa non crea sorgenti audio', () => {» nel flusso: Prove di regressione in countdown.test.js.
test('modalità silenziosa non crea sorgenti audio', () => {
  // DOC L0029: signals: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const signals = new TimerSignals(); signals.sound = false;
  // DOC L0030: createBufferSource: Prepara o verifica il caso di regressione «create Buffer Source»; le asserzioni nel corpo specificano l’esito atteso.
  signals.audio = { state: 'running', createBufferSource() { throw new Error('Audio creato in modalità silenziosa'); } }; signals.buffers['rest-3'] = {};
  // DOC L0031: Esegue il passaggio esplicito «signals.horn(3);» nel flusso: Prove di regressione in countdown.test.js.
  signals.horn(3);
// DOC L0032: Chiude il costrutto aperto alla riga 28.
});
// DOC L0033: Esegue il passaggio esplicito «test('sei campioni identici tra web e Android, ciascuno breve e seguito da silenzio prima del secondo successivo', () => {» nel flusso: Prove di regressione in countdown.test.js.
test('sei campioni identici tra web e Android, ciascuno breve e seguito da silenzio prima del secondo successivo', () => {
  // DOC L0034: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(Object.keys(COUNTDOWN_SOUNDS).length, 6);
  // DOC L0035: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  for (const [key, file] of Object.entries(COUNTDOWN_SOUNDS)) {
    // DOC L0036: web: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    const web = readFileSync(new URL(`../audio/${file}`, import.meta.url));
    // DOC L0037: android: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    const android = readFileSync(new URL(`../../android/app/src/main/res/raw/countdown_${key.replace('-', '_')}.wav`, import.meta.url));
    // DOC L0038: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.deepEqual(web, android); assert.equal(web.toString('ascii', 0, 4), 'RIFF');
    // DOC L0039: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.equal(web.readUInt32LE(24), 22050); assert.equal(web.readUInt16LE(22), 1);
    // DOC L0040: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.equal(web.length - 44, 2646 * 2);
    // DOC L0041: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.ok((web.length - 44) / 2 / 22050 < .15);
  // DOC L0042: Chiude il costrutto aperto alla riga 35.
  }
// DOC L0043: Chiude il costrutto aperto alla riga 33.
});
// DOC L0044: Esegue il passaggio esplicito «test('conto 3-2-1: tre note basse nel lavoro e tre alte in ogni recupero', () => {» nel flusso: Prove di regressione in countdown.test.js.
test('conto 3-2-1: tre note basse nel lavoro e tre alte in ogni recupero', () => {
  // DOC L0045: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  assert.deepEqual([3, 2, 1].map(n => hornSample(n, 'WORK')), ['work-3', 'work-2', 'work-1']);
  // DOC L0046: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  for (const phase of ['REST_REP', 'REST_EXERCISE', 'REST_CYCLE', 'REST_BLOCK', 'COOLDOWN', 'WARMUP'])
    // DOC L0047: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    assert.deepEqual([3, 2, 1].map(n => hornSample(n, phase)), ['rest-3', 'rest-2', 'rest-1']);
// DOC L0048: Chiude il costrutto aperto alla riga 44.
});
// DOC L0049: Esegue il passaggio esplicito «test('preparazione con una nota al secondo e nessun quarto suono allo zero', () => {» nel flusso: Prove di regressione in countdown.test.js.
test('preparazione con una nota al secondo e nessun quarto suono allo zero', () => {
  // DOC L0050: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  assert.deepEqual([3, 2, 1].map(n => hornSample(n, 'PREPARE')), ['rest-3', 'rest-2', 'rest-1']);
  // DOC L0051: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  for (const phase of ['WORK', 'COOLDOWN', 'PREPARE']) for (const n of [0, 4, -1]) assert.equal(hornSample(n, phase), null);
// DOC L0052: Chiude il costrutto aperto alla riga 49.
});
// DOC L0053: Esegue il passaggio esplicito «test('ogni WAV contiene una sola nota pulita, senza frasi ripetute o saturazione', () => {» nel flusso: Prove di regressione in countdown.test.js.
test('ogni WAV contiene una sola nota pulita, senza frasi ripetute o saturazione', () => {
  // DOC L0054: frequencies: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const frequencies = { 'work-3': 330, 'work-2': 370, 'work-1': 440, 'rest-3': 660, 'rest-2': 740, 'rest-1': 880 };
  // DOC L0055: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  for (const [key, frequency] of Object.entries(frequencies)) {
    // DOC L0056: wave: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    const wave = readFileSync(new URL(`../audio/${COUNTDOWN_SOUNDS[key]}`, import.meta.url));
    // DOC L0057: pcm: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    const pcm = Array.from({ length: (wave.length - 44) / 2 }, (_, i) => wave.readInt16LE(44 + i * 2));
    // DOC L0058: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    assert.ok(Math.max(...pcm.map(Math.abs)) < 32767);
    // DOC L0059: notes: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    let notes = 0, wasLoud = false, crossings = 0;
    // DOC L0060: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    for (let i = 1; i < pcm.length; i++) if (pcm[i - 1] <= 0 && pcm[i] > 0) crossings++;
    // DOC L0061: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    for (let offset = 0; offset < pcm.length; offset += 110) {
      // DOC L0062: window: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
      const window = pcm.slice(offset, offset + 110);
      // DOC L0063: loud: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
      const loud = Math.sqrt(window.reduce((sum, n) => sum + n * n, 0) / window.length) > 500;
      // DOC L0064: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
      if (loud && !wasLoud) notes++;
      // DOC L0065: Esegue il passaggio esplicito «wasLoud = loud;» nel flusso: Prove di regressione in countdown.test.js.
      wasLoud = loud;
    // DOC L0066: Chiude il costrutto aperto alla riga 61.
    }
    // DOC L0067: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.equal(notes, 1, key); assert.ok(Math.abs(crossings / (pcm.length / 22050) - frequency) <= 12, key);
    // DOC L0068: attack: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    const attack = pcm.slice(0, 110);
    // DOC L0069: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.ok(Math.sqrt(attack.reduce((sum, n) => sum + n * n, 0) / attack.length) > 2000, `Attacco immediato: ${key}`);
  // DOC L0070: Chiude il costrutto aperto alla riga 55.
  }
// DOC L0071: Chiude il costrutto aperto alla riga 53.
});
// DOC L0072: audioSetup: Prepara o verifica il caso di regressione «audio Setup»; le asserzioni nel corpo specificano l’esito atteso.
function audioSetup() {
  // DOC L0073: signals: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di audioSetup.
  const signals = new TimerSignals(), sources = [];
  // DOC L0074: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  signals.buffers = Object.fromEntries(Object.keys({ ...COUNTDOWN_SOUNDS, ...TRANSITION_SOUNDS }).map(name => [name, { name }]));
  // DOC L0075: Esegue il passaggio esplicito «signals.audio = {» nel flusso: Prove di regressione in countdown.test.js. Nel contesto di audioSetup.
  signals.audio = {
    // DOC L0076: Esegue il passaggio esplicito «state: 'running', destination: {},» nel flusso: Prove di regressione in countdown.test.js. Nel contesto di audioSetup.
    state: 'running', destination: {},
    // DOC L0077: createGain: Prepara o verifica il caso di regressione «create Gain»; le asserzioni nel corpo specificano l’esito atteso. connect: Prepara o verifica il caso di regressione «connect»; le asserzioni nel corpo specificano l’esito atteso. disconnect: Prepara o verifica il caso di regressione «disconnect»; le asserzioni nel corpo specificano l’esito atteso.
    createGain() { return { gain: {}, connect() {}, disconnect() {} }; },
    // DOC L0078: createBufferSource: Prepara o verifica il caso di regressione «create Buffer Source»; le asserzioni nel corpo specificano l’esito atteso.
    createBufferSource() {
      // DOC L0079: connect: Prepara o verifica il caso di regressione «connect»; le asserzioni nel corpo specificano l’esito atteso. disconnect: Prepara o verifica il caso di regressione «disconnect»; le asserzioni nel corpo specificano l’esito atteso. start: Prepara o verifica il caso di regressione «start»; le asserzioni nel corpo specificano l’esito atteso. stop: Prepara o verifica il caso di regressione «stop»; le asserzioni nel corpo specificano l’esito atteso.
      const source = { playbackRate: {}, connect(gain) { this.gain = gain; return gain; }, disconnect() {}, start() {}, stop() { this.stopped = true; this.onended(); } };
      // DOC L0080: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di createBufferSource.
      sources.push(source); return source;
    // DOC L0081: Chiude il costrutto aperto alla riga 78.
    }
  // DOC L0082: Chiude il costrutto aperto alla riga 75.
  };
  // DOC L0083: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di audioSetup.
  return { signals, sources };
// DOC L0084: Chiude il costrutto aperto alla riga 72.
}
// DOC L0085: Esegue il passaggio esplicito «test('una sola voce alla volta, durata dei campioni conservata e arresto della nota precedente', () => {» nel flusso: Prove di regressione in countdown.test.js.
test('una sola voce alla volta, durata dei campioni conservata e arresto della nota precedente', () => {
  // DOC L0086: Esegue il passaggio esplicito «const { signals, sources } = audioSetup(); signals.horn(3, 'WORK'); signals.horn(2, 'COOLDOWN');» nel flusso: Prove di regressione in countdown.test.js.
  const { signals, sources } = audioSetup(); signals.horn(3, 'WORK'); signals.horn(2, 'COOLDOWN');
  // DOC L0087: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(sources[0].buffer.name, 'work-3'); assert.equal(sources[1].buffer.name, 'rest-2');
  // DOC L0088: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(sources[0].stopped, true);
  // DOC L0089: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(sources[0].playbackRate.value, 1); assert.equal(sources[1].playbackRate.value, 1);
// DOC L0090: Chiude il costrutto aperto alla riga 85.
});
// DOC L0091: Esegue il passaggio esplicito «test('pausa e disattivazione audio interrompono subito la nota in corso', () => {» nel flusso: Prove di regressione in countdown.test.js.
test('pausa e disattivazione audio interrompono subito la nota in corso', () => {
  // DOC L0092: Esegue il passaggio esplicito «const { signals, sources } = audioSetup(); signals.horn(3, 'WORK'); signals.stop(); signals.stop();» nel flusso: Prove di regressione in countdown.test.js.
  const { signals, sources } = audioSetup(); signals.horn(3, 'WORK'); signals.stop(); signals.stop();
  // DOC L0093: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(sources[0].stopped, true); assert.equal(signals.sources.size, 0);
// DOC L0094: Chiude il costrutto aperto alla riga 91.
});

// DOC L0096: Esegue il passaggio esplicito «test('suoni di cambio turno rispettano modalità silenziosa e selezione del file', () => {» nel flusso: Prove di regressione in countdown.test.js.
test('suoni di cambio turno rispettano modalità silenziosa e selezione del file', () => {
  // DOC L0097: Esegue il passaggio esplicito «const { signals, sources } = audioSetup(); signals.sound = false;» nel flusso: Prove di regressione in countdown.test.js.
  const { signals, sources } = audioSetup(); signals.sound = false;
  // DOC L0098: Prepara o riproduce il segnale audio; il motore del tempo rimane indipendente dalla riproduzione. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  for (const key of ['whistle', 'sigh', 'finish']) signals.play(key);
  // DOC L0099: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Prepara o riproduce il segnale audio; il motore del tempo rimane indipendente dalla riproduzione.
  assert.equal(sources.length, 0); signals.sound = true; signals.play('finish'); assert.equal(sources[0].buffer.name, 'finish');
// DOC L0100: Chiude il costrutto aperto alla riga 96.
});

// DOC L0102: Esegue il passaggio esplicito «test('fischietto PCM 120% con gain 1, sospiro 85%, bip e campanella 65%', () => {» nel flusso: Prove di regressione in countdown.test.js.
test('fischietto PCM 120% con gain 1, sospiro 85%, bip e campanella 65%', () => {
  // DOC L0103: Esegue il passaggio esplicito «const { signals, sources } = audioSetup();» nel flusso: Prove di regressione in countdown.test.js.
  const { signals, sources } = audioSetup();
  // DOC L0104: Prepara o riproduce il segnale audio; il motore del tempo rimane indipendente dalla riproduzione. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  for (const key of ['whistle', 'sigh', 'finish', 'work-3', 'rest-3']) signals.play(key);
  // DOC L0105: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  assert.deepEqual(sources.map(s => s.gain.gain.value), [1, .85, .65, .65, .65]);
// DOC L0106: Chiude il costrutto aperto alla riga 102.
});
