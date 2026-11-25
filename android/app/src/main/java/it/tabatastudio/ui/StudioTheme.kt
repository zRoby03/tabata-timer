package it.tabatastudio.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp

internal data class StudioPalette(val id: String, val name: String, val swatch: Color, val lightAccent: Color, val darkAccent: Color, val darkFill: Color, val lightOnFill: Color, val darkOnFill: Color)
internal val studioPalettes = listOf(
    StudioPalette("purple", "Viola", Color(0xFF8650A7), Color(0xFF713B91), Color(0xFFD5ADF2), Color(0xFFBF8CE0), Color.White, Color(0xFF23122B)),
    StudioPalette("blue", "Blu", Color(0xFF275FC0), Color(0xFF2456A4), Color(0xFFA5C8FF), Color(0xFF8CB5FF), Color.White, Color(0xFF102344)),
    StudioPalette("lime", "Verde chiaro", Color(0xFFA4E35B), Color(0xFF386518), Color(0xFFB4ED7A), Color(0xFFA4E35B), Color(0xFF20320C), Color(0xFF20320C)),
    StudioPalette("green", "Verde scuro", Color(0xFF21734A), Color(0xFF19603D), Color(0xFF8BDBAD), Color(0xFF71C695), Color.White, Color(0xFF0B2E1B)),
    StudioPalette("sky", "Azzurro", Color(0xFF69D5F0), Color(0xFF006378), Color(0xFF8EE1F6), Color(0xFF69D5F0), Color(0xFF08313D), Color(0xFF08313D)),
    StudioPalette("orange", "Arancione", Color(0xFFFF9C3D), Color(0xFF9A4708), Color(0xFFFFBB7B), Color(0xFFFF9C3D), Color(0xFF3C1E04), Color(0xFF3C1E04)),
    StudioPalette("yellow", "Giallo", Color(0xFFFFE000), Color(0xFF746000), Color(0xFFFFE85B), Color(0xFFFFE000), Color(0xFF302800), Color(0xFF302800)),
    StudioPalette("red", "Rosso", Color(0xFFC52D3D), Color(0xFFB02A35), Color(0xFFFFABB2), Color(0xFFFF8996), Color.White, Color(0xFF3B1017)),
    StudioPalette("pink", "Rosa", Color(0xFFEE8BB8), Color(0xFF9B275D), Color(0xFFFFABD3), Color(0xFFF29AC5), Color(0xFF3A1024), Color(0xFF3A1024))
)
internal fun studioColors(dark: Boolean, paletteId: String, style: StudioStyle = StudioStyle.CLASSIC): ColorScheme {
    val p = studioPalettes.firstOrNull { it.id == paletteId } ?: studioPalettes.first()
    val accent = if (dark) p.darkAccent else p.lightAccent
    val fill = if (dark) p.darkFill else p.swatch
    val onFill = if (dark) p.darkOnFill else p.lightOnFill
    val surfaces = style.surfaces(dark)
    val surface = surfaces?.surface ?: if (dark) Color(0xFF1C222C) else Color.White
    val background = surfaces?.background ?: if (dark) Color(0xFF11151C) else Color(0xFFF4F6F8)
    val text = if (dark) Color(0xFFEDF1F7) else Color(0xFF202632)
    val muted = if (dark) Color(0xFFB2BDCC) else Color(0xFF5D6571)
    val line = surfaces?.line ?: if (dark) Color(0xFF414C5E) else Color(0xFFDCE1E7)
    val container = accent.copy(alpha = if (dark) 0.13f else 0.06f).compositeOver(surface)
    val base = if (dark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = accent, onPrimary = if (dark) Color(0xFF11151C) else Color.White,
        primaryContainer = container, onPrimaryContainer = accent,
        secondary = accent, onSecondary = text, secondaryContainer = container, onSecondaryContainer = accent,
        tertiary = fill, onTertiary = onFill,
        background = background, onBackground = text, surface = surface, onSurface = text,
        surfaceVariant = container, onSurfaceVariant = muted, surfaceTint = accent,
        surfaceContainerLowest = background, surfaceContainerLow = surface, surfaceContainer = surface,
        surfaceContainerHigh = surface, surfaceContainerHighest = container,
        outline = line, outlineVariant = line
    )
}

// Vivid fills have their own readable foreground, including yellow and light green.
@Composable internal fun AccentButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, content: @Composable RowScope.() -> Unit) {
    val style = LocalStudioStyle.current
    val colors = MaterialTheme.colorScheme
    val outlined = style == StudioStyle.NEO_BRUTALISM && enabled
    val frame = if (outlined) modifier.offsetShadow(style.controlCorner, Color(0xFF1C1820)) else modifier
    Button(
        onClick = onClick, modifier = frame, enabled = enabled,
        shape = if (style == StudioStyle.CLASSIC) ButtonDefaults.shape else RoundedCornerShape(style.controlCorner),
        border = if (outlined) BorderStroke(2.dp, colors.outline) else null,
        colors = ButtonDefaults.buttonColors(containerColor = colors.tertiary, contentColor = colors.onTertiary),
        content = content
    )
}
