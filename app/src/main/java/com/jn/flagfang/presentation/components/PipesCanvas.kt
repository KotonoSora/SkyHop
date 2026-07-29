package com.jn.flagfang.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.flagfang.domain.model.PipeState
import com.jn.flagfang.presentation.theme.AppTheme

private val limestoneColors = listOf(
    Color(0xFFD7C9A3), // light limestone
    Color(0xFFB7A57A), // tan
    Color(0xFF8D7966)  // darker brown
)

@Composable
fun PipesCanvas(pipes: List<PipeState>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        pipes.forEach { pipe ->
            // Brushes
            val topBrush = Brush.verticalGradient(
                colors = limestoneColors,
                startY = 0f,
                endY = pipe.gapTop
            )
            val bottomBrush = Brush.verticalGradient(
                colors = limestoneColors.reversed(),
                startY = pipe.gapTop + pipe.gapHeight,
                endY = size.height
            )

            // Geometry
            val apexWidth = pipe.width * 0.3f
            val apexLeft = pipe.x + (pipe.width - apexWidth) / 2f
            val apexRight = apexLeft + apexWidth
            val apexY = pipe.gapTop

            val baseWidth = apexWidth
            val baseLeft = apexLeft
            val baseRight = apexRight
            val baseY = pipe.gapTop + pipe.gapHeight

            // Top Pipe (Stalactite)
            drawPath(
                path = createStalactitePath(
                    pipe.x,
                    pipe.width,
                    apexLeft,
                    apexRight,
                    apexY,
                    apexWidth
                ),
                brush = topBrush,
                style = Fill
            )

            // Bottom Pipe (Stalagmite)
            drawPath(
                path = createStalagmitePath(
                    pipe.x,
                    pipe.width,
                    size.height,
                    baseLeft,
                    baseRight,
                    baseY,
                    baseWidth
                ),
                brush = bottomBrush,
                style = Fill
            )
        }
    }
}

private fun createStalactitePath(
    x: Float,
    width: Float,
    apexLeft: Float,
    apexRight: Float,
    apexY: Float,
    apexWidth: Float
): Path = Path().apply {
    moveTo(x, 0f)
    lineTo(x + width, 0f)
    quadraticTo(
        x + width * 0.85f, apexY * 0.7f,
        apexRight, apexY
    )
    quadraticTo(
        x + width / 2f, apexY + apexWidth * 0.25f,
        apexLeft, apexY
    )
    quadraticTo(
        x + width * 0.15f, apexY * 0.7f,
        x, 0f
    )
    close()
}

private fun createStalagmitePath(
    x: Float,
    width: Float,
    height: Float,
    baseLeft: Float,
    baseRight: Float,
    baseY: Float,
    baseWidth: Float
): Path = Path().apply {
    moveTo(x, height)
    lineTo(x + width, height)
    quadraticTo(
        x + width * 0.85f, baseY + (height - baseY) * 0.3f,
        baseRight, baseY
    )
    quadraticTo(
        x + width / 2f, baseY - baseWidth * 0.25f,
        baseLeft, baseY
    )
    quadraticTo(
        x + width * 0.15f, baseY + (height - baseY) * 0.3f,
        x, height
    )
    close()
}

@Preview(name = "Pipes Canvas", group = "Components")
@Composable
fun PipesCanvasPreview() {
    AppTheme {
        Box(modifier = Modifier.size(width = 400.dp, height = 800.dp)) {
            PipesCanvas(
                pipes = listOf(
                    PipeState(x = 50f, gapTop = 200f, gapHeight = 300f, width = 80f),
                    PipeState(x = 250f, gapTop = 400f, gapHeight = 250f, width = 80f)
                )
            )
        }
    }
}
