"""Crea commenti didattici e una spiegazione verificabile per ogni riga sorgente.

Legge la versione base da Git, non riscrive mai main e non modifica i binari.
Le righe interne a stringhe multilinea vengono spiegate nel documento affiancato.
"""
from pathlib import Path
from collections import Counter
from html import escape
from html.parser import HTMLParser
import argparse
import ast
import hashlib
import io
import json
import os
import re
import subprocess
import tokenize
from knowledge import MODULES, FUNCTIONS, OVERRIDES, VARIABLES, FIELDS

ROOT = Path(__file__).resolve().parents[2]
DEST = ROOT / 'docs' / 'documentazione'
REPO = 'https://github.com/zRoby03/tabata-timer'
MARKER = re.compile(r'^\s*(?://|#|/\*|<!--) DOC L\d+:.*(?:\*/|-->)?\r?\n?$')
TEXT_EXTENSIONS = {'.js', '.mjs', '.kt', '.kts', '.java', '.py', '.html', '.css', '.xml', '.json', '.properties', '.webmanifest', '.svg', '.txt', '.bat', '.toml', '.yml', '.yaml'}
GENERATED = {'web/js/messages.js', 'android/app/src/main/java/it/tabatastudio/i18n/MessageIds.kt'}
CURATED_MISSING = set()


def run(args, **kwargs):
    """Esegue un comando senza passare il suo testo attraverso una shell."""
    return subprocess.run(args, cwd=ROOT, check=True, capture_output=True, **kwargs)


def digest(value):
    """Calcola l'impronta UTF-8 usata per verificare sorgenti e documenti."""
    return hashlib.sha256(value.encode('utf-8')).hexdigest()


def clean(value):
    """Tiene le spiegazioni su una riga e neutralizza i terminatori dei commenti."""
    return re.sub(r'\s+', ' ', str(value)).strip().replace('*/', '* /').replace('-->', '-- >')


def source_files(base):
    """Seleziona tutti i file di codice/configurazione della consegna base."""
    names = run(['git', 'ls-tree', '-r', '--name-only', base], text=True).stdout.splitlines()
    sources, binaries = {}, []
    for name in names:
        if name.startswith('docs/') or name == 'README.md':
            continue
        raw = run(['git', 'show', f'{base}:{name}']).stdout
        if Path(name).suffix in TEXT_EXTENSIONS | {'.md'} or name in {'.gitignore', '.gitattributes', 'android/gradlew', 'hosting/cloudflare/_headers'}:
            sources[name] = raw.decode('utf-8').replace('\r\n', '\n')
        else:
            binaries.append({'path': name, 'bytes': len(raw), 'sha256': hashlib.sha256(raw).hexdigest()})
    for path in sorted((ROOT / 'tools' / 'documentation').glob('*')):
        if path.is_file() and path.suffix in TEXT_EXTENSIONS:
            sources[path.relative_to(ROOT).as_posix()] = path.read_text(encoding='utf-8').replace('\r\n', '\n')
    return sources, binaries


def parse_javascript(sources):
    """Acorn restituisce funzioni, zone letterali e AST; non esegue il sito."""
    subset = {name: text for name, text in sources.items() if Path(name).suffix in {'.js', '.mjs'}}
    result = run(['node', 'tools/documentation/javascript.mjs'], input=json.dumps(subset), text=True, encoding='utf-8')
    return json.loads(result.stdout)


