// DOC L0001: Assegna il file al namespace it.tabatastudio.timer, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.timer

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import android.app.Application. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.app.Application
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import android.content.Intent. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.content.Intent
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import android.os.SystemClock. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.os.SystemClock
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import android.os.VibrationEffect. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.os.VibrationEffect
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import android.os.Vibrator. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.os.Vibrator
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.runtime.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.runtime.*
// DOC L0009: Rende disponibili le dipendenze esplicite di questa riga: import androidx.core.content.ContextCompat. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.core.content.ContextCompat
// DOC L0010: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.data.StudioRepository. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.data.StudioRepository
// DOC L0011: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.domain.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.domain.*
// DOC L0012: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.ui.HornSignals. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.ui.HornSignals
// DOC L0013: Rende disponibili le dipendenze esplicite di questa riga: import kotlinx.coroutines.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import kotlinx.coroutines.*

/** One engine per application process. Only TimerService advances it and writes its checkpoint. */
// DOC L0016: Dichiara il tipo o contenitore di questo modulo: Proprietario del motore Android per l’intero processo. I campi e i metodi sono descritti nelle righe successive.
class TimerRuntime(private val app: Application, private val repository: StudioRepository) {
    // DOC L0017: initializationScope: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private val initializationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    // DOC L0018: initialized: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    val initialized = initializationScope.async { repository.initialize() }
    // DOC L0019: timer: motore corrente, opzionale quando nessuna sessione è stata avviata.
    var timer by mutableStateOf<TimerEngine?>(null); private set
    // DOC L0020: version: stato osservabile che fa aggiornare Compose quando il motore cambia.
    var version by mutableIntStateOf(0); private set
    // DOC L0021: archiveRevision: contatore che segnala alla UI una modifica persistente delle sessioni.
    var archiveRevision by mutableIntStateOf(0); private set
    // DOC L0022: message: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    var message by mutableStateOf("")
    // DOC L0023: starting: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    var starting by mutableStateOf(false); private set
    // DOC L0024: sound: preferenza che abilita i segnali, senza modificare i tempi.
    var sound by mutableStateOf(true); private set
    // DOC L0025: vibration: preferenza per il segnale aptico, indipendente dal conteggio.
    var vibration by mutableStateOf(false)
    // DOC L0026: serviceRunning: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    var serviceRunning by mutableStateOf(false); internal set
    // DOC L0027: pending: motore preparato che attende l’avvio del servizio foreground.
    private var pending: TimerEngine? = null
    // DOC L0028: savedSession: ID del risultato già scritto, usato per non salvarlo due volte.
    private var savedSession: String? = null
    // DOC L0029: lastCheckpoint: istante dell’ultimo checkpoint riuscito.
    private var lastCheckpoint = 0L
    // DOC L0030: lastAttempt: istante dell’ultimo tentativo di scrittura, distinto da un salvataggio riuscito.
    private var lastAttempt = 0L
    // DOC L0031: checkpointRequested: richiesta esplicita di scrivere lo stato corrente al prossimo passaggio.
    private var checkpointRequested = false
    // DOC L0032: horn: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private var horn: HornSignals? = null
    // DOC L0033: countdown: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private val countdown = CountdownCues()
    // DOC L0034: transitions: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private val transitions = TransitionCues()
    // DOC L0035: soundTimer: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private var soundTimer: TimerEngine? = null
    // DOC L0036: soundPhase: visita di fase alla quale appartiene l’ultimo segnale.
    private var soundPhase = -1
    // DOC L0037: soundState: stato precedente usato per fermare correttamente un suono.
    private var soundState = "idle"
    // DOC L0038: active: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    val active: Boolean get() = version.let { starting || timer?.state in listOf("running", "paused") }
    // DOC L0039: hasUnsavedResult: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    val hasUnsavedResult: Boolean get() = version.let { timer?.result?.let { savedSession != it.id } == true }

