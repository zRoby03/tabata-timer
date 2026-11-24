package it.tabatastudio.ui

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import it.tabatastudio.R
import it.tabatastudio.domain.*

class HornSignals(context: Context) {
    private val handler = Handler(Looper.getMainLooper())
    private val manager = context.getSystemService(AudioManager::class.java)
    private val attributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
    private var focusGeneration = 0
    private var focusRequest: AudioFocusRequest? = null
    private val lease = AudioFocusLease(SystemClock::elapsedRealtime, ::requestFocus, ::abandonFocus)
    private val pool = SoundPool.Builder().setMaxStreams(1).setAudioAttributes(attributes).build()
    private val resources = mapOf(
        "work_3" to R.raw.countdown_work_3, "work_2" to R.raw.countdown_work_2, "work_1" to R.raw.countdown_work_1,
        "rest_3" to R.raw.countdown_rest_3, "rest_2" to R.raw.countdown_rest_2, "rest_1" to R.raw.countdown_rest_1,
        "whistle" to R.raw.work_start, "sigh" to R.raw.rest_start, "finish" to R.raw.workout_finish,
        "ding1" to R.raw.workout_ding_1, "ding2" to R.raw.workout_ding_2, "ding3" to R.raw.workout_ding_3, "double_sigh" to R.raw.rest_double
    )
    @Volatile var isReady = false; private set
    val isFocusHeld: Boolean get() = lease.held
    private val samples = mutableMapOf<String, Int>()
    private val loaded = mutableSetOf<Int>()
    private var stream = 0
    private var playbackEnd = 0L
    val remainingPlaybackMs: Long get() = if (stream == 0) 0 else (playbackEnd - SystemClock.elapsedRealtime()).coerceAtLeast(0)
    private val expireFocus = object : Runnable {
        override fun run() {
            lease.expire()
            if (lease.held) handler.postDelayed(this, (lease.until - SystemClock.elapsedRealtime()).coerceAtLeast(1))
        }
    }
    init {
        pool.setOnLoadCompleteListener { _, sample, status ->
            if (status == 0) loaded.add(sample)
            isReady = loaded.size == resources.size
        }
        resources.forEach { (key, resource) -> samples[key] = pool.load(context, resource, 1) }
    }
    private fun requestFocus(): Boolean {
        val generation = ++focusGeneration
        val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
            .setAudioAttributes(attributes).setOnAudioFocusChangeListener({ change ->
                // A queued loss from an abandoned request must not stop the next visit/session.
                if (generation == focusGeneration && change < 0) stop()
            }, handler).build()
        focusRequest = request
        val granted = runCatching { manager.requestAudioFocus(request) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED }.getOrDefault(false)
        if (!granted) { focusRequest = null; focusGeneration++ }
        return granted
    }
    private fun abandonFocus() {
        val previous = focusRequest
        focusRequest = null; focusGeneration++
        if (previous != null) manager.abandonAudioFocusRequest(previous)
    }
    fun play(second: Int, phase: String = "PREPARE"): Int = hornSample(second, phase)?.let { playCue(it) } ?: 0
    fun playCue(key: String, focusMs: Long = cueDurationMs(key)): Int {
        if (!isReady) return 0
        val sample = samples[key] ?: return 0
        if (!lease.hold(focusMs)) return 0
        stopPlayback()
        val volume = cueVolume(key)
        stream = pool.play(sample, volume, volume, 1, 0, 1f)
        if (stream == 0) { stop(); return 0 }
        playbackEnd = SystemClock.elapsedRealtime() + cueDurationMs(key)
        handler.removeCallbacks(expireFocus)
        handler.postDelayed(expireFocus, (lease.until - SystemClock.elapsedRealtime()).coerceAtLeast(1))
        return stream
    }
    fun stopPlayback() { if (stream != 0) pool.stop(stream); stream = 0; playbackEnd = 0 }
    fun stop() { stopPlayback(); handler.removeCallbacks(expireFocus); lease.release() }
    fun release() { stop(); isReady = false; pool.release() }
}
