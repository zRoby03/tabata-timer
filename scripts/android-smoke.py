# DOC L0001: Esegue il passaggio esplicito «"""Run only against a dedicated emulator with the debug APK already installed.» nel flusso: Prova Android su emulatore dedicato.
"""Run only against a dedicated emulator with the debug APK already installed.
Checks a real process death, durable Room data and exactly-once checkpoint recovery.
No third party Python packages required. Does not clear or uninstall the app.
"""
# DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import json. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import json
# DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import os. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import os
# DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: from pathlib import Path. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
from pathlib import Path
# DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import re. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import re
# DOC L0009: Rende disponibili le dipendenze esplicite di questa riga: import sqlite3. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import sqlite3
# DOC L0010: Rende disponibili le dipendenze esplicite di questa riga: import subprocess. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import subprocess
# DOC L0011: Rende disponibili le dipendenze esplicite di questa riga: import time. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import time
# DOC L0012: Rende disponibili le dipendenze esplicite di questa riga: import tempfile. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import tempfile
# DOC L0013: Rende disponibili le dipendenze esplicite di questa riga: import xml.etree.ElementTree as ET. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import xml.etree.ElementTree as ET

# DOC L0015: Esegue il passaggio esplicito «sdk = Path(os.environ.get('ANDROID_HOME', Path.home() / 'AppData/Local/Android/Sdk'))» nel flusso: Prova Android su emulatore dedicato.
sdk = Path(os.environ.get('ANDROID_HOME', Path.home() / 'AppData/Local/Android/Sdk'))
# DOC L0016: Esegue il passaggio esplicito «adb = str(sdk / 'platform-tools/adb.exe')» nel flusso: Prova Android su emulatore dedicato.
adb = str(sdk / 'platform-tools/adb.exe')
# DOC L0017: Assegna il file al namespace = 'it.tabatastudio', distinto dai package UI, dominio, dati e servizio.
package = 'it.tabatastudio'
# DOC L0018: Esegue il passaggio esplicito «Path('tmp/android-smoke').mkdir(parents=True, exist_ok=True)» nel flusso: Prova Android su emulatore dedicato.
Path('tmp/android-smoke').mkdir(parents=True, exist_ok=True)
# DOC L0019: Esegue il passaggio esplicito «run_directory = Path(tempfile.mkdtemp(prefix='run-', dir='tmp/android-smoke'))» nel flusso: Prova Android su emulatore dedicato.
run_directory = Path(tempfile.mkdtemp(prefix='run-', dir='tmp/android-smoke'))

# DOC L0021: run: Passaggio «run» dello strumento: Prova Android su emulatore dedicato. Gli argomenti e le operazioni sono descritti nelle righe del corpo.
def run(*args, check=True):
    # DOC L0022: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di run.
    return subprocess.run([adb, *args], capture_output=True, check=check)

# DOC L0024: launch: Passaggio «launch» dello strumento: Prova Android su emulatore dedicato. Gli argomenti e le operazioni sono descritti nelle righe del corpo.
def launch():
    # DOC L0025: Esegue il passaggio esplicito «run('shell', 'am', 'start', '-n', package + '/.MainActivity')» nel flusso: Prova Android su emulatore dedicato. Nel contesto di launch.
    run('shell', 'am', 'start', '-n', package + '/.MainActivity')

# DOC L0027: hierarchy: Passaggio «hierarchy» dello strumento: Prova Android su emulatore dedicato. Gli argomenti e le operazioni sono descritti nelle righe del corpo.
def hierarchy():
    # DOC L0028: Esegue il passaggio esplicito «result = run('shell', 'uiautomator', 'dump', '/sdcard/tabata-test-window.xml')» nel flusso: Prova Android su emulatore dedicato. Nel contesto di hierarchy.
    result = run('shell', 'uiautomator', 'dump', '/sdcard/tabata-test-window.xml')
    # DOC L0029: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di hierarchy.
    if b'dumped to:' not in result.stdout:
        # DOC L0030: Esegue il passaggio esplicito «raise RuntimeError('UI emulatore non pronta')» nel flusso: Prova Android su emulatore dedicato. Nel contesto di hierarchy.
        raise RuntimeError('UI emulatore non pronta')
    # DOC L0031: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di hierarchy.
    return ET.fromstring(run('shell', 'cat', '/sdcard/tabata-test-window.xml').stdout)

