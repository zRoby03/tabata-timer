// DOC L0001: TRANSITION_SOUNDS: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
export const TRANSITION_SOUNDS = { whistle: 'work-start.wav', sigh: 'rest-start.wav', finish: 'workout-finish.wav' };

// Whistle PCM is pre-amplified by 1.2 so SoundPool and Web Audio share the same safe 120% level.
// DOC L0004: cueVolume: Seleziona il guadagno: fischio preamplificato con playback 1, sospiro 0,85 e altri segnali 0,65.
export const cueVolume = key => key === 'whistle' ? 1 : key === 'sigh' ? 0.85 : 0.65;

// Observe the current phase only; never replay a backlog of phase-start sounds.
// DOC L0007: Dichiara il tipo o contenitore di questo modulo: Segnali di cambio fase e relativi guadagni. I campi e i metodi sono descritti nelle righe successive.
export class TransitionCues {
  // DOC L0008: constructor: Inizializza l’istanza TransitionCues, i suoi riferimenti e lo stato iniziale, senza avviare automaticamente il timer.
  constructor() { this.engine = null; this.visit = -1; this.finished = false; }
  // DOC L0009: read: Seleziona il segnale della nuova visita o della fine automatica; non ripete il segnale alla ripresa da pausa.
  read(engine) {
    // DOC L0010: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
    if (!engine) return null;
    // DOC L0011: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di read.
    if (engine !== this.engine) { this.engine = engine; this.visit = -1; this.finished = false; }
    // DOC L0012: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di read.
    if (engine.state === 'finished') {
      // DOC L0013: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
      if (engine.index >= engine.phases.length && !this.finished) { this.finished = true; return 'finish'; }
      // DOC L0014: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
      return null;
    // DOC L0015: Chiude il costrutto aperto alla riga 12.
    }
    // DOC L0016: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
    if (engine.state !== 'running' || engine.phaseRun === this.visit) return null;
    // DOC L0017: Esegue il passaggio esplicito «this.visit = engine.phaseRun;» nel flusso: Segnali di cambio fase e relativi guadagni. Nel contesto di read.
    this.visit = engine.phaseRun;
    // DOC L0018: phase: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di read.
    const phase = engine.currentPhase;
    // DOC L0019: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
    if (phase?.type === 'COOLDOWN' && !this.finished) { this.finished = true; return 'finish'; }
    // DOC L0020: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
    if (!phase || phase.durationMs - engine.remainingMs > 500) return null;
    // DOC L0021: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
    if (phase.type === 'WORK') return 'whistle';
    // DOC L0022: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
    return phase.type.startsWith('REST_') ? 'sigh' : null;
  // DOC L0023: Chiude il costrutto aperto alla riga 9.
  }
// DOC L0024: Chiude il costrutto aperto alla riga 7.
}
