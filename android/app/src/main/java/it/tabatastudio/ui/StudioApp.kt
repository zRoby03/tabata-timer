@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
package it.tabatastudio.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.tabatastudio.domain.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal fun date(value: String): String = DateTimeFormatter.ofLocalizedDateTime(java.time.format.FormatStyle.SHORT).withLocale(it.tabatastudio.i18n.AppText.locale).withZone(ZoneId.systemDefault()).format(Instant.parse(value))

@Composable fun StudioApp(vm: StudioViewModel) {
    MaterialTheme(colorScheme = studioColors(vm.darkTheme, vm.palette)) {
        BackHandler(vm.route != "Home" || vm.fullscreen) { if (vm.fullscreen) vm.fullscreen = false else vm.navigate("Home") }
        Scaffold(containerColor = MaterialTheme.colorScheme.background, bottomBar = {
            if (!vm.fullscreen) Surface(tonalElevation = 2.dp) {
                Row(Modifier.fillMaxWidth().navigationBarsPadding().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.SpaceEvenly) {
                    val selected = when (vm.route) { "Home", "Timer" -> "Timer"; "Editor", "Allenamenti" -> "Allenamenti"; "Cronologia", "Statistiche" -> "Attività"; else -> "" }
                    listOf("Timer" to if (vm.active) "Timer" else "Home", vm.labels.collection to "Allenamenti", "Attività" to "Cronologia").forEach { (title, destination) ->
                        TextButton(onClick = { vm.navigate(destination) }, modifier = Modifier.weight(1f).heightIn(min = 48.dp)) {
                            Text(tr(title), fontSize = 13.sp, color = if ((if (title == tr(vm.labels.collection)) "Allenamenti" else title) == selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                if (!vm.fullscreen) Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(tr("t."), fontSize = 24.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Text(tr("Tabata Timer"), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(tr("Workout"), fontSize = 12.sp)
                            Switch(checked = vm.mode == "STUDIO", onCheckedChange = vm::chooseMode, enabled = !vm.active && !vm.quickSaveBusy, modifier = Modifier.semantics { contentDescription = tr("Modalità Studio") })
                            Text(tr("Studio"), fontSize = 12.sp)
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { vm.navigate("Guida") }) { Text(tr("Guida"), fontSize = 13.sp) }
                        TextButton(onClick = { vm.navigate("Dati") }, modifier = Modifier.semantics { contentDescription = tr("Impostazioni") }) { Text(tr("⚙"), fontSize = 23.sp) }
                    }
                }
                if (vm.message.isNotEmpty()) Surface(color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(tr(vm.message), modifier = Modifier.weight(1f).semantics { liveRegion = LiveRegionMode.Polite }, fontSize = 13.sp)
                        TextButton(onClick = { vm.message = "" }) { Text(tr("Chiudi")) }
                    }
                }
                if (!vm.ready) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(tr("Apertura archivio locale…")) }
                else key(vm.route, vm.mode) {
                    when (vm.route) {
                        "Home" -> QuickTimerScreen(vm)
                        "Allenamenti" -> Workouts(vm)
                        "Editor" -> Editor(vm)
                        "Timer" -> TimerScreen(vm)
                        "Cronologia" -> History(vm)
                        "Statistiche" -> StatsScreen(vm)
                        "Guida" -> HelpScreen(vm)
                        else -> DataScreen(vm)
                    }
                }
            }
        }
        if (vm.quickSaveDialog) AlertDialog(
            onDismissRequest = vm::dismissQuickSave,
            title = { Text(tr("Salva {0}", tr(vm.labels.noun))) },
            text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = vm.quickSaveName, onValueChange = { if (it.length <= 120) vm.quickSaveName = it }, label = { Text(if (vm.mode == "STUDIO") tr("Nome del programma") else tr("Nome dell’allenamento")) }, singleLine = true)
                Text(tr("Lo ritroverai in {0} anche dopo aver chiuso l’app. I dati restano solo sul telefono.", tr(vm.labels.collection)), fontSize = 13.sp)
                if (vm.quickSaveError.isNotEmpty()) Text(tr(vm.quickSaveError), color = MaterialTheme.colorScheme.error, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
            } },
            confirmButton = { TextButton(enabled = !vm.quickSaveBusy && vm.quickSaveName.isNotBlank(), onClick = vm::confirmQuickSave) { Text(if (vm.quickSaveBusy) tr("Salvataggio…") else tr("Salva sul dispositivo")) } },
            dismissButton = { TextButton(enabled = !vm.quickSaveBusy, onClick = vm::dismissQuickSave) { Text(tr("Annulla")) } }
        )
        if (vm.confirmTitle.isNotEmpty()) AlertDialog(onDismissRequest = vm::dismissConfirm, title = { Text(tr("Conferma")) }, text = { Text(tr(vm.confirmTitle)) }, confirmButton = { TextButton(onClick = vm::acceptConfirm) { Text(tr("Conferma")) } }, dismissButton = { TextButton(onClick = vm::dismissConfirm) { Text(tr("Annulla")) } })
        vm.importPreview?.let { backup -> AlertDialog(onDismissRequest = { vm.importPreview = null }, title = { Text(tr("Anteprima importazione")) }, text = { Text(tr("{0} programmi (Workout e Studio) · {1} sessioni. La conferma sostituisce tutti i dati. Esporta prima una copia JSON per conservarli.", backup.workouts.size, backup.sessions.size)) }, confirmButton = { TextButton(onClick = vm::applyImport) { Text(tr("Sostituisci dati")) } }, dismissButton = { TextButton(onClick = { vm.importPreview = null }) { Text(tr("Annulla")) } }) }
    }
}
@Composable internal fun Page(content: @Composable ColumnScope.() -> Unit) { Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp), content = content) }
@Composable internal fun Panel(content: @Composable ColumnScope.() -> Unit) { Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) { Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content) } }
@Composable internal fun Heading(kicker: String, title: String) { Text(kicker.uppercase(), color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, letterSpacing = 1.5.sp); Text(tr(title), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold) }
@Composable internal fun Buttons(content: @Composable FlowRowScope.() -> Unit) { FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp), content = content) }
@Composable private fun Workouts(vm: StudioViewModel) = Page {
    Text(tr(vm.labels.collection), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
    AccentButton(onClick = { vm.edit() }) { Text(tr("+ Crea {0}", tr(vm.labels.noun))) }
    vm.visibleData.workouts.forEach { w -> WorkoutCard(vm, w) }
    if (vm.visibleData.workouts.isEmpty()) Text(tr("Nessun {0} salvato. Crea il tuo o aggiungi quelli pronti.", tr(vm.labels.noun)))
    TextButton(onClick = vm::addPresets) { Text(if (vm.mode == "STUDIO") tr("+ Aggiungi programmi pronti") else tr("+ Aggiungi allenamenti pronti")) }
}
@Composable private fun WorkoutCard(vm: StudioViewModel, w: Workout) {
    var menu by remember { mutableStateOf(false) }
    Panel {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(12.dp).background(Color(android.graphics.Color.parseColor(w.color))))
            Spacer(Modifier.width(10.dp))
            Text(w.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Box {
                TextButton(onClick = { menu = true }, modifier = Modifier.semantics { contentDescription = tr("Opzioni per ${w.title}") }) { Text(tr("⋯"), fontSize = 24.sp) }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    DropdownMenuItem(text = { Text(tr("Modifica")) }, onClick = { menu = false; vm.edit(w) })
                    DropdownMenuItem(text = { Text(tr("Duplica")) }, onClick = { menu = false; vm.duplicate(w) })
                    DropdownMenuItem(text = { Text(tr("Elimina")) }, onClick = { menu = false; vm.remove(w) })
                }
            }
        }
        val phases = compileTimeline(w)
        if (normalizationNotice(w).isNotEmpty()) Text(tr(normalizationNotice(w)), fontSize = 12.sp)
        Text(tr("{0} · {1} intervalli", formatTime(plannedMs(phases)), phases.count { it.type == "WORK" }), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        AccentButton(onClick = { vm.start(w) }) { Text(tr("Avvia")) }
    }
}
@Composable private fun TimerScreen(vm: StudioViewModel) {
    val timer = vm.version.let { vm.timer }
    if (vm.starting) { Page { Text(tr("Avvio del timer…"), style = MaterialTheme.typography.headlineMedium) }; return }
    if (timer == null) { QuickTimerScreen(vm); return }
    val done = timer.state == "finished"
    val p = timer.currentPhase
    var controls by rememberSaveable { mutableStateOf(false) }
    Page {
        Text(timer.workout.title, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        Text(if (done) { if (timer.result!!.status == "COMPLETED") (if (timer.workout.studio) tr("Studio completato.") else tr("Allenamento completato.")) else tr("Sessione interrotta.") } else if (timer.state == "paused") tr("In pausa") else when (p?.type) { "PREPARE" -> tr("Preparazione"); "WORK" -> tr(vm.labels.focus); "WARMUP" -> tr("Riscaldamento"); "COOLDOWN" -> tr(vm.labels.final); "REST_LONG" -> tr("Pausa lunga"); else -> tr(vm.labels.rest) }, style = MaterialTheme.typography.headlineMedium)
        BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(formatTime(timer.remainingMs), fontSize = if (maxWidth < 350.dp) 88.sp else 108.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, modifier = Modifier.semantics { contentDescription = tr("Tempo rimanente ${formatTime(timer.remainingMs)}") })
        }
        Text(if (done) tr("Sessione registrata") else if (timer.index < 0) tr("Mettiti in posizione. Si parte tra poco.") else tr(p?.label ?: "Sessione registrata"), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
        LinearProgressIndicator(progress = { timer.index.coerceAtLeast(0).toFloat() / timer.phases.size }, modifier = Modifier.fillMaxWidth())
        Text(if (done) tr("{0}/{1} intervalli completati", timer.result!!.completedWorkIntervals, timer.result!!.plannedWorkIntervals) else if (timer.index < 0) tr("20 secondi per prepararti") else tr("Fase {0}/{1} · Tempo attivo {2}", (timer.index + 1).coerceAtMost(timer.phases.size), timer.phases.size, formatTime(timer.elapsedMs)), fontSize = 13.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AccentButton(enabled = !done, onClick = vm::pauseResume, modifier = Modifier.weight(1.5f).heightIn(min = 54.dp)) { Text(if (timer.state == "paused") tr("Riprendi") else tr("Pausa (timer)"), fontSize = 17.sp) }
            OutlinedButton(enabled = !done, onClick = vm::skip, modifier = Modifier.weight(1f).heightIn(min = 54.dp)) { Text(tr("Salta →")) }
        }
        Panel { val next = timer.nextPhase; Text(if (next != null) tr("Poi: {0} · {1}", tr(next.label), formatTime(next.durationMs)) else tr("Ultima fase"), fontSize = 13.sp) }
        TextButton(onClick = { controls = !controls }, modifier = Modifier.fillMaxWidth()) { Text(if (controls) tr("Meno controlli −") else tr("Altri controlli +"), color = MaterialTheme.colorScheme.onSurfaceVariant) }
        if (controls) {
            OutlinedButton(enabled = !done, onClick = vm::previous) { Text(tr("← Precedente")) }
            OutlinedButton(onClick = { vm.fullscreen = !vm.fullscreen }) { Text(if (vm.fullscreen) tr("Esci da schermo intero") else tr("Schermo intero")) }
            Toggle("Suoni", vm.sound, vm::chooseSound)
            Text(tr("Bip brevi sul 3, 2, 1. Fischietto all’inizio del lavoro, sospiro all’inizio del recupero e tre ding alla fine dell’allenamento, prima dell’eventuale defaticamento."), fontSize = 12.sp)
            Toggle("Vibrazione", vm.vibration) { vm.vibration = it }
            Toggle("Schermo acceso", vm.keepAwake) { vm.keepAwake = it }
            Text(tr("Se il sistema chiude l’app, alla riapertura l’attività viene salvata come interrotta fino all’ultimo punto registrato. Ruotare lo schermo conserva il timer. I suoni dipendono dal volume del dispositivo."), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        TextButton(enabled = !done, onClick = vm::stop, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text(tr("Termina"), color = MaterialTheme.colorScheme.error) }
        Text(tr("Il timer continua in background e a schermo spento. Puoi controllarlo dalla notifica e ascoltare musica o video."), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        timer.result?.let { s ->
            Text(tr("{0}% completato · {1} {2}", completion(s).toInt(), formatTime(s.workMs), if (vm.mode == "STUDIO") tr("di concentrazione") else tr("di lavoro")), fontSize = 14.sp)
            AccentButton(onClick = { vm.navigate("Cronologia") }) { Text(tr("Vedi attività")) }
            TextButton(onClick = { vm.navigate("Home") }) { Text(tr("Nuovo timer")) }
        }
    }
}
@Composable private fun Toggle(label: String, checked: Boolean, change: (Boolean) -> Unit) { Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) { Text(tr(label)); Switch(checked = checked, onCheckedChange = change) } }
@Composable private fun History(vm: StudioViewModel) = Page {
    Text(if (vm.mode == "STUDIO") tr("Attività di studio") else tr("Attività"), style = MaterialTheme.typography.headlineMedium)
    Text(tr("Le tue sessioni, completate o interrotte."), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
    OutlinedButton(onClick = { vm.navigate("Statistiche") }) { Text(tr("Statistiche →")) }
    if (vm.visibleData.sessions.isEmpty()) Text(tr("Avvia un {0} dal Timer. Al termine troverai qui il riepilogo.", tr(vm.labels.noun)))
    vm.visibleData.sessions.reversed().forEach { s -> SessionCard(vm, s) }
    TextButton(onClick = { vm.message = "Allenamenti e attività sono già salvati automaticamente sul telefono." }) { Text(tr("Salva cronologia")) }
}
@Composable private fun SessionCard(vm: StudioViewModel, s: Session) {
    var details by rememberSaveable(s.id) { mutableStateOf(false) }
    Panel {
        Text(s.workoutTitle.ifEmpty { tr("Allenamento eliminato") }, style = MaterialTheme.typography.titleMedium)
        Text(tr("{0} · {1}", date(s.startedAt), if (s.status == "COMPLETED") tr("Completata") else tr("Interrotta")), color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(1f)) { Text(tr("Tempo attivo"), fontSize = 11.sp); Text(formatTime(s.elapsedMs), fontSize = 21.sp) }
            Column(Modifier.weight(1f)) { Text(if (s.studio) tr("Concentrazione") else tr("Lavoro svolto"), fontSize = 11.sp); Text(formatTime(s.workMs), fontSize = 21.sp) }
            Column(Modifier.weight(1f)) { Text(tr("Completamento"), fontSize = 11.sp); Text(tr("{0}%", completion(s).toInt()), fontSize = 21.sp) }
        }
        TextButton(onClick = { details = !details }) { Text(if (details) tr("Chiudi dettagli") else tr("Dettagli")) }
        if (details) {
            Text(tr("Durata prevista: {0}\nIntervalli {1}: {2}/{3}\nPause: {4}", formatTime(s.plannedMs), if (vm.mode == "STUDIO") tr("di concentrazione") else tr("di lavoro"), s.completedWorkIntervals, s.plannedWorkIntervals, s.pauses), fontSize = 13.sp)
            if (s.plannedPhases != null) Text(tr("Fasi completate: {0}/{1}", s.completedPhases, s.plannedPhases), fontSize = 13.sp)
            TextButton(onClick = { vm.removeSession(s) }) { Text(tr("Elimina attività"), color = MaterialTheme.colorScheme.error) }
        }
    }
}
