package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.Bayt
import com.example.data.model.Chapter
import com.example.data.model.MatnId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TajweedRepository(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("tajweed_user_data", Context.MODE_PRIVATE)

    private val _memorizedVerses = MutableStateFlow<Set<String>>(loadMemorized())
    val memorizedVerses: StateFlow<Set<String>> = _memorizedVerses.asStateFlow()

    private val _bookmarkedVerses = MutableStateFlow<Set<String>>(loadBookmarks())
    val bookmarkedVerses: StateFlow<Set<String>> = _bookmarkedVerses.asStateFlow()

    private val _quizScores = MutableStateFlow<Map<String, Int>>(loadQuizScores())
    val quizScores: StateFlow<Map<String, Int>> = _quizScores.asStateFlow()

    fun getAllChapters(matnId: MatnId): List<Chapter> {
        return when (matnId) {
            MatnId.TUHFAT_AL_ATFAL -> TuhfatAlAtfalData.getChapters()
            MatnId.AL_JAZARIYYAH -> JazariyyahData.getChapters()
        }
    }

    fun getChapter(matnId: MatnId, chapterId: String): Chapter? {
        return getAllChapters(matnId).firstOrNull { it.id == chapterId }
    }

    fun getAllVerses(matnId: MatnId): List<Bayt> {
        return getAllChapters(matnId).flatMap { it.verses }
    }

    fun getChapterTitleForBayt(bayt: Bayt): String {
        val chapter = getAllChapters(bayt.matnId).firstOrNull { it.id == bayt.chapterId }
        return chapter?.title ?: ""
    }

    fun getExplanationForBayt(bayt: Bayt): String? {
        val chapter = getAllChapters(bayt.matnId).firstOrNull { it.id == bayt.chapterId } ?: return null
        val matchedPoint = chapter.explanations.firstOrNull { it.referenceBayt == bayt.number }
        return matchedPoint?.let { "${it.title}: ${it.details}" }
            ?: chapter.explanations.firstOrNull()?.let { "${it.title}: ${it.details}" }
    }

    fun getSavedFontSize(): Float {
        return prefs.getFloat("verse_font_size", 21f)
    }

    fun saveFontSize(size: Float) {
        prefs.edit().putFloat("verse_font_size", size).apply()
    }

    fun searchVerses(query: String): List<Bayt> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return emptyList()

        val normalizedQuery = normalizeArabic(trimmed)
        val allVerses = TuhfatAlAtfalData.getChapters().flatMap { it.verses } +
                JazariyyahData.getChapters().flatMap { it.verses }

        return allVerses.filter { bayt ->
            normalizeArabic(bayt.firstHalf).contains(normalizedQuery) ||
                    normalizeArabic(bayt.secondHalf).contains(normalizedQuery) ||
                    bayt.number.toString() == trimmed
        }
    }

    fun toggleMemorized(matnId: MatnId, baytNumber: Int) {
        val key = "${matnId.name}_$baytNumber"
        val current = _memorizedVerses.value.toMutableSet()
        if (current.contains(key)) {
            current.remove(key)
        } else {
            current.add(key)
        }
        _memorizedVerses.value = current
        saveMemorized(current)
    }

    fun isMemorized(matnId: MatnId, baytNumber: Int): Boolean {
        return _memorizedVerses.value.contains("${matnId.name}_$baytNumber")
    }

    fun toggleBookmark(matnId: MatnId, baytNumber: Int) {
        val key = "${matnId.name}_$baytNumber"
        val current = _bookmarkedVerses.value.toMutableSet()
        if (current.contains(key)) {
            current.remove(key)
        } else {
            current.add(key)
        }
        _bookmarkedVerses.value = current
        saveBookmarks(current)
    }

    fun isBookmarked(matnId: MatnId, baytNumber: Int): Boolean {
        return _bookmarkedVerses.value.contains("${matnId.name}_$baytNumber")
    }

    fun saveQuizScore(chapterId: String, scorePercentage: Int) {
        val current = _quizScores.value.toMutableMap()
        val old = current[chapterId] ?: 0
        if (scorePercentage > old) {
            current[chapterId] = scorePercentage
            _quizScores.value = current
            saveQuizScores(current)
        }
    }

    fun getMemorizedCount(matnId: MatnId): Int {
        val prefix = "${matnId.name}_"
        return _memorizedVerses.value.count { it.startsWith(prefix) }
    }

    private fun normalizeArabic(text: String): String {
        return text.replace("أ", "ا")
            .replace("إ", "ا")
            .replace("آ", "ا")
            .replace("ة", "ه")
            .replace("ى", "ي")
            .replace(Regex("[\\u064B-\\u065F\\u0670]"), "") // Remove diacritics / harakat
            .replace(Regex("[()،.]"), "")
    }

    private fun loadMemorized(): Set<String> {
        return prefs.getStringSet("memorized_verses", emptySet()) ?: emptySet()
    }

    private fun saveMemorized(set: Set<String>) {
        prefs.edit().putStringSet("memorized_verses", set).apply()
    }

    private fun loadBookmarks(): Set<String> {
        return prefs.getStringSet("bookmarks", emptySet()) ?: emptySet()
    }

    private fun saveBookmarks(set: Set<String>) {
        prefs.edit().putStringSet("bookmarks", set).apply()
    }

    private fun loadQuizScores(): Map<String, Int> {
        val str = prefs.getString("quiz_scores", "") ?: ""
        if (str.isEmpty()) return emptyMap()
        return str.split(";").mapNotNull { entry ->
            val parts = entry.split("=")
            if (parts.size == 2) parts[0] to (parts[1].toIntOrNull() ?: 0) else null
        }.toMap()
    }

    private fun saveQuizScores(map: Map<String, Int>) {
        val str = map.entries.joinToString(";") { "${it.key}=${it.value}" }
        prefs.edit().putString("quiz_scores", str).apply()
    }
}
