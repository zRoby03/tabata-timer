import { t, phaseLabel } from './i18n.js';
import { escapeHtml as h } from './dom.js';
import { formatTime } from './model.js';
import { isStudio } from './modes.js';
export function timelinePreview(workout, phases) {
  const shown = phases.slice(0, 150);
  if (isStudio(workout)) return `<ol class="phase-list">${shown.map(p => `<li><span>${h(phaseLabel(p))}</span><b>${formatTime(p.durationMs)}</b></li>`).join('')}</ol>`;
  const groups = new Map();
  shown.forEach((phase, index) => {
    const key = phase.blockIndex === undefined ? phase.type : `block-${phase.blockIndex}`;
    if (!groups.has(key)) groups.set(key, []);
    groups.get(key).push({ phase, index });
  });
  const lastWork = phases.findLastIndex(p => p.type === 'WORK');
  return [...groups.values()].map(entries => {
    const first = entries[0].phase;
    const title = first.blockIndex === undefined ? phaseLabel(first) : t('Blocco {0} — {1}', first.blockIndex + 1, first.blockTitle);
    let cycle = -1;
    const rows = entries.map(({ phase: p, index }) => {
      const heading = p.cycleIndex !== undefined && p.cycleIndex !== cycle ? `<h4>${t('Ciclo {0}/{1}', p.cycleIndex + 1, p.cycles)}</h4>` : '';
      cycle = p.cycleIndex;
      const name = p.type === 'WORK' ? (p.labelArgs[4] > 1 ? t('{0} · ripetizione {1}/{2}', p.exerciseTitle, p.repetitionIndex + 1, p.labelArgs[4]) : p.exerciseTitle) : phaseLabel(p);
      const note = p.entryCue === 'ding2' ? (p.cycleIndex > 0 ? t('Due ding — inizio ciclo {0}', p.cycleIndex + 1) : t('Due ding — inizio Blocco {0}', p.blockIndex + 1)) : p.type === 'REST_BLOCK' ? t('Due sospiri — cambio blocco') : '';
      const end = index === lastWork ? t('Tre ding — fine lavoro') : p.type === 'COOLDOWN' ? t('Tre ding — fine defaticamento') : '';
      return `${heading}${note ? `<p class="preview-cue">${h(note)}</p>` : ''}<div class="preview-phase ${p.type === 'WORK' ? 'preview-work' : 'preview-rest'}"><span>${h(name)}</span><b>${formatTime(p.durationMs)}</b></div>${end ? `<p class="preview-cue">${h(end)}</p>` : ''}`;
    }).join('');
    return `<section class="preview-block"><h3>${h(title)}</h3>${rows}</section>`;
  }).join('');
}
