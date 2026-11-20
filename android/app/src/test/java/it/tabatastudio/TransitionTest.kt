package it.tabatastudio

import it.tabatastudio.domain.*
import org.junit.Assert.*
import org.junit.Test

class TransitionTest {
    private var clock = 0L
    private fun timer(cooldown: Int = 4) = TimerEngine(Workout(cooldownSec = cooldown, blocks = listOf(Block(exercises = listOf(Exercise(workSec = 4, repetitions = 2, restBetweenRepsSec = 4))))), { clock }).also { it.start() }
    private fun advance(t: TimerEngine, ms: Long) { clock += ms; t.tick() }
    @Test fun whistleOnlyAtWorkStartNotAfterPause() {
        val t = timer(); val cues = TransitionCues(); assertNull(cues.read(t)); advance(t, 20000)
        assertEquals("whistle", cues.read(t)); assertNull(cues.read(t)); t.pause(); assertNull(cues.read(t)); advance(t, 5000); t.resume(); assertNull(cues.read(t))
    }
    @Test fun sighThenNextWhistle() {
        val t = timer(); val cues = TransitionCues(); advance(t, 20000); cues.read(t); advance(t, 4000)
        assertEquals("sigh", cues.read(t)); assertNull(cues.read(t)); advance(t, 4000); assertEquals("whistle", cues.read(t))
    }
    @Test fun bellOnlyOnceBeforeCooldown() {
        val t = timer(); val cues = TransitionCues(); advance(t, 32000); assertEquals("finish", cues.read(t))
        t.previous(); assertEquals("whistle", cues.read(t)); t.skip(); assertNull(cues.read(t)); advance(t, 4000); assertNull(cues.read(t))
    }
    @Test fun noCooldownBellAndNoBellOnManualStop() {
        val natural = timer(0); val cues = TransitionCues(); advance(natural, 32000); assertEquals("finish", cues.read(natural)); assertNull(cues.read(natural))
        val stopped = timer(); advance(stopped, 20000); cues.read(stopped); stopped.stop(); assertNull(cues.read(stopped))
    }
    @Test fun pausedSkipWaitsForResumeAndPreviousIsNewVisit() {
        val t = timer(); val cues = TransitionCues(); advance(t, 20000); cues.read(t); t.pause(); t.skip(); assertNull(cues.read(t))
        t.resume(); assertEquals("sigh", cues.read(t)); t.previous(); assertEquals("whistle", cues.read(t))
    }
    @Test fun delayedPollingDoesNotReplayOldWhistles() {
        val t = timer(); val cues = TransitionCues(); advance(t, 22000); assertNull(cues.read(t)); advance(t, 2000); assertEquals("sigh", cues.read(t))
        val other = timer(); advance(other, 20000); assertEquals("whistle", cues.read(other))
    }
    @Test fun raisedVolumeAppliesOnlyToWhistleAndSigh() {
        assertEquals(1f, cueVolume("whistle"), 0f) // PCM already amplified to 120%.
        assertEquals(0.85f, cueVolume("sigh"), 0f)
        for (key in listOf("finish", "work_3", "rest_3")) assertEquals(0.65f, cueVolume(key), 0f)
    }
}
