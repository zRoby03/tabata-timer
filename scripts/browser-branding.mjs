import {pathToFileURL} from 'node:url';
import {mkdir,writeFile} from 'node:fs/promises';
import assert from 'node:assert/strict';
const {chromium}=await import(pathToFileURL(process.env.PLAYWRIGHT_MODULE).href);
const browser=await chromium.launch({channel:'msedge',headless:true});
const manifest=await (await fetch('http://127.0.0.1:5500/manifest.webmanifest')).json();
assert.equal(manifest.name,'Tabata Timer');assert.equal(manifest.short_name,'Tabata Timer');
const checks=[];await mkdir('output/screenshots',{recursive:true});
try {
 for(const lang of ['it-IT','en-US','es-ES']){
  const p=await browser.newPage({locale:lang,viewport:{width:390,height:900}});
  await p.goto('http://127.0.0.1:5500/?v=1.7.0&mode=studio');
  await p.getByRole('heading',{name:'Tabata Timer',exact:true}).waitFor();
  assert.equal(await p.title(),'Tabata Timer');assert.equal(new URL(p.url()).search,'');
  assert.equal(await p.locator('body').getAttribute('data-mode'),'studio');
  await p.locator('#mode-switch').click();assert.equal(await p.title(),'Tabata Timer');assert.equal(new URL(p.url()).search,'');
  await p.screenshot({path:`output/screenshots/branding-${lang}.png`,fullPage:true});
  checks.push({language:lang,title:'Tabata Timer',cleanUrl:true,bothModes:true});await p.close();
 }
 const p=await browser.newPage({viewport:{width:512,height:512}});await p.goto('http://127.0.0.1:5500/icon.svg');await p.locator('svg').screenshot({path:'output/Tabata Timer.png',omitBackground:true});
 await writeFile('output/browser-branding.json',JSON.stringify({checks,errors:[]},null,2));console.log('Nome Tabata Timer e URL pulito verificati in tre lingue e due modalità; icona renderizzata.');
}finally{await browser.close();}
