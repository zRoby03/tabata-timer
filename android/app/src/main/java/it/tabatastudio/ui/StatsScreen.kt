// DOC L0001: Applica l’annotazione dichiarata: Compose identifica UI, Room genera accessi e schema, mentre le annotazioni Test selezionano i casi da eseguire.
@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
// DOC L0002: Assegna il file al namespace it.tabatastudio.ui, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.ui

// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.foundation.Canvas. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.foundation.Canvas
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.foundation.layout.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.foundation.layout.*
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.material3.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.material3.*
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.runtime.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.runtime.*
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.Modifier. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.Modifier
// DOC L0009: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.semantics.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.semantics.*
// DOC L0010: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.text.font.FontWeight. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.text.font.FontWeight
// DOC L0011: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.unit.dp. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.unit.dp
// DOC L0012: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.unit.sp. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.unit.sp
// DOC L0013: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.domain.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.domain.*
// DOC L0014: Rende disponibili le dipendenze esplicite di questa riga: import java.util.Locale. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import java.util.Locale
// DOC L0015: Rende disponibili le dipendenze esplicite di questa riga: import java.time.LocalDate. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import java.time.LocalDate
// DOC L0016: Rende disponibili le dipendenze esplicite di questa riga: import java.time.format.DateTimeFormatter. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import java.time.format.DateTimeFormatter

