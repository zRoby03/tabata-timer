// Optional local QA tool. No package install or dependency is needed by the website itself.
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import { pathToFileURL } from 'node:url';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { pathToFileURL } from 'node:url';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import { mkdir, writeFile } from 'node:fs/promises';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { mkdir, writeFile } from 'node:fs/promises';
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import assert from 'node:assert/strict';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import assert from 'node:assert/strict';
// DOC L0005: Esegue il passaggio esplicito «const { chromium } = await import(process.env.PLAYWRIGHT_MODULE ? pathToFileURL(process.env.PLAYWRIGHT_MODULE).href : 'playwright');» nel flusso: Prova browser automatizzata.
const { chromium } = await import(process.env.PLAYWRIGHT_MODULE ? pathToFileURL(process.env.PLAYWRIGHT_MODULE).href : 'playwright');
// DOC L0006: browser: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const browser = await chromium.launch({ channel: process.env.TABATA_BROWSER || 'msedge', headless: true });
// DOC L0007: context: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const context = await browser.newContext({ locale: 'it-IT', reducedMotion: 'reduce' });
// DOC L0008: page: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const page = await context.newPage(), errors = [], requests = [];
// DOC L0009: Esegue il passaggio esplicito «page.on('pageerror', error => errors.push(error.message));» nel flusso: Prova browser automatizzata.
page.on('pageerror', error => errors.push(error.message));
// DOC L0010: Esegue il passaggio esplicito «page.on('request', request => requests.push(request.url()));» nel flusso: Prova browser automatizzata.
page.on('request', request => requests.push(request.url()));
// DOC L0011: Esegue il passaggio esplicito «page.on('dialog', dialog => dialog.accept());» nel flusso: Prova browser automatizzata.
page.on('dialog', dialog => dialog.accept());
// DOC L0012: base: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const base = process.env.TABATA_URL || 'http://127.0.0.1:5500';
// DOC L0013: checks: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const checks = [];
// DOC L0014: Esegue il passaggio esplicito «await mkdir('output/screenshots', { recursive: true });» nel flusso: Prova browser automatizzata.
await mkdir('output/screenshots', { recursive: true });
// DOC L0015: Esegue il passaggio esplicito «try {» nel flusso: Prova browser automatizzata.
try {
  // DOC L0016: Esegue il passaggio esplicito «await page.goto(base); await page.getByRole('heading', { name: 'Tabata Timer' }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.goto(base); await page.getByRole('heading', { name: 'Tabata Timer' }).waitFor();
  // DOC L0017: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  for (const width of [320, 390, 768, 1440]) {
    // DOC L0018: Esegue il passaggio esplicito «await page.setViewportSize({ width, height: 950 });» nel flusso: Prova browser automatizzata.
    await page.setViewportSize({ width, height: 950 });
    // DOC L0019: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.equal(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth), true, `overflow home ${width}`);
    // DOC L0020: Esegue il passaggio esplicito «await page.screenshot({ path: `output/screenshots/web-home-${width}.png`, fullPage: true });» nel flusso: Prova browser automatizzata.
    await page.screenshot({ path: `output/screenshots/web-home-${width}.png`, fullPage: true });
    // DOC L0021: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.equal(await page.locator('[data-quick-path]:visible').count(), 3);
    // DOC L0022: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Allenamenti', exact: true }).click();» nel flusso: Prova browser automatizzata.
    await page.getByRole('button', { name: 'Allenamenti', exact: true }).click();
    // DOC L0023: Esegue il passaggio esplicito «await page.getByRole('button', { name: '+ Crea allenamento', exact: true }).click();» nel flusso: Prova browser automatizzata.
    await page.getByRole('button', { name: '+ Crea allenamento', exact: true }).click();
    // DOC L0024: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.equal(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth), true, `overflow editor ${width}`);
    // DOC L0025: Esegue il passaggio esplicito «await page.screenshot({ path: `output/screenshots/web-editor-${width}.png`, fullPage: true });» nel flusso: Prova browser automatizzata.
    await page.screenshot({ path: `output/screenshots/web-editor-${width}.png`, fullPage: true });
    // DOC L0026: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Torna agli allenamenti' }).click();» nel flusso: Prova browser automatizzata.
    await page.getByRole('button', { name: 'Torna agli allenamenti' }).click();
    // DOC L0027: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Timer', exact: true }).click();» nel flusso: Prova browser automatizzata.
    await page.getByRole('button', { name: 'Timer', exact: true }).click();
  // DOC L0028: Chiude il costrutto aperto alla riga 17.
  }
  // DOC L0029: Esegue il passaggio esplicito «checks.push('Timer rapido con tre campi iniziali ed editor: 320, 390, 768, 1440 senza overflow');» nel flusso: Prova browser automatizzata.
  checks.push('Timer rapido con tre campi iniziali ed editor: 320, 390, 768, 1440 senza overflow');
  // DOC L0030: Esegue il passaggio esplicito «await page.locator('[data-quick-path="blocks.0.exercises.0.repetitions"]').fill('3');» nel flusso: Prova browser automatizzata.
  await page.locator('[data-quick-path="blocks.0.exercises.0.repetitions"]').fill('3');
  // DOC L0031: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.
  await page.getByText('Altre opzioni', { exact: true }).click();
  // DOC L0032: Esegue il passaggio esplicito «await page.locator('[data-quick-path="blocks.0.cycles"]').fill('2');» nel flusso: Prova browser automatizzata.
  await page.locator('[data-quick-path="blocks.0.cycles"]').fill('2');
  // DOC L0033: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.locator('#quick-total').textContent(), '3:40');
  // DOC L0034: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Salva allenamento', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Salva allenamento', exact: true }).click();
  // DOC L0035: Esegue il passaggio esplicito «await page.locator('#save-name').fill('Riferimento 3:40');» nel flusso: Prova browser automatizzata.
  await page.locator('#save-name').fill('Riferimento 3:40');
  // DOC L0036: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.evaluate(() => localStorage.length), 0);
  // DOC L0037: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Salva sul dispositivo', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Salva sul dispositivo', exact: true }).click();
  // DOC L0038: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Legge il valore locale e applica i controlli/fallback riportati nell’espressione.
  assert.equal(await page.evaluate(() => JSON.parse(localStorage.getItem('tabata:v1:data')).workouts.some(w => w.title === 'Riferimento 3:40')), true);
  // DOC L0039: Esegue il passaggio esplicito «checks.push('Timer rapido 3:40, nome e conferma esplicita del salvataggio locale');» nel flusso: Prova browser automatizzata.
  checks.push('Timer rapido 3:40, nome e conferma esplicita del salvataggio locale');
  // DOC L0040: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();
  // DOC L0041: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Salva cronologia', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Salva cronologia', exact: true }).click();
  // DOC L0042: Esegue il passaggio esplicito «await page.reload();» nel flusso: Prova browser automatizzata.
  await page.reload();
  // DOC L0043: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Allenamenti', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Allenamenti', exact: true }).click();
  // DOC L0044: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Riferimento 3:40', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Riferimento 3:40', exact: true }).waitFor();
  // DOC L0045: Esegue il passaggio esplicito «checks.push('Persistenza esplicita conserva workout al reload');» nel flusso: Prova browser automatizzata.
  checks.push('Persistenza esplicita conserva workout al reload');
  // DOC L0046: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Timer', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Timer', exact: true }).click();
  // DOC L0047: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.
  await page.getByText('Altre opzioni', { exact: true }).click();
  // DOC L0048: Esegue il passaggio esplicito «await page.locator('[data-quick-path="blocks.0.exercises.0.workSec"]').fill('5');» nel flusso: Prova browser automatizzata.
  await page.locator('[data-quick-path="blocks.0.exercises.0.workSec"]').fill('5');
  // DOC L0049: Esegue il passaggio esplicito «await page.locator('[data-quick-path="blocks.0.exercises.0.restBetweenRepsSec"]').fill('2');» nel flusso: Prova browser automatizzata.
  await page.locator('[data-quick-path="blocks.0.exercises.0.restBetweenRepsSec"]').fill('2');
  // DOC L0050: Esegue il passaggio esplicito «await page.locator('[data-quick-path="blocks.0.exercises.0.repetitions"]').fill('2');» nel flusso: Prova browser automatizzata.
  await page.locator('[data-quick-path="blocks.0.exercises.0.repetitions"]').fill('2');
  // DOC L0051: Esegue il passaggio esplicito «await page.locator('[data-quick-path="blocks.0.cycles"]').fill('1');» nel flusso: Prova browser automatizzata.
  await page.locator('[data-quick-path="blocks.0.cycles"]').fill('1');
  // DOC L0052: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Avvia', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Avvia', exact: true }).click();
  // DOC L0053: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Preparazione', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Preparazione', exact: true }).waitFor();
  // DOC L0054: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.locator('#timer-digits').textContent(), '0:20');
  // DOC L0055: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Salta →', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Salta →', exact: true }).click();
  // DOC L0056: Esegue il passaggio esplicito «await page.waitForTimeout(600); await page.getByRole('button', { name: 'Pausa', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.waitForTimeout(600); await page.getByRole('button', { name: 'Pausa', exact: true }).click();
  // DOC L0057: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. paused: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const paused = await page.locator('#timer-digits').textContent(); await page.waitForTimeout(1200); assert.equal(await page.locator('#timer-digits').textContent(), paused);
  // DOC L0058: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Riprendi', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Riprendi', exact: true }).click();
  // DOC L0059: Esegue il passaggio esplicito «await page.waitForTimeout(12500); await page.getByRole('heading', { name: 'Allenamento completato.' }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.waitForTimeout(12500); await page.getByRole('heading', { name: 'Allenamento completato.' }).waitFor();
  // DOC L0060: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Legge il valore locale e applica i controlli/fallback riportati nell’espressione.
  assert.equal(await page.evaluate(() => JSON.parse(localStorage.getItem('tabata:v1:data')).sessions.length), 1);
  // DOC L0061: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.
  await page.getByRole('button', { name: 'Vedi attività' }).click(); await page.getByText('Completata', { exact: true }).waitFor();
  // DOC L0062: Esegue il passaggio esplicito «checks.push('Demo completa, pausa/ripresa, sessione unica salvata');» nel flusso: Prova browser automatizzata.
  checks.push('Demo completa, pausa/ripresa, sessione unica salvata');
  // DOC L0063: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  await page.getByRole('button', { name: /Statistiche/ }).click(); assert.equal(await page.locator('svg[role="img"]').count(), 3);
  // DOC L0064: Esegue il passaggio esplicito «checks.push('Tre grafici accessibili e tabelle dati');» nel flusso: Prova browser automatizzata.
  checks.push('Tre grafici accessibili e tabelle dati');
  // DOC L0065: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();
  // DOC L0066: Legge il valore locale e applica i controlli/fallback riportati nell’espressione. backup: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const backup = await page.evaluate(() => localStorage.getItem('tabata:v1:data'));
  // DOC L0067: Esegue il passaggio esplicito «await page.locator('#import-file').setInputFiles({ name: 'backup.json', mimeType: 'application/json', buffer: Buffer.from(backup) });» nel flusso: Prova browser automatizzata.
  await page.locator('#import-file').setInputFiles({ name: 'backup.json', mimeType: 'application/json', buffer: Buffer.from(backup) });
  // DOC L0068: Esegue il passaggio esplicito «await page.locator('#import-dialog').waitFor({ state: 'visible' }); await page.getByRole('button', { name: 'Annulla', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.locator('#import-dialog').waitFor({ state: 'visible' }); await page.getByRole('button', { name: 'Annulla', exact: true }).click();
  // DOC L0069: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Legge il valore locale e applica i controlli/fallback riportati nell’espressione.
  assert.equal(await page.evaluate(() => localStorage.getItem('tabata:v1:data')), backup);
  // DOC L0070: Esegue il passaggio esplicito «checks.push('Importazione validata e annullamento senza sostituzione');» nel flusso: Prova browser automatizzata.
  checks.push('Importazione validata e annullamento senza sostituzione');
  // DOC L0071: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Timer', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Timer', exact: true }).click();
  // DOC L0072: Esegue il passaggio esplicito «await page.evaluate(async () => { await navigator.serviceWorker.ready; });» nel flusso: Prova browser automatizzata.
  await page.evaluate(async () => { await navigator.serviceWorker.ready; });
  // DOC L0073: Esegue il passaggio esplicito «await page.reload(); await page.getByRole('heading', { name: 'Tabata Timer' }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.reload(); await page.getByRole('heading', { name: 'Tabata Timer' }).waitFor();
  // DOC L0074: Esegue il passaggio esplicito «await context.setOffline(true); await page.reload(); await page.getByRole('button', { name: 'Allenamenti', exact: true }).click(); await page.getByRole('heading', { name: 'Riferime» nel flusso: Prova browser automatizzata.
  await context.setOffline(true); await page.reload(); await page.getByRole('button', { name: 'Allenamenti', exact: true }).click(); await page.getByRole('heading', { name: 'Riferimento 3:40', exact: true }).waitFor();
  // DOC L0075: Esegue il passaggio esplicito «checks.push('Reload offline dopo cache service worker');» nel flusso: Prova browser automatizzata.
  checks.push('Reload offline dopo cache service worker');
  // DOC L0076: Esegue il passaggio esplicito «await context.setOffline(false);» nel flusso: Prova browser automatizzata.
  await context.setOffline(false);
  // DOC L0077: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.deepEqual(errors, []);
  // DOC L0078: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.ok(requests.every(url => url.startsWith(base)), 'Rete esterna inattesa');
  // DOC L0079: Esegue il passaggio esplicito «checks.push('Nessun errore JS e nessuna richiesta a terze parti');» nel flusso: Prova browser automatizzata.
  checks.push('Nessun errore JS e nessuna richiesta a terze parti');
  // DOC L0080: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  await writeFile('output/browser-check.json', JSON.stringify({ checks, errors, externalRequests: requests.filter(url => !url.startsWith(base)) }, null, 2));
  // DOC L0081: Esegue il passaggio esplicito «console.log(checks.join('\n'));» nel flusso: Prova browser automatizzata.
  console.log(checks.join('\n'));
// DOC L0082: Esegue il passaggio esplicito «} finally { await browser.close(); }» nel flusso: Prova browser automatizzata.
} finally { await browser.close(); }
