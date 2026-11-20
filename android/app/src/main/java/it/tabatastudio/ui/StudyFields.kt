package it.tabatastudio.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import it.tabatastudio.domain.*

@Composable internal fun StudyFields(w: Workout, change: (Workout) -> Unit) {
    val activities = w.activities!!
    fun activity(index: Int, transform: (StudyActivity) -> StudyActivity) { change(w.copy(activities = activities.mapIndexed { i, a -> if (i == index) transform(a) else a })) }
    activities.forEachIndexed { i, a -> key(a.id) {
        Text(tr("Attività {0}", i + 1), style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = a.title, onValueChange = { if (it.length <= 120) activity(i) { a -> a.copy(title = it) } }, label = { Text(tr("Nome attività")) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        StudyNumber("Concentrazione", a.concentrationSec, 60, factor = 60, accessibleLabel = "Concentrazione · Attività ${i + 1}") { value -> activity(i) { it.copy(concentrationSec = value) } }
        StudyNumber("Sessioni", a.sessions, 1, 100, accessibleLabel = "Sessioni · Attività ${i + 1}") { value -> activity(i) { it.copy(sessions = value) } }
        StudyNumber("Pausa", a.pauseSec, factor = 60, accessibleLabel = "Pausa · Attività ${i + 1}") { value -> activity(i) { it.copy(pauseSec = value) } }
        TextButton(enabled = activities.size > 1, onClick = { change(w.copy(activities = activities.filterIndexed { n, _ -> n != i })) }) { Text(tr("Elimina attività {0}", i + 1)) }
        HorizontalDivider()
    } }
    OutlinedButton(enabled = activities.size < 5000, onClick = { change(w.copy(activities = activities + StudyActivity(title = tr("Concentrazione")))) }) { Text(tr("+ Aggiungi attività")) }
    StudyNumber("Pausa tra attività", w.restBetweenActivitiesSec, factor = 60) { change(w.copy(restBetweenActivitiesSec = it)) }
}

@Composable private fun StudyNumber(label: String, value: Int, min: Int = 0, max: Int = 3600, factor: Int = 1, accessibleLabel: String = label, change: (Int) -> Unit) {
    OutlinedTextField(value = if (value < 0) "" else (value / factor).toString(), onValueChange = { if (it.length <= 6) change(it.toIntOrNull()?.let { n -> n * factor } ?: -1) }, label = { Text(tr(label)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), isError = value !in min..max, modifier = Modifier.fillMaxWidth().semantics { contentDescription = tr(accessibleLabel) }, singleLine = true)
}
