// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { t } from './i18n.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { t } from './i18n.js';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import { escapeHtml as h } from './dom.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { escapeHtml as h } from './dom.js';

// Both Studio forms edit the same flat model. Durations stay in seconds internally.
// DOC L0005: studyFields: Genera le schede piatte Studio e la pausa globale tra attività, con valori utente escapati.
export function studyFields(w, quick = false) {
  // DOC L0006: attr: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di studyFields.
  const attr = quick ? 'data-quick-path' : 'data-path';
  // DOC L0007: field: Costruisce un campo dell’editor con percorso del modello e conversione dell’unità.
  const field = (label, path, value, index, factor = 1, min = 0, max = 3600) => `<label>${label}<input ${attr}="${path}" data-unit-factor="${factor}" type="number" inputmode="numeric" value="${Number.isFinite(value) ? value / factor : ''}" min="${min / factor}" max="${max / factor}" step="1" required aria-label="${label}${index ? t(" · Attività {0}", index) : ''}"></label>`;
  // DOC L0008: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Restituisce il valore calcolato o conclude il percorso della funzione. Genera o aggiorna HTML della UI; i dati utente devono passare attraverso l’escaping previsto dal modulo.
  return `${w.activities.map((a, i) => `<fieldset class="study-activity"><legend>${t("Attività {0}", i + 1)}</legend><label>${t("Nome attività")}<input ${attr}="activities.${i}.title" value="${h(a.title)}" maxlength="120" required aria-label="${t("Nome attività · Attività {0}", i + 1)}"></label><div class="field-grid">${field(t("Concentrazione"), `activities.${i}.concentrationSec`, a.concentrationSec, i + 1, 60, 60)}${field(t("Sessioni"), `activities.${i}.sessions`, a.sessions, i + 1, 1, 1, 100)}${field(t("Pausa"), `activities.${i}.pauseSec`, a.pauseSec, i + 1, 60)}</div><button type="button" class="text-button danger" data-action="activity-delete" data-index="${i}" data-quick="${quick}" ${w.activities.length === 1 ? 'disabled' : ''} aria-label="${t("Elimina Attività {0}", i + 1)}">${t("Elimina attività")}</button></fieldset>`).join('')}<button type="button" data-action="activity-add" data-quick="${quick}">${t("+ Aggiungi attività")}</button>${field(t("Pausa tra attività"), 'restBetweenActivitiesSec', w.restBetweenActivitiesSec, 0, 60)}`;
// DOC L0009: Chiude il costrutto aperto alla riga 5.
}
