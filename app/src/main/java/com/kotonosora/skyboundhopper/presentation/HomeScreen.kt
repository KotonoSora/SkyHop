package com.kotonosora.skyboundhopper.view

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kotonosora.skyboundhopper.model.SkinData
import com.kotonosora.skyboundhopper.view.components.CoinBadge
import com.kotonosora.skyboundhopper.view.components.GameBackground
import com.kotonosora.skyboundhopper.view.components.GameButton
import com.kotonosora.skyboundhopper.view.theme.SkyHopTheme

@Composable
fun HomeScreen(
    onPlayClick: () -> Unit,
    onShopClick: () -> Unit,
    onGetCoinsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    coins: Int,
    selectedSkinId: String
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GameBackground(opacity = 0.3f)

        // ── Top-right group: coins badge + settings button ────────────
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .safeContentPadding()
                .padding(horizontal = 12.dp, vertical = 2.dp)
                .zIndex(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            CoinBadge(coins = coins)
            Spacer(modifier = Modifier.width(12.dp))
            GetCoinsButton(onClick = onGetCoinsClick)
            Spacer(modifier = Modifier.width(12.dp))
            SettingsIconButton(onClick = onSettingsClick)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedHomeScreenElements(
                selectedSkinId = selectedSkinId
            )

            Spacer(modifier = Modifier.height(16.dp))

            GameButton(
                text = "PLAY",
                onClick = { onPlayClick() },
                modifier = Modifier.fillMaxWidth(0.75f),
                backgroundColor = Color(0xFFF0EA2D),
                shadowColor = Color(0xFFF6E128),
                textColor = Color.Black,
                icon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black) }
            )


            Spacer(modifier = Modifier.height(16.dp))

            GameButton(
                text = "SKIN SHOP",
                onClick = {
                    onShopClick()
                },
                modifier = Modifier.fillMaxWidth(0.75f),
                backgroundColor = Color(0xFF66BB6A),
                shadowColor = Color(0xFF388E3C),
                textColor = Color.White,
                icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            GameButton(
                text = "LEADERBOARD",
                onClick = { onLeaderboardClick() },
                modifier = Modifier.fillMaxWidth(0.75f),
                backgroundColor = Color(0xFF42A5F5),
                shadowColor = Color(0xFF1565C0),
                textColor = Color.White,
                icon = { Icon(Icons.Default.Star, contentDescription = null, tint = Color.White) }
            )
        }
    }
}

@Composable
fun AnimatedHomeScreenElements(
    selectedSkinId: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "HomeAnimations")
    
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
        style = MaterialTheme.typography.displayMedium,
        modifier = Modifier.scale(logoScale)
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
fun GetCoinsButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color(0xFFFFCA28))
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = "Get Coins",
            tint = Color.Black,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Home Screen")
@Composable
private fun HomeScreenPreview() {
    SkyHopTheme(dynamicColor = false) {
        HomeScreen(
            onPlayClick = {},
            onShopClick = {},
            onGetCoinsClick = {},
            onSettingsClick = {},
            onLeaderboardClick = {},
            coins = 1250,
            selectedSkinId = ""
        )
    }
}

