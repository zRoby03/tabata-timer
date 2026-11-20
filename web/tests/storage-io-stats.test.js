import { test } from 'node:test';
import assert from 'node:assert/strict';
import { LocalRepository, STORAGE_KEY } from '../js/storage.js';
import { parseBackup, makeBackup, sessionsCsv } from '../js/io.js';
import { aggregate } from '../js/stats.js';
import { newWorkout } from '../js/model.js';
const session = { id: 's1', workoutId: 'w', workoutTitle: '=SUM(A1)', startedAt: '2026-11-16T10:00:00.000Z', endedAt: '2026-11-16T10:01:00.000Z', plannedMs: 60000, elapsedMs: 40000, workMs: 20000, completedWorkIntervals: 1, plannedWorkIntervals: 2, pauses: 1, status: 'INTERRUPTED' };
function memory() { const map = new Map(); return { writes: 0, getItem: k => map.get(k), setItem(k, v) { this.writes++; map.set(k, v); }, removeItem(k) { this.writes++; map.delete(k); } }; }
test('temporanea zero scritture, persistenza e reload', () => {
  const storage = memory(), repo = new LocalRepository(storage); repo.upsertWorkout(newWorkout()); repo.addSession(session); assert.equal(storage.writes, 0);
  assert.equal(repo.enable(), true); const reloaded = new LocalRepository(storage); reloaded.load(parseBackup); assert.equal(reloaded.data.sessions.length, 1); assert.equal(reloaded.persistent, true);
  reloaded.disable(); const before = storage.writes; reloaded.addSession({ ...session, id: 's2' }); assert.equal(storage.writes, before); assert.equal(storage.getItem(STORAGE_KEY), undefined);
});
test('salva sul dispositivo attiva la persistenza e conserva il nome al reload', () => {
  const storage = memory(), repo = new LocalRepository(storage), w = newWorkout();
  w.title = 'La mia pausa attiva';
  assert.equal(repo.saveWorkoutLocally(w), true);
  assert.equal(storage.writes, 1); assert.equal(repo.persistent, true);
  const reopened = new LocalRepository(storage); reopened.load(parseBackup);
  assert.equal(reopened.data.workouts[0].title, 'La mia pausa attiva');
});
test('salvataggio locale fallito conserva RAM e snapshot senza duplicare un nuovo tentativo', () => {
  const storage = memory(), repo = new LocalRepository(storage), w = newWorkout();
  storage.setItem = () => { throw new Error('quota'); };
  assert.equal(repo.saveWorkoutLocally(w), false); assert.equal(repo.persistent, false);
  assert.equal(repo.saveWorkoutLocally(w), false); assert.equal(repo.data.workouts.length, 1);
});
test('un errore non disattiva una persistenza già scelta', () => {
  const storage = memory(), repo = new LocalRepository(storage); repo.enable();
  const snapshot = storage.getItem(STORAGE_KEY); storage.setItem = () => { throw new Error('quota'); };
  assert.equal(repo.saveWorkoutLocally(newWorkout()), false);
  assert.equal(repo.persistent, true); assert.equal(storage.getItem(STORAGE_KEY), snapshot);
});
test('quota e storage indisponibile preservano RAM e snapshot', () => {
  const warnings = [], storage = memory(), repo = new LocalRepository(storage, m => warnings.push(m)); repo.upsertWorkout(newWorkout()); repo.enable(); const old = storage.getItem(STORAGE_KEY);
  storage.setItem = () => { throw new Error('quota'); }; repo.addSession(session); assert.equal(repo.data.sessions.length, 1); assert.equal(storage.getItem(STORAGE_KEY), old); assert.equal(warnings.length, 1);
  const denied = new LocalRepository({ getItem() { throw new Error('denied'); } }, m => warnings.push(m)); denied.load(parseBackup); assert.equal(denied.persistent, false);
});
test('corruzione non cancellata e sessioni non duplicate', () => {
  const storage = memory(); storage.setItem(STORAGE_KEY, 'invalid'); const repo = new LocalRepository(storage); repo.load(parseBackup); assert.equal(storage.getItem(STORAGE_KEY), 'invalid'); repo.addSession(session); repo.addSession(session); assert.equal(repo.data.sessions.length, 1);
});
test('JSON roundtrip, CSV neutralizza formule', () => {
  const raw = makeBackup({ workouts: [newWorkout()], sessions: [session] }); assert.equal(parseBackup(raw).sessions[0].id, 's1'); assert.match(sessionsCsv([session]), /'=SUM/);
});
for (const [name, change] of [
  ['versione', x => x.schemaVersion = 2], ['duplicate', x => x.sessions.push(session)], ['date', x => x.sessions[0].startedAt = '2026-02-30T00:00:00Z'],
  ['lavoro', x => x.sessions[0].workMs = 999999], ['intervalli', x => x.sessions[0].completedWorkIntervals = 3], ['stato', x => x.sessions[0].status = 'COMPLETED']
]) test(`backup rifiuta ${name}`, () => { const value = JSON.parse(makeBackup({ workouts: [], sessions: [session] })); change(value); assert.throws(() => parseBackup(JSON.stringify(value))); });
test('backup troppo grande o JSON malformato', () => { assert.throws(() => parseBackup(' '.repeat(5 * 1024 * 1024 + 1))); assert.throws(() => parseBackup('{')); });
test('statistiche settimana UTC, filtro workout e media', () => {
  const now = new Date('2026-11-17T12:00:00Z'); const all = aggregate([session, { ...session, id: 's2', workoutId: 'other', workMs: 40000 }], 7, '', now);
  assert.equal(all.count, 2); assert.equal(all.completion, 50); assert.equal(all.workMs, 60000); assert.equal(all.weeks.find(w => w.week === '2026-11-16').count, 2);
  assert.equal(aggregate([session], 7, 'other', now).count, 0); assert.equal(aggregate([], 7, '', now).completion, 0);
});
