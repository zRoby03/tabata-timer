import { t } from './i18n.js';
import { newWorkout, formatTime } from './model.js';
import { compileTimeline, plannedMs } from './timeline.js';
import { studyFields } from './study-fields.js';
import { MODES, isStudio, modeOf, unitFactor } from './modes.js';
import { escapeHtml as h } from './dom.js';

export const QUICK_PRESETS = [
  { name: t("Tabata 1"), workSec: 20, restSec: 10 },
  { name: t("Tabata 2"), workSec: 30, restSec: 15 },
  { name: t("Tabata 3"), workSec: 40, restSec: 20 }
];

export const STUDIO_PRESETS = [{ name: t("Studio 1"), workSec: 1500, restSec: 300 }, { name: t("Studio 2"), workSec: 3000, restSec: 600 }, { name: t("Studio 3"), workSec: 2700, restSec: 900 }];
export const quickPresets = mode => mode === 'STUDIO' ? STUDIO_PRESETS : QUICK_PRESETS;

export function newQuickWorkout(mode = 'WORKOUT') {
  const w = newWorkout(mode);
  if (mode === 'STUDIO') { w.id = 'quick-timer-studio'; w.title = t("Studio 1"); return w; }
  w.id = 'quick-timer'; w.title = t("Tabata 1");
  Object.assign(w.blocks[0], { id: 'quick-block', title: t("Intervalli"), restAfterBlockSec: 0 });
  Object.assign(w.blocks[0].exercises[0], { id: 'quick-exercise', title: t("Lavoro"), repetitions: 8, workSec: 20, restBetweenRepsSec: 10, restAfterExerciseSec: 0 });

  return w;
}

const numberField = (label, path, value, unit, min = 0, max = 3600, factor = 1) => `<label class="quick-field"><span>${t(label)}</span><span class="quick-value"><input data-quick-path="${path}" data-unit-factor="${factor}" type="number" inputmode="numeric" value="${Number.isFinite(value) ? value / factor : ''}" min="${min / factor}" max="${max / factor}" step="1" required aria-label="${t(label)}"><span>${unit}</span></span></label>`;

export function quickView(w, persistent) {
  const mode = modeOf(w), labels = MODES[mode], studio = isStudio(w), factor = unitFactor(w), unit = '';
  const e = studio ? { workSec: w.activities[0].concentrationSec, restBetweenRepsSec: w.activities[0].pauseSec } : w.blocks[0].exercises[0];
  let total = 0, error = '';
  try { total = plannedMs(compileTimeline(w)); } catch (e) { error = e.message; }
  return `<section class="quick-start">
    <div class="quick-heading"><p class="eyebrow">${t("IL TUO TEMPO, IL TUO RITMO")}</p><h1>Tabata Timer</h1><p class="muted">${t("{0}. Scegli i tempi e premi Avvia.", t(labels.subtitle))}</p></div>
    <div class="quick-presets" aria-label="${t("Impostazioni rapide")}">${quickPresets(mode).map((p, i) => `<button data-action="quick-preset" data-preset="${i}" aria-pressed="${e.workSec === p.workSec && e.restBetweenRepsSec === p.restSec}"><strong>${p.name}</strong><span>${p.workSec / factor} / ${p.restSec / factor}</span></button>`).join('')}</div>
    ${studio ? `<p class="muted small">${t("Le scelte rapide impostano i tempi della prima attività.")}</p>` : ''}
    <form id="quick-form" class="card quick-card">
      <div class="quick-total"><span>${studio ? t("Durata programma") : t("Durata allenamento")}</span><strong id="quick-total">${formatTime(total)}</strong><span class="prepare-badge">${t("+ 0:20 per prepararti")}</span></div>
      ${studio ? studyFields(w, true) : numberField(t(labels.focus), 'blocks.0.exercises.0.workSec', e.workSec, unit, 5) + numberField(t(labels.rest), 'blocks.0.exercises.0.restBetweenRepsSec', e.restBetweenRepsSec, unit) + numberField(t(labels.rounds), 'blocks.0.exercises.0.repetitions', e.repetitions, '', 1, 100)}
      <p id="quick-error" class="field-error" role="status" ${error ? '' : 'hidden'}>${h(t(error))}</p>
      <div class="quick-actions"><button type="submit" class="primary quick-start-button" id="quick-start-button" ${error ? 'disabled' : ''}>${t("Avvia")}</button><button type="button" data-action="quick-save" ${error ? 'disabled' : ''}>${t("Salva {0}", t(labels.noun))}</button></div>
      ${studio ? '' : `<details class="quick-options"><summary>${t("Altre opzioni")}</summary>
        ${numberField(t("Serie"), 'blocks.0.cycles', w.blocks[0].cycles, '', 1, 100)}
        ${numberField(t(labels.final), 'cooldownSec', w.cooldownSec, '')}
        <p class="muted">${t("Il recupero si applica tra i giri, mai dopo l’ultimo.")}</p>
        <div class="row"><button type="button" data-action="new">${t("Crea {0} personalizzato", t(labels.noun))}</button></div>
      </details>`}
    </form>

  </section>`;
}
