package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.AppLanguage
import com.example.core.localization.LocalAppStrings
import com.example.core.localization.toLocalizedDigits
import com.example.domain.model.DailyStreak
import com.example.ui.theme.CeruleanSky
import com.example.ui.theme.EmeraldMeadow
import com.example.ui.theme.HoneyGold
import com.example.ui.theme.MangoPrimary
import com.example.ui.theme.MangoRim

/**
 * Compact, animated 3D flame badge displayed on the Home Dashboard.
 * Pulses gently when active today and displays a bouncing gift indicator when a daily bonus is claimable.
 */
@Composable
fun AnimatedDailyStreakBadge(
    streak: DailyStreak,
    language: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val haptics = LocalHapticFeedback.current
    val effectiveDays = streak.effectiveStreak()
    val activeToday = streak.isActiveToday()
    val bonusReady = streak.canClaimDailyBonus()

    val infiniteTransition = rememberInfiniteTransition(label = "streak_flame")
    val flameScale by infiniteTransition.animateFloat(
        initialValue = if (activeToday) 0.94f else 0.96f,
        targetValue = if (activeToday) 1.16f else 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (activeToday) 720 else 1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_scale"
    )
    val flameTilt by infiniteTransition.animateFloat(
        initialValue = if (activeToday) -6f else -2f,
        targetValue = if (activeToday) 6f else 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_tilt"
    )

    val badgeGradient = if (activeToday) {
        Brush.horizontalGradient(listOf(MangoPrimary, Color(0xFFFF5E3A)))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFF434866).copy(alpha = 0.88f), Color(0xFF2D324D).copy(alpha = 0.92f)))
    }

    val dayWord = if (effectiveDays == 1) strings.streakDaySingular else strings.streakDayPlural
    val shape = RoundedCornerShape(50)

    Row(
        modifier = modifier
            .testTag("daily_streak_badge")
            .heightIn(min = 48.dp)
            .clip(shape)
            .background(badgeGradient)
            .border(
                width = 2.dp,
                color = if (bonusReady) HoneyGold else Color.White.copy(alpha = 0.8f),
                shape = shape
            )
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.LocalFireDepartment,
            contentDescription = strings.streakTitle,
            tint = if (activeToday) Color(0xFFFFF099) else Color(0xFFFFB566),
            modifier = Modifier
                .size(22.dp)
                .scale(flameScale)
                .rotate(flameTilt)
        )
        Text(
            text = "${effectiveDays.toLocalizedDigits(language)} $dayWord",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold
        )
        if (bonusReady) {
            Box(
                modifier = Modifier
                    .scale(flameScale)
                    .clip(RoundedCornerShape(50))
                    .background(HoneyGold)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = strings.streakBonusReadyTag,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF25233A),
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun DailyStreakDialog(
    streak: DailyStreak,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onClaimBonus: () -> Unit,
    onStartLearning: () -> Unit
) {
    val strings = LocalAppStrings.current
    val effectiveDays = streak.effectiveStreak()
    val activeToday = streak.isActiveToday()
    val bonusReady = streak.canClaimDailyBonus()
    val claimedToday = streak.isBonusClaimedToday()
    val bonusStars = streak.dailyBonusStars()
    val dayWord = if (effectiveDays == 1) strings.streakDaySingular else strings.streakDayPlural

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(28.dp),
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Floating3DBox(floatAmplitudePx = 6f, durationMillis = 1500) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    colors = if (activeToday) {
                                        listOf(HoneyGold, MangoPrimary)
                                    } else {
                                        listOf(Color(0xFF757DA8), Color(0xFF474E73))
                                    }
                                )
                            )
                            .border(3.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocalFireDepartment,
                            contentDescription = strings.streakTitle,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = strings.streakTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_streak_dialog")
            ) {
                // Big Streak Counter + Best Streak Pill
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Cartoon3DCard(
                        containerColor = MangoPrimary,
                        rimColor = MangoRim,
                        cornerRadius = 18.dp,
                        bevelDepth = 4.dp,
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "🔥 ${effectiveDays.toLocalizedDigits(language)} $dayWord",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(HoneyGold.copy(alpha = 0.2f))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = strings.streakBestLabel,
                            tint = MangoPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${strings.streakBestLabel}: ${streak.bestStreak.toLocalizedDigits(language)}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // 7-Day Consistency Milestone Dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val litDots = effectiveDays.coerceIn(0, 7)
                    repeat(7) { idx ->
                        val isLit = idx < litDots
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isLit) HoneyGold else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = if (isLit) MangoPrimary else MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLit) {
                                Icon(
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(
                                    text = (idx + 1).toLocalizedDigits(language),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Text(
                    text = when {
                        claimedToday -> strings.streakBonusClaimedMsg
                        activeToday -> strings.streakActiveTodayMsg
                        else -> strings.streakStartTodayMsg
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                // Primary Real Action Button
                when {
                    bonusReady -> {
                        Cartoon3DButton(
                            text = "${strings.streakClaimBonusButton} (+${bonusStars.toLocalizedDigits(language)}★)",
                            icon = Icons.Rounded.CardGiftcard,
                            containerColor = EmeraldMeadow,
                            onClick = {
                                onClaimBonus()
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("claim_streak_bonus_button")
                        )
                    }

                    claimedToday -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(EmeraldMeadow.copy(alpha = 0.16f))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldMeadow,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+${bonusStars.toLocalizedDigits(language)}★",
                                style = MaterialTheme.typography.titleMedium,
                                color = EmeraldMeadow,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    else -> {
                        Cartoon3DButton(
                            text = strings.streakLearnNowButton,
                            icon = Icons.Rounded.School,
                            containerColor = CeruleanSky,
                            onClick = {
                                onDismiss()
                                onStartLearning()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("streak_learn_now_button")
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.closeButton, fontWeight = FontWeight.Bold)
            }
        }
    )
}
