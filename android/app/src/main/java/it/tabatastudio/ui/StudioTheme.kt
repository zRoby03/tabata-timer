// DOC L0001: Assegna il file al namespace it.tabatastudio.ui, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.ui

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.foundation.layout.RowScope. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.foundation.layout.RowScope
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.material3.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.material3.*
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.runtime.Composable. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.runtime.Composable
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.Modifier. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.Modifier
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.graphics.Color. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.graphics.Color
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.graphics.compositeOver. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.graphics.compositeOver

// DOC L0010: Dichiara il tipo o contenitore di questo modulo: Colori Material e componenti accentati Android. I campi e i metodi sono descritti nelle righe successive.
internal data class StudioPalette(val id: String, val name: String, val swatch: Color, val lightAccent: Color, val darkAccent: Color, val darkFill: Color, val lightOnFill: Color, val darkOnFill: Color)
// DOC L0011: Esegue il passaggio esplicito «internal val studioPalettes = listOf(» nel flusso: Colori Material e componenti accentati Android.
internal val studioPalettes = listOf(
    // DOC L0012: Esegue il passaggio esplicito «StudioPalette("purple", "Viola", Color(0xFF8650A7), Color(0xFF713B91), Color(0xFFD5ADF2), Color(0xFFBF8CE0), Color.White, Color(0xFF23122B)),» nel flusso: Colori Material e componenti accentati Android.
    StudioPalette("purple", "Viola", Color(0xFF8650A7), Color(0xFF713B91), Color(0xFFD5ADF2), Color(0xFFBF8CE0), Color.White, Color(0xFF23122B)),
    // DOC L0013: Esegue il passaggio esplicito «StudioPalette("blue", "Blu", Color(0xFF275FC0), Color(0xFF2456A4), Color(0xFFA5C8FF), Color(0xFF8CB5FF), Color.White, Color(0xFF102344)),» nel flusso: Colori Material e componenti accentati Android.
    StudioPalette("blue", "Blu", Color(0xFF275FC0), Color(0xFF2456A4), Color(0xFFA5C8FF), Color(0xFF8CB5FF), Color.White, Color(0xFF102344)),
    // DOC L0014: Esegue il passaggio esplicito «StudioPalette("lime", "Verde chiaro", Color(0xFFA4E35B), Color(0xFF386518), Color(0xFFB4ED7A), Color(0xFFA4E35B), Color(0xFF20320C), Color(0xFF20320C)),» nel flusso: Colori Material e componenti accentati Android.
    StudioPalette("lime", "Verde chiaro", Color(0xFFA4E35B), Color(0xFF386518), Color(0xFFB4ED7A), Color(0xFFA4E35B), Color(0xFF20320C), Color(0xFF20320C)),
    // DOC L0015: Esegue il passaggio esplicito «StudioPalette("green", "Verde scuro", Color(0xFF21734A), Color(0xFF19603D), Color(0xFF8BDBAD), Color(0xFF71C695), Color.White, Color(0xFF0B2E1B)),» nel flusso: Colori Material e componenti accentati Android.
    StudioPalette("green", "Verde scuro", Color(0xFF21734A), Color(0xFF19603D), Color(0xFF8BDBAD), Color(0xFF71C695), Color.White, Color(0xFF0B2E1B)),
    // DOC L0016: Esegue il passaggio esplicito «StudioPalette("sky", "Azzurro", Color(0xFF69D5F0), Color(0xFF006378), Color(0xFF8EE1F6), Color(0xFF69D5F0), Color(0xFF08313D), Color(0xFF08313D)),» nel flusso: Colori Material e componenti accentati Android.
    StudioPalette("sky", "Azzurro", Color(0xFF69D5F0), Color(0xFF006378), Color(0xFF8EE1F6), Color(0xFF69D5F0), Color(0xFF08313D), Color(0xFF08313D)),
    // DOC L0017: Esegue il passaggio esplicito «StudioPalette("orange", "Arancione", Color(0xFFFF9C3D), Color(0xFF9A4708), Color(0xFFFFBB7B), Color(0xFFFF9C3D), Color(0xFF3C1E04), Color(0xFF3C1E04)),» nel flusso: Colori Material e componenti accentati Android.
    StudioPalette("orange", "Arancione", Color(0xFFFF9C3D), Color(0xFF9A4708), Color(0xFFFFBB7B), Color(0xFFFF9C3D), Color(0xFF3C1E04), Color(0xFF3C1E04)),
    // DOC L0018: Esegue il passaggio esplicito «StudioPalette("yellow", "Giallo", Color(0xFFFFE000), Color(0xFF746000), Color(0xFFFFE85B), Color(0xFFFFE000), Color(0xFF302800), Color(0xFF302800)),» nel flusso: Colori Material e componenti accentati Android.
    StudioPalette("yellow", "Giallo", Color(0xFFFFE000), Color(0xFF746000), Color(0xFFFFE85B), Color(0xFFFFE000), Color(0xFF302800), Color(0xFF302800)),
    // DOC L0019: Esegue il passaggio esplicito «StudioPalette("red", "Rosso", Color(0xFFC52D3D), Color(0xFFB02A35), Color(0xFFFFABB2), Color(0xFFFF8996), Color.White, Color(0xFF3B1017)),» nel flusso: Colori Material e componenti accentati Android.
    StudioPalette("red", "Rosso", Color(0xFFC52D3D), Color(0xFFB02A35), Color(0xFFFFABB2), Color(0xFFFF8996), Color.White, Color(0xFF3B1017)),
    // DOC L0020: Esegue il passaggio esplicito «StudioPalette("pink", "Rosa", Color(0xFFEE8BB8), Color(0xFF9B275D), Color(0xFFFFABD3), Color(0xFFF29AC5), Color(0xFF3A1024), Color(0xFF3A1024))» nel flusso: Colori Material e componenti accentati Android.
    StudioPalette("pink", "Rosa", Color(0xFFEE8BB8), Color(0xFF9B275D), Color(0xFFFFABD3), Color(0xFFF29AC5), Color(0xFF3A1024), Color(0xFF3A1024))
// DOC L0021: Chiude un blocco, una chiamata o un valore composto; non aggiunge una nuova operazione.
)
// DOC L0022: studioColors: Deriva la ColorScheme Material dal tema e dalla palette selezionati, con colori di testo leggibili.
internal fun studioColors(dark: Boolean, paletteId: String): ColorScheme {
    // DOC L0023: p: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di studioColors.
    val p = studioPalettes.firstOrNull { it.id == paletteId } ?: studioPalettes.first()
    // DOC L0024: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. accent: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di studioColors.
    val accent = if (dark) p.darkAccent else p.lightAccent
    // DOC L0025: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. fill: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di studioColors.
    val fill = if (dark) p.darkFill else p.swatch
    // DOC L0026: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. onFill: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di studioColors.
    val onFill = if (dark) p.darkOnFill else p.lightOnFill
    // DOC L0027: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. surface: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di studioColors.
    val surface = if (dark) Color(0xFF1C222C) else Color.White
    // DOC L0028: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. background: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di studioColors.
    val background = if (dark) Color(0xFF11151C) else Color(0xFFF4F6F8)
    // DOC L0029: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. text: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di studioColors.
    val text = if (dark) Color(0xFFEDF1F7) else Color(0xFF202632)
    // DOC L0030: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. muted: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di studioColors.
    val muted = if (dark) Color(0xFFB2BDCC) else Color(0xFF5D6571)
    // DOC L0031: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. line: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di studioColors.
    val line = if (dark) Color(0xFF414C5E) else Color(0xFFDCE1E7)
    // DOC L0032: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. container: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di studioColors.
    val container = accent.copy(alpha = if (dark) 0.13f else 0.06f).compositeOver(surface)
    // DOC L0033: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. base: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di studioColors.
    val base = if (dark) darkColorScheme() else lightColorScheme()
    // DOC L0034: Restituisce il valore calcolato o conclude il percorso della funzione. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di studioColors.
    return base.copy(
        // DOC L0035: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di studioColors.
        primary = accent, onPrimary = if (dark) Color(0xFF11151C) else Color.White,
        // DOC L0036: Esegue il passaggio esplicito «primaryContainer = container, onPrimaryContainer = accent,» nel flusso: Colori Material e componenti accentati Android. Nel contesto di studioColors.
        primaryContainer = container, onPrimaryContainer = accent,
        // DOC L0037: Esegue il passaggio esplicito «secondary = accent, onSecondary = text, secondaryContainer = container, onSecondaryContainer = accent,» nel flusso: Colori Material e componenti accentati Android. Nel contesto di studioColors.
        secondary = accent, onSecondary = text, secondaryContainer = container, onSecondaryContainer = accent,
        // DOC L0038: Esegue il passaggio esplicito «tertiary = fill, onTertiary = onFill,» nel flusso: Colori Material e componenti accentati Android. Nel contesto di studioColors.
        tertiary = fill, onTertiary = onFill,
        // DOC L0039: Esegue il passaggio esplicito «background = background, onBackground = text, surface = surface, onSurface = text,» nel flusso: Colori Material e componenti accentati Android. Nel contesto di studioColors.
        background = background, onBackground = text, surface = surface, onSurface = text,
        // DOC L0040: Esegue il passaggio esplicito «surfaceVariant = container, onSurfaceVariant = muted, surfaceTint = accent,» nel flusso: Colori Material e componenti accentati Android. Nel contesto di studioColors.
        surfaceVariant = container, onSurfaceVariant = muted, surfaceTint = accent,
        // DOC L0041: Esegue il passaggio esplicito «surfaceContainerLowest = background, surfaceContainerLow = surface, surfaceContainer = surface,» nel flusso: Colori Material e componenti accentati Android. Nel contesto di studioColors.
        surfaceContainerLowest = background, surfaceContainerLow = surface, surfaceContainer = surface,
        // DOC L0042: Esegue il passaggio esplicito «surfaceContainerHigh = surface, surfaceContainerHighest = container,» nel flusso: Colori Material e componenti accentati Android. Nel contesto di studioColors.
        surfaceContainerHigh = surface, surfaceContainerHighest = container,
        // DOC L0043: Esegue il passaggio esplicito «outline = line, outlineVariant = line» nel flusso: Colori Material e componenti accentati Android. Nel contesto di studioColors.
        outline = line, outlineVariant = line
    // DOC L0044: Chiude un blocco, una chiamata o un valore composto; non aggiunge una nuova operazione.
    )
// DOC L0045: Chiude il costrutto aperto alla riga 22.
}

// Vivid fills have their own readable foreground, including yellow and light green.
// DOC L0048: AccentButton: Rende il pulsante principale con i colori e gli stati abilitato/disabilitato del tema.
@Composable internal fun AccentButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, content: @Composable RowScope.() -> Unit) {
    // DOC L0049: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di AccentButton.
    Button(onClick = onClick, modifier = modifier, enabled = enabled, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary, contentColor = MaterialTheme.colorScheme.onTertiary), content = content)
// DOC L0050: Chiude il costrutto aperto alla riga 48.
}
