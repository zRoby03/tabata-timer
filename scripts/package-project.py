"""Package the local sources and QA reports without SDK paths, caches or temporary media."""
from pathlib import Path
import os
import zipfile

ROOT = Path(__file__).resolve().parent.parent
VERSION = '1.9.0'
DESTINATION = ROOT / 'output' / f'Tabata-Timer-{VERSION}-sorgenti.zip'
EXCLUDED_DIRS = {'.git', '.gradle', '.kotlin', '.idea', 'build', 'node_modules', '__pycache__'}
EXCLUDED_FILES = {'local.properties', '.DS_Store'}
files = [ROOT / 'README.md', ROOT / '.gitignore']
for folder in ('web', 'android', 'assets', 'docs', 'fixtures', 'scripts', 'locales', 'hosting'):
    for directory, names, filenames in os.walk(ROOT / folder):
        names[:] = sorted(name for name in names if name not in EXCLUDED_DIRS)
        files.extend(Path(directory) / name for name in sorted(filenames) if name not in EXCLUDED_FILES and not name.endswith('.iml'))
files.extend(sorted((ROOT / 'output').glob('browser-*.json')))
files.extend(path for path in (ROOT / 'output' / 'cloudflare').glob('*.json') if path.is_file())
files.extend(path for path in [ROOT / 'output' / 'android-languages.json', ROOT / 'output' / 'android-modes.json', ROOT / 'output' / 'android-background.json', ROOT / 'output' / 'android-smoke.json', ROOT / 'output' / f'verification-v{VERSION}.json', ROOT / 'output' / 'publication.json'] if path.exists())
DESTINATION.parent.mkdir(parents=True, exist_ok=True)
with zipfile.ZipFile(DESTINATION, 'w', compression=zipfile.ZIP_DEFLATED) as archive:
    for path in files:
        archive.write(path, f'Tabata-Timer-{VERSION}/{path.relative_to(ROOT).as_posix()}')
with zipfile.ZipFile(DESTINATION) as archive:
    assert archive.testzip() is None
print(f'{DESTINATION.name}: {len(files)} file, {DESTINATION.stat().st_size} byte; integrità ZIP verificata.')
