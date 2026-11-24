package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.assets.LocalAssetRegistry
import com.example.core.localization.LocalAppStrings
import com.example.ui.components.Cartoon3DCard
import com.example.ui.components.Floating3DBox
import com.example.ui.theme.BerryCoral
import com.example.ui.theme.CeruleanRim
import com.example.ui.theme.CeruleanSky
import com.example.ui.theme.EmeraldMeadow
import com.example.ui.theme.GrapePop
import com.example.ui.theme.HoneyGold
import com.example.ui.theme.MangoPrimary
import com.example.ui.theme.MangoRim
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    isDataReady: Boolean,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val logoScale = remember { Animatable(0.45f) }
    val textAlpha = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "splash_aura")
    val haloRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing)
        ),
        label = "halo_rot"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(Unit) {
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        delay(240L)
        textAlpha.animateTo(1f, animationSpec = tween(520))
    }

    LaunchedEffect(isDataReady) {
        if (isDataReady) {
            delay(1850L)
            onFinished()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1AA7FF),
                        CeruleanSky,
                        Color(0xFF7B61FF)
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { if (isDataReady) onFinished() }
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .testTag("splash_screen")
    ) {
        // Pixar Pastel Clouds, Sunbeam Glow & Rolling Hills Atmosphere
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drawCircle(
                color = Color.White.copy(alpha = 0.16f),
                radius = w * 0.42f,
                center = Offset(w * 0.15f, h * 0.14f)
            )
            drawCircle(
                color = HoneyGold.copy(alpha = 0.24f),
                radius = w * 0.34f,
                center = Offset(w * 0.88f, h * 0.24f)
            )

            // Puffy drifting Pixar clouds
            fun drawSplashCloud(cx: Float, cy: Float, sc: Float, tint: Color) {
                drawCircle(tint, 28f * sc, Offset(cx - 24f * sc, cy + 6f * sc))
                drawCircle(tint, 38f * sc, Offset(cx, cy - 6f * sc))
                drawCircle(tint, 26f * sc, Offset(cx + 26f * sc, cy + 8f * sc))
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(cx - 38f * sc, cy + 6f * sc),
                    size = Size(76f * sc, 22f * sc),
                    cornerRadius = CornerRadius(20f, 20f)
                )
            }
            drawSplashCloud(w * 0.18f, h * 0.16f, 1.1f, Color.White.copy(alpha = 0.28f))
            drawSplashCloud(w * 0.82f, h * 0.22f, 0.9f, Color.White.copy(alpha = 0.22f))

            // Soft layered storybook hills at bottom
            val hillBack = Path().apply {
                moveTo(0f, h * 0.84f)
                quadraticTo(w * 0.32f, h * 0.74f, w * 0.68f, h * 0.82f)
                quadraticTo(w * 0.88f, h * 0.86f, w, h * 0.78f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(hillBack, Color.White.copy(alpha = 0.14f))

            val hillFront = Path().apply {
                moveTo(0f, h * 0.91f)
                quadraticTo(w * 0.36f, h * 0.84f, w * 0.76f, h * 0.90f)
                quadraticTo(w * 0.92f, h * 0.93f, w, h * 0.87f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(hillFront, Color.White.copy(alpha = 0.18f))
        }

        // Center Brand Showcase
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(216.dp)
            ) {
                // Rotating glossy star-ring aura
                Box(
                    modifier = Modifier
                        .size(204.dp)
                        .scale(pulse)
                        .rotate(haloRotation)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                colors = listOf(
                                    HoneyGold.copy(alpha = 0.55f),
                                    Color.White.copy(alpha = 0.25f),
                                    EmeraldMeadow.copy(alpha = 0.45f),
                                    BerryCoral.copy(alpha = 0.45f),
                                    HoneyGold.copy(alpha = 0.55f)
                                )
                            )
                        )
                )

                Floating3DBox(floatAmplitudePx = 9f, durationMillis = 1900) {
                    Box(
                        modifier = Modifier
                            .size(168.dp)
                            .scale(logoScale.value)
                            .clip(CircleShape)
                            .background(CeruleanSky)
                            .border(6.dp, Color.White, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = LocalAssetRegistry.appCrestArtwork),
                            contentDescription = strings.brandTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Extruded 3D Cartoon Title
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.alpha(textAlpha.value)
            ) {
                Text(
                    text = "KID'Z WORLD",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 42.sp,
                        letterSpacing = 1.4.sp
                    ),
                    color = Color(0xFF1D4E89).copy(alpha = 0.52f),
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(x = 3.dp, y = 5.dp)
                )
                Text(
                    text = "KID'Z WORLD",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 42.sp,
                        letterSpacing = 1.4.sp
                    ),
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tagline Pill: Learn • Play • Grow
            Cartoon3DCard(
                containerColor = MangoPrimary,
                rimColor = MangoRim,
                cornerRadius = 50.dp,
                bevelDepth = 5.dp,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
                modifier = Modifier
                    .alpha(textAlpha.value)
                    .testTag("splash_tagline_pill")
            ) {
                Text(
                    text = strings.brandTagline,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Animated Bouncing Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(textAlpha.value)
            ) {
                listOf(HoneyGold, EmeraldMeadow, BerryCoral).forEachIndexed { idx, dotColor ->
                    val dotScale by infiniteTransition.animateFloat(
                        initialValue = 0.7f,
                        targetValue = 1.25f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = 550,
                                delayMillis = idx * 140,
                                easing = FastOutSlowInEasing
                            ),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot_$idx"
                    )
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .scale(dotScale)
                            .clip(CircleShape)
                            .background(dotColor)
                            .border(1.5.dp, Color.White, CircleShape)
                    )
                }
            }
        }

        // Bottom Developer Credit: Developed by Dev. E
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .alpha(textAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.22f))
                    .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(50))
                    .padding(horizontal = 18.dp, vertical = 8.dp)
                    .testTag("splash_developer_credit")
            ) {
                Text(
                    text = strings.developerCredit,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            if (isDataReady) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = strings.splashTapToContinue,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}
