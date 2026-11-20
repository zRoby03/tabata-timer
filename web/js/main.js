// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { t, setLanguage, translateDocument } from './i18n.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { t, setLanguage, translateDocument } from './i18n.js';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import { MODES, isStudio, modeData, loadMode, saveMode } from './modes.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { MODES, isStudio, modeData, loadMode, saveMode } from './modes.js';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import { TransitionCues } from './transitions.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { TransitionCues } from './transitions.js';
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import { loadAppearance, saveAppearance, applyAppearance } from './appearance.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { loadAppearance, saveAppearance, applyAppearance } from './appearance.js';
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import { normalizeWorkout, normalizationNotice, newStudyActivity, newWorkout, newBlock, newExercise, duplicateWorkout, presets, clone, uid, formatTime } from './model.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { normalizeWorkout, normalizationNotice, newStudyActivity, newWorkout, newBlock, newExercise, duplicateWorkout, presets, clone, uid, formatTime } from './model.js';
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import { compileTimeline, plannedMs } from './timeline.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { compileTimeline, plannedMs } from './timeline.js';
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import { LocalRepository } from './storage.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { LocalRepository } from './storage.js';
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import { editorView, preview } from './editor.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { editorView, preview } from './editor.js';
// DOC L0009: Rende disponibili le dipendenze esplicite di questa riga: import { TimerEngine, TimerSignals } from './timer.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { TimerEngine, TimerSignals } from './timer.js';
// DOC L0010: Rende disponibili le dipendenze esplicite di questa riga: import { CountdownCues } from './countdown.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { CountdownCues } from './countdown.js';
// DOC L0011: Rende disponibili le dipendenze esplicite di questa riga: import { completion } from './stats.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { completion } from './stats.js';
// DOC L0012: Rende disponibili le dipendenze esplicite di questa riga: import { workoutsView, historyView, statsView, settingsView } from './views.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { workoutsView, historyView, statsView, settingsView } from './views.js';
// DOC L0013: Rende disponibili le dipendenze esplicite di questa riga: import { parseBackup, makeBackup, sessionsCsv, download, MAX_BACKUP_BYTES } from './io.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { parseBackup, makeBackup, sessionsCsv, download, MAX_BACKUP_BYTES } from './io.js';
// DOC L0014: Rende disponibili le dipendenze esplicite di questa riga: import { escapeHtml as h, dateLabel } from './dom.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { escapeHtml as h, dateLabel } from './dom.js';
// DOC L0015: Rende disponibili le dipendenze esplicite di questa riga: import { newQuickWorkout, quickView, quickPresets } from './quick.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { newQuickWorkout, quickView, quickPresets } from './quick.js';
// DOC L0016: Rende disponibili le dipendenze esplicite di questa riga: import { helpView } from './help.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { helpView } from './help.js';

// DOC L0018: main: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const main = document.querySelector('main'), notice = document.querySelector('#notice');
// DOC L0019: announce: Mostra un messaggio tradotto nell’area di stato accessibile del browser.
function announce(message) { notice.textContent = t(message); notice.hidden = false; }
// DOC L0020: browserStorage: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
let browserStorage; try { browserStorage = window.localStorage; } catch { announce(t("Salvataggio browser non disponibile. Modalità temporanea attiva: esporta un backup prima di chiudere.")); }
// DOC L0021: appearance: preferenze di aspetto separate da programmi e cronologia.
let appearance = loadAppearance(browserStorage, announce); applyAppearance(appearance);
// DOC L0022: repo: repository browser che coordina memoria, validazione e persistenza locale.
const repo = new LocalRepository(browserStorage, announce); repo.load(parseBackup);
// DOC L0023: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
if (!repo.data.workouts.length && !repo.persistent) repo.data.workouts = presets();
// DOC L0024: route: schermata corrente della UI.
let route = 'home', draft = null, draftNotice = '', dirty = false, timer = null, savedSession = null, pendingImport = null, filterDays = 30, filterWorkout = '', pendingSave = null;
// DOC L0025: signals: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const signals = new TimerSignals();
// DOC L0026: countdown: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const countdown = new CountdownCues(), transitions = new TransitionCues();
// DOC L0027: soundTimer: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
let soundTimer = null, soundPhase = -1, soundState = 'idle';
// DOC L0028: soundCountdown: Sincronizza cambio fase e countdown, interrompendo segnali della visita precedente.
function soundCountdown() {
  // DOC L0029: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di soundCountdown.
  if (timer !== soundTimer || timer?.phaseRun !== soundPhase || (timer?.state !== soundState && timer?.state !== 'running')) {
    // DOC L0030: Esegue il passaggio esplicito «signals.stop(); soundTimer = timer; soundPhase = timer?.phaseRun;» nel flusso: Controller dell’applicazione web. Nel contesto di soundCountdown.
    signals.stop(); soundTimer = timer; soundPhase = timer?.phaseRun;
  // DOC L0031: Chiude il costrutto aperto alla riga 29.
  }
  // DOC L0032: Esegue il passaggio esplicito «soundState = timer?.state;» nel flusso: Controller dell’applicazione web. Nel contesto di soundCountdown.
  soundState = timer?.state;
  // DOC L0033: marker: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di soundCountdown.
  const marker = transitions.read(timer), second = countdown.read(timer);
  // DOC L0034: Prepara o riproduce il segnale audio; il motore del tempo rimane indipendente dalla riproduzione. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di soundCountdown.
  if (marker) signals.play(marker);
  // DOC L0035: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di soundCountdown.
  else if (second !== null) signals.horn(second, timer.currentPhase?.type);
// DOC L0036: Chiude il costrutto aperto alla riga 28.
}
// DOC L0037: cleanUrl: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const cleanUrl = new URL(location.href);
// DOC L0038: mode: modalità WORKOUT o STUDIO, usata per filtrare programmi e attività.
let mode = cleanUrl.searchParams.get('mode')?.toUpperCase();
// DOC L0039: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
if (cleanUrl.searchParams.has('v') || cleanUrl.searchParams.has('mode')) {
  // DOC L0040: Esegue il passaggio esplicito «cleanUrl.searchParams.delete('v'); cleanUrl.searchParams.delete('mode');» nel flusso: Controller dell’applicazione web.
  cleanUrl.searchParams.delete('v'); cleanUrl.searchParams.delete('mode');
  // DOC L0041: Esegue il passaggio esplicito «history.replaceState(null, '', cleanUrl);» nel flusso: Controller dell’applicazione web.
  history.replaceState(null, '', cleanUrl);
// DOC L0042: Chiude il costrutto aperto alla riga 39.
}
// DOC L0043: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
if (!Object.hasOwn(MODES, mode)) mode = loadMode(browserStorage);
// DOC L0044: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
else saveMode(browserStorage, mode);
// DOC L0045: quickByMode: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const quickByMode = { WORKOUT: newQuickWorkout(), STUDIO: newQuickWorkout('STUDIO') };
// DOC L0046: quick: modello del timer rapido per la modalità selezionata.
let quick = quickByMode[mode];
// DOC L0047: labels: Espone le etichette della modalità selezionata dal controller.
const labels = () => MODES[mode];
// DOC L0048: active: Indica se il timer corrente è in esecuzione o in pausa.
const active = () => timer && ['running', 'paused'].includes(timer.state);

