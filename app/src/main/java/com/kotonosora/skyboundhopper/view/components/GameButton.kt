package com.kotonosora.skyboundhopper.view.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFFFCA28),
    shadowColor: Color = Color(0xFFD4A017),
    textColor: Color = Color.Black,
    icon: @Composable (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(16.dp),
    height: Dp = 64.dp,
    borderWidth: Dp = 3.dp,
    borderColor: Color = Color.White,
    textStyle: TextStyle? = null,
    horizontalPadding: Dp = 16.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "scale"
    )

    val offsetY by animateFloatAsState(
        targetValue = if (isPressed) 6f else 0f,
        animationSpec = tween(durationMillis = 100),
        label = "offsetY"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .height(height + 8.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = 8.dp)
                .clip(shape)
                .background(shadowColor)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = offsetY.dp)
                .clip(shape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            backgroundColor.copy(alpha = 0.8f),
                            backgroundColor
                        )
                    )
                )
                .border(borderWidth, borderColor, shape),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                        CompositionLocalProvider(LocalContentColor provides textColor) {
                            icon()
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = textStyle ?: MaterialTheme.typography.labelLarge,
                    color = textColor,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
