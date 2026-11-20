# DOC L0001: Esegue il passaggio esplicito «"""Package the local sources and QA reports without SDK paths, caches or temporary media."""» nel flusso: Preparazione dell’archivio sorgenti locale.
"""Package the local sources and QA reports without SDK paths, caches or temporary media."""
# DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: from pathlib import Path. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
from pathlib import Path
# DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import os. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import os
# DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import zipfile. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import zipfile

# DOC L0006: Esegue il passaggio esplicito «ROOT = Path(__file__).resolve().parent.parent» nel flusso: Preparazione dell’archivio sorgenti locale.
ROOT = Path(__file__).resolve().parent.parent
# DOC L0007: Esegue il passaggio esplicito «VERSION = '1.8.2'» nel flusso: Preparazione dell’archivio sorgenti locale.
VERSION = '1.8.2'
# DOC L0008: Esegue il passaggio esplicito «DESTINATION = ROOT / 'output' / f'Tabata-Timer-{VERSION}-sorgenti.zip'» nel flusso: Preparazione dell’archivio sorgenti locale.
DESTINATION = ROOT / 'output' / f'Tabata-Timer-{VERSION}-sorgenti.zip'
# DOC L0009: Esegue il passaggio esplicito «EXCLUDED_DIRS = {'.git', '.gradle', '.kotlin', '.idea', 'build', 'node_modules', '__pycache__'}» nel flusso: Preparazione dell’archivio sorgenti locale.
EXCLUDED_DIRS = {'.git', '.gradle', '.kotlin', '.idea', 'build', 'node_modules', '__pycache__'}
# DOC L0010: Esegue il passaggio esplicito «EXCLUDED_FILES = {'local.properties', '.DS_Store'}» nel flusso: Preparazione dell’archivio sorgenti locale.
EXCLUDED_FILES = {'local.properties', '.DS_Store'}
# DOC L0011: Esegue il passaggio esplicito «files = [ROOT / 'README.md', ROOT / '.gitignore']» nel flusso: Preparazione dell’archivio sorgenti locale.
files = [ROOT / 'README.md', ROOT / '.gitignore']
# DOC L0012: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
for folder in ('web', 'android', 'assets', 'docs', 'fixtures', 'scripts', 'locales', 'hosting'):
    # DOC L0013: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    for directory, names, filenames in os.walk(ROOT / folder):
        # DOC L0014: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
        names[:] = sorted(name for name in names if name not in EXCLUDED_DIRS)
        # DOC L0015: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
        files.extend(Path(directory) / name for name in sorted(filenames) if name not in EXCLUDED_FILES and not name.endswith('.iml'))
# DOC L0016: Esegue il passaggio esplicito «files.extend(sorted((ROOT / 'output').glob('browser-*.json')))» nel flusso: Preparazione dell’archivio sorgenti locale.
files.extend(sorted((ROOT / 'output').glob('browser-*.json')))
# DOC L0017: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
files.extend(path for path in (ROOT / 'output' / 'cloudflare').glob('*.json') if path.is_file())
# DOC L0018: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
files.extend(path for path in [ROOT / 'output' / 'android-languages.json', ROOT / 'output' / 'android-modes.json', ROOT / 'output' / 'android-background.json', ROOT / 'output' / 'android-smoke.json', ROOT / 'output' / f'verification-v{VERSION}.json', ROOT / 'output' / 'publication.json'] if path.exists())
# DOC L0019: Esegue il passaggio esplicito «DESTINATION.parent.mkdir(parents=True, exist_ok=True)» nel flusso: Preparazione dell’archivio sorgenti locale.
DESTINATION.parent.mkdir(parents=True, exist_ok=True)
# DOC L0020: Esegue il passaggio esplicito «with zipfile.ZipFile(DESTINATION, 'w', compression=zipfile.ZIP_DEFLATED) as archive:» nel flusso: Preparazione dell’archivio sorgenti locale.
with zipfile.ZipFile(DESTINATION, 'w', compression=zipfile.ZIP_DEFLATED) as archive:
    # DOC L0021: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    for path in files:
        # DOC L0022: Esegue il passaggio esplicito «archive.write(path, f'Tabata-Timer-{VERSION}/{path.relative_to(ROOT).as_posix()}')» nel flusso: Preparazione dell’archivio sorgenti locale.
        archive.write(path, f'Tabata-Timer-{VERSION}/{path.relative_to(ROOT).as_posix()}')
# DOC L0023: Esegue il passaggio esplicito «with zipfile.ZipFile(DESTINATION) as archive:» nel flusso: Preparazione dell’archivio sorgenti locale.
with zipfile.ZipFile(DESTINATION) as archive:
    # DOC L0024: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert archive.testzip() is None
# DOC L0025: Esegue il passaggio esplicito «print(f'{DESTINATION.name}: {len(files)} file, {DESTINATION.stat().st_size} byte; integrità ZIP verificata.')» nel flusso: Preparazione dell’archivio sorgenti locale.
print(f'{DESTINATION.name}: {len(files)} file, {DESTINATION.stat().st_size} byte; integrità ZIP verificata.')
