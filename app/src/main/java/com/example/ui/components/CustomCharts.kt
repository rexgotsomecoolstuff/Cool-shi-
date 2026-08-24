package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SimpleSparkLineChart(
    dataPoints: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = CyanAccent,
    fillGradient: Boolean = true
) {
    if (dataPoints.isEmpty()) return

    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1000),
        label = "chart_anim"
    )

    val minVal = (dataPoints.minOrNull() ?: 0f) * 0.98f
    val maxVal = (dataPoints.maxOrNull() ?: 100f) * 1.02f
    val range = (maxVal - minVal).coerceAtLeast(1f)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val spacing = width / (dataPoints.size - 1).coerceAtLeast(1)

        val points = dataPoints.mapIndexed { index, value ->
            val x = index * spacing
            val normalizedY = (value - minVal) / range
            val y = height - (normalizedY * height * animatedProgress)
            Offset(x, y.coerceIn(0f, height))
        }

        if (fillGradient && points.size > 1) {
            val fillPath = Path().apply {
                moveTo(points.first().x, height)
                points.forEach { lineTo(it.x, it.y) }
                lineTo(points.last().x, height)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(lineColor.copy(alpha = 0.35f), Color.Transparent),
                    startY = 0f,
                    endY = height
                )
            )
        }

        // Draw Line
        for (i in 0 until points.size - 1) {
            drawLine(
                color = lineColor,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Draw points
        points.forEach { pt ->
            drawCircle(
                color = Color(0xFF080B11),
                radius = 4.dp.toPx(),
                center = pt
            )
            drawCircle(
                color = lineColor,
                radius = 3.dp.toPx(),
                center = pt
            )
        }
    }
}

@Composable
fun WeeklyBarActivityChart(
    days: List<String> = listOf("M", "T", "W", "T", "F", "S", "S"),
    completedFlags: List<Boolean>,
    modifier: Modifier = Modifier,
    activeColor: Color = CyanAccent
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        days.forEachIndexed { index, day ->
            val isDone = completedFlags.getOrElse(index) { false }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .height(if (isDone) 48.dp else 16.dp)
                        .width(14.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isDone) activeColor else ObsidianSurfaceElevated)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = day,
                    color = if (isDone) TextPrimary else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
