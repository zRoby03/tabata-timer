package it.tabatastudio

import it.tabatastudio.domain.*
import org.json.*
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant
import java.io.File

class ContractTest {
    @Test fun quickTimerKeepsTimelineSemantics() {
        val w = newQuickWorkout()
        assertEquals(230000L, plannedMs(compileTimeline(w)))
        val b = w.blocks[0]
        val reference = w.copy(blocks = listOf(b.copy(cycles = 2, exercises = listOf(b.exercises[0].copy(repetitions = 3)))))
        assertEquals(170000L, plannedMs(compileTimeline(reference)))
    }
    private fun resource(name: String) = javaClass.classLoader!!.getResource(name)!!.readText()
    private fun engine(): Pair<TimerEngine, (Long) -> Unit> {
        var clock = 0L
        val w = Workout(blocks = listOf(Block(exercises = listOf(Exercise(workSec = 5, repetitions = 2, restBetweenRepsSec = 5)))))
        val t = TimerEngine(w, { clock }, { Instant.ofEpochMilli(clock).toString() }); t.start(); clock += PREPARATION_MS; t.tick()
        return t to { ms: Long -> clock += ms; t.tick() }
    }
    @Test fun sharedTimelineFixtures() {
        val cases = JSONArray(resource("timeline-cases.json"))
        repeat(cases.length()) { i -> val c = cases.getJSONObject(i); val phases = compileTimeline(workoutFromJson(c.getJSONObject("workout")))
            assertEquals(c.getLong("expectedMs"), plannedMs(phases)); assertEquals(c.getJSONArray("expectedTypes").toString(), JSONArray(phases.map { it.type }).toString())
        }
    }
    @Test fun timerCatchup() { val (t, advance) = engine(); advance(99999); assertEquals(15000L, t.result!!.elapsedMs); assertEquals(10000L, t.result!!.workMs); assertEquals("COMPLETED", t.result!!.status); assertEquals(3, t.result!!.completedPhases); assertEquals(3, t.result!!.plannedPhases) }
    @Test fun timerPause() { val (t, advance) = engine(); advance(700); t.pause(); advance(10000); assertEquals(4300L, t.remainingMs); t.resume(); advance(4300); assertEquals(1, t.index); assertEquals(5000L, t.elapsedMs); assertEquals(1, t.pauses) }
    @Test fun timerSkip() { val (t, advance) = engine(); advance(500); t.skip(); t.skip(); advance(5000); assertEquals(5500L, t.result!!.workMs); assertEquals(1, t.result!!.completedWorkIntervals); assertEquals("INTERRUPTED", t.result!!.status) }
    @Test fun timerPrevious() { val (t, advance) = engine(); advance(5000); t.previous(); advance(15000); assertEquals(15000L, t.result!!.workMs); assertEquals(2, t.result!!.completedWorkIntervals) }
    @Test fun stopIdempotent() { val (t, advance) = engine(); advance(1000); t.stop(); val s = t.result; t.stop(); assertSame(s, t.result); assertEquals("INTERRUPTED", s!!.status) }
    @Test fun jsonRoundtrip() {
        val (t, advance) = engine(); advance(15000)
        val backup = Backup(presets(), listOf(t.result!!)); assertEquals(backup, parseBackup(backupJson(backup)))
    }
    @Test fun webFixtureAndAndroidExport() {
        val backup = parseBackup(resource("backup-web-v1.json")); assertEquals(170000L, plannedMs(compileTimeline(backup.workouts.first())))
        val out = File("build/interop/android-export-v1.json"); out.parentFile!!.mkdirs(); out.writeText(backupJson(backup))
    }
    @Test fun simpleModelsRoundtripAcrossPlatforms() {
        val backup = parseBackup(resource("backup-simple-v1.json"))
        assertEquals(85000L, plannedMs(compileTimeline(backup.workouts[0])))
        assertEquals(660000L, plannedMs(compileTimeline(backup.workouts[1])))
        assertEquals(backup, parseBackup(backupJson(backup)))
        val out = File("build/interop/android-export-simple-v1.json"); out.parentFile!!.mkdirs(); out.writeText(backupJson(backup))
    }
    @Test fun rejectsMalformedNumbersAndSchema() {
        val w = JSONObject(resource("backup-web-v1.json")).getJSONArray("workouts").getJSONObject(0)
        listOf(-1, 0.5, "10", JSONObject.NULL, 3601).forEach { v -> val bad = JSONObject(w.toString()).put("warmupSec", v); assertThrows(Exception::class.java) { workoutFromJson(bad) } }
        assertThrows(Exception::class.java) { parseBackup("{}") }
        assertThrows(Exception::class.java) { parseBackup(resource("backup-web-v1.json") + " trailing") }
        assertThrows(Exception::class.java) { parseBackup("{'schemaVersion':1}") }
        val backup = JSONObject(resource("backup-web-v1.json")).put("schemaVersion", 2)
        assertThrows(Exception::class.java) { parseBackup(backup.toString()) }
    }
    @Test fun rejectsDuplicateAndInvalidSession() {
        val raw = resource("backup-web-v1.json"); val backup = JSONObject(raw); val sessions = backup.getJSONArray("sessions"); sessions.put(sessions.getJSONObject(0))
        assertThrows(Exception::class.java) { parseBackup(backup.toString()) }
        val invalid = JSONObject(raw); invalid.getJSONArray("sessions").getJSONObject(0).put("workMs", 9999999)
        assertThrows(Exception::class.java) { parseBackup(invalid.toString()) }
        val badDate = JSONObject(raw); badDate.getJSONArray("sessions").getJSONObject(0).put("startedAt", "2026-02-30T10:00:00Z")
        assertThrows(Exception::class.java) { parseBackup(badDate.toString()) }
    }
    @Test fun statsUtcAndFilter() {
        val backup = parseBackup(resource("backup-web-v1.json")); val stats = aggregate(backup.sessions, 7, "", Instant.parse("2026-11-17T12:00:00Z"))
        assertEquals(1, stats.sessions.size); assertEquals(50.0, stats.completion, 0.001); assertEquals(1, stats.weeks.last().count); assertEquals(0, aggregate(backup.sessions, 7, "other", Instant.parse("2026-11-17T12:00:00Z")).sessions.size)
    }
}
