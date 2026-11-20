package it.tabatastudio.domain

val Workout.studio: Boolean get() = mode == "STUDIO"
val Session.studio: Boolean get() = mode == "STUDIO"
fun Backup.forMode(mode: String): Backup = Backup(workouts.filter { it.mode == mode }, sessions.filter { it.mode == mode })
data class ModeLabels(val title: String, val subtitle: String, val collection: String, val noun: String, val focus: String, val rest: String, val rounds: String, val final: String)
fun modeLabels(mode: String): ModeLabels = if (mode == "STUDIO") ModeLabels("Tabata Studio", "Timer Pomodoro per studio e lavoro", "Programmi di studio", "programma di studio", "Concentrazione", "Pausa", "Sessioni", "Pausa finale") else ModeLabels("Tabata Workout", "Allenamento a intervalli", "Allenamenti", "allenamento", "Lavoro", "Recupero", "Giri", "Defaticamento")
fun newExercise(mode: String): Exercise = if (mode == "STUDIO") Exercise(title = "Concentrazione", workSec = 1500, restBetweenRepsSec = 300, restAfterExerciseSec = 300) else Exercise()
fun newBlock(mode: String = "WORKOUT"): Block = Block(restBetweenExercisesSec = 15)
fun newWorkout(mode: String): Workout = if (mode == "STUDIO") Workout(mode = mode, title = "Il mio studio", blocks = emptyList(), activities = listOf(StudyActivity()), restBetweenActivitiesSec = 300) else Workout(blocks = listOf(newBlock()))
