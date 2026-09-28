package com.example.ui.screens

import android.content.Context
import android.content.Intent
import java.util.Locale
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FavoriteItem
import com.example.data.model.FeaturedRecitation
import com.example.data.model.FullReciterAudioSource
import com.example.data.model.QuranSurah
import com.example.data.model.UserPreferences
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.viewmodel.MainViewModel

@Composable
fun FeaturedRecitationsScreen(
    viewModel: MainViewModel,
    preferences: UserPreferences,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val featuredList = viewModel.featuredRecitations
    val favorites by viewModel.favorites.collectAsState()
    val playerState by viewModel.audioPlayer.state.collectAsState()
    val selectedFullReciter by viewModel.selectedFullReciter.collectAsState()
    val playingFullSurah by viewModel.playingFullSurah.collectAsState()
    val allSurahs = viewModel.allSurahs
    val fullReciters = viewModel.fullAudioReciters

    val isKidsSenior = preferences.isKidsSeniorMode
    val isAr = preferences.currentLanguage == "ar"

    // 0: Full Quran (114 Surahs), 1: Featured Clips & Maqams, 2: Favorites
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var surahSearchQuery by remember { mutableStateOf("") }
    var surahFilterType by remember { mutableStateOf("ALL") } // ALL, MAKKAH, MADINAH

    val filteredSurahs = remember(surahSearchQuery, surahFilterType) {
        allSurahs.filter { surah ->
            val matchesSearch = surahSearchQuery.isBlank() ||
                surah.nameAr.contains(surahSearchQuery, ignoreCase = true) ||
                surah.nameEn.contains(surahSearchQuery, ignoreCase = true) ||
                surah.number.toString() == surahSearchQuery.trim()

            val matchesFilter = when (surahFilterType) {
                "MAKKAH" -> surah.revelationType == "مكية"
                "MADINAH" -> surah.revelationType == "مدنية"
                else -> true
            }
            matchesSearch && matchesFilter
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("featured_recitations_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tab Row: Full Recitations (114) vs Featured Clips vs Favorites
        item {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EmeraldPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .testTag("featured_tab_row")
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            text = if (isAr) "تلاوات كاملة (١١٤)" else "Full Quran (114)",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            text = if (isAr) "مقاطع مميزة ومقامات" else "Featured Clips",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = {
                        Text(
                            text = if (isAr) "المفضلة (${favorites.size})" else "Favorites (${favorites.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTabIndex == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Active Player Card (when media is playing)
        item {
            AnimatedVisibility(visible = playerState.currentMediaId != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("active_player_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(GoldAccent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GraphicEq,
                                        contentDescription = null,
                                        tint = EmeraldDark,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = playerState.currentTitle ?: (if (isAr) "تلاوة جارية" else "Playing Recitation"),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = if (isAr) "صوت عالي النقاء • بث سحابي مباشر" else "Crystal Audio • Cloud Streaming",
                                        color = GoldLight,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            IconButton(onClick = { viewModel.audioPlayer.stop() }) {
                                Icon(
                                    imageVector = Icons.Default.Stop,
                                    contentDescription = "Stop",
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress Slider
                        val totalDuration = playerState.totalDurationMs
                        val currentPos = playerState.currentPositionMs
                        val progressFraction = if (totalDuration > 0) {
                            (currentPos.toFloat() / totalDuration).coerceIn(0f, 1f)
                        } else 0f

                        Slider(
                            value = progressFraction,
                            onValueChange = { frac ->
                                val targetMs = (frac * totalDuration).toInt()
                                viewModel.audioPlayer.seekTo(targetMs)
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = GoldAccent,
                                activeTrackColor = GoldAccent,
                                inactiveTrackColor = EmeraldPrimary.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Duration time indicators & controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatDuration(currentPos),
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )

                            // Controls: Rewind, Play/Pause, Forward, Speed
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(onClick = { viewModel.audioPlayer.seekTo((currentPos - 10000).coerceAtLeast(0)) }) {
                                    Icon(Icons.Default.FastRewind, contentDescription = "-10s", tint = Color.White)
                                }

                                IconButton(
                                    onClick = {
                                        if (playerState.isPlaying) viewModel.audioPlayer.pause()
                                        else viewModel.audioPlayer.resume()
                                    },
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(GoldAccent)
                                ) {
                                    Icon(
                                        imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play/Pause",
                                        tint = EmeraldDark,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                IconButton(onClick = { viewModel.audioPlayer.seekTo((currentPos + 10000).coerceAtMost(totalDuration)) }) {
                                    Icon(Icons.Default.FastForward, contentDescription = "+10s", tint = Color.White)
                                }
                            }

                            Text(
                                text = formatDuration(totalDuration),
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // TAB 0: FULL QURAN (114 SURAHS) CATALOG
        if (selectedTabIndex == 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isAr) "اختر القارئ للمصحف المرتل:" else "Select Master Reciter:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GoldAccent.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (isAr) "تلاوات كاملة ١١٤ سورة" else "Full 114 Surahs",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Reciter selector horizontal chips
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(fullReciters, key = { it.reciterId }) { reciter ->
                                val isSelected = selectedFullReciter.reciterId == reciter.reciterId
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .clickable { viewModel.setFullReciter(reciter) }
                                        .testTag("full_reciter_${reciter.reciterId}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else EmeraldPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isAr) reciter.reciterNameAr else reciter.reciterNameEn,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Search Bar & Filter for Surahs
            item {
                OutlinedTextField(
                    value = surahSearchQuery,
                    onValueChange = { surahSearchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("full_surah_search"),
                    placeholder = {
                        Text(
                            text = if (isAr) "ابحث في ١١٤ سورة بالاسم أو الرقم (مثال: البقرة أو 2)..." else "Search 114 surahs by name or number...",
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

                // Mecca/Medina chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        Pair("ALL", if (isAr) "كافة السور (١١٤)" else "All (114)"),
                        Pair("MAKKAH", if (isAr) "مكية" else "Meccan"),
                        Pair("MADINAH", if (isAr) "مدنية" else "Medinan")
                    ).forEach { (key, label) ->
                        val isSel = surahFilterType == key
                        Surface(
                            onClick = { surahFilterType = key },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // 114 Surahs List with Full Audio Streaming
            items(filteredSurahs, key = { it.number }) { surah ->
                val isCurrentlyPlaying = playingFullSurah?.number == surah.number && playerState.isPlaying
                val isThisSurahLoaded = playingFullSurah?.number == surah.number

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("full_surah_item_${surah.number}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isThisSurahLoaded) EmeraldPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                    ),
                    border = if (isThisSurahLoaded) androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Surah number circular badge
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isThisSurahLoaded) EmeraldPrimary else EmeraldDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = surah.number.toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "سورة ${surah.nameAr}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (isThisSurahLoaded) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (surah.revelationType == "مكية") GoldAccent.copy(alpha = 0.15f) else EmeraldPrimary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = surah.revelationType,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (surah.revelationType == "مكية") GoldAccent else EmeraldPrimary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${surah.nameEn} • ${surah.ayahCount} آية • بصوت ${selectedFullReciter.reciterNameAr}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Play/Pause Full Surah Button
                        IconButton(
                            onClick = {
                                if (isCurrentlyPlaying) {
                                    viewModel.audioPlayer.pause()
                                } else if (isThisSurahLoaded) {
                                    viewModel.audioPlayer.resume()
                                } else {
                                    viewModel.playFullSurah(surah)
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isCurrentlyPlaying) GoldAccent else EmeraldPrimary)
                        ) {
                            Icon(
                                imageVector = if (isCurrentlyPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play Surah",
                                tint = if (isCurrentlyPlaying) EmeraldDark else Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }

        // TAB 1 & 2: FEATURED RECITATIONS & FAVORITES
        if (selectedTabIndex == 1 || selectedTabIndex == 2) {
            val displayList = if (selectedTabIndex == 1) featuredList else featuredList.filter { f -> favorites.any { it.id == f.id } }

            if (displayList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isAr) "لا توجد تلاوات في هذه القائمة حالياً." else "No recitations found in this list.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            items(displayList, key = { it.id }) { item ->
                val isFav = favorites.any { it.id == item.id }
                val isPlaying = playerState.currentMediaId == item.id && playerState.isPlaying

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("featured_item_${item.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (isPlaying) GoldAccent else EmeraldDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = if (isPlaying) EmeraldDark else Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isAr) item.titleAr else item.titleEn,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "${item.reciterNameAr} • ${item.surahNameAr}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Favorite button
                            IconButton(
                                onClick = {
                                    viewModel.toggleFavorite(
                                        FavoriteItem(
                                            id = item.id,
                                            itemType = "RECITATION",
                                            titleAr = item.titleAr,
                                            titleEn = item.titleEn,
                                            subtitleAr = item.reciterNameAr,
                                            subtitleEn = item.reciterNameEn,
                                            audioUrl = item.audioUrl
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

                            // Share button
                            IconButton(
                                onClick = { shareRecitation(context, item) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Tags: Maqam & Ayah Range
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GoldAccent.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "مقام: ${item.maqamAr}",
                                    color = GoldAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldPrimary.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = item.ayahRange,
                                    color = EmeraldPrimary,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isAr) item.descriptionAr else item.descriptionEn,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Surface(
                                onClick = {
                                    if (isPlaying) viewModel.audioPlayer.pause()
                                    else viewModel.playRecitation(item.id, item.titleAr, item.audioUrl)
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isPlaying) GoldAccent else EmeraldPrimary,
                                modifier = Modifier.height(36.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = if (isPlaying) EmeraldDark else Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isPlaying) (if (isAr) "إيقاف مؤقت" else "Pause") else (if (isAr) "استمع للتلاوة" else "Play Recitation"),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPlaying) EmeraldDark else Color.White
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

private fun formatDuration(millis: Int): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}

private fun shareRecitation(context: Context, item: FeaturedRecitation) {
    val text = """
        ✨ استمع لهذه التلاوة المباركة من تطبيق «المقرئ الذكي»:
        🎙️ ${item.titleAr} - ${item.reciterNameAr}
        📖 ${item.surahNameAr} (${item.ayahRange})
        🎼 مقام: ${item.maqamAr}
        🔗 ${item.audioUrl}
        
        📲 تطبيق المقرئ الذكي • إشراف: Mahmoud Zakaria Aswad
    """.trimIndent()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "مشاركة التلاوة المباركة")
    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(shareIntent)
}
