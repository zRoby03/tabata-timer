// DOC L0001: Assegna il file al namespace it.tabatastudio.domain, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.domain

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import java.time.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import java.time.*
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import java.time.temporal.TemporalAdjusters. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import java.time.temporal.TemporalAdjusters

// DOC L0006: completion: Calcola la percentuale di intervalli di lavoro completati rispetto a quelli pianificati.
fun completion(s: Session): Double = s.completedWorkIntervals * 100.0 / s.plannedWorkIntervals
// DOC L0007: Dichiara il tipo o contenitore di questo modulo: Statistiche Kotlin equivalenti a quelle web. I campi e i metodi sono descritti nelle righe successive.
data class Week(val date: String, val count: Int, val workMs: Long, val completion: Double)
// DOC L0008: Dichiara il tipo o contenitore di questo modulo: Statistiche Kotlin equivalenti a quelle web. I campi e i metodi sono descritti nelle righe successive.
data class Statistics(val sessions: List<Session>, val weeks: List<Week>) {
    // DOC L0009: workMs: tempo realmente svolto nelle sole fasi WORK. Nel contesto di completion.
    val workMs get() = sessions.sumOf { it.workMs }
    // DOC L0010: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. completion: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di completion.
    val completion get() = if (sessions.isEmpty()) 0.0 else sessions.map(::completion).average()
// DOC L0011: Chiude il costrutto aperto alla riga 8.
}
// DOC L0012: aggregate: Filtra le sessioni per periodo e programma e calcola totali, media di completamento e settimane UTC.
fun aggregate(sessions: List<Session>, days: Int, workoutId: String = "", now: Instant = Instant.now()): Statistics {
    // DOC L0013: start: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di aggregate.
    val start = now.minusSeconds(days * 86400L)
    // DOC L0014: filtered: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. d: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di aggregate.
    val filtered = sessions.filter { val d = Instant.parse(it.startedAt); d >= start && d <= now && (workoutId.isEmpty() || it.workoutId == workoutId) }.sortedBy { it.startedAt }
    // DOC L0015: monday: Determina il lunedì della settimana UTC per un raggruppamento uguale sui due client.
    fun monday(instant: Instant) = instant.atZone(ZoneOffset.UTC).toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    // DOC L0016: grouped: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di monday.
    val grouped = filtered.groupBy { monday(Instant.parse(it.startedAt)) }
    // DOC L0017: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. weeks: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di monday.
    val weeks = generateSequence(monday(start)) { it.plusWeeks(1) }.takeWhile { it <= monday(now) }.map { d ->
        // DOC L0018: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. list: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di monday.
        val list = grouped[d].orEmpty(); Week(d.toString(), list.size, list.sumOf { it.workMs }, if (list.isEmpty()) 0.0 else list.map(::completion).average())
    // DOC L0019: Esegue il passaggio esplicito «}.toList()» nel flusso: Statistiche Kotlin equivalenti a quelle web. Nel contesto di monday.
    }.toList()
    // DOC L0020: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di monday.
    return Statistics(filtered, weeks)
// DOC L0021: Chiude il costrutto aperto alla riga 12.
}
