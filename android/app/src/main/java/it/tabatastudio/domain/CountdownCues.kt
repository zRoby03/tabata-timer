package it.tabatastudio.domain

class CountdownCues {
    private var engine: TimerEngine? = null
    private var visit = -1
    private val played = mutableSetOf<Int>()

    fun read(current: TimerEngine): Int? {
        if (engine !== current || visit != current.phaseRun) {
            engine = current; visit = current.phaseRun; played.clear()
        }
        if (current.state != "running") return null
        val second = ((current.remainingMs + 999) / 1000).toInt()
        if (second !in 1..3 || !played.add(second)) return null
        return second
    }
}

fun hornSample(second: Int, phase: String = "PREPARE"): String? {
    if (second !in 1..3) return null
    val kind = if (phase == "WORK") "work" else "rest"
    return "${kind}_$second"
}
