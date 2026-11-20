package it.tabatastudio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable internal fun AppearanceSettings(vm: StudioViewModel) = Panel {
    Text(tr("Aspetto"), style = MaterialTheme.typography.titleLarge)
    Text(tr("Scegli il tema e il colore dell’app. La modifica è immediata e resta su questo dispositivo."), fontSize = 13.sp)
    Text(tr("Tema"), style = MaterialTheme.typography.labelLarge)
    Buttons {
        FilterChip(selected = !vm.darkTheme, onClick = { vm.chooseDarkTheme(false) }, label = { Text(tr("Chiaro")) })
        FilterChip(selected = vm.darkTheme, onClick = { vm.chooseDarkTheme(true) }, label = { Text(tr("Scuro")) })
    }
    Text(tr("Palette colori"), style = MaterialTheme.typography.labelLarge)
    Buttons {
        studioPalettes.forEach { p ->
            FilterChip(selected = vm.palette == p.id, onClick = { vm.choosePalette(p.id) }, label = { Text(tr(p.name)) }, leadingIcon = { Box(Modifier.size(16.dp).background(p.swatch, CircleShape)) })
        }
    }
}
