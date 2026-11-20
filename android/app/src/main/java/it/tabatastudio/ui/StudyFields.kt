// DOC L0001: Assegna il file al namespace it.tabatastudio.ui, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.ui

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.foundation.layout.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.foundation.layout.*
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.foundation.text.KeyboardOptions. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.foundation.text.KeyboardOptions
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.material3.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.material3.*
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.runtime.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.runtime.*
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.Modifier. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.Modifier
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.semantics.contentDescription. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.semantics.contentDescription
// DOC L0009: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.semantics.semantics. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.semantics.semantics
// DOC L0010: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.text.input.KeyboardType. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.text.input.KeyboardType
// DOC L0011: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.domain.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.domain.*

// DOC L0013: StudyFields: Disegna la lista piatta di attività con nome, concentrazione, sessioni e pausa, più la pausa comune tra attività.
@Composable internal fun StudyFields(w: Workout, change: (Workout) -> Unit) {
    // DOC L0014: activities: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di StudyFields.
    val activities = w.activities!!
    // DOC L0015: activity: Aggiorna la sola attività con l’indice indicato, lasciando invariate le altre.
    fun activity(index: Int, transform: (StudyActivity) -> StudyActivity) { change(w.copy(activities = activities.mapIndexed { i, a -> if (i == index) transform(a) else a })) }
    // DOC L0016: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di activity.
    activities.forEachIndexed { i, a -> key(a.id) {
        // DOC L0017: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di activity.
        Text(tr("Attività {0}", i + 1), style = MaterialTheme.typography.titleMedium)
        // DOC L0018: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di activity.
        OutlinedTextField(value = a.title, onValueChange = { if (it.length <= 120) activity(i) { a -> a.copy(title = it) } }, label = { Text(tr("Nome attività")) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        // DOC L0019: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di activity.
        StudyNumber("Concentrazione", a.concentrationSec, 60, factor = 60, accessibleLabel = "Concentrazione · Attività ${i + 1}") { value -> activity(i) { it.copy(concentrationSec = value) } }
        // DOC L0020: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di activity.
        StudyNumber("Sessioni", a.sessions, 1, 100, accessibleLabel = "Sessioni · Attività ${i + 1}") { value -> activity(i) { it.copy(sessions = value) } }
        // DOC L0021: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di activity.
        StudyNumber("Pausa", a.pauseSec, factor = 60, accessibleLabel = "Pausa · Attività ${i + 1}") { value -> activity(i) { it.copy(pauseSec = value) } }
        // DOC L0022: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di activity.
        TextButton(enabled = activities.size > 1, onClick = { change(w.copy(activities = activities.filterIndexed { n, _ -> n != i })) }) { Text(tr("Elimina attività {0}", i + 1)) }
        // DOC L0023: Esegue il passaggio esplicito «HorizontalDivider()» nel flusso: Campi Compose condivisi per Studio. Nel contesto di activity.
        HorizontalDivider()
    // DOC L0024: Chiude il costrutto aperto alla riga 16.
    } }
    // DOC L0025: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di activity.
    OutlinedButton(enabled = activities.size < 5000, onClick = { change(w.copy(activities = activities + StudyActivity(title = tr("Concentrazione")))) }) { Text(tr("+ Aggiungi attività")) }
    // DOC L0026: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di activity.
    StudyNumber("Pausa tra attività", w.restBetweenActivitiesSec, factor = 60) { change(w.copy(restBetweenActivitiesSec = it)) }
// DOC L0027: Chiude il costrutto aperto alla riga 13.
}

// DOC L0029: StudyNumber: Rende un valore numerico Studio usando minuti nella UI e conversione esplicita verso i secondi del modello.
@Composable private fun StudyNumber(label: String, value: Int, min: Int = 0, max: Int = 3600, factor: Int = 1, accessibleLabel: String = label, change: (Int) -> Unit) {
    // DOC L0030: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StudyNumber.
    OutlinedTextField(value = if (value < 0) "" else (value / factor).toString(), onValueChange = { if (it.length <= 6) change(it.toIntOrNull()?.let { n -> n * factor } ?: -1) }, label = { Text(tr(label)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), isError = value !in min..max, modifier = Modifier.fillMaxWidth().semantics { contentDescription = tr(accessibleLabel) }, singleLine = true)
// DOC L0031: Chiude il costrutto aperto alla riga 29.
}
