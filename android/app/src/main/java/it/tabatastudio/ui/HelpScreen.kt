package it.tabatastudio.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import it.tabatastudio.i18n.helpSections

@Composable internal fun HelpScreen(vm: StudioViewModel) = Page {
    Text(tr("Guida all’uso"), style = MaterialTheme.typography.headlineMedium)
    helpSections.forEach { section ->
        Panel {
            Text(tr(section.title), style = MaterialTheme.typography.titleLarge)
            section.entries.forEach { entry ->
                Text((if (entry.kind == "li") "• " else "") + tr(entry.text), fontWeight = if (entry.kind == "h3") FontWeight.SemiBold else FontWeight.Normal)
            }
        }
    }
    AccentButton(onClick = { vm.navigate("Home") }) { Text(tr("Vai al timer")) }
}
