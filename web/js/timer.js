// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { modeOf } from './modes.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { modeOf } from './modes.js';
// DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import { TRANSITION_SOUNDS, cueVolume } from './transitions.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { TRANSITION_SOUNDS, cueVolume } from './transitions.js';
// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import { hornSample, COUNTDOWN_SOUNDS } from './countdown.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { hornSample, COUNTDOWN_SOUNDS } from './countdown.js';
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import { uid } from './model.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { uid } from './model.js';
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import { compileTimeline, plannedMs } from './timeline.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { compileTimeline, plannedMs } from './timeline.js';
// DOC L0006: AUDIO_SOUNDS: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const AUDIO_SOUNDS = { ...COUNTDOWN_SOUNDS, ...TRANSITION_SOUNDS };
// DOC L0007: Fissa la preparazione a 20.000 millisecondi, separata dal lavoro pianificato. PREPARATION_MS: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
export const PREPARATION_MS = 20000;
// DOC L0008: PREPARATION: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const PREPARATION = Object.freeze({ type: 'PREPARE', blockId: null, exerciseId: null, durationMs: PREPARATION_MS, label: 'Preparazione' });

// The clock supplies an absolute monotonic timestamp; polling never decrements a counter.
// DOC L0011: Dichiara il tipo o contenitore di questo modulo: Motore web e segnali Web Audio. I campi e i metodi sono descritti nelle righe successive.
export class TimerEngine {
  // DOC L0012: constructor: Inizializza l’istanza TimerEngine, i suoi riferimenti e lo stato iniziale, senza avviare automaticamente il timer.
  constructor(workout, now = () => performance.now(), wall = () => new Date().toISOString()) {
    // DOC L0013: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di constructor.
    this.workout = structuredClone(workout); this.phases = compileTimeline(workout); this.now = now; this.wall = wall;
    // DOC L0014: Esegue il passaggio esplicito «this.index = -1; this.remainingMs = PREPARATION_MS; this.state = 'idle'; this.phaseRun = 0;» nel flusso: Motore web e segnali Web Audio. Nel contesto di constructor.
    this.index = -1; this.remainingMs = PREPARATION_MS; this.state = 'idle'; this.phaseRun = 0;
    // DOC L0015: Esegue il passaggio esplicito «this.elapsedMs = 0; this.workMs = 0; this.pauses = 0; this.completed = new Set(); this.completedPhases = new Set(); this.result = null;» nel flusso: Motore web e segnali Web Audio. Nel contesto di constructor.
    this.elapsedMs = 0; this.workMs = 0; this.pauses = 0; this.completed = new Set(); this.completedPhases = new Set(); this.result = null;
  // DOC L0016: Chiude il costrutto aperto alla riga 12.
  }
  // DOC L0017: currentPhase: Restituisce la preparazione quando l’indice è negativo, altrimenti la fase corrente della timeline.
  get currentPhase() { return this.index < 0 ? PREPARATION : this.phases[this.index]; }
  // DOC L0018: nextPhase: Espone la fase successiva per l’anteprima del timer.
  get nextPhase() { return this.phases[this.index + 1]; }
  // DOC L0019: start: Avvia soltanto un motore idle, creando identità e riferimenti temporali della sessione.
  start() {
    // DOC L0020: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di start.
    if (this.state !== 'idle') return;
    // DOC L0021: Esegue il passaggio esplicito «this.id = uid(); this.startedAt = this.wall(); this.last = this.now(); this.state = 'running';» nel flusso: Motore web e segnali Web Audio. Nel contesto di start.
    this.id = uid(); this.startedAt = this.wall(); this.last = this.now(); this.state = 'running';
  // DOC L0022: Chiude il costrutto aperto alla riga 19.
  }
  // DOC L0023: tick: Calcola il tempo realmente trascorso dall’orologio monotono e attraversa tutte le fasi superate, anche se il polling è arrivato in ritardo.
  tick() {
    // DOC L0024: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di tick.
    if (this.state !== 'running') return;
    // DOC L0025: current: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. delta: tempo trascorso dall’ultimo aggiornamento, da distribuire sulle fasi. Nel contesto di tick.
    const current = this.now(); let delta = Math.max(0, current - this.last); this.last = current;
    // DOC L0026: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di tick.
    while (delta >= this.remainingMs && this.state === 'running') {
      // DOC L0027: Esegue il passaggio esplicito «this.consume(this.remainingMs); delta -= this.remainingMs;» nel flusso: Motore web e segnali Web Audio. Nel contesto di tick.
      this.consume(this.remainingMs); delta -= this.remainingMs;
      // DOC L0028: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di tick.
      if (this.index >= 0) {
        // DOC L0029: Esegue il passaggio esplicito «this.completedPhases.add(this.index);» nel flusso: Motore web e segnali Web Audio. Nel contesto di tick.
        this.completedPhases.add(this.index);
        // DOC L0030: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di tick.
        if (this.currentPhase.type === 'WORK') this.completed.add(this.index);
      // DOC L0031: Chiude il costrutto aperto alla riga 28.
      }
      // DOC L0032: Esegue il passaggio esplicito «this.advance();» nel flusso: Motore web e segnali Web Audio. Nel contesto di tick.
      this.advance();
    // DOC L0033: Chiude il costrutto aperto alla riga 26.
    }
    // DOC L0034: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di tick.
    if (this.state === 'running') { this.consume(delta); this.remainingMs -= delta; }
  // DOC L0035: Chiude il costrutto aperto alla riga 23.
  }
  // DOC L0036: consume: Accredita tempo attivo e tempo WORK, escludendo la preparazione.
  consume(ms) {
    // DOC L0037: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di consume.
    if (this.index < 0) return; // The lead-in is outside the planned workout and its statistics.
    // DOC L0038: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di consume.
    this.elapsedMs += ms; if (this.currentPhase.type === 'WORK') this.workMs += ms;
  // DOC L0039: Chiude il costrutto aperto alla riga 36.
  }
  // DOC L0040: advance: Incrementa indice e visita di fase; alla fine produce il risultato senza aggiungere un recupero finale.
  advance() {
    // DOC L0041: Esegue il passaggio esplicito «this.index++; this.phaseRun++;» nel flusso: Motore web e segnali Web Audio. Nel contesto di advance.
    this.index++; this.phaseRun++;
    // DOC L0042: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
    if (this.index >= this.phases.length) this.finish(this.completed.size === this.phases.filter(p => p.type === 'WORK').length ? 'COMPLETED' : 'INTERRUPTED');
    // DOC L0043: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di advance.
    else this.remainingMs = this.phases[this.index].durationMs;
  // DOC L0044: Chiude il costrutto aperto alla riga 40.
  }
  // DOC L0045: pause: Aggiorna prima il tempo trascorso e poi sospende un motore ancora in esecuzione.
  pause() { this.tick(); if (this.state === 'running') { this.state = 'paused'; this.pauses++; } }
  // DOC L0046: resume: Riparte dal tempo residuo, sostituendo il riferimento monotono così la pausa non viene conteggiata.
  resume() { if (this.state === 'paused') { this.last = this.now(); this.state = 'running'; } }
  // DOC L0047: skip: Salta la fase dopo aver aggiornato il timer, senza accreditarne il lavoro non svolto.
  skip() { this.tick(); if (['running', 'paused'].includes(this.state)) this.advance(); }
  // DOC L0048: previous: Ripete la fase precedente creando una visita nuova senza duplicare gli intervalli già completati.
  previous() {
    // DOC L0049: Esegue il passaggio esplicito «this.tick();» nel flusso: Motore web e segnali Web Audio. Nel contesto di previous.
    this.tick();
    // DOC L0050: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di previous.
    if (!['running', 'paused'].includes(this.state)) return;
    // DOC L0051: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di previous.
    if (this.index >= 0) this.index = Math.max(0, this.index - 1);
    // DOC L0052: Esegue il passaggio esplicito «this.phaseRun++; this.remainingMs = this.currentPhase.durationMs; this.last = this.now();» nel flusso: Motore web e segnali Web Audio. Nel contesto di previous.
    this.phaseRun++; this.remainingMs = this.currentPhase.durationMs; this.last = this.now();
  // DOC L0053: Chiude il costrutto aperto alla riga 48.
  }
  // DOC L0054: stop: Interrompe il motore con risultato idempotente; nella classe TimerSignals ferma invece le sorgenti audio attive.
  stop() { this.tick(); if (['running', 'paused'].includes(this.state)) this.finish('INTERRUPTED'); return this.result; }
  // DOC L0055: finish: Congela il risultato una sola volta, con contatori reali e stato COMPLETED o INTERRUPTED.
  finish(status) {
    // DOC L0056: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di finish.
    if (this.result) return this.result;
    // DOC L0057: Esegue il passaggio esplicito «this.state = 'finished'; this.remainingMs = 0;» nel flusso: Motore web e segnali Web Audio. Nel contesto di finish.
    this.state = 'finished'; this.remainingMs = 0;
    // DOC L0058: Esegue il passaggio esplicito «this.result = { id: this.id, mode: modeOf(this.workout), workoutId: this.workout.id, workoutTitle: this.workout.title, startedAt: this.startedAt, endedAt: this.wall(), plannedMs: p» nel flusso: Motore web e segnali Web Audio.
    this.result = { id: this.id, mode: modeOf(this.workout), workoutId: this.workout.id, workoutTitle: this.workout.title, startedAt: this.startedAt, endedAt: this.wall(), plannedMs: plannedMs(this.phases), elapsedMs: Math.round(this.elapsedMs), workMs: Math.round(this.workMs), completedWorkIntervals: this.completed.size, plannedWorkIntervals: this.phases.filter(p => p.type === 'WORK').length, completedPhases: this.completedPhases.size, plannedPhases: this.phases.length, pauses: this.pauses, status };
    // DOC L0059: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di finish.
    return this.result;
  // DOC L0060: Chiude il costrutto aperto alla riga 55.
  }
// DOC L0061: Chiude il costrutto aperto alla riga 11.
}

