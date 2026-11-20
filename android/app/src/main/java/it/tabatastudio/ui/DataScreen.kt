// DOC L0001: Assegna il file al namespace it.tabatastudio.ui, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.ui

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import androidx.activity.compose.rememberLauncherForActivityResult. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.activity.compose.rememberLauncherForActivityResult
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import androidx.activity.result.contract.ActivityResultContracts. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.activity.result.contract.ActivityResultContracts
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.material3.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.material3.*
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.runtime.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.runtime.*
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.platform.LocalContext. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.platform.LocalContext
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.unit.sp. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.unit.sp
// DOC L0009: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.domain.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.domain.*
// DOC L0010: Rende disponibili le dipendenze esplicite di questa riga: import kotlinx.coroutines.Dispatchers. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import kotlinx.coroutines.Dispatchers
// DOC L0011: Rende disponibili le dipendenze esplicite di questa riga: import kotlinx.coroutines.launch. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import kotlinx.coroutines.launch
// DOC L0012: Rende disponibili le dipendenze esplicite di questa riga: import kotlinx.coroutines.withContext. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import kotlinx.coroutines.withContext
// DOC L0013: Rende disponibili le dipendenze esplicite di questa riga: import java.io.ByteArrayOutputStream. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import java.io.ByteArrayOutputStream

