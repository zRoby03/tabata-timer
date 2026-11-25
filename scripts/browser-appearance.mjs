import { pathToFileURL } from 'node:url';
import { mkdir, writeFile } from 'node:fs/promises';
import assert from 'node:assert/strict';
import { PALETTES, STYLES, APPEARANCE_KEY } from '../web/js/appearance.js';
const { chromium } = await import(process.env.PLAYWRIGHT_MODULE ? pathToFileURL(process.env.PLAYWRIGHT_MODULE).href : 'playwright');
const browser = await chromium.launch({ channel: process.env.TABATA_BROWSER || 'msedge', headless: true });
const checks = [], errors = [];
const base = process.env.TABATA_URL || 'http://127.0.0.1:5500';
const output = process.env.TABATA_QA_DIR || 'output';
const screenshots = output + '/screenshots/appearance';
for (const theme of ['light', 'dark']) await mkdir(screenshots + '/' + theme, { recursive: true });
try {
  const context = await browser.newContext({ locale: 'it-IT', viewport: { width: 390, height: 900 } });
  const page = await context.newPage(); page.on('pageerror', e => errors.push(e.message));
  await page.goto(base); await page.getByRole('heading', { name: 'Tabata Timer' }).waitFor();
  assert.equal(await page.evaluate(() => localStorage.length), 0);
  for (const [i, seconds] of [20, 30, 40].entries()) {
    await page.getByRole('button', { name: `Tabata ${i + 1} ${seconds} / ${seconds / 2}`, exact: true }).click();
    assert.equal(await page.locator('[data-quick-path="blocks.0.exercises.0.workSec"]').inputValue(), String(seconds));
  }
  checks.push('Tabata 1, 2 e 3 mantengono i tempi 20/10, 30/15 e 40/20');
  await page.getByRole('button', { name: 'Tabata 1 20 / 10', exact: true }).click();
  for (const style of STYLES) {
    for (const theme of ['light', 'dark']) for (const p of PALETTES) {
      await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();
      assert.deepEqual(await page.locator('[data-action=style]').allTextContents(), STYLES.map(s => s.name));
      await page.getByRole('button', { name: style.name, exact: true }).press('Enter');
      assert.equal(await page.locator(':focus-visible').getAttribute('data-value'), style.id);
      await page.getByRole('button', { name: theme === 'dark' ? 'Scuro' : 'Chiaro', exact: true }).click();
      await page.getByRole('button', { name: p.name, exact: true }).click();
      const preferences = { theme, palette: p.id, style: style.id };
      assert.deepEqual(await page.evaluate(key => JSON.parse(localStorage.getItem(key)), APPEARANCE_KEY), preferences);
      assert.equal(await page.evaluate(() => localStorage.getItem('tabata:v1:data')), null);
      await page.locator('nav [data-route=home]').click();
      assert.deepEqual(await page.evaluate(() => ({ ...document.documentElement.dataset })), preferences);
      const appearance = await page.evaluate(() => {
        const ctx = document.createElement('canvas').getContext('2d', {willReadFrequently: true});
        function pixel(cssColor) {
          ctx.clearRect(0, 0, 1, 1);
          ctx.fillStyle = cssColor;
          ctx.fillRect(0, 0, 1, 1);
          return ctx.getImageData(0, 0, 1, 1).data;
        }
        function luminance(rgb) {
          const [red, green, blue] = rgb;
          const levels = [red, green, blue].map(channel => {
            const value = channel / 255;
            return value <= 0.04045 ? value / 12.92 : ((value + 0.055) / 1.055) ** 2.4;
          });
          return levels[0] * 0.2126 + levels[1] * 0.7152 + levels[2] * 0.0722;
        }
        function contrast(selector, surface = selector) {
          const fg = pixel(getComputedStyle(document.querySelector(selector)).color);
          let background = document.querySelector(surface);
          let bg = pixel(getComputedStyle(background).backgroundColor);
          while (bg[3] === 0 && background.parentElement) {
            background = background.parentElement;
            bg = pixel(getComputedStyle(background).backgroundColor);
          }
          const [low, high] = [luminance(fg), luminance(bg)].sort((a, b) => a - b);
          return (high + .05) / (low + .05);
        }
        const stage = document.querySelector('.quick-total');
        const stageBackground = pixel(getComputedStyle(stage).backgroundColor)[3] ? '.quick-total' : '.quick-card';
        const start = getComputedStyle(document.querySelector('#quick-start-button'));
        return {
          fill: start.backgroundColor, text: start.color,
          action: contrast('#quick-start-button'),
          timer: contrast('#quick-total', stageBackground),
          stageLabel: contrast('.quick-total > span:first-child', stageBackground),
          input: contrast('.quick-value input'),
          inputLabel: contrast('.quick-field > span:first-child', '.quick-field')
        };
      });
      const rgb = hex => 'rgb(' + hex.slice(1).match(/../g).map(x => parseInt(x, 16)).join(', ') + ')';
      assert.equal(appearance.fill, rgb(p[theme][1]));
      assert.equal(appearance.text, rgb(p[theme][2]));
      for (const key of ['action', 'timer', 'stageLabel', 'input', 'inputLabel']) {
        assert.ok(appearance[key] >= 4.5, [style.id, p.id, theme, key, appearance[key]].join(' '));
      }
      assert.ok(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth));
      if (['purple', 'lime', 'yellow'].includes(p.id)) {
        await page.screenshot({ path: screenshots + '/' + theme + '/' + style.id + '-' + p.id + '.png', fullPage: true });
      }
    }
  }
  checks.push('90 combinazioni: stile, tema e palette effettivi, focus da tastiera, contrasti e archivio separato');
  await page.reload();
  await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();
  assert.equal(await page.getByRole('button', { name: 'Scuro', exact: true }).getAttribute('aria-pressed'), 'true');
  assert.equal(await page.getByRole('button', { name: 'Rosa', exact: true }).getAttribute('aria-pressed'), 'true');
  assert.equal(await page.getByRole('button', { name: 'Bauhaus contemporaneo', exact: true }).getAttribute('aria-pressed'), 'true');
  await page.getByRole('button', { name: 'Retro digitale', exact: true }).click();
  await page.locator('#mode-switch').click();
  await page.setViewportSize({ width: 320, height: 800 });
  for (const style of STYLES) {
    await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();
    await page.getByRole('button', { name: style.name, exact: true }).click();
    await page.locator('nav [data-route=home]').click();
    assert.equal(await page.locator('#quick-total').innerText(), '1:55:00');
    await page.evaluate(() => document.fonts.ready);
    const fit = await page.locator('#quick-total').evaluate(node => {
      const range = document.createRange();
      range.selectNodeContents(node);
      const display = node.parentElement;
      const bounds = display.getBoundingClientRect(), text = range.getBoundingClientRect();
      const heading = display.firstElementChild.getBoundingClientRect();
      const hint = display.lastElementChild.getBoundingClientRect();
      const computed = getComputedStyle(display), motif = getComputedStyle(display, '::after');
      return text.x >= bounds.x && text.right <= bounds.right - (parseFloat(computed.paddingRight) || 0)
        && text.top >= heading.bottom - 1 && text.bottom <= hint.top + 1
        && (document.documentElement.dataset.style !== 'bauhaus'
          || computed.textAlign === 'start' && parseFloat(motif.width) >= bounds.width * .20
            && text.right < bounds.right - parseFloat(motif.width));
    });
    assert.ok(fit, style.id + ' timer lungo senza sovrapposizioni a 320 px');
  }
  await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();
  await page.getByRole('button', { name: 'Retro digitale', exact: true }).click();
  await page.locator('nav [data-route=home]').click();
  await page.locator('[data-quick-path="activities.0.concentrationSec"]').fill('60');
  await page.getByRole('button', { name: 'Avvia', exact: true }).click();
  await page.getByRole('button', { name: 'Salta →', exact: true }).click();
  assert.match(await page.locator('#timer-digits').innerText(), /^1:00:00$|^59:5[0-9]$/);
  await page.getByRole('button', { name: 'Pausa', exact: true }).click();
  const remaining = await page.locator('#timer-digits').innerText();
  await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();
  await page.getByRole('button', { name: 'Neo-brutalismo soft', exact: true }).click();
  await page.locator('nav [data-route=home]').click();
  assert.equal(await page.locator('#timer-digits').innerText(), remaining);
  await page.getByRole('button', { name: 'Riprendi', exact: true }).waitFor();
  page.once('dialog', dialog => dialog.accept());
  await page.getByRole('button', { name: 'Termina', exact: true }).click();
  await page.locator('#mode-switch').click();
  assert.equal(await page.evaluate(() => document.documentElement.dataset.style), 'neo-brutalism');
  checks.push('Timer Studio lungo a 320 px, pausa conservata dopo cambio stile, aspetto comune alle modalità');
  await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();
  await page.setViewportSize({ width: 320, height: 800 });
  assert.equal(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth), true);
  await page.screenshot({ path: screenshots + '/web-dark-settings-320.png', fullPage: true });
  await page.getByRole('button', { name: 'Allenamenti', exact: true }).click();
  await page.getByRole('button', { name: '+ Crea allenamento', exact: true }).click();
  assert.equal(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth), true);
  await page.screenshot({ path: screenshots + '/web-dark-editor-320.png', fullPage: true });
  await page.getByRole('button', { name: 'Torna agli allenamenti' }).click();
  await page.getByRole('button', { name: 'Attività', exact: true }).click();
  await page.getByRole('button', { name: 'Statistiche →', exact: true }).click();
  assert.equal(await page.locator('svg rect').first().evaluate(r => getComputedStyle(r).fill), 'rgb(242, 154, 197)');
  await page.getByRole('button', { name: 'Guida', exact: true }).click();
  assert.equal(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth), true);
  checks.push('Preferenze conservate al reload; impostazioni, editor, grafici e guida compatibili con tema scuro a 320 px');
  await page.evaluate(async () => { await navigator.serviceWorker.ready; });
  await page.reload(); await context.setOffline(true); await page.reload();
  await page.getByRole('heading', { name: 'Tabata Timer' }).waitFor();
  assert.equal(await page.evaluate(() => document.documentElement.dataset.theme), 'dark');
  await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();
  await page.getByRole('button', { name: 'Verde chiaro', exact: true }).click();
  assert.equal(await page.evaluate(() => document.documentElement.dataset.palette), 'lime');
  checks.push('Aspetto disponibile e modificabile anche offline');
  await context.close();
  const denied = await browser.newContext({ locale: 'it-IT' });
  await denied.addInitScript(key => { const set = Storage.prototype.setItem; Storage.prototype.setItem = function (k, v) { if (k === key) throw new Error('denied'); return set.call(this, k, v); }; }, APPEARANCE_KEY);
  const blocked = await denied.newPage(); await blocked.goto(base);
  await blocked.getByRole('button', { name: 'Impostazioni', exact: true }).click();
  await blocked.getByRole('button', { name: 'Scuro', exact: true }).click();
  assert.equal(await blocked.evaluate(() => document.documentElement.dataset.theme), 'dark');
  assert.match(await blocked.locator('#notice').textContent(), /non ha potuto salvare/);
  assert.equal(await blocked.evaluate(() => localStorage.length), 0);
  await denied.close();
  checks.push('Storage negato: tema applicato alla visita e avviso, senza falso salvataggio');
  assert.deepEqual(errors, []);
  await writeFile(output + '/browser-appearance.json', JSON.stringify({ checks, errors }, null, 2));
  console.log(checks.join('\n'));
} finally { await browser.close(); }
