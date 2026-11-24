package it.tabatastudio

import it.tabatastudio.domain.*
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class SecurityTest {
    private fun backup() = JSONObject(backupJson(Backup(listOf(newWorkout("WORKOUT")), emptyList())))
    @Test fun rejectsHostileIdsColorsAndNumbers() {
        for ((key, value) in listOf("id" to "../escape", "id" to "x\" id=\"example", "color" to "#fff;background:url(https://example.invalid)", "warmupSec" to 1e100, "warmupSec" to "5", "warmupSec" to 5.5)) {
            val b = backup(); b.getJSONArray("workouts").getJSONObject(0).put(key, value)
            assertThrows(Exception::class.java) { parseBackup(b.toString()) }
        }
    }
    @Test fun rejectsByteLimitCollectionsAndDuplicateIds() {
        assertThrows(Exception::class.java) { parseBackup("é".repeat(MAX_BACKUP_BYTES / 2 + 1)) }
        val b = backup(); val workouts = b.getJSONArray("workouts"); val w = workouts.getJSONObject(0)
        repeat(500) { workouts.put(w) }
        assertThrows(Exception::class.java) { parseBackup(b.toString()) }
        val duplicate = backup(); val exercises = duplicate.getJSONArray("workouts").getJSONObject(0).getJSONArray("blocks").getJSONObject(0).getJSONArray("exercises")
        exercises.put(exercises.getJSONObject(0))
        assertThrows(Exception::class.java) { parseBackup(duplicate.toString()) }
    }
    @Test fun csvGuardsFormulaPrefixes() {
        val session = Session("s", "w", "Test", "2026-11-17T10:00:00.000Z", "2026-11-17T10:00:01.000Z", 2000, 1000, 1000, 0, 1, 0, "INTERRUPTED")
        for (prefix in listOf("=", "+", "-", "@", "\t", "\r", "\n", "  =", "　＋", "＠", "　－", "＝", "\uFEFF-")) {
            assertTrue(sessionsCsv(listOf(session.copy(workoutTitle = prefix + "1+1"))).contains("\"'" + prefix + "1+1\""))
        }
    }
    @Test fun deeplyNestedUnknownJsonDoesNotCrashTheVm() {
        val raw = "{\"schemaVersion\":1,\"exportedAt\":\"2026-11-24T10:00:00Z\",\"workouts\":[],\"sessions\":[],\"unknown\":" + "[".repeat(2000) + "0" + "]".repeat(2000) + "}"
        val error = assertThrows(IllegalArgumentException::class.java) { parseBackup(raw) }
        assertEquals("JSON troppo annidato (massimo 64 livelli)", error.message)
    }
}
