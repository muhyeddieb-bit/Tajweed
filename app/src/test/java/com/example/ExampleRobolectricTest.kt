package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.MatnId
import com.example.data.repository.JazariyyahData
import com.example.data.repository.TajweedRepository
import com.example.data.repository.TuhfatAlAtfalData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("متون التجويد", appName)
    }

    @Test
    fun `verify Tuhfat Al Atfal has 61 verses and 10 chapters`() {
        val chapters = TuhfatAlAtfalData.getChapters()
        assertEquals(10, chapters.size)
        val allVerses = chapters.flatMap { it.verses }
        assertEquals(61, allVerses.size)
        assertEquals(1, allVerses.first().number)
        assertEquals(61, allVerses.last().number)
    }

    @Test
    fun `verify Jazariyyah has 109 verses and 18 chapters`() {
        val chapters = JazariyyahData.getChapters()
        assertEquals(18, chapters.size)
        val allVerses = chapters.flatMap { it.verses }
        assertEquals(109, allVerses.size)
        assertEquals(1, allVerses.first().number)
        assertEquals(109, allVerses.last().number)
    }

    @Test
    fun `verify TajweedRepository search and memorization toggle`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = TajweedRepository(context)

        // Toggle memorization
        assertFalse(repo.isMemorized(MatnId.TUHFAT_AL_ATFAL, 1))
        repo.toggleMemorized(MatnId.TUHFAT_AL_ATFAL, 1)
        assertTrue(repo.isMemorized(MatnId.TUHFAT_AL_ATFAL, 1))

        // Search for verses
        val searchResults = repo.searchVerses("الجمزوري")
        assertTrue(searchResults.isNotEmpty())
        assertEquals(1, searchResults.first().number)
    }

    @Test
    fun `verify font size scaling and persistence`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = TajweedRepository(context)

        // Initial default font size
        val initialSize = repo.getSavedFontSize()
        assertEquals(21f, initialSize)

        // Save scaled font size (e.g. zoomed for readability)
        repo.saveFontSize(28f)
        assertEquals(28f, repo.getSavedFontSize())

        // Verify chapter title and explanation lookup for bayt
        val tuhfaVerses = repo.getAllVerses(MatnId.TUHFAT_AL_ATFAL)
        val firstBayt = tuhfaVerses.first()
        val chapterTitle = repo.getChapterTitleForBayt(firstBayt)
        assertTrue(chapterTitle.isNotEmpty())
    }

    @Test
    fun `verify Visual Map nodes and chapter branching integrity`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = TajweedRepository(context)

        val tuhfaChapters = repo.getAllChapters(MatnId.TUHFAT_AL_ATFAL)
        val jazariyyahChapters = repo.getAllChapters(MatnId.AL_JAZARIYYAH)

        assertTrue(tuhfaChapters.isNotEmpty())
        assertTrue(jazariyyahChapters.isNotEmpty())

        // Verify all chapter nodes have valid identifiers, titles, and verse ranges
        tuhfaChapters.forEach { ch ->
            assertTrue(ch.id.isNotEmpty())
            assertTrue(ch.title.isNotEmpty())
            assertTrue(ch.verses.isNotEmpty())
            assertTrue(ch.endBayt >= ch.startBayt)
        }

        jazariyyahChapters.forEach { ch ->
            assertTrue(ch.id.isNotEmpty())
            assertTrue(ch.title.isNotEmpty())
            assertTrue(ch.verses.isNotEmpty())
            assertTrue(ch.endBayt >= ch.startBayt)
        }
    }

    @Test
    fun `verify rich verse explanations and anatomical makharij database`() {
        // Verify Jazariyyah verse 9 has general makharij explanation
        val bayt9 = com.example.data.repository.VerseExplanationsData.getExplanation(
            MatnId.AL_JAZARIYYAH, 9
        )
        org.junit.Assert.assertNotNull(bayt9)
        assertTrue(bayt9!!.simplifiedMeaning.isNotEmpty())
        assertTrue(bayt9.tajweedRules.isNotEmpty())

        // Verify Jawf letters on verse 10
        val bayt10 = com.example.data.repository.VerseExplanationsData.getExplanation(
            MatnId.AL_JAZARIYYAH, 10
        )
        org.junit.Assert.assertNotNull(bayt10)
        assertEquals(com.example.data.repository.AnatomicalArea.JAWF, bayt10!!.anatomicalOrgan)

        // Verify MakharijDatabase covers all 5 main organs
        val allLetters = com.example.ui.components.MakharijDatabase.allLetters
        assertTrue(allLetters.isNotEmpty())

        val halqLetters = com.example.ui.components.MakharijDatabase.getLettersForOrgan(
            com.example.data.repository.AnatomicalArea.HALQ
        )
        assertEquals(6, halqLetters.size) // الهمزة والهاء، العين والحاء، الغين والخاء

        val shafatanLetters = com.example.ui.components.MakharijDatabase.getLettersForOrgan(
            com.example.data.repository.AnatomicalArea.SHAFATAN
        )
        assertTrue(shafatanLetters.isNotEmpty())
    }
}
