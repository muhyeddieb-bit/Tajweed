package com.example.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Bayt
import com.example.data.model.MatnId
import com.example.data.repository.AnatomicalArea
import com.example.data.repository.VerseExplanationsData
import com.example.ui.components.AnatomicalVocalTractCanvas
import com.example.ui.components.MakharijDatabase
import com.example.ui.components.MakharijOrganDetailCard
import com.example.ui.components.MakhrajLetterDetail
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Gold100
import com.example.ui.theme.Gold200
import com.example.ui.theme.Gold50
import com.example.ui.theme.Gold700
import com.example.ui.theme.NaturalBg
import com.example.ui.theme.NaturalBorder
import com.example.ui.theme.NaturalForest
import com.example.ui.theme.NaturalSageLight
import com.example.ui.theme.NaturalSand
import com.example.ui.theme.NaturalSandLight
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.TajweedViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AnatomicalMakharijScreen(
    viewModel: TajweedViewModel,
    modifier: Modifier = Modifier
) {
    val isPlaying by viewModel.ttsManager.isPlaying.collectAsState()
    val playingBayt by viewModel.ttsManager.currentPlayingBayt.collectAsState()

    var selectedOrgan by remember { mutableStateOf<AnatomicalArea?>(AnatomicalArea.LISAN) }
    var selectedLetter by remember {
        mutableStateOf<MakhrajLetterDetail?>(
            MakharijDatabase.allLetters.firstOrNull { it.letter == "ق" }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = NaturalBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "المخارج المصورة تشريحياً",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "رسم سهمي لجهاز النطق ومواضع توليد الحروف",
                            style = MaterialTheme.typography.labelSmall,
                            color = Gold100
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        modifier = Modifier.testTag("nav_back_from_makharij")
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
                            viewModel.setVerseFilterChapterId("jaz_makharij")
                            viewModel.setVerseReaderMatn(MatnId.AL_JAZARIYYAH)
                            viewModel.navigateTo(Screen.VERSES_READER)
                        },
                        modifier = Modifier.testTag("action_read_makharij_verses")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = "أبيات باب المخارج في القارئ",
                            tint = Gold100
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Emerald900,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Interactive Anatomical Vocal Tract Canvas
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    AnatomicalVocalTractCanvas(
                        selectedOrgan = selectedOrgan,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 2. Organ Selector Filter Chips
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "المخارج العامة الخمسة:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Emerald900,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                    )

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedOrgan == null,
                                onClick = {
                                    selectedOrgan = null
                                    selectedLetter = null
                                },
                                label = { Text("نظرة عامة شاملة") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Emerald800,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }

                        items(AnatomicalArea.entries.toTypedArray()) { organ ->
                            val isSelected = selectedOrgan == organ
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedOrgan = organ
                                    selectedLetter = MakharijDatabase.getLettersForOrgan(organ).firstOrNull()
                                },
                                label = { Text(organ.titleAr.split(" ")[0]) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Emerald800,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("filter_organ_${organ.name}")
                            )
                        }
                    }
                }
            }

            // 3. Quick letter bar across all alphabet
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "فهرس الحروف الأبجدية السريع:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Gold700
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MakharijDatabase.allLetters.forEach { letterItem ->
                            val isSelected = selectedLetter == letterItem
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Emerald800 else Color.White,
                                contentColor = if (isSelected) Color.White else Emerald900,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) Emerald900 else NaturalBorder
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedLetter = letterItem
                                        selectedOrgan = letterItem.organ
                                    }
                            ) {
                                Text(
                                    text = letterItem.letter,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Detailed Organ Card with Selected Letter
            item {
                val organToDisplay = selectedOrgan ?: AnatomicalArea.LISAN
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    MakharijOrganDetailCard(
                        organ = organToDisplay,
                        selectedLetter = selectedLetter,
                        onLetterSelected = { letter ->
                            selectedLetter = letter
                            selectedOrgan = letter.organ
                        }
                    )
                }
            }

            // 5. Verse from Al-Jazariyyah for the active letter / organ
            item {
                val baytNumber = selectedLetter?.baytNumber ?: 9
                val verseExplanation = remember(baytNumber) {
                    VerseExplanationsData.getExplanation(MatnId.AL_JAZARIYYAH, baytNumber)
                }

                if (verseExplanation != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = NaturalSandLight),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NaturalBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            // Header of Verse
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(Emerald800, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$baytNumber",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "البيت الشاهد في المقدمة الجزرية:",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald900
                                    )
                                }

                                // Audio Play Button
                                IconButton(
                                    onClick = {
                                        val bayt = Bayt(
                                            number = baytNumber,
                                            firstHalf = verseExplanation.verseText.split("*").firstOrNull()?.trim() ?: "",
                                            secondHalf = verseExplanation.verseText.split("*").lastOrNull()?.trim() ?: "",
                                            chapterId = "jaz_makharij",
                                            matnId = MatnId.AL_JAZARIYYAH
                                        )
                                        viewModel.ttsManager.speakBayt(bayt)
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying && playingBayt == baytNumber) Icons.Default.Stop else Icons.Default.PlayArrow,
                                        contentDescription = "استماع للبيت",
                                        tint = Emerald800
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Verse Text Display
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Gold200),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = verseExplanation.verseText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = Emerald900,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Simplified Readable Meaning
                            Text(
                                text = "الشرح الميسر للبيت:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Gold700
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = verseExplanation.simplifiedMeaning,
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 22.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Practical Tip
                            if (verseExplanation.practicalTip != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = Gold700,
                                        modifier = Modifier.size(18.dp).padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = verseExplanation.practicalTip,
                                        style = MaterialTheme.typography.labelSmall.copy(lineHeight = 18.sp),
                                        fontWeight = FontWeight.SemiBold,
                                        color = Gold700
                                    )
                                }
                            }

                            // Quranic Examples
                            if (verseExplanation.quranicExamples.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "أمثلة قرآنية وتوجيه النطق:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald800
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                verseExplanation.quranicExamples.forEach { ex ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = NaturalSageLight,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "﴿ ${ex.wordOrPhrase} ﴾ - [${ex.surahAndAyah}]",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Emerald900
                                                )
                                                Text(
                                                    text = ex.pronunciationNote,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
