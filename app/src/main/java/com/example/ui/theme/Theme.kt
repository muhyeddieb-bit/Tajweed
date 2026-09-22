package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Gold400,
    onPrimary = NightBackground,
    primaryContainer = Emerald800,
    onPrimaryContainer = Gold100,
    secondary = Emerald600,
    onSecondary = TextPrimaryDark,
    secondaryContainer = NightSurfaceVariant,
    onSecondaryContainer = Gold200,
    tertiary = Gold500,
    background = NightBackground,
    onBackground = TextPrimaryDark,
    surface = NightSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = NightBorder
)

private val LightColorScheme = lightColorScheme(
    primary = NaturalForest,
    onPrimary = Color.White,
    primaryContainer = NaturalSage,
    onPrimaryContainer = NaturalForestDark,
    secondary = NaturalTerracotta,
    onSecondary = Color.White,
    secondaryContainer = NaturalSand,
    onSecondaryContainer = NaturalTerracotta,
    tertiary = NaturalForestLight,
    background = NaturalBg,
    onBackground = NaturalTextPrimary,
    surface = NaturalSurface,
    onSurface = NaturalTextPrimary,
    surfaceVariant = NaturalSageLight,
    onSurfaceVariant = NaturalTextSecondary,
    outline = NaturalBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent Islamic aesthetic by default
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
