@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
package it.tabatastudio.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.tabatastudio.domain.*
import java.util.Locale
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable internal fun StatsScreen(vm: StudioViewModel) {
    var days by remember { mutableIntStateOf(30) }; var workoutId by remember { mutableStateOf("") }
    val data = aggregate(vm.visibleData.sessions, days, workoutId)
    Page {
        Text(if (vm.mode == "STUDIO") tr("Statistiche di studio") else tr("Statistiche"), style = MaterialTheme.typography.headlineMedium)
        Text(if (vm.mode == "STUDIO") tr("Minuti di concentrazione e sessioni dedicate allo studio.") else tr("Misuriamo il tempo di lavoro e la continuità, senza valutare il miglioramento fisico."), fontSize = 13.sp)
        Buttons { listOf(7, 30, 90).forEach { d -> FilterChip(selected = days == d, onClick = { days = d }, label = { Text(tr("{0} giorni", d)) }) } }
        Text(if (vm.mode == "STUDIO") tr("Programma") else tr("Allenamento"))
        Buttons {
            FilterChip(selected = workoutId.isEmpty(), onClick = { workoutId = "" }, label = { Text(tr("Tutti")) })
            val ids = (vm.visibleData.workouts.map { it.id to it.title } + vm.visibleData.sessions.map { it.workoutId to it.workoutTitle }).distinctBy { it.first }
            ids.forEach { (id, title) -> FilterChip(selected = workoutId == id, onClick = { workoutId = id }, label = { Text(title.ifEmpty { tr("Allenamento eliminato") }) }) }
        }
        Panel { Text(tr("{0} sessioni", data.sessions.size), style = MaterialTheme.typography.titleLarge); Text(tr("%.1f {0} · %.0f%% completamento medio", if (vm.mode == "STUDIO") tr("minuti di concentrazione") else tr("minuti di lavoro")).format(it.tabatastudio.i18n.AppText.locale, data.workMs / 60000.0, data.completion)) }
        if (data.sessions.isEmpty()) Text(tr("Nessuna sessione nel periodo selezionato."))
        WeekChart(data.weeks, "Sessioni per settimana", "Sessioni") { it.count.toDouble() }
        WeekChart(data.weeks, if (vm.mode == "STUDIO") "Concentrazione" else "Lavoro svolto", if (vm.mode == "STUDIO") "Minuti di concentrazione" else "Minuti di lavoro") { it.workMs / 60000.0 }
        WeekChart(data.weeks, "Completamento", "Completamento %") { it.completion }
        Text(tr("Confronto sessioni"), style = MaterialTheme.typography.titleLarge)
        data.sessions.forEach { s -> Text(tr("{0} · {1}\n{2} lavoro · {3}%", date(s.startedAt), s.workoutTitle, formatTime(s.workMs), completion(s).toInt()), fontSize = 13.sp) }
    }
}
@Composable private fun WeekChart(weeks: List<Week>, title: String, unit: String, value: (Week) -> Double) = Panel {
    Text(tr(title), style = MaterialTheme.typography.titleLarge)
    val max = (weeks.maxOfOrNull(value) ?: 0.0).coerceAtLeast(1.0)
    val accent = MaterialTheme.colorScheme.tertiary
    Canvas(Modifier.fillMaxWidth().height(150.dp).semantics { contentDescription = tr("${tr(title)}. Colore principale: ${tr(unit)} per settimana. Valori elencati nella tabella seguente.") }) {
        val step = size.width / weeks.size.coerceAtLeast(1)
        weeks.forEachIndexed { i, week -> val height = (value(week) / max * size.height).toFloat(); drawRect(accent, topLeft = androidx.compose.ui.geometry.Offset(i * step + 2, size.height - height), size = androidx.compose.ui.geometry.Size((step - 4).coerceAtLeast(1f), height)) }
    }
    Text(tr("Colore principale: {0} · settimane dal lunedì (UTC)", tr(unit)), fontSize = 12.sp)
    Row(Modifier.fillMaxWidth()) { Text(tr("Settimana dal"), Modifier.weight(1f), fontWeight = FontWeight.Bold); Text(tr(unit), fontWeight = FontWeight.Bold) }
    weeks.forEach { w -> Row(Modifier.fillMaxWidth()) { Text(LocalDate.parse(w.date).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), Modifier.weight(1f), fontSize = 12.sp); Text("%.1f".format(it.tabatastudio.i18n.AppText.locale, value(w)), fontSize = 12.sp) } }
}
