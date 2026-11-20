# android/app/src/main/java/it/tabatastudio/domain/Modes.kt

Etichette, modelli iniziali e separazione delle modalità Android.

Tema, palette e lingua sono comuni; programmi, attività e statistiche vengono filtrati per modalità.
I numeri si riferiscono al sorgente originale; il collegamento apre esattamente quella riga. I commenti DOC nel branch sono aggiunte didattiche.
## Funzioni e callback
| Simbolo | Riga originale | Ruolo |
| --- | ---: | --- |
| forMode | [5](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/domain/Modes.kt#L5) | Filtra il backup Kotlin per modalità senza modificare il backup completo. |
| modeLabels | [7](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/domain/Modes.kt#L7) | Restituisce le etichette della modalità corrente, condivise tra schermate e notifica. |
| newExercise | [8](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/domain/Modes.kt#L8) | Crea un esercizio con identificativo nuovo e tempi iniziali coerenti con la modalità. |
| newBlock | [9](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/domain/Modes.kt#L9) | Crea un blocco Workout con un esercizio e il recupero comune tra esercizi. |
| newWorkout | [10](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/domain/Modes.kt#L10) | Crea un programma nuovo della modalità richiesta, con blocchi Workout oppure attività Studio. |

## Ogni riga

| Riga originale | Codice (estratto) | Spiegazione |
| ---: | --- | --- |
| [1](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/domain/Modes.kt#L1) | <code>package it.tabatastudio.domain</code> | Assegna il file al namespace it.tabatastudio.domain, distinto dai package UI, dominio, dati e servizio. |
| [2](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/domain/Modes.kt#L2) | <code>∅</code> | Riga vuota: separa visivamente i passaggi, senza introdurre istruzioni o dati. |
| [3](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/domain/Modes.kt#L3) | <code>val Workout.studio: Boolean get() = mode == &quot;STUDIO&quot;</code> | Workout: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. |
| [4](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/domain/Modes.kt#L4) | <code>val Session.studio: Boolean get() = mode == &quot;STUDIO&quot;</code> | Session: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. |
| [5](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/domain/Modes.kt#L5) | <code>fun Backup.forMode(mode: String): Backup = Backup(workouts.filter { it.mode == mode }, sessions.filter { it.mode == mode })</code> | forMode: Filtra il backup Kotlin per modalità senza modificare il backup completo. |
| [6](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/domain/Modes.kt#L6) | <code>data class ModeLabels(val title: String, val subtitle: String, val collection: String, val noun: String, val focus: String, val rest: String, val rounds: String…</code> | Dichiara il tipo o contenitore di questo modulo: Etichette, modelli iniziali e separazione delle modalità Android. I campi e i metodi sono descritti nelle righe successive. |
| [7](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/domain/Modes.kt#L7) | <code>fun modeLabels(mode: String): ModeLabels = if (mode == &quot;STUDIO&quot;) ModeLabels(&quot;Tabata Studio&quot;, &quot;Timer Pomodoro per studio e lavoro&quot;, &quot;Programmi di studio&quot;, &quot;progr…</code> | modeLabels: Restituisce le etichette della modalità corrente, condivise tra schermate e notifica. |
| [8](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/domain/Modes.kt#L8) | <code>fun newExercise(mode: String): Exercise = if (mode == &quot;STUDIO&quot;) Exercise(title = &quot;Concentrazione&quot;, workSec = 1500, restBetweenRepsSec = 300, restAfterExerciseSe…</code> | newExercise: Crea un esercizio con identificativo nuovo e tempi iniziali coerenti con la modalità. |
| [9](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/domain/Modes.kt#L9) | <code>fun newBlock(mode: String = &quot;WORKOUT&quot;): Block = Block(restBetweenExercisesSec = 15)</code> | newBlock: Crea un blocco Workout con un esercizio e il recupero comune tra esercizi. |
| [10](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/domain/Modes.kt#L10) | <code>fun newWorkout(mode: String): Workout = if (mode == &quot;STUDIO&quot;) Workout(mode = mode, title = &quot;Il mio studio&quot;, blocks = emptyList(), activities = listOf(StudyActiv…</code> | newWorkout: Crea un programma nuovo della modalità richiesta, con blocchi Workout oppure attività Studio. |
