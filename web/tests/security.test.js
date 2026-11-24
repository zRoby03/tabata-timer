import {test} from 'node:test';
import assert from 'node:assert/strict';
import {newWorkout, clone} from '../js/model.js';
import {parseBackup, makeBackup, sessionsCsv, MAX_BACKUP_BYTES} from '../js/io.js';
import {compileTimeline} from '../js/timeline.js';
import {LocalRepository, STORAGE_KEY} from '../js/storage.js';

const backup = () => JSON.parse(makeBackup({workouts:[newWorkout()],sessions:[]}));
test('security: identificatori, colore e numeri ostili non entrano nel modello',()=>{
  for(const mutate of [w=>w.id='../escape',w=>w.id='x" id="example',w=>w.color='#fff;background:url(https://example.invalid)',w=>w.blocks[0].cycles=1e100,w=>w.blocks[0].exercises[0].workSec='5',w=>w.blocks[0].exercises[0].workSec=5.5]){
    const b=backup();mutate(b.workouts[0]);assert.throws(()=>parseBackup(JSON.stringify(b)));
  }
});
test('security: limiti UTF-8, collezioni, duplicati e timeline eccessiva',()=>{
  assert.throws(()=>parseBackup('é'.repeat(MAX_BACKUP_BYTES/2+1)),/grande/);
  const tooMany=backup();tooMany.workouts=Array(501).fill(tooMany.workouts[0]);assert.throws(()=>parseBackup(JSON.stringify(tooMany)),/Collezioni/);
  const duplicate=backup();duplicate.workouts[0].blocks[0].exercises.push(clone(duplicate.workouts[0].blocks[0].exercises[0]));assert.throws(()=>parseBackup(JSON.stringify(duplicate)),/duplicato/);
  const w=newWorkout();w.blocks[0].cycles=100;w.blocks[0].exercises[0].repetitions=100;assert.throws(()=>compileTimeline(w),/10.000|24 ore/);
});
test('security: chiavi prototype in JSON non modificano prototipi o archivio',()=>{
  const b=backup();const raw=JSON.stringify(b).replace('{','{"__proto__":{"polluted":true},"constructor":{"prototype":{"polluted":true}},');
  const storage={getItem:()=>raw};const repo=new LocalRepository(storage);repo.load(parseBackup);
  assert.equal({}.polluted,undefined);assert.equal(Object.getPrototypeOf(repo.data),Object.prototype);assert.equal(repo.data.workouts.length,1);
});
test('security: CSV protects formula prefixes and preserves multiline quotes and numbers', () => {
  for (const prefix of ['=', '+', '-', '@', '\t', '\r', '\n', '  =', '　＋', '＠', '　－', '＝', '\uFEFF-']) {
    assert.ok(sessionsCsv([{workoutTitle: prefix + '1+1'}]).includes('"\'' + prefix + '1+1"'));
  }
  const name = 'Intervalli, "Mattina"\nStudio';
  const csv = sessionsCsv([{workoutTitle: name, elapsedMs: 1200}]);
  assert.ok(csv.includes('"Intervalli, ""Mattina""\nStudio"'));
  assert.ok(csv.includes(',"1200",'));
});
test('security: rejects deep JSON and reads brackets in quoted names', () => {
  const data = backup();
  data.workouts[0].title = '[{"Sessione"}]';
  assert.equal(parseBackup(JSON.stringify(data)).workouts[0].title, data.workouts[0].title);
  const deep = JSON.stringify(data).slice(0, -1) + ',"extra":' + '['.repeat(70) + '0' + ']'.repeat(70) + '}';
  assert.throws(() => parseBackup(deep), /troppo annidato/);
});
test('security: a failed persistent import preserves the current in-memory archive', () => {
  const raw = JSON.stringify(backup());
  const storage = { getItem: () => raw, setItem() { throw new Error('quota'); } };
  const repo = new LocalRepository(storage);
  repo.load(parseBackup);
  const previous = repo.data;
  assert.equal(repo.replace({workouts: [], sessions: []}), false);
  assert.equal(repo.data, previous);
  assert.equal(storage.getItem(STORAGE_KEY), raw);
});
test('security: backup rifiutato non cancella lo snapshot locale',()=>{
  const raw='{"schemaVersion":1,"workouts":[null],"sessions":[]}';let writes=0;const warnings=[];
  const storage={getItem:()=>raw,setItem:()=>writes++,removeItem:()=>writes++};const repo=new LocalRepository(storage,m=>warnings.push(m));
  repo.load(parseBackup);assert.equal(writes,0);assert.equal(storage.getItem(STORAGE_KEY),raw);assert.equal(warnings.length,1);assert.equal(repo.persistent,false);
});
