// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { test } from 'node:test';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { test } from 'node:test';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import assert from 'node:assert/strict';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import assert from 'node:assert/strict';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import { readFileSync } from 'node:fs';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { readFileSync } from 'node:fs';
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import { TransitionCues, TRANSITION_SOUNDS } from '../js/transitions.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { TransitionCues, TRANSITION_SOUNDS } from '../js/transitions.js';
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import { TimerEngine } from '../js/timer.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { TimerEngine } from '../js/timer.js';
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import { newWorkout } from '../js/model.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { newWorkout } from '../js/model.js';
// DOC L0007: setup: Prepara o verifica il caso di regressione «setup»; le asserzioni nel corpo specificano l’esito atteso.
function setup(cooldown = 4) {
  // DOC L0008: clock: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di setup.
  let clock = 0; const w = newWorkout(); w.cooldownSec = cooldown;
  // DOC L0009: Esegue il passaggio esplicito «Object.assign(w.blocks[0].exercises[0], { workSec: 4, repetitions: 2, restBetweenRepsSec: 4 });» nel flusso: Prove di regressione in transitions.test.js. Nel contesto di setup.
  Object.assign(w.blocks[0].exercises[0], { workSec: 4, repetitions: 2, restBetweenRepsSec: 4 });
  // DOC L0010: timer: motore corrente, opzionale quando nessuna sessione è stata avviata.
  const timer = new TimerEngine(w, () => clock), cues = new TransitionCues(); timer.start();
  // DOC L0011: advance: Incrementa indice e visita di fase; alla fine produce il risultato senza aggiungere un recupero finale.
  return { timer, cues, advance(ms) { clock += ms; timer.tick(); } };
// DOC L0012: Chiude il costrutto aperto alla riga 7.
}
// DOC L0013: Esegue il passaggio esplicito «test('fischietto all’inizio del lavoro, senza ripetizione dopo pausa', () => {» nel flusso: Prove di regressione in transitions.test.js.
test('fischietto all’inizio del lavoro, senza ripetizione dopo pausa', () => {
  // DOC L0014: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  const { timer, cues, advance } = setup(); assert.equal(cues.read(timer), null);
  // DOC L0015: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  advance(20000); assert.equal(cues.read(timer), 'whistle'); assert.equal(cues.read(timer), null);
  // DOC L0016: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  timer.pause(); assert.equal(cues.read(timer), null); advance(5000); timer.resume(); assert.equal(cues.read(timer), null);
// DOC L0017: Chiude il costrutto aperto alla riga 13.
});
// DOC L0018: Esegue il passaggio esplicito «test('sospiro all’inizio del recupero e fischietto al turno successivo', () => {» nel flusso: Prove di regressione in transitions.test.js.
test('sospiro all’inizio del recupero e fischietto al turno successivo', () => {
  // DOC L0019: Esegue il passaggio esplicito «const { timer, cues, advance } = setup(); advance(20000); cues.read(timer);» nel flusso: Prove di regressione in transitions.test.js.
  const { timer, cues, advance } = setup(); advance(20000); cues.read(timer);
  // DOC L0020: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  advance(4000); assert.equal(cues.read(timer), 'sigh'); assert.equal(cues.read(timer), null);
  // DOC L0021: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  advance(4000); assert.equal(cues.read(timer), 'whistle');
// DOC L0022: Chiude il costrutto aperto alla riga 18.
});
// DOC L0023: Esegue il passaggio esplicito «test('tre ding all’inizio del defaticamento, una sola volta anche tornando indietro', () => {» nel flusso: Prove di regressione in transitions.test.js.
test('tre ding all’inizio del defaticamento, una sola volta anche tornando indietro', () => {
  // DOC L0024: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  const { timer, cues, advance } = setup(); advance(32000); assert.equal(cues.read(timer), 'finish');
  // DOC L0025: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  timer.previous(); assert.equal(cues.read(timer), 'whistle'); timer.skip(); assert.equal(cues.read(timer), null);
  // DOC L0026: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  advance(4000); assert.equal(cues.read(timer), null);
// DOC L0027: Chiude il costrutto aperto alla riga 23.
});
// DOC L0028: Esegue il passaggio esplicito «test('senza defaticamento campanella alla fine; Termina manuale non suona', () => {» nel flusso: Prove di regressione in transitions.test.js.
test('senza defaticamento campanella alla fine; Termina manuale non suona', () => {
  // DOC L0029: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. natural: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const natural = setup(0); natural.advance(32000); assert.equal(natural.cues.read(natural.timer), 'finish'); assert.equal(natural.cues.read(natural.timer), null);
  // DOC L0030: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. stopped: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const stopped = setup(); stopped.advance(20000); stopped.cues.read(stopped.timer); stopped.timer.stop(); assert.equal(stopped.cues.read(stopped.timer), null);
// DOC L0031: Chiude il costrutto aperto alla riga 28.
});
// DOC L0032: Esegue il passaggio esplicito «test('cambio turno in pausa: segnale solo alla ripresa; precedente è una nuova visita', () => {» nel flusso: Prove di regressione in transitions.test.js.
test('cambio turno in pausa: segnale solo alla ripresa; precedente è una nuova visita', () => {
  // DOC L0033: Esegue il passaggio esplicito «const { timer, cues, advance } = setup(); advance(20000); cues.read(timer); timer.pause(); timer.skip();» nel flusso: Prove di regressione in transitions.test.js.
  const { timer, cues, advance } = setup(); advance(20000); cues.read(timer); timer.pause(); timer.skip();
  // DOC L0034: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(cues.read(timer), null); timer.resume(); assert.equal(cues.read(timer), 'sigh');
  // DOC L0035: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  timer.previous(); assert.equal(cues.read(timer), 'whistle');
// DOC L0036: Chiude il costrutto aperto alla riga 32.
});
// DOC L0037: Esegue il passaggio esplicito «test('ritardi non producono fischi arretrati; una nuova sessione ripristina i segnali', () => {» nel flusso: Prove di regressione in transitions.test.js.
test('ritardi non producono fischi arretrati; una nuova sessione ripristina i segnali', () => {
  // DOC L0038: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  const { timer, cues, advance } = setup(); advance(22000); assert.equal(cues.read(timer), null);
  // DOC L0039: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  advance(2000); assert.equal(cues.read(timer), 'sigh');
  // DOC L0040: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. other: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const other = setup(); other.advance(20000); assert.equal(cues.read(other.timer), 'whistle');
// DOC L0041: Chiude il costrutto aperto alla riga 37.
});
// DOC L0042: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
for (const [key, duration, expectedNotes] of [['whistle', .5, 1], ['sigh', .5, null], ['finish', .9, 3]]) test(`campione ${key}: durata, pause, livelli e identità Android/web`, () => {
  // DOC L0043: web: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const web = readFileSync(new URL(`../audio/${TRANSITION_SOUNDS[key]}`, import.meta.url));
  // DOC L0044: android: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const android = readFileSync(new URL(`../../android/app/src/main/res/raw/${TRANSITION_SOUNDS[key].replaceAll('-', '_')}`, import.meta.url));
  // DOC L0045: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.deepEqual(web, android); assert.equal(web.readUInt32LE(24), 24000);
  // DOC L0046: pcm: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const pcm = Array.from({ length: (web.length - 44) / 2 }, (_, i) => web.readInt16LE(44 + i * 2));
  // DOC L0047: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  assert.equal(pcm.length / 24000, duration); assert.ok(Math.max(...pcm.map(Math.abs)) < 32767);
  // DOC L0048: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  if (key === 'whistle') assert.equal(Math.max(...pcm.map(Math.abs)), 13200, '11000 * 120% con gain 1');
  // DOC L0049: notes: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  let notes = 0, wasLoud = false;
  // DOC L0050: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  for (let offset = 0; offset < pcm.length; offset += 120) {
    // DOC L0051: window: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    const window = pcm.slice(offset, offset + 120);
    // DOC L0052: loud: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    const loud = Math.sqrt(window.reduce((sum, n) => sum + n * n, 0) / window.length) > 500;
    // DOC L0053: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
    if (loud && !wasLoud) notes++;
    // DOC L0054: Esegue il passaggio esplicito «wasLoud = loud;» nel flusso: Prove di regressione in transitions.test.js.
    wasLoud = loud;
  // DOC L0055: Chiude il costrutto aperto alla riga 50.
  }
  // DOC L0056: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
  if (expectedNotes !== null) assert.equal(notes, expectedNotes);
  // DOC L0057: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
  if (key === 'sigh') assert.deepEqual(web, readFileSync(new URL('../../assets/audio/sigh-reference.wav', import.meta.url)));
// DOC L0058: Chiude il costrutto aperto alla riga 42.
});

