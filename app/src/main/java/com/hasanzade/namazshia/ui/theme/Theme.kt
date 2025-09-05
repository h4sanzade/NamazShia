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

val IslamicGold = Color(0xFFD4AF37)
val IslamicGoldLight = Color(0xFFE6C547)
val IslamicGoldDark = Color(0xFFB8941F)
val IslamicGreen = Color(0xFF2E7D32)
val IslamicGreenLight = Color(0xFF4CAF50)
val IslamicGreenDark = Color(0xFF1B5E20)
val IslamicTeal = Color(0xFF006064)
val IslamicBlue = Color(0xFF1565C0)
val IslamicMaroon = Color(0xFF7B1FA2)

val DeepNight = Color(0xFF0A0E1A)
val MidNight = Color(0xFF1A1F2E)
val SoftNight = Color(0xFF2A2F3E)
val LightNight = Color(0xFF3A3F4E)

// Light Theme Colors (for day mode)
val LightCream = Color(0xFFF8F6F0)         // Warm cream background
val WarmWhite = Color(0xFFFFFDF7)          // Warm white
val SoftGray = Color(0xFFE8E6E0)           // Soft gray for cards
val MediumGray = Color(0xFF6B6B6B)         // Medium gray for text

private val DarkColorScheme = darkColorScheme(
    primary = IslamicGold,
    onPrimary = Color.White,
    primaryContainer = IslamicGreenDark,
    onPrimaryContainer = IslamicGold,

    secondary = IslamicGreen,
    onSecondary = Color.White,
    secondaryContainer = IslamicTeal,
    onSecondaryContainer = Color.White,

    tertiary = IslamicGoldLight,
    onTertiary = Color.Black,
    tertiaryContainer = IslamicMaroon,
    onTertiaryContainer = Color.White,

    background = DeepNight,
    onBackground = Color.White,
    surface = MidNight,
    onSurface = Color.White,
    surfaceVariant = SoftNight,
    onSurfaceVariant = Color(0xFFE8E8E8),

    surfaceTint = IslamicGold,
    inverseSurface = Color.White,
    inverseOnSurface = DeepNight,

    outline = Color(0xFF4A4A4A),
    outlineVariant = Color(0xFF2A2A2A),

    error = Color(0xFFE57373),
    onError = Color.White,
    errorContainer = Color(0xFFB71C1C),
    onErrorContainer = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = IslamicGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8F5E8),
    onPrimaryContainer = IslamicGreenDark,

    secondary = IslamicGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF8E1),
    onSecondaryContainer = IslamicGoldDark,

    tertiary = IslamicTeal,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0F2F1),
    onTertiaryContainer = IslamicTeal,

    background = LightCream,
    onBackground = Color(0xFF1A1A1A),
    surface = WarmWhite,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = SoftGray,
    onSurfaceVariant = Color(0xFF424242),

    surfaceTint = IslamicGreen,
    inverseSurface = Color(0xFF2F2F2F),
    inverseOnSurface = Color.White,

    outline = Color(0xFFBDBDBD),
    outlineVariant = Color(0xFFE0E0E0),

    error = Color(0xFFB00020),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

@Composable
fun NamazShiaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep false to maintain Islamic theme
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
        typography = IslamicTypography,
        content = content
    )
}