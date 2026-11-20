// DOC L0001: Assegna il file al namespace it.tabatastudio, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import android.Manifest. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.Manifest
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import android.os.Build. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.os.Build
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import android.content.pm.PackageManager. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.content.pm.PackageManager
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import androidx.activity.result.contract.ActivityResultContracts. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.activity.result.contract.ActivityResultContracts
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import androidx.core.content.ContextCompat. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.core.content.ContextCompat
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import android.os.Bundle. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.os.Bundle
// DOC L0009: Rende disponibili le dipendenze esplicite di questa riga: import androidx.activity.ComponentActivity. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.activity.ComponentActivity
// DOC L0010: Rende disponibili le dipendenze esplicite di questa riga: import androidx.activity.compose.setContent. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.activity.compose.setContent
// DOC L0011: Rende disponibili le dipendenze esplicite di questa riga: import androidx.activity.viewModels. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.activity.viewModels
// DOC L0012: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.runtime.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.runtime.*
// DOC L0013: Rende disponibili le dipendenze esplicite di questa riga: import android.view.WindowManager. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.view.WindowManager
// DOC L0014: Rende disponibili le dipendenze esplicite di questa riga: import androidx.core.view.WindowCompat. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.core.view.WindowCompat
// DOC L0015: Rende disponibili le dipendenze esplicite di questa riga: import androidx.core.view.WindowInsetsCompat. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.core.view.WindowInsetsCompat
// DOC L0016: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.ui.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.ui.*

