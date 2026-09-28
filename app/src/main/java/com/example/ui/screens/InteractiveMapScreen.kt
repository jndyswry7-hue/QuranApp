package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IslamicLandmark
import com.example.data.model.UserPreferences
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.viewmodel.MainViewModel

@Composable
fun InteractiveMapScreen(
    viewModel: MainViewModel,
    preferences: UserPreferences,
    modifier: Modifier = Modifier
) {
    val landmarks = viewModel.islamicLandmarks
    var selectedLandmark by remember { mutableStateOf(landmarks.first()) }
    val isKidsSenior = preferences.isKidsSeniorMode

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("interactive_map_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("map_header_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Mosque,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (preferences.currentLanguage == "ar") "المعالم والمراكز القرآنية التاريخية" else "Quranic Centers & Holy Sites",
                            fontSize = if (isKidsSenior) 18.sp else 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (preferences.currentLanguage == "ar")
                            "خريطة تفاعلية للمعالم الإسلامية العظمى ومراكز التلاوة التي احتضنت أشهر أئمة وقراء القرآن الكريم عبر العصور."
                        else
                            "Interactive guide to Islamic sanctuaries and historic centers of Quranic recitation.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Interactive Map Canvas Area
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .testTag("interactive_map_canvas_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldDark)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Draw coordinate radar grid circles representing geography
                        val center = Offset(w / 2f, h / 2f)
                        drawCircle(
                            color = Color.White.copy(alpha = 0.08f),
                            radius = w * 0.45f,
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.06f),
                            radius = w * 0.3f,
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.04f),
                            radius = w * 0.15f,
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )

                        // Crosshairs
                        drawLine(
                            color = Color.White.copy(alpha = 0.1f),
                            start = Offset(0f, center.y),
                            end = Offset(w, center.y)
                        )
                        drawLine(
                            color = Color.White.copy(alpha = 0.1f),
                            start = Offset(center.x, 0f),
                            end = Offset(center.x, h)
                        )

                        // Landmark nodes plotted across space
                        val positions = listOf(
                            Offset(w * 0.52f, h * 0.65f), // Makkah
                            Offset(w * 0.51f, h * 0.48f), // Madinah
                            Offset(w * 0.43f, h * 0.35f), // Jerusalem
                            Offset(w * 0.35f, h * 0.38f), // Cairo
                            Offset(w * 0.46f, h * 0.28f)  // Damascus
                        )

                        positions.forEachIndexed { i, pt ->
                            val isSel = landmarks.getOrNull(i)?.id == selectedLandmark.id
                            drawCircle(
                                color = if (isSel) GoldAccent else EmeraldLight,
                                radius = if (isSel) 9.dp.toPx() else 6.dp.toPx(),
                                center = pt
                            )
                            if (isSel) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.35f),
                                    radius = 16.dp.toPx(),
                                    center = pt,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                        }
                    }

                    // Floating Card with selected landmark details
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(12.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.75f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(GoldAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = EmeraldDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedLandmark.nameAr,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${selectedLandmark.cityAr} • إحداثيات: ${selectedLandmark.latitude}° N, ${selectedLandmark.longitude}° E",
                                    color = GoldLight,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // List of Historic Landmarks
        item {
            Text(
                text = if (preferences.currentLanguage == "ar") "المواقع والمساجد التاريخية" else "Historic Mosques & Centers",
                fontWeight = FontWeight.Bold,
                fontSize = if (isKidsSenior) 17.sp else 15.sp
            )
        }

        items(landmarks, key = { it.id }) { landmark ->
            val isSelected = landmark.id == selectedLandmark.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedLandmark = landmark }
                    .testTag("landmark_card_${landmark.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) EmeraldPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                ),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldPrimary) else null
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) EmeraldPrimary else EmeraldPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mosque,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (preferences.currentLanguage == "ar") landmark.nameAr else landmark.nameEn,
                                fontWeight = FontWeight.Bold,
                                fontSize = if (isKidsSenior) 15.sp else 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${landmark.cityAr}، ${landmark.countryAr}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GoldAccent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (preferences.currentLanguage == "ar") "عرض" else "View",
                                color = GoldAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = landmark.descriptionAr,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "أعلام التلاوة المرتبطون: ${landmark.famousRecitersAr}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
