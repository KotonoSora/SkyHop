package com.jn.flagfang.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@Composable
fun Animal(
    density: Density,
    position: Offset,
    size: Size,
    rotation: Float,
    skinRes: Int,
    shieldActive: Boolean
) {
    val AnimalWidth = with(density) { size.width.toDp() }
    val AnimalHeight = with(density) { size.height.toDp() }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(position.x.roundToInt(), position.y.roundToInt())
            }
            .size(AnimalWidth, AnimalHeight)
    ) {
        Image(
            painter = painterResource(id = skinRes),
            contentDescription = "Animal",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationZ = rotation },
            contentScale = ContentScale.Fit
        )

        if (shieldActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = 1.3f
                        scaleY = 1.3f
                    }
                    .clip(CircleShape)
                    .background(Color(0xFF29B6F6).copy(alpha = 0.3f))
            )
        }
    }
}
