// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import test from 'node:test';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import test from 'node:test';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import assert from 'node:assert/strict';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import assert from 'node:assert/strict';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import { readFileSync } from 'node:fs';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { readFileSync } from 'node:fs';
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import { runInNewContext } from 'node:vm';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { runInNewContext } from 'node:vm';

// DOC L0006: Esegue il passaggio esplicito «test('offline: Cloudflare URL senza estensione e query mantengono la pagina corretta', async () => {» nel flusso: Prove di regressione in offline-pages.test.js.
test('offline: Cloudflare URL senza estensione e query mantengono la pagina corretta', async () => {
  // DOC L0007: origin: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const origin = 'https://example.pages.dev/', listeners = new Map();
  // DOC L0008: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. pages: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const pages = new Map(['index', 'about', 'privacy'].map(name => [new URL(`${name}.html`, origin).href, `${name} content`]));
  // DOC L0009: Esegue il passaggio esplicito «runInNewContext(readFileSync(new URL('../sw.js', import.meta.url), 'utf8'), {» nel flusso: Prove di regressione in offline-pages.test.js.
  runInNewContext(readFileSync(new URL('../sw.js', import.meta.url), 'utf8'), {
    // DOC L0010: Esegue il passaggio esplicito «URL,» nel flusso: Prove di regressione in offline-pages.test.js.
    URL,
    // DOC L0011: addEventListener: Prepara o verifica il caso di regressione «add Event Listener»; le asserzioni nel corpo specificano l’esito atteso.
    self: { location: new URL(`${origin}sw.js`), addEventListener: (name, handler) => listeners.set(name, handler) },
    // DOC L0012: fetch: Prepara o verifica il caso di regressione «fetch»; le asserzioni nel corpo specificano l’esito atteso.
    fetch: async () => { throw new Error('offline'); },
    // DOC L0013: match: Prepara o verifica il caso di regressione «match»; le asserzioni nel corpo specificano l’esito atteso.
    caches: { match: async request => pages.get(new URL(typeof request === 'string' ? request : request.url, origin).href) },
    // DOC L0014: error: Prepara o verifica il caso di regressione «error»; le asserzioni nel corpo specificano l’esito atteso.
    Response: { error: () => 'network error' }
  // DOC L0015: Chiude il costrutto aperto alla riga 9.
  });
  // DOC L0016: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  for (const [path, expected] of [
    // DOC L0017: Esegue il passaggio esplicito «['about', 'about'], ['about.html', 'about'], ['about?lang=en', 'about'],» nel flusso: Prove di regressione in offline-pages.test.js.
    ['about', 'about'], ['about.html', 'about'], ['about?lang=en', 'about'],
    // DOC L0018: Esegue il passaggio esplicito «['privacy', 'privacy'], ['privacy.html?lang=es', 'privacy'], ['privacy?lang=it', 'privacy'],» nel flusso: Prove di regressione in offline-pages.test.js.
    ['privacy', 'privacy'], ['privacy.html?lang=es', 'privacy'], ['privacy?lang=it', 'privacy'],
    // DOC L0019: Esegue il passaggio esplicito «['?mode=studio', 'index']» nel flusso: Prove di regressione in offline-pages.test.js.
    ['?mode=studio', 'index']
  // DOC L0020: Esegue il passaggio esplicito «]) {» nel flusso: Prove di regressione in offline-pages.test.js.
  ]) {
    // DOC L0021: response: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    let response;
    // DOC L0022: respondWith: Prepara o verifica il caso di regressione «respond With»; le asserzioni nel corpo specificano l’esito atteso.
    listeners.get('fetch')({ request: { url: new URL(path, origin).href, method: 'GET', mode: 'navigate' }, respondWith: value => { response = value; } });
    // DOC L0023: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.equal(await response, `${expected} content`, path);
  // DOC L0024: Chiude il costrutto aperto alla riga 20.
  }
// DOC L0025: Chiude il costrutto aperto alla riga 6.
});
