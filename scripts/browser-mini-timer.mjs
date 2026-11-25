import assert from 'node:assert/strict';
import { pathToFileURL } from 'node:url';

const { chromium } = await import(process.env.PLAYWRIGHT_MODULE ? pathToFileURL(process.env.PLAYWRIGHT_MODULE).href : 'playwright');
const browser = await chromium.launch({ channel: process.env.TABATA_BROWSER || 'msedge' });
const base = process.env.TABATA_URL || 'http://127.0.0.1:5500/';
const context = await browser.newContext({ locale: 'it-IT', viewport: { width: 1050, height: 850 } });
const errors = [];
context.on('page', page => page.on('pageerror', error => errors.push(error.message)));

try {
  await context.addInitScript(() => {
    window.testTimerClock = 1000;
    performance.now = () => window.testTimerClock;
    window.testCues = [];
    const startSource = AudioBufferSourceNode.prototype.start;
    AudioBufferSourceNode.prototype.start = function (...args) {
      if (this.buffer) window.testCues.push(Math.round(this.buffer.duration * 1000));
      return startSource.apply(this, args);
    };
  });
  const page = await context.newPage();
  page.on('dialog', dialog => dialog.accept());
  await page.goto(base);
  await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();
  await page.getByRole('button', { name: 'Salva cronologia', exact: true }).click();
  await page.getByRole('button', { name: 'Timer', exact: true }).click();
  const popupOpened = page.waitForEvent('popup');
  await page.getByRole('button', { name: 'Apri mini timer', exact: true }).click();
  const popup = await popupOpened;
  await popup.locator('.mini-time').waitFor();
  assert.equal(await popup.locator('.mini-time').textContent(), '0:20');
  await popup.setViewportSize({ width: 320, height: 240 });
  await popup.waitForFunction(() => {
    const rect = selector => document.querySelector(selector).getBoundingClientRect();
    const middle = box => box.left + box.width / 2;
    const letter = (selector, index) => {
      const node = document.querySelector(selector).firstChild, range = document.createRange();
      range.setStart(node, index);
      range.setEnd(node, index + 1);
      return range.getBoundingClientRect();
    };
    const close = (a, b) => Math.abs(a - b) < 1;
    return document.documentElement.scrollWidth === innerWidth &&
      close(rect('.mini-mode').right, rect('.mini-phase').right) &&
      close(middle(rect('.mini-colon')), middle(rect('.mini-phase'))) &&
      close(middle(rect('[data-action="less"]')), middle(letter('[data-kind="focus"]', 0))) &&
      close(middle(rect('[data-action="more"]')), middle(letter('[data-kind="break"]', 4))) &&
      close(middle(rect('#mini-rounds')), middle(rect('.mini-play')));
  });
  await popup.setViewportSize({ width: 370, height: 252 });
  const layout = await popup.evaluate(() => {
    const card = document.querySelector('.mini').getBoundingClientRect();
    const phase = document.querySelector('.mini-phase').getBoundingClientRect();
    const mode = document.querySelector('.mini-mode').getBoundingClientRect();
    return { cardRatio: card.width / card.height, phaseWidth: phase.width / card.width, phaseRatio: phase.width / phase.height, emphasis: phase.height / mode.height };
  });
  assert.ok(layout.cardRatio >= 2.2 && layout.cardRatio <= 2.4, JSON.stringify(layout));
  assert.ok(layout.phaseWidth >= 0.63 && layout.phaseWidth <= 0.68, JSON.stringify(layout));
  assert.ok(layout.phaseRatio >= 5.1 && layout.phaseRatio <= 5.8, JSON.stringify(layout));
  assert.ok(layout.emphasis >= 1.4, JSON.stringify(layout));
  if (process.env.TABATA_MINI_SCREENSHOT) await popup.screenshot({ path: process.env.TABATA_MINI_SCREENSHOT });
  const mainStart = () => page.getByRole('button', { name: 'Avvia', exact: true }).click();
  const miniStart = () => popup.getByRole('button', { name: 'Avvia', exact: true }).click();
  const advance = ms => popup.evaluate(delta => { window.testTimerClock += delta; }, ms);

  await mainStart();
  await page.getByRole('heading', { name: 'Preparazione', exact: true }).waitFor();
  await miniStart();
  await popup.getByText('Timer aperto in un’altra finestra.', { exact: true }).waitFor();
  await page.getByRole('button', { name: 'Termina', exact: true }).click();
  await page.getByRole('heading', { name: 'Sessione interrotta.', exact: true }).waitFor();
  await page.waitForFunction(async () => !(await navigator.locks.query()).held.some(lock => lock.name === 'tabata:running-timer'));

  await popup.getByRole('button', { name: 'Studio', exact: true }).click();
  const center = await popup.locator('.mini-colon').evaluate(node => node.getBoundingClientRect().x);
  await popup.getByRole('button', { name: 'Aumenta durata', exact: true }).click();
  assert.equal(await popup.locator('.mini-time').textContent(), '26:00');
  await popup.getByRole('button', { name: 'Workout', exact: true }).click();
  assert.equal(await popup.locator('.mini-time').textContent(), '0:20');
  assert.ok(Math.abs(await popup.locator('.mini-colon').evaluate(node => node.getBoundingClientRect().x) - center) < 0.6);
  const counts = popup.locator('#mini-rounds');
  await popup.locator('.mini-counter').evaluate(node => {
    for (let i = 0; i < 25; i++) node.querySelector('[data-action="round-more"]').click();
  });
  assert.equal(await counts.textContent(), '20');
  await popup.locator('.mini-counter').evaluate(node => {
    for (let i = 0; i < 25; i++) node.querySelector('[data-action="round-less"]').click();
  });
  assert.equal(await counts.textContent(), '1');
  await popup.locator('.mini-readout').evaluate(node => {
    for (let i = 0; i < 25; i++) node.querySelector('[data-action="less"]').click();
  });
  assert.equal(await popup.locator('.mini-time').textContent(), '0:05');

  await page.getByRole('button', { name: 'Nuovo timer', exact: true }).click();
  await miniStart();
  await popup.getByText('Preparazione', { exact: true }).waitFor();
  await mainStart();
  await page.getByText('Timer aperto in un’altra finestra.', { exact: true }).waitFor();
  await page.close();
  await advance(20000);
  await popup.getByText('Lavoro', { exact: true }).waitFor();
  await advance(5000);
  await popup.getByText('Sessione completata', { exact: true }).waitFor();
  await popup.waitForFunction(() => JSON.parse(localStorage.getItem('tabata:v1:data')).sessions.length === 2);
  assert.deepEqual(await popup.evaluate(() => window.testCues), [1050, 1920]);

  await popup.getByRole('button', { name: 'Break', exact: true }).click();
  await popup.locator('.mini-readout').evaluate(node => {
    for (let i = 0; i < 25; i++) node.querySelector('[data-action="less"]').click();
  });
  await miniStart();
  await popup.getByText('Recupero', { exact: true }).waitFor();
  await popup.getByRole('button', { name: 'Pausa', exact: true }).click();
  await popup.getByRole('button', { name: 'Termina', exact: true }).waitFor();
  await popup.getByRole('button', { name: 'Riprendi', exact: true }).click();
  await advance(5000);
  await popup.getByText('Sessione completata', { exact: true }).waitFor();
  assert.equal(await popup.evaluate(() => JSON.parse(localStorage.getItem('tabata:v1:data')).sessions.length), 2);
  assert.deepEqual(await popup.evaluate(() => window.testCues), [1050, 1920, 500, 1920]);

  await popup.evaluate(() => navigator.serviceWorker.ready.then(() => true));
  await popup.reload();
  await popup.waitForFunction(() => !!navigator.serviceWorker.controller);
  await context.setOffline(true);
  await popup.reload();
  assert.equal(await popup.locator('.mini-time').textContent(), '0:05');
  assert.equal(await popup.locator('#mini-rounds').textContent(), '1');
  assert.deepEqual(errors, []);
  console.log('Mini-finestra indipendente, proprietà esclusiva, limiti, segnali audio, pausa singola, cronologia e offline verificati.');
} finally {
  await browser.close();
}
