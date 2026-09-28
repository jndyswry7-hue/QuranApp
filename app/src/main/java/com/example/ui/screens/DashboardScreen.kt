package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserPreferences
import com.example.ui.components.ScoreCircularProgress
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.SuccessGreen
import com.example.viewmodel.MainViewModel

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    preferences: UserPreferences,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sessions by viewModel.practiceSessions.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val syncMessage by viewModel.syncMessage.collectAsState()
    val isKidsSenior = preferences.isKidsSeniorMode

    val avgScore = if (sessions.isNotEmpty()) sessions.map { it.overallScore }.average().toInt() else 88
    val streakDays = 7

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header & PDF Export Button
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (preferences.currentLanguage == "ar") "لوحة التحكم والإنجازات" else "Analytics & Dashboard",
                                fontSize = if (isKidsSenior) 20.sp else 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (preferences.currentLanguage == "ar") "تقارير دقيقة لمتابعة تطور تلاوتك وتجويدك" else "Accurate reports to monitor your Tajweed growth",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { viewModel.triggerCloudSync() },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary.copy(alpha = 0.12f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = "Sync",
                                tint = EmeraldPrimary
                            )
                        }
                    }

                    AnimatedVisibility(visible = syncMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldPrimary.copy(alpha = 0.12f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = syncMessage ?: "",
                                    fontSize = 11.sp,
                                    color = EmeraldDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.exportPdfReport(context) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("export_pdf_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = EmeraldDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (preferences.currentLanguage == "ar") "تصدير تقرير الإنجاز بصيغة PDF ومشاركته" else "Export PDF Progress Report & Share",
                            color = EmeraldDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Summary KPI Metrics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiCard(
                    title = if (preferences.currentLanguage == "ar") "جلسات التلاوة" else "Practices",
                    value = "${sessions.size}",
                    icon = Icons.Default.Mic,
                    color = EmeraldPrimary,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = if (preferences.currentLanguage == "ar") "متوسط التقييم" else "Avg Score",
                    value = "$avgScore%",
                    icon = Icons.Default.Star,
                    color = GoldAccent,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = if (preferences.currentLanguage == "ar") "أيام الالتزام" else "Streak",
                    value = "$streakDays أيام",
                    icon = Icons.Default.LocalFireDepartment,
                    color = Color(0xFFEA580C),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Performance Chart Over Time
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("performance_chart_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (preferences.currentLanguage == "ar") "منحنى تطور دقة التجويد والأداء" else "Tajweed Precision Curve",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldPrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = if (preferences.currentLanguage == "ar") "+12% تحسن ملحوظ" else "+12% Improvement",
                                color = EmeraldPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Custom Compose Canvas Chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            val points = listOf(78f, 82f, 85f, 84f, 89f, 92f, 95f)
                            val stepX = w / (points.size - 1)

                            // Grid lines
                            drawLine(
                                color = Color.LightGray.copy(alpha = 0.3f),
                                start = Offset(0f, h * 0.25f),
                                end = Offset(w, h * 0.25f),
                                strokeWidth = 1f
                            )
                            drawLine(
                                color = Color.LightGray.copy(alpha = 0.3f),
                                start = Offset(0f, h * 0.75f),
                                end = Offset(w, h * 0.75f),
                                strokeWidth = 1f
                            )

                            val path = Path()
                            points.forEachIndexed { i, score ->
                                val x = i * stepX
                                val y = h - ((score - 60f) / 40f) * h
                                if (i == 0) path.moveTo(x, y)
                                else path.lineTo(x, y)

                                drawCircle(
                                    color = GoldAccent,
                                    radius = 4.dp.toPx(),
                                    center = Offset(x, y)
                                )
                            }

                            drawPath(
                                path = path,
                                color = EmeraldPrimary,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("السبت", "الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "اليوم").forEach { day ->
                            Text(
                                text = day,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Achievements & Badges (الأوسمة المحققة)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("achievements_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (preferences.currentLanguage == "ar") "الأوسمة والإنجازات المحققة" else "Earned Badges",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val badges = listOf(
                        Triple("بلبل التجويد", "حصلت على تقييم تجويد 90%+ في مخارج الحروف", GoldAccent),
                        Triple("حارس الورد اليومي", "التزمت بقراءة الورد لـ 7 أيام متتالية", EmeraldPrimary),
                        Triple("خبير نغمات ومقامات", "تعرفت على أصوات 5 قراء بنجاح", Color(0xFF0284C7)),
                        Triple("فارس المسابقات", "أجبت بنجاح على أسئلة التجويد القرآنية", Color(0xFF7C3AED))
                    )

                    badges.forEach { (badgeTitle, badgeDesc, badgeColor) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(badgeColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = badgeColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = badgeTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = badgeDesc,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
