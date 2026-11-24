package it.tabatastudio.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.tabatastudio.domain.*

@Composable internal fun TimelinePreview(workout: Workout, phases: List<Phase>) {
    if (workout.studio) {
        phases.take(150).forEach { phase ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(phaseLabel(phase), Modifier.weight(1f), fontSize = 12.sp)
                Text(formatTime(phase.durationMs), fontSize = 12.sp)
            }
        }
        return
    }
    val lastWork = phases.indexOfLast { it.type == "WORK" }
    val groups = phases.take(150).withIndex().groupBy { it.value.blockIndex?.let { block -> "block-$block" } ?: it.value.type }
    groups.values.forEach { entries ->
        val first = entries.first().value
        OutlinedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(if (first.blockIndex == null) phaseLabel(first) else tr("Blocco {0} — {1}", first.blockIndex + 1, first.blockTitle), fontWeight = FontWeight.SemiBold)
                var cycle: Int? = null
                entries.forEach { (index, p) ->
                    if (p.cycleIndex != null && p.cycleIndex != cycle) {
                        Text(tr("Ciclo {0}/{1}", p.cycleIndex + 1, p.cycles), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        cycle = p.cycleIndex
                    }
                    if (p.entryCue == "ding2") Text(if ((p.cycleIndex ?: 0) > 0) tr("Due ding — inizio ciclo {0}", p.cycleIndex!! + 1) else tr("Due ding — inizio Blocco {0}", p.blockIndex!! + 1), fontSize = 11.sp)
                    if (p.type == "REST_BLOCK") Text(tr("Due sospiri — cambio blocco"), fontSize = 11.sp)
                    val name = if (p.type == "WORK") {
                        if (p.labelArgs.getOrNull(4)?.toIntOrNull()?.let { it > 1 } == true) tr("{0} · ripetizione {1}/{2}", p.labelArgs[0], p.labelArgs[3], p.labelArgs[4]) else p.labelArgs[0]
                    } else phaseLabel(p)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(name, Modifier.weight(1f), fontSize = 12.sp, color = if (p.type == "WORK") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(formatTime(p.durationMs), fontSize = 12.sp)
                    }
                    if (index == lastWork) Text(tr("Tre ding — fine lavoro"), fontSize = 11.sp)
                    if (p.type == "COOLDOWN") Text(tr("Tre ding — fine defaticamento"), fontSize = 11.sp)
                }
            }
        }
    }
}