// DOC L0050: navigate: Cambia schermata proteggendo il timer attivo e le bozze non salvate.
function navigate(next) {
  // DOC L0051: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di navigate.
  if (next === 'home' && active()) next = 'timer';
  // DOC L0052: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di navigate.
  if (route === 'editor' && next !== 'editor' && dirty) {
    // DOC L0053: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di navigate.
    if (!confirm(t("Uscire e scartare le modifiche non salvate?"))) return;
    // DOC L0054: Esegue il passaggio esplicito «dirty = false; draft = null;» nel flusso: Controller dell’applicazione web. Nel contesto di navigate.
    dirty = false; draft = null;
  // DOC L0055: Chiude il costrutto aperto alla riga 52.
  }
  // DOC L0056: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di navigate.
  if (next === 'editor' && !draft) draft = newWorkout(mode);
  // DOC L0057: Esegue il passaggio esplicito «route = next; render(); window.scrollTo(0, 0); main.focus({ preventScroll: true });» nel flusso: Controller dell’applicazione web. Nel contesto di navigate.
  route = next; render(); window.scrollTo(0, 0); main.focus({ preventScroll: true });
// DOC L0058: Chiude il costrutto aperto alla riga 50.
}
// DOC L0059: render: Seleziona la vista della route corrente, filtra l’archivio e sincronizza etichette, titolo e switch.
function render() {
  // DOC L0060: Esegue il passaggio esplicito «translateDocument();» nel flusso: Controller dell’applicazione web. Nel contesto di render.
  translateDocument();
  // DOC L0061: archive: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di render.
  const archive = modeData(repo.data, mode);
  // DOC L0062: Esegue il passaggio esplicito «document.title = "Tabata Timer";» nel flusso: Controller dell’applicazione web. Nel contesto di render.
  document.title = "Tabata Timer";
  // DOC L0063: Esegue il passaggio esplicito «document.body.dataset.mode = mode.toLowerCase();» nel flusso: Controller dell’applicazione web. Nel contesto di render.
  document.body.dataset.mode = mode.toLowerCase();
  // DOC L0064: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. modeSwitch: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di render.
  const modeSwitch = document.querySelector('#mode-switch');
  // DOC L0065: Esegue il passaggio esplicito «modeSwitch.setAttribute('aria-checked', String(mode === 'STUDIO'));» nel flusso: Controller dell’applicazione web. Nel contesto di render.
  modeSwitch.setAttribute('aria-checked', String(mode === 'STUDIO'));
  // DOC L0066: Esegue il passaggio esplicito «modeSwitch.disabled = active();» nel flusso: Controller dell’applicazione web. Nel contesto di render.
  modeSwitch.disabled = active();
  // DOC L0067: Esegue il passaggio esplicito «document.querySelector('[data-route=workouts]').textContent = t(labels().collection);» nel flusso: Controller dell’applicazione web. Nel contesto di render.
  document.querySelector('[data-route=workouts]').textContent = t(labels().collection);
  // DOC L0068: Esegue il passaggio esplicito «document.querySelector('#save-title').textContent = t("Salva {0}", t(labels().noun));» nel flusso: Controller dell’applicazione web. Nel contesto di render.
  document.querySelector('#save-title').textContent = t("Salva {0}", t(labels().noun));
  // DOC L0069: Esegue il passaggio esplicito «document.querySelector('#save-name-label').textContent = mode === 'STUDIO' ? t("Nome del programma") : t("Nome dell’allenamento");» nel flusso: Controller dell’applicazione web. Nel contesto di render.
  document.querySelector('#save-name-label').textContent = mode === 'STUDIO' ? t("Nome del programma") : t("Nome dell’allenamento");
  // DOC L0070: Esegue il passaggio esplicito «document.querySelector('#save-description').textContent = t("Lo ritroverai in {0} anche dopo aver chiuso il browser. Il salvataggio vale per questo browser e questo indirizzo.", t(» nel flusso: Controller dell’applicazione web. Nel contesto di render.
  document.querySelector('#save-description').textContent = t("Lo ritroverai in {0} anche dopo aver chiuso il browser. Il salvataggio vale per questo browser e questo indirizzo.", t(labels().collection));
  // DOC L0071: tab: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di render.
  const tab = ['home', 'timer'].includes(route) ? 'home' : ['editor', 'workouts'].includes(route) ? 'workouts' : ['history', 'stats'].includes(route) ? 'history' : '';
  // DOC L0072: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  document.querySelectorAll('[data-route]').forEach(b => b.setAttribute('aria-current', b.dataset.route === tab ? 'page' : 'false'));
  // DOC L0073: Esegue il passaggio esplicito «document.body.dataset.page = route;» nel flusso: Controller dell’applicazione web. Nel contesto di render.
  document.body.dataset.page = route;
  // DOC L0074: Genera o aggiorna HTML della UI; i dati utente devono passare attraverso l’escaping previsto dal modulo.
  main.innerHTML = route === 'home' ? quickView(quick, repo.persistent) : route === 'workouts' ? workoutsView(archive, repo.persistent, mode) : route === 'editor' ? editorView(draft, repo.data.workouts.some(w => w.id === draft.id), draftNotice) : route === 'timer' ? timerView() : route === 'history' ? historyView(archive.sessions, repo.persistent, mode) : route === 'stats' ? statsView(archive, filterDays, filterWorkout, mode) : route === 'help' ? helpView(mode) : settingsView(repo.persistent, appearance);
  // DOC L0075: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di render.
  if (route === 'timer') paintTimer();
// DOC L0076: Chiude il costrutto aperto alla riga 59.
}
// DOC L0077: timerView: Costruisce struttura e controlli della schermata timer; il polling ne aggiorna poi i soli valori necessari.
function timerView() {
  // DOC L0078: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di timerView.
  if (!timer) return quickView(quick, repo.persistent);
  // DOC L0079: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Restituisce il valore calcolato o conclude il percorso della funzione. Genera o aggiorna HTML della UI; i dati utente devono passare attraverso l’escaping previsto dal modulo. Nel contesto di timerView.
  return `<section class="card timer-panel"><p class="timer-workout-name">${h(timer.workout.title)}</p><h1 id="timer-status">${t("Pronto")}</h1><div class="timer-digits" id="timer-digits" role="timer" aria-label="${t("Tempo rimanente")}" aria-live="off">0:00</div><div class="timer-phase" id="timer-phase" aria-live="polite"></div><progress id="timer-progress" class="timer-progress" max="${timer.phases.length}" value="0" aria-label="${t("Avanzamento fasi")}"></progress><p id="timer-position" class="muted"></p><div class="row timer-main-controls"><button class="primary" data-action="pause" id="pause-button">${t("Pausa (timer)")}</button><button data-action="skip">${t("Salta →")}</button></div><div class="timer-next" id="timer-next"></div><details class="timer-options"><summary>${t("Altri controlli")}</summary><div class="row"><button data-action="previous">${t("← Precedente")}</button><button data-action="fullscreen">${t("Schermo intero")}</button></div><div class="timer-settings"><label class="check"><input type="checkbox" id="sound" ${signals.sound ? 'checked' : ''}>${t("Suoni")}</label><label class="check"><input type="checkbox" id="vibration" ${signals.vibration ? 'checked' : ''}>${t("Vibrazione")}</label><label class="check"><input type="checkbox" id="awake" ${signals.keepAwake ? 'checked' : ''}>${t("Schermo acceso")}</label></div><p class="timer-warning">${t("Bip brevi sul 3, 2, 1. Fischietto all’inizio del lavoro, sospiro all’inizio del recupero e tre ding alla fine dell’allenamento, prima dell’eventuale defaticamento.")}</p><p class="timer-warning">${t("Il timer recupera il tempo trascorso quando torni alla pagina. Audio e vibrazione possono non funzionare in background, a schermo bloccato o con browser sospeso.")}</p></details><button data-action="stop" class="text-button danger">${t("Termina")}</button><p class="timer-warning">${t("Mantieni la pagina visibile: gli avvisi in background non sono garantiti.")}</p><div id="timer-result" aria-live="polite"></div></section>`;
// DOC L0080: Chiude il costrutto aperto alla riga 77.
}
// DOC L0081: paintTimer: Aggiorna cifre, fase, prossima fase, controlli e risultato senza ricreare l’intera applicazione a ogni tick.
function paintTimer() {
  // DOC L0082: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di paintTimer.
  if (!timer || route !== 'timer') return;
  // DOC L0083: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di paintTimer.
  document.querySelector('#mode-switch').disabled = active();
  // DOC L0084: done: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di paintTimer.
  const done = timer.state === 'finished', p = timer.currentPhase;
  // DOC L0085: Esegue il passaggio esplicito «document.querySelector('#timer-status').textContent = done ? (timer.result.status === 'COMPLETED' ? (isStudio(timer.workout) ? t("Studio completato.") : t("Allenamento completato."» nel flusso: Controller dell’applicazione web. Nel contesto di paintTimer.
  document.querySelector('#timer-status').textContent = done ? (timer.result.status === 'COMPLETED' ? (isStudio(timer.workout) ? t("Studio completato.") : t("Allenamento completato.")) : t("Sessione interrotta.")) : timer.state === 'paused' ? t("In pausa") : p.type === 'PREPARE' ? t("Preparazione") : p.type === 'WORK' ? t(labels().focus) : p.type === 'WARMUP' ? t("Riscaldamento") : p.type === 'COOLDOWN' ? t(labels().final) : p.type === 'REST_LONG' ? t("Pausa lunga") : t(labels().rest);
  // DOC L0086: Esegue il passaggio esplicito «document.querySelector('#timer-digits').textContent = formatTime(timer.remainingMs);» nel flusso: Controller dell’applicazione web. Nel contesto di paintTimer.
  document.querySelector('#timer-digits').textContent = formatTime(timer.remainingMs);
  // DOC L0087: phaseText: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di paintTimer.
  const phaseText = done ? t("Sessione registrata") : timer.index < 0 ? t("Mettiti in posizione. Si parte tra poco.") : t(p.label);
  // DOC L0088: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. phaseNode: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di paintTimer.
  const phaseNode = document.querySelector('#timer-phase'); if (phaseNode.textContent !== phaseText) phaseNode.textContent = phaseText;
  // DOC L0089: Esegue il passaggio esplicito «document.querySelector('#timer-position').textContent = done ? t("{0}/{1} intervalli completati", timer.result.completedWorkIntervals, timer.result.plannedWorkIntervals) : timer.in» nel flusso: Controller dell’applicazione web. Nel contesto di paintTimer.
  document.querySelector('#timer-position').textContent = done ? t("{0}/{1} intervalli completati", timer.result.completedWorkIntervals, timer.result.plannedWorkIntervals) : timer.index < 0 ? t("20 secondi per prepararti") : t("Fase {0} di {1} · Tempo attivo {2}", timer.index + 1, timer.phases.length, formatTime(timer.elapsedMs));
  // DOC L0090: Esegue il passaggio esplicito «document.querySelector('#timer-progress').value = Math.max(0, Math.min(timer.index, timer.phases.length));» nel flusso: Controller dell’applicazione web. Nel contesto di paintTimer.
  document.querySelector('#timer-progress').value = Math.max(0, Math.min(timer.index, timer.phases.length));
  // DOC L0091: Esegue il passaggio esplicito «document.querySelector('#timer-next').textContent = timer.nextPhase ? t("Poi: {0} · {1}", t(timer.nextPhase.label), formatTime(timer.nextPhase.durationMs)) : t("Ultima fase. Il tuo» nel flusso: Controller dell’applicazione web. Nel contesto di paintTimer.
  document.querySelector('#timer-next').textContent = timer.nextPhase ? t("Poi: {0} · {1}", t(timer.nextPhase.label), formatTime(timer.nextPhase.durationMs)) : t("Ultima fase. Il tuo traguardo è qui.");
  // DOC L0092: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di paintTimer.
  for (const action of ['pause', 'skip', 'previous', 'stop']) document.querySelector(`[data-action="${action}"]`).disabled = done;
  // DOC L0093: Esegue il passaggio esplicito «document.querySelector('#pause-button').textContent = timer.state === 'paused' ? t("Riprendi") : t("Pausa (timer)");» nel flusso: Controller dell’applicazione web. Nel contesto di paintTimer.
  document.querySelector('#pause-button').textContent = timer.state === 'paused' ? t("Riprendi") : t("Pausa (timer)");
  // DOC L0094: resultNode: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di paintTimer.
  const resultNode = document.querySelector('#timer-result');
  // DOC L0095: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di paintTimer.
  if (done && resultNode.dataset.sessionId !== timer.result.id) {
    // DOC L0096: Esegue il passaggio esplicito «resultNode.dataset.sessionId = timer.result.id;» nel flusso: Controller dell’applicazione web. Nel contesto di paintTimer.
    resultNode.dataset.sessionId = timer.result.id;
    // DOC L0097: Genera o aggiorna HTML della UI; i dati utente devono passare attraverso l’escaping previsto dal modulo. Nel contesto di paintTimer.
    resultNode.innerHTML = `<p><strong>${t("{0}% completato", completion(timer.result).toFixed(0))}</strong> ${t("· {0} di {1} · {2} pause", formatTime(timer.workMs), isStudio(timer.workout) ? t("concentrazione effettiva") : t("lavoro effettivo"), timer.pauses)}</p><button data-action="history">${t("Vedi attività")}</button><button data-action="home">${t("Nuovo timer")}</button>`;
  // DOC L0098: Chiude il costrutto aperto alla riga 95.
  }
// DOC L0099: Chiude il costrutto aperto alla riga 81.
}
// DOC L0100: start: Protegge la sessione attiva, crea il motore, mostra il timer e sblocca audio e wake lock dopo il gesto dell’utente.
async function start(workout) {
  // DOC L0101: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di start.
  if (active()) { announce(t("C’è già una sessione attiva. Termina prima quella corrente.")); navigate('timer'); return; }
  // DOC L0102: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di start.
  if (dirty && !confirm(t("Avviare scartando le modifiche non salvate?"))) return;
  // DOC L0103: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. migration: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di start.
  const migration = normalizationNotice(workout); if (migration) announce(migration);
  // DOC L0104: Esegue il passaggio esplicito «timer = new TimerEngine(workout); savedSession = null; dirty = false; timer.start();» nel flusso: Controller dell’applicazione web. Nel contesto di start.
  timer = new TimerEngine(workout); savedSession = null; dirty = false; timer.start();
  // DOC L0105: Esegue il passaggio esplicito «navigate('timer');» nel flusso: Controller dell’applicazione web. Nel contesto di start.
  navigate('timer');
  // DOC L0106: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di start.
  if (!await signals.unlock()) announce(t("Suoni non disponibili in questo browser. Il timer continua senza audio."));
  // DOC L0107: Esegue il passaggio esplicito «try { await signals.wake(); } catch (e) { announce(e.message); }» nel flusso: Controller dell’applicazione web. Nel contesto di start.
  try { await signals.wake(); } catch (e) { announce(e.message); }
// DOC L0108: Chiude il costrutto aperto alla riga 100.
}
// DOC L0109: settle: Registra il risultato una sola volta, verificandone l’identità di sessione.
function settle() {
  // DOC L0110: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di settle.
  if (timer?.result && savedSession !== timer.result.id) {
    // DOC L0111: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. saved: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    const saved = repo.addSession(timer.result); savedSession = timer.result.id; signals.release().catch(() => {}); if (saved) announce(repo.persistent ? t("Sessione salvata in questo browser.") : t("Attività registrata per questa visita. Attiva il salvataggio o esporta una copia JSON per conservarla."));
  // DOC L0112: Chiude il costrutto aperto alla riga 110.
  }
// DOC L0113: Chiude il costrutto aperto alla riga 109.
}
// DOC L0114: mutateEditor: Applica aggiunta, rimozione o riordinamento alla bozza, rispettando i confini delle liste.
function mutateEditor(action, button) {
  // DOC L0115: bi: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di mutateEditor.
  const bi = Number(button.dataset.bi), ei = Number(button.dataset.ei), kind = action.startsWith('exercise') ? 'exercise' : 'block';
  // DOC L0116: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di mutateEditor.
  if (action === 'block-add') draft.blocks.push(newBlock(mode));
  // DOC L0117: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di mutateEditor.
  else if (action === 'exercise-add') draft.blocks[bi].exercises.push(newExercise(mode));
  // DOC L0118: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di mutateEditor.
  else {
    // DOC L0119: list: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di mutateEditor.
    const list = kind === 'block' ? draft.blocks : draft.blocks[bi].exercises, i = kind === 'block' ? bi : ei;
    // DOC L0120: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di mutateEditor.
    if (action.endsWith('delete')) { if (list.length === 1) { announce(t("Mantieni almeno un blocco e un esercizio.")); return; } if (!confirm(t("Eliminare questo elemento?"))) return; list.splice(i, 1); }
    // DOC L0121: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    else if (action.endsWith('copy')) { const copy = clone(list[i]); copy.id = uid(); if (kind === 'block') copy.exercises.forEach(e => { e.id = uid(); }); list.splice(i + 1, 0, copy); }
    // DOC L0122: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. to: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di mutateEditor.
    else { const to = i + (action.endsWith('up') ? -1 : 1); if (to < 0 || to >= list.length) return; [list[i], list[to]] = [list[to], list[i]]; }
  // DOC L0123: Chiude il costrutto aperto alla riga 118.
  }
  // DOC L0124: Esegue il passaggio esplicito «dirty = true; render();» nel flusso: Controller dell’applicazione web. Nel contesto di mutateEditor.
  dirty = true; render();
// DOC L0125: Chiude il costrutto aperto alla riga 114.
}
// DOC L0126: Registra il gestore dell’evento indicato; il corpo viene eseguito quando l’evento si verifica.
document.addEventListener('click', async event => {
  // DOC L0127: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. button: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Restituisce il valore calcolato o conclude il percorso della funzione.
  const button = event.target.closest('button'); if (!button) return;
  // DOC L0128: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione.
  if (button.dataset.route) { navigate(button.dataset.route); return; }
  // DOC L0129: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. action: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Restituisce il valore calcolato o conclude il percorso della funzione.
  const action = button.dataset.action; if (!action) return;
  // DOC L0130: workout: programma della sessione, distinto dalla bozza modificabile e dall’archivio.
  const workout = repo.data.workouts.find(w => w.id === button.dataset.id);
  // DOC L0131: Esegue il passaggio esplicito «try {» nel flusso: Controller dell’applicazione web.
  try {
    // DOC L0132: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
    if (action.startsWith('activity-')) {
      // DOC L0133: target: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
      const target = button.dataset.quick === 'true' ? quick : draft;
      // DOC L0134: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
      if (action === 'activity-add') target.activities.push(newStudyActivity());
      // DOC L0135: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
      else if (target.activities.length > 1) target.activities.splice(Number(button.dataset.index), 1);
      // DOC L0136: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione.
      if (target === draft) dirty = true; render(); return;
    // DOC L0137: Chiude il costrutto aperto alla riga 132.
    }
    // DOC L0138: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione.
    if (action.startsWith('block-') || action.startsWith('exercise-')) { mutateEditor(action, button); return; }
    // DOC L0139: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione.
    if (['home', 'workouts', 'history', 'stats', 'settings', 'help'].includes(action)) { navigate(action); return; }
    // DOC L0140: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
    switch (action) {
      // DOC L0141: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
      case 'language': { notice.hidden = true; notice.textContent = ''; const saved = setLanguage(button.dataset.value); render(); if (!saved) announce(t('Preferenza applicata per questa visita. Il browser non ha potuto salvarla.')); break; }
      // DOC L0142: Esegue il passaggio esplicito «case 'mode': {» nel flusso: Controller dell’applicazione web.
      case 'mode': {
        // DOC L0143: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione.
        if (active()) { announce(t("Termina la sessione prima di cambiare modalità.")); return; }
        // DOC L0144: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione.
        if (dirty && !confirm(t("Scartare le modifiche non salvate e cambiare modalità?"))) return;
        // DOC L0145: Esegue il passaggio esplicito «notice.hidden = true; notice.textContent = '';» nel flusso: Controller dell’applicazione web.
        notice.hidden = true; notice.textContent = '';
        // DOC L0146: Esegue il passaggio esplicito «mode = mode === 'WORKOUT' ? 'STUDIO' : 'WORKOUT'; quick = quickByMode[mode]; saveMode(browserStorage, mode);» nel flusso: Controller dell’applicazione web.
        mode = mode === 'WORKOUT' ? 'STUDIO' : 'WORKOUT'; quick = quickByMode[mode]; saveMode(browserStorage, mode);
        // DOC L0147: Esegue il passaggio esplicito «timer = null; draft = null; dirty = false; filterWorkout = ''; route = 'home'; render(); break;» nel flusso: Controller dell’applicazione web.
        timer = null; draft = null; dirty = false; filterWorkout = ''; route = 'home'; render(); break;
      // DOC L0148: Chiude il costrutto aperto alla riga 142.
      }
      // DOC L0149: Esegue il passaggio esplicito «case 'theme':» nel flusso: Controller dell’applicazione web.
      case 'theme':
      // DOC L0150: Esegue il passaggio esplicito «case 'palette': {» nel flusso: Controller dell’applicazione web.
      case 'palette': {
        // DOC L0151: Esegue il passaggio esplicito «appearance = { ...appearance, [action]: button.dataset.value };» nel flusso: Controller dell’applicazione web.
        appearance = { ...appearance, [action]: button.dataset.value };
        // DOC L0152: Esegue il passaggio esplicito «applyAppearance(appearance); saveAppearance(browserStorage, appearance, announce);» nel flusso: Controller dell’applicazione web.
        applyAppearance(appearance); saveAppearance(browserStorage, appearance, announce);
        // DOC L0153: Esegue il passaggio esplicito «render(); main.querySelector(`[data-action="${action}"][data-value="${button.dataset.value}"]`)?.focus();» nel flusso: Controller dell’applicazione web.
        render(); main.querySelector(`[data-action="${action}"][data-value="${button.dataset.value}"]`)?.focus();
        // DOC L0154: Esegue il passaggio esplicito «break;» nel flusso: Controller dell’applicazione web.
        break;
      // DOC L0155: Chiude il costrutto aperto alla riga 150.
      }
      // DOC L0156: Esegue il passaggio esplicito «case 'quick-preset': {» nel flusso: Controller dell’applicazione web.
      case 'quick-preset': {
        // DOC L0157: p: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
        const p = quickPresets(mode)[Number(button.dataset.preset)];
        // DOC L0158: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione.
        if (!p) return;
        // DOC L0159: Esegue il passaggio esplicito «quick.title = p.name;» nel flusso: Controller dell’applicazione web.
        quick.title = p.name;
        // DOC L0160: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
        if (mode === 'STUDIO') Object.assign(quick.activities[0], { concentrationSec: p.workSec, pauseSec: p.restSec });
        // DOC L0161: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
        else Object.assign(quick.blocks[0].exercises[0], { workSec: p.workSec, restBetweenRepsSec: p.restSec });
        // DOC L0162: Esegue il passaggio esplicito «render(); break;» nel flusso: Controller dell’applicazione web.
        render(); break;
      // DOC L0163: Chiude il costrutto aperto alla riga 156.
      }
      // DOC L0164: Esegue il passaggio esplicito «case 'quick-save': {» nel flusso: Controller dell’applicazione web.
      case 'quick-save': {
        // DOC L0165: Esegue il passaggio esplicito «compileTimeline(quick);» nel flusso: Controller dell’applicazione web.
        compileTimeline(quick);
        // DOC L0166: Esegue il passaggio esplicito «pendingSave = duplicateWorkout(quick); pendingSave.title = quick.title;» nel flusso: Controller dell’applicazione web.
        pendingSave = duplicateWorkout(quick); pendingSave.title = quick.title;
        // DOC L0167: Esegue il passaggio esplicito «document.querySelector('#save-name').value = quick.title;» nel flusso: Controller dell’applicazione web.
        document.querySelector('#save-name').value = quick.title;
        // DOC L0168: Esegue il passaggio esplicito «document.querySelector('#save-error').hidden = true;» nel flusso: Controller dell’applicazione web.
        document.querySelector('#save-error').hidden = true;
        // DOC L0169: Esegue il passaggio esplicito «document.querySelector('#save-dialog').showModal();» nel flusso: Controller dell’applicazione web.
        document.querySelector('#save-dialog').showModal();
        // DOC L0170: Esegue il passaggio esplicito «document.querySelector('#save-name').select();» nel flusso: Controller dell’applicazione web.
        document.querySelector('#save-name').select();
        // DOC L0171: Esegue il passaggio esplicito «break;» nel flusso: Controller dell’applicazione web.
        break;
      // DOC L0172: Chiude il costrutto aperto alla riga 164.
      }
      // DOC L0173: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione.
      case 'new': if (dirty && !confirm(t("Scartare le modifiche?"))) return; draft = newWorkout(mode); draftNotice = ''; dirty = false; navigate('editor'); break;
      // DOC L0174: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione.
      case 'edit': if (dirty && !confirm(t("Scartare le modifiche?"))) return; draftNotice = normalizationNotice(workout); draft = normalizeWorkout(workout); dirty = false; navigate('editor'); break;
      // DOC L0175: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
      case 'copy': { const saved = repo.upsertWorkout(duplicateWorkout(workout)); render(); if (saved) announce(t("Allenamento duplicato.")); break; }
      // DOC L0176: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione.
      case 'delete': if (!confirm(t("Eliminare “{0}”? Le sessioni restano in cronologia.", workout.title))) return; repo.data.workouts = repo.data.workouts.filter(w => w.id !== workout.id); repo.save(); render(); break;
      // DOC L0177: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
      case 'presets': presets(mode).forEach(w => repo.upsertWorkout(w)); render(); break;
      // DOC L0178: Esegue il passaggio esplicito «case 'start': await start(workout); break;» nel flusso: Controller dell’applicazione web.
      case 'start': await start(workout); break;
      // DOC L0179: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
      case 'pause': if (timer?.state === 'paused') { await signals.unlock(); timer.resume(); } else { timer?.pause(); signals.stop(); } soundCountdown(); settle(); paintTimer(); break;
      // DOC L0180: Esegue il passaggio esplicito «case 'skip': signals.stop(); timer?.skip(); soundCountdown(); settle(); paintTimer(); break;» nel flusso: Controller dell’applicazione web.
      case 'skip': signals.stop(); timer?.skip(); soundCountdown(); settle(); paintTimer(); break;
      // DOC L0181: Esegue il passaggio esplicito «case 'previous': signals.stop(); timer?.previous(); soundCountdown(); settle(); paintTimer(); break;» nel flusso: Controller dell’applicazione web.
      case 'previous': signals.stop(); timer?.previous(); soundCountdown(); settle(); paintTimer(); break;
      // DOC L0182: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
      case 'stop': if (active() && confirm(t("Terminare e registrare la sessione come interrotta?"))) { signals.stop(); timer.stop(); soundCountdown(); settle(); paintTimer(); } break;
      // DOC L0183: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
      case 'fullscreen': if (document.fullscreenElement) await document.exitFullscreen(); else if (main.querySelector('.timer-panel').requestFullscreen) await main.querySelector('.timer-panel').requestFullscreen(); else announce(t("Schermo intero non supportato: puoi usare il timer nella pagina.")); break;
      // DOC L0184: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
      case 'persist': if (repo.enable()) announce(t("Allenamenti e attività salvati in questo browser.")); render(); break;
      // DOC L0185: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
      case 'temporary': if (confirm(t("Rimuovere il salvataggio da questo browser e continuare solo per questa visita? Esporta prima una copia JSON per conservarlo."))) { repo.disable(); render(); } break;
      // DOC L0186: Esegue il passaggio esplicito «case 'export-json': download(makeBackup(repo.data), 'tabata-backup-v1.json', 'application/json'); break;» nel flusso: Controller dell’applicazione web.
      case 'export-json': download(makeBackup(repo.data), 'tabata-backup-v1.json', 'application/json'); break;
      // DOC L0187: Esegue il passaggio esplicito «case 'export-csv': download(sessionsCsv(modeData(repo.data, mode).sessions), 'tabata-sessioni.csv', 'text/csv;charset=utf-8'); break;» nel flusso: Controller dell’applicazione web.
      case 'export-csv': download(sessionsCsv(modeData(repo.data, mode).sessions), 'tabata-sessioni.csv', 'text/csv;charset=utf-8'); break;
      // DOC L0188: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione.
      case 'import': if (active()) { announce(t("Termina la sessione prima di importare.")); return; } document.querySelector('#import-file').click(); break;
      // DOC L0189: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione.
      case 'clear': if (active()) { announce(t("Termina la sessione prima di cancellare i dati.")); return; } if (confirm(t("Cancellare tutti gli allenamenti e tutte le attività? Questa operazione non può essere annullata."))) { if (!repo.disable()) return; repo.data = { workouts: [], sessions: [] }; draft = null; dirty = false; timer = null; render(); announce(t("Dati cancellati. Modalità temporanea attiva.")); } break;
      // DOC L0190: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
      case 'session-delete': if (confirm(t("Eliminare questa sessione?"))) { repo.data.sessions = repo.data.sessions.filter(s => s.id !== button.dataset.id); repo.save(); render(); } break;
    // DOC L0191: Chiude il costrutto aperto alla riga 140.
    }
  // DOC L0192: Esegue il passaggio esplicito «} catch (e) { announce(e.message); }» nel flusso: Controller dell’applicazione web.
  } catch (e) { announce(e.message); }
// DOC L0193: Chiude il costrutto aperto alla riga 126.
});
// DOC L0194: Registra il gestore dell’evento indicato; il corpo viene eseguito quando l’evento si verifica.
main.addEventListener('input', event => {
  // DOC L0195: input: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const input = event.target;
  // DOC L0196: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
  if (input.dataset.quickPath) {
    // DOC L0197: path: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. target: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    const path = input.dataset.quickPath.split('.'), key = path.pop(); let target = quick;
    // DOC L0198: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    path.forEach(p => { target = target[p]; });
    // DOC L0199: Esegue il passaggio esplicito «target[key] = input.type === 'number' ? (input.value === '' ? NaN : Number(input.value) * Number(input.dataset.unitFactor || 1)) : input.value;» nel flusso: Controller dell’applicazione web.
    target[key] = input.type === 'number' ? (input.value === '' ? NaN : Number(input.value) * Number(input.dataset.unitFactor || 1)) : input.value;
    // DOC L0200: error: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    const error = document.querySelector('#quick-error');
    // DOC L0201: Esegue il passaggio esplicito «try {» nel flusso: Controller dell’applicazione web.
    try {
      // DOC L0202: Esegue il passaggio esplicito «document.querySelector('#quick-total').textContent = formatTime(plannedMs(compileTimeline(quick)));» nel flusso: Controller dell’applicazione web.
      document.querySelector('#quick-total').textContent = formatTime(plannedMs(compileTimeline(quick)));
      // DOC L0203: Esegue il passaggio esplicito «document.querySelector('#quick-start-button').disabled = false; document.querySelector('[data-action=quick-save]').disabled = false; error.hidden = true;» nel flusso: Controller dell’applicazione web.
      document.querySelector('#quick-start-button').disabled = false; document.querySelector('[data-action=quick-save]').disabled = false; error.hidden = true;
    // DOC L0204: Esegue il passaggio esplicito «} catch (e) { error.textContent = t(e.message); error.hidden = false; document.querySelector('#quick-start-button').disabled = true; document.querySelector('[data-action=quick-save» nel flusso: Controller dell’applicazione web.
    } catch (e) { error.textContent = t(e.message); error.hidden = false; document.querySelector('#quick-start-button').disabled = true; document.querySelector('[data-action=quick-save]').disabled = true; }
    // DOC L0205: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    document.querySelectorAll('[data-preset]').forEach(b => { const p = quickPresets(mode)[Number(b.dataset.preset)], e = mode === 'STUDIO' ? { workSec: quick.activities[0].concentrationSec, restBetweenRepsSec: quick.activities[0].pauseSec } : quick.blocks[0].exercises[0]; b.setAttribute('aria-pressed', String(e.workSec === p.workSec && e.restBetweenRepsSec === p.restSec)); });
    // DOC L0206: Restituisce il valore calcolato o conclude il percorso della funzione.
    return;
  // DOC L0207: Chiude il costrutto aperto alla riga 196.
  }
  // DOC L0208: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione.
  if (!input.dataset.path) return;
  // DOC L0209: path: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. target: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const path = input.dataset.path.split('.'), key = path.pop(); let target = draft;
  // DOC L0210: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  path.forEach(p => { target = target[p]; }); target[key] = input.type === 'number' ? (input.value === '' ? NaN : Number(input.value) * Number(input.dataset.unitFactor || 1)) : input.value; dirty = true;
  // DOC L0211: Genera o aggiorna HTML della UI; i dati utente devono passare attraverso l’escaping previsto dal modulo.
  document.querySelector('#preview').innerHTML = preview(draft);
// DOC L0212: Chiude il costrutto aperto alla riga 194.
});
// DOC L0213: Registra il gestore dell’evento indicato; il corpo viene eseguito quando l’evento si verifica.
main.addEventListener('submit', event => {
  // DOC L0214: Esegue il passaggio esplicito «event.preventDefault();» nel flusso: Controller dell’applicazione web.
  event.preventDefault();
  // DOC L0215: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione.
  if (event.target.id === 'quick-form') { start(quick).catch(e => announce(e.message)); return; }
  // DOC L0216: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
  try { compileTimeline(draft); const saved = repo.saveWorkoutLocally(clone(draft)); if (saved) { dirty = false; announce(t("Allenamento salvato sul dispositivo.")); navigate('workouts'); } } catch (e) { announce(e.message); }
// DOC L0217: Chiude il costrutto aperto alla riga 213.
});
// DOC L0218: Registra il gestore dell’evento indicato; il corpo viene eseguito quando l’evento si verifica.
main.addEventListener('change', async event => {
  // DOC L0219: input: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const input = event.target;
  // DOC L0220: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
  if (input.id === 'sound') { signals.sound = input.checked; if (!signals.sound) signals.stop(); if (signals.sound) await signals.unlock(); }
  // DOC L0221: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
  if (input.id === 'vibration') { signals.vibration = input.checked; if (!navigator.vibrate && input.checked) announce(t("Vibrazione non supportata in questo browser.")); }
  // DOC L0222: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
  if (input.id === 'awake') { signals.keepAwake = input.checked; try { if (input.checked && active()) await signals.wake(); else await signals.release(); } catch (e) { announce(t("Schermo acceso non disponibile: {0}", e.message)); } }
  // DOC L0223: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
  if (input.id === 'filter-days') { filterDays = Number(input.value); render(); }
  // DOC L0224: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
  if (input.id === 'filter-workout') { filterWorkout = input.value; render(); }
// DOC L0225: Chiude il costrutto aperto alla riga 218.
});
// DOC L0226: Registra il gestore dell’evento indicato; il corpo viene eseguito quando l’evento si verifica.
document.querySelector('#save-form').addEventListener('submit', event => {
  // DOC L0227: Esegue il passaggio esplicito «event.preventDefault();» nel flusso: Controller dell’applicazione web.
  event.preventDefault();
  // DOC L0228: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione.
  if (!pendingSave) return;
  // DOC L0229: error: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const error = document.querySelector('#save-error');
  // DOC L0230: Esegue il passaggio esplicito «try {» nel flusso: Controller dell’applicazione web.
  try {
    // DOC L0231: Esegue il passaggio esplicito «pendingSave.title = document.querySelector('#save-name').value.trim();» nel flusso: Controller dell’applicazione web.
    pendingSave.title = document.querySelector('#save-name').value.trim();
    // DOC L0232: Esegue il passaggio esplicito «compileTimeline(pendingSave);» nel flusso: Controller dell’applicazione web.
    compileTimeline(pendingSave);
    // DOC L0233: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
    if (!repo.saveWorkoutLocally(clone(pendingSave))) {
      // DOC L0234: Esegue il passaggio esplicito «error.textContent = t("Salvataggio non riuscito. L’allenamento resta disponibile per questa visita. Puoi riprovare oppure chiudere e creare una copia JSON dalle impostazioni.");» nel flusso: Controller dell’applicazione web.
      error.textContent = t("Salvataggio non riuscito. L’allenamento resta disponibile per questa visita. Puoi riprovare oppure chiudere e creare una copia JSON dalle impostazioni.");
      // DOC L0235: Restituisce il valore calcolato o conclude il percorso della funzione.
      error.hidden = false; return;
    // DOC L0236: Chiude il costrutto aperto alla riga 233.
    }
    // DOC L0237: Esegue il passaggio esplicito «quick.title = pendingSave.title; pendingSave = null;» nel flusso: Controller dell’applicazione web.
    quick.title = pendingSave.title; pendingSave = null;
    // DOC L0238: Esegue il passaggio esplicito «document.querySelector('#save-dialog').close();» nel flusso: Controller dell’applicazione web.
    document.querySelector('#save-dialog').close();
    // DOC L0239: Esegue il passaggio esplicito «navigate('workouts'); announce(t("Salvato sul dispositivo. Lo trovi in {0}.", t(labels().collection)));» nel flusso: Controller dell’applicazione web.
    navigate('workouts'); announce(t("Salvato sul dispositivo. Lo trovi in {0}.", t(labels().collection)));
  // DOC L0240: Esegue il passaggio esplicito «} catch (e) { error.textContent = t(e.message); error.hidden = false; }» nel flusso: Controller dell’applicazione web.
  } catch (e) { error.textContent = t(e.message); error.hidden = false; }
// DOC L0241: Chiude il costrutto aperto alla riga 226.
});
// DOC L0242: Registra il gestore dell’evento indicato; il corpo viene eseguito quando l’evento si verifica.
document.querySelector('#cancel-save').addEventListener('click', () => { pendingSave = null; document.querySelector('#save-dialog').close(); });
// DOC L0243: Registra il gestore dell’evento indicato; il corpo viene eseguito quando l’evento si verifica.
document.querySelector('#save-dialog').addEventListener('cancel', () => { pendingSave = null; });

