// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { pathToFileURL } from 'node:url';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { pathToFileURL } from 'node:url';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import { writeFile } from 'node:fs/promises';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { writeFile } from 'node:fs/promises';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import assert from 'node:assert/strict';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import assert from 'node:assert/strict';
// DOC L0004: Esegue il passaggio esplicito «const { chromium } = await import(process.env.PLAYWRIGHT_MODULE ? pathToFileURL(process.env.PLAYWRIGHT_MODULE).href : 'playwright');» nel flusso: Prova browser automatizzata.
const { chromium } = await import(process.env.PLAYWRIGHT_MODULE ? pathToFileURL(process.env.PLAYWRIGHT_MODULE).href : 'playwright');
// DOC L0005: browser: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const browser = await chromium.launch({ channel: process.env.TABATA_BROWSER || 'msedge', headless: true });
// DOC L0006: Esegue il passaggio esplicito «try {» nel flusso: Prova browser automatizzata.
try {
  // DOC L0007: context: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const context = await browser.newContext({ locale: 'it-IT', reducedMotion: 'reduce' });
  // DOC L0008: Esegue il passaggio esplicito «await context.addInitScript(() => {» nel flusso: Prova browser automatizzata.
  await context.addInitScript(() => {
    // DOC L0009: Esegue il passaggio esplicito «window.__timerClock = 0; window.__hornRates = []; window.__hornSamples = []; window.__hornDecoded = 0; window.__hornEvents = []; window.__clipNames = new WeakMap(); window.__stopEv» nel flusso: Prova browser automatizzata.
    window.__timerClock = 0; window.__hornRates = []; window.__hornSamples = []; window.__hornDecoded = 0; window.__hornEvents = []; window.__clipNames = new WeakMap(); window.__stopEvents = []; window.__assetInputs = new WeakMap();
    // DOC L0010: originalFetch: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    const originalFetch = window.fetch;
    // DOC L0011: Esegue il passaggio esplicito «window.fetch = async function (input, ...args) {» nel flusso: Prova browser automatizzata.
    window.fetch = async function (input, ...args) {
      // DOC L0012: response: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
      const response = await originalFetch.call(this, input, ...args);
      // DOC L0013: file: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
      const file = new URL(typeof input === 'string' ? input : input.url || input.toString(), location.href).pathname.split('/').pop();
      // DOC L0014: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
      if (file.endsWith('.wav')) {
        // DOC L0015: read: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
        const read = response.arrayBuffer.bind(response);
        // DOC L0016: data: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Restituisce il valore calcolato o conclude il percorso della funzione.
        response.arrayBuffer = async () => { const data = await read(); window.__assetInputs.set(data, file); return data; };
      // DOC L0017: Chiude il costrutto aperto alla riga 14.
      }
      // DOC L0018: Restituisce il valore calcolato o conclude il percorso della funzione.
      return response;
    // DOC L0019: Chiude il costrutto aperto alla riga 11.
    };
    // DOC L0020: decode: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    const decode = AudioContext.prototype.decodeAudioData;
    // DOC L0021: Esegue il passaggio esplicito «AudioContext.prototype.decodeAudioData = async function (...args) {» nel flusso: Prova browser automatizzata.
    AudioContext.prototype.decodeAudioData = async function (...args) {
      // DOC L0022: file: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
      const file = window.__assetInputs.get(args[0]);
      // DOC L0023: buffer: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
      const buffer = await decode.apply(this, args), data = buffer.getChannelData(0);
      // DOC L0024: marker: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
      const marker = { 'work-start.wav': 'whistle', 'rest-start.wav': 'sigh', 'workout-finish.wav': 'finish' }[file];
      // DOC L0025: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione.
      if (marker) { window.__clipNames.set(buffer, marker); window.__hornDecoded++; return buffer; }
      // DOC L0026: expected: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
      const expected = { 'work-3': 330, 'work-2': 370, 'work-1': 440, 'rest-3': 660, 'rest-2': 740, 'rest-1': 880 };
      // DOC L0027: start: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
      const start = Math.round(buffer.sampleRate * .005), end = Math.round(buffer.sampleRate * (buffer.duration - .015));
      // DOC L0028: energy: Passaggio «energy» dello strumento: Prova browser automatizzata. Gli argomenti e le operazioni sono descritti nelle righe del corpo.
      const energy = frequency => {
        // DOC L0029: real: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di energy.
        let real = 0, imaginary = 0;
        // DOC L0030: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di energy.
        for (let i = start; i < end; i++) {
          // DOC L0031: angle: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di energy.
          const angle = 2 * Math.PI * frequency * i / buffer.sampleRate;
          // DOC L0032: window: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di energy.
          const window = .5 - .5 * Math.cos(2 * Math.PI * (i - start) / (end - start - 1));
          // DOC L0033: Esegue il passaggio esplicito «real += data[i] * window * Math.cos(angle); imaginary += data[i] * window * Math.sin(angle);» nel flusso: Prova browser automatizzata. Nel contesto di energy.
          real += data[i] * window * Math.cos(angle); imaginary += data[i] * window * Math.sin(angle);
        // DOC L0034: Chiude il costrutto aperto alla riga 30.
        }
        // DOC L0035: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di energy.
        return real * real + imaginary * imaginary;
      // DOC L0036: Chiude il costrutto aperto alla riga 28.
      };
      // DOC L0037: key: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
      const key = Object.keys(expected).sort((a, b) => energy(expected[b]) - energy(expected[a]))[0];
      // DOC L0038: Restituisce il valore calcolato o conclude il percorso della funzione.
      window.__clipNames.set(buffer, key); window.__hornDecoded++; return buffer;
    // DOC L0039: Chiude il costrutto aperto alla riga 21.
    };
    // DOC L0040: value: Passaggio «value» dello strumento: Prova browser automatizzata. Gli argomenti e le operazioni sono descritti nelle righe del corpo.
    Object.defineProperty(performance, 'now', { value: () => window.__timerClock });
    // DOC L0041: create: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    const create = AudioContext.prototype.createBufferSource;
    // DOC L0042: Esegue il passaggio esplicito «AudioContext.prototype.createBufferSource = function () {» nel flusso: Prova browser automatizzata.
    AudioContext.prototype.createBufferSource = function () {
      // DOC L0043: source: sorgente audio o testo sorgente: il contesto della funzione ne specifica l’uso.
      const source = create.call(this), start = source.start, stop = source.stop, connect = source.connect;
      // DOC L0044: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione.
      source.connect = function (target, ...args) { if (target.gain) this.__outputGain = target.gain; return connect.call(this, target, ...args); };
      // DOC L0045: Restituisce il valore calcolato o conclude il percorso della funzione.
      source.stop = function (...args) { window.__stopEvents.push({ clip: window.__clipNames.get(this.buffer), at: window.__timerClock }); return stop.apply(this, args); };
      // DOC L0046: Restituisce il valore calcolato o conclude il percorso della funzione.
      source.start = function (...args) { window.__hornRates.push(Number(this.playbackRate.value.toFixed(2))); window.__hornSamples.push(window.__clipNames.get(this.buffer)); window.__hornEvents.push({ at: window.__timerClock, duration: this.buffer.duration, volume: this.__outputGain?.value }); return start.apply(this, args); };
      // DOC L0047: Restituisce il valore calcolato o conclude il percorso della funzione.
      return source;
    // DOC L0048: Chiude il costrutto aperto alla riga 42.
    };
  // DOC L0049: Chiude il costrutto aperto alla riga 8.
  });
  // DOC L0050: page: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const page = await context.newPage(), errors = [];
  // DOC L0051: Esegue il passaggio esplicito «page.on('pageerror', e => errors.push(e.message));» nel flusso: Prova browser automatizzata.
  page.on('pageerror', e => errors.push(e.message));
  // DOC L0052: Esegue il passaggio esplicito «await page.goto(process.env.TABATA_URL || 'http://127.0.0.1:5500');» nel flusso: Prova browser automatizzata.
  await page.goto(process.env.TABATA_URL || 'http://127.0.0.1:5500');
  // DOC L0053: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Tabata Timer', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Tabata Timer', exact: true }).waitFor();
  // DOC L0054: Esegue il passaggio esplicito «await page.evaluate(async () => { await navigator.serviceWorker.ready; });» nel flusso: Prova browser automatizzata.
  await page.evaluate(async () => { await navigator.serviceWorker.ready; });
  // DOC L0055: Esegue il passaggio esplicito «await page.reload(); await page.getByRole('heading', { name: 'Tabata Timer', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.reload(); await page.getByRole('heading', { name: 'Tabata Timer', exact: true }).waitFor();
  // DOC L0056: Esegue il passaggio esplicito «await context.setOffline(true); await page.reload();» nel flusso: Prova browser automatizzata.
  await context.setOffline(true); await page.reload();
  // DOC L0057: Esegue il passaggio esplicito «await page.locator('[data-quick-path="blocks.0.exercises.0.workSec"]').fill('4');» nel flusso: Prova browser automatizzata.
  await page.locator('[data-quick-path="blocks.0.exercises.0.workSec"]').fill('4');
  // DOC L0058: Esegue il passaggio esplicito «await page.locator('[data-quick-path="blocks.0.exercises.0.restBetweenRepsSec"]').fill('4');» nel flusso: Prova browser automatizzata.
  await page.locator('[data-quick-path="blocks.0.exercises.0.restBetweenRepsSec"]').fill('4');
  // DOC L0059: Esegue il passaggio esplicito «await page.locator('[data-quick-path="blocks.0.exercises.0.repetitions"]').fill('2');» nel flusso: Prova browser automatizzata.
  await page.locator('[data-quick-path="blocks.0.exercises.0.repetitions"]').fill('2');
  // DOC L0060: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.
  await page.getByText('Altre opzioni', { exact: true }).click();
  // DOC L0061: Esegue il passaggio esplicito «await page.locator('[data-quick-path="cooldownSec"]').fill('4');» nel flusso: Prova browser automatizzata.
  await page.locator('[data-quick-path="cooldownSec"]').fill('4');
  // DOC L0062: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();
  // DOC L0063: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Salva cronologia', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Salva cronologia', exact: true }).click();
  // DOC L0064: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Timer', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Timer', exact: true }).click();
  // DOC L0065: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Avvia', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Avvia', exact: true }).click();
  // DOC L0066: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Preparazione', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Preparazione', exact: true }).waitFor();
  // DOC L0067: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.locator('#timer-digits').textContent(), '0:20');
  // DOC L0068: Esegue il passaggio esplicito «await page.waitForFunction(() => window.__hornDecoded === 9);» nel flusso: Prova browser automatizzata.
  await page.waitForFunction(() => window.__hornDecoded === 9);
  // DOC L0069: at: Passaggio «at» dello strumento: Prova browser automatizzata. Gli argomenti e le operazioni sono descritti nelle righe del corpo.
  const at = async (time, digits, notes) => {
    // DOC L0070: Esegue il passaggio esplicito «await page.evaluate(value => { window.__timerClock = value; }, time);» nel flusso: Prova browser automatizzata.
    await page.evaluate(value => { window.__timerClock = value; }, time);
    // DOC L0071: Esegue il passaggio esplicito «await page.waitForFunction(expected => document.querySelector('#timer-digits').textContent === expected, digits);» nel flusso: Prova browser automatizzata.
    await page.waitForFunction(expected => document.querySelector('#timer-digits').textContent === expected, digits);
    // DOC L0072: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.equal(await page.evaluate(() => __hornRates.length), notes);
  // DOC L0073: Chiude il costrutto aperto alla riga 69.
  };
  // DOC L0074: Esegue il passaggio esplicito «await at(17000, '0:03', 1);» nel flusso: Prova browser automatizzata.
  await at(17000, '0:03', 1);
  // DOC L0075: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Pausa', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Pausa', exact: true }).click();
  // DOC L0076: Esegue il passaggio esplicito «await at(27000, '0:03', 1);» nel flusso: Prova browser automatizzata.
  await at(27000, '0:03', 1);
  // DOC L0077: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Riprendi', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Riprendi', exact: true }).click();
  // DOC L0078: Esegue il passaggio esplicito «await at(28000, '0:02', 2); await at(29000, '0:01', 3);» nel flusso: Prova browser automatizzata.
  await at(28000, '0:02', 2); await at(29000, '0:01', 3);
  // DOC L0079: Esegue il passaggio esplicito «await at(30000, '0:04', 4); await page.getByRole('heading', { name: 'Lavoro', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await at(30000, '0:04', 4); await page.getByRole('heading', { name: 'Lavoro', exact: true }).waitFor();
  // DOC L0080: Esegue il passaggio esplicito «await at(31000, '0:03', 5); await at(31500, '0:03', 5); await at(32000, '0:02', 6); await at(33000, '0:01', 7);» nel flusso: Prova browser automatizzata.
  await at(31000, '0:03', 5); await at(31500, '0:03', 5); await at(32000, '0:02', 6); await at(33000, '0:01', 7);
  // DOC L0081: Esegue il passaggio esplicito «await at(34000, '0:04', 8); await page.getByRole('heading', { name: 'Recupero', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await at(34000, '0:04', 8); await page.getByRole('heading', { name: 'Recupero', exact: true }).waitFor();
  // DOC L0082: Esegue il passaggio esplicito «await at(35000, '0:03', 9); await at(36000, '0:02', 10); await at(37000, '0:01', 11);» nel flusso: Prova browser automatizzata.
  await at(35000, '0:03', 9); await at(36000, '0:02', 10); await at(37000, '0:01', 11);
  // DOC L0083: Esegue il passaggio esplicito «await at(38000, '0:04', 12); await at(39000, '0:03', 13); await at(40000, '0:02', 14); await at(41000, '0:01', 15);» nel flusso: Prova browser automatizzata.
  await at(38000, '0:04', 12); await at(39000, '0:03', 13); await at(40000, '0:02', 14); await at(41000, '0:01', 15);
  // DOC L0084: Esegue il passaggio esplicito «await at(42000, '0:04', 16); await page.getByRole('heading', { name: 'Defaticamento', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await at(42000, '0:04', 16); await page.getByRole('heading', { name: 'Defaticamento', exact: true }).waitFor();
  // DOC L0085: Esegue il passaggio esplicito «await at(43000, '0:03', 17); await at(44000, '0:02', 18); await at(45000, '0:01', 19); await at(46000, '0:00', 19);» nel flusso: Prova browser automatizzata.
  await at(43000, '0:03', 17); await at(44000, '0:02', 18); await at(45000, '0:01', 19); await at(46000, '0:00', 19);
  // DOC L0086: rates: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const rates = await page.evaluate(() => __hornRates);
  // DOC L0087: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.deepEqual(rates, Array(19).fill(1));
  // DOC L0088: samples: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const samples = await page.evaluate(() => __hornSamples);
  // DOC L0089: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.deepEqual(samples, ['rest-3', 'rest-2', 'rest-1', 'whistle', 'work-3', 'work-2', 'work-1', 'sigh', 'rest-3', 'rest-2', 'rest-1', 'whistle', 'work-3', 'work-2', 'work-1', 'finish', 'rest-3', 'rest-2', 'rest-1']);
  // DOC L0090: events: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const events = await page.evaluate(() => __hornEvents);
  // DOC L0091: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  assert.deepEqual(events.map(e => e.at), [17000, 28000, 29000, ...Array.from({ length: 16 }, (_, i) => (30 + i) * 1000)]);
  // DOC L0092: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  events.forEach((event, i) => assert.ok(Math.abs(event.duration - ({ whistle: .5, sigh: .5, finish: .9 }[samples[i]] ?? .12)) < .001));
  // DOC L0093: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  events.forEach((event, i) => assert.ok(Math.abs(event.volume - (samples[i] === 'whistle' ? 1 : samples[i] === 'sigh' ? .85 : .65)) < .00001));
  // DOC L0094: Legge il valore locale e applica i controlli/fallback riportati nell’espressione. session: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const session = await page.evaluate(() => JSON.parse(localStorage.getItem('tabata:v1:data')).sessions[0]);
  // DOC L0095: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(session.plannedMs, 16000); assert.equal(session.elapsedMs, 16000); assert.equal(session.workMs, 8000); assert.equal(session.pauses, 1);
  // A one-second interval: phase-start whistle has priority over the coincident beep.
  // DOC L0097: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Nuovo timer', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Nuovo timer', exact: true }).click();
  // DOC L0098: Esegue il passaggio esplicito «await page.locator('[data-quick-path="blocks.0.exercises.0.workSec"]').fill('1');» nel flusso: Prova browser automatizzata.
  await page.locator('[data-quick-path="blocks.0.exercises.0.workSec"]').fill('1');
  // DOC L0099: Esegue il passaggio esplicito «await page.locator('[data-quick-path="blocks.0.exercises.0.restBetweenRepsSec"]').fill('0');» nel flusso: Prova browser automatizzata.
  await page.locator('[data-quick-path="blocks.0.exercises.0.restBetweenRepsSec"]').fill('0');
  // DOC L0100: Esegue il passaggio esplicito «await page.locator('[data-quick-path="blocks.0.exercises.0.repetitions"]').fill('1');» nel flusso: Prova browser automatizzata.
  await page.locator('[data-quick-path="blocks.0.exercises.0.repetitions"]').fill('1');
  // DOC L0101: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.
  await page.getByText('Altre opzioni', { exact: true }).click();
  // DOC L0102: Esegue il passaggio esplicito «await page.locator('[data-quick-path="cooldownSec"]').fill('0');» nel flusso: Prova browser automatizzata.
  await page.locator('[data-quick-path="cooldownSec"]').fill('0');
  // DOC L0103: beforeShort: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const beforeShort = await page.evaluate(() => __hornSamples.length);
  // DOC L0104: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Avvia', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Avvia', exact: true }).click();
  // DOC L0105: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Salta →', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Salta →', exact: true }).click();
  // DOC L0106: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.deepEqual(await page.evaluate(n => __hornSamples.slice(n), beforeShort), ['whistle']);
  // DOC L0107: Esegue il passaggio esplicito «await page.evaluate(() => { window.__timerClock = 47000; });» nel flusso: Prova browser automatizzata.
  await page.evaluate(() => { window.__timerClock = 47000; });
  // DOC L0108: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Allenamento completato.', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Allenamento completato.', exact: true }).waitFor();
  // DOC L0109: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.deepEqual(await page.evaluate(n => __hornSamples.slice(n), beforeShort), ['whistle', 'finish']);
  // DOC L0110: stopCount: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const stopCount = await page.evaluate(() => __stopEvents.filter(e => e.clip === 'finish').length);
  // DOC L0111: Esegue il passaggio esplicito «await page.waitForTimeout(1100);» nel flusso: Prova browser automatizzata.
  await page.waitForTimeout(1100);
  // DOC L0112: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.evaluate(() => __stopEvents.filter(e => e.clip === 'finish').length), stopCount);
  // Manual interruption must never be announced as completed training.
  // DOC L0114: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Nuovo timer', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Nuovo timer', exact: true }).click();
  // DOC L0115: beforeStop: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const beforeStop = await page.evaluate(() => __hornSamples.length);
  // DOC L0116: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Avvia', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Avvia', exact: true }).click();
  // DOC L0117: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Salta →', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Salta →', exact: true }).click();
  // DOC L0118: Esegue il passaggio esplicito «page.once('dialog', d => d.accept());» nel flusso: Prova browser automatizzata.
  page.once('dialog', d => d.accept());
  // DOC L0119: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Termina', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Termina', exact: true }).click();
  // DOC L0120: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Sessione interrotta.', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Sessione interrotta.', exact: true }).waitFor();
  // DOC L0121: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.deepEqual(await page.evaluate(n => __hornSamples.slice(n), beforeStop), ['whistle']);
  // DOC L0122: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.deepEqual(errors, []);
  // DOC L0123: Esegue il passaggio esplicito «await writeFile('output/browser-countdown.json', JSON.stringify({ offline: true, shortIntervalPriority: true, completionWithoutCooldown: true, finishPlaysToEnd: true, noBellOnManua» nel flusso: Prova browser automatizzata.
  await writeFile('output/browser-countdown.json', JSON.stringify({ offline: true, shortIntervalPriority: true, completionWithoutCooldown: true, finishPlaysToEnd: true, noBellOnManualStop: true, preparationMs: 20000, hornRates: rates, hornSamples: samples, noteEvents: events, elapsedMs: session.elapsedMs, workMs: session.workMs, pauses: session.pauses, errors }, null, 2));
  // DOC L0124: Esegue il passaggio esplicito «console.log('Offline: bip 3-2-1 conservati, fischietto e sospiro 500 ms con fischio PCM 120% e sospiro 85%, conto basso nel lavoro e alto nel riposo, tre ding prima del defaticamen» nel flusso: Prova browser automatizzata.
  console.log('Offline: bip 3-2-1 conservati, fischietto e sospiro 500 ms con fischio PCM 120% e sospiro 85%, conto basso nel lavoro e alto nel riposo, tre ding prima del defaticamento; pause senza duplicati e statistiche corrette.');
// DOC L0125: Esegue il passaggio esplicito «} finally { await browser.close(); }» nel flusso: Prova browser automatizzata.
} finally { await browser.close(); }
