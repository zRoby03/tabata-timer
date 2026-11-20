"""Verifica copertura e identità del codice commentato rispetto alla base Git.

Non esegue pubblicazioni e non sostituisce i test dell'app: li affianca con
confronti di righe, AST JavaScript/Python, token Kotlin e risorse statiche.
"""
from pathlib import Path
import ast
import hashlib
import json
import re
import xml.etree.ElementTree as ET
from generate import ROOT, DEST, MARKER, digest, source_files, parse_javascript, parse_kotlin


def xml_structure(text):
    """Ignora la sola indentazione intorno ai commenti, conservando testi e attributi."""
    def node(element):
        meaningful = lambda value: value if value and value.strip() else ''
        return (element.tag, sorted(element.attrib.items()), meaningful(element.text), meaningful(element.tail), [node(child) for child in element])
    return node(ET.fromstring(text))


def verify():
    """Fallisce al primo scostamento tra sorgenti, spiegazioni e manifest."""
    manifest = json.loads((DEST / 'copertura.json').read_text(encoding='utf-8'))
    originals, binaries = source_files(manifest['baseCommit'])
    records = {item['path']: item for item in manifest['files']}
    assert set(originals) == set(records), 'Elenco sorgenti incompleto: rigenerare la documentazione'
    assert not manifest['uncuratedProductionFunctions'], 'Mancano descrizioni curate di funzioni del prodotto'
    current = {}
    counts = {'sourceFiles': len(originals), 'binaryFiles': len(binaries), 'sourceLines': 0, 'explainedLines': 0, 'inlineComments': 0, 'functions': 0}
    for name, original in originals.items():
        record = records[name]
        text = (ROOT / name).read_text(encoding='utf-8').replace('\r\n', '\n')
        current[name] = text
        source_lines = original.splitlines()
        text_lines = text.splitlines()
        removed = [line for line in text.splitlines(keepends=True) if MARKER.match(line)]
        restored = ''.join(line for line in text.splitlines(keepends=True) if not MARKER.match(line))
        assert restored == original, f'{name}: differenza oltre ai commenti DOC'
        assert record['sourceSha256'] == digest(original), f'{name}: hash originale errato'
        assert record['documentedSha256'] == digest(text), f'{name}: rigenerare dopo la modifica'
        assert record['inlineComments'] == len(removed), f'{name}: conteggio commenti'
        assert record['sourceLines'] == record['explainedLines'] == len(source_lines), f'{name}: copertura righe'
        pages = record.get('companionParts', [record['companion']])
        companion = '\n'.join((ROOT / page).read_text(encoding='utf-8') for page in pages)
        rows = re.findall(r'^\| \[(\d+)\]\([^\n]+?\) \| <code>.*?\| (.+) \|$', companion, re.MULTILINE)
        assert [int(number) for number, _ in rows] == list(range(1, len(source_lines) + 1)), f'{name}: righe mancanti o duplicate nella guida'
        assert all(len(explanation.strip()) > 15 for _, explanation in rows), f'{name}: spiegazione vuota'
        for number, original_line in enumerate(source_lines, 1):
            actual = record['mapping'][str(number)]
            assert text_lines[actual - 1] == original_line, f'{name}:{number}: collegamento riga errato'
        suffix = Path(name).suffix
        if suffix == '.py':
            assert ast.dump(ast.parse(original)) == ast.dump(ast.parse(text)), f'{name}: AST Python modificato'
        if suffix in {'.json', '.webmanifest'}:
            assert json.loads(original) == json.loads(text), f'{name}: JSON modificato'
        if suffix in {'.xml', '.svg'}:
            assert xml_structure(original) == xml_structure(text), f'{name}: struttura XML modificata'
        for key in ('sourceLines', 'explainedLines', 'inlineComments', 'functions'):
            counts[key] += record[key]
    js_original, js_current = parse_javascript(originals), parse_javascript(current)
    for name, data in js_original.items():
        assert data['ast'] == js_current[name]['ast'], f'{name}: AST JavaScript modificato'
        assert digest(json.dumps(data['ast'], sort_keys=True, ensure_ascii=False)) == records[name]['astSha256'], name
    kotlin_original, kotlin_current = parse_kotlin(originals), parse_kotlin(current)
    for name in kotlin_original:
        assert kotlin_original[name]['tokenSha256'] == kotlin_current[name]['tokenSha256'], f'{name}: token significativi modificati'
    for binary in binaries:
        assert hashlib.sha256((ROOT / binary['path']).read_bytes()).hexdigest() == binary['sha256'], f"Asset modificato: {binary['path']}"
    assert counts == manifest['totals'], 'Totali del manifest incoerenti'
    result = {'result': 'PASS', 'baseCommit': manifest['baseCommit'], 'coverage': counts, 'javascriptAstFiles': len(js_original), 'kotlinLexerFiles': len(kotlin_original), 'checks': ['righe e mappa', 'solo commenti aggiunti', 'AST JavaScript', 'AST Python', 'token Kotlin', 'JSON', 'XML/SVG', 'hash asset']}
    (DEST / 'verifica-commenti.json').write_text(json.dumps(result, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    print(json.dumps(result, ensure_ascii=False))


if __name__ == '__main__':
    verify()
