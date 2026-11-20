// DOC L0001: Assegna il file al namespace it.tabatastudio.domain, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.domain

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import org.json.JSONArray. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.json.JSONArray
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import org.json.JSONObject. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.json.JSONObject
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import java.time.Instant. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import java.time.Instant
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import java.time.format.DateTimeParseException. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import java.time.format.DateTimeParseException
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import java.time.temporal.ChronoUnit. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import java.time.temporal.ChronoUnit
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import kotlinx.serialization.json.Json. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import kotlinx.serialization.json.Json

// DOC L0010: Dichiara il tipo o contenitore di questo modulo: Serializzazione e validazione interoperabile Kotlin/JSON. I campi e i metodi sono descritti nelle righe successive.
data class Backup(val workouts: List<Workout>, val sessions: List<Session>)
// DOC L0011: val: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const val MAX_BACKUP_BYTES = 5 * 1024 * 1024
// DOC L0012: Esegue il passaggio esplicito «private const val MAX_SAFE = 9007199254740991L» nel flusso: Serializzazione e validazione interoperabile Kotlin/JSON.
private const val MAX_SAFE = 9007199254740991L
// DOC L0013: isoNow: Produce una data UTC ISO con precisione al millisecondo.
fun isoNow(): String = Instant.now().truncatedTo(ChronoUnit.MILLIS).toString()
// DOC L0014: string: Legge dal JSON una proprietà obbligatoria e ne controlla il tipo testo.
private fun JSONObject.string(key: String): String {
    // DOC L0015: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di string.
    require(has(key) && get(key) is String) { "$key: testo richiesto" }
    // DOC L0016: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di string.
    return getString(key)
// DOC L0017: Chiude il costrutto aperto alla riga 14.
}
// DOC L0018: number: Legge un numero JSON finito, intero e compreso tra i limiti.
private fun JSONObject.number(key: String, min: Long = 0, max: Long = MAX_SAFE): Long {
    // DOC L0019: v: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di number.
    val v = get(key)
    // DOC L0020: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di number.
    require(v is Number && v.toDouble().isFinite() && v.toDouble() == v.toLong().toDouble() && v.toDouble() >= min.toDouble() && v.toDouble() <= max.toDouble()) { "$key: intero non valido" }
    // DOC L0021: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di number.
    return v.toLong()
// DOC L0022: Chiude il costrutto aperto alla riga 18.
}
// DOC L0023: int: Converte il numero JSON validato nel tipo Int entro i limiti del campo.
private fun JSONObject.int(key: String, min: Int = 0, max: Int = 3600): Int = number(key, min.toLong(), max.toLong()).toInt()
// DOC L0024: objects: Converte gli elementi di JSONArray in una lista di JSONObject.
private fun JSONArray.objects(): List<JSONObject> = (0 until length()).map { getJSONObject(it) }
// DOC L0025: checkId: Rifiuta ID JSON non conformi al contratto condiviso.
private fun checkId(value: String) { require(value.matches(Regex("[A-Za-z0-9_-]{1,128}"))) { "ID non valido" } }
// DOC L0026: instant: Valida una data ISO UTC e la converte in Instant, impedendo normalizzazioni silenziose di date non valide.
private fun instant(value: String): Instant {
    // DOC L0027: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di instant.
    require(value.matches(Regex("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{1,3})?Z"))) { "Data UTC richiesta" }
    // DOC L0028: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. parsed: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di instant.
    val parsed = try { Instant.parse(value) } catch (e: DateTimeParseException) { throw IllegalArgumentException("Data non valida", e) }
    // DOC L0029: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di instant.
    require(parsed.toString().take(19) == value.take(19)) { "Data non valida" }
    // DOC L0030: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di instant.
    return parsed
// DOC L0031: Chiude il costrutto aperto alla riga 26.
}
// DOC L0032: workoutFromJson: Ricostruisce un programma dal JSON, applica i valori legacy previsti e valida la timeline.
fun workoutFromJson(o: JSONObject): Workout {
    // DOC L0033: w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di workoutFromJson.
    val w = Workout(
        // DOC L0034: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di workoutFromJson.
        activities = if (o.has("activities")) o.getJSONArray("activities").objects().map { a -> StudyActivity(a.string("id"), a.string("title"), a.int("concentrationSec", 60), a.int("sessions", 1, 100), a.int("pauseSec")) } else null,
        // DOC L0035: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di workoutFromJson.
        restBetweenActivitiesSec = if (o.has("activities") || o.has("restBetweenActivitiesSec")) o.int("restBetweenActivitiesSec") else 300,
        // DOC L0036: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di workoutFromJson.
        mode = if (o.has("mode")) o.string("mode") else "WORKOUT", longBreakSec = if (o.has("longBreakSec")) o.int("longBreakSec") else 0, longBreakEvery = if (o.has("longBreakEvery")) o.int("longBreakEvery", 2, 100) else 4, schemaVersion = o.int("schemaVersion", 1, 1), id = o.string("id"), title = o.string("title"), description = o.string("description"), color = if (o.has("color")) o.string("color") else "#9153b7", warmupSec = o.int("warmupSec"), cooldownSec = o.int("cooldownSec"), blocks = o.getJSONArray("blocks").objects().map { b ->
        // DOC L0037: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di workoutFromJson.
        Block(restBetweenExercisesSec = if (b.has("restBetweenExercisesSec")) b.int("restBetweenExercisesSec") else null, id = b.string("id"), title = b.string("title"), cycles = b.int("cycles", 1, 100), restBetweenCyclesSec = b.int("restBetweenCyclesSec"), restAfterBlockSec = b.int("restAfterBlockSec"), exercises = b.getJSONArray("exercises").objects().map { e ->
            // DOC L0038: Esegue il passaggio esplicito «Exercise(id = e.string("id"), title = e.string("title"), repetitions = e.int("repetitions", 1, 100), workSec = e.int("workSec", 1), restBetweenRepsSec = e.int("restBetweenRepsSec")» nel flusso: Serializzazione e validazione interoperabile Kotlin/JSON. Nel contesto di workoutFromJson.
            Exercise(id = e.string("id"), title = e.string("title"), repetitions = e.int("repetitions", 1, 100), workSec = e.int("workSec", 1), restBetweenRepsSec = e.int("restBetweenRepsSec"), restAfterExerciseSec = e.int("restAfterExerciseSec"))
        // DOC L0039: Chiude il costrutto aperto alla riga 37.
        })
    // DOC L0040: Chiude il costrutto aperto alla riga 36.
    })
    // DOC L0041: Esegue il passaggio esplicito «compileTimeline(w)» nel flusso: Serializzazione e validazione interoperabile Kotlin/JSON. Nel contesto di workoutFromJson.
    compileTimeline(w)
    // DOC L0042: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di workoutFromJson.
    return w
// DOC L0043: Chiude il costrutto aperto alla riga 32.
}
// DOC L0044: workoutJson: Serializza il modello mantenendo i campi necessari all’interoperabilità e alla compatibilità.
fun workoutJson(w: Workout): JSONObject = JSONObject().put("schemaVersion", 1).put("id", w.id).put("title", w.title).put("description", w.description).put("color", w.color).put("warmupSec", w.warmupSec).put("cooldownSec", w.cooldownSec).put("blocks", JSONArray(w.blocks.map { b ->
    // DOC L0045: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di workoutJson.
    JSONObject().also { if (b.restBetweenExercisesSec != null) it.put("restBetweenExercisesSec", b.restBetweenExercisesSec) }.put("id", b.id).put("title", b.title).put("cycles", b.cycles).put("restBetweenCyclesSec", b.restBetweenCyclesSec).put("restAfterBlockSec", b.restAfterBlockSec).put("exercises", JSONArray(b.exercises.map { e -> JSONObject().put("id", e.id).put("title", e.title).put("repetitions", e.repetitions).put("workSec", e.workSec).put("restBetweenRepsSec", e.restBetweenRepsSec).put("restAfterExerciseSec", e.restAfterExerciseSec) }))
// DOC L0046: Esegue il passaggio esplicito «})).also { o ->» nel flusso: Serializzazione e validazione interoperabile Kotlin/JSON. Nel contesto di workoutJson.
})).also { o ->
    // DOC L0047: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di workoutJson.
    if (w.studio) {
        // DOC L0048: Esegue il passaggio esplicito «o.put("mode", w.mode)» nel flusso: Serializzazione e validazione interoperabile Kotlin/JSON. Nel contesto di workoutJson.
        o.put("mode", w.mode)
        // DOC L0049: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di workoutJson.
        if (w.activities != null) o.put("activities", JSONArray(w.activities.map { a -> JSONObject().put("id", a.id).put("title", a.title).put("concentrationSec", a.concentrationSec).put("sessions", a.sessions).put("pauseSec", a.pauseSec) })).put("restBetweenActivitiesSec", w.restBetweenActivitiesSec)
        // DOC L0050: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di workoutJson.
        else o.put("longBreakSec", w.longBreakSec).put("longBreakEvery", w.longBreakEvery)
    // DOC L0051: Chiude il costrutto aperto alla riga 47.
    }
// DOC L0052: Chiude il costrutto aperto alla riga 46.
}
// DOC L0053: sessionJson: Converte la sessione in JSONObject, includendo modalità e metriche delle fasi quando disponibili.
fun sessionJson(s: Session): JSONObject = JSONObject().put("id", s.id).put("workoutId", s.workoutId).put("workoutTitle", s.workoutTitle).put("startedAt", s.startedAt).put("endedAt", s.endedAt).put("plannedMs", s.plannedMs).put("elapsedMs", s.elapsedMs).put("workMs", s.workMs).put("completedWorkIntervals", s.completedWorkIntervals).put("plannedWorkIntervals", s.plannedWorkIntervals).put("pauses", s.pauses).put("status", s.status).also { if (s.studio) it.put("mode", s.mode); if (s.completedPhases != null && s.plannedPhases != null) { it.put("completedPhases", s.completedPhases); it.put("plannedPhases", s.plannedPhases) } }
// DOC L0054: sessionFromJson: Ricostruisce una sessione e controlla ordine delle date, stato, modalità e coerenza dei contatori.
fun sessionFromJson(o: JSONObject): Session {
    // DOC L0055: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. s: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di sessionFromJson.
    val s = Session(mode = if (o.has("mode")) o.string("mode") else "WORKOUT", id = o.string("id"), workoutId = o.string("workoutId"), workoutTitle = if (o.has("workoutTitle")) o.string("workoutTitle") else "", startedAt = o.string("startedAt"), endedAt = o.string("endedAt"), plannedMs = o.number("plannedMs", 1, 86400000), elapsedMs = o.number("elapsedMs"), workMs = o.number("workMs"), completedWorkIntervals = o.int("completedWorkIntervals", 0, 10000), plannedWorkIntervals = o.int("plannedWorkIntervals", 1, 10000), pauses = o.number("pauses", 0, Int.MAX_VALUE.toLong()).toInt(), status = o.string("status"))
    // DOC L0056: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di sessionFromJson.
    require(s.mode in listOf("WORKOUT", "STUDIO")) { "Modalità sessione non valida" }
    // DOC L0057: Esegue il passaggio esplicito «checkId(s.id); checkId(s.workoutId)» nel flusso: Serializzazione e validazione interoperabile Kotlin/JSON. Nel contesto di sessionFromJson.
    checkId(s.id); checkId(s.workoutId)
    // DOC L0058: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di sessionFromJson.
    require(s.workoutTitle.length <= 120 && instant(s.endedAt) >= instant(s.startedAt) && s.workMs <= s.elapsedMs && s.completedWorkIntervals <= s.plannedWorkIntervals) { "Metriche sessione non valide" }
    // DOC L0059: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di sessionFromJson.
    require(s.status in listOf("COMPLETED", "INTERRUPTED") && (s.status != "COMPLETED" || s.completedWorkIntervals == s.plannedWorkIntervals)) { "Stato sessione non valido" }
    // DOC L0060: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di sessionFromJson.
    if (o.has("completedPhases") || o.has("plannedPhases")) return s.copy(plannedPhases = o.int("plannedPhases", s.plannedWorkIntervals, 10000), completedPhases = o.int("completedPhases", s.completedWorkIntervals, o.int("plannedPhases", s.plannedWorkIntervals, 10000)))
    // DOC L0061: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di sessionFromJson.
    return s
// DOC L0062: Chiude il costrutto aperto alla riga 54.
}
// DOC L0063: parseBackup: Valida dimensione, JSON, versione, programmi e sessioni prima di proporre la sostituzione dell’archivio.
fun parseBackup(raw: String): Backup {
    // DOC L0064: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di parseBackup.
    require(raw.toByteArray(Charsets.UTF_8).size <= MAX_BACKUP_BYTES) { "Backup massimo 5 MiB" }
    // DOC L0065: Esegue il passaggio esplicito «Json.parseToJsonElement(raw) // Reject lenient org.json extensions (single quotes, trailing tokens).» nel flusso: Serializzazione e validazione interoperabile Kotlin/JSON. Nel contesto di parseBackup.
    Json.parseToJsonElement(raw) // Reject lenient org.json extensions (single quotes, trailing tokens).
    // DOC L0066: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. o: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di parseBackup.
    val o = JSONObject(raw); require(o.int("schemaVersion", 1, 1) == 1); instant(o.string("exportedAt"))
    // DOC L0067: wa: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. sa: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di parseBackup.
    val wa = o.getJSONArray("workouts"); val sa = o.getJSONArray("sessions")
    // DOC L0068: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di parseBackup.
    require(wa.length() <= 500 && sa.length() <= 10000) { "Archivio troppo grande" }
    // DOC L0069: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. s: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di parseBackup.
    val w = wa.objects().map(::workoutFromJson); val s = sa.objects().map(::sessionFromJson)
    // DOC L0070: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di parseBackup.
    require(w.map { it.id }.distinct().size == w.size && s.map { it.id }.distinct().size == s.size) { "ID duplicato nel backup" }
    // DOC L0071: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di parseBackup.
    return Backup(w, s)
// DOC L0072: Chiude il costrutto aperto alla riga 63.
}
// DOC L0073: backupJson: Serializza il backup Kotlin nello stesso formato JSON v1 consumato dal web.
fun backupJson(backup: Backup): String = JSONObject().put("schemaVersion", 1).put("exportedAt", isoNow()).put("workouts", JSONArray(backup.workouts.map(::workoutJson))).put("sessions", JSONArray(backup.sessions.map(::sessionJson))).toString(2)
// DOC L0074: sessionsCsv: Esporta le sessioni in CSV con intestazione, virgolette e neutralizzazione delle formule.
fun sessionsCsv(sessions: List<Session>): String {
    // DOC L0075: fields: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di sessionsCsv.
    val fields = listOf("id", "mode", "workoutId", "workoutTitle", "startedAt", "endedAt", "status", "plannedMs", "elapsedMs", "workMs", "completedWorkIntervals", "plannedWorkIntervals", "completedPhases", "plannedPhases", "pauses")
    // DOC L0076: cell: Prepara una cella CSV, raddoppiando le virgolette e proteggendo i prefissi interpretabili come formule.
    fun cell(value: String): String { val safe = if (value.firstOrNull() in listOf('=', '+', '-', '@', '\t', '\r')) "'$value" else value; return "\"${safe.replace("\"", "\"\"")}\"" }
    // DOC L0077: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. json: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di cell.
    return "\uFEFF" + (listOf(fields.map(::cell).joinToString(",")) + sessions.map { s -> val json = sessionJson(s); fields.joinToString(",") { cell(if (it == "mode") s.mode else json.optString(it)) } }).joinToString("\r\n")
// DOC L0078: Chiude il costrutto aperto alla riga 74.
}
