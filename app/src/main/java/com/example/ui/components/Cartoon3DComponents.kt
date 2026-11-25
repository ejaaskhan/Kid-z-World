package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.assets.LocalAssetRegistry
import com.example.core.localization.AppLanguage
import com.example.core.localization.LocalAppStrings
import com.example.ui.theme.CeruleanSky
import com.example.ui.theme.EmeraldMeadow
import com.example.ui.theme.HoneyGold
import com.example.ui.theme.MangoPrimary
import com.example.ui.theme.MangoRim
import com.example.ui.theme.NightCardBlue
import com.example.ui.theme.NightSkyMid
import com.example.ui.theme.PastelHillBack
import com.example.ui.theme.PastelHillFront
import com.example.ui.theme.PastelLavenderCloud
import com.example.ui.theme.PastelPeachBottom
import com.example.ui.theme.PastelSkyMid
import com.example.ui.theme.PastelSkyTop
import com.example.ui.theme.TwilightIndigo
import com.example.ui.theme.darkenForRim
import com.example.ui.theme.lightenForGloss

/**
 * Pixar-inspired animated pastel sky world with softly drifting clouds,
 * layered storybook rolling hills, and gentle floating sparkles.
 */
@Composable
fun CartoonBackground(
    darkMode: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sky_drift")
    val cloudDrift by infiniteTransition.animateFloat(
        initialValue = -18f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloud_drift"
    )
    val twinkle by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle_twinkle"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (darkMode) {
                        listOf(TwilightIndigo, NightSkyMid, NightCardBlue)
                    } else {
                        listOf(PastelSkyTop, PastelSkyMid, PastelPeachBottom)
                    }
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            if (darkMode) {
                // Velvet twilight glow orbs + twinkling bedtime stars
                drawCircle(
                    color = Color(0xFF4759B5).copy(alpha = 0.25f),
                    radius = w * 0.42f,
                    center = Offset(w * 0.86f, h * 0.11f)
                )
                drawCircle(
                    color = Color(0xFF8C5BFF).copy(alpha = 0.16f),
                    radius = w * 0.34f,
                    center = Offset(w * 0.12f, h * 0.72f)
                )
                // Soft dark hills
                val hillBack = Path().apply {
                    moveTo(0f, h * 0.87f)
                    quadraticTo(w * 0.32f, h * 0.78f, w * 0.68f, h * 0.85f)
                    quadraticTo(w * 0.88f, h * 0.89f, w, h * 0.83f)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(hillBack, color = Color(0xFF1A2246).copy(alpha = 0.85f))

                // Twinkling stars
                val stars = listOf(
                    Offset(w * 0.14f, h * 0.10f) to 5f,
                    Offset(w * 0.36f, h * 0.07f) to 4f,
                    Offset(w * 0.65f, h * 0.13f) to 6f,
                    Offset(w * 0.84f, h * 0.18f) to 5f,
                    Offset(w * 0.22f, h * 0.26f) to 3.5f
                )
                stars.forEachIndexed { idx, (pos, r) ->
                    val alpha = if (idx % 2 == 0) twinkle else 1.25f - twinkle
                    drawCircle(color = HoneyGold.copy(alpha = alpha.coerceIn(0.2f, 0.9f)), radius = r, center = pos)
                }
            } else {
                // Warm golden sunbeam aura top-right
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFE59D).copy(alpha = 0.55f), Color.Transparent),
                        center = Offset(w * 0.88f, h * 0.08f),
                        radius = w * 0.45f
                    ),
                    radius = w * 0.45f,
                    center = Offset(w * 0.88f, h * 0.08f)
                )

                // Soft drifting puffy Pixar clouds
                fun drawPuffyCloud(cx: Float, cy: Float, scale: Float, tint: Color) {
                    drawCircle(color = tint, radius = 34f * scale, center = Offset(cx - 28f * scale, cy + 6f * scale))
                    drawCircle(color = tint, radius = 44f * scale, center = Offset(cx, cy - 6f * scale))
                    drawCircle(color = tint, radius = 32f * scale, center = Offset(cx + 30f * scale, cy + 8f * scale))
                    drawRoundRect(
                        color = tint,
                        topLeft = Offset(cx - 46f * scale, cy + 6f * scale),
                        size = Size(92f * scale, 26f * scale),
                        cornerRadius = CornerRadius(24f, 24f)
                    )
                }

                drawPuffyCloud(w * 0.18f + cloudDrift, h * 0.12f, 1.15f, Color.White.copy(alpha = 0.72f))
                drawPuffyCloud(w * 0.78f - cloudDrift * 0.8f, h * 0.20f, 0.92f, Color.White.copy(alpha = 0.62f))
                drawPuffyCloud(w * 0.52f + cloudDrift * 0.5f, h * 0.07f, 0.7f, PastelLavenderCloud.copy(alpha = 0.55f))

                // Rolling pastel storybook hills along the bottom
                val backHill = Path().apply {
                    moveTo(0f, h * 0.88f)
                    quadraticTo(w * 0.28f, h * 0.79f, w * 0.62f, h * 0.86f)
                    quadraticTo(w * 0.85f, h * 0.91f, w, h * 0.82f)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(backHill, color = PastelHillBack.copy(alpha = 0.65f))

                val frontHill = Path().apply {
                    moveTo(0f, h * 0.93f)
                    quadraticTo(w * 0.35f, h * 0.87f, w * 0.72f, h * 0.92f)
                    quadraticTo(w * 0.88f, h * 0.95f, w, h * 0.89f)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(frontHill, color = PastelHillFront.copy(alpha = 0.58f))

                // Soft pastel sun sparkles
                val sparkles = listOf(
                    Offset(w * 0.12f, h * 0.25f),
                    Offset(w * 0.88f, h * 0.34f),
                    Offset(w * 0.50f, h * 0.18f)
                )
                sparkles.forEachIndexed { idx, pos ->
                    val alpha = if (idx % 2 == 0) twinkle * 0.45f else (1f - twinkle) * 0.45f
                    drawCircle(color = HoneyGold.copy(alpha = alpha.coerceIn(0.15f, 0.5f)), radius = 6f, center = pos)
                }
            }
        }
        content()
    }
}

