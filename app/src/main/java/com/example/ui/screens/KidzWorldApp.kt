package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.core.audio.SoundEffect
import com.example.core.localization.AppLanguage
import com.example.core.localization.LocalAppStrings
import com.example.core.localization.toLocalizedDigits
import com.example.domain.model.AppSettings
import com.example.domain.model.BanglaPoem
import com.example.domain.model.ChildProfile
import com.example.domain.model.DailyStreak
import com.example.domain.model.GameScore
import com.example.domain.model.LearningPillar
import com.example.domain.model.LessonItem
import com.example.domain.model.ModuleProgress
import com.example.domain.model.ModuleType
import com.example.domain.model.Story
import com.example.domain.model.UiState
import com.example.domain.model.VoicePackStatus
import com.example.ui.components.Cartoon3DButton
import com.example.ui.components.CartoonAvatarBadge
import com.example.ui.components.CartoonBackground
import com.example.ui.components.Floating3DPillNavBar
import com.example.ui.components.FloatingNavItemData
import com.example.ui.components.KidzErrorBanner
import com.example.ui.components.StarBurstCelebration
import com.example.ui.theme.CeruleanSky
import com.example.ui.theme.EmeraldMeadow
import com.example.ui.theme.GrapePop
import com.example.ui.theme.HoneyGold
import com.example.ui.theme.KidzWorldTheme
import com.example.ui.theme.MangoPrimary
import com.example.ui.viewmodel.KidzWorldViewModel

const val ROUTE_HOME = "home"
const val ROUTE_LEARN = "learn"
const val ROUTE_POEMS = "poems"
const val ROUTE_GAMES = "games"
const val ROUTE_STORIES = "stories"
const val ROUTE_PROFILE = "profile"
const val ROUTE_PARENTS = "parents"
const val ROUTE_ABOUT = "about"