// DOC L0245: Registra il gestore dell’evento indicato; il corpo viene eseguito quando l’evento si verifica.
document.querySelector('#import-file').addEventListener('change', async event => {
  // DOC L0246: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. file: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Restituisce il valore calcolato o conclude il percorso della funzione.
  const file = event.target.files[0]; event.target.value = ''; if (!file) return;
  // DOC L0247: Esegue il passaggio esplicito «try {» nel flusso: Controller dell’applicazione web.
  try {
    // DOC L0248: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
    if (file.size > MAX_BACKUP_BYTES) throw new Error(t("File troppo grande: massimo 5 MiB"));
    // DOC L0249: Esegue il passaggio esplicito «pendingImport = parseBackup(await file.text());» nel flusso: Controller dell’applicazione web.
    pendingImport = parseBackup(await file.text());
    // DOC L0250: Esegue il passaggio esplicito «document.querySelector('#import-preview').textContent = t("{0} allenamenti · {1} sessioni · esportato il {2}", pendingImport.workouts.length, pendingImport.sessions.length, dateLab» nel flusso: Controller dell’applicazione web.
    document.querySelector('#import-preview').textContent = t("{0} allenamenti · {1} sessioni · esportato il {2}", pendingImport.workouts.length, pendingImport.sessions.length, dateLabel(pendingImport.exportedAt));
    // DOC L0251: Esegue il passaggio esplicito «document.querySelector('#import-dialog').showModal();» nel flusso: Controller dell’applicazione web.
    document.querySelector('#import-dialog').showModal();
  // DOC L0252: Esegue il passaggio esplicito «} catch (e) { announce(t("Importazione rifiutata: {0}", e.message)); }» nel flusso: Controller dell’applicazione web.
  } catch (e) { announce(t("Importazione rifiutata: {0}", e.message)); }
// DOC L0253: Chiude il costrutto aperto alla riga 245.
});
// DOC L0254: Registra il gestore dell’evento indicato; il corpo viene eseguito quando l’evento si verifica.
document.querySelector('#confirm-import').addEventListener('click', () => {
  // DOC L0255: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione.
  if (!pendingImport || active()) return;
  // DOC L0256: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
  repo.data = { workouts: pendingImport.workouts, sessions: pendingImport.sessions }; const saved = repo.save(); pendingImport = null; dirty = false; draft = null; timer = null; document.querySelector('#import-dialog').close(); navigate('workouts'); if (saved) announce(repo.persistent ? t("Importazione completata e salvata sul dispositivo.") : t("Importazione completata per questa visita. Attiva il salvataggio per conservarla."));
// DOC L0257: Chiude il costrutto aperto alla riga 254.
});
// DOC L0258: Registra il gestore dell’evento indicato; il corpo viene eseguito quando l’evento si verifica.
document.querySelector('#cancel-import').addEventListener('click', () => { pendingImport = null; document.querySelector('#import-dialog').close(); });
// DOC L0259: Registra il gestore dell’evento indicato; il corpo viene eseguito quando l’evento si verifica.
document.querySelector('#import-dialog').addEventListener('cancel', () => { pendingImport = null; });
// DOC L0260: Registra il gestore dell’evento indicato; il corpo viene eseguito quando l’evento si verifica.
document.querySelector('.brand').addEventListener('click', event => { event.preventDefault(); navigate('home'); });
// DOC L0261: Registra il gestore dell’evento indicato; il corpo viene eseguito quando l’evento si verifica. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
document.addEventListener('visibilitychange', () => { timer?.tick(); soundCountdown(); settle(); if (document.visibilityState === 'visible') { paintTimer(); if (active()) signals.wake().catch(e => announce(e.message)); } });
// DOC L0262: Registra il gestore dell’evento indicato; il corpo viene eseguito quando l’evento si verifica. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione.
window.addEventListener('beforeunload', event => { if (active() || dirty) { event.preventDefault(); event.returnValue = ''; } });
// DOC L0263: Programma il prossimo aggiornamento; la durata effettiva rimane misurata dall’orologio, non dal numero di aggiornamenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
setInterval(() => { timer?.tick(); soundCountdown(); settle(); if (route === 'timer') paintTimer(); }, 100);
// DOC L0264: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
if ('serviceWorker' in navigator) navigator.serviceWorker.register('./sw.js').catch(() => announce(t("Cache offline non disponibile. La pagina aperta funziona; per ricaricarla serve il server locale.")));
// DOC L0265: Esegue il passaggio esplicito «render();» nel flusso: Controller dell’applicazione web.
render();