/**
 * Glossy 3D Claymorphic Card with soft ambient shadow, vertical clay pillow gradient,
 * inner top specular sheen, sculpted bottom 3D bevel, and bouncy spring-squish press feedback.
 */
@Composable
fun Cartoon3DCard(
    containerColor: Color,
    modifier: Modifier = Modifier,
    rimColor: Color = containerColor.darkenForRim(0.76f),
    cornerRadius: Dp = 26.dp,
    bevelDepth: Dp = 6.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val safeBevel = bevelDepth.coerceAtLeast(0.dp)
    val animatedTopOffset by animateDpAsState(
        targetValue = if (isPressed && onClick != null) (safeBevel - 2.dp).coerceAtLeast(0.dp) else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_press_offset"
    )
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.975f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_press_scale"
    )
    val shape = RoundedCornerShape(cornerRadius)
    val glossTop = containerColor.lightenForGloss(0.16f)

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onClick()
                        }
                    )
                } else {
                    Modifier
                }
            )
    ) {
        // 3D Bottom Bevel Base + Soft Colored Ambient Drop Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = safeBevel)
                .shadow(
                    elevation = if (safeBevel > 3.dp) 10.dp else 5.dp,
                    shape = shape,
                    ambientColor = rimColor.copy(alpha = 0.45f),
                    spotColor = rimColor.copy(alpha = 0.55f)
                )
                .clip(shape)
                .background(rimColor)
        )

        // Elevated Glossy Clay Face
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = safeBevel)
                .graphicsLayer { translationY = animatedTopOffset.toPx() }
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(glossTop, containerColor)
                    )
                )
                .border(
                    width = 1.8.dp,
                    color = Color.White.copy(alpha = 0.52f),
                    shape = shape
                )
        ) {
            // Soft curved clay top sheen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.34f),
                                Color.Transparent
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding),
                content = content
            )
        }
    }
}

/**
 * Chunky Glossy 3D Jelly Pill Button with smooth gradient fill, specular shine,
 * soft ambient shadow, and spring press physics.
 */
@Composable
fun Cartoon3DButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MangoPrimary,
    rimColor: Color = containerColor.darkenForRim(0.72f),
    contentColor: Color = Color.White,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val bevel = 5.dp
    val pressOffset by animateDpAsState(
        targetValue = if (isPressed && enabled) 3.dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "btn_offset"
    )
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "btn_scale"
    )
    val shape = RoundedCornerShape(22.dp)
    val effectiveFace = if (enabled) containerColor else containerColor.copy(alpha = 0.45f)
    val effectiveRim = if (enabled) rimColor else rimColor.copy(alpha = 0.35f)
    val effectiveGloss = if (enabled) containerColor.lightenForGloss(0.24f) else effectiveFace

    Box(
        modifier = modifier
            .heightIn(min = 54.dp)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                }
            )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = bevel)
                .shadow(
                    elevation = if (enabled) 8.dp else 2.dp,
                    shape = shape,
                    ambientColor = effectiveRim.copy(alpha = 0.45f),
                    spotColor = effectiveRim.copy(alpha = 0.55f)
                )
                .clip(shape)
                .background(effectiveRim)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 49.dp)
                .padding(bottom = bevel)
                .graphicsLayer { translationY = pressOffset.toPx() }
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(effectiveGloss, effectiveFace)
                    )
                )
                .border(1.8.dp, Color.White.copy(alpha = 0.48f), shape)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Chunky circular 3D icon button (Back arrows, audio circles, prev/next arrows).
 */