private enum class AppStage { SPLASH, ONBOARDING, MAIN }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KidzWorldApp(
    viewModel: KidzWorldViewModel,
    modifier: Modifier = Modifier
) {
    val initState by viewModel.initState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val profiles by viewModel.profiles.collectAsStateWithLifecycle()
    val activeChild by viewModel.activeChild.collectAsStateWithLifecycle()
    val lessons by viewModel.lessons.collectAsStateWithLifecycle()
    val nextRecommendedLesson by viewModel.nextRecommendedLesson.collectAsStateWithLifecycle()
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    val poems by viewModel.poems.collectAsStateWithLifecycle()
    val recentScores by viewModel.recentScores.collectAsStateWithLifecycle()
    val dailyStreak by viewModel.dailyStreak.collectAsStateWithLifecycle()
    val moduleProgress by viewModel.moduleProgressMap.collectAsStateWithLifecycle()
    val celebrationMessage by viewModel.celebrationMessage.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.isSpeaking.collectAsStateWithLifecycle()
    val activeUtteranceIndex by viewModel.activeUtteranceIndex.collectAsStateWithLifecycle()
    val voiceStatus by viewModel.voiceStatus.collectAsStateWithLifecycle()
    val showOnboardingReplay by viewModel.showOnboardingReplay.collectAsStateWithLifecycle()

    var splashCompleted by rememberSaveable { mutableStateOf(false) }

    KidzWorldTheme(
        darkTheme = settings.darkModeEnabled,
        language = settings.language
    ) {
        val strings = LocalAppStrings.current
        val stage = when {
            !splashCompleted -> AppStage.SPLASH
            showOnboardingReplay || !settings.onboardingCompleted -> AppStage.ONBOARDING
            else -> AppStage.MAIN
        }

        CartoonBackground(
            darkMode = settings.darkModeEnabled,
            modifier = modifier
        ) {
            when (val state = initState) {
                is UiState.Error -> KidzErrorBanner(
                    message = state.message,
                    onRetry = { viewModel.initializeWorld() },
                    modifier = Modifier.statusBarsPadding()
                )

                else -> {
                    AnimatedContent(
                        targetState = stage,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(320)) togetherWith
                                fadeOut(animationSpec = tween(240))
                        },
                        label = "app_stage_transition"
                    ) { currentStage ->
                        when (currentStage) {
                            AppStage.SPLASH -> SplashScreen(
                                isDataReady = state is UiState.Success,
                                onFinished = { splashCompleted = true }
                            )

                            AppStage.ONBOARDING -> OnboardingScreen(
                                language = settings.language,
                                activeChild = activeChild,
                                onSelectLanguage = { viewModel.setLanguage(it) },
                                onSpeakWelcome = { text, lang -> viewModel.speakText(text, lang) },
                                onPlayEffect = { viewModel.playEffect(it) },
                                onComplete = { name, age, avatarKey ->
                                    viewModel.completeOnboarding(name, age, avatarKey)
                                },
                                onSkip = { viewModel.skipOnboarding() }
                            )

                            AppStage.MAIN -> MainKidzWorldShell(
                                viewModel = viewModel,
                                settings = settings,
                                profiles = profiles,
                                activeChild = activeChild,
                                lessons = lessons,
                                nextRecommendedLesson = nextRecommendedLesson,
                                stories = stories,
                                poems = poems,
                                recentScores = recentScores,
                                dailyStreak = dailyStreak,
                                moduleProgress = moduleProgress,
                                celebrationMessage = celebrationMessage,
                                isSpeaking = isSpeaking,
                                activeUtteranceIndex = activeUtteranceIndex,
                                voiceStatus = voiceStatus,
                                strings = strings
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainKidzWorldShell(
    viewModel: KidzWorldViewModel,
    settings: AppSettings,
    profiles: List<ChildProfile>,
    activeChild: ChildProfile?,
    lessons: List<LessonItem>,
    nextRecommendedLesson: LessonItem?,
    stories: List<Story>,
    poems: List<BanglaPoem>,
    recentScores: List<GameScore>,
    dailyStreak: DailyStreak,
    moduleProgress: Map<ModuleType, ModuleProgress>,
    celebrationMessage: String?,
    isSpeaking: Boolean,
    activeUtteranceIndex: Int?,
    voiceStatus: VoicePackStatus,
    strings: com.example.core.localization.AppStrings
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: ROUTE_HOME

    var selectedLearnModule by remember {
        mutableStateOf(
            if (settings.language == AppLanguage.BANGLA) ModuleType.ALPHABET_BN else ModuleType.ALPHABET_EN
        )
    }
    var activeLearnPillar by remember { mutableStateOf<LearningPillar?>(null) }
    var activeLearnLessonId by remember { mutableStateOf<String?>(null) }
    var autoStartDailyChallengeQuiz by remember { mutableStateOf(false) }
    var showChildSheet by remember { mutableStateOf(false) }
    var showQuickAddChild by remember { mutableStateOf(false) }

    val phoneDestinations = listOf(
        FloatingNavItemData(ROUTE_HOME, strings.navHome, Icons.Rounded.Home, MangoPrimary),
        FloatingNavItemData(ROUTE_LEARN, strings.navLearn, Icons.Rounded.School, CeruleanSky),
        FloatingNavItemData(ROUTE_POEMS, strings.navPoems, Icons.Rounded.MusicNote, Color(0xFF6B5CE7)),
        FloatingNavItemData(ROUTE_GAMES, strings.navGames, Icons.Rounded.SportsEsports, GrapePop),
        FloatingNavItemData(ROUTE_STORIES, strings.navStories, Icons.Rounded.AutoStories, HoneyGold)
    )

    val railDestinations = phoneDestinations + FloatingNavItemData(
        ROUTE_PROFILE,
        strings.navProfile,
        Icons.Rounded.Person,
        EmeraldMeadow
    )

    fun isTabSelected(tabRoute: String): Boolean {
        if (currentRoute == tabRoute) return true
        return tabRoute == ROUTE_PROFILE && (currentRoute == ROUTE_PARENTS || currentRoute == ROUTE_ABOUT)
    }

    fun navigateToTab(route: String, resetLearnHub: Boolean = false) {
        viewModel.stopSpeaking()
        viewModel.playEffect(SoundEffect.TAP_POP)
        if (resetLearnHub && route == ROUTE_LEARN) {
            activeLearnPillar = null
            activeLearnLessonId = null
        } else if (route != ROUTE_LEARN) {
            activeLearnLessonId = null
        }
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToSubRoute(route: String) {
        viewModel.stopSpeaking()
        viewModel.playEffect(SoundEffect.TAP_POP)
        navController.navigate(route) {
            launchSingleTop = true
        }
    }

    val isFullScreenActiveLesson = currentRoute == ROUTE_LEARN && activeLearnLessonId != null

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 700.dp

        if (isWideScreen) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                if (!isFullScreenActiveLesson) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 10.dp, horizontal = 8.dp)
                            .clip(RoundedCornerShape(28.dp))
                    ) {
                        Spacer(modifier = Modifier.height(14.dp))
                        railDestinations.forEach { dest ->
                            val selected = isTabSelected(dest.route)
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateToTab(dest.route, resetLearnHub = dest.route == ROUTE_LEARN) },
                                icon = {
                                    Icon(
                                        imageVector = dest.icon,
                                        contentDescription = dest.label
                                    )
                                },
                                label = { Text(dest.label, maxLines = 1) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = dest.accent,
                                    indicatorColor = dest.accent
                                ),
                                modifier = Modifier.testTag("nav_rail_${dest.route}")
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    KidzNavHost(
                        navController = navController,
                        viewModel = viewModel,
                        activeLearnPillar = activeLearnPillar,
                        onOpenLearnPillar = {
                            activeLearnPillar = it
                            if (it == null) activeLearnLessonId = null
                        },
                        selectedLearnModule = selectedLearnModule,
                        onSelectLearnModule = { selectedLearnModule = it },
                        activeLearnLessonId = activeLearnLessonId,
                        onOpenActiveLesson = { activeLearnLessonId = it },
                        autoStartDailyChallengeQuiz = autoStartDailyChallengeQuiz,
                        onStartDailyChallenge = {
                            autoStartDailyChallengeQuiz = true
                            navigateToTab(ROUTE_GAMES)
                        },
                        onConsumedDailyChallenge = { autoStartDailyChallengeQuiz = false },
                        isWideScreen = true,
                        activeChild = activeChild,
                        profiles = profiles,
                        settings = settings,
                        lessons = lessons,
                        nextRecommendedLesson = nextRecommendedLesson,
                        stories = stories,
                        poems = poems,
                        recentScores = recentScores,
                        dailyStreak = dailyStreak,
                        moduleProgress = moduleProgress,
                        isSpeaking = isSpeaking,
                        activeUtteranceIndex = activeUtteranceIndex,
                        voiceStatus = voiceStatus,
                        onOpenChildSwitcher = { showChildSheet = true },
                        onOpenAddChild = { showQuickAddChild = true },
                        onNavigateToTab = { navigateToTab(it) },
                        onNavigateToSubRoute = { navigateToSubRoute(it) },
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 1100.dp)
                    )
                }
            }
        } else {
            // Phone Shell: Full-bleed edge-to-edge world + Floating Glossy 3D Pill Dock
            Box(modifier = Modifier.fillMaxSize()) {
                KidzNavHost(
                    navController = navController,
                    viewModel = viewModel,
                    activeLearnPillar = activeLearnPillar,
                    onOpenLearnPillar = {
                        activeLearnPillar = it
                        if (it == null) activeLearnLessonId = null
                    },
                    selectedLearnModule = selectedLearnModule,
                    onSelectLearnModule = { selectedLearnModule = it },
                    activeLearnLessonId = activeLearnLessonId,
                    onOpenActiveLesson = { activeLearnLessonId = it },
                    autoStartDailyChallengeQuiz = autoStartDailyChallengeQuiz,
                    onStartDailyChallenge = {
                        autoStartDailyChallengeQuiz = true
                        navigateToTab(ROUTE_GAMES)
                    },
                    onConsumedDailyChallenge = { autoStartDailyChallengeQuiz = false },
                    isWideScreen = false,
                    activeChild = activeChild,
                    profiles = profiles,
                    settings = settings,
                    lessons = lessons,
                    nextRecommendedLesson = nextRecommendedLesson,
                    stories = stories,
                    poems = poems,
                    recentScores = recentScores,
                    dailyStreak = dailyStreak,
                    moduleProgress = moduleProgress,
                    isSpeaking = isSpeaking,
                    activeUtteranceIndex = activeUtteranceIndex,
                    voiceStatus = voiceStatus,
                    onOpenChildSwitcher = { showChildSheet = true },
                    onOpenAddChild = { showQuickAddChild = true },
                    onNavigateToTab = { navigateToTab(it) },
                    onNavigateToSubRoute = { navigateToSubRoute(it) },
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .then(if (isFullScreenActiveLesson) Modifier.navigationBarsPadding() else Modifier)
                )

                // Floating Glossy 3D Pill Bottom Navigation Dock (auto-hides during full-screen active lesson stage)
                AnimatedVisibility(
                    visible = !isFullScreenActiveLesson,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(tween(220)),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(tween(180)),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Floating3DPillNavBar(
                        items = phoneDestinations,
                        isSelected = { isTabSelected(it) },
                        onSelect = { route ->
                            navigateToTab(route, resetLearnHub = route == ROUTE_LEARN)
                        }
                    )
                }
            }
        }

        // Global Star Burst Celebration Toast Overlay
        StarBurstCelebration(
            message = celebrationMessage,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 14.dp)
                .widthIn(max = 420.dp)
        )
    }

    if (showChildSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showChildSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            ChildSwitcherSheetContent(
                profiles = profiles,
                activeChildId = activeChild?.id,
                language = settings.language,
                onSelect = { childId ->
                    viewModel.selectChild(childId)
                    showChildSheet = false
                },
                onAddChildClick = {
                    showChildSheet = false
                    showQuickAddChild = true
                }
            )
        }
    }

    if (showQuickAddChild) {
        AddChildProfileDialog(
            language = settings.language,
            onDismiss = { showQuickAddChild = false },
            onSave = { name, age, avatarKey ->
                viewModel.addChild(name, age, avatarKey)
                showQuickAddChild = false
            }
        )
    }
}

