package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.MatnId
import com.example.ui.components.BaytItemCard
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.TajweedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: TajweedViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val memorizedVerses by viewModel.memorizedVerses.collectAsState()
    val bookmarkedVerses by viewModel.bookmarkedVerses.collectAsState()
    val isPlaying by viewModel.ttsManager.isPlaying.collectAsState()
    val playingBayt by viewModel.ttsManager.currentPlayingBayt.collectAsState()
    val fontSize by viewModel.verseFontSize.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "البحث في المتون (170 بيتاً)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        modifier = Modifier.testTag("search_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
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
        modifier = modifier.testTag("search_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text("ابحث عن كلمة، حكم، أو رقم بيت...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Emerald800
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("search_text_field"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Emerald800,
                    unfocusedBorderColor = Color(0xFFD1D5DB)
                ),
                singleLine = true
            )

            // Results count
            if (searchQuery.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "تم العثور على ${searchResults.size} بيتاً",
                        style = MaterialTheme.typography.labelMedium,
                        color = Emerald900,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Results List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (searchQuery.isNotBlank() && searchResults.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "لم يتم العثور على أبيات تطابق بحثك",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(searchResults) { bayt ->
                        val isMemorized = memorizedVerses.contains("${bayt.matnId.name}_${bayt.number}")
                        val isBookmarked = bookmarkedVerses.contains("${bayt.matnId.name}_${bayt.number}")
                        val isBaytPlaying = isPlaying && playingBayt == bayt.number

                        Column {
                            // Badge indicating which Matn
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (bayt.matnId == MatnId.TUHFAT_AL_ATFAL) Color(0xFFE0F2FE) else Color(0xFFFEF3C7),
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (bayt.matnId == MatnId.TUHFAT_AL_ATFAL) "تحفة الأطفال" else "المقدمة الجزرية",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (bayt.matnId == MatnId.TUHFAT_AL_ATFAL) Color(0xFF0369A1) else Color(0xFFB45309),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            BaytItemCard(
                                bayt = bayt,
                                isMemorized = isMemorized,
                                isBookmarked = isBookmarked,
                                isPlaying = isBaytPlaying,
                                fontSize = fontSize,
                                onPlayClick = { viewModel.ttsManager.speakBayt(bayt) },
                                onMemorizeToggle = { viewModel.toggleMemorize(bayt) },
                                onBookmarkToggle = { viewModel.toggleBookmark(bayt) }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
