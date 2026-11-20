# android/app/src/main/java/it/tabatastudio/TabataApplication.kt

Contenitore delle dipendenze per il processo Android.

Repository e runtime sono unici nel processo; la chiusura della finestra non ricrea il timer.
I numeri si riferiscono al sorgente originale; il collegamento apre esattamente quella riga. I commenti DOC nel branch sono aggiunte didattiche.
## Funzioni e callback
| Simbolo | Riga originale | Ruolo |
| --- | ---: | --- |
| onCreate | [9](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/TabataApplication.kt#L9) | Inizializza la localizzazione nativa quando viene creato il processo. |

## Ogni riga

| Riga originale | Codice (estratto) | Spiegazione |
| ---: | --- | --- |
| [1](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/TabataApplication.kt#L1) | <code>package it.tabatastudio</code> | Assegna il file al namespace it.tabatastudio, distinto dai package UI, dominio, dati e servizio. |
| [2](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/TabataApplication.kt#L2) | <code>∅</code> | Riga vuota: separa visivamente i passaggi, senza introdurre istruzioni o dati. |
| [3](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/TabataApplication.kt#L3) | <code>import android.app.Application</code> | Rende disponibili le dipendenze esplicite di questa riga: import android.app.Application. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra. |
| [4](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/TabataApplication.kt#L4) | <code>import it.tabatastudio.data.StudioDatabase</code> | Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.data.StudioDatabase. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra. |
| [5](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/TabataApplication.kt#L5) | <code>import it.tabatastudio.data.StudioRepository</code> | Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.data.StudioRepository. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra. |
| [6](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/TabataApplication.kt#L6) | <code>import it.tabatastudio.timer.TimerRuntime</code> | Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.timer.TimerRuntime. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra. |
| [7](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/TabataApplication.kt#L7) | <code>∅</code> | Riga vuota: separa visivamente i passaggi, senza introdurre istruzioni o dati. |
| [8](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/TabataApplication.kt#L8) | <code>class TabataApplication : Application() {</code> | Dichiara il tipo o contenitore di questo modulo: Contenitore delle dipendenze per il processo Android. I campi e i metodi sono descritti nelle righe successive. |
| [9](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/TabataApplication.kt#L9) | <code>override fun onCreate() { super.onCreate(); it.tabatastudio.i18n.AppText.initialize(this) }</code> | onCreate: Inizializza la localizzazione nativa quando viene creato il processo. |
| [10](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/TabataApplication.kt#L10) | <code>val repository by lazy { StudioRepository(StudioDatabase.open(this)) }</code> | repository: accesso ai programmi, sessioni e checkpoint persistenti. Nel contesto di onCreate. |
| [11](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/TabataApplication.kt#L11) | <code>val timerRuntime by lazy { TimerRuntime(this, repository) }</code> | timerRuntime: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di onCreate. |
| [12](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/main/java/it/tabatastudio/TabataApplication.kt#L12) | <code>}</code> | Chiude il costrutto aperto alla riga 8. |
