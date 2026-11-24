@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
package it.tabatastudio.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import it.tabatastudio.domain.*

@Composable private fun Field(label: String, value: String, max: Int = 120, onChange: (String) -> Unit) { OutlinedTextField(value = value, onValueChange = { if (it.length <= max) onChange(it) }, label = { Text(tr(label)) }, modifier = Modifier.fillMaxWidth(), minLines = if (max > 120) 2 else 1, singleLine = max <= 120) }
@Composable private fun NumberField(label: String, value: Int, min: Int = 0, max: Int = 3600, factor: Int = 1, activeMin: Int = 0, onChange: (Int) -> Unit) {
    val invalid = value !in min..max || (value > 0 && value < activeMin)
    OutlinedTextField(value = if (value < 0) "" else (value / factor).toString(),
        onValueChange = { if (it.length <= 6) onChange(it.toIntOrNull()?.let { n -> n * factor } ?: -1) },
        label = { Text(tr(label)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), isError = invalid,
        supportingText = if (invalid) ({ Text(if (activeMin > 0) tr("Zero oppure almeno {0}", activeMin) else tr("Inserisci un intero {0}–{1}", min / factor, max / factor)) }) else null,
        modifier = Modifier.fillMaxWidth(), singleLine = true)
}
private fun <T> move(items: List<T>, index: Int, delta: Int): List<T> { val to = index + delta; if (to !in items.indices) return items; return items.toMutableList().also { val temp = it[index]; it[index] = it[to]; it[to] = temp } }
@Composable internal fun Editor(vm: StudioViewModel) {
    val w = vm.draft ?: return
    val factor = 1
    val labels = vm.labels
    Page {
        Text(if (vm.data.workouts.any { it.id == w.id }) tr("Modifica {0}", tr(labels.noun)) else tr("Crea {0}", tr(labels.noun)), style = MaterialTheme.typography.headlineMedium)
        if (vm.draftNotice.isNotEmpty()) Text(tr(vm.draftNotice), color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (w.studio) {
            Panel {
                Field("Nome del programma", w.title) { vm.update(w.copy(title = it)) }
                StudyFields(w, vm::update)
            }
        } else {
        Panel {
            Field(if (w.studio) "Materia o attività" else "Nome dell’allenamento", w.title) { vm.update(w.copy(title = it)) }
            Field("Descrizione", w.description, 2000) { vm.update(w.copy(description = it)) }
            Text(tr("Colore"), style = MaterialTheme.typography.labelLarge)
            Buttons {
                listOf("Viola" to "#9153B7", "Blu" to "#3367B0", "Verde" to "#287D68", "Rosa" to "#B54079", "Arancio" to "#B56825").forEach { (name, hex) ->
                    FilterChip(selected = w.color.equals(hex, ignoreCase = true), onClick = { vm.update(w.copy(color = hex)) }, label = { Text(tr(name)) })
                }
            }
            NumberField("${labels.final}", w.cooldownSec, factor = factor, activeMin = 10) { vm.update(w.copy(cooldownSec = it)) }
        }
        w.blocks.forEachIndexed { bi, b -> key(b.id) { Panel {
            Text(tr("Blocco {0}", bi + 1), style = MaterialTheme.typography.titleLarge)
            Buttons {
                OutlinedButton(onClick = { vm.update(w.copy(blocks = move(w.blocks, bi, -1))) }, enabled = bi > 0) { Text(tr("↑ Blocco")) }
                OutlinedButton(onClick = { vm.update(w.copy(blocks = move(w.blocks, bi, 1))) }, enabled = bi < w.blocks.lastIndex) { Text(tr("↓ Blocco")) }
                TextButton(onClick = { val copy = b.copy(id = uid(), exercises = b.exercises.map { it.copy(id = uid()) }); vm.update(w.copy(blocks = w.blocks.toMutableList().also { it.add(bi + 1, copy) })) }) { Text(tr("Duplica blocco")) }
                TextButton(enabled = w.blocks.size > 1, onClick = { vm.confirm("Eliminare il blocco?") { vm.update(w.copy(blocks = w.blocks.filterIndexed { i, _ -> i != bi })) } }) { Text(tr("Elimina blocco")) }
            }
            Field("Nome blocco", b.title) { value -> vm.block(bi) { it.copy(title = value) } }
            NumberField("Cicli", b.cycles, 1, 100) { value -> vm.block(bi) { it.copy(cycles = value) } }
            if (bi < w.blocks.lastIndex) NumberField("Recupero tra blocchi", b.restAfterBlockSec, 5) { value -> vm.block(bi) { it.copy(restAfterBlockSec = value) } }
            b.exercises.forEachIndexed { ei, e -> key(e.id) {
                HorizontalDivider(); Text(tr("{0} {1}", if (w.studio) tr("Attività") else tr("Esercizio"), ei + 1), color = MaterialTheme.colorScheme.primary)
                Buttons {
                    OutlinedButton(onClick = { vm.block(bi) { it.copy(exercises = move(it.exercises, ei, -1)) } }, enabled = ei > 0) { Text(tr("↑ Esercizio")) }
                    OutlinedButton(onClick = { vm.block(bi) { it.copy(exercises = move(it.exercises, ei, 1)) } }, enabled = ei < b.exercises.lastIndex) { Text(tr("↓ Esercizio")) }
                    TextButton(onClick = { vm.block(bi) { it.copy(exercises = it.exercises.toMutableList().also { list -> list.add(ei + 1, e.copy(id = uid())) }) } }) { Text(tr("Duplica")) }
                    TextButton(enabled = b.exercises.size > 1, onClick = { vm.confirm("Eliminare l’esercizio?") { vm.block(bi) { it.copy(exercises = it.exercises.filterIndexed { i, _ -> i != ei }) } } }) { Text(tr("Elimina")) }
                }
                Field(if (w.studio) "Materia o attività" else "Nome esercizio", e.title) { value -> vm.exercise(bi, ei) { it.copy(title = value) } }
                NumberField("${labels.focus}", e.workSec, 5, factor = factor) { value -> vm.exercise(bi, ei) { it.copy(workSec = value) } }
                NumberField(labels.rounds, e.repetitions, 1, 100) { value -> vm.exercise(bi, ei) { it.copy(repetitions = value) } }
                NumberField("Recupero", e.restBetweenRepsSec, factor = factor, activeMin = 5) { value -> vm.exercise(bi, ei) { it.copy(restBetweenRepsSec = value) } }
            } }
            OutlinedButton(onClick = { vm.block(bi) { it.copy(exercises = it.exercises + localizedNewExercise(newExercise(vm.mode))) } }, enabled = b.exercises.size < 100) { Text(tr("+ Esercizio")) }
        } } }
        OutlinedButton(onClick = { vm.update(w.copy(blocks = w.blocks + localizedNewBlock(newBlock(vm.mode)))) }, enabled = w.blocks.size < 50) { Text(tr("+ Blocco")) }
        }
        val compiled = runCatching { compileTimeline(w) }
        Panel {
            Text(tr("Anteprima"), style = MaterialTheme.typography.titleLarge)
            compiled.fold(onSuccess = { phases ->
                Text(formatTime(plannedMs(phases)), fontSize = 42.sp, color = MaterialTheme.colorScheme.primary)
                Text(tr("+ 0:20 di preparazione prima di iniziare"), fontSize = 12.sp)
                Text(tr("{0} fasi · {1} {2}", phases.size, phases.count { it.type == "WORK" }, if (w.studio) tr("sessioni di concentrazione") else tr("intervalli di lavoro")))
                TimelinePreview(w, phases)
                if (phases.size > 150) Text(tr("Mostrate le prime 150 fasi. Il timer le esegue tutte."))
            }, onFailure = { Text(tr(it.message ?: tr("Controlla i campi")), color = MaterialTheme.colorScheme.error) })
        }
        AccentButton(enabled = compiled.isSuccess, onClick = { vm.save() }, modifier = Modifier.fillMaxWidth()) { Text(tr("Salva sul dispositivo")) }
        Text(tr("Lo ritroverai in {0} anche dopo aver chiuso l’app.", tr(labels.collection)), fontSize = 12.sp)
    }
}
