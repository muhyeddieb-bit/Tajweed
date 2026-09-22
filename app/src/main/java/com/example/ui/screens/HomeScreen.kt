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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.model.Bayt
import com.example.data.model.MatnId
import com.example.ui.theme.NaturalBg
import com.example.ui.theme.NaturalBorder
import com.example.ui.theme.NaturalForest
import com.example.ui.theme.NaturalSage
import com.example.ui.theme.NaturalSageLight
import com.example.ui.theme.NaturalSand
import com.example.ui.theme.NaturalTerracotta
import com.example.ui.theme.NaturalTextPrimary
import com.example.ui.theme.NaturalTextSecondary
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.TajweedViewModel

@Composable
fun HomeScreen(
    viewModel: TajweedViewModel,
    modifier: Modifier = Modifier
) {
    val memorizedVerses by viewModel.memorizedVerses.collectAsState()
    val isPlaying by viewModel.ttsManager.isPlaying.collectAsState()
    val playingBayt by viewModel.ttsManager.currentPlayingBayt.collectAsState()

    val tuhfaCount = memorizedVerses.count { it.startsWith("TUHFAT_AL_ATFAL_") }
    val jazariyyahCount = memorizedVerses.count { it.startsWith("AL_JAZARIYYAH_") }
    val totalMemorized = tuhfaCount + jazariyyahCount
    val totalVerses = 170
    val overallPercent = (totalMemorized * 100) / totalVerses

    // Featured / Daily Bayt (Bayt 6 of Jazariyyah)
    val dailyBayt = Bayt(
        number = 6,
        firstHalf = "مَخَارِجَ الْحُرُوفِ وَالصِّفَاتِ",
        secondHalf = "لِيَلْفِظُوا بِأَفْصَحِ اللُّغَاتِ",
        chapterId = "jaz_intro",
        matnId = MatnId.AL_JAZARIYYAH
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NaturalBg)
            .testTag("home_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Natural Tones Header
        item {
            HeaderSection(viewModel = viewModel)
        }

        // Natural Forest Hero Card: التقدم الإجمالي
        item {
            PaddingHorizontal {
                HeroProgressCard(
                    overallPercent = overallPercent,
                    totalMemorized = totalMemorized,
                    totalVerses = totalVerses
                )
            }
        }

        // Two Matns Grid (تحفة الأطفال & المقدمة الجزرية)
        item {
            PaddingHorizontal {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 1: تحفة الأطفال (Natural Sage)
                    MatnGridCard(
                        title = "تحفة الأطفال",
                        subtitle = "61 بيتاً • 10 أبواب",
                        memorizedCount = tuhfaCount,
                        totalCount = 61,
                        containerColor = NaturalSage,
                        accentColor = NaturalForest,
                        iconText = "📜",
                        onClick = { viewModel.selectMatn(MatnId.TUHFAT_AL_ATFAL) },
                        modifier = Modifier.weight(1f)
                    )

                    // Card 2: المقدمة الجزرية (Natural Sand)
                    MatnGridCard(
                        title = "الجزرية",
                        subtitle = "109 أبيات • 18 باباً",
                        memorizedCount = jazariyyahCount,
                        totalCount = 109,
                        containerColor = NaturalSand,
                        accentColor = NaturalTerracotta,
                        iconText = "📖",
                        onClick = { viewModel.selectMatn(MatnId.AL_JAZARIYYAH) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Action Card: الخريطة البصرية التفاعلية للمتون
        item {
            PaddingHorizontal {
                ActionCard(
                    title = "الخريطة البصرية للمتون",
                    subtitle = "استكشف شجرة متني الجزرية وتحفة الأطفال تفاعلياً مع التكبير والتصغير ومتابعة الإنجاز",
                    buttonText = "استكشف الخريطة",
                    icon = Icons.Default.AccountTree,
                    onClick = { viewModel.navigateTo(Screen.VISUAL_MAP) },
                    testTag = "home_visual_map_card"
                )
            }
        }

        // Action Card: المخارج المصورة تشريحياً
        item {
            PaddingHorizontal {
                ActionCard(
                    title = "المخارج المصورة تشريحياً",
                    subtitle = "شرح تفاعلي بالرسم السهمي لجهاز النطق ومواضع توليد الحروف الـ 17 بالألوان وتدفق الصوت",
                    buttonText = "افتح الأطلس التشريحي",
                    icon = Icons.Default.RecordVoiceOver,
                    onClick = { viewModel.navigateTo(Screen.ANATOMICAL_MAKHARIS) },
                    testTag = "home_makharij_anatomy_card"
                )
            }
        }

        // Action Card: قارئ الأبيات التفاعلي (مع تكبير وتصغير الخط)
        item {
            PaddingHorizontal {
                ActionCard(
                    title = "قارئ الأبيات التفاعلي",
                    subtitle = "عرض أبيات المتون مع خيار تكبير وتصغير الخط والاستماع التفاعلي",
                    buttonText = "تصفح الأبيات",
                    icon = Icons.Default.FormatSize,
                    onClick = { viewModel.navigateTo(Screen.VERSES_READER) },
                    testTag = "home_verses_reader_card"
                )
            }
        }

        // Action Card: تطبيق الويب التقدمي (PWA)
        item {
            PaddingHorizontal {
                ActionCard(
                    title = "تطبيق الويب التقدمي (PWA)",
                    subtitle = "تشغيل نسخة الويب PWA وتثبيتها بدون متجر، وتصدير ملفات الحزمة الجاهزة للنشر بنقرة واحدة",
                    buttonText = "فتح نسخة الويب PWA",
                    icon = Icons.Default.Language,
                    onClick = { viewModel.navigateTo(Screen.PWA_VIEWER) },
                    testTag = "home_pwa_card"
                )
            }
        }

        // Action Card: خبير التجويد الذكي (AI Tutor)
        item {
            PaddingHorizontal {
                ActionCard(
                    title = "خبير التجويد الذكي",
                    subtitle = "توجيهات تفاعلية وشرح للأحكام مع الذكاء الاصطناعي",
                    buttonText = "اسأل الآن",
                    icon = Icons.Default.AutoAwesome,
                    onClick = { viewModel.navigateTo(Screen.AI_TUTOR) },
                    testTag = "home_ai_action_card"
                )
            }
        }

        // Action Card: البحث في المتون
        item {
            PaddingHorizontal {
                ActionCard(
                    title = "البحث في المنظومات",
                    subtitle = "ابحث في 170 بيتاً بالشكل التام وشواهد الأحكام",
                    buttonText = "ابحث",
                    icon = Icons.Default.Search,
                    onClick = { viewModel.navigateTo(Screen.SEARCH) },
                    testTag = "home_search_action_card"
                )
            }
        }

        // Last Studied / Daily Verse Card
        item {
            PaddingHorizontal {
                LastStudiedCard(
                    bayt = dailyBayt,
                    isMemorized = viewModel.repository.isMemorized(dailyBayt.matnId, dailyBayt.number),
                    isPlaying = isPlaying && playingBayt == dailyBayt.number,
                    onPlayClick = { viewModel.ttsManager.speakBayt(dailyBayt) },
                    onMemorizeToggle = { viewModel.toggleMemorize(dailyBayt) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HeaderSection(viewModel: TajweedViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "أهلاً بك في",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = NaturalForest.copy(alpha = 0.8f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "محرك التجويد",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = NaturalTextPrimary
            )
        }

        // Avatar / Quick shortcut badge
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(NaturalSage)
                .border(2.dp, NaturalForest.copy(alpha = 0.25f), CircleShape)
                .clickable { viewModel.navigateTo(Screen.AI_TUTOR) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "الملف الشخصي",
                tint = NaturalForest,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun HeroProgressCard(
    overallPercent: Int,
    totalMemorized: Int,
    totalVerses: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .testTag("home_hero_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = NaturalForest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            // Decorative background glowing circle
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.BottomStart)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "التقدم الإجمالي",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$overallPercent%",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { overallPercent.toFloat() / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.25f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (totalMemorized > 0)
                        "وصلت إلى: تم حفظ $totalMemorized من $totalVerses بيتاً بنجاح"
                    else
                        "ابدأ الآن رحلة حفظ تحفة الأطفال والمقدمة الجزرية",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun MatnGridCard(
    title: String,
    subtitle: String,
    memorizedCount: Int,
    totalCount: Int,
    containerColor: Color,
    accentColor: Color,
    iconText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .testTag("matn_grid_$title"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon Pill
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accentColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconText,
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NaturalTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = accentColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Progress mini badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "محفوظ: $memorizedCount / $totalCount",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = NaturalTextPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    buttonText: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, NaturalBorder, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(NaturalForest.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = NaturalForest,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = NaturalTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = NaturalTextSecondary
                )
            }

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NaturalForest,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LastStudiedCard(
    bayt: Bayt,
    isMemorized: Boolean,
    isPlaying: Boolean,
    onPlayClick: () -> Unit,
    onMemorizeToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .testTag("home_last_studied_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = NaturalSageLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "آخر بيت تمت دراسته",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = NaturalForest,
                    letterSpacing = 0.5.sp
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 0.5.dp
                ) {
                    Text(
                        text = "المقدمة الجزرية • بيت ${bayt.number}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = NaturalTextPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Verses
            Text(
                text = "« ${bayt.firstHalf} »",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = NaturalTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "« ${bayt.secondHalf} »",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = NaturalTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Audio & Memorize Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPlayClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "إيقاف الصوت" else "استمع للبيت",
                        tint = NaturalForest
                    )
                }

                IconButton(
                    onClick = onMemorizeToggle,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isMemorized) Icons.Default.CheckCircle else Icons.Default.Check,
                        contentDescription = "تم الحفظ",
                        tint = if (isMemorized) SuccessGreen else Color(0xFF8A8985)
                    )
                }
            }
        }
    }
}

@Composable
private fun PaddingHorizontal(content: @Composable () -> Unit) {
    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        content()
    }
}
