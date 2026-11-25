package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.AppLanguage
import com.example.core.localization.LocalAppStrings
import com.example.core.localization.toLocalizedDigits
import com.example.domain.model.BanglaPoem
import com.example.domain.model.ChildProfile
import com.example.domain.model.GameScore
import com.example.domain.model.LearningPillar
import com.example.domain.model.ModuleProgress
import com.example.domain.model.ModuleType
import com.example.domain.model.Story
import com.example.ui.components.Cartoon3DCard
import com.example.ui.components.Cartoon3DCircleIconButton
import com.example.ui.components.CartoonAvatarBadge
import com.example.ui.components.CartoonProgressBar
import com.example.ui.components.Floating3DBox
import com.example.ui.components.LanguageTogglePill
import com.example.ui.theme.BerryCoral
import com.example.ui.theme.CeruleanRim
import com.example.ui.theme.CeruleanSky
import com.example.ui.theme.EmeraldMeadow
import com.example.ui.theme.GrapePop
import com.example.ui.theme.GrapeRim
import com.example.ui.theme.HoneyGold
import com.example.ui.theme.MangoPrimary
import com.example.ui.theme.MangoRim
import com.example.ui.theme.darkenForRim

private data class AchievementBadge(
    val emoji: String,
    val title: String,
    val unlocked: Boolean,
    val color: Color
)

