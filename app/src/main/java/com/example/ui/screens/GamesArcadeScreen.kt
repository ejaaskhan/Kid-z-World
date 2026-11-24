package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.HelpOutline
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
import com.example.domain.model.GameScore
import com.example.domain.model.GameType
import com.example.domain.model.LessonItem
import com.example.domain.model.ModuleType
import com.example.ui.components.Cartoon3DButton
import com.example.ui.components.Cartoon3DCard
import com.example.ui.components.Cartoon3DCircleIconButton
import com.example.ui.components.KidzEmptyState
import com.example.ui.components.LanguageTogglePill
import com.example.ui.theme.BerryCoral
import com.example.ui.theme.BerryRim
import com.example.ui.theme.CeruleanRim
import com.example.ui.theme.CeruleanSky
import com.example.ui.theme.EmeraldMeadow
import com.example.ui.theme.GrapePop
import com.example.ui.theme.GrapeRim
import com.example.ui.theme.HoneyGold
import com.example.ui.theme.MangoPrimary
import com.example.ui.theme.darkenForRim
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class ArcadeMode { HUB, MEMORY_MATCH, PICTURE_QUIZ }

private data class MemoryCardModel(
    val uid: Int,
    val pairId: String,
    val badge: String,
    val label: String,
    val accentHex: Long,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false
)

