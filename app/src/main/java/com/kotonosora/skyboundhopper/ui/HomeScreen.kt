package com.kotonosora.skyboundhopper.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotonosora.skyboundhopper.R
import com.kotonosora.skyboundhopper.game.GameViewModel
import com.kotonosora.skyboundhopper.ui.components.GameButton
import com.kotonosora.skyboundhopper.ui.theme.SkyBlue

@Composable
fun HomeScreen(
    onPlayClick: () -> Unit,
    onShopClick: () -> Unit,
    onGetCoinsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    gameViewModel: GameViewModel = viewModel()
) {
    val infiniteTransition = rememberInfiniteTransition(label = "HomeAnimations")
    val shieldCount by gameViewModel.shieldCount.collectAsState()
    val multiplierCount by gameViewModel.multiplierCount.collectAsState()
    
    // Floating bird animation
    val birdOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BirdFloating"
    )

    // Scaling logo animation
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LogoScaling"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyBlue)
    ) {
        // Background Elements (Clouds/Islands)
        Image(
            painter = painterResource(id = R.drawable.img_floating_island),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 50.dp, y = (-100).dp)
                .size(200.dp),
            alpha = 0.6f
        )

        // Settings Button
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(16.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.3f))
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.Black)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Text(
                text = "SkyHop",
                fontSize = 72.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFFD54F),
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.scale(logoScale)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Hero Bird
            Image(
                painter = painterResource(id = R.drawable.img_bird_hero),
                contentDescription = "Hero Bird",
                modifier = Modifier
                    .size(180.dp)
                    .graphicsLayer {
                        translationY = birdOffset
                    },
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Power-up Quick Use
            Row(
                modifier = Modifier.padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PowerUpButton(
                    icon = Icons.Default.Shield,
                    count = shieldCount,
                    color = Color(0xFF29B6F6),
                    onClick = { gameViewModel.usePowerUp("shield") }
                )
                PowerUpButton(
                    icon = Icons.Default.Star,
                    count = multiplierCount,
                    color = Color(0xFFFFA726),
                    onClick = { gameViewModel.usePowerUp("multiplier") }
                )
            }

            // Play Button
            GameButton(
                text = "PLAY",
                onClick = onPlayClick,
                modifier = Modifier.fillMaxWidth(0.6f),
                icon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Get Coins Button
            GameButton(
                text = "GET COINS",
                onClick = onGetCoinsClick,
                modifier = Modifier.fillMaxWidth(0.6f),
                backgroundColor = Color(0xFFFFCA28),
                shadowColor = Color(0xFFFFA000),
                textColor = Color.Black,
                icon = { Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Skin Shop Button
            GameButton(
                text = "SKIN SHOP",
                onClick = onShopClick,
                modifier = Modifier.fillMaxWidth(0.6f),
                backgroundColor = Color(0xFF66BB6A),
                shadowColor = Color(0xFF388E3C),
                textColor = Color.White,
                icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White) }
            )
        }
    }
}

@Composable
fun PowerUpButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    color: Color,
    onClick: () -> Unit
) {
    Box(contentAlignment = Alignment.TopEnd) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f))
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
        }
        
        if (count > 0) {
            Surface(
                shape = CircleShape,
                color = color,
                modifier = Modifier.size(20.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = count.toString(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
