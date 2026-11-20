// Analizza sintassi e simboli JavaScript senza eseguire il programma.
import { readFileSync } from 'node:fs';
import { pathToFileURL } from 'node:url';
const { parse } = await import(process.env.ACORN_MODULE ? pathToFileURL(process.env.ACORN_MODULE).href : 'acorn');
const input = JSON.parse(readFileSync(0, 'utf8'));
const results = {};
for (const [path, source] of Object.entries(input)) {
  const tokens = [], comments = [];
  const ast = parse(source, { ecmaVersion: 'latest', sourceType: 'module', locations: true, onToken: tokens, onComment: comments });
  const protectedLines = new Set(), functions = [], closes = {}, stack = [];
  for (const token of [...tokens, ...comments]) {
    const kind = typeof token.type === 'string' ? token.type : token.type.label;
    if (['string', 'regexp', 'template', 'Block'].includes(kind)) {
      for (let line = token.loc.start.line + 1; line <= token.loc.end.line; line++) {
        if (line < token.loc.end.line || token.loc.end.column > 0) protectedLines.add(line);
      }
    }
  }
  for (const token of tokens) {
    if (token.type.label === '{' || token.type.label === '${') stack.push(token.loc.start.line);
    if (token.type.label === '}') { const from = stack.pop(); if (from) closes[token.loc.start.line] = from; }
  }
  function visit(node, parent = null, owner = '') {
    if (!node || typeof node !== 'object') return;
    if (['FunctionDeclaration', 'FunctionExpression', 'ArrowFunctionExpression'].includes(node.type)) {
      const name = node.id?.name || (parent?.type === 'VariableDeclarator' ? parent.id.name : ['MethodDefinition', 'Property', 'PropertyDefinition'].includes(parent?.type) ? parent.key.name || String(parent.key.value) : 'callback') || 'callback';
      const call = parent?.type === 'CallExpression' ? source.slice(parent.callee.start, parent.callee.end) : '';
      const event = parent?.type === 'CallExpression' && typeof parent.arguments[0]?.value === 'string' ? parent.arguments[0].value : '';
      functions.push({ name, owner, call, event, line: node.loc.start.line, endLine: node.loc.end.line, params: node.params.map(p => source.slice(p.start, p.end)), kind: node.type });
      owner = name === 'callback' ? owner : name;
    }
    if (node.type === 'ClassDeclaration') owner = node.id?.name || owner;
    for (const [key, value] of Object.entries(node)) {
      if (['loc', 'start', 'end'].includes(key)) continue;
      if (Array.isArray(value)) value.forEach(child => visit(child, node, owner));
      else if (value && typeof value === 'object') visit(value, node, owner);
    }
  }
  visit(ast);
  function withoutPositions(node) {
    if (Array.isArray(node)) return node.map(withoutPositions);
    if (node && typeof node === 'object') return Object.fromEntries(Object.entries(node).filter(([key]) => !['start', 'end', 'loc', 'raw'].includes(key)).map(([key, value]) => [key, withoutPositions(value)]));
    return node;
  }
  results[path] = { protectedLines: [...protectedLines], functions, closes, ast: withoutPositions(ast) };
}
process.stdout.write(JSON.stringify(results));
