package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AnatomicalArea
import com.example.ui.theme.Emerald100
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
import com.example.ui.theme.NaturalSage
import com.example.ui.theme.NaturalSageLight
import com.example.ui.theme.NaturalSand
import com.example.ui.theme.NaturalSandLight

/**
 * Letter articulation data structure connecting each letter to its anatomical position.
 */
data class MakhrajLetterDetail(
    val letter: String,
    val organ: AnatomicalArea,
    val subLocation: String,
    val baytNumber: Int,
    val description: String,
    val sifaSummary: String,
    val exerciseWord: String
)

object MakharijDatabase {
    val allLetters: List<MakhrajLetterDetail> = listOf(
        // الجوف
        MakhrajLetterDetail("ا", AnatomicalArea.JAWF, "الجوف (تجويف الحلق والفم)", 10, "خلاء الفم والحلق، يمتد فيه الصوت دون انحباس مع انفتاح الفم", "مد، لين، خفاء", "قَـالَ"),
        MakhrajLetterDetail("و (مدية)", AnatomicalArea.JAWF, "الجوف (تجويف الحلق والفم)", 10, "خلاء الفم والحلق مع انضمام الشفتين دون انحباس", "مد، لين، خفاء", "يَقُولُ"),
        MakhrajLetterDetail("ي (مدية)", AnatomicalArea.JAWF, "الجوف (تجويف الحلق والفم)", 10, "خلاء الفم والحلق مع انخفاض الفك قليلاً", "مد، لين، خفاء", "قِـيلَ"),

        // الحلق
        MakhrajLetterDetail("ء", AnatomicalArea.HALQ, "أقصى الحلق (عند الحنجرة)", 11, "انطباق الأوتار الصوتية في الحنجرة تماماً ثم انفراجها", "جهر، شدة، استفال، انفتاح", "يَـأْكُـلُ"),
        MakhrajLetterDetail("هـ", AnatomicalArea.HALQ, "أقصى الحلق (عند الحنجرة)", 11, "انفراج الأوتار الصوتية مع تدفق هواء النفس بهدوء", "همس، رخاوة، استفال، خفاء", "يَـهْـدِي"),
        MakhrajLetterDetail("ع", AnatomicalArea.HALQ, "وسط الحلق (لسان المزمار)", 11, "رجوع لسان المزمار إلى الخلف نحو الجدار الخلفي للبلعوم", "جهر، توسط (بين الشدة والرخاوة)", "يَـعْـلَمُ"),
        MakhrajLetterDetail("ح", AnatomicalArea.HALQ, "وسط الحلق (لسان المزمار)", 11, "تضييق وسط الحلق مع جريان النفس والصوت بنعومة", "همس، رخاوة، استفال", "الـرَّحْـمَٰنِ"),
        MakhrajLetterDetail("غ", AnatomicalArea.HALQ, "أدنى الحلق (قرب اللهاة)", 12, "اقتراب جذر اللسان من الحنك اللحمي مع جريان الصوت", "جهر، رخاوة، استعلاء (مفخم)", "يَـغْـفِرُ"),
        MakhrajLetterDetail("خ", AnatomicalArea.HALQ, "أدنى الحلق (قرب اللهاة)", 12, "احتكاك الهواء بين جذر اللسان والحنك اللحمي مع جريان النفس", "همس، رخاوة، استعلاء (مفخم)", "يَـخْـشَى"),

        // اللسان
        MakhrajLetterDetail("ق", AnatomicalArea.LISAN, "أقصى اللسان فوق (الحنك اللحمي)", 12, "التصاق أقصى اللسان بالحنك اللحمي الرخو مع تفخيم وقلقلة", "جهر، شدة، استعلاء، قلقلة", "يَـقْـطَعُ"),
        MakhrajLetterDetail("ك", AnatomicalArea.LISAN, "أقصى اللسان أسفل (الحنك العظمي واللحمي)", 12, "التصاق أقصى اللسان أسفل القاف وانحباس الصوت ثم انفراجه بالهمس", "همس، شدة، استفال", "يَـكْـتُبُ"),
        MakhrajLetterDetail("ج", AnatomicalArea.LISAN, "وسط اللسان مع الحنك الأعلى", 13, "التصاق وسط اللسان بالحنك الأعلى انطباقاً تاماً يقطع الصوت", "جهر، شدة، قلقلة", "الْـحَـجُّ"),
        MakhrajLetterDetail("ش", AnatomicalArea.LISAN, "وسط اللسان مع الحنك الأعلى", 13, "اقتراب وسط اللسان من الحنك الأعلى دون التصاق ليتفشى الهواء", "همس، رخاوة، تفشي", "يَـشْـكُرُ"),
        MakhrajLetterDetail("ي (محققة)", AnatomicalArea.LISAN, "وسط اللسان مع الحنك الأعلى", 13, "ارتفاع وسط اللسان نحو قبة الحنك دون ملامسة كاملة", "جهر، رخاوة، لين", "بَـيْـت"),
        MakhrajLetterDetail("ض", AnatomicalArea.LISAN, "إحدى حافتي اللسان مع الأضراس العليا", 13, "التصاق حافة اللسان بالأضراس العليا واستطالة الصوت للأمام", "جهر، رخاوة، استعلاء، إطباق، استطالة", "الـضَّـالِّينَ"),
        MakhrajLetterDetail("ل", AnatomicalArea.LISAN, "أدنى حافة اللسان إلى منتهاها", 14, "التصاق حافة اللسان الأمامية بلثة الأسنان مع انحراف الصوت جانباً", "جهر، توسط، انحراف", "الْـحَـمْدُ"),
        MakhrajLetterDetail("ن", AnatomicalArea.LISAN, "طرف اللسان مع لثة الثنايا العليا", 15, "قرع طرف اللسان للثة العليا مصحوباً بغنة من الخيشوم", "جهر، توسط، غنة", "أَنْـعَـمْتَ"),
        MakhrajLetterDetail("ر", AnatomicalArea.LISAN, "طرف اللسان مائلاً لظهره", 15, "طرف اللسان مع اللثة مع فجوة ضيقة بالوسط وانحراف وتكرير مقنن", "جهر، توسط، انحراف، تكرير", "الـرَّحِـيمِ"),
        MakhrajLetterDetail("ط", AnatomicalArea.LISAN, "طرف اللسان مع أصول الثنايا العليا", 16, "أقوى الحروف؛ إطباق طرف اللسان على منبت الأسنان العليا مع استعلاء", "جهر، شدة، استعلاء، إطباق، قلقلة", "يَـطْـمَعُ"),
        MakhrajLetterDetail("د", AnatomicalArea.LISAN, "طرف اللسان مع أصول الثنايا العليا", 16, "طرف اللسان مع منبت الأسنان العليا مع انحباس الصوت وقلقلته", "جهر، شدة، قلقلة", "مُـدَّتْ"),
        MakhrajLetterDetail("ت", AnatomicalArea.LISAN, "طرف اللسان مع أصول الثنايا العليا", 16, "طرف اللسان مع أصول الثنايا مع انحباس الصوت ثم جريان النفس خفيفاً", "همس، شدة، استفال", "فُـتِّـحَتْ"),
        MakhrajLetterDetail("ص", AnatomicalArea.LISAN, "طرف اللسان فوق الثنايا السفلى", 17, "رأس اللسان فوق الثنايا السفلى ويمر هواء قوي بصفير مع استعلاء وإطباق", "همس، رخاوة، استعلاء، إطباق، صفير", "الـصِّـرَاطَ"),
        MakhrajLetterDetail("ز", AnatomicalArea.LISAN, "طرف اللسان فوق الثنايا السفلى", 17, "رأس اللسان فوق الثنايا السفلى مع صفير وجهر للصوت", "جهر، رخاوة، صفير", "الـزَّكَاةَ"),
        MakhrajLetterDetail("س", AnatomicalArea.LISAN, "طرف اللسان فوق الثنايا السفلى", 17, "رأس اللسان فوق الثنايا السفلى مع صفير ورخاوة وترقيق", "همس، رخاوة، صفير", "الـسَّـمَاءِ"),
        MakhrajLetterDetail("ظ", AnatomicalArea.LISAN, "طرف اللسان مع أطراف الثنايا العليا", 17, "ملامسة رأس اللسان لحواف الأسنان العليا مع استعلاء وإطباق", "جهر، رخاوة، استعلاء، إطباق", "الـظَّـالِمِينَ"),
        MakhrajLetterDetail("ذ", AnatomicalArea.LISAN, "طرف اللسان مع أطراف الثنايا العليا", 17, "ملامسة رأس اللسان لحواف الأسنان العليا مع رخاوة وترقيق", "جهر، رخاوة، استفال", "الَّـذِينَ"),
        MakhrajLetterDetail("ث", AnatomicalArea.LISAN, "طرف اللسان مع أطراف الثنايا العليا", 17, "ملامسة رأس اللسان لحواف الأسنان العليا مع همس وتدفق نفس", "همس، رخاوة، استفال", "يَـلْـهَـثْ"),

        // الشفتان
        MakhrajLetterDetail("ف", AnatomicalArea.SHAFATAN, "بطن الشفة السفلى مع الثنايا العليا", 18, "استقرار أطراف الثنايا العليا على باطن الشفة السفلى مع جريان النفس", "همس، رخاوة", "يَـفْـعَلُ"),
        MakhrajLetterDetail("و (محققة)", AnatomicalArea.SHAFATAN, "بين الشفتين بانضمام", 19, "استدارة الشفتين وانضمامهما للأمام مع بقاء فرجة دائرية", "جهر، رخاوة", "قَـوْلٌ"),
        MakhrajLetterDetail("ب", AnatomicalArea.SHAFATAN, "بين الشفتين بانطباق تام", 19, "انطباق الشفتين بقوة يقطع الصوت والنفس ثم ينفك بالقلقلة", "جهر، شدة، قلقلة", "تَـبَّـتْ"),
        MakhrajLetterDetail("م", AnatomicalArea.SHAFATAN, "بين الشفتين بانطباق مع غنة", 19, "انطباق الشفتين بلطف مع انفتاح ممر الخيشوم للغنة", "جهر، توسط، غنة", "عَـمَّ"),

        // الخيشوم
        MakhrajLetterDetail("الغنة (ن/م)", AnatomicalArea.KHAYSHOOM, "التجويف الأنفي الداخلي", 19, "رنين صوتي رخيم يخرج من الخيشوم ملازم للنون والميم المشددتين والمخفيتين", "صفة ذات مخرج مقدر", "إِنَّ / أُمَّة"),
    )

