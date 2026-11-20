package it.tabatastudio.domain

import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import kotlinx.serialization.json.Json

data class Backup(val workouts: List<Workout>, val sessions: List<Session>)
const val MAX_BACKUP_BYTES = 5 * 1024 * 1024
private const val MAX_SAFE = 9007199254740991L
fun isoNow(): String = Instant.now().truncatedTo(ChronoUnit.MILLIS).toString()
private fun JSONObject.string(key: String): String {
    require(has(key) && get(key) is String) { "$key: testo richiesto" }
    return getString(key)
}
private fun JSONObject.number(key: String, min: Long = 0, max: Long = MAX_SAFE): Long {
    val v = get(key)
    require(v is Number && v.toDouble().isFinite() && v.toDouble() == v.toLong().toDouble() && v.toDouble() >= min.toDouble() && v.toDouble() <= max.toDouble()) { "$key: intero non valido" }
    return v.toLong()
}
private fun JSONObject.int(key: String, min: Int = 0, max: Int = 3600): Int = number(key, min.toLong(), max.toLong()).toInt()
private fun JSONArray.objects(): List<JSONObject> = (0 until length()).map { getJSONObject(it) }
private fun checkId(value: String) { require(value.matches(Regex("[A-Za-z0-9_-]{1,128}"))) { "ID non valido" } }
private fun instant(value: String): Instant {
    require(value.matches(Regex("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{1,3})?Z"))) { "Data UTC richiesta" }
    val parsed = try { Instant.parse(value) } catch (e: DateTimeParseException) { throw IllegalArgumentException("Data non valida", e) }
    require(parsed.toString().take(19) == value.take(19)) { "Data non valida" }
    return parsed
}
fun workoutFromJson(o: JSONObject): Workout {
    val w = Workout(
        activities = if (o.has("activities")) o.getJSONArray("activities").objects().map { a -> StudyActivity(a.string("id"), a.string("title"), a.int("concentrationSec", 60), a.int("sessions", 1, 100), a.int("pauseSec")) } else null,
        restBetweenActivitiesSec = if (o.has("activities") || o.has("restBetweenActivitiesSec")) o.int("restBetweenActivitiesSec") else 300,
        mode = if (o.has("mode")) o.string("mode") else "WORKOUT", longBreakSec = if (o.has("longBreakSec")) o.int("longBreakSec") else 0, longBreakEvery = if (o.has("longBreakEvery")) o.int("longBreakEvery", 2, 100) else 4, schemaVersion = o.int("schemaVersion", 1, 1), id = o.string("id"), title = o.string("title"), description = o.string("description"), color = if (o.has("color")) o.string("color") else "#9153b7", warmupSec = o.int("warmupSec"), cooldownSec = o.int("cooldownSec"), blocks = o.getJSONArray("blocks").objects().map { b ->
        Block(restBetweenExercisesSec = if (b.has("restBetweenExercisesSec")) b.int("restBetweenExercisesSec") else null, id = b.string("id"), title = b.string("title"), cycles = b.int("cycles", 1, 100), restBetweenCyclesSec = b.int("restBetweenCyclesSec"), restAfterBlockSec = b.int("restAfterBlockSec"), exercises = b.getJSONArray("exercises").objects().map { e ->
            Exercise(id = e.string("id"), title = e.string("title"), repetitions = e.int("repetitions", 1, 100), workSec = e.int("workSec", 1), restBetweenRepsSec = e.int("restBetweenRepsSec"), restAfterExerciseSec = e.int("restAfterExerciseSec"))
        })
    })
    compileTimeline(w)
    return w
}
fun workoutJson(w: Workout): JSONObject = JSONObject().put("schemaVersion", 1).put("id", w.id).put("title", w.title).put("description", w.description).put("color", w.color).put("warmupSec", w.warmupSec).put("cooldownSec", w.cooldownSec).put("blocks", JSONArray(w.blocks.map { b ->
    JSONObject().also { if (b.restBetweenExercisesSec != null) it.put("restBetweenExercisesSec", b.restBetweenExercisesSec) }.put("id", b.id).put("title", b.title).put("cycles", b.cycles).put("restBetweenCyclesSec", b.restBetweenCyclesSec).put("restAfterBlockSec", b.restAfterBlockSec).put("exercises", JSONArray(b.exercises.map { e -> JSONObject().put("id", e.id).put("title", e.title).put("repetitions", e.repetitions).put("workSec", e.workSec).put("restBetweenRepsSec", e.restBetweenRepsSec).put("restAfterExerciseSec", e.restAfterExerciseSec) }))
})).also { o ->
    if (w.studio) {
        o.put("mode", w.mode)
        if (w.activities != null) o.put("activities", JSONArray(w.activities.map { a -> JSONObject().put("id", a.id).put("title", a.title).put("concentrationSec", a.concentrationSec).put("sessions", a.sessions).put("pauseSec", a.pauseSec) })).put("restBetweenActivitiesSec", w.restBetweenActivitiesSec)
        else o.put("longBreakSec", w.longBreakSec).put("longBreakEvery", w.longBreakEvery)
    }
}
fun sessionJson(s: Session): JSONObject = JSONObject().put("id", s.id).put("workoutId", s.workoutId).put("workoutTitle", s.workoutTitle).put("startedAt", s.startedAt).put("endedAt", s.endedAt).put("plannedMs", s.plannedMs).put("elapsedMs", s.elapsedMs).put("workMs", s.workMs).put("completedWorkIntervals", s.completedWorkIntervals).put("plannedWorkIntervals", s.plannedWorkIntervals).put("pauses", s.pauses).put("status", s.status).also { if (s.studio) it.put("mode", s.mode); if (s.completedPhases != null && s.plannedPhases != null) { it.put("completedPhases", s.completedPhases); it.put("plannedPhases", s.plannedPhases) } }
fun sessionFromJson(o: JSONObject): Session {
    val s = Session(mode = if (o.has("mode")) o.string("mode") else "WORKOUT", id = o.string("id"), workoutId = o.string("workoutId"), workoutTitle = if (o.has("workoutTitle")) o.string("workoutTitle") else "", startedAt = o.string("startedAt"), endedAt = o.string("endedAt"), plannedMs = o.number("plannedMs", 1, 86400000), elapsedMs = o.number("elapsedMs"), workMs = o.number("workMs"), completedWorkIntervals = o.int("completedWorkIntervals", 0, 10000), plannedWorkIntervals = o.int("plannedWorkIntervals", 1, 10000), pauses = o.number("pauses", 0, Int.MAX_VALUE.toLong()).toInt(), status = o.string("status"))
    require(s.mode in listOf("WORKOUT", "STUDIO")) { "Modalità sessione non valida" }
    checkId(s.id); checkId(s.workoutId)
    require(s.workoutTitle.length <= 120 && instant(s.endedAt) >= instant(s.startedAt) && s.workMs <= s.elapsedMs && s.completedWorkIntervals <= s.plannedWorkIntervals) { "Metriche sessione non valide" }
    require(s.status in listOf("COMPLETED", "INTERRUPTED") && (s.status != "COMPLETED" || s.completedWorkIntervals == s.plannedWorkIntervals)) { "Stato sessione non valido" }
    if (o.has("completedPhases") || o.has("plannedPhases")) return s.copy(plannedPhases = o.int("plannedPhases", s.plannedWorkIntervals, 10000), completedPhases = o.int("completedPhases", s.completedWorkIntervals, o.int("plannedPhases", s.plannedWorkIntervals, 10000)))
    return s
}
fun parseBackup(raw: String): Backup {
    require(raw.toByteArray(Charsets.UTF_8).size <= MAX_BACKUP_BYTES) { "Backup massimo 5 MiB" }
    Json.parseToJsonElement(raw) // Reject lenient org.json extensions (single quotes, trailing tokens).
    val o = JSONObject(raw); require(o.int("schemaVersion", 1, 1) == 1); instant(o.string("exportedAt"))
    val wa = o.getJSONArray("workouts"); val sa = o.getJSONArray("sessions")
    require(wa.length() <= 500 && sa.length() <= 10000) { "Archivio troppo grande" }
    val w = wa.objects().map(::workoutFromJson); val s = sa.objects().map(::sessionFromJson)
    require(w.map { it.id }.distinct().size == w.size && s.map { it.id }.distinct().size == s.size) { "ID duplicato nel backup" }
    return Backup(w, s)
}
fun backupJson(backup: Backup): String = JSONObject().put("schemaVersion", 1).put("exportedAt", isoNow()).put("workouts", JSONArray(backup.workouts.map(::workoutJson))).put("sessions", JSONArray(backup.sessions.map(::sessionJson))).toString(2)
fun sessionsCsv(sessions: List<Session>): String {
    val fields = listOf("id", "mode", "workoutId", "workoutTitle", "startedAt", "endedAt", "status", "plannedMs", "elapsedMs", "workMs", "completedWorkIntervals", "plannedWorkIntervals", "completedPhases", "plannedPhases", "pauses")
    fun cell(value: String): String { val safe = if (value.firstOrNull() in listOf('=', '+', '-', '@', '\t', '\r')) "'$value" else value; return "\"${safe.replace("\"", "\"\"")}\"" }
    return "\uFEFF" + (listOf(fields.map(::cell).joinToString(",")) + sessions.map { s -> val json = sessionJson(s); fields.joinToString(",") { cell(if (it == "mode") s.mode else json.optString(it)) } }).joinToString("\r\n")
}
