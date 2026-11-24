package it.tabatastudio

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import it.tabatastudio.data.*
import it.tabatastudio.domain.*
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryTest {
    private lateinit var db: StudioDatabase
    private lateinit var repo: StudioRepository
    @Before fun setup() { db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), StudioDatabase::class.java).build(); repo = StudioRepository(db) }
    @After fun close() { db.close() }
    private fun session(): Session = Session("s", "w", "Test", "2026-11-17T10:00:00.000Z", "2026-11-17T10:00:01.000Z", 2000, 1000, 1000, 0, 1, 0, "INTERRUPTED")
    @Test fun atomicReplaceAndIdempotentSession() = runBlocking {
        repo.initialize(); assertEquals(2, repo.read().workouts.size)
        repo.finish(session()); repo.finish(session()); assertEquals(1, repo.read().sessions.size)
        repo.replace(Backup(emptyList(), emptyList())); repo.initialize(); assertTrue(repo.read().workouts.isEmpty()); assertTrue(repo.read().sessions.isEmpty())
    }
    @Test fun checkpointRecoveryExactlyOnce() = runBlocking {
        repo.initialize(); repo.checkpoint(session()); assertTrue(repo.initialize()); assertFalse(repo.initialize())
        val data = repo.read(); assertEquals(1, data.sessions.size); assertEquals("INTERRUPTED", data.sessions[0].status)
    }
    @Test fun workoutRoundtrip() = runBlocking {
        repo.initialize(); val w = Workout(title = "Persistenza"); repo.save(w); assertEquals(w, repo.read().workouts.find { it.id == w.id })
    }
    @Test fun sqlAndHtmlPunctuationRemainPlainText() = runBlocking {
        repo.initialize()
        val title = "Studio 'mattina'; select pausa <b>testo</b>"
        val w = newWorkout("WORKOUT").copy(title = title)
        repo.save(w)
        assertEquals(3, repo.read().workouts.size)
        assertEquals(title, repo.read().workouts.single { it.id == w.id }.title)
        repo.finish(session()); assertEquals(1, repo.read().sessions.size)
    }
    @Test fun studioPersistenceAndCheckpointKeepMode() = runBlocking {
        repo.initialize(); val w = newQuickWorkout("STUDIO").copy(activities = listOf(StudyActivity(title = "Matematica", sessions = 2), StudyActivity(title = "Storia", sessions = 1)), restBetweenActivitiesSec = 120)
        repo.save(w); repo.checkpoint(session().copy(mode = "STUDIO", workoutId = w.id))
        assertTrue(repo.initialize()); assertFalse(repo.initialize())
        val studio = repo.read().forMode("STUDIO")
        assertEquals(listOf(w), studio.workouts); assertEquals("STUDIO", studio.sessions.single().mode)
        assertEquals(2, repo.read().forMode("WORKOUT").workouts.size)
    }
}