def parse_kotlin(sources):
    """Usa il lexer Kotlin di Gradle sugli originali, anche nelle rigenerazioni."""
    cache = Path.home() / '.gradle' / 'caches' / 'modules-2' / 'files-2.1' / 'org.jetbrains.kotlin'
    version = re.search(r'plugin.compose"\) version "([^"]+)"', sources['android/build.gradle.kts']).group(1)
    jars = [next((cache / artifact / version).rglob('*.jar')) for artifact in ('kotlin-compiler-embeddable', 'kotlin-stdlib')]
    originals = ROOT / 'tmp' / 'documentation-originals'
    paths = []
    for name, text in sources.items():
        if Path(name).suffix in {'.kt', '.kts', '.java'}:
            target = originals / name
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_text(text, encoding='utf-8', newline='\n')
            paths.append(str(target))
    java_home = os.environ.get('JAVA_HOME')
    if not java_home and os.name == 'nt':
        java_home = 'C:/Program Files/Android/Android Studio/jbr'
    java = str(Path(java_home) / 'bin' / ('java.exe' if os.name == 'nt' else 'java')) if java_home else 'java'
    result = run([java, '--class-path', os.pathsep.join(map(str, jars)), 'tools/documentation/KotlinTokens.java', *paths], text=True, encoding='utf-8')
    parsed = {}
    for line in result.stdout.splitlines():
        data = json.loads(line)
        name = Path(data.pop('path')).relative_to(originals).as_posix()
        parsed[name] = data
    return parsed


def kotlin_functions(text):
    """Indicizza dichiarazioni fun, inclusi overload e funzioni locali."""
    found = []
    pattern = re.compile(r'\bfun\s+(?:<[^>]+>\s*)?(?:[A-Za-z_]\w*(?:<[^>]+>)?\.)?(`[^`]+`|[A-Za-z_]\w*)\s*\(')
    lines = text.splitlines()
    for number, line in enumerate(lines, 1):
        for match in pattern.finditer(line):
            found.append({'name': match.group(1).strip('`'), 'line': number, 'endLine': len(lines), 'params': [], 'kind': 'Kotlin function'})
    for index, function in enumerate(found[:-1]):
        function['endLine'] = max(function['line'], found[index + 1]['line'] - 1)
    return found


def python_metadata(text):
    """AST e tokenizer impediscono di inserire commenti dentro stringhe Python."""
    tree = ast.parse(text)
    functions = [{'name': node.name, 'line': node.lineno, 'endLine': node.end_lineno, 'params': [arg.arg for arg in node.args.args], 'kind': 'Python function'} for node in ast.walk(tree) if isinstance(node, (ast.FunctionDef, ast.AsyncFunctionDef))]
    protected = set()
    for token in tokenize.generate_tokens(io.StringIO(text).readline):
        if token.type == tokenize.STRING:
            protected.update(range(token.start[0] + 1, token.end[0] + 1))
    for index, line in enumerate(text.splitlines(), 1):
        if line.rstrip().endswith('\\'):
            protected.add(index + 1)
    return {'functions': functions, 'protectedLines': sorted(protected), 'closes': {}}


class MarkupLines(HTMLParser):
    """Riconosce attributi multilinea e contenuti raw di script/style."""
    def __init__(self, text):
        super().__init__(convert_charrefs=False)
        self.protected = set()
        self.raw = None
        self.feed(text)

    def handle_starttag(self, tag, attrs):
        line, column = self.getpos()
        self.protected.update(range(line + 1, line + self.get_starttag_text().count('\n') + 1))
        if tag in ('script', 'style', 'textarea'):
            self.raw = tag

    def handle_startendtag(self, tag, attrs):
        self.handle_starttag(tag, attrs)
        self.raw = None

    def handle_endtag(self, tag):
        if tag == self.raw:
            self.raw = None

    def handle_data(self, data):
        if self.raw:
            line, column = self.getpos()
            self.protected.update(range(line, line + data.count('\n') + 1))

    def handle_comment(self, data):
        line, column = self.getpos()
        self.protected.update(range(line + 1, line + data.count('\n') + 1))


