// Appearance is a device preference, separate from the workout archive.
// DOC L0002: APPEARANCE_KEY: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
export const APPEARANCE_KEY = 'tabata:v1:appearance';
// DOC L0003: DEFAULT_APPEARANCE: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
export const DEFAULT_APPEARANCE = { theme: 'light', palette: 'purple' };
// DOC L0004: PALETTES: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
export const PALETTES = [
  // DOC L0005: Esegue il passaggio esplicito «{ id: 'purple', name: 'Viola', swatch: '#8650a7', light: ['#713b91', '#8650a7', '#ffffff'], dark: ['#d5adf2', '#bf8ce0', '#23122b'] },» nel flusso: Preferenze locali di tema e palette web.
  { id: 'purple', name: 'Viola', swatch: '#8650a7', light: ['#713b91', '#8650a7', '#ffffff'], dark: ['#d5adf2', '#bf8ce0', '#23122b'] },
  // DOC L0006: Esegue il passaggio esplicito «{ id: 'blue', name: 'Blu', swatch: '#275fc0', light: ['#2456a4', '#275fc0', '#ffffff'], dark: ['#a5c8ff', '#8cb5ff', '#102344'] },» nel flusso: Preferenze locali di tema e palette web.
  { id: 'blue', name: 'Blu', swatch: '#275fc0', light: ['#2456a4', '#275fc0', '#ffffff'], dark: ['#a5c8ff', '#8cb5ff', '#102344'] },
  // DOC L0007: Esegue il passaggio esplicito «{ id: 'lime', name: 'Verde chiaro', swatch: '#a4e35b', light: ['#386518', '#a4e35b', '#20320c'], dark: ['#b4ed7a', '#a4e35b', '#20320c'] },» nel flusso: Preferenze locali di tema e palette web.
  { id: 'lime', name: 'Verde chiaro', swatch: '#a4e35b', light: ['#386518', '#a4e35b', '#20320c'], dark: ['#b4ed7a', '#a4e35b', '#20320c'] },
  // DOC L0008: Esegue il passaggio esplicito «{ id: 'green', name: 'Verde scuro', swatch: '#21734a', light: ['#19603d', '#21734a', '#ffffff'], dark: ['#8bdbad', '#71c695', '#0b2e1b'] },» nel flusso: Preferenze locali di tema e palette web.
  { id: 'green', name: 'Verde scuro', swatch: '#21734a', light: ['#19603d', '#21734a', '#ffffff'], dark: ['#8bdbad', '#71c695', '#0b2e1b'] },
  // DOC L0009: Esegue il passaggio esplicito «{ id: 'sky', name: 'Azzurro', swatch: '#69d5f0', light: ['#006378', '#69d5f0', '#08313d'], dark: ['#8ee1f6', '#69d5f0', '#08313d'] },» nel flusso: Preferenze locali di tema e palette web.
  { id: 'sky', name: 'Azzurro', swatch: '#69d5f0', light: ['#006378', '#69d5f0', '#08313d'], dark: ['#8ee1f6', '#69d5f0', '#08313d'] },
  // DOC L0010: Esegue il passaggio esplicito «{ id: 'orange', name: 'Arancione', swatch: '#ff9c3d', light: ['#9a4708', '#ff9c3d', '#3c1e04'], dark: ['#ffbb7b', '#ff9c3d', '#3c1e04'] },» nel flusso: Preferenze locali di tema e palette web.
  { id: 'orange', name: 'Arancione', swatch: '#ff9c3d', light: ['#9a4708', '#ff9c3d', '#3c1e04'], dark: ['#ffbb7b', '#ff9c3d', '#3c1e04'] },
  // DOC L0011: Esegue il passaggio esplicito «{ id: 'yellow', name: 'Giallo', swatch: '#ffe000', light: ['#746000', '#ffe000', '#302800'], dark: ['#ffe85b', '#ffe000', '#302800'] },» nel flusso: Preferenze locali di tema e palette web.
  { id: 'yellow', name: 'Giallo', swatch: '#ffe000', light: ['#746000', '#ffe000', '#302800'], dark: ['#ffe85b', '#ffe000', '#302800'] },
  // DOC L0012: Esegue il passaggio esplicito «{ id: 'red', name: 'Rosso', swatch: '#c52d3d', light: ['#b02a35', '#c52d3d', '#ffffff'], dark: ['#ffabb2', '#ff8996', '#3b1017'] },» nel flusso: Preferenze locali di tema e palette web.
  { id: 'red', name: 'Rosso', swatch: '#c52d3d', light: ['#b02a35', '#c52d3d', '#ffffff'], dark: ['#ffabb2', '#ff8996', '#3b1017'] },
  // DOC L0013: Esegue il passaggio esplicito «{ id: 'pink', name: 'Rosa', swatch: '#ee8bb8', light: ['#9b275d', '#ee8bb8', '#3a1024'], dark: ['#ffabd3', '#f29ac5', '#3a1024'] }» nel flusso: Preferenze locali di tema e palette web.
  { id: 'pink', name: 'Rosa', swatch: '#ee8bb8', light: ['#9b275d', '#ee8bb8', '#3a1024'], dark: ['#ffabd3', '#f29ac5', '#3a1024'] }
// DOC L0014: Chiude un blocco, una chiamata o un valore composto; non aggiunge una nuova operazione.
];
// DOC L0015: normalizeAppearance: Accetta solo tema e palette conosciuti e sostituisce preferenze mancanti o non valide.
export function normalizeAppearance(value) {
  // DOC L0016: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di normalizeAppearance.
  return {
    // DOC L0017: Esegue il passaggio esplicito «theme: value?.theme === 'dark' ? 'dark' : 'light',» nel flusso: Preferenze locali di tema e palette web. Nel contesto di normalizeAppearance.
    theme: value?.theme === 'dark' ? 'dark' : 'light',
    // DOC L0018: Esegue il passaggio esplicito «palette: PALETTES.some(p => p.id === value?.palette) ? value.palette : 'purple'» nel flusso: Preferenze locali di tema e palette web.
    palette: PALETTES.some(p => p.id === value?.palette) ? value.palette : 'purple'
  // DOC L0019: Chiude il costrutto aperto alla riga 16.
  };
// DOC L0020: Chiude il costrutto aperto alla riga 15.
}
// DOC L0021: loadAppearance: Legge le preferenze separate dall’archivio, con valori predefiniti in caso di errore.
export function loadAppearance(storage, warn = () => {}) {
  // DOC L0022: Legge il valore locale e applica i controlli/fallback riportati nell’espressione. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di loadAppearance.
  try { return normalizeAppearance(JSON.parse(storage?.getItem(APPEARANCE_KEY) || 'null')); }
  // DOC L0023: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di loadAppearance.
  catch { warn('Preferenze di aspetto non disponibili. Puoi scegliere di nuovo tema e colori.'); return { ...DEFAULT_APPEARANCE }; }
// DOC L0024: Chiude il costrutto aperto alla riga 21.
}
// DOC L0025: saveAppearance: Salva solo tema e palette e segnala l’eventuale fallimento di persistenza.
export function saveAppearance(storage, value, warn = () => {}) {
  // DOC L0026: Scrive la preferenza o la copia locale prevista, senza inviarla a un server. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di saveAppearance.
  try { storage.setItem(APPEARANCE_KEY, JSON.stringify(normalizeAppearance(value))); return true; }
  // DOC L0027: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di saveAppearance.
  catch { warn('Aspetto aggiornato per questa visita. Il browser non ha potuto salvare la preferenza.'); return false; }
// DOC L0028: Chiude il costrutto aperto alla riga 25.
}
// DOC L0029: applyAppearance: Applica tema, palette e variabili CSS alla radice del documento.
export function applyAppearance(value, root = document.documentElement) {
  // DOC L0030: prefs: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const prefs = normalizeAppearance(value), palette = PALETTES.find(p => p.id === prefs.palette);
  // DOC L0031: Esegue il passaggio esplicito «const [accent, fill, onAccent] = palette[prefs.theme];» nel flusso: Preferenze locali di tema e palette web. Nel contesto di applyAppearance.
  const [accent, fill, onAccent] = palette[prefs.theme];
  // DOC L0032: Esegue il passaggio esplicito «root.dataset.theme = prefs.theme; root.dataset.palette = prefs.palette;» nel flusso: Preferenze locali di tema e palette web. Nel contesto di applyAppearance.
  root.dataset.theme = prefs.theme; root.dataset.palette = prefs.palette;
  // DOC L0033: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di applyAppearance.
  for (const [key, color] of Object.entries({ accent, 'accent-dark': accent, 'accent-fill': fill, 'on-accent': onAccent, soft: accent + (prefs.theme === 'dark' ? '22' : '0e') })) root.style.setProperty('--' + key, color);
// DOC L0034: Chiude il costrutto aperto alla riga 29.
}
