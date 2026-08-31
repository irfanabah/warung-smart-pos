package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemePreset(val id: String, val title: String, val primaryColor: Color, val emoji: String) {
    AMBER("amber", "Emas Amber", Amber500, "🌟"),
    EMERALD("emerald", "Hijau Emerald", Emerald500, "🌿"),
    INDIGO("indigo", "Ungu Indigo", Indigo500, "🔮"),
    CYAN("cyan", "Biru Cyan", Cyan500, "⚡")
}

@Composable
fun WarungSmartTheme(
    selectedTheme: AppThemePreset = AppThemePreset.AMBER,
    content: @Composable () -> Unit
) {
    val primary = selectedTheme.primaryColor

    val darkScheme = darkColorScheme(
        primary = primary,
        onPrimary = Slate950,
        primaryContainer = primary.copy(alpha = 0.2f),
        onPrimaryContainer = primary,
        secondary = Slate400,
        onSecondary = Color.White,
        secondaryContainer = Slate800,
        onSecondaryContainer = Slate100,
        background = Slate900,
        onBackground = Slate100,
        surface = Slate950,
        onSurface = Slate100,
        surfaceVariant = Slate800,
        onSurfaceVariant = Slate300,
        outline = Slate700,
        outlineVariant = Slate800,
        error = ErrorRed,
        onError = Color.White
    )

    MaterialTheme(
        colorScheme = darkScheme,
        typography = Typography,
        content = content
    )
}