@Composable
private fun KidzNavHost(
    navController: NavHostController,
    viewModel: KidzWorldViewModel,
    activeLearnPillar: LearningPillar?,
    onOpenLearnPillar: (LearningPillar?) -> Unit,
    selectedLearnModule: ModuleType,
    onSelectLearnModule: (ModuleType) -> Unit,
    activeLearnLessonId: String?,
    onOpenActiveLesson: (String?) -> Unit,
    autoStartDailyChallengeQuiz: Boolean,
    onStartDailyChallenge: () -> Unit,
    onConsumedDailyChallenge: () -> Unit,
    isWideScreen: Boolean,
    activeChild: ChildProfile?,
    profiles: List<ChildProfile>,
    settings: AppSettings,
    lessons: List<LessonItem>,
    nextRecommendedLesson: LessonItem?,
    stories: List<Story>,
    poems: List<BanglaPoem>,
    recentScores: List<GameScore>,
    dailyStreak: DailyStreak,
    moduleProgress: Map<ModuleType, ModuleProgress>,
    isSpeaking: Boolean,
    activeUtteranceIndex: Int?,
    voiceStatus: VoicePackStatus,
    onOpenChildSwitcher: () -> Unit,
    onOpenAddChild: () -> Unit,
    onNavigateToTab: (String) -> Unit,
    onNavigateToSubRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = ROUTE_HOME,
        enterTransition = { fadeIn(animationSpec = tween(220)) + slideInHorizontally(tween(220)) { it / 12 } },
        exitTransition = { fadeOut(animationSpec = tween(180)) },
        popEnterTransition = { fadeIn(animationSpec = tween(220)) + slideInHorizontally(tween(220)) { -it / 12 } },
        popExitTransition = { fadeOut(animationSpec = tween(180)) + slideOutHorizontally(tween(180)) { it / 12 } },
        modifier = modifier
    ) {
        composable(ROUTE_HOME) {
            HomeHubScreen(
                activeChild = activeChild,
                language = settings.language,
                dailyStreak = dailyStreak,
                moduleProgress = moduleProgress,
                nextRecommendedLesson = nextRecommendedLesson,
                stories = stories,
                poems = poems,
                gamesPlayedCount = recentScores.size,
                isWideScreen = isWideScreen,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onOpenChildSwitcher = onOpenChildSwitcher,
                onClaimDailyBonus = { viewModel.claimDailyStreakBonus() },
                onContinueLesson = { lesson ->
                    onSelectLearnModule(lesson.moduleType)
                    onOpenLearnPillar(LearningPillar.forModule(lesson.moduleType))
                    onOpenActiveLesson(lesson.id)
                    onNavigateToTab(ROUTE_LEARN)
                },
                onStartDailyChallenge = onStartDailyChallenge,
                onNavigateToLearn = { module ->
                    onSelectLearnModule(module)
                    onOpenLearnPillar(LearningPillar.forModule(module))
                    onOpenActiveLesson(null)
                    onNavigateToTab(ROUTE_LEARN)
                },
                onNavigateToPoems = { onNavigateToTab(ROUTE_POEMS) },
                onNavigateToGames = { onNavigateToTab(ROUTE_GAMES) },
                onNavigateToStories = { onNavigateToTab(ROUTE_STORIES) },
                onOpenRewards = { onNavigateToSubRoute(ROUTE_PROFILE) }
            )
        }

        composable(ROUTE_LEARN) {
            LearningModuleScreen(
                activePillar = activeLearnPillar,
                selectedModule = selectedLearnModule,
                activeLessonId = activeLearnLessonId,
                activeChildName = activeChild?.name ?: "Explorer",
                activeChildAvatarKey = activeChild?.avatarKey ?: "lion",
                activeChildAge = activeChild?.age ?: 5,
                lessons = lessons,
                moduleProgress = moduleProgress,
                language = settings.language,
                isWideScreen = isWideScreen,
                onOpenPillar = onOpenLearnPillar,
                onSelectModule = onSelectLearnModule,
                onOpenActiveLesson = onOpenActiveLesson,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onSpeakLesson = { viewModel.speakLesson(it) },
                onSpeakText = { viewModel.speakText(it) },
                onSpeakCount = { number ->
                    viewModel.speakText(
                        text = number.toLocalizedDigits(settings.language),
                        language = settings.language
                    )
                },
                onPlayEffect = { viewModel.playEffect(it) },
                onMarkLearned = { viewModel.markLessonLearned(it) },
                onRecordQuizScore = { type, score, stars ->
                    viewModel.recordGameResult(type, score, stars)
                }
            )
        }

        composable(ROUTE_POEMS) {
            BanglaPoemsScreen(
                poems = poems,
                language = settings.language,
                isSpeaking = isSpeaking,
                activeUtteranceIndex = activeUtteranceIndex,
                voiceStatus = voiceStatus,
                isWideScreen = isWideScreen,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onRecitePoem = { poem, slow -> viewModel.recitePoem(poem, slow) },
                onReciteStanza = { text, slow -> viewModel.recitePoemStanza(text, slow) },
                onStopSpeaking = { viewModel.stopSpeaking() },
                onPlayEffect = { viewModel.playEffect(it) },
                onCompletePoem = { viewModel.completePoem(it) },
                onInstallBanglaPack = { viewModel.openBanglaVoicePackInstaller(context) },
                onRefreshVoices = { viewModel.refreshVoices() }
            )
        }

        composable(ROUTE_GAMES) {
            GamesArcadeScreen(
                lessons = lessons,
                recentScores = recentScores,
                language = settings.language,
                isWideScreen = isWideScreen,
                autoStartQuiz = autoStartDailyChallengeQuiz,
                onConsumedAutoStartQuiz = onConsumedDailyChallenge,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onSpeakText = { viewModel.speakText(it) },
                onPlayEffect = { viewModel.playEffect(it) },
                onRecordGameScore = { type, score, stars ->
                    viewModel.recordGameResult(type, score, stars)
                }
            )
        }

        composable(ROUTE_STORIES) {
            StoriesScreen(
                stories = stories,
                language = settings.language,
                isSpeaking = isSpeaking,
                isWideScreen = isWideScreen,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onSpeakText = { viewModel.speakText(it) },
                onStopSpeaking = { viewModel.stopSpeaking() },
                onPlayEffect = { viewModel.playEffect(it) },
                onCompleteStory = { viewModel.completeStory(it) }
            )
        }

        composable(ROUTE_PROFILE) {
            ProfileHubScreen(
                activeChild = activeChild,
                profiles = profiles,
                language = settings.language,
                moduleProgress = moduleProgress,
                stories = stories,
                poems = poems,
                recentScores = recentScores,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onSelectChild = { viewModel.selectChild(it) },
                onAddChildClick = onOpenAddChild,
                onOpenLearnModule = { module ->
                    onSelectLearnModule(module)
                    onOpenLearnPillar(LearningPillar.forModule(module))
                    onOpenActiveLesson(null)
                    onNavigateToTab(ROUTE_LEARN)
                },
                onOpenPoems = { onNavigateToTab(ROUTE_POEMS) },
                onOpenStories = { onNavigateToTab(ROUTE_STORIES) },
                onOpenParentZone = { onNavigateToSubRoute(ROUTE_PARENTS) },
                onOpenAboutApp = { onNavigateToSubRoute(ROUTE_ABOUT) },
                onBack = if (!isWideScreen) ({ navController.popBackStack() }) else null
            )
        }

        composable(ROUTE_PARENTS) {
            ParentZoneScreen(
                settings = settings,
                profiles = profiles,
                activeChild = activeChild,
                moduleProgress = moduleProgress,
                stories = stories,
                poems = poems,
                voiceStatus = voiceStatus,
                onSelectChild = { viewModel.selectChild(it) },
                onAddChild = { name, age, avatar -> viewModel.addChild(name, age, avatar) },
                onDeleteChild = { viewModel.deleteChild(it) },
                onToggleLanguage = { viewModel.toggleLanguage() },
                onSetSoundFx = { viewModel.setSoundFxEnabled(it) },
                onSetTts = { viewModel.setTtsEnabled(it) },
                onSetSpeechRate = { viewModel.setSpeechRate(it) },
                onSetDarkMode = { viewModel.setDarkModeEnabled(it) },
                onInstallBanglaPack = { viewModel.openBanglaVoicePackInstaller(context) },
                onRefreshVoices = { viewModel.refreshVoices() },
                onTestVoice = { text, lang -> viewModel.speakText(text, lang) },
                onResetProgress = { viewModel.resetActiveChildProgress() },
                onBack = { navController.popBackStack() },
                onOpenAboutApp = { onNavigateToSubRoute(ROUTE_ABOUT) }
            )
        }

        composable(ROUTE_ABOUT) {
            AboutAppScreen(
                language = settings.language,
                onBack = { navController.popBackStack() },
                onToggleLanguage = { viewModel.toggleLanguage() },
                onReplayOnboarding = { viewModel.replayOnboarding() },
                onOpenParentZone = { onNavigateToSubRoute(ROUTE_PARENTS) }
            )
        }
    }
}

@Composable
private fun ChildSwitcherSheetContent(
    profiles: List<ChildProfile>,
    activeChildId: Long?,
    language: AppLanguage,
    onSelect: (Long) -> Unit,
    onAddChildClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = strings.switchProfileTitle,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        profiles.forEach { profile ->
            val selected = profile.id == activeChildId
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (selected) CeruleanSky.copy(alpha = 0.16f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                    .clickable { onSelect(profile.id) }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CartoonAvatarBadge(avatarKey = profile.avatarKey, size = 48.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${profile.age.toLocalizedDigits(language)} ${strings.ageYearsSuffix} • ${profile.totalStars.toLocalizedDigits(language)} ${strings.starsLabel}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (selected) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = strings.activeLabel,
                        tint = HoneyGold,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Cartoon3DButton(
            text = strings.addChildButton,
            icon = Icons.Rounded.PersonAdd,
            containerColor = EmeraldMeadow,
            onClick = onAddChildClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sheet_add_child_button")
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}