def module_info(name):
    """Collega ciascun file al suo ruolo reale nell'app o nella toolchain."""
    leaf = Path(name).name
    if '/tests/' in name or '/test/' in name or '/androidTest/' in name:
        return (f'Prove di regressione in {leaf}.', 'Le asserzioni verificano il comportamento; fixture, clock e servizi di prova non sono funzionalità aggiunte al prodotto.')
    if leaf in MODULES:
        return MODULES[leaf]
    if name == 'locales/messages.json':
        return ('Catalogo editoriale delle tre lingue.', 'Ogni chiave è il testo italiano sorgente; l’array contiene inglese, spagnolo ed eventualmente una variante italiana esplicita.')
    if '/res/values' in name and leaf == 'messages.xml':
        return ('Traduzioni Android generate.', 'Il suffisso values-en o values-es seleziona la lingua. Gli ID corrispondono a MessageIds.kt; modificare il catalogo sorgente per rigenerare.')
    if name.startswith('tools/documentation/'):
        return ('Strumento di generazione della documentazione.', 'Lavora su Git e sui token dei sorgenti, senza eseguire l’app o pubblicare il sito. Il suo codice è spiegato nel documento affiancato.')
    if name.startswith('scripts/browser-'):
        return ('Prova browser automatizzata.', 'Playwright controlla DOM, flussi e risultati del sito servito; il codice non è incluso nel pacchetto web pubblico.')
    if name.startswith('scripts/android-'):
        return ('Prova Android su emulatore dedicato.', 'ADB legge o pilota una build di test; i dati preparati per il collaudo non vanno confusi con archivi personali.')
    if name.startswith('fixtures/'):
        return ('Dati campione del contratto tra piattaforme.', 'Verificano timeline, importazione, modalità e compatibilità senza dipendere da dati personali.')
    if 'gradle' in name or leaf.endswith('.properties'):
        return ('Configurazione di compilazione e avvio Gradle.', 'Versioni, plugin e dipendenze devono restare riproducibili. Il wrapper avvia Gradle e non costituisce il motore del timer.')
    if name.startswith('android/app/schemas/'):
        return ('Schema SQLite esportato da Room.', 'Documenta la versione 1 del database; non è un database con dati utente e non deve essere commentato come se fosse JSON permissivo.')
    if '/res/' in name or leaf.endswith('.svg'):
        return ('Risorsa grafica o configurazione Android.', 'I tag descrivono vettori, colori, icone e regole di backup, senza eseguire il conteggio del timer.')
    if leaf == 'manifest.webmanifest':
        return ('Manifest di installazione della web app.', 'Nome, percorso iniziale e icone permettono l’installazione PWA; non aggiungono i privilegi del servizio Android.')
    if leaf == 'package.json':
        return ('Metadati Node e comandi di sviluppo.', 'Il sito rimane buildless; le dipendenze degli strumenti non vengono caricate dal browser.')
    return (f'Dato o configurazione di progetto: {name}.', 'Il file è mantenuto nel repository per rendere riproducibile la consegna; non contiene salvataggi personali.')


def function_purpose(name, function):
    """Usa descrizioni curate e distingue callback, test e overload per file."""
    symbol = function['name']
    leaf = Path(name).name
    if symbol == 'callback':
        call = function.get('call') or 'una funzione di ordine superiore'
        event = function.get('event')
        owner = function.get('owner')
        detail = f' per l’evento o caso «{event}»' if event else ''
        return f'Callback di {call}{detail}; viene invocata dal chiamante' + (f' nel contesto di {owner}.' if owner else ' nel flusso del modulo.')
    if f'{leaf}:{symbol}' in OVERRIDES:
        return OVERRIDES[f'{leaf}:{symbol}']
    if symbol in FUNCTIONS:
        return FUNCTIONS[symbol]
    if symbol == 'constructor':
        return f'Inizializza l’istanza {function.get("owner", "della classe")}, i suoi riferimenti e lo stato iniziale, senza avviare automaticamente il timer.'
    test_file = '/tests/' in name or '/test/' in name or '/androidTest/' in name
    if test_file:
        words = re.sub(r'(?<!^)(?=[A-Z])', ' ', symbol)
        return f'Prepara o verifica il caso di regressione «{words}»; le asserzioni nel corpo specificano l’esito atteso.'
    if leaf.endswith('.py') or leaf.endswith('.mjs') or name.startswith('tools/'):
        return f'Passaggio «{symbol}» dello strumento: {module_info(name)[0]} Gli argomenti e le operazioni sono descritti nelle righe del corpo.'
    CURATED_MISSING.add(f'{leaf}:{symbol}')
    return f'Implementa «{symbol}» nel modulo: {module_info(name)[0]}'


