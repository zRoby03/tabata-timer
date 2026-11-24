package it.tabatastudio.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.tabatastudio.domain.*

@Composable internal fun QuickTimerScreen(vm: StudioViewModel) {
    val w = vm.quickWorkout
    val labels = vm.labels
    val factor = if (w.studio) 60 else 1
    val unit = ""
    val b = w.blocks.firstOrNull()
    val e = b?.exercises?.firstOrNull()
    val focus = if (w.studio) w.activities!![0].concentrationSec else e!!.workSec
    val rest = if (w.studio) w.activities!![0].pauseSec else e!!.restBetweenRepsSec
    var options by rememberSaveable { mutableStateOf(false) }
    val timeline = runCatching { compileTimeline(w) }
    fun exercise(transform: (Exercise) -> Exercise) { vm.updateQuick(w.copy(blocks = listOf(b!!.copy(exercises = listOf(transform(e!!)))))) }
    fun block(transform: (Block) -> Block) { vm.updateQuick(w.copy(blocks = listOf(transform(b!!)))) }

    Page {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Tabata Timer", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Text(tr("{0}. Scegli i tempi e premi Avvia.", tr(labels.subtitle)), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            quickPresets(vm.mode).forEach { preset ->
                val selected = focus == preset.workSec && rest == preset.restSec
                OutlinedButton(
                    onClick = { vm.quickPreset(preset) }, modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 10.dp),
                    border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(preset.name, fontSize = 13.sp)
                        Text(tr("{0} / {1}", preset.workSec / factor, preset.restSec / factor), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        if (w.studio) Text(tr("Le scelte rapide impostano i tempi della prima attività."), fontSize = 12.sp)
        Panel {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (w.studio) tr("Durata programma") else tr("Durata allenamento"), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                Text(timeline.getOrNull()?.let { formatTime(plannedMs(it)) } ?: "—", fontSize = 48.sp, fontWeight = FontWeight.SemiBold)
                Text(tr("+ 0:20 per prepararti"), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (w.studio) StudyFields(w, vm::updateQuick) else {
                QuickNumber(labels.focus, e!!.workSec, unit, 5) { value -> exercise { it.copy(workSec = value) } }
                QuickNumber(labels.rest, e.restBetweenRepsSec, unit, activeMin = 5) { value -> exercise { it.copy(restBetweenRepsSec = value) } }
                QuickNumber(labels.rounds, e.repetitions, "", 1, 100) { value -> exercise { it.copy(repetitions = value) } }
            }
            if (timeline.isFailure) Text(tr(timeline.exceptionOrNull()?.message ?: tr("Controlla i campi")), color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            AccentButton(enabled = timeline.isSuccess, onClick = { vm.start(w) }, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) { Text(tr("Avvia"), fontSize = 18.sp) }
            OutlinedButton(enabled = timeline.isSuccess, onClick = vm::openQuickSave, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(tr("Salva {0}", tr(labels.noun))) }
            if (!w.studio) {
                TextButton(onClick = { options = !options }, modifier = Modifier.fillMaxWidth()) { Text(if (options) tr("Meno opzioni −") else tr("Altre opzioni +"), color = MaterialTheme.colorScheme.onSurfaceVariant) }
                if (options) {
                    QuickNumber("Serie", b!!.cycles, "", 1, 100) { value -> block { it.copy(cycles = value) } }
                    QuickNumber(labels.final, w.cooldownSec, unit, activeMin = 10) { vm.updateQuick(w.copy(cooldownSec = it)) }
                    Text(tr("Il recupero si applica solo tra i giri, mai dopo l’ultimo."), fontSize = 12.sp)
                    TextButton(onClick = { vm.edit() }) { Text(tr("Crea {0} personalizzato", tr(labels.noun))) }
                }
            }
        }
    }
}

@Composable private fun QuickNumber(label: String, value: Int, unit: String, min: Int = 0, max: Int = 3600, factor: Int = 1, activeMin: Int = 0, onChange: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(tr(label), Modifier.weight(1f), fontSize = 15.sp)
        OutlinedTextField(
            value = if (value < 0) "" else (value / factor).toString(),
            onValueChange = { if (it.length <= 6) onChange(it.toIntOrNull()?.let { n -> n * factor } ?: -1) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true,
            isError = value !in min..max || (value > 0 && value < activeMin), modifier = Modifier.width(100.dp).semantics { contentDescription = tr(label) },
            suffix = if (unit.isNotEmpty()) ({ Text(tr(unit), fontSize = 11.sp) }) else null,
            textStyle = LocalTextStyle.current.copy(fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        )
    }
}
