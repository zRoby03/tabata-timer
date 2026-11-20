// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { pathToFileURL } from 'node:url';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { pathToFileURL } from 'node:url';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import { readFile, writeFile, mkdir } from 'node:fs/promises';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { readFile, writeFile, mkdir } from 'node:fs/promises';
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
  const page = await context.newPage(), errors = [], checks = [];
  // DOC L0051: Esegue il passaggio esplicito «page.on('pageerror', e => errors.push(e.message));» nel flusso: Prova browser automatizzata.
  page.on('pageerror', e => errors.push(e.message));
  // DOC L0052: base: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const base = process.env.TABATA_URL || 'http://127.0.0.1:5500';
  // DOC L0053: Esegue il passaggio esplicito «await page.goto(base);» nel flusso: Prova browser automatizzata.
  await page.goto(base);
  // DOC L0054: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Tabata Timer', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Tabata Timer', exact: true }).waitFor();
  // DOC L0055: Esegue il passaggio esplicito «await page.getByRole('spinbutton', { name: 'Lavoro', exact: true }).fill('7');» nel flusso: Prova browser automatizzata.
  await page.getByRole('spinbutton', { name: 'Lavoro', exact: true }).fill('7');
  // DOC L0056: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Salva allenamento', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Salva allenamento', exact: true }).click();
  // DOC L0057: Esegue il passaggio esplicito «await page.locator('#save-name').fill('Circuito locale');» nel flusso: Prova browser automatizzata.
  await page.locator('#save-name').fill('Circuito locale');
  // DOC L0058: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Salva sul dispositivo', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Salva sul dispositivo', exact: true }).click();
  // DOC L0059: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
  await page.getByRole('switch', { name: 'Modalità Studio' }).click();
  // DOC L0060: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Tabata Timer', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Tabata Timer', exact: true }).waitFor();
  // DOC L0061: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.getByRole('spinbutton', { name: 'Concentrazione · Attività 1', exact: true }).inputValue(), '25');
  // DOC L0062: Esegue il passaggio esplicito «await page.getByRole('spinbutton', { name: 'Concentrazione · Attività 1', exact: true }).fill('1');» nel flusso: Prova browser automatizzata.
  await page.getByRole('spinbutton', { name: 'Concentrazione · Attività 1', exact: true }).fill('1');
  // DOC L0063: Esegue il passaggio esplicito «await page.getByRole('spinbutton', { name: 'Pausa · Attività 1', exact: true }).fill('1');» nel flusso: Prova browser automatizzata.
  await page.getByRole('spinbutton', { name: 'Pausa · Attività 1', exact: true }).fill('1');
  // DOC L0064: Esegue il passaggio esplicito «await page.getByRole('spinbutton', { name: 'Sessioni · Attività 1', exact: true }).fill('2');» nel flusso: Prova browser automatizzata.
  await page.getByRole('spinbutton', { name: 'Sessioni · Attività 1', exact: true }).fill('2');
  // DOC L0065: Esegue il passaggio esplicito «await page.getByRole('button', { name: '+ Aggiungi attività', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: '+ Aggiungi attività', exact: true }).click();
  // DOC L0066: Esegue il passaggio esplicito «await page.getByRole('textbox', { name: 'Nome attività · Attività 2', exact: true }).fill('Storia');» nel flusso: Prova browser automatizzata.
  await page.getByRole('textbox', { name: 'Nome attività · Attività 2', exact: true }).fill('Storia');
  // DOC L0067: Esegue il passaggio esplicito «await page.getByRole('spinbutton', { name: 'Concentrazione · Attività 2', exact: true }).fill('3');» nel flusso: Prova browser automatizzata.
  await page.getByRole('spinbutton', { name: 'Concentrazione · Attività 2', exact: true }).fill('3');
  // DOC L0068: Esegue il passaggio esplicito «await page.getByRole('spinbutton', { name: 'Sessioni · Attività 2', exact: true }).fill('1');» nel flusso: Prova browser automatizzata.
  await page.getByRole('spinbutton', { name: 'Sessioni · Attività 2', exact: true }).fill('1');
  // DOC L0069: Esegue il passaggio esplicito «await page.getByRole('spinbutton', { name: 'Pausa tra attività', exact: true }).fill('4');» nel flusso: Prova browser automatizzata.
  await page.getByRole('spinbutton', { name: 'Pausa tra attività', exact: true }).fill('4');
  // DOC L0070: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.locator('#quick-total').textContent(), '10:00');
  // DOC L0071: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.locator('[data-quick-path]').count(), 9);
  // DOC L0072: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.
  assert.doesNotMatch(await page.locator('main').innerText(), /Pausa lunga|Blocco|Cicli|Serie|Pausa finale|\b(min|sec)\b/);
  // DOC L0073: Esegue il passaggio esplicito «checks.push('flat-activities-and-shared-pause-no-final-break');» nel flusso: Prova browser automatizzata.
  checks.push('flat-activities-and-shared-pause-no-final-break');
  // DOC L0074: Esegue il passaggio esplicito «await mkdir('output/screenshots', { recursive: true });» nel flusso: Prova browser automatizzata.
  await mkdir('output/screenshots', { recursive: true });
  // DOC L0075: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  for (const width of [320, 390, 768, 1440]) {
    // DOC L0076: Esegue il passaggio esplicito «await page.setViewportSize({ width, height: 900 });» nel flusso: Prova browser automatizzata.
    await page.setViewportSize({ width, height: 900 });
    // DOC L0077: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.ok(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth), `overflow at ${width}`);
    // DOC L0078: Esegue il passaggio esplicito «await page.screenshot({ path: `output/screenshots/studio-${width}.png`, fullPage: true });» nel flusso: Prova browser automatizzata.
    await page.screenshot({ path: `output/screenshots/studio-${width}.png`, fullPage: true });
  // DOC L0079: Chiude il costrutto aperto alla riga 75.
  }
  // DOC L0080: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
  checks.push('responsive-switch');
  // DOC L0081: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Salva programma di studio', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Salva programma di studio', exact: true }).click();
  // DOC L0082: Esegue il passaggio esplicito «await page.locator('#save-name').fill('Matematica');» nel flusso: Prova browser automatizzata.
  await page.locator('#save-name').fill('Matematica');
  // DOC L0083: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Salva sul dispositivo', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Salva sul dispositivo', exact: true }).click();
  // DOC L0084: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Programmi di studio', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Programmi di studio', exact: true }).waitFor();
  // DOC L0085: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.getByRole('heading', { name: 'Circuito locale', exact: true }).count(), 0);
  // DOC L0086: Esegue il passaggio esplicito «await page.locator('.workout-menu summary').click(); await page.getByRole('button', { name: 'Modifica', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.locator('.workout-menu summary').click(); await page.getByRole('button', { name: 'Modifica', exact: true }).click();
  // DOC L0087: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.locator('[data-path="activities.0.concentrationSec"]').inputValue(), '1');
  // DOC L0088: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.locator('[data-path="restBetweenActivitiesSec"]').inputValue(), '4');
  // DOC L0089: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.
  assert.match(await page.locator('#preview').innerText(), /Pausa tra sessioni/);
  // DOC L0090: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.
  assert.match(await page.locator('#preview').innerText(), /Pausa tra attività/);
  // DOC L0091: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.
  assert.doesNotMatch(await page.locator('#preview').innerText(), /Pausa tra serie|Pausa tra cicli|\b(min|sec)\b/);
  // DOC L0092: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Torna ai programmi', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Torna ai programmi', exact: true }).click();
  // DOC L0093: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Avvia', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Avvia', exact: true }).click();
  // DOC L0094: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Preparazione', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Preparazione', exact: true }).waitFor();
  // DOC L0095: Esegue il passaggio esplicito «await page.waitForFunction(() => __hornDecoded === 9);» nel flusso: Prova browser automatizzata.
  await page.waitForFunction(() => __hornDecoded === 9);
  // DOC L0096: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
  assert.equal(await page.getByRole('switch').isDisabled(), true);
  // DOC L0097: at: Passaggio «at» dello strumento: Prova browser automatizzata. Gli argomenti e le operazioni sono descritti nelle righe del corpo.
  const at = async (time, text) => {
    // DOC L0098: Esegue il passaggio esplicito «await page.evaluate(value => { window.__timerClock = value; }, time);» nel flusso: Prova browser automatizzata.
    await page.evaluate(value => { window.__timerClock = value; }, time);
    // DOC L0099: Esegue il passaggio esplicito «await page.waitForFunction(value => document.querySelector('#timer-digits').textContent === value, text);» nel flusso: Prova browser automatizzata.
    await page.waitForFunction(value => document.querySelector('#timer-digits').textContent === value, text);
  // DOC L0100: Chiude il costrutto aperto alla riga 97.
  };
  // DOC L0101: Esegue il passaggio esplicito «await at(17000, '0:03'); await at(18000, '0:02'); await at(19000, '0:01'); await at(20000, '1:00');» nel flusso: Prova browser automatizzata.
  await at(17000, '0:03'); await at(18000, '0:02'); await at(19000, '0:01'); await at(20000, '1:00');
  // DOC L0102: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.deepEqual(await page.evaluate(() => __hornSamples), ['rest-3', 'rest-2', 'rest-1', 'whistle']);
  // DOC L0103: Esegue il passaggio esplicito «await at(80000, '1:00'); await page.getByRole('heading', { name: 'Pausa', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await at(80000, '1:00'); await page.getByRole('heading', { name: 'Pausa', exact: true }).waitFor();
  // DOC L0104: Esegue il passaggio esplicito «await at(200000, '4:00'); await page.getByRole('heading', { name: 'Pausa', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await at(200000, '4:00'); await page.getByRole('heading', { name: 'Pausa', exact: true }).waitFor();
  // DOC L0105: Esegue il passaggio esplicito «await at(440000, '3:00'); await page.getByRole('heading', { name: 'Concentrazione', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await at(440000, '3:00'); await page.getByRole('heading', { name: 'Concentrazione', exact: true }).waitFor();
  // DOC L0106: Esegue il passaggio esplicito «await at(620000, '0:00'); await page.getByRole('heading', { name: 'Studio completato.', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await at(620000, '0:00'); await page.getByRole('heading', { name: 'Studio completato.', exact: true }).waitFor();
  // DOC L0107: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
  assert.equal(await page.getByRole('switch').isDisabled(), false);
  // DOC L0108: Esegue il passaggio esplicito «checks.push('studio-preparation-audio-completion');» nel flusso: Prova browser automatizzata.
  checks.push('studio-preparation-audio-completion');
  // DOC L0109: Legge il valore locale e applica i controlli/fallback riportati nell’espressione. archive: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const archive = await page.evaluate(() => JSON.parse(localStorage.getItem('tabata:v1:data')));
  // DOC L0110: studio: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const studio = archive.sessions.filter(s => s.mode === 'STUDIO');
  // DOC L0111: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(studio.length, 1); assert.equal(studio[0].workMs, 300000); assert.equal(studio[0].elapsedMs, 600000);
  // DOC L0112: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Vedi attività', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Vedi attività', exact: true }).click();
  // DOC L0113: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Attività di studio', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Attività di studio', exact: true }).waitFor();
  // DOC L0114: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Statistiche →', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Statistiche →', exact: true }).click();
  // DOC L0115: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Statistiche di studio', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Statistiche di studio', exact: true }).waitFor();
  // DOC L0116: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.match(await page.locator('.metrics').textContent(), /CONCENTRAZIONE/);
  // DOC L0117: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
  await page.getByRole('switch').click();
  // DOC L0118: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.getByRole('spinbutton', { name: 'Lavoro', exact: true }).inputValue(), '7');
  // DOC L0119: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Allenamenti', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Allenamenti', exact: true }).click();
  // DOC L0120: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Circuito locale', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Circuito locale', exact: true }).waitFor();
  // DOC L0121: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.getByRole('heading', { name: 'Matematica', exact: true }).count(), 0);
  // DOC L0122: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Attività', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Attività', exact: true }).click();
  // DOC L0123: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.locator('.session-card').count(), 0);
  // DOC L0124: Esegue il passaggio esplicito «checks.push('archives-activities-stats-separated');» nel flusso: Prova browser automatizzata.
  checks.push('archives-activities-stats-separated');
  // DOC L0125: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
  await page.getByRole('switch').click();
  // DOC L0126: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();
  // DOC L0127: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Scuro', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Scuro', exact: true }).click();
  // DOC L0128: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Blu', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Blu', exact: true }).click();
  // DOC L0129: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
  await page.getByRole('switch').click();
  // DOC L0130: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.locator('html').getAttribute('data-theme'), 'dark');
  // DOC L0131: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
  await page.getByRole('switch').click();
  // DOC L0132: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Guida', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Guida', exact: true }).click();
  // DOC L0133: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.deepEqual(await page.locator('main h2').allTextContents(), ['Workout e Studio', 'Per conservare un allenamento', 'Tema e Colori']);
  // DOC L0134: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.deepEqual(await page.locator('main section.card').first().locator('p').allTextContents(), [
    // DOC L0135: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
    'Usa lo switch accanto a Tabata Timer: Workout ha tempi in secondi, Studio in minuti. L’ultima modalità utilizzata viene ricordata.',
    // DOC L0136: Esegue il passaggio esplicito «'Programmi, attività e statistiche sono separati; tema e palette sono comuni.',» nel flusso: Prova browser automatizzata.
    'Programmi, attività e statistiche sono separati; tema e palette sono comuni.',
    // DOC L0137: Esegue il passaggio esplicito «'In Altre opzioni puoi impostare pause e serie personalizzate a tuo piacimento ed ad ogni tua necessità.',» nel flusso: Prova browser automatizzata.
    'In Altre opzioni puoi impostare pause e serie personalizzate a tuo piacimento ed ad ogni tua necessità.',
    // DOC L0138: Esegue il passaggio esplicito «'Termina il timer per cambiare modalità.'» nel flusso: Prova browser automatizzata.
    'Termina il timer per cambiare modalità.'
  // DOC L0139: Chiude un blocco, una chiamata o un valore composto; non aggiunge una nuova operazione.
  ]);
  // DOC L0140: Esegue il passaggio esplicito «checks.push('exact-guide-three-cards-four-paragraphs');» nel flusso: Prova browser automatizzata.
  checks.push('exact-guide-three-cards-four-paragraphs');
  // Keep the current archive while checking an old one in a separate browser context.
  // DOC L0142: legacyContext: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const legacyContext = await browser.newContext({ locale: 'it-IT' });
  // DOC L0143: legacyPage: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const legacyPage = await legacyContext.newPage();
  // DOC L0144: legacy: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const legacy = await readFile('fixtures/backup-modes-v1.json', 'utf8');
  // DOC L0145: Scrive la preferenza o la copia locale prevista, senza inviarla a un server.
  await legacyPage.addInitScript(raw => { localStorage.setItem('tabata:v1:data', raw);  }, legacy);
  // DOC L0146: Esegue il passaggio esplicito «await legacyPage.goto(base + '/?mode=studio');» nel flusso: Prova browser automatizzata.
  await legacyPage.goto(base + '/?mode=studio');
  // DOC L0147: Esegue il passaggio esplicito «await legacyPage.getByRole('button', { name: 'Programmi di studio', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await legacyPage.getByRole('button', { name: 'Programmi di studio', exact: true }).click();
  // DOC L0148: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.
  assert.match(await legacyPage.locator('main').innerText(), /Programma precedente/);
  // DOC L0149: Legge il valore locale e applica i controlli/fallback riportati nell’espressione. before: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const before = await legacyPage.evaluate(() => localStorage.getItem('tabata:v1:data'));
  // DOC L0150: Esegue il passaggio esplicito «await legacyPage.locator('.workout-menu summary').click(); await legacyPage.getByRole('button', { name: 'Modifica', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await legacyPage.locator('.workout-menu summary').click(); await legacyPage.getByRole('button', { name: 'Modifica', exact: true }).click();
  // DOC L0151: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await legacyPage.locator('[data-path="activities.0.sessions"]').inputValue(), '8');
  // DOC L0152: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Legge il valore locale e applica i controlli/fallback riportati nell’espressione.
  assert.equal(await legacyPage.evaluate(() => localStorage.getItem('tabata:v1:data')), before);
  // DOC L0153: Esegue il passaggio esplicito «await legacyPage.getByRole('button', { name: 'Salva sul dispositivo', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await legacyPage.getByRole('button', { name: 'Salva sul dispositivo', exact: true }).click();
  // DOC L0154: Legge il valore locale e applica i controlli/fallback riportati nell’espressione. after: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const after = await legacyPage.evaluate(() => JSON.parse(localStorage.getItem('tabata:v1:data')));
  // DOC L0155: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(after.workouts[0].activities.length, 1); assert.equal(after.workouts[0].longBreakSec, undefined);
  // DOC L0156: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.deepEqual(after.sessions, JSON.parse(legacy).sessions);
  // DOC L0157: Esegue il passaggio esplicito «await legacyContext.close();» nel flusso: Prova browser automatizzata.
  await legacyContext.close();
  // DOC L0158: Esegue il passaggio esplicito «checks.push('legacy-not-overwritten-until-save-history-preserved');» nel flusso: Prova browser automatizzata.
  checks.push('legacy-not-overwritten-until-save-history-preserved');
  // DOC L0159: Esegue il passaggio esplicito «await page.goto(base); await page.getByRole('heading', { name: 'Tabata Timer', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.goto(base); await page.getByRole('heading', { name: 'Tabata Timer', exact: true }).waitFor();
  // DOC L0160: Esegue il passaggio esplicito «await page.evaluate(async () => { await navigator.serviceWorker.ready; });» nel flusso: Prova browser automatizzata.
  await page.evaluate(async () => { await navigator.serviceWorker.ready; });
  // DOC L0161: Esegue il passaggio esplicito «await page.reload(); await context.setOffline(true); await page.reload();» nel flusso: Prova browser automatizzata.
  await page.reload(); await context.setOffline(true); await page.reload();
  // DOC L0162: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Tabata Timer', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Tabata Timer', exact: true }).waitFor();
  // DOC L0163: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Programmi di studio', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Programmi di studio', exact: true }).click();
  // DOC L0164: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Matematica', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Matematica', exact: true }).waitFor();
  // DOC L0165: Esegue il passaggio esplicito «checks.push('mode-preferences-and-offline');» nel flusso: Prova browser automatizzata.
  checks.push('mode-preferences-and-offline');
  // DOC L0166: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.deepEqual(errors, []);
  // DOC L0167: Esegue il passaggio esplicito «await writeFile('output/browser-modes.json', JSON.stringify({ checks, offline: true, studioSession: studio[0], errors }, null, 2));» nel flusso: Prova browser automatizzata.
  await writeFile('output/browser-modes.json', JSON.stringify({ checks, offline: true, studioSession: studio[0], errors }, null, 2));
  // DOC L0168: Esegue il passaggio esplicito «console.log(`Workout/Studio: ${checks.length} gruppi di controlli passati.`);» nel flusso: Prova browser automatizzata.
  console.log(`Workout/Studio: ${checks.length} gruppi di controlli passati.`);
// DOC L0169: Esegue il passaggio esplicito «} finally { await browser.close(); }» nel flusso: Prova browser automatizzata.
} finally { await browser.close(); }
