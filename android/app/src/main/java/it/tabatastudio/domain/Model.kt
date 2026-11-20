// DOC L0001: Assegna il file al namespace it.tabatastudio.domain, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.domain

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import java.util.UUID. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import java.util.UUID

// DOC L0005: uid: Genera un UUID per distinguere programmi, elementi e sessioni senza collisioni volontarie.
fun uid(): String = UUID.randomUUID().toString()
// DOC L0006: Dichiara il tipo o contenitore di questo modulo: Modello Kotlin equivalente al modello web. I campi e i metodi sono descritti nelle righe successive.
data class Exercise(val id: String = uid(), val title: String = "Nuovo esercizio", val repetitions: Int = 1, val workSec: Int = 30, val restBetweenRepsSec: Int = 10, val restAfterExerciseSec: Int = 15)
// DOC L0007: Dichiara il tipo o contenitore di questo modulo: Modello Kotlin equivalente al modello web. I campi e i metodi sono descritti nelle righe successive.
data class StudyActivity(val id: String = uid(), val title: String = "Concentrazione", val concentrationSec: Int = 1500, val sessions: Int = 4, val pauseSec: Int = 300)

// DOC L0009: Dichiara il tipo o contenitore di questo modulo: Modello Kotlin equivalente al modello web. I campi e i metodi sono descritti nelle righe successive.
data class Block(val id: String = uid(), val title: String = "Nuovo blocco", val cycles: Int = 1, val restBetweenCyclesSec: Int = 60, val restAfterBlockSec: Int = 60, val exercises: List<Exercise> = listOf(Exercise()), val restBetweenExercisesSec: Int? = null)
// DOC L0010: Dichiara il tipo o contenitore di questo modulo: Modello Kotlin equivalente al modello web. I campi e i metodi sono descritti nelle righe successive.
data class Workout(val schemaVersion: Int = 1, val id: String = uid(), val title: String = "Il mio allenamento", val description: String = "", val color: String = "#9153b7", val warmupSec: Int = 0, val cooldownSec: Int = 0, val blocks: List<Block> = listOf(Block()), val mode: String = "WORKOUT", val longBreakSec: Int = 0, val longBreakEvery: Int = 4, val activities: List<StudyActivity>? = null, val restBetweenActivitiesSec: Int = 300)
// DOC L0011: Dichiara il tipo o contenitore di questo modulo: Modello Kotlin equivalente al modello web. I campi e i metodi sono descritti nelle righe successive.
data class Phase(val type: String, val blockId: String?, val exerciseId: String?, val durationMs: Long, val label: String)
// DOC L0012: Dichiara il tipo o contenitore di questo modulo: Modello Kotlin equivalente al modello web. I campi e i metodi sono descritti nelle righe successive.
data class Session(val id: String, val workoutId: String, val workoutTitle: String = "", val startedAt: String, val endedAt: String, val plannedMs: Long, val elapsedMs: Long, val workMs: Long, val completedWorkIntervals: Int, val plannedWorkIntervals: Int, val pauses: Int, val status: String, val completedPhases: Int? = null, val plannedPhases: Int? = null, val mode: String = "WORKOUT")