    fun getLettersForOrgan(organ: AnatomicalArea): List<MakhrajLetterDetail> {
        return allLetters.filter { it.organ == organ }
    }

    fun findDetailForLetter(letter: String): MakhrajLetterDetail? {
        return allLetters.firstOrNull { it.letter == letter || it.letter.startsWith(letter) }
    }
}

/**
 * Anatomical Sagittal Cross-Section Canvas of Speech Organs.
 * Dynamically highlights the active organ with animated pulsing effects and focal labels.
 */
@Composable
fun AnatomicalVocalTractCanvas(
    selectedOrgan: AnatomicalArea?,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val highlightColor = Emerald800
    val activeGlowColor = Gold600

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.22f)
            .testTag("anatomical_vocal_tract_canvas_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = NaturalSandLight),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, NaturalBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasW = size.width
                val canvasH = size.height

                // Draw background anatomical guidelines
                drawVocalTractProfile(
                    canvasW = canvasW,
                    canvasH = canvasH,
                    selectedOrgan = selectedOrgan,
                    pulse = pulseScale,
                    highlightColor = highlightColor,
                    activeGlow = activeGlowColor
                )
            }

            // Legend Overlay inside card
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(Color.White.copy(alpha = 0.88f), RoundedCornerShape(10.dp))
                    .border(1.dp, NaturalBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                if (selectedOrgan != null) highlightColor else Color.Gray,
                                CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = selectedOrgan?.titleAr ?: "رسم تخطيطي تشريحي",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Emerald900
                    )
                }
            }

            // Bottom prompt note
            Text(
                text = "مقطع سهمي (Sagittal) لجهاز النطق ومواضع التوليد الصوتي",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp)
            )
        }
    }
}

