package com.example.data.model

/**
 * Complete Quran Surah representation with metadata.
 */
data class QuranSurah(
    val number: Int,              // 1 to 114
    val nameAr: String,           // e.g. "الفاتحة", "البقرة"
    val nameEn: String,           // e.g. "Al-Fatihah", "Al-Baqarah"
    val englishTranslation: String,// e.g. "The Opening", "The Cow"
    val ayahCount: Int,           // e.g. 7, 286
    val revelationType: String,   // "مكية" or "مدنية"
    val revelationOrder: Int,
    val juzNumber: Int,
    val pageNumber: Int,
    val sampleAyahText: String,   // Full or featured verses text with full Tashkeel
    val wordsList: List<String> = emptyList() // List of words for the tracker
)

/**
 * State of an individual word in the Smart Recitation Tracker.
 */
enum class WordRecitationState {
    PENDING,        // Not yet evaluated or in progress
    CORRECT,        // Recited with correct Tajweed & pronunciation (Green)
    MISTAKE         // Recited with error - vowel, phoneme, omission (RED ALERT)
}

/**
 * Individual tracked word with detailed error diagnostics.
 */
data class TrackedWord(
    val index: Int,
    val wordWithTashkeel: String,
    val wordClean: String,
    val ayahNumber: Int,
    val state: WordRecitationState = WordRecitationState.PENDING,
    val userPronounced: String? = null,
    val mistakeCategory: String? = null,      // e.g. "خطأ إعرابي", "مخرج حرف", "لحن جلي", "نقص مد"
    val mistakeExplanationAr: String? = null,  // e.g. "قُرئت بالضم 'الرَّحْمَانُ' والصواب بالكسر 'الرَّحْمَٰنِ'"
    val mistakeExplanationEn: String? = null,
    val correctionAdviceAr: String? = null     // e.g. "احرص على خفض الفك وإتمام الكسرة في النون"
)

/**
 * Result of the Smart Recitation Tracker session.
 */
data class SmartTrackerResult(
    val surahNumber: Int,
    val surahNameAr: String,
    val ayahRange: String,
    val totalWords: Int,
    val correctWordsCount: Int,
    val mistakeWordsCount: Int,
    val accuracyPercentage: Int,
    val trackedWords: List<TrackedWord>,
    val generalFeedbackAr: String,
    val generalFeedbackEn: String,
    val hasMistakes: Boolean = mistakeWordsCount > 0,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Full Surah audio recitation information.
 */
data class FullReciterAudioSource(
    val reciterId: String,
    val reciterNameAr: String,
    val reciterNameEn: String,
    val serverBaseUrl: String,
    val riwayah: String = "حفص عن عاصم",
    val quality: String = "128 kbps"
)
