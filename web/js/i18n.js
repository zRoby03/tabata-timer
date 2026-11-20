// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { messages } from './messages.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { messages } from './messages.js';
// DOC L0002: LANGUAGE_KEY: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
export const LANGUAGE_KEY = 'tabata:language';
// DOC L0003: LANGUAGES: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
export const LANGUAGES = [{ id: 'it', name: 'Italiano' }, { id: 'en', name: 'English' }, { id: 'es', name: 'Español' }];
// DOC L0004: supportedLanguage: Accetta solo italiano, inglese e spagnolo.
export const supportedLanguage = value => LANGUAGES.some(l => l.id === value);
// DOC L0005: loadLanguage: Preferisce la lingua salvata, poi quelle del dispositivo, infine l’italiano.
export function loadLanguage(storage, preferred = ['it']) {
  // DOC L0006: Legge il valore locale e applica i controlli/fallback riportati nell’espressione. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. saved: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di loadLanguage.
  try { const saved = storage?.getItem(LANGUAGE_KEY); if (supportedLanguage(saved)) return saved; } catch { /* Temporary language still works. */ }
  // DOC L0007: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. value: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. base: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di loadLanguage.
  for (const value of preferred) { const base = String(value).toLowerCase().split(/[-_]/)[0]; if (supportedLanguage(base)) return base; }
  // DOC L0008: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di loadLanguage.
  return 'it';
// DOC L0009: Chiude il costrutto aperto alla riga 5.
}
// DOC L0010: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. storage: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
let storage; try { if (typeof window !== 'undefined') storage = window.localStorage; } catch { /* Denied browser storage. */ }
// DOC L0011: language: lingua corrente, mantenuta separata dall’archivio.
let language = loadLanguage(storage, typeof window === 'undefined' ? ['it'] : navigator.languages || [navigator.language]);
// DOC L0012: getLanguage: Espone la lingua attualmente selezionata.
export const getLanguage = () => language;
// DOC L0013: locale: Restituisce la locale usata per date e numeri nella lingua corrente.
export const locale = () => ({ it: 'it-IT', en: 'en-GB', es: 'es-ES' }[language]);
// DOC L0014: setLanguage: Cambia la lingua e prova a conservarla, continuando a funzionare se lo storage non è disponibile.
export function setLanguage(value) {
  // DOC L0015: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di setLanguage.
  if (!supportedLanguage(value)) return false;
  // DOC L0016: Esegue il passaggio esplicito «language = value;» nel flusso: Localizzazione browser e scelta della lingua. Nel contesto di setLanguage.
  language = value;
  // DOC L0017: Scrive la preferenza o la copia locale prevista, senza inviarla a un server. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di setLanguage.
  try { storage?.setItem(LANGUAGE_KEY, value); return true; } catch { return false; }
// DOC L0018: Chiude il costrutto aperto alla riga 14.
}
// Explicit placeholders keep user-entered names intact. Legacy phase/error strings are adapted at display boundaries.
// DOC L0020: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. patternMessages: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const patternMessages = Object.keys(messages).filter(k => /\{\d+\}/.test(k) && /[A-Za-zÀ-ÿ]/.test(k.replace(/\{\d+\}/g, ''))).sort((a, b) => b.length - a.length).map(source => {
  // DOC L0021: indices: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const indices = [];
  // DOC L0022: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. pattern: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const pattern = source.split(/(\{\d+\})/).map(part => /^\{\d+\}$/.test(part) ? (indices.push(Number(part.slice(1, -1))), '([\\s\\S]*?)') : part.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')).join('');
  // DOC L0023: Restituisce il valore calcolato o conclude il percorso della funzione.
  return { source, indices, regex: new RegExp(`^${pattern}$`) };
// DOC L0024: Chiude il costrutto aperto alla riga 20.
});
// DOC L0025: translate: Traduce messaggi e segnaposto di sistema, lasciando i nomi utente come valori letterali.
export function translate(source, lang, ...values) {
  // DOC L0026: Esegue il passaggio esplicito «source = String(source ?? '');» nel flusso: Localizzazione browser e scelta della lingua. Nel contesto di translate.
  source = String(source ?? '');
  // DOC L0027: row: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di translate.
  const row = messages[source];
  // DOC L0028: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di translate.
  if (row) {
    // DOC L0029: result: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di translate.
    const result = lang === 'en' ? row[0] : lang === 'es' ? row[1] : row[2] ?? source;
    // DOC L0030: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di translate.
    if (values.length && (source.includes(': {0}') || source.startsWith('{0}:'))) values[0] = translate(values[0], lang);
    // DOC L0031: Restituisce il valore calcolato o conclude il percorso della funzione.
    return result.replace(/\{(\d+)\}/g, (match, index) => index < values.length ? String(values[index] ?? '') : match);
  // DOC L0032: Chiude il costrutto aperto alla riga 28.
  }
  // DOC L0033: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di translate.
  if (!values.length && lang !== 'it') for (const entry of patternMessages) {
    // DOC L0034: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. match: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di translate.
    const match = source.match(entry.regex); if (!match) continue;
    // DOC L0035: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. args: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    const args = []; entry.indices.forEach((index, n) => { args[index] = match[n + 1]; });
    // DOC L0036: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di translate.
    return translate(entry.source, lang, ...args);
  // DOC L0037: Chiude il costrutto aperto alla riga 33.
  }
  // DOC L0038: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di translate.
  return source;
// DOC L0039: Chiude il costrutto aperto alla riga 25.
}
// DOC L0040: t: Adatta la chiamata di traduzione alla lingua corrente.
export const t = (source, ...values) => translate(source, language, ...values);
// Only marked, application-owned nodes/attributes are localized. Form values and saved names are never traversed.
// DOC L0042: translateDocument: Traduce soltanto nodi e attributi marcati dall’app, senza attraversare valori dei moduli o nomi salvati.
export function translateDocument(root = document) {
  // DOC L0043: Esegue il passaggio esplicito «root.documentElement.lang = language;» nel flusso: Localizzazione browser e scelta della lingua. Nel contesto di translateDocument.
  root.documentElement.lang = language;
  // DOC L0044: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  root.querySelectorAll('[data-i18n]').forEach(node => { node.textContent = t(node.dataset.i18n); });
  // DOC L0045: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  for (const attribute of ['aria-label', 'title', 'placeholder', 'content']) root.querySelectorAll(`[data-i18n-${attribute}]`).forEach(node => node.setAttribute(attribute, t(node.getAttribute(`data-i18n-${attribute}`))));
// DOC L0046: Chiude il costrutto aperto alla riga 42.
}