# DOC L0033: wait_home: Passaggio «wait_home» dello strumento: Prova Android su emulatore dedicato. Gli argomenti e le operazioni sono descritti nelle righe del corpo.
def wait_home():
    # DOC L0034: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di wait_home.
    for _ in range(10):
        # DOC L0035: Esegue il passaggio esplicito «time.sleep(1)» nel flusso: Prova Android su emulatore dedicato. Nel contesto di wait_home.
        time.sleep(1)
        # DOC L0036: Esegue il passaggio esplicito «try:» nel flusso: Prova Android su emulatore dedicato. Nel contesto di wait_home.
        try:
            # DOC L0037: Esegue il passaggio esplicito «tree = hierarchy()» nel flusso: Prova Android su emulatore dedicato. Nel contesto di wait_home.
            tree = hierarchy()
        # DOC L0038: Esegue il passaggio esplicito «except RuntimeError:» nel flusso: Prova Android su emulatore dedicato. Nel contesto di wait_home.
        except RuntimeError:
            # DOC L0039: Esegue il passaggio esplicito «continue» nel flusso: Prova Android su emulatore dedicato. Nel contesto di wait_home.
            continue
        # DOC L0040: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di wait_home.
        if any(n.get('text') in ['Avvia', 'Start', 'Iniciar'] for n in tree.iter('node')):
            # DOC L0041: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di wait_home.
            return tree
    # DOC L0042: Esegue il passaggio esplicito «raise AssertionError('Home non disponibile')» nel flusso: Prova Android su emulatore dedicato. Nel contesto di wait_home.
    raise AssertionError('Home non disponibile')

# DOC L0044: snapshot: Crea il riepilogo corrente, usato per checkpoint e recupero di una sessione interrotta.
def snapshot(label):
    # DOC L0045: Esegue il passaggio esplicito «target = run_directory / label» nel flusso: Prova Android su emulatore dedicato. Nel contesto di snapshot.
    target = run_directory / label
    # DOC L0046: Esegue il passaggio esplicito «target.mkdir(parents=True, exist_ok=True)» nel flusso: Prova Android su emulatore dedicato. Nel contesto di snapshot.
    target.mkdir(parents=True, exist_ok=True)
    # DOC L0047: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di snapshot.
    for suffix in ['', '-wal']:
        # DOC L0048: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di snapshot.
        result = run('exec-out', 'run-as', package, 'cat', 'databases/tabata-studio.db' + suffix, check=False)
        # DOC L0049: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di snapshot.
        if result.returncode == 0:
            # DOC L0050: Esegue il passaggio esplicito «(target / ('tabata.db' + suffix)).write_bytes(result.stdout)» nel flusso: Prova Android su emulatore dedicato. Nel contesto di snapshot.
            (target / ('tabata.db' + suffix)).write_bytes(result.stdout)
    # DOC L0051: Esegue il passaggio esplicito «connection = sqlite3.connect(target / 'tabata.db')» nel flusso: Prova Android su emulatore dedicato. Nel contesto di snapshot.
    connection = sqlite3.connect(target / 'tabata.db')
    # DOC L0052: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di snapshot.
    data = {table: connection.execute('SELECT * FROM ' + table).fetchall() for table in ['workout', 'session_record', 'session_checkpoint']}
    # DOC L0053: Esegue il passaggio esplicito «connection.close()» nel flusso: Prova Android su emulatore dedicato. Nel contesto di snapshot.
    connection.close()
    # DOC L0054: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di snapshot.
    return data

# DOC L0056: wait_recovery: Passaggio «wait_recovery» dello strumento: Prova Android su emulatore dedicato. Gli argomenti e le operazioni sono descritti nelle righe del corpo.
def wait_recovery(expected_sessions, label):
    # A rendered window can still belong to the process being replaced. Wait for
    # the durable transaction, rather than treating a cached UI tree as readiness.
    # DOC L0059: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di wait_recovery.
    for poll in range(20):
        # DOC L0060: Esegue il passaggio esplicito «data = snapshot(f'{label}-{poll}')» nel flusso: Prova Android su emulatore dedicato. Nel contesto di wait_recovery.
        data = snapshot(f'{label}-{poll}')
        # DOC L0061: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di wait_recovery.
        if len(data['session_record']) == expected_sessions and not data['session_checkpoint']:
            # DOC L0062: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di wait_recovery.
            return data
        # DOC L0063: Esegue il passaggio esplicito «time.sleep(0.5)» nel flusso: Prova Android su emulatore dedicato. Nel contesto di wait_recovery.
        time.sleep(0.5)
    # DOC L0064: Esegue il passaggio esplicito «raise AssertionError('Recupero Room non completato entro il limite del test')» nel flusso: Prova Android su emulatore dedicato. Nel contesto di wait_recovery.
    raise AssertionError('Recupero Room non completato entro il limite del test')

