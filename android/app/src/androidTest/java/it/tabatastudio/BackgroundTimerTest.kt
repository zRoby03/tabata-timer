package it.tabatastudio

import android.app.NotificationManager
import android.content.Intent
import android.media.AudioManager
import android.os.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import it.tabatastudio.domain.*
import it.tabatastudio.timer.TimerRuntime
import it.tabatastudio.ui.StudioViewModel
import it.tabatastudio.timer.TimerService
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class BackgroundTimerTest {
    @get:Rule val activityRule = ActivityScenarioRule(MainActivity::class.java)
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private val app get() = instrumentation.targetContext.applicationContext as TabataApplication
    private val runtime get() = app.timerRuntime
    private fun main(action: () -> Unit) = instrumentation.runOnMainSync(action)
    private fun shell(command: String) = ParcelFileDescriptor.AutoCloseInputStream(instrumentation.uiAutomation.executeShellCommand(command)).use { it.readBytes() }
    private fun waitFor(condition: () -> Boolean) {
        val deadline = SystemClock.elapsedRealtime() + 15000
        while (!condition()) { check(SystemClock.elapsedRealtime() < deadline) { "Condizione del timer non raggiunta" }; Thread.sleep(25) }
    }
    @Before fun ready() {
        it.tabatastudio.i18n.AppText.choose("it")
        if (Build.VERSION.SDK_INT >= 33) shell("pm grant it.tabatastudio android.permission.POST_NOTIFICATIONS")
        runBlocking { runtime.initialized.await() }
    }
    @After fun cleanup() {
        shell("input keyevent KEYCODE_WAKEUP"); shell("wm dismiss-keyguard")
        main { if (runtime.active) runtime.stop() }
        waitFor { !runtime.active && !runtime.hasUnsavedResult }
        main { runtime.clearFinished() }
        waitFor { !runtime.serviceRunning }
        instrumentation.context.stopService(probeIntent())
        if (activityRule.scenario.state != Lifecycle.State.DESTROYED) activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
    }
    private fun start(mode: String = "WORKOUT") {
        val w = if (mode == "STUDIO") newQuickWorkout(mode) else Workout(title = "Background test", blocks = listOf(Block(exercises = listOf(Exercise(workSec = 5, repetitions = 2, restBetweenRepsSec = 5)))))
        main { runtime.start(w) }; waitFor { runtime.timer?.state == "running" && !runtime.starting }
        main { runtime.skip() } // Keep production's 20-second preparation; advance it with the actual control.
    }
    @Test fun backgroundAndNotificationActionsKeepOneSession() {
        start()
        val id = runtime.timer!!.sessionId
        val manager = app.getSystemService(NotificationManager::class.java)
        waitFor { manager.activeNotifications.any { it.id == TimerService.NOTIFICATION_ID } }
        val before = runtime.timer!!.elapsedMs
        activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        waitFor { runtime.timer!!.elapsedMs >= before + 1200 }
        assertEquals("running", runtime.timer!!.state)
        fun action(title: String) = manager.activeNotifications.single { it.id == TimerService.NOTIFICATION_ID }.notification.actions.single { it.title.toString() == title }.actionIntent
        action("Pausa").send(); waitFor { runtime.timer!!.state == "paused" }
        assertFalse(shell("dumpsys power").toString(Charsets.UTF_8).lineSequence().any { it.contains("PARTIAL_WAKE_LOCK") && it.contains("it.tabatastudio:interval-timer") })
        val pausedAt = runtime.timer!!.elapsedMs
        Thread.sleep(1100)
        assertEquals(pausedAt, runtime.timer!!.elapsedMs)
        waitFor { manager.activeNotifications.single { it.id == TimerService.NOTIFICATION_ID }.notification.actions.any { it.title.toString() == "Riprendi" } }
        action("Riprendi").send(); waitFor { runtime.timer!!.state == "running" }
        action("Salta").send(); waitFor { runtime.timer!!.index >= 1 }
        action("Termina").send(); waitFor { runtime.timer!!.result != null && !runtime.hasUnsavedResult }
        waitFor { !runtime.serviceRunning }
        val sessions = runBlocking { app.repository.read() }.sessions.filter { it.id == id }
        assertEquals(1, sessions.size); assertEquals("INTERRUPTED", sessions.single().status)
        assertTrue(manager.activeNotifications.none { it.id == TimerService.NOTIFICATION_ID })
        assertFalse(shell("dumpsys power").toString(Charsets.UTF_8).lineSequence().any { it.contains("PARTIAL_WAKE_LOCK") && it.contains("it.tabatastudio:interval-timer") })
    }
    @Test fun studySurvivesActivityDestructionAndScreenOff() {
        start("STUDIO")
        val id = runtime.timer!!.sessionId
        activityRule.scenario.close()
        shell("input keyevent KEYCODE_SLEEP")
        assertFalse(app.getSystemService(PowerManager::class.java).isInteractive)
        val before = runtime.timer!!.elapsedMs
        waitFor { runtime.timer!!.elapsedMs > before + 1200 }
        assertEquals("running", runtime.timer!!.state); assertEquals(id, runtime.timer!!.sessionId)
        assertTrue(runtime.serviceRunning)
        shell("input keyevent KEYCODE_WAKEUP"); shell("wm dismiss-keyguard")
        androidx.test.core.app.ActivityScenario.launch(MainActivity::class.java).use { reopened ->
            var vm: StudioViewModel? = null
            reopened.onActivity { vm = androidx.lifecycle.ViewModelProvider(it)[StudioViewModel::class.java] }
            waitFor { vm?.ready == true }
            assertEquals("Timer", vm!!.route); assertEquals("STUDIO", vm!!.mode)
            assertEquals(id, vm!!.timer!!.sessionId)
        }
        main { runtime.stop() }; waitFor { !runtime.hasUnsavedResult }
        assertEquals("STUDIO", runBlocking { app.repository.read() }.sessions.single { it.id == id }.mode)
    }
    @Test fun consecutiveTimersAreSavedWithoutDuplicates() {
        val ids = mutableListOf<String>()
        repeat(3) {
            start(); ids.add(runtime.timer!!.sessionId)
            main { runtime.stop() }; waitFor { !runtime.hasUnsavedResult }
        }
        assertEquals(3, ids.distinct().size)
        val sessions = runBlocking { app.repository.read() }.sessions
        for (id in ids) assertEquals(1, sessions.count { it.id == id })
    }
    @Test fun externalMediaContinuesDuringDuckingCuesWithScreenOff() {
        // This API 37 emulator's public playback callback omits SoundPool. Observe AudioService's
        // real player registrations/start events, restricted to this UID and newly created pools.
        val before = soundPoolIds(shell("dumpsys audio").toString(Charsets.UTF_8))
        ContextCompat.startForegroundService(instrumentation.context, probeIntent())
        val initial = probeStatus()
        assertNotEquals(android.os.Process.myUid(), initial.getInt("uid"))
        assertEquals(AudioManager.AUDIOFOCUS_REQUEST_GRANTED, initial.getInt("focus")); assertTrue(initial.getBoolean("playing"))
        start(); activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        shell("input keyevent KEYCODE_SLEEP")
        assertFalse(app.getSystemService(PowerManager::class.java).isInteractive)
        waitFor { runtime.timer!!.result != null && !runtime.hasUnsavedResult }
        val after = probeStatus()
        assertTrue(after.getBoolean("playing")); assertTrue(after.getIntArray("changes")!!.none { it == AudioManager.AUDIOFOCUS_LOSS || it == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT })
        val audioDump = shell("dumpsys audio").toString(Charsets.UTF_8)
        val starts = (soundPoolIds(audioDump) - before).sumOf { id -> Regex("player piid:$id event:started").findAll(audioDump).count() }
        assertTrue("Avvisi non rilevati da AudioService: $starts", starts >= 3)
        assertEquals("COMPLETED", runtime.timer!!.result!!.status)
    }
    private fun soundPoolIds(dump: String): Set<String> = Regex("new player piid:(\\d+) uid/pid:${android.os.Process.myUid()}/\\d+ package:it.tabatastudio type:android.media.SoundPool").findAll(dump).map { it.groupValues[1] }.toSet()
    private fun probeIntent() = Intent().setClassName(instrumentation.context.packageName, MediaPlaybackProbeService::class.java.name)
    private fun probeStatus(): Bundle {
        var result: Bundle? = null; val ready = CountDownLatch(1)
        val receiver = object : ResultReceiver(Handler(Looper.getMainLooper())) {
            override fun onReceiveResult(resultCode: Int, data: Bundle) { result = data; ready.countDown() }
        }
        instrumentation.context.startService(probeIntent().putExtra("receiver", receiver))
        assertTrue("Lettore di prova non disponibile", ready.await(10, TimeUnit.SECONDS))
        return result!!
    }
}
