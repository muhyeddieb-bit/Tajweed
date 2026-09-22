package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Bayt
import com.example.data.model.DiagramItem
import com.example.data.model.ExplanationPoint
import com.example.data.model.QuizQuestion
import com.example.data.model.VisualDiagramData
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.Gold100
import com.example.ui.theme.Gold200
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold50
import com.example.ui.theme.Gold500
import com.example.ui.theme.Gold600
import com.example.ui.theme.Gold700
import com.example.ui.theme.NaturalSage
import com.example.ui.theme.NaturalSageLight
import com.example.ui.theme.SuccessGreen

@Composable
fun IslamicHeaderBanner(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Emerald800)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Emerald900, Emerald800, Color(0xFF4C7E44))
                    )
                )
                .padding(22.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    style = MaterialTheme.typography.titleMedium,
                    color = Gold100,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Emerald100,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun BaytItemCard(
    bayt: Bayt,
    isMemorized: Boolean,
    isBookmarked: Boolean,
    isPlaying: Boolean,
    onPlayClick: () -> Unit,
    onMemorizeToggle: () -> Unit,
    onBookmarkToggle: () -> Unit,
    modifier: Modifier = Modifier,
    fontSize: Float = 21f,
    chapterTitle: String? = null,
    explanationSnippet: String? = null,
    onOpenExplanationDetail: ((Bayt) -> Unit)? = null
) {
    val context = LocalContext.current
    var isExplanationExpanded by remember { mutableStateOf(false) }

    val containerBg by animateColorAsState(
        targetValue = if (isPlaying) Gold50 else if (isMemorized) Emerald50 else Color.White,
        label = "baytBg"
    )
    val borderColor = if (isPlaying) Gold700 else if (isMemorized) Emerald600.copy(alpha = 0.5f) else Color(0xFFE1E9DB)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .border(if (isPlaying) 2.dp else 1.dp, borderColor, RoundedCornerShape(20.dp))
            .testTag("bayt_card_${bayt.number}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPlaying) 3.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header row with Bayt Number & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Bayt number pill & Optional chapter title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isPlaying) Gold700 else Emerald800,
                        contentColor = Color.White,
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Text(
                            text = "البيت ${bayt.number}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    if (!chapterTitle.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NaturalSage,
                            contentColor = Emerald900
                        ) {
                            Text(
                                text = chapterTitle,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Action Buttons: Audio Play, Memorize check, Bookmark, Explanation toggle, Copy
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Audio Button
                    IconButton(
                        onClick = onPlayClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("play_bayt_${bayt.number}")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "إيقاف الصوت" else "استمع للبيت",
                            tint = if (isPlaying) Gold700 else Emerald800
                        )
                    }

                    // Memorize Check Button
                    IconButton(
                        onClick = onMemorizeToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("memorize_bayt_${bayt.number}")
                    ) {
                        Icon(
                            imageVector = if (isMemorized) Icons.Default.CheckCircle else Icons.Default.Check,
                            contentDescription = "تم الحفظ",
                            tint = if (isMemorized) SuccessGreen else Color(0xFF8A8985)
                        )
                    }

                    // Bookmark Button
                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "المفضلة",
                            tint = if (isBookmarked) Gold700 else Color(0xFF8A8985)
                        )
                    }

                    // Explanation Toggle Button (if available)
                    if (explanationSnippet != null) {
                        IconButton(
                            onClick = { isExplanationExpanded = !isExplanationExpanded },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isExplanationExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.Lightbulb,
                                contentDescription = "الشرح والتوجيه",
                                tint = if (isExplanationExpanded) Gold700 else Emerald700
                            )
                        }
                    }

                    // Copy Button
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("بيت التجويد", bayt.fullText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "تم نسخ البيت بنجاح", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "نسخ البيت",
                            tint = Color(0xFF8A8985)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // First Half (الصدر) with dynamic zoomed font size
            Text(
                text = bayt.firstHalf,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = fontSize.sp,
                    lineHeight = (fontSize * 1.72f).sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Central poetic separator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = (fontSize * 0.2f).dp.coerceIn(2.dp, 8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "۞",
                    color = Gold600,
                    fontSize = (fontSize * 0.9f).sp
                )
            }

            // Second Half (العجز) with dynamic zoomed font size
            Text(
                text = bayt.secondHalf,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = fontSize.sp,
                    lineHeight = (fontSize * 1.72f).sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Expandable Explanation Card
            AnimatedVisibility(visible = isExplanationExpanded && explanationSnippet != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .background(Gold50, RoundedCornerShape(12.dp))
                        .border(1.dp, Gold200, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Gold700,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "فائدة وتوجيه تجويدي:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Gold700
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = explanationSnippet ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 22.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (onOpenExplanationDetail != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Emerald800,
                            contentColor = Color.White,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onOpenExplanationDetail(bayt) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = Gold100,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "الشرح المقروء والأطلس التشريحي",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Modern interactive toolbar for zooming font size with steppers, presets, and reset.
 */
@Composable
fun FontSizeZoomControlBar(
    fontSize: Float,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onSetSize: (Float) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("font_size_zoom_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Label & Stepper Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Emerald100, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = null,
                            tint = Emerald900,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "حجم خط الأبيات",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Emerald900
                        )
                        Text(
                            text = "${fontSize.toInt()} نقطة (sp)",
                            style = MaterialTheme.typography.labelSmall,
                            color = Gold700,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Controls: Zoom Out, Reset, Zoom In
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Decrease button (A-)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = NaturalSageLight,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(enabled = fontSize > 15f) { onDecrease() }
                            .testTag("decrease_font_size")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ZoomOut,
                                contentDescription = "تصغير الخط",
                                tint = if (fontSize > 15f) Emerald900 else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "أ-",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (fontSize > 15f) Emerald900 else Color.Gray
                            )
                        }
                    }

                    // Reset button
                    IconButton(
                        onClick = onReset,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("reset_font_size")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "إعادة ضبط الحجم",
                            tint = Color(0xFF8A8985),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Increase button (A+)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Emerald800,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(enabled = fontSize < 36f) { onIncrease() }
                            .testTag("increase_font_size")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ZoomIn,
                                contentDescription = "تكبير الخط",
                                tint = if (fontSize < 36f) Color.White else Color.LightGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "أ+",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (fontSize < 36f) Color.White else Color.LightGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Presets row: 18, 22, 26, 30
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    17f to "صغير",
                    21f to "عادي",
                    26f to "كبير",
                    32f to "كبير جداً"
                ).forEach { (size, label) ->
                    val isSelected = (fontSize.toInt() == size.toInt()) ||
                            (size == 17f && fontSize < 19f) ||
                            (size == 21f && fontSize in 19f..23f) ||
                            (size == 26f && fontSize in 24f..28f) ||
                            (size == 32f && fontSize > 28f)

                    FilterChip(
                        selected = isSelected,
                        onClick = { onSetSize(size) },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald800,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ExplanationPointCard(
    point: ExplanationPoint,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = point.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Emerald800,
                    fontWeight = FontWeight.Bold
                )

                if (point.referenceBayt != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Gold100,
                        contentColor = Gold700
                    ) {
                        Text(
                            text = "الشاهد: بيت ${point.referenceBayt}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = point.details,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 26.sp
            )

            if (point.examples.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "أمثلة من القرآن الكريم:",
                    style = MaterialTheme.typography.labelLarge,
                    color = Gold700,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    point.examples.forEach { example ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Emerald100,
                            contentColor = Emerald900,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = example,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VisualDiagramCard(
    diagram: VisualDiagramData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Gold50),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Gold500, Emerald700)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Emerald800, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = diagram.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Emerald900
                    )
                    Text(
                        text = diagram.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Gold700
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            diagram.items.forEach { item ->
                DiagramItemRow(item = item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DiagramItemRow(item: DiagramItem) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Emerald900
                )
                if (item.badge != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Gold100,
                        contentColor = Gold700
                    ) {
                        Text(
                            text = item.badge,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (item.subItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item.subItems.forEach { sub ->
                        Text(
                            text = "• $sub",
                            style = MaterialTheme.typography.labelSmall,
                            color = Emerald800
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuizQuestionCard(
    question: QuizQuestion,
    questionIndex: Int,
    totalQuestions: Int,
    selectedOption: Int?,
    isSubmitted: Boolean,
    onOptionSelected: (Int) -> Unit,
    onSubmitClick: () -> Unit,
    onNextClick: () -> Unit,
    onReadBaytClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("quiz_question_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header: question counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Emerald100,
                    contentColor = Emerald900
                ) {
                    Text(
                        text = "السؤال ${questionIndex + 1} من $totalQuestions",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Gold100,
                    contentColor = Gold700
                ) {
                    Text(
                        text = "المرجع: بيت ${question.referenceBaytNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Question text
            Text(
                text = question.question,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp, lineHeight = 26.sp),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4 Options
            question.options.forEachIndexed { index, optionText ->
                val isSelected = selectedOption == index
                val isCorrect = index == question.correctIndex

                val optionBg = when {
                    !isSubmitted && isSelected -> Emerald100
                    isSubmitted && isCorrect -> Color(0xFFDCFCE7)
                    isSubmitted && isSelected && !isCorrect -> Color(0xFFFEE2E2)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }

                val optionBorder = when {
                    !isSubmitted && isSelected -> Emerald800
                    isSubmitted && isCorrect -> SuccessGreen
                    isSubmitted && isSelected && !isCorrect -> ErrorRed
                    else -> Color.Transparent
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.5.dp, optionBorder, RoundedCornerShape(12.dp))
                        .clickable(enabled = !isSubmitted) { onOptionSelected(index) }
                        .testTag("quiz_option_$index"),
                    color = optionBg,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Choice badge
                        val optionLetter = when (index) {
                            0 -> "أ"
                            1 -> "ب"
                            2 -> "ج"
                            else -> "د"
                        }
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(
                                    if (isSelected) Emerald800 else Color.White,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = optionLetter,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Emerald900
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        if (isSubmitted) {
                            if (isCorrect) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "صحيح",
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "خطأ",
                                    tint = ErrorRed,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Feedback Card when submitted
            AnimatedVisibility(visible = isSubmitted) {
                val isAnswerCorrect = selectedOption == question.correctIndex
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isAnswerCorrect) Color(0xFFF0FDF4) else Color(0xFFFEF2F2),
                            RoundedCornerShape(14.dp)
                        )
                        .border(
                            1.dp,
                            if (isAnswerCorrect) Color(0xFF86EFAC) else Color(0xFFFCA5A5),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(14.dp)
                ) {
                    Text(
                        text = if (isAnswerCorrect) "إجابة صحيحة ومتقنة! أحسنت 🌟" else "إجابة غير صحيحة، راجع البيت بعناية 📖",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isAnswerCorrect) SuccessGreen else ErrorRed
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = question.feedback,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 24.sp
                    )

                    if (!isAnswerCorrect) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { onReadBaytClick(question.referenceBaytNumber) },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(text = "اقرأ البيت ${question.referenceBaytNumber} لتثبيت الفهم")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Button (Submit or Next)
            if (!isSubmitted) {
                Button(
                    onClick = onSubmitClick,
                    enabled = selectedOption != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_quiz_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald800)
                ) {
                    Text(
                        text = "تأكيد الإجابة",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else {
                Button(
                    onClick = onNextClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("next_quiz_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald800)
                ) {
                    Text(
                        text = if (questionIndex + 1 < totalQuestions) "السؤال التالي" else "عرض نتيجة الاختبار",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