# DOC L0066: Esegue il passaggio esplicito «run('shell', 'input', 'keyevent', 'KEYCODE_WAKEUP')» nel flusso: Prova Android su emulatore dedicato.
run('shell', 'input', 'keyevent', 'KEYCODE_WAKEUP')
# DOC L0067: Esegue il passaggio esplicito «run('shell', 'wm', 'dismiss-keyguard')» nel flusso: Prova Android su emulatore dedicato.
run('shell', 'wm', 'dismiss-keyguard')
# DOC L0068: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
if int(run('shell', 'getprop', 'ro.build.version.sdk').stdout) >= 33:
    # DOC L0069: Esegue il passaggio esplicito «run('shell', 'pm', 'grant', package, 'android.permission.POST_NOTIFICATIONS')» nel flusso: Prova Android su emulatore dedicato.
    run('shell', 'pm', 'grant', package, 'android.permission.POST_NOTIFICATIONS')
# DOC L0070: Esegue il passaggio esplicito «run('shell', 'input', 'keyevent', 'KEYCODE_BACK')» nel flusso: Prova Android su emulatore dedicato.
run('shell', 'input', 'keyevent', 'KEYCODE_BACK')
# DOC L0071: Esegue il passaggio esplicito «run('shell', 'am', 'force-stop', package)» nel flusso: Prova Android su emulatore dedicato.
run('shell', 'am', 'force-stop', package)
# DOC L0072: Esegue il passaggio esplicito «launch()» nel flusso: Prova Android su emulatore dedicato.
launch()
# DOC L0073: Esegue il passaggio esplicito «tree = wait_home()» nel flusso: Prova Android su emulatore dedicato.
tree = wait_home()
# DOC L0074: Esegue il passaggio esplicito «before = snapshot('before')» nel flusso: Prova Android su emulatore dedicato.
before = snapshot('before')
# The default quick timer is long enough to copy the DB before completion.
# DOC L0076: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
for attempt in range(2):
    # DOC L0077: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    node = next(n for n in tree.iter('node') if n.get('text') in ['Avvia', 'Start', 'Iniciar'])
    # DOC L0078: Esegue il passaggio esplicito «x1, y1, x2, y2 = map(int, re.findall(r'\d+', node.get('bounds')))» nel flusso: Prova Android su emulatore dedicato.
    x1, y1, x2, y2 = map(int, re.findall(r'\d+', node.get('bounds')))
    # DOC L0079: Esegue il passaggio esplicito «run('shell', 'input', 'tap', str((x1 + x2) // 2), str((y1 + y2) // 2))» nel flusso: Prova Android su emulatore dedicato.
    run('shell', 'input', 'tap', str((x1 + x2) // 2), str((y1 + y2) // 2))
    # DOC L0080: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    for poll in range(5):
        # DOC L0081: Esegue il passaggio esplicito «time.sleep(0.6)» nel flusso: Prova Android su emulatore dedicato.
        time.sleep(0.6)
        # DOC L0082: Esegue il passaggio esplicito «active = snapshot(f'active-{attempt}-{poll}')» nel flusso: Prova Android su emulatore dedicato.
        active = snapshot(f'active-{attempt}-{poll}')
        # DOC L0083: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
        if active['session_checkpoint']:
            # DOC L0084: Esegue il passaggio esplicito «break» nel flusso: Prova Android su emulatore dedicato.
            break
    # DOC L0085: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
    if active['session_checkpoint']:
        # DOC L0086: Esegue il passaggio esplicito «break» nel flusso: Prova Android su emulatore dedicato.
        break
    # DOC L0087: Esegue il passaggio esplicito «tree = hierarchy()» nel flusso: Prova Android su emulatore dedicato.
    tree = hierarchy()
# DOC L0088: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert active['session_checkpoint'], 'Sessione non avviata dal tap sull’emulatore'
# DOC L0089: Esegue il passaggio esplicito «Path('output/screenshots').mkdir(parents=True, exist_ok=True)» nel flusso: Prova Android su emulatore dedicato.
Path('output/screenshots').mkdir(parents=True, exist_ok=True)
# DOC L0090: Esegue il passaggio esplicito «Path('output/screenshots/android-timer.png').write_bytes(run('exec-out', 'screencap', '-p').stdout)» nel flusso: Prova Android su emulatore dedicato.
Path('output/screenshots/android-timer.png').write_bytes(run('exec-out', 'screencap', '-p').stdout)
# DOC L0091: Esegue il passaggio esplicito «run('shell', 'cmd', 'statusbar', 'expand-notifications')» nel flusso: Prova Android su emulatore dedicato.
run('shell', 'cmd', 'statusbar', 'expand-notifications')
# DOC L0092: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
time.sleep(0.6)  # Wait only for the notification drawer's opening animation.
# DOC L0093: Esegue il passaggio esplicito «Path('output/screenshots/android-notification-v180.png').write_bytes(run('exec-out', 'screencap', '-p').stdout)» nel flusso: Prova Android su emulatore dedicato.
Path('output/screenshots/android-notification-v180.png').write_bytes(run('exec-out', 'screencap', '-p').stdout)
# DOC L0094: Esegue il passaggio esplicito «run('shell', 'input', 'keyevent', 'KEYCODE_BACK')» nel flusso: Prova Android su emulatore dedicato.
run('shell', 'input', 'keyevent', 'KEYCODE_BACK')
# DOC L0095: Esegue il passaggio esplicito «run('shell', 'am', 'force-stop', package)» nel flusso: Prova Android su emulatore dedicato.
run('shell', 'am', 'force-stop', package)
# DOC L0096: Esegue il passaggio esplicito «killed = snapshot('killed')» nel flusso: Prova Android su emulatore dedicato.
killed = snapshot('killed')
# DOC L0097: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert len(killed['session_checkpoint']) == 1, 'Checkpoint non scritto prima della morte del processo'
# DOC L0098: Esegue il passaggio esplicito «launch()» nel flusso: Prova Android su emulatore dedicato.
launch()
# DOC L0099: Esegue il passaggio esplicito «wait_home()» nel flusso: Prova Android su emulatore dedicato.
wait_home()
# DOC L0100: Esegue il passaggio esplicito «after = wait_recovery(len(before['session_record']) + 1, 'after')» nel flusso: Prova Android su emulatore dedicato.
after = wait_recovery(len(before['session_record']) + 1, 'after')
# DOC L0101: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert len(after['session_record']) == len(before['session_record']) + 1
# DOC L0102: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert after['workout'] == before['workout'], 'Workout modificati al riavvio'
# DOC L0103: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert len(after['session_checkpoint']) == 0
# DOC L0104: Esegue il passaggio esplicito «recovered = json.loads(after['session_record'][-1][1])» nel flusso: Prova Android su emulatore dedicato.
recovered = json.loads(after['session_record'][-1][1])
# DOC L0105: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert recovered['status'] == 'INTERRUPTED'
# DOC L0106: Esegue il passaggio esplicito «Path('output/screenshots/android-home.png').write_bytes(run('exec-out', 'screencap', '-p').stdout)» nel flusso: Prova Android su emulatore dedicato.
Path('output/screenshots/android-home.png').write_bytes(run('exec-out', 'screencap', '-p').stdout)
# DOC L0107: Esegue il passaggio esplicito «run('shell', 'am', 'force-stop', package)» nel flusso: Prova Android su emulatore dedicato.
run('shell', 'am', 'force-stop', package)
# DOC L0108: Esegue il passaggio esplicito «launch()» nel flusso: Prova Android su emulatore dedicato.
launch()
# DOC L0109: Esegue il passaggio esplicito «wait_home()» nel flusso: Prova Android su emulatore dedicato.
wait_home()
# DOC L0110: Esegue il passaggio esplicito «again = wait_recovery(len(after['session_record']), 'again')» nel flusso: Prova Android su emulatore dedicato.
again = wait_recovery(len(after['session_record']), 'again')
# DOC L0111: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert len(again['session_record']) == len(after['session_record']), 'Sessione duplicata al secondo avvio'
# DOC L0112: Esegue il passaggio esplicito «installed_version = re.search(r'versionName=([^\s]+)', run('shell', 'dumpsys', 'package', package).stdout.decode('utf-8')).group(1)» nel flusso: Prova Android su emulatore dedicato.
installed_version = re.search(r'versionName=([^\s]+)', run('shell', 'dumpsys', 'package', package).stdout.decode('utf-8')).group(1)
# DOC L0113: Esegue il passaggio esplicito «report = {'androidVersion': installed_version, 'workoutsPreserved': len(after['workout']), 'sessionsBefore': len(before['session_record']), 'sessionsAfter': len(after['session_reco» nel flusso: Prova Android su emulatore dedicato.
report = {'androidVersion': installed_version, 'workoutsPreserved': len(after['workout']), 'sessionsBefore': len(before['session_record']), 'sessionsAfter': len(after['session_record']), 'sessionsAfterSecondRestart': len(again['session_record']), 'recoveredStatus': recovered['status'], 'checkpointCleared': True}
# DOC L0114: Esegue il passaggio esplicito «Path('output/android-smoke.json').write_text(json.dumps(report, indent=2), encoding='utf-8')» nel flusso: Prova Android su emulatore dedicato.
Path('output/android-smoke.json').write_text(json.dumps(report, indent=2), encoding='utf-8')
# DOC L0115: Esegue il passaggio esplicito «print(json.dumps(report, indent=2))» nel flusso: Prova Android su emulatore dedicato.
print(json.dumps(report, indent=2))
