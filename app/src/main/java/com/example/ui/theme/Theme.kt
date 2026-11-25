package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.core.localization.AppLanguage
import com.example.core.localization.LocalAppStrings
import com.example.core.localization.stringsFor
import com.example.domain.model.ThemeMode

private val KidzDarkColorScheme = darkColorScheme(
    primary = MangoPrimary,
    onPrimary = Color.White,
    primaryContainer = NightCardElevated,
    onPrimaryContainer = StarlightWhite,
    secondary = CeruleanSky,
    onSecondary = Color.White,
    secondaryContainer = NightCardBlue,
    onSecondaryContainer = StarlightWhite,
    tertiary = EmeraldMeadow,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF1F4248),
    onTertiaryContainer = StarlightWhite,
    background = TwilightIndigo,
    onBackground = StarlightWhite,
    surface = NightCardBlue,
    onSurface = StarlightWhite,
    surfaceVariant = NightCardElevated,
    onSurfaceVariant = MoonlitLavender,
    surfaceContainerLowest = Color(0xFF10142C),
    surfaceContainerLow = Color(0xFF1A2044),
    surfaceContainer = NightCardBlue,
    surfaceContainerHigh = NightCardElevated,
    surfaceContainerHighest = Color(0xFF3C4987),
    outline = Color(0xFF4F5D99),
    outlineVariant = Color(0xFF364173),
    error = Color(0xFFFF6E7F),
    onError = Color.White
)

private val KidzLightColorScheme = lightColorScheme(
    primary = MangoPrimary,
    onPrimary = Color.White,
    primaryContainer = WarmButterSurface,
    onPrimaryContainer = DeepCocoaInk,
    secondary = CeruleanSky,
    onSecondary = Color.White,
    secondaryContainer = SoftSkyBg,
    onSecondaryContainer = DeepCocoaInk,
    tertiary = EmeraldMeadow,
    onTertiary = Color.White,
    tertiaryContainer = SoftMintBg,
    onTertiaryContainer = DeepCocoaInk,
    background = SkyCream,
    onBackground = DeepCocoaInk,
    surface = SoftCloudWhite,
    onSurface = DeepCocoaInk,
    surfaceVariant = WarmButterSurface,
    onSurfaceVariant = MutedPlumInk,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = SoftCloudWhite,
    surfaceContainer = WarmButterSurface,
    surfaceContainerHigh = Color(0xFFFFE9C4),
    surfaceContainerHighest = Color(0xFFFCE1B2),
    outline = Color(0xFFE7DAC2),
    outlineVariant = Color(0xFFF0E4CE),
    error = Color(0xFFE53955),
    onError = Color.White
)

val LocalIsDarkTheme = staticCompositionLocalOf { false }

@Composable
fun KidzWorldTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    language: AppLanguage = AppLanguage.BANGLA,
    content: @Composable () -> Unit
) {
    val isDark = themeMode.resolveDarkTheme(isSystemInDarkTheme())
    KidzWorldTheme(
        darkTheme = isDark,
        language = language,
        content = content
    )
}

@Composable
fun KidzWorldTheme(
    darkTheme: Boolean,
    language: AppLanguage = AppLanguage.BANGLA,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) KidzDarkColorScheme else KidzLightColorScheme
    val typography = kidzTypographyFor(language)
    val strings = stringsFor(language)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalAppStrings provides strings,
        LocalIsDarkTheme provides darkTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    KidzWorldTheme(darkTheme = darkTheme, content = content)
}
