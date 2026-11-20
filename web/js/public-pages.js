import { getLanguage, setLanguage, supportedLanguage } from './i18n.js';
import { loadAppearance, applyAppearance } from './appearance.js';
import { siteLinksView } from './site-links.js';

let storage;
try { storage = localStorage; } catch { /* Public information also works without storage. */ }
applyAppearance(loadAppearance(storage));
const url = new URL(location.href), requested = url.searchParams.get('lang');
if (supportedLanguage(requested)) setLanguage(requested);
if (url.searchParams.has('lang')) { url.searchParams.delete('lang'); history.replaceState(null, '', url); }
function render() {
  const language = getLanguage();
  document.documentElement.lang = language;
  document.querySelectorAll('[data-language-content]').forEach(section => { section.hidden = section.dataset.languageContent !== language; });
  document.querySelectorAll('[data-site-language]').forEach(button => { button.setAttribute('aria-pressed', String(button.dataset.siteLanguage === language)); });
  document.querySelector('[data-site-links]').innerHTML = siteLinksView();
}
document.querySelector('[data-language-picker]').addEventListener('click', event => {
  const button = event.target.closest('[data-site-language]');
  if (button) { setLanguage(button.dataset.siteLanguage); render(); }
});
render();
if ('serviceWorker' in navigator) navigator.serviceWorker.register('./sw.js').catch(() => {});
