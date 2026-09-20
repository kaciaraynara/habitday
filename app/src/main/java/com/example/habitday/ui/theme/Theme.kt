package com.example.habitday.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.habitday.data.local.UserPreferencesEntity

private val DarkColorScheme = darkColorScheme(
    primary = BrandBlue,
    secondary = BrandYellow,
    tertiary = SuccessGreen,
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onPrimary = BackgroundWhite,
    onBackground = BackgroundWhite,
    onSurface = BackgroundWhite
)

private val LightColorScheme = lightColorScheme(
    primary = BrandBlue,
    secondary = BrandYellow,
    tertiary = SuccessGreen,
    background = BackgroundWhite,
    surface = BackgroundWhite,
    onPrimary = BackgroundWhite,
    onSecondary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = BorderBlue
)

val HabitDayShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun HabitdayTheme(
    preferences: UserPreferencesEntity? = null,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val darkTheme = when (preferences?.themeMode) {
        "CLARO" -> false
        "ESCURO" -> true
        else -> isSystemDark
    }

    val highlightColor = preferences?.highlightColor?.let { 
        try { Color(android.graphics.Color.parseColor(it)) } catch (e: Exception) { BrandBlue }
    } ?: BrandBlue

    val backgroundColor = preferences?.backgroundColor?.let {
        try { Color(android.graphics.Color.parseColor(it)) } catch (e: Exception) { BackgroundWhite }
    } ?: BackgroundWhite

    val colorScheme = if (darkTheme) {
        DarkColorScheme.copy(primary = highlightColor)
    } else {
        LightColorScheme.copy(
            primary = highlightColor,
            background = backgroundColor,
            surface = backgroundColor
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = HabitDayShapes,
        content = content
    )
}
