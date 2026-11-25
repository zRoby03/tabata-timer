package it.tabatastudio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
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
        "bauhaus", "Bauhaus contemporaneo", 2.dp, 2.dp,
        StyleSurfaces(Color(0xFFF4EFE5), Color(0xFFFFFCF5), Color(0xFF766C5E)),
        StyleSurfaces(Color(0xFF1C1A17), Color(0xFF292621), Color(0xFF938678))
    );

    fun surfaces(darkTheme: Boolean) = if (darkTheme) dark else light
    fun readoutBackground(colors: ColorScheme): Color = when (this) {
        NEO_BRUTALISM -> colors.tertiary.copy(alpha = if (colors.darkAppearance) .29f else .50f).compositeOver(colors.surface)
        RETRO -> {
            val base = if (colors.darkAppearance) Color(0xFFC7C2B0) else Color(0xFFE0DACA)
            colors.tertiary.copy(alpha = if (colors.darkAppearance) .32f else .43f).compositeOver(base)
        }
        else -> colors.surface
    }

    fun readoutInk(colors: ColorScheme) = if (this == RETRO) Color(0xFF1D221D) else colors.onSurface

    fun quickFieldBackground(colors: ColorScheme) = when (this) {
        NEO_BRUTALISM -> colors.tertiary.copy(alpha = if (colors.darkAppearance) .10f else .18f).compositeOver(colors.surface)
        RETRO -> colors.surfaceVariant
        else -> colors.surface
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
internal val ColorScheme.darkAppearance get() = background.luminance() < .5f
private val digitalFont = FontFamily(Font(R.font.dseg7_bold, FontWeight.Bold))

@Composable internal fun pageInset() = when (LocalConfiguration.current.screenWidthDp) {
    in 0..359 -> 12.dp
    in 360..767 -> 16.dp
    else -> 24.dp
}

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
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val curveShare = if (LocalConfiguration.current.screenWidthDp < 360) .24f else .32f
        val curveWidth = maxWidth * curveShare
        val maxSize = when {
            running -> if (maxWidth < 350.dp) 88.sp else 108.sp
            style == StudioStyle.CLASSIC -> 48.sp
            style == StudioStyle.EDITORIAL -> 72.sp
            else -> 64.sp
        }
        ReadoutBody(value, running, heading, hint, style, colors, curveWidth, maxSize)
    }
}

@Composable
private fun ReadoutBody(
    value: String,
    running: Boolean,
    heading: String?,
    hint: String?,
    style: StudioStyle,
    colors: ColorScheme,
    curveWidth: Dp,
    maxSize: TextUnit
) {
    val shape = RoundedCornerShape(style.corner)
    val leftAligned = style == StudioStyle.BAUHAUS
    val ink = style.readoutInk(colors)
    val styleFrame = when (style) {
        StudioStyle.CLASSIC -> Modifier
        StudioStyle.EDITORIAL -> Modifier.drawBehind {
            val stroke = 1.dp.toPx()
            for (y in listOf(stroke / 2, size.height - stroke / 2)) {
                drawLine(colors.outline, Offset(0f, y), Offset(size.width, y), stroke)
            }
        }.padding(vertical = 16.dp)
        StudioStyle.NEO_BRUTALISM -> Modifier
            .offsetShadow(style.corner, if (colors.darkAppearance) Color(0xFF0A090D) else Color(0xFF27212A))
            .background(style.readoutBackground(colors), shape)
            .border(2.dp, colors.outline, shape)
            .padding(12.dp)
        StudioStyle.RETRO -> Modifier
            .background(style.readoutBackground(colors), shape)
            .border(3.dp, Color(0xFF171512), shape)
            .padding(3.dp)
            .border(1.dp, Color(0x3328221B), RoundedCornerShape((style.corner - 3.dp).coerceAtLeast(0.dp)))
            .padding(9.dp)
        StudioStyle.BAUHAUS -> Modifier
            .clip(shape)
            .background(style.readoutBackground(colors), shape)
            .border(1.dp, colors.outline, shape)
            .drawBehind {
                val radiusX = curveWidth.toPx()
                drawArc(
                    colors.tertiary, 90f, 180f, useCenter = true,
                    topLeft = Offset(size.width - radiusX, 1.dp.toPx()),
                    size = Size(radiusX * 2, (size.height - 2.dp.toPx()).coerceAtLeast(0f))
                )
            }.padding(start = 12.dp, top = 12.dp, end = curveWidth + 8.dp, bottom = 12.dp)
    }
    Column(
        Modifier.fillMaxWidth().then(styleFrame),
        horizontalAlignment = if (leftAligned) Alignment.Start else Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(if (style == StudioStyle.CLASSIC) 0.dp else 4.dp)
    ) {
        val labelColor = if (style == StudioStyle.CLASSIC) colors.onSurfaceVariant else ink
        heading?.let { Text(it, color = labelColor, fontSize = 12.sp) }
        val description = if (running) tr("Tempo rimanente {0}", value) else null
        BasicText(
            text = value,
            modifier = Modifier.fillMaxWidth().semantics { if (description != null) contentDescription = description },
            style = LocalTextStyle.current.copy(
                color = if (running && style == StudioStyle.CLASSIC) colors.primary else ink,
                fontSize = maxSize,
                fontFamily = if (style == StudioStyle.RETRO) digitalFont else LocalTextStyle.current.fontFamily,
                fontWeight = when (style) {
                    StudioStyle.CLASSIC -> FontWeight.SemiBold
                    StudioStyle.RETRO -> FontWeight.Bold
                    else -> FontWeight.ExtraBold
                },
                fontFeatureSettings = "tnum",
                textGeometricTransform = if (style == StudioStyle.EDITORIAL) TextGeometricTransform(scaleX = .93f) else null,
                textAlign = if (leftAligned) TextAlign.Start else TextAlign.Center
            ),
            softWrap = false,
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(minFontSize = 16.sp, maxFontSize = maxSize, stepSize = 1.sp)
        )
        hint?.let { Text(it, fontSize = 12.sp, color = labelColor) }
    }
}
