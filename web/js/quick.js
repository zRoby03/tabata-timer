// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { t } from './i18n.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { t } from './i18n.js';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import { newWorkout, formatTime } from './model.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { newWorkout, formatTime } from './model.js';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import { compileTimeline, plannedMs } from './timeline.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { compileTimeline, plannedMs } from './timeline.js';
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import { studyFields } from './study-fields.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { studyFields } from './study-fields.js';
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import { MODES, isStudio, modeOf, unitFactor } from './modes.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { MODES, isStudio, modeOf, unitFactor } from './modes.js';
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import { escapeHtml as h } from './dom.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { escapeHtml as h } from './dom.js';

// DOC L0008: QUICK_PRESETS: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
export const QUICK_PRESETS = [
  // DOC L0009: Esegue il passaggio esplicito «{ name: t("Tabata 1"), workSec: 20, restSec: 10 },» nel flusso: Schermata rapida Workout e Studio.
  { name: t("Tabata 1"), workSec: 20, restSec: 10 },
  // DOC L0010: Esegue il passaggio esplicito «{ name: t("Tabata 2"), workSec: 30, restSec: 15 },» nel flusso: Schermata rapida Workout e Studio.
  { name: t("Tabata 2"), workSec: 30, restSec: 15 },
  // DOC L0011: Esegue il passaggio esplicito «{ name: t("Tabata 3"), workSec: 40, restSec: 20 }» nel flusso: Schermata rapida Workout e Studio.
  { name: t("Tabata 3"), workSec: 40, restSec: 20 }
// DOC L0012: Chiude un blocco, una chiamata o un valore composto; non aggiunge una nuova operazione.
];

// DOC L0014: STUDIO_PRESETS: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
export const STUDIO_PRESETS = [{ name: t("Studio 1"), workSec: 1500, restSec: 300 }, { name: t("Studio 2"), workSec: 3000, restSec: 600 }, { name: t("Studio 3"), workSec: 2700, restSec: 900 }];
// DOC L0015: quickPresets: Seleziona le tre scelte rapide appropriate alla modalità corrente.
export const quickPresets = mode => mode === 'STUDIO' ? STUDIO_PRESETS : QUICK_PRESETS;

// DOC L0017: newQuickWorkout: Prepara il modello del timer rapido, distinto tra Workout e Studio.
export function newQuickWorkout(mode = 'WORKOUT') {
  // DOC L0018: w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di newQuickWorkout.
  const w = newWorkout(mode);
  // DOC L0019: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di newQuickWorkout.
  if (mode === 'STUDIO') { w.id = 'quick-timer-studio'; w.title = t("Studio 1"); return w; }
  // DOC L0020: Esegue il passaggio esplicito «w.id = 'quick-timer'; w.title = t("Tabata 1");» nel flusso: Schermata rapida Workout e Studio. Nel contesto di newQuickWorkout.
  w.id = 'quick-timer'; w.title = t("Tabata 1");
  // DOC L0021: Esegue il passaggio esplicito «Object.assign(w.blocks[0], { id: 'quick-block', title: t("Intervalli"), restAfterBlockSec: 0 });» nel flusso: Schermata rapida Workout e Studio. Nel contesto di newQuickWorkout.
  Object.assign(w.blocks[0], { id: 'quick-block', title: t("Intervalli"), restAfterBlockSec: 0 });
  // DOC L0022: Esegue il passaggio esplicito «Object.assign(w.blocks[0].exercises[0], { id: 'quick-exercise', title: t("Lavoro"), repetitions: 8, workSec: 20, restBetweenRepsSec: 10, restAfterExerciseSec: 0 });» nel flusso: Schermata rapida Workout e Studio. Nel contesto di newQuickWorkout.
  Object.assign(w.blocks[0].exercises[0], { id: 'quick-exercise', title: t("Lavoro"), repetitions: 8, workSec: 20, restBetweenRepsSec: 10, restAfterExerciseSec: 0 });

  // DOC L0024: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di newQuickWorkout.
  return w;
// DOC L0025: Chiude il costrutto aperto alla riga 17.
}

