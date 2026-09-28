package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight

/**
 * Persistent responsive footer bar crafted with precision and elegance.
 * Explicitly requested: "Mahmoud Zakaria Aswad"
 */
@Composable
fun MahmoudZakariaAswadFooter(
    modifier: Modifier = Modifier,
    isKidsSeniorMode: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        EmeraldDark,
                        EmeraldPrimary,
                        EmeraldDark
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(GoldAccent, GoldLight, GoldAccent)
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 16.dp, vertical = if (isKidsSeniorMode) 10.dp else 7.dp)
            .testTag("mahmoud_zakaria_aswad_footer"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "Verified Seal",
                tint = GoldAccent,
                modifier = Modifier.size(if (isKidsSeniorMode) 18.dp else 14.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Mahmoud Zakaria Aswad",
                color = GoldLight,
                fontSize = if (isKidsSeniorMode) 14.sp else 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "• المقرئ الذكي",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                fontSize = if (isKidsSeniorMode) 12.sp else 10.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}