    // DOC L0041: start: Prepara un nuovo motore e richiede l’avvio foreground, impedendo sovrapposizioni o risultati non ancora salvati.
    fun start(workout: Workout) {
        // DOC L0042: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di start.
        check(!active) { "Termina prima la sessione in corso." }
        // DOC L0043: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di start.
        check(!hasUnsavedResult) { "La sessione precedente non è ancora salvata. Esporta una copia JSON prima di ripartire." }
        // DOC L0044: prepared: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di start.
        val prepared = TimerEngine(workout, SystemClock::elapsedRealtime)
        // DOC L0045: Esegue il passaggio esplicito «pending = prepared; starting = true» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di start.
        pending = prepared; starting = true
        // DOC L0046: Esegue il passaggio esplicito «try {» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di start.
        try {
            // DOC L0047: Esegue il passaggio esplicito «ContextCompat.startForegroundService(app, Intent(app, TimerService::class.java).setAction(TimerService.START))» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di start.
            ContextCompat.startForegroundService(app, Intent(app, TimerService::class.java).setAction(TimerService.START))
        // DOC L0048: Esegue il passaggio esplicito «} catch (e: Exception) {» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di start.
        } catch (e: Exception) {
            // DOC L0049: Esegue il passaggio esplicito «pending = null; starting = false» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di start.
            pending = null; starting = false
            // DOC L0050: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti. Nel contesto di start.
            throw IllegalStateException("Avvio in background non disponibile: ${e.message}. Riprova con l’app aperta.", e)
        // DOC L0051: Chiude il costrutto aperto alla riga 48.
        }
    // DOC L0052: Chiude il costrutto aperto alla riga 41.
    }

