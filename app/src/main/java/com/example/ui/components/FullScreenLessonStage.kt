package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.SoundEffect
import com.example.core.localization.AppLanguage
import com.example.core.localization.LocalAppStrings
import com.example.core.localization.toLocalizedDigits
import com.example.domain.model.LessonItem
import com.example.domain.model.ModuleType
import com.example.ui.theme.BerryCoral
import com.example.ui.theme.CeruleanSky
import com.example.ui.theme.EmeraldMeadow
import com.example.ui.theme.GrapePop
import com.example.ui.theme.HoneyGold
import com.example.ui.theme.MangoPrimary
import com.example.ui.theme.darkenForRim
import com.example.ui.theme.lightenForGloss
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Full-Screen Active 3D Lesson Stage:
 * Displays a giant 3D extruded letter/symbol, rich custom 3D illustration medallion
 * (e.g., glossy 3D red apple for Letter A), clear English & Bangla word cards,
 * dedicated Pronounce & Replay Audio buttons, Prev & Next controls, and an animated Star Reward burst.
 */
@Composable
fun FullScreenActiveLessonStage(
    item: LessonItem,
    currentIndex: Int,
    totalCount: Int,
    activeAvatarKey: String,
    language: AppLanguage,
    canGoPrev: Boolean,
    canGoNext: Boolean,
    onBackToGrid: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onToggleLanguage: () -> Unit,
    onSpeakLesson: (LessonItem) -> Unit,
    onSpeakCount: (Int) -> Unit,
    onPlayEffect: (SoundEffect) -> Unit,
    onMarkLearned: (LessonItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var showStarBurst by remember(item.id) { mutableStateOf(false) }

    LaunchedEffect(showStarBurst) {
        if (showStarBurst) {
            delay(1500L)
            showStarBurst = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("full_screen_lesson_stage")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Top Bar: Chunky 3D Back + Lesson Counter Pill + Language Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Cartoon3DCircleIconButton(
                        icon = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = strings.onboardingBack,
                        containerColor = CeruleanSky,
                        onClick = onBackToGrid,
                        modifier = Modifier.testTag("full_lesson_back_button")
                    )
                    CartoonAvatarBadge(avatarKey = activeAvatarKey, size = 44.dp)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                            .border(1.5.dp, Color.White, RoundedCornerShape(50))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${(currentIndex + 1).toLocalizedDigits(language)} / ${totalCount.toLocalizedDigits(language)}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                LanguageTogglePill(language = language, onToggle = onToggleLanguage)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Animated Letter / Lesson Transition Stage
            AnimatedContent(
                targetState = item,
                transitionSpec = {
                    val forward = targetState.sortOrder >= initialState.sortOrder
                    if (forward) {
                        (slideInHorizontally(tween(280)) { it / 2 } + fadeIn(tween(260))) togetherWith
                            (slideOutHorizontally(tween(240)) { -it / 2 } + fadeOut(tween(200)))
                    } else {
                        (slideInHorizontally(tween(280)) { -it / 2 } + fadeIn(tween(260))) togetherWith
                            (slideOutHorizontally(tween(240)) { it / 2 } + fadeOut(tween(200)))
                    }.using(SizeTransform(clip = false))
                },
                label = "active_lesson_transition",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { current ->
                ActiveLessonHeroCard(
                    item = current,
                    language = language,
                    onSpeakLesson = onSpeakLesson,
                    onSpeakCount = onSpeakCount,
                    onPlayEffect = onPlayEffect,
                    onComplete = {
                        showStarBurst = true
                        onMarkLearned(current)
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Floating Previous / Complete / Next Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Cartoon3DCircleIconButton(
                    icon = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = strings.prevLesson,
                    containerColor = GrapePop,
                    enabled = canGoPrev,
                    size = 56.dp,
                    onClick = onPrev,
                    modifier = Modifier.testTag("lesson_prev_button")
                )

                Cartoon3DButton(
                    text = if (item.isLearned) strings.learnedBadge else strings.completeLessonReward,
                    icon = Icons.Rounded.Star,
                    containerColor = if (item.isLearned) EmeraldMeadow else MangoPrimary,
                    onClick = {
                        showStarBurst = true
                        onMarkLearned(item)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mark_learned_button")
                )

                Cartoon3DCircleIconButton(
                    icon = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = strings.nextLesson,
                    containerColor = GrapePop,
                    enabled = canGoNext,
                    size = 56.dp,
                    onClick = onNext,
                    modifier = Modifier.testTag("lesson_next_button")
                )
            }
        }

        // Animated 3D Star Burst Particle Celebration Overlay
        LessonStarRewardExplosion(
            visible = showStarBurst,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun ActiveLessonHeroCard(
    item: LessonItem,
    language: AppLanguage,
    onSpeakLesson: (LessonItem) -> Unit,
    onSpeakCount: (Int) -> Unit,
    onPlayEffect: (SoundEffect) -> Unit,
    onComplete: () -> Unit
) {
    val strings = LocalAppStrings.current
    val accent = Color(item.accentColorHex)
    var tappedCount by remember(item.id) { mutableIntStateOf(0) }

    LaunchedEffect(item.id) {
        tappedCount = 0
    }

    val isAlphabet = item.moduleType == ModuleType.ALPHABET_EN || item.moduleType == ModuleType.ALPHABET_BN
    val isNumber = item.moduleType == ModuleType.NUMBERS
    val isSentence = item.moduleType == ModuleType.SIMPLE_SENTENCES

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Giant 3D Showcase Card
        Cartoon3DCard(
            containerColor = MaterialTheme.colorScheme.surface,
            rimColor = accent.darkenForRim(0.76f),
            cornerRadius = 32.dp,
            bevelDepth = 8.dp,
            contentPadding = PaddingValues(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("flashcard_stage")
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Status pill if mastered
                if (item.isLearned) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(EmeraldMeadow.copy(alpha = 0.16f))
                            .border(1.dp, EmeraldMeadow.copy(alpha = 0.4f), RoundedCornerShape(50))
                            .padding(horizontal = 14.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = strings.learnedBadge,
                            tint = EmeraldMeadow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "⭐ ${strings.learnedBadge}",
                            style = MaterialTheme.typography.labelLarge,
                            color = EmeraldMeadow
                        )
                    }
                }

                // Giant 3D Extruded Hero Letter/Number + 3D Framed Illustration Side-by-Side (or Centered Hero Art)
                if (isAlphabet || isNumber) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(26.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        accent.copy(alpha = 0.16f),
                                        HoneyGold.copy(alpha = 0.12f)
                                    )
                                )
                            )
                            .border(2.dp, accent.copy(alpha = 0.28f), RoundedCornerShape(26.dp))
                            .clickable { onSpeakLesson(item) }
                            .padding(horizontal = 16.dp, vertical = 20.dp)
                            .testTag("flashcard_glyph_tap"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Giant Extruded 3D Letter / Number
                        Floating3DBox(floatAmplitudePx = 7f, durationMillis = 2000) {
                            Extruded3DSymbol(
                                text = item.displaySymbol(language),
                                accent = accent,
                                fontSizeSp = if (item.displaySymbol(language).length >= 3) 64 else 92
                            )
                        }

                        // Framed 3D Illustration Medallion (custom glossy 3D red apple for A, etc.)
                        Floating3DBox(floatAmplitudePx = 6f, durationMillis = 2300) {
                            Rich3DLessonIllustration(
                                item = item,
                                accent = accent,
                                modifier = Modifier.size(116.dp)
                            )
                        }
                    }
                } else {
                    // World Exploration & Language Big Centered 3D Artwork Medallion
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(26.dp))
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        accent.copy(alpha = 0.22f),
                                        accent.copy(alpha = 0.06f)
                                    )
                                )
                            )
                            .border(2.dp, accent.copy(alpha = 0.26f), RoundedCornerShape(26.dp))
                            .clickable { onSpeakLesson(item) }
                            .padding(vertical = 22.dp)
                            .testTag("flashcard_glyph_tap"),
                        contentAlignment = Alignment.Center
                    ) {
                        Floating3DBox(floatAmplitudePx = 7f, durationMillis = 2100) {
                            Rich3DLessonIllustration(
                                item = item,
                                accent = accent,
                                modifier = Modifier.size(134.dp)
                            )
                        }
                    }
                }

                // Clear Bilingual Phrase Display (e.g., "A is for Apple" + "বাংলা: A মানে Apple (আপেল)")
                val headlinePrimary = when (item.moduleType) {
                    ModuleType.ALPHABET_EN -> "${item.symbol} is for ${item.exampleEn}"
                    ModuleType.ALPHABET_BN -> "${item.symbolBn} তে ${item.exampleBn}"
                    ModuleType.NUMBERS -> "${item.displaySymbol(language)} — ${item.displayTitle(language)}"
                    else -> item.displayTitle(language)
                }

                val bilingualSubline = when (item.moduleType) {
                    ModuleType.ALPHABET_EN -> "বাংলা: ${item.symbol} মানে ${item.exampleEn} (${item.exampleBn})"
                    ModuleType.ALPHABET_BN -> "English: ${item.symbolBn} (${item.symbol}) is for ${item.exampleEn}"
                    ModuleType.NUMBERS -> if (language == AppLanguage.BANGLA) {
                        "English: ${item.symbol} — ${item.titleEn}"
                    } else {
                        "বাংলা: ${item.symbolBn} — ${item.titleBn}"
                    }
                    ModuleType.SIMPLE_SENTENCES -> if (language == AppLanguage.BANGLA) item.titleEn else item.titleBn
                    else -> if (language == AppLanguage.BANGLA) {
                        "English: ${item.titleEn} • ${item.exampleEn}"
                    } else {
                        "বাংলা: ${item.titleBn} • ${item.exampleBn}"
                    }
                }

                Text(
                    text = headlinePrimary,
                    style = if (isSentence) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(accent.copy(alpha = 0.12f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = bilingualSubline,
                        style = MaterialTheme.typography.titleMedium,
                        color = accent.darkenForRim(0.78f),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                // Extra interactive counting board for Numbers 1-100
                if (isNumber && item.countValue > 0) {
                    NumberCountingBoard3D(
                        item = item,
                        language = language,
                        accent = accent,
                        tappedCount = tappedCount,
                        onTapObject = { next ->
                            tappedCount = next
                            onPlayEffect(SoundEffect.TAP_POP)
                            onSpeakCount(next)
                        }
                    )
                }

                // Dedicated Pronounce & Replay Audio 3D Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Cartoon3DButton(
                        text = strings.pronounceButton,
                        icon = Icons.AutoMirrored.Rounded.VolumeUp,
                        containerColor = CeruleanSky,
                        onClick = { onSpeakLesson(item) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tap_to_hear_button")
                    )
                    Cartoon3DButton(
                        text = strings.replayAudio,
                        icon = Icons.Rounded.Replay,
                        containerColor = BerryCoral,
                        onClick = { onSpeakLesson(item) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("replay_audio_button")
                    )
                }
            }
        }
    }
}