// DOC L0018: StatsScreen: Calcola e presenta le statistiche delle sole sessioni della modalità corrente nel periodo selezionato.
@Composable internal fun StatsScreen(vm: StudioViewModel) {
    // DOC L0019: days: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. workoutId: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di StatsScreen.
    var days by remember { mutableIntStateOf(30) }; var workoutId by remember { mutableStateOf("") }
    // DOC L0020: data: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di StatsScreen.
    val data = aggregate(vm.visibleData.sessions, days, workoutId)
    // DOC L0021: Esegue il passaggio esplicito «Page {» nel flusso: Statistiche e grafici Compose. Nel contesto di StatsScreen.
    Page {
        // DOC L0022: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StatsScreen.
        Text(if (vm.mode == "STUDIO") tr("Statistiche di studio") else tr("Statistiche"), style = MaterialTheme.typography.headlineMedium)
        // DOC L0023: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StatsScreen.
        Text(if (vm.mode == "STUDIO") tr("Minuti di concentrazione e sessioni dedicate allo studio.") else tr("Misuriamo il tempo di lavoro e la continuità, senza valutare il miglioramento fisico."), fontSize = 13.sp)
        // DOC L0024: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StatsScreen.
        Buttons { listOf(7, 30, 90).forEach { d -> FilterChip(selected = days == d, onClick = { days = d }, label = { Text(tr("{0} giorni", d)) }) } }
        // DOC L0025: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StatsScreen.
        Text(if (vm.mode == "STUDIO") tr("Programma") else tr("Allenamento"))
        // DOC L0026: Esegue il passaggio esplicito «Buttons {» nel flusso: Statistiche e grafici Compose. Nel contesto di StatsScreen.
        Buttons {
            // DOC L0027: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StatsScreen.
            FilterChip(selected = workoutId.isEmpty(), onClick = { workoutId = "" }, label = { Text(tr("Tutti")) })
            // DOC L0028: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. ids: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di StatsScreen.
            val ids = (vm.visibleData.workouts.map { it.id to it.title } + vm.visibleData.sessions.map { it.workoutId to it.workoutTitle }).distinctBy { it.first }
            // DOC L0029: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StatsScreen.
            ids.forEach { (id, title) -> FilterChip(selected = workoutId == id, onClick = { workoutId = id }, label = { Text(title.ifEmpty { tr("Allenamento eliminato") }) }) }
        // DOC L0030: Chiude il costrutto aperto alla riga 26.
        }
        // DOC L0031: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StatsScreen.
        Panel { Text(tr("{0} sessioni", data.sessions.size), style = MaterialTheme.typography.titleLarge); Text(tr("%.1f {0} · %.0f%% completamento medio", if (vm.mode == "STUDIO") tr("minuti di concentrazione") else tr("minuti di lavoro")).format(it.tabatastudio.i18n.AppText.locale, data.workMs / 60000.0, data.completion)) }
        // DOC L0032: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StatsScreen.
        if (data.sessions.isEmpty()) Text(tr("Nessuna sessione nel periodo selezionato."))
        // DOC L0033: Esegue il passaggio esplicito «WeekChart(data.weeks, "Sessioni per settimana", "Sessioni") { it.count.toDouble() }» nel flusso: Statistiche e grafici Compose. Nel contesto di StatsScreen.
        WeekChart(data.weeks, "Sessioni per settimana", "Sessioni") { it.count.toDouble() }
        // DOC L0034: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di StatsScreen.
        WeekChart(data.weeks, if (vm.mode == "STUDIO") "Concentrazione" else "Lavoro svolto", if (vm.mode == "STUDIO") "Minuti di concentrazione" else "Minuti di lavoro") { it.workMs / 60000.0 }
        // DOC L0035: Esegue il passaggio esplicito «WeekChart(data.weeks, "Completamento", "Completamento %") { it.completion }» nel flusso: Statistiche e grafici Compose. Nel contesto di StatsScreen.
        WeekChart(data.weeks, "Completamento", "Completamento %") { it.completion }
        // DOC L0036: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StatsScreen.
        Text(tr("Confronto sessioni"), style = MaterialTheme.typography.titleLarge)
        // DOC L0037: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di StatsScreen.
        data.sessions.forEach { s -> Text(tr("{0} · {1}\n{2} lavoro · {3}%", date(s.startedAt), s.workoutTitle, formatTime(s.workMs), completion(s).toInt()), fontSize = 13.sp) }
    // DOC L0038: Chiude il costrutto aperto alla riga 21.
    }
// DOC L0039: Chiude il costrutto aperto alla riga 18.
}
// DOC L0040: WeekChart: Disegna le barre dei giorni dai dati aggregati; la grafica non modifica i risultati registrati.
@Composable private fun WeekChart(weeks: List<Week>, title: String, unit: String, value: (Week) -> Double) = Panel {
    // DOC L0041: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di WeekChart.
    Text(tr(title), style = MaterialTheme.typography.titleLarge)
    // DOC L0042: max: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di WeekChart.
    val max = (weeks.maxOfOrNull(value) ?: 0.0).coerceAtLeast(1.0)
    // DOC L0043: accent: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di WeekChart.
    val accent = MaterialTheme.colorScheme.tertiary
    // DOC L0044: Esegue il passaggio esplicito «Canvas(Modifier.fillMaxWidth().height(150.dp).semantics { contentDescription = tr("${tr(title)}. Colore principale: ${tr(unit)} per settimana. Valori elencati nella tabella seguent» nel flusso: Statistiche e grafici Compose. Nel contesto di WeekChart.
    Canvas(Modifier.fillMaxWidth().height(150.dp).semantics { contentDescription = tr("${tr(title)}. Colore principale: ${tr(unit)} per settimana. Valori elencati nella tabella seguente.") }) {
        // DOC L0045: step: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di WeekChart.
        val step = size.width / weeks.size.coerceAtLeast(1)
        // DOC L0046: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di WeekChart.
        weeks.forEachIndexed { i, week -> val height = (value(week) / max * size.height).toFloat(); drawRect(accent, topLeft = androidx.compose.ui.geometry.Offset(i * step + 2, size.height - height), size = androidx.compose.ui.geometry.Size((step - 4).coerceAtLeast(1f), height)) }
    // DOC L0047: Chiude il costrutto aperto alla riga 44.
    }
    // DOC L0048: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di WeekChart.
    Text(tr("Colore principale: {0} · settimane dal lunedì (UTC)", tr(unit)), fontSize = 12.sp)
    // DOC L0049: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di WeekChart.
    Row(Modifier.fillMaxWidth()) { Text(tr("Settimana dal"), Modifier.weight(1f), fontWeight = FontWeight.Bold); Text(tr(unit), fontWeight = FontWeight.Bold) }
    // DOC L0050: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di WeekChart.
    weeks.forEach { w -> Row(Modifier.fillMaxWidth()) { Text(LocalDate.parse(w.date).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), Modifier.weight(1f), fontSize = 12.sp); Text("%.1f".format(it.tabatastudio.i18n.AppText.locale, value(w)), fontSize = 12.sp) } }
// DOC L0051: Chiude il costrutto aperto alla riga 40.
}
