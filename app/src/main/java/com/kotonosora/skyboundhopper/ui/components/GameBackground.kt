package com.kotonosora.skyboundhopper.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kotonosora.skyboundhopper.R
import com.kotonosora.skyboundhopper.ui.theme.CloudWhite
import com.kotonosora.skyboundhopper.ui.theme.SkyBlue

@Composable
fun GameBackground(
    modifier: Modifier = Modifier,
    opacity: Float = 1f
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SkyBlue) // Solid sky color background
    ) {
        // Full screen background image (img_floating_island used as the landscape)
        Image(
            painter = painterResource(id = R.drawable.img_floating_island),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = opacity
        )

        // Decorative Elements (Island and Clouds)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Decorative Clouds
            Cloud(Modifier.offset(x = (-120).dp, y = (-180).dp).scale(0.8f).alpha(0.6f))
            Cloud(Modifier.offset(x = 140.dp, y = (-100).dp).scale(1.1f).alpha(0.5f))
            Cloud(Modifier.offset(x = (-100).dp, y = 140.dp).scale(0.9f).alpha(0.6f))
            Cloud(Modifier.offset(x = 120.dp, y = 200.dp).alpha(0.5f))
        }
    }
}

@Composable
private fun Cloud(modifier: Modifier = Modifier) {
    Box(modifier = modifier.width(100.dp).height(60.dp)) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.BottomStart)
                .background(CloudWhite.copy(alpha = 0.6f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.Center)
                .background(CloudWhite.copy(alpha = 0.6f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(45.dp)
                .align(Alignment.BottomEnd)
                .background(CloudWhite.copy(alpha = 0.6f), CircleShape)
        )
    }
}