// DOC L0015: DataScreen: Espone backup JSON, CSV, importazione con anteprima e cancellazione confermata attraverso il selettore file Android.
@Composable internal fun DataScreen(vm: StudioViewModel) {
    // DOC L0016: context: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di DataScreen.
    val context = LocalContext.current
    // DOC L0017: scope: ambito coroutine annullato alla chiusura del servizio o del componente. Nel contesto di DataScreen.
    val scope = rememberCoroutineScope()
    // DOC L0018: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. exportJson: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di DataScreen.
    val exportJson = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri -> if (uri != null) scope.launch {
        // DOC L0019: Esegue il passaggio esplicito «runCatching { withContext(Dispatchers.IO) { context.contentResolver.openOutputStream(uri)?.use { it.write(vm.exportContent.toByteArray(Charsets.UTF_8)) } ?: error("File non disponi» nel flusso: Impostazioni dati, backup e lingua Android. Nel contesto di DataScreen.
        runCatching { withContext(Dispatchers.IO) { context.contentResolver.openOutputStream(uri)?.use { it.write(vm.exportContent.toByteArray(Charsets.UTF_8)) } ?: error("File non disponibile") } }.onSuccess { vm.message = "Copia JSON esportata." }.onFailure { vm.message = "Esportazione non riuscita: ${it.message}" }
    // DOC L0020: Chiude il costrutto aperto alla riga 18.
    } }
    // DOC L0021: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. exportCsv: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di DataScreen.
    val exportCsv = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri -> if (uri != null) scope.launch {
        // DOC L0022: Esegue il passaggio esplicito «runCatching { withContext(Dispatchers.IO) { context.contentResolver.openOutputStream(uri)?.use { it.write(vm.exportContent.toByteArray(Charsets.UTF_8)) } ?: error("File non disponi» nel flusso: Impostazioni dati, backup e lingua Android. Nel contesto di DataScreen.
        runCatching { withContext(Dispatchers.IO) { context.contentResolver.openOutputStream(uri)?.use { it.write(vm.exportContent.toByteArray(Charsets.UTF_8)) } ?: error("File non disponibile") } }.onSuccess { vm.message = "CSV esportato." }.onFailure { vm.message = "Esportazione non riuscita: ${it.message}" }
    // DOC L0023: Chiude il costrutto aperto alla riga 21.
    } }
    // DOC L0024: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. importer: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di DataScreen.
    val importer = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> if (uri != null) scope.launch {
        // DOC L0025: Esegue il passaggio esplicito «runCatching { withContext(Dispatchers.IO) {» nel flusso: Impostazioni dati, backup e lingua Android. Nel contesto di DataScreen.
        runCatching { withContext(Dispatchers.IO) {
            // DOC L0026: Esegue il passaggio esplicito «context.contentResolver.openInputStream(uri)?.use { stream ->» nel flusso: Impostazioni dati, backup e lingua Android. Nel contesto di DataScreen.
            context.contentResolver.openInputStream(uri)?.use { stream ->
                // DOC L0027: out: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. buffer: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di DataScreen.
                val out = ByteArrayOutputStream(); val buffer = ByteArray(8192)
                // DOC L0028: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di DataScreen.
                while (true) { val count = stream.read(buffer); if (count < 0) break; require(out.size() + count <= MAX_BACKUP_BYTES) { "File troppo grande: massimo 5 MiB" }; out.write(buffer, 0, count) }
                // DOC L0029: Esegue il passaggio esplicito «out.toByteArray().toString(Charsets.UTF_8)» nel flusso: Impostazioni dati, backup e lingua Android. Nel contesto di DataScreen.
                out.toByteArray().toString(Charsets.UTF_8)
            // DOC L0030: Esegue il passaggio esplicito «} ?: error("File non disponibile")» nel flusso: Impostazioni dati, backup e lingua Android. Nel contesto di DataScreen.
            } ?: error("File non disponibile")
        // DOC L0031: Esegue il passaggio esplicito «} }.onSuccess(vm::loadImport).onFailure { vm.message = "Importazione rifiutata: ${it.message}" }» nel flusso: Impostazioni dati, backup e lingua Android. Nel contesto di DataScreen.
        } }.onSuccess(vm::loadImport).onFailure { vm.message = "Importazione rifiutata: ${it.message}" }
    // DOC L0032: Chiude il costrutto aperto alla riga 24.
    } }
    // DOC L0033: Esegue il passaggio esplicito «Page {» nel flusso: Impostazioni dati, backup e lingua Android. Nel contesto di DataScreen.
    Page {
        // DOC L0034: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
        Text(tr("Impostazioni"), style = MaterialTheme.typography.headlineMedium)
        // DOC L0035: Esegue il passaggio esplicito «Panel {» nel flusso: Impostazioni dati, backup e lingua Android. Nel contesto di DataScreen.
        Panel {
            // DOC L0036: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
            Text(tr("Lingua"), style = MaterialTheme.typography.titleLarge)
            // DOC L0037: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
            Text(tr("La lingua iniziale segue il dispositivo. La scelta resta su questo dispositivo."))
            // DOC L0038: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
            Buttons { listOf("it" to "Italiano", "en" to "English", "es" to "Español").forEach { (code, name) -> FilterChip(selected = vm.language == code, onClick = { vm.chooseLanguage(code) }, label = { Text(name) }) } }
        // DOC L0039: Chiude il costrutto aperto alla riga 35.
        }
        // DOC L0040: Esegue il passaggio esplicito «AppearanceSettings(vm)» nel flusso: Impostazioni dati, backup e lingua Android. Nel contesto di DataScreen.
        AppearanceSettings(vm)
        // DOC L0041: Esegue il passaggio esplicito «Panel {» nel flusso: Impostazioni dati, backup e lingua Android. Nel contesto di DataScreen.
        Panel {
            // DOC L0042: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
            Text(tr("Timer in background"), style = MaterialTheme.typography.titleLarge)
            // DOC L0043: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
            Text(tr("Continua anche con lo schermo spento o mentre usi altre app. La notifica offre Pausa, Riprendi, Salta e Termina. I segnali si mescolano alla musica e usano il volume multimediale."), fontSize = 14.sp)
            // DOC L0044: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
            Text(tr("Se non vedi i controlli nella notifica, abilita le notifiche di Tabata Timer nelle impostazioni Android."), fontSize = 13.sp)
            // DOC L0045: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
            OutlinedButton(onClick = { context.startActivity(android.content.Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)) }) { Text(tr("Notifiche Android")) }
        // DOC L0046: Chiude il costrutto aperto alla riga 41.
        }
        // DOC L0047: Esegue il passaggio esplicito «Panel {» nel flusso: Impostazioni dati, backup e lingua Android. Nel contesto di DataScreen.
        Panel {
            // DOC L0048: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
            Text(tr("Salvataggio"), style = MaterialTheme.typography.titleLarge)
            // DOC L0049: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
            Text(tr("Attivo su questo dispositivo"), color = MaterialTheme.colorScheme.primary)
            // DOC L0050: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
            Text(tr("Allenamenti e attività sono salvati automaticamente sul telefono, anche dopo la chiusura dell’app."), fontSize = 14.sp)
            // DOC L0051: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
            Text(tr("Se disinstalli l’app o ne cancelli i dati, l’archivio viene eliminato. Esporta una copia JSON per conservarlo anche nei tuoi file."), fontSize = 13.sp)
        // DOC L0052: Chiude il costrutto aperto alla riga 47.
        }
        // DOC L0053: Esegue il passaggio esplicito «Panel {» nel flusso: Impostazioni dati, backup e lingua Android. Nel contesto di DataScreen.
        Panel {
            // DOC L0054: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
            Text(tr("Copia di sicurezza"), style = MaterialTheme.typography.titleLarge)
            // DOC L0055: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
            Text(tr("Il file JSON contiene allenamenti, programmi di studio e attività di entrambe le modalità. Puoi conservarlo sul telefono o trasferirlo manualmente su un altro dispositivo."), fontSize = 13.sp)
            // DOC L0056: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
            AccentButton(onClick = { vm.exportContent = backupJson(vm.backupForExport()); exportJson.launch("tabata-backup-v1.json") }) { Text(tr("Esporta copia JSON")) }
            // DOC L0057: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
            OutlinedButton(enabled = !vm.active, onClick = { importer.launch(arrayOf("application/json", "text/plain", "application/octet-stream")) }) { Text(tr("Importa copia JSON")) }
            // DOC L0058: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
            Text(tr("L’importazione mostra un’anteprima e sostituisce i dati soltanto dopo conferma."), fontSize = 13.sp)
            // DOC L0059: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
            OutlinedButton(onClick = { vm.exportContent = sessionsCsv(vm.backupForExport().forMode(vm.mode).sessions); exportCsv.launch("tabata-sessioni.csv") }) { Text(tr("Esporta attività CSV")) }
            // DOC L0060: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
            Text(tr("Il CSV serve per i fogli di calcolo. Per ripristinare i dati nell’app usa il JSON."), fontSize = 12.sp)
        // DOC L0061: Chiude il costrutto aperto alla riga 53.
        }
        // DOC L0062: Esegue il passaggio esplicito «Panel {» nel flusso: Impostazioni dati, backup e lingua Android. Nel contesto di DataScreen.
        Panel {
            // DOC L0063: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
            Text(tr("Gestione dati"), style = MaterialTheme.typography.titleLarge)
            // DOC L0064: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
            Text(tr("La cancellazione elimina i dati di entrambe le modalità e richiede conferma."), fontSize = 13.sp)
            // DOC L0065: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
            TextButton(onClick = vm::clear) { Text(tr("Cancella tutti i dati"), color = MaterialTheme.colorScheme.error) }
        // DOC L0066: Chiude il costrutto aperto alla riga 62.
        }
        // DOC L0067: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di DataScreen.
        OutlinedButton(onClick = { vm.navigate("Guida") }) { Text(tr("Apri la guida")) }
    // DOC L0068: Chiude il costrutto aperto alla riga 33.
    }
// DOC L0069: Chiude il costrutto aperto alla riga 15.
}
