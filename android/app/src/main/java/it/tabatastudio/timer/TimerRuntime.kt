package it.tabatastudio.timer

import android.app.Application
import android.content.Intent
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import it.tabatastudio.data.StudioRepository
import it.tabatastudio.domain.*
import it.tabatastudio.ui.HornSignals
import kotlinx.coroutines.*

/** One engine per application process. Only TimerService advances it and writes its checkpoint. */
class TimerRuntime(private val app: Application, private val repository: StudioRepository) {
    private val initializationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    val initialized = initializationScope.async { repository.initialize() }
    var timer by mutableStateOf<TimerEngine?>(null); private set
    var version by mutableIntStateOf(0); private set
    var archiveRevision by mutableIntStateOf(0); private set
    var message by mutableStateOf("")
    var starting by mutableStateOf(false); private set
    var sound by mutableStateOf(true); private set
    var vibration by mutableStateOf(false)
    var serviceRunning by mutableStateOf(false); internal set
    private var pending: TimerEngine? = null
    private var savedSession: String? = null
    private var lastCheckpoint = 0L
    private var lastAttempt = 0L
    private var checkpointRequested = false
    private var horn: HornSignals? = null
    private val countdown = CountdownCues()
    private val transitions = TransitionCues()
    private var soundTimer: TimerEngine? = null
    private var soundPhase = -1
    private var soundState = "idle"
    val active: Boolean get() = version.let { starting || timer?.state in listOf("running", "paused") }
    val hasUnsavedResult: Boolean get() = version.let { timer?.result?.let { savedSession != it.id } == true }

    fun start(workout: Workout) {
        check(!active) { "Termina prima la sessione in corso." }
        check(!hasUnsavedResult) { "La sessione precedente non è ancora salvata. Esporta una copia JSON prima di ripartire." }
        val prepared = TimerEngine(workout, SystemClock::elapsedRealtime)
        pending = prepared; starting = true
        try {
            ContextCompat.startForegroundService(app, Intent(app, TimerService::class.java).setAction(TimerService.START))
        } catch (e: Exception) {
            pending = null; starting = false
            throw IllegalStateException("Avvio in background non disponibile: ${e.message}. Riprova con l’app aperta.", e)
        }
    }

    internal fun beginPending() {
        val prepared = pending ?: return
        horn?.stop(); timer = prepared; pending = null; savedSession = null
        timer!!.start(); starting = false; lastCheckpoint = 0; lastAttempt = 0; checkpointRequested = true; version++
    }
    internal fun attachAudio() { if (horn == null) horn = HornSignals(app) }
    internal fun releaseAudio() { horn?.release(); horn = null }
    fun chooseSound(enabled: Boolean) { sound = enabled; if (!enabled) horn?.stop() }
    fun pause() = control { it.pause() }
    fun resume() {
        val engine = timer ?: return
        if (engine.state != "paused") return
        if (!serviceRunning) {
            starting = true
            runCatching {
                ContextCompat.startForegroundService(app, Intent(app, TimerService::class.java).setAction(TimerService.RESUME).putExtra(TimerService.SESSION, engine.sessionId))
            }.onFailure { starting = false; message = "Ripresa non disponibile: ${it.message}. Riprova con l’app aperta." }
        } else { starting = false; control { it.resume() } }
    }
    fun pauseResume() { if (timer?.state == "paused") resume() else pause() }
    fun skip() = control { it.skip() }
    fun previous() = control { it.previous() }
    fun stop() = control { it.stop() }
    private fun control(action: (TimerEngine) -> Unit) {
        val engine = timer ?: return
        if (engine.state !in listOf("running", "paused")) return
        horn?.stop(); action(engine); version++; checkpointRequested = true; emitCues()
        TimerService.refreshResources()
    }
    fun clearFinished() {
        check(!active && !hasUnsavedResult) { "Attendi il salvataggio della sessione." }
        horn?.stop(); timer = null; version++
    }

    internal suspend fun tickAndSave() {
        val engine = timer ?: return
        val running = engine.state == "running"
        engine.tick(); if (running) version++
        emitCues()
        val now = SystemClock.elapsedRealtime()
        if (now - lastAttempt < 1000 && !checkpointRequested) return
        if (!hasUnsavedResult && !checkpointRequested && (engine.state != "running" || now - lastCheckpoint < 1000)) return
        checkpointRequested = false; lastAttempt = now
        try {
            val result = engine.result
            if (result != null && savedSession != result.id) {
                repository.finish(result); savedSession = result.id; archiveRevision++
                message = "Sessione salvata sul telefono."
            } else if (result == null) {
                repository.checkpoint(engine.snapshot()); lastCheckpoint = now
            }
        } catch (e: Exception) {
            message = "Salvataggio non riuscito: ${e.message}. Esporta una copia JSON prima di chiudere."
        }
    }

    internal fun serviceStopped() {
        serviceRunning = false
        // A new foreground-service start may already be queued while the old instance is stopping.
        if (pending == null) starting = false
        if (pending == null && timer?.state == "running") { timer?.pause(); version++; message = "Il servizio del timer è stato arrestato. Riapri il timer per riprendere." }
        releaseAudio()
    }
    private fun emitCues() {
        val engine = timer ?: return
        if (soundTimer !== engine || soundPhase != engine.phaseRun || (soundState != engine.state && engine.state != "running")) {
            horn?.stop(); soundTimer = engine; soundPhase = engine.phaseRun
        }
        soundState = engine.state
        val marker = transitions.read(engine)
        val second = countdown.read(engine)
        val key = marker ?: second?.let { hornSample(it, engine.currentPhase?.type ?: "PREPARE") }
        if (key != null) {
            if (sound) runCatching { horn?.playCue(key) }
            if (vibration) runCatching { app.getSystemService(Vibrator::class.java)?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)) }
        }
    }
}
