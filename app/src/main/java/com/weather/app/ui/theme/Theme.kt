package com.weather.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color.LightGray,
    primaryContainer = ActiveCardDark,
    secondaryContainer = CardDark,
    tertiaryContainer = TopSectionDark,
    background = BackgroundDark,
    onBackground = Color.White,
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = Color.Black,
    primaryContainer = ActiveCardLight,
    secondaryContainer = CardLight,
    tertiaryContainer = TopSectionLight,
    background = BackgroundLight,
    onBackground = Color.Black,
    outline = OutlineLight
)

@Composable
fun WeatherAppTheme(
    darkTheme: Boolean? = null,
    content: @Composable () -> Unit,
) {
    val isDarkTheme = darkTheme ?: isSystemInDarkTheme()

    val colorScheme = when {
        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}