package com.jn.flagfang.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jn.flagfang.R
import com.jn.flagfang.model.GameState
import com.jn.flagfang.model.PowerUpType
import java.util.Locale
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
            HUDBadge(
                text = "${stringResource(R.string.title_score)}: ${gameState.score}",
                icon = Icons.Default.Star,
                contentDescription = stringResource(R.string.desc_star)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Fixed Level Display: Showing progress (pipes passed within current level)
            val pipesInCurrentLevel = gameState.pipesPassed % 10
            HUDBadge(
                text = "${stringResource(R.string.title_level)}: ${gameState.level} ($pipesInCurrentLevel/10)",
                icon = Icons.Default.PlayArrow,
                contentDescription = stringResource(R.string.desc_play)
            )
        }

        CoinDisplayHUD(
            cents = gameState.cents,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 16.dp, end = 24.dp)
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 32.dp, start = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PowerUpInventoryBadge(
                iconRes = R.drawable.img_powerup_shield_icon,
                count = gameState.shieldCount,
                isActive = gameState.shieldActive,
                timeLeft = gameState.shieldTimeLeft,
                onClick = { onUsePowerUp(PowerUpType.SHIELD.id) },
                contentDescription = stringResource(R.string.desc_shield)
            )
            PowerUpInventoryBadge(
                iconRes = R.drawable.img_powerup_multiplier_icon,
                count = gameState.multiplierCount,
                isActive = gameState.multiplierActive,
                timeLeft = gameState.multiplierTimeLeft,
                onClick = { onUsePowerUp(PowerUpType.MULTIPLIER.id) },
                contentDescription = stringResource(R.string.desc_multiplier)
            )
        }
    }
}

@Composable
fun HUDBadge(text: String, icon: ImageVector, contentDescription: String?) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.Black.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = contentDescription,
                tint = Color(0xFFFFD54F),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CoinDisplayHUD(
    cents: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.Black.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.MonetizationOn,
                contentDescription = stringResource(R.string.desc_cent),
                tint = Color(0xFFFFD54F),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = String.format(Locale.getDefault(), "%,d", cents),
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun PowerUpInventoryBadge(
    iconRes: Int,
    count: Int,
    isActive: Boolean,
    timeLeft: Float,
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val progress = (timeLeft / 10f).coerceIn(0f, 1f)
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .size(68.dp)
            .clip(shape)
            .background(Color.Black.copy(alpha = 0.5f))
            .border(
                width = 2.dp,
                color = if (isActive) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.2f),
                shape = shape
            )
            .clickable(enabled = count > 0 && !isActive, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isActive) {
            // Background progress fill
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = Color(0xFF4CAF50).copy(alpha = 0.25f),
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = true
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = contentDescription,
                modifier = Modifier.size(32.dp),
                contentScale = ContentScale.Fit,
                alpha = if (count > 0 || isActive) 1f else 0.3f
            )
            if (isActive) {
                Text(
                    text = "${ceil(timeLeft).toInt()}s",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }

        // Count badge
        if (count > 0 && !isActive) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp),
                shape = CircleShape,
                color = Color(0xFFFF5252),
                tonalElevation = 4.dp
            ) {
                Text(
                    text = count.toString(),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }

        // Locked overlay if count is 0
        if (count <= 0 && !isActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.1f))
            )
        }
    }
}