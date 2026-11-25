package it.tabatastudio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.tabatastudio.R

internal data class StyleSurfaces(val background: Color, val surface: Color, val line: Color)

internal enum class StudioStyle(
    val id: String,
    val label: String,
    val corner: Dp,
    val controlCorner: Dp,
    private val light: StyleSurfaces? = null,
    private val dark: StyleSurfaces? = null
) {
    CLASSIC("classic", "Classico", 12.dp, 12.dp),
    EDITORIAL(
        "editorial", "Editoriale sportivo", 10.dp, 8.dp,
        StyleSurfaces(Color(0xFFF4F2EE), Color.White, Color(0xFFD7D3CC)),
        StyleSurfaces(Color(0xFF141517), Color(0xFF212224), Color(0xFF4C4D50))
    ),
    NEO_BRUTALISM(
        "neo-brutalism", "Neo-brutalismo soft", 12.dp, 9.dp,
        StyleSurfaces(Color(0xFFFFF6E9), Color(0xFFFFFBF5), Color(0xFF554751)),
        StyleSurfaces(Color(0xFF1A181F), Color(0xFF25212A), Color(0xFFB5A5B6))
    ),
    RETRO(
        "retro", "Retro digitale", 12.dp, 7.dp,
        StyleSurfaces(Color(0xFFEFEBE3), Color(0xFFFAF7F0), Color(0xFF8C8274)),
        StyleSurfaces(Color(0xFF201E1B), Color(0xFF302C27), Color(0xFF867B6E))
    ),
    BAUHAUS(
        "bauhaus", "Bauhaus contemporaneo", 4.dp, 3.dp,
        StyleSurfaces(Color(0xFFF4EFE5), Color(0xFFFFFCF5), Color(0xFF766C5E)),
        StyleSurfaces(Color(0xFF1C1A17), Color(0xFF292621), Color(0xFF938678))
    );

    fun surfaces(darkTheme: Boolean) = if (darkTheme) dark else light
    fun readoutBackground(colors: ColorScheme): Color {
        val tint = when (this) {
            NEO_BRUTALISM -> .22f
            RETRO -> .20f
            BAUHAUS -> .14f
            CLASSIC, EDITORIAL -> return colors.surface
        }
        return colors.tertiary.copy(alpha = tint).compositeOver(colors.surface)
    }

    fun shapes(base: Shapes): Shapes {
        if (this == CLASSIC) return base
        val control = RoundedCornerShape(controlCorner)
        val panel = RoundedCornerShape(corner)
        return base.copy(extraSmall = control, small = control, medium = panel, large = panel, extraLarge = panel)
    }

    companion object {
        fun fromId(id: String?) = entries.firstOrNull { it.id == id } ?: CLASSIC
    }
}

internal val LocalStudioStyle = staticCompositionLocalOf { StudioStyle.CLASSIC }
private val digitalFont = FontFamily(Font(R.font.dseg7_bold, FontWeight.Bold))

internal fun Modifier.offsetShadow(radius: Dp, color: Color) = drawBehind {
    val offset = 3.dp.toPx()
    drawRoundRect(color, topLeft = Offset(offset, offset), size = size, cornerRadius = CornerRadius(radius.toPx()))
}

@Composable
internal fun TimerReadout(
    value: String,
    running: Boolean = false,
    heading: String? = null,
    hint: String? = null
) {
    val style = LocalStudioStyle.current
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(style.corner)
    val styleFrame = when (style) {
        StudioStyle.CLASSIC -> Modifier
        StudioStyle.EDITORIAL -> Modifier.drawBehind {
            val stroke = 1.dp.toPx()
            for (y in listOf(stroke / 2, size.height - stroke / 2)) {
                drawLine(colors.outline, Offset(0f, y), Offset(size.width, y), stroke)
            }
        }.padding(vertical = 10.dp)
        StudioStyle.NEO_BRUTALISM -> Modifier
            .offsetShadow(style.corner, Color(0xFF1C1820))
            .background(style.readoutBackground(colors), shape)
            .border(2.dp, colors.outline, shape)
            .padding(10.dp)
        StudioStyle.RETRO -> Modifier
            .background(style.readoutBackground(colors), shape)
            .border(2.dp, colors.outline, shape)
            .padding(12.dp)
        StudioStyle.BAUHAUS -> Modifier
            .background(style.readoutBackground(colors), shape)
            .border(1.dp, colors.outline, shape)
            .drawBehind {
                // The semicircle is decorative. The existing progress indicator remains the time cue.
                val diameter = 38.dp.toPx()
                drawArc(
                    colors.tertiary, -90f, 180f, useCenter = true,
                    topLeft = Offset(size.width - diameter - 7.dp.toPx(), (size.height - diameter) / 2),
                    size = Size(diameter, diameter)
                )
            }.padding(start = 10.dp, top = 10.dp, end = 34.dp, bottom = 10.dp)
    }
    Column(Modifier.fillMaxWidth().then(styleFrame), horizontalAlignment = Alignment.CenterHorizontally) {
        val labelColor = if (style == StudioStyle.CLASSIC) colors.onSurfaceVariant else colors.onSurface
        heading?.let { Text(it, color = labelColor, fontSize = 12.sp) }
        BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            val maxSize = when {
                running -> if (maxWidth < 350.dp) 88.sp else 108.sp
                style == StudioStyle.CLASSIC -> 48.sp
                else -> 58.sp
            }
            val description = if (running) tr("Tempo rimanente {0}", value) else null
            BasicText(
                text = value,
                modifier = Modifier.semantics { if (description != null) contentDescription = description },
                style = LocalTextStyle.current.copy(
                    color = if (running && style == StudioStyle.CLASSIC) colors.primary else colors.onSurface,
                    fontSize = maxSize,
                    fontFamily = if (style == StudioStyle.RETRO) digitalFont else LocalTextStyle.current.fontFamily,
                    fontWeight = when (style) {
                        StudioStyle.CLASSIC -> FontWeight.SemiBold
                        StudioStyle.RETRO -> FontWeight.Bold
                        else -> FontWeight.ExtraBold
                    },
                    fontFeatureSettings = "tnum",
                    textGeometricTransform = if (style == StudioStyle.EDITORIAL) TextGeometricTransform(scaleX = .93f) else null,
                    textAlign = TextAlign.Center
                ),
                softWrap = false,
                maxLines = 1,
                autoSize = TextAutoSize.StepBased(minFontSize = 16.sp, maxFontSize = maxSize, stepSize = 1.sp)
            )
        }
        hint?.let { Text(it, fontSize = 12.sp, color = labelColor) }
    }
}
