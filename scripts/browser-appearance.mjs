// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { pathToFileURL } from 'node:url';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { pathToFileURL } from 'node:url';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import { mkdir, writeFile } from 'node:fs/promises';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { mkdir, writeFile } from 'node:fs/promises';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import assert from 'node:assert/strict';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import assert from 'node:assert/strict';
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import { PALETTES, APPEARANCE_KEY } from '../web/js/appearance.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { PALETTES, APPEARANCE_KEY } from '../web/js/appearance.js';
// DOC L0005: Esegue il passaggio esplicito «const { chromium } = await import(process.env.PLAYWRIGHT_MODULE ? pathToFileURL(process.env.PLAYWRIGHT_MODULE).href : 'playwright');» nel flusso: Prova browser automatizzata.
const { chromium } = await import(process.env.PLAYWRIGHT_MODULE ? pathToFileURL(process.env.PLAYWRIGHT_MODULE).href : 'playwright');
// DOC L0006: browser: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const browser = await chromium.launch({ channel: process.env.TABATA_BROWSER || 'msedge', headless: true });
// DOC L0007: checks: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const checks = [], errors = [];
// DOC L0008: base: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const base = process.env.TABATA_URL || 'http://127.0.0.1:5500';
// DOC L0009: Esegue il passaggio esplicito «await mkdir('output/screenshots', { recursive: true });» nel flusso: Prova browser automatizzata.
await mkdir('output/screenshots', { recursive: true });
// DOC L0010: Esegue il passaggio esplicito «try {» nel flusso: Prova browser automatizzata.
try {
  // DOC L0011: context: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const context = await browser.newContext({ locale: 'it-IT', viewport: { width: 390, height: 900 } });
  // DOC L0012: page: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const page = await context.newPage(); page.on('pageerror', e => errors.push(e.message));
  // DOC L0013: Esegue il passaggio esplicito «await page.goto(base); await page.getByRole('heading', { name: 'Tabata Timer' }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.goto(base); await page.getByRole('heading', { name: 'Tabata Timer' }).waitFor();
  // DOC L0014: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.evaluate(() => localStorage.length), 0);
  // DOC L0015: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  for (const [i, seconds] of [20, 30, 40].entries()) {
    // DOC L0016: Esegue il passaggio esplicito «await page.getByRole('button', { name: `Tabata ${i + 1} ${seconds} / ${seconds / 2}`, exact: true }).click();» nel flusso: Prova browser automatizzata.
    await page.getByRole('button', { name: `Tabata ${i + 1} ${seconds} / ${seconds / 2}`, exact: true }).click();
    // DOC L0017: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.equal(await page.locator('[data-quick-path="blocks.0.exercises.0.workSec"]').inputValue(), String(seconds));
  // DOC L0018: Chiude il costrutto aperto alla riga 15.
  }
  // DOC L0019: Esegue il passaggio esplicito «checks.push('Tabata 1, 2 e 3 mantengono i tempi 20/10, 30/15 e 40/20');» nel flusso: Prova browser automatizzata.
  checks.push('Tabata 1, 2 e 3 mantengono i tempi 20/10, 30/15 e 40/20');
  // DOC L0020: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  for (const theme of ['light', 'dark']) for (const p of PALETTES) {
    // DOC L0021: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();» nel flusso: Prova browser automatizzata.
    await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();
    // DOC L0022: Esegue il passaggio esplicito «await page.getByRole('button', { name: theme === 'dark' ? 'Scuro' : 'Chiaro', exact: true }).click();» nel flusso: Prova browser automatizzata.
    await page.getByRole('button', { name: theme === 'dark' ? 'Scuro' : 'Chiaro', exact: true }).click();
    // DOC L0023: Esegue il passaggio esplicito «await page.getByRole('button', { name: p.name, exact: true }).click();» nel flusso: Prova browser automatizzata.
    await page.getByRole('button', { name: p.name, exact: true }).click();
    // DOC L0024: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.equal(await page.getByRole('button', { name: p.name, exact: true }).getAttribute('aria-pressed'), 'true');
    // DOC L0025: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Legge il valore locale e applica i controlli/fallback riportati nell’espressione.
    assert.deepEqual(await page.evaluate(key => JSON.parse(localStorage.getItem(key)), APPEARANCE_KEY), { theme, palette: p.id });
    // DOC L0026: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Legge il valore locale e applica i controlli/fallback riportati nell’espressione.
    assert.equal(await page.evaluate(() => localStorage.getItem('tabata:v1:data')), null);
    // DOC L0027: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Timer', exact: true }).click();» nel flusso: Prova browser automatizzata.
    await page.getByRole('button', { name: 'Timer', exact: true }).click();
    // DOC L0028: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.deepEqual(await page.evaluate(() => ({ ...document.documentElement.dataset })), { theme, palette: p.id });
    // DOC L0029: colors: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. c: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Restituisce il valore calcolato o conclude il percorso della funzione.
    const colors = await page.locator('#quick-start-button').evaluate(b => { const c = getComputedStyle(b); return { fill: c.backgroundColor, text: c.color }; });
    // DOC L0030: rgb: Passaggio «rgb» dello strumento: Prova browser automatizzata. Gli argomenti e le operazioni sono descritti nelle righe del corpo.
    const rgb = hex => `rgb(${hex.slice(1).match(/../g).map(x => parseInt(x, 16)).join(', ')})`;
    // DOC L0031: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.equal(colors.fill, rgb(p[theme][1])); assert.equal(colors.text, rgb(p[theme][2]));
    // DOC L0032: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.equal(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth), true);
    // DOC L0033: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
    if (['yellow', 'blue'].includes(p.id)) await page.screenshot({ path: `output/screenshots/web-${theme}-${p.id}.png`, fullPage: true });
  // DOC L0034: Chiude il costrutto aperto alla riga 20.
  }
  // DOC L0035: Esegue il passaggio esplicito «checks.push('Tutti i 18 abbinamenti tema/palette: colori effettivi, testo leggibile e archivio allenamenti invariato');» nel flusso: Prova browser automatizzata.
  checks.push('Tutti i 18 abbinamenti tema/palette: colori effettivi, testo leggibile e archivio allenamenti invariato');
  // DOC L0036: Esegue il passaggio esplicito «await page.reload();» nel flusso: Prova browser automatizzata.
  await page.reload();
  // DOC L0037: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();
  // DOC L0038: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.getByRole('button', { name: 'Scuro', exact: true }).getAttribute('aria-pressed'), 'true');
  // DOC L0039: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.getByRole('button', { name: 'Rosa', exact: true }).getAttribute('aria-pressed'), 'true');
  // DOC L0040: Esegue il passaggio esplicito «await page.setViewportSize({ width: 320, height: 800 });» nel flusso: Prova browser automatizzata.
  await page.setViewportSize({ width: 320, height: 800 });
  // DOC L0041: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth), true);
  // DOC L0042: Esegue il passaggio esplicito «await page.screenshot({ path: 'output/screenshots/web-dark-settings-320.png', fullPage: true });» nel flusso: Prova browser automatizzata.
  await page.screenshot({ path: 'output/screenshots/web-dark-settings-320.png', fullPage: true });
  // DOC L0043: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Allenamenti', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Allenamenti', exact: true }).click();
  // DOC L0044: Esegue il passaggio esplicito «await page.getByRole('button', { name: '+ Crea allenamento', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: '+ Crea allenamento', exact: true }).click();
  // DOC L0045: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth), true);
  // DOC L0046: Esegue il passaggio esplicito «await page.screenshot({ path: 'output/screenshots/web-dark-editor-320.png', fullPage: true });» nel flusso: Prova browser automatizzata.
  await page.screenshot({ path: 'output/screenshots/web-dark-editor-320.png', fullPage: true });
  // DOC L0047: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Torna agli allenamenti' }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Torna agli allenamenti' }).click();
  // DOC L0048: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Attività', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Attività', exact: true }).click();
  // DOC L0049: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Statistiche →', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Statistiche →', exact: true }).click();
  // DOC L0050: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.locator('svg rect').first().evaluate(r => getComputedStyle(r).fill), 'rgb(242, 154, 197)');
  // DOC L0051: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Guida', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Guida', exact: true }).click();
  // DOC L0052: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth), true);
  // DOC L0053: Esegue il passaggio esplicito «checks.push('Preferenze conservate al reload; impostazioni, editor, grafici e guida compatibili con tema scuro a 320 px');» nel flusso: Prova browser automatizzata.
  checks.push('Preferenze conservate al reload; impostazioni, editor, grafici e guida compatibili con tema scuro a 320 px');
  // DOC L0054: Esegue il passaggio esplicito «await page.evaluate(async () => { await navigator.serviceWorker.ready; });» nel flusso: Prova browser automatizzata.
  await page.evaluate(async () => { await navigator.serviceWorker.ready; });
  // DOC L0055: Esegue il passaggio esplicito «await page.reload(); await context.setOffline(true); await page.reload();» nel flusso: Prova browser automatizzata.
  await page.reload(); await context.setOffline(true); await page.reload();
  // DOC L0056: Esegue il passaggio esplicito «await page.getByRole('heading', { name: 'Tabata Timer' }).waitFor();» nel flusso: Prova browser automatizzata.
  await page.getByRole('heading', { name: 'Tabata Timer' }).waitFor();
  // DOC L0057: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.evaluate(() => document.documentElement.dataset.theme), 'dark');
  // DOC L0058: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Impostazioni', exact: true }).click();
  // DOC L0059: Esegue il passaggio esplicito «await page.getByRole('button', { name: 'Verde chiaro', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await page.getByRole('button', { name: 'Verde chiaro', exact: true }).click();
  // DOC L0060: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await page.evaluate(() => document.documentElement.dataset.palette), 'lime');
  // DOC L0061: Esegue il passaggio esplicito «checks.push('Aspetto disponibile e modificabile anche offline');» nel flusso: Prova browser automatizzata.
  checks.push('Aspetto disponibile e modificabile anche offline');
  // DOC L0062: Esegue il passaggio esplicito «await context.close();» nel flusso: Prova browser automatizzata.
  await context.close();
  // DOC L0063: denied: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const denied = await browser.newContext({ locale: 'it-IT' });
  // DOC L0064: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. set: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Restituisce il valore calcolato o conclude il percorso della funzione.
  await denied.addInitScript(key => { const set = Storage.prototype.setItem; Storage.prototype.setItem = function (k, v) { if (k === key) throw new Error('denied'); return set.call(this, k, v); }; }, APPEARANCE_KEY);
  // DOC L0065: blocked: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const blocked = await denied.newPage(); await blocked.goto(base);
  // DOC L0066: Esegue il passaggio esplicito «await blocked.getByRole('button', { name: 'Impostazioni', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await blocked.getByRole('button', { name: 'Impostazioni', exact: true }).click();
  // DOC L0067: Esegue il passaggio esplicito «await blocked.getByRole('button', { name: 'Scuro', exact: true }).click();» nel flusso: Prova browser automatizzata.
  await blocked.getByRole('button', { name: 'Scuro', exact: true }).click();
  // DOC L0068: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await blocked.evaluate(() => document.documentElement.dataset.theme), 'dark');
  // DOC L0069: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.match(await blocked.locator('#notice').textContent(), /non ha potuto salvare/);
  // DOC L0070: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await blocked.evaluate(() => localStorage.length), 0);
  // DOC L0071: Esegue il passaggio esplicito «await denied.close();» nel flusso: Prova browser automatizzata.
  await denied.close();
  // DOC L0072: Esegue il passaggio esplicito «checks.push('Storage negato: tema applicato alla visita e avviso, senza falso salvataggio');» nel flusso: Prova browser automatizzata.
  checks.push('Storage negato: tema applicato alla visita e avviso, senza falso salvataggio');
  // DOC L0073: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.deepEqual(errors, []);
  // DOC L0074: Esegue il passaggio esplicito «await writeFile('output/browser-appearance.json', JSON.stringify({ checks, errors }, null, 2));» nel flusso: Prova browser automatizzata.
  await writeFile('output/browser-appearance.json', JSON.stringify({ checks, errors }, null, 2));
  // DOC L0075: Esegue il passaggio esplicito «console.log(checks.join('\n'));» nel flusso: Prova browser automatizzata.
  console.log(checks.join('\n'));
// DOC L0076: Esegue il passaggio esplicito «} finally { await browser.close(); }» nel flusso: Prova browser automatizzata.
} finally { await browser.close(); }
