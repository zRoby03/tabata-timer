// DOC L0001: Assegna il file al namespace it.tabatastudio.domain, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.domain

// DOC L0003: Workout: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
val Workout.studio: Boolean get() = mode == "STUDIO"
// DOC L0004: Session: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
val Session.studio: Boolean get() = mode == "STUDIO"
// DOC L0005: forMode: Filtra il backup Kotlin per modalità senza modificare il backup completo.
fun Backup.forMode(mode: String): Backup = Backup(workouts.filter { it.mode == mode }, sessions.filter { it.mode == mode })
// DOC L0006: Dichiara il tipo o contenitore di questo modulo: Etichette, modelli iniziali e separazione delle modalità Android. I campi e i metodi sono descritti nelle righe successive.
data class ModeLabels(val title: String, val subtitle: String, val collection: String, val noun: String, val focus: String, val rest: String, val rounds: String, val final: String)
// DOC L0007: modeLabels: Restituisce le etichette della modalità corrente, condivise tra schermate e notifica.
fun modeLabels(mode: String): ModeLabels = if (mode == "STUDIO") ModeLabels("Tabata Studio", "Timer Pomodoro per studio e lavoro", "Programmi di studio", "programma di studio", "Concentrazione", "Pausa", "Sessioni", "Pausa finale") else ModeLabels("Tabata Workout", "Allenamento a intervalli", "Allenamenti", "allenamento", "Lavoro", "Recupero", "Giri", "Defaticamento")
// DOC L0008: newExercise: Crea un esercizio con identificativo nuovo e tempi iniziali coerenti con la modalità.
fun newExercise(mode: String): Exercise = if (mode == "STUDIO") Exercise(title = "Concentrazione", workSec = 1500, restBetweenRepsSec = 300, restAfterExerciseSec = 300) else Exercise()
// DOC L0009: newBlock: Crea un blocco Workout con un esercizio e il recupero comune tra esercizi.
fun newBlock(mode: String = "WORKOUT"): Block = Block(restBetweenExercisesSec = 15)
// DOC L0010: newWorkout: Crea un programma nuovo della modalità richiesta, con blocchi Workout oppure attività Studio.
fun newWorkout(mode: String): Workout = if (mode == "STUDIO") Workout(mode = mode, title = "Il mio studio", blocks = emptyList(), activities = listOf(StudyActivity()), restBetweenActivitiesSec = 300) else Workout(blocks = listOf(newBlock()))
