package com.kotonosora.skyboundhopper.view.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun StartSequenceOverlay(timeLeft: Float) {
    val phase = if (timeLeft > 5f) "SHIELD ACTIVE" else "BOOST ACTIVE"
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = phase, fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text(
                text = "${String.format(Locale.US, "%.1f", if (timeLeft > 5f) timeLeft - 5f else timeLeft)}s",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD54F)
            )
        }
    }
}

@Composable
fun AutoPlayTimer(timeLeft: Float) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressBar(
                progress = timeLeft,
                maxProgress = 10f,
                size = 200.dp,
                progressColor = Color(0xFFFFD54F),
                backgroundColor = Color.White.copy(alpha = 0.2f),
                strokeWidth = 10.dp,
                textSize = 72.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = Color(0xFFFFD54F).copy(alpha = 0.8f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "AUTO PLAY ACTIVE",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun CircularProgressBar(
    progress: Float,
    maxProgress: Float,
    size: Dp,
    progressColor: Color,
    backgroundColor: Color,
    strokeWidth: Dp,
    textSize: TextUnit
) {
    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            drawArc(
                color = backgroundColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = (progress / maxProgress) * 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = "${progress.toInt()}s",
            fontSize = textSize,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}
