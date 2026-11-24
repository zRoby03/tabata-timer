import { messages } from './messages.js';
export const LANGUAGE_KEY = 'tabata:language';
export const LANGUAGES = [{ id: 'it', name: 'Italiano' }, { id: 'en', name: 'English' }, { id: 'es', name: 'Español' }];
export const supportedLanguage = value => LANGUAGES.some(l => l.id === value);
export function loadLanguage(storage, preferred = ['it']) {
  try { const saved = storage?.getItem(LANGUAGE_KEY); if (supportedLanguage(saved)) return saved; } catch { /* Temporary language still works. */ }
  for (const value of preferred) { const base = String(value).toLowerCase().split(/[-_]/)[0]; if (supportedLanguage(base)) return base; }
  return 'it';
}
let storage; try { if (typeof window !== 'undefined') storage = window.localStorage; } catch { /* Denied browser storage. */ }
let language = loadLanguage(storage, typeof window === 'undefined' ? ['it'] : navigator.languages || [navigator.language]);
export const getLanguage = () => language;
export const locale = () => ({ it: 'it-IT', en: 'en-GB', es: 'es-ES' }[language]);
export function setLanguage(value) {
  if (!supportedLanguage(value)) return false;
  language = value;
  try { storage?.setItem(LANGUAGE_KEY, value); return true; } catch { return false; }
}
// Explicit placeholders keep user-entered names intact. Legacy phase/error strings are adapted at display boundaries.
const patternMessages = Object.keys(messages).filter(k => /\{\d+\}/.test(k) && /[A-Za-zÀ-ÿ]/.test(k.replace(/\{\d+\}/g, ''))).sort((a, b) => b.length - a.length).map(source => {
  const indices = [];
  const pattern = source.split(/(\{\d+\})/).map(part => /^\{\d+\}$/.test(part) ? (indices.push(Number(part.slice(1, -1))), '([\\s\\S]*?)') : part.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')).join('');
  return { source, indices, regex: new RegExp(`^${pattern}$`) };
});
export function translate(source, lang, ...values) {
  source = String(source ?? '');
  const row = messages[source];
  if (row) {
    const result = lang === 'en' ? row[0] : lang === 'es' ? row[1] : row[2] ?? source;
    if (values.length && (source.includes(': {0}') || source.startsWith('{0}:'))) values[0] = translate(values[0], lang);
    return result.replace(/\{(\d+)\}/g, (match, index) => index < values.length ? String(values[index] ?? '') : match);
  }
  if (!values.length && lang !== 'it') for (const entry of patternMessages) {
    const match = source.match(entry.regex); if (!match) continue;
    const args = []; entry.indices.forEach((index, n) => { args[index] = match[n + 1]; });
    return translate(entry.source, lang, ...args);
  }
  return source;
}
export const t = (source, ...values) => translate(source, language, ...values);
export const phaseLabel = phase => t(phase.labelKey ?? phase.label, ...(phase.labelArgs ?? []));
// Only marked, application-owned nodes/attributes are localized. Form values and saved names are never traversed.
export function translateDocument(root = document) {
  root.documentElement.lang = language;
  root.querySelectorAll('[data-i18n]').forEach(node => { node.textContent = t(node.dataset.i18n); });
  for (const attribute of ['aria-label', 'title', 'placeholder', 'content']) root.querySelectorAll(`[data-i18n-${attribute}]`).forEach(node => node.setAttribute(attribute, t(node.getAttribute(`data-i18n-${attribute}`))));
}
