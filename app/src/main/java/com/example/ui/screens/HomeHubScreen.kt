package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Pin
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.assets.LocalAssetRegistry
import com.example.core.localization.AppLanguage
import com.example.core.localization.LocalAppStrings
import com.example.core.localization.toLocalizedDigits
import com.example.domain.model.BanglaPoem
import com.example.domain.model.ChildProfile
import com.example.domain.model.DailyStreak
import com.example.domain.model.LearningPillar
import com.example.domain.model.LessonItem
import com.example.domain.model.ModuleProgress
import com.example.domain.model.ModuleType
import com.example.domain.model.Story
import com.example.ui.components.AnimatedDailyStreakBadge
import com.example.ui.components.Cartoon3DButton
import com.example.ui.components.Cartoon3DCard
import com.example.ui.components.Cartoon3DCircleIconButton
import com.example.ui.components.CartoonAvatarBadge
import com.example.ui.components.CartoonProgressBar
import com.example.ui.components.DailyStreakDialog
import com.example.ui.components.Floating3DBox
import com.example.ui.components.LanguageTogglePill
import com.example.ui.theme.BerryCoral
import com.example.ui.theme.BerryRim
import com.example.ui.theme.CeruleanRim
import com.example.ui.theme.CeruleanSky
import com.example.ui.theme.EmeraldMeadow
import com.example.ui.theme.EmeraldRim
import com.example.ui.theme.GrapePop
import com.example.ui.theme.GrapeRim
import com.example.ui.theme.HoneyGold
import com.example.ui.theme.HoneyRim
import com.example.ui.theme.MangoPrimary
import com.example.ui.theme.MangoRim
import com.example.ui.theme.darkenForRim
import com.example.ui.theme.lightenForGloss

private data class WorldTileData(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val icon: ImageVector,
    val color: Color,
    val rimColor: Color,
    val progressText: String,
    val progressFraction: Float,
    val onClick: () -> Unit
)

private data class HomeRewardMedallion(
    val emoji: String,
    val title: String,
    val unlocked: Boolean,
    val color: Color
)