// DOC L0060: Esegue il passaggio esplicito «test('fischio da arbitro meno acuto: tono dominante sotto 2100 Hz e attacco morbido', () => {» nel flusso: Prove di regressione in transitions.test.js.
test('fischio da arbitro meno acuto: tono dominante sotto 2100 Hz e attacco morbido', () => {
  // DOC L0061: wav: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const wav = readFileSync(new URL('../audio/work-start.wav', import.meta.url));
  // DOC L0062: rate: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const rate = wav.readUInt32LE(24), count = (wav.length - 44) / 2;
  // DOC L0063: pcm: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const pcm = Array.from({ length: count }, (_, i) => wav.readInt16LE(44 + i * 2));
  // DOC L0064: central: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const central = pcm.slice(Math.round(.05 * rate), Math.round(.45 * rate));
  // DOC L0065: positiveCrossings: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const positiveCrossings = central.slice(1).filter((v, i) => central[i] <= 0 && v > 0).length;
  // DOC L0066: frequency: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const frequency = positiveCrossings / (central.length / rate);
  // DOC L0067: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.ok(frequency > 1400 && frequency < 2100, `Dominante ${frequency} Hz`);
  // DOC L0068: rms: Prepara o verifica il caso di regressione «rms»; le asserzioni nel corpo specificano l’esito atteso.
  const rms = values => Math.sqrt(values.reduce((sum, x) => sum + x * x, 0) / values.length);
  // DOC L0069: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.ok(rms(pcm.slice(0, Math.round(.005 * rate))) < rms(central) * .4);
  // DOC L0070: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.ok(rms(pcm.slice(-Math.round(.01 * rate))) < rms(central) * .3);
// DOC L0071: Chiude il costrutto aperto alla riga 60.
});
