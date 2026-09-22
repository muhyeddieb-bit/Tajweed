package com.example.ui.screens

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Chapter
import com.example.data.model.MatnId
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Gold100
import com.example.ui.theme.Gold500
import com.example.ui.theme.Gold600
import com.example.ui.theme.Gold700
import com.example.ui.theme.NaturalBg
import com.example.ui.theme.NaturalBorder
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StudyTab
import com.example.ui.viewmodel.TajweedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChaptersScreen(
    viewModel: TajweedViewModel,
    modifier: Modifier = Modifier
) {
    val selectedMatn by viewModel.selectedMatn.collectAsState()
    val memorizedVerses by viewModel.memorizedVerses.collectAsState()
    val quizScores by viewModel.quizScores.collectAsState()

    val chapters = viewModel.repository.getAllChapters(selectedMatn)

    val matnTitle = when (selectedMatn) {
        MatnId.TUHFAT_AL_ATFAL -> "تحفة الأطفال (61 بيتاً)"
        MatnId.AL_JAZARIYYAH -> "المقدمة الجزرية (109 أبيات)"
    }

    Scaffold(
        containerColor = NaturalBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = matnTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "فهرس الأبواب والمنهج التعليمي",
                            style = MaterialTheme.typography.bodySmall,
                            color = Emerald100
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        modifier = Modifier.testTag("back_to_home")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.navigateTo(Screen.VISUAL_MAP)
                        },
                        modifier = Modifier.testTag("action_visual_map")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountTree,
                            contentDescription = "الخريطة البصرية التفاعلية",
                            tint = Gold100
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.setVerseReaderMatn(selectedMatn)
                            viewModel.navigateTo(Screen.VERSES_READER)
                        },
                        modifier = Modifier.testTag("action_verses_reader")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = "قارئ الأبيات وتكبير الخط",
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
        modifier = modifier.testTag("chapters_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Switch Matn Segment
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedMatn == MatnId.TUHFAT_AL_ATFAL,
                        onClick = { viewModel.selectMatn(MatnId.TUHFAT_AL_ATFAL) },
                        label = { Text("تحفة الأطفال") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald800,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = selectedMatn == MatnId.AL_JAZARIYYAH,
                        onClick = { viewModel.selectMatn(MatnId.AL_JAZARIYYAH) },
                        label = { Text("المقدمة الجزرية") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald800,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Chapters List
            itemsIndexed(chapters) { index, chapter ->
                val chapterMemorized = chapter.verses.count { bayt ->
                    memorizedVerses.contains("${selectedMatn.name}_${bayt.number}")
                }
                val bestScore = quizScores[chapter.id]

                ChapterListItemCard(
                    chapterIndex = index + 1,
                    chapter = chapter,
                    memorizedCount = chapterMemorized,
                    totalVerses = chapter.verses.size,
                    quizScore = bestScore,
                    onOpenChapter = { tab ->
                        viewModel.selectChapter(chapter, tab)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ChapterListItemCard(
    chapterIndex: Int,
    chapter: Chapter,
    memorizedCount: Int,
    totalVerses: Int,
    quizScore: Int?,
    onOpenChapter: (StudyTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (totalVerses > 0) memorizedCount.toFloat() / totalVerses else 0f
    val isFullyMemorized = memorizedCount == totalVerses && totalVerses > 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, NaturalBorder, RoundedCornerShape(20.dp))
            .clickable { onOpenChapter(StudyTab.MEMORIZATION) }
            .testTag("chapter_card_${chapter.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Chapter number & Range
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(if (isFullyMemorized) SuccessGreen else Emerald800, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$chapterIndex",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = chapter.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Emerald900
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Gold100,
                    contentColor = Gold700
                ) {
                    Text(
                        text = "الأبيات: ${chapter.startBayt} - ${chapter.endBayt}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Commentary source info
            Text(
                text = "المرجع الشارح: ${chapter.commentarySource}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Memorization Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isFullyMemorized) Icons.Default.CheckCircle else Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = if (isFullyMemorized) SuccessGreen else Emerald800,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "الحفظ: $memorizedCount من $totalVerses بيتاً",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (quizScore != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (quizScore >= 80) Color(0xFFDCFCE7) else Color(0xFFFEF9C3)
                    ) {
                        Text(
                            text = "الاختبار: $quizScore%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (quizScore >= 80) Color(0xFF166534) else Color(0xFF854D0E),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (isFullyMemorized) SuccessGreen else Gold500,
                trackColor = Color(0xFFE5E7EB)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Three Action Buttons (الحفظ • الفهم • التقييم)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onOpenChapter(StudyTab.MEMORIZATION) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "الحفظ", style = MaterialTheme.typography.labelMedium)
                }
                OutlinedButton(
                    onClick = { onOpenChapter(StudyTab.UNDERSTANDING) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "الفهم", style = MaterialTheme.typography.labelMedium)
                }
                OutlinedButton(
                    onClick = { onOpenChapter(StudyTab.QUIZ) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "الاختبار", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
