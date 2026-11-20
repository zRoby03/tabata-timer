import { t } from './i18n.js';
import { escapeHtml as h, decimal } from './dom.js';
export function chart(weeks, field, title, unit) {
  const max = Math.max(1, ...weeks.map(w => field === 'workMs' ? w.workMs / 60000 : w[field]));
  const width = 640, step = 560 / Math.max(1, weeks.length);
  return `<figure class="card chart"><figcaption>${h(t(title))}</figcaption><svg viewBox="0 0 ${width} 220" role="img" aria-label="${t("{0}. Valori nella tabella seguente.", h(t(title)))}"><line x1="45" y1="180" x2="620" y2="180" stroke="#b5acbd"/>${weeks.map((w, i) => {
    const v = field === 'workMs' ? w.workMs / 60000 : w[field], height = v / max * 135, x = 50 + i * step;
    return `<rect x="${x}" y="${180 - height}" width="${Math.max(3, step - 8)}" height="${height}" rx="3" fill="var(--accent-fill)"/><text x="${x + (step - 8) / 2}" y="${170 - height}" text-anchor="middle">${field === 'count' ? String(v) : decimal(v)}</text><text x="${x + (step - 8) / 2}" y="202" text-anchor="middle">${w.week.slice(8, 10)}/${w.week.slice(5, 7)}</text>`;
  }).join('')}</svg><p class="muted">${t("Colore principale: {0} per settimana (lunedì, UTC).", h(t(unit)))}</p><details><summary>${t("Tabella dati · {0}", h(t(title)))}</summary><div class="table-scroll"><table><thead><tr><th>${t("Settimana dal")}</th><th>${h(t(unit))}</th></tr></thead><tbody>${weeks.map(w => `<tr><td>${w.week.split('-').reverse().join('/')}</td><td>${field === 'count' ? w.count : decimal(field === 'workMs' ? w.workMs / 60000 : w[field])}</td></tr>`).join('')}</tbody></table></div></details></figure>`;
}
