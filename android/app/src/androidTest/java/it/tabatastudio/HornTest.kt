// DOC L0001: Assegna il file al namespace it.tabatastudio, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import androidx.test.core.app.ApplicationProvider. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.test.core.app.ApplicationProvider
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import androidx.test.ext.junit.runners.AndroidJUnit4. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.test.ext.junit.runners.AndroidJUnit4
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.ui.HornSignals. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.ui.HornSignals
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import kotlinx.coroutines.delay. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import kotlinx.coroutines.delay
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import kotlinx.coroutines.runBlocking. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import kotlinx.coroutines.runBlocking
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.Assert.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.Assert.*
// DOC L0009: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.Test. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.Test
// DOC L0010: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.runner.RunWith. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.runner.RunWith

// DOC L0012: Applica l’annotazione dichiarata: Compose identifica UI, Room genera accessi e schema, mentre le annotazioni Test selezionano i casi da eseguire.
@RunWith(AndroidJUnit4::class)
// DOC L0013: Dichiara il tipo o contenitore di questo modulo: Prove di regressione in HornTest.kt. I campi e i metodi sono descritti nelle righe successive.
class HornTest {
    // DOC L0014: localNotesLoadAndPlayOncePerSecond: Prepara o verifica il caso di regressione «local Notes Load And Play Once Per Second»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun localNotesLoadAndPlayOncePerSecond() = runBlocking {
        // DOC L0015: horn: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di localNotesLoadAndPlayOncePerSecond.
        val horn = HornSignals(ApplicationProvider.getApplicationContext())
        // DOC L0016: Esegue il passaggio esplicito «try {» nel flusso: Prove di regressione in HornTest.kt. Nel contesto di localNotesLoadAndPlayOncePerSecond.
        try {
            // DOC L0017: Programma il prossimo aggiornamento; la durata effettiva rimane misurata dall’orologio, non dal numero di aggiornamenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di localNotesLoadAndPlayOncePerSecond.
            repeat(100) { if (!horn.isReady) delay(20) }
            // DOC L0018: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di localNotesLoadAndPlayOncePerSecond.
            assertTrue("Campione WAV locale non caricato", horn.isReady)
            // DOC L0019: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di localNotesLoadAndPlayOncePerSecond.
            for (phase in listOf("WORK", "COOLDOWN")) for (second in listOf(3, 2, 1)) {
                // DOC L0020: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Prepara o riproduce il segnale audio; il motore del tempo rimane indipendente dalla riproduzione. Nel contesto di localNotesLoadAndPlayOncePerSecond.
                assertTrue("Riproduzione trombetta non avviata", horn.play(second, phase) > 0)
                // DOC L0021: Programma il prossimo aggiornamento; la durata effettiva rimane misurata dall’orologio, non dal numero di aggiornamenti. Nel contesto di localNotesLoadAndPlayOncePerSecond.
                delay(1050)
            // DOC L0022: Chiude il costrutto aperto alla riga 19.
            }
            // DOC L0023: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di localNotesLoadAndPlayOncePerSecond.
            for (key in listOf("whistle", "sigh", "finish")) {
                // DOC L0024: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di localNotesLoadAndPlayOncePerSecond.
                assertTrue("Suono di cambio turno non avviato", horn.playCue(key) > 0)
                // DOC L0025: Programma il prossimo aggiornamento; la durata effettiva rimane misurata dall’orologio, non dal numero di aggiornamenti. Nel contesto di localNotesLoadAndPlayOncePerSecond.
                delay(1050)
            // DOC L0026: Chiude il costrutto aperto alla riga 23.
            }
            // DOC L0027: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Prepara o riproduce il segnale audio; il motore del tempo rimane indipendente dalla riproduzione. Nel contesto di localNotesLoadAndPlayOncePerSecond.
            assertEquals(0, horn.play(0, "WORK"))
        // DOC L0028: Esegue il passaggio esplicito «} finally { horn.release() }» nel flusso: Prove di regressione in HornTest.kt. Nel contesto di localNotesLoadAndPlayOncePerSecond.
        } finally { horn.release() }
    // DOC L0029: Chiude il costrutto aperto alla riga 14.
    }
// DOC L0030: Chiude il costrutto aperto alla riga 13.
}
