package com.example.data.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiTutorService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .build()

    private val systemInstruction = """
        أنت محرك ذكي وخبير تعليمي متخصص في علوم التجويد وهندسة التطبيقات (Backend Logic Engine).
        مهمتك الأساسية هي تعليم وتحفيظ وشرح "متن الجزرية" و"متن تحفة الأطفال".
        اعتمد على الشروح المعتمدة: "فتح الأقفال" و"منحة ذي الجلال" لتحفة الأطفال، و"المنح الفكرية" لملا علي القاري و"الدقائق المحكمة" لزكريا الأنصاري وتحقيق د. أيمن رشدي سويد للجزرية.
        قدم إجاباتك مشكولة عند إيراد أبيات المتون أو الآيات، مقسمة بنقاط وعناوين واضحة ومختصرة، موجهة لطالب التجويد بلطف وتشجيع، مع ربط كل قاعدة ببيت المتن المناسب.
    """.trimIndent()

    suspend fun askTutor(question: String, currentChapter: String? = null): String {
        return withContext(Dispatchers.IO) {
            val apiKey = try {
                val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
                field.get(null) as? String ?: ""
            } catch (e: Throwable) {
                System.getenv("GEMINI_API_KEY") ?: ""
            }

            if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
                try {
                    val promptWithContext = if (!currentChapter.isNullOrBlank()) {
                        "السياق الحالي: دراسة $currentChapter.\nسؤال الطالب: $question"
                    } else {
                        "سؤال الطالب في التجويد: $question"
                    }

                    val json = JSONObject().apply {
                        put("systemInstruction", JSONObject().apply {
                            put("parts", JSONArray().put(JSONObject().put("text", systemInstruction)))
                        })
                        put("contents", JSONArray().put(JSONObject().apply {
                            put("role", "user")
                            put("parts", JSONArray().put(JSONObject().put("text", promptWithContext)))
                        }))
                    }

                    val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                    val request = Request.Builder()
                        .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
                        .post(body)
                        .build()

                    val response = client.newCall(request).execute()
                    val respBody = response.body?.string()
                    if (response.isSuccessful && !respBody.isNullOrBlank()) {
                        val respJson = JSONObject(respBody)
                        val text = respJson.getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text")
                        return@withContext text
                    }
                } catch (e: Exception) {
                    // Fall back to offline knowledge base
                }
            }

            // High-quality offline fallback answers for core queries
            getOfflineKnowledgeAnswer(question)
        }
    }

    private fun getOfflineKnowledgeAnswer(question: String): String {
        val q = question.lowercase()
        return when {
            q.contains("نون") || q.contains("تنوين") || q.contains("إظهار") || q.contains("إدغام") -> {
                """
                🌿 **أحكام النون الساكنة والتنوين (تحفة الأطفال):**
                للنون الساكنة والتنوين 4 أحكام:
                1. **الإظهار الحلقي:** عند 6 أحرف (همز، هاء، عين، حاء، غين، خاء).
                   *الشاهد:* (فَالأَوَّلُ الإِظْهَارُ قَبْلَ أَحْرُفِ * لِلْحَلْقِ سِتٌّ رُتِّبَتْ فَلْتَعْرِفِ).
                2. **الإدغام:** في أحرف (يَرْمُلُونَ)، بغنة في (يَنْمُو)، وبغير غنة في (اللام والراء).
                   *شرطه:* أن يكون من كلمتين، وإلا فهو إظهار مطلق كـ (دُنْيَا، صِنْوَان).
                3. **الإقلاب:** قلب النون ميماً مخفاة بغنة عند حرف (الباء).
                4. **الإخفاء الحقيقي:** عند 15 حرفاً مجموعة في أوائل كلمات:
                   (صِفْ ذَا ثَنَا كَمْ جَادَ شَخْصٌ قَدْ سَمَا * دُمْ طَيِّبًا زِدْ فِي تُقًى ضَعْ ظَالِمَا).
                """.trimIndent()
            }
            q.contains("ضاد") || q.contains("ظاء") -> {
                """
                🎯 **الفرق بين الضاد والظاء عند الإمام ابن الجزري:**
                * المخرج: الضاد تخرج من إحدى حافتي اللسان أو كلتيهما مع الأضراس العليا، بينما الظاء من طرف اللسان مع أطراف الثنايا العليا.
                * الصفة الفارقة: تتميز الضاد بالاستطالة دون الظاء.
                * الشاهد من الجزرية:
                  (وَالضَّادَ بِاسْتِطَالَةٍ وَمَخْرَجِ * مَيِّزْ مِنَ الظَّاءِ وَكُلُّهَا تَجِي).
                * انتبه لتغيّر المعنى:
                  - (نَاظِرَة) بالظاء: من النظر والرؤية.
                  - (نَاضِرَة) بالضاد: من النعومة والبهجة والحسن.
                """.trimIndent()
            }
            q.contains("مخرج") || q.contains("مخارج") -> {
                """
                📍 **مخارج الحروف في الجزرية:**
                * عدد المخارج الخاصة: 17 مخرجاً على مذهب الخليل بن أحمد وابن الجزري.
                * المخارج العامة الخمسة:
                  1. **الجوف:** لحروف المد الثلاثة (الألف، والواو والياء الساكنتان بعد حركة مجانسة).
                  2. **الحلق:** وفيه 3 مخارج لـ 6 أحرف (أقصاه: ء هـ، وسطه: ع ح، أدناه: غ خ).
                  3. **اللسان:** وفيه 10 مخارج لـ 18 حرفاً (أقصاه، وسطه، حافته، طرفه).
                  4. **الشفتان:** مخرجان لـ 4 أحرف (الفاء، الواو، الباء، الميم).
                  5. **الخيشوم:** مخرج صوت الغنة.
                """.trimIndent()
            }
            q.contains("مد") || q.contains("لازم") || q.contains("متصل") || q.contains("منفصل") -> {
                """
                🌊 **أحكام المدود في التحفة والجزرية:**
                * **المد الأصلي (الطبيعي):** حركتان (نُوحِيهَا).
                * **المد الفرعي بسبب الهمز:**
                  - المتصل: واجب 4-5 حركات (جَاءَ، السَّمَاء).
                  - المنفصل: جائز 4-5 حركات ويقصر لحركتين (بِمَا أُنزِلَ).
                  - البدل: جائز حركتان لحفص (آمَنُوا).
                * **المد الفرعي بسبب السكون:**
                  - العارض للسكون: جائز 2 أو 4 أو 6 حركات (الْعَالَمِينَ).
                  - اللازم: واجب 6 حركات لزوماً (الضَّالِّينَ، الم)، وينقسم إلى كلمي وحرفي، وكل منهما مخفف أو مثقل.
                """.trimIndent()
            }
            else -> {
                """
                ✨ **خبير التجويد معك:**
                أهلاً بك يا طالب علم القرآن. يمكنك سؤالي عن أي مسألة أو حكم في:
                * مخارج الحروف وصفاتها (الجهر، الرخاوة، القلقلة، الاستطالة).
                * أحكام النون الساكنة والتنوين والميم الساكنة.
                * أحكام المدود وأنواعها وأزمنتها.
                * المقطوع والموصول والتاءات في المصحف.
                * استحضار أي بيت من منظومة (تحفة الأطفال) أو (المقدمة الجزرية).
                """.trimIndent()
            }
        }
    }
}
