"""Prepare a buildless Cloudflare Pages upload, without APKs or personal data."""
from pathlib import Path
from ast import literal_eval
import argparse
import re
import hashlib
import json
import zipfile

ROOT = Path(__file__).resolve().parent.parent
WEB = ROOT / 'web'
parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument('--output', type=Path, default=ROOT / 'output' / 'cloudflare')
OUTPUT = parser.parse_args().output.resolve()
DESTINATION = OUTPUT / 'Tabata Timer.zip'
HEADERS = ROOT / 'hosting' / 'cloudflare' / '_headers'
MAX_ASSET_BYTES = 25 * 1024 * 1024

# The service worker already enumerates all runtime assets. The two additional
# files are served directly and do not need to be cached for offline use.
worker = (WEB / 'sw.js').read_text(encoding='utf-8')
manifest = re.search(r'^const ASSETS = (\[.*\]);$', worker, re.MULTILINE)
assert manifest, 'Missing offline asset manifest'
asset_names = [entry.removeprefix('./') for entry in literal_eval(manifest[1]) if entry != './']
asset_names += ['sw.js', 'ads.txt']
assert len(asset_names) == len(set(asset_names))
files = {}
for name in sorted(asset_names):
    path = WEB / name
    assert path.is_file() and path.resolve().is_relative_to(WEB), name
    assert not any(part.startswith('.') for part in Path(name).parts), name
    assert path.suffix in {'.html', '.css', '.js', '.wav', '.woff2', '.png', '.svg', '.webmanifest', '.txt'}, name
    files[name] = path
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
    'archive': str(DESTINATION),
    'sha256': hashlib.sha256(DESTINATION.read_bytes()).hexdigest(),
    'files': len(files),
    'apkIncluded': False,
    'adsTxtIncluded': 'ads.txt' in files,
    'largestAssetBytes': max(path.stat().st_size for path in files.values()),
    'checks': ['ZIP integrity', 'exact source bytes', 'file count', '25 MiB asset limit'],
}
print(json.dumps(report))
