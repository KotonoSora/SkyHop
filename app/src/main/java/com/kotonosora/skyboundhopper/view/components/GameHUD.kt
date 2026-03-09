package com.kotonosora.skyboundhopper.view.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotonosora.skyboundhopper.model.GameState
import kotlin.math.ceil

@Composable
fun GameHUD(
    gameState: GameState,
    onUsePowerUp: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 16.dp, start = 24.dp)
        ) {
            HUDBadge(text = "SCORE: ${gameState.score}", icon = Icons.Default.Star)
            Spacer(modifier = Modifier.height(8.dp))
            HUDBadge(text = "LEVEL: ${gameState.level}", icon = Icons.Default.PlayArrow)
        }

        CoinDisplayHUD(
            coins = gameState.coins,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 16.dp, end = 24.dp)
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 32.dp, start = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Support only shield and multiplier
            PowerUpInventoryBadge(
                icon = Icons.Default.Shield,
                count = gameState.shieldCount,
                isActive = gameState.shieldActive,
                timeLeft = gameState.shieldTimeLeft,
                onClick = { onUsePowerUp("shield") },
                typeKey = "shield"
            )
            PowerUpInventoryBadge(
                icon = Icons.Default.DoubleArrow,
                count = gameState.multiplierCount,
                isActive = gameState.multiplierActive,
                timeLeft = gameState.multiplierTimeLeft,
                onClick = { onUsePowerUp("multiplier") },
                typeKey = "multiplier"
            )
        }
    }
}

@Composable
fun HUDBadge(text: String, icon: ImageVector) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.Black.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.labelLarge // Changed from titleSmall for better size
            )
        }
    }
}

@Composable
fun CoinDisplayHUD(
    coins: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.Black.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = Color(0xFFFFD54F),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = coins.toString(),
                color = Color.White,
                style = MaterialTheme.typography.labelLarge // Changed for consistency
            )
        }
    }
}

@Composable
fun PowerUpInventoryBadge(
    icon: ImageVector,
    count: Int,
    isActive: Boolean,
    timeLeft: Float,
    onClick: () -> Unit,
    typeKey: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    val baseColor = Color.Black.copy(alpha = 0.4f)
    val activeColor = Color(0xFF4CAF50).copy(alpha = 0.9f)
    val ringColor = Color(0xFF66BB6A)

    Surface(
        modifier = modifier
            .size(80.dp)
            .clickable(onClick = onClick),
        shape = shape,
        color = if (isActive) activeColor else baseColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isActive) {
                Text(
                    text = "${ceil(timeLeft).toInt()}S",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            } else if (count > 0) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(icon, contentDescription = typeKey, tint = Color.White, modifier = Modifier.size(32.dp))
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            } else {
                Icon(icon, contentDescription = typeKey, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(32.dp))
            }

            if (!isActive && count > 0) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = ringColor,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}
