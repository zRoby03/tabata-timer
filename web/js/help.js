import { t } from './i18n.js';
import { escapeHtml as h } from './dom.js';
import { helpSections } from './help-content.js';
export function helpView() {
  return `<section class="help-page"><div class="page-heading"><h1>${t('Guida all’uso')}</h1><button data-action="home">${t('Vai al timer')}</button></div><div class="stack">${helpSections.map(section => `<section class="card"><h2>${h(t(section.title))}</h2>${section.entries.map((entry, index, entries) => {
    const text = h(t(entry.text));
    if (entry.kind === 'li') return `${entries[index - 1]?.kind !== 'li' ? '<ul>' : ''}<li>${text}</li>${entries[index + 1]?.kind !== 'li' ? '</ul>' : ''}`;
    return entry.kind === 'h3' ? `<h3>${text}</h3>` : `<p>${text}</p>`;
  }).join('')}</section>`).join('')}</div></section>`;
}
