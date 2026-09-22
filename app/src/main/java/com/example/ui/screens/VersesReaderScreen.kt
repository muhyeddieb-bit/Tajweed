package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.ui.components.BaytItemCard
import com.example.ui.components.FontSizeZoomControlBar
import com.example.ui.viewmodel.Screen
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Gold100
import com.example.ui.theme.Gold200
import com.example.ui.theme.Gold700
import com.example.ui.theme.NaturalBg
import com.example.ui.theme.NaturalBorder
import com.example.ui.theme.NaturalForest
import com.example.ui.theme.NaturalSageLight
import com.example.ui.theme.NaturalSandLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.TajweedViewModel
import com.example.ui.viewmodel.VerseFilterCategory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersesReaderScreen(
    viewModel: TajweedViewModel,
    modifier: Modifier = Modifier
) {
    val selectedMatn by viewModel.verseReaderMatn.collectAsState()
    val filterCategory by viewModel.verseFilterCategory.collectAsState()
    val selectedChapterId by viewModel.verseFilterChapterId.collectAsState()
    val fontSize by viewModel.verseFontSize.collectAsState()
    val searchQuery by viewModel.readerSearchQuery.collectAsState()

    val memorizedVerses by viewModel.memorizedVerses.collectAsState()
    val bookmarkedVerses by viewModel.bookmarkedVerses.collectAsState()
    val isPlaying by viewModel.ttsManager.isPlaying.collectAsState()
    val playingBayt by viewModel.ttsManager.currentPlayingBayt.collectAsState()

    val chapters = viewModel.repository.getAllChapters(selectedMatn)
    val allVerses = viewModel.repository.getAllVerses(selectedMatn)

    // Filtered list of verses
    val filteredVerses by remember(selectedMatn, filterCategory, selectedChapterId, searchQuery, memorizedVerses, bookmarkedVerses) {
        derivedStateOf {
            allVerses.filter { bayt ->
                // Chapter filter
                val matchesChapter = selectedChapterId == null || bayt.chapterId == selectedChapterId

                // Category filter
                val baytKey = "${bayt.matnId.name}_${bayt.number}"
                val matchesCategory = when (filterCategory) {
                    VerseFilterCategory.ALL -> true
                    VerseFilterCategory.MEMORIZED -> memorizedVerses.contains(baytKey)
                    VerseFilterCategory.BOOKMARKED -> bookmarkedVerses.contains(baytKey)
                }

                // Search query
                val matchesSearch = if (searchQuery.isBlank()) true else {
                    bayt.firstHalf.contains(searchQuery.trim()) ||
                            bayt.secondHalf.contains(searchQuery.trim()) ||
                            bayt.number.toString() == searchQuery.trim()
                }

                matchesChapter && matchesCategory && matchesSearch
            }
        }
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var showJumpDialog by remember { mutableStateOf(false) }
    var jumpBaytInput by remember { mutableStateOf("") }
    var showSearchField by remember { mutableStateOf(false) }
    var detailBayt by remember { mutableStateOf<Bayt?>(null) }

    val matnTitle = when (selectedMatn) {
        MatnId.TUHFAT_AL_ATFAL -> "تحفة الأطفال"
        MatnId.AL_JAZARIYYAH -> "المقدمة الجزرية"
    }

    val memorizedCountForMatn = allVerses.count {
        memorizedVerses.contains("${selectedMatn.name}_${it.number}")
    }

    Scaffold(
        containerColor = NaturalBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoStories,
                                contentDescription = null,
                                tint = Gold100,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "أبيات $matnTitle",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "المحفوظ: $memorizedCountForMatn من ${allVerses.size} بيتاً • خط: ${fontSize.toInt()}sp",
                            style = MaterialTheme.typography.bodySmall,
                            color = Emerald100
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        modifier = Modifier.testTag("verses_reader_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع للرئيسية",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Jump to verse button
                    IconButton(
                        onClick = { showJumpDialog = true },
                        modifier = Modifier.testTag("jump_to_bayt_action")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = "انتقال لبيت محدد",
                            tint = Gold100
                        )
                    }

                    // Search toggle
                    IconButton(onClick = { showSearchField = !showSearchField }) {
                        Icon(
                            imageVector = if (showSearchField) Icons.Default.Clear else Icons.Default.Search,
                            contentDescription = "بحث في الأبيات",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Emerald900,
                    titleContentColor = Color.White
                )
            )
        },
        modifier = modifier.testTag("verses_reader_screen")
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 1. Matn Selection Chips (تحفة الأطفال / المقدمة الجزرية)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = selectedMatn == MatnId.TUHFAT_AL_ATFAL,
                        onClick = {
                            viewModel.setVerseReaderMatn(MatnId.TUHFAT_AL_ATFAL)
                            coroutineScope.launch { listState.scrollToItem(0) }
                        },
                        label = {
                            Text(
                                text = "تحفة الأطفال (61 بيتاً)",
                                fontWeight = if (selectedMatn == MatnId.TUHFAT_AL_ATFAL) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald800,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_matn_tuhfa")
                    )

                    FilterChip(
                        selected = selectedMatn == MatnId.AL_JAZARIYYAH,
                        onClick = {
                            viewModel.setVerseReaderMatn(MatnId.AL_JAZARIYYAH)
                            coroutineScope.launch { listState.scrollToItem(0) }
                        },
                        label = {
                            Text(
                                text = "المقدمة الجزرية (109 أبيات)",
                                fontWeight = if (selectedMatn == MatnId.AL_JAZARIYYAH) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald800,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_matn_jazariyyah")
                    )
                }
            }

            // 2. Search Field (if enabled)
            if (showSearchField) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setReaderSearchQuery(it) },
                        placeholder = { Text("ابحث في أبيات $matnTitle...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Emerald800)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setReaderSearchQuery("") }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Emerald800,
                            unfocusedBorderColor = NaturalBorder
                        ),
                        singleLine = true
                    )
                }
            }

            // 3. Font Size Zoom Control Bar (خيار تكبير الخط لتسهيل القراءة)
            item {
                FontSizeZoomControlBar(
                    fontSize = fontSize,
                    onIncrease = { viewModel.increaseVerseFontSize() },
                    onDecrease = { viewModel.decreaseVerseFontSize() },
                    onSetSize = { viewModel.setVerseFontSize(it) },
                    onReset = { viewModel.resetVerseFontSize() }
                )
            }

            // 4. Category Filter Chips (جميع الأبيات / المحفوظة / المفضلة)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VerseFilterCategory.entries.forEach { category ->
                        val count = when (category) {
                            VerseFilterCategory.ALL -> allVerses.size
                            VerseFilterCategory.MEMORIZED -> allVerses.count { memorizedVerses.contains("${selectedMatn.name}_${it.number}") }
                            VerseFilterCategory.BOOKMARKED -> allVerses.count { bookmarkedVerses.contains("${selectedMatn.name}_${it.number}") }
                        }

                        val isSelected = filterCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setVerseFilterCategory(category) },
                            label = {
                                Text(
                                    text = "${category.titleAr} ($count)",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NaturalForest,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_${category.name}")
                        )
                    }
                }
            }

            // 5. Chapter selector chip row (فلترة بحسب الباب)
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedChapterId == null,
                            onClick = { viewModel.setVerseFilterChapterId(null) },
                            label = { Text("جميع الأبواب (${chapters.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Emerald800,
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    items(chapters) { chapter ->
                        val isSelected = selectedChapterId == chapter.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setVerseFilterChapterId(chapter.id) },
                            label = { Text(chapter.title) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Emerald800,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Results count banner
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "عدد الأبيات المعروضة: ${filteredVerses.size} بيتاً",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Emerald900
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Gold100,
                        contentColor = Gold700
                    ) {
                        Text(
                            text = "حجم الخط: ${fontSize.toInt()} نقطة",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Empty state if no verses match filter
            if (filteredVerses.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📖",
                                fontSize = 42.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "لا توجد أبيات تطابق هذا التحديد",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Emerald900,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "جرّب تغيير خيارات الفلترة أو مسح كلمة البحث",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    viewModel.setVerseFilterCategory(VerseFilterCategory.ALL)
                                    viewModel.setVerseFilterChapterId(null)
                                    viewModel.setReaderSearchQuery("")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("إعادة ضبط الفلاتر")
                            }
                        }
                    }
                }
            } else {
                // 6. List of Verses in LazyColumn with Zoomable Font Size!
                items(
                    items = filteredVerses,
                    key = { "${it.matnId.name}_${it.number}" }
                ) { bayt ->
                    val isMemorized = memorizedVerses.contains("${bayt.matnId.name}_${bayt.number}")
                    val isBookmarked = bookmarkedVerses.contains("${bayt.matnId.name}_${bayt.number}")
                    val isBaytPlaying = isPlaying && playingBayt == bayt.number
                    val chapterTitle = viewModel.repository.getChapterTitleForBayt(bayt)
                    val explanationSnippet = viewModel.repository.getExplanationForBayt(bayt)

                    BaytItemCard(
                        bayt = bayt,
                        isMemorized = isMemorized,
                        isBookmarked = isBookmarked,
                        isPlaying = isBaytPlaying,
                        fontSize = fontSize,
                        chapterTitle = chapterTitle,
                        explanationSnippet = explanationSnippet,
                        onPlayClick = { viewModel.ttsManager.speakBayt(bayt) },
                        onMemorizeToggle = { viewModel.toggleMemorize(bayt) },
                        onBookmarkToggle = { viewModel.toggleBookmark(bayt) },
                        onOpenExplanationDetail = { detailBayt = it }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Jump to Verse Dialog
    if (showJumpDialog) {
        val maxNumber = allVerses.size
        AlertDialog(
            onDismissRequest = { showJumpDialog = false },
            title = {
                Text(
                    text = "انتقال سريع لبيت محدد",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "أدخل رقم البيت في $matnTitle (من 1 إلى $maxNumber):",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = jumpBaytInput,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() }) {
                                jumpBaytInput = input
                            }
                        },
                        placeholder = { Text("مثال: 15") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = jumpBaytInput.toIntOrNull()
                        if (num != null && num in 1..maxNumber) {
                            showJumpDialog = false
                            jumpBaytInput = ""
                            // Find index in filtered verses
                            val index = filteredVerses.indexOfFirst { it.number >= num }
                            if (index >= 0) {
                                coroutineScope.launch {
                                    listState.animateScrollToItem((index + 5).coerceAtLeast(0))
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald800)
                ) {
                    Text("انتقال")
                }
            },
            dismissButton = {
                TextButton(onClick = { showJumpDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Rich Verse Explanation & Anatomical BottomSheet
    detailBayt?.let { bayt ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val richExplanation = remember(bayt.matnId, bayt.number) {
            VerseExplanationsData.getExplanation(bayt.matnId, bayt.number)
        }

        ModalBottomSheet(
            onDismissRequest = { detailBayt = null },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "شرح وتوضيح البيت ${bayt.number}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Emerald900
                            )
                            Text(
                                text = if (bayt.matnId == MatnId.AL_JAZARIYYAH) "المقدمة الجزرية في التجويد" else "تحفة الأطفال والغلمان",
                                style = MaterialTheme.typography.labelSmall,
                                color = Gold700
                            )
                        }

                        IconButton(
                            onClick = { viewModel.ttsManager.speakBayt(bayt) }
                        ) {
                            Icon(
                                imageVector = if (isPlaying && playingBayt == bayt.number) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = "استماع",
                                tint = Emerald800
                            )
                        }
                    }
                }

                // Verse Display
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = NaturalSandLight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NaturalBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = bayt.firstHalf,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Emerald900,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "۞",
                                color = Gold700,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            Text(
                                text = bayt.secondHalf,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Emerald900,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // If Anatomical organ is present, show interactive Sagittal Diagram
                if (richExplanation?.anatomicalOrgan != null) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = NaturalBg),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Gold200),
                            modifier = Modifier.fillMaxWidth()
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
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "المخرج المشروح: ${richExplanation.anatomicalOrgan.titleAr}",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Emerald900
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            detailBayt = null
                                            viewModel.navigateTo(Screen.ANATOMICAL_MAKHARIS)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("الأطلس التشريحي", style = MaterialTheme.typography.labelSmall)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                AnatomicalVocalTractCanvas(
                                    selectedOrgan = richExplanation.anatomicalOrgan,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // Readable Simplified Explanation
                if (richExplanation != null) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = NaturalSandLight),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NaturalBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "الشرح الميسر المقروء:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald900
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = richExplanation.simplifiedMeaning,
                                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                if (richExplanation.tajweedRules.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "الأحكام التجويدية المستفادة:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Gold700
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    richExplanation.tajweedRules.forEach { rule ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 2.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text("• ", color = Emerald800, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = rule,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }

                                if (richExplanation.practicalTip != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "💡 تنبيه وإرشاد تطبيقي: ${richExplanation.practicalTip}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Gold700
                                    )
                                }
                            }
                        }
                    }
                } else {
                    item {
                        val snippet = viewModel.repository.getExplanationForBayt(bayt)
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = NaturalSandLight),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NaturalBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "الشرح والبيان:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald900
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = snippet ?: "لا يوجد شرح متوفر لهذا البيت حالياً.",
                                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}
