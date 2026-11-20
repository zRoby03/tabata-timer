// DOC L0001: Assegna il file al namespace it.tabatastudio.ui, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.ui

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.foundation.BorderStroke. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.foundation.BorderStroke
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.foundation.layout.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.foundation.layout.*
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.foundation.text.KeyboardOptions. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.foundation.text.KeyboardOptions
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.material3.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.material3.*
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.runtime.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.runtime.*
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.runtime.saveable.rememberSaveable. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.runtime.saveable.rememberSaveable
// DOC L0009: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.Alignment. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.Alignment
// DOC L0010: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.Modifier. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.Modifier
// DOC L0011: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.graphics.Color. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.graphics.Color
// DOC L0012: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.semantics.contentDescription. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.semantics.contentDescription
// DOC L0013: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.semantics.semantics. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.semantics.semantics
// DOC L0014: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.text.font.FontWeight. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.text.font.FontWeight
// DOC L0015: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.text.input.KeyboardType. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.text.input.KeyboardType
// DOC L0016: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.unit.dp. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.unit.dp
// DOC L0017: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.unit.sp. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.unit.sp
// DOC L0018: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.domain.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.domain.*

// DOC L0020: QuickTimerScreen: Disegna la schermata iniziale per la modalità corrente, con preset, durata, Avvia e salvataggio nominato.
@Composable internal fun QuickTimerScreen(vm: StudioViewModel) {
    // DOC L0021: w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di QuickTimerScreen.
    val w = vm.quickWorkout
    // DOC L0022: labels: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di QuickTimerScreen.
    val labels = vm.labels
    // DOC L0023: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. factor: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di QuickTimerScreen.
    val factor = if (w.studio) 60 else 1
    // DOC L0024: unit: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di QuickTimerScreen.
    val unit = ""
    // DOC L0025: b: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di QuickTimerScreen.
    val b = w.blocks.firstOrNull()
    // DOC L0026: e: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di QuickTimerScreen.
    val e = b?.exercises?.firstOrNull()
    // DOC L0027: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. focus: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di QuickTimerScreen.
    val focus = if (w.studio) w.activities!![0].concentrationSec else e!!.workSec
    // DOC L0028: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. rest: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di QuickTimerScreen.
    val rest = if (w.studio) w.activities!![0].pauseSec else e!!.restBetweenRepsSec
    // DOC L0029: options: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di QuickTimerScreen.
    var options by rememberSaveable { mutableStateOf(false) }
    // DOC L0030: timeline: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di QuickTimerScreen.
    val timeline = runCatching { compileTimeline(w) }
    // DOC L0031: exercise: Applica una trasformazione al primo esercizio del timer rapido Workout.
    fun exercise(transform: (Exercise) -> Exercise) { vm.updateQuick(w.copy(blocks = listOf(b!!.copy(exercises = listOf(transform(e!!)))))) }
    // DOC L0032: block: Applica una trasformazione al primo blocco del timer rapido Workout.
    fun block(transform: (Block) -> Block) { vm.updateQuick(w.copy(blocks = listOf(transform(b!!)))) }

    // DOC L0034: Esegue il passaggio esplicito «Page {» nel flusso: Interfaccia Compose del timer rapido. Nel contesto di block.
    Page {
        // DOC L0035: Esegue il passaggio esplicito «Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {» nel flusso: Interfaccia Compose del timer rapido. Nel contesto di block.
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            // DOC L0036: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di block.
            Text("Tabata Timer", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            // DOC L0037: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di block.
            Text(tr("{0}. Scegli i tempi e premi Avvia.", tr(labels.subtitle)), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        // DOC L0038: Chiude il costrutto aperto alla riga 35.
        }
        // DOC L0039: Esegue il passaggio esplicito «Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {» nel flusso: Interfaccia Compose del timer rapido. Nel contesto di block.
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // DOC L0040: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di block.
            quickPresets(vm.mode).forEach { preset ->
                // DOC L0041: selected: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di block.
                val selected = focus == preset.workSec && rest == preset.restSec
                // DOC L0042: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di block.
                OutlinedButton(
                    // DOC L0043: Esegue il passaggio esplicito «onClick = { vm.quickPreset(preset) }, modifier = Modifier.weight(1f),» nel flusso: Interfaccia Compose del timer rapido. Nel contesto di block.
                    onClick = { vm.quickPreset(preset) }, modifier = Modifier.weight(1f),
                    // DOC L0044: Esegue il passaggio esplicito «contentPadding = PaddingValues(horizontal = 4.dp, vertical = 10.dp),» nel flusso: Interfaccia Compose del timer rapido. Nel contesto di block.
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 10.dp),
                    // DOC L0045: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di block.
                    border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                    // DOC L0046: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di block.
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                // DOC L0047: Esegue il passaggio esplicito «) {» nel flusso: Interfaccia Compose del timer rapido. Nel contesto di block.
                ) {
                    // DOC L0048: Esegue il passaggio esplicito «Column(horizontalAlignment = Alignment.CenterHorizontally) {» nel flusso: Interfaccia Compose del timer rapido. Nel contesto di block.
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // DOC L0049: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di block.
                        Text(preset.name, fontSize = 13.sp)
                        // DOC L0050: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di block.
                        Text(tr("{0} / {1}", preset.workSec / factor, preset.restSec / factor), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    // DOC L0051: Chiude il costrutto aperto alla riga 48.
                    }
                // DOC L0052: Chiude il costrutto aperto alla riga 47.
                }
            // DOC L0053: Chiude il costrutto aperto alla riga 40.
            }
        // DOC L0054: Chiude il costrutto aperto alla riga 39.
        }
        // DOC L0055: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di block.
        if (w.studio) Text(tr("Le scelte rapide impostano i tempi della prima attività."), fontSize = 12.sp)
        // DOC L0056: Esegue il passaggio esplicito «Panel {» nel flusso: Interfaccia Compose del timer rapido. Nel contesto di block.
        Panel {
            // DOC L0057: Esegue il passaggio esplicito «Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {» nel flusso: Interfaccia Compose del timer rapido. Nel contesto di block.
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                // DOC L0058: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di block.
                Text(if (w.studio) tr("Durata programma") else tr("Durata allenamento"), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                // DOC L0059: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di block.
                Text(timeline.getOrNull()?.let { formatTime(plannedMs(it)) } ?: "—", fontSize = 48.sp, fontWeight = FontWeight.SemiBold)
                // DOC L0060: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di block.
                Text(tr("+ 0:20 per prepararti"), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            // DOC L0061: Chiude il costrutto aperto alla riga 57.
            }
            // DOC L0062: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di block.
            if (w.studio) StudyFields(w, vm::updateQuick) else {
                // DOC L0063: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di block.
                QuickNumber(labels.focus, e!!.workSec, unit, 1) { value -> exercise { it.copy(workSec = value) } }
                // DOC L0064: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di block.
                QuickNumber(labels.rest, e.restBetweenRepsSec, unit) { value -> exercise { it.copy(restBetweenRepsSec = value) } }
                // DOC L0065: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di block.
                QuickNumber(labels.rounds, e.repetitions, "", 1, 100) { value -> exercise { it.copy(repetitions = value) } }
            // DOC L0066: Chiude il costrutto aperto alla riga 62.
            }
            // DOC L0067: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di block.
            if (timeline.isFailure) Text(tr(timeline.exceptionOrNull()?.message ?: tr("Controlla i campi")), color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            // DOC L0068: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di block.
            AccentButton(enabled = timeline.isSuccess, onClick = { vm.start(w) }, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) { Text(tr("Avvia"), fontSize = 18.sp) }
            // DOC L0069: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di block.
            OutlinedButton(enabled = timeline.isSuccess, onClick = vm::openQuickSave, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(tr("Salva {0}", tr(labels.noun))) }
            // DOC L0070: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di block.
            if (!w.studio) {
                // DOC L0071: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di block.
                TextButton(onClick = { options = !options }, modifier = Modifier.fillMaxWidth()) { Text(if (options) tr("Meno opzioni −") else tr("Altre opzioni +"), color = MaterialTheme.colorScheme.onSurfaceVariant) }
                // DOC L0072: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di block.
                if (options) {
                    // DOC L0073: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di block.
                    QuickNumber("Serie", b!!.cycles, "", 1, 100) { value -> block { it.copy(cycles = value) } }
                    // DOC L0074: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di block.
                    QuickNumber("Recupero tra serie", b.restBetweenCyclesSec, unit) { value -> block { it.copy(restBetweenCyclesSec = value) } }
                    // DOC L0075: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di block.
                    QuickNumber(labels.final, w.cooldownSec, unit) { vm.updateQuick(w.copy(cooldownSec = it)) }
                    // DOC L0076: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di block.
                    Text(tr("Il recupero si applica solo tra i giri, mai dopo l’ultimo."), fontSize = 12.sp)
                    // DOC L0077: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di block.
                    TextButton(onClick = { vm.edit() }) { Text(tr("Crea {0} personalizzato", tr(labels.noun))) }
                // DOC L0078: Chiude il costrutto aperto alla riga 72.
                }
            // DOC L0079: Chiude il costrutto aperto alla riga 70.
            }
        // DOC L0080: Chiude il costrutto aperto alla riga 56.
        }
    // DOC L0081: Chiude il costrutto aperto alla riga 34.
    }
// DOC L0082: Chiude il costrutto aperto alla riga 20.
}

// DOC L0084: QuickNumber: Collega un numero del timer rapido al suo callback, evitando di mantenere una seconda copia dello stato.
@Composable private fun QuickNumber(label: String, value: Int, unit: String, min: Int = 0, max: Int = 3600, factor: Int = 1, onChange: (Int) -> Unit) {
    // DOC L0085: Esegue il passaggio esplicito «Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {» nel flusso: Interfaccia Compose del timer rapido. Nel contesto di QuickNumber.
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        // DOC L0086: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di QuickNumber.
        Text(tr(label), Modifier.weight(1f), fontSize = 15.sp)
        // DOC L0087: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di QuickNumber.
        OutlinedTextField(
            // DOC L0088: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di QuickNumber.
            value = if (value < 0) "" else (value / factor).toString(),
            // DOC L0089: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di QuickNumber.
            onValueChange = { if (it.length <= 6) onChange(it.toIntOrNull()?.let { n -> n * factor } ?: -1) },
            // DOC L0090: Esegue il passaggio esplicito «keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true,» nel flusso: Interfaccia Compose del timer rapido. Nel contesto di QuickNumber.
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true,
            // DOC L0091: Esegue il passaggio esplicito «isError = value !in min..max, modifier = Modifier.width(100.dp).semantics { contentDescription = tr(label) },» nel flusso: Interfaccia Compose del timer rapido. Nel contesto di QuickNumber.
            isError = value !in min..max, modifier = Modifier.width(100.dp).semantics { contentDescription = tr(label) },
            // DOC L0092: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di QuickNumber.
            suffix = if (unit.isNotEmpty()) ({ Text(tr(unit), fontSize = 11.sp) }) else null,
            // DOC L0093: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di QuickNumber.
            textStyle = LocalTextStyle.current.copy(fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        // DOC L0094: Chiude un blocco, una chiamata o un valore composto; non aggiunge una nuova operazione.
        )
    // DOC L0095: Chiude il costrutto aperto alla riga 85.
    }
// DOC L0096: Chiude il costrutto aperto alla riga 84.
}
