package com.veltrix.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkColors = darkColorScheme(
    primary = Color(0xFF6366F1),
    secondary = Color(0xFF10B981),
    tertiary = Color(0xFF38BDF8),
    background = Color(0xFF020617),
    surface = Color(0xFF0B1220),
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF4F46E5),
    secondary = Color(0xFF059669),
    tertiary = Color(0xFF0891B2),
    background = Color(0xFFF8FAFC),
    surface = Color.White.copy(alpha = 0.68f),
    onBackground = Color(0xFF020617),
    onSurface = Color(0xFF020617)
)

private val AppTypography = Typography(
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 32.sp, letterSpacing = (-0.6).sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 24.sp, letterSpacing = (-0.4).sp),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 20.sp, letterSpacing = (-0.2).sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 16.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, fontSize = 12.sp)
)

@Immutable
data class VeltrixColors(
    val glass: Color,
    val glassBorder: Color,
    val glassHighlight: Color,
    val gradientStart: Color,
    val gradientEnd: Color,
    val scrollbar: Color
)

private val LocalVeltrixColors = staticCompositionLocalOf {
    VeltrixColors(
        glass = Color(0xFF0F172A).copy(alpha = 0.82f),
        glassBorder = Color.White.copy(alpha = 0.08f),
        glassHighlight = Color(0xFF111827).copy(alpha = 0.92f),
        gradientStart = Color(0xFF6366F1),
        gradientEnd = Color(0xFF10B981),
        scrollbar = Color(0xFF6366F1)
    )
}

val springStiffness = 500f
val springDampingRatio = 0.75f

@Composable
fun VeltrixTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalVeltrixColors provides VeltrixColors(
            glass = if (darkTheme) Color(0xFF0B1220).copy(alpha = 0.88f) else Color.White.copy(alpha = 0.68f),
            glassBorder = if (darkTheme) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.72f),
            glassHighlight = if (darkTheme) Color(0xFF111827).copy(alpha = 0.96f) else Color(0xFF6366F1).copy(alpha = 0.08f),
            gradientStart = Color(0xFF6366F1),
            gradientEnd = Color(0xFF10B981),
            scrollbar = Color(0xFF6366F1)
        )
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = AppTypography,
            content = content
        )
    }
}

object VeltrixThemeDefaults {
    val colors: VeltrixColors
        @Composable get() = LocalVeltrixColors.current
}

@Composable
fun VeltrixBackground(content: @Composable () -> Unit) {
    val colors = VeltrixThemeDefaults.colors
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-120).dp, y = (-90).dp)
                .size(280.dp)
                .blur(120.dp)
                .background(
                    brush = Brush.radialGradient(
                        listOf(colors.gradientStart.copy(alpha = 0.15f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 120.dp, y = (-60).dp)
                .size(260.dp)
                .blur(120.dp)
                .background(
                    brush = Brush.radialGradient(
                        listOf(colors.gradientEnd.copy(alpha = 0.10f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        content()
    }
}
