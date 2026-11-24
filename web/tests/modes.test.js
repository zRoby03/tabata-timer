import { test } from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { newStudyActivity, newWorkout, newExercise, normalizeWorkout, normalizationNotice, duplicateWorkout } from '../js/model.js';
import { newQuickWorkout } from '../js/quick.js';
import { compileTimeline, plannedMs } from '../js/timeline.js';
import { TimerEngine } from '../js/timer.js';
import { TransitionCues } from '../js/transitions.js';
import { CountdownCues, hornSample } from '../js/countdown.js';
import { modeOf, modeData, loadMode, saveMode } from '../js/modes.js';
import { parseBackup, makeBackup } from '../js/io.js';
import { LocalRepository } from '../js/storage.js';
import { aggregate } from '../js/stats.js';

test('Studio usa minuti, quattro sessioni e nessuna pausa finale', () => {
  const w = newQuickWorkout('STUDIO'), phases = compileTimeline(w);
  assert.equal(plannedMs(phases), 115 * 60000);
  assert.equal(phases.filter(p => p.type === 'WORK').length, 4);
  assert.equal(phases.at(-1).type, 'WORK');
  assert.throws(() => { w.activities[0].concentrationSec = 61; compileTimeline(w); });
});
test('Studio piatto: pause solo tra sessioni e tra attività, mai finali', () => {
  const w = newQuickWorkout('STUDIO');
  Object.assign(w.activities[0], { concentrationSec: 60, sessions: 2, pauseSec: 120 });
  w.activities.push({ ...newStudyActivity(), concentrationSec: 180, sessions: 1, pauseSec: 3600 });
  w.restBetweenActivitiesSec = 240;
  const phases = compileTimeline(w);
  assert.deepEqual(phases.map(p => p.type), ['WORK', 'REST_REP', 'WORK', 'REST_ACTIVITY', 'WORK']);
  assert.deepEqual(phases.map(p => p.durationMs), [60, 120, 60, 240, 180].map(s => s * 1000));
  assert.equal(phases[1].label, 'Pausa Concentrazione — tra sessioni'); assert.equal(phases[3].label, 'Pausa Concentrazione → Concentrazione — tra attività');
  let now = 0; const timer = new TimerEngine(w, () => now); timer.start(); now = 20000 + plannedMs(phases); timer.tick();
  assert.equal(timer.result.workMs, 300000); assert.equal(timer.result.plannedMs, 660000); assert.equal(timer.result.plannedWorkIntervals, 3);
  w.restBetweenActivitiesSec = 0; w.activities[0].pauseSec = 0;
  assert.equal(compileTimeline(w).length, 3);
});

test('vecchio Studio: conversione esplicita, originale e cronologia intatti', () => {
  const backup = parseBackup(readFileSync(new URL('../../fixtures/backup-modes-v1.json', import.meta.url), 'utf8'));
  const original = structuredClone(backup); const w = backup.workouts[0];
  assert.match(normalizationNotice(w), /precedente/);
  const normalized = normalizeWorkout(w);
  assert.equal(normalized.activities.length, 1); assert.equal(normalized.blocks.length, 0);
  assert.equal(normalized.longBreakSec, undefined); assert.equal(normalized.cooldownSec, 0);
  assert.equal(plannedMs(compileTimeline(w)), 235 * 60000);
  assert.deepEqual(backup, original); assert.deepEqual(parseBackup(makeBackup(backup)).workouts, original.workouts);
  const legacy = structuredClone(w); legacy.blocks[0].cycles = 2; legacy.blocks[0].exercises[0].repetitions = 1;
  assert.equal(normalizeWorkout(legacy).activities.length, 2);
  assert.equal(new Set(duplicateWorkout(legacy).activities.map(a => a.id)).size, 2);
});

