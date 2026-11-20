// DOC L0001: Assegna il file al namespace it.tabatastudio.domain, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.domain

// Whistle PCM already contains the 1.2 boost; SoundPool accepts gains only up to 1.
// DOC L0004: cueVolume: Seleziona il guadagno: fischio preamplificato con playback 1, sospiro 0,85 e altri segnali 0,65.
fun cueVolume(key: String): Float = if (key == "whistle") 1f else if (key == "sigh") 0.85f else 0.65f

// DOC L0006: Dichiara il tipo o contenitore di questo modulo: Segnali di cambio fase equivalenti a quelli web. I campi e i metodi sono descritti nelle righe successive.
class TransitionCues {
    // DOC L0007: engine: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di cueVolume.
    private var engine: TimerEngine? = null
    // DOC L0008: visit: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di cueVolume.
    private var visit = -1
    // DOC L0009: finished: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di cueVolume.
    private var finished = false
    // DOC L0010: read: Seleziona il segnale della nuova visita o della fine automatica; non ripete il segnale alla ripresa da pausa.
    fun read(current: TimerEngine): String? {
        // DOC L0011: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di read.
        if (engine !== current) { engine = current; visit = -1; finished = false }
        // DOC L0012: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di read.
        if (current.state == "finished") {
            // DOC L0013: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
            if (current.index >= current.phases.size && !finished) { finished = true; return "finish" }
            // DOC L0014: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
            return null
        // DOC L0015: Chiude il costrutto aperto alla riga 12.
        }
        // DOC L0016: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
        if (current.state != "running" || current.phaseRun == visit) return null
        // DOC L0017: Esegue il passaggio esplicito «visit = current.phaseRun» nel flusso: Segnali di cambio fase equivalenti a quelli web. Nel contesto di read.
        visit = current.phaseRun
        // DOC L0018: phase: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
        val phase = current.currentPhase ?: return null
        // DOC L0019: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
        if (phase.type == "COOLDOWN" && !finished) { finished = true; return "finish" }
        // DOC L0020: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
        if (phase.durationMs - current.remainingMs > 500) return null
        // DOC L0021: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
        if (phase.type == "WORK") return "whistle"
        // DOC L0022: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
        return if (phase.type.startsWith("REST_")) "sigh" else null
    // DOC L0023: Chiude il costrutto aperto alla riga 10.
    }
// DOC L0024: Chiude il costrutto aperto alla riga 6.
}
