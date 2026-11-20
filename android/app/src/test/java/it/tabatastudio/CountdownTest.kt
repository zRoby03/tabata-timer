// DOC L0001: Assegna il file al namespace it.tabatastudio, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.domain.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.domain.*
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.Assert.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.Assert.*
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.Test. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.Test

// DOC L0007: Dichiara il tipo o contenitore di questo modulo: Prove di regressione in CountdownTest.kt. I campi e i metodi sono descritti nelle righe successive.
class CountdownTest {
    // DOC L0008: clock: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private var clock = 0L
    // DOC L0009: timer: motore corrente, opzionale quando nessuna sessione è stata avviata.
    private val timer = TimerEngine(Workout(blocks = listOf(Block(exercises = listOf(Exercise(workSec = 4))))), { clock })
    // DOC L0010: cues: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private val cues = CountdownCues()
    // DOC L0011: advance: Incrementa indice e visita di fase; alla fine produce il risultato senza aggiungere un recupero finale.
    private fun advance(ms: Long) { clock += ms; timer.tick() }

    // DOC L0013: preparation20ThenWork: Prepara o verifica il caso di regressione «preparation20 Then Work»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun preparation20ThenWork() {
        // DOC L0014: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di preparation20ThenWork.
        timer.start(); assertEquals("PREPARE", timer.currentPhase!!.type); assertEquals(20000L, timer.remainingMs)
        // DOC L0015: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di preparation20ThenWork.
        advance(19999); assertEquals(-1, timer.index); assertEquals(0L, timer.elapsedMs); assertEquals(0L, timer.workMs)
        // DOC L0016: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di preparation20ThenWork.
        advance(1); assertEquals(0, timer.index); assertEquals(4000L, timer.remainingMs)
        // DOC L0017: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di preparation20ThenWork.
        advance(1000); assertEquals(1000L, timer.workMs)
    // DOC L0018: Chiude il costrutto aperto alla riga 13.
    }
    // DOC L0019: pausePreparationAndSkip: Prepara o verifica il caso di regressione «pause Preparation And Skip»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun pausePreparationAndSkip() {
        // DOC L0020: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di pausePreparationAndSkip.
        timer.start(); advance(5000); timer.pause(); advance(30000); assertEquals(15000L, timer.remainingMs)
        // DOC L0021: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di pausePreparationAndSkip.
        timer.resume(); timer.skip(); assertEquals(0, timer.index); assertEquals(0L, timer.elapsedMs)
        // DOC L0022: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di pausePreparationAndSkip.
        timer.previous(); assertEquals(0, timer.index)
    // DOC L0023: Chiude il costrutto aperto alla riga 19.
    }
    // DOC L0024: stopPreparationDoesNotCreditWork: Prepara o verifica il caso di regressione «stop Preparation Does Not Credit Work»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun stopPreparationDoesNotCreditWork() {
        // DOC L0025: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di stopPreparationDoesNotCreditWork.
        timer.start(); advance(5000); timer.stop(); assertEquals("INTERRUPTED", timer.result!!.status)
        // DOC L0026: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di stopPreparationDoesNotCreditWork.
        assertEquals(0L, timer.result!!.elapsedMs); assertEquals(0, timer.result!!.completedPhases)
    // DOC L0027: Chiude il costrutto aperto alla riga 24.
    }
    // DOC L0028: catchupPreservesWorkoutTotals: Prepara o verifica il caso di regressione «catchup Preserves Workout Totals»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun catchupPreservesWorkoutTotals() {
        // DOC L0029: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di catchupPreservesWorkoutTotals.
        timer.start(); advance(99999); assertEquals(4000L, timer.result!!.elapsedMs); assertEquals(4000L, timer.result!!.workMs); assertEquals(1, timer.result!!.completedPhases)
    // DOC L0030: Chiude il costrutto aperto alla riga 28.
    }
    // DOC L0031: cues321OnlyOnceAndPauseDoesNotRepeat: Prepara o verifica il caso di regressione «cues321 Only Once And Pause Does Not Repeat»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun cues321OnlyOnceAndPauseDoesNotRepeat() {
        // DOC L0032: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di cues321OnlyOnceAndPauseDoesNotRepeat.
        timer.start(); assertNull(cues.read(timer)); advance(17000); assertEquals(3, cues.read(timer)); assertNull(cues.read(timer))
        // DOC L0033: Esegue il passaggio esplicito «timer.pause(); advance(10000); assertNull(cues.read(timer)); timer.resume(); assertNull(cues.read(timer))» nel flusso: Prove di regressione in CountdownTest.kt. Nel contesto di cues321OnlyOnceAndPauseDoesNotRepeat.
        timer.pause(); advance(10000); assertNull(cues.read(timer)); timer.resume(); assertNull(cues.read(timer))
        // DOC L0034: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di cues321OnlyOnceAndPauseDoesNotRepeat.
        advance(1000); assertEquals(2, cues.read(timer)); advance(1000); assertEquals(1, cues.read(timer))
        // DOC L0035: Esegue il passaggio esplicito «timer.stop(); assertNull(cues.read(timer))» nel flusso: Prove di regressione in CountdownTest.kt. Nel contesto di cues321OnlyOnceAndPauseDoesNotRepeat.
        timer.stop(); assertNull(cues.read(timer))
    // DOC L0036: Chiude il costrutto aperto alla riga 31.
    }
    // DOC L0037: delayedCuesAndReplay: Prepara o verifica il caso di regressione «delayed Cues And Replay»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun delayedCuesAndReplay() {
        // DOC L0038: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di delayedCuesAndReplay.
        timer.start(); advance(19000); assertEquals(1, cues.read(timer)); assertNull(cues.read(timer))
        // DOC L0039: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di delayedCuesAndReplay.
        advance(2000); assertEquals(3, cues.read(timer)); timer.previous(); assertNull(cues.read(timer)); advance(1000); assertEquals(3, cues.read(timer))
    // DOC L0040: Chiude il costrutto aperto alla riga 37.
    }
    // DOC L0041: workAndRecoveryUseOneNoteForEachSecond: Prepara o verifica il caso di regressione «work And Recovery Use One Note For Each Second»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun workAndRecoveryUseOneNoteForEachSecond() {
        // DOC L0042: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di workAndRecoveryUseOneNoteForEachSecond.
        assertEquals(listOf("work_3", "work_2", "work_1"), listOf(3, 2, 1).map { hornSample(it, "WORK") })
        // DOC L0043: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di workAndRecoveryUseOneNoteForEachSecond.
        for (phase in listOf("REST_REP", "REST_EXERCISE", "REST_CYCLE", "REST_BLOCK", "COOLDOWN", "WARMUP")) {
            // DOC L0044: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di workAndRecoveryUseOneNoteForEachSecond.
            assertEquals(listOf("rest_3", "rest_2", "rest_1"), listOf(3, 2, 1).map { hornSample(it, phase) })
        // DOC L0045: Chiude il costrutto aperto alla riga 43.
        }
    // DOC L0046: Chiude il costrutto aperto alla riga 41.
    }
    // DOC L0047: preparationHasThreeNotesAndNoFourthCueAtZero: Prepara o verifica il caso di regressione «preparation Has Three Notes And No Fourth Cue At Zero»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun preparationHasThreeNotesAndNoFourthCueAtZero() {
        // DOC L0048: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di preparationHasThreeNotesAndNoFourthCueAtZero.
        assertEquals(listOf("rest_3", "rest_2", "rest_1"), listOf(3, 2, 1).map { hornSample(it, "PREPARE") })
        // DOC L0049: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di preparationHasThreeNotesAndNoFourthCueAtZero.
        for (phase in listOf("WORK", "COOLDOWN", "PREPARE")) for (n in listOf(0, 4, -1)) assertNull(hornSample(n, phase))
    // DOC L0050: Chiude il costrutto aperto alla riga 47.
    }
// DOC L0051: Chiude il costrutto aperto alla riga 7.
}
