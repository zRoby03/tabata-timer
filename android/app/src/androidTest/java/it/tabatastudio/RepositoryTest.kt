// DOC L0001: Assegna il file al namespace it.tabatastudio, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import androidx.room.Room. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.room.Room
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import androidx.test.core.app.ApplicationProvider. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.test.core.app.ApplicationProvider
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import androidx.test.ext.junit.runners.AndroidJUnit4. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.test.ext.junit.runners.AndroidJUnit4
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.data.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.data.*
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.domain.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.domain.*
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import kotlinx.coroutines.runBlocking. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import kotlinx.coroutines.runBlocking
// DOC L0009: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.*
// DOC L0010: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.Assert.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.Assert.*
// DOC L0011: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.runner.RunWith. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.runner.RunWith

// DOC L0013: Applica l’annotazione dichiarata: Compose identifica UI, Room genera accessi e schema, mentre le annotazioni Test selezionano i casi da eseguire.
@RunWith(AndroidJUnit4::class)
// DOC L0014: Dichiara il tipo o contenitore di questo modulo: Prove di regressione in RepositoryTest.kt. I campi e i metodi sono descritti nelle righe successive.
class RepositoryTest {
    // DOC L0015: Esegue il passaggio esplicito «private lateinit var db: StudioDatabase» nel flusso: Prove di regressione in RepositoryTest.kt.
    private lateinit var db: StudioDatabase
    // DOC L0016: Esegue il passaggio esplicito «private lateinit var repo: StudioRepository» nel flusso: Prove di regressione in RepositoryTest.kt.
    private lateinit var repo: StudioRepository
    // DOC L0017: setup: Prepara o verifica il caso di regressione «setup»; le asserzioni nel corpo specificano l’esito atteso.
    @Before fun setup() { db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), StudioDatabase::class.java).build(); repo = StudioRepository(db) }
    // DOC L0018: close: Prepara o verifica il caso di regressione «close»; le asserzioni nel corpo specificano l’esito atteso.
    @After fun close() { db.close() }
    // DOC L0019: session: Prepara o verifica il caso di regressione «session»; le asserzioni nel corpo specificano l’esito atteso.
    private fun session(): Session = Session("s", "w", "Test", "2026-11-17T10:00:00.000Z", "2026-11-17T10:00:01.000Z", 2000, 1000, 1000, 0, 1, 0, "INTERRUPTED")
    // DOC L0020: atomicReplaceAndIdempotentSession: Prepara o verifica il caso di regressione «atomic Replace And Idempotent Session»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun atomicReplaceAndIdempotentSession() = runBlocking {
        // DOC L0021: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di atomicReplaceAndIdempotentSession.
        repo.initialize(); assertEquals(2, repo.read().workouts.size)
        // DOC L0022: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di atomicReplaceAndIdempotentSession.
        repo.finish(session()); repo.finish(session()); assertEquals(1, repo.read().sessions.size)
        // DOC L0023: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di atomicReplaceAndIdempotentSession.
        repo.replace(Backup(emptyList(), emptyList())); repo.initialize(); assertTrue(repo.read().workouts.isEmpty()); assertTrue(repo.read().sessions.isEmpty())
    // DOC L0024: Chiude il costrutto aperto alla riga 20.
    }
    // DOC L0025: checkpointRecoveryExactlyOnce: Prepara o verifica il caso di regressione «checkpoint Recovery Exactly Once»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun checkpointRecoveryExactlyOnce() = runBlocking {
        // DOC L0026: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di checkpointRecoveryExactlyOnce.
        repo.initialize(); repo.checkpoint(session()); assertTrue(repo.initialize()); assertFalse(repo.initialize())
        // DOC L0027: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. data: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di checkpointRecoveryExactlyOnce.
        val data = repo.read(); assertEquals(1, data.sessions.size); assertEquals("INTERRUPTED", data.sessions[0].status)
    // DOC L0028: Chiude il costrutto aperto alla riga 25.
    }
    // DOC L0029: workoutRoundtrip: Prepara o verifica il caso di regressione «workout Roundtrip»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun workoutRoundtrip() = runBlocking {
        // DOC L0030: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di workoutRoundtrip.
        repo.initialize(); val w = Workout(title = "Persistenza"); repo.save(w); assertEquals(w, repo.read().workouts.find { it.id == w.id })
    // DOC L0031: Chiude il costrutto aperto alla riga 29.
    }
    // DOC L0032: studioPersistenceAndCheckpointKeepMode: Prepara o verifica il caso di regressione «studio Persistence And Checkpoint Keep Mode»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun studioPersistenceAndCheckpointKeepMode() = runBlocking {
        // DOC L0033: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di studioPersistenceAndCheckpointKeepMode.
        repo.initialize(); val w = newQuickWorkout("STUDIO").copy(activities = listOf(StudyActivity(title = "Matematica", sessions = 2), StudyActivity(title = "Storia", sessions = 1)), restBetweenActivitiesSec = 120)
        // DOC L0034: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di studioPersistenceAndCheckpointKeepMode.
        repo.save(w); repo.checkpoint(session().copy(mode = "STUDIO", workoutId = w.id))
        // DOC L0035: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di studioPersistenceAndCheckpointKeepMode.
        assertTrue(repo.initialize()); assertFalse(repo.initialize())
        // DOC L0036: studio: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di studioPersistenceAndCheckpointKeepMode.
        val studio = repo.read().forMode("STUDIO")
        // DOC L0037: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di studioPersistenceAndCheckpointKeepMode.
        assertEquals(listOf(w), studio.workouts); assertEquals("STUDIO", studio.sessions.single().mode)
        // DOC L0038: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di studioPersistenceAndCheckpointKeepMode.
        assertEquals(2, repo.read().forMode("WORKOUT").workouts.size)
    // DOC L0039: Chiude il costrutto aperto alla riga 32.
    }
// DOC L0040: Chiude il costrutto aperto alla riga 14.
}
