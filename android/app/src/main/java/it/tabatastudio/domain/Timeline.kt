package it.tabatastudio.domain

fun compileTimeline(original: Workout): List<Phase> {
    validateWorkout(original)
    val w = normalizeWorkout(original)
    validateWorkout(w)
    val phases = mutableListOf<Phase>()
    var total = 0L
    fun add(type: String, sec: Int, label: String, b: String? = null, e: String? = null) {
        if (sec == 0) return
        total += sec * 1000L
        require(phases.size < 10000 && total <= 86400000L) { "Massimo 10.000 fasi e 24 ore" }
        phases.add(Phase(type, b, e, sec * 1000L, label))
    }
    if (w.studio) {
        w.activities!!.forEachIndexed { i, a ->
            repeat(a.sessions) { session ->
                add("WORK", a.concentrationSec, "Attività ${i + 1} · ${a.title} · sessione ${session + 1}/${a.sessions}", e = a.id)
                if (session < a.sessions - 1) add("REST_REP", a.pauseSec, "Pausa tra sessioni", e = a.id)
            }
            if (i < w.activities.lastIndex) add("REST_ACTIVITY", w.restBetweenActivitiesSec, "Pausa tra attività", e = a.id)
        }
        return phases
    }
    add("WARMUP", w.warmupSec, "Riscaldamento")
    w.blocks.forEachIndexed { bi, b ->
        repeat(b.cycles) { c ->
            b.exercises.forEachIndexed { ei, e ->
                repeat(e.repetitions) { r ->
                    add("WORK", e.workSec, if (w.studio) "${e.title} · sessione ${r + 1}/${e.repetitions}" else "${e.title} · giro ${c + 1}/${b.cycles} · ${r + 1}/${e.repetitions}", b.id, e.id)
                    if (r < e.repetitions - 1) add("REST_REP", e.restBetweenRepsSec, if (w.studio) "Pausa" else "Recupero ripetizioni", b.id, e.id)
                }
                if (ei < b.exercises.lastIndex) add("REST_EXERCISE", b.restBetweenExercisesSec!!, "Recupero tra esercizi", b.id, e.id)
            }
            if (c < b.cycles - 1) add("REST_CYCLE", b.restBetweenCyclesSec, if (w.studio) "Pausa tra serie" else "Recupero cicli", b.id)
        }
        if (bi < w.blocks.lastIndex) add("REST_BLOCK", b.restAfterBlockSec, if (w.studio) "Pausa tra blocchi" else "Recupero blocchi", b.id)
    }
    add("COOLDOWN", w.cooldownSec, if (w.studio) "Pausa finale" else "Defaticamento")
    return phases
}
fun plannedMs(phases: List<Phase>): Long = phases.sumOf { it.durationMs }
