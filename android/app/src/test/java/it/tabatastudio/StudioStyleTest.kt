package it.tabatastudio

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import it.tabatastudio.ui.StudioStyle
import it.tabatastudio.ui.studioColors
import it.tabatastudio.ui.studioPalettes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StudioStyleTest {
    @Test fun oldAndUnknownStylesUseClassic() {
        for (value in listOf(null, "", "unrecognized")) assertEquals(StudioStyle.CLASSIC, StudioStyle.fromId(value))
        for (style in StudioStyle.entries) assertEquals(style, StudioStyle.fromId(style.id))
        assertEquals(
            listOf("Classico", "Editoriale sportivo", "Neo-brutalismo soft", "Retro digitale", "Bauhaus contemporaneo"),
            StudioStyle.entries.map { it.label }
        )
    }

    @Test fun allStylesPreservePaletteAndReadableTextInBothThemes() {
        for (style in StudioStyle.entries) for (dark in listOf(false, true)) for (palette in studioPalettes) {
            val colors = studioColors(dark, palette.id, style)
            val original = studioColors(dark, palette.id)
            assertEquals(original.primary, colors.primary)
            assertEquals(original.tertiary, colors.tertiary)
            assertEquals(original.onTertiary, colors.onTertiary)
            val description = "${style.id}, ${palette.id}, dark=$dark"
            assertTrue(description, contrast(colors.tertiary, colors.onTertiary) >= 4.5)
            assertTrue(description, contrast(colors.background, colors.onBackground) >= 4.5)
            assertTrue(description, contrast(colors.surface, colors.onSurfaceVariant) >= 4.5)
            assertTrue(description, contrast(style.readoutBackground(colors), colors.onSurface) >= 4.5)
            assertTrue(description, contrast(colors.primaryContainer, colors.onPrimaryContainer) >= 4.5)
        }
    }

    private fun contrast(a: Color, b: Color): Double {
        val min = minOf(a.luminance(), b.luminance())
        val max = maxOf(a.luminance(), b.luminance())
        return (max + .05) / (min + .05)
    }
}
