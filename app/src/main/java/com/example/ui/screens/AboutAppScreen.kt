package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.BuildConfig
import com.example.core.assets.LocalAssetRegistry
import com.example.core.audio.SoundEffect
import com.example.core.localization.AppLanguage
import com.example.core.localization.LocalAppStrings
import com.example.core.util.ExternalLinkOpener
import com.example.ui.components.Cartoon3DButton
import com.example.ui.components.Cartoon3DCard
import com.example.ui.components.Cartoon3DCircleIconButton
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

@Composable
fun AboutAppScreen(
    language: AppLanguage,
    onBack: () -> Unit,
    onToggleLanguage: () -> Unit,
    onReplayOnboarding: () -> Unit,
    onOpenParentZone: () -> Unit,
    onPlayEffect: (SoundEffect) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("about_app_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 114.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Navigation Bar
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
                        onClick = onBack,
                        modifier = Modifier.testTag("about_back_button")
                    )
                    Text(
                        text = strings.aboutAppTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                LanguageTogglePill(language = language, onToggle = onToggleLanguage)
            }
        }

        // Hero App Identity Card
        item {
            Cartoon3DCard(
                containerColor = MaterialTheme.colorScheme.surface,
                rimColor = CeruleanRim,
                cornerRadius = 28.dp,
                bevelDepth = 6.dp,
                contentPadding = PaddingValues(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Floating3DBox(floatAmplitudePx = 7f) {
                        Box(
                            modifier = Modifier
                                .size(116.dp)
                                .clip(CircleShape)
                                .background(CeruleanSky)
                                .border(5.dp, Color.White, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = LocalAssetRegistry.appCrestArtwork),
                                contentDescription = strings.brandTitle,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "KID'Z WORLD",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MangoPrimary.copy(alpha = 0.16f))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = strings.brandTagline,
                            style = MaterialTheme.typography.titleMedium,
                            color = MangoPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${strings.aboutVersionLabel} ${BuildConfig.VERSION_NAME}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = strings.aboutMissionText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Studio Spotlight Card: Clickable Developer Link (Dev. E -> official website)
        item {
            Cartoon3DCard(
                containerColor = GrapePop,
                rimColor = GrapeRim,
                cornerRadius = 26.dp,
                bevelDepth = 6.dp,
                contentPadding = PaddingValues(20.dp),
                onClick = {
                    onPlayEffect(SoundEffect.TAP_POP)
                    val opened = ExternalLinkOpener.openExternalUrl(
                        context = context,
                        url = ExternalLinkOpener.DEVELOPER_WEBSITE_URL
                    )
                    if (!opened) {
                        Toast.makeText(context, strings.openWebsiteError, Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("about_dev_e_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val avatarShape = RoundedCornerShape(20.dp)
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = avatarShape,
                                ambientColor = GrapeRim.copy(alpha = 0.55f),
                                spotColor = GrapeRim.copy(alpha = 0.72f)
                            )
                            .clip(avatarShape)
                            .background(Color.White)
                            .border(2.5.dp, Color.White.copy(alpha = 0.85f), avatarShape)
                            .testTag("about_dev_e_photo"),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = LocalAssetRegistry.developerPhoto),
                            contentDescription = "${strings.developerLabel}: ${ExternalLinkOpener.DEVELOPER_NAME}",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.TopCenter,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = HoneyGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${strings.developerLabel}: ${ExternalLinkOpener.DEVELOPER_NAME}",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                textDecoration = TextDecoration.Underline
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                                contentDescription = strings.visitWebsiteHint,
                                tint = HoneyGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = strings.aboutStudioRole,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.92f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${strings.visitWebsiteHint} • ebrahimjamansumon.blogspot.com",
                            style = MaterialTheme.typography.labelMedium,
                            color = HoneyGold
                        )
                    }
                }
            }
        }

        // 2x2 Kid-Safe Feature Highlights
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AboutFeatureTile(
                        icon = Icons.Rounded.CloudOff,
                        label = strings.aboutFeatureOffline,
                        color = EmeraldMeadow,
                        modifier = Modifier.weight(1f)
                    )
                    AboutFeatureTile(
                        icon = Icons.Rounded.Translate,
                        label = strings.aboutFeatureBilingual,
                        color = CeruleanSky,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AboutFeatureTile(
                        icon = Icons.Rounded.VerifiedUser,
                        label = strings.aboutFeatureKidSafe,
                        color = BerryCoral,
                        modifier = Modifier.weight(1f)
                    )
                    AboutFeatureTile(
                        icon = Icons.Rounded.ChildCare,
                        label = strings.aboutFeatureAges,
                        color = HoneyGold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Real Interactive Action Buttons (Zero Mockups)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Cartoon3DButton(
                    text = strings.replayOnboardingButton,
                    icon = Icons.Rounded.Replay,
                    containerColor = MangoPrimary,
                    rimColor = MangoRim,
                    onClick = onReplayOnboarding,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("replay_onboarding_button")
                )
                Cartoon3DButton(
                    text = strings.openParentZoneTitle,
                    icon = Icons.Rounded.Shield,
                    containerColor = CeruleanSky,
                    rimColor = CeruleanRim,
                    onClick = onOpenParentZone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("about_open_parent_zone_button")
                )
            }
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }
    }
}

@Composable
private fun AboutFeatureTile(
    icon: ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Cartoon3DCard(
        containerColor = MaterialTheme.colorScheme.surface,
        rimColor = color.darkenForRim(0.82f),
        cornerRadius = 22.dp,
        bevelDepth = 4.dp,
        contentPadding = PaddingValues(14.dp),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}
