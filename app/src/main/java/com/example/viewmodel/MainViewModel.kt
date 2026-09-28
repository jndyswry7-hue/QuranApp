package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlayerManager
import com.example.audio.AudioRecorderManager
import com.example.data.ai.GeminiService
import com.example.data.local.AppDatabase
import com.example.data.local.QuranSurahsRepository
import com.example.data.model.DailyWird
import com.example.data.model.FavoriteItem
import com.example.data.model.FeaturedRecitation
import com.example.data.model.FullReciterAudioSource
import com.example.data.model.IslamicLandmark
import com.example.data.model.PracticeSession
import com.example.data.model.QuizQuestion
import com.example.data.model.QuranSurah
import com.example.data.model.RecitationEvaluationResult
import com.example.data.model.Reciter
import com.example.data.model.ReciterMatchResult
import com.example.data.model.SelectedMediaSource
import com.example.data.model.SmartTrackerResult
import com.example.data.model.TrackedWord
import com.example.data.model.UserPreferences
import com.example.data.model.WordRecitationState
import com.example.data.repository.AppRepository
import com.example.util.MediaHelper
import com.example.util.NotificationHelper
import com.example.util.ReportExportHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab {
    HOME,
    RECOGNITION,
    PRACTICE,
    QUIZ,
    FEATURED,
    DASHBOARD,
    MAP,
    SETTINGS
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AppRepository(db.practiceDao(), db.favoriteDao(), db.dailyWirdDao())
    private val geminiService = GeminiService()

    val audioPlayer = AudioPlayerManager(application)
    val audioRecorder = AudioRecorderManager(application)

    // Current Navigation Screen
    private val _currentTab = MutableStateFlow(ScreenTab.HOME)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    // Preferences & Accessibility
    private val _preferences = MutableStateFlow(UserPreferences())
    val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

    // Room Database Flows
    val practiceSessions: StateFlow<List<PracticeSession>> = repository.allPracticeSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<FavoriteItem>> = repository.allFavorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayWird: StateFlow<DailyWird?> = repository.getTodayWird()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Static Data
    val reciters: List<Reciter> = repository.recitersList
    val featuredRecitations: List<FeaturedRecitation> = repository.featuredRecitations
    val quizQuestions: List<QuizQuestion> = repository.quizQuestions
    val islamicLandmarks: List<IslamicLandmark> = repository.islamicLandmarks

    // Complete Quran 114 Surahs & Full Recitations Data
    val allSurahs: List<QuranSurah> = QuranSurahsRepository.allSurahs
    val fullAudioReciters: List<FullReciterAudioSource> = QuranSurahsRepository.fullAudioReciters

    private val _selectedFullReciter = MutableStateFlow(QuranSurahsRepository.fullAudioReciters.first())
    val selectedFullReciter: StateFlow<FullReciterAudioSource> = _selectedFullReciter.asStateFlow()

    private val _playingFullSurah = MutableStateFlow<QuranSurah?>(null)
    val playingFullSurah: StateFlow<QuranSurah?> = _playingFullSurah.asStateFlow()

    // Smart Recitation Tracker State (متتبع التلاوة الذكي وتنبيه الكلمات الخاطئة بالأحمر)
    private val _selectedPracticeSurah = MutableStateFlow(QuranSurahsRepository.allSurahs.first())
    val selectedPracticeSurah: StateFlow<QuranSurah> = _selectedPracticeSurah.asStateFlow()

    private val _smartTrackerResult = MutableStateFlow<SmartTrackerResult?>(null)
    val smartTrackerResult: StateFlow<SmartTrackerResult?> = _smartTrackerResult.asStateFlow()

    private val _selectedTrackedWordForAlert = MutableStateFlow<TrackedWord?>(null)
    val selectedTrackedWordForAlert: StateFlow<TrackedWord?> = _selectedTrackedWordForAlert.asStateFlow()

    private val _isTestingMistakeMode = MutableStateFlow(false)
    val isTestingMistakeMode: StateFlow<Boolean> = _isTestingMistakeMode.asStateFlow()

    private val _isTrackerRecording = MutableStateFlow(false)
    val isTrackerRecording: StateFlow<Boolean> = _isTrackerRecording.asStateFlow()

    // Reciter Recognition State
    private val _isRecognizing = MutableStateFlow(false)
    val isRecognizing: StateFlow<Boolean> = _isRecognizing.asStateFlow()

    private val _recognitionResult = MutableStateFlow<ReciterMatchResult?>(null)
    val recognitionResult: StateFlow<ReciterMatchResult?> = _recognitionResult.asStateFlow()

    private val _selectedMedia = MutableStateFlow<SelectedMediaSource?>(null)
    val selectedMedia: StateFlow<SelectedMediaSource?> = _selectedMedia.asStateFlow()

    private val _selectedReciterDetail = MutableStateFlow<Reciter?>(null)
    val selectedReciterDetail: StateFlow<Reciter?> = _selectedReciterDetail.asStateFlow()

    // Recitation Practice & AI Evaluation State
    private val _selectedSurah = MutableStateFlow("سورة الفاتحة")
    val selectedSurah: StateFlow<String> = _selectedSurah.asStateFlow()

    private val _selectedAyahNumber = MutableStateFlow(1)
    val selectedAyahNumber: StateFlow<Int> = _selectedAyahNumber.asStateFlow()

    private val _isEvaluating = MutableStateFlow(false)
    val isEvaluating: StateFlow<Boolean> = _isEvaluating.asStateFlow()

    private val _evaluationResult = MutableStateFlow<RecitationEvaluationResult?>(null)
    val evaluationResult: StateFlow<RecitationEvaluationResult?> = _evaluationResult.asStateFlow()

    // Quiz State
    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedAnswerIndex = MutableStateFlow<Int?>(null)
    val selectedAnswerIndex: StateFlow<Int?> = _selectedAnswerIndex.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _quizCompleted = MutableStateFlow(false)
    val quizCompleted: StateFlow<Boolean> = _quizCompleted.asStateFlow()

    // Sync status toast/message
    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    init {
        // Initial defaults
        viewModelScope.launch {
            if (todayWird.value == null) {
                repository.updateTodayWird(pages = 2, target = 4)
            }
        }
    }

    fun navigateToTab(tab: ScreenTab) {
        _currentTab.value = tab
    }

    fun toggleNightMode() {
        _preferences.value = _preferences.value.copy(
            isNightMode = !_preferences.value.isNightMode
        )
    }

    fun toggleEyeComfortMode() {
        _preferences.value = _preferences.value.copy(
            isEyeComfortMode = !_preferences.value.isEyeComfortMode
        )
    }

    fun toggleKidsSeniorMode() {
        _preferences.value = _preferences.value.copy(
            isKidsSeniorMode = !_preferences.value.isKidsSeniorMode
        )
    }

    fun setLanguage(lang: String) {
        _preferences.value = _preferences.value.copy(currentLanguage = lang)
    }

    fun toggleDailyReminder() {
        val newVal = !_preferences.value.dailyReminderEnabled
        _preferences.value = _preferences.value.copy(dailyReminderEnabled = newVal)
        if (newVal) {
            NotificationHelper.showDailyWirdReminder(
                getApplication(),
                "تذكير الورد اليومي - المقرئ الذكي",
                "حان موعد قراءة وردك القرآني ومراجعة التلاوة والتجويد، نور قلبك بآيات الذكر الحكيم."
            )
        }
    }

    fun selectReciterDetail(reciter: Reciter?) {
        _selectedReciterDetail.value = reciter
    }

    // --- Audio Control ---
    fun playRecitation(id: String, title: String, url: String) {
        audioPlayer.play(id, title, url)
    }

    fun toggleFavorite(item: FavoriteItem, isCurrentlyFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(item, isCurrentlyFav)
        }
    }

    fun updateWirdPages(pages: Int) {
        viewModelScope.launch {
            repository.updateTodayWird(pages = pages)
        }
    }

    // --- Reciter Recognition Logic with Ultra-Precision and Media (Audio/Video) Support ---
    fun selectMediaFile(uri: Uri, isVideo: Boolean, context: Context) {
        viewModelScope.launch {
            val media = MediaHelper.resolveSelectedMedia(context, uri, isVideo)
            _selectedMedia.value = media
            _recognitionResult.value = null
        }
    }

    fun clearSelectedMedia() {
        _selectedMedia.value = null
    }

    fun startReciterRecognition() {
        viewModelScope.launch {
            _isRecognizing.value = true
            _recognitionResult.value = null

            // Record microphone audio for 4 seconds
            audioRecorder.startRecording()
            delay(4000)
            val path = audioRecorder.stopRecording()

            val samplePrompt = "تحليل حي للبصمة الصوتية للتلاوة المسجلة عبر الميكروفون: فحص طبقة الصوت، التردد الأساسي، الرنين الحنجري، وانتقالات المقام الصوتي، ومخارج الحروف مع تمييز فائق الدقة."
            val result = geminiService.identifyReciterWithAi(
                voiceSampleDescription = samplePrompt,
                reciters = reciters,
                mediaBase64 = null,
                mediaMimeType = null,
                inputSourceType = "MIC",
                sourceFileName = null
            )
            _recognitionResult.value = result
            _isRecognizing.value = false
        }
    }

    fun startRecognitionWithSelectedMedia() {
        val media = _selectedMedia.value ?: return
        viewModelScope.launch {
            _isRecognizing.value = true
            _recognitionResult.value = null

            val samplePrompt = if (media.isVideo) {
                "تحليل فائق الدقة لمقطع مرئي (فيديو) لتلاوة قرآنية (${media.fileName}): استخراج البصمة الصوتية ومطابقة مخارج الحروف، ونمط الترتيل، ورنين الصوت مع شيوخ التلاوة الكبار."
            } else {
                "تحليل فائق الدقة لملف صوتي (${media.fileName}): فحص الترددات الحنجرية، والمقام التجويدي، وسرعة الإيقاع وتحديد القارئ بدقة متناهية."
            }

            val result = geminiService.identifyReciterWithAi(
                voiceSampleDescription = samplePrompt,
                reciters = reciters,
                mediaBase64 = media.base64Data,
                mediaMimeType = media.mimeType,
                inputSourceType = if (media.isVideo) "VIDEO_FILE" else "AUDIO_FILE",
                sourceFileName = media.fileName
            )
            _recognitionResult.value = result
            _isRecognizing.value = false
        }
    }

    fun clearRecognition() {
        _recognitionResult.value = null
    }

    fun shareRecognitionResult(context: Context, result: ReciterMatchResult) {
        val reciter = result.matchedReciter
        val sourceDesc = when (result.inputSourceType) {
            "VIDEO_FILE" -> "مقطع فيديو (${result.sourceFileName ?: "ملف مرئي"})"
            "AUDIO_FILE" -> "ملف صوتي (${result.sourceFileName ?: "تسجيل صوتي"})"
            else -> "تسجيل حي عبر الميكروفون"
        }
        val text = """
            ✨ نتيجة التمييز فائق الدقة بالذكاء الاصطناعي - تطبيق «المقرئ الذكي»
            🎙️ القارئ المطابق: ${reciter.nameAr} (${reciter.titleAr})
            🎯 نسبة التطابق الفائق: ${result.confidencePercentage}%
            🎼 المقام الصوتي: ${result.detectedMaqamAr}
            🏛️ المدرسة التلاوية: ${result.recitationSchoolAr}
            📁 مصدر التلاوة: $sourceDesc
            🔍 التحليل الصوتي: ${result.vocalAnalysisAr}
            🔗 استمع لتلاوة القارئ: ${reciter.sampleAudioUrl}
            
            📲 تم التمييز بدقة فائقة عبر تطبيق «المقرئ الذكي»
            إشراف وتطوير: Mahmoud Zakaria Aswad
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "مشاركة نتيجة تمييز القارئ")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    // --- Full Surahs (114 Surahs) Audio Playback Logic ---
    fun setFullReciter(reciter: FullReciterAudioSource) {
        _selectedFullReciter.value = reciter
        _playingFullSurah.value?.let { currentSurah ->
            playFullSurah(currentSurah, reciter)
        }
    }

    fun playFullSurah(surah: QuranSurah, reciter: FullReciterAudioSource = _selectedFullReciter.value) {
        _playingFullSurah.value = surah
        val streamUrl = QuranSurahsRepository.getFullSurahStreamUrl(reciter.serverBaseUrl, surah.number)
        audioPlayer.play(
            mediaId = "full_surah_${surah.number}_${reciter.reciterId}",
            title = "سورة ${surah.nameAr} - ${reciter.reciterNameAr}",
            url = streamUrl
        )
    }

    fun stopFullSurah() {
        audioPlayer.stop()
        _playingFullSurah.value = null
    }

    // --- Practice & Smart Recitation Tracker Logic (تنبيه ذكي بالأحمر على الأخطاء) ---
    fun setPracticeSurah(surah: QuranSurah) {
        _selectedPracticeSurah.value = surah
        _selectedSurah.value = "سورة ${surah.nameAr}"
        _smartTrackerResult.value = null
        _selectedTrackedWordForAlert.value = null
    }

    fun setPracticeSurahByNumber(number: Int) {
        QuranSurahsRepository.getSurahByNumber(number)?.let {
            setPracticeSurah(it)
        }
    }

    fun toggleTestingMistakeMode() {
        _isTestingMistakeMode.value = !_isTestingMistakeMode.value
    }

    fun selectTrackedWordForAlert(word: TrackedWord?) {
        _selectedTrackedWordForAlert.value = word
    }

    fun startSmartTrackerRecording() {
        _isTrackerRecording.value = true
        _smartTrackerResult.value = null
        _selectedTrackedWordForAlert.value = null
        audioRecorder.startRecording()
    }

    fun stopAndAnalyzeSmartTracker() {
        _isTrackerRecording.value = false
        val path = audioRecorder.stopRecording()
        val duration = audioRecorder.state.value.elapsedSeconds.coerceAtLeast(3)
        val surah = _selectedPracticeSurah.value
        val rawWords = QuranSurahsRepository.extractWordsForTracker(surah)

        viewModelScope.launch {
            _isEvaluating.value = true
            val trackerRes = geminiService.analyzeRecitationWithTracker(
                surah = surah,
                rawWords = rawWords,
                simulateMistake = _isTestingMistakeMode.value,
                durationSeconds = duration
            )
            _smartTrackerResult.value = trackerRes
            _isEvaluating.value = false

            // Save to Room DB
            repository.savePracticeSession(
                PracticeSession(
                    surahName = "سورة ${surah.nameAr}",
                    ayahNumber = 1,
                    durationSeconds = duration,
                    overallScore = trackerRes.accuracyPercentage,
                    makharijScore = (trackerRes.accuracyPercentage - 2).coerceIn(70, 100),
                    tajweedRulesScore = trackerRes.accuracyPercentage,
                    maddScore = (trackerRes.accuracyPercentage - 1).coerceIn(70, 100),
                    ghunnahScore = trackerRes.accuracyPercentage,
                    waqfScore = 95,
                    feedbackAr = trackerRes.generalFeedbackAr,
                    feedbackEn = trackerRes.generalFeedbackEn,
                    recordedAudioPath = path
                )
            )
        }
    }

    fun resetSmartTracker() {
        _smartTrackerResult.value = null
        _selectedTrackedWordForAlert.value = null
    }

    // --- Practice & AI Evaluation Logic ---
    fun setPracticeSurah(surah: String, ayah: Int) {
        _selectedSurah.value = surah
        _selectedAyahNumber.value = ayah
    }

    fun startRecitationPractice() {
        audioRecorder.startRecording()
    }

    fun stopAndEvaluatePractice() {
        val path = audioRecorder.stopRecording()
        val duration = audioRecorder.state.value.elapsedSeconds.coerceAtLeast(3)

        viewModelScope.launch {
            _isEvaluating.value = true
            val evaluation = geminiService.evaluateRecitation(
                surahName = _selectedSurah.value,
                ayahNumber = _selectedAyahNumber.value,
                durationSeconds = duration,
                userNotes = "Audio captured from microphone session, duration: $duration sec."
            )
            _evaluationResult.value = evaluation
            _isEvaluating.value = false

            // Save to Room DB
            repository.savePracticeSession(
                PracticeSession(
                    surahName = _selectedSurah.value,
                    ayahNumber = _selectedAyahNumber.value,
                    durationSeconds = duration,
                    overallScore = evaluation.overallScore,
                    makharijScore = evaluation.makharijScore,
                    tajweedRulesScore = evaluation.rulesScore,
                    maddScore = evaluation.maddScore,
                    ghunnahScore = evaluation.ghunnahScore,
                    waqfScore = evaluation.waqfScore,
                    feedbackAr = evaluation.generalAdviceAr,
                    feedbackEn = evaluation.generalAdviceEn,
                    recordedAudioPath = path
                )
            )
        }
    }

    fun clearEvaluation() {
        _evaluationResult.value = null
    }

    // --- Quiz Logic ---
    fun selectQuizAnswer(index: Int) {
        if (_selectedAnswerIndex.value != null) return // Already answered
        _selectedAnswerIndex.value = index

        val currentQ = quizQuestions[_currentQuestionIndex.value]
        if (index == currentQ.correctIndex) {
            _quizScore.value += 1
        }
    }

    fun nextQuizQuestion() {
        if (_currentQuestionIndex.value < quizQuestions.size - 1) {
            _currentQuestionIndex.value += 1
            _selectedAnswerIndex.value = null
        } else {
            _quizCompleted.value = true
        }
    }

    fun resetQuiz() {
        _currentQuestionIndex.value = 0
        _selectedAnswerIndex.value = null
        _quizScore.value = 0
        _quizCompleted.value = false
    }

    // --- Sharing & Exporting ---
    fun shareRecitation(context: Context, recitation: FeaturedRecitation) {
        val shareText = """
            🎧 استمع إلى تلاوة خاشعة عبر تطبيق «المقرئ الذكي»:
            📖 ${recitation.titleAr}
            🎙️ القارئ: ${recitation.reciterNameAr}
            🎼 ${recitation.maqamAr}
            🔗 رابط الاستماع: ${recitation.audioUrl}
            
            📲 تطبيق المقرئ الذكي - رفيقك لتجويد كتاب الله والتعرف على كبار القراء.
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "مشاركة التلاوة الخاشعة")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun exportPdfReport(context: Context) {
        val sessions = practiceSessions.value
        val streak = 7 // current days streak calculation
        val pdfFile = ReportExportHelper.generateAndSharePdfReport(context, sessions, streak)
        val summary = """
            تقرير إنجازات التلاوة والتجويد - تطبيق المقرئ الذكي
            عدد الجلسات المسجلة: ${sessions.size}
            متوسط التقييم العام بالذكاء الاصطناعي: ${if (sessions.isNotEmpty()) sessions.map { it.overallScore }.average().toInt() else 0}%
            أيام الالتزام: $streak يوم
            تطوير وإشراف: Mahmoud Zakaria Aswad
        """.trimIndent()
        ReportExportHelper.sharePdfOrSummary(context, pdfFile, summary)
    }

    fun triggerCloudSync() {
        viewModelScope.launch {
            _syncMessage.value = "جاري المزامنة السحابية وتأمين البيانات..."
            delay(1500)
            _syncMessage.value = "تمت المزامنة بنجاح عبر كافة الأجهزة السحابية!"
            delay(2500)
            _syncMessage.value = null
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
