// DOC L0001: Assegna il file al namespace it.tabatastudio.timer, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.timer

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import android.app.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.app.*
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import android.Manifest. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.Manifest
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import android.content.Intent. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.content.Intent
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import android.content.pm.ServiceInfo. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.content.pm.ServiceInfo
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import android.net.Uri. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.net.Uri
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import android.os.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.os.*
// DOC L0009: Rende disponibili le dipendenze esplicite di questa riga: import androidx.core.app.NotificationCompat. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.core.app.NotificationCompat
// DOC L0010: Rende disponibili le dipendenze esplicite di questa riga: import androidx.core.app.ServiceCompat. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.core.app.ServiceCompat
// DOC L0011: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.MainActivity. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.MainActivity
// DOC L0012: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.i18n.AppText. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.i18n.AppText
// DOC L0013: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.R. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.R
// DOC L0014: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.TabataApplication. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.TabataApplication
// DOC L0015: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.domain.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.domain.*
// DOC L0016: Rende disponibili le dipendenze esplicite di questa riga: import kotlinx.coroutines.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import kotlinx.coroutines.*

/** User-started interval timer; the silent notification never competes with other media. */
// DOC L0019: Dichiara il tipo o contenitore di questo modulo: Servizio Android in primo piano per background e schermo spento. I campi e i metodi sono descritti nelle righe successive.
class TimerService : Service() {
    // DOC L0020: scope: ambito coroutine annullato alla chiusura del servizio o del componente.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    // DOC L0021: runtime: proprietario del motore Android per il processo, condiviso tra servizio e UI.
    private val runtime get() = (application as TabataApplication).timerRuntime
    // DOC L0022: notifications: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private val notifications get() = getSystemService(NotificationManager::class.java)
    // DOC L0023: Esegue il passaggio esplicito «private lateinit var wakeLock: PowerManager.WakeLock» nel flusso: Servizio Android in primo piano per background e schermo spento.
    private lateinit var wakeLock: PowerManager.WakeLock
    // DOC L0024: finishedEngine: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private var finishedEngine: TimerEngine? = null
    // DOC L0025: finishDeadline: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private var finishDeadline = 0L
    // DOC L0026: lastNotification: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private var lastNotification = ""
    // DOC L0027: onBind: Dichiara che il servizio non espone un binding client.
    override fun onBind(intent: Intent?) = null