def scope_for(number, functions):
    """Trova la funzione più interna che contiene la riga originale."""
    candidates = [f for f in functions if f['line'] <= number <= f['endLine']]
    return min(candidates, key=lambda f: (f['endLine'] - f['line'], f['name'] == 'callback'), default=None)


def json_context(text):
    """Indicizza chiavi e valori JSON per spiegare anche file non commentabili."""
    tokens = list(re.finditer(r'"(?:\\.|[^"\\])*"|true|false|null|-?\d+(?:\.\d+)?(?:[eE][+-]?\d+)?|[{}\[\]:,]', text))
    rows, position = {}, 0
    def record(token, path, role):
        line = text.count('\n', 0, token.start()) + 1
        rows.setdefault(line, []).append((path, role, token.group()))
    def value(path):
        nonlocal position
        token = tokens[position]
        raw = token.group()
        record(token, path, 'value')
        position += 1
        if raw == '{':
            while tokens[position].group() != '}':
                key = tokens[position]
                key_name = json.loads(key.group())
                record(key, path + [key_name], 'key')
                position += 2
                value(path + [key_name])
                if tokens[position].group() == ',': position += 1
            record(tokens[position], path, 'close'); position += 1
        elif raw == '[':
            index = 0
            while tokens[position].group() != ']':
                value(path + [index]); index += 1
                if tokens[position].group() == ',': position += 1
            record(tokens[position], path, 'close'); position += 1
    json.loads(text)
    value([])
    return rows


