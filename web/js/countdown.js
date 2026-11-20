// Read only the current second: delayed polling never produces a burst of old cues.
// DOC L0002: Dichiara il tipo o contenitore di questo modulo: Rilevazione delle tre note del conto alla rovescia. I campi e i metodi sono descritti nelle righe successive.
export class CountdownCues {
  // DOC L0003: constructor: Inizializza l’istanza CountdownCues, i suoi riferimenti e lo stato iniziale, senza avviare automaticamente il timer.
  constructor() { this.engine = null; this.visit = -1; this.played = new Set(); }
  // DOC L0004: read: Rileva una sola volta i secondi 3, 2, 1 della visita attiva, senza recuperare note ormai trascorse.
  read(engine) {
    // DOC L0005: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
    if (!engine) return null;
    // DOC L0006: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di read.
    if (engine !== this.engine || engine.phaseRun !== this.visit) {
      // DOC L0007: Esegue il passaggio esplicito «this.engine = engine; this.visit = engine.phaseRun; this.played.clear();» nel flusso: Rilevazione delle tre note del conto alla rovescia. Nel contesto di read.
      this.engine = engine; this.visit = engine.phaseRun; this.played.clear();
    // DOC L0008: Chiude il costrutto aperto alla riga 6.
    }
    // DOC L0009: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
    if (engine.state !== 'running') return null;
    // DOC L0010: second: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di read.
    const second = Math.ceil(engine.remainingMs / 1000);
    // DOC L0011: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
    if (second < 1 || second > 3 || this.played.has(second)) return null;
    // DOC L0012: Esegue il passaggio esplicito «this.played.add(second);» nel flusso: Rilevazione delle tre note del conto alla rovescia. Nel contesto di read.
    this.played.add(second);
    // DOC L0013: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
    return second;
  // DOC L0014: Chiude il costrutto aperto alla riga 4.
  }
// DOC L0015: Chiude il costrutto aperto alla riga 2.
}

// One short beep on each of 3, 2, 1; silence separates the cues.
// DOC L0018: COUNTDOWN_SOUNDS: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
export const COUNTDOWN_SOUNDS = Object.fromEntries(['work', 'rest'].flatMap(kind =>
  // DOC L0019: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
  [3, 2, 1].map(second => [`${kind}-${second}`, `countdown-${kind}-${second}.wav`])
// DOC L0020: Chiude un blocco, una chiamata o un valore composto; non aggiunge una nuova operazione.
));
// DOC L0021: hornSample: Associa 3, 2 o 1 alla nota bassa del lavoro o alla nota alta delle altre fasi.
export function hornSample(second, phase = 'PREPARE') {
  // DOC L0022: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di hornSample.
  if (![3, 2, 1].includes(second)) return null;
  // DOC L0023: kind: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di hornSample.
  const kind = phase === 'WORK' ? 'work' : 'rest';
  // DOC L0024: Restituisce il valore calcolato o conclude il percorso della funzione. Genera o aggiorna HTML della UI; i dati utente devono passare attraverso l’escaping previsto dal modulo. Nel contesto di hornSample.
  return `${kind}-${second}`;
// DOC L0025: Chiude il costrutto aperto alla riga 21.
}
