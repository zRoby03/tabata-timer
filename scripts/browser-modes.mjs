import { pathToFileURL } from 'node:url';
import { readFile, writeFile, mkdir } from 'node:fs/promises';
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
      const marker = { 'work-start.wav': 'whistle', 'rest-start.wav': 'sigh', 'workout-finish.wav': 'finish', 'workout-ding-1.wav': 'ding1', 'workout-ding-2.wav': 'ding2', 'workout-ding-3.wav': 'ding3', 'rest-double.wav': 'double-sigh' }[file];
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
  const page = await context.newPage(), errors = [], checks = [];
  page.on('pageerror', e => errors.push(e.message));
  const base = process.env.TABATA_URL || 'http://127.0.0.1:5500';
  await page.goto(base);
  await page.getByRole('heading', { name: 'Tabata Timer', exact: true }).waitFor();
  await page.getByRole('spinbutton', { name: 'Lavoro', exact: true }).fill('7');
  await page.getByRole('button', { name: 'Salva allenamento', exact: true }).click();
  await page.locator('#save-name').fill('Circuito locale');
  await page.getByRole('button', { name: 'Salva sul dispositivo', exact: true }).click();
  await page.getByRole('switch', { name: 'Modalità Studio' }).click();
  await page.getByRole('heading', { name: 'Tabata Timer', exact: true }).waitFor();
  assert.equal(await page.getByRole('spinbutton', { name: 'Concentrazione · Attività 1', exact: true }).inputValue(), '25');
  await page.getByRole('spinbutton', { name: 'Concentrazione · Attività 1', exact: true }).fill('1');
  await page.getByRole('spinbutton', { name: 'Pausa · Attività 1', exact: true }).fill('1');
  await page.getByRole('spinbutton', { name: 'Sessioni · Attività 1', exact: true }).fill('2');
  await page.getByRole('button', { name: '+ Aggiungi attività', exact: true }).click();
  await page.getByRole('textbox', { name: 'Nome attività · Attività 2', exact: true }).fill('Storia');
  await page.getByRole('spinbutton', { name: 'Concentrazione · Attività 2', exact: true }).fill('3');
  await page.getByRole('spinbutton', { name: 'Sessioni · Attività 2', exact: true }).fill('1');
  await page.getByRole('spinbutton', { name: 'Pausa tra attività', exact: true }).fill('4');
  assert.equal(await page.locator('#quick-total').textContent(), '10:00');
  assert.equal(await page.locator('[data-quick-path]').count(), 9);
  assert.doesNotMatch(await page.locator('main').innerText(), /Pausa lunga|Blocco|Cicli|Serie|Pausa finale|\b(min|sec)\b/);
  checks.push('flat-activities-and-shared-pause-no-final-break');
  await mkdir('output/screenshots', { recursive: true });
  for (const width of [320, 390, 768, 1440]) {
    await page.setViewportSize({ width, height: 900 });
    assert.ok(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth), `overflow at ${width}`);
    await page.screenshot({ path: `output/screenshots/studio-${width}.png`, fullPage: true });
  }
  checks.push('responsive-switch');
  await page.getByRole('button', { name: 'Salva programma di studio', exact: true }).click();
  await page.locator('#save-name').fill('Matematica');
  await page.getByRole('button', { name: 'Salva sul dispositivo', exact: true }).click();
  await page.getByRole('heading', { name: 'Programmi di studio', exact: true }).waitFor();
  assert.equal(await page.getByRole('heading', { name: 'Circuito locale', exact: true }).count(), 0);
  await page.locator('.workout-menu summary').click(); await page.getByRole('button', { name: 'Modifica', exact: true }).click();
  assert.equal(await page.locator('[data-path="activities.0.concentrationSec"]').inputValue(), '1');
  assert.equal(await page.locator('[data-path="restBetweenActivitiesSec"]').inputValue(), '4');
  assert.match(await page.locator('#preview').innerText(), /Pausa .* — tra sessioni/);
  assert.match(await page.locator('#preview').innerText(), /Pausa .* — tra attività/);
  assert.doesNotMatch(await page.locator('#preview').innerText(), /Pausa tra serie|Pausa tra cicli|\b(min|sec)\b/);
  await page.getByRole('button', { name: 'Torna ai programmi', exact: true }).click();
  await page.getByRole('button', { name: 'Avvia', exact: true }).click();
  await page.getByRole('heading', { name: 'Preparazione', exact: true }).waitFor();
  await page.waitForFunction(() => __hornDecoded === 13);
  assert.equal(await page.getByRole('switch').isDisabled(), true);
  const at = async (time, text) => {
    await page.evaluate(value => { window.__timerClock = value; }, time);
    await page.waitForFunction(value => document.querySelector('#timer-digits').textContent === value, text);
  };
  await at(17000, '0:03'); await at(18000, '0:02'); await at(19000, '0:01'); await at(20000, '1:00');
  assert.deepEqual(await page.evaluate(() => __hornSamples), ['rest-3', 'rest-2', 'rest-1', 'whistle']);
  await at(80000, '1:00'); await page.getByRole('heading', { name: 'Pausa', exact: true }).waitFor();
  await at(200000, '4:00'); await page.getByRole('heading', { name: 'Pausa', exact: true }).waitFor();
  await at(440000, '3:00'); await page.getByRole('heading', { name: 'Concentrazione', exact: true }).waitFor();
  await at(620000, '0:00'); await page.getByRole('heading', { name: 'Studio completato.', exact: true }).waitFor();
  assert.equal(await page.getByRole('switch').isDisabled(), false);
  checks.push('studio-preparation-audio-completion');
  const archive = await page.evaluate(() => JSON.parse(localStorage.getItem('tabata:v1:data')));
  const studio = archive.sessions.filter(s => s.mode === 'STUDIO');
  assert.equal(studio.length, 1); assert.equal(studio[0].workMs, 300000); assert.equal(studio[0].elapsedMs, 600000);
  await page.getByRole('button', { name: 'Vedi attività', exact: true }).click();
  await page.getByRole('heading', { name: 'Attività di studio', exact: true }).waitFor();
  await page.getByRole('button', { name: 'Statistiche →', exact: true }).click();
  await page.getByRole('heading', { name: 'Statistiche di studio', exact: true }).waitFor();
  assert.match(await page.locator('.metrics').textContent(), /CONCENTRAZIONE/);
  await page.getByRole('switch').click();
  assert.equal(await page.getByRole('spinbutton', { name: 'Lavoro', exact: true }).inputValue(), '7');
  await page.getByRole('button', { name: 'Allenamenti', exact: true }).click();
  await page.getByRole('heading', { name: 'Circuito locale', exact: true }).waitFor();
  assert.equal(await page.getByRole('heading', { name: 'Matematica', exact: true }).count(), 0);
  await page.getByRole('button', { name: 'Attività', exact: true }).click();
  assert.equal(await page.locator('.session-card').count(), 0);
  checks.push('archives-activities-stats-separated');
  await page.getByRole('switch').click();
  await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();
  await page.getByRole('button', { name: 'Scuro', exact: true }).click();
  await page.getByRole('button', { name: 'Blu', exact: true }).click();
  await page.getByRole('switch').click();
  assert.equal(await page.locator('html').getAttribute('data-theme'), 'dark');
  await page.getByRole('switch').click();
  await page.getByRole('button', { name: 'Guida', exact: true }).click();
  assert.deepEqual(await page.locator('main h2').allTextContents(), ['Workout e Studio', 'Per conservare un allenamento', 'Tema e Colori']);
  const guide=await page.locator('main section.card').first().innerText();
  assert.ok(guide.includes('Un ding: inizio del lavoro ordinario.'));
  assert.ok(guide.includes('Alla sua fine suonano nuovamente gli stessi tre ding.'));
  checks.push('updated-guide-three-cards-audio-mapping');
  // Keep the current archive while checking an old one in a separate browser context.
  const legacyContext = await browser.newContext({ locale: 'it-IT' });
  const legacyPage = await legacyContext.newPage();
  const legacy = await readFile('fixtures/backup-modes-v1.json', 'utf8');
  await legacyPage.addInitScript(raw => { localStorage.setItem('tabata:v1:data', raw);  }, legacy);
  await legacyPage.goto(base + '/?mode=studio');
  await legacyPage.getByRole('button', { name: 'Programmi di studio', exact: true }).click();
  assert.match(await legacyPage.locator('main').innerText(), /Programma precedente/);
  const before = await legacyPage.evaluate(() => localStorage.getItem('tabata:v1:data'));
  await legacyPage.locator('.workout-menu summary').click(); await legacyPage.getByRole('button', { name: 'Modifica', exact: true }).click();
  assert.equal(await legacyPage.locator('[data-path="activities.0.sessions"]').inputValue(), '8');
  assert.equal(await legacyPage.evaluate(() => localStorage.getItem('tabata:v1:data')), before);
  await legacyPage.getByRole('button', { name: 'Salva sul dispositivo', exact: true }).click();
  const after = await legacyPage.evaluate(() => JSON.parse(localStorage.getItem('tabata:v1:data')));
  assert.equal(after.workouts[0].activities.length, 1); assert.equal(after.workouts[0].longBreakSec, undefined);
  assert.deepEqual(after.sessions, JSON.parse(legacy).sessions);
  await legacyContext.close();
  checks.push('legacy-not-overwritten-until-save-history-preserved');
  await page.goto(base); await page.getByRole('heading', { name: 'Tabata Timer', exact: true }).waitFor();
  await page.evaluate(async () => { await navigator.serviceWorker.ready; });
  await page.reload(); await context.setOffline(true); await page.reload();
  await page.getByRole('heading', { name: 'Tabata Timer', exact: true }).waitFor();
  await page.getByRole('button', { name: 'Programmi di studio', exact: true }).click();
  await page.getByRole('heading', { name: 'Matematica', exact: true }).waitFor();
  checks.push('mode-preferences-and-offline');
  assert.deepEqual(errors, []);
  await writeFile('output/browser-modes.json', JSON.stringify({ checks, offline: true, studioSession: studio[0], errors }, null, 2));
  console.log(`Workout/Studio: ${checks.length} gruppi di controlli passati.`);
} finally { await browser.close(); }
