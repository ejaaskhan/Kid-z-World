package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StopCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.assets.LocalAssetRegistry
import com.example.core.audio.SoundEffect
import com.example.core.localization.AppLanguage
import com.example.core.localization.LocalAppStrings
import com.example.core.localization.toLocalizedDigits
import com.example.domain.model.Story
import com.example.ui.components.Cartoon3DButton
import com.example.ui.components.Cartoon3DCard
import com.example.ui.components.Cartoon3DCircleIconButton
import com.example.ui.components.KidzEmptyState
import com.example.ui.components.LanguageTogglePill
import com.example.ui.theme.CeruleanSky
import com.example.ui.theme.EmeraldMeadow
import com.example.ui.theme.GrapePop
import com.example.ui.theme.HoneyGold
import com.example.ui.theme.MangoPrimary
import com.example.ui.theme.darkenForRim

@Composable
fun StoriesScreen(
    stories: List<Story>,
    language: AppLanguage,
    isSpeaking: Boolean,
    isWideScreen: Boolean,
    onToggleLanguage: () -> Unit,
    onSpeakStoryPage: (storyId: String, pageNumber: Int, fallbackEnglishText: String) -> Unit,
    onStopSpeaking: () -> Unit,
    onPlayEffect: (SoundEffect) -> Unit,
    onCompleteStory: (Story) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeStoryId by remember { mutableStateOf<String?>(null) }
    val activeStory = remember(stories, activeStoryId) {
        stories.firstOrNull { it.id == activeStoryId }
    }

    BackHandler(enabled = activeStory != null) {
        onStopSpeaking()
        activeStoryId = null
    }

    DisposableEffect(activeStoryId) {
        onDispose { onStopSpeaking() }
    }

    if (activeStory == null) {
        StoryShelfView(
            stories = stories,
            language = language,
            isWideScreen = isWideScreen,
            onToggleLanguage = onToggleLanguage,
            onOpenStory = { story ->
                onPlayEffect(SoundEffect.PAGE_TURN)
                activeStoryId = story.id
            },
            modifier = modifier
        )
    } else {
        StoryReaderView(
            story = activeStory,
            language = language,
            isSpeaking = isSpeaking,
            isWideScreen = isWideScreen,
            onToggleLanguage = {
                onStopSpeaking()
                onToggleLanguage()
            },
            onSpeakStoryPage = onSpeakStoryPage,
            onStopSpeaking = onStopSpeaking,
            onPlayEffect = onPlayEffect,
            onComplete = {
                onStopSpeaking()
                onCompleteStory(activeStory)
                activeStoryId = null
            },
            onBack = {
                onStopSpeaking()
                activeStoryId = null
            },
            modifier = modifier
        )
    }
}

