package it.tabatastudio.domain

/** Pure read adapter: old records remain in the archive until the edited copy is saved. */
fun normalizeWorkout(w: Workout): Workout {
    if (!w.studio) return w.copy(blocks = w.blocks.map { b -> b.copy(restBetweenExercisesSec = b.restBetweenExercisesSec ?: b.exercises.firstOrNull()?.restAfterExerciseSec ?: 15) })
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
    !w.studio && w.blocks.any { b -> b.restBetweenExercisesSec == null && b.exercises.dropLast(1).map { it.restAfterExerciseSec }.distinct().size > 1 } -> "Allenamento precedente: il recupero tra esercizi è ora unico per blocco e usa il primo valore precedente. L’originale resta nell’archivio fino al salvataggio."
    else -> ""
}
