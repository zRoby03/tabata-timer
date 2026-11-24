package it.tabatastudio.domain

/** Short renewable lease. The Android adapter schedules expire(); no delayed cue is queued. */
class AudioFocusLease(private val now: () -> Long, private val request: () -> Boolean, private val abandon: () -> Unit) {
    var held = false; private set
    var until = 0L; private set
    fun hold(durationMs: Long): Boolean {
        expire()
        if (!held && !request()) return false
        held = true
        until = maxOf(until, now() + durationMs.coerceAtLeast(0))
        return true
    }
    fun expire() { if (held && now() >= until) release() }
    fun release() {
        if (!held) return
        held = false; until = 0
        abandon()
    }
}
