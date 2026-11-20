// DOC L0001: Applica l’annotazione dichiarata: Compose identifica UI, Room genera accessi e schema, mentre le annotazioni Test selezionano i casi da eseguire.
@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
// DOC L0002: Assegna il file al namespace it.tabatastudio.ui, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.ui

// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.foundation.layout.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.foundation.layout.*
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.foundation.text.KeyboardOptions. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.foundation.text.KeyboardOptions
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.material3.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.material3.*
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.runtime.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.runtime.*
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.Modifier. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.Modifier
// DOC L0009: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.text.input.KeyboardType. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.text.input.KeyboardType
// DOC L0010: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.unit.sp. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.unit.sp
// DOC L0011: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.domain.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.domain.*

// DOC L0013: Field: Rende un campo testo Compose, collegando etichetta, valore e callback della bozza.
@Composable private fun Field(label: String, value: String, max: Int = 120, onChange: (String) -> Unit) { OutlinedTextField(value = value, onValueChange = { if (it.length <= max) onChange(it) }, label = { Text(tr(label)) }, modifier = Modifier.fillMaxWidth(), minLines = if (max > 120) 2 else 1, singleLine = max <= 120) }
// DOC L0014: NumberField: Rende un campo numerico controllato; la validazione completa avviene prima di anteprima, salvataggio e avvio.
@Composable private fun NumberField(label: String, value: Int, min: Int = 0, max: Int = 3600, factor: Int = 1, onChange: (Int) -> Unit) { OutlinedTextField(value = if (value < 0) "" else (value / factor).toString(), onValueChange = { if (it.length <= 6) onChange(it.toIntOrNull()?.let { n -> n * factor } ?: -1) }, label = { Text(tr(label)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), isError = value !in min..max, supportingText = if (value !in min..max) ({ Text(tr("Inserisci un intero {0}–{1}", min / factor, max / factor)) }) else null, modifier = Modifier.fillMaxWidth(), singleLine = true) }
// DOC L0015: move: Sposta l’elemento nella lista senza modificarne identità e contenuto, verificando la destinazione.
private fun <T> move(items: List<T>, index: Int, delta: Int): List<T> { val to = index + delta; if (to !in items.indices) return items; return items.toMutableList().also { val temp = it[index]; it[index] = it[to]; it[to] = temp } }
// DOC L0016: Editor: Compone l’editor: attività piatte per Studio oppure blocchi/esercizi per Workout, con anteprima compilata dal dominio.
@Composable internal fun Editor(vm: StudioViewModel) {
    // DOC L0017: w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di Editor.
    val w = vm.draft ?: return
    // DOC L0018: factor: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di Editor.
    val factor = 1
    // DOC L0019: labels: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di Editor.
    val labels = vm.labels
    // DOC L0020: Esegue il passaggio esplicito «Page {» nel flusso: Editor Compose dei blocchi Workout e delle attività Studio. Nel contesto di Editor.
    Page {
        // DOC L0021: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di Editor.
        Text(if (vm.data.workouts.any { it.id == w.id }) tr("Modifica {0}", tr(labels.noun)) else tr("Crea {0}", tr(labels.noun)), style = MaterialTheme.typography.headlineMedium)
        // DOC L0022: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di Editor.
        if (vm.draftNotice.isNotEmpty()) Text(tr(vm.draftNotice), color = MaterialTheme.colorScheme.onSurfaceVariant)
        // DOC L0023: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di Editor.
        if (w.studio) {
            // DOC L0024: Esegue il passaggio esplicito «Panel {» nel flusso: Editor Compose dei blocchi Workout e delle attività Studio. Nel contesto di Editor.
            Panel {
                // DOC L0025: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
                Field("Nome del programma", w.title) { vm.update(w.copy(title = it)) }
                // DOC L0026: Esegue il passaggio esplicito «StudyFields(w, vm::update)» nel flusso: Editor Compose dei blocchi Workout e delle attività Studio. Nel contesto di Editor.
                StudyFields(w, vm::update)
            // DOC L0027: Chiude il costrutto aperto alla riga 24.
            }
        // DOC L0028: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di Editor.
        } else {
        // DOC L0029: Esegue il passaggio esplicito «Panel {» nel flusso: Editor Compose dei blocchi Workout e delle attività Studio. Nel contesto di Editor.
        Panel {
            // DOC L0030: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
            Field(if (w.studio) "Materia o attività" else "Nome dell’allenamento", w.title) { vm.update(w.copy(title = it)) }
            // DOC L0031: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
            Field("Descrizione", w.description, 2000) { vm.update(w.copy(description = it)) }
            // DOC L0032: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di Editor.
            Text(tr("Colore"), style = MaterialTheme.typography.labelLarge)
            // DOC L0033: Esegue il passaggio esplicito «Buttons {» nel flusso: Editor Compose dei blocchi Workout e delle attività Studio. Nel contesto di Editor.
            Buttons {
                // DOC L0034: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di Editor.
                listOf("Viola" to "#9153B7", "Blu" to "#3367B0", "Verde" to "#287D68", "Rosa" to "#B54079", "Arancio" to "#B56825").forEach { (name, hex) ->
                    // DOC L0035: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
                    FilterChip(selected = w.color.equals(hex, ignoreCase = true), onClick = { vm.update(w.copy(color = hex)) }, label = { Text(tr(name)) })
                // DOC L0036: Chiude il costrutto aperto alla riga 34.
                }
            // DOC L0037: Chiude il costrutto aperto alla riga 33.
            }
            // DOC L0038: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
            NumberField("${labels.final}", w.cooldownSec, factor = factor) { vm.update(w.copy(cooldownSec = it)) }
        // DOC L0039: Chiude il costrutto aperto alla riga 29.
        }
        // DOC L0040: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di Editor.
        w.blocks.forEachIndexed { bi, b -> key(b.id) { Panel {
            // DOC L0041: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di Editor.
            Text(tr("Blocco {0}", bi + 1), style = MaterialTheme.typography.titleLarge)
            // DOC L0042: Esegue il passaggio esplicito «Buttons {» nel flusso: Editor Compose dei blocchi Workout e delle attività Studio. Nel contesto di Editor.
            Buttons {
                // DOC L0043: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
                OutlinedButton(onClick = { vm.update(w.copy(blocks = move(w.blocks, bi, -1))) }, enabled = bi > 0) { Text(tr("↑ Blocco")) }
                // DOC L0044: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
                OutlinedButton(onClick = { vm.update(w.copy(blocks = move(w.blocks, bi, 1))) }, enabled = bi < w.blocks.lastIndex) { Text(tr("↓ Blocco")) }
                // DOC L0045: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
                TextButton(onClick = { val copy = b.copy(id = uid(), exercises = b.exercises.map { it.copy(id = uid()) }); vm.update(w.copy(blocks = w.blocks.toMutableList().also { it.add(bi + 1, copy) })) }) { Text(tr("Duplica blocco")) }
                // DOC L0046: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
                TextButton(enabled = w.blocks.size > 1, onClick = { vm.confirm("Eliminare il blocco?") { vm.update(w.copy(blocks = w.blocks.filterIndexed { i, _ -> i != bi })) } }) { Text(tr("Elimina blocco")) }
            // DOC L0047: Chiude il costrutto aperto alla riga 42.
            }
            // DOC L0048: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
            Field("Nome blocco", b.title) { value -> vm.block(bi) { it.copy(title = value) } }
            // DOC L0049: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
            NumberField("Cicli", b.cycles, 1, 100) { value -> vm.block(bi) { it.copy(cycles = value) } }
            // DOC L0050: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
            NumberField("${if (w.studio) "Pausa" else "Recupero"} tra cicli", b.restBetweenCyclesSec, factor = factor) { value -> vm.block(bi) { it.copy(restBetweenCyclesSec = value) } }
            // DOC L0051: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
            NumberField("Recupero tra blocchi", b.restAfterBlockSec) { value -> vm.block(bi) { it.copy(restAfterBlockSec = value) } }
            // DOC L0052: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
            NumberField("Recupero tra esercizi", b.restBetweenExercisesSec!!) { value -> vm.block(bi) { it.copy(restBetweenExercisesSec = value) } }
            // DOC L0053: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di Editor.
            b.exercises.forEachIndexed { ei, e -> key(e.id) {
                // DOC L0054: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di Editor.
                HorizontalDivider(); Text(tr("{0} {1}", if (w.studio) tr("Attività") else tr("Esercizio"), ei + 1), color = MaterialTheme.colorScheme.primary)
                // DOC L0055: Esegue il passaggio esplicito «Buttons {» nel flusso: Editor Compose dei blocchi Workout e delle attività Studio. Nel contesto di Editor.
                Buttons {
                    // DOC L0056: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
                    OutlinedButton(onClick = { vm.block(bi) { it.copy(exercises = move(it.exercises, ei, -1)) } }, enabled = ei > 0) { Text(tr("↑ Esercizio")) }
                    // DOC L0057: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
                    OutlinedButton(onClick = { vm.block(bi) { it.copy(exercises = move(it.exercises, ei, 1)) } }, enabled = ei < b.exercises.lastIndex) { Text(tr("↓ Esercizio")) }
                    // DOC L0058: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
                    TextButton(onClick = { vm.block(bi) { it.copy(exercises = it.exercises.toMutableList().also { list -> list.add(ei + 1, e.copy(id = uid())) }) } }) { Text(tr("Duplica")) }
                    // DOC L0059: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
                    TextButton(enabled = b.exercises.size > 1, onClick = { vm.confirm("Eliminare l’esercizio?") { vm.block(bi) { it.copy(exercises = it.exercises.filterIndexed { i, _ -> i != ei }) } } }) { Text(tr("Elimina")) }
                // DOC L0060: Chiude il costrutto aperto alla riga 55.
                }
                // DOC L0061: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
                Field(if (w.studio) "Materia o attività" else "Nome esercizio", e.title) { value -> vm.exercise(bi, ei) { it.copy(title = value) } }
                // DOC L0062: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
                NumberField("${labels.focus}", e.workSec, factor, factor = factor) { value -> vm.exercise(bi, ei) { it.copy(workSec = value) } }
                // DOC L0063: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
                NumberField(labels.rounds, e.repetitions, 1, 100) { value -> vm.exercise(bi, ei) { it.copy(repetitions = value) } }
                // DOC L0064: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
                NumberField("${if (w.studio) "Pausa tra sessioni" else "Recupero tra ripetizioni"}", e.restBetweenRepsSec, factor = factor) { value -> vm.exercise(bi, ei) { it.copy(restBetweenRepsSec = value) } }
            // DOC L0065: Chiude il costrutto aperto alla riga 53.
            } }
            // DOC L0066: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
            OutlinedButton(onClick = { vm.block(bi) { it.copy(exercises = it.exercises + localizedNewExercise(newExercise(vm.mode))) } }, enabled = b.exercises.size < 100) { Text(tr("+ Esercizio")) }
        // DOC L0067: Chiude il costrutto aperto alla riga 40.
        } } }
        // DOC L0068: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di Editor.
        OutlinedButton(onClick = { vm.update(w.copy(blocks = w.blocks + localizedNewBlock(newBlock(vm.mode)))) }, enabled = w.blocks.size < 50) { Text(tr("+ Blocco")) }
        // DOC L0069: Chiude il costrutto aperto alla riga 28.
        }
        // DOC L0070: compiled: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di Editor.
        val compiled = runCatching { compileTimeline(w) }
        // DOC L0071: Esegue il passaggio esplicito «Panel {» nel flusso: Editor Compose dei blocchi Workout e delle attività Studio. Nel contesto di Editor.
        Panel {
            // DOC L0072: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di Editor.
            Text(tr("Anteprima"), style = MaterialTheme.typography.titleLarge)
            // DOC L0073: Esegue il passaggio esplicito «compiled.fold(onSuccess = { phases ->» nel flusso: Editor Compose dei blocchi Workout e delle attività Studio. Nel contesto di Editor.
            compiled.fold(onSuccess = { phases ->
                // DOC L0074: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di Editor.
                Text(formatTime(plannedMs(phases)), fontSize = 42.sp, color = MaterialTheme.colorScheme.primary)
                // DOC L0075: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di Editor.
                Text(tr("+ 0:20 di preparazione prima di iniziare"), fontSize = 12.sp)
                // DOC L0076: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di Editor.
                Text(tr("{0} fasi · {1} {2}", phases.size, phases.count { it.type == "WORK" }, if (w.studio) tr("sessioni di concentrazione") else tr("intervalli di lavoro")))
                // DOC L0077: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di Editor.
                phases.take(150).forEachIndexed { i, p -> Text(tr("{0}. {1} · {2}", i + 1, p.label, formatTime(p.durationMs)), fontSize = 12.sp) }
                // DOC L0078: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di Editor.
                if (phases.size > 150) Text(tr("Mostrate le prime 150 fasi. Il timer le esegue tutte."))
            // DOC L0079: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di Editor.
            }, onFailure = { Text(tr(it.message ?: tr("Controlla i campi")), color = MaterialTheme.colorScheme.error) })
        // DOC L0080: Chiude il costrutto aperto alla riga 71.
        }
        // DOC L0081: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di Editor.
        AccentButton(enabled = compiled.isSuccess, onClick = { vm.save() }, modifier = Modifier.fillMaxWidth()) { Text(tr("Salva sul dispositivo")) }
        // DOC L0082: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di Editor.
        Text(tr("Lo ritroverai in {0} anche dopo aver chiuso l’app.", tr(labels.collection)), fontSize = 12.sp)
    // DOC L0083: Chiude il costrutto aperto alla riga 20.
    }
// DOC L0084: Chiude il costrutto aperto alla riga 16.
}
