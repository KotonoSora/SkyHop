package com.kotonosora.zamstu.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.kotonosora.zamstu.R
import com.kotonosora.zamstu.domain.model.Point
import com.kotonosora.zamstu.domain.model.Size
import com.kotonosora.zamstu.presentation.theme.AppTheme
import kotlin.math.roundToInt

@Composable
fun Animal(
    density: Density,
    position: Point,
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

@Preview(name = "Animal - Normal", group = "Components")
@Composable
fun AnimalNormalPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Animal(
                density = LocalDensity.current,
                position = Point(0f, 0f),
                size = Size(100f, 100f),
                rotation = 0f,
                skinRes = R.drawable.img_idle_bat_normal,
                shieldActive = false
            )
        }
    }
}

@Preview(name = "Animal - Shielded", group = "Components")
@Composable
fun AnimalShieldedPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Animal(
                density = LocalDensity.current,
                position = Point(0f, 0f),
                size = Size(100f, 100f),
                rotation = -15f,
                skinRes = R.drawable.img_fly_bat_normal,
                shieldActive = true
            )
        }
    }
}
