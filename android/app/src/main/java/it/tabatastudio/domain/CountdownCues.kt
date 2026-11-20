// DOC L0001: Assegna il file al namespace it.tabatastudio.domain, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.domain

// DOC L0003: Dichiara il tipo o contenitore di questo modulo: Rilevazione Kotlin del 3, 2, 1. I campi e i metodi sono descritti nelle righe successive.
class CountdownCues {
    // DOC L0004: engine: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private var engine: TimerEngine? = null
    // DOC L0005: visit: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private var visit = -1
    // DOC L0006: played: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private val played = mutableSetOf<Int>()

    // DOC L0008: read: Rileva una sola volta i secondi 3, 2, 1 della visita attiva, senza recuperare note ormai trascorse.
    fun read(current: TimerEngine): Int? {
        // DOC L0009: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di read.
        if (engine !== current || visit != current.phaseRun) {
            // DOC L0010: Esegue il passaggio esplicito «engine = current; visit = current.phaseRun; played.clear()» nel flusso: Rilevazione Kotlin del 3, 2, 1. Nel contesto di read.
            engine = current; visit = current.phaseRun; played.clear()
        // DOC L0011: Chiude il costrutto aperto alla riga 9.
        }
        // DOC L0012: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
        if (current.state != "running") return null
        // DOC L0013: second: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di read.
        val second = ((current.remainingMs + 999) / 1000).toInt()
        // DOC L0014: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
        if (second !in 1..3 || !played.add(second)) return null
        // DOC L0015: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di read.
        return second
    // DOC L0016: Chiude il costrutto aperto alla riga 8.
    }
// DOC L0017: Chiude il costrutto aperto alla riga 3.
}

// DOC L0019: hornSample: Associa 3, 2 o 1 alla nota bassa del lavoro o alla nota alta delle altre fasi.
fun hornSample(second: Int, phase: String = "PREPARE"): String? {
    // DOC L0020: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di hornSample.
    if (second !in 1..3) return null
    // DOC L0021: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. kind: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di hornSample.
    val kind = if (phase == "WORK") "work" else "rest"
    // DOC L0022: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di hornSample.
    return "${kind}_$second"
// DOC L0023: Chiude il costrutto aperto alla riga 19.
}
