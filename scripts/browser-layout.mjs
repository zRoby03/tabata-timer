import assert from 'node:assert/strict';
import { readFile, writeFile } from 'node:fs/promises';
import { pathToFileURL } from 'node:url';
import { APPEARANCE_KEY, STYLES } from '../web/js/appearance.js';
const { chromium } = await import(process.env.PLAYWRIGHT_MODULE ? pathToFileURL(process.env.PLAYWRIGHT_MODULE).href : 'playwright');
const browser = await chromium.launch({
  channel: process.env.TABATA_BROWSER || 'msedge', headless: true,
  ignoreDefaultArgs: ['--hide-scrollbars'], args: ['--disable-features=OverlayScrollbar']
});
const base = process.env.TABATA_URL || 'http://127.0.0.1:5500';
const fixture = JSON.parse(await readFile('fixtures/backup-simple-v1.json', 'utf8'));
const results = [];
try {
  for (const style of STYLES) for (const populated of [false, true]) {
    const context = await browser.newContext({ locale: 'en-US', viewport: { width: 1280, height: 820 } });
    await context.addInitScript(({key, id}) => localStorage.setItem(key, JSON.stringify({style: id})), {key: APPEARANCE_KEY, id: style.id});
    if (populated) {
      const repeat = items => Array.from({ length: 24 }, (_, i) => items.map(item => ({ ...item, id: item.id + '-' + i }))).flat();
      await context.addInitScript(data => localStorage.setItem('tabata:v1:data', JSON.stringify(data)),
        { ...fixture, workouts: repeat(fixture.workouts), sessions: repeat(fixture.sessions) });
    }
    const page = await context.newPage();
    await page.goto(base);
    await page.keyboard.press('Tab');
    assert.equal(await page.locator(':focus').getAttribute('class'), 'skip-link');
    await page.keyboard.press('Enter');
    assert.equal(await page.locator(':focus').getAttribute('id'), 'main');
    for (const width of [320, 390, 768, 1280]) {
      await page.setViewportSize({ width, height: 820 });
      for (const mode of ['workout', 'studio']) {
        const switcher = page.locator('#mode-switch');
        if ((await switcher.getAttribute('aria-checked') === 'true') !== (mode === 'studio')) await switcher.click();
        const dimensions = [];
        for (const route of ['home', 'workouts', 'history']) {
          await page.locator('nav [data-route=' + route + ']').click();
          const size = await page.evaluate(() => {
            const box = selector => document.querySelector(selector).getBoundingClientRect();
            const { scrollWidth, scrollHeight } = document.documentElement;
            return {
              logo: box('.brand').x, header: box('header').x, nav: box('nav').x,
              main: box('main').x + box('main').width / 2, scrollWidth, scrollHeight
            };
          });
          dimensions.push(size);
          assert.ok(size.scrollWidth <= width, [mode, route, width].join(' '));
          if (style.id === 'classic' && !populated && width === 1280 && mode === 'studio') {
            await page.screenshot({ path: 'output/screenshots/after-' + route + '.png' });
          }
        }
        for (const current of dimensions.slice(1)) {
          for (const key of ['logo', 'header', 'nav', 'main']) {
            assert.equal(current[key], dimensions[0][key], [style.id, mode, width, key].join(' '));
          }
        }
        results.push({ style: style.id, populated, width, mode, dimensions });
      }
    }
    await context.close();
  }
  await writeFile('output/layout-after.json', JSON.stringify(results, null, 2));
  console.log('Layout stable: 240 screens across five styles, both modes, empty and populated archives, 320–1280 px.');
} finally {
  await browser.close();
}