    // DOC L0054: beginPending: Consegna al servizio il motore preparato e azzera i riferimenti della sessione precedente.
    internal fun beginPending() {
        // DOC L0055: prepared: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di beginPending.
        val prepared = pending ?: return
        // DOC L0056: Esegue il passaggio esplicito «horn?.stop(); timer = prepared; pending = null; savedSession = null» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di beginPending.
        horn?.stop(); timer = prepared; pending = null; savedSession = null
        // DOC L0057: Esegue il passaggio esplicito «timer!!.start(); starting = false; lastCheckpoint = 0; lastAttempt = 0; checkpointRequested = true; version++» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di beginPending.
        timer!!.start(); starting = false; lastCheckpoint = 0; lastAttempt = 0; checkpointRequested = true; version++
    // DOC L0058: Chiude il costrutto aperto alla riga 54.
    }
    // DOC L0059: attachAudio: Crea SoundPool una sola volta quando il servizio è pronto.
    internal fun attachAudio() { if (horn == null) horn = HornSignals(app) }
    // DOC L0060: releaseAudio: Arresta e rilascia i campioni nativi alla chiusura del servizio.
    internal fun releaseAudio() { horn?.release(); horn = null }
    // DOC L0061: chooseSound: Aggiorna la preferenza audio e ferma subito i segnali attivi se viene disabilitata.
    fun chooseSound(enabled: Boolean) { sound = enabled; if (!enabled) horn?.stop() }
    // DOC L0062: pause: Aggiorna prima il tempo trascorso e poi sospende un motore ancora in esecuzione.
    fun pause() = control { it.pause() }
    // DOC L0063: resume: Riprende attraverso il servizio esistente o ne richiede la riapertura con lo stesso ID di sessione.
    fun resume() {
        // DOC L0064: engine: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di resume.
        val engine = timer ?: return
        // DOC L0065: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di resume.
        if (engine.state != "paused") return
        // DOC L0066: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di resume.
        if (!serviceRunning) {
            // DOC L0067: Esegue il passaggio esplicito «starting = true» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di resume.
            starting = true
            // DOC L0068: Esegue il passaggio esplicito «runCatching {» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di resume.
            runCatching {
                // DOC L0069: Esegue il passaggio esplicito «ContextCompat.startForegroundService(app, Intent(app, TimerService::class.java).setAction(TimerService.RESUME).putExtra(TimerService.SESSION, engine.sessionId))» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di resume.
                ContextCompat.startForegroundService(app, Intent(app, TimerService::class.java).setAction(TimerService.RESUME).putExtra(TimerService.SESSION, engine.sessionId))
            // DOC L0070: Esegue il passaggio esplicito «}.onFailure { starting = false; message = "Ripresa non disponibile: ${it.message}. Riprova con l’app aperta." }» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di resume.
            }.onFailure { starting = false; message = "Ripresa non disponibile: ${it.message}. Riprova con l’app aperta." }
        // DOC L0071: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di resume.
        } else { starting = false; control { it.resume() } }
    // DOC L0072: Chiude il costrutto aperto alla riga 63.
    }
    // DOC L0073: pauseResume: Sceglie pausa o ripresa in base allo stato reale del motore.
    fun pauseResume() { if (timer?.state == "paused") resume() else pause() }
    // DOC L0074: skip: Salta la fase dopo aver aggiornato il timer, senza accreditarne il lavoro non svolto.
    fun skip() = control { it.skip() }
    // DOC L0075: previous: Ripete la fase precedente creando una visita nuova senza duplicare gli intervalli già completati.
    fun previous() = control { it.previous() }
    // DOC L0076: stop: Invia al controllo serializzato la conclusione interrotta del timer, con salvataggio del risultato.
    fun stop() = control { it.stop() }
    // DOC L0077: control: Applica un comando al motore attivo, richiede un checkpoint e aggiorna segnali e notifica.
    private fun control(action: (TimerEngine) -> Unit) {
        // DOC L0078: engine: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di control.
        val engine = timer ?: return
        // DOC L0079: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di control.
        if (engine.state !in listOf("running", "paused")) return
        // DOC L0080: Esegue il passaggio esplicito «horn?.stop(); action(engine); version++; checkpointRequested = true; emitCues()» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di control.
        horn?.stop(); action(engine); version++; checkpointRequested = true; emitCues()
        // DOC L0081: Esegue il passaggio esplicito «TimerService.refreshResources()» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di control.
        TimerService.refreshResources()
    // DOC L0082: Chiude il costrutto aperto alla riga 77.
    }
    // DOC L0083: clearFinished: Elimina il riferimento al timer concluso soltanto dopo il salvataggio del risultato.
    fun clearFinished() {
        // DOC L0084: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di clearFinished.
        check(!active && !hasUnsavedResult) { "Attendi il salvataggio della sessione." }
        // DOC L0085: Esegue il passaggio esplicito «horn?.stop(); timer = null; version++» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di clearFinished.
        horn?.stop(); timer = null; version++
    // DOC L0086: Chiude il costrutto aperto alla riga 83.
    }

    // DOC L0088: tickAndSave: Avanza il motore, emette segnali e salva checkpoint o risultato, limitando frequenza e duplicazioni.
    internal suspend fun tickAndSave() {
        // DOC L0089: engine: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di tickAndSave.
        val engine = timer ?: return
        // DOC L0090: running: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di tickAndSave.
        val running = engine.state == "running"
        // DOC L0091: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di tickAndSave.
        engine.tick(); if (running) version++
        // DOC L0092: Esegue il passaggio esplicito «emitCues()» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di tickAndSave.
        emitCues()
        // DOC L0093: now: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di tickAndSave.
        val now = SystemClock.elapsedRealtime()
        // DOC L0094: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di tickAndSave.
        if (now - lastAttempt < 1000 && !checkpointRequested) return
        // DOC L0095: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di tickAndSave.
        if (!hasUnsavedResult && !checkpointRequested && (engine.state != "running" || now - lastCheckpoint < 1000)) return
        // DOC L0096: Esegue il passaggio esplicito «checkpointRequested = false; lastAttempt = now» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di tickAndSave.
        checkpointRequested = false; lastAttempt = now
        // DOC L0097: Esegue il passaggio esplicito «try {» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di tickAndSave.
        try {
            // DOC L0098: result: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di tickAndSave.
            val result = engine.result
            // DOC L0099: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di tickAndSave.
            if (result != null && savedSession != result.id) {
                // DOC L0100: Esegue il passaggio esplicito «repository.finish(result); savedSession = result.id; archiveRevision++» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di tickAndSave.
                repository.finish(result); savedSession = result.id; archiveRevision++
                // DOC L0101: Esegue il passaggio esplicito «message = "Sessione salvata sul telefono."» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di tickAndSave.
                message = "Sessione salvata sul telefono."
            // DOC L0102: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di tickAndSave.
            } else if (result == null) {
                // DOC L0103: Esegue il passaggio esplicito «repository.checkpoint(engine.snapshot()); lastCheckpoint = now» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di tickAndSave.
                repository.checkpoint(engine.snapshot()); lastCheckpoint = now
            // DOC L0104: Chiude il costrutto aperto alla riga 102.
            }
        // DOC L0105: Esegue il passaggio esplicito «} catch (e: Exception) {» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di tickAndSave.
        } catch (e: Exception) {
            // DOC L0106: Esegue il passaggio esplicito «message = "Salvataggio non riuscito: ${e.message}. Esporta una copia JSON prima di chiudere."» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di tickAndSave.
            message = "Salvataggio non riuscito: ${e.message}. Esporta una copia JSON prima di chiudere."
        // DOC L0107: Chiude il costrutto aperto alla riga 105.
        }
    // DOC L0108: Chiude il costrutto aperto alla riga 88.
    }

