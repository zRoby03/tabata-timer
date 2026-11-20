// DOC L0001: STORAGE_KEY: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
export const STORAGE_KEY = 'tabata:v1:data';
// DOC L0002: Dichiara il tipo o contenitore di questo modulo: Repository browser con modalità temporanea e persistente. I campi e i metodi sono descritti nelle righe successive.
export class LocalRepository {
  // DOC L0003: constructor: Inizializza un archivio in memoria e conserva l’accesso opzionale a localStorage e al segnalatore degli errori.
  constructor(storage, onWarning = () => {}) { this.storage = storage; this.onWarning = onWarning; this.persistent = false; this.data = { workouts: [], sessions: [] }; }
  // DOC L0004: load: Legge e valida l’archivio persistente soltanto se era stato abilitato, preservando i contenuti corrotti per il recupero.
  load(validate) {
    // DOC L0005: Esegue il passaggio esplicito «try {» nel flusso: Repository browser con modalità temporanea e persistente. Nel contesto di load.
    try {
      // DOC L0006: Legge il valore locale e applica i controlli/fallback riportati nell’espressione. raw: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di load.
      const raw = this.storage?.getItem(STORAGE_KEY);
      // DOC L0007: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di load.
      if (raw) { const valid = validate(raw); this.data = { workouts: valid.workouts, sessions: valid.sessions }; this.persistent = true; }
    // DOC L0008: Esegue il passaggio esplicito «} catch (e) { this.onWarning(`Archivio non disponibile: ${e.message}. Uso la memoria temporanea; l’archivio precedente è stato conservato.`); }» nel flusso: Repository browser con modalità temporanea e persistente. Nel contesto di load.
    } catch (e) { this.onWarning(`Archivio non disponibile: ${e.message}. Uso la memoria temporanea; l’archivio precedente è stato conservato.`); }
    // DOC L0009: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di load.
    return this.data;
  // DOC L0010: Chiude il costrutto aperto alla riga 4.
  }
  // DOC L0011: save: Scrive una copia coerente quando la persistenza è attiva e segnala quota o storage negato.
  save() {
    // DOC L0012: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di save.
    if (!this.persistent) return true;
    // DOC L0013: Scrive la preferenza o la copia locale prevista, senza inviarla a un server. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di save.
    try { this.storage.setItem(STORAGE_KEY, JSON.stringify({ schemaVersion: 1, exportedAt: new Date().toISOString(), ...this.data })); return true; }
    // DOC L0014: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di save.
    catch { this.onWarning('Salvataggio non riuscito. I dati restano in memoria: esporta una copia JSON prima di chiudere.'); return false; }
  // DOC L0015: Chiude il costrutto aperto alla riga 11.
  }
  // DOC L0016: enable: Abilita la persistenza dopo una scelta esplicita dell’utente.
  enable() { const previous = this.persistent; this.persistent = true; if (!this.save()) { this.persistent = previous; return false; } return true; }
  // DOC L0017: disable: Rimuove la copia persistente soltanto su richiesta, mantenendo il risultato dell’operazione controllabile.
  disable() {
    // DOC L0018: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di disable.
    try { this.storage?.removeItem(STORAGE_KEY); this.persistent = false; return true; }
    // DOC L0019: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di disable.
    catch { this.onWarning('Impossibile rimuovere il salvataggio precedente. Usa le impostazioni del browser prima di passare alla modalità temporanea.'); return false; }
  // DOC L0020: Chiude il costrutto aperto alla riga 17.
  }
  // DOC L0021: putWorkout: Inserisce o sostituisce un programma per ID nell’archivio in memoria.
  putWorkout(workout) { const i = this.data.workouts.findIndex(w => w.id === workout.id); if (i < 0) this.data.workouts.push(workout); else this.data.workouts[i] = workout; }
  // DOC L0022: upsertWorkout: Valida il programma, lo inserisce o aggiorna e tenta il salvataggio previsto.
  upsertWorkout(workout) { this.putWorkout(workout); return this.save(); }
  // DOC L0023: saveWorkoutLocally: Attiva la persistenza per il salvataggio esplicito di un programma e preserva il modello anche se la scrittura fallisce.
  saveWorkoutLocally(workout) { this.putWorkout(workout); return this.enable(); }
  // DOC L0024: addSession: Aggiunge una sessione soltanto se il suo ID non è già presente.
  addSession(session) { if (!this.data.sessions.some(s => s.id === session.id)) this.data.sessions.push(session); return this.save(); }
// DOC L0025: Chiude il costrutto aperto alla riga 2.
}
