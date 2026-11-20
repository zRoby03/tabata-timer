# DOC L0001: Esegue il passaggio esplicito «"""Prepare a buildless Cloudflare Pages upload, without APKs or personal data."""» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
"""Prepare a buildless Cloudflare Pages upload, without APKs or personal data."""
# DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: from pathlib import Path. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
from pathlib import Path
# DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import hashlib. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import hashlib
# DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import json. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import json
# DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import zipfile. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import zipfile

# DOC L0007: Esegue il passaggio esplicito «ROOT = Path(__file__).resolve().parent.parent» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
ROOT = Path(__file__).resolve().parent.parent
# DOC L0008: Esegue il passaggio esplicito «WEB = ROOT / 'web'» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
WEB = ROOT / 'web'
# DOC L0009: Esegue il passaggio esplicito «OUTPUT = ROOT / 'output' / 'cloudflare'» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
OUTPUT = ROOT / 'output' / 'cloudflare'
# DOC L0010: Esegue il passaggio esplicito «DESTINATION = OUTPUT / 'Tabata Timer.zip'» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
DESTINATION = OUTPUT / 'Tabata Timer.zip'
# DOC L0011: Esegue il passaggio esplicito «HEADERS = ROOT / 'hosting' / 'cloudflare' / '_headers'» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
HEADERS = ROOT / 'hosting' / 'cloudflare' / '_headers'
# DOC L0012: Esegue il passaggio esplicito «MAX_ASSET_BYTES = 25 * 1024 * 1024» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
MAX_ASSET_BYTES = 25 * 1024 * 1024

# DOC L0014: Esegue il passaggio esplicito «files = {» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
files = {
    # DOC L0015: Esegue il passaggio esplicito «path.relative_to(WEB).as_posix(): path» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
    path.relative_to(WEB).as_posix(): path
    # DOC L0016: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    for path in sorted(WEB.rglob('*'))
    # DOC L0017: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
    if path.is_file() and path.relative_to(WEB).parts[0] != 'tests'
# DOC L0018: Chiude un blocco, una chiamata o un valore composto; non aggiunge una nuova operazione.
}
# DOC L0019: Esegue il passaggio esplicito «files['_headers'] = HEADERS» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
files['_headers'] = HEADERS
# DOC L0020: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert 'index.html' in files
# DOC L0021: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
assert len(files) <= 1000, 'Dashboard Direct Upload supports at most 1000 files.'
# DOC L0022: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
assert all(path.stat().st_size <= MAX_ASSET_BYTES for path in files.values())
# DOC L0023: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
assert all(not name.lower().endswith(('.apk', '.aab', '.jks', '.keystore')) for name in files)

# DOC L0025: Esegue il passaggio esplicito «OUTPUT.mkdir(parents=True, exist_ok=True)» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
OUTPUT.mkdir(parents=True, exist_ok=True)
# DOC L0026: Esegue il passaggio esplicito «with zipfile.ZipFile(DESTINATION, 'w', compression=zipfile.ZIP_DEFLATED) as archive:» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
with zipfile.ZipFile(DESTINATION, 'w', compression=zipfile.ZIP_DEFLATED) as archive:
    # DOC L0027: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    for name, path in files.items():
        # DOC L0028: Esegue il passaggio esplicito «archive.write(path, name)» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
        archive.write(path, name)
# DOC L0029: Esegue il passaggio esplicito «with zipfile.ZipFile(DESTINATION) as archive:» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
with zipfile.ZipFile(DESTINATION) as archive:
    # DOC L0030: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert archive.testzip() is None
    # DOC L0031: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert set(archive.namelist()) == set(files)
    # DOC L0032: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    for name, path in files.items():
        # DOC L0033: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
        assert archive.read(name) == path.read_bytes(), name

# DOC L0035: Esegue il passaggio esplicito «report = {» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
report = {
    # DOC L0036: Esegue il passaggio esplicito «'title': 'Tabata Timer',» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
    'title': 'Tabata Timer',
    # DOC L0037: Esegue il passaggio esplicito «'provider': 'Cloudflare Pages',» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
    'provider': 'Cloudflare Pages',
    # DOC L0038: Esegue il passaggio esplicito «'archive': DESTINATION.relative_to(ROOT).as_posix(),» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
    'archive': DESTINATION.relative_to(ROOT).as_posix(),
    # DOC L0039: Esegue il passaggio esplicito «'sha256': hashlib.sha256(DESTINATION.read_bytes()).hexdigest(),» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
    'sha256': hashlib.sha256(DESTINATION.read_bytes()).hexdigest(),
    # DOC L0040: Esegue il passaggio esplicito «'files': len(files),» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
    'files': len(files),
    # DOC L0041: Esegue il passaggio esplicito «'runtimeFilesUnchanged': True,» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
    'runtimeFilesUnchanged': True,
    # DOC L0042: Esegue il passaggio esplicito «'apkIncluded': False,» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
    'apkIncluded': False,
    # DOC L0043: Esegue il passaggio esplicito «'adsIncluded': False,» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
    'adsIncluded': False,
    # DOC L0044: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    'largestAssetBytes': max(path.stat().st_size for path in files.values()),
    # DOC L0045: Esegue il passaggio esplicito «'checks': ['ZIP integrity', 'exact source bytes', 'file count', '25 MiB asset limit'],» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
    'checks': ['ZIP integrity', 'exact source bytes', 'file count', '25 MiB asset limit'],
# DOC L0046: Chiude un blocco, una chiamata o un valore composto; non aggiunge una nuova operazione.
}
# DOC L0047: Esegue il passaggio esplicito «(OUTPUT / 'package.json').write_text(json.dumps(report, indent=2) + '\n', encoding='utf-8')» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
(OUTPUT / 'package.json').write_text(json.dumps(report, indent=2) + '\n', encoding='utf-8')
# DOC L0048: Esegue il passaggio esplicito «print(json.dumps(report))» nel flusso: Preparazione dello ZIP statico Cloudflare Pages.
print(json.dumps(report))
