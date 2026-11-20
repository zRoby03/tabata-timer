// Rasterize the shared SVG for web app installation. Playwright is only needed to regenerate PNGs.
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import {pathToFileURL} from 'node:url';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import {pathToFileURL} from 'node:url';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import {mkdir,readFile} from 'node:fs/promises';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import {mkdir,readFile} from 'node:fs/promises';
// DOC L0004: Esegue il passaggio esplicito «const {chromium}=await import(process.env.PLAYWRIGHT_MODULE ? pathToFileURL(process.env.PLAYWRIGHT_MODULE).href : 'playwright');» nel flusso: Rasterizzazione delle icone web installabili.
const {chromium}=await import(process.env.PLAYWRIGHT_MODULE ? pathToFileURL(process.env.PLAYWRIGHT_MODULE).href : 'playwright');
// DOC L0005: browser: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const browser=await chromium.launch({channel:process.env.TABATA_BROWSER || 'msedge',headless:true});
// DOC L0006: Esegue il passaggio esplicito «try {» nel flusso: Rasterizzazione delle icone web installabili.
try {
 // DOC L0007: source: sorgente audio o testo sorgente: il contesto della funzione ne specifica l’uso.
 const source=await readFile('web/icon.svg','utf8');await mkdir('web/icons',{recursive:true});
 // DOC L0008: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
 for(const size of [192,512]){
  // DOC L0009: p: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const p=await browser.newPage({viewport:{width:size,height:size},deviceScaleFactor:1});
  // DOC L0010: Esegue il passaggio esplicito «await p.setContent(`<style>html,body{margin:0;background:transparent}svg{display:block;width:${size}px;height:${size}px}</style>${source}`);» nel flusso: Rasterizzazione delle icone web installabili.
  await p.setContent(`<style>html,body{margin:0;background:transparent}svg{display:block;width:${size}px;height:${size}px}</style>${source}`);
  // DOC L0011: Esegue il passaggio esplicito «await p.locator('svg').screenshot({path:`web/icons/icon-${size}.png`,omitBackground:true});await p.close();» nel flusso: Rasterizzazione delle icone web installabili.
  await p.locator('svg').screenshot({path:`web/icons/icon-${size}.png`,omitBackground:true});await p.close();
 // DOC L0012: Chiude il costrutto aperto alla riga 8.
 }
// DOC L0013: Esegue il passaggio esplicito «}finally{await browser.close();}» nel flusso: Rasterizzazione delle icone web installabili.
}finally{await browser.close();}
