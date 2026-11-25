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
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.FolderOpen
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
import com.example.domain.model.LearnFolder
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
    activeFolder: LearnFolder?,
    selectedModule: ModuleType,
    activeLessonId: String?,
    activeChildName: String,
    activeChildAvatarKey: String,
    activeChildAge: Int,
    lessons: List<LessonItem>,
    moduleProgress: Map<ModuleType, ModuleProgress>,
    language: AppLanguage,
    isWideScreen: Boolean,
    onOpenFolder: (LearnFolder?) -> Unit,
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

    BackHandler(enabled = activeLessonId != null || activeFolder != null) {
        if (activeLessonId != null) {
            onOpenActiveLesson(null)
        } else {
            onOpenFolder(null)
        }
    }

    if (activeFolder == null) {
        LearnFiveFolderHub(
            activeChildName = activeChildName,
            activeChildAvatarKey = activeChildAvatarKey,
            lessons = lessons,
            language = language,
            isWideScreen = isWideScreen,
            onToggleLanguage = onToggleLanguage,
            onOpenFolder = { folder ->
                onPlayEffect(SoundEffect.TAP_POP)
                onOpenActiveLesson(null)
                onSelectModule(folder.defaultModule())
                onOpenFolder(folder)
            },
            modifier = modifier
        )
    } else {
        FolderStudioExperience(
            folder = activeFolder,
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
                onOpenFolder(null)
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

private data class FolderCardInfo(
    val folder: LearnFolder,
    val badgeText: String,
    val title: String,
    val subtitle: String,
    val color: Color,
    val rim: Color
)

@Composable
private fun LearnFiveFolderHub(
    activeChildName: String,
    activeChildAvatarKey: String,
    lessons: List<LessonItem>,
    language: AppLanguage,
    isWideScreen: Boolean,
    onToggleLanguage: () -> Unit,
    onOpenFolder: (LearnFolder) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val isBn = language == AppLanguage.BANGLA

    fun progressFor(folder: LearnFolder): Pair<Int, Int> {
        val items = folder.filterLessons(lessons)
        val done = items.count { it.isLearned }
        val total = items.size.coerceAtLeast(1)
        return done to total
    }

    val folders = listOf(
        FolderCardInfo(
            folder = LearnFolder.VOWELS,
            badgeText = "অ",
            title = if (isBn) "স্বরবর্ণ (Vowels)" else "Vowels (স্বরবর্ণ)",
            subtitle = if (isBn) "অ থেকে ঔ • ১১টি স্বরবর্ণ, উচ্চারণ ও হাতের লেখা" else "অ – ঔ • 11 Bangla Vowels, Audio & Tracing",
            color = MangoPrimary,
            rim = MangoRim
        ),
        FolderCardInfo(
            folder = LearnFolder.CONSONANTS,
            badgeText = "ক",
            title = if (isBn) "ব্যঞ্জনবর্ণ (Consonants)" else "Consonants (ব্যঞ্জনবর্ণ)",
            subtitle = if (isBn) "ক থেকে ঁ • ৩৯টি ব্যঞ্জনবর্ণ, উচ্চারণ ও হাতের লেখা" else "ক – ঁ • 39 Bangla Consonants, Audio & Tracing",
            color = BerryCoral,
            rim = BerryRim
        ),
        FolderCardInfo(
            folder = LearnFolder.ENGLISH_ALPHABET,
            badgeText = "ABC",
            title = if (isBn) "ইংরেজি বর্ণমালা (English Alphabet)" else "English Alphabet",
            subtitle = if (isBn) "A থেকে Z • ২৬টি ইংরেজি বর্ণ, ধ্বনি ও লেখা" else "A – Z • 26 Letters, Human Phonics & Tracing",
            color = CeruleanSky,
            rim = CeruleanRim
        ),
        FolderCardInfo(
            folder = LearnFolder.SENTENCES,
            badgeText = "💬",
            title = if (isBn) "বাক্য শিখি (Sentences)" else "Sentences",
            subtitle = if (isBn) "সহজ বাক্য, প্রতিদিনের শব্দ ও বাংলা-ইংরেজি কথা" else "Simple Sentences, Basic Words & Everyday Talk",
            color = GrapePop,
            rim = GrapeRim
        ),
        FolderCardInfo(
            folder = LearnFolder.BASIC_KNOWLEDGE,
            badgeText = "🌍",
            title = if (isBn) "সাধারণ জ্ঞান (Basic Knowledge)" else "Basic Knowledge",
            subtitle = if (isBn) "সংখ্যা ১–১০০, রঙ, আকার, প্রাণী, ফল, শরীর, যানবাহন ও প্রকৃতি" else "Numbers 1–100, Colors, Shapes, Animals, Fruits & More",
            color = EmeraldMeadow,
            rim = EmeraldRim
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("learn_module_hub"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                speechMessage = if (isBn) {
                    "যেকোনো ফোল্ডারে ট্যাপ করে মজার পড়া শুরু করো!"
                } else {
                    "Pick any learning folder below to explore!"
                },
                accentColor = CeruleanSky
            )
        }

        // Clean 5-Folder Learning List
        val cols = if (isWideScreen) 2 else 1
        val rows = folders.chunked(cols)
        items(rows.size) { rowIndex ->
            val rowItems = rows[rowIndex]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { info ->
                    val (done, total) = progressFor(info.folder)
                    LearnFolderListCard(
                        info = info,
                        done = done,
                        total = total,
                        language = language,
                        onClick = { onOpenFolder(info.folder) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (cols == 2 && rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun LearnFolderListCard(
    info: FolderCardInfo,
    done: Int,
    total: Int,
    language: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Cartoon3DCard(
        containerColor = info.color,
        rimColor = info.rim,
        cornerRadius = 26.dp,
        bevelDepth = 7.dp,
        contentPadding = PaddingValues(16.dp),
        onClick = onClick,
        modifier = modifier.testTag("learn_folder_${info.folder.key}")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Glossy 3D Folder Badge Emblem
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.24f))
                        .border(1.5.dp, Color.White.copy(alpha = 0.55f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = info.badgeText,
                        fontSize = when {
                            info.badgeText.length >= 3 -> 19.sp
                            info.badgeText.length == 2 -> 24.sp
                            else -> 28.sp
                        },
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FolderOpen,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.92f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = info.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = info.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.94f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Count pill + Chevron
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.25f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${done.toLocalizedDigits(language)}/${total.toLocalizedDigits(language)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = info.title,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            CartoonProgressBar(
                progress = done.toFloat() / total.toFloat(),
                fillColor = Color.White,
                trackColor = Color.Black.copy(alpha = 0.16f),
                height = 8.dp
            )
        }
    }
}

@Composable
private fun FolderStudioExperience(
    folder: LearnFolder,
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
    val isBn = language == AppLanguage.BANGLA
    val allowedModules = remember(folder) { folder.moduleTypes() }
    val activeModule = if (selectedModule in allowedModules) selectedModule else folder.defaultModule()

    var mode by remember(folder) { mutableStateOf(StudioMode.LESSONS) }
    var numberRangeIndex by remember(difficulty) { mutableIntStateOf(0) }
    var traceSelectedId by remember(folder, activeModule) { mutableStateOf<String?>(null) }

    val isAlphabetFolder = folder == LearnFolder.VOWELS ||
        folder == LearnFolder.CONSONANTS ||
        folder == LearnFolder.ENGLISH_ALPHABET
    val isNumbersActive = folder == LearnFolder.BASIC_KNOWLEDGE && activeModule == ModuleType.NUMBERS

    LaunchedEffect(folder, activeModule) {
        if (mode == StudioMode.TRACE && !isAlphabetFolder) {
            mode = StudioMode.LESSONS
        }
        if (mode == StudioMode.MATH_LAB && !isNumbersActive) {
            mode = StudioMode.LESSONS
        }
    }

    val activeLessonSequence = remember(lessons, folder, activeModule) {
        when (folder) {
            LearnFolder.VOWELS -> lessons.filter { it.isBanglaVowel }
            LearnFolder.CONSONANTS -> lessons.filter { it.isBanglaConsonant }
            LearnFolder.ENGLISH_ALPHABET -> lessons.filter { it.moduleType == ModuleType.ALPHABET_EN }
            LearnFolder.SENTENCES,
            LearnFolder.BASIC_KNOWLEDGE -> lessons.filter { it.moduleType == activeModule }
        }
    }

    // For Numbers inside Basic Knowledge, split cleanly into 1–20, 21–50, 51–100 pills
    val gridLessons = remember(activeLessonSequence, isNumbersActive, numberRangeIndex) {
        if (isNumbersActive) {
            when (numberRangeIndex) {
                0 -> activeLessonSequence.filter { it.countValue in 1..20 }
                1 -> activeLessonSequence.filter { it.countValue in 21..50 }
                else -> activeLessonSequence.filter { it.countValue in 51..100 }
            }
        } else {
            activeLessonSequence
        }
    }

    val quizPool = remember(lessons, folder, activeModule, activeLessonSequence, difficulty) {
        when (folder) {
            LearnFolder.VOWELS,
            LearnFolder.CONSONANTS,
            LearnFolder.ENGLISH_ALPHABET -> activeLessonSequence
            LearnFolder.SENTENCES -> folder.filterLessons(lessons)
            LearnFolder.BASIC_KNOWLEDGE -> if (activeModule == ModuleType.NUMBERS) {
                lessons.filter { it.moduleType == ModuleType.NUMBERS && it.countValue <= difficulty.maxNumber }
            } else {
                lessons.filter { it.moduleType in folder.moduleTypes() && it.moduleType != ModuleType.NUMBERS }
            }
        }
    }

    val activeFullLessonIndex = remember(activeLessonSequence, activeLessonId) {
        if (activeLessonId == null) -1 else activeLessonSequence.indexOfFirst { it.id == activeLessonId }
    }
    val activeFullLesson = activeLessonSequence.getOrNull(activeFullLessonIndex)

    LaunchedEffect(activeLessonSequence) {
        if (traceSelectedId == null || activeLessonSequence.none { it.id == traceSelectedId }) {
            traceSelectedId = activeLessonSequence.firstOrNull()?.id
        }
    }

    AnimatedContent(
        targetState = activeFullLesson != null,
        transitionSpec = {
            (fadeIn(tween(240)) + scaleIn(tween(240), initialScale = 0.96f)) togetherWith
                fadeOut(tween(180))
        },
        label = "folder_lesson_stage_transition",
        modifier = modifier.fillMaxSize()
    ) { showingFullStage ->
        if (showingFullStage && activeFullLesson != null) {
            FullScreenActiveLessonStage(
                item = activeFullLesson,
                currentIndex = activeFullLessonIndex,
                totalCount = activeLessonSequence.size,
                activeAvatarKey = activeChildAvatarKey,
                language = language,
                canGoPrev = activeFullLessonIndex > 0,
                canGoNext = activeFullLessonIndex < activeLessonSequence.lastIndex,
                onBackToGrid = {
                    onPlayEffect(SoundEffect.TAP_POP)
                    onOpenActiveLesson(null)
                },
                onPrev = {
                    if (activeFullLessonIndex > 0) {
                        val prev = activeLessonSequence[activeFullLessonIndex - 1]
                        onPlayEffect(SoundEffect.PAGE_TURN)
                        onOpenActiveLesson(prev.id)
                        onSpeakLesson(prev)
                    }
                },
                onNext = {
                    if (activeFullLessonIndex < activeLessonSequence.lastIndex) {
                        val next = activeLessonSequence[activeFullLessonIndex + 1]
                        onPlayEffect(SoundEffect.PAGE_TURN)
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
            val folderTitle = when (folder) {
                LearnFolder.VOWELS -> if (isBn) "স্বরবর্ণ" else "Vowels"
                LearnFolder.CONSONANTS -> if (isBn) "ব্যঞ্জনবর্ণ" else "Consonants"
                LearnFolder.ENGLISH_ALPHABET -> if (isBn) "ইংরেজি বর্ণমালা" else "English Alphabet"
                LearnFolder.SENTENCES -> if (isBn) "বাক্য শিখি" else "Sentences"
                LearnFolder.BASIC_KNOWLEDGE -> if (isBn) "সাধারণ জ্ঞান" else "Basic Knowledge"
            }
            val folderAccent = when (folder) {
                LearnFolder.VOWELS -> MangoPrimary
                LearnFolder.CONSONANTS -> BerryCoral
                LearnFolder.ENGLISH_ALPHABET -> CeruleanSky
                LearnFolder.SENTENCES -> GrapePop
                LearnFolder.BASIC_KNOWLEDGE -> EmeraldMeadow
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp)
                    .testTag("learning_module_screen")
            ) {
                // TOP BAR: Back to 5-Folder List + Folder Title + Language Switcher
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
                            containerColor = folderAccent,
                            onClick = onBackToHub,
                            modifier = Modifier.testTag("back_to_modules_button")
                        )
                        Text(
                            text = folderTitle,
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

                // Mode Cards (Learn / Trace / Quiz for alphabet folders; Learn / Math Lab / Quiz for Numbers; Learn / Quiz for others)
                val modeCards: List<ModeCardSpec> = when {
                    isAlphabetFolder -> listOf(
                        ModeCardSpec(StudioMode.LESSONS, strings.modeLearn, "📖", Icons.AutoMirrored.Rounded.MenuBook, folderAccent, folderAccent.darkenForRim(0.72f)),
                        ModeCardSpec(StudioMode.TRACE, strings.modeTrace, "🖍️", Icons.Rounded.Draw, CeruleanSky, CeruleanRim),
                        ModeCardSpec(StudioMode.QUIZ, strings.modeQuiz, "🎯", Icons.Rounded.Quiz, GrapePop, GrapeRim)
                    )
                    isNumbersActive -> listOf(
                        ModeCardSpec(StudioMode.LESSONS, "1–100", "🔢", Icons.AutoMirrored.Rounded.MenuBook, EmeraldMeadow, EmeraldRim),
                        ModeCardSpec(StudioMode.MATH_LAB, strings.modeMathLab, "➕", Icons.Rounded.Calculate, MangoPrimary, MangoRim),
                        ModeCardSpec(StudioMode.QUIZ, strings.modeQuiz, "🏆", Icons.Rounded.Quiz, GrapePop, GrapeRim)
                    )
                    else -> listOf(
                        ModeCardSpec(StudioMode.LESSONS, strings.modeLearn, "🌟", Icons.AutoMirrored.Rounded.MenuBook, folderAccent, folderAccent.darkenForRim(0.72f)),
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
                            cornerRadius = 20.dp,
                            bevelDepth = if (isSelected) 6.dp else 4.dp,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
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
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(text = spec.emoji, fontSize = 22.sp)
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

                // Topic Sub-Deck Chips ONLY for multi-topic folders (Sentences & Basic Knowledge)
                if (allowedModules.size > 1 && mode != StudioMode.QUIZ) {
                    SubDeckChipRow(
                        modules = allowedModules,
                        activeModule = activeModule,
                        onSelect = onSelectModule
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Number Range Pills / Age Difficulty ONLY when Numbers 1–100 is active inside Basic Knowledge
                if (isNumbersActive) {
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
                }

                // Folder Content Body
                when (mode) {
                    StudioMode.TRACE -> {
                        val traceIdx = activeLessonSequence.indexOfFirst { it.id == traceSelectedId }.coerceAtLeast(0)
                        val traceItem = activeLessonSequence.getOrNull(traceIdx)
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
                                    canGoNext = traceIdx < activeLessonSequence.lastIndex,
                                    onPrev = {
                                        if (traceIdx > 0) {
                                            val prev = activeLessonSequence[traceIdx - 1]
                                            traceSelectedId = prev.id
                                            onSpeakLesson(prev)
                                        }
                                    },
                                    onNext = {
                                        if (traceIdx < activeLessonSequence.lastIndex) {
                                            val next = activeLessonSequence[traceIdx + 1]
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
                            if (isNumbersActive) {
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
                                val quizType = when (folder) {
                                    LearnFolder.VOWELS,
                                    LearnFolder.CONSONANTS,
                                    LearnFolder.ENGLISH_ALPHABET -> GameType.ALPHABET_QUIZ
                                    LearnFolder.SENTENCES -> GameType.LANGUAGE_QUIZ
                                    LearnFolder.BASIC_KNOWLEDGE -> GameType.WORLD_QUIZ
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
                            val cols = when {
                                isWideScreen -> if (isAlphabetFolder || isNumbersActive) 4 else 3
                                isAlphabetFolder || isNumbersActive -> 3
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
