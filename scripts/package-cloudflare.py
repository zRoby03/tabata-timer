"""Prepare a buildless Cloudflare Pages upload, without APKs or personal data."""
from pathlib import Path
import hashlib
import json
import zipfile

ROOT = Path(__file__).resolve().parent.parent
WEB = ROOT / 'web'
OUTPUT = ROOT / 'output' / 'cloudflare'
DESTINATION = OUTPUT / 'Tabata Timer.zip'
HEADERS = ROOT / 'hosting' / 'cloudflare' / '_headers'
MAX_ASSET_BYTES = 25 * 1024 * 1024

files = {
    path.relative_to(WEB).as_posix(): path
    for path in sorted(WEB.rglob('*'))
    if path.is_file() and path.relative_to(WEB).parts[0] != 'tests'
}
files['_headers'] = HEADERS
assert 'index.html' in files
assert len(files) <= 1000, 'Dashboard Direct Upload supports at most 1000 files.'
assert all(path.stat().st_size <= MAX_ASSET_BYTES for path in files.values())
assert all(not name.lower().endswith(('.apk', '.aab', '.jks', '.keystore')) for name in files)

OUTPUT.mkdir(parents=True, exist_ok=True)
with zipfile.ZipFile(DESTINATION, 'w', compression=zipfile.ZIP_DEFLATED) as archive:
    for name, path in files.items():
        archive.write(path, name)
with zipfile.ZipFile(DESTINATION) as archive:
    assert archive.testzip() is None
    assert set(archive.namelist()) == set(files)
    for name, path in files.items():
        assert archive.read(name) == path.read_bytes(), name

report = {
    'title': 'Tabata Timer',
    'provider': 'Cloudflare Pages',
    'archive': DESTINATION.relative_to(ROOT).as_posix(),
    'sha256': hashlib.sha256(DESTINATION.read_bytes()).hexdigest(),
    'files': len(files),
    'runtimeFilesUnchanged': True,
    'apkIncluded': False,
    'adsIncluded': False,
    'largestAssetBytes': max(path.stat().st_size for path in files.values()),
    'checks': ['ZIP integrity', 'exact source bytes', 'file count', '25 MiB asset limit'],
}
(OUTPUT / 'package.json').write_text(json.dumps(report, indent=2) + '\n', encoding='utf-8')
print(json.dumps(report))