    // DOC L0110: serviceStopped: Registra la chiusura del servizio, protegge un avvio già in coda e rilascia l’audio.
    internal fun serviceStopped() {
        // DOC L0111: Esegue il passaggio esplicito «serviceRunning = false» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di serviceStopped.
        serviceRunning = false
        // A new foreground-service start may already be queued while the old instance is stopping.
        // DOC L0113: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di serviceStopped.
        if (pending == null) starting = false
        // DOC L0114: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di serviceStopped.
        if (pending == null && timer?.state == "running") { timer?.pause(); version++; message = "Il servizio del timer è stato arrestato. Riapri il timer per riprendere." }
        // DOC L0115: Esegue il passaggio esplicito «releaseAudio()» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di serviceStopped.
        releaseAudio()
    // DOC L0116: Chiude il costrutto aperto alla riga 110.
    }
    // DOC L0117: emitCues: Sincronizza note e cambi turno con il motore corrente, rispettando suoni, vibrazione e visite di fase.
    private fun emitCues() {
        // DOC L0118: engine: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di emitCues.
        val engine = timer ?: return
        // DOC L0119: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di emitCues.
        if (soundTimer !== engine || soundPhase != engine.phaseRun || (soundState != engine.state && engine.state != "running")) {
            // DOC L0120: Esegue il passaggio esplicito «horn?.stop(); soundTimer = engine; soundPhase = engine.phaseRun» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di emitCues.
            horn?.stop(); soundTimer = engine; soundPhase = engine.phaseRun
        // DOC L0121: Chiude il costrutto aperto alla riga 119.
        }
        // DOC L0122: Esegue il passaggio esplicito «soundState = engine.state» nel flusso: Proprietario del motore Android per l’intero processo. Nel contesto di emitCues.
        soundState = engine.state
        // DOC L0123: marker: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di emitCues.
        val marker = transitions.read(engine)
        // DOC L0124: second: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di emitCues.
        val second = countdown.read(engine)
        // DOC L0125: key: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di emitCues.
        val key = marker ?: second?.let { hornSample(it, engine.currentPhase?.type ?: "PREPARE") }
        // DOC L0126: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di emitCues.
        if (key != null) {
            // DOC L0127: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di emitCues.
            if (sound) runCatching { horn?.playCue(key) }
            // DOC L0128: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di emitCues.
            if (vibration) runCatching { app.getSystemService(Vibrator::class.java)?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)) }
        // DOC L0129: Chiude il costrutto aperto alla riga 126.
        }
    // DOC L0130: Chiude il costrutto aperto alla riga 117.
    }
// DOC L0131: Chiude il costrutto aperto alla riga 16.
}
