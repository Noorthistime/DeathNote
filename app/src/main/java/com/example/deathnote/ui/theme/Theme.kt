package com.example.deathnote.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

object DeathNoteThemeManager {
    var currentAccentColor by mutableStateOf(ThemeOrange)
}

val LocalAccentColor = staticCompositionLocalOf { ThemeOrange }

private val DarkColorScheme = darkColorScheme(
    primary = NoirPrimary,
    onPrimary = NoirOnPrimary,
    background = NoirBackground,
    surface = NoirSurface,
    onBackground = NoirTextPrimary,
    onSurface = NoirTextPrimary,
    secondary = NoirTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    background = LightBackground,
    surface = LightSurface,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    secondary = LightTextSecondary
)

@Composable
fun DeathNoteTheme(
    darkTheme: Boolean = true, // Force dark theme for Monolith Noir aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val accentColor = DeathNoteThemeManager.currentAccentColor
    val colorScheme = darkColorScheme(
        primary = accentColor,
        onPrimary = NoirOnPrimary,
        background = NoirBackground,
        surface = NoirSurface,
        onBackground = NoirTextPrimary,
        onSurface = NoirTextPrimary,
        secondary = NoirTextSecondary
    )
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Black.toArgb()
            window.navigationBarColor = Color.Black.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = {
            androidx.compose.runtime.CompositionLocalProvider(
                LocalAccentColor provides accentColor
            ) {
                Surface(color = Color.Black) {
                    content()
                }
            }
        }
    )
}
