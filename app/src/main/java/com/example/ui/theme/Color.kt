package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Pixar Pastel Sky & Storybook Light Palette
val PastelSkyTop = Color(0xFFC5EBFF)
val PastelSkyMid = Color(0xFFEFF8FF)
val PastelPeachBottom = Color(0xFFFFF1DC)
val PastelHillBack = Color(0xFFD6F4E5)
val PastelHillFront = Color(0xFFBDF0D8)
val PastelLavenderCloud = Color(0xFFEAE3FF)

val SkyCream = Color(0xFFFFF8EE)
val SoftCloudWhite = Color(0xFFFFFDF9)
val WarmButterSurface = Color(0xFFFFF1D6)
val DeepCocoaInk = Color(0xFF23203A)
val MutedPlumInk = Color(0xFF5D597A)

// Cozy Bedtime Dark Palette
val TwilightIndigo = Color(0xFF141936)
val NightSkyMid = Color(0xFF1E2652)
val NightCardBlue = Color(0xFF252E5B)
val NightCardElevated = Color(0xFF313C70)
val StarlightWhite = Color(0xFFF7F8FF)
val MoonlitLavender = Color(0xFFB8C0E6)

// Vibrant 3D Clay Module Accents & Sculpted Rims
val MangoPrimary = Color(0xFFFF8A1F)
val MangoRim = Color(0xFFD45E00)

val CeruleanSky = Color(0xFF2CB5FF)
val CeruleanRim = Color(0xFF0682C9)

val EmeraldMeadow = Color(0xFF26CF7A)
val EmeraldRim = Color(0xFF0E9852)

val BerryCoral = Color(0xFFFF5E8D)
val BerryRim = Color(0xFFC92B5B)

val GrapePop = Color(0xFF8C5BFF)
val GrapeRim = Color(0xFF5E2ED1)

val HoneyGold = Color(0xFFFFB800)
val HoneyRim = Color(0xFFCA8600)

val SoftMintBg = Color(0xFFE4FFF0)
val SoftSkyBg = Color(0xFFE3F5FF)
val SoftCoralBg = Color(0xFFFFE8F0)
val SoftGrapeBg = Color(0xFFF0E9FF)
val SoftGoldBg = Color(0xFFFFF5D4)

fun Color.darkenForRim(factor: Float = 0.72f): Color = Color(
    red = (red * factor).coerceIn(0f, 1f),
    green = (green * factor).coerceIn(0f, 1f),
    blue = (blue * factor).coerceIn(0f, 1f),
    alpha = alpha
)

fun Color.lightenForGloss(amount: Float = 0.22f): Color = Color(
    red = (red + (1f - red) * amount).coerceIn(0f, 1f),
    green = (green + (1f - green) * amount).coerceIn(0f, 1f),
    blue = (blue + (1f - blue) * amount).coerceIn(0f, 1f),
    alpha = alpha
)
