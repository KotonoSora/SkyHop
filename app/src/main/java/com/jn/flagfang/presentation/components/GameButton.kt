package com.jn.flagfang.view.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    enabled: Boolean = true,
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
            .scale(if (enabled) scale else 1f)
            .height(height + 8.dp)
            .then(
                if (enabled) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
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
                .offset(y = if (enabled) offsetY.dp else 0.dp)
                .clip(shape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (enabled) {
                            listOf(
                                backgroundColor.copy(alpha = 0.8f),
                                backgroundColor
                            )
                        } else {
                            listOf(
                                Color.LightGray.copy(alpha = 0.8f),
                                Color.LightGray
                            )
                        }
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