def explain(name, number, line, metadata, json_rows):
    """Combina ruolo, funzione, struttura e operazioni effettive della riga."""
    code = line.strip()
    functions = metadata.get('functions', [])
    declarations = [f for f in functions if f['line'] == number and f['name'] != 'callback']
    if declarations:
        return ' '.join(f'{f["name"]}: {function_purpose(name, f)}' for f in declarations)
    if not code:
        return 'Riga vuota: separa visivamente i passaggi, senza introdurre istruzioni o dati.'
    if name == 'locales/messages.json' and number in json_rows:
        details = []
        for path, role, raw in json_rows[number]:
            if path and isinstance(path[-1], int) and raw.startswith('"'):
                language = ['inglese', 'spagnolo', 'variante italiana'][min(path[-1], 2)]
                details.append(f'Traduzione {language} della chiave «{path[-2]}»: {json.loads(raw)}.')
            elif path and role == 'key': details.append(f'Chiave italiana di sistema «{path[-1]}»; non è un nome utente da tradurre.')
        if details: return clean(' '.join(details))
    if json_rows.get(number):
        details = []
        for path, role, raw in json_rows[number]:
            key = path[-1] if path else None
            if role == 'key' and key in FIELDS: details.append(f'{key}: {FIELDS[key]}.')
            elif role == 'value' and raw not in ('{', '['): details.append(f'Valore {raw} per ' + '.'.join(map(str, path)) + '.')
        if details: return clean(' '.join(details))
        return 'Delimita la struttura JSON del percorso ' + '.'.join(map(str, json_rows[number][0][0])) + '; chiavi e valori restano JSON standard, senza commenti nel file.'
    if code.startswith(('//', '#', '/*', '* ', '<!--')):
        return 'Commento originale: conserva la motivazione o l’istruzione già presente nel sorgente. ' + clean(code.lstrip('/#*<!- '))
    if code.startswith('package '):
        return 'Assegna il file al namespace ' + code[8:].rstrip(';') + ', distinto dai package UI, dominio, dati e servizio.'
    if code.startswith(('import ', 'from ')):
        return 'Rende disponibili le dipendenze esplicite di questa riga: ' + clean(code) + '. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.'
    if '<string ' in code:
        match = re.search(r'name="([^"]+)"[^>]*>(.*?)</string>', code)
        if match: return f'Associa la risorsa {match[1]} al testo localizzato «{match[2]}», recuperato da AppText attraverso MessageIds.'
    if name.endswith('MessageIds.kt') and ' to R.string.' in code:
        return 'Collega la chiave italiana al suo ID di risorsa Android, generato dallo stesso catalogo usato dal web; non riscrive le stringhe salvate dall’utente.'
    if name.endswith('messages.js'):
        return 'Esporta l’intero catalogo generato in forma compatta: ogni chiave italiana ha inglese/spagnolo e, quando prevista, variante italiana. La sorgente leggibile è locales/messages.json.'
    if code.startswith(('<?xml', '<!doctype', '<!DOCTYPE')):
        return 'Dichiara il formato del documento prima dei suoi elementi; deve rimanere nella posizione prevista dal formato.'
    if code.startswith('<'):
        tags = list(dict.fromkeys(re.findall(r'<\s*/?\s*([A-Za-z][\w:.-]*)', code)))
        if 'meta' in tags and 'Content-Security-Policy' in code:
            return 'Definisce la CSP del documento: risorse locali consentite e contenuti attivi esterni bloccati. Lo snippet AdSense richiede un’integrazione separata prima di poter essere caricato.'
        if 'script' in tags: return 'Collega lo script locale indicato dal documento; type=module abilita import/export e non avvia servizi cloud.'
        if 'uses-permission' in tags: return 'Dichiara il permesso Android riportato qui; la richiesta runtime delle notifiche è gestita nell’Activity, mentre il manifest non concede Internet.'
        if any(t in tags for t in ('application', 'activity', 'service')):
            return 'Dichiara componenti e attributi Android: ' + ', '.join(tags) + '. I nomi collegano il manifest alle classi del progetto.'
        if 'path' in tags or 'vector' in tags: return 'Descrive geometria e colori del marchio t. come vettore, senza caricare un font esterno.'
        if 'data-language-content=' in code: return 'Raggruppa la versione linguistica dell’articolo; public-pages.js ne rende visibile una sola secondo la preferenza.'
        if 'data-i18n' in code: return 'Marca testo o attributi dell’app per la traduzione esplicita, senza attraversare i valori utente.'
        return 'Definisce la struttura dichiarativa ' + ', '.join(tags[:12]) + ' nel documento: ' + module_info(name)[0]
    if Path(name).suffix == '.css':
        if code.startswith('--'): return 'Definisce un token di colore o dimensione riutilizzato dai componenti; tema e palette possono cambiarne il valore.'
        if code.startswith('@media'): return 'Applica le regole successive soltanto alla condizione responsive indicata, per evitare overflow e adattare i controlli.'
        if code in ('}', '};'): return 'Chiude il selettore o la media query precedentemente aperti.'
        if '{' in code: return 'Seleziona gli elementi CSS indicati e apre le regole di presentazione, senza cambiare i dati del timer.'
        return 'Imposta le proprietà CSS di questa riga sul selettore corrente: ' + clean(code)
    if re.fullmatch(r'[}\]);,\s]+', code):
        opening = metadata.get('closes', {}).get(str(number))
        return f'Chiude il costrutto aperto alla riga {opening}.' if opening else 'Chiude un blocco, una chiamata o un valore composto; non aggiunge una nuova operazione.'
    if re.search(r'\b(class|interface|object|enum class)\s+[A-Za-z_]', code):
        return 'Dichiara il tipo o contenitore di questo modulo: ' + module_info(name)[0] + ' I campi e i metodi sono descritti nelle righe successive.'
    if code.startswith('@'):
        return 'Applica l’annotazione dichiarata: Compose identifica UI, Room genera accessi e schema, mentre le annotazioni Test selezionano i casi da eseguire.'
    scope = scope_for(number, functions)
    context = f' Nel contesto di {scope["name"]}.' if scope and scope['name'] != 'callback' else ''
    facts = []
    if 'PREPARATION_MS' in code and ('20000' in code or '20_000' in code): facts.append('Fissa la preparazione a 20.000 millisecondi, separata dal lavoro pianificato.')
    if re.search(r'\b(?:assert|assertEquals|assertTrue|assertFalse|assertThrows|require|check)\b', code): facts.append('Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.')
    if re.search(r'\bthrow\b', code): facts.append('Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti.')
    if '.setItem(' in code or '.putBoolean(' in code or '.putString(' in code: facts.append('Scrive la preferenza o la copia locale prevista, senza inviarla a un server.')
    if '.getItem(' in code or '.getBoolean(' in code or '.getString(' in code: facts.append('Legge il valore locale e applica i controlli/fallback riportati nell’espressione.')
    if '.withTransaction' in code: facts.append('Raggruppa le scritture Room in una transazione: o vengono applicate tutte, o nessuna.')
    if 'mutex.withLock' in code: facts.append('Serializza l’operazione del repository per non sovrapporre salvataggi e importazioni.')
    if 'addEventListener(' in code: facts.append('Registra il gestore dell’evento indicato; il corpo viene eseguito quando l’evento si verifica.')
    if 'setInterval(' in code or 'delay(' in code: facts.append('Programma il prossimo aggiornamento; la durata effettiva rimane misurata dall’orologio, non dal numero di aggiornamenti.')
    if 'SoundPool' in code or '.play(' in code or 'createBufferSource(' in code: facts.append('Prepara o riproduce il segnale audio; il motore del tempo rimane indipendente dalla riproduzione.')
    if 'PARTIAL_WAKE_LOCK' in code: facts.append('Richiede il mantenimento della CPU durante il timer Android, distinto dallo schermo acceso.')
    if 'PendingIntent' in code: facts.append('Collega un’azione esterna della notifica alla sessione, usando i flag dichiarati per aggiornamento e immutabilità.')
    if re.search(r'\b(if|when|else|switch)\b', code): facts.append('Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.')
    if re.search(r'\b(for|while)\b|\.forEach|\.map(?:Indexed)?\s*[({]', code): facts.append('Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.')
    if 'return' in code or re.match(r'^(?:val|var|const|let|export const|private val|private var)\s+', code):
        names = list(dict.fromkeys(re.findall(r'\b(?:const|let|val|var)\s+([A-Za-z_]\w*)', code)))
        for symbol in names:
            facts.append(f'{symbol}: {VARIABLES.get(symbol, "valore locale ottenuto dall’espressione di inizializzazione riportata nella riga")}.')
        if 'return' in code: facts.append('Restituisce il valore calcolato o conclude il percorso della funzione.')
    if 'innerHTML' in code or 'return `' in code: facts.append('Genera o aggiorna HTML della UI; i dati utente devono passare attraverso l’escaping previsto dal modulo.')
    if 'Text(' in code or 'Button(' in code or 'OutlinedTextField(' in code: facts.append('Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile.')
    if 'copy(' in code or 'structuredClone(' in code: facts.append('Lavora su una copia per mantenere distinta la bozza dal valore precedente.')
    if facts: return clean(' '.join(dict.fromkeys(facts)) + context)
    if name.endswith('.properties') or name.endswith('_headers') or name in {'.gitignore', '.gitattributes'}:
        return 'Configura la regola letterale «' + clean(code) + '» per ' + module_info(name)[0].lower()
    if number in metadata.get('protectedLines', []):
        return 'Continua un letterale o un commento multilinea: il suo contenuto deve restare identico perché può essere HTML, SQL, testo o dato del programma.' + context
    return 'Esegue il passaggio esplicito «' + clean(code[:180]) + '» nel flusso: ' + module_info(name)[0] + context


