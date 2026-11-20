// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { t } from './i18n.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { t } from './i18n.js';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import { escapeHtml as h, decimal } from './dom.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { escapeHtml as h, decimal } from './dom.js';
// DOC L0003: chart: Genera il grafico SVG accessibile per una metrica delle settimane aggregate.
export function chart(weeks, field, title, unit) {
  // DOC L0004: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. max: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const max = Math.max(1, ...weeks.map(w => field === 'workMs' ? w.workMs / 60000 : w[field]));
  // DOC L0005: width: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chart.
  const width = 640, step = 560 / Math.max(1, weeks.length);
  // DOC L0006: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Restituisce il valore calcolato o conclude il percorso della funzione. Genera o aggiorna HTML della UI; i dati utente devono passare attraverso l’escaping previsto dal modulo.
  return `<figure class="card chart"><figcaption>${h(t(title))}</figcaption><svg viewBox="0 0 ${width} 220" role="img" aria-label="${t("{0}. Valori nella tabella seguente.", h(t(title)))}"><line x1="45" y1="180" x2="620" y2="180" stroke="#b5acbd"/>${weeks.map((w, i) => {
    // DOC L0007: v: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    const v = field === 'workMs' ? w.workMs / 60000 : w[field], height = v / max * 135, x = 50 + i * step;
    // DOC L0008: Restituisce il valore calcolato o conclude il percorso della funzione. Genera o aggiorna HTML della UI; i dati utente devono passare attraverso l’escaping previsto dal modulo.
    return `<rect x="${x}" y="${180 - height}" width="${Math.max(3, step - 8)}" height="${height}" rx="3" fill="var(--accent-fill)"/><text x="${x + (step - 8) / 2}" y="${170 - height}" text-anchor="middle">${field === 'count' ? String(v) : decimal(v)}</text><text x="${x + (step - 8) / 2}" y="202" text-anchor="middle">${w.week.slice(8, 10)}/${w.week.slice(5, 7)}</text>`;
  // DOC L0009: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  }).join('')}</svg><p class="muted">${t("Colore principale: {0} per settimana (lunedì, UTC).", h(t(unit)))}</p><details><summary>${t("Tabella dati · {0}", h(t(title)))}</summary><div class="table-scroll"><table><thead><tr><th>${t("Settimana dal")}</th><th>${h(t(unit))}</th></tr></thead><tbody>${weeks.map(w => `<tr><td>${w.week.split('-').reverse().join('/')}</td><td>${field === 'count' ? w.count : decimal(field === 'workMs' ? w.workMs / 60000 : w[field])}</td></tr>`).join('')}</tbody></table></div></details></figure>`;
// DOC L0010: Chiude il costrutto aperto alla riga 3.
}
