# android/app/src/main/java/it/tabatastudio/ui/LocaleText.kt

Adattatori di testo per la UI Compose.

Localizza messaggi e nomi iniziali nuovi senza rinominare programmi già salvati dall’utente.
I numeri si riferiscono al sorgente originale; il collegamento apre esattamente quella riga. I commenti DOC nel branch sono aggiunte didattiche.
## Funzioni e callback
| Simbolo | Riga originale | Ruolo |
| --- | ---: | --- |
| tr | [5](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/ui/LocaleText.kt#L5) | Delega la traduzione della UI ad AppText preservando gli argomenti dei segnaposto. |
| localizedNewWorkout | [7](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/ui/LocaleText.kt#L7) | Traduce soltanto le etichette di un programma appena creato, senza migrare i nomi degli archivi. |
| localizedNewBlock | [12](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/ui/LocaleText.kt#L12) | Localizza il titolo iniziale del blocco e i suoi esercizi nuovi. |
| localizedNewExercise | [13](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/ui/LocaleText.kt#L13) | Localizza il titolo predefinito di un esercizio appena creato. |

## Ogni riga

| Riga originale | Codice (estratto) | Spiegazione |
| ---: | --- | --- |
| [1](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/ui/LocaleText.kt#L1) | <code>package it.tabatastudio.ui</code> | Assegna il file al namespace it.tabatastudio.ui, distinto dai package UI, dominio, dati e servizio. |
| [2](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/ui/LocaleText.kt#L2) | <code>∅</code> | Riga vuota: separa visivamente i passaggi, senza introdurre istruzioni o dati. |
| [3](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/ui/LocaleText.kt#L3) | <code>import it.tabatastudio.i18n.AppText</code> | Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.i18n.AppText. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra. |
| [4](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/ui/LocaleText.kt#L4) | <code>∅</code> | Riga vuota: separa visivamente i passaggi, senza introdurre istruzioni o dati. |
| [5](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/ui/LocaleText.kt#L5) | <code>internal fun tr(source: String?, vararg values: Any?): String = AppText.text(source, *values)</code> | tr: Delega la traduzione della UI ad AppText preservando gli argomenti dei segnaposto. |
| [6](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/ui/LocaleText.kt#L6) | <code>∅</code> | Riga vuota: separa visivamente i passaggi, senza introdurre istruzioni o dati. |
| [7](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/ui/LocaleText.kt#L7) | <code>internal fun localizedNewWorkout(w: it.tabatastudio.domain.Workout): it.tabatastudio.domain.Workout = w.copy(</code> | localizedNewWorkout: Traduce soltanto le etichette di un programma appena creato, senza migrare i nomi degli archivi. |
| [8](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/ui/LocaleText.kt#L8) | <code>title = tr(w.title), description = tr(w.description),</code> | Esegue il passaggio esplicito «title = tr(w.title), description = tr(w.description),» nel flusso: Adattatori di testo per la UI Compose. Nel contesto di localizedNewWorkout. |
| [9](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/ui/LocaleText.kt#L9) | <code>blocks = w.blocks.map { localizedNewBlock(it) },</code> | Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di localizedNewWorkout. |
| [10](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/ui/LocaleText.kt#L10) | <code>activities = w.activities?.map { it.copy(title = tr(it.title)) }</code> | Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di localizedNewWorkout. |
| [11](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/ui/LocaleText.kt#L11) | <code>)</code> | Chiude un blocco, una chiamata o un valore composto; non aggiunge una nuova operazione. |
| [12](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/ui/LocaleText.kt#L12) | <code>internal fun localizedNewBlock(b: it.tabatastudio.domain.Block): it.tabatastudio.domain.Block = b.copy(title = tr(b.title), exercises = b.exercises.map { locali…</code> | localizedNewBlock: Localizza il titolo iniziale del blocco e i suoi esercizi nuovi. |
| [13](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/ui/LocaleText.kt#L13) | <code>internal fun localizedNewExercise(e: it.tabatastudio.domain.Exercise): it.tabatastudio.domain.Exercise = e.copy(title = tr(e.title))</code> | localizedNewExercise: Localizza il titolo predefinito di un esercizio appena creato. |
