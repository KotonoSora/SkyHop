package com.kotonosora.skyboundhopper.view

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotonosora.skyboundhopper.BuildConfig
import com.kotonosora.skyboundhopper.viewmodel.GameViewModel
import com.kotonosora.skyboundhopper.model.GameState
import com.kotonosora.skyboundhopper.view.components.GameBackground
import com.kotonosora.skyboundhopper.view.components.GameButton
import com.kotonosora.skyboundhopper.model.SkinData

@Composable
fun HomeScreen(
    onPlayClick: () -> Unit,
    onShopClick: () -> Unit,
    onGetCoinsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    gameViewModel: GameViewModel = viewModel()
) {
    val gameState by gameViewModel.gameState.collectAsState()
    val selectedSkinId by gameViewModel.selectedSkinId.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GameBackground(opacity = 0.3f)

        SettingsIconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedHomeScreenElements(
                gameState = gameState,
                selectedSkinId = selectedSkinId,
                onLogoDebugClick = onGetCoinsClick
            )

            Spacer(modifier = Modifier.height(32.dp))

            HomeMenuButton(
                text = "PLAY",
                onClick = onPlayClick,
                modifier = Modifier.fillMaxWidth(0.75f),
                backgroundColor = MaterialTheme.colorScheme.primary, 
                shadowColor = Color.Black,
                textColor = Color.Black,
                icon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            HomeMenuButton(
                text = "GET COINS",
                onClick = onGetCoinsClick,
                modifier = Modifier.fillMaxWidth(0.75f),
                backgroundColor = Color(0xFFFFCA28),
                shadowColor = Color(0xFFFFA000),
                textColor = Color.Black,
                icon = { Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            HomeMenuButton(
                text = "SKIN SHOP",
                onClick = onShopClick,
                modifier = Modifier.fillMaxWidth(0.75f),
                backgroundColor = Color(0xFF66BB6A),
                shadowColor = Color(0xFF388E3C),
                textColor = Color.White,
                icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White) }
            )
        }
    }
}

@Composable
fun AnimatedHomeScreenElements(
    gameState: GameState,
    selectedSkinId: String,
    onLogoDebugClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "HomeAnimations")
    var debugTapCount by remember { mutableIntStateOf(0) }
    
    val birdOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BirdFloating"
    )

    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LogoScaling"
    )

    Text(
        text = "SKYHOP",
        color = Color(0xFFFFD54F),
        style = MaterialTheme.typography.displayLarge,
        modifier = Modifier
            .scale(logoScale)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                enabled = BuildConfig.DEBUG
            ) {
                debugTapCount++
                if (debugTapCount >= 5) {
                    onLogoDebugClick()
                    debugTapCount = 0
                }
            }
    )

    Spacer(modifier = Modifier.height(32.dp))

    val birdSkinRes = remember(selectedSkinId) { SkinData.getBirdSkinResource(selectedSkinId) }
    Image(
        painter = painterResource(id = birdSkinRes),
        contentDescription = "Hero Bird",
        modifier = Modifier
            .size(180.dp)
            .graphicsLayer {
                translationY = birdOffset
            },
        contentScale = ContentScale.Fit
    )
}

@Composable
fun SettingsIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.3f))
    ) {
        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.Black)
    }
}

@Composable
fun HomeMenuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    shadowColor: Color,
    textColor: Color,
    icon: @Composable () -> Unit
) {
    GameButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        backgroundColor = backgroundColor,
        shadowColor = shadowColor,
        textColor = textColor,
        icon = icon
    )
}