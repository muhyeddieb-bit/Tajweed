package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Chapter
import com.example.data.model.MatnId
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Gold100
import com.example.ui.theme.Gold200
import com.example.ui.theme.Gold50
import com.example.ui.theme.Gold600
import com.example.ui.theme.Gold700
import com.example.ui.theme.NaturalBg
import com.example.ui.theme.NaturalBorder
import com.example.ui.theme.NaturalForest
import com.example.ui.theme.NaturalForestDark
import com.example.ui.theme.NaturalSage
import com.example.ui.theme.NaturalSageLight
import com.example.ui.theme.NaturalSand
import com.example.ui.theme.NaturalSandLight
import com.example.ui.theme.NaturalSurface
import com.example.ui.theme.NaturalTerracotta
import com.example.ui.theme.NaturalTextPrimary
import com.example.ui.theme.NaturalTextSecondary
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StudyTab
import com.example.ui.viewmodel.TajweedViewModel
import kotlin.math.roundToInt

enum class VisualMapScope(val titleAr: String) {
    BOTH("كلا المتنين"),
    TUHFAT("تحفة الأطفال"),
    JAZARIYYAH("المقدمة الجزرية")
}

enum class ChapterProgressStatus {
    COMPLETED,
    IN_PROGRESS,
    NOT_STARTED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualMapScreen(
    viewModel: TajweedViewModel,
    modifier: Modifier = Modifier
) {
    val memorizedVerses by viewModel.memorizedVerses.collectAsState()
    val quizScores by viewModel.quizScores.collectAsState()

    val tuhfaChapters = remember { viewModel.repository.getAllChapters(MatnId.TUHFAT_AL_ATFAL) }
    val jazariyyahChapters = remember { viewModel.repository.getAllChapters(MatnId.AL_JAZARIYYAH) }

    var selectedScope by remember { mutableStateOf(VisualMapScope.BOTH) }
    var activeChapterForSheet by remember { mutableStateOf<Chapter?>(null) }

    // Zoom and pan states
    var scale by remember { mutableFloatStateOf(1.0f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "الخريطة البصرية التفاعلية",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "شجرة متني الجزرية وتحفة الأطفال التفاعلية",
                            fontSize = 12.sp,
                            color = Gold100
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        modifier = Modifier.testTag("visual_map_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الرجوع للرئيسية",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scale = 1.0f
                            panOffset = Offset.Zero
                        },
                        modifier = Modifier.testTag("visual_map_reset_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "إعادة ضبط العرض",
                            tint = Gold100
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Emerald900,
                    titleContentColor = Color.White
                )
            )
        },
        modifier = modifier.testTag("visual_map_screen")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(NaturalBg)
        ) {
            // Interactive 2D Zoom/Pan Map Container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(0.5f, 2.3f)
                            panOffset += pan
                        }
                    }
            ) {
                // Zoomable & Pannable Graph Surface
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = panOffset.x,
                            translationY = panOffset.y
                        )
                ) {
                    VisualMapTreeContent(
                        selectedScope = selectedScope,
                        tuhfaChapters = tuhfaChapters,
                        jazariyyahChapters = jazariyyahChapters,
                        memorizedVerses = memorizedVerses,
                        quizScores = quizScores,
                        onChapterClick = { chapter ->
                            activeChapterForSheet = chapter
                        }
                    )
                }
            }

            // Top Scope & Legend Header Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Scope Filter Row
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NaturalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "نطاق الخريطة:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaturalForestDark
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                VisualMapScope.entries.forEach { scope ->
                                    FilterChip(
                                        selected = selectedScope == scope,
                                        onClick = { selectedScope = scope },
                                        label = {
                                            Text(
                                                text = scope.titleAr,
                                                fontSize = 11.sp,
                                                fontWeight = if (selectedScope == scope) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = NaturalForest,
                                            selectedLabelColor = Color.White,
                                            containerColor = NaturalSageLight,
                                            labelColor = NaturalTextPrimary
                                        ),
                                        modifier = Modifier.testTag("scope_chip_${scope.name}")
                                    )
                                }
                            }
                        }

                        // Progress Legend
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LegendIndicator(color = Emerald600, label = "مكتمل (100%)")
                            LegendIndicator(color = Gold600, label = "قيد الحفظ")
                            LegendIndicator(color = Color(0xFF9E9E9E), label = "لم يبدأ")
                            Text(
                                text = "💡 اسحب وحرك للتكبير والتنقل",
                                fontSize = 10.sp,
                                color = NaturalTextSecondary
                            )
                        }
                    }
                }
            }

            // Floating Controls HUD (Bottom Left for intuitive reach)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Zoom in (+)
                FloatingActionButton(
                    onClick = { scale = (scale + 0.2f).coerceAtMost(2.3f) },
                    containerColor = NaturalSurface,
                    contentColor = NaturalForest,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp),
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("zoom_in_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "تكبير الخريطة")
                }

                // Zoom percentage indicator
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NaturalForestDark,
                    shadowElevation = 2.dp,
                    modifier = Modifier.widthIn(min = 44.dp)
                ) {
                    Text(
                        text = "${(scale * 100).roundToInt()}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                // Zoom out (-)
                FloatingActionButton(
                    onClick = { scale = (scale - 0.2f).coerceAtLeast(0.5f) },
                    containerColor = NaturalSurface,
                    contentColor = NaturalForest,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp),
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("zoom_out_button")
                ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "تصغير الخريطة")
                }

                // Center & Reset
                FloatingActionButton(
                    onClick = {
                        scale = 1.0f
                        panOffset = Offset.Zero
                    },
                    containerColor = NaturalSage,
                    contentColor = NaturalForestDark,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp),
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("reset_view_button")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "إعادة ضبط التمركز")
                }
            }

            // Chapter Action ModalBottomSheet
            if (activeChapterForSheet != null) {
                val chapter = activeChapterForSheet!!
                ChapterNodeActionSheet(
                    chapter = chapter,
                    memorizedVerses = memorizedVerses,
                    quizScores = quizScores,
                    onDismiss = { activeChapterForSheet = null },
                    onStartStudy = { initialTab ->
                        activeChapterForSheet = null
                        viewModel.selectChapter(chapter, initialTab)
                    },
                    onViewVersesOnly = {
                        activeChapterForSheet = null
                        viewModel.openChapterVersesOnly(chapter)
                    }
                )
            }
        }
    }
}