    // DOC L0029: onCreate: Crea canale silenzioso, notifica foreground, wake lock e coroutine che avanza e salva il timer.
    override fun onCreate() {
        // DOC L0030: Esegue il passaggio esplicito «super.onCreate(); current = this» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di onCreate.
        super.onCreate(); current = this
        // DOC L0031: Esegue il passaggio esplicito «notifications.createNotificationChannel(NotificationChannel(CHANNEL, AppText.text("Timer in corso"), NotificationManager.IMPORTANCE_LOW).apply {» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di onCreate.
        notifications.createNotificationChannel(NotificationChannel(CHANNEL, AppText.text("Timer in corso"), NotificationManager.IMPORTANCE_LOW).apply {
            // DOC L0032: Esegue il passaggio esplicito «description = AppText.text("Avanzamento e controlli del timer, anche a schermo spento")» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di onCreate.
            description = AppText.text("Avanzamento e controlli del timer, anche a schermo spento")
            // DOC L0033: Esegue il passaggio esplicito «setSound(null, null); enableVibration(false); setShowBadge(false)» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di onCreate.
            setSound(null, null); enableVibration(false); setShowBadge(false)
        // DOC L0034: Chiude il costrutto aperto alla riga 31.
        })
        // DOC L0035: Richiede il mantenimento della CPU durante il timer Android, distinto dallo schermo acceso. Nel contesto di onCreate.
        wakeLock = getSystemService(PowerManager::class.java).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "$packageName:interval-timer").apply { setReferenceCounted(false) }
        // DOC L0036: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di onCreate.
        ServiceCompat.startForeground(this, NOTIFICATION_ID, notification(), if (Build.VERSION.SDK_INT >= 34) ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE else 0)
        // DOC L0037: Esegue il passaggio esplicito «runtime.serviceRunning = true; runtime.attachAudio()» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di onCreate.
        runtime.serviceRunning = true; runtime.attachAudio()
        // DOC L0038: Esegue il passaggio esplicito «scope.launch {» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di onCreate.
        scope.launch {
            // DOC L0039: Esegue il passaggio esplicito «try {» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di onCreate.
            try {
                // DOC L0040: Esegue il passaggio esplicito «runtime.initialized.await()» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di onCreate.
                runtime.initialized.await()
                // DOC L0041: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di onCreate.
                while (isActive) {
                    // DOC L0042: Esegue il passaggio esplicito «runtime.tickAndSave(); updateResources()» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di onCreate.
                    runtime.tickAndSave(); updateResources()
                    // DOC L0043: engine: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di onCreate.
                    val engine = runtime.timer
                    // DOC L0044: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di onCreate.
                    if (!runtime.active && !runtime.starting) {
                        // DOC L0045: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di onCreate.
                        if (engine !== finishedEngine) { finishedEngine = engine; finishDeadline = SystemClock.elapsedRealtime() + 1200 }
                        // DOC L0046: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di onCreate.
                        if (engine == null || (!runtime.hasUnsavedResult && SystemClock.elapsedRealtime() >= finishDeadline)) { stopForeground(STOP_FOREGROUND_REMOVE); stopSelf(); break }
                    // DOC L0047: Chiude il costrutto aperto alla riga 44.
                    }
                    // DOC L0048: Programma il prossimo aggiornamento; la durata effettiva rimane misurata dall’orologio, non dal numero di aggiornamenti. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di onCreate.
                    delay(if (engine?.state == "paused") 500 else 100)
                // DOC L0049: Chiude il costrutto aperto alla riga 41.
                }
            // DOC L0050: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Nel contesto di onCreate.
            } catch (e: CancellationException) { throw e }
            // DOC L0051: Esegue il passaggio esplicito «catch (e: Exception) { runtime.message = "Servizio del timer non disponibile: ${e.message}"; stopSelf() }» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di onCreate.
            catch (e: Exception) { runtime.message = "Servizio del timer non disponibile: ${e.message}"; stopSelf() }
        // DOC L0052: Chiude il costrutto aperto alla riga 38.
        }
    // DOC L0053: Chiude il costrutto aperto alla riga 29.
    }
    // DOC L0054: onStartCommand: Accetta avvio e controlli solo per la sessione corrente; usa START_NOT_STICKY per non inventare nuovi allenamenti dopo un arresto.
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // DOC L0055: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di onStartCommand.
        if (intent?.action == START) runtime.beginPending()
        // DOC L0056: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di onStartCommand.
        else if (intent?.getStringExtra(SESSION) == runtime.timer?.sessionId && runtime.active) when (intent?.action) {
            // DOC L0057: Esegue il passaggio esplicito «PAUSE -> runtime.pause()» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di onStartCommand.
            PAUSE -> runtime.pause()
            // DOC L0058: Esegue il passaggio esplicito «RESUME -> runtime.resume()» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di onStartCommand.
            RESUME -> runtime.resume()
            // DOC L0059: Esegue il passaggio esplicito «SKIP -> runtime.skip()» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di onStartCommand.
            SKIP -> runtime.skip()
            // DOC L0060: Esegue il passaggio esplicito «STOP -> runtime.stop()» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di onStartCommand.
            STOP -> runtime.stop()
        // DOC L0061: Chiude il costrutto aperto alla riga 56.
        }
        // DOC L0062: Esegue il passaggio esplicito «updateResources()» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di onStartCommand.
        updateResources()
        // A killed/force-stopped process is recovered once from Room, never restarted as a new workout.
        // DOC L0064: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di onStartCommand.
        return START_NOT_STICKY
    // DOC L0065: Chiude il costrutto aperto alla riga 54.
    }
    // DOC L0066: updateResources: Aggiorna wake lock e notifica quando stato, fase o secondo cambiano, lasciando terminare la campanella.
    internal fun updateResources() {
        // DOC L0067: engine: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di updateResources.
        val engine = runtime.timer
        // DOC L0068: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di updateResources.
        if (engine?.state != "finished") { finishedEngine = null; finishDeadline = 0 }
        // DOC L0069: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di updateResources.
        else if (finishedEngine !== engine) { finishedEngine = engine; finishDeadline = SystemClock.elapsedRealtime() + 1200 }
        // DOC L0070: awake: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di updateResources.
        val awake = engine?.state == "running" || (engine?.state == "finished" && SystemClock.elapsedRealtime() < finishDeadline)
        // DOC L0071: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di updateResources.
        if (awake && !wakeLock.isHeld) wakeLock.acquire(24 * 60 * 60 * 1000L + PREPARATION_MS + 5000)
        // DOC L0072: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di updateResources.
        else if (!awake && wakeLock.isHeld) wakeLock.release()
        // DOC L0073: key: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di updateResources.
        val key = "${AppText.language}:${engine?.sessionId}:${engine?.state}:${engine?.index}:${(engine?.remainingMs ?: 0) / 1000}:${runtime.hasUnsavedResult}"
        // DOC L0074: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di updateResources.
        if (key != lastNotification && (Build.VERSION.SDK_INT < 33 || checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED)) {
            // DOC L0075: Esegue il passaggio esplicito «notifications.notify(NOTIFICATION_ID, notification()); lastNotification = key» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di updateResources.
            notifications.notify(NOTIFICATION_ID, notification()); lastNotification = key
        // DOC L0076: Chiude il costrutto aperto alla riga 74.
        }
    // DOC L0077: Chiude il costrutto aperto alla riga 66.
    }
    // DOC L0078: notification: Costruisce notifica tradotta, tempo residuo e controlli collegati a PendingIntent immutabili della sessione.
    private fun notification(): Notification {
        // DOC L0079: engine: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di notification.
        val engine = runtime.timer
        // DOC L0080: paused: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di notification.
        val paused = engine?.state == "paused"
        // DOC L0081: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. label: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di notification.
        val label = when {
            // DOC L0082: Esegue il passaggio esplicito «engine == null -> "Avvio del timer…"» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di notification.
            engine == null -> "Avvio del timer…"
            // DOC L0083: Esegue il passaggio esplicito «paused -> "In pausa"» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di notification.
            paused -> "In pausa"
            // DOC L0084: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di notification.
            engine.state == "finished" -> if (engine.result?.status == "COMPLETED") "Sessione completata" else "Sessione interrotta"
            // DOC L0085: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di notification.
            else -> when (engine.currentPhase?.type) {
                // DOC L0086: Esegue il passaggio esplicito «"PREPARE" -> "Preparazione"» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di notification.
                "PREPARE" -> "Preparazione"
                // DOC L0087: Esegue il passaggio esplicito «"WORK" -> modeLabels(engine.workout.mode).focus» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di notification.
                "WORK" -> modeLabels(engine.workout.mode).focus
                // DOC L0088: Esegue il passaggio esplicito «"COOLDOWN" -> modeLabels(engine.workout.mode).final» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di notification.
                "COOLDOWN" -> modeLabels(engine.workout.mode).final
                // DOC L0089: Esegue il passaggio esplicito «"REST_LONG" -> "Pausa lunga"» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di notification.
                "REST_LONG" -> "Pausa lunga"
                // DOC L0090: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di notification.
                else -> modeLabels(engine.workout.mode).rest
            // DOC L0091: Chiude il costrutto aperto alla riga 85.
            }
        // DOC L0092: Chiude il costrutto aperto alla riga 81.
        }
        // DOC L0093: Collega un’azione esterna della notifica alla sessione, usando i flag dichiarati per aggiornamento e immutabilità. open: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di notification.
        val open = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java).setAction(OPEN_TIMER).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        // DOC L0094: builder: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di notification.
        val builder = NotificationCompat.Builder(this, CHANNEL).setSmallIcon(R.drawable.ic_timer_notification)
            // DOC L0095: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di notification.
            .setContentTitle(engine?.workout?.title ?: "Tabata Timer").setContentText("${AppText.text(label)} · ${formatTime(engine?.remainingMs ?: 0)}")
            // DOC L0096: Esegue il passaggio esplicito «.setContentIntent(open).setOngoing(true).setOnlyAlertOnce(true).setSilent(true).setShowWhen(false)» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di notification.
            .setContentIntent(open).setOngoing(true).setOnlyAlertOnce(true).setSilent(true).setShowWhen(false)
            // DOC L0097: Esegue il passaggio esplicito «.setCategory(NotificationCompat.CATEGORY_PROGRESS).setVisibility(NotificationCompat.VISIBILITY_PRIVATE)» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di notification.
            .setCategory(NotificationCompat.CATEGORY_PROGRESS).setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
        // DOC L0098: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di notification.
        if (engine != null && engine.state in listOf("running", "paused")) {
            // DOC L0099: session: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di notification.
            val session = engine.sessionId
            // DOC L0100: action: Aggiunge un comando di notifica con URI e ID della sessione, evitando comandi riferiti a timer precedenti.
            fun action(command: String, title: String, icon: Int) {
                // DOC L0101: intent: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di action.
                val intent = Intent(this, TimerService::class.java).setAction(command).putExtra(SESSION, session).setData(Uri.parse("tabata://timer/$session/$command"))
                // DOC L0102: Collega un’azione esterna della notifica alla sessione, usando i flag dichiarati per aggiornamento e immutabilità. Nel contesto di action.
                builder.addAction(icon, AppText.text(title), PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
            // DOC L0103: Chiude il costrutto aperto alla riga 100.
            }
            // DOC L0104: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di action.
            action(if (paused) RESUME else PAUSE, if (paused) "Riprendi" else "Pausa (timer)", if (paused) android.R.drawable.ic_media_play else android.R.drawable.ic_media_pause)
            // DOC L0105: Esegue il passaggio esplicito «action(SKIP, "Salta", android.R.drawable.ic_media_next)» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di action.
            action(SKIP, "Salta", android.R.drawable.ic_media_next)
            // DOC L0106: Esegue il passaggio esplicito «action(STOP, "Termina", android.R.drawable.ic_menu_close_clear_cancel)» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di action.
            action(STOP, "Termina", android.R.drawable.ic_menu_close_clear_cancel)
        // DOC L0107: Chiude il costrutto aperto alla riga 98.
        }
        // DOC L0108: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di action.
        return builder.build()
    // DOC L0109: Chiude il costrutto aperto alla riga 78.
    }
    // DOC L0110: onDestroy: Cancella la coroutine, rilascia il wake lock e notifica al runtime la chiusura del servizio.
    override fun onDestroy() {
        // DOC L0111: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di onDestroy.
        if (current === this) current = null
        // DOC L0112: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di onDestroy.
        scope.cancel(); if (::wakeLock.isInitialized && wakeLock.isHeld) wakeLock.release()
        // DOC L0113: Esegue il passaggio esplicito «runtime.serviceStopped(); super.onDestroy()» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di onDestroy.
        runtime.serviceStopped(); super.onDestroy()
    // DOC L0114: Chiude il costrutto aperto alla riga 110.
    }
    // DOC L0115: Esegue il passaggio esplicito «companion object {» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di onDestroy.
    companion object {
        // DOC L0116: val: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di onDestroy.
        const val CHANNEL = "tabata-running-timer"
        // DOC L0117: val: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di onDestroy.
        const val NOTIFICATION_ID = 41
        // DOC L0118: val: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di onDestroy.
        const val START = "it.tabatastudio.timer.START"
        // DOC L0119: val: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di onDestroy.
        const val PAUSE = "it.tabatastudio.timer.PAUSE"
        // DOC L0120: val: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di onDestroy.
        const val RESUME = "it.tabatastudio.timer.RESUME"
        // DOC L0121: val: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di onDestroy.
        const val SKIP = "it.tabatastudio.timer.SKIP"
        // DOC L0122: val: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di onDestroy.
        const val STOP = "it.tabatastudio.timer.STOP"
        // DOC L0123: val: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di onDestroy.
        const val OPEN_TIMER = "it.tabatastudio.timer.OPEN"
        // DOC L0124: Esegue il passaggio esplicito «internal const val SESSION = "session"» nel flusso: Servizio Android in primo piano per background e schermo spento. Nel contesto di onDestroy.
        internal const val SESSION = "session"
        // DOC L0125: current: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di onDestroy.
        private var current: TimerService? = null
        // DOC L0126: refreshResources: Invita l’istanza del servizio, se presente, ad aggiornare notifica e risorse.
        internal fun refreshResources() { current?.updateResources() }
    // DOC L0127: Chiude il costrutto aperto alla riga 115.
    }
// DOC L0128: Chiude il costrutto aperto alla riga 19.
}
