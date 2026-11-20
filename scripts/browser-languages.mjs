// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { pathToFileURL } from 'node:url';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { pathToFileURL } from 'node:url';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import { mkdir, writeFile } from 'node:fs/promises';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { mkdir, writeFile } from 'node:fs/promises';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import assert from 'node:assert/strict';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import assert from 'node:assert/strict';
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import { translate } from '../web/js/i18n.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { translate } from '../web/js/i18n.js';
// DOC L0005: Esegue il passaggio esplicito «const { chromium } = await import(process.env.PLAYWRIGHT_MODULE ? pathToFileURL(process.env.PLAYWRIGHT_MODULE).href : 'playwright');» nel flusso: Prova browser automatizzata.
const { chromium } = await import(process.env.PLAYWRIGHT_MODULE ? pathToFileURL(process.env.PLAYWRIGHT_MODULE).href : 'playwright');
// DOC L0006: browser: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const browser = await chromium.launch({ channel: process.env.TABATA_BROWSER || 'msedge', headless: true });
// DOC L0007: base: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const base = process.env.TABATA_URL || 'http://127.0.0.1:5500', checks = [], errors = [];
// DOC L0008: Esegue il passaggio esplicito «await mkdir('output/screenshots', {recursive:true});» nel flusso: Prova browser automatizzata.
await mkdir('output/screenshots', {recursive:true});
// DOC L0009: Esegue il passaggio esplicito «try {» nel flusso: Prova browser automatizzata.
try {
 // DOC L0010: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
 for (const lang of ['it','en','es']) {
  // DOC L0011: context: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const context = await browser.newContext({locale: {it:'it-IT',en:'en-US',es:'es-MX'}[lang], reducedMotion:'reduce'});
  // DOC L0012: page: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const page = await context.newPage(); page.on('pageerror',e=>errors.push(e.message));page.on('dialog',d=>d.accept());
  // DOC L0013: tr: Delega la traduzione della UI ad AppText preservando gli argomenti dei segnaposto.
  const tr=(source,...args)=>translate(source,lang,...args);
  // DOC L0014: Esegue il passaggio esplicito «await page.goto(base);await page.getByRole('heading',{name:'Tabata Timer',exact:true}).waitFor();» nel flusso: Prova browser automatizzata.
  await page.goto(base);await page.getByRole('heading',{name:'Tabata Timer',exact:true}).waitFor();
  // DOC L0015: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.locator('html').getAttribute('lang'),lang);
  // DOC L0016: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.
  assert.equal(await page.locator('.brand b').innerText(), 'Timer');
  // DOC L0017: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.locator('footer, .quick-privacy, [data-action=demo]').count(),0);
  // DOC L0018: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.
  assert.equal(await page.locator('#quick-total').innerText(),'3:50');
  // DOC L0019: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.
  await page.getByText(tr('Altre opzioni'),{exact:true}).click();
  // DOC L0020: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.locator('[data-action=demo]').count(),0);
  // DOC L0021: Esegue il passaggio esplicito «await page.getByRole('button',{name:tr('Salva allenamento'),exact:true}).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button',{name:tr('Salva allenamento'),exact:true}).click();
  // DOC L0022: custom: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const custom='Lavoro · Mi sesión <uno>';
  // DOC L0023: Esegue il passaggio esplicito «await page.locator('#save-name').fill(custom);await page.getByRole('button',{name:tr('Salva sul dispositivo'),exact:true}).click();» nel flusso: Prova browser automatizzata.
  await page.locator('#save-name').fill(custom);await page.getByRole('button',{name:tr('Salva sul dispositivo'),exact:true}).click();
  // DOC L0024: Esegue il passaggio esplicito «await page.getByRole('heading',{name:custom,exact:true}).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading',{name:custom,exact:true}).waitFor();
  // DOC L0025: Legge il valore locale e applica i controlli/fallback riportati nell’espressione. saved: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const saved=await page.evaluate(()=>JSON.parse(localStorage.getItem('tabata:v1:data')));
  // DOC L0026: Esegue il passaggio esplicito «await page.locator('[data-action=settings]').first().click();» nel flusso: Prova browser automatizzata.
  await page.locator('[data-action=settings]').first().click();
  // DOC L0027: Esegue il passaggio esplicito «await page.locator('[data-action=language][data-value=en]').click();» nel flusso: Prova browser automatizzata.
  await page.locator('[data-action=language][data-value=en]').click();
  // DOC L0028: Esegue il passaggio esplicito «await page.getByRole('heading',{name:'Settings',exact:true}).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading',{name:'Settings',exact:true}).waitFor();
  // DOC L0029: Esegue il passaggio esplicito «await page.reload();await page.getByRole('heading',{name:'Tabata Timer',exact:true}).waitFor();» nel flusso: Prova browser automatizzata.
  await page.reload();await page.getByRole('heading',{name:'Tabata Timer',exact:true}).waitFor();
  // DOC L0030: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.locator('html').getAttribute('lang'),'en');
  // DOC L0031: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Legge il valore locale e applica i controlli/fallback riportati nell’espressione.
  assert.deepEqual((await page.evaluate(()=>JSON.parse(localStorage.getItem('tabata:v1:data')))).workouts,saved.workouts);
  // DOC L0032: Esegue il passaggio esplicito «await page.locator('[data-action=settings]').first().click();await page.locator(`[data-action=language][data-value=${lang}]`).click();» nel flusso: Prova browser automatizzata.
  await page.locator('[data-action=settings]').first().click();await page.locator(`[data-action=language][data-value=${lang}]`).click();
  // DOC L0033: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  assert.deepEqual(await page.locator('[data-action=palette]').allTextContents(), ['Viola','Blu','Verde chiaro','Verde scuro','Azzurro','Arancione','Giallo','Rosso','Rosa'].map(x=>tr(x)));
  // DOC L0034: Esegue il passaggio esplicito «await page.locator('[data-action=help]').first().click();» nel flusso: Prova browser automatizzata.
  await page.locator('[data-action=help]').first().click();
  // DOC L0035: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  assert.deepEqual(await page.locator('main h2').allTextContents(),['Workout e Studio','Per conservare un allenamento','Tema e Colori'].map(x=>tr(x)));
  // DOC L0036: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
  await page.locator('#mode-switch').click();await page.getByRole('heading',{name:'Tabata Timer',exact:true}).waitFor();
  // DOC L0037: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.locator('[data-action=demo]').count(),0);
  // DOC L0038: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.locator('[data-quick-path="activities.0.concentrationSec"]').inputValue(),'25');
  // DOC L0039: Esegue il passaggio esplicito «await page.locator('[data-quick-path="activities.0.title"]').fill(custom);» nel flusso: Prova browser automatizzata.
  await page.locator('[data-quick-path="activities.0.title"]').fill(custom);
  // DOC L0040: Esegue il passaggio esplicito «await page.locator('[data-action=activity-add]').click();await page.locator('[data-action=activity-delete][data-index="1"]').click();» nel flusso: Prova browser automatizzata.
  await page.locator('[data-action=activity-add]').click();await page.locator('[data-action=activity-delete][data-index="1"]').click();
  // DOC L0041: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.locator('.study-activity').count(),1);
  // DOC L0042: Esegue il passaggio esplicito «await page.getByRole('button',{name:tr('Salva {0}',tr('programma di studio')),exact:true}).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button',{name:tr('Salva {0}',tr('programma di studio')),exact:true}).click();
  // DOC L0043: Esegue il passaggio esplicito «await page.locator('#save-name').fill('Matemática · My study');await page.getByRole('button',{name:tr('Salva sul dispositivo'),exact:true}).click();» nel flusso: Prova browser automatizzata.
  await page.locator('#save-name').fill('Matemática · My study');await page.getByRole('button',{name:tr('Salva sul dispositivo'),exact:true}).click();
  // DOC L0044: Esegue il passaggio esplicito «await page.locator('.workout-menu summary').click();await page.getByRole('button',{name:tr('Modifica'),exact:true}).click();» nel flusso: Prova browser automatizzata.
  await page.locator('.workout-menu summary').click();await page.getByRole('button',{name:tr('Modifica'),exact:true}).click();
  // DOC L0045: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.
  assert.match(await page.locator('#preview').innerText(), new RegExp(tr('Pausa tra sessioni')));
  // DOC L0046: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.
  assert.ok((await page.locator('#preview').innerText()).includes(custom));
  // DOC L0047: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  for (const width of [320,390,768,1440]) {
   // DOC L0048: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
   await page.setViewportSize({width,height:900});assert.ok(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth),`${lang} ${width}`);
  // DOC L0049: Chiude il costrutto aperto alla riga 47.
  }
  // DOC L0050: Esegue il passaggio esplicito «await page.setViewportSize({width:390,height:900});await page.screenshot({path:`output/screenshots/${lang}-studio-editor-v180.png`,fullPage:true});» nel flusso: Prova browser automatizzata.
  await page.setViewportSize({width:390,height:900});await page.screenshot({path:`output/screenshots/${lang}-studio-editor-v180.png`,fullPage:true});
  // DOC L0051: Esegue il passaggio esplicito «await page.locator('[data-action=workouts]').first().click();» nel flusso: Prova browser automatizzata.
  await page.locator('[data-action=workouts]').first().click();
  // DOC L0052: Esegue il passaggio esplicito «await page.getByRole('button',{name:tr('Avvia'),exact:true}).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button',{name:tr('Avvia'),exact:true}).click();
  // DOC L0053: Esegue il passaggio esplicito «await page.getByRole('heading',{name:tr('Preparazione'),exact:true}).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading',{name:tr('Preparazione'),exact:true}).waitFor();
  // DOC L0054: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. digits: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.
  const digits=await page.locator('#timer-digits').innerText();assert.match(digits,/^0:(?:1\d|20)$/);
  // DOC L0055: Esegue il passaggio esplicito «await page.locator('[data-action=skip]').click();await page.getByRole('heading',{name:tr('Concentrazione'),exact:true}).waitFor();» nel flusso: Prova browser automatizzata.
  await page.locator('[data-action=skip]').click();await page.getByRole('heading',{name:tr('Concentrazione'),exact:true}).waitFor();
  // DOC L0056: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.
  assert.ok((await page.locator('#timer-phase').innerText()).includes(custom));
  // DOC L0057: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.
  assert.ok((await page.locator('#timer-next').innerText()).includes(tr('Pausa tra sessioni')));
  // DOC L0058: Esegue il passaggio esplicito «await page.locator('[data-action=stop]').click();await page.getByRole('heading',{name:tr('Sessione interrotta.'),exact:true}).waitFor();» nel flusso: Prova browser automatizzata.
  await page.locator('[data-action=stop]').click();await page.getByRole('heading',{name:tr('Sessione interrotta.'),exact:true}).waitFor();
  // DOC L0059: Esegue il passaggio esplicito «await page.locator('[data-action=history]').click();await page.locator('[data-action=stats]').click();await page.getByRole('heading',{name:tr('Statistiche di studio'),exact:true}).» nel flusso: Prova browser automatizzata.
  await page.locator('[data-action=history]').click();await page.locator('[data-action=stats]').click();await page.getByRole('heading',{name:tr('Statistiche di studio'),exact:true}).waitFor();
  // DOC L0060: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.
  assert.ok((await page.locator('main').innerText()).includes('Matemática · My study'));
  // DOC L0061: Esegue il passaggio esplicito «await page.evaluate(async()=>{await navigator.serviceWorker.ready});await page.reload();await context.setOffline(true);await page.reload();» nel flusso: Prova browser automatizzata.
  await page.evaluate(async()=>{await navigator.serviceWorker.ready});await page.reload();await context.setOffline(true);await page.reload();
  // DOC L0062: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  await page.getByRole('heading',{name:'Tabata Timer',exact:true}).waitFor();assert.equal(await page.locator('html').getAttribute('lang'),lang);
  // DOC L0063: Esegue il passaggio esplicito «checks.push({language:lang,deviceDetection:true,preferencePersisted:true,savedNamesPreserved:true,editorTimerStatistics:true,responsive:[320,390,768,1440],offline:true,demosFooterA» nel flusso: Prova browser automatizzata.
  checks.push({language:lang,deviceDetection:true,preferencePersisted:true,savedNamesPreserved:true,editorTimerStatistics:true,responsive:[320,390,768,1440],offline:true,demosFooterAndStorageLineRemoved:true});
  // DOC L0064: Esegue il passaggio esplicito «await context.close();» nel flusso: Prova browser automatizzata.
  await context.close();
 // DOC L0065: Chiude il costrutto aperto alla riga 10.
 }
 // DOC L0066: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
 assert.deepEqual(errors,[]);await writeFile('output/browser-languages.json',JSON.stringify({version:'1.8.0',checks,errors},null,2));
 // DOC L0067: Esegue il passaggio esplicito «console.log('Italiano, English, Español: flussi completi, nomi intatti, tre guide, nove palette, quattro larghezze e offline verificati.');» nel flusso: Prova browser automatizzata.
 console.log('Italiano, English, Español: flussi completi, nomi intatti, tre guide, nove palette, quattro larghezze e offline verificati.');
// DOC L0068: Esegue il passaggio esplicito «} finally {await browser.close();}» nel flusso: Prova browser automatizzata.
} finally {await browser.close();}
