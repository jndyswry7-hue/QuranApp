package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserPreferences
import com.example.ui.components.ScoreCircularProgress
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.SuccessGreen
import com.example.viewmodel.MainViewModel

@Composable
fun QuizScreen(
    viewModel: MainViewModel,
    preferences: UserPreferences,
    modifier: Modifier = Modifier
) {
    val questions = viewModel.quizQuestions
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedIndex by viewModel.selectedAnswerIndex.collectAsState()
    val score by viewModel.quizScore.collectAsState()
    val isCompleted by viewModel.quizCompleted.collectAsState()
    val isKidsSenior = preferences.isKidsSeniorMode

    val currentQ = questions.getOrNull(currentIndex) ?: questions.first()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("quiz_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header & Score tracker
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quiz_header_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (preferences.currentLanguage == "ar") "المسابقات والأسئلة القرآنية" else "Islamic Quiz Challenge",
                                fontSize = if (isKidsSenior) 18.sp else 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (preferences.currentLanguage == "ar") "اختبر معلوماتك في التجويد وأعلام القراء" else "Test your knowledge in Tajweed & Reciters",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GoldAccent.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$score / ${questions.size}",
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val progress = ((currentIndex + 1).toFloat() / questions.size).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = EmeraldPrimary,
                        trackColor = EmeraldPrimary.copy(alpha = 0.15f)
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (preferences.currentLanguage == "ar")
                            "السؤال ${currentIndex + 1} من أصل ${questions.size}"
                        else
                            "Question ${currentIndex + 1} of ${questions.size}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (isCompleted) {
            // Quiz Completion Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quiz_completed_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ScoreCircularProgress(
                            score = ((score.toFloat() / questions.size) * 100).toInt(),
                            label = if (preferences.currentLanguage == "ar") "النتيجة" else "Score",
                            size = 110.dp,
                            strokeWidth = 10.dp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (preferences.currentLanguage == "ar") "مبارك إتمام المسابقة بنجاح!" else "Quiz Completed Successfully!",
                            fontSize = if (isKidsSenior) 20.sp else 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (score >= questions.size - 1)
                                (if (preferences.currentLanguage == "ar") "ما شاء الله! إتقان رائع ومميز في علوم القرآن والتجويد." else "Outstanding mastery in Quranic sciences!")
                            else
                                (if (preferences.currentLanguage == "ar") "أداء طيب، استمر في الاستماع لكبار القراء لتعزيز رصيدك." else "Great effort, keep learning with master reciters."),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { viewModel.resetQuiz() },
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(48.dp)
                                .testTag("restart_quiz_button"),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (preferences.currentLanguage == "ar") "إعادة المسابقة" else "Restart Quiz",
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        } else {
            // Question Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quiz_question_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldPrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = currentQ.category,
                                color = EmeraldPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (preferences.currentLanguage == "ar") currentQ.questionAr else currentQ.questionEn,
                            fontSize = if (isKidsSenior) 18.sp else 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Options
                        val options = if (preferences.currentLanguage == "ar") currentQ.optionsAr else currentQ.optionsEn
                        options.forEachIndexed { index, optionText ->
                            val isChosen = selectedIndex == index
                            val isCorrect = index == currentQ.correctIndex
                            val showFeedback = selectedIndex != null

                            val backgroundColor = when {
                                showFeedback && isCorrect -> SuccessGreen.copy(alpha = 0.18f)
                                showFeedback && isChosen && !isCorrect -> Color.Red.copy(alpha = 0.15f)
                                isChosen -> EmeraldPrimary.copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            }

                            val borderColor = when {
                                showFeedback && isCorrect -> SuccessGreen
                                showFeedback && isChosen && !isCorrect -> Color.Red
                                else -> Color.Transparent
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.2.dp, borderColor, RoundedCornerShape(12.dp))
                                    .clickable(enabled = selectedIndex == null) {
                                        viewModel.selectQuizAnswer(index)
                                    }
                                    .testTag("quiz_option_$index"),
                                color = backgroundColor,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    showFeedback && isCorrect -> SuccessGreen
                                                    showFeedback && isChosen && !isCorrect -> Color.Red
                                                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (showFeedback && isCorrect) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        } else if (showFeedback && isChosen && !isCorrect) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        } else {
                                            Text(
                                                text = "${index + 1}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = optionText,
                                        fontSize = if (isKidsSenior) 15.sp else 13.sp,
                                        fontWeight = if (isChosen || (showFeedback && isCorrect)) FontWeight.Bold else FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // Explanation & Next Button
                        AnimatedVisibility(visible = selectedIndex != null) {
                            Column {
                                Spacer(modifier = Modifier.height(14.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = GoldAccent.copy(alpha = 0.1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = GoldAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = if (preferences.currentLanguage == "ar") "الإيضاح القرآني والتجويدي:" else "Explanation:",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = if (preferences.currentLanguage == "ar") currentQ.explanationAr else currentQ.explanationEn,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = { viewModel.nextQuizQuestion() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp)
                                        .testTag("next_quiz_question_button"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                                ) {
                                    Text(
                                        text = if (currentIndex < questions.size - 1)
                                            (if (preferences.currentLanguage == "ar") "السؤال التالي" else "Next Question")
                                        else
                                            (if (preferences.currentLanguage == "ar") "عرض النتيجة النهائية" else "Show Results"),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                                        contentDescription = null,
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
