// DOC L0001: Assegna il file al namespace it.tabatastudio, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import android.app.Application. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.app.Application
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.data.StudioDatabase. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.data.StudioDatabase
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.data.StudioRepository. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.data.StudioRepository
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.timer.TimerRuntime. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.timer.TimerRuntime

// DOC L0008: Dichiara il tipo o contenitore di questo modulo: Contenitore delle dipendenze per il processo Android. I campi e i metodi sono descritti nelle righe successive.
class TabataApplication : Application() {
    // DOC L0009: onCreate: Inizializza la localizzazione nativa quando viene creato il processo.
    override fun onCreate() { super.onCreate(); it.tabatastudio.i18n.AppText.initialize(this) }
    // DOC L0010: repository: accesso ai programmi, sessioni e checkpoint persistenti. Nel contesto di onCreate.
    val repository by lazy { StudioRepository(StudioDatabase.open(this)) }
    // DOC L0011: timerRuntime: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di onCreate.
    val timerRuntime by lazy { TimerRuntime(this, repository) }
// DOC L0012: Chiude il costrutto aperto alla riga 8.
}
