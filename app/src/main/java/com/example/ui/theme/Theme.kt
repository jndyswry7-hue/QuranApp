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
    primary = EmeraldLight,
    onPrimary = Color(0xFF003825),
    primaryContainer = EmeraldDark,
    onPrimaryContainer = EmeraldContainer,
    secondary = GoldAccent,
    onSecondary = Color(0xFF452200),
    secondaryContainer = GoldDark,
    onSecondaryContainer = GoldLight,
    tertiary = EmeraldMedium,
    background = NightDeepBackground,
    surface = NightSurface,
    surfaceVariant = NightSurfaceVariant,
    onBackground = NightTextPrimary,
    onSurface = NightTextPrimary,
    onSurfaceVariant = NightTextSecondary,
    outline = NightCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = OnEmeraldContainer,
    secondary = GoldDark,
    onSecondary = Color.White,
    secondaryContainer = GoldContainer,
    onSecondaryContainer = OnGoldContainer,
    tertiary = EmeraldMedium,
    background = WarmCream,
    surface = SoftSurface,
    surfaceVariant = Color(0xFFF0EBE1),
    onBackground = Color(0xFF191C1E),
    onSurface = Color(0xFF191C1E),
    onSurfaceVariant = Color(0xFF4A453E),
    outline = OutlineWarm
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // disabled by default to preserve the spiritual emerald-gold identity
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
