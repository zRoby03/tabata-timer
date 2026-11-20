// Rasterize the shared SVG for web app installation. Playwright is only needed to regenerate PNGs.
import {pathToFileURL} from 'node:url';
import {mkdir,readFile} from 'node:fs/promises';
const {chromium}=await import(process.env.PLAYWRIGHT_MODULE ? pathToFileURL(process.env.PLAYWRIGHT_MODULE).href : 'playwright');
const browser=await chromium.launch({channel:process.env.TABATA_BROWSER || 'msedge',headless:true});
try {
 const source=await readFile('web/icon.svg','utf8');await mkdir('web/icons',{recursive:true});
 for(const size of [192,512]){
  const p=await browser.newPage({viewport:{width:size,height:size},deviceScaleFactor:1});
  await p.setContent(`<style>html,body{margin:0;background:transparent}svg{display:block;width:${size}px;height:${size}px}</style>${source}`);
  await p.locator('svg').screenshot({path:`web/icons/icon-${size}.png`,omitBackground:true});await p.close();
 }
}finally{await browser.close();}
