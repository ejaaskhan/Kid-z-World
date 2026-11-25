package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.SlowMotionVideo
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.assets.LocalAssetRegistry
import com.example.core.audio.SoundEffect
import com.example.core.localization.AppLanguage
import com.example.core.localization.LocalAppStrings
import com.example.core.localization.toLocalizedDigits
import com.example.domain.model.BanglaPoem
import com.example.domain.model.VoicePackStatus
import com.example.ui.components.BanglaVoicePackPromptCard
import com.example.ui.components.Cartoon3DButton
import com.example.ui.components.Cartoon3DCard
import com.example.ui.components.Cartoon3DCircleIconButton
import com.example.ui.components.Floating3DBox
import com.example.ui.components.LanguageTogglePill
import com.example.ui.components.SpeakingWaveBadge
import com.example.ui.theme.BerryCoral
import com.example.ui.theme.BerryRim
import com.example.ui.theme.CeruleanSky
import com.example.ui.theme.EmeraldMeadow
import com.example.ui.theme.EmeraldRim
import com.example.ui.theme.GrapePop
import com.example.ui.theme.HoneyGold
import com.example.ui.theme.MangoPrimary
import com.example.ui.theme.MangoRim
import com.example.ui.theme.darkenForRim

@Composable
fun BanglaPoemsScreen(
    poems: List<BanglaPoem>,
    language: AppLanguage,
    isSpeaking: Boolean,
    activeUtteranceIndex: Int?,
    voiceStatus: VoicePackStatus,
    isWideScreen: Boolean,
    onToggleLanguage: () -> Unit,
    onRecitePoem: (BanglaPoem, Boolean) -> Unit,
    onReciteStanza: (String, Boolean) -> Unit,
    onStopSpeaking: () -> Unit,
    onPlayEffect: (SoundEffect) -> Unit,
    onCompletePoem: (BanglaPoem) -> Unit,
    onInstallBanglaPack: () -> Unit,
    onRefreshVoices: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activePoemId by rememberSaveable { mutableStateOf<String?>(null) }
    val activePoem = remember(poems, activePoemId) {
        poems.firstOrNull { it.id == activePoemId }
    }

    DisposableEffect(Unit) {
        onDispose { onStopSpeaking() }
    }

    BackHandler(enabled = activePoem != null) {
        onStopSpeaking()
        onPlayEffect(SoundEffect.TAP_POP)
        activePoemId = null
    }

    if (activePoem == null) {
        PoemsShelfView(
            poems = poems,
            language = language,
            voiceStatus = voiceStatus,
            isWideScreen = isWideScreen,
            onToggleLanguage = onToggleLanguage,
            onSelectPoem = { poem ->
                onPlayEffect(SoundEffect.PAGE_TURN)
                activePoemId = poem.id
            },
            onInstallBanglaPack = onInstallBanglaPack,
            onRefreshVoices = onRefreshVoices,
            modifier = modifier
        )
    } else {
        PoemTheatreView(
            poem = activePoem,
            language = language,
            isSpeaking = isSpeaking,
            activeUtteranceIndex = activeUtteranceIndex,
            voiceStatus = voiceStatus,
            onBack = {
                onStopSpeaking()
                onPlayEffect(SoundEffect.TAP_POP)
                activePoemId = null
            },
            onRecitePoem = onRecitePoem,
            onReciteStanza = onReciteStanza,
            onStopSpeaking = onStopSpeaking,
            onCompletePoem = {
                onStopSpeaking()
                onCompletePoem(activePoem)
            },
            onInstallBanglaPack = onInstallBanglaPack,
            onRefreshVoices = onRefreshVoices,
            modifier = modifier
        )
    }
}

