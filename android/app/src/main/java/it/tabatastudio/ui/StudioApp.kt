// DOC L0001: Applica l’annotazione dichiarata: Compose identifica UI, Room genera accessi e schema, mentre le annotazioni Test selezionano i casi da eseguire.
@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
// DOC L0002: Assegna il file al namespace it.tabatastudio.ui, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.ui

// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import androidx.activity.compose.BackHandler. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.activity.compose.BackHandler
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.foundation.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.foundation.*
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.foundation.layout.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.foundation.layout.*
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.material3.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.material3.*
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.runtime.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.runtime.*
// DOC L0009: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.runtime.saveable.rememberSaveable. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.runtime.saveable.rememberSaveable
// DOC L0010: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.Alignment. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.Alignment
// DOC L0011: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.Modifier. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.Modifier
// DOC L0012: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.graphics.Color. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.graphics.Color
// DOC L0013: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.semantics.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.semantics.*
// DOC L0014: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.text.font.FontWeight. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.text.font.FontWeight
// DOC L0015: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.unit.dp. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.unit.dp
// DOC L0016: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.unit.sp. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.unit.sp
// DOC L0017: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.domain.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.domain.*
// DOC L0018: Rende disponibili le dipendenze esplicite di questa riga: import java.time.Instant. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import java.time.Instant
// DOC L0019: Rende disponibili le dipendenze esplicite di questa riga: import java.time.ZoneId. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import java.time.ZoneId
// DOC L0020: Rende disponibili le dipendenze esplicite di questa riga: import java.time.format.DateTimeFormatter. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import java.time.format.DateTimeFormatter

// DOC L0022: date: Formatta l’istante della cronologia secondo la lingua selezionata, senza cambiare il valore UTC salvato.
internal fun date(value: String): String = DateTimeFormatter.ofLocalizedDateTime(java.time.format.FormatStyle.SHORT).withLocale(it.tabatastudio.i18n.AppText.locale).withZone(ZoneId.systemDefault()).format(Instant.parse(value))