/**
 * Giant Extruded 3D Clay Typography for hero letters and numbers.
 */
@Composable
fun Extruded3DSymbol(
    text: String,
    accent: Color,
    fontSizeSp: Int = 92,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Soft extruded 3D shadow layers
        Text(
            text = text,
            fontSize = fontSizeSp.sp,
            fontWeight = FontWeight.ExtraBold,
            color = accent.darkenForRim(0.55f),
            modifier = Modifier.offset(x = 4.dp, y = 7.dp)
        )
        Text(
            text = text,
            fontSize = fontSizeSp.sp,
            fontWeight = FontWeight.ExtraBold,
            color = accent.darkenForRim(0.72f),
            modifier = Modifier.offset(x = 2.dp, y = 4.dp)
        )
        // Bright front face
        Text(
            text = text,
            fontSize = fontSizeSp.sp,
            fontWeight = FontWeight.ExtraBold,
            color = accent
        )
    }
}

/**
 * Rich custom 3D illustration medallion:
 * Draws a multi-layered glossy 3D Red Apple for Letter A / Apple,
 * custom glossy 3D color spheres for Colors, custom 3D geometric art for Shapes,
 * and glowing framed 3D clay medallions for every other lesson!
 */
@Composable
private fun Rich3DLessonIllustration(
    item: LessonItem,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val isApple = item.id == "en_a" || item.id == "fruitveg_apple"
    Box(
        modifier = modifier
            .shadow(12.dp, CircleShape, ambientColor = accent.copy(alpha = 0.4f), spotColor = accent.copy(alpha = 0.5f))
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        accent.lightenForGloss(0.68f),
                        accent.copy(alpha = 0.32f)
                    )
                )
            )
            .border(4.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        when {
            isApple -> Glossy3DRedAppleArtwork(modifier = Modifier.size(84.dp))
            item.moduleType == ModuleType.COLORS -> {
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.78f), accent, accent.darkenForRim(0.75f))
                            )
                        )
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.visualBadge, fontSize = 36.sp)
                }
            }
            item.moduleType == ModuleType.SHAPES -> {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(Brush.verticalGradient(listOf(accent.lightenForGloss(0.2f), accent))),
                    contentAlignment = Alignment.Center
                ) {
                    GeometricShape3DCanvas(shapeId = item.id, modifier = Modifier.size(58.dp))
                }
            }
            else -> {
                Text(
                    text = item.visualBadge,
                    fontSize = 56.sp
                )
            }
        }
    }
}

