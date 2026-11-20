// DOC L0001: CACHE: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const CACHE = 'tabata-static-v1.8.2-pages';
// DOC L0002: ASSETS: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const ASSETS = ['./', './about.html', './privacy.html', './js/site-links.js', './js/public-pages.js', './index.html', './styles.css', './icon.svg', './icons/icon-192.png', './icons/icon-512.png', './manifest.webmanifest', './js/main.js', './js/i18n.js', './js/messages.js', './js/modes.js', './js/model.js', './js/dom.js', './js/editor.js', './js/timeline.js', './js/timer.js', './js/storage.js', './js/stats.js', './js/charts.js', './js/io.js', './js/quick.js', './js/study-fields.js', './js/countdown.js', './js/transitions.js', './js/help.js', './js/views.js', './js/appearance.js', './audio/work-start.wav', './audio/rest-start.wav', './audio/workout-finish.wav', './audio/countdown-work-3.wav', './audio/countdown-work-2.wav', './audio/countdown-work-1.wav', './audio/countdown-rest-3.wav', './audio/countdown-rest-2.wav', './audio/countdown-rest-1.wav'];
// DOC L0003: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. ASSET_URLS: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const ASSET_URLS = new Set(ASSETS.map(path => new URL(path, self.location.href).href));
// Pages serves HTML at extensionless URLs; both forms use the same offline copy.
// DOC L0005: STATIC_PAGES: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const STATIC_PAGES = new Map(['about', 'privacy'].flatMap(name => {
  // DOC L0006: cached: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const cached = new URL(`./${name}.html`, self.location.href).href;
  // DOC L0007: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Restituisce il valore calcolato o conclude il percorso della funzione.
  return [name, `${name}.html`].map(path => [new URL(`./${path}`, self.location.href).pathname, cached]);
// DOC L0008: Chiude il costrutto aperto alla riga 5.
}));
// DOC L0009: Registra il gestore dell’evento indicato; il corpo viene eseguito quando l’evento si verifica.
self.addEventListener('install', event => { event.waitUntil(caches.open(CACHE).then(cache => cache.addAll(ASSETS))); });
// DOC L0010: Registra il gestore dell’evento indicato; il corpo viene eseguito quando l’evento si verifica. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
self.addEventListener('activate', event => { event.waitUntil(caches.keys().then(keys => Promise.all(keys.filter(key => key.startsWith('tabata-static-') && key !== CACHE).map(key => caches.delete(key)))).then(() => self.clients.claim())); });
// DOC L0011: Registra il gestore dell’evento indicato; il corpo viene eseguito quando l’evento si verifica.
self.addEventListener('fetch', event => {
  // DOC L0012: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione.
  if (event.request.method !== 'GET' || new URL(event.request.url).origin !== self.location.origin) return;
  // DOC L0013: Esegue il passaggio esplicito «event.respondWith(fetch(event.request).then(response => {» nel flusso: Service worker per la cache offline dei soli file statici.
  event.respondWith(fetch(event.request).then(response => {
    // DOC L0014: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
    if (response.ok && ASSET_URLS.has(event.request.url)) event.waitUntil(caches.open(CACHE).then(cache => cache.put(event.request, response.clone())));
    // DOC L0015: Restituisce il valore calcolato o conclude il percorso della funzione.
    return response;
  // DOC L0016: Esegue il passaggio esplicito «}).catch(() => caches.match(STATIC_PAGES.get(new URL(event.request.url).pathname) || event.request).then(cached => cached || (event.request.mode === 'navigate' ? caches.match('./in» nel flusso: Service worker per la cache offline dei soli file statici.
  }).catch(() => caches.match(STATIC_PAGES.get(new URL(event.request.url).pathname) || event.request).then(cached => cached || (event.request.mode === 'navigate' ? caches.match('./index.html') : Response.error()))));
// DOC L0017: Chiude il costrutto aperto alla riga 11.
});
