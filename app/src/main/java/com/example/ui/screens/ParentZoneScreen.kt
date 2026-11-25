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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.assets.LocalAssetRegistry
import com.example.core.localization.AppLanguage
import com.example.core.localization.LocalAppStrings
import com.example.core.localization.toLocalizedDigits
import com.example.domain.model.AppSettings
import com.example.domain.model.BanglaPoem
import com.example.domain.model.BundledBanglaAudioStatus
import com.example.domain.model.ChildProfile
import com.example.domain.model.LearningPillar
import com.example.domain.model.ModuleProgress
import com.example.domain.model.ModuleType
import com.example.domain.model.Story
import com.example.domain.model.ThemeMode
import com.example.ui.components.BundledAudioStatusSection
import com.example.ui.components.Cartoon3DButton
import com.example.ui.components.Cartoon3DCard
import com.example.ui.components.Cartoon3DCircleIconButton
import com.example.ui.components.CartoonAvatarBadge
import com.example.ui.components.CartoonProgressBar
import com.example.ui.components.LanguageTogglePill
import com.example.ui.theme.BerryCoral
import com.example.ui.theme.CeruleanSky
import com.example.ui.theme.EmeraldMeadow
import com.example.ui.theme.GrapePop
import com.example.ui.theme.HoneyGold
import com.example.ui.theme.MangoPrimary
import com.example.ui.theme.darkenForRim
import kotlin.random.Random

@Composable
fun ParentZoneScreen(
    settings: AppSettings,
    profiles: List<ChildProfile>,
    activeChild: ChildProfile?,
    moduleProgress: Map<ModuleType, ModuleProgress>,
    stories: List<Story>,
    poems: List<BanglaPoem>,
    audioStatus: BundledBanglaAudioStatus,
    onSelectChild: (Long) -> Unit,
    onAddChild: (String, Int, String) -> Unit,
    onDeleteChild: (Long) -> Unit,
    onToggleLanguage: () -> Unit,
    onSetSoundFx: (Boolean) -> Unit,
    onSetTts: (Boolean) -> Unit,
    onSetSpeechRate: (Float) -> Unit,
    onSetThemeMode: (ThemeMode) -> Unit,
    onTestBanglaVoice: () -> Unit,
    onTestEnglishVoice: () -> Unit,
    onResetProgress: () -> Unit,
    onBack: (() -> Unit)? = null,
    onOpenAboutApp: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var unlocked by remember { mutableStateOf(false) }

    if (!unlocked) {
        ParentMathGateView(
            language = settings.language,
            onBack = onBack,
            onUnlocked = { unlocked = true },
            modifier = modifier
        )
    } else {
        ParentDashboardView(
            settings = settings,
            profiles = profiles,
            activeChild = activeChild,
            moduleProgress = moduleProgress,
            stories = stories,
            poems = poems,
            audioStatus = audioStatus,
            onLock = { unlocked = false },
            onBack = onBack,
            onOpenAboutApp = onOpenAboutApp,
            onSelectChild = onSelectChild,
            onAddChild = onAddChild,
            onDeleteChild = onDeleteChild,
            onToggleLanguage = onToggleLanguage,
            onSetSoundFx = onSetSoundFx,
            onSetTts = onSetTts,
            onSetSpeechRate = onSetSpeechRate,
            onSetThemeMode = onSetThemeMode,
            onTestBanglaVoice = onTestBanglaVoice,
            onTestEnglishVoice = onTestEnglishVoice,
            onResetProgress = onResetProgress,
            modifier = modifier
        )
    }
}