// DOC L0063: Dichiara il tipo o contenitore di questo modulo: Motore web e segnali Web Audio. I campi e i metodi sono descritti nelle righe successive.
export class TimerSignals {
  // DOC L0064: constructor: Inizializza l’istanza TimerSignals, i suoi riferimenti e lo stato iniziale, senza avviare automaticamente il timer.
  constructor() { this.audio = null; this.buffers = {}; this.sources = new Set(); this.lock = null; this.sound = true; this.vibration = false; this.keepAwake = false; }
  // DOC L0065: unlock: Apre o riprende Web Audio dopo un gesto utente e decodifica i nove WAV locali.
  async unlock() {
    // DOC L0066: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di unlock.
    if (!this.sound) return true;
    // DOC L0067: Esegue il passaggio esplicito «try {» nel flusso: Motore web e segnali Web Audio. Nel contesto di unlock.
    try {
      // DOC L0068: Audio: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di unlock.
      const Audio = globalThis.AudioContext || globalThis.webkitAudioContext;
      // DOC L0069: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di unlock.
      if (!Audio) return false;
      // DOC L0070: Esegue il passaggio esplicito «this.audio ??= new Audio(); await this.audio.resume();» nel flusso: Motore web e segnali Web Audio. Nel contesto di unlock.
      this.audio ??= new Audio(); await this.audio.resume();
      // DOC L0071: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
      if (Object.keys(AUDIO_SOUNDS).some(key => !this.buffers[key])) {
        // DOC L0072: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. loaded: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
        const loaded = await Promise.all(Object.entries(AUDIO_SOUNDS).map(async ([key, file]) => {
          // DOC L0073: response: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
          const response = await fetch(new URL('../audio/' + file, import.meta.url));
          // DOC L0074: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
          if (!response.ok) throw new Error('Suono non disponibile');
          // DOC L0075: Restituisce il valore calcolato o conclude il percorso della funzione.
          return [key, await this.audio.decodeAudioData(await response.arrayBuffer())];
        // DOC L0076: Chiude il costrutto aperto alla riga 72.
        }));
        // DOC L0077: Esegue il passaggio esplicito «this.buffers = Object.fromEntries(loaded);» nel flusso: Motore web e segnali Web Audio. Nel contesto di unlock.
        this.buffers = Object.fromEntries(loaded);
      // DOC L0078: Chiude il costrutto aperto alla riga 71.
      }
      // DOC L0079: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di unlock.
      return this.audio.state === 'running';
    // DOC L0080: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di unlock.
    } catch { return false; }
  // DOC L0081: Chiude il costrutto aperto alla riga 65.
  }
  // DOC L0082: horn: Traduce il secondo residuo nella chiave del campione e delega la riproduzione.
  horn(second, phase = 'PREPARE') {
    // DOC L0083: Prepara o riproduce il segnale audio; il motore del tempo rimane indipendente dalla riproduzione. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. key: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di horn.
    const key = hornSample(second, phase); if (key) this.play(key);
  // DOC L0084: Chiude il costrutto aperto alla riga 82.
  }
  // DOC L0085: play: Riproduce un solo WAV al guadagno previsto, senza accelerarlo né sovrapporlo al precedente.
  play(key) {
    // DOC L0086: buffer: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di play.
    const buffer = this.buffers[key];
    // DOC L0087: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di play.
    if (this.sound && buffer && this.audio?.state === 'running') {
      // DOC L0088: Esegue il passaggio esplicito «this.stop();» nel flusso: Motore web e segnali Web Audio. Nel contesto di play.
      this.stop();
      // DOC L0089: Prepara o riproduce il segnale audio; il motore del tempo rimane indipendente dalla riproduzione. source: sorgente audio o testo sorgente: il contesto della funzione ne specifica l’uso. Nel contesto di play.
      const source = this.audio.createBufferSource(), gain = this.audio.createGain();
      // DOC L0090: Esegue il passaggio esplicito «source.buffer = buffer;» nel flusso: Motore web e segnali Web Audio. Nel contesto di play.
      source.buffer = buffer;
      // DOC L0091: Esegue il passaggio esplicito «source.playbackRate.value = 1;» nel flusso: Motore web e segnali Web Audio. Nel contesto di play.
      source.playbackRate.value = 1;
      // DOC L0092: Esegue il passaggio esplicito «gain.gain.value = cueVolume(key);» nel flusso: Motore web e segnali Web Audio. Nel contesto di play.
      gain.gain.value = cueVolume(key);
      // DOC L0093: Esegue il passaggio esplicito «source.connect(gain).connect(this.audio.destination);» nel flusso: Motore web e segnali Web Audio. Nel contesto di play.
      source.connect(gain).connect(this.audio.destination);
      // DOC L0094: Esegue il passaggio esplicito «source.onended = () => { this.sources.delete(source); source.disconnect(); gain.disconnect(); };» nel flusso: Motore web e segnali Web Audio.
      source.onended = () => { this.sources.delete(source); source.disconnect(); gain.disconnect(); };
      // DOC L0095: Esegue il passaggio esplicito «this.sources.add(source);» nel flusso: Motore web e segnali Web Audio. Nel contesto di play.
      this.sources.add(source);
      // DOC L0096: Esegue il passaggio esplicito «source.start();» nel flusso: Motore web e segnali Web Audio. Nel contesto di play.
      source.start();
    // DOC L0097: Chiude il costrutto aperto alla riga 87.
    }
    // DOC L0098: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di play.
    if (this.vibration) navigator.vibrate?.(100);
  // DOC L0099: Chiude il costrutto aperto alla riga 85.
  }
  // DOC L0100: stop: Interrompe il motore con risultato idempotente; nella classe TimerSignals ferma invece le sorgenti audio attive.
  stop() { for (const source of this.sources) source.stop(); this.sources.clear(); }
  // DOC L0101: wake: Richiede lo screen wake lock soltanto con pagina visibile e preferenza attiva.
  async wake() {
    // DOC L0102: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di wake.
    if (!this.keepAwake || this.lock || document.visibilityState !== 'visible') return;
    // DOC L0103: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di wake.
    if (!navigator.wakeLock) throw new Error('Schermo acceso non supportato in questo browser.');
    // DOC L0104: Esegue il passaggio esplicito «this.lock = await navigator.wakeLock.request('screen');» nel flusso: Motore web e segnali Web Audio. Nel contesto di wake.
    this.lock = await navigator.wakeLock.request('screen');
    // DOC L0105: Registra il gestore dell’evento indicato; il corpo viene eseguito quando l’evento si verifica.
    this.lock.addEventListener('release', () => { this.lock = null; });
  // DOC L0106: Chiude il costrutto aperto alla riga 101.
  }
  // DOC L0107: release: Rilascia lo screen wake lock quando non serve più.
  async release() { if (this.lock) { await this.lock.release(); this.lock = null; } }
// DOC L0108: Chiude il costrutto aperto alla riga 63.
}
