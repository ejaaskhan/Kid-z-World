package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ModuleQuizStage(
    pool: List<LessonItem>,
    language: AppLanguage,
    onSpeakLesson: (LessonItem) -> Unit,
    onPlayEffect: (SoundEffect) -> Unit,
    onQuizCompleted: (score: Int, stars: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val scope = rememberCoroutineScope()
    val totalRounds = 5

    var round by remember(pool.size) { mutableIntStateOf(1) }
    var score by remember(pool.size) { mutableIntStateOf(0) }
    var streak by remember(pool.size) { mutableIntStateOf(0) }
    var target by remember(pool.size) { mutableStateOf<LessonItem?>(null) }
    var options by remember(pool.size) { mutableStateOf<List<LessonItem>>(emptyList()) }
    var feedback by remember(pool.size) { mutableStateOf<String?>(null) }
    var finished by remember(pool.size) { mutableStateOf(false) }

    fun loadNextQuestion() {
        if (pool.size < 4) return
        val picked = pool.shuffled().take(4)
        val answer = picked.random()
        options = picked
        target = answer
        feedback = null
        onSpeakLesson(answer)
    }

    LaunchedEffect(pool.size, language) {
        if (pool.size >= 4 && target == null && !finished) {
            loadNextQuestion()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("module_quiz_stage"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (pool.size < 4) {
            KidzEmptyState(
                title = strings.emptyLessonsTitle,
                subtitle = strings.emptyLessonsBody,
                emoji = "🎯"
            )
            return@Column
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${round.toLocalizedDigits(language)} / ${totalRounds.toLocalizedDigits(language)}",
                style = MaterialTheme.typography.titleMedium,
                color = CeruleanSky
            )
            Text(
                text = "${strings.scoreLabel}: ${score.toLocalizedDigits(language)}  •  🔥 ${streak.toLocalizedDigits(language)}",
                style = MaterialTheme.typography.titleMedium,
                color = MangoPrimary
            )
        }

        if (finished) {
            Cartoon3DCard(
                containerColor = GrapePop,
                rimColor = GrapePop.darkenForRim(),
                cornerRadius = 26.dp,
                contentPadding = PaddingValues(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.EmojiEvents,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = strings.greatJob,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Text(
                        text = "${strings.scoreLabel}: ${score.toLocalizedDigits(language)} / ${(totalRounds * 20).toLocalizedDigits(language)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Cartoon3DButton(
                        text = strings.playAgain,
                        icon = Icons.Rounded.Replay,
                        containerColor = HoneyGold,
                        onClick = {
                            round = 1
                            score = 0
                            streak = 0
                            finished = false
                            loadNextQuestion()
                        }
                    )
                }
            }
        } else if (target != null) {
            val answer = target!!
            val isAlphabet = answer.moduleType == ModuleType.ALPHABET_EN || answer.moduleType == ModuleType.ALPHABET_BN
            val promptLabel = if (isAlphabet) {
                "${strings.quizPromptPrefix} ${answer.visualBadge} ${answer.displayExample(language)}"
            } else {
                "${strings.quizPromptPrefix} ${answer.displayTitle(language)}"
            }

            Cartoon3DCard(
                containerColor = MaterialTheme.colorScheme.surface,
                rimColor = CeruleanSky.darkenForRim(0.8f),
                cornerRadius = 24.dp,
                contentPadding = PaddingValues(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = promptLabel,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Cartoon3DButton(
                        text = strings.replayAudio,
                        icon = Icons.AutoMirrored.Rounded.VolumeUp,
                        containerColor = CeruleanSky,
                        onClick = { onSpeakLesson(answer) },
                        modifier = Modifier.width(206.dp)
                    )
                    if (feedback != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = feedback!!,
                            style = MaterialTheme.typography.titleMedium,
                            color = MangoPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            val rows = options.chunked(2)
            rows.forEachIndexed { rIdx, pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEachIndexed { cIdx, option ->
                        val accent = Color(option.accentColorHex)
                        Cartoon3DCard(
                            containerColor = accent,
                            rimColor = accent.darkenForRim(0.72f),
                            cornerRadius = 22.dp,
                            bevelDepth = 5.dp,
                            contentPadding = PaddingValues(14.dp),
                            onClick = {
                                if (option.id == answer.id) {
                                    score += 20
                                    streak += 1
                                    feedback = "⭐ ${strings.greatJob}"
                                    onPlayEffect(SoundEffect.CORRECT_CHIME)
                                    scope.launch {
                                        delay(650L)
                                        if (round >= totalRounds) {
                                            finished = true
                                            onQuizCompleted(score, (score / 10).coerceAtLeast(10))
                                        } else {
                                            round += 1
                                            loadNextQuestion()
                                        }
                                    }
                                } else {
                                    streak = 0
                                    feedback = strings.tryAgainHint
                                    onPlayEffect(SoundEffect.TRY_AGAIN_BOOP)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(112.dp)
                                .testTag("module_quiz_opt_${rIdx * 2 + cIdx}")
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isAlphabet) option.displaySymbol(language) else option.visualBadge,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isAlphabet) option.displayExample(language) else option.displayTitle(language),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White,
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
    }
}
