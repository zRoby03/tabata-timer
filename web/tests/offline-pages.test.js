import test from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { runInNewContext } from 'node:vm';

test('offline: Cloudflare URL senza estensione e query mantengono la pagina corretta', async () => {
  const origin = 'https://example.pages.dev/', listeners = new Map();
  const pages = new Map(['index', 'about', 'privacy'].map(name => [new URL(`${name}.html`, origin).href, `${name} content`]));
  runInNewContext(readFileSync(new URL('../sw.js', import.meta.url), 'utf8'), {
    URL,
    self: { location: new URL(`${origin}sw.js`), addEventListener: (name, handler) => listeners.set(name, handler) },
    fetch: async () => { throw new Error('offline'); },
    caches: { match: async request => pages.get(new URL(typeof request === 'string' ? request : request.url, origin).href) },
    Response: { error: () => 'network error' }
  });
  for (const [path, expected] of [
    ['about', 'about'], ['about.html', 'about'], ['about?lang=en', 'about'],
    ['privacy', 'privacy'], ['privacy.html?lang=es', 'privacy'], ['privacy?lang=it', 'privacy'],
    ['?mode=studio', 'index']
  ]) {
    let response;
    listeners.get('fetch')({ request: { url: new URL(path, origin).href, method: 'GET', mode: 'navigate' }, respondWith: value => { response = value; } });
    assert.equal(await response, `${expected} content`, path);
  }
});
