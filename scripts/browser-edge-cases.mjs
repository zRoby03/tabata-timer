// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { pathToFileURL } from 'node:url';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { pathToFileURL } from 'node:url';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import assert from 'node:assert/strict';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import assert from 'node:assert/strict';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import { readFile, writeFile } from 'node:fs/promises';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { readFile, writeFile } from 'node:fs/promises';
// DOC L0004: Esegue il passaggio esplicito «const { chromium } = await import(process.env.PLAYWRIGHT_MODULE ? pathToFileURL(process.env.PLAYWRIGHT_MODULE).href : 'playwright');» nel flusso: Prova browser automatizzata.
const { chromium } = await import(process.env.PLAYWRIGHT_MODULE ? pathToFileURL(process.env.PLAYWRIGHT_MODULE).href : 'playwright');
// DOC L0005: browser: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const browser = await chromium.launch({ channel: process.env.TABATA_BROWSER || 'msedge', headless: true });
// DOC L0006: base: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const base = process.env.TABATA_URL || 'http://127.0.0.1:5500', checks = [];
// DOC L0007: Esegue il passaggio esplicito «try {» nel flusso: Prova browser automatizzata.
try {
  // DOC L0008: context: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const context = await browser.newContext({ locale: 'it-IT', reducedMotion: 'reduce', viewport: { width: 390, height: 844 } });
  // DOC L0009: page: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const page = await context.newPage(); page.on('dialog', d => d.accept()); await page.goto(base);
  // DOC L0010: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.locator('nav button').count(), 3);
  // DOC L0011: Esegue il passaggio esplicito «await page.locator('[data-quick-path="blocks.0.exercises.0.workSec"]').fill('0');» nel flusso: Prova browser automatizzata.
  await page.locator('[data-quick-path="blocks.0.exercises.0.workSec"]').fill('0');
  // DOC L0012: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.locator('#quick-start-button').isDisabled(), true);
  // DOC L0013: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.locator('[data-action="quick-save"]').isDisabled(), true);
  // DOC L0014: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Allenamenti', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Allenamenti', exact: true }).click();
  // DOC L0015: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Timer', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Timer', exact: true }).click();
  // DOC L0016: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.locator('#quick-start-button').isDisabled(), true);
  // DOC L0017: Esegue il passaggio esplicito «checks.push('Valori invalidi bloccano avvio e salvataggio anche tornando al timer');» nel flusso: Prova browser automatizzata.
  checks.push('Valori invalidi bloccano avvio e salvataggio anche tornando al timer');
  // DOC L0018: Esegue il passaggio esplicito «await page.locator('[data-quick-path="blocks.0.exercises.0.workSec"]').fill('1');» nel flusso: Prova browser automatizzata.
  await page.locator('[data-quick-path="blocks.0.exercises.0.workSec"]').fill('1');
  // DOC L0019: Esegue il passaggio esplicito «await page.locator('[data-quick-path="blocks.0.exercises.0.restBetweenRepsSec"]').fill('0');» nel flusso: Prova browser automatizzata.
  await page.locator('[data-quick-path="blocks.0.exercises.0.restBetweenRepsSec"]').fill('0');
  // DOC L0020: Esegue il passaggio esplicito «await page.locator('[data-quick-path="blocks.0.exercises.0.repetitions"]').fill('2');» nel flusso: Prova browser automatizzata.
  await page.locator('[data-quick-path="blocks.0.exercises.0.repetitions"]').fill('2');
  // DOC L0021: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Avvia', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Avvia', exact: true }).click();
  // DOC L0022: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Preparazione', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Preparazione', exact: true }).waitFor();
  // DOC L0023: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Salta →', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Salta →', exact: true }).click();
  // DOC L0024: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Allenamento completato.', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Allenamento completato.', exact: true }).waitFor();
  // DOC L0025: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.evaluate(() => localStorage.length), 0);
  // DOC L0026: Esegue il passaggio esplicito «await page.reload(); await page.getByRole('button', { name: 'Attività', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.reload(); await page.getByRole('button', { name: 'Attività', exact: true }).click();
  // DOC L0027: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Si comincia da qui', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Si comincia da qui', exact: true }).waitFor();
  // DOC L0028: Esegue il passaggio esplicito «checks.push('Uso temporaneo senza scritture e senza attività al reload');» nel flusso: Prova browser automatizzata.
  checks.push('Uso temporaneo senza scritture e senza attività al reload');
  // DOC L0029: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Timer', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Timer', exact: true }).click();
  // DOC L0030: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Salva allenamento', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Salva allenamento', exact: true }).click();
  // DOC L0031: Esegue il passaggio esplicito «await page.locator('#save-name').fill('Da annullare');» nel flusso: Prova browser automatizzata.
  await page.locator('#save-name').fill('Da annullare');
  // DOC L0032: Esegue il passaggio esplicito «await page.screenshot({ path: 'output/screenshots/web-save-390.png' });» nel flusso: Prova browser automatizzata.
  await page.screenshot({ path: 'output/screenshots/web-save-390.png' });
  // DOC L0033: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Annulla', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Annulla', exact: true }).click();
  // DOC L0034: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.evaluate(() => localStorage.length), 0);
  // DOC L0035: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Allenamenti', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Allenamenti', exact: true }).click();
  // DOC L0036: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.getByRole('heading', { name: 'Da annullare', exact: true }).count(), 0);
  // DOC L0037: Esegue il passaggio esplicito «checks.push('Annullamento del salvataggio senza dati creati o scritture');» nel flusso: Prova browser automatizzata.
  checks.push('Annullamento del salvataggio senza dati creati o scritture');
  // DOC L0038: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Guida', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Guida', exact: true }).click();
  // DOC L0039: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Guida all’uso', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Guida all’uso', exact: true }).waitFor();
  // DOC L0040: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Per conservare un allenamento', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Per conservare un allenamento', exact: true }).waitFor();
  // DOC L0041: Esegue il passaggio esplicito «await page.screenshot({ path: 'output/screenshots/web-guide-390.png' });» nel flusso: Prova browser automatizzata.
  await page.screenshot({ path: 'output/screenshots/web-guide-390.png' });
  // DOC L0042: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth), true);
  // DOC L0043: Esegue il passaggio esplicito «checks.push('Guida accessibile dall’intestazione, con salvataggio e tutte le funzioni');» nel flusso: Prova browser automatizzata.
  checks.push('Guida accessibile dall’intestazione, con salvataggio e tutte le funzioni');
  // DOC L0044: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Allenamenti', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Allenamenti', exact: true }).click();
  // DOC L0045: Esegue il passaggio esplicito «await page.getByRole('button', { name: '+ Crea allenamento', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: '+ Crea allenamento', exact: true }).click();
  // DOC L0046: Esegue il passaggio esplicito «await page.locator('[data-path="title"]').fill('Sequenza personalizzata');» nel flusso: Prova browser automatizzata.
  await page.locator('[data-path="title"]').fill('Sequenza personalizzata');
  // DOC L0047: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Salva sul dispositivo', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Salva sul dispositivo', exact: true }).click();
  // DOC L0048: Esegue il passaggio esplicito «await page.reload(); await page.getByRole('button', { name: 'Allenamenti', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.reload(); await page.getByRole('button', { name: 'Allenamenti', exact: true }).click();
  // DOC L0049: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Sequenza personalizzata', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Sequenza personalizzata', exact: true }).waitFor();
  // DOC L0050: Esegue il passaggio esplicito «checks.push('Anche l’editor salva sul dispositivo e conserva l’allenamento al reload');» nel flusso: Prova browser automatizzata.
  checks.push('Anche l’editor salva sul dispositivo e conserva l’allenamento al reload');
  // DOC L0051: Esegue il passaggio esplicito «await page.setViewportSize({ width: 720, height: 1000 });» nel flusso: Prova browser automatizzata.
  await page.setViewportSize({ width: 720, height: 1000 });
  // DOC L0052: Esegue il passaggio esplicito «await page.getByRole('button', { name: '+ Crea allenamento', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: '+ Crea allenamento', exact: true }).click();
  // DOC L0053: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth), true);
  // DOC L0054: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.evaluate(() => matchMedia('(prefers-reduced-motion: reduce)').matches), true);
  // DOC L0055: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. smallTargets: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const smallTargets = await page.locator('button:visible').evaluateAll(buttons => buttons.filter(b => b.getBoundingClientRect().height < 44 || b.getBoundingClientRect().width < 44).map(b => b.textContent));
  // DOC L0056: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.deepEqual(smallTargets, []);
  // DOC L0057: Esegue il passaggio esplicito «checks.push('Viewport equivalente a zoom 200%, riduzione movimento e pulsanti da almeno 44 px');» nel flusso: Prova browser automatizzata.
  checks.push('Viewport equivalente a zoom 200%, riduzione movimento e pulsanti da almeno 44 px');
  // DOC L0058: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Torna agli allenamenti' }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Torna agli allenamenti' }).click();
  // DOC L0059: Legge il valore locale e applica i controlli/fallback riportati nell’espressione. before: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const before = await page.evaluate(() => localStorage.getItem('tabata:v1:data'));
  // DOC L0060: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti.
  await page.evaluate(() => { Storage.prototype.setItem = () => { throw new DOMException('quota', 'QuotaExceededError'); }; });
  // DOC L0061: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Timer', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Timer', exact: true }).click();
  // DOC L0062: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Salva allenamento', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Salva allenamento', exact: true }).click();
  // DOC L0063: Esegue il passaggio esplicito «await page.locator('#save-name').fill('Resta in memoria');» nel flusso: Prova browser automatizzata.
  await page.locator('#save-name').fill('Resta in memoria');
  // DOC L0064: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Salva sul dispositivo', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Salva sul dispositivo', exact: true }).click();
  // DOC L0065: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.match(await page.locator('#save-error').textContent(), /Salvataggio non riuscito/);
  // DOC L0066: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Salva sul dispositivo', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Salva sul dispositivo', exact: true }).click();
  // DOC L0067: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Legge il valore locale e applica i controlli/fallback riportati nell’espressione.
  assert.equal(await page.evaluate(() => localStorage.getItem('tabata:v1:data')), before);
  // DOC L0068: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Annulla', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Annulla', exact: true }).click();
  // DOC L0069: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Allenamenti', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Allenamenti', exact: true }).click();
  // DOC L0070: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.getByRole('heading', { name: 'Resta in memoria', exact: true }).count(), 1);
  // DOC L0071: Esegue il passaggio esplicito «checks.push('Quota esaurita: errore nella finestra, snapshot intatto e tentativi senza duplicati');» nel flusso: Prova browser automatizzata.
  checks.push('Quota esaurita: errore nella finestra, snapshot intatto e tentativi senza duplicati');
  // DOC L0072: Esegue il passaggio esplicito «await context.close();» nel flusso: Prova browser automatizzata.
  await context.close();
  // DOC L0073: denied: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const denied = await browser.newContext({ locale: 'it-IT' });
  // DOC L0074: get: Passaggio «get» dello strumento: Prova browser automatizzata. Gli argomenti e le operazioni sono descritti nelle righe del corpo.
  await denied.addInitScript(() => { Object.defineProperty(window, 'localStorage', { get() { throw new DOMException('denied', 'SecurityError'); } }); });
  // DOC L0075: deniedPage: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const deniedPage = await denied.newPage(); await deniedPage.goto(base);
  // DOC L0076: Esegue il passaggio esplicito «await deniedPage.getByRole('heading', { name: 'Tabata Timer' }).waitFor();» nel flusso: Prova browser automatizzata.
  await deniedPage.getByRole('heading', { name: 'Tabata Timer' }).waitFor();
  // DOC L0077: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.match(await deniedPage.locator('#notice').textContent(), /non disponibile/);
  // DOC L0078: Esegue il passaggio esplicito «checks.push('Storage bloccato: fallback temporaneo e avviso'); await denied.close();» nel flusso: Prova browser automatizzata.
  checks.push('Storage bloccato: fallback temporaneo e avviso'); await denied.close();
  // DOC L0079: imported: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. p: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const imported = await browser.newContext({ locale: 'it-IT' }); const p = await imported.newPage(); await p.goto(base);
  // DOC L0080: Esegue il passaggio esplicito «await p.locator('#import-file').setInputFiles({ name: 'fixture.json', mimeType: 'application/json', buffer: await readFile('fixtures/backup-web-v1.json') });» nel flusso: Prova browser automatizzata.
  await p.locator('#import-file').setInputFiles({ name: 'fixture.json', mimeType: 'application/json', buffer: await readFile('fixtures/backup-web-v1.json') });
  // DOC L0081: Esegue il passaggio esplicito «await p.getByRole('button', { name: 'Sostituisci dati' }).click();» nel flusso: Prova browser automatizzata.
  await p.getByRole('button', { name: 'Sostituisci dati' }).click();
  // DOC L0082: Esegue il passaggio esplicito «await p.getByRole('heading', { name: 'Riferimento 3:40', exact: true }).waitFor();» nel flusso: Prova browser automatizzata.
  await p.getByRole('heading', { name: 'Riferimento 3:40', exact: true }).waitFor();
  // DOC L0083: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.
  await p.getByRole('button', { name: 'Attività', exact: true }).click(); await p.getByText('50%', { exact: true }).waitFor();
  // DOC L0084: Esegue il passaggio esplicito «await p.locator('#import-file').setInputFiles({ name: 'bad.json', mimeType: 'application/json', buffer: Buffer.from('{"schemaVersion":2}') });» nel flusso: Prova browser automatizzata.
  await p.locator('#import-file').setInputFiles({ name: 'bad.json', mimeType: 'application/json', buffer: Buffer.from('{"schemaVersion":2}') });
  // DOC L0085: Esegue il passaggio esplicito «await p.waitForFunction(() => document.querySelector('#notice').textContent.includes('rifiutata'));» nel flusso: Prova browser automatizzata.
  await p.waitForFunction(() => document.querySelector('#notice').textContent.includes('rifiutata'));
  // DOC L0086: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.match(await p.locator('#notice').textContent(), /rifiutata/);
  // DOC L0087: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.
  await p.getByText('50%', { exact: true }).waitFor();
  // DOC L0088: Esegue il passaggio esplicito «checks.push('Importazione valida con anteprima; file errato rifiutato senza perdere i dati');» nel flusso: Prova browser automatizzata.
  checks.push('Importazione valida con anteprima; file errato rifiutato senza perdere i dati');
  // DOC L0089: Esegue il passaggio esplicito «await imported.close();» nel flusso: Prova browser automatizzata.
  await imported.close();
  // DOC L0090: Esegue il passaggio esplicito «await writeFile('output/browser-edge-cases.json', JSON.stringify({ checks }, null, 2)); console.log(checks.join('\n'));» nel flusso: Prova browser automatizzata.
  await writeFile('output/browser-edge-cases.json', JSON.stringify({ checks }, null, 2)); console.log(checks.join('\n'));
// DOC L0091: Esegue il passaggio esplicito «} finally { await browser.close(); }» nel flusso: Prova browser automatizzata.
} finally { await browser.close(); }
