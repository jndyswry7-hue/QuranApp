package com.example.data.repository

import com.example.data.local.DailyWirdDao
import com.example.data.local.FavoriteDao
import com.example.data.local.PracticeDao
import com.example.data.model.DailyWird
import com.example.data.model.FavoriteItem
import com.example.data.model.FeaturedRecitation
import com.example.data.model.IslamicLandmark
import com.example.data.model.PracticeSession
import com.example.data.model.QuizQuestion
import com.example.data.model.Reciter
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppRepository(
    private val practiceDao: PracticeDao,
    private val favoriteDao: FavoriteDao,
    private val dailyWirdDao: DailyWirdDao
) {
    // --- Room Database Operations ---
    val allPracticeSessions: Flow<List<PracticeSession>> = practiceDao.getAllSessions()
    val practiceCount: Flow<Int> = practiceDao.getSessionCount()
    val averageScore: Flow<Double?> = practiceDao.getAverageOverallScore()
    val allFavorites: Flow<List<FavoriteItem>> = favoriteDao.getAllFavorites()

    suspend fun savePracticeSession(session: PracticeSession): Long =
        practiceDao.insertSession(session)

    suspend fun deletePracticeSession(id: Long) =
        practiceDao.deleteSessionById(id)

    fun isFavorite(id: String): Flow<Boolean> = favoriteDao.isFavorite(id)

    suspend fun toggleFavorite(item: FavoriteItem, isFav: Boolean) {
        if (isFav) {
            favoriteDao.deleteFavoriteById(item.id)
        } else {
            favoriteDao.insertFavorite(item)
        }
    }

    fun getTodayWird(): Flow<DailyWird?> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        return dailyWirdDao.getWirdForDate(today)
    }

    suspend fun updateTodayWird(pages: Int, target: Int = 4) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val isDone = pages >= target
        dailyWirdDao.insertOrUpdateWird(
            DailyWird(
                dateStr = today,
                targetPages = target,
                readPages = pages,
                isCompleted = isDone
            )
        )
    }

    // --- Curated Famous Quran Reciters Database ---
    val recitersList: List<Reciter> = listOf(
        Reciter(
            id = "abdulbasit",
            nameAr = "الشيخ عبد الباسط عبد الصمد",
            nameEn = "Sheikh Abdul Basit Abdus Samad",
            titleAr = "صوت مكة وكروان الجنة",
            titleEn = "Voice of Makkah",
            country = "مصر / Egypt",
            birthYear = "1927 - 1988",
            primaryMaqam = "البياتي والصبا (Bayati & Saba)",
            voiceTraitsAr = listOf("نَفَس طويل استثنائي", "طبقة صوتية حادة وشجية (جواب الجواب)", "تحكم فريد في النبرات والأوزان"),
            voiceTraitsEn = listOf("Exceptional breath control", "High soaring register (Jawab al-Jawab)", "Iconic spiritual resonance"),
            bioAr = "أحد أشهر قراء القرآن الكريم في العالم الإسلامي، لُقِّب بـ 'صوت مكة' لجمال تلاوته وخشوع نبراته وإتقانه لأحكام التجويد والقراءات السبع.",
            bioEn = "One of the most revered Quran reciters in history, known globally for his breathtaking vocal range and profound mastery of Tajweed.",
            sampleAudioUrl = "https://everyayah.com/data/Abdul_Basit_Murattal_192kbps/001001.mp3",
            famousSurah = "سورة مريم والحاقة والرحمن"
        ),
        Reciter(
            id = "minshawi",
            nameAr = "الشيخ محمد صديق المنشاوي",
            nameEn = "Sheikh Mohamed Siddiq El-Minshawi",
            titleAr = "الصوت الباكي ذو الخشوع الفائق",
            titleEn = "The Weeping Voice",
            country = "مصر / Egypt",
            birthYear = "1920 - 1969",
            primaryMaqam = "النهاوند والرست (Nahawand & Rast)",
            voiceTraitsAr = listOf("نبرة حزينة باكية تأسر القلوب", "إيقاع هادئ وتؤدة بالغة في الترتيل", "دقة لا متناهية في مخارج الحروف"),
            voiceTraitsEn = listOf("Soulful, tearful resonance", "Calm, deeply meditative pacing", "Impeccable letter articulation"),
            bioAr = "عَلَم من أعلام التلاوة في العصر الذهبي، عُرِف بنبرته الباكية التي تنفذ إلى أعماق النفس، وُلد في المنشاة بسوهاج ونشأ في بيت علم وقرآن.",
            bioEn = "Revered for his deeply moving, contemplative style that conveys the solemn beauty and deep meanings of the Holy Quran.",
            sampleAudioUrl = "https://everyayah.com/data/Minshawy_Murattal_128kbps/001001.mp3",
            famousSurah = "سورة يوسف والروم والفجر"
        ),
        Reciter(
            id = "husary",
            nameAr = "الشيخ محمود خليل الحصري",
            nameEn = "Sheikh Mahmoud Khalil Al-Husary",
            titleAr = "شيخ عموم المقارئ المصرية ومعلم الأجيال",
            titleEn = "Master of Precise Tajweed",
            country = "مصر / Egypt",
            birthYear = "1917 - 1980",
            primaryMaqam = "البياتي والعجم (Bayati & Ajam)",
            voiceTraitsAr = listOf("المعيار الذهبي لأحكام التجويد", "نطق سليم للمدود والغُنَن", "وضوح مخارج الحروف لتعليم الصغار والكبار"),
            voiceTraitsEn = listOf("Gold standard of Tajweed rules", "Flawless phonetic accuracy", "Ideal for students and learners"),
            bioAr = "أول من سجل المصحف المرتل في العالم برواية حفص عن عاصم، وكان حجة في علوم القراءات والتجويد ورئيساً لمصححي المصاحف.",
            bioEn = "The first to record the complete recited Quran (Murattal); universally considered the ultimate reference for Tajweed accuracy.",
            sampleAudioUrl = "https://everyayah.com/data/Husary_128kbps/001001.mp3",
            famousSurah = "المصحف المرتل كاملاً وسورة إبراهيم"
        ),
        Reciter(
            id = "alafasy",
            nameAr = "الشيخ مشاري راشد العفاسي",
            nameEn = "Sheikh Mishary Rashid Alafasy",
            titleAr = "إمام المسجد الكبير بالكويت ومنشد الآفاق",
            titleEn = "Imam of the Grand Mosque of Kuwait",
            country = "الكويت / Kuwait",
            birthYear = "1976 - معاصر",
            primaryMaqam = "الحجاز والكرد (Hijaz & Kurd)",
            voiceTraitsAr = listOf("نقاء صوتي رنان وعصري", "تأثير عاطفي وسلاسة في الانتقال الصوتي", "عذوبة متميزة في الأداء"),
            voiceTraitsEn = listOf("Resonant modern acoustic timbre", "Emotional transitions", "Crystal-clear melodic vocal flow"),
            bioAr = "قارئ ومنشد كويتي نال شهرة عالمية واسعة، درس القراءات العشر والتفسير في الجامعة الإسلامية بالمدينة المنورة وتخرج بامتياز.",
            bioEn = "Internationally acclaimed reciter and nasheed artist, studied the Ten Recitations and Tafsir at the Islamic University of Madinah.",
            sampleAudioUrl = "https://everyayah.com/data/Alafasy_128kbps/001001.mp3",
            famousSurah = "سورة الكهف والملك وغافر"
        ),
        Reciter(
            id = "sudais",
            nameAr = "الشيخ عبد الرحمن السديس",
            nameEn = "Sheikh Abdul Rahman Al-Sudais",
            titleAr = "إمام وخطيب المسجد الحرام بمكة المكرمة",
            titleEn = "Imam of the Grand Mosque of Makkah",
            country = "السعودية / Saudi Arabia",
            birthYear = "1960 - معاصر",
            primaryMaqam = "الرست والسيكاه (Rast & Sikah)",
            voiceTraitsAr = listOf("نبرة جهرية مؤثرة تميز صلوات الحرم", "إيقاع حماسي متدفق مع الخشوع", "تأثر بالدعاء والبكاء في التهجد"),
            voiceTraitsEn = listOf("Iconic Makkah prayer reverberation", "Energetic flowing cadence", "Deeply emotive supplication tone"),
            bioAr = "الرئيس العام لشؤون المسجد الحرام والمسجد النبوي وإمام الحرم المكي لأكثر من أربعة عقود، بصوته الذي يصدح في أرجاء العالم في صلوات التراويح.",
            bioEn = "Chief Imam and leader of the Grand Mosque in Makkah for over 40 years, loved globally for his Ramadan Taraweeh recitations.",
            sampleAudioUrl = "https://everyayah.com/data/Abdurrahmaan_As-Sudais_192kbps/001001.mp3",
            famousSurah = "سورة البقرة وصلوات التراويح والختمات"
        ),
        Reciter(
            id = "muaiqly",
            nameAr = "الشيخ ماهر المعيقلي",
            nameEn = "Sheikh Maher Al-Muaiqly",
            titleAr = "إمام المسجد الحرام",
            titleEn = "Imam of the Sacred Mosque",
            country = "السعودية / Saudi Arabia",
            birthYear = "1969 - معاصر",
            primaryMaqam = "البياتي والكرد (Bayati & Kurd)",
            voiceTraitsAr = listOf("صوت رخيم دافئ ومهدئ للأعصاب", "ترتيل عذب يبعث السكينة والاطمئنان", "توازن مثالي بين السرعة والخشوع"),
            voiceTraitsEn = listOf("Warm, soothing baritone", "Calming contemplative delivery", "Perfect balance of tempo and devotion"),
            bioAr = "إمام الحرم المكي الشريف، حاصل على الدكتوراه في الشريعة، اشتُهر بتلاواته الهادئة العذبة التي يستمع إليها الملايين حول العالم.",
            bioEn = "Beloved Imam of Makkah known for his tranquil, melodious voice that touches hearts across the world.",
            sampleAudioUrl = "https://everyayah.com/data/MaherAlMuaiqly128kbps/001001.mp3",
            famousSurah = "سورة النمل ويس والواقعة"
        ),
        Reciter(
            id = "ghamdi",
            nameAr = "الشيخ سعد الغامدي",
            nameEn = "Sheikh Saad Al-Ghamdi",
            titleAr = "صاحب الصوت الرخيم والأداء المتوازن",
            titleEn = "Gentle Harmonious Reciter",
            country = "السعودية / Saudi Arabia",
            birthYear = "1967 - معاصر",
            primaryMaqam = "النهاوند (Nahawand)",
            voiceTraitsAr = listOf("صوت رصين ومتناسق النغمات", "مخارج واضحة وسهلة المتابعة", "طابع شجي وقور"),
            voiceTraitsEn = listOf("Smooth harmonic transitions", "Clear accessible cadence", "Serene and dignified style"),
            bioAr = "قارئ سعودي وإمام سابق لعدة مساجد كبرى، نالت تسجيلاته للمصحف المرتل انتشاراً واسعاً على مدى عقود متتالية.",
            bioEn = "Renowned Saudi reciter whose recordings are treasured globally for their peaceful clarity.",
            sampleAudioUrl = "https://everyayah.com/data/Ghamadi_40kbps/001001.mp3",
            famousSurah = "سورة آل عمران ومريم"
        ),
        Reciter(
            id = "dosari",
            nameAr = "الشيخ ياسر الدوسري",
            nameEn = "Sheikh Yasser Al-Dosari",
            titleAr = "إمام وخطيب المسجد الحرام",
            titleEn = "Imam of the Grand Mosque of Makkah",
            country = "السعودية / Saudi Arabia",
            birthYear = "1980 - معاصر",
            primaryMaqam = "الصبا والحجاز (Saba & Hijaz)",
            voiceTraitsAr = listOf("قوة صوتية هائلة وطبقات عليا", "تنويع مقامي مبهر مع إحساس طاغٍ", "تأثير روحي عميق"),
            voiceTraitsEn = listOf("Vocal power with high notes", "Dynamic maqam diversity", "Intense spiritual emotion"),
            bioAr = "إمام وخطيب المسجد الحرام، أستاذ الفقه المقارن، يتميز بصوته القوي الشجي وأدائه الآسر للقلوب في صلوات الفجر والتراويح.",
            bioEn = "Vibrant Imam of Makkah renowned for his impassioned recitations and wide dynamic range.",
            sampleAudioUrl = "https://everyayah.com/data/Yasser_Ad-Dussary_128kbps/001001.mp3",
            famousSurah = "سورة ق والصافات والقيامة"
        ),
        Reciter(
            id = "mustafa_ismail",
            nameAr = "الشيخ مصطفى إسماعيل",
            nameEn = "Sheikh Mustafa Ismail",
            titleAr = "سلطان التلاوة وعبقري النغم القرآني",
            titleEn = "The Sultan of Recitation",
            country = "مصر / Egypt",
            birthYear = "1905 - 1978",
            primaryMaqam = "الرست والسيكاه والبياتي المركب",
            voiceTraitsAr = listOf("تنقلات مقامية فريدة ومستحيلة", "جوابات وجواب الجواب بارتياح تام", "تفسير تصويري للآيات بنبرات الصوت"),
            voiceTraitsEn = listOf("Legendary maqam transitions", "Extensive multioctave register", "Vocal exegesis painting meanings"),
            bioAr = "قارئ الملوك والرؤساء، قارئ الجامع الأزهر الشريف، اعتبره كبار الموسيقيين ظاهرة صوتية خارقة في التلوين النغمي القرآني الخاشع.",
            bioEn = "One of the greatest reciters of all time, revered for his unprecedented ability to shift seamlessly through complex maqams.",
            sampleAudioUrl = "https://everyayah.com/data/Mustafa_Ismail_48kbps/001001.mp3",
            famousSurah = "سورة هود وفاطر والحجرات"
        ),
        Reciter(
            id = "banna",
            nameAr = "الشيخ محمود علي البنا",
            nameEn = "Sheikh Mahmoud Ali Al-Banna",
            titleAr = "الصوت الندي الخاشع وأستاذ الترتيل",
            titleEn = "The Gentle & Precise Reciter",
            country = "مصر / Egypt",
            birthYear = "1926 - 1985",
            primaryMaqam = "البياتي والصبا (Bayati & Saba)",
            voiceTraitsAr = listOf("نبرة ندية مريحة للنفس", "انضباط تجويدي فائق الشفافية", "نفس هادئ ممتد بخشوع"),
            voiceTraitsEn = listOf("Pure soothing timbre", "Flawless Tajweed discipline", "Serene meditative flow"),
            bioAr = "من الرعيل الذهبي لكبار القراء، اشتُهر بحلاوة صوته ورصانته، وسجل المصحف المرتل برواية حفص عن عاصم، وكان إماماً لمسجد الإمام الحسين.",
            bioEn = "Prominent golden era master, famous for his crystal purity and tranquil delivery.",
            sampleAudioUrl = "https://everyayah.com/data/Banna_32kbps/001001.mp3",
            famousSurah = "سورة مريم ومحمد وق"
        ),
        Reciter(
            id = "tablawi",
            nameAr = "الشيخ محمد محمود الطبلاوي",
            nameEn = "Sheikh Mohamed Mahmoud Al-Tablawi",
            titleAr = "نقيب القراء وصاحب النبرة القوية الصداحة",
            titleEn = "The Resonant Powerhouse",
            country = "مصر / Egypt",
            birthYear = "1934 - 2020",
            primaryMaqam = "الرست والحجاز (Rast & Hijaz)",
            voiceTraitsAr = listOf("قوة حنجرية استثنائية ونبرة مجلجلة", "إحكام تام لقفلات الآيات", "عُرب صوتية أصيلة"),
            voiceTraitsEn = listOf("Resonant robust acoustic power", "Iconic cadential endings", "Deep classical Arab ornamentation"),
            bioAr = "نقيب قراء مصر الراحل، أحد أبرز أعلام التلاوة، امتاز بصوته الفريد ذي البحة المميزة والقوة التي تملأ الآفاق هيبة وجلالاً.",
            bioEn = "Former head of the Egyptian Reciters Guild, renowned for his distinct powerhouse timbre and soaring cadences.",
            sampleAudioUrl = "https://everyayah.com/data/Mohammad_al_Tablaway_128kbps/001001.mp3",
            famousSurah = "سورة القيامة والملك والرحمن"
        )
    )

    // --- Curated Featured Recitations ---
    val featuredRecitations: List<FeaturedRecitation> = listOf(
        FeaturedRecitation(
            id = "feat_1",
            titleAr = "تلاوة خاشعة نادرة - الفاتحة وأول البقرة",
            titleEn = "Spiritual Masterpiece - Al-Fatihah & Al-Baqarah",
            reciterId = "abdulbasit",
            reciterNameAr = "الشيخ عبد الباسط عبد الصمد",
            reciterNameEn = "Sheikh Abdul Basit",
            surahNameAr = "سورة الفاتحة",
            surahNameEn = "Surah Al-Fatihah",
            ayahRange = "١ - ٧",
            maqamAr = "مقام البياتي",
            maqamEn = "Maqam Bayati",
            audioUrl = "https://everyayah.com/data/Abdul_Basit_Murattal_192kbps/001001.mp3",
            descriptionAr = "تلاوة خالدة مجودة تسافر بالروح إلى عوالم النقاء الإيماني، تسحر السامعين بنَفَسها المديد.",
            descriptionEn = "Timeless recitation touching the soul with unmatched breath control and majestic reverence.",
            durationSeconds = 65
        ),
        FeaturedRecitation(
            id = "feat_2",
            titleAr = "الصوت الباكي - سورة يوسف بتؤدة",
            titleEn = "The Weeping Voice - Surah Yusuf",
            reciterId = "minshawi",
            reciterNameAr = "الشيخ محمد صديق المنشاوي",
            reciterNameEn = "Sheikh Mohamed Siddiq El-Minshawi",
            surahNameAr = "سورة يوسف",
            surahNameEn = "Surah Yusuf",
            ayahRange = "١ - ١٥",
            maqamAr = "مقام النهاوند الحزين",
            maqamEn = "Maqam Nahawand",
            audioUrl = "https://everyayah.com/data/Minshawy_Murattal_128kbps/001001.mp3",
            descriptionAr = "خشوع لا يُضاهى يجسد معاني الصبر والفرج بأداء يفيض رقة وبكاءً وإيماناً.",
            descriptionEn = "Incomparable devotion expressing grief and hope with profound humility.",
            durationSeconds = 120
        ),
        FeaturedRecitation(
            id = "feat_3",
            titleAr = "ترتيل متقن - سورة الإخلاص والمعوذتين",
            titleEn = "Flawless Tajweed - Al-Ikhlas & Al-Mu'awwidhatayn",
            reciterId = "husary",
            reciterNameAr = "الشيخ محمود خليل الحصري",
            reciterNameEn = "Sheikh Mahmoud Khalil Al-Husary",
            surahNameAr = "سورة الإخلاص",
            surahNameEn = "Surah Al-Ikhlas",
            ayahRange = "١ - ٤",
            maqamAr = "مقام الرست المستقيم",
            maqamEn = "Maqam Rast",
            audioUrl = "https://everyayah.com/data/Husary_128kbps/001001.mp3",
            descriptionAr = "الميزان التجويدي الأدق، نموذج يحتذى به في ضبط الحروف وتطبيق أحكام الوقف والوصل.",
            descriptionEn = "The definitive benchmark for exact Tajweed phonetics and measured pacing.",
            durationSeconds = 45
        ),
        FeaturedRecitation(
            id = "feat_4",
            titleAr = "سكينة الفجر - سورة الرحمن",
            titleEn = "Dawn Tranquility - Surah Ar-Rahman",
            reciterId = "alafasy",
            reciterNameAr = "الشيخ مشاري راشد العفاسي",
            reciterNameEn = "Sheikh Mishary Rashid Alafasy",
            surahNameAr = "سورة الرحمن",
            surahNameEn = "Surah Ar-Rahman",
            ayahRange = "١ - ٢٥",
            maqamAr = "مقام الحجاز العذب",
            maqamEn = "Maqam Hijaz",
            audioUrl = "https://everyayah.com/data/Alafasy_128kbps/001001.mp3",
            descriptionAr = "تلاوة عذبة تبعث الطمأنينة في القلب وتذكر بنعم الله وآلائه الكثيرة.",
            descriptionEn = "Melodious, serene dawn recitation reminding of the boundless blessings of Allah.",
            durationSeconds = 90
        ),
        FeaturedRecitation(
            id = "feat_5",
            titleAr = "صلوات الحرم المكي - سورة الكهف",
            titleEn = "Haram Makkah - Surah Al-Kahf",
            reciterId = "muaiqly",
            reciterNameAr = "الشيخ ماهر المعيقلي",
            reciterNameEn = "Sheikh Maher Al-Muaiqly",
            surahNameAr = "سورة الكهف",
            surahNameEn = "Surah Al-Kahf",
            ayahRange = "١ - ١٠",
            maqamAr = "مقام الكرد الهادئ",
            maqamEn = "Maqam Kurd",
            audioUrl = "https://everyayah.com/data/MaherAlMuaiqly128kbps/001001.mp3",
            descriptionAr = "نسمات روحانية من بيت الله الحرام بصوت دافئ يروي ظمأ الأرواح المشتاقة.",
            descriptionEn = "Spiritual whispers from the Holy Kaaba with a soothing and tender tone.",
            durationSeconds = 85
        )
    )

    // --- Interactive Islamic Quiz Questions ---
    val quizQuestions: List<QuizQuestion> = listOf(
        QuizQuestion(
            id = 1,
            category = "TAJWEED",
            questionAr = "ما هو حكم النون الساكنة إذا جاء بعدها حرف 'الباء'؟",
            questionEn = "What is the Tajweed rule when a noon sakinah is followed by the letter 'Baa'?",
            optionsAr = listOf("الإظهار الحلقي", "الإقلاب (القلب)", "الإدغام بغنة", "الإخفاء الحقيقي"),
            optionsEn = listOf("Izhar", "Iqlab", "Idgham with Ghunnah", "Ikhfa"),
            correctIndex = 1,
            explanationAr = "الإقلاب هو قلب النون الساكنة أو التنوين ميماً مخفاة بغنة عند ملاقاة حرف الباء (وحرفه الوحيد هو الباء).",
            explanationEn = "Iqlab is turning noon sakinah or tanween into a hidden Meem with ghunnah when followed by Baa."
        ),
        QuizQuestion(
            id = 2,
            category = "RECITERS",
            questionAr = "من هو القارئ الملقب بـ 'صوت مكة' و'كروان الجنة'؟",
            questionEn = "Which legendary reciter was nicknamed 'The Voice of Makkah'?",
            optionsAr = listOf("الشيخ محمود خليل الحصري", "الشيخ عبد الباسط عبد الصمد", "الشيخ محمد صديق المنشاوي", "الشيخ مصطفى إسماعيل"),
            optionsEn = listOf("Sheikh Al-Husary", "Sheikh Abdul Basit Abdus Samad", "Sheikh El-Minshawi", "Sheikh Mustafa Ismail"),
            correctIndex = 1,
            explanationAr = "الشيخ عبد الباسط عبد الصمد رحمه الله هو من لُقب بصوت مكة لكثرة تلاواته المهيبة وشهرته العريضة في الحرمين والعالم.",
            explanationEn = "Sheikh Abdul Basit was known as the Voice of Makkah due to his globally celebrated recitations."
        ),
        QuizQuestion(
            id = 3,
            category = "TAJWEED",
            questionAr = "كم عدد حروف الإظهار الحلقي في أحكام النون الساكنة والتنوين؟",
            questionEn = "How many throat letters cause Izhar Halqi?",
            optionsAr = listOf("٤ حروف", "٥ حروف", "٦ حروف", "٨ حروف"),
            optionsEn = listOf("4 letters", "5 letters", "6 letters", "8 letters"),
            correctIndex = 2,
            explanationAr = "حروف الإظهار الحلقي ستة مجموعة في أوائل كلمات: (أخي هاك علماً حازه غير خاسر) وهي: الهمزة، الهاء، العين، الحاء، الغين، الخاء.",
            explanationEn = "There are 6 throat letters: Hamzah, Haa, 'Ayn, Haa', Ghayn, Khaa'."
        ),
        QuizQuestion(
            id = 4,
            category = "RECITERS",
            questionAr = "من هو أول قارئ سجل المصحف المرتل كاملاً في تاريخ الإذاعة الإسلامية؟",
            questionEn = "Who was the first reciter in history to record the complete recited Quran (Murattal)?",
            optionsAr = listOf("الشيخ محمود خليل الحصري", "الشيخ علي جابر", "الشيخ محمد رفعت", "الشيخ عبد الرحمن السديس"),
            optionsEn = listOf("Sheikh Mahmoud Khalil Al-Husary", "Sheikh Ali Jaber", "Sheikh Mohamed Rifat", "Sheikh Al-Sudais"),
            correctIndex = 0,
            explanationAr = "الشيخ محمود خليل الحصري عام 1961م هو أول من سجل المصحف المرتل برواية حفص عن عاصم ليكون مرجعاً عالمياً.",
            explanationEn = "Sheikh Al-Husary was the first to record the complete recited Quran in 1961."
        ),
        QuizQuestion(
            id = 5,
            category = "QURAN_SCIENCES",
            questionAr = "ما هي السورة التي لا تبدأ بـ 'بسم الله الرحمن الرحيم'؟",
            questionEn = "Which Surah does not begin with the Basmalah?",
            optionsAr = listOf("سورة الأنفال", "سورة التوبة (براءة)", "سورة النمل", "سورة الإخلاص"),
            optionsEn = listOf("Surah Al-Anfal", "Surah At-Tawbah", "Surah An-Naml", "Surah Al-Ikhlas"),
            correctIndex = 1,
            explanationAr = "سورة التوبة هي السورة الوحيدة التي جُرِّدت من البسملة في أولها لأنها نزلت بالسيف والبراءة من المشركين.",
            explanationEn = "Surah At-Tawbah is the only surah without Basmalah at its opening."
        ),
        QuizQuestion(
            id = 6,
            category = "TAJWEED",
            questionAr = "ما هي حروف القلقلة المجموعة في كلمة واحدة؟",
            questionEn = "What is the mnemonic word gathering all letters of Qalqalah?",
            optionsAr = listOf("يرملون", "قطب جد", "أخي هاك", "فحثه شخص سكت"),
            optionsEn = listOf("Yarmaloon", "Qutb Jadd", "Akhi Haak", "Fahathahu Shakhs Sakat"),
            correctIndex = 1,
            explanationAr = "حروف القلقلة خمسة مجموعة في كلمة 'قُطْبُ جَدٍّ' (القاف، الطاء، الباء، الجيم، الدال) حين تكون ساكنة.",
            explanationEn = "The Qalqalah letters are (Qaf, Taa, Baa, Jeem, Dal) combined in 'Qutb Jadd'."
        ),
        QuizQuestion(
            id = 7,
            category = "RECITERS",
            questionAr = "ما هو المقام الموسيقي الأساسي الذي اشتُهر به الشيخ محمد صديق المنشاوي في أدائه الخاشع؟",
            questionEn = "Which melodic maqam is famously associated with Sheikh El-Minshawi's weeping recitations?",
            optionsAr = listOf("مقام النهاوند", "مقام السيكاه", "مقام الكرد", "مقام الرست"),
            optionsEn = listOf("Maqam Nahawand", "Maqam Sikah", "Maqam Kurd", "Maqam Rast"),
            correctIndex = 0,
            explanationAr = "يُعد الشيخ المنشاوي إمام مقام النهاوند بنبرته الباكية التي تلامس القلوب وتظهر عظمة الآيات.",
            explanationEn = "Sheikh El-Minshawi is renowned for mastering Maqam Nahawand with heart-stirring reverence."
        )
    )

    // --- Interactive Islamic Landmarks & Quranic Historic Centers ---
    val islamicLandmarks: List<IslamicLandmark> = listOf(
        IslamicLandmark(
            id = "makkah_haram",
            nameAr = "المسجد الحرام والكعبة المشرفة",
            nameEn = "The Grand Mosque (Al-Masjid Al-Haram)",
            cityAr = "مكة المكرمة",
            cityEn = "Makkah",
            countryAr = "المملكة العربية السعودية",
            countryEn = "Saudi Arabia",
            latitude = 21.4225,
            longitude = 39.8262,
            descriptionAr = "أعظم مسجد في الإسلام وقبلة المسلمين قاطبة، فيه صلاة بمائة ألف صلاة، ومقر أشهر أئمة التلاوة عبر التاريخ.",
            descriptionEn = "The holiest sanctuary in Islam, direction of Muslim prayers worldwide, home to leading historic Quran reciters.",
            famousRecitersAr = "الشيخ عبد الرحمن السديس، الشيخ سعود الشريم، الشيخ ماهر المعيقلي، الشيخ ياسر الدوسري، الشيخ علي جابر",
            famousRecitersEn = "Sheikh Al-Sudais, Sheikh Al-Shuraim, Sheikh Al-Muaiqly, Sheikh Al-Dosari, Sheikh Ali Jaber"
        ),
        IslamicLandmark(
            id = "madinah_nabawi",
            nameAr = "المسجد النبوي الشريف",
            nameEn = "The Prophet's Mosque (Al-Masjid An-Nabawi)",
            cityAr = "المدينة المنورة",
            cityEn = "Madinah",
            countryAr = "المملكة العربية السعودية",
            countryEn = "Saudi Arabia",
            latitude = 24.4672,
            longitude = 39.6111,
            descriptionAr = "مسجد رسول الله صلى الله عليه وسلم، روضة من رياض الجنة، ومركز انطلاق الدعوة الإسلامية وعلوم القراءات.",
            descriptionEn = "The second holiest site in Islam, established by the Prophet Muhammad (PBUH), beacon of Quranic knowledge.",
            famousRecitersAr = "الشيخ علي الحذيفي، الشيخ محمد أيوب، الشيخ عبد المحسن القاسم، الشيخ عبد الباري الثبيتي",
            famousRecitersEn = "Sheikh Ali Al-Hudhaify, Sheikh Muhammad Ayyub, Sheikh Abdul Muhsin Al-Qasim"
        ),
        IslamicLandmark(
            id = "jerusalem_aqsa",
            nameAr = "المسجد الأقصى وقبة الصخرة",
            nameEn = "Al-Aqsa Mosque & Dome of the Rock",
            cityAr = "القدس الشريف",
            cityEn = "Jerusalem",
            countryAr = "فلسطين",
            countryEn = "Palestine",
            latitude = 31.7761,
            longitude = 35.2358,
            descriptionAr = "أولى القبلتين وثالث الحرمين الشريفين، ومسرى رسول الله صلى الله عليه وسلم في ليلة الإسراء والمعراج.",
            descriptionEn = "The first Qibla and third holiest mosque in Islam, associated with the miraculous Night Journey (Isra & Mi'raj).",
            famousRecitersAr = "الشيخ محمد رشاد الشريف، الشيخ يوسف أبو سنينة، الشيخ عكرمة صبري",
            famousRecitersEn = "Sheikh Mohammad Rashad Al-Sharif, Sheikh Yousef Abu Sneina"
        ),
        IslamicLandmark(
            id = "cairo_azhar",
            nameAr = "الجامع الأزهر الشريف",
            nameEn = "Al-Azhar Mosque",
            cityAr = "القاهرة",
            cityEn = "Cairo",
            countryAr = "مصر",
            countryEn = "Egypt",
            latitude = 30.0458,
            longitude = 31.2625,
            descriptionAr = "منارة العلوم الإسلامية وقلعة القراءات القرآنية في العالم الإسلامي لأكثر من ألف عام، تخرج منه عمالقة التلاوة.",
            descriptionEn = "Historical heart of Islamic scholarship and Quranic recitation studies for over a millennium.",
            famousRecitersAr = "الشيخ محمد رفعت، الشيخ الحصري، الشيخ عبد الباسط، الشيخ المنشاوي، الشيخ مصطفى إسماعيل",
            famousRecitersEn = "Sheikh Mohamed Rifat, Sheikh Al-Husary, Sheikh Abdul Basit, Sheikh El-Minshawi"
        ),
        IslamicLandmark(
            id = "damascus_umayyad",
            nameAr = "جامع بني أمية الكبير (الجامع الأموي)",
            nameEn = "Umayyad Mosque",
            cityAr = "دمشق",
            cityEn = "Damascus",
            countryAr = "سوريا",
            countryEn = "Syria",
            latitude = 33.5117,
            longitude = 36.3067,
            descriptionAr = "أحد أقدم وأعظم المعالم الإسلامية المعمارية، موطن مدرسة دمشق للتلاوة والمقامات والموشحات الدينية.",
            descriptionEn = "One of the grandest mosques in history, cradle of the Levantine school of Quranic recitation.",
            famousRecitersAr = "الشيخ محمد فؤاد عبد المجيد، الشيخ شكري البرني، الشيخ محيي الدين الكردي",
            famousRecitersEn = "Sheikh Shukri Al-Barni, Sheikh Muhyiddin Al-Kurdi"
        )
    )
}