private fun DrawScope.drawVocalTractProfile(
    canvasW: Float,
    canvasH: Float,
    selectedOrgan: AnatomicalArea?,
    pulse: Float,
    highlightColor: Color,
    activeGlow: Color
) {
    // Reference coordinates normalized to 0..1
    fun x(ratio: Float) = canvasW * ratio
    fun y(ratio: Float) = canvasH * ratio

    val outlineColor = Color(0xFF4A463C)
    val fleshBaseColor = Color(0xFFF0DFD1)
    val airwayColor = Color(0xFFFFFFFF)
    val cartilageColor = Color(0xFFD6C6B7)

    // 1. Draw outer facial silhouette (forehead, nose, lips, chin, neck)
    val headPath = Path().apply {
        moveTo(x(0.85f), y(0.08f)) // Forehead
        cubicTo(x(0.72f), y(0.10f), x(0.60f), y(0.18f), x(0.55f), y(0.28f)) // Brow
        lineTo(x(0.48f), y(0.38f)) // Nose bridge
        cubicTo(x(0.44f), y(0.42f), x(0.42f), y(0.45f), x(0.46f), y(0.48f)) // Nose tip
        lineTo(x(0.51f), y(0.50f)) // Under nose
        cubicTo(x(0.50f), y(0.54f), x(0.48f), y(0.56f), x(0.49f), y(0.59f)) // Upper lip
        cubicTo(x(0.52f), y(0.61f), x(0.50f), y(0.63f), x(0.48f), y(0.66f)) // Lower lip
        cubicTo(x(0.49f), y(0.72f), x(0.53f), y(0.77f), x(0.56f), y(0.80f)) // Chin
        cubicTo(x(0.60f), y(0.85f), x(0.65f), y(0.89f), x(0.70f), y(0.94f)) // Neck front
    }
    drawPath(
        path = headPath,
        color = outlineColor.copy(alpha = 0.65f),
        style = Stroke(width = 3.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // 2. Draw Back of Head & Cervical Spine (Posterior Wall)
    val backWallPath = Path().apply {
        moveTo(x(0.88f), y(0.25f))
        cubicTo(x(0.82f), y(0.40f), x(0.80f), y(0.65f), x(0.80f), y(0.95f))
    }
    drawPath(
        path = backWallPath,
        color = outlineColor.copy(alpha = 0.35f),
        style = Stroke(width = 3f)
    )

    // 3. Nasal Cavity (الخيشوم)
    val isKhayshoomActive = selectedOrgan == AnatomicalArea.KHAYSHOOM
    val khayshoomPath = Path().apply {
        moveTo(x(0.52f), y(0.44f))
        cubicTo(x(0.55f), y(0.32f), x(0.68f), y(0.26f), x(0.74f), y(0.34f))
        cubicTo(x(0.75f), y(0.42f), x(0.70f), y(0.48f), x(0.65f), y(0.48f))
        lineTo(x(0.54f), y(0.48f))
        close()
    }
    drawPath(
        path = khayshoomPath,
        color = if (isKhayshoomActive) activeGlow.copy(alpha = 0.35f) else Color(0xFFF3E7DC),
        style = Fill
    )
    drawPath(
        path = khayshoomPath,
        color = if (isKhayshoomActive) highlightColor else outlineColor.copy(alpha = 0.5f),
        style = Stroke(width = if (isKhayshoomActive) 3.5f else 1.8f)
    )

    // 4. Hard Palate & Soft Palate with Uvula (الحنك العظمي واللحمي واللهاة)
    val palatePath = Path().apply {
        moveTo(x(0.53f), y(0.56f)) // Behind upper incisors
        cubicTo(x(0.58f), y(0.50f), x(0.66f), y(0.50f), x(0.72f), y(0.56f)) // Hard to soft palate
        cubicTo(x(0.74f), y(0.60f), x(0.75f), y(0.64f), x(0.73f), y(0.66f)) // Uvula (اللهاة)
    }
    drawPath(
        path = palatePath,
        color = if (selectedOrgan == AnatomicalArea.LISAN) highlightColor else outlineColor,
        style = Stroke(width = 3f, cap = StrokeCap.Round)
    )

    // 5. The Tongue (اللسان) with distinct anatomical curve
    val isLisanActive = selectedOrgan == AnatomicalArea.LISAN
    val tonguePath = Path().apply {
        moveTo(x(0.55f), y(0.68f)) // Tip of tongue (طرف اللسان)
        cubicTo(x(0.59f), y(0.62f), x(0.65f), y(0.60f), x(0.70f), y(0.68f)) // Tongue dorsum & middle (وسط وأقصى اللسان)
        cubicTo(x(0.72f), y(0.74f), x(0.71f), y(0.80f), x(0.69f), y(0.84f)) // Tongue root (جذر اللسان)
        lineTo(x(0.60f), y(0.84f))
        cubicTo(x(0.56f), y(0.80f), x(0.54f), y(0.75f), x(0.55f), y(0.68f))
        close()
    }
    drawPath(
        path = tonguePath,
        color = if (isLisanActive) activeGlow.copy(alpha = 0.40f) else Color(0xFFF7D8C8),
        style = Fill
    )
    drawPath(
        path = tonguePath,
        color = if (isLisanActive) highlightColor else outlineColor.copy(alpha = 0.8f),
        style = Stroke(width = if (isLisanActive) 4f else 2.2f)
    )

    // 6. Teeth: Upper Incisor & Lower Incisor (الثنايا العليا والسفلى)
    // Upper incisor
    drawRect(
        color = Color.White,
        topLeft = Offset(x(0.515f), y(0.57f)),
        size = Size(x(0.024f), y(0.045f))
    )
    drawRect(
        color = outlineColor,
        topLeft = Offset(x(0.515f), y(0.57f)),
        size = Size(x(0.024f), y(0.045f)),
        style = Stroke(width = 1.5f)
    )
    // Lower incisor
    drawRect(
        color = Color.White,
        topLeft = Offset(x(0.518f), y(0.65f)),
        size = Size(x(0.022f), y(0.04f))
    )
    drawRect(
        color = outlineColor,
        topLeft = Offset(x(0.518f), y(0.65f)),
        size = Size(x(0.022f), y(0.04f)),
        style = Stroke(width = 1.5f)
    )

    // 7. Throat / Pharynx & Epiglottis (الحلق ولسان المزمار والحنجرة)
    val isHalqActive = selectedOrgan == AnatomicalArea.HALQ
    // Epiglottis flap (لسان المزمار)
    val epiglottisPath = Path().apply {
        moveTo(x(0.69f), y(0.81f))
        cubicTo(x(0.71f), y(0.78f), x(0.73f), y(0.78f), x(0.72f), y(0.83f))
        close()
    }
    drawPath(
        path = epiglottisPath,
        color = if (isHalqActive) highlightColor else cartilageColor,
        style = Fill
    )
    drawPath(
        path = epiglottisPath,
        color = outlineColor,
        style = Stroke(width = 1.8f)
    )

    // Larynx and Vocal Cords (الحنجرة والأوتار الصوتية في أقصى الحلق)
    val vocalCordsCenter = Offset(x(0.72f), y(0.91f))
    drawCircle(
        color = if (isHalqActive) activeGlow.copy(alpha = 0.5f * pulse) else Color(0xFFE8D0C0),
        radius = x(0.035f) * (if (isHalqActive) pulse else 1f),
        center = vocalCordsCenter
    )
    drawCircle(
        color = if (isHalqActive) highlightColor else outlineColor.copy(alpha = 0.6f),
        radius = x(0.035f),
        center = vocalCordsCenter,
        style = Stroke(width = if (isHalqActive) 3f else 1.5f)
    )

    // 8. Jawf (الجوف) - Pharyngeal and Oral open passage
    val isJawfActive = selectedOrgan == AnatomicalArea.JAWF
    if (isJawfActive) {
        // Draw illuminated acoustic airway channel from vocal cords up through mouth
        val airwayFlowPath = Path().apply {
            moveTo(x(0.72f), y(0.92f)) // Larynx
            cubicTo(x(0.74f), y(0.80f), x(0.70f), y(0.68f), x(0.63f), y(0.60f)) // Pharynx into oral
            cubicTo(x(0.58f), y(0.56f), x(0.52f), y(0.58f), x(0.47f), y(0.62f)) // Out through lips
        }
        drawPath(
            path = airwayFlowPath,
            color = activeGlow.copy(alpha = 0.7f),
            style = Stroke(
                width = 14f * pulse,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }

    // 9. Lips (الشفتان)
    val isShafatanActive = selectedOrgan == AnatomicalArea.SHAFATAN
    if (isShafatanActive) {
        // Upper & lower lip pulse glow
        drawCircle(
            color = highlightColor.copy(alpha = 0.35f * pulse),
            radius = x(0.045f) * pulse,
            center = Offset(x(0.485f), y(0.625f))
        )
        drawCircle(
            color = highlightColor,
            radius = x(0.04f),
            center = Offset(x(0.485f), y(0.625f)),
            style = Stroke(width = 2.5f)
        )
    }

    // 10. Labels and Pointers for Anatomical Regions
    when (selectedOrgan) {
        AnatomicalArea.KHAYSHOOM -> {
            drawCalloutBadge(
                center = Offset(x(0.63f), y(0.38f)),
                title = "الخَيْشُوم (التجويف الأنفي)",
                sub = "مخرج صوت الغنة في النون والميم",
                color = highlightColor
            )
        }
        AnatomicalArea.JAWF -> {
            drawCalloutBadge(
                center = Offset(x(0.58f), y(0.58f)),
                title = "الجَوْف (التجويف الهوائي الممتد)",
                sub = "مخرج حروف المد الثلاثة (ا، و، ي)",
                color = highlightColor
            )
        }
        AnatomicalArea.HALQ -> {
            // Three levels of throat
            drawCalloutBadge(
                center = Offset(x(0.72f), y(0.91f)),
                title = "أقصى الحلق (الحنجرة): ء، هـ",
                sub = "وسط الحلق: ع، ح | أدنى الحلق: غ، خ",
                color = highlightColor
            )
        }
        AnatomicalArea.LISAN -> {
            drawCalloutBadge(
                center = Offset(x(0.62f), y(0.66f)),
                title = "اللِّسَان (10 مخارج و18 حرفاً)",
                sub = "أقصاه (ق، ك) - وسطه (ج، ش، ي) - حافته (ض، ل) - طرفه (11 حرفاً)",
                color = highlightColor
            )
        }
        AnatomicalArea.SHAFATAN -> {
            drawCalloutBadge(
                center = Offset(x(0.485f), y(0.625f)),
                title = "الشَّفَتَان (مخرجان خاصان)",
                sub = "بطن الشفة (ف) | بين الشفتين (و، ب، م)",
                color = highlightColor
            )
        }
        null -> {
            // Draw default general overview markers
            drawCalloutDot(Offset(x(0.63f), y(0.38f)), "الخيشوم", outlineColor)
            drawCalloutDot(Offset(x(0.62f), y(0.65f)), "اللسان", outlineColor)
            drawCalloutDot(Offset(x(0.72f), y(0.86f)), "الحلق", outlineColor)
            drawCalloutDot(Offset(x(0.485f), y(0.625f)), "الشفتان", outlineColor)
        }
    }
}

private fun DrawScope.drawCalloutDot(center: Offset, label: String, color: Color) {
    drawCircle(
        color = color.copy(alpha = 0.7f),
        radius = 5.5f,
        center = center
    )
    drawCircle(
        color = Color.White,
        radius = 2.5f,
        center = center
    )
}

private fun DrawScope.drawCalloutBadge(
    center: Offset,
    title: String,
    sub: String,
    color: Color
) {
    drawCircle(
        color = color,
        radius = 7f,
        center = center
    )
    drawCircle(
        color = Color.White,
        radius = 3.5f,
        center = center
    )
}

/**
 * Interactive Organ Detail Component displaying the anatomy, letters, and practical tajweed execution.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MakharijOrganDetailCard(
    organ: AnatomicalArea,
    selectedLetter: MakhrajLetterDetail?,
    onLetterSelected: (MakhrajLetterDetail) -> Unit,
    modifier: Modifier = Modifier
) {
    val organLetters = remember(organ) { MakharijDatabase.getLettersForOrgan(organ) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("organ_detail_card_${organ.name}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Emerald100, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = Emerald900,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = organ.titleAr,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Emerald900
                        )
                        Text(
                            text = "${organLetters.size} أحرف / مخارج صوتية",
                            style = MaterialTheme.typography.labelSmall,
                            color = Gold700,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-description note
            Text(
                text = organ.iconDescription,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Letter selector chips
            Text(
                text = "اختر الحرف لمشاهدة موضعه التشريحي وكيفية نطقه:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Emerald800
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                organLetters.forEach { letterDetail ->
                    val isSelected = selectedLetter == letterDetail
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Emerald800 else NaturalSandLight,
                        contentColor = if (isSelected) Color.White else Emerald900,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Emerald900 else NaturalBorder
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onLetterSelected(letterDetail) }
                            .testTag("letter_chip_${letterDetail.letter}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = letterDetail.letter,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Detailed view of selected letter
            if (selectedLetter != null && selectedLetter.organ == organ) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Gold50),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Gold200)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Emerald800,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = selectedLetter.letter,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = selectedLetter.subLocation,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald900
                                    )
                                    Text(
                                        text = "البيت رقم ${selectedLetter.baytNumber} في المقدمة الجزرية",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Gold700
                                    )
                                }
                            }

                            // Exercise word pill
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Emerald100,
                                contentColor = Emerald900
                            ) {
                                Text(
                                    text = "مثال: ${selectedLetter.exerciseWord}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // How it is anatomically articulated
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Emerald800,
                                modifier = Modifier.size(18.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedLetter.description,
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Sifat Summary
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = Gold700,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "أبرز صفاته: ${selectedLetter.sifaSummary}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Gold700
                            )
                        }
                    }
                }
            }
        }
    }
}
