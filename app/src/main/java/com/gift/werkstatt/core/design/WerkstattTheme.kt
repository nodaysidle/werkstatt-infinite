package com.gift.werkstatt.core.design

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Volt = Color(0xFFC8FF00)

private val Ink = Color(0xFFF0F0F5)
private val DeepBackground = Color(0xFF0A0A0F)
private val DeepSurface = Color(0xFF14141F)
private val RaisedSurface = Color(0xFF1A1A28)
private val Muted = Color(0xFF6B6B80)
private val BlueAccent = Color(0xFF58C7F3)
private val Error = Color(0xFFFF7E7E)
private val CanvasPaper = Color(0xFF0E0E16)
private val CanvasPaperRaised = Color(0xFF181828)

@Immutable
data class WerkstattColors(
    val background: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val text: Color,
    val textMuted: Color,
    val accent: Color,
    val accentSecondary: Color,
    val onAccent: Color,
    val error: Color,
    val canvas: Color,
    val canvasRaised: Color,
    val grid: Color,
    val strokeDefault: Color
)

val LocalWerkstattColors = staticCompositionLocalOf {
    WerkstattColors(
        background = DeepBackground,
        surface = DeepSurface,
        surfaceRaised = RaisedSurface,
        text = Ink,
        textMuted = Muted,
        accent = Volt,
        accentSecondary = BlueAccent,
        onAccent = DeepBackground,
        error = Error,
        canvas = CanvasPaper,
        canvasRaised = CanvasPaperRaised,
        grid = Volt.copy(alpha = 0.10f),
        strokeDefault = Ink
    )
}

private val WerkstattScheme: ColorScheme = darkColorScheme(
    primary = Volt,
    onPrimary = DeepBackground,
    primaryContainer = Volt.copy(alpha = 0.16f),
    onPrimaryContainer = Volt,
    secondary = BlueAccent,
    onSecondary = DeepBackground,
    secondaryContainer = BlueAccent.copy(alpha = 0.16f),
    onSecondaryContainer = BlueAccent,
    background = DeepBackground,
    onBackground = Ink,
    surface = DeepSurface,
    onSurface = Ink,
    surfaceVariant = RaisedSurface,
    onSurfaceVariant = Ink.copy(alpha = 0.72f),
    outline = Muted,
    outlineVariant = Muted.copy(alpha = 0.42f),
    error = Error,
    onError = DeepBackground,
    errorContainer = Error.copy(alpha = 0.16f),
    onErrorContainer = Error
)

private val WerkstattTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    )
)

private val WerkstattShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
)

@Composable
fun WerkstattTheme(
    content: @Composable () -> Unit
) {
    val colors = LocalWerkstattColors.current
    CompositionLocalProvider(LocalWerkstattColors provides colors) {
        MaterialTheme(
            colorScheme = WerkstattScheme,
            typography = WerkstattTypography,
            shapes = WerkstattShapes,
            content = content
        )
    }
}
