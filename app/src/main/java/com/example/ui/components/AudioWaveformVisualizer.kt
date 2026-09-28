package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@Composable
fun AudioWaveformVisualizer(
    isActive: Boolean,
    amplitudes: List<Float> = emptyList(),
    barColor: Color = EmeraldPrimary,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")
    val phaseAnim by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val barCount = 24
        for (i in 0 until barCount) {
            val heightFraction = if (isActive) {
                if (amplitudes.isNotEmpty() && i < amplitudes.size) {
                    amplitudes[i].coerceIn(0.15f, 1f)
                } else {
                    // harmonic wave calculation
                    val factor = kotlin.math.sin((i.toDouble() / barCount * Math.PI) * phaseAnim)
                    (0.2f + 0.75f * kotlin.math.abs(factor).toFloat()).coerceIn(0.15f, 1f)
                }
            } else {
                0.15f
            }

            val currentBarColor = if (i % 4 == 0) GoldAccent else if (i % 2 == 0) EmeraldLight else barColor

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height((56 * heightFraction).dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(currentBarColor)
            )
        }
    }
}
