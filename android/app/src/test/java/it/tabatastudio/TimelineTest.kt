// DOC L0001: Assegna il file al namespace it.tabatastudio, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.domain.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.domain.*
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.Assert.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.Assert.*
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.Test. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.Test

// DOC L0007: Dichiara il tipo o contenitore di questo modulo: Prove di regressione in TimelineTest.kt. I campi e i metodi sono descritti nelle righe successive.
class TimelineTest {
    // DOC L0008: sample: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private val sample = Workout(id = "w", blocks = listOf(Block(id = "b", cycles = 2, exercises = listOf(Exercise(id = "e", repetitions = 3, workSec = 20)))))
    // DOC L0009: reference220: Prepara o verifica il caso di regressione «reference220»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun reference220() { assertEquals(220000L, plannedMs(compileTimeline(sample))) }
    // DOC L0010: noTrailingRest: Prepara o verifica il caso di regressione «no Trailing Rest»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun noTrailingRest() {
        // DOC L0011: one: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di noTrailingRest.
        val one = sample.copy(blocks = listOf(sample.blocks[0].copy(cycles = 1, exercises = listOf(sample.blocks[0].exercises[0].copy(repetitions = 1)))))
        // DOC L0012: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di noTrailingRest.
        assertEquals(listOf("WORK"), compileTimeline(one).map { it.type })
    // DOC L0013: Chiude il costrutto aperto alla riga 10.
    }
    // DOC L0014: limits: Prepara o verifica il caso di regressione «limits»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun limits() {
        // DOC L0015: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di limits.
        listOf(sample.copy(schemaVersion = 2), sample.copy(warmupSec = -1), sample.copy(blocks = emptyList()), sample.copy(blocks = listOf(sample.blocks[0].copy(cycles = 101))), sample.copy(blocks = listOf(sample.blocks[0].copy(id = "w")))).forEach { w ->
            // DOC L0016: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di limits.
            assertThrows(IllegalArgumentException::class.java) { compileTimeline(w) }
        // DOC L0017: Chiude il costrutto aperto alla riga 15.
        }
    // DOC L0018: Chiude il costrutto aperto alla riga 14.
    }
    // DOC L0019: expansionBounded: Prepara o verifica il caso di regressione «expansion Bounded»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun expansionBounded() {
        // DOC L0020: w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di expansionBounded.
        val w = sample.copy(blocks = listOf(sample.blocks[0].copy(cycles = 100, exercises = listOf(sample.blocks[0].exercises[0].copy(repetitions = 100)))))
        // DOC L0021: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di expansionBounded.
        assertThrows(IllegalArgumentException::class.java) { compileTimeline(w) }
    // DOC L0022: Chiude il costrutto aperto alla riga 19.
    }
// DOC L0023: Chiude il costrutto aperto alla riga 7.
}