def annotate(name, text, metadata, explanations):
    """Inserisce solo commenti in posizioni lessicalmente sicure e mappa le righe."""
    generated = name in GENERATED or ('/res/values' in name and name.endswith('/messages.xml'))
    suffix = Path(name).suffix
    style = '//' if suffix in {'.js', '.mjs', '.kt', '.kts', '.java'} else '#' if suffix == '.py' else 'css' if suffix == '.css' else 'xml' if suffix in {'.html', '.xml'} else None
    if generated or name.startswith('tools/') or name.startswith('android/gradlew') or not style:
        return text, {index: index for index in range(1, len(text.splitlines()) + 1)}, 0
    protected = set(metadata.get('protectedLines', []))
    if suffix in {'.html', '.xml'}: protected.update(MarkupLines(text).protected)
    if suffix == '.css':
        for match in re.finditer(r'/\*[\s\S]*?\*/', text):
            first = text.count('\n', 0, match.start()) + 1
            last = text.count('\n', 0, match.end()) + 1
            protected.update(range(first, last + 1))
    output, mapping, count = [], {}, 0
    for index, line in enumerate(text.splitlines(keepends=True), 1):
        code = line.strip()
        comment = code.startswith(('//', '#', '/*', '* ', '<!--', '<?xml', '<!doctype', '<!DOCTYPE'))
        if code and index not in protected and not comment:
            indent = line[:len(line) - len(line.lstrip())]
            message = clean(explanations[index])
            if style == 'xml': extra = f'{indent}<!-- DOC L{index:04}: {message} -->\n'
            elif style == 'css': extra = f'{indent}/* DOC L{index:04}: {message} */\n'
            else: extra = f'{indent}{style} DOC L{index:04}: {message}\n'
            output.append(extra); count += 1
        mapping[index] = index + count
        output.append(line)
    result = ''.join(output)
    restored = ''.join(line for line in result.splitlines(keepends=True) if not MARKER.match(line))
    assert restored == text, name
    return result, mapping, count


