package it.tabatastudio.domain

data class QuickPreset(val name: String, val workSec: Int, val restSec: Int)
val quickPresets = listOf(QuickPreset("Tabata 1", 20, 10), QuickPreset("Tabata 2", 30, 15), QuickPreset("Tabata 3", 40, 20))

val studioPresets = listOf(QuickPreset("Studio 1", 1500, 300), QuickPreset("Studio 2", 3000, 600), QuickPreset("Studio 3", 2700, 900))
fun quickPresets(mode: String): List<QuickPreset> = if (mode == "STUDIO") studioPresets else quickPresets

fun newQuickWorkout(mode: String = "WORKOUT"): Workout = if (mode == "STUDIO") newWorkout(mode).copy(id = "quick-timer-studio", title = "Studio 1") else Workout(
    timingVersion = 2, id = "quick-timer", title = "Tabata 1",
    blocks = listOf(Block(id = "quick-block", title = "Intervalli", restAfterBlockSec = 0, restBetweenExercisesSec = 15,
        exercises = listOf(Exercise(id = "quick-exercise", title = "Lavoro", repetitions = 8, workSec = 20, restBetweenRepsSec = 10, restAfterExerciseSec = 0))))
)
