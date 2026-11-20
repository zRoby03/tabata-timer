// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { getLanguage, setLanguage, supportedLanguage } from './i18n.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { getLanguage, setLanguage, supportedLanguage } from './i18n.js';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import { loadAppearance, applyAppearance } from './appearance.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { loadAppearance, applyAppearance } from './appearance.js';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import { siteLinksView } from './site-links.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { siteLinksView } from './site-links.js';

// DOC L0005: storage: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
let storage;
// DOC L0006: Esegue il passaggio esplicito «try { storage = localStorage; } catch { /* Public information also works without storage. * / }» nel flusso: Lingua e aspetto delle pagine pubbliche Informazioni/Privacy.
try { storage = localStorage; } catch { /* Public information also works without storage. */ }
// DOC L0007: Esegue il passaggio esplicito «applyAppearance(loadAppearance(storage));» nel flusso: Lingua e aspetto delle pagine pubbliche Informazioni/Privacy.
applyAppearance(loadAppearance(storage));
// DOC L0008: url: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const url = new URL(location.href), requested = url.searchParams.get('lang');
// DOC L0009: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
if (supportedLanguage(requested)) setLanguage(requested);
// DOC L0010: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
if (url.searchParams.has('lang')) { url.searchParams.delete('lang'); history.replaceState(null, '', url); }
// DOC L0011: render: Mostra l’articolo della lingua scelta e aggiorna pulsanti e collegamenti delle pagine pubbliche.
function render() {
  // DOC L0012: language: lingua corrente, mantenuta separata dall’archivio. Nel contesto di render.
  const language = getLanguage();
  // DOC L0013: Esegue il passaggio esplicito «document.documentElement.lang = language;» nel flusso: Lingua e aspetto delle pagine pubbliche Informazioni/Privacy. Nel contesto di render.
  document.documentElement.lang = language;
  // DOC L0014: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  document.querySelectorAll('[data-language-content]').forEach(section => { section.hidden = section.dataset.languageContent !== language; });
  // DOC L0015: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  document.querySelectorAll('[data-site-language]').forEach(button => { button.setAttribute('aria-pressed', String(button.dataset.siteLanguage === language)); });
  // DOC L0016: Genera o aggiorna HTML della UI; i dati utente devono passare attraverso l’escaping previsto dal modulo. Nel contesto di render.
  document.querySelector('[data-site-links]').innerHTML = siteLinksView();
// DOC L0017: Chiude il costrutto aperto alla riga 11.
}
// DOC L0018: Registra il gestore dell’evento indicato; il corpo viene eseguito quando l’evento si verifica.
document.querySelector('[data-language-picker]').addEventListener('click', event => {
  // DOC L0019: button: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const button = event.target.closest('[data-site-language]');
  // DOC L0020: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
  if (button) { setLanguage(button.dataset.siteLanguage); render(); }
// DOC L0021: Chiude il costrutto aperto alla riga 18.
});
// DOC L0022: Esegue il passaggio esplicito «render();» nel flusso: Lingua e aspetto delle pagine pubbliche Informazioni/Privacy.
render();
// DOC L0023: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
if ('serviceWorker' in navigator) navigator.serviceWorker.register('./sw.js').catch(() => {});
