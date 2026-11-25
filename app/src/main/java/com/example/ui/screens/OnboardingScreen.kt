package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.assets.LocalAssetRegistry
import com.example.core.audio.SoundEffect
import com.example.core.localization.AppLanguage
import com.example.core.localization.LocalAppStrings
import com.example.core.localization.toLocalizedDigits
import com.example.domain.model.ChildProfile
import com.example.ui.components.Cartoon3DButton
import com.example.ui.components.Cartoon3DCard
import com.example.ui.components.CartoonAvatarBadge
import com.example.ui.components.Floating3DBox
import com.example.ui.theme.BerryCoral
import com.example.ui.theme.CeruleanRim
import com.example.ui.theme.CeruleanSky
import com.example.ui.theme.EmeraldMeadow
import com.example.ui.theme.EmeraldRim
import com.example.ui.theme.GrapePop
import com.example.ui.theme.HoneyGold
import com.example.ui.theme.MangoPrimary
import com.example.ui.theme.MangoRim
import com.example.ui.theme.darkenForRim

@Composable
fun OnboardingScreen(
    language: AppLanguage,
    activeChild: ChildProfile?,
    onSelectLanguage: (AppLanguage) -> Unit,
    onSpeakWelcome: (String, AppLanguage) -> Unit,
    onPlayEffect: (SoundEffect) -> Unit,
    onComplete: (childName: String, age: Int, avatarKey: String) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var step by remember { mutableIntStateOf(0) } // 0 = Language, 1 = Tour, 2 = Child Setup
    var childName by remember(activeChild?.id) { mutableStateOf(activeChild?.name ?: "") }
    var childAge by remember(activeChild?.id) { mutableIntStateOf(activeChild?.age ?: 5) }
    var selectedAvatar by remember(activeChild?.id) {
        mutableStateOf(activeChild?.avatarKey ?: LocalAssetRegistry.avatars.first().key)
    }

    BackHandler(enabled = step > 0) {
        onPlayEffect(SoundEffect.PAGE_TURN)
        step -= 1
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .testTag("onboarding_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 620.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Brand pill + Step Progress Dots + Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) { idx ->
                        val active = idx == step
                        Box(
                            modifier = Modifier
                                .height(10.dp)
                                .width(if (active) 32.dp else 10.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (active) MangoPrimary
                                    else if (idx < step) EmeraldMeadow
                                    else MaterialTheme.colorScheme.outline
                                )
                        )
                    }
                }
                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.testTag("onboarding_skip_button")
                ) {
                    Text(
                        text = strings.onboardingSkip,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Animated Step Body
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    val forward = targetState > initialState
                    if (forward) {
                        (slideInHorizontally { it / 2 } + fadeIn()) togetherWith
                            (slideOutHorizontally { -it / 2 } + fadeOut())
                    } else {
                        (slideInHorizontally { -it / 2 } + fadeIn()) togetherWith
                            (slideOutHorizontally { it / 2 } + fadeOut())
                    }.using(SizeTransform(clip = false))
                },
                label = "onboarding_step",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { currentStep ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (currentStep) {
                        0 -> OnboardingLanguageStep(
                            selectedLanguage = language,
                            onChoose = { lang ->
                                onSelectLanguage(lang)
                                val greeting = if (lang == AppLanguage.BANGLA) {
                                    "স্বাগতম কিড'জ ওয়ার্ল্ডে!"
                                } else {
                                    "Welcome to Kid'z World! Learn, play, and grow!"
                                }
                                onSpeakWelcome(greeting, lang)
                            }
                        )

                        1 -> OnboardingFeatureTourStep()

                        else -> OnboardingChildSetupStep(
                            language = language,
                            name = childName,
                            age = childAge,
                            avatarKey = selectedAvatar,
                            onNameChange = { childName = it.take(18) },
                            onAgeChange = {
                                onPlayEffect(SoundEffect.TAP_POP)
                                childAge = it
                            },
                            onAvatarChange = {
                                onPlayEffect(SoundEffect.TAP_POP)
                                selectedAvatar = it
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Bottom Navigation Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (step > 0) {
                    Cartoon3DButton(
                        text = strings.onboardingBack,
                        icon = Icons.AutoMirrored.Rounded.ArrowBack,
                        containerColor = GrapePop,
                        onClick = {
                            onPlayEffect(SoundEffect.PAGE_TURN)
                            step -= 1
                        },
                        modifier = Modifier
                            .weight(0.9f)
                            .testTag("onboarding_back_button")
                    )
                }

                Cartoon3DButton(
                    text = if (step == 2) strings.onboardingStartAdventure else strings.onboardingNext,
                    icon = if (step == 2) Icons.Rounded.RocketLaunch else Icons.AutoMirrored.Rounded.ArrowForward,
                    containerColor = if (step == 2) EmeraldMeadow else MangoPrimary,
                    onClick = {
                        if (step < 2) {
                            onPlayEffect(SoundEffect.PAGE_TURN)
                            step += 1
                        } else {
                            onComplete(childName, childAge, selectedAvatar)
                        }
                    },
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("onboarding_next_button")
                )
            }
        }
    }
}

@Composable
private fun OnboardingLanguageStep(
    selectedLanguage: AppLanguage,
    onChoose: (AppLanguage) -> Unit
) {
    val strings = LocalAppStrings.current

    Floating3DBox(floatAmplitudePx = 8f) {
        Box(
            modifier = Modifier
                .size(116.dp)
                .clip(CircleShape)
                .background(CeruleanSky)
                .border(4.dp, Color.White, CircleShape)
        ) {
            Image(
                painter = painterResource(id = LocalAssetRegistry.appCrestArtwork),
                contentDescription = strings.brandTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    Text(
        text = strings.onboardingStep1Title,
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
    )
    Text(
        text = strings.onboardingStep1Sub,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )

    // Giant Tactile 3D Language Cards (Bangla first as default)
    val options = listOf(
        Triple(AppLanguage.BANGLA, "বাংলা (Bangla)", "অ–ঔ • ছড়া • সংখ্যা • গল্প") to (EmeraldMeadow to EmeraldRim),
        Triple(AppLanguage.ENGLISH, "English", "A–Z • Poems • Numbers • Stories") to (CeruleanSky to CeruleanRim)
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        options.forEach { (triple, colors) ->
            val (lang, title, subtitle) = triple
            val (face, rim) = colors
            val isSelected = lang == selectedLanguage
            Cartoon3DCard(
                containerColor = if (isSelected) face else MaterialTheme.colorScheme.surface,
                rimColor = if (isSelected) rim else MaterialTheme.colorScheme.outline,
                cornerRadius = 26.dp,
                bevelDepth = 6.dp,
                contentPadding = PaddingValues(18.dp),
                onClick = { onChoose(lang) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_lang_${lang.code}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    if (isSelected) Color.White.copy(alpha = 0.24f)
                                    else face.copy(alpha = 0.14f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (lang == AppLanguage.ENGLISH) "ABC" else "অআ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) Color.White else face
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) Color.White.copy(alpha = 0.9f)
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingFeatureTourStep() {
    val strings = LocalAppStrings.current

    Cartoon3DCard(
        containerColor = MaterialTheme.colorScheme.surface,
        rimColor = MangoRim,
        cornerRadius = 28.dp,
        bevelDepth = 6.dp,
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
    ) {
        Image(
            painter = painterResource(id = LocalAssetRegistry.heroIslandBanner),
            contentDescription = strings.onboardingStep2Title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }

    Text(
        text = strings.onboardingStep2Title,
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
    )
    Text(
        text = strings.onboardingStep2Sub,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )

    val highlights = listOf(
        Triple("🔤", strings.onboardingTourFeature1, CeruleanSky),
        Triple("🔢", strings.onboardingTourFeature2, EmeraldMeadow),
        Triple("🎮", strings.onboardingTourFeature3, GrapePop),
        Triple("📖", strings.onboardingTourFeature4, HoneyGold)
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        highlights.forEach { (emoji, label, accent) ->
            Cartoon3DCard(
                containerColor = MaterialTheme.colorScheme.surface,
                rimColor = accent.darkenForRim(0.82f),
                cornerRadius = 20.dp,
                bevelDepth = 4.dp,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(accent.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 22.sp)
                    }
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingChildSetupStep(
    language: AppLanguage,
    name: String,
    age: Int,
    avatarKey: String,
    onNameChange: (String) -> Unit,
    onAgeChange: (Int) -> Unit,
    onAvatarChange: (String) -> Unit
) {
    val strings = LocalAppStrings.current

    Floating3DBox(floatAmplitudePx = 7f) {
        CartoonAvatarBadge(avatarKey = avatarKey, size = 94.dp)
    }

    Text(
        text = strings.onboardingStep3Title,
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
    )
    Text(
        text = strings.onboardingStep3Sub,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )

    Cartoon3DCard(
        containerColor = MaterialTheme.colorScheme.surface,
        rimColor = MaterialTheme.colorScheme.outline,
        cornerRadius = 26.dp,
        bevelDepth = 5.dp,
        contentPadding = PaddingValues(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text(strings.childNameLabel) },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_child_name_input")
            )

            Column {
                Text(
                    text = "${strings.childAgeLabel}: ${age.toLocalizedDigits(language)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    (3..8).forEach { candidate ->
                        val selected = candidate == age
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selected) MangoPrimary else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .border(
                                    width = if (selected) 2.5.dp else 1.dp,
                                    color = if (selected) Color.White else MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                )
                                .clickable { onAgeChange(candidate) }
                                .testTag("onboarding_age_$candidate"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = candidate.toLocalizedDigits(language),
                                style = MaterialTheme.typography.titleMedium,
                                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Column {
                Text(
                    text = strings.chooseAvatarLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(LocalAssetRegistry.avatars) { avatar ->
                        val selected = avatar.key == avatarKey
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    if (selected) BerryCoral.copy(alpha = 0.14f) else Color.Transparent
                                )
                                .border(
                                    width = if (selected) 2.5.dp else 0.dp,
                                    color = if (selected) BerryCoral else Color.Transparent,
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .clickable { onAvatarChange(avatar.key) }
                                .padding(8.dp)
                                .testTag("onboarding_avatar_${avatar.key}")
                        ) {
                            CartoonAvatarBadge(avatarKey = avatar.key, size = 54.dp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (language == AppLanguage.BANGLA) avatar.labelBn else avatar.labelEn,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
