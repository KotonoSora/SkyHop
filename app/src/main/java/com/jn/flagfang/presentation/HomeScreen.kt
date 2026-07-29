package com.jn.flagfang.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.flagfang.R
import com.jn.flagfang.feature.shop.SkinData
import com.jn.flagfang.presentation.components.GameBackground
import com.jn.flagfang.presentation.components.GameButton
import com.jn.flagfang.presentation.components.GameHeader
import com.jn.flagfang.presentation.theme.AppTheme
import com.jn.flagfang.presentation.theme.NeonCyan
import com.jn.flagfang.presentation.theme.NeonGreen
import com.jn.flagfang.presentation.theme.NeonMagenta
import com.jn.flagfang.presentation.theme.NeonYellow

@Composable
fun HomeScreen(
    onPlayClick: () -> Unit,
    onDailyChallengeClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onHelpClick: () -> Unit,
    onShopClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onGetCoinsClick: () -> Unit,
    onClaimRewardClick: () -> Unit,
    coins: Int,
    selectedSkinId: String,
    canClaimDailyReward: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GameBackground(opacity = 0.3f)

        GameHeader(
            coins = coins,
            onShopClick = onShopClick,
            onCoinsClick = onGetCoinsClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .padding(top = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedHomeScreenElements(
                selectedSkinId = selectedSkinId
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Button options ──────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(0.8f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (canClaimDailyReward) {
                    GameButton(
                        text = "CLAIM DAILY REWARD",
                        onClick = onClaimRewardClick,
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color(0xFFFF4081),
                        glowColor = Color(0xFFC2185B),
                        textColor = Color.White,
                        icon = {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    )
                }

                GameButton(
                    text = "PLAY GAME",
                    onClick = onPlayClick,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = NeonYellow,
                    textColor = Color.Black,
                    icon = {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                )

                GameButton(
                    text = "DAILY CHALLENGE",
                    onClick = onDailyChallengeClick,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = NeonGreen,
                    textColor = Color.Black,
                    icon = {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                )

                GameButton(
                    text = "LEADERBOARD",
                    onClick = onLeaderboardClick,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = NeonCyan,
                    textColor = Color.Black,
                    icon = {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                )

                GameButton(
                    text = "HELP",
                    onClick = onHelpClick,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.DarkGray,
                    textColor = Color.White
                )

                GameButton(
                    text = "SKIN/POWER-UP SHOP",
                    onClick = onShopClick,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = NeonMagenta,
                    textColor = Color.White,
                    icon = {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                )

                GameButton(
                    text = "SETTING",
                    onClick = onSettingsClick,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFF78909C),
                    textColor = Color.White,
                    icon = {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun AnimatedHomeScreenElements(
    selectedSkinId: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "HomeAnimations")

    val animalOffset by infiniteTransition.animateFloat(
        initialValue = -10f, targetValue = 10f, animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearOutSlowInEasing), repeatMode = RepeatMode.Reverse
        ), label = "AnimalFloating"
    )

    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.95f, targetValue = 1.05f, animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse
        ), label = "LogoScaling"
    )

    Text(
        text = stringResource(R.string.app_name),
        color = Color(0xFFFFD54F),
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.scale(logoScale)
    )

    Spacer(modifier = Modifier.height(32.dp))

    val animalSkinRes =
        remember(selectedSkinId) { SkinData.getIDLEAnimalSkinResource(selectedSkinId) }
    Image(
        painter = painterResource(id = animalSkinRes),
        contentDescription = "Hero Animal",
        modifier = Modifier
            .size(180.dp)
            .graphicsLayer {
                translationY = animalOffset
            },
        contentScale = ContentScale.Fit
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Home Screen - Reward Available")
@Composable
fun HomeScreenRewardAvailablePreview() {
    AppTheme {
        HomeScreen(
            onPlayClick = {},
            onDailyChallengeClick = {},
            onLeaderboardClick = {},
            onHelpClick = {},
            onShopClick = {},
            onSettingsClick = {},
            onGetCoinsClick = {},
            onClaimRewardClick = {},
            coins = 1250,
            selectedSkinId = "",
            canClaimDailyReward = true
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Home Screen - No Reward")
@Composable
fun HomeScreenNoRewardPreview() {
    AppTheme {
        HomeScreen(
            onPlayClick = {},
            onDailyChallengeClick = {},
            onLeaderboardClick = {},
            onHelpClick = {},
            onShopClick = {},
            onSettingsClick = {},
            onGetCoinsClick = {},
            onClaimRewardClick = {},
            coins = 1250,
            selectedSkinId = "",
            canClaimDailyReward = false
        )
    }
}

