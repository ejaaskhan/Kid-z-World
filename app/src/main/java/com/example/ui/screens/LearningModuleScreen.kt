package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Quiz
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.domain.model.AgeDifficulty
import com.example.domain.model.GameType
import com.example.domain.model.LearningPillar
import com.example.domain.model.LessonItem
import com.example.domain.model.ModuleProgress
import com.example.domain.model.ModuleType
import com.example.ui.components.Cartoon3DCard
import com.example.ui.components.Cartoon3DCircleIconButton
import com.example.ui.components.CartoonMascotBanner
import com.example.ui.components.CartoonProgressBar
import com.example.ui.components.FullScreenActiveLessonStage
import com.example.ui.components.KidzEmptyState
import com.example.ui.components.LanguageTogglePill
import com.example.ui.components.LetterTracingStudio
import com.example.ui.components.ModuleQuizStage
import com.example.ui.components.Spacious3DLessonGrid
import com.example.ui.components.VisualMathLab
import com.example.ui.theme.BerryCoral
import com.example.ui.theme.BerryRim
import com.example.ui.theme.CeruleanRim
import com.example.ui.theme.CeruleanSky
import com.example.ui.theme.EmeraldMeadow
import com.example.ui.theme.EmeraldRim
import com.example.ui.theme.GrapePop
import com.example.ui.theme.GrapeRim
import com.example.ui.theme.HoneyGold
import com.example.ui.theme.MangoPrimary
import com.example.ui.theme.MangoRim
import com.example.ui.theme.darkenForRim

private enum class StudioMode { LESSONS, TRACE, MATH_LAB, QUIZ }