/**
 * Custom Pixar-style Glossy 3D Red Apple vector illustration with stem, bright green leaf,
 * radial volume shading, and bright specular highlight bubble.
 */
@Composable
private fun Glossy3DRedAppleArtwork(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Soft grounding shadow beneath apple
        drawOval(
            color = Color.Black.copy(alpha = 0.14f),
            topLeft = Offset(w * 0.18f, h * 0.84f),
            size = Size(w * 0.64f, h * 0.12f)
        )

        // Left & right plump apple lobes with 3D radial shading
        val appleBrush = Brush.radialGradient(
            colors = listOf(Color(0xFFFF6E7F), Color(0xFFE62E43), Color(0xFFA81228)),
            center = Offset(w * 0.36f, h * 0.42f),
            radius = w * 0.62f
        )
        drawCircle(brush = appleBrush, radius = w * 0.31f, center = Offset(w * 0.36f, h * 0.56f))
        drawCircle(brush = appleBrush, radius = w * 0.31f, center = Offset(w * 0.64f, h * 0.56f))
        drawOval(
            brush = appleBrush,
            topLeft = Offset(w * 0.16f, h * 0.32f),
            size = Size(w * 0.68f, h * 0.58f)
        )

        // Glossy specular shine bubble on top-left
        drawOval(
            color = Color.White.copy(alpha = 0.52f),
            topLeft = Offset(w * 0.24f, h * 0.36f),
            size = Size(w * 0.16f, h * 0.24f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.72f),
            radius = w * 0.045f,
            center = Offset(w * 0.32f, h * 0.40f)
        )

        // Warm brown curved stem
        val stemPath = Path().apply {
            moveTo(w * 0.50f, h * 0.34f)
            quadraticTo(w * 0.52f, h * 0.18f, w * 0.59f, h * 0.12f)
        }
        drawPath(
            path = stemPath,
            color = Color(0xFF7B4828),
            style = Stroke(width = w * 0.07f, cap = StrokeCap.Round)
        )

        // Fresh 3D emerald leaf
        val leafPath = Path().apply {
            moveTo(w * 0.54f, h * 0.26f)
            quadraticTo(w * 0.78f, h * 0.12f, w * 0.84f, h * 0.30f)
            quadraticTo(w * 0.68f, h * 0.38f, w * 0.54f, h * 0.26f)
            close()
        }
        drawPath(
            path = leafPath,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF5BF098), Color(0xFF1CB864))
            )
        )
    }
}

