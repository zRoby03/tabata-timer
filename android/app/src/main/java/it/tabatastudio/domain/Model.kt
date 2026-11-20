package it.tabatastudio.domain

import java.util.UUID

fun uid(): String = UUID.randomUUID().toString()
data class Exercise(val id: String = uid(), val title: String = "Nuovo esercizio", val repetitions: Int = 1, val workSec: Int = 30, val restBetweenRepsSec: Int = 10, val restAfterExerciseSec: Int = 15)
data class StudyActivity(val id: String = uid(), val title: String = "Concentrazione", val concentrationSec: Int = 1500, val sessions: Int = 4, val pauseSec: Int = 300)

data class Block(val id: String = uid(), val title: String = "Nuovo blocco", val cycles: Int = 1, val restBetweenCyclesSec: Int = 60, val restAfterBlockSec: Int = 60, val exercises: List<Exercise> = listOf(Exercise()), val restBetweenExercisesSec: Int? = null)
data class Workout(val schemaVersion: Int = 1, val id: String = uid(), val title: String = "Il mio allenamento", val description: String = "", val color: String = "#9153b7", val warmupSec: Int = 0, val cooldownSec: Int = 0, val blocks: List<Block> = listOf(Block()), val mode: String = "WORKOUT", val longBreakSec: Int = 0, val longBreakEvery: Int = 4, val activities: List<StudyActivity>? = null, val restBetweenActivitiesSec: Int = 300)
data class Phase(val type: String, val blockId: String?, val exerciseId: String?, val durationMs: Long, val label: String)
data class Session(val id: String, val workoutId: String, val workoutTitle: String = "", val startedAt: String, val endedAt: String, val plannedMs: Long, val elapsedMs: Long, val workMs: Long, val completedWorkIntervals: Int, val plannedWorkIntervals: Int, val pauses: Int, val status: String, val completedPhases: Int? = null, val plannedPhases: Int? = null, val mode: String = "WORKOUT")

fun validateWorkout(w: Workout) {
    require(w.schemaVersion == 1) { "Versione allenamento non supportata" }
    require(w.mode in listOf("WORKOUT", "STUDIO")) { "Modalità non valida" }
    require(w.longBreakSec in 0..3600 && w.longBreakEvery in 2..100) { "Pausa lunga non valida" }
    val ids = mutableSetOf<String>()
    fun unique(id: String) { require(id.matches(Regex("[A-Za-z0-9_-]{1,128}")) && ids.add(id)) { "ID non valido o duplicato" } }
    fun title(value: String) { require(value.isNotBlank() && value.length <= 120) { "Titolo non valido" } }
    fun sec(value: Int, min: Int = 0) { require(value in min..3600 && (!w.studio || value % 60 == 0)) { "Durata fuori limite: Studio usa minuti interi (massimo 60)" } }
    sec(w.longBreakSec)
    unique(w.id); title(w.title)
    require(w.description.length <= 2000 && w.color.matches(Regex("#[0-9a-fA-F]{6}"))) { "Descrizione o colore non valido" }
    sec(w.warmupSec); sec(w.cooldownSec)
    if (w.studio && w.activities != null) {
        sec(w.restBetweenActivitiesSec)
        require(w.activities.size in 1..5000) { "Serve almeno un’attività (massimo 5000)" }
        w.activities.forEach { a -> unique(a.id); title(a.title); sec(a.concentrationSec, 60); require(a.sessions in 1..100); sec(a.pauseSec) }
        return
    }
    require(w.blocks.size in 1..50) { "Servono da 1 a 50 blocchi" }
    w.blocks.forEach { b ->
        unique(b.id); title(b.title); b.restBetweenExercisesSec?.let { sec(it) }; require(b.cycles in 1..100); sec(b.restBetweenCyclesSec); sec(b.restAfterBlockSec)
        require(b.exercises.size in 1..100) { "Servono da 1 a 100 esercizi" }
        b.exercises.forEach { e -> unique(e.id); title(e.title); require(e.repetitions in 1..100); sec(e.workSec, 1); sec(e.restBetweenRepsSec); sec(e.restAfterExerciseSec) }
    }
}
fun duplicateWorkout(w: Workout): Workout {
    val copy = normalizeWorkout(w).copy(id = uid(), title = w.title.take(110) + " (copia)")
    return if (copy.studio) copy.copy(activities = copy.activities!!.map { it.copy(id = uid()) }) else copy.copy(blocks = copy.blocks.map { b -> b.copy(id = uid(), exercises = b.exercises.map { it.copy(id = uid()) }) })
}
fun presets(mode: String = "WORKOUT"): List<Workout> = if (mode == "STUDIO") listOf(25 to 5, 50 to 10).map { (focus, rest) -> newWorkout("STUDIO").copy(title = "Studio $focus/$rest", activities = listOf(StudyActivity(concentrationSec = focus * 60, pauseSec = rest * 60))) } else listOf(
    Workout(title = "Tabata classico", description = "Otto intervalli di lavoro, senza recupero finale.", blocks = listOf(Block(title = "Tabata", restBetweenCyclesSec = 0, restAfterBlockSec = 0, exercises = listOf(Exercise(title = "Esercizio libero", repetitions = 8, workSec = 20, restAfterExerciseSec = 0))))),
    Workout(title = "Circuito completo", description = "Tre movimenti, due giri. Personalizza il tuo ritmo.", color = "#b54079", warmupSec = 0, cooldownSec = 60, blocks = listOf(Block(title = "Circuito", cycles = 2, exercises = listOf("Squat", "Push-up", "Plank").map { Exercise(title = it) })))
)
fun formatTime(ms: Long): String {
    val s = (ms.coerceAtLeast(0) + 999) / 1000
    return if (s >= 3600) "%d:%02d:%02d".format(s / 3600, s / 60 % 60, s % 60) else "%d:%02d".format(s / 60, s % 60)
}
