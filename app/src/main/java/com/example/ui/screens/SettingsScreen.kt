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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.SettingsBrightness
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.core.audio.SoundEffect
import com.example.core.localization.LocalAppStrings
import com.example.domain.model.AppSettings
import com.example.domain.model.BundledBanglaAudioStatus
import com.example.domain.model.ThemeMode
import com.example.ui.components.BundledAudioStatusSection
import com.example.ui.components.Cartoon3DCard
import com.example.ui.components.Cartoon3DCircleIconButton
import com.example.ui.components.LanguageTogglePill
import com.example.ui.theme.CeruleanRim
import com.example.ui.theme.CeruleanSky
import com.example.ui.theme.EmeraldMeadow
import com.example.ui.theme.GrapePop
import com.example.ui.theme.HoneyGold
import com.example.ui.theme.MangoPrimary
import com.example.ui.theme.darkenForRim

@Composable
fun SettingsScreen(
    settings: AppSettings,
    audioStatus: BundledBanglaAudioStatus,
    onBack: () -> Unit,
    onToggleLanguage: () -> Unit,
    onSetThemeMode: (ThemeMode) -> Unit,
    onSetSoundFx: (Boolean) -> Unit,
    onSetTts: (Boolean) -> Unit,
    onSetSpeechRate: (Float) -> Unit,
    onTestBanglaVoice: () -> Unit,
    onTestEnglishVoice: () -> Unit,
    onPlayEffect: (SoundEffect) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val lang = settings.language

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 114.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
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
                    Cartoon3DCircleIconButton(
                        icon = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = strings.onboardingBack,
                        containerColor = CeruleanSky,
                        onClick = {
                            onPlayEffect(SoundEffect.TAP_POP)
                            onBack()
                        },
                        modifier = Modifier.testTag("settings_back_button")
                    )
                    Column {
                        Text(
                            text = strings.settingsTitle,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = strings.settingsSubtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                LanguageTogglePill(language = lang, onToggle = onToggleLanguage)
            }
        }

        // =====================================================================
        // SECTION 1: GENERAL (Theme Mode & Language)
        // =====================================================================
        item {
            Cartoon3DCard(
                containerColor = MaterialTheme.colorScheme.surface,
                rimColor = CeruleanRim.copy(alpha = 0.55f),
                cornerRadius = 26.dp,
                bevelDepth = 6.dp,
                contentPadding = PaddingValues(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_section_general")
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SettingsSectionHeader(
                        icon = Icons.Rounded.Tune,
                        title = strings.settingsSectionGeneral,
                        accentColor = CeruleanSky
                    )

                    Text(
                        text = strings.themeModeTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    ThemeModeSelectorRow(
                        selectedMode = settings.themeMode,
                        onSelectMode = onSetThemeMode
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.defaultLanguageLabel,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        LanguageTogglePill(language = lang, onToggle = onToggleLanguage)
                    }
                }
            }
        }

        // =====================================================================
        // SECTION 2: SOUND (Independent UI Sound FX + Educational Voice)
        // =====================================================================
        item {
            Cartoon3DCard(
                containerColor = MaterialTheme.colorScheme.surface,
                rimColor = MangoPrimary.darkenForRim(0.76f),
                cornerRadius = 26.dp,
                bevelDepth = 6.dp,
                contentPadding = PaddingValues(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_section_sound")
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SettingsSectionHeader(
                        icon = if (settings.soundFxEnabled) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
                        title = strings.settingsSectionSound,
                        accentColor = MangoPrimary
                    )

                    // Playful Sound Effects Switch (celebration & quiz chimes; button taps stay silent)
                    SettingsRichToggleRow(
                        title = strings.soundEffectsLabel,
                        subtitle = strings.uiSoundSubtitle,
                        checked = settings.soundFxEnabled,
                        accentColor = MangoPrimary,
                        onCheckedChange = onSetSoundFx,
                        switchTestTag = "settings_ui_sound_switch"
                    )

                    // Educational Voice Narration On/Off Switch
                    SettingsRichToggleRow(
                        title = strings.voiceNarrationLabel,
                        subtitle = strings.eduVoiceSubtitle,
                        checked = settings.ttsEnabled,
                        accentColor = EmeraldMeadow,
                        onCheckedChange = onSetTts,
                        switchTestTag = "settings_voice_switch"
                    )

                    // Comfortable Child-Friendly Voice Speed Slider
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.speechSpeedLabel,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${"%.2f".format(settings.speechRate)}x",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = CeruleanSky
                            )
                        }
                        Slider(
                            value = settings.speechRate,
                            onValueChange = onSetSpeechRate,
                            valueRange = 0.68f..1.15f,
                            colors = SliderDefaults.colors(
                                thumbColor = MangoPrimary,
                                activeTrackColor = MangoPrimary
                            )
                        )
                    }

                    // Built-in Offline Bangla MP3 Status + Voice Quality Test Buttons
                    BundledAudioStatusSection(
                        audioStatus = audioStatus,
                        language = lang,
                        onTestBanglaVoice = onTestBanglaVoice,
                        onTestEnglishVoice = onTestEnglishVoice
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(
    icon: ImageVector,
    title: String,
    accentColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SettingsRichToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    accentColor: Color,
    onCheckedChange: (Boolean) -> Unit,
    switchTestTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accentColor
            ),
            modifier = Modifier.testTag(switchTestTag)
        )
    }
}

/**
 * Shared 3-way Light / Dark / System Default glossy pill selector used in Settings and Parent Zone.
 */
@Composable
fun ThemeModeSelectorRow(
    selectedMode: ThemeMode,
    onSelectMode: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val options = listOf(
        Triple(ThemeMode.LIGHT, strings.themeModeLight, Icons.Rounded.LightMode),
        Triple(ThemeMode.DARK, strings.themeModeDark, Icons.Rounded.DarkMode),
        Triple(ThemeMode.SYSTEM, strings.themeModeSystem, Icons.Rounded.SettingsBrightness)
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (mode, label, icon) ->
            val isSelected = mode == selectedMode
            val accent = when (mode) {
                ThemeMode.LIGHT -> HoneyGold
                ThemeMode.DARK -> GrapePop
                ThemeMode.SYSTEM -> CeruleanSky
            }
            val shape = RoundedCornerShape(18.dp)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 66.dp)
                    .clip(shape)
                    .background(
                        if (isSelected) accent
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                    )
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) accent.darkenForRim(0.75f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                        shape = shape
                    )
                    .clickable { onSelectMode(mode) }
                    .padding(horizontal = 6.dp, vertical = 10.dp)
                    .testTag("theme_mode_${mode.key}"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isSelected) Color.White else accent,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
