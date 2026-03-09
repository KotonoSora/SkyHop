package com.kotonosora.skyboundhopper.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density

@Composable
fun Bird(
    density: Density,
    position: Offset,
    size: Size,
    rotation: Float,
    skinRes: Int,
    shieldActive: Boolean
) {
    val birdX = with(density) { position.x.toDp() }
    val birdY = with(density) { position.y.toDp() }
    val birdWidth = with(density) { size.width.toDp() }
    val birdHeight = with(density) { size.height.toDp() }

    Box(
        modifier = Modifier
            .offset(x = birdX, y = birdY)
            .size(birdWidth, birdHeight)
    ) {
        Image(
            painter = painterResource(id = skinRes),
            contentDescription = "Bird",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationZ = rotation },
            contentScale = ContentScale.Fit
        )
        
        if (shieldActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color(0xFF29B6F6).copy(alpha = 0.3f))
                    .graphicsLayer {
                        scaleX = 1.3f
                        scaleY = 1.3f
                    }
            )
        }
    }
}