@Composable
private fun LegendIndicator(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(text = label, fontSize = 10.sp, color = NaturalTextSecondary)
    }
}

@Composable
fun VisualMapTreeContent(
    selectedScope: VisualMapScope,
    tuhfaChapters: List<Chapter>,
    jazariyyahChapters: List<Chapter>,
    memorizedVerses: Set<String>,
    quizScores: Map<String, Int>,
    onChapterClick: (Chapter) -> Unit,
    modifier: Modifier = Modifier
) {
    // Generous canvas bounds allowing smooth panning in both directions
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 100.dp, bottom = 120.dp, start = 16.dp, end = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        // Display Tuhfat Al-Atfal Branch if selected
        if (selectedScope == VisualMapScope.BOTH || selectedScope == VisualMapScope.TUHFAT) {
            MatnBranchSection(
                matnId = MatnId.TUHFAT_AL_ATFAL,
                title = "متن تحفة الأطفال",
                author = "الإمام سليمان الجمزوري",
                totalVerses = 61,
                chapters = tuhfaChapters,
                accentColor = NaturalForest,
                accentLightColor = NaturalSage,
                memorizedVerses = memorizedVerses,
                quizScores = quizScores,
                onChapterClick = onChapterClick
            )
        }

        // Connecting bridge if both are visible
        if (selectedScope == VisualMapScope.BOTH) {
            DualUnitBridgeDivider()
        }

        // Display Al-Jazariyyah Branch if selected
        if (selectedScope == VisualMapScope.BOTH || selectedScope == VisualMapScope.JAZARIYYAH) {
            MatnBranchSection(
                matnId = MatnId.AL_JAZARIYYAH,
                title = "منظومة المقدمة (الجزرية)",
                author = "الإمام شمس الدين ابن الجزري",
                totalVerses = 109,
                chapters = jazariyyahChapters,
                accentColor = NaturalTerracotta,
                accentLightColor = NaturalSand,
                memorizedVerses = memorizedVerses,
                quizScores = quizScores,
                onChapterClick = onChapterClick
            )
        }
    }
}

