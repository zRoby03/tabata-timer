// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import {pathToFileURL} from 'node:url';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import {pathToFileURL} from 'node:url';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import {mkdir,writeFile} from 'node:fs/promises';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import {mkdir,writeFile} from 'node:fs/promises';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import assert from 'node:assert/strict';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import assert from 'node:assert/strict';
// DOC L0004: Esegue il passaggio esplicito «const {chromium}=await import(pathToFileURL(process.env.PLAYWRIGHT_MODULE).href);» nel flusso: Prova browser automatizzata.
const {chromium}=await import(pathToFileURL(process.env.PLAYWRIGHT_MODULE).href);
// DOC L0005: browser: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const browser=await chromium.launch({channel:'msedge',headless:true});
// DOC L0006: manifest: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const manifest=await (await fetch('http://127.0.0.1:5500/manifest.webmanifest')).json();
// DOC L0007: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert.equal(manifest.name,'Tabata Timer');assert.equal(manifest.short_name,'Tabata Timer');
// DOC L0008: checks: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const checks=[];await mkdir('output/screenshots',{recursive:true});
// DOC L0009: Esegue il passaggio esplicito «try {» nel flusso: Prova browser automatizzata.
try {
 // DOC L0010: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
 for(const lang of ['it-IT','en-US','es-ES']){
  // DOC L0011: p: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const p=await browser.newPage({locale:lang,viewport:{width:390,height:900}});
  // DOC L0012: Esegue il passaggio esplicito «await p.goto('http://127.0.0.1:5500/?v=1.7.0&mode=studio');» nel flusso: Prova browser automatizzata.
  await p.goto('http://127.0.0.1:5500/?v=1.7.0&mode=studio');
  // DOC L0013: Esegue il passaggio esplicito «await p.getByRole('heading',{name:'Tabata Timer',exact:true}).waitFor();» nel flusso: Prova browser automatizzata.
  await p.getByRole('heading',{name:'Tabata Timer',exact:true}).waitFor();
  // DOC L0014: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await p.title(),'Tabata Timer');assert.equal(new URL(p.url()).search,'');
  // DOC L0015: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
  assert.equal(await p.locator('body').getAttribute('data-mode'),'studio');
  // DOC L0016: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
  await p.locator('#mode-switch').click();assert.equal(await p.title(),'Tabata Timer');assert.equal(new URL(p.url()).search,'');
  // DOC L0017: Esegue il passaggio esplicito «await p.screenshot({path:`output/screenshots/branding-${lang}.png`,fullPage:true});» nel flusso: Prova browser automatizzata.
  await p.screenshot({path:`output/screenshots/branding-${lang}.png`,fullPage:true});
  // DOC L0018: Esegue il passaggio esplicito «checks.push({language:lang,title:'Tabata Timer',cleanUrl:true,bothModes:true});await p.close();» nel flusso: Prova browser automatizzata.
  checks.push({language:lang,title:'Tabata Timer',cleanUrl:true,bothModes:true});await p.close();
 // DOC L0019: Chiude il costrutto aperto alla riga 10.
 }
 // DOC L0020: p: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
 const p=await browser.newPage({viewport:{width:512,height:512}});await p.goto('http://127.0.0.1:5500/icon.svg');await p.locator('svg').screenshot({path:'output/Tabata Timer.png',omitBackground:true});
 // DOC L0021: Esegue il passaggio esplicito «await writeFile('output/browser-branding.json',JSON.stringify({checks,errors:[]},null,2));console.log('Nome Tabata Timer e URL pulito verificati in tre lingue e due modalità; icona» nel flusso: Prova browser automatizzata.
 await writeFile('output/browser-branding.json',JSON.stringify({checks,errors:[]},null,2));console.log('Nome Tabata Timer e URL pulito verificati in tre lingue e due modalità; icona renderizzata.');
// DOC L0022: Esegue il passaggio esplicito «}finally{await browser.close();}» nel flusso: Prova browser automatizzata.
}finally{await browser.close();}
