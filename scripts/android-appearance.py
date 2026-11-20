# DOC L0001: Esegue il passaggio esplicito «"""QA on a dedicated emulator with the debug APK installed. Changes appearance only."""» nel flusso: Prova Android su emulatore dedicato.
"""QA on a dedicated emulator with the debug APK installed. Changes appearance only."""
# DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import json. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import json
# DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import os. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import os
# DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: from pathlib import Path. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
from pathlib import Path
# DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import re. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import re
# DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import subprocess. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import subprocess
# DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import time. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import time
# DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import xml.etree.ElementTree as ET. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import xml.etree.ElementTree as ET

# DOC L0010: Esegue il passaggio esplicito «adb = str(Path(os.environ.get('ANDROID_HOME', Path.home() / 'AppData/Local/Android/Sdk')) / 'platform-tools/adb.exe')» nel flusso: Prova Android su emulatore dedicato.
adb = str(Path(os.environ.get('ANDROID_HOME', Path.home() / 'AppData/Local/Android/Sdk')) / 'platform-tools/adb.exe')
# DOC L0011: Assegna il file al namespace = 'it.tabatastudio', distinto dai package UI, dominio, dati e servizio.
package = 'it.tabatastudio'
# DOC L0012: run: Passaggio «run» dello strumento: Prova Android su emulatore dedicato. Gli argomenti e le operazioni sono descritti nelle righe del corpo.
def run(*args):
    # DOC L0013: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di run.
    return subprocess.check_output([adb, *args], timeout=20)
# DOC L0014: tree: Passaggio «tree» dello strumento: Prova Android su emulatore dedicato. Gli argomenti e le operazioni sono descritti nelle righe del corpo.
def tree():
    # DOC L0015: Esegue il passaggio esplicito «result = subprocess.run([adb, 'shell', 'uiautomator', 'dump', '/sdcard/tabata-appearance.xml'], capture_output=True, timeout=20)» nel flusso: Prova Android su emulatore dedicato. Nel contesto di tree.
    result = subprocess.run([adb, 'shell', 'uiautomator', 'dump', '/sdcard/tabata-appearance.xml'], capture_output=True, timeout=20)
    # DOC L0016: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di tree.
    if result.returncode or b'dumped to:' not in result.stdout:
        # DOC L0017: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di tree.
        return ET.Element('not-ready')
    # DOC L0018: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di tree.
    return ET.fromstring(run('shell', 'cat', '/sdcard/tabata-appearance.xml'))
