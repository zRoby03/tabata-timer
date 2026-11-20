import { test } from 'node:test';
import assert from 'node:assert/strict';
import { messages } from '../js/messages.js';
import { translate, loadLanguage, setLanguage, t, locale } from '../js/i18n.js';
import { newWorkout } from '../js/model.js';
import { compileTimeline, plannedMs } from '../js/timeline.js';
import { parseBackup, makeBackup } from '../js/io.js';

test('language: stored preference, device language, invalid/denied storage and fallback', () => {
  const store = { getItem: () => 'es' };
  assert.equal(loadLanguage(store, ['en-US']), 'es');
  assert.equal(loadLanguage(null, ['en-US']), 'en'); assert.equal(loadLanguage(null, ['es-MX']), 'es');
  assert.equal(loadLanguage({ getItem() { throw Error(); } }, ['it-IT']), 'it');
  assert.equal(loadLanguage({ getItem: () => 'unknown' }, ['de-DE']), 'it');
});
test('all messages have English and Spanish with matching placeholders', () => {
  const tokens = s => [...new Set(s.match(/\{\d+\}/g) || [])].sort();
  for (const [source, values] of Object.entries(messages)) for (const value of values) {
    assert.ok(typeof value === 'string' && value.length); assert.deepEqual(tokens(value), tokens(source), source);
  }
});
test('formatted phase labels preserve user-entered names in all languages', () => {
  for (const language of ['it', 'en', 'es']) {
    const label = translate('Attività 2 · Lavoro <mio> · sessione 1/4', language);
    assert.ok(label.includes('Lavoro <mio>'));
    assert.ok(label.includes(language === 'en' ? 'session 1/4' : language === 'es' ? 'sesión 1/4' : 'sessione 1/4'));
  }
  assert.equal(translate('Eliminare “Work · lavoro”? Le sessioni restano in cronologia.', 'en'), 'Delete “Work · lavoro”? Sessions stay in history.');
});
test('language changes never change times or stored program names', () => {
  setLanguage('it'); const w = newWorkout('STUDIO'); w.title = 'Il mio nome: Lavoro';
  const backup = makeBackup({ workouts: [w], sessions: [] });
  for (const language of ['en', 'es', 'it']) { setLanguage(language); assert.equal(plannedMs(compileTimeline(w)), 6900000); assert.equal(parseBackup(backup).workouts[0].title, w.title); }
  setLanguage('en'); assert.equal(t('Recupero'), 'Rest'); assert.equal(newWorkout('STUDIO').title, 'My study program'); assert.equal(locale(), 'en-GB'); setLanguage('it');
});
test('validation errors and guide terms are translated without changing identifiers', () => {
  assert.equal(translate('Concentrazione: intero tra 60 e 3600', 'es'), 'Concentración: número entero entre 60 y 3600');
  assert.equal(translate('Pausa (timer)', 'en'), 'Pause'); assert.equal(translate('Pausa (timer)', 'es'), 'Pausar'); assert.equal(translate('Pausa (timer)', 'it'), 'Pausa');
  assert.equal(translate('Termina il timer per cambiare modalità.', 'en'), 'Stop the timer to change modes.');
});
