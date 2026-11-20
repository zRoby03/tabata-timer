// DOC L0001: Assegna il file al namespace it.tabatastudio.ui, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.ui

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.foundation.background. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.foundation.background
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.foundation.layout.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.foundation.layout.*
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.foundation.shape.CircleShape. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.foundation.shape.CircleShape
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.material3.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.material3.*
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.runtime.Composable. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.runtime.Composable
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.Modifier. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.Modifier
// DOC L0009: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.unit.dp. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.unit.dp
// DOC L0010: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.unit.sp. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.unit.sp

// DOC L0012: AppearanceSettings: Disegna le scelte Chiaro/Scuro e le nove palette, delegando il salvataggio al ViewModel.
@Composable internal fun AppearanceSettings(vm: StudioViewModel) = Panel {
    // DOC L0013: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di AppearanceSettings.
    Text(tr("Aspetto"), style = MaterialTheme.typography.titleLarge)
    // DOC L0014: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di AppearanceSettings.
    Text(tr("Scegli il tema e il colore dell’app. La modifica è immediata e resta su questo dispositivo."), fontSize = 13.sp)
    // DOC L0015: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di AppearanceSettings.
    Text(tr("Tema"), style = MaterialTheme.typography.labelLarge)
    // DOC L0016: Esegue il passaggio esplicito «Buttons {» nel flusso: Scelta di tema e palette in Compose. Nel contesto di AppearanceSettings.
    Buttons {
        // DOC L0017: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di AppearanceSettings.
        FilterChip(selected = !vm.darkTheme, onClick = { vm.chooseDarkTheme(false) }, label = { Text(tr("Chiaro")) })
        // DOC L0018: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di AppearanceSettings.
        FilterChip(selected = vm.darkTheme, onClick = { vm.chooseDarkTheme(true) }, label = { Text(tr("Scuro")) })
    // DOC L0019: Chiude il costrutto aperto alla riga 16.
    }
    // DOC L0020: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di AppearanceSettings.
    Text(tr("Palette colori"), style = MaterialTheme.typography.labelLarge)
    // DOC L0021: Esegue il passaggio esplicito «Buttons {» nel flusso: Scelta di tema e palette in Compose. Nel contesto di AppearanceSettings.
    Buttons {
        // DOC L0022: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di AppearanceSettings.
        studioPalettes.forEach { p ->
            // DOC L0023: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di AppearanceSettings.
            FilterChip(selected = vm.palette == p.id, onClick = { vm.choosePalette(p.id) }, label = { Text(tr(p.name)) }, leadingIcon = { Box(Modifier.size(16.dp).background(p.swatch, CircleShape)) })
        // DOC L0024: Chiude il costrutto aperto alla riga 22.
        }
    // DOC L0025: Chiude il costrutto aperto alla riga 21.
    }
// DOC L0026: Chiude il costrutto aperto alla riga 12.
}
