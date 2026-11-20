"""QA on a dedicated emulator with the debug APK installed. Changes appearance only."""
import json
import os
from pathlib import Path
import re
import subprocess
import time
import xml.etree.ElementTree as ET

adb = str(Path(os.environ.get('ANDROID_HOME', Path.home() / 'AppData/Local/Android/Sdk')) / 'platform-tools/adb.exe')
package = 'it.tabatastudio'
def run(*args):
    return subprocess.check_output([adb, *args], timeout=20)
def tree():
    result = subprocess.run([adb, 'shell', 'uiautomator', 'dump', '/sdcard/tabata-appearance.xml'], capture_output=True, timeout=20)
    if result.returncode or b'dumped to:' not in result.stdout:
        return ET.Element('not-ready')
    return ET.fromstring(run('shell', 'cat', '/sdcard/tabata-appearance.xml'))
def tap(label):
    for attempt in range(10):
        for n in tree().iter('node'):
            if n.get('text') == label or n.get('content-desc') == label:
                x1, y1, x2, y2 = map(int, re.findall(r'\d+', n.get('bounds')))
                run('shell', 'input', 'tap', str((x1 + x2) // 2), str((y1 + y2) // 2))
                time.sleep(.3)
                return
        time.sleep(.5)
    raise AssertionError('Control not found: ' + label)
Path('output/screenshots').mkdir(parents=True, exist_ok=True)
run('shell', 'am', 'force-stop', package)
run('shell', 'am', 'start', '-n', package + '/.MainActivity')
tap('Impostazioni')
tap('Scuro')
tap('Giallo')
Path('output/screenshots/android-dark-settings-v170.png').write_bytes(run('exec-out', 'screencap', '-p'))
tap('Timer')
run('shell', 'am', 'force-stop', package)
run('shell', 'am', 'start', '-n', package + '/.MainActivity')
for attempt in range(10):
    labels = [n.get('text') for n in tree().iter('node')]
    if 'Tabata 1' in labels:
        break
    time.sleep(.5)
assert all(label in labels for label in ['Tabata 1', 'Tabata 2', 'Tabata 3'])
prefs = ET.fromstring(run('exec-out', 'run-as', package, 'cat', 'shared_prefs/tabata-appearance.xml'))
assert prefs.find("boolean[@name='dark']").get('value') == 'true'
assert prefs.find("string[@name='palette']").text == 'yellow'
Path('output/screenshots/android-dark-home-v170.png').write_bytes(run('exec-out', 'screencap', '-p'))
Path('output/android-appearance.json').write_text(json.dumps({'theme': 'dark', 'palette': 'yellow', 'preservedAfterForceStop': True, 'numberedPresets': True}, indent=2), encoding='utf-8')
print('Android appearance: dark/yellow preserved after force-stop, numbered presets visible.')