@Composable
private fun GeometricShape3DCanvas(
    shapeId: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val pad = w * 0.08f
        val fill = Color.White
        when {
            shapeId.contains("circle") -> drawCircle(color = fill, radius = (w / 2f) - pad)
            shapeId.contains("square") -> drawRoundRect(
                color = fill,
                topLeft = Offset(pad, pad),
                size = Size(w - pad * 2, h - pad * 2),
                cornerRadius = CornerRadius(14f, 14f)
            )
            shapeId.contains("rectangle") -> drawRoundRect(
                color = fill,
                topLeft = Offset(pad * 0.5f, h * 0.22f),
                size = Size(w - pad, h * 0.56f),
                cornerRadius = CornerRadius(12f, 12f)
            )
            shapeId.contains("oval") -> drawOval(
                color = fill,
                topLeft = Offset(pad * 0.5f, h * 0.18f),
                size = Size(w - pad, h * 0.64f)
            )
            shapeId.contains("triangle") -> {
                val path = Path().apply {
                    moveTo(w / 2f, pad)
                    lineTo(w - pad, h - pad)
                    lineTo(pad, h - pad)
                    close()
                }
                drawPath(path, color = fill)
            }
            shapeId.contains("diamond") -> {
                val path = Path().apply {
                    moveTo(w / 2f, pad)
                    lineTo(w - pad, h / 2f)
                    lineTo(w / 2f, h - pad)
                    lineTo(pad, h / 2f)
                    close()
                }
                drawPath(path, color = fill)
            }
            shapeId.contains("heart") -> {
                val path = Path().apply {
                    moveTo(w / 2f, h * 0.85f)
                    cubicTo(w * 0.06f, h * 0.55f, w * 0.12f, h * 0.12f, w / 2f, h * 0.34f)
                    cubicTo(w * 0.88f, h * 0.12f, w * 0.94f, h * 0.55f, w / 2f, h * 0.85f)
                    close()
                }
                drawPath(path, color = fill)
            }
            else -> {
                val path = Path()
                val cx = w / 2f
                val cy = h / 2f
                val outerR = w * 0.44f
                val innerR = w * 0.20f
                for (i in 0 until 10) {
                    val angle = (-90.0 + i * 36.0) * PI / 180.0
                    val r = if (i % 2 == 0) outerR else innerR
                    val x = (cx + r * cos(angle)).toFloat()
                    val y = (cy + r * sin(angle)).toFloat()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                drawPath(path, color = fill)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NumberCountingBoard3D(
    item: LessonItem,
    language: AppLanguage,
    accent: Color,
    tappedCount: Int,
    onTapObject: (Int) -> Unit
) {
    val strings = LocalAppStrings.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(accent.copy(alpha = 0.11f))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (item.countValue <= 20) {
            Text(
                text = "${strings.tapObjectsToCount} (${strings.countedSoFar}: ${tappedCount.toLocalizedDigits(language)}/${item.countValue.toLocalizedDigits(language)})",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(item.countValue) { idx ->
                    val isCounted = idx < tappedCount
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isCounted) accent else Color.White)
                            .border(2.dp, accent.copy(alpha = 0.55f), CircleShape)
                            .clickable {
                                val nextCount = if (idx + 1 == tappedCount) tappedCount else (tappedCount + 1).coerceAtMost(item.countValue)
                                onTapObject(nextCount)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isCounted) (idx + 1).toLocalizedDigits(language) else item.visualBadge,
                            fontSize = if (isCounted) 14.sp else 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCounted) Color.White else Color.Unspecified
                        )
                    }
                }
            }
        } else {
            val tens = item.countValue / 10
            val ones = item.countValue % 10
            Text(
                text = "${tens.toLocalizedDigits(language)} ${strings.tensLabel}  +  ${ones.toLocalizedDigits(language)} ${strings.onesLabel}  =  ${item.countValue.toLocalizedDigits(language)}",
                style = MaterialTheme.typography.titleMedium,
                color = accent,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(tens) { bundleIdx ->
                    val cumulative = (bundleIdx + 1) * 10
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(accent)
                            .clickable { onTapObject(cumulative) }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = "${10.toLocalizedDigits(language)} ${item.visualBadge}",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                repeat(ones) { oneIdx ->
                    val cumulative = tens * 10 + oneIdx + 1
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.dp, accent, CircleShape)
                            .clickable { onTapObject(cumulative) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = item.visualBadge, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

/**
 * Celebratory floating 3D stars & sparkles burst animation shown after completing a lesson.
 */
@Composable
private fun LessonStarRewardExplosion(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(visible) {
        if (visible) {
            progress.snapTo(0f)
            progress.animateTo(1f, animationSpec = tween(1200, easing = FastOutSlowInEasing))
        }
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(initialScale = 0.7f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
        exit = fadeOut() + scaleOut(targetScale = 1.15f),
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height * 0.42f
            val colors = listOf(HoneyGold, MangoPrimary, BerryCoral, CeruleanSky, EmeraldMeadow, GrapePop)
            val p = progress.value
            for (i in 0 until 14) {
                val angle = (i * (360.0 / 14.0)) * PI / 180.0
                val dist = size.minDimension * 0.36f * p
                val x = (cx + dist * cos(angle)).toFloat()
                val y = (cy + dist * sin(angle)).toFloat()
                val alpha = (1f - p * 0.85f).coerceIn(0f, 1f)
                val radius = (18f - p * 6f).coerceAtLeast(5f)
                drawCircle(color = colors[i % colors.size].copy(alpha = alpha), radius = radius, center = Offset(x, y))
            }
        }
    }
}

/**
 * Spacious Colorful 3D Letter & Lesson Grid:
 * Large unmistakable 3D clay tiles with oversized letters, mini cartoon illustrations,
 * and bright emerald star progress badges on completed lessons.
 */
@Composable
fun Spacious3DLessonGrid(
    items: List<LessonItem>,
    language: AppLanguage,
    columns: Int,
    onPick: (LessonItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = 110.dp),
        modifier = modifier
    ) {
        items(items, key = { it.id }) { item ->
            val accent = Color(item.accentColorHex)
            val isAlphabetOrNum = item.moduleType == ModuleType.ALPHABET_EN ||
                item.moduleType == ModuleType.ALPHABET_BN ||
                item.moduleType == ModuleType.NUMBERS

            Cartoon3DCard(
                containerColor = accent.lightenForGloss(0.84f),
                rimColor = accent.copy(alpha = 0.55f),
                cornerRadius = 24.dp,
                bevelDepth = 6.dp,
                contentPadding = PaddingValues(12.dp),
                onClick = { onPick(item) },
                modifier = Modifier.testTag("lesson_tile_${item.id}")
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Completion indicator top-right + mini cartoon illustration badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.88f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = item.visualBadge, fontSize = 16.sp)
                        }
                        if (item.isLearned) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldMeadow),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(24.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Big bold 3D letter/symbol
                    if (isAlphabetOrNum) {
                        Text(
                            text = item.displaySymbol(language),
                            fontSize = if (item.displaySymbol(language).length >= 3) 30.sp else 40.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = accent.darkenForRim(0.82f),
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (item.moduleType == ModuleType.NUMBERS) item.displayTitle(language) else item.displayExample(language),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF332F4D),
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = item.visualBadge,
                            fontSize = 38.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.displayTitle(language),
                            style = MaterialTheme.typography.titleSmall,
                            color = Color(0xFF25233A),
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
