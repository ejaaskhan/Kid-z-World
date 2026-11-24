package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.SoundEffect
import com.example.core.localization.AppLanguage
import com.example.core.localization.LocalAppStrings
import com.example.domain.model.LessonItem
import com.example.ui.theme.BerryCoral
import com.example.ui.theme.CeruleanSky
import com.example.ui.theme.EmeraldMeadow
import com.example.ui.theme.GrapePop
import com.example.ui.theme.HoneyGold
import com.example.ui.theme.MangoPrimary
import com.example.ui.theme.darkenForRim

private data class CrayonStroke(
    val color: Color,
    val points: List<Offset>
)

@Composable
fun LetterTracingStudio(
    item: LessonItem,
    language: AppLanguage,
    canGoPrev: Boolean,
    canGoNext: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onReplayAudio: () -> Unit,
    onPlayEffect: (SoundEffect) -> Unit,
    onCompleteTrace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val crayonColors = listOf(MangoPrimary, CeruleanSky, EmeraldMeadow, BerryCoral, GrapePop, HoneyGold)
    var selectedColor by remember { mutableStateOf(crayonColors.first()) }
    val strokes = remember(item.id) { mutableStateListOf<CrayonStroke>() }
    var currentPoints by remember(item.id) { mutableStateOf<List<Offset>>(emptyList()) }

    LaunchedEffect(item.id) {
        strokes.clear()
        currentPoints = emptyList()
    }

    val displayGlyph = item.displaySymbol(language)
    val accent = Color(item.accentColorHex)

    Cartoon3DCard(
        containerColor = MaterialTheme.colorScheme.surface,
        rimColor = accent.darkenForRim(0.8f),
        cornerRadius = 28.dp,
        bevelDepth = 6.dp,
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("letter_tracing_studio")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Prev / Letter Info / Next
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    enabled = canGoPrev,
                    onClick = onPrev,
                    modifier = Modifier.testTag("trace_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = strings.prevLesson,
                        tint = if (canGoPrev) accent else MaterialTheme.colorScheme.outline
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${item.visualBadge} ${item.displayTitle(language)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = strings.traceHint,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                IconButton(
                    enabled = canGoNext,
                    onClick = onNext,
                    modifier = Modifier.testTag("trace_next_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = strings.nextLesson,
                        tint = if (canGoNext) accent else MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Glowing Crayon Guide Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(236.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(accent.copy(alpha = 0.08f))
                    .border(2.5.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                    .pointerInput(item.id, selectedColor) {
                        detectDragGestures(
                            onDragStart = { start ->
                                currentPoints = listOf(start)
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                currentPoints = currentPoints + change.position
                            },
                            onDragEnd = {
                                if (currentPoints.size >= 2) {
                                    strokes.add(CrayonStroke(selectedColor, currentPoints))
                                    onPlayEffect(SoundEffect.TAP_POP)
                                }
                                currentPoints = emptyList()
                            },
                            onDragCancel = {
                                currentPoints = emptyList()
                            }
                        )
                    }
                    .testTag("tracing_canvas"),
                contentAlignment = Alignment.Center
            ) {
                // Soft Guide Watermark Letter
                Text(
                    text = displayGlyph,
                    fontSize = 152.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accent.copy(alpha = 0.22f),
                    textAlign = TextAlign.Center
                )

                // Multi-stroke Glowing Crayon Layer
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Subtle ruled preschool handwriting guidelines
                    val topY = size.height * 0.22f
                    val midY = size.height * 0.50f
                    val botY = size.height * 0.78f
                    drawLine(
                        color = accent.copy(alpha = 0.14f),
                        start = Offset(24f, topY),
                        end = Offset(size.width - 24f, topY),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = accent.copy(alpha = 0.18f),
                        start = Offset(24f, midY),
                        end = Offset(size.width - 24f, midY),
                        strokeWidth = 2.5f
                    )
                    drawLine(
                        color = accent.copy(alpha = 0.14f),
                        start = Offset(24f, botY),
                        end = Offset(size.width - 24f, botY),
                        strokeWidth = 3f
                    )

                    fun drawGlowingStroke(color: Color, pts: List<Offset>) {
                        if (pts.size < 2) return
                        val path = Path().apply {
                            moveTo(pts.first().x, pts.first().y)
                            for (i in 1 until pts.size) {
                                val prev = pts[i - 1]
                                val curr = pts[i]
                                quadraticTo(prev.x, prev.y, (prev.x + curr.x) / 2f, (prev.y + curr.y) / 2f)
                            }
                        }
                        // Outer soft glow
                        drawPath(
                            path = path,
                            color = color.copy(alpha = 0.30f),
                            style = Stroke(width = 34f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                        // Vibrant crayon core
                        drawPath(
                            path = path,
                            color = color,
                            style = Stroke(width = 18f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }

                    strokes.forEach { drawGlowingStroke(it.color, it.points) }
                    if (currentPoints.size >= 2) {
                        drawGlowingStroke(selectedColor, currentPoints)
                    }
                }
            }

            // Crayon Palette + Undo & Clear Tools
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    crayonColors.forEachIndexed { idx, color ->
                        val active = color == selectedColor
                        Box(
                            modifier = Modifier
                                .size(if (active) 38.dp else 32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (active) 3.dp else 1.5.dp,
                                    color = if (active) MaterialTheme.colorScheme.onSurface else Color.White,
                                    shape = CircleShape
                                )
                                .clickable {
                                    onPlayEffect(SoundEffect.TAP_POP)
                                    selectedColor = color
                                }
                                .testTag("crayon_color_$idx")
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        enabled = strokes.isNotEmpty(),
                        onClick = {
                            if (strokes.isNotEmpty()) {
                                strokes.removeAt(strokes.lastIndex)
                                onPlayEffect(SoundEffect.TAP_POP)
                            }
                        },
                        modifier = Modifier.testTag("trace_undo_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Undo,
                            contentDescription = strings.traceUndo,
                            tint = if (strokes.isNotEmpty()) GrapePop else MaterialTheme.colorScheme.outline
                        )
                    }
                    IconButton(
                        enabled = strokes.isNotEmpty(),
                        onClick = {
                            strokes.clear()
                            onPlayEffect(SoundEffect.TAP_POP)
                        },
                        modifier = Modifier.testTag("trace_clear_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CleaningServices,
                            contentDescription = strings.traceClear,
                            tint = if (strokes.isNotEmpty()) BerryCoral else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            // Audio Replay & Star Reward Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Cartoon3DButton(
                    text = strings.replayAudio,
                    icon = Icons.AutoMirrored.Rounded.VolumeUp,
                    containerColor = CeruleanSky,
                    onClick = onReplayAudio,
                    modifier = Modifier.weight(1f)
                )
                Cartoon3DButton(
                    text = strings.traceFinishReward,
                    icon = Icons.Rounded.Star,
                    containerColor = EmeraldMeadow,
                    enabled = strokes.isNotEmpty(),
                    onClick = onCompleteTrace,
                    modifier = Modifier
                        .weight(1.1f)
                        .testTag("trace_finish_button")
                )
            }
        }
    }
}
