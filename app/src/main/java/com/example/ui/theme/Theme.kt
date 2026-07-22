package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

enum class AppThemeStyle {
    TURQUOISE, // Eau (Default)
    FOREST,    // Forêt
    SUNSET,    // Crépuscule
    LAVENDER   // Sérénité
}

enum class AppThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

// 1. TURQUOISE COLOR SCHEMES
private val TurquoiseDarkColorScheme = darkColorScheme(
    primary = TurquoisePrimary,
    secondary = TurquoiseSecondary,
    tertiary = TurquoiseTertiary,
    background = TurquoiseDarkBg,
    surface = TurquoiseDarkSurface,
    surfaceVariant = TurquoiseDarkSurfaceVariant,
    onPrimary = TurquoiseOnPrimary,
    onSecondary = TurquoiseOnSecondary,
    onBackground = TurquoiseOnBackground,
    onSurface = TurquoiseOnSurface,
    onSurfaceVariant = TurquoiseOnBackground
)

private val TurquoiseLightColorScheme = lightColorScheme(
    primary = TurquoisePrimary,
    secondary = TurquoiseSecondary,
    tertiary = TurquoiseTertiary,
    background = Color(0xFFF0FBFD),
    surface = Color.White,
    surfaceVariant = Color(0xFFE0F2F1),
    onPrimary = TurquoiseOnPrimary,
    onSecondary = TurquoiseOnSecondary,
    onBackground = Color(0xFF001F24),
    onSurface = Color(0xFF001F24),
    onSurfaceVariant = Color(0xFF004D40)
)

// 2. FOREST COLOR SCHEMES
private val ForestDarkColorScheme = darkColorScheme(
    primary = Color(0xFF00E676),
    secondary = Color(0xFF00BFA5),
    tertiary = Color(0xFFB9F6CA),
    background = Color(0xFF07110C),
    surface = Color(0xFF0E1F16),
    surfaceVariant = Color(0xFF152D21),
    onPrimary = Color(0xFF00371E),
    onSecondary = Color(0xFF003B32),
    onBackground = Color(0xFFE8F5E9),
    onSurface = Color(0xFFE8F5E9),
    onSurfaceVariant = Color(0xFFE8F5E9)
)

private val ForestLightColorScheme = lightColorScheme(
    primary = Color(0xFF00C853),
    secondary = Color(0xFF009688),
    tertiary = Color(0xFF81C784),
    background = Color(0xFFF1FBF4),
    surface = Color.White,
    surfaceVariant = Color(0xFFE8F5E9),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF0A2012),
    onSurface = Color(0xFF0A2012),
    onSurfaceVariant = Color(0xFF1B5E20)
)

// 3. SUNSET COLOR SCHEMES
private val SunsetDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF7043),
    secondary = Color(0xFFFFB300),
    tertiary = Color(0xFFFFA726),
    background = Color(0xFF160F0C),
    surface = Color(0xFF231713),
    surfaceVariant = Color(0xFF30201A),
    onPrimary = Color(0xFF3E1100),
    onSecondary = Color(0xFF3E2700),
    onBackground = Color(0xFFFBE9E7),
    onSurface = Color(0xFFFBE9E7),
    onSurfaceVariant = Color(0xFFFBE9E7)
)

private val SunsetLightColorScheme = lightColorScheme(
    primary = Color(0xFFE64A19),
    secondary = Color(0xFFFFA000),
    tertiary = Color(0xFFFFB74D),
    background = Color(0xFFFFF7F2),
    surface = Color.White,
    surfaceVariant = Color(0xFFFBE9E7),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF2E0C00),
    onSurface = Color(0xFF2E0C00),
    onSurfaceVariant = Color(0xFFBF360C)
)

// 4. LAVENDER COLOR SCHEMES
private val LavenderDarkColorScheme = darkColorScheme(
    primary = Color(0xFFB388FF),
    secondary = Color(0xFF8C9EFF),
    tertiary = Color(0xFFD1C4E9),
    background = Color(0xFF110C16),
    surface = Color(0xFF1C1325),
    surfaceVariant = Color(0xFF271B34),
    onPrimary = Color(0xFF23005B),
    onSecondary = Color(0xFF00115B),
    onBackground = Color(0xFFF3E5F5),
    onSurface = Color(0xFFF3E5F5),
    onSurfaceVariant = Color(0xFFF3E5F5)
)

private val LavenderLightColorScheme = lightColorScheme(
    primary = Color(0xFF7C4DFF),
    secondary = Color(0xFF536DFE),
    tertiary = Color(0xFF9575CD),
    background = Color(0xFFF7F2FA),
    surface = Color.White,
    surfaceVariant = Color(0xFFEDE7F6),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF12002E),
    onSurface = Color(0xFF12002E),
    onSurfaceVariant = Color(0xFF4A148C)
)

@Composable
fun MyApplicationTheme(
    themeStyle: AppThemeStyle = AppThemeStyle.TURQUOISE,
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val useDarkTheme = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> darkTheme
    }

    val colorScheme = when (themeStyle) {
        AppThemeStyle.TURQUOISE -> if (useDarkTheme) TurquoiseDarkColorScheme else TurquoiseLightColorScheme
        AppThemeStyle.FOREST -> if (useDarkTheme) ForestDarkColorScheme else ForestLightColorScheme
        AppThemeStyle.SUNSET -> if (useDarkTheme) SunsetDarkColorScheme else SunsetLightColorScheme
        AppThemeStyle.LAVENDER -> if (useDarkTheme) LavenderDarkColorScheme else LavenderLightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

