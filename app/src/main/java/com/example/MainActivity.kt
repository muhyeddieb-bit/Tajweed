package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ui.screens.AiTutorScreen
import com.example.ui.screens.AnatomicalMakharijScreen
import com.example.ui.screens.ChapterStudyScreen
import com.example.ui.screens.ChaptersScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PwaScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.VersesReaderScreen
import com.example.ui.screens.VisualMapScreen
import com.example.ui.theme.NaturalBorder
import com.example.ui.theme.NaturalForest
import com.example.ui.theme.NaturalNavMuted
import com.example.ui.theme.NaturalSage
import com.example.ui.theme.NaturalTextPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.TajweedViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TajweedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    TajweedApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun TajweedApp(viewModel: TajweedViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Handle system back button
    BackHandler(enabled = currentScreen != Screen.HOME) {
        when (currentScreen) {
            Screen.STUDY -> viewModel.navigateTo(Screen.CHAPTERS)
            Screen.VISUAL_MAP, Screen.VERSES_READER, Screen.ANATOMICAL_MAKHARIS, Screen.PWA_VIEWER, Screen.CHAPTERS, Screen.SEARCH, Screen.AI_TUTOR -> viewModel.navigateTo(Screen.HOME)
            Screen.HOME -> Unit
        }
    }

    val showBottomBar = currentScreen != Screen.STUDY

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = NaturalTextPrimary,
                    tonalElevation = 6.dp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .border(
                            width = 1.dp,
                            color = NaturalBorder,
                            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                        )
                        .testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentScreen == Screen.HOME,
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "الرئيسية") },
                        label = { Text("الرئيسية") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NaturalForest,
                            selectedTextColor = NaturalForest,
                            indicatorColor = NaturalSage,
                            unselectedIconColor = NaturalNavMuted,
                            unselectedTextColor = NaturalNavMuted
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.VISUAL_MAP,
                        onClick = { viewModel.navigateTo(Screen.VISUAL_MAP) },
                        icon = { Icon(imageVector = Icons.Default.AccountTree, contentDescription = "الخريطة") },
                        label = { Text("الخريطة") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NaturalForest,
                            selectedTextColor = NaturalForest,
                            indicatorColor = NaturalSage,
                            unselectedIconColor = NaturalNavMuted,
                            unselectedTextColor = NaturalNavMuted
                        ),
                        modifier = Modifier.testTag("nav_visual_map")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.CHAPTERS,
                        onClick = { viewModel.navigateTo(Screen.CHAPTERS) },
                        icon = { Icon(imageVector = Icons.Default.MenuBook, contentDescription = "الأبواب") },
                        label = { Text("الأبواب") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NaturalForest,
                            selectedTextColor = NaturalForest,
                            indicatorColor = NaturalSage,
                            unselectedIconColor = NaturalNavMuted,
                            unselectedTextColor = NaturalNavMuted
                        ),
                        modifier = Modifier.testTag("nav_chapters")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.VERSES_READER,
                        onClick = { viewModel.navigateTo(Screen.VERSES_READER) },
                        icon = { Icon(imageVector = Icons.Default.FormatSize, contentDescription = "الأبيات") },
                        label = { Text("الأبيات") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NaturalForest,
                            selectedTextColor = NaturalForest,
                            indicatorColor = NaturalSage,
                            unselectedIconColor = NaturalNavMuted,
                            unselectedTextColor = NaturalNavMuted
                        ),
                        modifier = Modifier.testTag("nav_verses_reader")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.AI_TUTOR,
                        onClick = { viewModel.navigateTo(Screen.AI_TUTOR) },
                        icon = { Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "خبير التجويد") },
                        label = { Text("الخبير") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NaturalForest,
                            selectedTextColor = NaturalForest,
                            indicatorColor = NaturalSage,
                            unselectedIconColor = NaturalNavMuted,
                            unselectedTextColor = NaturalNavMuted
                        ),
                        modifier = Modifier.testTag("nav_ai")
                    )
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screen_transition",
            modifier = Modifier.padding(innerPadding)
        ) { screen ->
            when (screen) {
                Screen.HOME -> HomeScreen(viewModel = viewModel)
                Screen.VISUAL_MAP -> VisualMapScreen(viewModel = viewModel)
                Screen.VERSES_READER -> VersesReaderScreen(viewModel = viewModel)
                Screen.ANATOMICAL_MAKHARIS -> AnatomicalMakharijScreen(viewModel = viewModel)
                Screen.PWA_VIEWER -> PwaScreen(viewModel = viewModel)
                Screen.CHAPTERS -> ChaptersScreen(viewModel = viewModel)
                Screen.STUDY -> ChapterStudyScreen(viewModel = viewModel)
                Screen.SEARCH -> SearchScreen(viewModel = viewModel)
                Screen.AI_TUTOR -> AiTutorScreen(viewModel = viewModel)
            }
        }
    }
}

