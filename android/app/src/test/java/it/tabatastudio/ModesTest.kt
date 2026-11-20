// DOC L0001: Assegna il file al namespace it.tabatastudio, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.domain.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.domain.*
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import org.json.JSONObject. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.json.JSONObject
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.Assert.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.Assert.*
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.Test. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.Test
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import java.io.File. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import java.io.File

// DOC L0009: Dichiara il tipo o contenitore di questo modulo: Prove di regressione in ModesTest.kt. I campi e i metodi sono descritti nelle righe successive.
class ModesTest {
    // DOC L0010: studioFlatActivitiesAndBoundaries: Prepara o verifica il caso di regressione «studio Flat Activities And Boundaries»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun studioFlatActivitiesAndBoundaries() {
        // DOC L0011: w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di studioFlatActivitiesAndBoundaries.
        val w = newQuickWorkout("STUDIO")
        // DOC L0012: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di studioFlatActivitiesAndBoundaries.
        assertEquals(115 * 60000L, plannedMs(compileTimeline(w)))
        // DOC L0013: simple: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di studioFlatActivitiesAndBoundaries.
        val simple = w.copy(activities = listOf(StudyActivity(concentrationSec = 60, sessions = 2, pauseSec = 120), StudyActivity(concentrationSec = 180, sessions = 1, pauseSec = 3600)), restBetweenActivitiesSec = 240)
        // DOC L0014: phases: sequenza cronologica compilata dal programma. Nel contesto di studioFlatActivitiesAndBoundaries.
        val phases = compileTimeline(simple)
        // DOC L0015: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di studioFlatActivitiesAndBoundaries.
        assertEquals(listOf("WORK", "REST_REP", "WORK", "REST_ACTIVITY", "WORK"), phases.map { it.type })
        // DOC L0016: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di studioFlatActivitiesAndBoundaries.
        assertEquals(listOf(60000L, 120000L, 60000L, 240000L, 180000L), phases.map { it.durationMs })
        // DOC L0017: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di studioFlatActivitiesAndBoundaries.
        assertEquals("Pausa tra sessioni", phases[1].label)
        // DOC L0018: clock: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. timer: motore corrente, opzionale quando nessuna sessione è stata avviata. Nel contesto di studioFlatActivitiesAndBoundaries.
        var clock = 0L; val timer = TimerEngine(simple, { clock }); timer.start(); clock = PREPARATION_MS + plannedMs(phases); timer.tick()
        // DOC L0019: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di studioFlatActivitiesAndBoundaries.
        assertEquals(300000L, timer.result!!.workMs); assertEquals(660000L, timer.result!!.plannedMs); assertEquals(3, timer.result!!.plannedWorkIntervals)
        // DOC L0020: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di studioFlatActivitiesAndBoundaries.
        assertThrows(Exception::class.java) { compileTimeline(w.copy(activities = emptyList())) }
        // DOC L0021: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di studioFlatActivitiesAndBoundaries.
        assertThrows(Exception::class.java) { compileTimeline(w.copy(activities = listOf(StudyActivity(concentrationSec = 61)))) }
        // DOC L0022: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di studioFlatActivitiesAndBoundaries.
        assertThrows(Exception::class.java) { compileTimeline(w.copy(restBetweenActivitiesSec = 61)) }
    // DOC L0023: Chiude il costrutto aperto alla riga 10.
    }
    // DOC L0024: workoutRestIsSharedPerBlock: Prepara o verifica il caso di regressione «workout Rest Is Shared Per Block»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun workoutRestIsSharedPerBlock() {
        // DOC L0025: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. exercises: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di workoutRestIsSharedPerBlock.
        val exercises = (1..3).map { Exercise(title = "E$it", repetitions = 2, workSec = 10, restBetweenRepsSec = 3, restAfterExerciseSec = it * 20) }
        // DOC L0026: w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di workoutRestIsSharedPerBlock.
        val w = Workout(blocks = listOf(Block(exercises = exercises, restBetweenExercisesSec = 7)))
        // DOC L0027: phases: sequenza cronologica compilata dal programma. Nel contesto di workoutRestIsSharedPerBlock.
        val phases = compileTimeline(w)
        // DOC L0028: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di workoutRestIsSharedPerBlock.
        assertEquals(listOf(7000L, 7000L), phases.filter { it.type == "REST_EXERCISE" }.map { it.durationMs })
        // DOC L0029: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di workoutRestIsSharedPerBlock.
        assertEquals(3, phases.count { it.type == "REST_REP" }); assertEquals("WORK", phases.last().type)
        // DOC L0030: legacy: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di workoutRestIsSharedPerBlock.
        val legacy = w.copy(blocks = listOf(w.blocks[0].copy(restBetweenExercisesSec = null)))
        // DOC L0031: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di workoutRestIsSharedPerBlock.
        assertTrue(normalizationNotice(legacy).isNotEmpty()); assertEquals(20, normalizeWorkout(legacy).blocks[0].restBetweenExercisesSec)
        // DOC L0032: Esegue il passaggio esplicito «assertNull(legacy.blocks[0].restBetweenExercisesSec)» nel flusso: Prove di regressione in ModesTest.kt. Nel contesto di workoutRestIsSharedPerBlock.
        assertNull(legacy.blocks[0].restBetweenExercisesSec)
    // DOC L0033: Chiude il costrutto aperto alla riga 24.
    }
    // DOC L0034: studioPreparationAndSessionMode: Prepara o verifica il caso di regressione «studio Preparation And Session Mode»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun studioPreparationAndSessionMode() {
        // DOC L0035: clock: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. timer: motore corrente, opzionale quando nessuna sessione è stata avviata. Nel contesto di studioPreparationAndSessionMode.
        var clock = 0L; val timer = TimerEngine(newQuickWorkout("STUDIO"), { clock })
        // DOC L0036: countdown: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. transitions: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di studioPreparationAndSessionMode.
        val countdown = CountdownCues(); val transitions = TransitionCues(); timer.start()
        // DOC L0037: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di studioPreparationAndSessionMode.
        assertEquals(20000L, timer.remainingMs)
        // DOC L0038: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di studioPreparationAndSessionMode.
        for (second in listOf(3, 2, 1)) { clock = 20000L - second * 1000; timer.tick(); assertEquals("rest_$second", hornSample(countdown.read(timer)!!, timer.currentPhase!!.type)) }
        // DOC L0039: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di studioPreparationAndSessionMode.
        clock = 20000; timer.tick(); assertEquals("whistle", transitions.read(timer)); assertEquals(0L, timer.elapsedMs)
        // DOC L0040: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di studioPreparationAndSessionMode.
        clock += 1500000; timer.tick(); assertEquals("sigh", transitions.read(timer)); timer.stop()
        // DOC L0041: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di studioPreparationAndSessionMode.
        assertEquals("STUDIO", timer.result!!.mode); assertEquals("STUDIO", timer.snapshot().mode)
    // DOC L0042: Chiude il costrutto aperto alla riga 34.
    }
    // DOC L0043: oldDataRemainWorkoutAndViewsAreSeparate: Prepara o verifica il caso di regressione «old Data Remain Workout And Views Are Separate»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun oldDataRemainWorkoutAndViewsAreSeparate() {
        // DOC L0044: legacy: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di oldDataRemainWorkoutAndViewsAreSeparate.
        val legacy = parseBackup(javaClass.classLoader!!.getResource("backup-web-v1.json")!!.readText())
        // DOC L0045: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di oldDataRemainWorkoutAndViewsAreSeparate.
        assertTrue(legacy.workouts.all { it.mode == "WORKOUT" }); assertTrue(legacy.sessions.all { it.mode == "WORKOUT" })
        // DOC L0046: studio: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. session: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di oldDataRemainWorkoutAndViewsAreSeparate.
        val studio = newQuickWorkout("STUDIO"); val session = legacy.sessions.first().copy(id = "studio-session", workoutId = studio.id, mode = "STUDIO")
        // DOC L0047: all: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di oldDataRemainWorkoutAndViewsAreSeparate.
        val all = Backup(legacy.workouts + studio, legacy.sessions + session)
        // DOC L0048: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di oldDataRemainWorkoutAndViewsAreSeparate.
        assertEquals(legacy, all.forMode("WORKOUT")); assertEquals(Backup(listOf(studio), listOf(session)), all.forMode("STUDIO"))
        // DOC L0049: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di oldDataRemainWorkoutAndViewsAreSeparate.
        assertEquals(all, parseBackup(backupJson(all)))
        // DOC L0050: bad: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di oldDataRemainWorkoutAndViewsAreSeparate.
        val bad = workoutJson(studio).put("mode", "OTHER")
        // DOC L0051: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di oldDataRemainWorkoutAndViewsAreSeparate.
        assertThrows(Exception::class.java) { workoutFromJson(bad) }
        // DOC L0052: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di oldDataRemainWorkoutAndViewsAreSeparate.
        assertThrows(Exception::class.java) { sessionFromJson(sessionJson(session).put("mode", "OTHER")) }
    // DOC L0053: Chiude il costrutto aperto alla riga 43.
    }
    // DOC L0054: sharedStudioBackupRoundtripAndTimeline: Prepara o verifica il caso di regressione «shared Studio Backup Roundtrip And Timeline»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun sharedStudioBackupRoundtripAndTimeline() {
        // DOC L0055: backup: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di sharedStudioBackupRoundtripAndTimeline.
        val backup = parseBackup(javaClass.classLoader!!.getResource("backup-modes-v1.json")!!.readText())
        // DOC L0056: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di sharedStudioBackupRoundtripAndTimeline.
        assertEquals(14100000L, plannedMs(compileTimeline(backup.workouts.single())))
        // DOC L0057: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di sharedStudioBackupRoundtripAndTimeline.
        assertEquals("STUDIO", backup.sessions.single().mode)
        // DOC L0058: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di sharedStudioBackupRoundtripAndTimeline.
        assertTrue(normalizationNotice(backup.workouts.single()).isNotEmpty())
        // DOC L0059: legacy: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. normalized: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di sharedStudioBackupRoundtripAndTimeline.
        val legacy = backup.workouts.single(); val normalized = normalizeWorkout(legacy)
        // DOC L0060: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di sharedStudioBackupRoundtripAndTimeline.
        assertTrue(normalized.blocks.isEmpty()); assertEquals(0, normalized.longBreakSec); assertEquals(0, normalized.cooldownSec)
        // DOC L0061: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di sharedStudioBackupRoundtripAndTimeline.
        assertEquals(900, legacy.longBreakSec)
        // DOC L0062: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. repeated: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di sharedStudioBackupRoundtripAndTimeline.
        val repeated = legacy.copy(blocks = legacy.blocks.map { it.copy(cycles = 2) })
        // DOC L0063: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di sharedStudioBackupRoundtripAndTimeline.
        assertEquals(2, normalizeWorkout(repeated).activities!!.size)
        // DOC L0064: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di sharedStudioBackupRoundtripAndTimeline.
        assertEquals(backup, parseBackup(backupJson(backup)))
        // DOC L0065: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di sharedStudioBackupRoundtripAndTimeline.
        File("build/interop/android-export-modes-v1.json").writeText(backupJson(backup))
    // DOC L0066: Chiude il costrutto aperto alla riga 54.
    }
// DOC L0067: Chiude il costrutto aperto alla riga 9.
}