// DOC L0027: numberField: Produce un campo numerico web con limiti e fattore di conversione espliciti.
const numberField = (label, path, value, unit, min = 0, max = 3600, factor = 1) => `<label class="quick-field"><span>${t(label)}</span><span class="quick-value"><input data-quick-path="${path}" data-unit-factor="${factor}" type="number" inputmode="numeric" value="${Number.isFinite(value) ? value / factor : ''}" min="${min / factor}" max="${max / factor}" step="1" required aria-label="${t(label)}"><span>${unit}</span></span></label>`;

// DOC L0029: quickView: Mostra durata, preparazione, preset e campi essenziali, ricavando gli errori dalla timeline.
export function quickView(w, persistent) {
  // DOC L0030: mode: modalità WORKOUT o STUDIO, usata per filtrare programmi e attività. Nel contesto di quickView.
  const mode = modeOf(w), labels = MODES[mode], studio = isStudio(w), factor = unitFactor(w), unit = '';
  // DOC L0031: e: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di quickView.
  const e = studio ? { workSec: w.activities[0].concentrationSec, restBetweenRepsSec: w.activities[0].pauseSec } : w.blocks[0].exercises[0];
  // DOC L0032: total: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di quickView.
  let total = 0, error = '';
  // DOC L0033: Esegue il passaggio esplicito «try { total = plannedMs(compileTimeline(w)); } catch (e) { error = e.message; }» nel flusso: Schermata rapida Workout e Studio. Nel contesto di quickView.
  try { total = plannedMs(compileTimeline(w)); } catch (e) { error = e.message; }
  // DOC L0034: Restituisce il valore calcolato o conclude il percorso della funzione. Genera o aggiorna HTML della UI; i dati utente devono passare attraverso l’escaping previsto dal modulo. Nel contesto di quickView.
  return `<section class="quick-start">
    <div class="quick-heading"><p class="eyebrow">${t("IL TUO TEMPO, IL TUO RITMO")}</p><h1>Tabata Timer</h1><p class="muted">${t("{0}. Scegli i tempi e premi Avvia.", t(labels.subtitle))}</p></div>
    <div class="quick-presets" aria-label="${t("Impostazioni rapide")}">${quickPresets(mode).map((p, i) => `<button data-action="quick-preset" data-preset="${i}" aria-pressed="${e.workSec === p.workSec && e.restBetweenRepsSec === p.restSec}"><strong>${p.name}</strong><span>${p.workSec / factor} / ${p.restSec / factor}</span></button>`).join('')}</div>
    ${studio ? `<p class="muted small">${t("Le scelte rapide impostano i tempi della prima attività.")}</p>` : ''}
    <form id="quick-form" class="card quick-card">
      <div class="quick-total"><span>${studio ? t("Durata programma") : t("Durata allenamento")}</span><strong id="quick-total">${formatTime(total)}</strong><span class="prepare-badge">${t("+ 0:20 per prepararti")}</span></div>
      ${studio ? studyFields(w, true) : numberField(t(labels.focus), 'blocks.0.exercises.0.workSec', e.workSec, unit, 1) + numberField(t(labels.rest), 'blocks.0.exercises.0.restBetweenRepsSec', e.restBetweenRepsSec, unit) + numberField(t(labels.rounds), 'blocks.0.exercises.0.repetitions', e.repetitions, '', 1, 100)}
      <p id="quick-error" class="field-error" role="status" ${error ? '' : 'hidden'}>${h(t(error))}</p>
      <div class="quick-actions"><button type="submit" class="primary quick-start-button" id="quick-start-button" ${error ? 'disabled' : ''}>${t("Avvia")}</button><button type="button" data-action="quick-save" ${error ? 'disabled' : ''}>${t("Salva {0}", t(labels.noun))}</button></div>
      ${studio ? '' : `<details class="quick-options"><summary>${t("Altre opzioni")}</summary>
        ${numberField(t("Serie"), 'blocks.0.cycles', w.blocks[0].cycles, '', 1, 100)}
        ${numberField(t("Recupero tra serie"), 'blocks.0.restBetweenCyclesSec', w.blocks[0].restBetweenCyclesSec, '')}
        ${numberField(t(labels.final), 'cooldownSec', w.cooldownSec, '')}
        <p class="muted">${t("Il recupero si applica tra i giri, mai dopo l’ultimo.")}</p>
        <div class="row"><button type="button" data-action="new">${t("Crea {0} personalizzato", t(labels.noun))}</button></div>
      </details>`}
    </form>

  </section>`;
// DOC L0053: Chiude il costrutto aperto alla riga 29.
}
