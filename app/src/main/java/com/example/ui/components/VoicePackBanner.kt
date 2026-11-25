package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.localization.LocalAppStrings
import com.example.domain.model.VoicePackStatus
import com.example.ui.theme.CeruleanRim
import com.example.ui.theme.CeruleanSky
import com.example.ui.theme.EmeraldMeadow
import com.example.ui.theme.EmeraldRim
import com.example.ui.theme.HoneyGold
import com.example.ui.theme.MangoPrimary
import com.example.ui.theme.MangoRim
import com.example.ui.theme.darkenForRim

/**
 * Friendly, non-blocking 3D card prompting parents/users to download Android's free
 * offline Bangla TTS voice pack if missing, with a one-tap installer + refresh button.
 */
@Composable
fun BanglaVoicePackPromptCard(
    voiceStatus: VoicePackStatus,
    onInstallBanglaPack: () -> Unit,
    onRefreshVoices: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!voiceStatus.isInitialized || voiceStatus.banglaInstalled) return
    val strings = LocalAppStrings.current

    Cartoon3DCard(
        containerColor = HoneyGold.copy(alpha = 0.2f),
        rimColor = HoneyGold.darkenForRim(0.8f),
        cornerRadius = 24.dp,
        bevelDepth = 4.dp,
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("bangla_voice_pack_prompt_card")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MangoPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                        contentDescription = strings.voicePackTitle,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.voicePackTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = strings.voicePackMissingBody,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Cartoon3DButton(
                    text = strings.installBanglaPackButton,
                    icon = Icons.Rounded.Download,
                    containerColor = MangoPrimary,
                    rimColor = MangoRim,
                    onClick = onInstallBanglaPack,
                    modifier = Modifier
                        .weight(1.4f)
                        .testTag("install_bangla_voice_button")
                )
                Cartoon3DButton(
                    text = strings.refreshVoicesButton,
                    icon = Icons.Rounded.Refresh,
                    containerColor = CeruleanSky,
                    rimColor = CeruleanRim,
                    onClick = onRefreshVoices,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("refresh_voices_button")
                )
            }
        }
    }
}

/**
 * Compact status pill + voice test controls for Parent Zone audio settings.
 */
@Composable
fun VoicePackStatusSection(
    voiceStatus: VoicePackStatus,
    onInstallBanglaPack: () -> Unit,
    onRefreshVoices: () -> Unit,
    onTestBanglaVoice: () -> Unit,
    onTestEnglishVoice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (voiceStatus.banglaInstalled) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(EmeraldMeadow.copy(alpha = 0.14f))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = strings.voiceReadyBadge,
                        tint = EmeraldMeadow,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = strings.voiceReadyBadge,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${voiceStatus.selectedBanglaVoiceLabel} • ${voiceStatus.selectedEnglishVoiceLabel}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Cartoon3DCircleIconButton(
                    icon = Icons.Rounded.Refresh,
                    contentDescription = strings.refreshVoicesButton,
                    containerColor = EmeraldMeadow,
                    size = 42.dp,
                    onClick = onRefreshVoices,
                    modifier = Modifier.testTag("parent_refresh_voices_button")
                )
            }
        } else {
            BanglaVoicePackPromptCard(
                voiceStatus = voiceStatus,
                onInstallBanglaPack = onInstallBanglaPack,
                onRefreshVoices = onRefreshVoices
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Cartoon3DButton(
                text = strings.testBanglaVoiceButton,
                icon = Icons.AutoMirrored.Rounded.VolumeUp,
                containerColor = EmeraldMeadow,
                rimColor = EmeraldRim,
                onClick = onTestBanglaVoice,
                modifier = Modifier
                    .weight(1f)
                    .testTag("test_bangla_voice_button")
            )
            Cartoon3DButton(
                text = strings.testEnglishVoiceButton,
                icon = Icons.AutoMirrored.Rounded.VolumeUp,
                containerColor = CeruleanSky,
                rimColor = CeruleanRim,
                onClick = onTestEnglishVoice,
                modifier = Modifier
                    .weight(1f)
                    .testTag("test_english_voice_button")
            )
        }
    }
}

/**
 * Animated visual audio pulse badge so children see when voice narration is actively speaking.
 */
@Composable
fun SpeakingWaveBadge(
    isSpeaking: Boolean,
    accentColor: Color = MangoPrimary,
    modifier: Modifier = Modifier
) {
    if (!isSpeaking) return
    val transition = rememberInfiniteTransition(label = "speaking_pulse")
    val pulse by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 480, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Row(
        modifier = modifier
            .scale(pulse)
            .clip(RoundedCornerShape(50))
            .background(accentColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.GraphicEq,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "♫",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
