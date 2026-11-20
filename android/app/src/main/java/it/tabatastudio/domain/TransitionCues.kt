package it.tabatastudio.domain

// Whistle PCM already contains the 1.2 boost; SoundPool accepts gains only up to 1.
fun cueVolume(key: String): Float = if (key == "whistle") 1f else if (key == "sigh") 0.85f else 0.65f

class TransitionCues {
    private var engine: TimerEngine? = null
    private var visit = -1
    private var finished = false
    fun read(current: TimerEngine): String? {
        if (engine !== current) { engine = current; visit = -1; finished = false }
        if (current.state == "finished") {
            if (current.index >= current.phases.size && !finished) { finished = true; return "finish" }
            return null
        }
        if (current.state != "running" || current.phaseRun == visit) return null
        visit = current.phaseRun
        val phase = current.currentPhase ?: return null
        if (phase.type == "COOLDOWN" && !finished) { finished = true; return "finish" }
        if (phase.durationMs - current.remainingMs > 500) return null
        if (phase.type == "WORK") return "whistle"
        return if (phase.type.startsWith("REST_")) "sigh" else null
    }
}
