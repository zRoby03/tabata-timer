// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { test } from 'node:test';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { test } from 'node:test';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import assert from 'node:assert/strict';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import assert from 'node:assert/strict';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import { messages } from '../js/messages.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { messages } from '../js/messages.js';
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import { translate, loadLanguage, setLanguage, t, locale } from '../js/i18n.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { translate, loadLanguage, setLanguage, t, locale } from '../js/i18n.js';
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import { newWorkout } from '../js/model.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { newWorkout } from '../js/model.js';
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import { compileTimeline, plannedMs } from '../js/timeline.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { compileTimeline, plannedMs } from '../js/timeline.js';
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import { parseBackup, makeBackup } from '../js/io.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { parseBackup, makeBackup } from '../js/io.js';

// DOC L0009: Esegue il passaggio esplicito «test('language: stored preference, device language, invalid/denied storage and fallback', () => {» nel flusso: Prove di regressione in i18n.test.js.
test('language: stored preference, device language, invalid/denied storage and fallback', () => {
  // DOC L0010: getItem: Prepara o verifica il caso di regressione «get Item»; le asserzioni nel corpo specificano l’esito atteso.
  const store = { getItem: () => 'es' };
  // DOC L0011: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(loadLanguage(store, ['en-US']), 'es');
  // DOC L0012: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(loadLanguage(null, ['en-US']), 'en'); assert.equal(loadLanguage(null, ['es-MX']), 'es');
  // DOC L0013: getItem: Prepara o verifica il caso di regressione «get Item»; le asserzioni nel corpo specificano l’esito atteso.
  assert.equal(loadLanguage({ getItem() { throw Error(); } }, ['it-IT']), 'it');
  // DOC L0014: getItem: Prepara o verifica il caso di regressione «get Item»; le asserzioni nel corpo specificano l’esito atteso.
  assert.equal(loadLanguage({ getItem: () => 'unknown' }, ['de-DE']), 'it');
// DOC L0015: Chiude il costrutto aperto alla riga 9.
});
// DOC L0016: Esegue il passaggio esplicito «test('all messages have English and Spanish with matching placeholders', () => {» nel flusso: Prove di regressione in i18n.test.js.
test('all messages have English and Spanish with matching placeholders', () => {
  // DOC L0017: tokens: Prepara o verifica il caso di regressione «tokens»; le asserzioni nel corpo specificano l’esito atteso.
  const tokens = s => [...new Set(s.match(/\{\d+\}/g) || [])].sort();
  // DOC L0018: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  for (const [source, values] of Object.entries(messages)) for (const value of values) {
    // DOC L0019: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.ok(typeof value === 'string' && value.length); assert.deepEqual(tokens(value), tokens(source), source);
  // DOC L0020: Chiude il costrutto aperto alla riga 18.
  }
// DOC L0021: Chiude il costrutto aperto alla riga 16.
});
// DOC L0022: Esegue il passaggio esplicito «test('formatted phase labels preserve user-entered names in all languages', () => {» nel flusso: Prove di regressione in i18n.test.js.
test('formatted phase labels preserve user-entered names in all languages', () => {
  // DOC L0023: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  for (const language of ['it', 'en', 'es']) {
    // DOC L0024: label: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    const label = translate('Attività 2 · Lavoro <mio> · sessione 1/4', language);
    // DOC L0025: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.ok(label.includes('Lavoro <mio>'));
    // DOC L0026: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert.ok(label.includes(language === 'en' ? 'session 1/4' : language === 'es' ? 'sesión 1/4' : 'sessione 1/4'));
  // DOC L0027: Chiude il costrutto aperto alla riga 23.
  }
  // DOC L0028: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(translate('Eliminare “Work · lavoro”? Le sessioni restano in cronologia.', 'en'), 'Delete “Work · lavoro”? Sessions stay in history.');
// DOC L0029: Chiude il costrutto aperto alla riga 22.
});
// DOC L0030: Esegue il passaggio esplicito «test('language changes never change times or stored program names', () => {» nel flusso: Prove di regressione in i18n.test.js.
test('language changes never change times or stored program names', () => {
  // DOC L0031: Esegue il passaggio esplicito «setLanguage('it'); const w = newWorkout('STUDIO'); w.title = 'Il mio nome: Lavoro';» nel flusso: Prove di regressione in i18n.test.js.
  setLanguage('it'); const w = newWorkout('STUDIO'); w.title = 'Il mio nome: Lavoro';
  // DOC L0032: backup: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const backup = makeBackup({ workouts: [w], sessions: [] });
  // DOC L0033: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  for (const language of ['en', 'es', 'it']) { setLanguage(language); assert.equal(plannedMs(compileTimeline(w)), 6900000); assert.equal(parseBackup(backup).workouts[0].title, w.title); }
  // DOC L0034: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  setLanguage('en'); assert.equal(t('Recupero'), 'Rest'); assert.equal(newWorkout('STUDIO').title, 'My study program'); assert.equal(locale(), 'en-GB'); setLanguage('it');
// DOC L0035: Chiude il costrutto aperto alla riga 30.
});
// DOC L0036: Esegue il passaggio esplicito «test('validation errors and guide terms are translated without changing identifiers', () => {» nel flusso: Prove di regressione in i18n.test.js.
test('validation errors and guide terms are translated without changing identifiers', () => {
  // DOC L0037: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(translate('Concentrazione: intero tra 60 e 3600', 'es'), 'Concentración: número entero entre 60 y 3600');
  // DOC L0038: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(translate('Pausa (timer)', 'en'), 'Pause'); assert.equal(translate('Pausa (timer)', 'es'), 'Pausar'); assert.equal(translate('Pausa (timer)', 'it'), 'Pausa');
  // DOC L0039: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(translate('Termina il timer per cambiare modalità.', 'en'), 'Stop the timer to change modes.');
// DOC L0040: Chiude il costrutto aperto alla riga 36.
});