// DOC L0024: StudioApp: Collega stato osservabile, navigazione, switch modalità, dialoghi e schermate Compose dell’app.
@Composable fun StudioApp(vm: StudioViewModel) {
    // DOC L0025: Esegue il passaggio esplicito «MaterialTheme(colorScheme = studioColors(vm.darkTheme, vm.palette)) {» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di StudioApp.
    MaterialTheme(colorScheme = studioColors(vm.darkTheme, vm.palette)) {
        // DOC L0026: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di StudioApp.
        BackHandler(vm.route != "Home" || vm.fullscreen) { if (vm.fullscreen) vm.fullscreen = false else vm.navigate("Home") }
        // DOC L0027: Esegue il passaggio esplicito «Scaffold(containerColor = MaterialTheme.colorScheme.background, bottomBar = {» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di StudioApp.
        Scaffold(containerColor = MaterialTheme.colorScheme.background, bottomBar = {
            // DOC L0028: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di StudioApp.
            if (!vm.fullscreen) Surface(tonalElevation = 2.dp) {
                // DOC L0029: Esegue il passaggio esplicito «Row(Modifier.fillMaxWidth().navigationBarsPadding().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.SpaceEvenly) {» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di StudioApp.
                Row(Modifier.fillMaxWidth().navigationBarsPadding().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.SpaceEvenly) {
                    // DOC L0030: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. selected: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di StudioApp.
                    val selected = when (vm.route) { "Home", "Timer" -> "Timer"; "Editor", "Allenamenti" -> "Allenamenti"; "Cronologia", "Statistiche" -> "Attività"; else -> "" }
                    // DOC L0031: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di StudioApp.
                    listOf("Timer" to if (vm.active) "Timer" else "Home", vm.labels.collection to "Allenamenti", "Attività" to "Cronologia").forEach { (title, destination) ->
                        // DOC L0032: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StudioApp.
                        TextButton(onClick = { vm.navigate(destination) }, modifier = Modifier.weight(1f).heightIn(min = 48.dp)) {
                            // DOC L0033: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StudioApp.
                            Text(tr(title), fontSize = 13.sp, color = if ((if (title == tr(vm.labels.collection)) "Allenamenti" else title) == selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        // DOC L0034: Chiude il costrutto aperto alla riga 32.
                        }
                    // DOC L0035: Chiude il costrutto aperto alla riga 31.
                    }
                // DOC L0036: Chiude il costrutto aperto alla riga 29.
                }
            // DOC L0037: Chiude il costrutto aperto alla riga 28.
            }
        // DOC L0038: Esegue il passaggio esplicito «}) { padding ->» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di StudioApp.
        }) { padding ->
            // DOC L0039: Esegue il passaggio esplicito «Column(Modifier.fillMaxSize().padding(padding)) {» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di StudioApp.
            Column(Modifier.fillMaxSize().padding(padding)) {
                // DOC L0040: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di StudioApp.
                if (!vm.fullscreen) Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    // DOC L0041: Esegue il passaggio esplicito «Column(Modifier.weight(1f)) {» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di StudioApp.
                    Column(Modifier.weight(1f)) {
                        // DOC L0042: Esegue il passaggio esplicito «Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di StudioApp.
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // DOC L0043: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StudioApp.
                            Text(tr("t."), fontSize = 24.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            // DOC L0044: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StudioApp.
                            Text(tr("Tabata Timer"), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        // DOC L0045: Chiude il costrutto aperto alla riga 42.
                        }
                        // DOC L0046: Esegue il passaggio esplicito «Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di StudioApp.
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // DOC L0047: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StudioApp.
                            Text(tr("Workout"), fontSize = 12.sp)
                            // DOC L0048: Esegue il passaggio esplicito «Switch(checked = vm.mode == "STUDIO", onCheckedChange = vm::chooseMode, enabled = !vm.active && !vm.quickSaveBusy, modifier = Modifier.semantics { contentDescription = tr("Modalità» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di StudioApp.
                            Switch(checked = vm.mode == "STUDIO", onCheckedChange = vm::chooseMode, enabled = !vm.active && !vm.quickSaveBusy, modifier = Modifier.semantics { contentDescription = tr("Modalità Studio") })
                            // DOC L0049: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StudioApp.
                            Text(tr("Studio"), fontSize = 12.sp)
                        // DOC L0050: Chiude il costrutto aperto alla riga 46.
                        }
                    // DOC L0051: Chiude il costrutto aperto alla riga 41.
                    }
                    // DOC L0052: Esegue il passaggio esplicito «Row(verticalAlignment = Alignment.CenterVertically) {» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di StudioApp.
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // DOC L0053: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StudioApp.
                        TextButton(onClick = { vm.navigate("Guida") }) { Text(tr("Guida"), fontSize = 13.sp) }
                        // DOC L0054: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StudioApp.
                        TextButton(onClick = { vm.navigate("Dati") }, modifier = Modifier.semantics { contentDescription = tr("Impostazioni") }) { Text(tr("⚙"), fontSize = 23.sp) }
                    // DOC L0055: Chiude il costrutto aperto alla riga 52.
                    }
                // DOC L0056: Chiude il costrutto aperto alla riga 40.
                }
                // DOC L0057: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di StudioApp.
                if (vm.message.isNotEmpty()) Surface(color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth()) {
                    // DOC L0058: Esegue il passaggio esplicito «Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di StudioApp.
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        // DOC L0059: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StudioApp.
                        Text(tr(vm.message), modifier = Modifier.weight(1f).semantics { liveRegion = LiveRegionMode.Polite }, fontSize = 13.sp)
                        // DOC L0060: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StudioApp.
                        TextButton(onClick = { vm.message = "" }) { Text(tr("Chiudi")) }
                    // DOC L0061: Chiude il costrutto aperto alla riga 58.
                    }
                // DOC L0062: Chiude il costrutto aperto alla riga 57.
                }
                // DOC L0063: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StudioApp.
                if (!vm.ready) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(tr("Apertura archivio locale…")) }
                // DOC L0064: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di StudioApp.
                else key(vm.route, vm.mode) {
                    // DOC L0065: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di StudioApp.
                    when (vm.route) {
                        // DOC L0066: Esegue il passaggio esplicito «"Home" -> QuickTimerScreen(vm)» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di StudioApp.
                        "Home" -> QuickTimerScreen(vm)
                        // DOC L0067: Esegue il passaggio esplicito «"Allenamenti" -> Workouts(vm)» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di StudioApp.
                        "Allenamenti" -> Workouts(vm)
                        // DOC L0068: Esegue il passaggio esplicito «"Editor" -> Editor(vm)» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di StudioApp.
                        "Editor" -> Editor(vm)
                        // DOC L0069: Esegue il passaggio esplicito «"Timer" -> TimerScreen(vm)» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di StudioApp.
                        "Timer" -> TimerScreen(vm)
                        // DOC L0070: Esegue il passaggio esplicito «"Cronologia" -> History(vm)» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di StudioApp.
                        "Cronologia" -> History(vm)
                        // DOC L0071: Esegue il passaggio esplicito «"Statistiche" -> StatsScreen(vm)» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di StudioApp.
                        "Statistiche" -> StatsScreen(vm)
                        // DOC L0072: Esegue il passaggio esplicito «"Guida" -> HelpScreen(vm)» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di StudioApp.
                        "Guida" -> HelpScreen(vm)
                        // DOC L0073: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di StudioApp.
                        else -> DataScreen(vm)
                    // DOC L0074: Chiude il costrutto aperto alla riga 65.
                    }
                // DOC L0075: Chiude il costrutto aperto alla riga 64.
                }
            // DOC L0076: Chiude il costrutto aperto alla riga 39.
            }
        // DOC L0077: Chiude il costrutto aperto alla riga 38.
        }
        // DOC L0078: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di StudioApp.
        if (vm.quickSaveDialog) AlertDialog(
            // DOC L0079: Esegue il passaggio esplicito «onDismissRequest = vm::dismissQuickSave,» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di StudioApp.
            onDismissRequest = vm::dismissQuickSave,
            // DOC L0080: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StudioApp.
            title = { Text(tr("Salva {0}", tr(vm.labels.noun))) },
            // DOC L0081: Esegue il passaggio esplicito «text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di StudioApp.
            text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // DOC L0082: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StudioApp.
                OutlinedTextField(value = vm.quickSaveName, onValueChange = { if (it.length <= 120) vm.quickSaveName = it }, label = { Text(if (vm.mode == "STUDIO") tr("Nome del programma") else tr("Nome dell’allenamento")) }, singleLine = true)
                // DOC L0083: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StudioApp.
                Text(tr("Lo ritroverai in {0} anche dopo aver chiuso l’app. I dati restano solo sul telefono.", tr(vm.labels.collection)), fontSize = 13.sp)
                // DOC L0084: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StudioApp.
                if (vm.quickSaveError.isNotEmpty()) Text(tr(vm.quickSaveError), color = MaterialTheme.colorScheme.error, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
            // DOC L0085: Chiude il costrutto aperto alla riga 81.
            } },
            // DOC L0086: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StudioApp.
            confirmButton = { TextButton(enabled = !vm.quickSaveBusy && vm.quickSaveName.isNotBlank(), onClick = vm::confirmQuickSave) { Text(if (vm.quickSaveBusy) tr("Salvataggio…") else tr("Salva sul dispositivo")) } },
            // DOC L0087: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StudioApp.
            dismissButton = { TextButton(enabled = !vm.quickSaveBusy, onClick = vm::dismissQuickSave) { Text(tr("Annulla")) } }
        // DOC L0088: Chiude un blocco, una chiamata o un valore composto; non aggiunge una nuova operazione.
        )
        // DOC L0089: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StudioApp.
        if (vm.confirmTitle.isNotEmpty()) AlertDialog(onDismissRequest = vm::dismissConfirm, title = { Text(tr("Conferma")) }, text = { Text(tr(vm.confirmTitle)) }, confirmButton = { TextButton(onClick = vm::acceptConfirm) { Text(tr("Conferma")) } }, dismissButton = { TextButton(onClick = vm::dismissConfirm) { Text(tr("Annulla")) } })
        // DOC L0090: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StudioApp.
        vm.importPreview?.let { backup -> AlertDialog(onDismissRequest = { vm.importPreview = null }, title = { Text(tr("Anteprima importazione")) }, text = { Text(tr("{0} programmi (Workout e Studio) · {1} sessioni. La conferma sostituisce tutti i dati. Esporta prima una copia JSON per conservarli.", backup.workouts.size, backup.sessions.size)) }, confirmButton = { TextButton(onClick = vm::applyImport) { Text(tr("Sostituisci dati")) } }, dismissButton = { TextButton(onClick = { vm.importPreview = null }) { Text(tr("Annulla")) } }) }
    // DOC L0091: Chiude il costrutto aperto alla riga 25.
    }
