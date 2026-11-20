// DOC L0001: Assegna il file al namespace it.tabatastudio.domain, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.domain

// DOC L0003: compileTimeline: Espande il programma in fasi cronologiche, saltando durate zero e pause dopo l’ultimo elemento.
fun compileTimeline(original: Workout): List<Phase> {
    // DOC L0004: Esegue il passaggio esplicito «validateWorkout(original)» nel flusso: Compilazione Kotlin della stessa sequenza di fasi del web. Nel contesto di compileTimeline.
    validateWorkout(original)
    // DOC L0005: w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di compileTimeline.
    val w = normalizeWorkout(original)
    // DOC L0006: Esegue il passaggio esplicito «validateWorkout(w)» nel flusso: Compilazione Kotlin della stessa sequenza di fasi del web. Nel contesto di compileTimeline.
    validateWorkout(w)
    // DOC L0007: phases: sequenza cronologica compilata dal programma. Nel contesto di compileTimeline.
    val phases = mutableListOf<Phase>()
    // DOC L0008: total: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di compileTimeline.
    var total = 0L
    // DOC L0009: add: Aggiunge una fase non vuota, converte secondi in millisecondi e verifica i limiti di quantità e durata complessiva.
    fun add(type: String, sec: Int, label: String, b: String? = null, e: String? = null) {
        // DOC L0010: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di add.
        if (sec == 0) return
        // DOC L0011: Esegue il passaggio esplicito «total += sec * 1000L» nel flusso: Compilazione Kotlin della stessa sequenza di fasi del web. Nel contesto di add.
        total += sec * 1000L
        // DOC L0012: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di add.
        require(phases.size < 10000 && total <= 86400000L) { "Massimo 10.000 fasi e 24 ore" }
        // DOC L0013: Esegue il passaggio esplicito «phases.add(Phase(type, b, e, sec * 1000L, label))» nel flusso: Compilazione Kotlin della stessa sequenza di fasi del web. Nel contesto di add.
        phases.add(Phase(type, b, e, sec * 1000L, label))
    // DOC L0014: Chiude il costrutto aperto alla riga 9.
    }
    // DOC L0015: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di add.
    if (w.studio) {
        // DOC L0016: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di add.
        w.activities!!.forEachIndexed { i, a ->
            // DOC L0017: Esegue il passaggio esplicito «repeat(a.sessions) { session ->» nel flusso: Compilazione Kotlin della stessa sequenza di fasi del web. Nel contesto di add.
            repeat(a.sessions) { session ->
                // DOC L0018: Esegue il passaggio esplicito «add("WORK", a.concentrationSec, "Attività ${i + 1} · ${a.title} · sessione ${session + 1}/${a.sessions}", e = a.id)» nel flusso: Compilazione Kotlin della stessa sequenza di fasi del web. Nel contesto di add.
                add("WORK", a.concentrationSec, "Attività ${i + 1} · ${a.title} · sessione ${session + 1}/${a.sessions}", e = a.id)
                // DOC L0019: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di add.
                if (session < a.sessions - 1) add("REST_REP", a.pauseSec, "Pausa tra sessioni", e = a.id)
            // DOC L0020: Chiude il costrutto aperto alla riga 17.
            }
            // DOC L0021: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di add.
            if (i < w.activities.lastIndex) add("REST_ACTIVITY", w.restBetweenActivitiesSec, "Pausa tra attività", e = a.id)
        // DOC L0022: Chiude il costrutto aperto alla riga 16.
        }
        // DOC L0023: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di add.
        return phases
    // DOC L0024: Chiude il costrutto aperto alla riga 15.
    }
    // DOC L0025: Esegue il passaggio esplicito «add("WARMUP", w.warmupSec, "Riscaldamento")» nel flusso: Compilazione Kotlin della stessa sequenza di fasi del web. Nel contesto di add.
    add("WARMUP", w.warmupSec, "Riscaldamento")
    // DOC L0026: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di add.
    w.blocks.forEachIndexed { bi, b ->
        // DOC L0027: Esegue il passaggio esplicito «repeat(b.cycles) { c ->» nel flusso: Compilazione Kotlin della stessa sequenza di fasi del web. Nel contesto di add.
        repeat(b.cycles) { c ->
            // DOC L0028: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di add.
            b.exercises.forEachIndexed { ei, e ->
                // DOC L0029: Esegue il passaggio esplicito «repeat(e.repetitions) { r ->» nel flusso: Compilazione Kotlin della stessa sequenza di fasi del web. Nel contesto di add.
                repeat(e.repetitions) { r ->
                    // DOC L0030: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di add.
                    add("WORK", e.workSec, if (w.studio) "${e.title} · sessione ${r + 1}/${e.repetitions}" else "${e.title} · giro ${c + 1}/${b.cycles} · ${r + 1}/${e.repetitions}", b.id, e.id)
                    // DOC L0031: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di add.
                    if (r < e.repetitions - 1) add("REST_REP", e.restBetweenRepsSec, if (w.studio) "Pausa" else "Recupero ripetizioni", b.id, e.id)
                // DOC L0032: Chiude il costrutto aperto alla riga 29.
                }
                // DOC L0033: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di add.
                if (ei < b.exercises.lastIndex) add("REST_EXERCISE", b.restBetweenExercisesSec!!, "Recupero tra esercizi", b.id, e.id)
            // DOC L0034: Chiude il costrutto aperto alla riga 28.
            }
            // DOC L0035: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di add.
            if (c < b.cycles - 1) add("REST_CYCLE", b.restBetweenCyclesSec, if (w.studio) "Pausa tra serie" else "Recupero cicli", b.id)
        // DOC L0036: Chiude il costrutto aperto alla riga 27.
        }
        // DOC L0037: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di add.
        if (bi < w.blocks.lastIndex) add("REST_BLOCK", b.restAfterBlockSec, if (w.studio) "Pausa tra blocchi" else "Recupero blocchi", b.id)
    // DOC L0038: Chiude il costrutto aperto alla riga 26.
    }
    // DOC L0039: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di add.
    add("COOLDOWN", w.cooldownSec, if (w.studio) "Pausa finale" else "Defaticamento")
    // DOC L0040: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di add.
    return phases
// DOC L0041: Chiude il costrutto aperto alla riga 3.
}
// DOC L0042: plannedMs: Somma le durate della timeline; i 20 secondi di preparazione restano fuori da questo totale.
fun plannedMs(phases: List<Phase>): Long = phases.sumOf { it.durationMs }
