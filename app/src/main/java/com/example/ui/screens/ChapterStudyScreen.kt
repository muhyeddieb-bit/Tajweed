package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Chapter
import com.example.data.repository.AnatomicalArea
import com.example.ui.components.AnatomicalVocalTractCanvas
import com.example.ui.components.BaytItemCard
import com.example.ui.components.ExplanationPointCard
import com.example.ui.components.FontSizeZoomControlBar
import com.example.ui.components.QuizQuestionCard
import com.example.ui.components.VisualDiagramCard
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Gold100
import com.example.ui.theme.Gold500
import com.example.ui.theme.Gold700
import com.example.ui.theme.NaturalBg
import com.example.ui.theme.NaturalBorder
import com.example.ui.theme.NaturalForest
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StudyTab
import com.example.ui.viewmodel.TajweedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterStudyScreen(
    viewModel: TajweedViewModel,
    modifier: Modifier = Modifier
) {
    val chapter = viewModel.selectedChapter.collectAsState().value
    val activeTab by viewModel.activeStudyTab.collectAsState()
    val memorizedVerses by viewModel.memorizedVerses.collectAsState()
    val bookmarkedVerses by viewModel.bookmarkedVerses.collectAsState()
    val isPlaying by viewModel.ttsManager.isPlaying.collectAsState()
    val playingBayt by viewModel.ttsManager.currentPlayingBayt.collectAsState()
    val repeatCount by viewModel.ttsManager.repeatCount.collectAsState()

    // Quiz states
    val questionIndex by viewModel.quizQuestionIndex.collectAsState()
    val selectedOption by viewModel.selectedQuizOption.collectAsState()
    val isSubmitted by viewModel.isQuizSubmitted.collectAsState()
    val correctCount by viewModel.quizCorrectAnswersCount.collectAsState()
    val isCompleted by viewModel.isQuizCompleted.collectAsState()

    if (chapter == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("لم يتم تحديد باب")
        }
        return
    }

    Scaffold(
        containerColor = NaturalBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = chapter.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "الأبيات: ${chapter.startBayt} - ${chapter.endBayt} • ${chapter.commentarySource}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Emerald100,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.ttsManager.stop()
                            viewModel.navigateTo(Screen.CHAPTERS)
                        },
                        modifier = Modifier.testTag("back_to_chapters")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع للأبواب",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.AI_TUTOR) }) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "اسأل خبير التجويد حول هذا الباب",
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
        modifier = modifier.testTag("study_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Three Tabs (أ. الحفظ | ب. الفهم | ج. التقييم)
            SecondaryTabRow(
                selectedTabIndex = activeTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Emerald900
            ) {
                StudyTab.entries.forEach { tab ->
                    Tab(
                        selected = activeTab == tab,
                        onClick = { viewModel.setStudyTab(tab) },
                        text = {
                            Text(
                                text = tab.titleAr,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (activeTab == tab) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("study_tab_${tab.name}")
                    )
                }
            }

            // Tab Content
            when (activeTab) {
                StudyTab.MEMORIZATION -> {
                    MemorizationTabContent(
                        chapter = chapter,
                        viewModel = viewModel,
                        memorizedVerses = memorizedVerses,
                        bookmarkedVerses = bookmarkedVerses,
                        isPlaying = isPlaying,
                        playingBayt = playingBayt,
                        repeatCount = repeatCount
                    )
                }
                StudyTab.UNDERSTANDING -> {
                    UnderstandingTabContent(chapter = chapter, viewModel = viewModel)
                }
                StudyTab.QUIZ -> {
                    QuizTabContent(
                        chapter = chapter,
                        questionIndex = questionIndex,
                        selectedOption = selectedOption,
                        isSubmitted = isSubmitted,
                        correctCount = correctCount,
                        isCompleted = isCompleted,
                        onOptionSelected = { viewModel.selectQuizOption(it) },
                        onSubmit = { viewModel.submitQuizAnswer() },
                        onNext = { viewModel.nextQuizQuestion() },
                        onReset = { viewModel.resetQuizState() },
                        onReadBayt = { baytNum ->
                            // Switch to Memorization Tab so student can re-read
                            viewModel.setStudyTab(StudyTab.MEMORIZATION)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MemorizationTabContent(
    chapter: Chapter,
    viewModel: TajweedViewModel,
    memorizedVerses: Set<String>,
    bookmarkedVerses: Set<String>,
    isPlaying: Boolean,
    playingBayt: Int?,
    repeatCount: Int
) {
    val fontSize by viewModel.verseFontSize.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Audio Controls & Repeat Selector Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Gold100)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = null,
                            tint = Gold700,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تكرار الاستماع للحفظ:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Gold700
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(1, 3, 5).forEach { count ->
                            val isSelected = repeatCount == count
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Emerald800 else Color.White,
                                contentColor = if (isSelected) Color.White else Emerald900,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.ttsManager.setRepeatCount(count) }
                            ) {
                                Text(
                                    text = "$count مَرَّات",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Font Size Zoom Toolbar
        item {
            FontSizeZoomControlBar(
                fontSize = fontSize,
                onIncrease = { viewModel.increaseVerseFontSize() },
                onDecrease = { viewModel.decreaseVerseFontSize() },
                onSetSize = { viewModel.setVerseFontSize(it) },
                onReset = { viewModel.resetVerseFontSize() }
            )
        }

        // List of Verses
        items(chapter.verses) { bayt ->
            val isMemorized = memorizedVerses.contains("${bayt.matnId.name}_${bayt.number}")
            val isBookmarked = bookmarkedVerses.contains("${bayt.matnId.name}_${bayt.number}")
            val isBaytPlaying = isPlaying && playingBayt == bayt.number
            val explanation = viewModel.repository.getExplanationForBayt(bayt)

            BaytItemCard(
                bayt = bayt,
                isMemorized = isMemorized,
                isBookmarked = isBookmarked,
                isPlaying = isBaytPlaying,
                fontSize = fontSize,
                explanationSnippet = explanation,
                onPlayClick = { viewModel.ttsManager.speakBayt(bayt) },
                onMemorizeToggle = { viewModel.toggleMemorize(bayt) },
                onBookmarkToggle = { viewModel.toggleBookmark(bayt) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun UnderstandingTabContent(chapter: Chapter, viewModel: TajweedViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Source Banner
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = Emerald100
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Emerald800,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "الشرح المعتمد: ${chapter.commentarySource}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Emerald900
                    )
                }
            }
        }

        // Anatomical vocal tract banner if Makharij chapter
        if (chapter.id == "jaz_makharij") {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NaturalBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = null,
                                    tint = Emerald800,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "الرسم التشريحي لمخارج الحروف",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald900
                                )
                            }

                            Button(
                                onClick = { viewModel.navigateTo(Screen.ANATOMICAL_MAKHARIS) },
                                colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("الأطلس التشريحي الكامل", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        AnatomicalVocalTractCanvas(
                            selectedOrgan = AnatomicalArea.LISAN,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Visual Diagram Card
        if (chapter.diagram != null) {
            item {
                VisualDiagramCard(diagram = chapter.diagram)
            }
        }

        // Explanation Points
        items(chapter.explanations) { point ->
            ExplanationPointCard(point = point)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuizTabContent(
    chapter: Chapter,
    questionIndex: Int,
    selectedOption: Int?,
    isSubmitted: Boolean,
    correctCount: Int,
    isCompleted: Boolean,
    onOptionSelected: (Int) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
    onReset: () -> Unit,
    onReadBayt: (Int) -> Unit
) {
    val total = chapter.quiz.size

    if (total == 0) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("لا توجد أسئلة تقييم لهذا الباب حالياً")
        }
        return
    }

    if (isCompleted) {
        // Completion Card
        val percentage = (correctCount * 100) / total
        val isPassed = percentage >= 70

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                if (isPassed) Color(0xFFDCFCE7) else Color(0xFFFEF2F2),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isPassed) "🌟" else "📖",
                            fontSize = 32.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isPassed) "مبارك! أتقنت اختبار هذا الباب" else "أحسنت المحاولة! راجع الأبيات وأعد الاختبار",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Emerald900,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "نتيجتك: $correctCount من $total أسئلة ($percentage%)",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isPassed) SuccessGreen else Emerald800
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onReset,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald800)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("إعادة الاختبار لتثبيت الحفظ")
                    }
                }
            }
        }
    } else {
        val currentQuestion = chapter.quiz[questionIndex]
        QuizQuestionCard(
            question = currentQuestion,
            questionIndex = questionIndex,
            totalQuestions = total,
            selectedOption = selectedOption,
            isSubmitted = isSubmitted,
            onOptionSelected = onOptionSelected,
            onSubmitClick = onSubmit,
            onNextClick = onNext,
            onReadBaytClick = onReadBayt
        )
    }
}
