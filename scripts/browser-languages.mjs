import { pathToFileURL } from 'node:url';
import { mkdir, writeFile } from 'node:fs/promises';
import assert from 'node:assert/strict';
import { translate } from '../web/js/i18n.js';
const { chromium } = await import(process.env.PLAYWRIGHT_MODULE ? pathToFileURL(process.env.PLAYWRIGHT_MODULE).href : 'playwright');
const browser = await chromium.launch({ channel: process.env.TABATA_BROWSER || 'msedge', headless: true });
const base = process.env.TABATA_URL || 'http://127.0.0.1:5500', checks = [], errors = [];
await mkdir('output/screenshots', {recursive:true});
try {
 for (const lang of ['it','en','es']) {
  const context = await browser.newContext({locale: {it:'it-IT',en:'en-US',es:'es-MX'}[lang], reducedMotion:'reduce'});
  const page = await context.newPage(); page.on('pageerror',e=>errors.push(e.message));page.on('dialog',d=>d.accept());
  const tr=(source,...args)=>translate(source,lang,...args);
  await page.goto(base);await page.getByRole('heading',{name:'Tabata Timer',exact:true}).waitFor();
  assert.equal(await page.locator('html').getAttribute('lang'),lang);
  assert.equal(await page.locator('.brand b').innerText(), 'Timer');
  assert.equal(await page.locator('footer, .quick-privacy, [data-action=demo]').count(),0);
  assert.equal(await page.locator('#quick-total').innerText(),'3:50');
  await page.getByText(tr('Altre opzioni'),{exact:true}).click();
  assert.equal(await page.locator('[data-action=demo]').count(),0);
  await page.getByRole('button',{name:tr('Salva allenamento'),exact:true}).click();
  const custom='Lavoro · Mi sesión <uno>';
  await page.locator('#save-name').fill(custom);await page.getByRole('button',{name:tr('Salva sul dispositivo'),exact:true}).click();
  await page.getByRole('heading',{name:custom,exact:true}).waitFor();
  const saved=await page.evaluate(()=>JSON.parse(localStorage.getItem('tabata:v1:data')));
  await page.locator('[data-action=settings]').first().click();
  await page.locator('[data-action=language][data-value=en]').click();
  await page.getByRole('heading',{name:'Settings',exact:true}).waitFor();
  await page.reload();await page.getByRole('heading',{name:'Tabata Timer',exact:true}).waitFor();
  assert.equal(await page.locator('html').getAttribute('lang'),'en');
  assert.deepEqual((await page.evaluate(()=>JSON.parse(localStorage.getItem('tabata:v1:data')))).workouts,saved.workouts);
  await page.locator('[data-action=settings]').first().click();await page.locator(`[data-action=language][data-value=${lang}]`).click();
  assert.deepEqual(await page.locator('[data-action=palette]').allTextContents(), ['Viola','Blu','Verde chiaro','Verde scuro','Azzurro','Arancione','Giallo','Rosso','Rosa'].map(x=>tr(x)));
  await page.locator('[data-action=help]').first().click();
  assert.deepEqual(await page.locator('main h2').allTextContents(),['Workout e Studio','Per conservare un allenamento','Tema e Colori'].map(x=>tr(x)));
  await page.locator('#mode-switch').click();await page.getByRole('heading',{name:'Tabata Timer',exact:true}).waitFor();
  assert.equal(await page.locator('[data-action=demo]').count(),0);
  assert.equal(await page.locator('[data-quick-path="activities.0.concentrationSec"]').inputValue(),'25');
  await page.locator('[data-quick-path="activities.0.title"]').fill(custom);
  await page.locator('[data-action=activity-add]').click();await page.locator('[data-action=activity-delete][data-index="1"]').click();
  assert.equal(await page.locator('.study-activity').count(),1);
  await page.getByRole('button',{name:tr('Salva {0}',tr('programma di studio')),exact:true}).click();
  await page.locator('#save-name').fill('Matemática · My study');await page.getByRole('button',{name:tr('Salva sul dispositivo'),exact:true}).click();
  await page.locator('.workout-menu summary').click();await page.getByRole('button',{name:tr('Modifica'),exact:true}).click();
  assert.ok((await page.locator('#preview').innerText()).includes(tr('Pausa {0} — tra sessioni',custom)));
  assert.ok((await page.locator('#preview').innerText()).includes(custom));
  for (const width of [320,390,768,1440]) {
   await page.setViewportSize({width,height:900});assert.ok(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth),`${lang} ${width}`);
  }
  await page.setViewportSize({width:390,height:900});await page.screenshot({path:`output/screenshots/${lang}-studio-editor-v180.png`,fullPage:true});
  await page.locator('[data-action=workouts]').first().click();
  await page.getByRole('button',{name:tr('Avvia'),exact:true}).click();
  await page.getByRole('heading',{name:tr('Preparazione'),exact:true}).waitFor();
  const digits=await page.locator('#timer-digits').innerText();assert.match(digits,/^0:(?:1\d|20)$/);
  await page.locator('[data-action=skip]').click();await page.getByRole('heading',{name:tr('Concentrazione'),exact:true}).waitFor();
  assert.ok((await page.locator('#timer-phase').innerText()).includes(custom));
  assert.ok((await page.locator('#timer-next').innerText()).includes(tr('Pausa {0} — tra sessioni',custom)));
  await page.locator('[data-action=stop]').click();await page.getByRole('heading',{name:tr('Sessione interrotta.'),exact:true}).waitFor();
  await page.locator('[data-action=history]').click();await page.locator('[data-action=stats]').click();await page.getByRole('heading',{name:tr('Statistiche di studio'),exact:true}).waitFor();
  assert.ok((await page.locator('main').innerText()).includes('Matemática · My study'));
  await page.evaluate(async()=>{await navigator.serviceWorker.ready});await page.reload();await context.setOffline(true);await page.reload();
  await page.getByRole('heading',{name:'Tabata Timer',exact:true}).waitFor();assert.equal(await page.locator('html').getAttribute('lang'),lang);
  checks.push({language:lang,deviceDetection:true,preferencePersisted:true,savedNamesPreserved:true,editorTimerStatistics:true,responsive:[320,390,768,1440],offline:true,demosFooterAndStorageLineRemoved:true});
  await context.close();
 }
 assert.deepEqual(errors,[]);await writeFile('output/browser-languages.json',JSON.stringify({version:'1.8.0',checks,errors},null,2));
 console.log('Italiano, English, Español: flussi completi, nomi intatti, tre guide, nove palette, quattro larghezze e offline verificati.');
} finally {await browser.close();}
