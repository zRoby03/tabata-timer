package it.tabatastudio.domain

/** Pure read adapter: old records remain in the archive until the edited copy is saved. */
fun normalizeWorkout(w: Workout): Workout {
    if (!w.studio) {
        if (w.timingVersion == 2) return w
        fun active(value: Int, minimum: Int) = if (value > 0) value.coerceAtLeast(minimum) else value
        return w.copy(timingVersion = 2, cooldownSec = active(w.cooldownSec, 10), blocks = w.blocks.mapIndexed { bi, b ->
            b.copy(restAfterBlockSec = if (bi < w.blocks.lastIndex) b.restAfterBlockSec.coerceAtLeast(5) else b.restAfterBlockSec,
                exercises = b.exercises.map { e -> e.copy(workSec = e.workSec.coerceAtLeast(5), restBetweenRepsSec = active(e.restBetweenRepsSec, 5)) })
        })
    }
    if (w.activities != null) return w.copy(blocks = emptyList(), warmupSec = 0, cooldownSec = 0, longBreakSec = 0, longBreakEvery = 4)
    val activities = mutableListOf<StudyActivity>()
    val boundaries = mutableListOf<Int>()
    val ids = mutableSetOf(w.id)
    w.blocks.forEachIndexed { bi, b ->
        repeat(b.cycles) { c ->
            b.exercises.forEachIndexed { ei, e ->
                var id = "legacy-${w.id.take(80)}-${activities.size + 1}"
                while (id in ids) id += "_x"
                require(activities.size < 5000) { "Troppe attività nel programma precedente" }
                ids.add(id)
                activities.add(StudyActivity(id, e.title, e.workSec, e.repetitions, e.restBetweenRepsSec))
                if (ei < b.exercises.lastIndex) boundaries.add(e.restAfterExerciseSec)
            }
            if (c < b.cycles - 1) boundaries.add(b.restBetweenCyclesSec)
        }
        if (bi < w.blocks.lastIndex) boundaries.add(b.restAfterBlockSec)
    }
    return w.copy(blocks = emptyList(), activities = activities, restBetweenActivitiesSec = boundaries.firstOrNull() ?: 300, warmupSec = 0, cooldownSec = 0, longBreakSec = 0, longBreakEvery = 4)
}
fun normalizationNotice(w: Workout): String = when {
    w.studio && w.activities == null -> "Programma precedente: le serie diventano attività; pause lunghe e finali sono escluse. La pausa tra attività usa il primo valore precedente. L’originale resta nell’archivio fino al salvataggio."
    !w.studio && w.timingVersion != 2 && (normalizeWorkout(w).copy(timingVersion = w.timingVersion) != w || w.blocks.any { it.cycles > 1 || it.exercises.size > 1 }) -> "Allenamento precedente: uso il recupero personale anche tra esercizi e cicli; tra blocchi lo sostituisce il recupero del blocco. Applico i nuovi minimi alla sola copia di esecuzione. Controlla la durata in anteprima. L’originale resta nell’archivio fino al salvataggio."
    else -> ""
}

internal fun prepareWorkoutEdit(original: Workout): Pair<Workout, String> {
    val normalized = normalizeWorkout(original)
    val draft = if (normalized.studio) normalized.copy(
        restBetweenActivitiesSec = normalized.restBetweenActivitiesSec.coerceAtLeast(STUDIO_MIN_SECONDS),
        activities = normalized.activities!!.map { it.copy(pauseSec = it.pauseSec.coerceAtLeast(STUDIO_MIN_SECONDS)) }
    ) else normalized.copy(blocks = normalized.blocks.map { block ->
        block.copy(exercises = block.exercises.map { exercise ->
            exercise.copy(restBetweenRepsSec = exercise.restBetweenRepsSec.coerceAtLeast(WORKOUT_MIN_SECONDS))
        })
    })
    val notice = if (draft != normalized) {
        "Le pause a zero sono impostate al minimo nella copia da modificare. Il programma salvato si aggiorna quando salvi."
    } else normalizationNotice(original)
    return draft to notice
}