// DOC L0014: validateWorkout: Controlla schema, modalità, durate, titoli, quantità e unicità degli ID prima di compilare o salvare il programma.
fun validateWorkout(w: Workout) {
    // DOC L0015: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di validateWorkout.
    require(w.schemaVersion == 1) { "Versione allenamento non supportata" }
    // DOC L0016: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di validateWorkout.
    require(w.mode in listOf("WORKOUT", "STUDIO")) { "Modalità non valida" }
    // DOC L0017: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di validateWorkout.
    require(w.longBreakSec in 0..3600 && w.longBreakEvery in 2..100) { "Pausa lunga non valida" }
    // DOC L0018: ids: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di validateWorkout.
    val ids = mutableSetOf<String>()
    // DOC L0019: unique: Valida l’ID e lo inserisce nell’insieme locale per rilevare duplicati nello stesso programma.
    fun unique(id: String) { require(id.matches(Regex("[A-Za-z0-9_-]{1,128}")) && ids.add(id)) { "ID non valido o duplicato" } }
    // DOC L0020: title: Rifiuta titoli vuoti o oltre il limite del modello.
    fun title(value: String) { require(value.isNotBlank() && value.length <= 120) { "Titolo non valido" } }
    // DOC L0021: sec: Valida una durata e, in Studio, rifiuta valori non esprimibili in minuti interi.
    fun sec(value: Int, min: Int = 0) { require(value in min..3600 && (!w.studio || value % 60 == 0)) { "Durata fuori limite: Studio usa minuti interi (massimo 60)" } }
    // DOC L0022: Esegue il passaggio esplicito «sec(w.longBreakSec)» nel flusso: Modello Kotlin equivalente al modello web. Nel contesto di sec.
    sec(w.longBreakSec)
    // DOC L0023: Esegue il passaggio esplicito «unique(w.id); title(w.title)» nel flusso: Modello Kotlin equivalente al modello web. Nel contesto di sec.
    unique(w.id); title(w.title)
    // DOC L0024: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di sec.
    require(w.description.length <= 2000 && w.color.matches(Regex("#[0-9a-fA-F]{6}"))) { "Descrizione o colore non valido" }
    // DOC L0025: Esegue il passaggio esplicito «sec(w.warmupSec); sec(w.cooldownSec)» nel flusso: Modello Kotlin equivalente al modello web. Nel contesto di sec.
    sec(w.warmupSec); sec(w.cooldownSec)
    // DOC L0026: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di sec.
    if (w.studio && w.activities != null) {
        // DOC L0027: Esegue il passaggio esplicito «sec(w.restBetweenActivitiesSec)» nel flusso: Modello Kotlin equivalente al modello web. Nel contesto di sec.
        sec(w.restBetweenActivitiesSec)
        // DOC L0028: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di sec.
        require(w.activities.size in 1..5000) { "Serve almeno un’attività (massimo 5000)" }
        // DOC L0029: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di sec.
        w.activities.forEach { a -> unique(a.id); title(a.title); sec(a.concentrationSec, 60); require(a.sessions in 1..100); sec(a.pauseSec) }
        // DOC L0030: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di sec.
        return
    // DOC L0031: Chiude il costrutto aperto alla riga 26.
    }
    // DOC L0032: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di sec.
    require(w.blocks.size in 1..50) { "Servono da 1 a 50 blocchi" }
    // DOC L0033: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di sec.
    w.blocks.forEach { b ->
        // DOC L0034: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di sec.
        unique(b.id); title(b.title); b.restBetweenExercisesSec?.let { sec(it) }; require(b.cycles in 1..100); sec(b.restBetweenCyclesSec); sec(b.restAfterBlockSec)
        // DOC L0035: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di sec.
        require(b.exercises.size in 1..100) { "Servono da 1 a 100 esercizi" }
        // DOC L0036: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di sec.
        b.exercises.forEach { e -> unique(e.id); title(e.title); require(e.repetitions in 1..100); sec(e.workSec, 1); sec(e.restBetweenRepsSec); sec(e.restAfterExerciseSec) }
    // DOC L0037: Chiude il costrutto aperto alla riga 33.
    }
// DOC L0038: Chiude il costrutto aperto alla riga 14.
}
// DOC L0039: duplicateWorkout: Duplica il programma normalizzato assegnando ID nuovi anche ai suoi elementi.
fun duplicateWorkout(w: Workout): Workout {
    // DOC L0040: copy: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di duplicateWorkout.
    val copy = normalizeWorkout(w).copy(id = uid(), title = w.title.take(110) + " (copia)")
    // DOC L0041: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Restituisce il valore calcolato o conclude il percorso della funzione. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di duplicateWorkout.
    return if (copy.studio) copy.copy(activities = copy.activities!!.map { it.copy(id = uid()) }) else copy.copy(blocks = copy.blocks.map { b -> b.copy(id = uid(), exercises = b.exercises.map { it.copy(id = uid()) }) })
// DOC L0042: Chiude il costrutto aperto alla riga 39.
}
// DOC L0043: presets: Costruisce programmi pronti distinti per modalità con durate effettive espresse internamente in secondi.
fun presets(mode: String = "WORKOUT"): List<Workout> = if (mode == "STUDIO") listOf(25 to 5, 50 to 10).map { (focus, rest) -> newWorkout("STUDIO").copy(title = "Studio $focus/$rest", activities = listOf(StudyActivity(concentrationSec = focus * 60, pauseSec = rest * 60))) } else listOf(
    // DOC L0044: Esegue il passaggio esplicito «Workout(title = "Tabata classico", description = "Otto intervalli di lavoro, senza recupero finale.", blocks = listOf(Block(title = "Tabata", restBetweenCyclesSec = 0, restAfterBlo» nel flusso: Modello Kotlin equivalente al modello web. Nel contesto di presets.
    Workout(title = "Tabata classico", description = "Otto intervalli di lavoro, senza recupero finale.", blocks = listOf(Block(title = "Tabata", restBetweenCyclesSec = 0, restAfterBlockSec = 0, exercises = listOf(Exercise(title = "Esercizio libero", repetitions = 8, workSec = 20, restAfterExerciseSec = 0))))),
    // DOC L0045: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di presets.
    Workout(title = "Circuito completo", description = "Tre movimenti, due giri. Personalizza il tuo ritmo.", color = "#b54079", warmupSec = 0, cooldownSec = 60, blocks = listOf(Block(title = "Circuito", cycles = 2, exercises = listOf("Squat", "Push-up", "Plank").map { Exercise(title = it) })))
// DOC L0046: Chiude un blocco, una chiamata o un valore composto; non aggiunge una nuova operazione.
)
// DOC L0047: formatTime: Converte millisecondi in m:ss o h:mm:ss, arrotondando il residuo verso l’alto e impedendo valori negativi.
fun formatTime(ms: Long): String {
    // DOC L0048: s: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di formatTime.
    val s = (ms.coerceAtLeast(0) + 999) / 1000
    // DOC L0049: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di formatTime.
    return if (s >= 3600) "%d:%02d:%02d".format(s / 3600, s / 60 % 60, s % 60) else "%d:%02d".format(s / 60, s % 60)
// DOC L0050: Chiude il costrutto aperto alla riga 47.
}
