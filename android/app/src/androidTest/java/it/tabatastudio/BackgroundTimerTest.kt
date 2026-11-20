// DOC L0001: Assegna il file al namespace it.tabatastudio, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import android.app.NotificationManager. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.app.NotificationManager
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import android.content.Intent. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.content.Intent
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import android.media.AudioManager. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.media.AudioManager
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import android.os.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.os.*
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import androidx.test.ext.junit.rules.ActivityScenarioRule. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.test.ext.junit.rules.ActivityScenarioRule
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import androidx.core.content.ContextCompat. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.core.content.ContextCompat
// DOC L0009: Rende disponibili le dipendenze esplicite di questa riga: import androidx.lifecycle.Lifecycle. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.lifecycle.Lifecycle
// DOC L0010: Rende disponibili le dipendenze esplicite di questa riga: import androidx.test.ext.junit.runners.AndroidJUnit4. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.test.ext.junit.runners.AndroidJUnit4
// DOC L0011: Rende disponibili le dipendenze esplicite di questa riga: import androidx.test.platform.app.InstrumentationRegistry. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.test.platform.app.InstrumentationRegistry
// DOC L0012: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.domain.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.domain.*
// DOC L0013: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.timer.TimerRuntime. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.timer.TimerRuntime
// DOC L0014: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.ui.StudioViewModel. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.ui.StudioViewModel
// DOC L0015: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.timer.TimerService. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.timer.TimerService
// DOC L0016: Rende disponibili le dipendenze esplicite di questa riga: import kotlinx.coroutines.runBlocking. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import kotlinx.coroutines.runBlocking
// DOC L0017: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.*
// DOC L0018: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.Assert.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.Assert.*
// DOC L0019: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.runner.RunWith. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.runner.RunWith
// DOC L0020: Rende disponibili le dipendenze esplicite di questa riga: import java.util.concurrent.CountDownLatch. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import java.util.concurrent.CountDownLatch
// DOC L0021: Rende disponibili le dipendenze esplicite di questa riga: import java.util.concurrent.TimeUnit. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import java.util.concurrent.TimeUnit