@Composable
fun ProfileHubScreen(
    activeChild: ChildProfile?,
    profiles: List<ChildProfile>,
    language: AppLanguage,
    moduleProgress: Map<ModuleType, ModuleProgress>,
    stories: List<Story>,
    poems: List<BanglaPoem>,
    recentScores: List<GameScore>,
    onToggleLanguage: () -> Unit,
    onSelectChild: (Long) -> Unit,
    onAddChildClick: () -> Unit,
    onOpenLearnModule: (ModuleType) -> Unit,
    onOpenPoems: () -> Unit,
    onOpenStories: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenParentZone: () -> Unit,
    onOpenAboutApp: () -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    val alphaTypes = LearningPillar.ALPHABET.moduleTypes()
    val alphaDone = alphaTypes.sumOf { moduleProgress[it]?.completedCount ?: 0 }
    val alphaTotal = alphaTypes.sumOf { moduleProgress[it]?.totalCount ?: 0 }.coerceAtLeast(1)

    val numDone = moduleProgress[ModuleType.NUMBERS]?.completedCount ?: 0
    val numTotal = (moduleProgress[ModuleType.NUMBERS]?.totalCount ?: 100).coerceAtLeast(1)

    val explorerTypes = LearningPillar.WORLD.moduleTypes()
    val explorerDone = explorerTypes.sumOf { moduleProgress[it]?.completedCount ?: 0 }
    val explorerTotal = explorerTypes.sumOf { moduleProgress[it]?.totalCount ?: 0 }.coerceAtLeast(1)

    val langTypes = LearningPillar.LANGUAGE.moduleTypes()
    val langDone = langTypes.sumOf { moduleProgress[it]?.completedCount ?: 0 }
    val langTotal = langTypes.sumOf { moduleProgress[it]?.totalCount ?: 0 }.coerceAtLeast(1)

    val storiesDone = stories.count { it.isCompleted }
    val storiesTotal = stories.size.coerceAtLeast(1)
    val poemsDone = poems.count { it.isCompleted }
    val poemsTotal = poems.size.coerceAtLeast(1)

    val badges = listOf(
        AchievementBadge("🌟", strings.badgeRisingStar, (activeChild?.totalStars ?: 0) >= 10, HoneyGold),
        AchievementBadge("🎵", strings.badgePoemStar, poemsDone >= 1, Color(0xFF6B5CE7)),
        AchievementBadge("🔤", strings.badgeAlphabetHero, alphaDone >= 3, CeruleanSky),
        AchievementBadge("🔢", strings.badgeNumberChamp, numDone >= 3, EmeraldMeadow),
        AchievementBadge("📖", strings.badgeStoryLover, storiesDone >= 1, BerryCoral),
        AchievementBadge("🏆", strings.badgeGameMaster, recentScores.isNotEmpty(), GrapePop)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_hub_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 114.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (onBack != null) {
                        Cartoon3DCircleIconButton(
                            icon = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = strings.onboardingBack,
                            containerColor = CeruleanSky,
                            onClick = onBack,
                            modifier = Modifier.testTag("profile_back_button")
                        )
                    }
                    Text(
                        text = strings.profileHubTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                LanguageTogglePill(language = language, onToggle = onToggleLanguage)
            }
        }

        // Active Child 3D Hero Card
        if (activeChild != null) {
            item {
                Cartoon3DCard(
                    containerColor = CeruleanSky,
                    rimColor = CeruleanRim,
                    cornerRadius = 28.dp,
                    bevelDepth = 6.dp,
                    contentPadding = PaddingValues(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Floating3DBox(floatAmplitudePx = 5f) {
                            CartoonAvatarBadge(avatarKey = activeChild.avatarKey, size = 76.dp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activeChild.name,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                            Text(
                                text = "${activeChild.age.toLocalizedDigits(language)} ${strings.ageYearsSuffix}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.White.copy(alpha = 0.24f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = strings.starsLabel,
                                    tint = HoneyGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${activeChild.totalStars.toLocalizedDigits(language)} ${strings.starsLabel}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Child Profile Switcher Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(profiles, key = { it.id }) { profile ->
                    val isSelected = profile.id == activeChild?.id
                    Cartoon3DCard(
                        containerColor = if (isSelected) MangoPrimary else MaterialTheme.colorScheme.surface,
                        rimColor = if (isSelected) MangoRim else MaterialTheme.colorScheme.outline,
                        cornerRadius = 20.dp,
                        bevelDepth = 4.dp,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        onClick = { onSelectChild(profile.id) },
                        modifier = Modifier.testTag("profile_switch_${profile.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CartoonAvatarBadge(avatarKey = profile.avatarKey, size = 34.dp)
                            Text(
                                text = profile.name,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                item {
                    Cartoon3DCard(
                        containerColor = EmeraldMeadow,
                        rimColor = EmeraldMeadow.darkenForRim(0.75f),
                        cornerRadius = 20.dp,
                        bevelDepth = 4.dp,
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        onClick = onAddChildClick,
                        modifier = Modifier.testTag("profile_add_child_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PersonAdd,
                                contentDescription = strings.addChildButton,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = strings.addChildButton,
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Interactive Learning Progress Snapshot (each row opens its corresponding learning world!)
        item {
            Cartoon3DCard(
                containerColor = MaterialTheme.colorScheme.surface,
                rimColor = MaterialTheme.colorScheme.outline,
                cornerRadius = 24.dp,
                contentPadding = PaddingValues(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = strings.progressOverviewTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TappableMasteryRow(
                        label = strings.worldAlphabets,
                        done = alphaDone,
                        total = alphaTotal,
                        color = CeruleanSky,
                        language = language,
                        onClick = {
                            onOpenLearnModule(
                                if (language == AppLanguage.BANGLA) ModuleType.ALPHABET_BN else ModuleType.ALPHABET_EN
                            )
                        }
                    )
                    TappableMasteryRow(
                        label = strings.worldNumbers,
                        done = numDone,
                        total = numTotal,
                        color = EmeraldMeadow,
                        language = language,
                        onClick = { onOpenLearnModule(ModuleType.NUMBERS) }
                    )
                    TappableMasteryRow(
                        label = strings.pillarWorldTitle,
                        done = explorerDone,
                        total = explorerTotal,
                        color = BerryCoral,
                        language = language,
                        onClick = { onOpenLearnModule(ModuleType.ANIMALS) }
                    )
                    TappableMasteryRow(
                        label = strings.pillarLanguageTitle,
                        done = langDone,
                        total = langTotal,
                        color = GrapePop,
                        language = language,
                        onClick = {
                            onOpenLearnModule(
                                if (language == AppLanguage.BANGLA) ModuleType.BANGLA_VOCAB else ModuleType.BASIC_WORDS
                            )
                        }
                    )
                    TappableMasteryRow(
                        label = strings.worldPoems,
                        done = poemsDone,
                        total = poemsTotal,
                        color = Color(0xFF6B5CE7),
                        language = language,
                        onClick = onOpenPoems
                    )
                    TappableMasteryRow(
                        label = strings.worldStories,
                        done = storiesDone,
                        total = storiesTotal,
                        color = HoneyGold,
                        language = language,
                        onClick = onOpenStories
                    )
                }
            }
        }

        // Achievements Shelf
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "🏅 ${strings.achievementsTitle}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(badges) { badge ->
                        Cartoon3DCard(
                            containerColor = if (badge.unlocked) badge.color else MaterialTheme.colorScheme.surface,
                            rimColor = if (badge.unlocked) badge.color.darkenForRim(0.75f) else MaterialTheme.colorScheme.outline,
                            cornerRadius = 22.dp,
                            bevelDepth = 4.dp,
                            contentPadding = PaddingValues(12.dp),
                            modifier = Modifier
                                .width(114.dp)
                                .alpha(if (badge.unlocked) 1f else 0.65f)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = badge.emoji, fontSize = 30.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = badge.title,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (badge.unlocked) Color.White else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }

        // Parent Zone Entry Card (Math Gate)
        item {
            Cartoon3DCard(
                containerColor = GrapePop,
                rimColor = GrapeRim,
                cornerRadius = 24.dp,
                bevelDepth = 6.dp,
                contentPadding = PaddingValues(18.dp),
                onClick = onOpenParentZone,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_open_parent_zone")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Shield,
                                contentDescription = strings.openParentZoneTitle,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = strings.openParentZoneTitle,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Text(
                                text = strings.openParentZoneSub,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        // Settings Card (Theme Mode, UI Sound On/Off, Voice & About)
        item {
            Cartoon3DCard(
                containerColor = CeruleanSky,
                rimColor = CeruleanRim,
                cornerRadius = 24.dp,
                bevelDepth = 6.dp,
                contentPadding = PaddingValues(18.dp),
                onClick = onOpenSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_open_settings")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = strings.settingsTitle,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = strings.settingsTitle,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Text(
                                text = strings.settingsSubtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        // About KID'Z WORLD (Dev. E) Card
        item {
            Cartoon3DCard(
                containerColor = MangoPrimary,
                rimColor = MangoRim,
                cornerRadius = 24.dp,
                bevelDepth = 6.dp,
                contentPadding = PaddingValues(18.dp),
                onClick = onOpenAboutApp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_open_about_app")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = strings.aboutAppTitle,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = strings.aboutAppTitle,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Text(
                                text = strings.aboutAppSub,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }
    }
}

@Composable
private fun TappableMasteryRow(
    label: String,
    done: Int,
    total: Int,
    color: Color,
    language: AppLanguage,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${done.toLocalizedDigits(language)}/${total.toLocalizedDigits(language)}",
                style = MaterialTheme.typography.labelLarge,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        CartoonProgressBar(
            progress = done.toFloat() / total.toFloat(),
            fillColor = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            height = 10.dp
        )
    }
}
