const CACHE = 'tabata-static-workout-audio-20261124';
const ASSETS = ['./js/preview.js', './js/help-content.js', './audio/workout-ding-1.wav', './audio/workout-ding-2.wav', './audio/workout-ding-3.wav', './audio/rest-double.wav', './', './about.html', './privacy.html', './js/site-links.js', './js/public-pages.js', './index.html', './styles.css', './icon.svg', './icons/icon-192.png', './icons/icon-512.png', './manifest.webmanifest', './js/main.js', './js/i18n.js', './js/messages.js', './js/modes.js', './js/model.js', './js/dom.js', './js/editor.js', './js/timeline.js', './js/timer.js', './js/storage.js', './js/stats.js', './js/charts.js', './js/io.js', './js/quick.js', './js/study-fields.js', './js/countdown.js', './js/transitions.js', './js/help.js', './js/views.js', './js/appearance.js', './audio/work-start.wav', './audio/rest-start.wav', './audio/workout-finish.wav', './audio/countdown-work-3.wav', './audio/countdown-work-2.wav', './audio/countdown-work-1.wav', './audio/countdown-rest-3.wav', './audio/countdown-rest-2.wav', './audio/countdown-rest-1.wav'];
const ASSET_URLS = new Set(ASSETS.map(path => new URL(path, self.location.href).href));
// Pages serves HTML at extensionless URLs; both forms use the same offline copy.
const STATIC_PAGES = new Map(['about', 'privacy'].flatMap(name => {
  const cached = new URL(`./${name}.html`, self.location.href).href;
  return [name, `${name}.html`].map(path => [new URL(`./${path}`, self.location.href).pathname, cached]);
}));
self.addEventListener('install', event => { event.waitUntil(caches.open(CACHE).then(cache => cache.addAll(ASSETS))); });
self.addEventListener('activate', event => { event.waitUntil(caches.keys().then(keys => Promise.all(keys.filter(key => key.startsWith('tabata-static-') && key !== CACHE).map(key => caches.delete(key)))).then(() => self.clients.claim())); });
self.addEventListener('fetch', event => {
  if (event.request.method !== 'GET' || new URL(event.request.url).origin !== self.location.origin) return;
  event.respondWith(fetch(event.request).then(response => {
    if (response.ok && ASSET_URLS.has(event.request.url)) event.waitUntil(caches.open(CACHE).then(cache => cache.put(event.request, response.clone())));
    return response;
  }).catch(() => caches.match(STATIC_PAGES.get(new URL(event.request.url).pathname) || event.request).then(cached => cached || (event.request.mode === 'navigate' ? caches.match('./index.html') : Response.error()))));
});
