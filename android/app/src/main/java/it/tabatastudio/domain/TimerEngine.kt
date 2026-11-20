package it.tabatastudio.domain

const val PREPARATION_MS = 20000L
private val preparation = Phase("PREPARE", null, null, PREPARATION_MS, "Preparazione")

class TimerEngine(val workout: Workout, private val now: () -> Long, private val wall: () -> String = ::isoNow) {
    val phases = compileTimeline(workout)
    var index = -1; private set
    var remainingMs = PREPARATION_MS; private set
    var phaseRun = 0; private set
    val currentPhase: Phase? get() = if (index < 0) preparation else phases.getOrNull(index)
    val nextPhase: Phase? get() = phases.getOrNull(index + 1)
    var state = "idle"; private set
    var elapsedMs = 0L; private set
    var workMs = 0L; private set
    var pauses = 0; private set
    var result: Session? = null; private set
    private val completed = mutableSetOf<Int>()
    private val completedPhases = mutableSetOf<Int>()
    private var last = 0L
    val sessionId = uid()
    private var startedAt = ""
    fun start() { if (state == "idle") { startedAt = wall(); last = now(); state = "running" } }
    fun tick() {
        if (state != "running") return
        val current = now(); var delta = (current - last).coerceAtLeast(0); last = current
        while (state == "running" && delta >= remainingMs) {
            consume(remainingMs); delta -= remainingMs
            if (index >= 0) {
                completedPhases.add(index)
                if (currentPhase?.type == "WORK") completed.add(index)
            }
            advance()
        }
        if (state == "running") { consume(delta); remainingMs -= delta }
    }
    private fun consume(ms: Long) {
        if (index < 0) return // Preparation is outside the planned workout and its statistics.
        elapsedMs += ms; if (currentPhase?.type == "WORK") workMs += ms
    }
    private fun advance() {
        index++; phaseRun++
        if (index >= phases.size) finish(if (completed.size == phases.count { it.type == "WORK" }) "COMPLETED" else "INTERRUPTED") else remainingMs = phases[index].durationMs
    }
    fun pause() { tick(); if (state == "running") { state = "paused"; pauses++ } }
    fun resume() { if (state == "paused") { last = now(); state = "running" } }
    fun skip() { tick(); if (state in listOf("running", "paused")) advance() }
    fun previous() {
        tick()
        if (state in listOf("running", "paused")) {
            if (index >= 0) index = (index - 1).coerceAtLeast(0)
            phaseRun++; remainingMs = currentPhase!!.durationMs; last = now()
        }
    }
    fun stop() { tick(); if (state in listOf("running", "paused")) finish("INTERRUPTED") }
    fun snapshot(): Session = result ?: Session(sessionId, workout.id, workout.title, startedAt, wall(), plannedMs(phases), elapsedMs, workMs, completed.size, phases.count { it.type == "WORK" }, pauses, "INTERRUPTED", completedPhases.size, phases.size, workout.mode)
    private fun finish(status: String) { if (result != null) return; result = snapshot().copy(status = status); state = "finished"; remainingMs = 0 }
}
