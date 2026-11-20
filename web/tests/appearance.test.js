// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { test } from 'node:test';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { test } from 'node:test';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import assert from 'node:assert/strict';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import assert from 'node:assert/strict';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import { APPEARANCE_KEY, PALETTES, loadAppearance, saveAppearance, applyAppearance } from '../js/appearance.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { APPEARANCE_KEY, PALETTES, loadAppearance, saveAppearance, applyAppearance } from '../js/appearance.js';

// DOC L0005: Esegue il passaggio esplicito «test('aspetto predefinito senza scritture; vecchi archivi senza preferenze compatibili', () => {» nel flusso: Prove di regressione in appearance.test.js.
test('aspetto predefinito senza scritture; vecchi archivi senza preferenze compatibili', () => {
  // DOC L0006: writes: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  let writes = 0;
  // DOC L0007: getItem: Prepara o verifica il caso di regressione «get Item»; le asserzioni nel corpo specificano l’esito atteso. setItem: Prepara o verifica il caso di regressione «set Item»; le asserzioni nel corpo specificano l’esito atteso.
  const storage = { getItem: () => null, setItem() { writes++; } };
  // DOC L0008: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.deepEqual(loadAppearance(storage), { theme: 'light', palette: 'purple' }); assert.equal(writes, 0);
// DOC L0009: Chiude il costrutto aperto alla riga 5.
});
// DOC L0010: Esegue il passaggio esplicito «test('aspetto salvato separatamente senza alterare allenamenti o modalità temporanea', () => {» nel flusso: Prove di regressione in appearance.test.js.
test('aspetto salvato separatamente senza alterare allenamenti o modalità temporanea', () => {
  // DOC L0011: data: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const data = new Map([['tabata:v1:data', 'archivio invariato']]);
  // DOC L0012: getItem: Prepara o verifica il caso di regressione «get Item»; le asserzioni nel corpo specificano l’esito atteso. setItem: Prepara o verifica il caso di regressione «set Item»; le asserzioni nel corpo specificano l’esito atteso.
  const storage = { getItem: k => data.get(k), setItem: (k, v) => data.set(k, v) };
  // DOC L0013: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(saveAppearance(storage, { theme: 'dark', palette: 'yellow' }), true);
  // DOC L0014: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.deepEqual(loadAppearance(storage), { theme: 'dark', palette: 'yellow' });
  // DOC L0015: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(data.get('tabata:v1:data'), 'archivio invariato'); assert.equal(data.size, 2);
// DOC L0016: Chiude il costrutto aperto alla riga 10.
});
// DOC L0017: Esegue il passaggio esplicito «test('preferenze corrotte o sconosciute e storage bloccato hanno un fallback', () => {» nel flusso: Prove di regressione in appearance.test.js.
test('preferenze corrotte o sconosciute e storage bloccato hanno un fallback', () => {
  // DOC L0018: warnings: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const warnings = [];
  // DOC L0019: getItem: Prepara o verifica il caso di regressione «get Item»; le asserzioni nel corpo specificano l’esito atteso.
  assert.deepEqual(loadAppearance({ getItem: () => '{' }, m => warnings.push(m)), { theme: 'light', palette: 'purple' });
  // DOC L0020: getItem: Prepara o verifica il caso di regressione «get Item»; le asserzioni nel corpo specificano l’esito atteso.
  assert.deepEqual(loadAppearance({ getItem: () => '{"theme":"other","palette":"unknown"}' }), { theme: 'light', palette: 'purple' });
  // DOC L0021: setItem: Prepara o verifica il caso di regressione «set Item»; le asserzioni nel corpo specificano l’esito atteso.
  assert.equal(saveAppearance({ setItem() { throw Error('denied'); } }, { theme: 'dark', palette: 'red' }, m => warnings.push(m)), false);
  // DOC L0022: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(warnings.length, 2);
// DOC L0023: Chiude il costrutto aperto alla riga 17.
});
// DOC L0024: luminance: Prepara o verifica il caso di regressione «luminance»; le asserzioni nel corpo specificano l’esito atteso.
function luminance(hex) {
  // DOC L0025: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. rgb: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const rgb = hex.slice(1).match(/../g).map(x => parseInt(x, 16) / 255).map(x => x <= .04045 ? x / 12.92 : ((x + .055) / 1.055) ** 2.4);
  // DOC L0026: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di luminance.
  return rgb[0] * .2126 + rgb[1] * .7152 + rgb[2] * .0722;
// DOC L0027: Chiude il costrutto aperto alla riga 24.
}
// DOC L0028: contrast: Prepara o verifica il caso di regressione «contrast»; le asserzioni nel corpo specificano l’esito atteso.
function contrast(a, b) { const [low, high] = [luminance(a), luminance(b)].sort((x, y) => x - y); return (high + .05) / (low + .05); }
// DOC L0029: Esegue il passaggio esplicito «test('nove palette: contrasto del testo almeno 4.5:1 in entrambi i temi', () => {» nel flusso: Prove di regressione in appearance.test.js.
test('nove palette: contrasto del testo almeno 4.5:1 in entrambi i temi', () => {
  // DOC L0030: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  assert.deepEqual(PALETTES.map(p => p.name), ['Viola', 'Blu', 'Verde chiaro', 'Verde scuro', 'Azzurro', 'Arancione', 'Giallo', 'Rosso', 'Rosa']);
  // DOC L0031: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  for (const p of PALETTES) for (const theme of ['light', 'dark']) {
    // DOC L0032: Esegue il passaggio esplicito «const [accent, fill, onFill] = p[theme], surface = theme === 'dark' ? '#1c222c' : '#ffffff';» nel flusso: Prove di regressione in appearance.test.js.
    const [accent, fill, onFill] = p[theme], surface = theme === 'dark' ? '#1c222c' : '#ffffff';
    // DOC L0033: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.ok(contrast(fill, onFill) >= 4.5, `${p.name} ${theme}: pulsanti`);
    // DOC L0034: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.ok(contrast(accent, surface) >= 4.5, `${p.name} ${theme}: testo colorato`);
  // DOC L0035: Chiude il costrutto aperto alla riga 31.
  }
// DOC L0036: Chiude il costrutto aperto alla riga 29.
});
// DOC L0037: Esegue il passaggio esplicito «test('tema e colori vengono applicati senza toccare contenuto e stato del timer', () => {» nel flusso: Prove di regressione in appearance.test.js.
test('tema e colori vengono applicati senza toccare contenuto e stato del timer', () => {
  // DOC L0038: setProperty: Prepara o verifica il caso di regressione «set Property»; le asserzioni nel corpo specificano l’esito atteso.
  const root = { dataset: {}, style: { setProperty(k, v) { this[k] = v; } } };
  // DOC L0039: Esegue il passaggio esplicito «applyAppearance({ theme: 'dark', palette: 'yellow' }, root);» nel flusso: Prove di regressione in appearance.test.js.
  applyAppearance({ theme: 'dark', palette: 'yellow' }, root);
  // DOC L0040: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.deepEqual(root.dataset, { theme: 'dark', palette: 'yellow' });
  // DOC L0041: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(root.style['--accent-fill'], '#ffe000'); assert.equal(root.style['--on-accent'], '#302800');
// DOC L0042: Chiude il costrutto aperto alla riga 37.
});