@Composable
fun Cartoon3DCircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = CeruleanSky,
    rimColor: Color = containerColor.darkenForRim(0.72f),
    iconTint: Color = Color.White,
    size: Dp = 50.dp,
    enabled: Boolean = true
) {
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressOffset by animateDpAsState(
        targetValue = if (isPressed && enabled) 3.dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "circle_btn_offset"
    )
    val bevel = 4.dp
    val face = if (enabled) containerColor else containerColor.copy(alpha = 0.4f)
    val rim = if (enabled) rimColor else rimColor.copy(alpha = 0.3f)

    Box(
        modifier = modifier
            .size(size)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                }
            )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = bevel)
                .shadow(6.dp, CircleShape, ambientColor = rim.copy(alpha = 0.4f), spotColor = rim.copy(alpha = 0.5f))
                .clip(CircleShape)
                .background(rim)
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(bottom = bevel)
                .graphicsLayer { translationY = pressOffset.toPx() }
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(face.lightenForGloss(0.26f), face)
                    )
                )
                .border(1.8.dp, Color.White.copy(alpha = 0.55f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconTint,
                modifier = Modifier.size(size * 0.50f)
            )
        }
    }
}

@Composable
fun LanguageTogglePill(
    language: AppLanguage,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier = modifier
            .testTag("language_toggle_pill")
            .heightIn(min = 48.dp)
            .shadow(6.dp, shape, ambientColor = CeruleanSky.copy(alpha = 0.25f), spotColor = CeruleanSky.copy(alpha = 0.3f))
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(2.dp, CeruleanSky.copy(alpha = 0.45f), shape)
            .clickable(onClick = onToggle)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(AppLanguage.ENGLISH, AppLanguage.BANGLA).forEach { lang ->
            val selected = lang == language
            Box(
                modifier = Modifier
                    .clip(shape)
                    .background(
                        if (selected) {
                            Brush.verticalGradient(listOf(CeruleanSky.lightenForGloss(0.2f), CeruleanSky))
                        } else {
                            Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                        }
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = lang.shortLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Glossy 3D Clay Mascot Character Medallion dynamically styled from the active child's avatar.
 */
@Composable
fun CartoonAvatarBadge(
    avatarKey: String,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    val asset = LocalAssetRegistry.avatarByKey(avatarKey)
    Box(
        modifier = modifier
            .size(size)
            .shadow(6.dp, CircleShape, ambientColor = asset.rimColor.copy(alpha = 0.4f), spotColor = asset.rimColor.copy(alpha = 0.5f))
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        asset.primaryColor.lightenForGloss(0.32f),
                        asset.primaryColor,
                        asset.rimColor
                    )
                )
            )
            .border(3.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Glossy top clay highlight oval
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = size * 0.08f)
                .size(width = size * 0.56f, height = size * 0.22f)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.34f))
        )
        Text(
            text = asset.emoji,
            fontSize = (size.value * 0.54f).sp
        )
    }
}

/**
 * Animated friendly 3D Mascot greeting banner showing the active child's mascot + pastel speech bubble.
 */
