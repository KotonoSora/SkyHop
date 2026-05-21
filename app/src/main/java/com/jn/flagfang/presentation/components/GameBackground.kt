package com.jn.flagfang.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jn.flagfang.R
import com.jn.flagfang.presentation.theme.CloudWhite
import com.jn.flagfang.presentation.theme.SurfaceDark

@Composable
fun GameBackground(
    modifier: Modifier = Modifier,
    opacity: Float = 1f
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_cave_night),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = opacity
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Cloud(
                Modifier
                    .offset(x = (-115).dp, y = (-175).dp)
                    .scale(0.8f)
                    .alpha(0.6f)
            )
            Cloud(
                Modifier
                    .offset(x = 145.dp, y = (-105).dp)
                    .scale(1.1f)
                    .alpha(0.5f)
            )
            Cloud(
                Modifier
                    .offset(x = (-120).dp, y = 130.dp)
                    .scale(0.9f)
                    .alpha(0.6f)
            )
            Cloud(
                Modifier
                    .offset(x = 110.dp, y = 205.dp)
                    .alpha(0.5f)
            )
        }
    }
}

@Composable
private fun Cloud(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(100.dp)
            .height(60.dp)
    ) {
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
