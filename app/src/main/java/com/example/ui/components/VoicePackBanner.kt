package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.GraphicEq
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
import com.example.core.localization.AppLanguage
import com.example.core.localization.LocalAppStrings
import com.example.core.localization.toLocalizedDigits
import com.example.domain.model.BundledBanglaAudioStatus
import com.example.ui.theme.CeruleanRim
import com.example.ui.theme.CeruleanSky
import com.example.ui.theme.EmeraldMeadow
import com.example.ui.theme.EmeraldRim
import com.example.ui.theme.MangoPrimary

/**
 * Clean built-in offline Bangla audio status badge + voice test controls for Parent Zone.
 * Zero Android TTS settings redirects and zero voice pack installation prompts.
 */
@Composable
fun BundledAudioStatusSection(
    audioStatus: BundledBanglaAudioStatus,
    language: AppLanguage,
    onTestBanglaVoice: () -> Unit,
    onTestEnglishVoice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bundled_audio_status_section"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(EmeraldMeadow.copy(alpha = 0.14f))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = strings.voiceReadyBadge,
                tint = EmeraldMeadow,
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = strings.voiceReadyBadge,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val countSummary = if (audioStatus.totalClipsCount > 0) {
                    "${audioStatus.verifiedClipsCount.toLocalizedDigits(language)}/${audioStatus.totalClipsCount.toLocalizedDigits(language)} ${strings.bundledAudioClipsSuffix}"
                } else {
                    audioStatus.localeLabel
                }
                Text(
                    text = countSummary,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