@Composable
fun GamesArcadeScreen(
    lessons: List<LessonItem>,
    recentScores: List<GameScore>,
    language: AppLanguage,
    isWideScreen: Boolean,
    autoStartQuiz: Boolean = false,
    onConsumedAutoStartQuiz: () -> Unit = {},
    onToggleLanguage: () -> Unit,
    onSpeakText: (String) -> Unit,
    onPlayEffect: (SoundEffect) -> Unit,
    onRecordGameScore: (GameType, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var mode by remember { mutableStateOf(ArcadeMode.HUB) }

    LaunchedEffect(autoStartQuiz) {
        if (autoStartQuiz) {
            mode = ArcadeMode.PICTURE_QUIZ
            onConsumedAutoStartQuiz()
        }
    }

    BackHandler(enabled = mode != ArcadeMode.HUB) {
        mode = ArcadeMode.HUB
    }

    when (mode) {
        ArcadeMode.HUB -> ArcadeHubView(
            recentScores = recentScores,
            language = language,
            isWideScreen = isWideScreen,
            onToggleLanguage = onToggleLanguage,
            onLaunchMemory = {
                onPlayEffect(SoundEffect.TAP_POP)
                mode = ArcadeMode.MEMORY_MATCH
            },
            onLaunchQuiz = {
                onPlayEffect(SoundEffect.TAP_POP)
                mode = ArcadeMode.PICTURE_QUIZ
            },
            modifier = modifier
        )

        ArcadeMode.MEMORY_MATCH -> MemoryMatchGameView(
            lessons = lessons,
            language = language,
            isWideScreen = isWideScreen,
            onBack = { mode = ArcadeMode.HUB },
            onSpeakText = onSpeakText,
            onPlayEffect = onPlayEffect,
            onWin = { score, stars -> onRecordGameScore(GameType.MEMORY_MATCH, score, stars) },
            modifier = modifier
        )

        ArcadeMode.PICTURE_QUIZ -> PictureQuizGameView(
            lessons = lessons,
            language = language,
            isWideScreen = isWideScreen,
            onBack = { mode = ArcadeMode.HUB },
            onSpeakText = onSpeakText,
            onPlayEffect = onPlayEffect,
            onFinish = { score, stars -> onRecordGameScore(GameType.PICTURE_QUIZ, score, stars) },
            modifier = modifier
        )
    }
}

@Composable
private fun ArcadeHubView(
    recentScores: List<GameScore>,
    language: AppLanguage,
    isWideScreen: Boolean,
    onToggleLanguage: () -> Unit,
    onLaunchMemory: () -> Unit,
    onLaunchQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("games_arcade_hub"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 114.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.worldGames,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                LanguageTogglePill(language = language, onToggle = onToggleLanguage)
            }
        }

        item {
            if (isWideScreen) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    GameLauncherCard(
                        emoji = "🃏",
                        title = strings.memoryMatchTitle,
                        subtitle = strings.memoryMatchSub,
                        color = GrapePop,
                        rimColor = GrapeRim,
                        testTag = "launch_memory_game",
                        onClick = onLaunchMemory,
                        modifier = Modifier.weight(1f)
                    )
                    GameLauncherCard(
                        emoji = "🎯",
                        title = strings.pictureQuizTitle,
                        subtitle = strings.pictureQuizSub,
                        color = BerryCoral,
                        rimColor = BerryRim,
                        testTag = "launch_quiz_game",
                        onClick = onLaunchQuiz,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    GameLauncherCard(
                        emoji = "🃏",
                        title = strings.memoryMatchTitle,
                        subtitle = strings.memoryMatchSub,
                        color = GrapePop,
                        rimColor = GrapeRim,
                        testTag = "launch_memory_game",
                        onClick = onLaunchMemory,
                        modifier = Modifier.fillMaxWidth()
                    )
                    GameLauncherCard(
                        emoji = "🎯",
                        title = strings.pictureQuizTitle,
                        subtitle = strings.pictureQuizSub,
                        color = BerryCoral,
                        rimColor = BerryRim,
                        testTag = "launch_quiz_game",
                        onClick = onLaunchQuiz,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            Text(
                text = "🏆 ${strings.recentHighScores}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (recentScores.isEmpty()) {
            item {
                KidzEmptyState(
                    title = strings.recentHighScores,
                    subtitle = strings.noScoresYet,
                    emoji = "🏆"
                )
            }
        } else {
            items(recentScores.take(6), key = { it.id }) { score ->
                val (badgeEmoji, gameTitle) = when (score.gameType) {
                    GameType.MEMORY_MATCH -> "🃏" to strings.memoryMatchTitle
                    GameType.PICTURE_QUIZ -> "🎯" to strings.pictureQuizTitle
                    GameType.ALPHABET_QUIZ -> "🔤" to "${strings.pillarAlphabetTitle} • ${strings.modeQuiz}"
                    GameType.MATH_QUIZ -> "🔢" to "${strings.pillarNumbersTitle} • ${strings.modeQuiz}"
                    GameType.WORLD_QUIZ -> "🌍" to "${strings.pillarWorldTitle} • ${strings.modeQuiz}"
                    GameType.LANGUAGE_QUIZ -> "💬" to "${strings.pillarLanguageTitle} • ${strings.modeQuiz}"
                }
                Cartoon3DCard(
                    containerColor = MaterialTheme.colorScheme.surface,
                    rimColor = MaterialTheme.colorScheme.outline,
                    cornerRadius = 20.dp,
                    bevelDepth = 4.dp,
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = badgeEmoji, fontSize = 26.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = gameTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${strings.scoreLabel}: ${score.score.toLocalizedDigits(language)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(HoneyGold.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = MangoPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+${score.starsEarned.toLocalizedDigits(language)}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MangoPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GameLauncherCard(
    emoji: String,
    title: String,
    subtitle: String,
    color: Color,
    rimColor: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Cartoon3DCard(
        containerColor = color,
        rimColor = rimColor,
        cornerRadius = 26.dp,
        bevelDepth = 6.dp,
        contentPadding = PaddingValues(18.dp),
        onClick = onClick,
        modifier = modifier.testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.24f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 34.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.92f)
                )
            }
        }
    }
}

@Composable
private fun MemoryMatchGameView(
    lessons: List<LessonItem>,
    language: AppLanguage,
    isWideScreen: Boolean,
    onBack: () -> Unit,
    onSpeakText: (String) -> Unit,
    onPlayEffect: (SoundEffect) -> Unit,
    onWin: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val scope = rememberCoroutineScope()
    var pairCount by remember { mutableIntStateOf(3) } // 3 pairs = 6 cards (Easy), 6 pairs = 12 cards (Challenge)
    var cards by remember { mutableStateOf<List<MemoryCardModel>>(emptyList()) }
    var firstIndex by remember { mutableStateOf<Int?>(null) }
    var isBusy by remember { mutableStateOf(false) }
    var moves by remember { mutableIntStateOf(0) }
    var won by remember { mutableStateOf(false) }

    fun startNewRound(pairs: Int) {
        val pool = lessons.filter {
            it.moduleType == ModuleType.ANIMALS || it.moduleType == ModuleType.COLORS || it.moduleType == ModuleType.SHAPES
        }.ifEmpty { lessons }.shuffled().take(pairs)

        val deck = pool.flatMap { item ->
            listOf(
                MemoryCardModel(
                    uid = item.id.hashCode() * 2,
                    pairId = item.id,
                    badge = item.visualBadge,
                    label = item.displayTitle(language),
                    accentHex = item.accentColorHex
                ),
                MemoryCardModel(
                    uid = item.id.hashCode() * 2 + 1,
                    pairId = item.id,
                    badge = item.visualBadge,
                    label = item.displayTitle(language),
                    accentHex = item.accentColorHex
                )
            )
        }.shuffled().mapIndexed { idx, c -> c.copy(uid = idx) }

        cards = deck
        firstIndex = null
        isBusy = false
        moves = 0
        won = false
    }

    LaunchedEffect(pairCount, language, lessons.size) {
        if (lessons.isNotEmpty()) startNewRound(pairCount)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 104.dp)
            .testTag("memory_match_screen")
    ) {
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
                    contentDescription = strings.backToArcade,
                    containerColor = CeruleanSky,
                    onClick = onBack
                )
                Text(
                    text = strings.memoryMatchTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Cartoon3DCard(
                    containerColor = if (pairCount == 3) MangoPrimary else MaterialTheme.colorScheme.surface,
                    rimColor = if (pairCount == 3) MangoPrimary.darkenForRim() else MaterialTheme.colorScheme.outline,
                    cornerRadius = 14.dp,
                    bevelDepth = 3.dp,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                    onClick = { pairCount = 3 }
                ) {
                    Text(
                        text = strings.easyMode,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (pairCount == 3) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
                Cartoon3DCard(
                    containerColor = if (pairCount == 6) GrapePop else MaterialTheme.colorScheme.surface,
                    rimColor = if (pairCount == 6) GrapeRim else MaterialTheme.colorScheme.outline,
                    cornerRadius = 14.dp,
                    bevelDepth = 3.dp,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                    onClick = { pairCount = 6 }
                ) {
                    Text(
                        text = strings.challengeMode,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (pairCount == 6) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val matchedPairs = cards.count { it.isMatched } / 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${strings.pairsLabel}: ${matchedPairs.toLocalizedDigits(language)}/${pairCount.toLocalizedDigits(language)}",
                style = MaterialTheme.typography.titleMedium,
                color = EmeraldMeadow
            )
            Text(
                text = "${strings.movesLabel}: ${moves.toLocalizedDigits(language)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (won) {
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
                        text = strings.greatJob,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Cartoon3DButton(
                        text = strings.playAgain,
                        icon = Icons.Rounded.Replay,
                        containerColor = HoneyGold,
                        onClick = { startNewRound(pairCount) }
                    )
                }
            }
        } else {
            val cols = if (isWideScreen) 4 else 3
            LazyVerticalGrid(
                columns = GridCells.Fixed(cols),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(cards, key = { _, c -> c.uid }) { index, card ->
                    val revealed = card.isFlipped || card.isMatched
                    val cardColor by animateColorAsState(
                        targetValue = when {
                            card.isMatched -> EmeraldMeadow
                            card.isFlipped -> Color(card.accentHex)
                            else -> GrapePop
                        },
                        label = "mem_card"
                    )
                    Cartoon3DCard(
                        containerColor = cardColor,
                        rimColor = cardColor.darkenForRim(0.72f),
                        cornerRadius = 20.dp,
                        bevelDepth = 5.dp,
                        contentPadding = PaddingValues(8.dp),
                        onClick = {
                            if (isBusy || revealed) return@Cartoon3DCard
                            onPlayEffect(SoundEffect.TAP_POP)
                            val updated = cards.toMutableList()
                            updated[index] = card.copy(isFlipped = true)
                            cards = updated

                            val prev = firstIndex
                            if (prev == null) {
                                firstIndex = index
                            } else {
                                moves += 1
                                val prevCard = updated[prev]
                                if (prevCard.pairId == card.pairId) {
                                    updated[prev] = prevCard.copy(isMatched = true)
                                    updated[index] = updated[index].copy(isMatched = true)
                                    cards = updated
                                    firstIndex = null
                                    onPlayEffect(SoundEffect.CORRECT_CHIME)
                                    onSpeakText(card.label)
                                    if (updated.all { it.isMatched }) {
                                        won = true
                                        val score = ((pairCount * 25) - moves * 2).coerceAtLeast(20)
                                        val stars = if (pairCount >= 6) 15 else 10
                                        onWin(score, stars)
                                    }
                                } else {
                                    isBusy = true
                                    scope.launch {
                                        delay(750L)
                                        onPlayEffect(SoundEffect.TRY_AGAIN_BOOP)
                                        val reset = cards.toMutableList()
                                        if (prev < reset.size) reset[prev] = reset[prev].copy(isFlipped = false)
                                        if (index < reset.size) reset[index] = reset[index].copy(isFlipped = false)
                                        cards = reset
                                        firstIndex = null
                                        isBusy = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .aspectRatio(0.92f)
                            .testTag("memory_card_$index")
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (revealed) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = card.badge, fontSize = 34.sp)
                                    Text(
                                        text = card.label,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White,
                                        maxLines = 1,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.Extension,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PictureQuizGameView(
    lessons: List<LessonItem>,
    language: AppLanguage,
    isWideScreen: Boolean,
    onBack: () -> Unit,
    onSpeakText: (String) -> Unit,
    onPlayEffect: (SoundEffect) -> Unit,
    onFinish: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val scope = rememberCoroutineScope()
    val quizPool = remember(lessons) {
        lessons.filter {
            it.moduleType == ModuleType.ANIMALS ||
                it.moduleType == ModuleType.COLORS ||
                it.moduleType == ModuleType.SHAPES ||
                it.moduleType == ModuleType.NUMBERS
        }.ifEmpty { lessons }
    }

    var round by remember { mutableIntStateOf(1) }
    val totalRounds = 5
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var target by remember { mutableStateOf<LessonItem?>(null) }
    var options by remember { mutableStateOf<List<LessonItem>>(emptyList()) }
    var feedback by remember { mutableStateOf<String?>(null) }
    var finished by remember { mutableStateOf(false) }

    fun loadQuestion() {
        if (quizPool.size < 4) return
        val picked = quizPool.shuffled().take(4)
        val answer = picked.random()
        options = picked
        target = answer
        feedback = null
        onSpeakText("${strings.quizPromptPrefix} ${answer.displayTitle(language)}")
    }

    LaunchedEffect(quizPool.size, language) {
        if (quizPool.size >= 4 && target == null && !finished) {
            loadQuestion()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 104.dp)
            .testTag("picture_quiz_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
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
                    contentDescription = strings.backToArcade,
                    containerColor = CeruleanSky,
                    onClick = onBack
                )
                Text(
                    text = strings.pictureQuizTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "${strings.scoreLabel}: ${score.toLocalizedDigits(language)}  •  🔥 ${streak.toLocalizedDigits(language)}",
                style = MaterialTheme.typography.titleMedium,
                color = MangoPrimary
            )
        }

        if (finished) {
            Cartoon3DCard(
                containerColor = BerryCoral,
                rimColor = BerryRim,
                cornerRadius = 28.dp,
                contentPadding = PaddingValues(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "🎉🏆🎉", fontSize = 44.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = strings.greatJob,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${strings.scoreLabel}: ${score.toLocalizedDigits(language)} / ${(totalRounds * 20).toLocalizedDigits(language)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Cartoon3DButton(
                        text = strings.playAgain,
                        icon = Icons.Rounded.Replay,
                        containerColor = HoneyGold,
                        onClick = {
                            round = 1
                            score = 0
                            streak = 0
                            finished = false
                            loadQuestion()
                        }
                    )
                }
            }
        } else if (target != null) {
            val currentTarget = target!!
            Cartoon3DCard(
                containerColor = MaterialTheme.colorScheme.surface,
                rimColor = CeruleanRim,
                cornerRadius = 26.dp,
                contentPadding = PaddingValues(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${round.toLocalizedDigits(language)} / ${totalRounds.toLocalizedDigits(language)}",
                        style = MaterialTheme.typography.labelLarge,
                        color = CeruleanSky
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${strings.quizPromptPrefix} ${currentTarget.displayTitle(language)}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Cartoon3DButton(
                        text = strings.tapToHear,
                        icon = Icons.AutoMirrored.Rounded.VolumeUp,
                        containerColor = CeruleanSky,
                        onClick = { onSpeakText("${strings.quizPromptPrefix} ${currentTarget.displayTitle(language)}") },
                        modifier = Modifier.width(200.dp)
                    )
                    if (feedback != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = feedback!!,
                            style = MaterialTheme.typography.titleMedium,
                            color = MangoPrimary
                        )
                    }
                }
            }

            val cols = if (isWideScreen) 4 else 2
            LazyVerticalGrid(
                columns = GridCells.Fixed(cols),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(options, key = { _, opt -> opt.id }) { idx, option ->
                    val accent = Color(option.accentColorHex)
                    Cartoon3DCard(
                        containerColor = accent,
                        rimColor = accent.darkenForRim(0.72f),
                        cornerRadius = 24.dp,
                        bevelDepth = 6.dp,
                        contentPadding = PaddingValues(16.dp),
                        onClick = {
                            if (option.id == currentTarget.id) {
                                score += 20
                                streak += 1
                                feedback = "⭐ ${strings.greatJob}"
                                onPlayEffect(SoundEffect.CORRECT_CHIME)
                                scope.launch {
                                    delay(700L)
                                    if (round >= totalRounds) {
                                        finished = true
                                        val stars = (score / 10).coerceAtLeast(5)
                                        onFinish(score, stars)
                                    } else {
                                        round += 1
                                        loadQuestion()
                                    }
                                }
                            } else {
                                streak = 0
                                feedback = strings.tryAgainHint
                                onPlayEffect(SoundEffect.TRY_AGAIN_BOOP)
                            }
                        },
                        modifier = Modifier
                            .height(132.dp)
                            .testTag("quiz_option_$idx")
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = option.visualBadge, fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = option.displaySymbol(language),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        } else {
            KidzEmptyState(
                title = strings.pictureQuizTitle,
                subtitle = strings.loadingWorld,
                emoji = "🎯"
            )
        }
    }
}
