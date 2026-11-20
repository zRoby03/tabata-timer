package it.tabatastudio.timer

import android.app.*
import android.Manifest
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import it.tabatastudio.MainActivity
import it.tabatastudio.i18n.AppText
import it.tabatastudio.R
import it.tabatastudio.TabataApplication
import it.tabatastudio.domain.*
import kotlinx.coroutines.*

/** User-started interval timer; the silent notification never competes with other media. */
class TimerService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val runtime get() = (application as TabataApplication).timerRuntime
    private val notifications get() = getSystemService(NotificationManager::class.java)
    private lateinit var wakeLock: PowerManager.WakeLock
    private var finishedEngine: TimerEngine? = null
    private var finishDeadline = 0L
    private var lastNotification = ""
    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate(); current = this
        notifications.createNotificationChannel(NotificationChannel(CHANNEL, AppText.text("Timer in corso"), NotificationManager.IMPORTANCE_LOW).apply {
            description = AppText.text("Avanzamento e controlli del timer, anche a schermo spento")
            setSound(null, null); enableVibration(false); setShowBadge(false)
        })
        wakeLock = getSystemService(PowerManager::class.java).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "$packageName:interval-timer").apply { setReferenceCounted(false) }
        ServiceCompat.startForeground(this, NOTIFICATION_ID, notification(), if (Build.VERSION.SDK_INT >= 34) ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE else 0)
        runtime.serviceRunning = true; runtime.attachAudio()
        scope.launch {
            try {
                runtime.initialized.await()
                while (isActive) {
                    runtime.tickAndSave(); updateResources()
                    val engine = runtime.timer
                    if (!runtime.active && !runtime.starting) {
                        if (engine !== finishedEngine) { finishedEngine = engine; finishDeadline = SystemClock.elapsedRealtime() + 1200 }
                        if (engine == null || (!runtime.hasUnsavedResult && SystemClock.elapsedRealtime() >= finishDeadline)) { stopForeground(STOP_FOREGROUND_REMOVE); stopSelf(); break }
                    }
                    delay(if (engine?.state == "paused") 500 else 100)
                }
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) { runtime.message = "Servizio del timer non disponibile: ${e.message}"; stopSelf() }
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == START) runtime.beginPending()
        else if (intent?.getStringExtra(SESSION) == runtime.timer?.sessionId && runtime.active) when (intent?.action) {
            PAUSE -> runtime.pause()
            RESUME -> runtime.resume()
            SKIP -> runtime.skip()
            STOP -> runtime.stop()
        }
        updateResources()
        // A killed/force-stopped process is recovered once from Room, never restarted as a new workout.
        return START_NOT_STICKY
    }
    internal fun updateResources() {
        val engine = runtime.timer
        if (engine?.state != "finished") { finishedEngine = null; finishDeadline = 0 }
        else if (finishedEngine !== engine) { finishedEngine = engine; finishDeadline = SystemClock.elapsedRealtime() + 1200 }
        val awake = engine?.state == "running" || (engine?.state == "finished" && SystemClock.elapsedRealtime() < finishDeadline)
        if (awake && !wakeLock.isHeld) wakeLock.acquire(24 * 60 * 60 * 1000L + PREPARATION_MS + 5000)
        else if (!awake && wakeLock.isHeld) wakeLock.release()
        val key = "${AppText.language}:${engine?.sessionId}:${engine?.state}:${engine?.index}:${(engine?.remainingMs ?: 0) / 1000}:${runtime.hasUnsavedResult}"
        if (key != lastNotification && (Build.VERSION.SDK_INT < 33 || checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED)) {
            notifications.notify(NOTIFICATION_ID, notification()); lastNotification = key
        }
    }
    private fun notification(): Notification {
        val engine = runtime.timer
        val paused = engine?.state == "paused"
        val label = when {
            engine == null -> "Avvio del timer…"
            paused -> "In pausa"
            engine.state == "finished" -> if (engine.result?.status == "COMPLETED") "Sessione completata" else "Sessione interrotta"
            else -> when (engine.currentPhase?.type) {
                "PREPARE" -> "Preparazione"
                "WORK" -> modeLabels(engine.workout.mode).focus
                "COOLDOWN" -> modeLabels(engine.workout.mode).final
                "REST_LONG" -> "Pausa lunga"
                else -> modeLabels(engine.workout.mode).rest
            }
        }
        val open = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java).setAction(OPEN_TIMER).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this, CHANNEL).setSmallIcon(R.drawable.ic_timer_notification)
            .setContentTitle(engine?.workout?.title ?: "Tabata Timer").setContentText("${AppText.text(label)} · ${formatTime(engine?.remainingMs ?: 0)}")
            .setContentIntent(open).setOngoing(true).setOnlyAlertOnce(true).setSilent(true).setShowWhen(false)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS).setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
        if (engine != null && engine.state in listOf("running", "paused")) {
            val session = engine.sessionId
            fun action(command: String, title: String, icon: Int) {
                val intent = Intent(this, TimerService::class.java).setAction(command).putExtra(SESSION, session).setData(Uri.parse("tabata://timer/$session/$command"))
                builder.addAction(icon, AppText.text(title), PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
            }
            action(if (paused) RESUME else PAUSE, if (paused) "Riprendi" else "Pausa (timer)", if (paused) android.R.drawable.ic_media_play else android.R.drawable.ic_media_pause)
            action(SKIP, "Salta", android.R.drawable.ic_media_next)
            action(STOP, "Termina", android.R.drawable.ic_menu_close_clear_cancel)
        }
        return builder.build()
    }
    override fun onDestroy() {
        if (current === this) current = null
        scope.cancel(); if (::wakeLock.isInitialized && wakeLock.isHeld) wakeLock.release()
        runtime.serviceStopped(); super.onDestroy()
    }
    companion object {
        const val CHANNEL = "tabata-running-timer"
        const val NOTIFICATION_ID = 41
        const val START = "it.tabatastudio.timer.START"
        const val PAUSE = "it.tabatastudio.timer.PAUSE"
        const val RESUME = "it.tabatastudio.timer.RESUME"
        const val SKIP = "it.tabatastudio.timer.SKIP"
        const val STOP = "it.tabatastudio.timer.STOP"
        const val OPEN_TIMER = "it.tabatastudio.timer.OPEN"
        internal const val SESSION = "session"
        private var current: TimerService? = null
        internal fun refreshResources() { current?.updateResources() }
    }
}