@Composable
fun HomeHubScreen(
    activeChild: ChildProfile?,
    language: AppLanguage,
    dailyStreak: DailyStreak,
    moduleProgress: Map<ModuleType, ModuleProgress>,
    nextRecommendedLesson: LessonItem?,
    stories: List<Story>,
    poems: List<BanglaPoem>,
    gamesPlayedCount: Int,
    isWideScreen: Boolean,
    onToggleLanguage: () -> Unit,
    onOpenChildSwitcher: () -> Unit,
    onClaimDailyBonus: () -> Unit,
    onContinueLesson: (LessonItem) -> Unit,
    onStartDailyChallenge: () -> Unit,
    onNavigateToLearn: (ModuleType) -> Unit,
    onNavigateToPoems: () -> Unit,
    onNavigateToGames: () -> Unit,
    onNavigateToStories: () -> Unit,
    onOpenRewards: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var showStreakDialog by remember { mutableStateOf(false) }

    val alphaTypes = LearningPillar.ALPHABET.moduleTypes()
    val alphaDone = alphaTypes.sumOf { moduleProgress[it]?.completedCount ?: 0 }
    val alphaTotal = alphaTypes.sumOf { moduleProgress[it]?.totalCount ?: 0 }

    val numProg = moduleProgress[ModuleType.NUMBERS]
    val numDone = numProg?.completedCount ?: 0
    val numTotal = numProg?.totalCount ?: 100

    val expTypes = LearningPillar.WORLD.moduleTypes()
    val expDone = expTypes.sumOf { moduleProgress[it]?.completedCount ?: 0 }
    val expTotal = expTypes.sumOf { moduleProgress[it]?.totalCount ?: 0 }

    val langTypes = LearningPillar.LANGUAGE.moduleTypes()
    val langDone = langTypes.sumOf { moduleProgress[it]?.completedCount ?: 0 }
    val langTotal = langTypes.sumOf { moduleProgress[it]?.totalCount ?: 0 }

    val storiesDone = stories.count { it.isCompleted }
    val storiesTotal = stories.size.coerceAtLeast(1)

    val poemsDone = poems.count { it.isCompleted }
    val poemsTotal = poems.size.coerceAtLeast(1)

    val totalLessonsDone = alphaDone + numDone + expDone + langDone + storiesDone + poemsDone
    val totalLessonsAll = (alphaTotal + numTotal + expTotal + langTotal + storiesTotal + poemsTotal).coerceAtLeast(1)

    val worlds = listOf(
        WorldTileData(
            id = "world_alphabets",
            title = strings.alphabetAdventureTitle,
            subtitle = strings.pillarAlphabetSub,
            emoji = "🔤",
            icon = Icons.Rounded.Translate,
            color = CeruleanSky,
            rimColor = CeruleanRim,
            progressText = "${alphaDone.toLocalizedDigits(language)}/${alphaTotal.toLocalizedDigits(language)}",
            progressFraction = if (alphaTotal == 0) 0f else alphaDone.toFloat() / alphaTotal,
            onClick = {
                onNavigateToLearn(
                    if (language == AppLanguage.BANGLA) ModuleType.ALPHABET_BN else ModuleType.ALPHABET_EN
                )
            }
        ),
        WorldTileData(
            id = "world_numbers",
            title = strings.pillarNumbersTitle,
            subtitle = strings.pillarNumbersSub,
            emoji = "🔢",
            icon = Icons.Rounded.Pin,
            color = EmeraldMeadow,
            rimColor = EmeraldRim,
            progressText = "${numDone.toLocalizedDigits(language)}/${numTotal.toLocalizedDigits(language)}",
            progressFraction = if (numTotal == 0) 0f else numDone.toFloat() / numTotal,
            onClick = { onNavigateToLearn(ModuleType.NUMBERS) }
        ),
        WorldTileData(
            id = "world_explorer",
            title = strings.pillarWorldTitle,
            subtitle = strings.pillarWorldSub,
            emoji = "🌍",
            icon = Icons.Rounded.Palette,
            color = BerryCoral,
            rimColor = BerryRim,
            progressText = "${expDone.toLocalizedDigits(language)}/${expTotal.toLocalizedDigits(language)}",
            progressFraction = if (expTotal == 0) 0f else expDone.toFloat() / expTotal,
            onClick = { onNavigateToLearn(ModuleType.ANIMALS) }
        ),
        WorldTileData(
            id = "world_language",
            title = strings.pillarLanguageTitle,
            subtitle = strings.pillarLanguageSub,
            emoji = "💬",
            icon = Icons.Rounded.Translate,
            color = MangoPrimary,
            rimColor = MangoRim,
            progressText = "${langDone.toLocalizedDigits(language)}/${langTotal.toLocalizedDigits(language)}",
            progressFraction = if (langTotal == 0) 0f else langDone.toFloat() / langTotal,
            onClick = {
                onNavigateToLearn(
                    if (language == AppLanguage.BANGLA) ModuleType.BANGLA_VOCAB else ModuleType.BASIC_WORDS
                )
            }
        ),
        WorldTileData(
            id = "world_poems",
            title = strings.worldPoems,
            subtitle = strings.worldPoemsSub,
            emoji = "🌙",
            icon = Icons.Rounded.MusicNote,
            color = Color(0xFF6B5CE7),
            rimColor = Color(0xFF4737B5),
            progressText = "${poemsDone.toLocalizedDigits(language)}/${poemsTotal.toLocalizedDigits(language)}",
            progressFraction = poemsDone.toFloat() / poemsTotal,
            onClick = onNavigateToPoems
        ),
        WorldTileData(
            id = "world_games",
            title = strings.worldGames,
            subtitle = strings.worldGamesSub,
            emoji = "🎮",
            icon = Icons.Rounded.SportsEsports,
            color = GrapePop,
            rimColor = GrapeRim,
            progressText = "${gamesPlayedCount.toLocalizedDigits(language)} 🏆",
            progressFraction = (gamesPlayedCount / 5f).coerceIn(0.12f, 1f),
            onClick = onNavigateToGames
        ),
        WorldTileData(
            id = "world_stories",
            title = strings.worldStories,
            subtitle = strings.worldStoriesSub,
            emoji = "📖",
            icon = Icons.Rounded.AutoStories,
            color = HoneyGold,
            rimColor = HoneyRim,
            progressText = "${storiesDone.toLocalizedDigits(language)}/${storiesTotal.toLocalizedDigits(language)}",
            progressFraction = storiesDone.toFloat() / storiesTotal,
            onClick = onNavigateToStories
        )
    )

    val homeBadges = listOf(
        HomeRewardMedallion("🌟", strings.badgeRisingStar, (activeChild?.totalStars ?: 0) >= 10, HoneyGold),
        HomeRewardMedallion("🔤", strings.badgeAlphabetHero, alphaDone >= 3, CeruleanSky),
        HomeRewardMedallion("🌙", strings.badgePoemStar, poemsDone >= 1, Color(0xFF6B5CE7)),
        HomeRewardMedallion("🔢", strings.badgeNumberChamp, numDone >= 3, EmeraldMeadow),
        HomeRewardMedallion("📖", strings.badgeStoryLover, storiesDone >= 1, BerryCoral),
        HomeRewardMedallion("🏆", strings.badgeGameMaster, gamesPlayedCount > 0, GrapePop)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_hub_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 114.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header: Child Switcher Clay Pill + Instant Bilingual Switcher
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (activeChild != null) {
                    val pillShape = RoundedCornerShape(50)
                    Row(
                        modifier = Modifier
                            .testTag("child_switcher_button")
                            .shadow(
                                elevation = 8.dp,
                                shape = pillShape,
                                ambientColor = CeruleanSky.copy(alpha = 0.25f),
                                spotColor = CeruleanSky.copy(alpha = 0.30f)
                            )
                            .clip(pillShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(2.dp, Color.White.copy(alpha = 0.85f), pillShape)
                            .clickable(onClick = onOpenChildSwitcher)
                            .padding(start = 6.dp, end = 14.dp, top = 5.dp, bottom = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CartoonAvatarBadge(avatarKey = activeChild.avatarKey, size = 44.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = activeChild.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Icon(
                                    imageVector = Icons.Rounded.ExpandMore,
                                    contentDescription = strings.switchProfileTitle,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = strings.starsLabel,
                                    tint = HoneyGold,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${activeChild.totalStars.toLocalizedDigits(language)} ${strings.starsLabel}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MangoPrimary,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LanguageTogglePill(
                        language = language,
                        onToggle = onToggleLanguage
                    )
                    Cartoon3DCircleIconButton(
                        icon = Icons.Rounded.Person,
                        contentDescription = strings.navProfile,
                        containerColor = EmeraldMeadow,
                        size = 48.dp,
                        onClick = onOpenRewards,
                        modifier = Modifier.testTag("home_open_profile_button")
                    )
                }
            }
        }

        // Magical 3D Pixar Hero Island + Animated Profile-Matched Mascot Companion
        item {
            Cartoon3DCard(
                containerColor = MaterialTheme.colorScheme.surface,
                rimColor = CeruleanRim.copy(alpha = 0.45f),
                cornerRadius = 30.dp,
                bevelDepth = 7.dp,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (isWideScreen) 218.dp else 188.dp)
                    ) {
                        Image(
                            painter = painterResource(id = LocalAssetRegistry.heroIslandBanner),
                            contentDescription = strings.brandTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.06f),
                                            Color.Black.copy(alpha = 0.62f)
                                        )
                                    )
                                )
                        )

                        // Brand Pill Top Left
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(14.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0.94f))
                                .border(1.5.dp, Color.White, RoundedCornerShape(50))
                                .padding(horizontal = 12.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.brandTitle,
                                style = MaterialTheme.typography.labelLarge,
                                color = MangoPrimary,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${strings.developerTag}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF474463)
                            )
                        }

                        // Small Animated Daily Streak Badge Top Right
                        AnimatedDailyStreakBadge(
                            streak = dailyStreak,
                            language = language,
                            onClick = { showStreakDialog = true },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                        )

                        // Animated Friendly 3D Mascot Companion + Welcome Message Bottom
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Floating3DBox(floatAmplitudePx = 5f, durationMillis = 1900) {
                                CartoonAvatarBadge(
                                    avatarKey = activeChild?.avatarKey ?: "lion",
                                    size = 58.dp
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${strings.greetingHello}, ${activeChild?.name ?: "Explorer"}!",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = strings.readyToExplore,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.94f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Overall Mastery Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "🌟 ${strings.masteredLabel}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        CartoonProgressBar(
                            progress = totalLessonsDone.toFloat() / totalLessonsAll.toFloat(),
                            fillColor = EmeraldMeadow,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${totalLessonsDone.toLocalizedDigits(language)}/${totalLessonsAll.toLocalizedDigits(language)}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldMeadow
                        )
                    }
                }
            }
        }

        // Continue Learning + Daily Challenge Spotlight Row (Side-by-side on Tablet, Stacked on Phone)
        if (nextRecommendedLesson != null) {
            item {
                if (isWideScreen) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        ContinueLearning3DCard(
                            lesson = nextRecommendedLesson,
                            language = language,
                            onContinue = { onContinueLesson(nextRecommendedLesson) },
                            modifier = Modifier.weight(1f)
                        )
                        DailyChallenge3DCard(
                            onPlayChallenge = onStartDailyChallenge,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        ContinueLearning3DCard(
                            lesson = nextRecommendedLesson,
                            language = language,
                            onContinue = { onContinueLesson(nextRecommendedLesson) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DailyChallenge3DCard(
                            onPlayChallenge = onStartDailyChallenge,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        } else {
            item {
                DailyChallenge3DCard(
                    onPlayChallenge = onStartDailyChallenge,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Learning Worlds Section Heading
        item {
            Text(
                text = "✨ ${strings.worldsTitle}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        // Responsive 2-column (Phone) or 3-column (Tablet) Glossy 3D Learning Worlds Grid
        val columns = if (isWideScreen) 3 else 2
        val rows = worlds.chunked(columns)
        items(rows.size) { rowIndex ->
            val rowItems = rows[rowIndex]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { world ->
                    World3DCard(
                        world = world,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size < columns) {
                    repeat(columns - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Rewards & Achievements Shelf directly on Home
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🏅 ${strings.rewardsShelfTitle}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(CeruleanSky.copy(alpha = 0.14f))
                            .clickable(onClick = onOpenRewards)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("home_see_all_rewards")
                    ) {
                        Text(
                            text = strings.seeAllRewards,
                            style = MaterialTheme.typography.labelLarge,
                            color = CeruleanSky,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = strings.seeAllRewards,
                            tint = CeruleanSky,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(homeBadges) { badge ->
                        Cartoon3DCard(
                            containerColor = if (badge.unlocked) badge.color else MaterialTheme.colorScheme.surface,
                            rimColor = if (badge.unlocked) badge.color.darkenForRim(0.75f) else MaterialTheme.colorScheme.outline,
                            cornerRadius = 22.dp,
                            bevelDepth = 4.dp,
                            contentPadding = PaddingValues(12.dp),
                            onClick = onOpenRewards,
                            modifier = Modifier
                                .width(114.dp)
                                .alpha(if (badge.unlocked) 1f else 0.7f)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (badge.unlocked) Color.White.copy(alpha = 0.25f)
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = badge.emoji, fontSize = 26.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = badge.title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (badge.unlocked) Color.White else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showStreakDialog) {
        DailyStreakDialog(
            streak = dailyStreak,
            language = language,
            onDismiss = { showStreakDialog = false },
            onClaimBonus = onClaimDailyBonus,
            onStartLearning = {
                onNavigateToLearn(
                    if (language == AppLanguage.BANGLA) ModuleType.ALPHABET_BN else ModuleType.ALPHABET_EN
                )
            }
        )
    }
}

@Composable
private fun ContinueLearning3DCard(
    lesson: LessonItem,
    language: AppLanguage,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val accent = Color(lesson.accentColorHex)

    Cartoon3DCard(
        containerColor = MaterialTheme.colorScheme.surface,
        rimColor = accent.darkenForRim(0.82f),
        cornerRadius = 26.dp,
        bevelDepth = 6.dp,
        contentPadding = PaddingValues(14.dp),
        onClick = onContinue,
        modifier = modifier.testTag("home_continue_learning_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Big Glossy 3D Symbol Medallion
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(accent.lightenForGloss(0.24f), accent)
                        )
                    )
                    .border(2.5.dp, Color.White, RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                val symbolText = lesson.displaySymbol(language)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = symbolText,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 1
                    )
                    if (symbolText != lesson.visualBadge) {
                        Text(text = lesson.visualBadge, fontSize = 18.sp)
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = strings.continueLearningTitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = accent,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = lesson.displayTitle(language),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = lesson.displayExample(language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Cartoon3DButton(
                text = strings.continueLearningCta,
                icon = Icons.Rounded.PlayArrow,
                containerColor = accent,
                onClick = onContinue,
                modifier = Modifier.width(126.dp)
            )
        }
    }
}

@Composable
private fun DailyChallenge3DCard(
    onPlayChallenge: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    Cartoon3DCard(
        containerColor = BerryCoral,
        rimColor = BerryRim,
        cornerRadius = 26.dp,
        bevelDepth = 6.dp,
        contentPadding = PaddingValues(16.dp),
        onClick = onPlayChallenge,
        modifier = modifier.testTag("home_daily_challenge_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f))
                    .border(2.dp, Color.White.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎯", fontSize = 32.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Bolt,
                        contentDescription = null,
                        tint = HoneyGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = strings.dailyChallengeTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = strings.dailyChallengeSub,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.94f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Cartoon3DButton(
                text = strings.dailyChallengePlay,
                containerColor = HoneyGold,
                rimColor = HoneyRim,
                onClick = onPlayChallenge,
                modifier = Modifier.width(114.dp)
            )
        }
    }
}

@Composable
private fun World3DCard(
    world: WorldTileData,
    modifier: Modifier = Modifier
) {
    Cartoon3DCard(
        containerColor = world.color,
        rimColor = world.rimColor,
        cornerRadius = 26.dp,
        bevelDepth = 6.dp,
        contentPadding = PaddingValues(14.dp),
        onClick = world.onClick,
        modifier = modifier.testTag(world.id)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.28f))
                        .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = world.emoji, fontSize = 26.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.28f))
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = world.progressText,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = world.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = world.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.92f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            CartoonProgressBar(
                progress = world.progressFraction,
                fillColor = Color.White,
                trackColor = Color.Black.copy(alpha = 0.16f),
                height = 8.dp
            )
        }
    }
}
