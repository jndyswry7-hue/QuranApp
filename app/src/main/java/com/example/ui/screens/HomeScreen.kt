package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DailyWird
import com.example.data.model.Reciter
import com.example.data.model.UserPreferences
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.ScreenTab

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    preferences: UserPreferences,
    todayWird: DailyWird?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val reciters = viewModel.reciters
    val spotlightReciter = reciters.firstOrNull() ?: viewModel.reciters[0]
    val isKidsSenior = preferences.isKidsSeniorMode

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner with Reciter illustration & Spiritual Welcome
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldDark)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isKidsSenior) 200.dp else 170.dp)
                ) {
                    // Background image
                    Image(
                        painter = painterResource(id = R.drawable.reciter_banner_1790604208861),
                        contentDescription = "Quran Reciter Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Subtle gradient overlay for high contrast text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.4f),
                                        EmeraldDark.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GoldAccent.copy(alpha = 0.25f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = GoldLight,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (preferences.currentLanguage == "ar") "مقرئ الذكاء الاصطناعي" else "AI Quran Master",
                                        color = GoldLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.toggleDailyReminder() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (preferences.dailyReminderEnabled) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                    contentDescription = "Daily Reminder",
                                    tint = if (preferences.dailyReminderEnabled) GoldAccent else Color.White
                                )
                            }
                        }

                        Column {
                            Text(
                                text = if (preferences.currentLanguage == "ar") "تطبيق المقرئ الذكي" else "The Smart Quran Reciter",
                                color = Color.White,
                                fontSize = if (isKidsSenior) 22.sp else 19.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (preferences.currentLanguage == "ar")
                                    "تعرف على أصوات القراء بالاستماع، تدرب على التجويد، وقيّم تلاوتك آلياً"
                                else
                                    "Recognize reciters by voice, practice Tajweed, and get instant AI feedback",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = if (isKidsSenior) 14.sp else 12.sp,
                                lineHeight = if (isKidsSenior) 20.sp else 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Daily Quran Portion Widget (ورد التلاوة اليومي)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_wird_widget"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (preferences.currentLanguage == "ar") "ورد التلاوة اليومي" else "Daily Reading Portion",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = if (isKidsSenior) 16.sp else 14.sp
                                )
                                Text(
                                    text = if (preferences.currentLanguage == "ar") "المستهدف: 4 صفحات يومياً" else "Target: 4 pages daily",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        val readPages = todayWird?.readPages ?: 2
                        val targetPages = todayWird?.targetPages ?: 4
                        Text(
                            text = "$readPages / $targetPages",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    val readPages = todayWird?.readPages ?: 2
                    val targetPages = todayWird?.targetPages ?: 4
                    LinearProgressIndicator(
                        progress = { (readPages.toFloat() / targetPages).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = EmeraldPrimary,
                        trackColor = EmeraldPrimary.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (readPages >= targetPages)
                                (if (preferences.currentLanguage == "ar") "أحسنت! أكملت ورد اليوم 🎉" else "Well done! Today's portion complete")
                            else
                                (if (preferences.currentLanguage == "ar") "تبقى صفحتان على الإكمال" else "2 pages remaining"),
                            fontSize = 12.sp,
                            color = if (readPages >= targetPages) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            onClick = {
                                val nextPages = if (readPages < targetPages) targetPages else 0
                                viewModel.updateWirdPages(nextPages)
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (readPages >= targetPages)
                                    (if (preferences.currentLanguage == "ar") "إعادة البدء" else "Reset")
                                else
                                    (if (preferences.currentLanguage == "ar") "أتممت القراءة" else "Mark Done"),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Quick Navigation Grid
        item {
            Text(
                text = if (preferences.currentLanguage == "ar") "الخدمات والخصائص الذكية" else "Smart Features",
                fontSize = if (isKidsSenior) 18.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeatureCard(
                    title = if (preferences.currentLanguage == "ar") "تعرف على القارئ" else "Identify Reciter",
                    subtitle = if (preferences.currentLanguage == "ar") "دقة فائقة (صوت/فيديو/مايك)" else "Ultra-Precision (Audio/Video)",
                    icon = Icons.Default.Hearing,
                    iconBg = EmeraldPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateToTab(ScreenTab.RECOGNITION) },
                    testTag = "nav_recognition_card"
                )
                FeatureCard(
                    title = if (preferences.currentLanguage == "ar") "المتتبع الذكي للتلاوة" else "Smart Recitation Tracker",
                    subtitle = if (preferences.currentLanguage == "ar") "تنبيه أحمر للأخطاء (١١٤ سورة)" else "Red Alert for Mistakes",
                    icon = Icons.Default.Mic,
                    iconBg = GoldAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateToTab(ScreenTab.PRACTICE) },
                    testTag = "nav_practice_card"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeatureCard(
                    title = if (preferences.currentLanguage == "ar") "تلاوات كاملة (١١٤)" else "Full 114 Recitations",
                    subtitle = if (preferences.currentLanguage == "ar") "المصحف كاملاً بأصوات الشيوخ" else "Full Quran by Masters",
                    icon = Icons.Default.Star,
                    iconBg = Color(0xFF7C3AED),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateToTab(ScreenTab.FEATURED) },
                    testTag = "nav_featured_card"
                )
                FeatureCard(
                    title = if (preferences.currentLanguage == "ar") "أسئلة دينية" else "Islamic Quiz",
                    subtitle = if (preferences.currentLanguage == "ar") "تجويد وتاريخ القراء" else "Rules & Reciters",
                    icon = Icons.Default.Quiz,
                    iconBg = Color(0xFF0284C7),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateToTab(ScreenTab.QUIZ) },
                    testTag = "nav_quiz_card"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeatureCard(
                    title = if (preferences.currentLanguage == "ar") "المعالم القرآنية" else "Quran Centers",
                    subtitle = if (preferences.currentLanguage == "ar") "خريطة تفاعلية" else "Interactive Map",
                    icon = Icons.Default.Map,
                    iconBg = Color(0xFF059669),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateToTab(ScreenTab.MAP) },
                    testTag = "nav_map_card"
                )
                FeatureCard(
                    title = if (preferences.currentLanguage == "ar") "لوحة الإنجازات" else "Achievements",
                    subtitle = if (preferences.currentLanguage == "ar") "وتصدير التقارير" else "Export Report",
                    icon = Icons.Default.AutoAwesome,
                    iconBg = Color(0xFFD97706),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateToTab(ScreenTab.DASHBOARD) },
                    testTag = "nav_dashboard_card"
                )
            }
        }

        // Spotlight Reciter of the Day
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("spotlight_reciter_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (preferences.currentLanguage == "ar") "قارئ اليوم المتميز" else "Featured Reciter",
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary,
                            fontSize = 13.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldAccent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = spotlightReciter.primaryMaqam,
                                color = GoldAccent,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(EmeraldDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = spotlightReciter.nameAr.take(2),
                                color = GoldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (preferences.currentLanguage == "ar") spotlightReciter.nameAr else spotlightReciter.nameEn,
                                fontWeight = FontWeight.Bold,
                                fontSize = if (isKidsSenior) 16.sp else 14.sp
                            )
                            Text(
                                text = spotlightReciter.titleAr,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                viewModel.playRecitation(
                                    spotlightReciter.id,
                                    spotlightReciter.nameAr,
                                    spotlightReciter.sampleAudioUrl
                                )
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Recitation",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = spotlightReciter.bioAr,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp,
                        maxLines = 3
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconBg,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
