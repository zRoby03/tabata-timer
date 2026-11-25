package it.tabatastudio.domain

fun compileTimeline(original: Workout): List<Phase> {
    validateWorkout(original)
    val w = normalizeWorkout(original)
    validateWorkout(w)
    val phases = mutableListOf<Phase>()
    var total = 0L
    fun add(type: String, sec: Int, key: String, args: List<String> = emptyList(), b: Block? = null, bi: Int? = null, c: Int? = null, e: Exercise? = null, activityId: String? = null, cue: String? = null) {
        if (sec == 0) return
        total += sec * 1000L
        require(phases.size < 10000 && total <= 86400000L) { "Massimo 10.000 fasi e 24 ore" }
        val label = Regex("\\{(\\d+)\\}").replace(key) { args[it.groupValues[1].toInt()] }
        phases.add(Phase(type, b?.id, e?.id ?: activityId, sec * 1000L, label, key, args, bi, b?.title, c, b?.cycles, cue))
    }
    if (w.studio) {
        w.activities!!.forEachIndexed { i, a ->
            repeat(a.sessions) { session ->
                add("WORK", a.concentrationSec, "Attività {0} · {1} · sessione {2}/{3}", listOf("${i + 1}", a.title, "${session + 1}", "${a.sessions}"), activityId = a.id)
                if (session < a.sessions - 1) add("REST_REP", a.pauseSec, "Pausa {0} — tra sessioni", listOf(a.title), activityId = a.id)
            }
            if (i < w.activities.lastIndex) add("REST_ACTIVITY", w.restBetweenActivitiesSec, "Pausa {0} → {1} — tra attività", listOf(a.title, w.activities[i + 1].title), activityId = a.id)
        }
        return phases
    }
    add("WARMUP", w.warmupSec, "Riscaldamento")
    w.blocks.forEachIndexed { bi, b ->
        repeat(b.cycles) { c ->
            b.exercises.forEachIndexed { ei, e ->
                repeat(e.repetitions) { r ->
                    val cue = if (ei == 0 && r == 0 && (bi > 0 || c > 0)) "ding2" else "ding1"
                    add("WORK", e.workSec, "{0} · giro {1}/{2} · {3}/{4}", listOf(e.title, "${c + 1}", "${b.cycles}", "${r + 1}", "${e.repetitions}"), b, bi, c, e, cue = cue)
                    val nextRep = r < e.repetitions - 1
                    val nextExercise = ei < b.exercises.lastIndex
                    val nextCycle = c < b.cycles - 1
                    if (nextRep || nextExercise || nextCycle) {
                        add(if (nextRep) "REST_REP" else if (nextExercise) "REST_EXERCISE" else "REST_REPEAT", e.restBetweenRepsSec, "Recupero {0}", listOf(e.title), b, bi, c, e)
                    } else if (bi < w.blocks.lastIndex) {
                        add("REST_BLOCK", b.restAfterBlockSec, "Recupero Blocco {0} → {1}", listOf("${bi + 1}", "${bi + 2}"), b, bi, c, e)
                    }
                }
            }
        }
    }
    add("COOLDOWN", w.cooldownSec, "Defaticamento")
    return phases
}
fun plannedMs(phases: List<Phase>): Long = phases.sumOf { it.durationMs }

internal fun compileEditedTimeline(workout: Workout): List<Phase> {
    validateDurationInputs(workout)
    return compileTimeline(workout)
}
