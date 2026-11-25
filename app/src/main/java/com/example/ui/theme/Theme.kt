package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.example.core.localization.AppLanguage
import com.example.core.localization.LocalAppStrings
import com.example.core.localization.stringsFor

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
    background = TwilightIndigo,
    onBackground = StarlightWhite,
    surface = NightCardBlue,
    onSurface = StarlightWhite,
    surfaceVariant = NightCardElevated,
    onSurfaceVariant = MoonlitLavender,
    outline = Color(0xFF475285)
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
    background = SkyCream,
    onBackground = DeepCocoaInk,
    surface = SoftCloudWhite,
    onSurface = DeepCocoaInk,
    surfaceVariant = WarmButterSurface,
    onSurfaceVariant = MutedPlumInk,
    outline = Color(0xFFE7DAC2)
)

@Composable
fun KidzWorldTheme(
    darkTheme: Boolean = false,
    language: AppLanguage = AppLanguage.BANGLA,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) KidzDarkColorScheme else KidzLightColorScheme
    val typography = kidzTypographyFor(language)
    val strings = stringsFor(language)

    CompositionLocalProvider(LocalAppStrings provides strings) {
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
