package it.tabatastudio.domain

fun cueVolume(key: String): Float = if (key == "whistle") 1f else if (key == "sigh" || key == "double_sigh") 0.85f else 0.65f
fun cueDurationMs(key: String?): Long = when (key) {
    "whistle", "sigh" -> 500L
    "finish" -> 900L
    "ding1" -> 1050L
    "ding2" -> 1485L
    "ding3" -> 1920L
    "double_sigh" -> 1200L
    null -> 0L
    else -> 120L
}
fun phaseStartCue(phase: Phase?, studio: Boolean): String? = when {
    phase?.type == "WORK" -> if (studio) "whistle" else phase.entryCue ?: "ding1"
    phase?.type?.startsWith("REST_") == true -> if (!studio && phase.type == "REST_BLOCK") "double_sigh" else "sigh"
    else -> null
}
fun cueFocusDuration(engine: TimerEngine, key: String, second: Int?): Long {
    if (second == null) return cueDurationMs(key)
    val next = if (engine.index == engine.lastWorkIndex || engine.nextPhase == null) {
        if (engine.workout.studio) "finish" else "ding3"
    } else phaseStartCue(engine.nextPhase, engine.workout.studio)
    return engine.remainingMs + cueDurationMs(next)
}
class TransitionCues {
    private var engine: TimerEngine? = null
    private var visit = -1
    private var finished = false
    private val announced = mutableSetOf<String>()
    fun read(current: TimerEngine): String? {
        if (engine !== current) { engine = current; visit = -1; finished = false; announced.clear() }
        if (current.standaloneBreak && current.state == "finished") {
            if (finished || "break" !in current.naturalEndings || current.finalOverrunMs > 500) return null
            finished = true
            return if (current.workout.studio) "finish" else "ding3"
        }
        if (current.workout.studio) {
            if (current.state == "finished") {
                if (current.index >= current.phases.size && !finished) { finished = true; return "finish" }
                return null
            }
        } else {
            if (current.manualStop || current.state !in listOf("running", "finished")) return null
            for (ending in listOf("work", "cooldown")) {
                val at = current.naturalEndings[ending] ?: continue
                if (!announced.add(ending)) continue
                val age = current.elapsedMs - at + if (current.state == "finished") current.finalOverrunMs else 0L
                if (age <= 500L) return "ding3"
            }
        }
        if (current.state != "running" || current.phaseRun == visit) return null
        visit = current.phaseRun
        val phase = current.currentPhase ?: return null
        if (phase.durationMs - current.remainingMs > 500) return null
        return phaseStartCue(phase, current.workout.studio)
    }
}
