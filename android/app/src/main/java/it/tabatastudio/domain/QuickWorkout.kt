// DOC L0001: Assegna il file al namespace it.tabatastudio.domain, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.domain

// DOC L0003: Dichiara il tipo o contenitore di questo modulo: Preset e modello iniziale del timer rapido Android. I campi e i metodi sono descritti nelle righe successive.
data class QuickPreset(val name: String, val workSec: Int, val restSec: Int)
// DOC L0004: quickPresets: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
val quickPresets = listOf(QuickPreset("Tabata 1", 20, 10), QuickPreset("Tabata 2", 30, 15), QuickPreset("Tabata 3", 40, 20))

// DOC L0006: studioPresets: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
val studioPresets = listOf(QuickPreset("Studio 1", 1500, 300), QuickPreset("Studio 2", 3000, 600), QuickPreset("Studio 3", 2700, 900))
// DOC L0007: quickPresets: Seleziona le tre scelte rapide appropriate alla modalità corrente.
fun quickPresets(mode: String): List<QuickPreset> = if (mode == "STUDIO") studioPresets else quickPresets

// DOC L0009: newQuickWorkout: Prepara il modello del timer rapido, distinto tra Workout e Studio.
fun newQuickWorkout(mode: String = "WORKOUT"): Workout = if (mode == "STUDIO") newWorkout(mode).copy(id = "quick-timer-studio", title = "Studio 1") else Workout(
    // DOC L0010: Esegue il passaggio esplicito «id = "quick-timer", title = "Tabata 1",» nel flusso: Preset e modello iniziale del timer rapido Android. Nel contesto di newQuickWorkout.
    id = "quick-timer", title = "Tabata 1",
    // DOC L0011: Esegue il passaggio esplicito «blocks = listOf(Block(id = "quick-block", title = "Intervalli", restAfterBlockSec = 0, restBetweenExercisesSec = 15,» nel flusso: Preset e modello iniziale del timer rapido Android. Nel contesto di newQuickWorkout.
    blocks = listOf(Block(id = "quick-block", title = "Intervalli", restAfterBlockSec = 0, restBetweenExercisesSec = 15,
        // DOC L0012: Esegue il passaggio esplicito «exercises = listOf(Exercise(id = "quick-exercise", title = "Lavoro", repetitions = 8, workSec = 20, restBetweenRepsSec = 10, restAfterExerciseSec = 0))))» nel flusso: Preset e modello iniziale del timer rapido Android. Nel contesto di newQuickWorkout.
        exercises = listOf(Exercise(id = "quick-exercise", title = "Lavoro", repetitions = 8, workSec = 20, restBetweenRepsSec = 10, restAfterExerciseSec = 0))))
// DOC L0013: Chiude un blocco, una chiamata o un valore composto; non aggiunge una nuova operazione.
)
