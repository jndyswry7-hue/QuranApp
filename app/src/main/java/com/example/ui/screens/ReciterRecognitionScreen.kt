package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FavoriteItem
import com.example.data.model.Reciter
import com.example.data.model.ReciterMatchResult
import com.example.data.model.SelectedMediaSource
import com.example.data.model.UserPreferences
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.ScoreCircularProgress
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.viewmodel.MainViewModel

@Composable
fun ReciterRecognitionScreen(
    viewModel: MainViewModel,
    preferences: UserPreferences,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isRecognizing by viewModel.isRecognizing.collectAsState()
    val recognitionResult by viewModel.recognitionResult.collectAsState()
    val selectedMedia by viewModel.selectedMedia.collectAsState()
    val recorderState by viewModel.audioRecorder.state.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val isKidsSenior = preferences.isKidsSeniorMode
    val isAr = preferences.currentLanguage == "ar"

    // Recognition Mode: 0 = Microphone, 1 = Audio File, 2 = Video Clip
    var selectedInputMode by remember { mutableIntStateOf(0) }

    // Launcher for selecting Audio files
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.selectMediaFile(it, isVideo = false, context)
            selectedInputMode = 1
        }
    }

    // Launcher for selecting Video files (Compliant with Android Photo Picker policy)
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.selectMediaFile(it, isVideo = true, context)
            selectedInputMode = 2
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    val filteredReciters = remember(searchQuery) {
        if (searchQuery.isBlank()) viewModel.reciters
        else viewModel.reciters.filter {
            it.nameAr.contains(searchQuery, ignoreCase = true) ||
            it.nameEn.contains(searchQuery, ignoreCase = true) ||
            it.primaryMaqam.contains(searchQuery, ignoreCase = true) ||
            it.famousSurah.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("reciter_recognition_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Recognition Action Header & Input Modes Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recognition_action_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Title with Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GoldAccent.copy(alpha = 0.15f),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isAr) "دقة فائقة بالتمييز" else "Ultra-Precision AI",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isAr) "التعرف الذكي على صوت القارئ" else "AI Reciter Identification",
                        fontSize = if (isKidsSenior) 21.sp else 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isAr)
                            "اختر طريقة التغذية المناسبة (ميكروفون مباشر، ملف صوتي، أو مقطع فيديو)، ليقوم الذكاء الاصطناعي بمطابقة البصمة الصوتية والمقام ومخارج الحروف مع كبار القراء بدقة فائقة."
                        else
                            "Choose your input method (Live mic, Audio file, or Video clip). AI matches vocal formants, Maqam, and articulation with master reciters.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input Source Mode Tabs (Mic, Audio, Video)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Tab 0: Microphone
                            InputModeButton(
                                selected = selectedInputMode == 0,
                                icon = Icons.Default.Mic,
                                label = if (isAr) "ميكروفون" else "Live Mic",
                                onClick = { selectedInputMode = 0 },
                                modifier = Modifier.weight(1f)
                            )
                            // Tab 1: Audio File
                            InputModeButton(
                                selected = selectedInputMode == 1,
                                icon = Icons.Default.Audiotrack,
                                label = if (isAr) "ملف صوت" else "Audio File",
                                onClick = {
                                    selectedInputMode = 1
                                    if (selectedMedia == null || selectedMedia?.isVideo == true) {
                                        audioPickerLauncher.launch("audio/*")
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            // Tab 2: Video File
                            InputModeButton(
                                selected = selectedInputMode == 2,
                                icon = Icons.Default.Videocam,
                                label = if (isAr) "مقطع فيديو" else "Video Clip",
                                onClick = {
                                    selectedInputMode = 2
                                    if (selectedMedia == null || selectedMedia?.isVideo == false) {
                                        videoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                        )
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Dynamic Content based on selected mode
                    when (selectedInputMode) {
                        0 -> {
                            // MICROPHONE MODE
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AudioWaveformVisualizer(
                                    isActive = isRecognizing,
                                    amplitudes = recorderState.amplitudesHistory,
                                    barColor = EmeraldPrimary
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                Button(
                                    onClick = {
                                        if (!isRecognizing) {
                                            viewModel.startReciterRecognition()
                                        }
                                    },
                                    enabled = !isRecognizing,
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .height(52.dp)
                                        .testTag("listen_reciter_button"),
                                    shape = RoundedCornerShape(26.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                                ) {
                                    if (isRecognizing) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.5.dp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = if (isAr) "جاري الاستماع والتمييز الفائق..." else "Analyzing Reciter...",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Hearing,
                                            contentDescription = null,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isAr) "استمع وتعرّف الآن" else "Listen & Identify",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        1 -> {
                            // AUDIO FILE MODE
                            MediaSelectorBlock(
                                isVideo = false,
                                selectedMedia = selectedMedia?.takeIf { !it.isVideo },
                                isRecognizing = isRecognizing,
                                isAr = isAr,
                                onPick = { audioPickerLauncher.launch("audio/*") },
                                onClear = { viewModel.clearSelectedMedia() },
                                onAnalyze = { viewModel.startRecognitionWithSelectedMedia() }
                            )
                        }

                        2 -> {
                            // VIDEO FILE MODE
                            MediaSelectorBlock(
                                isVideo = true,
                                selectedMedia = selectedMedia?.takeIf { it.isVideo },
                                isRecognizing = isRecognizing,
                                isAr = isAr,
                                onPick = {
                                    videoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                    )
                                },
                                onClear = { viewModel.clearSelectedMedia() },
                                onAnalyze = { viewModel.startRecognitionWithSelectedMedia() }
                            )
                        }
                    }
                }
            }
        }

        // Recognition Result Card with Ultra-Precision
        item {
            AnimatedVisibility(
                visible = recognitionResult != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                recognitionResult?.let { result ->
                    val reciter = result.matchedReciter
                    val isFav = favorites.any { it.id == reciter.id }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.5.dp,
                                brush = Brush.horizontalGradient(listOf(GoldAccent, EmeraldPrimary)),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .testTag("recognition_result_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            // Top Row: Ultra-precision tag + Input source + Reset
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = EmeraldPrimary.copy(alpha = 0.15f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = EmeraldPrimary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isAr) "تطابق صوتي فائق الدقة" else "Ultra-Precision Match",
                                                color = EmeraldPrimary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Source indicator
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = GoldAccent.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = when (result.inputSourceType) {
                                                "VIDEO_FILE" -> if (isAr) "🎬 مقطع فيديو" else "🎬 Video Clip"
                                                "AUDIO_FILE" -> if (isAr) "🎵 ملف صوتي" else "🎵 Audio File"
                                                else -> if (isAr) "🎙️ تسجيل حي" else "🎙️ Live Mic"
                                            },
                                            color = GoldAccent,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.clearRecognition() },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Reset Recognition",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Main Sheikh Info & Circular Match Meter
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ScoreCircularProgress(
                                    score = result.confidencePercentage,
                                    label = if (isAr) "تطابق فائق" else "Precision",
                                    size = 80.dp,
                                    strokeWidth = 7.dp
                                )

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isAr) reciter.nameAr else reciter.nameEn,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = reciter.titleAr,
                                        fontSize = 12.sp,
                                        color = GoldAccent,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${reciter.country} • ${reciter.birthYear}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        viewModel.toggleFavorite(
                                            FavoriteItem(
                                                id = reciter.id,
                                                itemType = "RECITER",
                                                titleAr = reciter.nameAr,
                                                titleEn = reciter.nameEn,
                                                subtitleAr = reciter.titleAr,
                                                subtitleEn = reciter.titleEn,
                                                audioUrl = reciter.sampleAudioUrl
                                            ),
                                            isCurrentlyFav = isFav
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (isFav) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Melodic Maqam & Recitation School Pills
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = EmeraldPrimary.copy(alpha = 0.12f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                        Text(
                                            text = if (isAr) "المقام الصوتي:" else "Maqam:",
                                            fontSize = 10.sp,
                                            color = EmeraldDark,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = result.detectedMaqamAr,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldPrimary
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GoldAccent.copy(alpha = 0.12f),
                                    modifier = Modifier.weight(1.2f)
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                        Text(
                                            text = if (isAr) "المدرسة التلاوية:" else "Recitation School:",
                                            fontSize = 10.sp,
                                            color = GoldAccent,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = result.recitationSchoolAr,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            if (!result.detectedSurahOrAyah.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (isAr) "📖 السورة والأسلوب: ${result.detectedSurahOrAyah}" else "📖 Matched Surah: ${result.detectedSurahOrAyah}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            // Biometric Acoustic Affinities Chips
                            if (result.acousticAffinities.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (isAr) "البصمات الصوتية الحيوية المطابقة:" else "Biometric Acoustic Signatures:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    result.acousticAffinities.forEach { affinity ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(GoldAccent)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = affinity,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Vocal Analysis Commentary
                            Text(
                                text = result.vocalAnalysisAr,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Action Buttons: Play Sample Recitation & Share Result
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.playRecitation(
                                            reciter.id,
                                            reciter.nameAr,
                                            reciter.sampleAudioUrl
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isAr) "استمع لتلاوة القارئ" else "Play Sample",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        viewModel.shareRecognitionResult(context, result)
                                    },
                                    modifier = Modifier
                                        .weight(0.9f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = EmeraldPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isAr) "مشاركة النتيجة" else "Share Result",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quran Reciters Encyclopedia & Search
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "موسوعة أعلام القراء" else "Encyclopedia of Reciters",
                    fontSize = if (isKidsSenior) 18.sp else 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${filteredReciters.size} قارئ",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reciter_search_field"),
                placeholder = {
                    Text(
                        text = if (isAr) "ابحث باسم القارئ أو المقام أو السورة..." else "Search reciter, maqam, or style...",
                        fontSize = 12.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Reciters List
        items(filteredReciters, key = { it.id }) { reciter ->
            val isFav = favorites.any { it.id == reciter.id }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reciter_card_${reciter.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(EmeraldDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = reciter.nameAr.take(2),
                                color = GoldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAr) reciter.nameAr else reciter.nameEn,
                                fontWeight = FontWeight.Bold,
                                fontSize = if (isKidsSenior) 15.sp else 14.sp
                            )
                            Text(
                                text = "${reciter.country} • ${reciter.birthYear}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                viewModel.toggleFavorite(
                                    FavoriteItem(
                                        id = reciter.id,
                                        itemType = "RECITER",
                                        titleAr = reciter.nameAr,
                                        titleEn = reciter.nameEn,
                                        subtitleAr = reciter.titleAr,
                                        subtitleEn = reciter.titleEn,
                                        audioUrl = reciter.sampleAudioUrl
                                    ),
                                    isCurrentlyFav = isFav
                                )
                            }
                        ) {
                            Icon(
                                imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFav) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                viewModel.playRecitation(
                                    reciter.id,
                                    reciter.nameAr,
                                    reciter.sampleAudioUrl
                                )
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GoldAccent.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = reciter.primaryMaqam,
                                color = GoldAccent,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldPrimary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = reciter.famousSurah,
                                color = EmeraldPrimary,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = reciter.bioAr,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun InputModeButton(
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        selected = selected,
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (selected) EmeraldPrimary else Color.Transparent,
        modifier = modifier.height(38.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MediaSelectorBlock(
    isVideo: Boolean,
    selectedMedia: SelectedMediaSource?,
    isRecognizing: Boolean,
    isAr: Boolean,
    onPick: () -> Unit,
    onClear: () -> Unit,
    onAnalyze: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (selectedMedia == null) {
            // Dropzone/Select Card
            Surface(
                onClick = onPick,
                shape = RoundedCornerShape(16.dp),
                color = EmeraldPrimary.copy(alpha = 0.05f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldLight.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (isVideo) Icons.Default.VideoFile else Icons.Default.UploadFile,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isVideo)
                            (if (isAr) "انقر لاختيار مقطع فيديو للتلاوة" else "Tap to choose a recitation video")
                        else
                            (if (isAr) "انقر لاختيار ملف صوتي للتلاوة" else "Tap to choose an audio file"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isVideo)
                            (if (isAr) "يدعم كافة صيغ الفيديو (MP4, MKV, WebM, MOV)" else "Supports MP4, MKV, WebM, MOV")
                        else
                            (if (isAr) "يدعم كافة صيغ الصوت (MP3, WAV, M4A, AAC)" else "Supports MP3, WAV, M4A, AAC"),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Selected File Details Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isVideo) GoldAccent.copy(alpha = 0.2f) else EmeraldPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isVideo) Icons.Default.Videocam else Icons.Default.Audiotrack,
                            contentDescription = null,
                            tint = if (isVideo) GoldAccent else EmeraldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedMedia.fileName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${selectedMedia.fileSizeFormatted} • ${if (isVideo) "مقطع مرئي" else "تسجيل صوتي"}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onClear) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove file",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Button to run Ultra-Precision Analysis
            Button(
                onClick = onAnalyze,
                enabled = !isRecognizing,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                if (isRecognizing) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isAr) "جاري التمييز الفائق بالذكاء الاصطناعي..." else "Analyzing with Ultra-Precision...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = GoldLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isVideo)
                            (if (isAr) "بدء تمييز القارئ من الفيديو" else "Identify Reciter from Video")
                        else
                            (if (isAr) "بدء تمييز القارئ من الصوت" else "Identify Reciter from Audio"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            TextButton(
                onClick = onPick,
                enabled = !isRecognizing
            ) {
                Text(
                    text = if (isAr) "تغيير الملف المختار" else "Choose different file",
                    fontSize = 12.sp,
                    color = EmeraldPrimary
                )
            }
        }
    }
}
