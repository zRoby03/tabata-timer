package it.tabatastudio.ui

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import it.tabatastudio.R
import it.tabatastudio.domain.hornSample
import it.tabatastudio.domain.cueVolume

class HornSignals(context: Context) {
    private val pool = SoundPool.Builder().setMaxStreams(1)
        // Short cues mix into the media stream. Never request focus, pause or duck another player.
        .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
        .build()
    @Volatile var isReady = false
        private set
    private val samples = mutableMapOf<String, Int>()
    private val loaded = mutableSetOf<Int>()
    private var stream = 0

    init {
        pool.setOnLoadCompleteListener { _, sample, status ->
            if (status == 0) loaded.add(sample)
            isReady = loaded.size == 9
        }
        mapOf(
            "work_3" to R.raw.countdown_work_3, "work_2" to R.raw.countdown_work_2, "work_1" to R.raw.countdown_work_1,
            "rest_3" to R.raw.countdown_rest_3, "rest_2" to R.raw.countdown_rest_2, "rest_1" to R.raw.countdown_rest_1,
            "whistle" to R.raw.work_start, "sigh" to R.raw.rest_start, "finish" to R.raw.workout_finish
        ).forEach { (key, resource) -> samples[key] = pool.load(context, resource, 1) }
    }
    fun play(second: Int, phase: String = "PREPARE"): Int {
        val key = hornSample(second, phase) ?: return 0
        return playCue(key)
    }
    fun playCue(key: String): Int {
        if (!isReady) return 0
        val sample = samples[key] ?: return 0
        stop()
        val volume = cueVolume(key)
        stream = pool.play(sample, volume, volume, 1, 0, 1f)
        return stream
    }
    fun stop() { if (stream != 0) pool.stop(stream); stream = 0 }
    fun release() { stop(); isReady = false; pool.release() }
}