@Composable
fun CartoonMascotBanner(
    avatarKey: String,
    title: String,
    speechMessage: String,
    accentColor: Color = CeruleanSky,
    modifier: Modifier = Modifier
) {
    Cartoon3DCard(
        containerColor = MaterialTheme.colorScheme.surface,
        rimColor = accentColor.copy(alpha = 0.35f),
        cornerRadius = 26.dp,
        bevelDepth = 5.dp,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Floating3DBox(floatAmplitudePx = 5f, durationMillis = 1900) {
                CartoonAvatarBadge(avatarKey = avatarKey, size = 58.dp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = accentColor,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp))
                        .background(accentColor.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = speechMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

data class FloatingNavItemData(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val accent: Color
)

/**
 * Floating Glossy 3D Pill Bottom Navigation Dock respecting WindowInsets.navigationBars.
 */
@Composable
fun Floating3DPillNavBar(
    items: List<FloatingNavItemData>,
    isSelected: (String) -> Boolean,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(30.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // 3D Sculpted Pill Dock Base
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = shape,
                    ambientColor = CeruleanSky.copy(alpha = 0.25f),
                    spotColor = Color.Black.copy(alpha = 0.2f)
                )
                .clip(shape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.96f))
                .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.65f), shape)
                .padding(horizontal = 8.dp, vertical = 7.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val selected = isSelected(item.route)
                    val liftOffset by animateDpAsState(
                        targetValue = if (selected) (-2).dp else 0.dp,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "nav_lift_${item.route}"
                    )
                    val itemScale by animateFloatAsState(
                        targetValue = if (selected) 1.05f else 0.96f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "nav_scale_${item.route}"
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 52.dp)
                            .graphicsLayer {
                                translationY = liftOffset.toPx()
                                scaleX = itemScale
                                scaleY = itemScale
                            }
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                if (selected) {
                                    Brush.verticalGradient(
                                        listOf(item.accent.lightenForGloss(0.2f), item.accent)
                                    )
                                } else {
                                    Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                                }
                            )
                            .clickable { onSelect(item.route) }
                            .padding(vertical = 6.dp, horizontal = 4.dp)
                            .testTag("nav_bar_${item.route}")
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(23.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartoonProgressBar(
    progress: Float,
    fillColor: Color,
    modifier: Modifier = Modifier,
    trackColor: Color = Color.White.copy(alpha = 0.35f),
    height: Dp = 12.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "progress_bar"
    )
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(trackColor)
    ) {
        if (animatedProgress > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.coerceAtLeast(0.06f))
                    .height(height)
                    .clip(shape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(fillColor.lightenForGloss(0.2f), fillColor)
                        )
                    )
            )
        }
    }
}

@Composable
fun StarBurstCelebration(
    message: String?,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "star_burst")
    val starPulse by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.16f,
        animationSpec = infiniteRepeatable(
            animation = tween(420, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_pulse"
    )

    AnimatedVisibility(
        visible = message != null,
        enter = fadeIn() + scaleIn(initialScale = 0.65f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
        exit = fadeOut() + scaleOut(targetScale = 0.85f),
        modifier = modifier
    ) {
        if (message != null) {
            Cartoon3DCard(
                containerColor = HoneyGold,
                rimColor = MangoRim,
                cornerRadius = 30.dp,
                bevelDepth = 6.dp,
                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 14.dp),
                modifier = Modifier.testTag("celebration_banner")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(28.dp)
                            .graphicsLayer {
                                scaleX = starPulse
                                scaleY = starPulse
                            }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(28.dp)
                            .graphicsLayer {
                                scaleX = starPulse
                                scaleY = starPulse
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun KidzLoadingState(modifier: Modifier = Modifier) {
    val strings = LocalAppStrings.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
            .testTag("loading_state"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Cartoon3DCard(
            containerColor = MaterialTheme.colorScheme.surface,
            rimColor = MaterialTheme.colorScheme.outline,
            cornerRadius = 32.dp,
            contentPadding = PaddingValues(28.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "🌟", fontSize = 52.sp)
                Spacer(modifier = Modifier.height(14.dp))
                CircularProgressIndicator(
                    color = MangoPrimary,
                    strokeWidth = 5.dp,
                    modifier = Modifier.size(42.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = strings.loadingWorld,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun KidzErrorBanner(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("error_state"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Cartoon3DCard(
            containerColor = MaterialTheme.colorScheme.surface,
            rimColor = MaterialTheme.colorScheme.outline,
            cornerRadius = 28.dp,
            contentPadding = PaddingValues(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Rounded.ErrorOutline,
                    contentDescription = null,
                    tint = Color(0xFFFF4D5A),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(18.dp))
                Cartoon3DButton(
                    text = strings.retryButton,
                    icon = Icons.Rounded.Refresh,
                    onClick = onRetry,
                    modifier = Modifier.testTag("retry_button")
                )
            }
        }
    }
}

@Composable
fun KidzEmptyState(
    title: String,
    subtitle: String,
    emoji: String = "🌈",
    modifier: Modifier = Modifier
) {
    Cartoon3DCard(
        containerColor = MaterialTheme.colorScheme.surface,
        rimColor = MaterialTheme.colorScheme.outline,
        cornerRadius = 26.dp,
        contentPadding = PaddingValues(24.dp),
        modifier = modifier.testTag("empty_state")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = emoji, fontSize = 44.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun Floating3DBox(
    modifier: Modifier = Modifier,
    floatAmplitudePx: Float = 12f,
    durationMillis: Int = 2200,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating_3d")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -floatAmplitudePx,
        targetValue = floatAmplitudePx,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_y"
    )
    Box(
        modifier = modifier.graphicsLayer { translationY = offsetY },
        content = content
    )
}
