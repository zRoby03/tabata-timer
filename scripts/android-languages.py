"""Language/persistence screenshots on a dedicated Android emulator; never starts a timer."""
import json,os,re,subprocess,time,xml.etree.ElementTree as ET
from pathlib import Path
adb=str(Path(os.environ.get('ANDROID_HOME',Path.home()/'AppData/Local/Android/Sdk'))/'platform-tools/adb.exe')
package='it.tabatastudio'
def run(*args):return subprocess.check_output([adb,*args],timeout=25)
def tree():
 run('shell','uiautomator','dump','/sdcard/tabata-languages.xml')
 return ET.fromstring(run('shell','cat','/sdcard/tabata-languages.xml'))
def tap_any(labels):
 for attempt in range(12):
  for n in tree().iter('node'):
   if n.get('text') in labels or n.get('content-desc') in labels:
    x1,y1,x2,y2=map(int,re.findall(r'\d+',n.get('bounds')))
    run('shell','input','tap',str((x1+x2)//2),str((y1+y2)//2));time.sleep(.35);return
  time.sleep(.3)
 raise AssertionError('Missing '+repr(labels))
def launch():
 run('shell','am','force-stop',package);run('shell','am','start','-n',package+'/.MainActivity')
 for attempt in range(12):
  time.sleep(.4)
  if any(n.get('text')=='Tabata Timer' for n in tree().iter('node')):return
 raise AssertionError('Home not ready')
run('shell','input','keyevent','KEYCODE_WAKEUP');run('shell','wm','dismiss-keyguard');run('shell','input','keyevent','KEYCODE_BACK')
Path('output/screenshots').mkdir(parents=True,exist_ok=True)
launch();checks=[]
for code,autonym,work,guide in [('en','English','Work','Guide'),('es','Español','Trabajo','Guía'),('it','Italiano','Lavoro','Guida')]:
 tap_any(['Impostazioni','Settings','Ajustes']);tap_any([autonym]);tap_any(['Timer','Temporizador'])
 launch()
 labels=[n.get('text') for n in tree().iter('node')]
 assert work in labels and guide in labels,(code,labels)
 prefs=ET.fromstring(run('exec-out','run-as',package,'cat','shared_prefs/tabata-appearance.xml'))
 assert prefs.find("string[@name='language']").text==code
 Path(f'output/screenshots/android-{code}-home-v180.png').write_bytes(run('exec-out','screencap','-p'))
 checks.append({'language':code,'uiTranslated':True,'persistedAfterForceStop':True})
Path('output/android-languages.json').write_text(json.dumps({'version':'1.8.0','checks':checks,'physicalDeviceTested':False},indent=2)+'\n',encoding='utf-8')
print('Android: English, Español, Italiano conservati dopo force-stop; schermate verificate.')
