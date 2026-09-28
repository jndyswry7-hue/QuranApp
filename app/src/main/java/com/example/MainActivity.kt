package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.UserPreferences
import com.example.ui.components.MahmoudZakariaAswadFooter
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FeaturedRecitationsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InteractiveMapScreen
import com.example.ui.screens.PracticeScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.ReciterRecognitionScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EyeComfortTint
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.ScreenTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val preferences by mainViewModel.preferences.collectAsState()

            // Request runtime permissions if not granted
            val context = LocalContext.current
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { /* Handled gracefully */ }

            LaunchedEffect(Unit) {
                val permissionsToRequest = mutableListOf(Manifest.permission.RECORD_AUDIO)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                }
                val ungranted = permissionsToRequest.filter {
                    ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
                }
                if (ungranted.isNotEmpty()) {
                    permissionLauncher.launch(ungranted.toTypedArray())
                }
            }

            MyApplicationTheme(darkTheme = preferences.isNightMode) {
                AppRootContent(
                    viewModel = mainViewModel,
                    preferences = preferences
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRootContent(
    viewModel: MainViewModel,
    preferences: UserPreferences
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val todayWird by viewModel.todayWird.collectAsState()

    // Handle Android system Back Button
    BackHandler(enabled = currentTab != ScreenTab.HOME) {
        viewModel.navigateToTab(ScreenTab.HOME)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (preferences.currentLanguage == "ar") "المقرئ الذكي" else "Al-Muqri' Al-Dhaki",
                                fontWeight = FontWeight.Bold,
                                fontSize = if (preferences.isKidsSeniorMode) 20.sp else 18.sp,
                                color = Color.White
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.toggleNightMode() },
                            modifier = Modifier.testTag("appbar_night_mode_button")
                        ) {
                            Icon(
                                imageVector = if (preferences.isNightMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Night Mode",
                                tint = GoldLight
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.navigateToTab(ScreenTab.SETTINGS) },
                            modifier = Modifier.testTag("appbar_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = EmeraldDark
                    )
                )
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    // Persistent Developer Seal and App attribution
                    // Explicitly requested: "Mahmoud Zakaria Aswad"
                    MahmoudZakariaAswadFooter(
                        isKidsSeniorMode = preferences.isKidsSeniorMode
                    )

                    // Navigation Bar
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        modifier = Modifier.testTag("bottom_nav_bar")
                    ) {
                        val items = listOf(
                            Triple(ScreenTab.HOME, if (preferences.currentLanguage == "ar") "الرئيسية" else "Home", Icons.Default.Home),
                            Triple(ScreenTab.RECOGNITION, if (preferences.currentLanguage == "ar") "القارئ" else "Reciter", Icons.Default.Hearing),
                            Triple(ScreenTab.PRACTICE, if (preferences.currentLanguage == "ar") "تجويد" else "Practice", Icons.Default.Mic),
                            Triple(ScreenTab.QUIZ, if (preferences.currentLanguage == "ar") "مسابقات" else "Quiz", Icons.Default.Quiz),
                            Triple(ScreenTab.FEATURED, if (preferences.currentLanguage == "ar") "تلاوات" else "Clips", Icons.Default.Star),
                            Triple(ScreenTab.DASHBOARD, if (preferences.currentLanguage == "ar") "إنجازات" else "Stats", Icons.Default.Timeline)
                        )

                        items.forEach { (tab, label, icon) ->
                            val isSelected = currentTab == tab
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.navigateToTab(tab) },
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = EmeraldDark,
                                    selectedTextColor = EmeraldPrimary,
                                    indicatorColor = GoldAccent.copy(alpha = 0.35f),
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentTab) {
                    ScreenTab.HOME -> HomeScreen(
                        viewModel = viewModel,
                        preferences = preferences,
                        todayWird = todayWird
                    )
                    ScreenTab.RECOGNITION -> ReciterRecognitionScreen(
                        viewModel = viewModel,
                        preferences = preferences
                    )
                    ScreenTab.PRACTICE -> PracticeScreen(
                        viewModel = viewModel,
                        preferences = preferences
                    )
                    ScreenTab.QUIZ -> QuizScreen(
                        viewModel = viewModel,
                        preferences = preferences
                    )
                    ScreenTab.FEATURED -> FeaturedRecitationsScreen(
                        viewModel = viewModel,
                        preferences = preferences
                    )
                    ScreenTab.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        preferences = preferences
                    )
                    ScreenTab.MAP -> InteractiveMapScreen(
                        viewModel = viewModel,
                        preferences = preferences
                    )
                    ScreenTab.SETTINGS -> SettingsScreen(
                        viewModel = viewModel,
                        preferences = preferences
                    )
                }
            }
        }

        // Amber Eye Comfort Tint Overlay (when enabled in settings)
        if (preferences.isEyeComfortMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(EyeComfortTint.copy(alpha = 0.18f))
            )
        }
    }
}