@Composable
fun DualUnitBridgeDivider() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = NaturalBorder, thickness = 1.5.dp)
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Gold50,
            border = androidx.compose.foundation.BorderStroke(1.dp, Gold200),
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "❖", fontSize = 14.sp, color = Gold700)
                Text(
                    text = "منظومتا التجويد المباركتان",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaturalForestDark
                )
                Text(text = "❖", fontSize = 14.sp, color = Gold700)
            }
        }
        HorizontalDivider(modifier = Modifier.weight(1f), color = NaturalBorder, thickness = 1.5.dp)
    }
}

@Composable
fun MatnBranchSection(
    matnId: MatnId,
    title: String,
    author: String,
    totalVerses: Int,
    chapters: List<Chapter>,
    accentColor: Color,
    accentLightColor: Color,
    memorizedVerses: Set<String>,
    quizScores: Map<String, Int>,
    onChapterClick: (Chapter) -> Unit
) {
    // Calculate total memorized for this unit
    val unitMemorizedCount = chapters.sumOf { ch ->
        ch.verses.count { bayt -> memorizedVerses.contains("${matnId.name}_${bayt.number}") }
    }
    val unitPercentage = if (totalVerses > 0) (unitMemorizedCount * 100) / totalVerses else 0

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 1. Root Unit Node (العقدة الرئيسية) ---
        RootUnitNodeCard(
            title = title,
            author = author,
            totalChapters = chapters.size,
            totalVerses = totalVerses,
            memorizedVerses = unitMemorizedCount,
            percentage = unitPercentage,
            accentColor = accentColor,
            accentLightColor = accentLightColor
        )

        // --- 2. Connecting Trunk Line with visual tree branches ---
        Canvas(
            modifier = Modifier
                .width(4.dp)
                .height(32.dp)
        ) {
            drawLine(
                brush = Brush.verticalGradient(listOf(accentColor, accentColor.copy(alpha = 0.4f))),
                start = Offset(size.width / 2, 0f),
                end = Offset(size.width / 2, size.height),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // --- 3. Branch Children Chapters Layout ---
        // Displaying chapters in a structured 2-column alternating branch layout
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Group chapters in pairs for a balanced mindmap tree
            val chunkedChapters = chapters.chunked(2)
            chunkedChapters.forEachIndexed { pairIndex, pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // First chapter in pair
                    val firstChapter = pair[0]
                    ChapterNodeCard(
                        chapter = firstChapter,
                        index = pairIndex * 2 + 1,
                        memorizedVerses = memorizedVerses,
                        quizScore = quizScores[firstChapter.id],
                        onClick = { onChapterClick(firstChapter) },
                        modifier = Modifier.weight(1f)
                    )

                    // Second chapter if exists
                    if (pair.size > 1) {
                        val secondChapter = pair[1]
                        ChapterNodeCard(
                            chapter = secondChapter,
                            index = pairIndex * 2 + 2,
                            memorizedVerses = memorizedVerses,
                            quizScore = quizScores[secondChapter.id],
                            onClick = { onChapterClick(secondChapter) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun RootUnitNodeCard(
    title: String,
    author: String,
    totalChapters: Int,
    totalVerses: Int,
    memorizedVerses: Int,
    percentage: Int,
    accentColor: Color,
    accentLightColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NaturalSurface),
        border = androidx.compose.foundation.BorderStroke(2.dp, accentColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            accentLightColor.copy(alpha = 0.35f),
                            Color.White
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(accentColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalTextPrimary
                        )
                        Text(
                            text = author,
                            fontSize = 12.sp,
                            color = NaturalTextSecondary
                        )
                    }
                }

                // Progress badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (percentage == 100) Emerald700 else accentColor,
                    contentColor = Color.White
                ) {
                    Text(
                        text = "$percentage%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar and details
            LinearProgressIndicator(
                progress = { if (totalVerses > 0) memorizedVerses.toFloat() / totalVerses else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (percentage == 100) Emerald600 else accentColor,
                trackColor = accentLightColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "عدد الأبواب: $totalChapters باباً",
                    fontSize = 11.sp,
                    color = NaturalTextSecondary
                )
                Text(
                    text = "المحفوظ: $memorizedVerses من $totalVerses بيتاً",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NaturalForestDark
                )
            }
        }
    }
}

@Composable
fun ChapterNodeCard(
    chapter: Chapter,
    index: Int,
    memorizedVerses: Set<String>,
    quizScore: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalVerses = chapter.verses.size
    val memorizedCount = chapter.verses.count { bayt ->
        memorizedVerses.contains("${chapter.matnId.name}_${bayt.number}")
    }

    val status = when {
        totalVerses > 0 && memorizedCount == totalVerses -> ChapterProgressStatus.COMPLETED
        memorizedCount > 0 -> ChapterProgressStatus.IN_PROGRESS
        else -> ChapterProgressStatus.NOT_STARTED
    }

    val (cardBg, borderColor, statusText, statusBadgeBg, statusBadgeText) = when (status) {
        ChapterProgressStatus.COMPLETED -> CardStyle(
            bg = Emerald50,
            border = Emerald600,
            statusLabel = "مكتمل ✓",
            badgeBg = Emerald600,
            badgeTextColor = Color.White
        )
        ChapterProgressStatus.IN_PROGRESS -> CardStyle(
            bg = Gold50,
            border = Gold600,
            statusLabel = "$memorizedCount/$totalVerses بيت",
            badgeBg = Gold600,
            badgeTextColor = Color.White
        )
        ChapterProgressStatus.NOT_STARTED -> CardStyle(
            bg = Color.White,
            border = NaturalBorder,
            statusLabel = "لم يبدأ",
            badgeBg = NaturalSageLight,
            badgeTextColor = NaturalTextSecondary
        )
    }

    Card(
        modifier = modifier
            .shadow(if (status == ChapterProgressStatus.COMPLETED) 4.dp else 2.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("chapter_node_${chapter.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = androidx.compose.foundation.BorderStroke(
            width = if (status == ChapterProgressStatus.COMPLETED) 2.dp else 1.2.dp,
            color = borderColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header with Chapter Index and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = when (status) {
                        ChapterProgressStatus.COMPLETED -> Emerald700
                        ChapterProgressStatus.IN_PROGRESS -> Gold700
                        ChapterProgressStatus.NOT_STARTED -> NaturalForest
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "$index",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusBadgeBg
                ) {
                    Text(
                        text = statusText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusBadgeText,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chapter Title
            Text(
                text = chapter.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NaturalTextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Bayt Range
            Text(
                text = "الأبيات: ${chapter.startBayt} - ${chapter.endBayt} (${totalVerses} بيتاً)",
                fontSize = 11.sp,
                color = NaturalTextSecondary
            )

            // Progress bar
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { if (totalVerses > 0) memorizedCount.toFloat() / totalVerses else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = when (status) {
                    ChapterProgressStatus.COMPLETED -> Emerald600
                    ChapterProgressStatus.IN_PROGRESS -> Gold600
                    ChapterProgressStatus.NOT_STARTED -> NaturalSage
                },
                trackColor = Color(0xFFE8ECE5)
            )

            // Quiz score badge if available
            if (quizScore != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "★", fontSize = 11.sp, color = Gold700)
                    Text(
                        text = "نتيجة الاختبار: $quizScore%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Gold700
                    )
                }
            }
        }
    }
}

private data class CardStyle(
    val bg: Color,
    val border: Color,
    val statusLabel: String,
    val badgeBg: Color,
    val badgeTextColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterNodeActionSheet(
    chapter: Chapter,
    memorizedVerses: Set<String>,
    quizScores: Map<String, Int>,
    onDismiss: () -> Unit,
    onStartStudy: (StudyTab) -> Unit,
    onViewVersesOnly: () -> Unit
) {
    val totalVerses = chapter.verses.size
    val memorizedCount = chapter.verses.count { bayt ->
        memorizedVerses.contains("${chapter.matnId.name}_${bayt.number}")
    }
    val percentage = if (totalVerses > 0) (memorizedCount * 100) / totalVerses else 0
    val quizScore = quizScores[chapter.id]

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = NaturalSurface,
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header: Chapter Title & Matn
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NaturalSage
                    ) {
                        Text(
                            text = chapter.matnId.titleAr,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalForestDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = chapter.title,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalTextPrimary,
                        lineHeight = 24.sp
                    )

                    Text(
                        text = "الأبيات من ${chapter.startBayt} إلى ${chapter.endBayt} • إجمالي $totalVerses بيتاً",
                        fontSize = 12.sp,
                        color = NaturalTextSecondary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق",
                        tint = NaturalTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NaturalSageLight),
                border = androidx.compose.foundation.BorderStroke(1.dp, NaturalBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "نسبة الإتقان", fontSize = 11.sp, color = NaturalTextSecondary)
                        Text(
                            text = "$percentage%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (percentage == 100) Emerald700 else NaturalForest
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(NaturalBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "المحفوظ", fontSize = 11.sp, color = NaturalTextSecondary)
                        Text(
                            text = "$memorizedCount من $totalVerses",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalTextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(NaturalBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "اختبار الباب", fontSize = 11.sp, color = NaturalTextSecondary)
                        Text(
                            text = if (quizScore != null) "$quizScore%" else "لم يُختبر",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (quizScore != null) Gold700 else NaturalTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "اختر طريقة المتابعة:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NaturalForestDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Option 1: البدء بالدراسة (الحفظ، الشرح، التقييم)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStartStudy(StudyTab.MEMORIZATION) }
                    .testTag("action_start_study"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Emerald900),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Gold100),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = Emerald900,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "البدء بالدراسة الشاملة",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "الحفظ الصوتي • الشرح التفاعلي • التقييم",
                                    fontSize = 11.sp,
                                    color = Gold100
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Direct sub-tab buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StudySubActionChip(
                            label = "الحفظ والتكرار",
                            icon = Icons.Default.PlayArrow,
                            onClick = { onStartStudy(StudyTab.MEMORIZATION) },
                            modifier = Modifier.weight(1f)
                        )
                        StudySubActionChip(
                            label = "الشرح والرسوم",
                            icon = Icons.Default.Lightbulb,
                            onClick = { onStartStudy(StudyTab.UNDERSTANDING) },
                            modifier = Modifier.weight(1f)
                        )
                        StudySubActionChip(
                            label = "الاختبار",
                            icon = Icons.Default.Quiz,
                            onClick = { onStartStudy(StudyTab.QUIZ) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Option 2: عرض الأبيات فقط
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewVersesOnly() }
                    .testTag("action_view_verses_only"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NaturalSandLight),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Gold200)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(NaturalSand),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = null,
                                tint = NaturalTerracotta,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "عرض الأبيات فقط",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaturalTextPrimary
                            )
                            Text(
                                text = "قراءة منظومة الباب مع خيارات تكبير الخط والاستماع",
                                fontSize = 11.sp,
                                color = NaturalTextSecondary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.AutoStories,
                        contentDescription = null,
                        tint = NaturalTerracotta
                    )
                }
            }
        }
    }
}

@Composable
private fun StudySubActionChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = Color.White.copy(alpha = 0.15f),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Gold100,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}