def write_companion(name, text, metadata, explanations, mapping, base):
    """Scrive una tabella con una voce per ogni riga, incluse quelle non eseguibili."""
    target = DEST / 'righe' / (name + '.md')
    target.parent.mkdir(parents=True, exist_ok=True)
    title, role = module_info(name)
    parts = [f'# {name}\n\n{title}\n\n{role}\n', 'I numeri si riferiscono al sorgente originale; il collegamento apre esattamente quella riga. I commenti DOC nel branch sono aggiunte didattiche.\n']
    symbols = metadata.get('functions', [])
    if symbols:
        parts += ['## Funzioni e callback\n', '| Simbolo | Riga originale | Ruolo |\n| --- | ---: | --- |\n']
        for function in symbols:
            label = function['name']
            if function.get('owner'): label = function['owner'] + '.' + label
            link = f'{REPO}/blob/{base}/{name}#L{function["line"]}' if not name.startswith('tools/') else f'{REPO}/blob/documentazione/{name}#L{mapping[function["line"]]}'
            parts.append(f'| {escape(clean(label)).replace("|", "&#124;")} | [{function["line"]}]({link}) | {escape(clean(function_purpose(name, function))).replace("|", "&#124;")} |\n')
    heading = '\n## Ogni riga\n\n| Riga originale | Codice (estratto) | Spiegazione |\n| ---: | --- | --- |\n'
    rows = []
    for number, line in enumerate(text.splitlines(), 1):
        code = escape(line.strip()[:160]).replace('|', '&#124;') or '∅'
        if len(line.strip()) > 160: code += '…'
        link = f'{REPO}/blob/{base}/{name}#L{number}' if not name.startswith('tools/') else f'{REPO}/blob/documentazione/{name}#L{mapping[number]}'
        rows.append(f'| [{number}]({link}) | <code>{code}</code> | {escape(explanations[number]).replace("|", "&#124;")} |\n')
    pages = []
    if len(''.join(rows).encode('utf-8')) > 250_000:
        parts.append('\n## Lettura riga per riga in pagine\n\nIl catalogo è diviso in pagine brevi per la lettura su GitHub; nessuna riga è omessa.\n\n')
        for start in range(0, len(rows), 400):
            page = target.with_name(target.stem + f'.parte-{start // 400 + 1}.md')
            last = min(start + 400, len(rows))
            page.write_text(f'# {name}: righe {start + 1}–{last}\n\n[Torna all’indice del file]({target.name})\n' + heading + ''.join(rows[start:last]), encoding='utf-8', newline='\n')
            pages.append(page.relative_to(ROOT).as_posix())
            parts.append(f'- [Righe {start + 1}–{last}]({page.name})\n')
    else:
        parts += [heading, *rows]
    target.write_text(''.join(parts), encoding='utf-8', newline='\n')
    return target.relative_to(ROOT).as_posix(), pages


