package com.kotonosora.skyboundhopper.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Forward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotonosora.skyboundhopper.R
import com.kotonosora.skyboundhopper.game.GameViewModel
import com.kotonosora.skyboundhopper.ui.theme.PipeGreen
import com.kotonosora.skyboundhopper.ui.theme.SkyBlue
import java.util.Locale

@Composable
fun GameScreen(viewModel: GameViewModel, onBackToHome: () -> Unit, onGoToShop: () -> Unit) {
    val gameState by viewModel.gameState.collectAsState()
    val selectedSkinId by viewModel.selectedSkinId.collectAsState()
    val density = LocalDensity.current

    val birdSkinRes = when (selectedSkinId) {
        "skin_pirate" -> R.drawable.img_skin_pirate
        "skin_ninja" -> R.drawable.img_skin_ninja
        "skin_robot" -> R.drawable.img_skin_robot
        "skin_space" -> R.drawable.img_skin_space_voyager
        "skin_golden" -> R.drawable.img_skin_golden_phoenix
        "skin_steampunk" -> R.drawable.img_skin_steampunk_flyer
        else -> R.drawable.img_bird_hero
    }

    val rotation by animateFloatAsState(
        targetValue = (gameState.bird.velocity * 3f).coerceIn(-30f, 90f),
        animationSpec = tween(durationMillis = 100)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyBlue)
            .onGloballyPositioned { coordinates ->
                viewModel.onScreenSizeChanged(
                    coordinates.size.width.toFloat(),
                    coordinates.size.height.toFloat()
                )
            }
            .clickable {
                if (!gameState.isGameOver) {
                    viewModel.jump()
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            gameState.pipes.forEach { pipe ->
                drawRoundRect(
                    color = PipeGreen,
                    topLeft = Offset(pipe.x, 0f),
                    size = Size(pipe.width, pipe.gapTop),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
                drawRoundRect(
                    color = PipeGreen,
                    topLeft = Offset(pipe.x, pipe.gapTop + pipe.gapHeight),
                    size = Size(pipe.width, size.height - (pipe.gapTop + pipe.gapHeight)),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
            }
        }

        val birdX = with(density) { gameState.bird.position.x.toDp() }
        val birdY = with(density) { gameState.bird.position.y.toDp() }
        val birdWidth = with(density) { gameState.bird.size.width.toDp() }
        val birdHeight = with(density) { gameState.bird.size.height.toDp() }

        Box(
            modifier = Modifier
                .offset(x = birdX, y = birdY)
                .size(birdWidth, birdHeight)
        ) {
            Image(
                painter = painterResource(id = birdSkinRes),
                contentDescription = "Bird",
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationZ = rotation
                    },
                contentScale = ContentScale.Fit
            )
            
            if (gameState.shieldActive) {
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

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 48.dp, start = 24.dp)
        ) {
            HUDBadge(text = "SCORE: ${gameState.score}", icon = Icons.Default.Star)
            Spacer(modifier = Modifier.height(8.dp))
            HUDBadge(text = "LEVEL ${gameState.level}", icon = Icons.Default.PlayArrow)
        }

        if (gameState.isStartSequenceActive) {
            StartSequenceOverlay(gameState.startSequenceTimeLeft)
        }

        if (gameState.isAutoPlayActive && !gameState.isStartSequenceActive) {
            AutoPlayTimer(gameState.autoPlayTimeLeft)
        }

        Surface(
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 48.dp, end = 24.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.Black.copy(alpha = 0.4f)
        ) {
            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = gameState.coins.toString(), color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        AnimatedVisibility(
            visible = gameState.isGameOver,
            enter = fadeIn() + scaleIn(),
            modifier = Modifier.fillMaxSize()
        ) {
            GameOverShopOverlay(
                score = gameState.score,
                coins = gameState.coins,
                onReviveShield = { viewModel.purchasePowerUp("shield", 50, {}, { onGoToShop() }) },
                onPowerUpBoost = { viewModel.purchasePowerUp("boost", 50, {}, { onGoToShop() }) },
                onGetMoreCoins = onGoToShop,
                onHome = onBackToHome,
                onPlayAgain = { viewModel.startGame() }
            )
        }
    }
}

@Composable
fun HUDBadge(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        color = Color.Black.copy(alpha = 0.4f),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.height(36.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StartSequenceOverlay(timeLeft: Float) {
    val phase = if (timeLeft > 5f) "SHIELD ACTIVE" else "BOOST ACTIVE"
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = phase, fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text(text = "${String.format(Locale.US, "%.1f", if (timeLeft > 5f) timeLeft - 5f else timeLeft)}s", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F))
        }
    }
}

@Composable
fun AutoPlayTimer(timeLeft: Float) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    drawArc(
                        color = Color.White.copy(alpha = 0.2f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = Color(0xFFFFD54F),
                        startAngle = -90f,
                        sweepAngle = (timeLeft / 10f) * 360f,
                        useCenter = false,
                        style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = "${timeLeft.toInt()}s",
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = Color(0xFFFFD54F).copy(alpha = 0.8f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "AUTO PLAY ACTIVE",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun GameOverShopOverlay(
    score: Int,
    coins: Int,
    onReviveShield: () -> Unit,
    onPowerUpBoost: () -> Unit,
    onGetMoreCoins: () -> Unit,
    onHome: () -> Unit,
    onPlayAgain: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1976D2),
                modifier = Modifier.fillMaxWidth().height(120.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "GAME OVER", fontSize = 48.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "FINAL SCORE", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = String.format(Locale.US, "%,d", score), color = Color(0xFFFFCA28), fontSize = 64.sp, fontWeight = FontWeight.Black)

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                PowerUpPurchaseCard(
                    modifier = Modifier.weight(1f),
                    title = "REVIVE WITH\nSHIELD",
                    icon = Icons.Default.Shield,
                    cost = 50,
                    color = Color(0xFF0D47A1),
                    onPurchase = onReviveShield
                )
                PowerUpPurchaseCard(
                    modifier = Modifier.weight(1f),
                    title = "POWER-UP\nWITH BOOST",
                    icon = Icons.AutoMirrored.Filled.Forward,
                    cost = 50,
                    color = Color(0xFFFFA000),
                    onPurchase = onPowerUpBoost
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onGetMoreCoins,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE64A19)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("GET MORE COINS", fontWeight = FontWeight.Black)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = onHome,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("HOME", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { onPlayAgain() },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBC02D)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("PLAY AGAIN", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PowerUpPurchaseCard(
    modifier: Modifier,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    cost: Int,
    color: Color,
    onPurchase: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = color.copy(alpha = 0.9f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "$cost Coins", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onPurchase,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "PURCHASE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}