# DOC L0019: tap: Passaggio «tap» dello strumento: Prova Android su emulatore dedicato. Gli argomenti e le operazioni sono descritti nelle righe del corpo.
def tap(label):
    # DOC L0020: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di tap.
    for attempt in range(10):
        # DOC L0021: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di tap.
        for n in tree().iter('node'):
            # DOC L0022: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di tap.
            if n.get('text') == label or n.get('content-desc') == label:
                # DOC L0023: Esegue il passaggio esplicito «x1, y1, x2, y2 = map(int, re.findall(r'\d+', n.get('bounds')))» nel flusso: Prova Android su emulatore dedicato. Nel contesto di tap.
                x1, y1, x2, y2 = map(int, re.findall(r'\d+', n.get('bounds')))
                # DOC L0024: Esegue il passaggio esplicito «run('shell', 'input', 'tap', str((x1 + x2) // 2), str((y1 + y2) // 2))» nel flusso: Prova Android su emulatore dedicato. Nel contesto di tap.
                run('shell', 'input', 'tap', str((x1 + x2) // 2), str((y1 + y2) // 2))
                # DOC L0025: Esegue il passaggio esplicito «time.sleep(.3)» nel flusso: Prova Android su emulatore dedicato. Nel contesto di tap.
                time.sleep(.3)
                # DOC L0026: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di tap.
                return
        # DOC L0027: Esegue il passaggio esplicito «time.sleep(.5)» nel flusso: Prova Android su emulatore dedicato. Nel contesto di tap.
        time.sleep(.5)
    # DOC L0028: Esegue il passaggio esplicito «raise AssertionError('Control not found: ' + label)» nel flusso: Prova Android su emulatore dedicato. Nel contesto di tap.
    raise AssertionError('Control not found: ' + label)
# DOC L0029: Esegue il passaggio esplicito «Path('output/screenshots').mkdir(parents=True, exist_ok=True)» nel flusso: Prova Android su emulatore dedicato.
Path('output/screenshots').mkdir(parents=True, exist_ok=True)
# DOC L0030: Esegue il passaggio esplicito «run('shell', 'am', 'force-stop', package)» nel flusso: Prova Android su emulatore dedicato.
run('shell', 'am', 'force-stop', package)
# DOC L0031: Esegue il passaggio esplicito «run('shell', 'am', 'start', '-n', package + '/.MainActivity')» nel flusso: Prova Android su emulatore dedicato.
run('shell', 'am', 'start', '-n', package + '/.MainActivity')
# DOC L0032: Esegue il passaggio esplicito «tap('Impostazioni')» nel flusso: Prova Android su emulatore dedicato.
tap('Impostazioni')
# DOC L0033: Esegue il passaggio esplicito «tap('Scuro')» nel flusso: Prova Android su emulatore dedicato.
tap('Scuro')
# DOC L0034: Esegue il passaggio esplicito «tap('Giallo')» nel flusso: Prova Android su emulatore dedicato.
tap('Giallo')
# DOC L0035: Esegue il passaggio esplicito «Path('output/screenshots/android-dark-settings-v170.png').write_bytes(run('exec-out', 'screencap', '-p'))» nel flusso: Prova Android su emulatore dedicato.
Path('output/screenshots/android-dark-settings-v170.png').write_bytes(run('exec-out', 'screencap', '-p'))
# DOC L0036: Esegue il passaggio esplicito «tap('Timer')» nel flusso: Prova Android su emulatore dedicato.
tap('Timer')
# DOC L0037: Esegue il passaggio esplicito «run('shell', 'am', 'force-stop', package)» nel flusso: Prova Android su emulatore dedicato.
run('shell', 'am', 'force-stop', package)
# DOC L0038: Esegue il passaggio esplicito «run('shell', 'am', 'start', '-n', package + '/.MainActivity')» nel flusso: Prova Android su emulatore dedicato.
run('shell', 'am', 'start', '-n', package + '/.MainActivity')
# DOC L0039: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
for attempt in range(10):
    # DOC L0040: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    labels = [n.get('text') for n in tree().iter('node')]
    # DOC L0041: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
    if 'Tabata 1' in labels:
        # DOC L0042: Esegue il passaggio esplicito «break» nel flusso: Prova Android su emulatore dedicato.
        break
    # DOC L0043: Esegue il passaggio esplicito «time.sleep(.5)» nel flusso: Prova Android su emulatore dedicato.
    time.sleep(.5)
# DOC L0044: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
assert all(label in labels for label in ['Tabata 1', 'Tabata 2', 'Tabata 3'])
# DOC L0045: Esegue il passaggio esplicito «prefs = ET.fromstring(run('exec-out', 'run-as', package, 'cat', 'shared_prefs/tabata-appearance.xml'))» nel flusso: Prova Android su emulatore dedicato.
prefs = ET.fromstring(run('exec-out', 'run-as', package, 'cat', 'shared_prefs/tabata-appearance.xml'))
# DOC L0046: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert prefs.find("boolean[@name='dark']").get('value') == 'true'
# DOC L0047: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert prefs.find("string[@name='palette']").text == 'yellow'
# DOC L0048: Esegue il passaggio esplicito «Path('output/screenshots/android-dark-home-v170.png').write_bytes(run('exec-out', 'screencap', '-p'))» nel flusso: Prova Android su emulatore dedicato.
Path('output/screenshots/android-dark-home-v170.png').write_bytes(run('exec-out', 'screencap', '-p'))
# DOC L0049: Esegue il passaggio esplicito «Path('output/android-appearance.json').write_text(json.dumps({'theme': 'dark', 'palette': 'yellow', 'preservedAfterForceStop': True, 'numberedPresets': True}, indent=2), encoding='» nel flusso: Prova Android su emulatore dedicato.
Path('output/android-appearance.json').write_text(json.dumps({'theme': 'dark', 'palette': 'yellow', 'preservedAfterForceStop': True, 'numberedPresets': True}, indent=2), encoding='utf-8')
# DOC L0050: Esegue il passaggio esplicito «print('Android appearance: dark/yellow preserved after force-stop, numbered presets visible.')» nel flusso: Prova Android su emulatore dedicato.
print('Android appearance: dark/yellow preserved after force-stop, numbered presets visible.')