@Composable
private fun StoryShelfView(
    stories: List<Story>,
    language: AppLanguage,
    isWideScreen: Boolean,
    onToggleLanguage: () -> Unit,
    onOpenStory: (Story) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("stories_shelf_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 114.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.storyShelfTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = strings.storyShelfSub,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                LanguageTogglePill(language = language, onToggle = onToggleLanguage)
            }
        }

        if (stories.isEmpty()) {
            item {
                KidzEmptyState(
                    title = strings.storyShelfTitle,
                    subtitle = strings.loadingWorld,
                    emoji = "📖"
                )
            }
        } else if (isWideScreen) {
            val rows = stories.chunked(2)
            items(rows.size) { rowIdx ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    rows[rowIdx].forEach { story ->
                        StoryCoverCard(
                            story = story,
                            language = language,
                            onClick = { onOpenStory(story) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rows[rowIdx].size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        } else {
            items(stories, key = { it.id }) { story ->
                StoryCoverCard(
                    story = story,
                    language = language,
                    onClick = { onOpenStory(story) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun StoryCoverCard(
    story: Story,
    language: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val accent = Color(story.accentColorHex)

    Cartoon3DCard(
        containerColor = MaterialTheme.colorScheme.surface,
        rimColor = accent.darkenForRim(0.8f),
        cornerRadius = 26.dp,
        bevelDepth = 6.dp,
        contentPadding = PaddingValues(0.dp),
        onClick = onClick,
        modifier = modifier.testTag("story_card_${story.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
            ) {
                Image(
                    painter = painterResource(id = LocalAssetRegistry.storyIllustration(story.coverAssetKey)),
                    contentDescription = story.title(language),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.92f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${story.ageTag} ${strings.ageYearsSuffix}",
                            style = MaterialTheme.typography.labelMedium,
                            color = accent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (story.isCompleted) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(EmeraldMeadow)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = strings.completedStory,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = strings.completedStory,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }
                }
                Text(
                    text = story.title(language),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = story.subtitle(language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${story.pages.size.toLocalizedDigits(language)} ${strings.pageIndicator}",
                    style = MaterialTheme.typography.labelLarge,
                    color = accent
                )
            }
        }
    }
}

@Composable
private fun StoryReaderView(
    story: Story,
    language: AppLanguage,
    isSpeaking: Boolean,
    isWideScreen: Boolean,
    onToggleLanguage: () -> Unit,
    onSpeakStoryPage: (storyId: String, pageNumber: Int, fallbackEnglishText: String) -> Unit,
    onStopSpeaking: () -> Unit,
    onPlayEffect: (SoundEffect) -> Unit,
    onComplete: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val pages = story.pages
    var pageIdx by remember(story.id) { mutableIntStateOf(0) }
    val currentPage = pages.getOrNull(pageIdx)
    val isLastPage = pageIdx >= pages.lastIndex
    val accent = Color(story.accentColorHex)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 104.dp)
            .testTag("story_reader_screen")
    ) {
        // Top Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Cartoon3DCircleIconButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = strings.previousPage,
                containerColor = CeruleanSky,
                onClick = onBack
            )
            Text(
                text = "${strings.pageIndicator} ${(pageIdx + 1).toLocalizedDigits(language)}/${pages.size.coerceAtLeast(1).toLocalizedDigits(language)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            LanguageTogglePill(language = language, onToggle = onToggleLanguage)
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (currentPage != null) {
            val contentModifier = Modifier.weight(1f)
            if (isWideScreen) {
                Row(
                    modifier = contentModifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Cartoon3DCard(
                        containerColor = MaterialTheme.colorScheme.surface,
                        rimColor = accent.darkenForRim(0.8f),
                        cornerRadius = 26.dp,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Image(
                            painter = painterResource(id = LocalAssetRegistry.storyIllustration(currentPage.sceneAssetKey)),
                            contentDescription = story.title(language),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StoryTextSection(
                            story = story,
                            pageText = currentPage.text(language),
                            isLastPage = isLastPage,
                            isSpeaking = isSpeaking,
                            language = language,
                            accent = accent,
                            onReadAloud = {
                                if (isSpeaking) {
                                    onStopSpeaking()
                                } else {
                                    onSpeakStoryPage(story.id, currentPage.pageNumber, currentPage.textEn)
                                }
                            }
                        )
                    }
                }
            } else {
                Column(
                    modifier = contentModifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Cartoon3DCard(
                        containerColor = MaterialTheme.colorScheme.surface,
                        rimColor = accent.darkenForRim(0.8f),
                        cornerRadius = 26.dp,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    ) {
                        Image(
                            painter = painterResource(id = LocalAssetRegistry.storyIllustration(currentPage.sceneAssetKey)),
                            contentDescription = story.title(language),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    StoryTextSection(
                        story = story,
                        pageText = currentPage.text(language),
                        isLastPage = isLastPage,
                        isSpeaking = isSpeaking,
                        language = language,
                        accent = accent,
                        onReadAloud = {
                            if (isSpeaking) {
                                onStopSpeaking()
                            } else {
                                onSpeakStoryPage(story.id, currentPage.pageNumber, currentPage.textEn)
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Page Dots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pages.size) { idx ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (idx == pageIdx) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (idx == pageIdx) accent else MaterialTheme.colorScheme.outline)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bottom Paging Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Cartoon3DButton(
                text = strings.previousPage,
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                containerColor = GrapePop,
                enabled = pageIdx > 0,
                onClick = {
                    onStopSpeaking()
                    onPlayEffect(SoundEffect.PAGE_TURN)
                    pageIdx = (pageIdx - 1).coerceAtLeast(0)
                },
                modifier = Modifier.weight(1f)
            )
            Cartoon3DButton(
                text = if (isLastPage) strings.finishStory else strings.nextPage,
                icon = if (isLastPage) Icons.Rounded.Star else Icons.AutoMirrored.Rounded.ArrowForward,
                containerColor = if (isLastPage) EmeraldMeadow else MangoPrimary,
                onClick = {
                    onStopSpeaking()
                    if (isLastPage) {
                        onComplete()
                    } else {
                        onPlayEffect(SoundEffect.PAGE_TURN)
                        pageIdx = (pageIdx + 1).coerceAtMost(pages.lastIndex)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("story_next_or_finish_button")
            )
        }
    }
}

@Composable
private fun StoryTextSection(
    story: Story,
    pageText: String,
    isLastPage: Boolean,
    isSpeaking: Boolean,
    language: AppLanguage,
    accent: Color,
    onReadAloud: () -> Unit
) {
    val strings = LocalAppStrings.current
    Cartoon3DCard(
        containerColor = MaterialTheme.colorScheme.surface,
        rimColor = MaterialTheme.colorScheme.outline,
        cornerRadius = 24.dp,
        contentPadding = PaddingValues(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = story.title(language),
                style = MaterialTheme.typography.titleLarge,
                color = accent
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = pageText,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, lineHeight = 28.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(14.dp))
            Cartoon3DButton(
                text = if (isSpeaking) strings.stopReading else strings.readAloud,
                icon = if (isSpeaking) Icons.Rounded.StopCircle else Icons.AutoMirrored.Rounded.VolumeUp,
                containerColor = if (isSpeaking) Color(0xFFFF4D5A) else CeruleanSky,
                onClick = onReadAloud,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("story_read_aloud_button")
            )
        }
    }

    if (isLastPage) {
        Cartoon3DCard(
            containerColor = HoneyGold,
            rimColor = HoneyGold.darkenForRim(0.75f),
            cornerRadius = 22.dp,
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "✨ ${strings.moralLesson}",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = story.moral(language),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}
