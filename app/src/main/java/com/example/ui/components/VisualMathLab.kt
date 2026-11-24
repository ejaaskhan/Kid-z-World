package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Remove
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.SoundEffect
import com.example.core.localization.AppLanguage
import com.example.core.localization.LocalAppStrings
import com.example.core.localization.toLocalizedDigits
import com.example.domain.engine.MathOperation
import com.example.domain.engine.MathProblemGenerator
import com.example.domain.model.AgeDifficulty
import com.example.ui.theme.BerryCoral
import com.example.ui.theme.CeruleanSky
import com.example.ui.theme.EmeraldMeadow
import com.example.ui.theme.GrapePop
import com.example.ui.theme.HoneyGold
import com.example.ui.theme.MangoPrimary
import com.example.ui.theme.darkenForRim
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VisualMathLab(
    difficulty: AgeDifficulty,
    language: AppLanguage,
    isQuizMode: Boolean,
    onSpeakText: (String) -> Unit,
    onPlayEffect: (SoundEffect) -> Unit,
    onQuizCompleted: (score: Int, stars: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val scope = rememberCoroutineScope()

    var operation by remember { mutableStateOf(MathOperation.ADDITION) }
    var problem by remember(difficulty, operation) {
        mutableStateOf(MathProblemGenerator.generate(difficulty, operation))
    }
    var feedback by remember { mutableStateOf<String?>(null) }
    var round by remember(isQuizMode, difficulty) { mutableIntStateOf(1) }
    var score by remember(isQuizMode, difficulty) { mutableIntStateOf(0) }
    var streak by remember(isQuizMode, difficulty) { mutableIntStateOf(0) }
    var finished by remember(isQuizMode, difficulty) { mutableStateOf(false) }
    val totalRounds = 5

    fun nextProblem(nextOp: MathOperation = operation) {
        problem = MathProblemGenerator.generate(difficulty, nextOp)
        feedback = null
    }

    LaunchedEffect(difficulty, isQuizMode) {
        round = 1
        score = 0
        streak = 0
        finished = false
        val startOp = if (isQuizMode) MathOperation.ADDITION else operation
        problem = MathProblemGenerator.generate(difficulty, startOp)
        feedback = null
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag(if (isQuizMode) "math_quiz_stage" else "visual_math_lab"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Operation Selector in Lab Mode (Quiz Mode mixes + and − automatically!)
        if (!isQuizMode) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Cartoon3DButton(
                    text = strings.mathAddition,
                    icon = Icons.Rounded.Add,
                    containerColor = if (operation == MathOperation.ADDITION) EmeraldMeadow else MaterialTheme.colorScheme.surface,
                    rimColor = if (operation == MathOperation.ADDITION) EmeraldMeadow.darkenForRim() else MaterialTheme.colorScheme.outline,
                    contentColor = if (operation == MathOperation.ADDITION) Color.White else MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        onPlayEffect(SoundEffect.TAP_POP)
                        operation = MathOperation.ADDITION
                        nextProblem(MathOperation.ADDITION)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("math_op_add")
                )
                Cartoon3DButton(
                    text = strings.mathSubtraction,
                    icon = Icons.Rounded.Remove,
                    containerColor = if (operation == MathOperation.SUBTRACTION) BerryCoral else MaterialTheme.colorScheme.surface,
                    rimColor = if (operation == MathOperation.SUBTRACTION) BerryCoral.darkenForRim() else MaterialTheme.colorScheme.outline,
                    contentColor = if (operation == MathOperation.SUBTRACTION) Color.White else MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        onPlayEffect(SoundEffect.TAP_POP)
                        operation = MathOperation.SUBTRACTION
                        nextProblem(MathOperation.SUBTRACTION)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("math_op_sub")
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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
        }

        if (isQuizMode && finished) {
            Cartoon3DCard(
                containerColor = EmeraldMeadow,
                rimColor = EmeraldMeadow.darkenForRim(),
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
                        text = strings.mathCorrectMsg,
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
                            nextProblem(MathOperation.ADDITION)
                        }
                    )
                }
            }
        } else {
            // Equation & Visual Objects Stage
            Cartoon3DCard(
                containerColor = MaterialTheme.colorScheme.surface,
                rimColor = EmeraldMeadow.darkenForRim(0.8f),
                cornerRadius = 26.dp,
                contentPadding = PaddingValues(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = problem.formattedEquation(language),
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = 40.sp),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    // Visual Countable Group for smaller age-friendly operands
                    if (problem.left <= 12 && problem.right <= 12) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(EmeraldMeadow.copy(alpha = 0.11f))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                repeat(problem.left) {
                                    Text(text = problem.visualEmoji, fontSize = 20.sp)
                                }
                            }
                            Text(
                                text = " ${problem.operation.symbol} ",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MangoPrimary,
                                fontWeight = FontWeight.ExtraBold
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                repeat(problem.right) {
                                    Text(text = problem.visualEmoji, fontSize = 20.sp)
                                }
                            }
                        }
                    }

                    Cartoon3DButton(
                        text = strings.replayAudio,
                        icon = Icons.AutoMirrored.Rounded.VolumeUp,
                        containerColor = CeruleanSky,
                        onClick = { onSpeakText(problem.spokenPrompt(language)) },
                        modifier = Modifier.width(210.dp)
                    )

                    if (feedback != null) {
                        Text(
                            text = feedback!!,
                            style = MaterialTheme.typography.titleMedium,
                            color = MangoPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 4 Chunky 3D Answer Buttons (2x2 Grid)
            val optionColors = listOf(CeruleanSky, MangoPrimary, GrapePop, BerryCoral)
            val rows = problem.options.chunked(2)
            rows.forEachIndexed { rowIdx, pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEachIndexed { colIdx, candidate ->
                        val color = optionColors[(rowIdx * 2 + colIdx) % optionColors.size]
                        Cartoon3DButton(
                            text = candidate.toLocalizedDigits(language),
                            containerColor = color,
                            onClick = {
                                if (candidate == problem.correctAnswer) {
                                    onPlayEffect(SoundEffect.CORRECT_CHIME)
                                    feedback = "⭐ ${strings.mathCorrectMsg}"
                                    if (isQuizMode) {
                                        score += 20
                                        streak += 1
                                        scope.launch {
                                            delay(650L)
                                            if (round >= totalRounds) {
                                                finished = true
                                                onQuizCompleted(score, (score / 10).coerceAtLeast(10))
                                            } else {
                                                round += 1
                                                val nextOp = if (round % 2 == 0) MathOperation.SUBTRACTION else MathOperation.ADDITION
                                                nextProblem(nextOp)
                                            }
                                        }
                                    } else {
                                        scope.launch {
                                            delay(650L)
                                            nextProblem(operation)
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
                                .testTag("math_option_${rowIdx * 2 + colIdx}")
                        )
                    }
                }
            }
        }
    }
}