// DOC L0023: Applica l’annotazione dichiarata: Compose identifica UI, Room genera accessi e schema, mentre le annotazioni Test selezionano i casi da eseguire.
@RunWith(AndroidJUnit4::class)
// DOC L0024: Dichiara il tipo o contenitore di questo modulo: Prove di regressione in BackgroundTimerTest.kt. I campi e i metodi sono descritti nelle righe successive.
class BackgroundTimerTest {
    // DOC L0025: Applica l’annotazione dichiarata: Compose identifica UI, Room genera accessi e schema, mentre le annotazioni Test selezionano i casi da eseguire.
    @get:Rule val activityRule = ActivityScenarioRule(MainActivity::class.java)
    // DOC L0026: instrumentation: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    // DOC L0027: app: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private val app get() = instrumentation.targetContext.applicationContext as TabataApplication
    // DOC L0028: runtime: proprietario del motore Android per il processo, condiviso tra servizio e UI.
    private val runtime get() = app.timerRuntime
    // DOC L0029: main: Prepara o verifica il caso di regressione «main»; le asserzioni nel corpo specificano l’esito atteso.
    private fun main(action: () -> Unit) = instrumentation.runOnMainSync(action)
    // DOC L0030: shell: Prepara o verifica il caso di regressione «shell»; le asserzioni nel corpo specificano l’esito atteso.
    private fun shell(command: String) = ParcelFileDescriptor.AutoCloseInputStream(instrumentation.uiAutomation.executeShellCommand(command)).use { it.readBytes() }
    // DOC L0031: waitFor: Prepara o verifica il caso di regressione «wait For»; le asserzioni nel corpo specificano l’esito atteso.
    private fun waitFor(condition: () -> Boolean) {
        // DOC L0032: deadline: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di waitFor.
        val deadline = SystemClock.elapsedRealtime() + 15000
        // DOC L0033: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di waitFor.
        while (!condition()) { check(SystemClock.elapsedRealtime() < deadline) { "Condizione del timer non raggiunta" }; Thread.sleep(25) }
    // DOC L0034: Chiude il costrutto aperto alla riga 31.
    }
    // DOC L0035: ready: Prepara o verifica il caso di regressione «ready»; le asserzioni nel corpo specificano l’esito atteso.
    @Before fun ready() {
        // DOC L0036: Esegue il passaggio esplicito «it.tabatastudio.i18n.AppText.choose("it")» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di ready.
        it.tabatastudio.i18n.AppText.choose("it")
        // DOC L0037: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di ready.
        if (Build.VERSION.SDK_INT >= 33) shell("pm grant it.tabatastudio android.permission.POST_NOTIFICATIONS")
        // DOC L0038: Esegue il passaggio esplicito «runBlocking { runtime.initialized.await() }» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di ready.
        runBlocking { runtime.initialized.await() }
    // DOC L0039: Chiude il costrutto aperto alla riga 35.
    }
    // DOC L0040: cleanup: Prepara o verifica il caso di regressione «cleanup»; le asserzioni nel corpo specificano l’esito atteso.
    @After fun cleanup() {
        // DOC L0041: Esegue il passaggio esplicito «shell("input keyevent KEYCODE_WAKEUP"); shell("wm dismiss-keyguard")» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di cleanup.
        shell("input keyevent KEYCODE_WAKEUP"); shell("wm dismiss-keyguard")
        // DOC L0042: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di cleanup.
        main { if (runtime.active) runtime.stop() }
        // DOC L0043: Esegue il passaggio esplicito «waitFor { !runtime.active && !runtime.hasUnsavedResult }» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di cleanup.
        waitFor { !runtime.active && !runtime.hasUnsavedResult }
        // DOC L0044: Esegue il passaggio esplicito «main { runtime.clearFinished() }» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di cleanup.
        main { runtime.clearFinished() }
        // DOC L0045: Esegue il passaggio esplicito «waitFor { !runtime.serviceRunning }» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di cleanup.
        waitFor { !runtime.serviceRunning }
        // DOC L0046: Esegue il passaggio esplicito «instrumentation.context.stopService(probeIntent())» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di cleanup.
        instrumentation.context.stopService(probeIntent())
        // DOC L0047: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di cleanup.
        if (activityRule.scenario.state != Lifecycle.State.DESTROYED) activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
    // DOC L0048: Chiude il costrutto aperto alla riga 40.
    }
    // DOC L0049: start: Prepara o verifica il caso di regressione «start»; le asserzioni nel corpo specificano l’esito atteso.
    private fun start(mode: String = "WORKOUT") {
        // DOC L0050: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di start.
        val w = if (mode == "STUDIO") newQuickWorkout(mode) else Workout(title = "Background test", blocks = listOf(Block(exercises = listOf(Exercise(workSec = 4, repetitions = 2, restBetweenRepsSec = 4)))))
        // DOC L0051: Esegue il passaggio esplicito «main { runtime.start(w) }; waitFor { runtime.timer?.state == "running" && !runtime.starting }» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di start.
        main { runtime.start(w) }; waitFor { runtime.timer?.state == "running" && !runtime.starting }
        // DOC L0052: Esegue il passaggio esplicito «main { runtime.skip() } // Keep production's 20-second preparation; advance it with the actual control.» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di start.
        main { runtime.skip() } // Keep production's 20-second preparation; advance it with the actual control.
    // DOC L0053: Chiude il costrutto aperto alla riga 49.
    }
    // DOC L0054: backgroundAndNotificationActionsKeepOneSession: Prepara o verifica il caso di regressione «background And Notification Actions Keep One Session»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun backgroundAndNotificationActionsKeepOneSession() {
        // DOC L0055: Esegue il passaggio esplicito «start()» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di backgroundAndNotificationActionsKeepOneSession.
        start()
        // DOC L0056: id: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di backgroundAndNotificationActionsKeepOneSession.
        val id = runtime.timer!!.sessionId
        // DOC L0057: manager: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di backgroundAndNotificationActionsKeepOneSession.
        val manager = app.getSystemService(NotificationManager::class.java)
        // DOC L0058: Esegue il passaggio esplicito «waitFor { manager.activeNotifications.any { it.id == TimerService.NOTIFICATION_ID } }» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di backgroundAndNotificationActionsKeepOneSession.
        waitFor { manager.activeNotifications.any { it.id == TimerService.NOTIFICATION_ID } }
        // DOC L0059: before: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di backgroundAndNotificationActionsKeepOneSession.
        val before = runtime.timer!!.elapsedMs
        // DOC L0060: Esegue il passaggio esplicito «activityRule.scenario.moveToState(Lifecycle.State.CREATED)» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di backgroundAndNotificationActionsKeepOneSession.
        activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        // DOC L0061: Esegue il passaggio esplicito «waitFor { runtime.timer!!.elapsedMs >= before + 1200 }» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di backgroundAndNotificationActionsKeepOneSession.
        waitFor { runtime.timer!!.elapsedMs >= before + 1200 }
        // DOC L0062: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di backgroundAndNotificationActionsKeepOneSession.
        assertEquals("running", runtime.timer!!.state)
        // DOC L0063: action: Prepara o verifica il caso di regressione «action»; le asserzioni nel corpo specificano l’esito atteso.
        fun action(title: String) = manager.activeNotifications.single { it.id == TimerService.NOTIFICATION_ID }.notification.actions.single { it.title.toString() == title }.actionIntent
        // DOC L0064: Esegue il passaggio esplicito «action("Pausa").send(); waitFor { runtime.timer!!.state == "paused" }» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di action.
        action("Pausa").send(); waitFor { runtime.timer!!.state == "paused" }
        // DOC L0065: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Richiede il mantenimento della CPU durante il timer Android, distinto dallo schermo acceso. Nel contesto di action.
        assertFalse(shell("dumpsys power").toString(Charsets.UTF_8).lineSequence().any { it.contains("PARTIAL_WAKE_LOCK") && it.contains("it.tabatastudio:interval-timer") })
        // DOC L0066: pausedAt: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di action.
        val pausedAt = runtime.timer!!.elapsedMs
        // DOC L0067: Esegue il passaggio esplicito «Thread.sleep(1100)» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di action.
        Thread.sleep(1100)
        // DOC L0068: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di action.
        assertEquals(pausedAt, runtime.timer!!.elapsedMs)
        // DOC L0069: Esegue il passaggio esplicito «waitFor { manager.activeNotifications.single { it.id == TimerService.NOTIFICATION_ID }.notification.actions.any { it.title.toString() == "Riprendi" } }» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di action.
        waitFor { manager.activeNotifications.single { it.id == TimerService.NOTIFICATION_ID }.notification.actions.any { it.title.toString() == "Riprendi" } }
        // DOC L0070: Esegue il passaggio esplicito «action("Riprendi").send(); waitFor { runtime.timer!!.state == "running" }» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di action.
        action("Riprendi").send(); waitFor { runtime.timer!!.state == "running" }
        // DOC L0071: Esegue il passaggio esplicito «action("Salta").send(); waitFor { runtime.timer!!.index >= 1 }» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di action.
        action("Salta").send(); waitFor { runtime.timer!!.index >= 1 }
        // DOC L0072: Esegue il passaggio esplicito «action("Termina").send(); waitFor { runtime.timer!!.result != null && !runtime.hasUnsavedResult }» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di action.
        action("Termina").send(); waitFor { runtime.timer!!.result != null && !runtime.hasUnsavedResult }
        // DOC L0073: Esegue il passaggio esplicito «waitFor { !runtime.serviceRunning }» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di action.
        waitFor { !runtime.serviceRunning }
        // DOC L0074: sessions: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di action.
        val sessions = runBlocking { app.repository.read() }.sessions.filter { it.id == id }
        // DOC L0075: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di action.
        assertEquals(1, sessions.size); assertEquals("INTERRUPTED", sessions.single().status)
        // DOC L0076: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di action.
        assertTrue(manager.activeNotifications.none { it.id == TimerService.NOTIFICATION_ID })
        // DOC L0077: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Richiede il mantenimento della CPU durante il timer Android, distinto dallo schermo acceso. Nel contesto di action.
        assertFalse(shell("dumpsys power").toString(Charsets.UTF_8).lineSequence().any { it.contains("PARTIAL_WAKE_LOCK") && it.contains("it.tabatastudio:interval-timer") })
    // DOC L0078: Chiude il costrutto aperto alla riga 54.
    }
    // DOC L0079: studySurvivesActivityDestructionAndScreenOff: Prepara o verifica il caso di regressione «study Survives Activity Destruction And Screen Off»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun studySurvivesActivityDestructionAndScreenOff() {
        // DOC L0080: Esegue il passaggio esplicito «start("STUDIO")» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di studySurvivesActivityDestructionAndScreenOff.
        start("STUDIO")
        // DOC L0081: id: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di studySurvivesActivityDestructionAndScreenOff.
        val id = runtime.timer!!.sessionId
        // DOC L0082: Esegue il passaggio esplicito «activityRule.scenario.close()» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di studySurvivesActivityDestructionAndScreenOff.
        activityRule.scenario.close()
        // DOC L0083: Esegue il passaggio esplicito «shell("input keyevent KEYCODE_SLEEP")» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di studySurvivesActivityDestructionAndScreenOff.
        shell("input keyevent KEYCODE_SLEEP")
        // DOC L0084: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di studySurvivesActivityDestructionAndScreenOff.
        assertFalse(app.getSystemService(PowerManager::class.java).isInteractive)
        // DOC L0085: before: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di studySurvivesActivityDestructionAndScreenOff.
        val before = runtime.timer!!.elapsedMs
        // DOC L0086: Esegue il passaggio esplicito «waitFor { runtime.timer!!.elapsedMs > before + 1200 }» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di studySurvivesActivityDestructionAndScreenOff.
        waitFor { runtime.timer!!.elapsedMs > before + 1200 }
        // DOC L0087: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di studySurvivesActivityDestructionAndScreenOff.
        assertEquals("running", runtime.timer!!.state); assertEquals(id, runtime.timer!!.sessionId)
        // DOC L0088: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di studySurvivesActivityDestructionAndScreenOff.
        assertTrue(runtime.serviceRunning)
        // DOC L0089: Esegue il passaggio esplicito «shell("input keyevent KEYCODE_WAKEUP"); shell("wm dismiss-keyguard")» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di studySurvivesActivityDestructionAndScreenOff.
        shell("input keyevent KEYCODE_WAKEUP"); shell("wm dismiss-keyguard")
        // DOC L0090: Esegue il passaggio esplicito «androidx.test.core.app.ActivityScenario.launch(MainActivity::class.java).use { reopened ->» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di studySurvivesActivityDestructionAndScreenOff.
        androidx.test.core.app.ActivityScenario.launch(MainActivity::class.java).use { reopened ->
            // DOC L0091: vm: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di studySurvivesActivityDestructionAndScreenOff.
            var vm: StudioViewModel? = null
            // DOC L0092: Esegue il passaggio esplicito «reopened.onActivity { vm = androidx.lifecycle.ViewModelProvider(it)[StudioViewModel::class.java] }» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di studySurvivesActivityDestructionAndScreenOff.
            reopened.onActivity { vm = androidx.lifecycle.ViewModelProvider(it)[StudioViewModel::class.java] }
            // DOC L0093: Esegue il passaggio esplicito «waitFor { vm?.ready == true }» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di studySurvivesActivityDestructionAndScreenOff.
            waitFor { vm?.ready == true }
            // DOC L0094: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di studySurvivesActivityDestructionAndScreenOff.
            assertEquals("Timer", vm!!.route); assertEquals("STUDIO", vm!!.mode)
            // DOC L0095: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di studySurvivesActivityDestructionAndScreenOff.
            assertEquals(id, vm!!.timer!!.sessionId)
        // DOC L0096: Chiude il costrutto aperto alla riga 90.
        }
        // DOC L0097: Esegue il passaggio esplicito «main { runtime.stop() }; waitFor { !runtime.hasUnsavedResult }» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di studySurvivesActivityDestructionAndScreenOff.
        main { runtime.stop() }; waitFor { !runtime.hasUnsavedResult }
        // DOC L0098: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di studySurvivesActivityDestructionAndScreenOff.
        assertEquals("STUDIO", runBlocking { app.repository.read() }.sessions.single { it.id == id }.mode)
    // DOC L0099: Chiude il costrutto aperto alla riga 79.
    }
    // DOC L0100: consecutiveTimersAreSavedWithoutDuplicates: Prepara o verifica il caso di regressione «consecutive Timers Are Saved Without Duplicates»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun consecutiveTimersAreSavedWithoutDuplicates() {
        // DOC L0101: ids: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di consecutiveTimersAreSavedWithoutDuplicates.
        val ids = mutableListOf<String>()
        // DOC L0102: Esegue il passaggio esplicito «repeat(3) {» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di consecutiveTimersAreSavedWithoutDuplicates.
        repeat(3) {
            // DOC L0103: Esegue il passaggio esplicito «start(); ids.add(runtime.timer!!.sessionId)» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di consecutiveTimersAreSavedWithoutDuplicates.
            start(); ids.add(runtime.timer!!.sessionId)
            // DOC L0104: Esegue il passaggio esplicito «main { runtime.stop() }; waitFor { !runtime.hasUnsavedResult }» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di consecutiveTimersAreSavedWithoutDuplicates.
            main { runtime.stop() }; waitFor { !runtime.hasUnsavedResult }
        // DOC L0105: Chiude il costrutto aperto alla riga 102.
        }
        // DOC L0106: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di consecutiveTimersAreSavedWithoutDuplicates.
        assertEquals(3, ids.distinct().size)
        // DOC L0107: sessions: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di consecutiveTimersAreSavedWithoutDuplicates.
        val sessions = runBlocking { app.repository.read() }.sessions
        // DOC L0108: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di consecutiveTimersAreSavedWithoutDuplicates.
        for (id in ids) assertEquals(1, sessions.count { it.id == id })
    // DOC L0109: Chiude il costrutto aperto alla riga 100.
    }
    // DOC L0110: externalMediaKeepsFocusWhileCuesPlayWithScreenOff: Prepara o verifica il caso di regressione «external Media Keeps Focus While Cues Play With Screen Off»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun externalMediaKeepsFocusWhileCuesPlayWithScreenOff() {
        // This API 37 emulator's public playback callback omits SoundPool. Observe AudioService's
        // real player registrations/start events, restricted to this UID and newly created pools.
        // DOC L0113: before: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di externalMediaKeepsFocusWhileCuesPlayWithScreenOff.
        val before = soundPoolIds(shell("dumpsys audio").toString(Charsets.UTF_8))
        // DOC L0114: Esegue il passaggio esplicito «ContextCompat.startForegroundService(instrumentation.context, probeIntent())» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di externalMediaKeepsFocusWhileCuesPlayWithScreenOff.
        ContextCompat.startForegroundService(instrumentation.context, probeIntent())
        // DOC L0115: initial: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di externalMediaKeepsFocusWhileCuesPlayWithScreenOff.
        val initial = probeStatus()
        // DOC L0116: Esegue il passaggio esplicito «assertNotEquals(android.os.Process.myUid(), initial.getInt("uid"))» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di externalMediaKeepsFocusWhileCuesPlayWithScreenOff.
        assertNotEquals(android.os.Process.myUid(), initial.getInt("uid"))
        // DOC L0117: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Legge il valore locale e applica i controlli/fallback riportati nell’espressione. Nel contesto di externalMediaKeepsFocusWhileCuesPlayWithScreenOff.
        assertEquals(AudioManager.AUDIOFOCUS_REQUEST_GRANTED, initial.getInt("focus")); assertTrue(initial.getBoolean("playing"))
        // DOC L0118: Esegue il passaggio esplicito «start(); activityRule.scenario.moveToState(Lifecycle.State.CREATED)» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di externalMediaKeepsFocusWhileCuesPlayWithScreenOff.
        start(); activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        // DOC L0119: Esegue il passaggio esplicito «shell("input keyevent KEYCODE_SLEEP")» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di externalMediaKeepsFocusWhileCuesPlayWithScreenOff.
        shell("input keyevent KEYCODE_SLEEP")
        // DOC L0120: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di externalMediaKeepsFocusWhileCuesPlayWithScreenOff.
        assertFalse(app.getSystemService(PowerManager::class.java).isInteractive)
        // DOC L0121: Esegue il passaggio esplicito «waitFor { runtime.timer!!.result != null && !runtime.hasUnsavedResult }» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di externalMediaKeepsFocusWhileCuesPlayWithScreenOff.
        waitFor { runtime.timer!!.result != null && !runtime.hasUnsavedResult }
        // DOC L0122: after: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di externalMediaKeepsFocusWhileCuesPlayWithScreenOff.
        val after = probeStatus()
        // DOC L0123: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Legge il valore locale e applica i controlli/fallback riportati nell’espressione. Nel contesto di externalMediaKeepsFocusWhileCuesPlayWithScreenOff.
        assertTrue(after.getBoolean("playing")); assertTrue(after.getIntArray("changes")!!.none { it < 0 })
        // DOC L0124: audioDump: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di externalMediaKeepsFocusWhileCuesPlayWithScreenOff.
        val audioDump = shell("dumpsys audio").toString(Charsets.UTF_8)
        // DOC L0125: starts: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di externalMediaKeepsFocusWhileCuesPlayWithScreenOff.
        val starts = (soundPoolIds(audioDump) - before).sumOf { id -> Regex("player piid:$id event:started").findAll(audioDump).count() }
        // DOC L0126: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di externalMediaKeepsFocusWhileCuesPlayWithScreenOff.
        assertTrue("Avvisi non rilevati da AudioService: $starts", starts >= 3)
        // DOC L0127: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di externalMediaKeepsFocusWhileCuesPlayWithScreenOff.
        assertEquals("COMPLETED", runtime.timer!!.result!!.status)
    // DOC L0128: Chiude il costrutto aperto alla riga 110.
    }
    // DOC L0129: soundPoolIds: Prepara o verifica il caso di regressione «sound Pool Ids»; le asserzioni nel corpo specificano l’esito atteso.
    private fun soundPoolIds(dump: String): Set<String> = Regex("new player piid:(\\d+) uid/pid:${android.os.Process.myUid()}/\\d+ package:it.tabatastudio type:android.media.SoundPool").findAll(dump).map { it.groupValues[1] }.toSet()
    // DOC L0130: probeIntent: Prepara o verifica il caso di regressione «probe Intent»; le asserzioni nel corpo specificano l’esito atteso.
    private fun probeIntent() = Intent().setClassName(instrumentation.context.packageName, MediaPlaybackProbeService::class.java.name)
    // DOC L0131: probeStatus: Prepara o verifica il caso di regressione «probe Status»; le asserzioni nel corpo specificano l’esito atteso.
    private fun probeStatus(): Bundle {
        // DOC L0132: result: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. ready: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di probeStatus.
        var result: Bundle? = null; val ready = CountDownLatch(1)
        // DOC L0133: receiver: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di probeStatus.
        val receiver = object : ResultReceiver(Handler(Looper.getMainLooper())) {
            // DOC L0134: onReceiveResult: Prepara o verifica il caso di regressione «on Receive Result»; le asserzioni nel corpo specificano l’esito atteso.
            override fun onReceiveResult(resultCode: Int, data: Bundle) { result = data; ready.countDown() }
        // DOC L0135: Chiude il costrutto aperto alla riga 133.
        }
        // DOC L0136: Esegue il passaggio esplicito «instrumentation.context.startService(probeIntent().putExtra("receiver", receiver))» nel flusso: Prove di regressione in BackgroundTimerTest.kt. Nel contesto di onReceiveResult.
        instrumentation.context.startService(probeIntent().putExtra("receiver", receiver))
        // DOC L0137: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di onReceiveResult.
        assertTrue("Lettore di prova non disponibile", ready.await(10, TimeUnit.SECONDS))
        // DOC L0138: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di onReceiveResult.
        return result!!
    // DOC L0139: Chiude il costrutto aperto alla riga 131.
    }
// DOC L0140: Chiude il costrutto aperto alla riga 24.
}