// DOC L0018: Dichiara il tipo o contenitore di questo modulo: Punto di ingresso della finestra Android. I campi e i metodi sono descritti nelle righe successive.
class MainActivity : ComponentActivity() {
    // DOC L0019: studio: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private val studio: StudioViewModel by viewModels()
    // DOC L0020: notificationPermission: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        // DOC L0021: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
        if (!granted) studio.message = "Il timer continua in background. Per vedere i controlli nella notifica, abilita le notifiche nelle impostazioni Android."
    // DOC L0022: Chiude il costrutto aperto alla riga 20.
    }
    // DOC L0023: onCreate: Monta Compose, collega l’apertura dalla notifica e gestisce notifiche e opzioni della finestra senza possedere il timer.
    override fun onCreate(savedInstanceState: Bundle?) {
        // DOC L0024: Esegue il passaggio esplicito «super.onCreate(savedInstanceState)» nel flusso: Punto di ingresso della finestra Android. Nel contesto di onCreate.
        super.onCreate(savedInstanceState)
        // DOC L0025: Esegue il passaggio esplicito «volumeControlStream = android.media.AudioManager.STREAM_MUSIC» nel flusso: Punto di ingresso della finestra Android. Nel contesto di onCreate.
        volumeControlStream = android.media.AudioManager.STREAM_MUSIC
        // DOC L0026: Esegue il passaggio esplicito «addOnNewIntentListener { incoming ->» nel flusso: Punto di ingresso della finestra Android. Nel contesto di onCreate.
        addOnNewIntentListener { incoming ->
            // DOC L0027: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di onCreate.
            if (incoming.action == it.tabatastudio.timer.TimerService.OPEN_TIMER) studio.navigate("Timer")
        // DOC L0028: Chiude il costrutto aperto alla riga 26.
        }
        // DOC L0029: Esegue il passaggio esplicito «WindowCompat.getInsetsController(window, window.decorView).apply {» nel flusso: Punto di ingresso della finestra Android. Nel contesto di onCreate.
        WindowCompat.getInsetsController(window, window.decorView).apply {
            // DOC L0030: Esegue il passaggio esplicito «isAppearanceLightStatusBars = true» nel flusso: Punto di ingresso della finestra Android. Nel contesto di onCreate.
            isAppearanceLightStatusBars = true
            // DOC L0031: Esegue il passaggio esplicito «isAppearanceLightNavigationBars = true» nel flusso: Punto di ingresso della finestra Android. Nel contesto di onCreate.
            isAppearanceLightNavigationBars = true
        // DOC L0032: Chiude il costrutto aperto alla riga 29.
        }
        // DOC L0033: Esegue il passaggio esplicito «setContent {» nel flusso: Punto di ingresso della finestra Android. Nel contesto di onCreate.
        setContent {
            // DOC L0034: Esegue il passaggio esplicito «SideEffect {» nel flusso: Punto di ingresso della finestra Android. Nel contesto di onCreate.
            SideEffect {
                // DOC L0035: Esegue il passaggio esplicito «WindowCompat.getInsetsController(window, window.decorView).apply {» nel flusso: Punto di ingresso della finestra Android. Nel contesto di onCreate.
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    // DOC L0036: Esegue il passaggio esplicito «isAppearanceLightStatusBars = !studio.darkTheme» nel flusso: Punto di ingresso della finestra Android. Nel contesto di onCreate.
                    isAppearanceLightStatusBars = !studio.darkTheme
                    // DOC L0037: Esegue il passaggio esplicito «isAppearanceLightNavigationBars = !studio.darkTheme» nel flusso: Punto di ingresso della finestra Android. Nel contesto di onCreate.
                    isAppearanceLightNavigationBars = !studio.darkTheme
                // DOC L0038: Chiude il costrutto aperto alla riga 35.
                }
            // DOC L0039: Chiude il costrutto aperto alla riga 34.
            }
            // DOC L0040: timerActive: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di onCreate.
            val timerActive = studio.active
            // DOC L0041: Esegue il passaggio esplicito «LaunchedEffect(timerActive) {» nel flusso: Punto di ingresso della finestra Android. Nel contesto di onCreate.
            LaunchedEffect(timerActive) {
                // DOC L0042: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di onCreate.
                if (timerActive && Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // DOC L0043: prefs: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di onCreate.
                    val prefs = getSharedPreferences("tabata-appearance", MODE_PRIVATE)
                    // DOC L0044: Legge il valore locale e applica i controlli/fallback riportati nell’espressione. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di onCreate.
                    if (!prefs.getBoolean("notification-requested", false)) {
                        // DOC L0045: Scrive la preferenza o la copia locale prevista, senza inviarla a un server. Nel contesto di onCreate.
                        prefs.edit().putBoolean("notification-requested", true).apply()
                        // DOC L0046: Esegue il passaggio esplicito «notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)» nel flusso: Punto di ingresso della finestra Android. Nel contesto di onCreate.
                        notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    // DOC L0047: Chiude il costrutto aperto alla riga 44.
                    }
                // DOC L0048: Chiude il costrutto aperto alla riga 42.
                }
            // DOC L0049: Chiude il costrutto aperto alla riga 41.
            }
            // DOC L0050: Esegue il passaggio esplicito «DisposableEffect(studio.keepAwake, timerActive) {» nel flusso: Punto di ingresso della finestra Android. Nel contesto di onCreate.
            DisposableEffect(studio.keepAwake, timerActive) {
                // DOC L0051: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di onCreate.
                if (studio.keepAwake && timerActive) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                // DOC L0052: Esegue il passaggio esplicito «onDispose { window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }» nel flusso: Punto di ingresso della finestra Android. Nel contesto di onCreate.
                onDispose { window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
            // DOC L0053: Chiude il costrutto aperto alla riga 50.
            }
            // DOC L0054: Esegue il passaggio esplicito «DisposableEffect(studio.fullscreen) {» nel flusso: Punto di ingresso della finestra Android. Nel contesto di onCreate.
            DisposableEffect(studio.fullscreen) {
                // DOC L0055: controller: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di onCreate.
                val controller = WindowCompat.getInsetsController(window, window.decorView)
                // DOC L0056: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di onCreate.
                if (studio.fullscreen) controller.hide(WindowInsetsCompat.Type.systemBars()) else controller.show(WindowInsetsCompat.Type.systemBars())
                // DOC L0057: Esegue il passaggio esplicito «onDispose { controller.show(WindowInsetsCompat.Type.systemBars()) }» nel flusso: Punto di ingresso della finestra Android. Nel contesto di onCreate.
                onDispose { controller.show(WindowInsetsCompat.Type.systemBars()) }
            // DOC L0058: Chiude il costrutto aperto alla riga 54.
            }
            // DOC L0059: Esegue il passaggio esplicito «StudioApp(studio)» nel flusso: Punto di ingresso della finestra Android. Nel contesto di onCreate.
            StudioApp(studio)
        // DOC L0060: Chiude il costrutto aperto alla riga 33.
        }
    // DOC L0061: Chiude il costrutto aperto alla riga 23.
    }
// DOC L0062: Chiude il costrutto aperto alla riga 18.
}
