package it.tabatastudio

import it.tabatastudio.domain.*
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class ModesTest {
    @Test fun studioFlatActivitiesAndBoundaries() {
        val w = newQuickWorkout("STUDIO")
        assertEquals(115 * 60000L, plannedMs(compileTimeline(w)))
        val simple = w.copy(activities = listOf(StudyActivity(concentrationSec = 60, sessions = 2, pauseSec = 120), StudyActivity(concentrationSec = 180, sessions = 1, pauseSec = 3600)), restBetweenActivitiesSec = 240)
        val phases = compileTimeline(simple)
        assertEquals(listOf("WORK", "REST_REP", "WORK", "REST_ACTIVITY", "WORK"), phases.map { it.type })
        assertEquals(listOf(60000L, 120000L, 60000L, 240000L, 180000L), phases.map { it.durationMs })
        assertEquals("Pausa Concentrazione — tra sessioni", phases[1].label)
        var clock = 0L; val timer = TimerEngine(simple, { clock }); timer.start(); clock = PREPARATION_MS + plannedMs(phases); timer.tick()
        assertEquals(300000L, timer.result!!.workMs); assertEquals(660000L, timer.result!!.plannedMs); assertEquals(3, timer.result!!.plannedWorkIntervals)
        assertThrows(Exception::class.java) { compileTimeline(w.copy(activities = emptyList())) }
        assertThrows(Exception::class.java) { compileTimeline(w.copy(activities = listOf(StudyActivity(concentrationSec = 61)))) }
        assertThrows(Exception::class.java) { compileTimeline(w.copy(restBetweenActivitiesSec = 61)) }
    }
    @Test fun workoutRestBelongsToEachExercise() {
        val exercises = (1..3).map { Exercise(title = "E$it", repetitions = 2, workSec = 10, restBetweenRepsSec = 5 + it, restAfterExerciseSec = it * 20) }
        val w = Workout(blocks = listOf(Block(exercises = exercises, restBetweenExercisesSec = 99)))
        val phases = compileTimeline(w)
        assertEquals(listOf(6000L, 7000L), phases.filter { it.type == "REST_EXERCISE" }.map { it.durationMs })
        assertEquals(3, phases.count { it.type == "REST_REP" }); assertEquals("WORK", phases.last().type)
        assertTrue(normalizationNotice(w).isNotEmpty()); assertEquals(99, normalizeWorkout(w).blocks[0].restBetweenExercisesSec)
        assertNull(w.timingVersion)
    }
    @Test fun studioPreparationAndSessionMode() {
        var clock = 0L; val timer = TimerEngine(newQuickWorkout("STUDIO"), { clock })
        val countdown = CountdownCues(); val transitions = TransitionCues(); timer.start()
        assertEquals(20000L, timer.remainingMs)
        for (second in listOf(3, 2, 1)) { clock = 20000L - second * 1000; timer.tick(); assertEquals("rest_$second", hornSample(countdown.read(timer)!!, timer.currentPhase!!.type)) }
        clock = 20000; timer.tick(); assertEquals("whistle", transitions.read(timer)); assertEquals(0L, timer.elapsedMs)
        clock += 1500000; timer.tick(); assertEquals("sigh", transitions.read(timer)); timer.stop()
        assertEquals("STUDIO", timer.result!!.mode); assertEquals("STUDIO", timer.snapshot().mode)
    }
    @Test fun oldDataRemainWorkoutAndViewsAreSeparate() {
        val legacy = parseBackup(javaClass.classLoader!!.getResource("backup-web-v1.json")!!.readText())
        assertTrue(legacy.workouts.all { it.mode == "WORKOUT" }); assertTrue(legacy.sessions.all { it.mode == "WORKOUT" })
        val studio = newQuickWorkout("STUDIO"); val session = legacy.sessions.first().copy(id = "studio-session", workoutId = studio.id, mode = "STUDIO")
        val all = Backup(legacy.workouts + studio, legacy.sessions + session)
        assertEquals(legacy, all.forMode("WORKOUT")); assertEquals(Backup(listOf(studio), listOf(session)), all.forMode("STUDIO"))
        assertEquals(all, parseBackup(backupJson(all)))
        val bad = workoutJson(studio).put("mode", "OTHER")
        assertThrows(Exception::class.java) { workoutFromJson(bad) }
        assertThrows(Exception::class.java) { sessionFromJson(sessionJson(session).put("mode", "OTHER")) }
    }
    @Test fun sharedStudioBackupRoundtripAndTimeline() {
        val backup = parseBackup(javaClass.classLoader!!.getResource("backup-modes-v1.json")!!.readText())
        assertEquals(14100000L, plannedMs(compileTimeline(backup.workouts.single())))
        assertEquals("STUDIO", backup.sessions.single().mode)
        assertTrue(normalizationNotice(backup.workouts.single()).isNotEmpty())
        val legacy = backup.workouts.single(); val normalized = normalizeWorkout(legacy)
        assertTrue(normalized.blocks.isEmpty()); assertEquals(0, normalized.longBreakSec); assertEquals(0, normalized.cooldownSec)
        assertEquals(900, legacy.longBreakSec)
        val repeated = legacy.copy(blocks = legacy.blocks.map { it.copy(cycles = 2) })
        assertEquals(2, normalizeWorkout(repeated).activities!!.size)
        assertEquals(backup, parseBackup(backupJson(backup)))
        File("build/interop/android-export-modes-v1.json").writeText(backupJson(backup))
    }
}