// DOC L0092: Chiude il costrutto aperto alla riga 24.
}
// DOC L0093: Page: Fornisce il contenitore scorrevole e la spaziatura comune alle schermate.
@Composable internal fun Page(content: @Composable ColumnScope.() -> Unit) { Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp), content = content) }
// DOC L0094: Panel: Raggruppa i contenuti in una scheda con bordo e colori coerenti con il tema.
@Composable internal fun Panel(content: @Composable ColumnScope.() -> Unit) { Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) { Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content) } }
// DOC L0095: Heading: Presenta titolo e contenuto accessorio della sezione con gerarchia visiva uniforme.
@Composable internal fun Heading(kicker: String, title: String) { Text(kicker.uppercase(), color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, letterSpacing = 1.5.sp); Text(tr(title), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold) }
// DOC L0096: Buttons: Dispone azioni ripetute mantenendo stile e callback espliciti.
@Composable internal fun Buttons(content: @Composable FlowRowScope.() -> Unit) { FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp), content = content) }
// DOC L0097: Workouts: Elenca la raccolta filtrata per modalità e collega creazione, esecuzione e modifica.
@Composable private fun Workouts(vm: StudioViewModel) = Page {
    // DOC L0098: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di Workouts.
    Text(tr(vm.labels.collection), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
    // DOC L0099: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di Workouts.
    AccentButton(onClick = { vm.edit() }) { Text(tr("+ Crea {0}", tr(vm.labels.noun))) }
    // DOC L0100: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di Workouts.
    vm.visibleData.workouts.forEach { w -> WorkoutCard(vm, w) }
    // DOC L0101: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di Workouts.
    if (vm.visibleData.workouts.isEmpty()) Text(tr("Nessun {0} salvato. Crea il tuo o aggiungi quelli pronti.", tr(vm.labels.noun)))
    // DOC L0102: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di Workouts.
    TextButton(onClick = vm::addPresets) { Text(if (vm.mode == "STUDIO") tr("+ Aggiungi programmi pronti") else tr("+ Aggiungi allenamenti pronti")) }
// DOC L0103: Chiude il costrutto aperto alla riga 97.
}
// DOC L0104: WorkoutCard: Presenta un programma e le azioni Avvia, Modifica, Duplica ed Elimina delegate al ViewModel.
@Composable private fun WorkoutCard(vm: StudioViewModel, w: Workout) {
    // DOC L0105: menu: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di WorkoutCard.
    var menu by remember { mutableStateOf(false) }
    // DOC L0106: Esegue il passaggio esplicito «Panel {» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di WorkoutCard.
    Panel {
        // DOC L0107: Esegue il passaggio esplicito «Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di WorkoutCard.
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            // DOC L0108: Esegue il passaggio esplicito «Box(Modifier.size(12.dp).background(Color(android.graphics.Color.parseColor(w.color))))» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di WorkoutCard.
            Box(Modifier.size(12.dp).background(Color(android.graphics.Color.parseColor(w.color))))
            // DOC L0109: Esegue il passaggio esplicito «Spacer(Modifier.width(10.dp))» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di WorkoutCard.
            Spacer(Modifier.width(10.dp))
            // DOC L0110: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di WorkoutCard.
            Text(w.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            // DOC L0111: Esegue il passaggio esplicito «Box {» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di WorkoutCard.
            Box {
                // DOC L0112: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di WorkoutCard.
                TextButton(onClick = { menu = true }, modifier = Modifier.semantics { contentDescription = tr("Opzioni per ${w.title}") }) { Text(tr("⋯"), fontSize = 24.sp) }
                // DOC L0113: Esegue il passaggio esplicito «DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di WorkoutCard.
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    // DOC L0114: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di WorkoutCard.
                    DropdownMenuItem(text = { Text(tr("Modifica")) }, onClick = { menu = false; vm.edit(w) })
                    // DOC L0115: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di WorkoutCard.
                    DropdownMenuItem(text = { Text(tr("Duplica")) }, onClick = { menu = false; vm.duplicate(w) })
                    // DOC L0116: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di WorkoutCard.
                    DropdownMenuItem(text = { Text(tr("Elimina")) }, onClick = { menu = false; vm.remove(w) })
                // DOC L0117: Chiude il costrutto aperto alla riga 113.
                }
            // DOC L0118: Chiude il costrutto aperto alla riga 111.
            }
        // DOC L0119: Chiude il costrutto aperto alla riga 107.
        }
        // DOC L0120: phases: sequenza cronologica compilata dal programma. Nel contesto di WorkoutCard.
        val phases = compileTimeline(w)
        // DOC L0121: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di WorkoutCard.
        if (normalizationNotice(w).isNotEmpty()) Text(tr(normalizationNotice(w)), fontSize = 12.sp)
        // DOC L0122: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di WorkoutCard.
        Text(tr("{0} · {1} intervalli", formatTime(plannedMs(phases)), phases.count { it.type == "WORK" }), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        // DOC L0123: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di WorkoutCard.
        AccentButton(onClick = { vm.start(w) }) { Text(tr("Avvia")) }
    // DOC L0124: Chiude il costrutto aperto alla riga 106.
    }
// DOC L0125: Chiude il costrutto aperto alla riga 104.
}
// DOC L0126: TimerScreen: Mostra fase, tempo residuo, avanzamento e controlli del motore condiviso con il servizio.
@Composable private fun TimerScreen(vm: StudioViewModel) {
    // DOC L0127: timer: motore corrente, opzionale quando nessuna sessione è stata avviata. Nel contesto di TimerScreen.
    val timer = vm.version.let { vm.timer }
    // DOC L0128: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di TimerScreen.
    if (vm.starting) { Page { Text(tr("Avvio del timer…"), style = MaterialTheme.typography.headlineMedium) }; return }
    // DOC L0129: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di TimerScreen.
    if (timer == null) { QuickTimerScreen(vm); return }
    // DOC L0130: done: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di TimerScreen.
    val done = timer.state == "finished"
    // DOC L0131: p: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di TimerScreen.
    val p = timer.currentPhase
    // DOC L0132: controls: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di TimerScreen.
    var controls by rememberSaveable { mutableStateOf(false) }
    // DOC L0133: Esegue il passaggio esplicito «Page {» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di TimerScreen.
    Page {
        // DOC L0134: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di TimerScreen.
        Text(timer.workout.title, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        // DOC L0135: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di TimerScreen.
        Text(if (done) { if (timer.result!!.status == "COMPLETED") (if (timer.workout.studio) tr("Studio completato.") else tr("Allenamento completato.")) else tr("Sessione interrotta.") } else if (timer.state == "paused") tr("In pausa") else when (p?.type) { "PREPARE" -> tr("Preparazione"); "WORK" -> tr(vm.labels.focus); "WARMUP" -> tr("Riscaldamento"); "COOLDOWN" -> tr(vm.labels.final); "REST_LONG" -> tr("Pausa lunga"); else -> tr(vm.labels.rest) }, style = MaterialTheme.typography.headlineMedium)
        // DOC L0136: Esegue il passaggio esplicito «BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di TimerScreen.
        BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            // DOC L0137: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di TimerScreen.
            Text(formatTime(timer.remainingMs), fontSize = if (maxWidth < 350.dp) 88.sp else 108.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, modifier = Modifier.semantics { contentDescription = tr("Tempo rimanente ${formatTime(timer.remainingMs)}") })
        // DOC L0138: Chiude il costrutto aperto alla riga 136.
        }
        // DOC L0139: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di TimerScreen.
        Text(if (done) tr("Sessione registrata") else if (timer.index < 0) tr("Mettiti in posizione. Si parte tra poco.") else tr(p?.label ?: "Sessione registrata"), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
        // DOC L0140: Esegue il passaggio esplicito «LinearProgressIndicator(progress = { timer.index.coerceAtLeast(0).toFloat() / timer.phases.size }, modifier = Modifier.fillMaxWidth())» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di TimerScreen.
        LinearProgressIndicator(progress = { timer.index.coerceAtLeast(0).toFloat() / timer.phases.size }, modifier = Modifier.fillMaxWidth())
        // DOC L0141: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di TimerScreen.
        Text(if (done) tr("{0}/{1} intervalli completati", timer.result!!.completedWorkIntervals, timer.result!!.plannedWorkIntervals) else if (timer.index < 0) tr("20 secondi per prepararti") else tr("Fase {0}/{1} · Tempo attivo {2}", (timer.index + 1).coerceAtMost(timer.phases.size), timer.phases.size, formatTime(timer.elapsedMs)), fontSize = 13.sp)
        // DOC L0142: Esegue il passaggio esplicito «Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di TimerScreen.
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // DOC L0143: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di TimerScreen.
            AccentButton(enabled = !done, onClick = vm::pauseResume, modifier = Modifier.weight(1.5f).heightIn(min = 54.dp)) { Text(if (timer.state == "paused") tr("Riprendi") else tr("Pausa (timer)"), fontSize = 17.sp) }
            // DOC L0144: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di TimerScreen.
            OutlinedButton(enabled = !done, onClick = vm::skip, modifier = Modifier.weight(1f).heightIn(min = 54.dp)) { Text(tr("Salta →")) }
        // DOC L0145: Chiude il costrutto aperto alla riga 142.
        }
        // DOC L0146: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di TimerScreen.
        Panel { val next = timer.nextPhase; Text(if (next != null) tr("Poi: {0} · {1}", tr(next.label), formatTime(next.durationMs)) else tr("Ultima fase"), fontSize = 13.sp) }
        // DOC L0147: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di TimerScreen.
        TextButton(onClick = { controls = !controls }, modifier = Modifier.fillMaxWidth()) { Text(if (controls) tr("Meno controlli −") else tr("Altri controlli +"), color = MaterialTheme.colorScheme.onSurfaceVariant) }
        // DOC L0148: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di TimerScreen.
        if (controls) {
            // DOC L0149: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di TimerScreen.
            OutlinedButton(enabled = !done, onClick = vm::previous) { Text(tr("← Precedente")) }
            // DOC L0150: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di TimerScreen.
            OutlinedButton(onClick = { vm.fullscreen = !vm.fullscreen }) { Text(if (vm.fullscreen) tr("Esci da schermo intero") else tr("Schermo intero")) }
            // DOC L0151: Esegue il passaggio esplicito «Toggle("Suoni", vm.sound, vm::chooseSound)» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di TimerScreen.
            Toggle("Suoni", vm.sound, vm::chooseSound)
            // DOC L0152: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di TimerScreen.
            Text(tr("Bip brevi sul 3, 2, 1. Fischietto all’inizio del lavoro, sospiro all’inizio del recupero e tre ding alla fine dell’allenamento, prima dell’eventuale defaticamento."), fontSize = 12.sp)
            // DOC L0153: Esegue il passaggio esplicito «Toggle("Vibrazione", vm.vibration) { vm.vibration = it }» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di TimerScreen.
            Toggle("Vibrazione", vm.vibration) { vm.vibration = it }
            // DOC L0154: Esegue il passaggio esplicito «Toggle("Schermo acceso", vm.keepAwake) { vm.keepAwake = it }» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di TimerScreen.
            Toggle("Schermo acceso", vm.keepAwake) { vm.keepAwake = it }
            // DOC L0155: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di TimerScreen.
            Text(tr("Se il sistema chiude l’app, alla riapertura l’attività viene salvata come interrotta fino all’ultimo punto registrato. Ruotare lo schermo conserva il timer. I suoni dipendono dal volume del dispositivo."), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        // DOC L0156: Chiude il costrutto aperto alla riga 148.
        }
        // DOC L0157: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di TimerScreen.
        TextButton(enabled = !done, onClick = vm::stop, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text(tr("Termina"), color = MaterialTheme.colorScheme.error) }
        // DOC L0158: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di TimerScreen.
        Text(tr("Il timer continua in background e a schermo spento. Puoi controllarlo dalla notifica e ascoltare musica o video."), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        // DOC L0159: Esegue il passaggio esplicito «timer.result?.let { s ->» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di TimerScreen.
        timer.result?.let { s ->
            // DOC L0160: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di TimerScreen.
            Text(tr("{0}% completato · {1} {2}", completion(s).toInt(), formatTime(s.workMs), if (vm.mode == "STUDIO") tr("di concentrazione") else tr("di lavoro")), fontSize = 14.sp)
            // DOC L0161: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di TimerScreen.
            AccentButton(onClick = { vm.navigate("Cronologia") }) { Text(tr("Vedi attività")) }
            // DOC L0162: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di TimerScreen.
            TextButton(onClick = { vm.navigate("Home") }) { Text(tr("Nuovo timer")) }
        // DOC L0163: Chiude il costrutto aperto alla riga 159.
        }
    // DOC L0164: Chiude il costrutto aperto alla riga 133.
    }
// DOC L0165: Chiude il costrutto aperto alla riga 126.
}
// DOC L0166: Toggle: Rende un interruttore controllato che invia il nuovo valore al chiamante.
@Composable private fun Toggle(label: String, checked: Boolean, change: (Boolean) -> Unit) { Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) { Text(tr(label)); Switch(checked = checked, onCheckedChange = change) } }
// DOC L0167: History: Elenca le attività registrate nella modalità corrente, con accesso ai dettagli.
@Composable private fun History(vm: StudioViewModel) = Page {
    // DOC L0168: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di History.
    Text(if (vm.mode == "STUDIO") tr("Attività di studio") else tr("Attività"), style = MaterialTheme.typography.headlineMedium)
    // DOC L0169: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di History.
    Text(tr("Le tue sessioni, completate o interrotte."), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
    // DOC L0170: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di History.
    OutlinedButton(onClick = { vm.navigate("Statistiche") }) { Text(tr("Statistiche →")) }
    // DOC L0171: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di History.
    if (vm.visibleData.sessions.isEmpty()) Text(tr("Avvia un {0} dal Timer. Al termine troverai qui il riepilogo.", tr(vm.labels.noun)))
    // DOC L0172: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di History.
    vm.visibleData.sessions.reversed().forEach { s -> SessionCard(vm, s) }
    // DOC L0173: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di History.
    TextButton(onClick = { vm.message = "Allenamenti e attività sono già salvati automaticamente sul telefono." }) { Text(tr("Salva cronologia")) }
// DOC L0174: Chiude il costrutto aperto alla riga 167.
}
// DOC L0175: SessionCard: Presenta risultato, durate e completamento di una sessione senza ricalcolarne la cronologia.
@Composable private fun SessionCard(vm: StudioViewModel, s: Session) {
    // DOC L0176: details: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di SessionCard.
    var details by rememberSaveable(s.id) { mutableStateOf(false) }
    // DOC L0177: Esegue il passaggio esplicito «Panel {» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di SessionCard.
    Panel {
        // DOC L0178: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di SessionCard.
        Text(s.workoutTitle.ifEmpty { tr("Allenamento eliminato") }, style = MaterialTheme.typography.titleMedium)
        // DOC L0179: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di SessionCard.
        Text(tr("{0} · {1}", date(s.startedAt), if (s.status == "COMPLETED") tr("Completata") else tr("Interrotta")), color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
        // DOC L0180: Esegue il passaggio esplicito «Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {» nel flusso: Struttura generale Compose e schermate condivise. Nel contesto di SessionCard.
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // DOC L0181: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di SessionCard.
            Column(Modifier.weight(1f)) { Text(tr("Tempo attivo"), fontSize = 11.sp); Text(formatTime(s.elapsedMs), fontSize = 21.sp) }
            // DOC L0182: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di SessionCard.
            Column(Modifier.weight(1f)) { Text(if (s.studio) tr("Concentrazione") else tr("Lavoro svolto"), fontSize = 11.sp); Text(formatTime(s.workMs), fontSize = 21.sp) }
            // DOC L0183: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di SessionCard.
            Column(Modifier.weight(1f)) { Text(tr("Completamento"), fontSize = 11.sp); Text(tr("{0}%", completion(s).toInt()), fontSize = 21.sp) }
        // DOC L0184: Chiude il costrutto aperto alla riga 180.
        }
        // DOC L0185: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di SessionCard.
        TextButton(onClick = { details = !details }) { Text(if (details) tr("Chiudi dettagli") else tr("Dettagli")) }
        // DOC L0186: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di SessionCard.
        if (details) {
            // DOC L0187: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di SessionCard.
            Text(tr("Durata prevista: {0}\nIntervalli {1}: {2}/{3}\nPause: {4}", formatTime(s.plannedMs), if (vm.mode == "STUDIO") tr("di concentrazione") else tr("di lavoro"), s.completedWorkIntervals, s.plannedWorkIntervals, s.pauses), fontSize = 13.sp)
            // DOC L0188: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di SessionCard.
            if (s.plannedPhases != null) Text(tr("Fasi completate: {0}/{1}", s.completedPhases, s.plannedPhases), fontSize = 13.sp)
            // DOC L0189: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di SessionCard.
            TextButton(onClick = { vm.removeSession(s) }) { Text(tr("Elimina attività"), color = MaterialTheme.colorScheme.error) }
        // DOC L0190: Chiude il costrutto aperto alla riga 186.
        }
    // DOC L0191: Chiude il costrutto aperto alla riga 177.
    }
// DOC L0192: Chiude il costrutto aperto alla riga 175.
}
