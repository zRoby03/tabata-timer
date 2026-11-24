import {test} from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import {compileTimeline,plannedMs} from '../js/timeline.js';
import {newWorkout,newExercise,clone,normalizeWorkout,normalizationNotice} from '../js/model.js';
import {TimerEngine} from '../js/timer.js';
import {TransitionCues,TRANSITION_SOUNDS,cueVolume} from '../js/transitions.js';
import {CountdownCues} from '../js/countdown.js';
import {preview,editorView} from '../js/editor.js';
import {translate} from '../js/i18n.js';
import {makeBackup,parseBackup} from '../js/io.js';
const cases=JSON.parse(readFileSync(new URL('../../fixtures/workout-recovery-v2.json',import.meta.url)));
for(const row of cases)test(`recuperi v2: ${row.name}`,()=>{
  const before=clone(row.workout),phases=compileTimeline(row.workout);
  assert.equal(plannedMs(phases),row.expectedMs);assert.deepEqual(phases.map(p=>p.type),row.expectedTypes);
  assert.deepEqual(row.workout,before);assert.ok(!phases.some(p=>p.type==='REST_CYCLE'));
  assert.equal(phases.filter(p=>p.type==='WORK').at(0).entryCue,'ding1');
});
test('input nuovi rigorosi, minimi non multipli e dati legacy conservati',()=>{
  for(const change of [w=>w.blocks[0].exercises[0].workSec=4,w=>w.blocks[0].exercises[0].restBetweenRepsSec=3,w=>w.cooldownSec=9,w=>{w.blocks.push({...w.blocks[0],id:'other',exercises:[newExercise()]});w.blocks[0].restAfterBlockSec=0;}]){
    const w=newWorkout();change(w);assert.throws(()=>compileTimeline(w),/minimo/);
    delete w.timingVersion;const original=clone(w);assert.doesNotThrow(()=>compileTimeline(w));assert.match(normalizationNotice(w),/precedente/);assert.deepEqual(w,original);
    assert.deepEqual(parseBackup(makeBackup({workouts:[w],sessions:[]})).workouts,[w]);
    assert.equal(normalizeWorkout(w).timingVersion,2);
  }
  const w=newWorkout();w.blocks[0].exercises[0].workSec=6;w.blocks[0].exercises[0].restBetweenRepsSec=7;assert.doesNotThrow(()=>compileTimeline(w));
});
test('anteprima raggruppata, confini e nomi utente, campi visibili solo dove necessari',()=>{
  const w=clone(cases.find(c=>c.name==='demo-completa').workout);
  const html=preview(w);assert.match(html,/Ciclo 1\/2/);assert.match(html,/Ciclo 2\/2/);assert.match(html,/Recupero Squat/);assert.match(html,/Recupero Blocco 1 → 2/);
  assert.equal((html.match(/Recupero Push-up/g)||[]).length,1);
  assert.equal((editorView(w).match(/data-path="blocks.0.restAfterBlockSec"/g)||[]).length,1);
  assert.ok(!editorView(w).includes('data-path="blocks.1.restAfterBlockSec"'));
  assert.ok(!editorView(w).includes('data-path="blocks.0.restBetweenCyclesSec"'));
  assert.ok(!editorView(w).includes('data-path="blocks.0.restBetweenExercisesSec"'));
  assert.equal(translate('Pausa {0} — tra sessioni','en','Recupero Squat'),'Break for Recupero Squat — between sessions');
});
test('demo completa: segnali e contatori sullo stesso asse temporale, niente bip nel defaticamento',()=>{
  let now=0;const engine=new TimerEngine(cases.find(c=>c.name==='demo-completa').workout,()=>now),transitions=new TransitionCues(),countdown=new CountdownCues();engine.start();
  const events=[];
  for(now=0;now<=95000;now+=100){engine.tick();const key=transitions.read(engine),second=countdown.read(engine);if(key)events.push([now,key]);if(now>=85000&&now<95000)assert.equal(second,null);}
  assert.deepEqual(events,[[20000,'ding1'],[25000,'sigh'],[30000,'ding1'],[35000,'sigh'],[40000,'ding1'],[45000,'sigh'],[50000,'ding2'],[55000,'sigh'],[60000,'ding1'],[65000,'sigh'],[70000,'ding1'],[75000,'double-sigh'],[80000,'ding2'],[85000,'ding3'],[95000,'ding3']]);
  assert.equal(engine.result.workMs,35000);assert.equal(engine.result.elapsedMs,75000);assert.equal(engine.result.plannedMs,75000);assert.equal(engine.result.completedWorkIntervals,7);
});
test('ding originali identici sulle due piattaforme; doppio sospiro composto senza sovrapposizioni',()=>{
  for(const [key,length] of [['ding1',1050],['ding2',1485],['ding3',1920],['double-sigh',1200]]){
    const name=TRANSITION_SOUNDS[key],web=readFileSync(new URL('../audio/'+name,import.meta.url)),native=readFileSync(new URL('../../android/app/src/main/res/raw/'+name.replaceAll('-','_'),import.meta.url));
    assert.deepEqual(web,native);assert.equal((web.length-44)/2/24,length);assert.equal(cueVolume(key),key==='double-sigh'?.85:.65);
    if(key.startsWith('ding'))assert.deepEqual(web,readFileSync(new URL('../../assets/audio/workout-'+key.replace('ding','ding-')+'.wav',import.meta.url)));
  }
  const double=readFileSync(new URL('../audio/rest-double.wav',import.meta.url)).subarray(44),single=readFileSync(new URL('../audio/rest-start.wav',import.meta.url)).subarray(44);
  assert.deepEqual(double.subarray(0,single.length),single);assert.ok(double.subarray(single.length,single.length+9600).every(v=>v===0));assert.deepEqual(double.subarray(single.length+9600),single);
});