@Composable
fun LearningModuleScreen(
    activePillar: LearningPillar?,
    selectedModule: ModuleType,
    activeLessonId: String?,
    activeChildName: String,
    activeChildAvatarKey: String,
    activeChildAge: Int,
    lessons: List<LessonItem>,
    moduleProgress: Map<ModuleType, ModuleProgress>,
    language: AppLanguage,
    isWideScreen: Boolean,
    onOpenPillar: (LearningPillar?) -> Unit,
    onSelectModule: (ModuleType) -> Unit,
    onOpenActiveLesson: (String?) -> Unit,
    onToggleLanguage: () -> Unit,
    onSpeakLesson: (LessonItem) -> Unit,
    onSpeakText: (String) -> Unit,
    onSpeakCount: (Int) -> Unit,
    onPlayEffect: (SoundEffect) -> Unit,
    onMarkLearned: (LessonItem) -> Unit,
    onRecordQuizScore: (GameType, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var difficulty by remember(activeChildAge) {
        mutableStateOf(AgeDifficulty.fromAge(activeChildAge))
    }

    BackHandler(enabled = activeLessonId != null || activePillar != null) {
        if (activeLessonId != null) {
            onOpenActiveLesson(null)
        } else {
            onOpenPillar(null)
        }
    }

    if (activePillar == null) {
        LearnFourModuleHub(
            activeChildName = activeChildName,
            activeChildAvatarKey = activeChildAvatarKey,
            difficulty = difficulty,
            moduleProgress = moduleProgress,
            language = language,
            isWideScreen = isWideScreen,
            onSelectDifficulty = {
                onPlayEffect(SoundEffect.TAP_POP)
                difficulty = it
            },
            onToggleLanguage = onToggleLanguage,
            onOpenPillar = { pillar ->
                onPlayEffect(SoundEffect.TAP_POP)
                val defaultModule = when (pillar) {
                    LearningPillar.ALPHABET -> if (language == AppLanguage.BANGLA) ModuleType.ALPHABET_BN else ModuleType.ALPHABET_EN
                    LearningPillar.NUMBERS -> ModuleType.NUMBERS
                    LearningPillar.WORLD -> ModuleType.ANIMALS
                    LearningPillar.LANGUAGE -> if (language == AppLanguage.BANGLA) ModuleType.BANGLA_VOCAB else ModuleType.BASIC_WORDS
                }
                onOpenActiveLesson(null)
                onSelectModule(defaultModule)
                onOpenPillar(pillar)
            },
            modifier = modifier
        )
    } else {
        PillarStudioExperience(
            pillar = activePillar,
            selectedModule = selectedModule,
            activeLessonId = activeLessonId,
            activeChildName = activeChildName,
            activeChildAvatarKey = activeChildAvatarKey,
            difficulty = difficulty,
            lessons = lessons,
            language = language,
            isWideScreen = isWideScreen,
            onBackToHub = {
                onPlayEffect(SoundEffect.TAP_POP)
                onOpenActiveLesson(null)
                onOpenPillar(null)
            },
            onSelectDifficulty = {
                onPlayEffect(SoundEffect.TAP_POP)
                difficulty = it
            },
            onSelectModule = {
                onPlayEffect(SoundEffect.TAP_POP)
                onOpenActiveLesson(null)
                onSelectModule(it)
            },
            onOpenActiveLesson = onOpenActiveLesson,
            onToggleLanguage = onToggleLanguage,
            onSpeakLesson = onSpeakLesson,
            onSpeakText = onSpeakText,
            onSpeakCount = onSpeakCount,
            onPlayEffect = onPlayEffect,
            onMarkLearned = onMarkLearned,
            onRecordQuizScore = onRecordQuizScore,
            modifier = modifier
        )
    }
}

@Composable
private fun LearnFourModuleHub(
    activeChildName: String,
    activeChildAvatarKey: String,
    difficulty: AgeDifficulty,
    moduleProgress: Map<ModuleType, ModuleProgress>,
    language: AppLanguage,
    isWideScreen: Boolean,
    onSelectDifficulty: (AgeDifficulty) -> Unit,
    onToggleLanguage: () -> Unit,
    onOpenPillar: (LearningPillar) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    fun progressFor(pillar: LearningPillar): Pair<Int, Int> {
        val types = pillar.moduleTypes()
        val done = types.sumOf { moduleProgress[it]?.completedCount ?: 0 }
        val total = types.sumOf { moduleProgress[it]?.totalCount ?: 0 }.coerceAtLeast(1)
        return done to total
    }

    data class PillarCardInfo(
        val pillar: LearningPillar,
        val emoji: String,
        val title: String,
        val subtitle: String,
        val color: Color,
        val rim: Color
    )

    val pillars = listOf(
        PillarCardInfo(LearningPillar.ALPHABET, "🔤", strings.alphabetAdventureTitle, strings.pillarAlphabetSub, CeruleanSky, CeruleanRim),
        PillarCardInfo(LearningPillar.NUMBERS, "🔢", strings.pillarNumbersTitle, strings.pillarNumbersSub, EmeraldMeadow, EmeraldRim),
        PillarCardInfo(LearningPillar.WORLD, "🌍", strings.pillarWorldTitle, strings.pillarWorldSub, BerryCoral, BerryRim),
        PillarCardInfo(LearningPillar.LANGUAGE, "💬", strings.pillarLanguageTitle, strings.pillarLanguageSub, GrapePop, GrapeRim)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("learn_module_hub"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.navLearn,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                LanguageTogglePill(language = language, onToggle = onToggleLanguage)
            }
        }

        // Friendly Profile-Matched 3D Mascot Greeting
        item {
            CartoonMascotBanner(
                avatarKey = activeChildAvatarKey,
                title = "${strings.greetingHello}, $activeChildName! ✨",
                speechMessage = strings.mascotAlphabetGreeting,
                accentColor = CeruleanSky
            )
        }

        item {
            AgeDifficultySwitcherRow(
                difficulty = difficulty,
                onSelect = onSelectDifficulty
            )
        }

        val cols = if (isWideScreen) 2 else 1
        val rows = pillars.chunked(cols)
        items(rows.size) { rowIndex ->
            val rowItems = rows[rowIndex]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                rowItems.forEach { info ->
                    val (done, total) = progressFor(info.pillar)
                    Cartoon3DCard(
                        containerColor = info.color,
                        rimColor = info.rim,
                        cornerRadius = 28.dp,
                        bevelDepth = 7.dp,
                        contentPadding = PaddingValues(18.dp),
                        onClick = { onOpenPillar(info.pillar) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pillar_card_${info.pillar.key}")
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.28f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = info.emoji, fontSize = 30.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(Color.White.copy(alpha = 0.28f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "${done.toLocalizedDigits(language)}/${total.toLocalizedDigits(language)}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = info.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = info.subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.94f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            CartoonProgressBar(
                                progress = done.toFloat() / total.toFloat(),
                                fillColor = Color.White,
                                trackColor = Color.Black.copy(alpha = 0.16f),
                                height = 10.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PillarStudioExperience(
    pillar: LearningPillar,
    selectedModule: ModuleType,
    activeLessonId: String?,
    activeChildName: String,
    activeChildAvatarKey: String,
    difficulty: AgeDifficulty,
    lessons: List<LessonItem>,
    language: AppLanguage,
    isWideScreen: Boolean,
    onBackToHub: () -> Unit,
    onSelectDifficulty: (AgeDifficulty) -> Unit,
    onSelectModule: (ModuleType) -> Unit,
    onOpenActiveLesson: (String?) -> Unit,
    onToggleLanguage: () -> Unit,
    onSpeakLesson: (LessonItem) -> Unit,
    onSpeakText: (String) -> Unit,
    onSpeakCount: (Int) -> Unit,
    onPlayEffect: (SoundEffect) -> Unit,
    onMarkLearned: (LessonItem) -> Unit,
    onRecordQuizScore: (GameType, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val allowedModules = remember(pillar) { pillar.moduleTypes() }
    val activeModule = if (selectedModule in allowedModules) selectedModule else allowedModules.first()

    var mode by remember(pillar) { mutableStateOf(StudioMode.LESSONS) }
    var numberRangeIndex by remember(difficulty) { mutableIntStateOf(0) }
    var traceSelectedId by remember(activeModule) { mutableStateOf<String?>(null) }

    val allModuleLessons = remember(lessons, activeModule) {
        lessons.filter { it.moduleType == activeModule }
    }

    // For Numbers grid, filter by 1-20, 21-50, 51-100 pills (while full-screen stage navigates seamlessly across all 1-100!)
    val gridLessons = remember(allModuleLessons, pillar, numberRangeIndex) {
        if (pillar == LearningPillar.NUMBERS) {
            when (numberRangeIndex) {
                0 -> allModuleLessons.filter { it.countValue in 1..20 }
                1 -> allModuleLessons.filter { it.countValue in 21..50 }
                else -> allModuleLessons.filter { it.countValue in 51..100 }
            }
        } else {
            allModuleLessons
        }
    }

    val quizPool = remember(lessons, pillar, activeModule, difficulty) {
        when (pillar) {
            LearningPillar.ALPHABET -> lessons.filter { it.moduleType == activeModule }
            LearningPillar.WORLD -> lessons.filter { it.moduleType in LearningPillar.WORLD.moduleTypes() }
            LearningPillar.LANGUAGE -> lessons.filter { it.moduleType in LearningPillar.LANGUAGE.moduleTypes() }
            LearningPillar.NUMBERS -> lessons.filter { it.moduleType == ModuleType.NUMBERS && it.countValue <= difficulty.maxNumber }
        }
    }

    val activeFullLessonIndex = remember(allModuleLessons, activeLessonId) {
        if (activeLessonId == null) -1 else allModuleLessons.indexOfFirst { it.id == activeLessonId }
    }
    val activeFullLesson = allModuleLessons.getOrNull(activeFullLessonIndex)

    LaunchedEffect(allModuleLessons) {
        if (traceSelectedId == null || allModuleLessons.none { it.id == traceSelectedId }) {
            traceSelectedId = allModuleLessons.firstOrNull()?.id
        }
    }

    // Smooth Transition between Spacious Grid Hub and Full-Screen Active Lesson Stage!
    AnimatedContent(
        targetState = activeFullLesson != null && mode == StudioMode.LESSONS,
        transitionSpec = {
            (fadeIn(tween(240)) + scaleIn(initialScale = 0.94f, animationSpec = tween(240))) togetherWith
                fadeOut(tween(180))
        },
        label = "lesson_fullscreen_switch",
        modifier = modifier.fillMaxSize()
    ) { showingFullScreen ->
        if (showingFullScreen && activeFullLesson != null) {
            FullScreenActiveLessonStage(
                item = activeFullLesson,
                currentIndex = activeFullLessonIndex,
                totalCount = allModuleLessons.size,
                activeAvatarKey = activeChildAvatarKey,
                language = language,
                canGoPrev = activeFullLessonIndex > 0,
                canGoNext = activeFullLessonIndex < allModuleLessons.lastIndex,
                onBackToGrid = {
                    onPlayEffect(SoundEffect.TAP_POP)
                    onOpenActiveLesson(null)
                },
                onPrev = {
                    if (activeFullLessonIndex > 0) {
                        val prev = allModuleLessons[activeFullLessonIndex - 1]
                        onOpenActiveLesson(prev.id)
                        onSpeakLesson(prev)
                    }
                },
                onNext = {
                    if (activeFullLessonIndex < allModuleLessons.lastIndex) {
                        val next = allModuleLessons[activeFullLessonIndex + 1]
                        onOpenActiveLesson(next.id)
                        onSpeakLesson(next)
                    }
                },
                onToggleLanguage = onToggleLanguage,
                onSpeakLesson = onSpeakLesson,
                onSpeakCount = onSpeakCount,
                onPlayEffect = onPlayEffect,
                onMarkLearned = onMarkLearned
            )
        } else {
            val pillarTitle = when (pillar) {
                LearningPillar.ALPHABET -> strings.alphabetAdventureTitle
                LearningPillar.NUMBERS -> strings.pillarNumbersTitle
                LearningPillar.WORLD -> strings.pillarWorldTitle
                LearningPillar.LANGUAGE -> strings.pillarLanguageTitle
            }
            val pillarAccent = when (pillar) {
                LearningPillar.ALPHABET -> CeruleanSky
                LearningPillar.NUMBERS -> EmeraldMeadow
                LearningPillar.WORLD -> BerryCoral
                LearningPillar.LANGUAGE -> GrapePop
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp)
                    .testTag("learning_module_screen")
            ) {
                // TOP SECTION: Circular 3D Back Button + Section Title + Language Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
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
                            contentDescription = strings.backToModules,
                            containerColor = pillarAccent,
                            onClick = onBackToHub,
                            modifier = Modifier.testTag("back_to_modules_button")
                        )
                        Text(
                            text = pillarTitle,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    LanguageTogglePill(language = language, onToggle = onToggleLanguage)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Friendly Mascot Greeting Banner (matches active child's avatar!)
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    CartoonMascotBanner(
                        avatarKey = activeChildAvatarKey,
                        title = "${strings.greetingHello}, $activeChildName!",
                        speechMessage = if (pillar == LearningPillar.ALPHABET) {
                            strings.mascotAlphabetGreeting
                        } else {
                            strings.tapAnyCardToOpen
                        },
                        accentColor = pillarAccent
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3 Attractive 3D Learning Mode Cards (Learn / Trace / Quiz or 1-100 / Math Lab / Quiz)
                val modeCards: List<ModeCardSpec> = when (pillar) {
                    LearningPillar.ALPHABET -> listOf(
                        ModeCardSpec(StudioMode.LESSONS, strings.modeLearn, "📖", Icons.AutoMirrored.Rounded.MenuBook, CeruleanSky, CeruleanRim),
                        ModeCardSpec(StudioMode.TRACE, strings.modeTrace, "🖍️", Icons.Rounded.Draw, MangoPrimary, MangoRim),
                        ModeCardSpec(StudioMode.QUIZ, strings.modeQuiz, "🎯", Icons.Rounded.Quiz, GrapePop, GrapeRim)
                    )
                    LearningPillar.NUMBERS -> listOf(
                        ModeCardSpec(StudioMode.LESSONS, "1–100", "🔢", Icons.AutoMirrored.Rounded.MenuBook, EmeraldMeadow, EmeraldRim),
                        ModeCardSpec(StudioMode.MATH_LAB, strings.modeMathLab, "➕", Icons.Rounded.Calculate, MangoPrimary, MangoRim),
                        ModeCardSpec(StudioMode.QUIZ, strings.modeQuiz, "🏆", Icons.Rounded.Quiz, GrapePop, GrapeRim)
                    )
                    else -> listOf(
                        ModeCardSpec(StudioMode.LESSONS, strings.modeLearn, "🌟", Icons.AutoMirrored.Rounded.MenuBook, pillarAccent, pillarAccent.darkenForRim(0.72f)),
                        ModeCardSpec(StudioMode.QUIZ, strings.modeQuiz, "🎯", Icons.Rounded.Quiz, GrapePop, GrapeRim)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    modeCards.forEach { spec ->
                        val isSelected = mode == spec.mode
                        Cartoon3DCard(
                            containerColor = if (isSelected) spec.color else MaterialTheme.colorScheme.surface,
                            rimColor = if (isSelected) spec.rim else spec.color.copy(alpha = 0.35f),
                            cornerRadius = 22.dp,
                            bevelDepth = if (isSelected) 6.dp else 4.dp,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp),
                            onClick = {
                                onPlayEffect(SoundEffect.TAP_POP)
                                mode = spec.mode
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("studio_mode_${spec.mode.name.lowercase()}")
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = spec.emoji, fontSize = 24.sp)
                                Text(
                                    text = spec.label,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Contextual Sub-Deck Chips or Number Range / Difficulty Switcher
                if (pillar == LearningPillar.NUMBERS) {
                    if (mode == StudioMode.LESSONS) {
                        val rangeLabels = listOf(
                            "${1.toLocalizedDigits(language)}–${20.toLocalizedDigits(language)}",
                            "${21.toLocalizedDigits(language)}–${50.toLocalizedDigits(language)}",
                            "${51.toLocalizedDigits(language)}–${100.toLocalizedDigits(language)}"
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rangeLabels.forEachIndexed { idx, label ->
                                val active = numberRangeIndex == idx
                                Cartoon3DCard(
                                    containerColor = if (active) MangoPrimary else MaterialTheme.colorScheme.surface,
                                    rimColor = if (active) MangoRim else MaterialTheme.colorScheme.outline,
                                    cornerRadius = 16.dp,
                                    bevelDepth = 4.dp,
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                    onClick = {
                                        onPlayEffect(SoundEffect.TAP_POP)
                                        numberRangeIndex = idx
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("number_range_$idx")
                                ) {
                                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (active) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            AgeDifficultySwitcherRow(difficulty = difficulty, onSelect = onSelectDifficulty)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                } else if (mode != StudioMode.QUIZ || pillar == LearningPillar.ALPHABET) {
                    SubDeckChipRow(
                        modules = allowedModules,
                        activeModule = activeModule,
                        onSelect = onSelectModule
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Mode Body
                when (mode) {
                    StudioMode.TRACE -> {
                        val traceIdx = allModuleLessons.indexOfFirst { it.id == traceSelectedId }.coerceAtLeast(0)
                        val traceItem = allModuleLessons.getOrNull(traceIdx)
                        if (traceItem != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 112.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                LetterTracingStudio(
                                    item = traceItem,
                                    language = language,
                                    canGoPrev = traceIdx > 0,
                                    canGoNext = traceIdx < allModuleLessons.lastIndex,
                                    onPrev = {
                                        if (traceIdx > 0) {
                                            val prev = allModuleLessons[traceIdx - 1]
                                            traceSelectedId = prev.id
                                            onSpeakLesson(prev)
                                        }
                                    },
                                    onNext = {
                                        if (traceIdx < allModuleLessons.lastIndex) {
                                            val next = allModuleLessons[traceIdx + 1]
                                            traceSelectedId = next.id
                                            onSpeakLesson(next)
                                        }
                                    },
                                    onReplayAudio = { onSpeakLesson(traceItem) },
                                    onPlayEffect = onPlayEffect,
                                    onCompleteTrace = { onMarkLearned(traceItem) }
                                )
                            }
                        }
                    }

                    StudioMode.MATH_LAB -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 112.dp)
                        ) {
                            VisualMathLab(
                                difficulty = difficulty,
                                language = language,
                                isQuizMode = false,
                                onSpeakText = onSpeakText,
                                onPlayEffect = onPlayEffect,
                                onQuizCompleted = { score, stars ->
                                    onRecordQuizScore(GameType.MATH_QUIZ, score, stars)
                                }
                            )
                        }
                    }

                    StudioMode.QUIZ -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 112.dp)
                        ) {
                            if (pillar == LearningPillar.NUMBERS) {
                                VisualMathLab(
                                    difficulty = difficulty,
                                    language = language,
                                    isQuizMode = true,
                                    onSpeakText = onSpeakText,
                                    onPlayEffect = onPlayEffect,
                                    onQuizCompleted = { score, stars ->
                                        onRecordQuizScore(GameType.MATH_QUIZ, score, stars)
                                    }
                                )
                            } else {
                                val quizType = when (pillar) {
                                    LearningPillar.ALPHABET -> GameType.ALPHABET_QUIZ
                                    LearningPillar.WORLD -> GameType.WORLD_QUIZ
                                    LearningPillar.LANGUAGE -> GameType.LANGUAGE_QUIZ
                                    LearningPillar.NUMBERS -> GameType.MATH_QUIZ
                                }
                                ModuleQuizStage(
                                    pool = quizPool,
                                    language = language,
                                    onSpeakLesson = onSpeakLesson,
                                    onPlayEffect = onPlayEffect,
                                    onQuizCompleted = { score, stars ->
                                        onRecordQuizScore(quizType, score, stars)
                                    }
                                )
                            }
                        }
                    }

                    StudioMode.LESSONS -> {
                        if (gridLessons.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                KidzEmptyState(
                                    title = strings.emptyLessonsTitle,
                                    subtitle = strings.emptyLessonsBody
                                )
                            }
                        } else {
                            // Spacious 3-Column Colorful Letter/Number Grid (4-column on Tablets, 2-column for World/Language cards)
                            val cols = when {
                                isWideScreen -> if (activeModule == ModuleType.ALPHABET_EN || activeModule == ModuleType.ALPHABET_BN || activeModule == ModuleType.NUMBERS) 4 else 3
                                activeModule == ModuleType.ALPHABET_EN || activeModule == ModuleType.ALPHABET_BN || activeModule == ModuleType.NUMBERS -> 3
                                else -> 2
                            }
                            Spacious3DLessonGrid(
                                items = gridLessons,
                                language = language,
                                columns = cols,
                                onPick = { picked ->
                                    onOpenActiveLesson(picked.id)
                                    onSpeakLesson(picked)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class ModeCardSpec(
    val mode: StudioMode,
    val label: String,
    val emoji: String,
    val icon: ImageVector,
    val color: Color,
    val rim: Color
)

@Composable
private fun AgeDifficultySwitcherRow(
    difficulty: AgeDifficulty,
    onSelect: (AgeDifficulty) -> Unit
) {
    val strings = LocalAppStrings.current
    val items = listOf(
        Triple(AgeDifficulty.STARTER, strings.diffStarter, EmeraldMeadow),
        Triple(AgeDifficulty.EXPLORER, strings.diffExplorer, MangoPrimary),
        Triple(AgeDifficulty.SCHOLAR, strings.diffScholar, GrapePop)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("age_difficulty_switcher"),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { (level, label, color) ->
            val selected = level == difficulty
            Cartoon3DCard(
                containerColor = if (selected) color else MaterialTheme.colorScheme.surface,
                rimColor = if (selected) color.darkenForRim(0.75f) else MaterialTheme.colorScheme.outline,
                cornerRadius = 18.dp,
                bevelDepth = 4.dp,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 9.dp),
                onClick = { onSelect(level) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("difficulty_chip_${level.key}")
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun SubDeckChipRow(
    modules: List<ModuleType>,
    activeModule: ModuleType,
    onSelect: (ModuleType) -> Unit
) {
    val strings = LocalAppStrings.current
    val palette = listOf(CeruleanSky, MangoPrimary, EmeraldMeadow, BerryCoral, GrapePop, HoneyGold)
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(modules, key = { it.key }) { type ->
            val label = when (type) {
                ModuleType.ALPHABET_EN -> strings.tabEnglishAbc
                ModuleType.ALPHABET_BN -> strings.tabBanglaLetters
                ModuleType.NUMBERS -> strings.tabNumbers
                ModuleType.ANIMALS -> strings.tabAnimals
                ModuleType.FRUITS_VEGGIES -> strings.tabFruitsVeggies
                ModuleType.COLORS -> strings.tabColors
                ModuleType.SHAPES -> strings.tabShapes
                ModuleType.HUMAN_BODY -> strings.tabHumanBody
                ModuleType.VEHICLES -> strings.tabVehicles
                ModuleType.NATURE -> strings.tabNature
                ModuleType.BASIC_WORDS -> strings.tabBasicWords
                ModuleType.EVERYDAY_VOCAB -> strings.tabEverydayVocab
                ModuleType.SIMPLE_SENTENCES -> strings.tabSimpleSentences
                ModuleType.BANGLA_VOCAB -> strings.tabBanglaVocab
            }
            val color = palette[type.ordinal % palette.size]
            val selected = type == activeModule
            val bg by animateColorAsState(
                targetValue = if (selected) color else MaterialTheme.colorScheme.surface,
                label = "sub_deck_bg"
            )
            Cartoon3DCard(
                containerColor = bg,
                rimColor = if (selected) color.darkenForRim(0.72f) else color.copy(alpha = 0.35f),
                cornerRadius = 18.dp,
                bevelDepth = 4.dp,
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 9.dp),
                onClick = { onSelect(type) },
                modifier = Modifier.testTag("module_tab_${type.key}")
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
