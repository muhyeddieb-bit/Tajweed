package com.example.ui.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.data.model.Bayt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class MatnTtsManager(context: Context) {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPlayingBayt = MutableStateFlow<Int?>(null)
    val currentPlayingBayt: StateFlow<Int?> = _currentPlayingBayt.asStateFlow()

    private val _repeatCount = MutableStateFlow(1) // 1x, 3x, 5x
    val repeatCount: StateFlow<Int> = _repeatCount.asStateFlow()

    private var remainingRepeats = 0
    private var activeBayt: Bayt? = null

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val localeAr = Locale("ar")
                val result = tts?.setLanguage(localeAr)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setSpeechRate(0.88f) // Clear, measured recitation pace
                    tts?.setPitch(1.0f)
                    isInitialized = true
                }
            }
        }

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isPlaying.value = true
            }

            override fun onDone(utteranceId: String?) {
                if (remainingRepeats > 1 && activeBayt != null) {
                    remainingRepeats--
                    speakCurrentBayt()
                } else {
                    _isPlaying.value = false
                    _currentPlayingBayt.value = null
                    activeBayt = null
                }
            }

            override fun onError(utteranceId: String?) {
                _isPlaying.value = false
                _currentPlayingBayt.value = null
            }
        })
    }

    fun setRepeatCount(count: Int) {
        _repeatCount.value = count
    }

    fun speakBayt(bayt: Bayt) {
        if (!isInitialized) return

        if (_currentPlayingBayt.value == bayt.number && _isPlaying.value) {
            stop()
            return
        }

        stop()
        activeBayt = bayt
        remainingRepeats = _repeatCount.value
        _currentPlayingBayt.value = bayt.number
        speakCurrentBayt()
    }

    private fun speakCurrentBayt() {
        val bayt = activeBayt ?: return
        val speechText = "${bayt.firstHalf} ... ${bayt.secondHalf}"
        tts?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "bayt_${bayt.number}_${System.currentTimeMillis()}")
    }

    fun stop() {
        tts?.stop()
        _isPlaying.value = false
        _currentPlayingBayt.value = null
        activeBayt = null
        remainingRepeats = 0
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
