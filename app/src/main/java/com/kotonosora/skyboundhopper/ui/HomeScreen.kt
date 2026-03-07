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
import com.kotonosora.skyboundhopper.ui.theme.SkyBlue

@Composable
fun HomeScreen(
    onPlayClick: () -> Unit,
    onShopClick: () -> Unit,
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
            Button(
                onClick = onPlayClick,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCA28))
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PLAY",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Skin Shop Button
            OutlinedButton(
                onClick = onShopClick,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White.copy(alpha = 0.3f),
                    contentColor = Color.White
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp)
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SKIN SHOP",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
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
