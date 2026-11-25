// Appearance is a device preference, separate from the workout archive.
export const APPEARANCE_KEY = 'tabata:v1:appearance';
export const DEFAULT_APPEARANCE = { theme: 'light', palette: 'purple', style: 'classic' };
export const STYLES = [
  { id: 'classic', name: 'Classico' },
  { id: 'editorial', name: 'Editoriale sportivo' },
  { id: 'neo-brutalism', name: 'Neo-brutalismo soft' },
  { id: 'retro', name: 'Retro digitale' },
  { id: 'bauhaus', name: 'Bauhaus contemporaneo' }
];
export const PALETTES = [
  { id: 'purple', name: 'Viola', swatch: '#8650a7', light: ['#713b91', '#8650a7', '#ffffff'], dark: ['#d5adf2', '#bf8ce0', '#23122b'] },
  { id: 'blue', name: 'Blu', swatch: '#275fc0', light: ['#2456a4', '#275fc0', '#ffffff'], dark: ['#a5c8ff', '#8cb5ff', '#102344'] },
  { id: 'lime', name: 'Verde chiaro', swatch: '#a4e35b', light: ['#386518', '#a4e35b', '#20320c'], dark: ['#b4ed7a', '#a4e35b', '#20320c'] },
  { id: 'green', name: 'Verde scuro', swatch: '#21734a', light: ['#19603d', '#21734a', '#ffffff'], dark: ['#8bdbad', '#71c695', '#0b2e1b'] },
  { id: 'sky', name: 'Azzurro', swatch: '#69d5f0', light: ['#006378', '#69d5f0', '#08313d'], dark: ['#8ee1f6', '#69d5f0', '#08313d'] },
  { id: 'orange', name: 'Arancione', swatch: '#ff9c3d', light: ['#9a4708', '#ff9c3d', '#3c1e04'], dark: ['#ffbb7b', '#ff9c3d', '#3c1e04'] },
  { id: 'yellow', name: 'Giallo', swatch: '#ffe000', light: ['#746000', '#ffe000', '#302800'], dark: ['#ffe85b', '#ffe000', '#302800'] },
  { id: 'red', name: 'Rosso', swatch: '#c52d3d', light: ['#b02a35', '#c52d3d', '#ffffff'], dark: ['#ffabb2', '#ff8996', '#3b1017'] },
  { id: 'pink', name: 'Rosa', swatch: '#ee8bb8', light: ['#9b275d', '#ee8bb8', '#3a1024'], dark: ['#ffabd3', '#f29ac5', '#3a1024'] }
];
export function normalizeAppearance(value) {
  return {
    theme: value?.theme === 'dark' ? 'dark' : 'light',
    palette: PALETTES.some(p => p.id === value?.palette) ? value.palette : DEFAULT_APPEARANCE.palette,
    style: STYLES.some(style => style.id === value?.style) ? value.style : DEFAULT_APPEARANCE.style
  };
}
export function loadAppearance(storage, warn = () => {}) {
  try { return normalizeAppearance(JSON.parse(storage?.getItem(APPEARANCE_KEY) || 'null')); }
  catch { warn('Preferenze di aspetto non disponibili. Puoi scegliere di nuovo tema e colori.'); return { ...DEFAULT_APPEARANCE }; }
}
export function saveAppearance(storage, value, warn = () => {}) {
  try { storage.setItem(APPEARANCE_KEY, JSON.stringify(normalizeAppearance(value))); return true; }
  catch { warn('Aspetto aggiornato per questa visita. Il browser non ha potuto salvare la preferenza.'); return false; }
}
export function applyAppearance(value, root = document.documentElement) {
  const prefs = normalizeAppearance(value), palette = PALETTES.find(p => p.id === prefs.palette);
  const [accent, fill, onAccent] = palette[prefs.theme];
  root.dataset.theme = prefs.theme; root.dataset.palette = prefs.palette; root.dataset.style = prefs.style;
  for (const [key, color] of Object.entries({ accent, 'accent-dark': accent, 'accent-fill': fill, 'on-accent': onAccent, soft: accent + (prefs.theme === 'dark' ? '22' : '0e') })) root.style.setProperty('--' + key, color);
}
