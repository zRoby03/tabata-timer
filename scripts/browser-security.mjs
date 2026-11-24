// Local, isolated QA only: hostile content never goes to the public site.
import {pathToFileURL} from 'node:url';
import {readFile,writeFile,mkdir} from 'node:fs/promises';
import assert from 'node:assert/strict';
const {chromium}=await import(process.env.PLAYWRIGHT_MODULE?pathToFileURL(process.env.PLAYWRIGHT_MODULE).href:'playwright');
const browser=await chromium.launch({channel:'msedge',headless:true});
const base='http://127.0.0.1:'+(process.env.TABATA_SECURITY_PORT || '5500');
const checks=[],errors=[],external=[];
await mkdir('output/security',{recursive:true});
try{
 const context=await browser.newContext({locale:'it-IT'});
 const archive=JSON.parse(await readFile('fixtures/backup-web-v1.json','utf8'));
 const payload='<b id="security-injected">Titolo di prova & "test"</b>';
 archive.workouts[0].title=payload;archive.workouts[0].description=payload;
 archive.workouts[0].blocks[0].title=payload;archive.workouts[0].blocks[0].exercises[0].title=payload;
 archive.sessions[0].workoutTitle=payload;
 await context.addInitScript(data=>localStorage.setItem('tabata:v1:data',JSON.stringify(data)),archive);
 const page=await context.newPage();page.on('pageerror',e=>errors.push(e.message));page.on('request',r=>{if(!r.url().startsWith(base))external.push(r.url())});
 await page.goto(base);await page.getByRole('heading',{name:'Tabata Timer',exact:true}).waitFor();
 const intact=async()=>{assert.equal(await page.locator('#security-injected').count(),0)};
 await page.getByRole('button',{name:'Allenamenti',exact:true}).click();await intact();
 assert.ok((await page.locator('main').innerText()).includes(payload));
 await page.locator('.workout-menu summary').click();await page.getByRole('button',{name:'Modifica',exact:true}).click();await intact();
 assert.equal(await page.locator('[data-path=title]').inputValue(),payload);
 assert.ok((await page.locator('#preview').innerText()).includes(payload));
 await page.getByRole('button',{name:'Attività',exact:true}).click();await intact();
 await page.getByRole('button',{name:/Statistiche/}).click();await intact();
 checks.push('XSS persistente: titolo, descrizione, blocco, esercizio, anteprima, attività e statistiche restano testo, senza nodi o esecuzione');
 const before=await page.evaluate(()=>localStorage.getItem('tabata:v1:data'));
 await page.locator('#import-file').setInputFiles({name:'invalid.json',mimeType:'application/json',buffer:Buffer.from('{"schemaVersion":2}')});
 await page.waitForFunction(()=>document.querySelector('#notice').textContent.includes('rifiutata'));
 assert.equal(await page.evaluate(()=>localStorage.getItem('tabata:v1:data')),before);
 await page.locator('#import-file').setInputFiles({name:'large.json',mimeType:'application/json',buffer:Buffer.alloc(5*1024*1024+1,32)});
 await page.waitForFunction(()=>document.querySelector('#notice').textContent.includes('troppo grande'));
 assert.equal(await page.evaluate(()=>localStorage.getItem('tabata:v1:data')),before);
 checks.push('Import JSON errato o oltre 5 MiB rifiutato prima della sostituzione: snapshot precedente identico');
 for (const [name, buffer, error] of [
  ['depth.json', Buffer.from('{"extra":' + '['.repeat(70) + '0' + ']'.repeat(70) + '}'), 'troppo annidato'],
  ['invalid-utf8.json', Buffer.from([0xff, 0xfe]), 'UTF-8'],
  ['renamed.json', Buffer.from('<p>File di prova</p>'), 'JSON non valido'],
 ]) {
  await page.locator('#import-file').setInputFiles({name,mimeType:'application/json',buffer});
  await page.waitForFunction(text=>document.querySelector('#notice').textContent.includes(text),error);
  assert.equal(await page.evaluate(()=>localStorage.getItem('tabata:v1:data')),before);
 }
 checks.push('Profondità, UTF-8 errato e file rinominato rifiutati senza sostituire l’archivio');
 const policy=await page.evaluate(async()=>{
  const violations=[];document.addEventListener('securitypolicyviolation',e=>violations.push(e.violatedDirective));
  const probe=document.createElement('script');probe.textContent='window.__inlineSecurityProbe=1';document.head.append(probe);
  await fetch('https://example.invalid/tabata-security-probe').catch(()=>{});
  await new Promise(resolve=>setTimeout(resolve,150));
  return {inline:window.__inlineSecurityProbe??null,violations};
 });
 assert.equal(policy.inline,null);assert.ok(policy.violations.some(v=>v.startsWith('script-src')));assert.ok(policy.violations.includes('connect-src'));
 checks.push('CSP effettiva: script inline e connessione esterna bloccati');
 assert.deepEqual(errors,[]);assert.deepEqual(external,[]);
 await writeFile('output/security/browser-security.json',JSON.stringify({checks,errors,external,policy},null,2));
 console.log(`${checks.length} gruppi di sicurezza browser passati.`);
 await context.close();
}finally{await browser.close()}
