package it.tabatastudio.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import it.tabatastudio.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.charset.CharacterCodingException
import kotlinx.coroutines.CancellationException

@Composable internal fun DataScreen(vm: StudioViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val exportJson = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri -> if (uri != null) scope.launch {
        runCatching { withContext(Dispatchers.IO) { context.contentResolver.openOutputStream(uri)?.use { it.write(vm.exportContent.toByteArray(Charsets.UTF_8)) } ?: error("File non disponibile") } }.onSuccess { vm.message = "Copia JSON esportata." }.onFailure { vm.message = "Esportazione non riuscita: ${it.message}" }
    } }
    val exportCsv = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri -> if (uri != null) scope.launch {
        runCatching { withContext(Dispatchers.IO) { context.contentResolver.openOutputStream(uri)?.use { it.write(vm.exportContent.toByteArray(Charsets.UTF_8)) } ?: error("File non disponibile") } }.onSuccess { vm.message = "CSV esportato." }.onFailure { vm.message = "Esportazione non riuscita: ${it.message}" }
    } }
    val importer = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> if (uri != null) scope.launch {
        runCatching { withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val out = ByteArrayOutputStream(); val buffer = ByteArray(8192)
                while (true) { val count = stream.read(buffer); if (count < 0) break; require(out.size() + count <= MAX_BACKUP_BYTES) { "File troppo grande: massimo 5 MiB" }; out.write(buffer, 0, count) }
                val raw = try {
                    Charsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(out.toByteArray())).toString().removePrefix("\uFEFF")
                } catch (_: CharacterCodingException) {
                    error("Il file non è un JSON UTF-8 valido.")
                }
                parseBackup(raw)
            } ?: error("File non disponibile")
        } }.onSuccess(vm::loadImport).onFailure {
            if (it is CancellationException) throw it
            vm.message = tr("Importazione rifiutata: {0}", tr(it.message ?: "JSON non valido"))
        }
    } }
    Page {
        Text(tr("Impostazioni"), style = MaterialTheme.typography.headlineMedium)
        Panel {
            Text(tr("Lingua"), style = MaterialTheme.typography.titleLarge)
            Text(tr("La lingua iniziale segue il dispositivo. La scelta resta su questo dispositivo."))
            Buttons { listOf("it" to "Italiano", "en" to "English", "es" to "Español").forEach { (code, name) -> FilterChip(selected = vm.language == code, onClick = { vm.chooseLanguage(code) }, label = { Text(name) }) } }
        }
        AppearanceSettings(vm)
        Panel {
            Text(tr("Timer in background"), style = MaterialTheme.typography.titleLarge)
            Text(tr("Continua anche con lo schermo spento o mentre usi altre app. La notifica offre Pausa, Riprendi, Salta e Termina. I segnali si mescolano alla musica e usano il volume multimediale."), fontSize = 14.sp)
            Text(tr("Se non vedi i controlli nella notifica, abilita le notifiche di Tabata Timer nelle impostazioni Android."), fontSize = 13.sp)
            OutlinedButton(onClick = { context.startActivity(android.content.Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)) }) { Text(tr("Notifiche Android")) }
        }
        Panel {
            Text(tr("Salvataggio"), style = MaterialTheme.typography.titleLarge)
            Text(tr("Attivo su questo dispositivo"), color = MaterialTheme.colorScheme.primary)
            Text(tr("Allenamenti e attività sono salvati automaticamente sul telefono, anche dopo la chiusura dell’app."), fontSize = 14.sp)
            Text(tr("Se disinstalli l’app o ne cancelli i dati, l’archivio viene eliminato. Esporta una copia JSON per conservarlo anche nei tuoi file."), fontSize = 13.sp)
        }
        Panel {
            Text(tr("Copia di sicurezza"), style = MaterialTheme.typography.titleLarge)
            Text(tr("Il file JSON contiene allenamenti, programmi di studio e attività di entrambe le modalità. Puoi conservarlo sul telefono o trasferirlo manualmente su un altro dispositivo."), fontSize = 13.sp)
            AccentButton(onClick = { vm.exportContent = backupJson(vm.backupForExport()); exportJson.launch("tabata-backup-v1.json") }) { Text(tr("Esporta copia JSON")) }
            OutlinedButton(enabled = !vm.active, onClick = { importer.launch(arrayOf("application/json", "text/plain", "application/octet-stream")) }) { Text(tr("Importa copia JSON")) }
            Text(tr("L’importazione mostra un’anteprima e sostituisce i dati soltanto dopo conferma."), fontSize = 13.sp)
            OutlinedButton(onClick = { vm.exportContent = sessionsCsv(vm.backupForExport().forMode(vm.mode).sessions); exportCsv.launch("tabata-sessioni.csv") }) { Text(tr("Esporta attività CSV")) }
            Text(tr("Il CSV serve per i fogli di calcolo. Per ripristinare i dati nell’app usa il JSON."), fontSize = 12.sp)
        }
        Panel {
            Text(tr("Gestione dati"), style = MaterialTheme.typography.titleLarge)
            Text(tr("La cancellazione elimina programmi e attività di entrambe le modalità. Le preferenze e le copie esportate restano disponibili."), fontSize = 13.sp)
            TextButton(onClick = vm::clear) { Text(tr("Cancella tutti i dati"), color = MaterialTheme.colorScheme.error) }
        }
        OutlinedButton(onClick = { vm.navigate("Guida") }) { Text(tr("Apri la guida")) }
    }
}
