package com.kotonosora.skyboundhopper.view.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.kotonosora.skyboundhopper.model.PipeState
import com.kotonosora.skyboundhopper.view.theme.DarkPipeGreen
import com.kotonosora.skyboundhopper.view.theme.PipeGreen

@Composable
fun PipesCanvas(pipes: List<PipeState>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val cornerRadius = CornerRadius(8.dp.toPx())
        val strokeWidth = 3.dp.toPx()
        
        pipes.forEach { pipe ->
            // Top Pipe
            drawRoundRect(
                color = PipeGreen,
                topLeft = Offset(pipe.x, -strokeWidth),
                size = Size(pipe.width, pipe.gapTop + strokeWidth),
                cornerRadius = cornerRadius
            )
            drawRoundRect(
                color = DarkPipeGreen,
                topLeft = Offset(pipe.x, -strokeWidth),
                size = Size(pipe.width, pipe.gapTop + strokeWidth),
                cornerRadius = cornerRadius,
                style = Stroke(width = strokeWidth)
            )

            // Bottom Pipe
            drawRoundRect(
                color = PipeGreen,
                topLeft = Offset(pipe.x, pipe.gapTop + pipe.gapHeight),
                size = Size(pipe.width, size.height - (pipe.gapTop + pipe.gapHeight) + strokeWidth),
                cornerRadius = cornerRadius
            )
            drawRoundRect(
                color = DarkPipeGreen,
                topLeft = Offset(pipe.x, pipe.gapTop + pipe.gapHeight),
                size = Size(pipe.width, size.height - (pipe.gapTop + pipe.gapHeight) + strokeWidth),
                cornerRadius = cornerRadius,
                style = Stroke(width = strokeWidth)
            )
        }
    }
}
