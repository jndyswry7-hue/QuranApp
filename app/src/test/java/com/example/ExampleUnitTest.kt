package com.example

import com.example.data.local.QuranSurahsRepository
import com.example.util.MediaHelper
import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun `media helper formatFileSize converts bytes properly`() {
    assertEquals("1.5 MB", MediaHelper.formatFileSize((1.5 * 1024 * 1024).toLong()))
    assertEquals("500 KB", MediaHelper.formatFileSize(500 * 1024))
  }

  @Test
  fun `quran surahs repository contains all 114 surahs`() {
    val surahs = QuranSurahsRepository.allSurahs
    assertEquals(114, surahs.size)
    assertEquals(1, surahs.first().number)
    assertEquals("الفاتحة", surahs.first().nameAr)
    assertEquals(114, surahs.last().number)
    assertEquals("الناس", surahs.last().nameAr)
  }

  @Test
  fun `quran surahs repository formats stream url with 3-digit padding`() {
    val url1 = QuranSurahsRepository.getFullSurahStreamUrl("https://server8.mp3quran.net/afs/", 1)
    assertEquals("https://server8.mp3quran.net/afs/001.mp3", url1)

    val url114 = QuranSurahsRepository.getFullSurahStreamUrl("https://server8.mp3quran.net/afs", 114)
    assertEquals("https://server8.mp3quran.net/afs/114.mp3", url114)
  }

  @Test
  fun `words extractor extracts words cleanly for recitation tracker`() {
    val fatihah = QuranSurahsRepository.getSurahByNumber(1)!!
    val words = QuranSurahsRepository.extractWordsForTracker(fatihah)
    assertTrue(words.isNotEmpty())
    assertFalse(words.any { it.contains("۝") })
    assertTrue(words.first().contains("بِسْمِ") || words.first().contains("بسم"))
  }
}

