package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.QuranSurahsRepository
import com.example.data.model.QuranSurah
import com.example.data.model.TrackedWord
import com.example.data.model.UserPreferences
import com.example.data.model.WordRecitationState
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.ScoreCircularProgress
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    viewModel: MainViewModel,
    preferences: UserPreferences,
    modifier: Modifier = Modifier
) {
    val recorderState by viewModel.audioRecorder.state.collectAsState()
    val isEvaluating by viewModel.isEvaluating.collectAsState()
    val practiceSessions by viewModel.practiceSessions.collectAsState()
    val selectedSurah by viewModel.selectedPracticeSurah.collectAsState()
    val trackerResult by viewModel.smartTrackerResult.collectAsState()
    val selectedWordForAlert by viewModel.selectedTrackedWordForAlert.collectAsState()
    val isTestingMistakeMode by viewModel.isTestingMistakeMode.collectAsState()
    val isTrackerRecording by viewModel.audioRecorder.state.collectAsState()

    val isKidsSenior = preferences.isKidsSeniorMode
    val isAr = preferences.currentLanguage == "ar"

    var showAllSurahsDialog by remember { mutableStateOf(false) }

    // Popular Quick Practice Surahs
    val quickSurahs = remember {
        listOf(1, 112, 113, 114, 108, 110, 103, 97, 67, 36).mapNotNull {
            QuranSurahsRepository.getSurahByNumber(it)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("practice_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Studio Header with 114 Surahs Access
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("surah_selector_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = EmeraldPrimary.copy(alpha = 0.12f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = EmeraldPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isAr) "المتتبع الذكي اللحظي" else "Smart Recitation Tracker",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldPrimary
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isAr) "مختبر التلاوة والمتتبع الذكي" else "Tajweed Lab & Smart Tracker",
                                fontSize = if (isKidsSenior) 20.sp else 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Button to open all 114 Surahs
                        OutlinedButton(
                            onClick = { showAllSurahsDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("open_all_surahs_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.LibraryBooks,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = EmeraldPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "كافة السور (١١٤)" else "All 114 Surahs",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isAr)
                            "سجّل تلاوتك بصوتك، وسيقوم المتتبع الذكي بفحص كل كلمة لحظياً؛ وإذا أخطأت في القراءة يظهر تنبيه ذكي باللون الأحمر على الكلمة الخطأ مع بيان الصواب."
                        else
                            "Recite with your voice. The smart tracker evaluates each word: if you make a mistake, it displays a smart alert in RED on the exact word.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Surahs Carousel
                    Text(
                        text = if (isAr) "السور الأكثر تكراراً للتدريب:" else "Quick Select Surahs:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(quickSurahs, key = { it.number }) { surah ->
                            val isSelected = selectedSurah.number == surah.number
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .clickable { viewModel.setPracticeSurah(surah) }
                                    .testTag("quick_surah_${surah.number}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${surah.number}. سورة ${surah.nameAr}",
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quranic Mushaf Display Board with Word-by-Word Tokens
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quran_mushaf_board"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = GoldAccent.copy(alpha = 0.4f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header of Surah in Mushaf Style
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldAccent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${selectedSurah.revelationType} • ${selectedSurah.ayahCount} آيات",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = "سورة ${selectedSurah.nameAr}",
                            fontSize = if (isKidsSenior) 22.sp else 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldPrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "الجزء ${selectedSurah.juzNumber}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Words Display with Color Highlighting (Green = Correct, RED = MISTAKE, Neutral = Pending)
                    val wordsToDisplay: List<TrackedWord> = trackerResult?.trackedWords ?: remember(selectedSurah) {
                        QuranSurahsRepository.extractWordsForTracker(selectedSurah).mapIndexed { i, w ->
                            TrackedWord(
                                index = i,
                                wordWithTashkeel = w,
                                wordClean = QuranSurahsRepository.stripTashkeel(w),
                                ayahNumber = 1,
                                state = WordRecitationState.PENDING
                            )
                        }
                    }

                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        wordsToDisplay.forEach { word ->
                            TrackedWordChip(
                                word = word,
                                onClick = {
                                    if (word.state == WordRecitationState.MISTAKE) {
                                        viewModel.selectTrackedWordForAlert(word)
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Legend: Red = Mistake, Green = Correct
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD32F2F))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAr) "تنبيه باللون الأحمر: خطأ تم رصده (انقر للتصحيح)" else "Red: Mistake alert",
                                fontSize = 10.sp,
                                color = Color(0xFFD32F2F),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAr) "أخضر: قراءة صحيحة" else "Green: Correct",
                                fontSize = 10.sp,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Live Voice Recording Studio & Deliberate Mistake Test Switch
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voice_recorder_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (recorderState.isRecording)
                            "🎙️ المتتبع الذكي يستمع لتلاوتك... (⏱️ ${recorderState.elapsedSeconds} ث)"
                        else
                            (if (isAr) "ابدأ التسجيل واقرأ الآيات الكريمة" else "Start recording & recite the verses"),
                        fontSize = 14.sp,
                        color = if (recorderState.isRecording) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Audio Waveform
                    AudioWaveformVisualizer(
                        isActive = recorderState.isRecording,
                        amplitudes = recorderState.amplitudesHistory,
                        barColor = if (recorderState.isRecording) Color(0xFFD32F2F) else EmeraldPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Test deliberate mistake switch for immediate verification
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isTestingMistakeMode) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isTestingMistakeMode) Icons.Default.Warning else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (isTestingMistakeMode) Color(0xFFD32F2F) else EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isAr) "تجربة رصد الأخطاء والتنبيه الأحمر" else "Test Deliberate Mistake Alert",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isTestingMistakeMode) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isAr) "محاكاة لحن جلي لرؤية التنبيه الأحمر بالكلمة الخطأ فوراً" else "Simulates mispronounced word to verify red alert",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = isTestingMistakeMode,
                                onCheckedChange = { viewModel.toggleTestingMistakeMode() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFFD32F2F),
                                    checkedTrackColor = Color(0xFFFFCDD2)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (recorderState.isRecording) {
                        Button(
                            onClick = { viewModel.stopAndAnalyzeSmartTracker() },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(52.dp)
                                .testTag("stop_and_evaluate_button"),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "إيقاف وفحص الأخطاء بالمتتبع الذكي" else "Stop & Smart Tracker Check",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Button(
                            onClick = { viewModel.startSmartTrackerRecording() },
                            enabled = !isEvaluating,
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(52.dp)
                                .testTag("start_practice_recording_button"),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "ابدأ التلاوة والتتبع الذكي" else "Start Recitation Tracker",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (isEvaluating) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = GoldAccent
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isAr) "المتتبع الذكي يطابق الكلمات ومخارج الحروف..." else "Smart Tracker comparing words & articulation...",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Smart Recitation Mistake Alert Card (Shows whenever trackerResult has mistakes or finishes)
        item {
            AnimatedVisibility(visible = trackerResult != null) {
                trackerResult?.let { res ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.5.dp,
                                color = if (res.hasMistakes) Color(0xFFD32F2F) else SuccessGreen,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .testTag("tracker_result_alert_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (res.hasMistakes) Color(0xFFFFF5F5) else Color(0xFFF1F8E9)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (res.hasMistakes) Icons.Default.Warning else Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = if (res.hasMistakes) Color(0xFFD32F2F) else SuccessGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (res.hasMistakes)
                                            (if (isAr) "⚠️ تنبيه ذكي: رصد أخطاء في التلاوة!" else "⚠️ Smart Alert: Recitation Mistakes!")
                                        else
                                            (if (isAr) "✨ تلاوة نموذجية خالية من الأخطاء!" else "✨ Flawless Recitation!"),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (res.hasMistakes) Color(0xFFB71C1C) else EmeraldDark
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.resetSmartTracker() },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Reset",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ScoreCircularProgress(
                                    score = res.accuracyPercentage,
                                    label = if (isAr) "دقة التلاوة" else "Accuracy",
                                    size = 76.dp,
                                    strokeWidth = 6.dp
                                )

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (res.hasMistakes)
                                            (if (isAr) "تم رصد ${res.mistakeWordsCount} كلمات بها خطأ تلاوة (محددة بالأحمر)" else "${res.mistakeWordsCount} words with mistakes (in Red)")
                                        else
                                            (if (isAr) "جميع الكلمات (${res.totalWords}) قُرئت بنطق سليم 100%" else "All ${res.totalWords} words pronounced correctly"),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (res.hasMistakes) Color(0xFFC62828) else SuccessGreen
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = res.generalFeedbackAr,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            // If mistakes exist, list them prominently
                            if (res.hasMistakes) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (isAr) "قائمة الكلمات التي ورد فيها الخطأ:" else "Mistaken Words Breakdown:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB71C1C)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                res.trackedWords.filter { it.state == WordRecitationState.MISTAKE }.forEach { mistWord ->
                                    Surface(
                                        onClick = { viewModel.selectTrackedWordForAlert(mistWord) },
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color.White,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFFFFEBEE)
                                            ) {
                                                Text(
                                                    text = mistWord.wordWithTashkeel,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFD32F2F),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = mistWord.mistakeCategory ?: "لحن جلي",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFB71C1C)
                                                )
                                                Text(
                                                    text = mistWord.mistakeExplanationAr ?: "",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1
                                                )
                                            }
                                            Icon(
                                                imageVector = Icons.Default.Hearing,
                                                contentDescription = null,
                                                tint = Color(0xFFD32F2F),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Practice History List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "سجل التلاوات السابقة" else "Practice History",
                    fontSize = if (isKidsSenior) 18.sp else 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${practiceSessions.size} جلسة",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (practiceSessions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isAr) "لم تقم بتسجيل تلاوات بعد. ابدأ الآن تلاوة سورة ${selectedSurah.nameAr}!" else "No recitation sessions yet. Start reciting!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(practiceSessions, key = { it.id }) { session ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ScoreCircularProgress(
                            score = session.overallScore,
                            label = if (isAr) "الدرجة" else "Score",
                            size = 48.dp,
                            strokeWidth = 4.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = session.surahName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            val dateStr = SimpleDateFormat("dd MMMM - HH:mm", Locale.getDefault()).format(Date(session.timestamp))
                            Text(
                                text = "$dateStr • ${session.durationSeconds} ثانية",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = session.feedbackAr,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Alert Dialog for Mispronounced / Mistaken Word in RED
    selectedWordForAlert?.let { mistakeWord ->
        AlertDialog(
            onDismissRequest = { viewModel.selectTrackedWordForAlert(null) },
            confirmButton = {
                Button(
                    onClick = { viewModel.selectTrackedWordForAlert(null) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text(if (isAr) "حسناً، فهمت التصحيح" else "Got it")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        // Play Sheikh sample recitation for guidance
                        viewModel.playRecitation(
                            "correction_${mistakeWord.index}",
                            mistakeWord.wordWithTashkeel,
                            "https://everyayah.com/data/Husary_128kbps/001001.mp3"
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = EmeraldPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "استمع للنطق الصحيح" else "Hear Sheikh",
                        color = EmeraldPrimary,
                        fontSize = 12.sp
                    )
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) "🚨 تنبيه ذكي: خطأ في تلاوة الكلمة" else "🚨 Smart Alert: Word Mistake",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB71C1C),
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Correct vs What User Said
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFEBEE),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF9A9A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isAr) "الكلمة الصحيحة في المصحف:" else "Correct Quranic word:",
                                    fontSize = 11.sp,
                                    color = Color(0xFFB71C1C)
                                )
                                Text(
                                    text = mistakeWord.wordWithTashkeel,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark
                                )
                            }
                            if (!mistakeWord.userPronounced.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isAr) "ما رصده المتتبع بصوتك:" else "Detected from your voice:",
                                        fontSize = 11.sp,
                                        color = Color(0xFFB71C1C)
                                    )
                                    Text(
                                        text = mistakeWord.userPronounced,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD32F2F)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isAr) "نوع الخطأ:" else "Mistake type:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = mistakeWord.mistakeCategory ?: "لحن جلي",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFD32F2F)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isAr) "التوجيه الصوتي والتصويب:" else "Correction Advice:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = mistakeWord.mistakeExplanationAr ?: "احرص على قراءة الكلمة بتؤدة مع ضبط حركات الحروف بدقة.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )

                    if (!mistakeWord.correctionAdviceAr.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldPrimary.copy(alpha = 0.08f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💡 نصيحة الشيخ: ${mistakeWord.correctionAdviceAr}",
                                fontSize = 11.sp,
                                color = EmeraldDark,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        )
    }

    // Modal Sheet to Select Any of the 114 Surahs
    if (showAllSurahsDialog) {
        AllSurahsSelectionSheet(
            surahs = viewModel.allSurahs,
            selectedSurahNumber = selectedSurah.number,
            onSurahSelected = { chosen ->
                viewModel.setPracticeSurah(chosen)
                showAllSurahsDialog = false
            },
            onDismiss = { showAllSurahsDialog = false },
            isAr = isAr
        )
    }
}

/**
 * Word token chip with 3-state visualization:
 * - PENDING: Neutral surface
 * - CORRECT: Soft Green with checkmark
 * - MISTAKE: VIBRANT RED with exclamation badge and alert border
 */
@Composable
private fun TrackedWordChip(
    word: TrackedWord,
    onClick: () -> Unit
) {
    val isMistake = word.state == WordRecitationState.MISTAKE
    val isCorrect = word.state == WordRecitationState.CORRECT

    val containerColor = when {
        isMistake -> Color(0xFFFFEBEE)
        isCorrect -> Color(0xFFE8F5E9)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    }

    val borderColor = when {
        isMistake -> Color(0xFFD32F2F)
        isCorrect -> Color(0xFF4CAF50)
        else -> Color.Transparent
    }

    val textColor = when {
        isMistake -> Color(0xFFB71C1C)
        isCorrect -> Color(0xFF1B5E20)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(if (isMistake || isCorrect) 1.5.dp else 0.dp, borderColor),
        modifier = Modifier.padding(horizontal = 3.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = word.wordWithTashkeel,
                fontSize = 15.sp,
                fontWeight = if (isMistake) FontWeight.ExtraBold else FontWeight.Medium,
                color = textColor
            )

            if (isMistake) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Mistake Alert",
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(14.dp)
                )
            } else if (isCorrect) {
                Spacer(modifier = Modifier.width(3.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Correct",
                    tint = Color(0xFF388E3C),
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

/**
 * Bottom Sheet displaying all 114 Surahs with instant search and filtering.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllSurahsSelectionSheet(
    surahs: List<QuranSurah>,
    selectedSurahNumber: Int,
    onSurahSelected: (QuranSurah) -> Unit,
    onDismiss: () -> Unit,
    isAr: Boolean
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, MAKKAH, MADINAH

    val filteredSurahs = remember(searchQuery, selectedFilter) {
        surahs.filter { surah ->
            val matchesSearch = searchQuery.isBlank() ||
                surah.nameAr.contains(searchQuery, ignoreCase = true) ||
                surah.nameEn.contains(searchQuery, ignoreCase = true) ||
                surah.number.toString() == searchQuery.trim()

            val matchesFilter = when (selectedFilter) {
                "MAKKAH" -> surah.revelationType == "مكية"
                "MADINAH" -> surah.revelationType == "مدنية"
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .heightIn(max = 600.dp)
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "اختر سورة من القرآن الكريم (١١٤ سورة)" else "Select a Surah (All 114)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDark
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = if (isAr) "ابحث باسم السورة أو رقمها (مثال: الكهف أو 18)..." else "Search surah name or number...",
                        fontSize = 12.sp
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = EmeraldPrimary)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Pair("ALL", if (isAr) "كافة السور (١١٤)" else "All (114)"),
                    Pair("MAKKAH", if (isAr) "المكية" else "Meccan"),
                    Pair("MADINAH", if (isAr) "المدنية" else "Medinan")
                ).forEach { (key, label) ->
                    val isSel = selectedFilter == key
                    Surface(
                        onClick = { selectedFilter = key },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSel) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Surahs List
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredSurahs, key = { it.number }) { surah ->
                    val isSelected = surah.number == selectedSurahNumber
                    Surface(
                        onClick = { onSurahSelected(surah) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldPrimary) else null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) EmeraldPrimary else EmeraldDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = surah.number.toString(),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "سورة ${surah.nameAr}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${surah.nameEn} • ${surah.revelationType} • ${surah.ayahCount} آية",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GoldAccent.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "الجزء ${surah.juzNumber}",
                                    fontSize = 10.sp,
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
