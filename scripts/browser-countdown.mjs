import { pathToFileURL } from 'node:url';
import { writeFile } from 'node:fs/promises';
import assert from 'node:assert/strict';
const { chromium } = await import(process.env.PLAYWRIGHT_MODULE ? pathToFileURL(process.env.PLAYWRIGHT_MODULE).href : 'playwright');
const browser = await chromium.launch({ channel: process.env.TABATA_BROWSER || 'msedge', headless: true });
try {
  const context = await browser.newContext({ locale: 'it-IT', reducedMotion: 'reduce' });
  await context.addInitScript(() => {
    window.__timerClock = 0; window.__hornRates = []; window.__hornSamples = []; window.__hornDecoded = 0; window.__hornEvents = []; window.__clipNames = new WeakMap(); window.__stopEvents = []; window.__assetInputs = new WeakMap();
    const originalFetch = window.fetch;
    window.fetch = async function (input, ...args) {
      const response = await originalFetch.call(this, input, ...args);
      const file = new URL(typeof input === 'string' ? input : input.url || input.toString(), location.href).pathname.split('/').pop();
      if (file.endsWith('.wav')) {
        const read = response.arrayBuffer.bind(response);
        response.arrayBuffer = async () => { const data = await read(); window.__assetInputs.set(data, file); return data; };
      }
      return response;
    };
    const decode = AudioContext.prototype.decodeAudioData;
    AudioContext.prototype.decodeAudioData = async function (...args) {
      const file = window.__assetInputs.get(args[0]);
      const buffer = await decode.apply(this, args), data = buffer.getChannelData(0);
      const marker = { 'work-start.wav': 'whistle', 'rest-start.wav': 'sigh', 'workout-finish.wav': 'finish' }[file];
      if (marker) { window.__clipNames.set(buffer, marker); window.__hornDecoded++; return buffer; }
      const expected = { 'work-3': 330, 'work-2': 370, 'work-1': 440, 'rest-3': 660, 'rest-2': 740, 'rest-1': 880 };
      const start = Math.round(buffer.sampleRate * .005), end = Math.round(buffer.sampleRate * (buffer.duration - .015));
      const energy = frequency => {
        let real = 0, imaginary = 0;
        for (let i = start; i < end; i++) {
          const angle = 2 * Math.PI * frequency * i / buffer.sampleRate;
          const window = .5 - .5 * Math.cos(2 * Math.PI * (i - start) / (end - start - 1));
          real += data[i] * window * Math.cos(angle); imaginary += data[i] * window * Math.sin(angle);
        }
        return real * real + imaginary * imaginary;
      };
      const key = Object.keys(expected).sort((a, b) => energy(expected[b]) - energy(expected[a]))[0];
      window.__clipNames.set(buffer, key); window.__hornDecoded++; return buffer;
    };
    Object.defineProperty(performance, 'now', { value: () => window.__timerClock });
    const create = AudioContext.prototype.createBufferSource;
    AudioContext.prototype.createBufferSource = function () {
      const source = create.call(this), start = source.start, stop = source.stop, connect = source.connect;
      source.connect = function (target, ...args) { if (target.gain) this.__outputGain = target.gain; return connect.call(this, target, ...args); };
      source.stop = function (...args) { window.__stopEvents.push({ clip: window.__clipNames.get(this.buffer), at: window.__timerClock }); return stop.apply(this, args); };
      source.start = function (...args) { window.__hornRates.push(Number(this.playbackRate.value.toFixed(2))); window.__hornSamples.push(window.__clipNames.get(this.buffer)); window.__hornEvents.push({ at: window.__timerClock, duration: this.buffer.duration, volume: this.__outputGain?.value }); return start.apply(this, args); };
      return source;
    };
  });
  const page = await context.newPage(), errors = [];
  page.on('pageerror', e => errors.push(e.message));
  await page.goto(process.env.TABATA_URL || 'http://127.0.0.1:5500');
  await page.getByRole('heading', { name: 'Tabata Timer', exact: true }).waitFor();
  await page.evaluate(async () => { await navigator.serviceWorker.ready; });
  await page.reload(); await page.getByRole('heading', { name: 'Tabata Timer', exact: true }).waitFor();
  await context.setOffline(true); await page.reload();
  await page.locator('[data-quick-path="blocks.0.exercises.0.workSec"]').fill('4');
  await page.locator('[data-quick-path="blocks.0.exercises.0.restBetweenRepsSec"]').fill('4');
  await page.locator('[data-quick-path="blocks.0.exercises.0.repetitions"]').fill('2');
  await page.getByText('Altre opzioni', { exact: true }).click();
  await page.locator('[data-quick-path="cooldownSec"]').fill('4');
  await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();
  await page.getByRole('button', { name: 'Salva cronologia', exact: true }).click();
  await page.getByRole('button', { name: 'Timer', exact: true }).click();
  await page.getByRole('button', { name: 'Avvia', exact: true }).click();
  await page.getByRole('heading', { name: 'Preparazione', exact: true }).waitFor();
  assert.equal(await page.locator('#timer-digits').textContent(), '0:20');
  await page.waitForFunction(() => window.__hornDecoded === 9);
  const at = async (time, digits, notes) => {
    await page.evaluate(value => { window.__timerClock = value; }, time);
    await page.waitForFunction(expected => document.querySelector('#timer-digits').textContent === expected, digits);
    assert.equal(await page.evaluate(() => __hornRates.length), notes);
  };
  await at(17000, '0:03', 1);
  await page.getByRole('button', { name: 'Pausa', exact: true }).click();
  await at(27000, '0:03', 1);
  await page.getByRole('button', { name: 'Riprendi', exact: true }).click();
  await at(28000, '0:02', 2); await at(29000, '0:01', 3);
  await at(30000, '0:04', 4); await page.getByRole('heading', { name: 'Lavoro', exact: true }).waitFor();
  await at(31000, '0:03', 5); await at(31500, '0:03', 5); await at(32000, '0:02', 6); await at(33000, '0:01', 7);
  await at(34000, '0:04', 8); await page.getByRole('heading', { name: 'Recupero', exact: true }).waitFor();
  await at(35000, '0:03', 9); await at(36000, '0:02', 10); await at(37000, '0:01', 11);
  await at(38000, '0:04', 12); await at(39000, '0:03', 13); await at(40000, '0:02', 14); await at(41000, '0:01', 15);
  await at(42000, '0:04', 16); await page.getByRole('heading', { name: 'Defaticamento', exact: true }).waitFor();
  await at(43000, '0:03', 17); await at(44000, '0:02', 18); await at(45000, '0:01', 19); await at(46000, '0:00', 19);
  const rates = await page.evaluate(() => __hornRates);
  assert.deepEqual(rates, Array(19).fill(1));
  const samples = await page.evaluate(() => __hornSamples);
  assert.deepEqual(samples, ['rest-3', 'rest-2', 'rest-1', 'whistle', 'work-3', 'work-2', 'work-1', 'sigh', 'rest-3', 'rest-2', 'rest-1', 'whistle', 'work-3', 'work-2', 'work-1', 'finish', 'rest-3', 'rest-2', 'rest-1']);
  const events = await page.evaluate(() => __hornEvents);
  assert.deepEqual(events.map(e => e.at), [17000, 28000, 29000, ...Array.from({ length: 16 }, (_, i) => (30 + i) * 1000)]);
  events.forEach((event, i) => assert.ok(Math.abs(event.duration - ({ whistle: .5, sigh: .5, finish: .9 }[samples[i]] ?? .12)) < .001));
  events.forEach((event, i) => assert.ok(Math.abs(event.volume - (samples[i] === 'whistle' ? 1 : samples[i] === 'sigh' ? .85 : .65)) < .00001));
  const session = await page.evaluate(() => JSON.parse(localStorage.getItem('tabata:v1:data')).sessions[0]);
  assert.equal(session.plannedMs, 16000); assert.equal(session.elapsedMs, 16000); assert.equal(session.workMs, 8000); assert.equal(session.pauses, 1);
  // A one-second interval: phase-start whistle has priority over the coincident beep.
  await page.getByRole('button', { name: 'Nuovo timer', exact: true }).click();
  await page.locator('[data-quick-path="blocks.0.exercises.0.workSec"]').fill('1');
  await page.locator('[data-quick-path="blocks.0.exercises.0.restBetweenRepsSec"]').fill('0');
  await page.locator('[data-quick-path="blocks.0.exercises.0.repetitions"]').fill('1');
  await page.getByText('Altre opzioni', { exact: true }).click();
  await page.locator('[data-quick-path="cooldownSec"]').fill('0');
  const beforeShort = await page.evaluate(() => __hornSamples.length);
  await page.getByRole('button', { name: 'Avvia', exact: true }).click();
  await page.getByRole('button', { name: 'Salta →', exact: true }).click();
  assert.deepEqual(await page.evaluate(n => __hornSamples.slice(n), beforeShort), ['whistle']);
  await page.evaluate(() => { window.__timerClock = 47000; });
  await page.getByRole('heading', { name: 'Allenamento completato.', exact: true }).waitFor();
  assert.deepEqual(await page.evaluate(n => __hornSamples.slice(n), beforeShort), ['whistle', 'finish']);
  const stopCount = await page.evaluate(() => __stopEvents.filter(e => e.clip === 'finish').length);
  await page.waitForTimeout(1100);
  assert.equal(await page.evaluate(() => __stopEvents.filter(e => e.clip === 'finish').length), stopCount);
  // Manual interruption must never be announced as completed training.
  await page.getByRole('button', { name: 'Nuovo timer', exact: true }).click();
  const beforeStop = await page.evaluate(() => __hornSamples.length);
  await page.getByRole('button', { name: 'Avvia', exact: true }).click();
  await page.getByRole('button', { name: 'Salta →', exact: true }).click();
  page.once('dialog', d => d.accept());
  await page.getByRole('button', { name: 'Termina', exact: true }).click();
  await page.getByRole('heading', { name: 'Sessione interrotta.', exact: true }).waitFor();
  assert.deepEqual(await page.evaluate(n => __hornSamples.slice(n), beforeStop), ['whistle']);
  assert.deepEqual(errors, []);
  await writeFile('output/browser-countdown.json', JSON.stringify({ offline: true, shortIntervalPriority: true, completionWithoutCooldown: true, finishPlaysToEnd: true, noBellOnManualStop: true, preparationMs: 20000, hornRates: rates, hornSamples: samples, noteEvents: events, elapsedMs: session.elapsedMs, workMs: session.workMs, pauses: session.pauses, errors }, null, 2));
  console.log('Offline: bip 3-2-1 conservati, fischietto e sospiro 500 ms con fischio PCM 120% e sospiro 85%, conto basso nel lavoro e alto nel riposo, tre ding prima del defaticamento; pause senza duplicati e statistiche corrette.');
} finally { await browser.close(); }
