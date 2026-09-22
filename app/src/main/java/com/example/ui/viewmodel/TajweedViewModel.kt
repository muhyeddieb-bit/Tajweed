package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiTutorService
import com.example.data.model.Bayt
import com.example.data.model.Chapter
import com.example.data.model.MatnId
import com.example.data.repository.TajweedRepository
import com.example.ui.audio.MatnTtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    VISUAL_MAP,
    VERSES_READER,
    ANATOMICAL_MAKHARIS,
    PWA_VIEWER,
    CHAPTERS,
    STUDY,
    SEARCH,
    AI_TUTOR
}

enum class VerseFilterCategory(val titleAr: String) {
    ALL("جميع الأبيات"),
    MEMORIZED("المحفوظة"),
    BOOKMARKED("المفضلة")
}

enum class StudyTab(val titleAr: String) {
    MEMORIZATION("أ. الحفظ (المتن)"),
    UNDERSTANDING("ب. الفهم (الشرح والرسوم)"),
    QUIZ("ج. التقييم (الاختبار)")
}

data class ChatMessage(
    val sender: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class TajweedViewModel(application: Application) : AndroidViewModel(application) {

    val repository = TajweedRepository(application)
    val ttsManager = MatnTtsManager(application)
    private val aiService = GeminiTutorService()

    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _selectedMatn = MutableStateFlow(MatnId.TUHFAT_AL_ATFAL)
    val selectedMatn: StateFlow<MatnId> = _selectedMatn.asStateFlow()

    private val _selectedChapter = MutableStateFlow<Chapter?>(null)
    val selectedChapter: StateFlow<Chapter?> = _selectedChapter.asStateFlow()

    private val _activeStudyTab = MutableStateFlow(StudyTab.MEMORIZATION)
    val activeStudyTab: StateFlow<StudyTab> = _activeStudyTab.asStateFlow()

    // Quiz State
    private val _quizQuestionIndex = MutableStateFlow(0)
    val quizQuestionIndex: StateFlow<Int> = _quizQuestionIndex.asStateFlow()

    private val _selectedQuizOption = MutableStateFlow<Int?>(null)
    val selectedQuizOption: StateFlow<Int?> = _selectedQuizOption.asStateFlow()

    private val _isQuizSubmitted = MutableStateFlow(false)
    val isQuizSubmitted: StateFlow<Boolean> = _isQuizSubmitted.asStateFlow()

    private val _quizCorrectAnswersCount = MutableStateFlow(0)
    val quizCorrectAnswersCount: StateFlow<Int> = _quizCorrectAnswersCount.asStateFlow()

    private val _isQuizCompleted = MutableStateFlow(false)
    val isQuizCompleted: StateFlow<Boolean> = _isQuizCompleted.asStateFlow()

    // Search State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Bayt>>(emptyList())
    val searchResults: StateFlow<List<Bayt>> = _searchResults.asStateFlow()

    // AI Tutor State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "خبير التجويد",
                content = "مرحباً بك في المحرك التعليمي لمتني الجزرية وتحفة الأطفال! 📖\nاسألني عن أي حكم، مخرج حرف، توجيه بيّت، أو سبب لخطأ في التلاوة.",
                isFromUser = false
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    val memorizedVerses: StateFlow<Set<String>> = repository.memorizedVerses
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    val bookmarkedVerses: StateFlow<Set<String>> = repository.bookmarkedVerses
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    val quizScores: StateFlow<Map<String, Int>> = repository.quizScores
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    // Font Size Zoom State (تكبير وتصغير الخط)
    private val _verseFontSize = MutableStateFlow(repository.getSavedFontSize())
    val verseFontSize: StateFlow<Float> = _verseFontSize.asStateFlow()

    fun increaseVerseFontSize() {
        val newSize = (_verseFontSize.value + 2f).coerceAtMost(36f)
        _verseFontSize.value = newSize
        repository.saveFontSize(newSize)
    }

    fun decreaseVerseFontSize() {
        val newSize = (_verseFontSize.value - 2f).coerceAtLeast(15f)
        _verseFontSize.value = newSize
        repository.saveFontSize(newSize)
    }

    fun setVerseFontSize(size: Float) {
        val clamped = size.coerceIn(15f, 36f)
        _verseFontSize.value = clamped
        repository.saveFontSize(clamped)
    }

    fun resetVerseFontSize() {
        _verseFontSize.value = 21f
        repository.saveFontSize(21f)
    }

    // Verses Reader Interactive Filters State
    private val _verseReaderMatn = MutableStateFlow(MatnId.TUHFAT_AL_ATFAL)
    val verseReaderMatn: StateFlow<MatnId> = _verseReaderMatn.asStateFlow()

    private val _verseFilterCategory = MutableStateFlow(VerseFilterCategory.ALL)
    val verseFilterCategory: StateFlow<VerseFilterCategory> = _verseFilterCategory.asStateFlow()

    private val _verseFilterChapterId = MutableStateFlow<String?>(null)
    val verseFilterChapterId: StateFlow<String?> = _verseFilterChapterId.asStateFlow()

    private val _readerSearchQuery = MutableStateFlow("")
    val readerSearchQuery: StateFlow<String> = _readerSearchQuery.asStateFlow()

    fun setVerseReaderMatn(matnId: MatnId) {
        _verseReaderMatn.value = matnId
        _verseFilterChapterId.value = null
    }

    fun setVerseFilterCategory(category: VerseFilterCategory) {
        _verseFilterCategory.value = category
    }

    fun setVerseFilterChapterId(chapterId: String?) {
        _verseFilterChapterId.value = chapterId
    }

    fun setReaderSearchQuery(query: String) {
        _readerSearchQuery.value = query
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun selectMatn(matnId: MatnId) {
        _selectedMatn.value = matnId
        _selectedChapter.value = null
        _currentScreen.value = Screen.CHAPTERS
    }

    fun selectChapter(chapter: Chapter, initialTab: StudyTab = StudyTab.MEMORIZATION) {
        _selectedChapter.value = chapter
        _activeStudyTab.value = initialTab
        resetQuizState()
        _currentScreen.value = Screen.STUDY
    }

    fun openChapterVersesOnly(chapter: Chapter) {
        _verseReaderMatn.value = chapter.matnId
        _verseFilterCategory.value = VerseFilterCategory.ALL
        _verseFilterChapterId.value = chapter.id
        _readerSearchQuery.value = ""
        _currentScreen.value = Screen.VERSES_READER
    }

    fun setStudyTab(tab: StudyTab) {
        _activeStudyTab.value = tab
    }

    fun toggleMemorize(bayt: Bayt) {
        repository.toggleMemorized(bayt.matnId, bayt.number)
    }

    fun toggleBookmark(bayt: Bayt) {
        repository.toggleBookmark(bayt.matnId, bayt.number)
    }

    // Quiz functions
    fun selectQuizOption(index: Int) {
        if (!_isQuizSubmitted.value) {
            _selectedQuizOption.value = index
        }
    }

    fun submitQuizAnswer() {
        val chapter = _selectedChapter.value ?: return
        val currentQuestion = chapter.quiz.getOrNull(_quizQuestionIndex.value) ?: return
        val selected = _selectedQuizOption.value ?: return

        _isQuizSubmitted.value = true
        if (selected == currentQuestion.correctIndex) {
            _quizCorrectAnswersCount.value += 1
        }
    }

    fun nextQuizQuestion() {
        val chapter = _selectedChapter.value ?: return
        if (_quizQuestionIndex.value + 1 < chapter.quiz.size) {
            _quizQuestionIndex.value += 1
            _selectedQuizOption.value = null
            _isQuizSubmitted.value = false
        } else {
            // Quiz completed!
            _isQuizCompleted.value = true
            val total = chapter.quiz.size
            val percentage = if (total > 0) (_quizCorrectAnswersCount.value * 100) / total else 100
            repository.saveQuizScore(chapter.id, percentage)
        }
    }

    fun resetQuizState() {
        _quizQuestionIndex.value = 0
        _selectedQuizOption.value = null
        _isQuizSubmitted.value = false
        _quizCorrectAnswersCount.value = 0
        _isQuizCompleted.value = false
    }

    // Search
    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        _searchResults.value = repository.searchVerses(newQuery)
    }

    // AI Tutor
    fun sendAiQuestion(userQuestion: String) {
        val trimmed = userQuestion.trim()
        if (trimmed.isEmpty()) return

        val userMsg = ChatMessage(sender = "أنت", content = trimmed, isFromUser = true)
        _chatMessages.value = _chatMessages.value + userMsg
        _isAiLoading.value = true

        viewModelScope.launch {
            val chapterTitle = _selectedChapter.value?.title
            val responseText = aiService.askTutor(trimmed, chapterTitle)
            val aiMsg = ChatMessage(sender = "خبير التجويد", content = responseText, isFromUser = false)
            _chatMessages.value = _chatMessages.value + aiMsg
            _isAiLoading.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
