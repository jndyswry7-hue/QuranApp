package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Elderly
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserPreferences
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    preferences: UserPreferences,
    modifier: Modifier = Modifier
) {
    val isKidsSenior = preferences.isKidsSeniorMode

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = if (preferences.currentLanguage == "ar") "إعدادات التطبيق والمظهر" else "Settings & Preferences",
                fontSize = if (isKidsSenior) 20.sp else 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Night Reading & Eye Comfort Group
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (preferences.currentLanguage == "ar") "راحة العين والقراءة الليلية" else "Display & Eye Comfort",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingToggleRow(
                        title = if (preferences.currentLanguage == "ar") "وضع القراءة الليلية (Night Mode)" else "Night Reading Mode",
                        subtitle = if (preferences.currentLanguage == "ar") "خلفية داكنة مريحة للعين أثناء التلاوة في الظلام" else "Dark AMOLED friendly theme",
                        icon = Icons.Default.DarkMode,
                        checked = preferences.isNightMode,
                        onCheckedChange = { viewModel.toggleNightMode() },
                        testTag = "toggle_night_mode"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingToggleRow(
                        title = if (preferences.currentLanguage == "ar") "فلتر راحة العين (Warm Amber Tint)" else "Eye Comfort Tint",
                        subtitle = if (preferences.currentLanguage == "ar") "تقليل الضوء الأزرق وإضفاء صبغة دافئة ومريحة" else "Warm yellow tint to reduce blue light",
                        icon = Icons.Default.Visibility,
                        checked = preferences.isEyeComfortMode,
                        onCheckedChange = { viewModel.toggleEyeComfortMode() },
                        testTag = "toggle_eye_comfort"
                    )
                }
            }
        }

        // Accessibility (Kids & Seniors Mode)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (preferences.currentLanguage == "ar") "تسهيل الاستخدام وسهولة الوصول" else "Accessibility",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingToggleRow(
                        title = if (preferences.currentLanguage == "ar") "واجهة مخصصة للأطفال وكبار السن" else "Kids & Seniors Mode",
                        subtitle = if (preferences.currentLanguage == "ar") "تكبير الخطوط وتبسيط الأزرار لتجربة بديهية ومريحة" else "Larger typography and simplified touch targets",
                        icon = Icons.Default.Elderly,
                        checked = preferences.isKidsSeniorMode,
                        onCheckedChange = { viewModel.toggleKidsSeniorMode() },
                        testTag = "toggle_kids_senior"
                    )
                }
            }
        }

        // Reminders & Sync
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (preferences.currentLanguage == "ar") "التنبيهات والمزامنة السحابية" else "Alerts & Cloud Sync",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingToggleRow(
                        title = if (preferences.currentLanguage == "ar") "تنبيه الورد اليومي ومواعيد المراجعة" else "Daily Wird Reminder",
                        subtitle = if (preferences.currentLanguage == "ar") "إشعار ذكي يومي لتذكيرك بقراءة وردك ومراجعة حفظك" else "Daily notification for your Quran portion",
                        icon = Icons.Default.NotificationsActive,
                        checked = preferences.dailyReminderEnabled,
                        onCheckedChange = { viewModel.toggleDailyReminder() },
                        testTag = "toggle_daily_reminder"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingToggleRow(
                        title = if (preferences.currentLanguage == "ar") "المزامنة السحابية التلقائية" else "Cloud Backup & Sync",
                        subtitle = if (preferences.currentLanguage == "ar") "حفظ سجل التلاوات والدرجات والمفضلة عبر مختلف أجهزتك" else "Sync progress and bookmarks across devices",
                        icon = Icons.Default.CloudSync,
                        checked = preferences.cloudSyncEnabled,
                        onCheckedChange = { viewModel.triggerCloudSync() },
                        testTag = "toggle_cloud_sync"
                    )
                }
            }
        }

        // Language Switcher
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (preferences.currentLanguage == "ar") "لغة التطبيق (Language)" else "App Language",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("ar", "العربية", "Arabic"),
                            Triple("en", "English", "الإنجليزية"),
                            Triple("fr", "Français", "الفرنسية")
                        ).forEach { (code, label, sub) ->
                            val isSel = preferences.currentLanguage == code
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .testTag("lang_button_$code"),
                                onClick = { viewModel.setLanguage(code) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // About & Developer Seal Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldDark)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(GoldAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "تطبيق المقرئ الذكي • الإصدار 1.0",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "تصميم وتطوير: Mahmoud Zakaria Aswad",
                        color = GoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "تطبيق إسلامي ذكي لخدمة كتاب الله وتيسير معرفة القراء والتجويد",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String = ""
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(EmeraldPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 14.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = EmeraldPrimary
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}
