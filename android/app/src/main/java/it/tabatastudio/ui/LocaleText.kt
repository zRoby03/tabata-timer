package it.tabatastudio.ui

import it.tabatastudio.i18n.AppText

internal fun tr(source: String?, vararg values: Any?): String = AppText.text(source, *values)

internal fun localizedNewWorkout(w: it.tabatastudio.domain.Workout): it.tabatastudio.domain.Workout = w.copy(
    title = tr(w.title), description = tr(w.description),
    blocks = w.blocks.map { localizedNewBlock(it) },
    activities = w.activities?.map { it.copy(title = tr(it.title)) }
)
internal fun localizedNewBlock(b: it.tabatastudio.domain.Block): it.tabatastudio.domain.Block = b.copy(title = tr(b.title), exercises = b.exercises.map { localizedNewExercise(it) })
internal fun localizedNewExercise(e: it.tabatastudio.domain.Exercise): it.tabatastudio.domain.Exercise = e.copy(title = tr(e.title))