@Composable
private fun PoemsShelfView(
    poems: List<BanglaPoem>,
    language: AppLanguage,
    voiceStatus: VoicePackStatus,
    isWideScreen: Boolean,
    onToggleLanguage: () -> Unit,
    onSelectPoem: (BanglaPoem) -> Unit,
    onInstallBanglaPack: () -> Unit,
    onRefreshVoices: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val completedCount = poems.count { it.isCompleted }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("bangla_poems_shelf"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 118.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.poemsShelfTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = strings.poemsShelfSub,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                LanguageTogglePill(language = language, onToggle = onToggleLanguage)
            }
        }

        // Magical Moonlit Bangla Rhymes Hero Banner
        item {
            Cartoon3DCard(
                containerColor = Color(0xFF5647C9),
                rimColor = Color(0xFF3A2E9C),
                cornerRadius = 28.dp,
                bevelDepth = 6.dp,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(152.dp)
                ) {
                    Image(
                        painter = painterResource(id = LocalAssetRegistry.poemsMoonlitArtwork),
                        contentDescription = strings.poemsShelfTitle,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF1E1752).copy(alpha = 0.84f),
                                        Color(0xFF1E1752).copy(alpha = 0.32f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(HoneyGold)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.MusicNote,
                                contentDescription = null,
                                tint = Color(0xFF3E2600),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${completedCount.toLocalizedDigits(language)}/${poems.size.toLocalizedDigits(language)} ★",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF3E2600)
                            )
                        }
                        Text(
                            text = if (language == AppLanguage.BANGLA) "সুরে সুরে বাংলা ছড়া" else "Musical Bangla Rhymes",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = if (language == AppLanguage.BANGLA) "ট্যাপ করে সুন্দর বাংলা কণ্ঠে শোনো" else "Tap any card for clear Bangla voice!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.92f)
                        )
                    }
                }
            }
        }

        // One-tap Bangla Voice Pack prompt when missing on the device
        if (voiceStatus.isInitialized && !voiceStatus.banglaInstalled) {
            item {
                BanglaVoicePackPromptCard(
                    voiceStatus = voiceStatus,
                    onInstallBanglaPack = onInstallBanglaPack,
                    onRefreshVoices = onRefreshVoices
                )
            }
        }

        val chunkSize = if (isWideScreen) 2 else 1
        val rows = poems.chunked(chunkSize)
        itemsIndexed(rows) { _, rowPoems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowPoems.forEach { poem ->
                    PoemShelfCard(
                        poem = poem,
                        language = language,
                        onClick = { onSelectPoem(poem) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowPoems.size < chunkSize) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PoemShelfCard(
    poem: BanglaPoem,
    language: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val accent = Color(poem.accentColorHex)
    val previewLine = poem.stanzasBn.firstOrNull().orEmpty().replace("\n", " • ")

    Cartoon3DCard(
        containerColor = MaterialTheme.colorScheme.surface,
        rimColor = accent.darkenForRim(0.8f),
        cornerRadius = 24.dp,
        bevelDepth = 5.dp,
        contentPadding = PaddingValues(14.dp),
        onClick = onClick,
        modifier = modifier.testTag("poem_card_${poem.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(accent.copy(alpha = 0.18f))
                    .border(2.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = poem.emoji, fontSize = 32.sp)
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = poem.titleBn,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (poem.isCompleted) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = strings.completedPoem,
                            tint = EmeraldMeadow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (language == AppLanguage.ENGLISH) {
                    Text(
                        text = poem.titleEn,
                        style = MaterialTheme.typography.labelMedium,
                        color = accent,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = previewLine,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                    contentDescription = strings.recitePoem,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun PoemTheatreView(
    poem: BanglaPoem,
    language: AppLanguage,
    isSpeaking: Boolean,
    activeUtteranceIndex: Int?,
    voiceStatus: VoicePackStatus,
    onBack: () -> Unit,
    onRecitePoem: (BanglaPoem, Boolean) -> Unit,
    onReciteStanza: (String, Boolean) -> Unit,
    onStopSpeaking: () -> Unit,
    onCompletePoem: () -> Unit,
    onInstallBanglaPack: () -> Unit,
    onRefreshVoices: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val accent = Color(poem.accentColorHex)
    var slowMode by rememberSaveable(poem.id) { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("poem_theatre_${poem.id}"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 122.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header with Back Button & Pace Toggle
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Cartoon3DCircleIconButton(
                        icon = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = strings.onboardingBack,
                        containerColor = accent,
                        onClick = onBack,
                        modifier = Modifier.testTag("poem_back_button")
                    )
                    Column {
                        Text(
                            text = poem.titleBn,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (language == AppLanguage.BANGLA) poem.traditionBn else poem.titleEn,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f)
                        )
                    }
                }

                // Slow vs Normal child listening speed pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (slowMode) CeruleanSky else MaterialTheme.colorScheme.surface)
                        .border(2.dp, CeruleanSky, RoundedCornerShape(50))
                        .clickable { slowMode = !slowMode }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("poem_pace_toggle"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = if (slowMode) Icons.Rounded.SlowMotionVideo else Icons.Rounded.Speed,
                        contentDescription = null,
                        tint = if (slowMode) Color.White else CeruleanSky,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (slowMode) strings.slowVoicePace else strings.normalVoicePace,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (slowMode) Color.White else CeruleanSky
                    )
                }
            }
        }

        // Illustrated 3D Scene Card
        item {
            Cartoon3DCard(
                containerColor = accent,
                rimColor = accent.darkenForRim(0.75f),
                cornerRadius = 28.dp,
                bevelDepth = 6.dp,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp)
                ) {
                    Image(
                        painter = painterResource(id = LocalAssetRegistry.poemIllustration(poem.artworkKey)),
                        contentDescription = poem.titleBn,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f))
                                )
                            )
                    )
                    Floating3DBox(
                        floatAmplitudePx = 6f,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.92f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = poem.emoji, fontSize = 30.sp)
                        }
                    }

                    SpeakingWaveBadge(
                        isSpeaking = isSpeaking,
                        accentColor = MangoPrimary,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(14.dp)
                    )

                    Text(
                        text = strings.tapStanzaHint,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    )
                }
            }
        }

        if (voiceStatus.isInitialized && !voiceStatus.banglaInstalled) {
            item {
                BanglaVoicePackPromptCard(
                    voiceStatus = voiceStatus,
                    onInstallBanglaPack = onInstallBanglaPack,
                    onRefreshVoices = onRefreshVoices
                )
            }
        }

        // Primary Recite + Reward Controls (Big 56dp+ Kid Targets)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Cartoon3DButton(
                    text = if (isSpeaking) strings.stopReciting else strings.recitePoem,
                    icon = if (isSpeaking) Icons.Rounded.Stop else Icons.AutoMirrored.Rounded.VolumeUp,
                    containerColor = if (isSpeaking) BerryCoral else MangoPrimary,
                    rimColor = if (isSpeaking) BerryRim else MangoRim,
                    onClick = {
                        if (isSpeaking) onStopSpeaking() else onRecitePoem(poem, slowMode)
                    },
                    modifier = Modifier
                        .weight(1.25f)
                        .height(58.dp)
                        .testTag("poem_recite_button")
                )
                Cartoon3DButton(
                    text = if (poem.isCompleted) strings.completedPoem else strings.finishPoem,
                    icon = Icons.Rounded.Star,
                    containerColor = EmeraldMeadow,
                    rimColor = EmeraldRim,
                    onClick = onCompletePoem,
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp)
                        .testTag("poem_finish_button")
                )
            }
        }

        // Interactive Stanza Cards with Live Highlighting
        itemsIndexed(poem.stanzasBn) { index, stanza ->
            // activeUtteranceIndex == 0 is the title; stanza index i maps to utterance index i + 1
            val isHighlighted = isSpeaking && activeUtteranceIndex == (index + 1)
            val cardBg by animateColorAsState(
                targetValue = if (isHighlighted) accent.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surface,
                label = "stanza_bg"
            )
            val scale by animateFloatAsState(
                targetValue = if (isHighlighted) 1.02f else 1f,
                animationSpec = spring(dampingRatio = 0.7f),
                label = "stanza_scale"
            )

            Cartoon3DCard(
                containerColor = cardBg,
                rimColor = if (isHighlighted) accent else accent.darkenForRim(0.84f),
                cornerRadius = 24.dp,
                bevelDepth = if (isHighlighted) 6.dp else 4.dp,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
                onClick = { onReciteStanza(stanza, slowMode) },
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scale)
                    .testTag("poem_stanza_$index")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stanza,
                        fontSize = 22.sp,
                        lineHeight = 36.sp,
                        fontWeight = if (isHighlighted) FontWeight.ExtraBold else FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isHighlighted) accent else accent.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                            contentDescription = strings.recitePoem,
                            tint = if (isHighlighted) Color.White else accent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // Bilingual Meaning Note
        item {
            Cartoon3DCard(
                containerColor = GrapePop.copy(alpha = 0.1f),
                rimColor = GrapePop.darkenForRim(0.84f),
                cornerRadius = 22.dp,
                bevelDepth = 3.dp,
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "✨ ${strings.meaningLabel}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = GrapePop
                    )
                    Text(
                        text = poem.meaningEn,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}