@Composable
private fun ParentMathGateView(
    language: AppLanguage,
    onBack: (() -> Unit)?,
    onUnlocked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var left by remember { mutableIntStateOf(Random.nextInt(4, 10)) }
    var right by remember { mutableIntStateOf(Random.nextInt(3, 9)) }
    var answerInput by remember { mutableStateOf("") }
    var errorShown by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 104.dp)
            .testTag("parent_math_gate")
    ) {
        if (onBack != null) {
            Cartoon3DCircleIconButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = strings.onboardingBack,
                containerColor = CeruleanSky,
                onClick = onBack,
                modifier = Modifier.testTag("parent_gate_back_button")
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
        Cartoon3DCard(
            containerColor = MaterialTheme.colorScheme.surface,
            rimColor = GrapePop.darkenForRim(0.8f),
            cornerRadius = 28.dp,
            contentPadding = PaddingValues(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(GrapePop.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Shield,
                        contentDescription = strings.parentGateTitle,
                        tint = GrapePop,
                        modifier = Modifier.size(34.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = strings.parentGateTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = strings.parentGateSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(18.dp))

                // Math Problem Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(GrapePop.copy(alpha = 0.12f))
                        .padding(horizontal = 24.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "${left.toLocalizedDigits(language)} × ${right.toLocalizedDigits(language)} = ?",
                        style = MaterialTheme.typography.displayMedium,
                        color = GrapePop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {
                        left = Random.nextInt(4, 10)
                        right = Random.nextInt(3, 9)
                        answerInput = ""
                        errorShown = false
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "New problem",
                            tint = GrapePop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = answerInput,
                    onValueChange = { raw ->
                        answerInput = raw.filter { it.isDigit() }.take(3)
                        errorShown = false
                    },
                    label = { Text(strings.enterAnswer) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errorShown,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("parent_gate_input")
                )

                if (errorShown) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = strings.wrongAnswerHint,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFFF4D5A)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Cartoon3DButton(
                    text = strings.unlockButton,
                    icon = Icons.Rounded.LockOpen,
                    containerColor = GrapePop,
                    onClick = {
                        val expected = left * right
                        val parsed = answerInput.trim().toIntOrNull()
                        if (parsed == expected) {
                            onUnlocked()
                        } else {
                            errorShown = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("parent_gate_unlock_button")
                )
            }
        }
        }
    }
}

@Composable
private fun ParentDashboardView(
    settings: AppSettings,
    profiles: List<ChildProfile>,
    activeChild: ChildProfile?,
    moduleProgress: Map<ModuleType, ModuleProgress>,
    stories: List<Story>,
    poems: List<BanglaPoem>,
    audioStatus: BundledBanglaAudioStatus,
    onLock: () -> Unit,
    onBack: (() -> Unit)?,
    onOpenAboutApp: (() -> Unit)?,
    onSelectChild: (Long) -> Unit,
    onAddChild: (String, Int, String) -> Unit,
    onDeleteChild: (Long) -> Unit,
    onToggleLanguage: () -> Unit,
    onSetSoundFx: (Boolean) -> Unit,
    onSetTts: (Boolean) -> Unit,
    onSetSpeechRate: (Float) -> Unit,
    onSetThemeMode: (ThemeMode) -> Unit,
    onTestBanglaVoice: () -> Unit,
    onTestEnglishVoice: () -> Unit,
    onResetProgress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val lang = settings.language
    var showAddDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("parent_dashboard_screen"),
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
                            onClick = onBack
                        )
                    }
                    Text(
                        text = strings.parentZoneTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Cartoon3DButton(
                    text = strings.lockParentZone,
                    icon = Icons.Rounded.Lock,
                    containerColor = GrapePop,
                    onClick = onLock,
                    modifier = Modifier.width(114.dp)
                )
            }
        }

        // Child Profiles Management
        item {
            Cartoon3DCard(
                containerColor = MaterialTheme.colorScheme.surface,
                rimColor = MaterialTheme.colorScheme.outline,
                cornerRadius = 24.dp,
                contentPadding = PaddingValues(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.childProfilesTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Cartoon3DButton(
                            text = strings.addChildButton,
                            icon = Icons.Rounded.PersonAdd,
                            containerColor = EmeraldMeadow,
                            onClick = { showAddDialog = true },
                            modifier = Modifier
                                .width(156.dp)
                                .testTag("add_child_button")
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    profiles.forEach { profile ->
                        val isActive = profile.id == activeChild?.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    if (isActive) CeruleanSky.copy(alpha = 0.14f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                                )
                                .clickable { onSelectChild(profile.id) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CartoonAvatarBadge(avatarKey = profile.avatarKey, size = 44.dp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "${profile.name} (${profile.age.toLocalizedDigits(lang)} ${strings.ageYearsSuffix})",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Rounded.Star,
                                            contentDescription = null,
                                            tint = HoneyGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${profile.totalStars.toLocalizedDigits(lang)} ${strings.starsLabel}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MangoPrimary
                                        )
                                    }
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isActive) {
                                    Text(
                                        text = strings.activeLabel,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = CeruleanSky,
                                        modifier = Modifier.padding(end = 6.dp)
                                    )
                                }
                                if (profiles.size > 1) {
                                    IconButton(onClick = { onDeleteChild(profile.id) }) {
                                        Icon(
                                            imageVector = Icons.Rounded.DeleteOutline,
                                            contentDescription = strings.deleteLabel,
                                            tint = BerryCoral
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Per-Child Progress Analytics
        item {
            Cartoon3DCard(
                containerColor = MaterialTheme.colorScheme.surface,
                rimColor = MaterialTheme.colorScheme.outline,
                cornerRadius = 24.dp,
                contentPadding = PaddingValues(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "${strings.progressOverviewTitle} — ${activeChild?.name.orEmpty()}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    ProgressMetricRow(
                        label = strings.tabEnglishAbc,
                        done = moduleProgress[ModuleType.ALPHABET_EN]?.completedCount ?: 0,
                        total = moduleProgress[ModuleType.ALPHABET_EN]?.totalCount ?: 26,
                        color = CeruleanSky,
                        language = lang
                    )
                    ProgressMetricRow(
                        label = strings.tabBanglaLetters,
                        done = moduleProgress[ModuleType.ALPHABET_BN]?.completedCount ?: 0,
                        total = moduleProgress[ModuleType.ALPHABET_BN]?.totalCount ?: 25,
                        color = MangoPrimary,
                        language = lang
                    )
                    ProgressMetricRow(
                        label = strings.pillarNumbersTitle,
                        done = moduleProgress[ModuleType.NUMBERS]?.completedCount ?: 0,
                        total = moduleProgress[ModuleType.NUMBERS]?.totalCount ?: 100,
                        color = EmeraldMeadow,
                        language = lang
                    )
                    val expTypes = LearningPillar.WORLD.moduleTypes()
                    val expDone = expTypes.sumOf { moduleProgress[it]?.completedCount ?: 0 }
                    val expTotal = expTypes.sumOf { moduleProgress[it]?.totalCount ?: 0 }
                    ProgressMetricRow(
                        label = strings.pillarWorldTitle,
                        done = expDone,
                        total = expTotal.coerceAtLeast(1),
                        color = BerryCoral,
                        language = lang
                    )
                    val langTypes = LearningPillar.LANGUAGE.moduleTypes()
                    val langDone = langTypes.sumOf { moduleProgress[it]?.completedCount ?: 0 }
                    val langTotal = langTypes.sumOf { moduleProgress[it]?.totalCount ?: 0 }
                    ProgressMetricRow(
                        label = strings.pillarLanguageTitle,
                        done = langDone,
                        total = langTotal.coerceAtLeast(1),
                        color = GrapePop,
                        language = lang
                    )
                    ProgressMetricRow(
                        label = strings.worldPoems,
                        done = poems.count { it.isCompleted },
                        total = poems.size.coerceAtLeast(1),
                        color = Color(0xFF6B5CE7),
                        language = lang
                    )
                    ProgressMetricRow(
                        label = strings.worldStories,
                        done = stories.count { it.isCompleted },
                        total = stories.size.coerceAtLeast(1),
                        color = HoneyGold,
                        language = lang
                    )
                }
            }
        }

        // Audio, Language & Theme Controls
        item {
            Cartoon3DCard(
                containerColor = MaterialTheme.colorScheme.surface,
                rimColor = MaterialTheme.colorScheme.outline,
                cornerRadius = 24.dp,
                contentPadding = PaddingValues(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = strings.audioSettingsTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.defaultLanguageLabel,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        LanguageTogglePill(language = lang, onToggle = onToggleLanguage)
                    }

                    SettingsToggleRow(
                        label = strings.soundEffectsLabel,
                        checked = settings.soundFxEnabled,
                        onCheckedChange = onSetSoundFx
                    )
                    SettingsToggleRow(
                        label = strings.voiceNarrationLabel,
                        checked = settings.ttsEnabled,
                        onCheckedChange = onSetTts
                    )

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = strings.speechSpeedLabel,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${"%.2f".format(settings.speechRate)}x",
                                style = MaterialTheme.typography.labelLarge,
                                color = CeruleanSky
                            )
                        }
                        Slider(
                            value = settings.speechRate,
                            onValueChange = onSetSpeechRate,
                            valueRange = 0.65f..1.20f,
                            colors = SliderDefaults.colors(
                                thumbColor = MangoPrimary,
                                activeTrackColor = MangoPrimary
                            )
                        )
                    }

                    BundledAudioStatusSection(
                        audioStatus = audioStatus,
                        language = lang,
                        onTestBanglaVoice = onTestBanglaVoice,
                        onTestEnglishVoice = onTestEnglishVoice
                    )

                    Text(
                        text = strings.themeModeTitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    ThemeModeSelectorRow(
                        selectedMode = settings.themeMode,
                        onSelectMode = onSetThemeMode
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Cartoon3DButton(
                        text = strings.resetProgressButton,
                        containerColor = BerryCoral,
                        onClick = { showResetDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reset_progress_button")
                    )
                }
            }
        }

        // Developer Brand Card (Dev. E) — taps straight to About App screen
        item {
            Cartoon3DCard(
                containerColor = CeruleanSky,
                rimColor = CeruleanSky.darkenForRim(0.75f),
                cornerRadius = 24.dp,
                contentPadding = PaddingValues(18.dp),
                onClick = onOpenAboutApp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.brandTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                        Text(
                            text = "${strings.developerCredit} • ${strings.brandTagline}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.92f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = strings.aboutAppTitle,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddChildProfileDialog(
            language = lang,
            onDismiss = { showAddDialog = false },
            onSave = { name, age, avatarKey ->
                onAddChild(name, age, avatarKey)
                showAddDialog = false
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(strings.resetConfirmTitle) },
            text = { Text(strings.resetConfirmBody) },
            confirmButton = {
                TextButton(onClick = {
                    onResetProgress()
                    showResetDialog = false
                }) {
                    Text(strings.confirmResetButton, color = BerryCoral, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(strings.cancelButton)
                }
            }
        )
    }
}

@Composable
private fun ProgressMetricRow(
    label: String,
    done: Int,
    total: Int,
    color: Color,
    language: AppLanguage
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${done.toLocalizedDigits(language)}/${total.toLocalizedDigits(language)}",
                style = MaterialTheme.typography.labelLarge,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        CartoonProgressBar(
            progress = if (total == 0) 0f else done.toFloat() / total.toFloat(),
            fillColor = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            height = 10.dp
        )
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun AddChildProfileDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (String, Int, String) -> Unit
) {
    val strings = LocalAppStrings.current
    var name by remember { mutableStateOf("") }
    var age by remember { mutableIntStateOf(5) }
    var avatarKey by remember { mutableStateOf(LocalAssetRegistry.avatars.first().key) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.addChildButton) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(18) },
                    label = { Text(strings.childNameLabel) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_child_name_input")
                )

                Column {
                    Text(
                        text = "${strings.childAgeLabel}: ${age.toLocalizedDigits(language)}",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        (3..8).forEach { candidate ->
                            val selected = candidate == age
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (selected) MangoPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { age = candidate },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = candidate.toLocalizedDigits(language),
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
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(LocalAssetRegistry.avatars) { avatar ->
                            val selected = avatar.key == avatarKey
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .border(
                                        width = if (selected) 3.dp else 0.dp,
                                        color = if (selected) MangoPrimary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { avatarKey = avatar.key }
                                    .padding(3.dp)
                            ) {
                                CartoonAvatarBadge(avatarKey = avatar.key, size = 46.dp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = { onSave(name.trim(), age, avatarKey) },
                modifier = Modifier.testTag("save_child_button")
            ) {
                Text(strings.saveChildButton, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancelButton)
            }
        }
    )
}
