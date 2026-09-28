package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data model for famous Quran reciters.
 */
data class Reciter(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val titleAr: String,
    val titleEn: String,
    val country: String,
    val birthYear: String,
    val primaryMaqam: String,
    val voiceTraitsAr: List<String>,
    val voiceTraitsEn: List<String>,
    val bioAr: String,
    val bioEn: String,
    val sampleAudioUrl: String,
    val famousSurah: String,
    val isRecognizableByAi: Boolean = true
)

/**
 * Featured recitations curated for users to discover and share.
 */
data class FeaturedRecitation(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val reciterId: String,
    val reciterNameAr: String,
    val reciterNameEn: String,
    val surahNameAr: String,
    val surahNameEn: String,
    val ayahRange: String,
    val maqamAr: String,
    val maqamEn: String,
    val audioUrl: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val durationSeconds: Int,
    val isFavorite: Boolean = false
)

/**
 * Interactive Islamic quiz question.
 */
data class QuizQuestion(
    val id: Int,
    val category: String, // TAJWEED, RECITERS, QURAN_SCIENCES, ISLAMIC_HISTORY
    val questionAr: String,
    val questionEn: String,
    val optionsAr: List<String>,
    val optionsEn: List<String>,
    val correctIndex: Int,
    val explanationAr: String,
    val explanationEn: String
)

/**
 * Room Entity: user recitation practice session with AI Tajweed evaluation scores.
 */
@Entity(tableName = "practice_sessions")
data class PracticeSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val surahName: String,
    val ayahNumber: Int,
    val durationSeconds: Int,
    val overallScore: Int,
    val makharijScore: Int,
    val tajweedRulesScore: Int,
    val maddScore: Int,
    val ghunnahScore: Int,
    val waqfScore: Int,
    val feedbackAr: String,
    val feedbackEn: String,
    val recordedAudioPath: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Room Entity: user favorites (reciters or audio clips).
 */
@Entity(tableName = "favorites")
data class FavoriteItem(
    @PrimaryKey val id: String,
    val itemType: String, // RECITER or RECITATION
    val titleAr: String,
    val titleEn: String,
    val subtitleAr: String,
    val subtitleEn: String,
    val audioUrl: String,
    val extraData: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Room Entity: daily Quran reading portion (الورد اليومي).
 */
@Entity(tableName = "daily_wird")
data class DailyWird(
    @PrimaryKey val dateStr: String, // YYYY-MM-DD
    val targetPages: Int = 4,
    val readPages: Int = 0,
    val isCompleted: Boolean = false,
    val notes: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Islamic Historic Centers & Mosques.
 */
data class IslamicLandmark(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val cityAr: String,
    val cityEn: String,
    val countryAr: String,
    val countryEn: String,
    val latitude: Double,
    val longitude: Double,
    val descriptionAr: String,
    val descriptionEn: String,
    val famousRecitersAr: String,
    val famousRecitersEn: String
)

/**
 * AI recitation assessment breakdown.
 */
data class RecitationEvaluationResult(
    val overallScore: Int,
    val makharijScore: Int,
    val rulesScore: Int,
    val maddScore: Int,
    val ghunnahScore: Int,
    val waqfScore: Int,
    val strengthsAr: List<String>,
    val improvementsAr: List<String>,
    val strengthsEn: List<String>,
    val improvementsEn: List<String>,
    val generalAdviceAr: String,
    val generalAdviceEn: String
)

/**
 * AI Reciter Recognition Result with Ultra-Precision Biometric and Acoustic Metrics.
 */
data class ReciterMatchResult(
    val matchedReciter: Reciter,
    val confidencePercentage: Int,
    val detectedMaqamAr: String,
    val detectedMaqamEn: String,
    val vocalAnalysisAr: String,
    val vocalAnalysisEn: String,
    val recitationSchoolAr: String = "المدرسة المصرية الذهبية",
    val recitationSchoolEn: String = "Golden Classical School",
    val detectedSurahOrAyah: String? = null,
    val acousticAffinities: List<String> = emptyList(),
    val inputSourceType: String = "MIC", // "MIC", "AUDIO_FILE", "VIDEO_FILE"
    val sourceFileName: String? = null
)

/**
 * Information about selected audio or video file for reciter recognition.
 */
data class SelectedMediaSource(
    val uriString: String,
    val fileName: String,
    val fileSizeFormatted: String,
    val mimeType: String,
    val isVideo: Boolean,
    val base64Data: String? = null
)

/**
 * User Preferences State.
 */
data class UserPreferences(
    val isNightMode: Boolean = false,
    val isEyeComfortMode: Boolean = false,
    val isKidsSeniorMode: Boolean = false,
    val currentLanguage: String = "ar", // "ar", "en", "fr"
    val dailyReminderEnabled: Boolean = true,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
    val cloudSyncEnabled: Boolean = true
)
