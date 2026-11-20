import { t } from './i18n.js';
import { escapeHtml as h } from './dom.js';

// Both Studio forms edit the same flat model. Durations stay in seconds internally.
export function studyFields(w, quick = false) {
  const attr = quick ? 'data-quick-path' : 'data-path';
  const field = (label, path, value, index, factor = 1, min = 0, max = 3600) => `<label>${label}<input ${attr}="${path}" data-unit-factor="${factor}" type="number" inputmode="numeric" value="${Number.isFinite(value) ? value / factor : ''}" min="${min / factor}" max="${max / factor}" step="1" required aria-label="${label}${index ? t(" · Attività {0}", index) : ''}"></label>`;
  return `${w.activities.map((a, i) => `<fieldset class="study-activity"><legend>${t("Attività {0}", i + 1)}</legend><label>${t("Nome attività")}<input ${attr}="activities.${i}.title" value="${h(a.title)}" maxlength="120" required aria-label="${t("Nome attività · Attività {0}", i + 1)}"></label><div class="field-grid">${field(t("Concentrazione"), `activities.${i}.concentrationSec`, a.concentrationSec, i + 1, 60, 60)}${field(t("Sessioni"), `activities.${i}.sessions`, a.sessions, i + 1, 1, 1, 100)}${field(t("Pausa"), `activities.${i}.pauseSec`, a.pauseSec, i + 1, 60)}</div><button type="button" class="text-button danger" data-action="activity-delete" data-index="${i}" data-quick="${quick}" ${w.activities.length === 1 ? 'disabled' : ''} aria-label="${t("Elimina Attività {0}", i + 1)}">${t("Elimina attività")}</button></fieldset>`).join('')}<button type="button" data-action="activity-add" data-quick="${quick}">${t("+ Aggiungi attività")}</button>${field(t("Pausa tra attività"), 'restBetweenActivitiesSec', w.restBetweenActivitiesSec, 0, 60)}`;
}
