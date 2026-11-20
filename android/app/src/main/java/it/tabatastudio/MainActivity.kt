package it.tabatastudio

import android.Manifest
import android.os.Build
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import it.tabatastudio.ui.*

class MainActivity : ComponentActivity() {
    private val studio: StudioViewModel by viewModels()
    private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) studio.message = "Il timer continua in background. Per vedere i controlli nella notifica, abilita le notifiche nelle impostazioni Android."
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        volumeControlStream = android.media.AudioManager.STREAM_MUSIC
        addOnNewIntentListener { incoming ->
            if (incoming.action == it.tabatastudio.timer.TimerService.OPEN_TIMER) studio.navigate("Timer")
        }
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        setContent {
            SideEffect {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !studio.darkTheme
                    isAppearanceLightNavigationBars = !studio.darkTheme
                }
            }
            val timerActive = studio.active
            LaunchedEffect(timerActive) {
                if (timerActive && Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    val prefs = getSharedPreferences("tabata-appearance", MODE_PRIVATE)
                    if (!prefs.getBoolean("notification-requested", false)) {
                        prefs.edit().putBoolean("notification-requested", true).apply()
                        notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
            DisposableEffect(studio.keepAwake, timerActive) {
                if (studio.keepAwake && timerActive) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                onDispose { window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
            }
            DisposableEffect(studio.fullscreen) {
                val controller = WindowCompat.getInsetsController(window, window.decorView)
                if (studio.fullscreen) controller.hide(WindowInsetsCompat.Type.systemBars()) else controller.show(WindowInsetsCompat.Type.systemBars())
                onDispose { controller.show(WindowInsetsCompat.Type.systemBars()) }
            }
            StudioApp(studio)
        }
    }
}
