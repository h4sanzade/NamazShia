package com.hasanzade.namazshia.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Islamic Green Color Palette
val IslamicGreen = Color(0xFF2E7D32)        // Primary green
val IslamicGreenLight = Color(0xFF4CAF50)   // Light green
val IslamicGreenDark = Color(0xFF1B5E20)    // Dark green
val IslamicGold = Color(0xFFFFD700)         // Gold accent
val DarkBackground = Color(0xFF0F1419)      // Very dark background
val DarkSurface = Color(0xFF1A1A1A)         // Dark surface
val DarkCard = Color(0xFF2D2D2D)            // Dark card background

private val DarkColorScheme = darkColorScheme(
    primary = IslamicGreenLight,
    onPrimary = Color.White,
    primaryContainer = IslamicGreenDark,
    onPrimaryContainer = Color.White,

    secondary = IslamicGold,
    onSecondary = Color.Black,
    secondaryContainer = IslamicGreen,
    onSecondaryContainer = Color.White,

    tertiary = Color(0xFF81C784),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF2E5D2E),
    onTertiaryContainer = Color.White,

    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = DarkCard,
    onSurfaceVariant = Color(0xFFE0E0E0),

    error = Color(0xFFCF6679),
    onError = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = IslamicGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = IslamicGreenDark,

    secondary = IslamicGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFF1F8E9),
    onSecondaryContainer = IslamicGreen,

    tertiary = Color(0xFF66BB6A),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE8F5E8),
    onTertiaryContainer = IslamicGreenDark,

    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF1A1A1A),
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF8F8F8),
    onSurfaceVariant = Color(0xFF424242),

    error = Color(0xFFB00020),
    onError = Color.White
)

@Composable
fun NamazShiaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic color to use our Islamic theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}