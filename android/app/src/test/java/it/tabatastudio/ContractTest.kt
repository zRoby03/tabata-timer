// DOC L0001: Assegna il file al namespace it.tabatastudio, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.domain.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.domain.*
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import org.json.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.json.*
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.Assert.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.Assert.*
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.Test. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.Test
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import java.time.Instant. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import java.time.Instant
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import java.io.File. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import java.io.File

// DOC L0010: Dichiara il tipo o contenitore di questo modulo: Prove di regressione in ContractTest.kt. I campi e i metodi sono descritti nelle righe successive.
class ContractTest {
    // DOC L0011: quickTimerKeepsTimelineSemantics: Prepara o verifica il caso di regressione «quick Timer Keeps Timeline Semantics»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun quickTimerKeepsTimelineSemantics() {
        // DOC L0012: w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di quickTimerKeepsTimelineSemantics.
        val w = newQuickWorkout()
        // DOC L0013: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di quickTimerKeepsTimelineSemantics.
        assertEquals(230000L, plannedMs(compileTimeline(w)))
        // DOC L0014: b: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di quickTimerKeepsTimelineSemantics.
        val b = w.blocks[0]
        // DOC L0015: reference: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di quickTimerKeepsTimelineSemantics.
        val reference = w.copy(blocks = listOf(b.copy(cycles = 2, exercises = listOf(b.exercises[0].copy(repetitions = 3)))))
        // DOC L0016: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di quickTimerKeepsTimelineSemantics.
        assertEquals(220000L, plannedMs(compileTimeline(reference)))
    // DOC L0017: Chiude il costrutto aperto alla riga 11.
    }
    // DOC L0018: resource: Prepara o verifica il caso di regressione «resource»; le asserzioni nel corpo specificano l’esito atteso.
    private fun resource(name: String) = javaClass.classLoader!!.getResource(name)!!.readText()
    // DOC L0019: engine: Prepara o verifica il caso di regressione «engine»; le asserzioni nel corpo specificano l’esito atteso.
    private fun engine(): Pair<TimerEngine, (Long) -> Unit> {
        // DOC L0020: clock: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di engine.
        var clock = 0L
        // DOC L0021: w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di engine.
        val w = Workout(blocks = listOf(Block(exercises = listOf(Exercise(workSec = 2, repetitions = 2, restBetweenRepsSec = 1)))))
        // DOC L0022: t: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di engine.
        val t = TimerEngine(w, { clock }, { Instant.ofEpochMilli(clock).toString() }); t.start(); clock += PREPARATION_MS; t.tick()
        // DOC L0023: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di engine.
        return t to { ms: Long -> clock += ms; t.tick() }
    // DOC L0024: Chiude il costrutto aperto alla riga 19.
    }
    // DOC L0025: sharedTimelineFixtures: Prepara o verifica il caso di regressione «shared Timeline Fixtures»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun sharedTimelineFixtures() {
        // DOC L0026: cases: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di sharedTimelineFixtures.
        val cases = JSONArray(resource("timeline-cases.json"))
        // DOC L0027: Esegue il passaggio esplicito «repeat(cases.length()) { i -> val c = cases.getJSONObject(i); val phases = compileTimeline(workoutFromJson(c.getJSONObject("workout")))» nel flusso: Prove di regressione in ContractTest.kt. Nel contesto di sharedTimelineFixtures.
        repeat(cases.length()) { i -> val c = cases.getJSONObject(i); val phases = compileTimeline(workoutFromJson(c.getJSONObject("workout")))
            // DOC L0028: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di sharedTimelineFixtures.
            assertEquals(c.getLong("expectedMs"), plannedMs(phases)); assertEquals(c.getJSONArray("expectedTypes").toString(), JSONArray(phases.map { it.type }).toString())
        // DOC L0029: Chiude il costrutto aperto alla riga 27.
        }
    // DOC L0030: Chiude il costrutto aperto alla riga 25.
    }
    // DOC L0031: timerCatchup: Prepara o verifica il caso di regressione «timer Catchup»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun timerCatchup() { val (t, advance) = engine(); advance(99999); assertEquals(5000L, t.result!!.elapsedMs); assertEquals(4000L, t.result!!.workMs); assertEquals("COMPLETED", t.result!!.status); assertEquals(3, t.result!!.completedPhases); assertEquals(3, t.result!!.plannedPhases) }
    // DOC L0032: timerPause: Prepara o verifica il caso di regressione «timer Pause»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun timerPause() { val (t, advance) = engine(); advance(700); t.pause(); advance(10000); assertEquals(1300L, t.remainingMs); t.resume(); advance(1300); assertEquals(1, t.index); assertEquals(2000L, t.elapsedMs); assertEquals(1, t.pauses) }
    // DOC L0033: timerSkip: Prepara o verifica il caso di regressione «timer Skip»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun timerSkip() { val (t, advance) = engine(); advance(500); t.skip(); t.skip(); advance(2000); assertEquals(2500L, t.result!!.workMs); assertEquals(1, t.result!!.completedWorkIntervals); assertEquals("INTERRUPTED", t.result!!.status) }
    // DOC L0034: timerPrevious: Prepara o verifica il caso di regressione «timer Previous»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun timerPrevious() { val (t, advance) = engine(); advance(2000); t.previous(); advance(5000); assertEquals(6000L, t.result!!.workMs); assertEquals(2, t.result!!.completedWorkIntervals) }
    // DOC L0035: stopIdempotent: Prepara o verifica il caso di regressione «stop Idempotent»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun stopIdempotent() { val (t, advance) = engine(); advance(1000); t.stop(); val s = t.result; t.stop(); assertSame(s, t.result); assertEquals("INTERRUPTED", s!!.status) }
    // DOC L0036: jsonRoundtrip: Prepara o verifica il caso di regressione «json Roundtrip»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun jsonRoundtrip() {
        // DOC L0037: Esegue il passaggio esplicito «val (t, advance) = engine(); advance(5000)» nel flusso: Prove di regressione in ContractTest.kt. Nel contesto di jsonRoundtrip.
        val (t, advance) = engine(); advance(5000)
        // DOC L0038: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. backup: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di jsonRoundtrip.
        val backup = Backup(presets(), listOf(t.result!!)); assertEquals(backup, parseBackup(backupJson(backup)))
    // DOC L0039: Chiude il costrutto aperto alla riga 36.
    }
    // DOC L0040: webFixtureAndAndroidExport: Prepara o verifica il caso di regressione «web Fixture And Android Export»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun webFixtureAndAndroidExport() {
        // DOC L0041: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. backup: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di webFixtureAndAndroidExport.
        val backup = parseBackup(resource("backup-web-v1.json")); assertEquals(220000L, plannedMs(compileTimeline(backup.workouts.first())))
        // DOC L0042: out: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di webFixtureAndAndroidExport.
        val out = File("build/interop/android-export-v1.json"); out.parentFile!!.mkdirs(); out.writeText(backupJson(backup))
    // DOC L0043: Chiude il costrutto aperto alla riga 40.
    }
    // DOC L0044: simpleModelsRoundtripAcrossPlatforms: Prepara o verifica il caso di regressione «simple Models Roundtrip Across Platforms»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun simpleModelsRoundtripAcrossPlatforms() {
        // DOC L0045: backup: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di simpleModelsRoundtripAcrossPlatforms.
        val backup = parseBackup(resource("backup-simple-v1.json"))
        // DOC L0046: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di simpleModelsRoundtripAcrossPlatforms.
        assertEquals(83000L, plannedMs(compileTimeline(backup.workouts[0])))
        // DOC L0047: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di simpleModelsRoundtripAcrossPlatforms.
        assertEquals(660000L, plannedMs(compileTimeline(backup.workouts[1])))
        // DOC L0048: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di simpleModelsRoundtripAcrossPlatforms.
        assertEquals(backup, parseBackup(backupJson(backup)))
        // DOC L0049: out: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di simpleModelsRoundtripAcrossPlatforms.
        val out = File("build/interop/android-export-simple-v1.json"); out.parentFile!!.mkdirs(); out.writeText(backupJson(backup))
    // DOC L0050: Chiude il costrutto aperto alla riga 44.
    }
    // DOC L0051: rejectsMalformedNumbersAndSchema: Prepara o verifica il caso di regressione «rejects Malformed Numbers And Schema»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun rejectsMalformedNumbersAndSchema() {
        // DOC L0052: w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di rejectsMalformedNumbersAndSchema.
        val w = JSONObject(resource("backup-web-v1.json")).getJSONArray("workouts").getJSONObject(0)
        // DOC L0053: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di rejectsMalformedNumbersAndSchema.
        listOf(-1, 0.5, "10", JSONObject.NULL, 3601).forEach { v -> val bad = JSONObject(w.toString()).put("warmupSec", v); assertThrows(Exception::class.java) { workoutFromJson(bad) } }
        // DOC L0054: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di rejectsMalformedNumbersAndSchema.
        assertThrows(Exception::class.java) { parseBackup("{}") }
        // DOC L0055: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di rejectsMalformedNumbersAndSchema.
        assertThrows(Exception::class.java) { parseBackup(resource("backup-web-v1.json") + " trailing") }
        // DOC L0056: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di rejectsMalformedNumbersAndSchema.
        assertThrows(Exception::class.java) { parseBackup("{'schemaVersion':1}") }
        // DOC L0057: backup: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di rejectsMalformedNumbersAndSchema.
        val backup = JSONObject(resource("backup-web-v1.json")).put("schemaVersion", 2)
        // DOC L0058: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di rejectsMalformedNumbersAndSchema.
        assertThrows(Exception::class.java) { parseBackup(backup.toString()) }
    // DOC L0059: Chiude il costrutto aperto alla riga 51.
    }
    // DOC L0060: rejectsDuplicateAndInvalidSession: Prepara o verifica il caso di regressione «rejects Duplicate And Invalid Session»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun rejectsDuplicateAndInvalidSession() {
        // DOC L0061: raw: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. backup: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. sessions: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di rejectsDuplicateAndInvalidSession.
        val raw = resource("backup-web-v1.json"); val backup = JSONObject(raw); val sessions = backup.getJSONArray("sessions"); sessions.put(sessions.getJSONObject(0))
        // DOC L0062: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di rejectsDuplicateAndInvalidSession.
        assertThrows(Exception::class.java) { parseBackup(backup.toString()) }
        // DOC L0063: invalid: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di rejectsDuplicateAndInvalidSession.
        val invalid = JSONObject(raw); invalid.getJSONArray("sessions").getJSONObject(0).put("workMs", 9999999)
        // DOC L0064: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di rejectsDuplicateAndInvalidSession.
        assertThrows(Exception::class.java) { parseBackup(invalid.toString()) }
        // DOC L0065: badDate: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di rejectsDuplicateAndInvalidSession.
        val badDate = JSONObject(raw); badDate.getJSONArray("sessions").getJSONObject(0).put("startedAt", "2026-02-30T10:00:00Z")
        // DOC L0066: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di rejectsDuplicateAndInvalidSession.
        assertThrows(Exception::class.java) { parseBackup(badDate.toString()) }
    // DOC L0067: Chiude il costrutto aperto alla riga 60.
    }
    // DOC L0068: statsUtcAndFilter: Prepara o verifica il caso di regressione «stats Utc And Filter»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun statsUtcAndFilter() {
        // DOC L0069: backup: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. stats: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di statsUtcAndFilter.
        val backup = parseBackup(resource("backup-web-v1.json")); val stats = aggregate(backup.sessions, 7, "", Instant.parse("2026-11-17T12:00:00Z"))
        // DOC L0070: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di statsUtcAndFilter.
        assertEquals(1, stats.sessions.size); assertEquals(50.0, stats.completion, 0.001); assertEquals(1, stats.weeks.last().count); assertEquals(0, aggregate(backup.sessions, 7, "other", Instant.parse("2026-11-17T12:00:00Z")).sessions.size)
    // DOC L0071: Chiude il costrutto aperto alla riga 68.
    }
// DOC L0072: Chiude il costrutto aperto alla riga 10.
}
