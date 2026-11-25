import { test } from 'node:test';
import assert from 'node:assert/strict';
import { APPEARANCE_KEY, PALETTES, STYLES, loadAppearance, saveAppearance, applyAppearance } from '../js/appearance.js';

test('aspetto predefinito senza scritture; vecchi archivi senza preferenze compatibili', () => {
  let writes = 0;
  const storage = { getItem: () => null, setItem() { writes++; } };
  assert.deepEqual(loadAppearance(storage), { theme: 'light', palette: 'purple', style: 'classic' }); assert.equal(writes, 0);
});
test('aspetto salvato separatamente senza alterare allenamenti o modalità temporanea', () => {
  const data = new Map([['tabata:v1:data', 'archivio invariato']]);
  const storage = { getItem: k => data.get(k), setItem: (k, v) => data.set(k, v) };
  assert.equal(saveAppearance(storage, { theme: 'dark', palette: 'yellow', style: 'bauhaus' }), true);
  assert.deepEqual(loadAppearance(storage), { theme: 'dark', palette: 'yellow', style: 'bauhaus' });
  assert.equal(data.get('tabata:v1:data'), 'archivio invariato'); assert.equal(data.size, 2);
});
test('preferenze corrotte o sconosciute e storage bloccato hanno un fallback', () => {
  const warnings = [];
  assert.deepEqual(loadAppearance({ getItem: () => '{' }, m => warnings.push(m)), { theme: 'light', palette: 'purple', style: 'classic' });
  assert.deepEqual(loadAppearance({ getItem: () => '{"theme":"other","palette":"unknown"}' }), { theme: 'light', palette: 'purple', style: 'classic' });
  assert.equal(saveAppearance({ setItem() { throw Error('denied'); } }, { theme: 'dark', palette: 'red' }, m => warnings.push(m)), false);
  assert.equal(warnings.length, 2);
});
function luminance(hex) {
  const rgb = hex.slice(1).match(/../g).map(x => parseInt(x, 16) / 255).map(x => x <= .04045 ? x / 12.92 : ((x + .055) / 1.055) ** 2.4);
  return rgb[0] * .2126 + rgb[1] * .7152 + rgb[2] * .0722;
}
function contrast(a, b) { const [low, high] = [luminance(a), luminance(b)].sort((x, y) => x - y); return (high + .05) / (low + .05); }
test('nove palette: contrasto del testo almeno 4.5:1 in entrambi i temi', () => {
  assert.deepEqual(PALETTES.map(p => p.name), ['Viola', 'Blu', 'Verde chiaro', 'Verde scuro', 'Azzurro', 'Arancione', 'Giallo', 'Rosso', 'Rosa']);
  for (const p of PALETTES) for (const theme of ['light', 'dark']) {
    const [accent, fill, onFill] = p[theme], surface = theme === 'dark' ? '#1c222c' : '#ffffff';
    assert.ok(contrast(fill, onFill) >= 4.5, `${p.name} ${theme}: pulsanti`);
    assert.ok(contrast(accent, surface) >= 4.5, `${p.name} ${theme}: testo colorato`);
  }
});
test('stile predefinito per preferenze legacy e stile sconosciuto senza perdere tema o palette', () => {
  assert.deepEqual(STYLES.map(style => style.name), ['Classico', 'Editoriale sportivo', 'Neo-brutalismo soft', 'Retro digitale', 'Bauhaus contemporaneo']);
  for (const saved of [{ theme: 'dark', palette: 'yellow' }, { theme: 'dark', palette: 'yellow', style: 'unknown' }]) {
    assert.deepEqual(loadAppearance({ getItem: () => JSON.stringify(saved) }), { theme: 'dark', palette: 'yellow', style: 'classic' });
  }
});
test('stile, tema e colori vengono applicati senza toccare contenuto e stato del timer', () => {
  const root = { dataset: {}, style: { setProperty(k, v) { this[k] = v; } } };
  applyAppearance({ theme: 'dark', palette: 'yellow', style: 'bauhaus' }, root);
  assert.deepEqual(root.dataset, { theme: 'dark', palette: 'yellow', style: 'bauhaus' });
  assert.equal(root.style['--accent-fill'], '#ffe000'); assert.equal(root.style['--on-accent'], '#302800');
});
