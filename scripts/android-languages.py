# DOC L0001: Esegue il passaggio esplicito «"""Language/persistence screenshots on a dedicated Android emulator; never starts a timer."""» nel flusso: Prova Android su emulatore dedicato.
"""Language/persistence screenshots on a dedicated Android emulator; never starts a timer."""
# DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import json,os,re,subprocess,time,xml.etree.ElementTree as ET. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import json,os,re,subprocess,time,xml.etree.ElementTree as ET
# DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: from pathlib import Path. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
from pathlib import Path
# DOC L0004: Esegue il passaggio esplicito «adb=str(Path(os.environ.get('ANDROID_HOME',Path.home()/'AppData/Local/Android/Sdk'))/'platform-tools/adb.exe')» nel flusso: Prova Android su emulatore dedicato.
adb=str(Path(os.environ.get('ANDROID_HOME',Path.home()/'AppData/Local/Android/Sdk'))/'platform-tools/adb.exe')
# DOC L0005: Esegue il passaggio esplicito «package='it.tabatastudio'» nel flusso: Prova Android su emulatore dedicato.
package='it.tabatastudio'
# DOC L0006: run: Passaggio «run» dello strumento: Prova Android su emulatore dedicato. Gli argomenti e le operazioni sono descritti nelle righe del corpo.
def run(*args):return subprocess.check_output([adb,*args],timeout=25)
# DOC L0007: tree: Passaggio «tree» dello strumento: Prova Android su emulatore dedicato. Gli argomenti e le operazioni sono descritti nelle righe del corpo.
def tree():
 # DOC L0008: Esegue il passaggio esplicito «run('shell','uiautomator','dump','/sdcard/tabata-languages.xml')» nel flusso: Prova Android su emulatore dedicato. Nel contesto di tree.
 run('shell','uiautomator','dump','/sdcard/tabata-languages.xml')
 # DOC L0009: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di tree.
 return ET.fromstring(run('shell','cat','/sdcard/tabata-languages.xml'))
