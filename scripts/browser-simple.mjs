// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { writeFile } from 'node:fs/promises';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { writeFile } from 'node:fs/promises';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import { pathToFileURL } from 'node:url';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { pathToFileURL } from 'node:url';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import assert from 'node:assert/strict';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import assert from 'node:assert/strict';
// DOC L0004: Esegue il passaggio esplicito «const {chromium}=await import(pathToFileURL(process.env.PLAYWRIGHT_MODULE).href);» nel flusso: Prova browser automatizzata.
const {chromium}=await import(pathToFileURL(process.env.PLAYWRIGHT_MODULE).href);
// DOC L0005: browser: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const browser=await chromium.launch({channel:'msedge',headless:true});
// DOC L0006: Esegue il passaggio esplicito «try {» nel flusso: Prova browser automatizzata.
try {
 // DOC L0007: page: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
 const page=await browser.newPage({ locale: 'it-IT',viewport:{width:390,height:900}});
 // DOC L0008: Esegue il passaggio esplicito «await page.goto('http://127.0.0.1:5500/?mode=studio');» nel flusso: Prova browser automatizzata.
 await page.goto('http://127.0.0.1:5500/?mode=studio');
 // DOC L0009: Esegue il passaggio esplicito «await page.getByRole('button',{name:'+ Aggiungi attività',exact:true}).click();» nel flusso: Prova browser automatizzata.
 await page.getByRole('button',{name:'+ Aggiungi attività',exact:true}).click();
 // DOC L0010: Esegue il passaggio esplicito «await page.getByRole('textbox',{name:'Nome attività · Attività 1',exact:true}).fill('Lettura');» nel flusso: Prova browser automatizzata.
 await page.getByRole('textbox',{name:'Nome attività · Attività 1',exact:true}).fill('Lettura');
 // DOC L0011: Esegue il passaggio esplicito «await page.getByRole('button',{name:'Elimina Attività 2',exact:true}).click();» nel flusso: Prova browser automatizzata.
 await page.getByRole('button',{name:'Elimina Attività 2',exact:true}).click();
 // DOC L0012: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
 assert.equal(await page.locator('.study-activity').count(),1);
 // DOC L0013: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
 assert.equal(await page.getByRole('textbox',{name:'Nome attività · Attività 1',exact:true}).inputValue(),'Lettura');
 // DOC L0014: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
 assert.equal(await page.getByRole('button',{name:'Elimina Attività 1',exact:true}).isDisabled(),true);
 // DOC L0015: Esegue il passaggio esplicito «await page.evaluate(async()=>{document.activeElement.blur();scrollTo(0,0);await document.fonts.ready;await new Promise(r=>requestAnimationFrame(()=>requestAnimationFrame(r)));});» nel flusso: Prova browser automatizzata.
 await page.evaluate(async()=>{document.activeElement.blur();scrollTo(0,0);await document.fonts.ready;await new Promise(r=>requestAnimationFrame(()=>requestAnimationFrame(r)));});
 // DOC L0016: Esegue il passaggio esplicito «await page.screenshot({path:'output/screenshots/web-studio-v170-390.png'});» nel flusso: Prova browser automatizzata.
 await page.screenshot({path:'output/screenshots/web-studio-v170-390.png'});
 // DOC L0017: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
 await page.getByRole('switch').click();
 // DOC L0018: Esegue il passaggio esplicito «await page.getByRole('button',{name:'Allenamenti',exact:true}).click();» nel flusso: Prova browser automatizzata.
 await page.getByRole('button',{name:'Allenamenti',exact:true}).click();
 // DOC L0019: Esegue il passaggio esplicito «await page.getByRole('button',{name:'+ Crea allenamento',exact:true}).click();» nel flusso: Prova browser automatizzata.
 await page.getByRole('button',{name:'+ Crea allenamento',exact:true}).click();
 // DOC L0020: Esegue il passaggio esplicito «await page.getByRole('button',{name:'+ Aggiungi esercizio',exact:true}).click();» nel flusso: Prova browser automatizzata.
 await page.getByRole('button',{name:'+ Aggiungi esercizio',exact:true}).click();
 // DOC L0021: Esegue il passaggio esplicito «await page.getByRole('button',{name:'+ Aggiungi esercizio',exact:true}).click();» nel flusso: Prova browser automatizzata.
 await page.getByRole('button',{name:'+ Aggiungi esercizio',exact:true}).click();
 // DOC L0022: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
 assert.equal(await page.locator('input[data-path$=".restBetweenExercisesSec"]').count(),1);
 // DOC L0023: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
 assert.equal(await page.locator('input[data-path$=".restAfterExerciseSec"]').count(),0);
 // DOC L0024: Esegue il passaggio esplicito «await page.locator('[data-path="blocks.0.restBetweenExercisesSec"]').fill('7');» nel flusso: Prova browser automatizzata.
 await page.locator('[data-path="blocks.0.restBetweenExercisesSec"]').fill('7');
 // DOC L0025: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
 assert.equal(await page.locator('.phase-list li').filter({hasText:'Recupero tra esercizi'}).count(),2);
 // DOC L0026: labels: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
 const labels=await page.locator('#workout-form label').allTextContents();
 // DOC L0027: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
 assert.ok(labels.every(x=>!/(?:\bmin\b|\bsec\b|\(s\))/.test(x)));
 // DOC L0028: Esegue il passaggio esplicito «await page.screenshot({path:'output/screenshots/web-workout-editor-v170-390.png',fullPage:true});» nel flusso: Prova browser automatizzata.
 await page.screenshot({path:'output/screenshots/web-workout-editor-v170-390.png',fullPage:true});
 // DOC L0029: Esegue il passaggio esplicito «await writeFile('output/browser-simple.json', JSON.stringify({ version: '1.7.0', studyAddDelete: true, finalActivityProtected: true, workoutSingleRestPerBlock: true, unitSuffixesRe» nel flusso: Prova browser automatizzata.
 await writeFile('output/browser-simple.json', JSON.stringify({ version: '1.7.0', studyAddDelete: true, finalActivityProtected: true, workoutSingleRestPerBlock: true, unitSuffixesRemoved: true }, null, 2));
 // DOC L0030: Esegue il passaggio esplicito «console.log('Aggiunta/eliminazione Studio, ultima attività protetta, recupero Workout unico anche con tre esercizi: verificati.');» nel flusso: Prova browser automatizzata.
 console.log('Aggiunta/eliminazione Studio, ultima attività protetta, recupero Workout unico anche con tre esercizi: verificati.');
// DOC L0031: Esegue il passaggio esplicito «} finally {await browser.close();}» nel flusso: Prova browser automatizzata.
} finally {await browser.close();}
