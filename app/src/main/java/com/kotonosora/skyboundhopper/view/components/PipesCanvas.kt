package com.kotonosora.skyboundhopper.view.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.kotonosora.skyboundhopper.model.PipeState
import com.kotonosora.skyboundhopper.view.theme.DarkPipeGreen
import com.kotonosora.skyboundhopper.view.theme.PipeGreen

@Composable
fun PipesCanvas(pipes: List<PipeState>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        pipes.forEach { pipe ->
            val cornerRadius = CornerRadius(8.dp.toPx())
            val strokeWidth = 3.dp.toPx()
            
            val topPipePath = Path().apply {
                addRoundRect(
                    RoundRect(
                        left = pipe.x,
                        top = -strokeWidth,
                        right = pipe.x + pipe.width,
                        bottom = pipe.gapTop,
                        bottomLeftCornerRadius = cornerRadius,
                        bottomRightCornerRadius = cornerRadius,
                        topLeftCornerRadius = CornerRadius.Zero,
                        topRightCornerRadius = CornerRadius.Zero
                    )
                )
            }
            drawPath(path = topPipePath, color = PipeGreen)
            drawPath(path = topPipePath, color = DarkPipeGreen, style = Stroke(width = strokeWidth))

            val bottomPipePath = Path().apply {
                addRoundRect(
                    RoundRect(
                        left = pipe.x,
                        top = pipe.gapTop + pipe.gapHeight,
                        right = pipe.x + pipe.width,
                        bottom = size.height + strokeWidth,
                        topLeftCornerRadius = cornerRadius,
                        topRightCornerRadius = cornerRadius,
                        bottomLeftCornerRadius = CornerRadius.Zero,
                        bottomRightCornerRadius = CornerRadius.Zero
                    )
                )
            }
            drawPath(path = bottomPipePath, color = PipeGreen)
            drawPath(path = bottomPipePath, color = DarkPipeGreen, style = Stroke(width = strokeWidth))
        }
    }
}
