package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OraxisColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = TextDark,
    primaryContainer = CyanContainer,
    onPrimaryContainer = Color(0xFFA5F3FC),
    secondary = AmberAccent,
    onSecondary = TextDark,
    secondaryContainer = AmberContainer,
    onSecondaryContainer = Color(0xFFFDE68A),
    tertiary = EmeraldAccent,
    onTertiary = TextDark,
    tertiaryContainer = EmeraldContainer,
    onTertiaryContainer = Color(0xFFA7F3D0),
    background = ObsidianBg,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = ObsidianSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = ObsidianBorder,
    outlineVariant = ObsidianBorderGlow,
    error = CrimsonAccent,
    onError = TextDark,
    errorContainer = CrimsonContainer,
    onErrorContainer = Color(0xFFFECACA)
)

@Composable
fun OraxisPhysicTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = OraxisColorScheme,
        typography = Typography,
        content = content
    )
}
