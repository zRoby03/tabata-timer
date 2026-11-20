"""Run only against a dedicated emulator with the debug APK already installed.
Checks a real process death, durable Room data and exactly-once checkpoint recovery.
No third party Python packages required. Does not clear or uninstall the app.
"""
import json
import os
from pathlib import Path
import re
import sqlite3
import subprocess
import time
import tempfile
import xml.etree.ElementTree as ET

sdk = Path(os.environ.get('ANDROID_HOME', Path.home() / 'AppData/Local/Android/Sdk'))
adb = str(sdk / 'platform-tools/adb.exe')
package = 'it.tabatastudio'
Path('tmp/android-smoke').mkdir(parents=True, exist_ok=True)
run_directory = Path(tempfile.mkdtemp(prefix='run-', dir='tmp/android-smoke'))

def run(*args, check=True):
    return subprocess.run([adb, *args], capture_output=True, check=check)

def launch():
    run('shell', 'am', 'start', '-n', package + '/.MainActivity')

def hierarchy():
    result = run('shell', 'uiautomator', 'dump', '/sdcard/tabata-test-window.xml')
    if b'dumped to:' not in result.stdout:
        raise RuntimeError('UI emulatore non pronta')
    return ET.fromstring(run('shell', 'cat', '/sdcard/tabata-test-window.xml').stdout)

def wait_home():
    for _ in range(10):
        time.sleep(1)
        try:
            tree = hierarchy()
        except RuntimeError:
            continue
        if any(n.get('text') in ['Avvia', 'Start', 'Iniciar'] for n in tree.iter('node')):
            return tree
    raise AssertionError('Home non disponibile')

def snapshot(label):
    target = run_directory / label
    target.mkdir(parents=True, exist_ok=True)
    for suffix in ['', '-wal']:
        result = run('exec-out', 'run-as', package, 'cat', 'databases/tabata-studio.db' + suffix, check=False)
        if result.returncode == 0:
            (target / ('tabata.db' + suffix)).write_bytes(result.stdout)
    connection = sqlite3.connect(target / 'tabata.db')
    data = {table: connection.execute('SELECT * FROM ' + table).fetchall() for table in ['workout', 'session_record', 'session_checkpoint']}
    connection.close()
    return data

def wait_recovery(expected_sessions, label):
    # A rendered window can still belong to the process being replaced. Wait for
    # the durable transaction, rather than treating a cached UI tree as readiness.
    for poll in range(20):
        data = snapshot(f'{label}-{poll}')
        if len(data['session_record']) == expected_sessions and not data['session_checkpoint']:
            return data
        time.sleep(0.5)
    raise AssertionError('Recupero Room non completato entro il limite del test')

run('shell', 'input', 'keyevent', 'KEYCODE_WAKEUP')
run('shell', 'wm', 'dismiss-keyguard')
if int(run('shell', 'getprop', 'ro.build.version.sdk').stdout) >= 33:
    run('shell', 'pm', 'grant', package, 'android.permission.POST_NOTIFICATIONS')
run('shell', 'input', 'keyevent', 'KEYCODE_BACK')
run('shell', 'am', 'force-stop', package)
launch()
tree = wait_home()
before = snapshot('before')
# The default quick timer is long enough to copy the DB before completion.
for attempt in range(2):
    node = next(n for n in tree.iter('node') if n.get('text') in ['Avvia', 'Start', 'Iniciar'])
    x1, y1, x2, y2 = map(int, re.findall(r'\d+', node.get('bounds')))
    run('shell', 'input', 'tap', str((x1 + x2) // 2), str((y1 + y2) // 2))
    for poll in range(5):
        time.sleep(0.6)
        active = snapshot(f'active-{attempt}-{poll}')
        if active['session_checkpoint']:
            break
    if active['session_checkpoint']:
        break
    tree = hierarchy()
assert active['session_checkpoint'], 'Sessione non avviata dal tap sull’emulatore'
Path('output/screenshots').mkdir(parents=True, exist_ok=True)
Path('output/screenshots/android-timer.png').write_bytes(run('exec-out', 'screencap', '-p').stdout)
run('shell', 'cmd', 'statusbar', 'expand-notifications')
time.sleep(0.6)  # Wait only for the notification drawer's opening animation.
Path('output/screenshots/android-notification-v180.png').write_bytes(run('exec-out', 'screencap', '-p').stdout)
run('shell', 'input', 'keyevent', 'KEYCODE_BACK')
run('shell', 'am', 'force-stop', package)
killed = snapshot('killed')
assert len(killed['session_checkpoint']) == 1, 'Checkpoint non scritto prima della morte del processo'
launch()
wait_home()
after = wait_recovery(len(before['session_record']) + 1, 'after')
assert len(after['session_record']) == len(before['session_record']) + 1
assert after['workout'] == before['workout'], 'Workout modificati al riavvio'
assert len(after['session_checkpoint']) == 0
recovered = json.loads(after['session_record'][-1][1])
assert recovered['status'] == 'INTERRUPTED'
Path('output/screenshots/android-home.png').write_bytes(run('exec-out', 'screencap', '-p').stdout)
run('shell', 'am', 'force-stop', package)
launch()
wait_home()
again = wait_recovery(len(after['session_record']), 'again')
assert len(again['session_record']) == len(after['session_record']), 'Sessione duplicata al secondo avvio'
installed_version = re.search(r'versionName=([^\s]+)', run('shell', 'dumpsys', 'package', package).stdout.decode('utf-8')).group(1)
report = {'androidVersion': installed_version, 'workoutsPreserved': len(after['workout']), 'sessionsBefore': len(before['session_record']), 'sessionsAfter': len(after['session_record']), 'sessionsAfterSecondRestart': len(again['session_record']), 'recoveredStatus': recovered['status'], 'checkpointCleared': True}
Path('output/android-smoke.json').write_text(json.dumps(report, indent=2), encoding='utf-8')
print(json.dumps(report, indent=2))
