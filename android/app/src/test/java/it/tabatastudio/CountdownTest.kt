package it.tabatastudio

import it.tabatastudio.domain.*
import org.junit.Assert.*
import org.junit.Test

class CountdownTest {
    private var clock = 0L
    private val timer = TimerEngine(Workout(blocks = listOf(Block(exercises = listOf(Exercise(workSec = 5))))), { clock })
    private val cues = CountdownCues()
    private fun advance(ms: Long) { clock += ms; timer.tick() }

    @Test fun preparation20ThenWork() {
        timer.start(); assertEquals("PREPARE", timer.currentPhase!!.type); assertEquals(20000L, timer.remainingMs)
        advance(19999); assertEquals(-1, timer.index); assertEquals(0L, timer.elapsedMs); assertEquals(0L, timer.workMs)
        advance(1); assertEquals(0, timer.index); assertEquals(5000L, timer.remainingMs)
        advance(1000); assertEquals(1000L, timer.workMs)
    }
    @Test fun pausePreparationAndSkip() {
        timer.start(); advance(5000); timer.pause(); advance(30000); assertEquals(15000L, timer.remainingMs)
        timer.resume(); timer.skip(); assertEquals(0, timer.index); assertEquals(0L, timer.elapsedMs)
        timer.previous(); assertEquals(0, timer.index)
    }
    @Test fun stopPreparationDoesNotCreditWork() {
        timer.start(); advance(5000); timer.stop(); assertEquals("INTERRUPTED", timer.result!!.status)
        assertEquals(0L, timer.result!!.elapsedMs); assertEquals(0, timer.result!!.completedPhases)
    }
    @Test fun catchupPreservesWorkoutTotals() {
        timer.start(); advance(99999); assertEquals(5000L, timer.result!!.elapsedMs); assertEquals(5000L, timer.result!!.workMs); assertEquals(1, timer.result!!.completedPhases)
    }
    @Test fun cues321OnlyOnceAndPauseDoesNotRepeat() {
        timer.start(); assertNull(cues.read(timer)); advance(17000); assertEquals(3, cues.read(timer)); assertNull(cues.read(timer))
        timer.pause(); advance(10000); assertNull(cues.read(timer)); timer.resume(); assertNull(cues.read(timer))
        advance(1000); assertEquals(2, cues.read(timer)); advance(1000); assertEquals(1, cues.read(timer))
        timer.stop(); assertNull(cues.read(timer))
    }
    @Test fun delayedCuesAndReplay() {
        timer.start(); advance(19000); assertEquals(1, cues.read(timer)); assertNull(cues.read(timer))
        advance(3000); assertEquals(3, cues.read(timer)); timer.previous(); assertNull(cues.read(timer)); advance(2000); assertEquals(3, cues.read(timer))
    }
    @Test fun workAndRecoveryUseOneNoteForEachSecond() {
        assertEquals(listOf("work_3", "work_2", "work_1"), listOf(3, 2, 1).map { hornSample(it, "WORK") })
        for (phase in listOf("REST_REP", "REST_EXERCISE", "REST_CYCLE", "REST_BLOCK", "COOLDOWN", "WARMUP")) {
            assertEquals(listOf("rest_3", "rest_2", "rest_1"), listOf(3, 2, 1).map { hornSample(it, phase) })
        }
    }
    @Test fun preparationHasThreeNotesAndNoFourthCueAtZero() {
        assertEquals(listOf("rest_3", "rest_2", "rest_1"), listOf(3, 2, 1).map { hornSample(it, "PREPARE") })
        for (phase in listOf("WORK", "COOLDOWN", "PREPARE")) for (n in listOf(0, 4, -1)) assertNull(hornSample(n, phase))
    }
}
