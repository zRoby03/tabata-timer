// DOC L0001: Assegna il file al namespace it.tabatastudio, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.domain.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.domain.*
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.Assert.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.Assert.*
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.Test. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.Test

// DOC L0007: Dichiara il tipo o contenitore di questo modulo: Prove di regressione in TransitionTest.kt. I campi e i metodi sono descritti nelle righe successive.
class TransitionTest {
    // DOC L0008: clock: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private var clock = 0L
    // DOC L0009: timer: Prepara o verifica il caso di regressione «timer»; le asserzioni nel corpo specificano l’esito atteso.
    private fun timer(cooldown: Int = 4) = TimerEngine(Workout(cooldownSec = cooldown, blocks = listOf(Block(exercises = listOf(Exercise(workSec = 4, repetitions = 2, restBetweenRepsSec = 4))))), { clock }).also { it.start() }
    // DOC L0010: advance: Incrementa indice e visita di fase; alla fine produce il risultato senza aggiungere un recupero finale.
    private fun advance(t: TimerEngine, ms: Long) { clock += ms; t.tick() }
    // DOC L0011: whistleOnlyAtWorkStartNotAfterPause: Prepara o verifica il caso di regressione «whistle Only At Work Start Not After Pause»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun whistleOnlyAtWorkStartNotAfterPause() {
        // DOC L0012: t: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. cues: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di whistleOnlyAtWorkStartNotAfterPause.
        val t = timer(); val cues = TransitionCues(); assertNull(cues.read(t)); advance(t, 20000)
        // DOC L0013: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di whistleOnlyAtWorkStartNotAfterPause.
        assertEquals("whistle", cues.read(t)); assertNull(cues.read(t)); t.pause(); assertNull(cues.read(t)); advance(t, 5000); t.resume(); assertNull(cues.read(t))
    // DOC L0014: Chiude il costrutto aperto alla riga 11.
    }
    // DOC L0015: sighThenNextWhistle: Prepara o verifica il caso di regressione «sigh Then Next Whistle»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun sighThenNextWhistle() {
        // DOC L0016: t: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. cues: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di sighThenNextWhistle.
        val t = timer(); val cues = TransitionCues(); advance(t, 20000); cues.read(t); advance(t, 4000)
        // DOC L0017: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di sighThenNextWhistle.
        assertEquals("sigh", cues.read(t)); assertNull(cues.read(t)); advance(t, 4000); assertEquals("whistle", cues.read(t))
    // DOC L0018: Chiude il costrutto aperto alla riga 15.
    }
    // DOC L0019: bellOnlyOnceBeforeCooldown: Prepara o verifica il caso di regressione «bell Only Once Before Cooldown»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun bellOnlyOnceBeforeCooldown() {
        // DOC L0020: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. t: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. cues: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di bellOnlyOnceBeforeCooldown.
        val t = timer(); val cues = TransitionCues(); advance(t, 32000); assertEquals("finish", cues.read(t))
        // DOC L0021: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di bellOnlyOnceBeforeCooldown.
        t.previous(); assertEquals("whistle", cues.read(t)); t.skip(); assertNull(cues.read(t)); advance(t, 4000); assertNull(cues.read(t))
    // DOC L0022: Chiude il costrutto aperto alla riga 19.
    }
    // DOC L0023: noCooldownBellAndNoBellOnManualStop: Prepara o verifica il caso di regressione «no Cooldown Bell And No Bell On Manual Stop»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun noCooldownBellAndNoBellOnManualStop() {
        // DOC L0024: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. natural: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. cues: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di noCooldownBellAndNoBellOnManualStop.
        val natural = timer(0); val cues = TransitionCues(); advance(natural, 32000); assertEquals("finish", cues.read(natural)); assertNull(cues.read(natural))
        // DOC L0025: stopped: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di noCooldownBellAndNoBellOnManualStop.
        val stopped = timer(); advance(stopped, 20000); cues.read(stopped); stopped.stop(); assertNull(cues.read(stopped))
    // DOC L0026: Chiude il costrutto aperto alla riga 23.
    }
    // DOC L0027: pausedSkipWaitsForResumeAndPreviousIsNewVisit: Prepara o verifica il caso di regressione «paused Skip Waits For Resume And Previous Is New Visit»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun pausedSkipWaitsForResumeAndPreviousIsNewVisit() {
        // DOC L0028: t: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. cues: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di pausedSkipWaitsForResumeAndPreviousIsNewVisit.
        val t = timer(); val cues = TransitionCues(); advance(t, 20000); cues.read(t); t.pause(); t.skip(); assertNull(cues.read(t))
        // DOC L0029: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di pausedSkipWaitsForResumeAndPreviousIsNewVisit.
        t.resume(); assertEquals("sigh", cues.read(t)); t.previous(); assertEquals("whistle", cues.read(t))
    // DOC L0030: Chiude il costrutto aperto alla riga 27.
    }
    // DOC L0031: delayedPollingDoesNotReplayOldWhistles: Prepara o verifica il caso di regressione «delayed Polling Does Not Replay Old Whistles»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun delayedPollingDoesNotReplayOldWhistles() {
        // DOC L0032: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. t: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. cues: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di delayedPollingDoesNotReplayOldWhistles.
        val t = timer(); val cues = TransitionCues(); advance(t, 22000); assertNull(cues.read(t)); advance(t, 2000); assertEquals("sigh", cues.read(t))
        // DOC L0033: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. other: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di delayedPollingDoesNotReplayOldWhistles.
        val other = timer(); advance(other, 20000); assertEquals("whistle", cues.read(other))
    // DOC L0034: Chiude il costrutto aperto alla riga 31.
    }
    // DOC L0035: raisedVolumeAppliesOnlyToWhistleAndSigh: Prepara o verifica il caso di regressione «raised Volume Applies Only To Whistle And Sigh»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun raisedVolumeAppliesOnlyToWhistleAndSigh() {
        // DOC L0036: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di raisedVolumeAppliesOnlyToWhistleAndSigh.
        assertEquals(1f, cueVolume("whistle"), 0f) // PCM already amplified to 120%.
        // DOC L0037: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di raisedVolumeAppliesOnlyToWhistleAndSigh.
        assertEquals(0.85f, cueVolume("sigh"), 0f)
        // DOC L0038: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di raisedVolumeAppliesOnlyToWhistleAndSigh.
        for (key in listOf("finish", "work_3", "rest_3")) assertEquals(0.65f, cueVolume(key), 0f)
    // DOC L0039: Chiude il costrutto aperto alla riga 35.
    }
// DOC L0040: Chiude il costrutto aperto alla riga 7.
}
