package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.assets.LocalAssetRegistry
import com.example.core.audio.SoundEffect
import com.example.core.localization.AppLanguage
import com.example.core.localization.LocalAppStrings
import com.example.core.localization.toLocalizedDigits
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Generates a guaranteed-shuffled list of tile indices [0 until count] that is never already solved.
 */
fun generateShuffledJigsawBoard(tileCount: Int): List<Int> {
    if (tileCount <= 1) return listOf(0)
    val solved = (0 until tileCount).toList()
    var shuffled = solved.shuffled()
    var guard = 0
    while (shuffled == solved && guard < 12) {
        shuffled = solved.shuffled()
        guard++
    }
    if (shuffled == solved) {
        return solved.reversed()
    }
    return shuffled
}

// =========================================================================================
// 1. PICTURE JIGSAW PUZZLE (2x2 Easy & 3x3 Fun — Tap-to-Swap with 3D Cartoon Art)
// =========================================================================================

@Composable
fun PictureJigsawPuzzleView(
    language: AppLanguage,
    onBack: () -> Unit,
    onSpeakText: (String) -> Unit,
    onPlayEffect: (SoundEffect) -> Unit,
    onWin: (score: Int, stars: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val artworks = LocalAssetRegistry.puzzleArtworks
    var artworkIndex by remember { mutableIntStateOf(0) }
    var gridSize by remember { mutableIntStateOf(2) }
    var tiles by remember(artworkIndex, gridSize) {
        mutableStateOf(generateShuffledJigsawBoard(gridSize * gridSize))
    }
    var selectedSlot by remember(artworkIndex, gridSize) { mutableStateOf<Int?>(null) }
    var moves by remember(artworkIndex, gridSize) { mutableIntStateOf(0) }
    var showPreview by remember { mutableStateOf(false) }
    var solved by remember(artworkIndex, gridSize) { mutableStateOf(false) }

    val activeArtwork = artworks[artworkIndex]
    val artworkBitmap = ImageBitmap.imageResource(id = activeArtwork.drawableRes)
    val accent = Color(activeArtwork.accentHex)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("jigsaw_puzzle_view"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                    Cartoon3DCircleIconButton(
                        icon = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = strings.backToArcade,
                        containerColor = CeruleanSky,
                        onClick = onBack,
                        modifier = Modifier.testTag("jigsaw_back_button")
                    )
                    Column {
                        Text(
                            text = strings.jigsawPuzzleTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${strings.movesLabel}: ${moves.toLocalizedDigits(language)}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f)
                        )
                    }
                }

                Cartoon3DCircleIconButton(
                    icon = if (showPreview) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                    contentDescription = strings.jigsawHintPeek,
                    containerColor = HoneyGold,
                    onClick = {
                        onPlayEffect(SoundEffect.TAP_POP)
                        showPreview = !showPreview
                    },
                    modifier = Modifier.testTag("jigsaw_peek_button")
                )
            }
        }

        // Artwork Selector Carousel
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(artworks) { idx, art ->
                    val isSelected = idx == artworkIndex
                    val chipColor = Color(art.accentHex)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (isSelected) chipColor else MaterialTheme.colorScheme.surface)
                            .border(2.dp, chipColor, RoundedCornerShape(50))
                            .clickable {
                                onPlayEffect(SoundEffect.TAP_POP)
                                artworkIndex = idx
                            }
                            .padding(horizontal = 14.dp, vertical = 9.dp)
                            .testTag("jigsaw_art_$idx"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = art.emoji, fontSize = 18.sp)
                        Text(
                            text = if (language == AppLanguage.BANGLA) art.titleBn else art.titleEn,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Easy (2x2) vs Fun (3x3) Mode Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Cartoon3DButton(
                    text = strings.jigsawEasyMode,
                    containerColor = if (gridSize == 2) EmeraldMeadow else MaterialTheme.colorScheme.surfaceVariant,
                    rimColor = if (gridSize == 2) EmeraldRim else MaterialTheme.colorScheme.outlineVariant,
                    contentColor = if (gridSize == 2) Color.White else MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        onPlayEffect(SoundEffect.TAP_POP)
                        gridSize = 2
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("jigsaw_mode_2x2")
                )
                Cartoon3DButton(
                    text = strings.jigsawChallengeMode,
                    containerColor = if (gridSize == 3) GrapePop else MaterialTheme.colorScheme.surfaceVariant,
                    rimColor = if (gridSize == 3) GrapeRim else MaterialTheme.colorScheme.outlineVariant,
                    contentColor = if (gridSize == 3) Color.White else MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        onPlayEffect(SoundEffect.TAP_POP)
                        gridSize = 3
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("jigsaw_mode_3x3")
                )
            }
        }

        // Optional Reference Preview
        item {
            AnimatedVisibility(visible = showPreview) {
                Cartoon3DCard(
                    containerColor = MaterialTheme.colorScheme.surface,
                    rimColor = HoneyRim,
                    cornerRadius = 22.dp,
                    contentPadding = PaddingValues(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = activeArtwork.drawableRes),
                            contentDescription = strings.jigsawHintPeek,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(86.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                        Text(
                            text = strings.jigsawTapToSwapHint,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Puzzle Board
        item {
            Cartoon3DCard(
                containerColor = MaterialTheme.colorScheme.surface,
                rimColor = accent.darkenForRim(0.8f),
                cornerRadius = 28.dp,
                bevelDepth = 6.dp,
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (row in 0 until gridSize) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (col in 0 until gridSize) {
                                val slotIndex = row * gridSize + col
                                val pieceIndex = tiles[slotIndex]
                                val isSelected = selectedSlot == slotIndex
                                val isCorrectSpot = pieceIndex == slotIndex
                                val tileScale by animateFloatAsState(
                                    targetValue = if (isSelected) 0.94f else 1f,
                                    animationSpec = spring(dampingRatio = 0.65f),
                                    label = "jigsaw_tile_scale"
                                )

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .scale(tileScale)
                                        .clip(RoundedCornerShape(18.dp))
                                        .border(
                                            width = if (isSelected) 4.dp else if (solved) 3.dp else 2.dp,
                                            color = when {
                                                solved -> EmeraldMeadow
                                                isSelected -> HoneyGold
                                                isCorrectSpot -> EmeraldMeadow.copy(alpha = 0.7f)
                                                else -> Color.White.copy(alpha = 0.8f)
                                            },
                                            shape = RoundedCornerShape(18.dp)
                                        )
                                        .clickable(enabled = !solved) {
                                            val first = selectedSlot
                                            if (first == null) {
                                                onPlayEffect(SoundEffect.TAP_POP)
                                                selectedSlot = slotIndex
                                            } else if (first == slotIndex) {
                                                onPlayEffect(SoundEffect.TAP_POP)
                                                selectedSlot = null
                                            } else {
                                                val updated = tiles.toMutableList()
                                                val tmp = updated[first]
                                                updated[first] = updated[slotIndex]
                                                updated[slotIndex] = tmp
                                                tiles = updated
                                                selectedSlot = null
                                                moves += 1

                                                val nowSolved = updated.indices.all { updated[it] == it }
                                                if (nowSolved) {
                                                    solved = true
                                                    val stars = if (gridSize == 3) 25 else 15
                                                    val score = (120 - moves * 4).coerceAtLeast(40)
                                                    onWin(score, stars)
                                                    val praise = if (language == AppLanguage.BANGLA) {
                                                        "শাবাশ! দারুণ ছবি মিলিয়েছ!"
                                                    } else {
                                                        "Awesome! You solved the picture puzzle!"
                                                    }
                                                    onSpeakText(praise)
                                                } else if (updated[slotIndex] == slotIndex || updated[first] == first) {
                                                    onPlayEffect(SoundEffect.CORRECT_CHIME)
                                                } else {
                                                    onPlayEffect(SoundEffect.TAP_POP)
                                                }
                                            }
                                        }
                                        .testTag("jigsaw_slot_$slotIndex")
                                ) {
                                    JigsawTileCanvas(
                                        bitmap = artworkBitmap,
                                        pieceIndex = pieceIndex,
                                        gridSize = gridSize,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    if (isCorrectSpot && !solved) {
                                        Icon(
                                            imageVector = Icons.Rounded.CheckCircle,
                                            contentDescription = null,
                                            tint = EmeraldMeadow,
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(6.dp)
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Solved Celebration Banner or Friendly Hint
        item {
            if (solved) {
                Cartoon3DCard(
                    containerColor = EmeraldMeadow,
                    rimColor = EmeraldRim,
                    cornerRadius = 24.dp,
                    contentPadding = PaddingValues(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🎉 ${strings.puzzleSolvedBanner}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Cartoon3DButton(
                            text = strings.playAgain,
                            icon = Icons.Rounded.Replay,
                            containerColor = HoneyGold,
                            rimColor = HoneyRim,
                            contentColor = Color(0xFF3E2600),
                            onClick = {
                                onPlayEffect(SoundEffect.TAP_POP)
                                tiles = generateShuffledJigsawBoard(gridSize * gridSize)
                                selectedSlot = null
                                moves = 0
                                solved = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("jigsaw_play_again_button")
                        )
                    }
                }
            } else {
                Text(
                    text = "💡 ${strings.jigsawTapToSwapHint}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun JigsawTileCanvas(
    bitmap: ImageBitmap,
    pieceIndex: Int,
    gridSize: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val squareSrc = minOf(bitmap.width, bitmap.height)
        val srcOffsetX = (bitmap.width - squareSrc) / 2
        val srcOffsetY = (bitmap.height - squareSrc) / 2
        val cellSrc = squareSrc / gridSize
        val srcCol = pieceIndex % gridSize
        val srcRow = pieceIndex / gridSize

        drawImage(
            image = bitmap,
            srcOffset = IntOffset(
                x = srcOffsetX + srcCol * cellSrc,
                y = srcOffsetY + srcRow * cellSrc
            ),
            srcSize = IntSize(cellSrc, cellSrc),
            dstSize = IntSize(size.width.toInt().coerceAtLeast(1), size.height.toInt().coerceAtLeast(1))
        )
    }
}

// =========================================================================================
// 2. ANIMAL, FRUIT & VEHICLE SORTING PUZZLE (Bilingual Voice + Baskets)
// =========================================================================================

enum class SortingCategory(val emoji: String, val accent: Color, val rim: Color) {
    ANIMALS("🦁", MangoPrimary, MangoRim),
    FRUITS("🍎", BerryCoral, BerryRim),
    VEHICLES("🚌", CeruleanSky, CeruleanRim)
}

data class SortingItem(
    val id: String,
    val emoji: String,
    val nameBn: String,
    val nameEn: String,
    val category: SortingCategory
)

object SortingPuzzleCatalog {
    val items: List<SortingItem> = listOf(
        SortingItem("s_lion", "🦁", "সিংহ", "Lion", SortingCategory.ANIMALS),
        SortingItem("s_tiger", "🐯", "বাঘ", "Tiger", SortingCategory.ANIMALS),
        SortingItem("s_elephant", "🐘", "হাতি", "Elephant", SortingCategory.ANIMALS),
        SortingItem("s_rabbit", "🐰", "খরগোশ", "Rabbit", SortingCategory.ANIMALS),
        SortingItem("s_parrot", "🦜", "তোতা পাখি", "Parrot", SortingCategory.ANIMALS),
        SortingItem("s_cat", "🐱", "বিড়াল", "Cat", SortingCategory.ANIMALS),
        SortingItem("s_mango", "🥭", "আম", "Mango", SortingCategory.FRUITS),
        SortingItem("s_apple", "🍎", "আপেল", "Apple", SortingCategory.FRUITS),
        SortingItem("s_banana", "🍌", "কলা", "Banana", SortingCategory.FRUITS),
        SortingItem("s_grapes", "🍇", "আঙুর", "Grapes", SortingCategory.FRUITS),
        SortingItem("s_watermelon", "🍉", "তরমুজ", "Watermelon", SortingCategory.FRUITS),
        SortingItem("s_orange", "🍊", "কমলা", "Orange", SortingCategory.FRUITS),
        SortingItem("s_bus", "🚌", "বাস", "Bus", SortingCategory.VEHICLES),
        SortingItem("s_train", "🚂", "ট্রেন", "Train", SortingCategory.VEHICLES),
        SortingItem("s_boat", "⛵", "নৌকা", "Boat", SortingCategory.VEHICLES),
        SortingItem("s_plane", "✈️", "বিমান", "Airplane", SortingCategory.VEHICLES),
        SortingItem("s_bike", "🚲", "সাইকেল", "Bicycle", SortingCategory.VEHICLES),
        SortingItem("s_car", "🚗", "গাড়ি", "Car", SortingCategory.VEHICLES)
    )
}

@Composable
fun AnimalFruitSortingPuzzleView(
    language: AppLanguage,
    onBack: () -> Unit,
    onSpeakText: (String) -> Unit,
    onPlayEffect: (SoundEffect) -> Unit,
    onFinish: (score: Int, stars: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val scope = rememberCoroutineScope()
    val totalRounds = 6

    var deck by remember { mutableStateOf(SortingPuzzleCatalog.items.shuffled().take(totalRounds)) }
    var roundIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var isCorrectFeedback by remember { mutableStateOf(true) }
    var finished by remember { mutableStateOf(false) }

    val currentItem = deck.getOrNull(roundIndex)

    LaunchedEffect(roundIndex, deck, language) {
        if (!finished && currentItem != null) {
            val spokenName = if (language == AppLanguage.BANGLA) currentItem.nameBn else currentItem.nameEn
            onSpeakText(spokenName)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("sorting_puzzle_view"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                        contentDescription = strings.backToArcade,
                        containerColor = BerryCoral,
                        onClick = onBack,
                        modifier = Modifier.testTag("sorting_back_button")
                    )
                    Column {
                        Text(
                            text = strings.sortingPuzzleTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${strings.roundLabel} ${(roundIndex + 1).coerceAtMost(totalRounds).toLocalizedDigits(language)}/${totalRounds.toLocalizedDigits(language)} • ${strings.scoreLabel}: ${score.toLocalizedDigits(language)}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                        )
                    }
                }
            }
        }

        if (finished) {
            item {
                PuzzleVictoryCard(
                    score = score,
                    starsEarned = 20,
                    language = language,
                    onPlayAgain = {
                        onPlayEffect(SoundEffect.TAP_POP)
                        deck = SortingPuzzleCatalog.items.shuffled().take(totalRounds)
                        roundIndex = 0
                        score = 0
                        feedbackMessage = null
                        finished = false
                    },
                    onBack = onBack
                )
            }
        } else if (currentItem != null) {
            // Big Bouncing Item Card with Voice Replay
            item {
                Cartoon3DCard(
                    containerColor = MaterialTheme.colorScheme.surface,
                    rimColor = currentItem.category.accent.darkenForRim(0.82f),
                    cornerRadius = 30.dp,
                    bevelDepth = 6.dp,
                    contentPadding = PaddingValues(24.dp),
                    onClick = {
                        onPlayEffect(SoundEffect.TAP_POP)
                        onSpeakText(if (language == AppLanguage.BANGLA) currentItem.nameBn else currentItem.nameEn)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sorting_active_item_card")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = strings.sortingPromptHint,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Floating3DBox(floatAmplitudePx = 7f) {
                            Text(text = currentItem.emoji, fontSize = 84.sp)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(CeruleanSky.copy(alpha = 0.14f))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                                contentDescription = strings.tapToHear,
                                tint = CeruleanSky,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (language == AppLanguage.BANGLA) currentItem.nameBn else currentItem.nameEn,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        feedbackMessage?.let { msg ->
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isCorrectFeedback) EmeraldMeadow else BerryCoral
                            )
                        }
                    }
                }
            }

            // 3 Oversized Glossy Basket Targets
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SortingCategory.entries.forEach { category ->
                        val basketLabel = when (category) {
                            SortingCategory.ANIMALS -> strings.sortingBasketAnimals
                            SortingCategory.FRUITS -> strings.sortingBasketFruits
                            SortingCategory.VEHICLES -> strings.sortingBasketVehicles
                        }
                        Cartoon3DCard(
                            containerColor = category.accent,
                            rimColor = category.rim,
                            cornerRadius = 24.dp,
                            bevelDepth = 6.dp,
                            contentPadding = PaddingValues(vertical = 18.dp, horizontal = 8.dp),
                            onClick = {
                                if (currentItem.category == category) {
                                    onPlayEffect(SoundEffect.CORRECT_CHIME)
                                    score += 20
                                    isCorrectFeedback = true
                                    feedbackMessage = "⭐ ${strings.greatJob}"
                                    scope.launch {
                                        delay(550L)
                                        feedbackMessage = null
                                        if (roundIndex + 1 >= totalRounds) {
                                            finished = true
                                            onFinish(score, 20)
                                        } else {
                                            roundIndex += 1
                                        }
                                    }
                                } else {
                                    onPlayEffect(SoundEffect.TRY_AGAIN_BOOP)
                                    isCorrectFeedback = false
                                    feedbackMessage = strings.tryAgainHint
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sorting_basket_${category.name.lowercase()}")
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = category.emoji, fontSize = 36.sp)
                                Text(
                                    text = basketLabel,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================================
// 3. PATTERN & SEQUENCE PUZZLE (ABAB / AABB / ABC Visual Logic)
// =========================================================================================

data class PatternToken(
    val emoji: String,
    val labelBn: String,
    val labelEn: String,
    val color: Color
)

data class PatternQuestion(
    val sequence: List<PatternToken>,
    val answer: PatternToken,
    val choices: List<PatternToken>
)

object PatternPuzzleGenerator {
    private val pool = listOf(
        PatternToken("🍎", "আপেল", "Apple", BerryCoral),
        PatternToken("🍌", "কলা", "Banana", HoneyGold),
        PatternToken("🌟", "তারা", "Star", MangoPrimary),
        PatternToken("🔵", "নীল বৃত্ত", "Blue Circle", CeruleanSky),
        PatternToken("🐸", "ব্যাঙ", "Frog", EmeraldMeadow),
        PatternToken("🦋", "প্রজাপতি", "Butterfly", GrapePop),
        PatternToken("🍓", "স্ট্রবেরি", "Strawberry", BerryCoral),
        PatternToken("🌻", "সূর্যমুখী", "Sunflower", HoneyGold)
    )

    fun generateRounds(count: Int = 5): List<PatternQuestion> {
        return List(count) { roundIdx ->
            val picked = pool.shuffled().take(3)
            val a = picked[0]
            val b = picked[1]
            val c = picked[2]
            val (seq, ans) = when (roundIdx % 3) {
                0 -> listOf(a, b, a, b, a) to b // ABAB
                1 -> listOf(a, a, b, b, a) to a // AABB
                else -> listOf(a, b, c, a, b) to c // ABC
            }
            PatternQuestion(
                sequence = seq,
                answer = ans,
                choices = listOf(a, b, c).shuffled()
            )
        }
    }
}

@Composable
fun PatternSequencePuzzleView(
    language: AppLanguage,
    onBack: () -> Unit,
    onSpeakText: (String) -> Unit,
    onPlayEffect: (SoundEffect) -> Unit,
    onFinish: (score: Int, stars: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val scope = rememberCoroutineScope()
    var rounds by remember { mutableStateOf(PatternPuzzleGenerator.generateRounds(5)) }
    var roundIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var solvedToken by remember { mutableStateOf<PatternToken?>(null) }
    var wrongEmoji by remember { mutableStateOf<String?>(null) }
    var finished by remember { mutableStateOf(false) }

    val question = rounds.getOrNull(roundIndex)

    LaunchedEffect(roundIndex, finished, language) {
        if (!finished && question != null) {
            onSpeakText(strings.patternPromptHint)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("pattern_puzzle_view"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                        contentDescription = strings.backToArcade,
                        containerColor = EmeraldMeadow,
                        onClick = onBack,
                        modifier = Modifier.testTag("pattern_back_button")
                    )
                    Column {
                        Text(
                            text = strings.patternPuzzleTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${strings.roundLabel} ${(roundIndex + 1).coerceAtMost(rounds.size).toLocalizedDigits(language)}/${rounds.size.toLocalizedDigits(language)} • ${strings.scoreLabel}: ${score.toLocalizedDigits(language)}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                        )
                    }
                }
            }
        }

        if (finished) {
            item {
                PuzzleVictoryCard(
                    score = score,
                    starsEarned = 20,
                    language = language,
                    onPlayAgain = {
                        onPlayEffect(SoundEffect.TAP_POP)
                        rounds = PatternPuzzleGenerator.generateRounds(5)
                        roundIndex = 0
                        score = 0
                        solvedToken = null
                        wrongEmoji = null
                        finished = false
                    },
                    onBack = onBack
                )
            }
        } else if (question != null) {
            // Pattern Chain Card
            item {
                Cartoon3DCard(
                    containerColor = MaterialTheme.colorScheme.surface,
                    rimColor = EmeraldRim,
                    cornerRadius = 28.dp,
                    bevelDepth = 6.dp,
                    contentPadding = PaddingValues(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = strings.patternPromptHint,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            question.sequence.forEach { token ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(token.color.copy(alpha = 0.16f))
                                        .border(2.dp, token.color.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = token.emoji, fontSize = 26.sp)
                                }
                            }

                            // Glowing Mystery Slot "?"
                            val slotBg by animateColorAsState(
                                targetValue = if (solvedToken != null) EmeraldMeadow.copy(alpha = 0.25f) else HoneyGold.copy(alpha = 0.28f),
                                label = "mystery_slot_bg"
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(slotBg)
                                    .border(
                                        width = 3.dp,
                                        color = if (solvedToken != null) EmeraldMeadow else HoneyGold,
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = solvedToken?.emoji ?: "?",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (solvedToken != null) Color.Unspecified else MangoPrimary
                                )
                            }
                        }

                        if (wrongEmoji != null) {
                            Text(
                                text = strings.tryAgainHint,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = BerryCoral
                            )
                        }
                    }
                }
            }

            // 3 Oversized 3D Answer Choices
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    question.choices.forEachIndexed { idx, choice ->
                        val isWrong = wrongEmoji == choice.emoji
                        Cartoon3DCard(
                            containerColor = if (isWrong) BerryCoral.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface,
                            rimColor = choice.color.darkenForRim(0.8f),
                            cornerRadius = 24.dp,
                            bevelDepth = 6.dp,
                            contentPadding = PaddingValues(vertical = 20.dp, horizontal = 8.dp),
                            onClick = {
                                if (solvedToken != null) return@Cartoon3DCard
                                if (choice.emoji == question.answer.emoji) {
                                    solvedToken = choice
                                    wrongEmoji = null
                                    score += 20
                                    onPlayEffect(SoundEffect.CORRECT_CHIME)
                                    val spoken = if (language == AppLanguage.BANGLA) choice.labelBn else choice.labelEn
                                    onSpeakText(spoken)
                                    scope.launch {
                                        delay(750L)
                                        solvedToken = null
                                        if (roundIndex + 1 >= rounds.size) {
                                            finished = true
                                            onFinish(score, 20)
                                        } else {
                                            roundIndex += 1
                                        }
                                    }
                                } else {
                                    wrongEmoji = choice.emoji
                                    onPlayEffect(SoundEffect.TRY_AGAIN_BOOP)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("pattern_choice_$idx")
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = choice.emoji, fontSize = 42.sp)
                                Text(
                                    text = if (language == AppLanguage.BANGLA) choice.labelBn else choice.labelEn,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PuzzleVictoryCard(
    score: Int,
    starsEarned: Int,
    language: AppLanguage,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalAppStrings.current
    Cartoon3DCard(
        containerColor = EmeraldMeadow,
        rimColor = EmeraldRim,
        cornerRadius = 28.dp,
        bevelDepth = 6.dp,
        contentPadding = PaddingValues(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "🏆", fontSize = 62.sp)
            Text(
                text = strings.puzzleSolvedBanner,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = "${strings.scoreLabel}: ${score.toLocalizedDigits(language)} • +${starsEarned.toLocalizedDigits(language)}★",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.94f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Cartoon3DButton(
                    text = strings.playAgain,
                    icon = Icons.Rounded.Replay,
                    containerColor = HoneyGold,
                    rimColor = HoneyRim,
                    contentColor = Color(0xFF3E2600),
                    onClick = onPlayAgain,
                    modifier = Modifier.weight(1f)
                )
                Cartoon3DButton(
                    text = strings.backToArcade,
                    icon = Icons.Rounded.Star,
                    containerColor = CeruleanSky,
                    rimColor = CeruleanRim,
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
