package com.luigi.lifeos.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Root theme wrapper. Defaults to Material You dynamic colour on Android 12+,
 * falls back to the hand-tuned brand palette below.
 *
 * Pass `dynamicColor = false` from previews / tests for stable colours.
 */
@Composable
fun LifeOsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LifeOsTypography,
        content = content,
    )
}

private val LightColorScheme = lightColorScheme(
    primary = Teal40,
    onPrimary = Neutral99,
    primaryContainer = Teal80,
    onPrimaryContainer = Teal20,
    secondary = Coral40,
    onSecondary = Neutral99,
    secondaryContainer = Coral80,
    onSecondaryContainer = Coral20,
    tertiary = Amber40,
    onTertiary = Neutral99,
    tertiaryContainer = Amber80,
    onTertiaryContainer = Amber20,
    background = Neutral99,
    onBackground = Neutral10,
    surface = Neutral99,
    onSurface = Neutral10,
    surfaceVariant = Neutral95,
    error = ErrorRed,
)

private val DarkColorScheme = darkColorScheme(
    primary = Teal80,
    onPrimary = Teal20,
    primaryContainer = Teal40,
    onPrimaryContainer = Teal80,
    secondary = Coral80,
    onSecondary = Coral20,
    secondaryContainer = Coral40,
    onSecondaryContainer = Coral80,
    tertiary = Amber80,
    onTertiary = Amber20,
    background = Neutral10,
    onBackground = Neutral99,
    surface = Neutral10,
    onSurface = Neutral99,
    surfaceVariant = Neutral20,
    error = ErrorRed,
)