def generate(base):
    """Coordina analisi, annotazioni, pagine e manifest di copertura."""
    full_base = run(['git', 'rev-parse', base], text=True).stdout.strip()
    sources, binaries = source_files(full_base)
    js = parse_javascript(sources)
    kotlin = parse_kotlin(sources)
    records = []
    for name, text in sources.items():
        suffix = Path(name).suffix
        metadata = js.get(name, kotlin.get(name, {'functions': [], 'protectedLines': [], 'closes': {}}))
        if suffix in {'.kt', '.kts'}: metadata['functions'] = kotlin_functions(text)
        if suffix == '.py': metadata = python_metadata(text)
        if suffix == '.java':
            metadata['functions'] = [{'name': match.group(1), 'line': text.count('\n', 0, match.start()) + 1, 'endLine': text.count('\n', 0, match.start()) + 1, 'params': [], 'kind': 'Java method'} for match in re.finditer(r'\b(?:public|private|protected)\s+(?:static\s+)?[\w<>?\[\]]+\s+(\w+)\s*\(', text)]
        rows = json_context(text) if suffix in {'.json', '.webmanifest'} else {}
        explanations = {number: clean(explain(name, number, line, metadata, rows)) for number, line in enumerate(text.splitlines(), 1)}
        documented, mapping, inserted = annotate(name, text, metadata, explanations)
        (ROOT / name).write_text(documented, encoding='utf-8', newline='\n')
        companion, pages = write_companion(name, text, metadata, explanations, mapping, full_base)
        record = {'path': name, 'sourceSha256': digest(text), 'documentedSha256': digest(documented), 'sourceLines': len(text.splitlines()), 'explainedLines': len(explanations), 'inlineComments': inserted, 'functions': len(metadata.get('functions', [])), 'companion': companion, 'mapping': mapping}
        if pages: record['companionParts'] = pages
        if name in js: record['astSha256'] = digest(json.dumps(js[name]['ast'], sort_keys=True, ensure_ascii=False))
        records.append(record)
    totals = {key: sum(record[key] for record in records) for key in ('sourceLines', 'explainedLines', 'inlineComments', 'functions')}
    manifest = {'baseCommit': full_base, 'repository': REPO, 'branch': 'documentazione', 'files': records, 'binaryAssets': binaries, 'totals': {'sourceFiles': len(records), 'binaryFiles': len(binaries), **totals}, 'uncuratedProductionFunctions': sorted(CURATED_MISSING)}
    (DEST / 'copertura.json').write_text(json.dumps(manifest, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    index = ['# Indice dei file commentati\n\n', f'Base immutabile: `{full_base}`.\n\n', f'{len(records)} file testuali, {totals["sourceLines"]} righe spiegate e {totals["functions"]} funzioni/callback indicizzate.\n\n', '| File | Righe | Funzioni | Commenti nel sorgente | Lettura completa |\n| --- | ---: | ---: | ---: | --- |\n']
    for record in records:
        index.append(f'| {record["path"]} | {record["sourceLines"]} | {record["functions"]} | {record["inlineComments"]} | [Riga per riga](righe/{record["path"]}.md) |\n')
    index += ['\n## File binari\n\nNon hanno righe di codice testuale: sono elencati con formato, dimensione e SHA-256 nel manifest di copertura. Audio e icone sono documentati nelle guide dedicate; il JAR è il wrapper ufficiale Gradle.\n']
    (DEST / 'indice-file.md').write_text(''.join(index), encoding='utf-8')
    print(json.dumps({'totals': manifest['totals'], 'uncuratedProductionFunctions': sorted(CURATED_MISSING)}, ensure_ascii=False))


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--base', default='main')
    options = parser.parse_args()
    generate(options.base)
