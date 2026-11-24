package it.tabatastudio

import it.tabatastudio.domain.*
import org.json.JSONArray
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class WorkoutRecoveryTest {
    private fun cases() = JSONArray(javaClass.classLoader!!.getResource("workout-recovery-v2.json")!!.readText())
    private fun demo(): Workout {
        val rows = cases()
        return (0 until rows.length()).map { rows.getJSONObject(it) }.first { it.getString("name") == "demo-completa" }.getJSONObject("workout").let(::workoutFromJson)
    }
    @Test fun sharedRecoveryFixturesAndMetadata() {
        val rows = cases()
        repeat(rows.length()) { i ->
            val row = rows.getJSONObject(i); val w = workoutFromJson(row.getJSONObject("workout")); val before = workoutJson(w).toString()
            val phases = compileTimeline(w)
            assertEquals(row.getString("name"), row.getLong("expectedMs"), plannedMs(phases))
            assertEquals(row.getJSONArray("expectedTypes").toString(), JSONArray(phases.map { it.type }).toString())
            assertEquals(before, workoutJson(w).toString()); assertFalse(phases.any { it.type == "REST_CYCLE" })
            assertEquals("ding1", phases.first { it.type == "WORK" }.entryCue)
        }
    }
    @Test fun strictNewInputsAndLegacyCopyPreserveRawData() {
        val base = newWorkout("WORKOUT")
        val invalid = listOf(
            base.copy(cooldownSec = 9),
            base.copy(blocks = listOf(base.blocks[0].copy(exercises = listOf(base.blocks[0].exercises[0].copy(workSec = 4))))),
            base.copy(blocks = listOf(base.blocks[0].copy(exercises = listOf(base.blocks[0].exercises[0].copy(restBetweenRepsSec = 3))))),
            base.copy(blocks = listOf(base.blocks[0].copy(restAfterBlockSec = 0), newBlock()))
        )
        invalid.forEach { w ->
            assertThrows(IllegalArgumentException::class.java) { compileTimeline(w) }
            val legacy = w.copy(timingVersion = null)
            assertTrue(compileTimeline(legacy).isNotEmpty()); assertTrue(normalizationNotice(legacy).isNotEmpty())
            assertEquals(legacy, workoutFromJson(workoutJson(legacy))); assertNull(legacy.timingVersion)
            assertEquals(2, normalizeWorkout(legacy).timingVersion)
        }
        assertTrue(compileTimeline(base.copy(blocks = listOf(base.blocks[0].copy(exercises = listOf(Exercise(workSec = 6, restBetweenRepsSec = 7)))))).isNotEmpty())
    }
    @Test fun completeDemoEventsAndStatistics() {
        var clock = 0L; val engine = TimerEngine(demo(), { clock }); val cues = TransitionCues(); val countdown = CountdownCues(); engine.start()
        val events = mutableListOf<Pair<Long, String>>()
        while (clock <= 95000) {
            engine.tick(); cues.read(engine)?.let { events.add(clock to it) }
            val second = countdown.read(engine)
            if (clock in 85000L until 95000L) assertNull(second)
            clock += 100
        }
        assertEquals(listOf(20000L to "ding1",25000L to "sigh",30000L to "ding1",35000L to "sigh",40000L to "ding1",45000L to "sigh",50000L to "ding2",55000L to "sigh",60000L to "ding1",65000L to "sigh",70000L to "ding1",75000L to "double_sigh",80000L to "ding2",85000L to "ding3",95000L to "ding3"),events)
        assertEquals(35000L,engine.result!!.workMs);assertEquals(75000L,engine.result!!.elapsedMs);assertEquals(7,engine.result!!.completedWorkIntervals)
        File("build/interop").mkdirs()
        File("build/interop/android-export-recovery-v2.json").writeText(backupJson(Backup(listOf(demo()),listOf(engine.result!!))))
    }
    @Test fun countdownFocusWindowIncludesNextCueAndFinalTail() {
        var clock=0L; val engine=TimerEngine(demo(),{clock});engine.start()
        clock=17000;engine.tick();assertEquals(4050L,cueFocusDuration(engine,"rest_3",3))
        clock=47000;engine.tick();assertEquals(4485L,cueFocusDuration(engine,"rest_3",3))
        clock=82000;engine.tick();assertEquals(4920L,cueFocusDuration(engine,"work_3",3))
        assertEquals(1920L,cueFocusDuration(engine,"ding3",null))
    }
    @Test fun skipsAndManualStopNeverAnnounceNaturalFinals() {
        var clock=0L;val engine=TimerEngine(newWorkout("WORKOUT").copy(cooldownSec=10),{clock});engine.start()
        clock=20000;engine.tick();engine.skip();assertNull(TransitionCues().read(engine));engine.skip();assertNull(TransitionCues().read(engine))
        val stopped=TimerEngine(newWorkout("WORKOUT"),{clock});stopped.start();stopped.stop();assertNull(TransitionCues().read(stopped))
    }
}