test('Workout: recuperi personali anche fra esercizi, campi comuni ignorati', () => {
  const w = newWorkout(), b = w.blocks[0]; b.restBetweenExercisesSec = 99;
  b.exercises = [1, 2, 3].map(n => ({ ...newExercise(), title: `E${n}`, repetitions: 2, workSec: 10, restBetweenRepsSec: 5+n, restAfterExerciseSec: 99 }));
  const phases = compileTimeline(w);
  assert.deepEqual(phases.filter(p => p.type === 'REST_EXERCISE').map(p => p.durationMs), [6000, 7000]);
  assert.equal(phases.filter(p => p.type === 'REST_REP').length, 3); assert.equal(phases.at(-1).type, 'WORK');
  const legacy=structuredClone(w);delete legacy.timingVersion;
  assert.match(normalizationNotice(legacy), /recupero personale/);
  assert.equal(normalizeWorkout(legacy).blocks[0].restBetweenExercisesSec,99);
  assert.equal(legacy.timingVersion,undefined);
});

test('nuovi campi Studio: valori interi, identità e limiti controllati', () => {
  for (const change of [w => w.activities = [], w => w.activities[0].sessions = 0, w => w.activities[0].title = '', w => w.restBetweenActivitiesSec = 61, w => w.activities.push({ ...w.activities[0] })]) {
    const w = newQuickWorkout('STUDIO'); change(w); assert.throws(() => compileTimeline(w));
  }
});

test('preparazione Studio: 20 secondi, tre bip alti e fischio all’avvio', () => {
  let clock = 0; const timer = new TimerEngine(newQuickWorkout('STUDIO'), () => clock);
  const countdown = new CountdownCues(), transitions = new TransitionCues(); timer.start();
  assert.equal(timer.remainingMs, 20000);
  for (const second of [3, 2, 1]) { clock = 20000 - second * 1000; timer.tick(); assert.equal(hornSample(countdown.read(timer), timer.currentPhase.type), `rest-${second}`); }
  clock = 20000; timer.tick(); assert.equal(transitions.read(timer), 'whistle'); assert.equal(timer.elapsedMs, 0);
  clock += 1500000; timer.tick(); assert.equal(transitions.read(timer), 'sigh');
  timer.stop(); assert.equal(timer.result.mode, 'STUDIO'); assert.equal(timer.result.workMs, 1500000);
});
test('archivio legacy rimane Workout; salvataggio, attività e statistiche separati', () => {
  const legacy = parseBackup(readFileSync(new URL('../../fixtures/backup-web-v1.json', import.meta.url), 'utf8'));
  const w = newQuickWorkout('STUDIO'); const s = { ...legacy.sessions[0], id: 'studio-session', workoutId: w.id, mode: 'STUDIO', workMs: 30000 };
  const values = new Map(), storage = { getItem: k => values.get(k), setItem: (k, v) => values.set(k, v) };
  const repo = new LocalRepository(storage); repo.data = legacy; repo.saveWorkoutLocally(w); repo.addSession(s);
  const reopened = new LocalRepository(storage); reopened.load(parseBackup);
  const studio = modeData(reopened.data, 'STUDIO'), workout = modeData(reopened.data, 'WORKOUT');
  assert.equal(studio.workouts.length, 1); assert.equal(studio.sessions.length, 1);
  assert.equal(workout.workouts.length, legacy.workouts.length - 1); assert.equal(workout.sessions.length, legacy.sessions.length - 1);
  assert.equal(modeOf(workout.workouts[0]), 'WORKOUT');
  assert.equal(aggregate(studio.sessions, 36500).workMs, 30000);
  assert.deepEqual(parseBackup(makeBackup(reopened.data)).workouts, reopened.data.workouts);
});
test('modalità ricordata separatamente dai dati; storage negato non blocca', () => {
  const values = new Map(), storage = { getItem: k => values.get(k), setItem: (k, v) => values.set(k, v) };
  saveMode(storage, 'STUDIO'); assert.equal(loadMode(storage), 'STUDIO'); assert.equal(values.size, 1);
  const denied = { getItem() { throw Error(); }, setItem() { throw Error(); } };
  assert.doesNotThrow(() => saveMode(denied, 'STUDIO')); assert.equal(loadMode(denied), 'WORKOUT');
});
test('backup rifiuta modalità sconosciute per programmi e attività', () => {
  const data = parseBackup(readFileSync(new URL('../../fixtures/backup-web-v1.json', import.meta.url), 'utf8'));
  data.workouts[0].mode = 'OTHER'; assert.throws(() => parseBackup(makeBackup(data)));
  delete data.workouts[0].mode; data.sessions[0].mode = 'OTHER'; assert.throws(() => parseBackup(makeBackup(data)));
});