# DOC L0010: tap_any: Passaggio «tap_any» dello strumento: Prova Android su emulatore dedicato. Gli argomenti e le operazioni sono descritti nelle righe del corpo.
def tap_any(labels):
 # DOC L0011: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di tap_any.
 for attempt in range(12):
  # DOC L0012: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di tap_any.
  for n in tree().iter('node'):
   # DOC L0013: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di tap_any.
   if n.get('text') in labels or n.get('content-desc') in labels:
    # DOC L0014: Esegue il passaggio esplicito «x1,y1,x2,y2=map(int,re.findall(r'\d+',n.get('bounds')))» nel flusso: Prova Android su emulatore dedicato. Nel contesto di tap_any.
    x1,y1,x2,y2=map(int,re.findall(r'\d+',n.get('bounds')))
    # DOC L0015: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di tap_any.
    run('shell','input','tap',str((x1+x2)//2),str((y1+y2)//2));time.sleep(.35);return
  # DOC L0016: Esegue il passaggio esplicito «time.sleep(.3)» nel flusso: Prova Android su emulatore dedicato. Nel contesto di tap_any.
  time.sleep(.3)
 # DOC L0017: Esegue il passaggio esplicito «raise AssertionError('Missing '+repr(labels))» nel flusso: Prova Android su emulatore dedicato. Nel contesto di tap_any.
 raise AssertionError('Missing '+repr(labels))
# DOC L0018: launch: Passaggio «launch» dello strumento: Prova Android su emulatore dedicato. Gli argomenti e le operazioni sono descritti nelle righe del corpo.
def launch():
 # DOC L0019: Esegue il passaggio esplicito «run('shell','am','force-stop',package);run('shell','am','start','-n',package+'/.MainActivity')» nel flusso: Prova Android su emulatore dedicato. Nel contesto di launch.
 run('shell','am','force-stop',package);run('shell','am','start','-n',package+'/.MainActivity')
 # DOC L0020: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di launch.
 for attempt in range(12):
  # DOC L0021: Esegue il passaggio esplicito «time.sleep(.4)» nel flusso: Prova Android su emulatore dedicato. Nel contesto di launch.
  time.sleep(.4)
  # DOC L0022: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di launch.
  if any(n.get('text')=='Tabata Timer' for n in tree().iter('node')):return
 # DOC L0023: Esegue il passaggio esplicito «raise AssertionError('Home not ready')» nel flusso: Prova Android su emulatore dedicato. Nel contesto di launch.
 raise AssertionError('Home not ready')
# DOC L0024: Esegue il passaggio esplicito «run('shell','input','keyevent','KEYCODE_WAKEUP');run('shell','wm','dismiss-keyguard');run('shell','input','keyevent','KEYCODE_BACK')» nel flusso: Prova Android su emulatore dedicato.
run('shell','input','keyevent','KEYCODE_WAKEUP');run('shell','wm','dismiss-keyguard');run('shell','input','keyevent','KEYCODE_BACK')
# DOC L0025: Esegue il passaggio esplicito «Path('output/screenshots').mkdir(parents=True,exist_ok=True)» nel flusso: Prova Android su emulatore dedicato.
Path('output/screenshots').mkdir(parents=True,exist_ok=True)
# DOC L0026: Esegue il passaggio esplicito «launch();checks=[]» nel flusso: Prova Android su emulatore dedicato.
launch();checks=[]
# DOC L0027: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
for code,autonym,work,guide in [('en','English','Work','Guide'),('es','Español','Trabajo','Guía'),('it','Italiano','Lavoro','Guida')]:
 # DOC L0028: Esegue il passaggio esplicito «tap_any(['Impostazioni','Settings','Ajustes']);tap_any([autonym]);tap_any(['Timer','Temporizador'])» nel flusso: Prova Android su emulatore dedicato.
 tap_any(['Impostazioni','Settings','Ajustes']);tap_any([autonym]);tap_any(['Timer','Temporizador'])
 # DOC L0029: Esegue il passaggio esplicito «launch()» nel flusso: Prova Android su emulatore dedicato.
 launch()
 # DOC L0030: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
 labels=[n.get('text') for n in tree().iter('node')]
 # DOC L0031: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
 assert work in labels and guide in labels,(code,labels)
 # DOC L0032: Esegue il passaggio esplicito «prefs=ET.fromstring(run('exec-out','run-as',package,'cat','shared_prefs/tabata-appearance.xml'))» nel flusso: Prova Android su emulatore dedicato.
 prefs=ET.fromstring(run('exec-out','run-as',package,'cat','shared_prefs/tabata-appearance.xml'))
 # DOC L0033: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
 assert prefs.find("string[@name='language']").text==code
 # DOC L0034: Esegue il passaggio esplicito «Path(f'output/screenshots/android-{code}-home-v180.png').write_bytes(run('exec-out','screencap','-p'))» nel flusso: Prova Android su emulatore dedicato.
 Path(f'output/screenshots/android-{code}-home-v180.png').write_bytes(run('exec-out','screencap','-p'))
 # DOC L0035: Esegue il passaggio esplicito «checks.append({'language':code,'uiTranslated':True,'persistedAfterForceStop':True})» nel flusso: Prova Android su emulatore dedicato.
 checks.append({'language':code,'uiTranslated':True,'persistedAfterForceStop':True})
# DOC L0036: Esegue il passaggio esplicito «Path('output/android-languages.json').write_text(json.dumps({'version':'1.8.0','checks':checks,'physicalDeviceTested':False},indent=2)+'\n',encoding='utf-8')» nel flusso: Prova Android su emulatore dedicato.
Path('output/android-languages.json').write_text(json.dumps({'version':'1.8.0','checks':checks,'physicalDeviceTested':False},indent=2)+'\n',encoding='utf-8')
# DOC L0037: Esegue il passaggio esplicito «print('Android: English, Español, Italiano conservati dopo force-stop; schermate verificate.')» nel flusso: Prova Android su emulatore dedicato.
print('Android: English, Español, Italiano conservati dopo force-stop; schermate verificate.')
