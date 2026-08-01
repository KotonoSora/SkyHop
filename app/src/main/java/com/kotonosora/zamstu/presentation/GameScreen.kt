package com.kotonosora.zamstu.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import com.kotonosora.zamstu.core.AppConstants
import com.kotonosora.zamstu.domain.model.GameState
import com.kotonosora.zamstu.domain.model.PipeState
import com.kotonosora.zamstu.feature.shop.SkinData
import com.kotonosora.zamstu.presentation.components.Animal
import com.kotonosora.zamstu.presentation.components.GameBackground
import com.kotonosora.zamstu.presentation.components.GameHUD
import com.kotonosora.zamstu.presentation.components.GameOverShopOverlay
import com.kotonosora.zamstu.presentation.components.GameWinOverlay
import com.kotonosora.zamstu.presentation.components.PipesCanvas
import com.kotonosora.zamstu.presentation.theme.AppTheme
import com.kotonosora.zamstu.viewmodel.GameIntent
import com.kotonosora.zamstu.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onBackToHome: () -> Unit,
    onGoToShop: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    val selectedSkinId by viewModel.selectedSkinId.collectAsState()
    val density = LocalDensity.current

    val animalSkinRes = remember(selectedSkinId) {
        SkinData.getFlyAnimalSkinResource(selectedSkinId)
    }

    val rotation by animateFloatAsState(
        targetValue = (gameState.animal.velocity * 3f).coerceIn(-30f, 90f),
        animationSpec = tween(durationMillis = 100),
        label = "AnimalRotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                viewModel.onIntent(
                    GameIntent.ScreenSizeChanged(
                        size.width.toFloat(),
                        size.height.toFloat()
                    )
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = !gameState.isGameOver && !gameState.isWin
            ) {
                viewModel.onIntent(GameIntent.Jump)
            }) {
        GameContent(
            gameState = gameState,
            animalSkinRes = animalSkinRes,
            rotation = rotation,
            density = density,
            onUsePowerUp = { viewModel.onIntent(GameIntent.UsePowerUp(it)) },
            onQuickBuy = onGoToShop,
            onHome = onBackToHome,
            onPlayAgain = {
                viewModel.onIntent(
                    GameIntent.StartGame(
                        level = gameState.level,
                        isEndless = gameState.isEndless
                    )
                )
            },
            onNextLevel = {
                viewModel.onIntent(
                    GameIntent.StartGame(
                        level = gameState.level + 1,
                        isEndless = false
                    )
                )
            },
            onShopClick = onGoToShop
        )
    }
}

@Composable
fun GameContent(
    gameState: GameState,
    animalSkinRes: Int,
    rotation: Float,
    density: Density,
    onUsePowerUp: (String) -> Unit,
    onQuickBuy: () -> Unit,
    onHome: () -> Unit,
    onPlayAgain: () -> Unit,
    onNextLevel: () -> Unit,
    onShopClick: () -> Unit
) {
    GameBackground(opacity = 0.2f)

    PipesCanvas(gameState.pipes)

    Animal(
        density = density,
        position = gameState.animal.position,
        size = gameState.animal.size,
        rotation = rotation,
        skinRes = animalSkinRes,
        shieldActive = gameState.shieldActive
    )

    GameHUD(
        gameState = gameState, onUsePowerUp = onUsePowerUp, onQuickBuy = onQuickBuy
    )

    AnimatedVisibility(
        visible = gameState.isGameOver,
        enter = fadeIn() + scaleIn(),
        modifier = Modifier.fillMaxSize()
    ) {
        GameOverShopOverlay(
            score = gameState.score,
            level = gameState.level,
            onHome = onHome,
            onPlayAgain = onPlayAgain,
            onShopClick = onShopClick
        )
    }

    AnimatedVisibility(
        visible = gameState.isWin,
        enter = fadeIn() + scaleIn(),
        modifier = Modifier.fillMaxSize()
    ) {
        GameWinOverlay(
            reward = gameState.rewardCoins,
            onHome = onHome,
            onNextLevel = onNextLevel
        )
    }
}

@Preview(showBackground = true, showSystemUi = false, name = "Game Screen – playing")
@Composable
fun GameScreenPlayingPreview() {
    AppTheme {
        GameContent(
            gameState = GameState(
                score = 42,
                level = 2,
                coins = AppConstants.DEFAULT_INITIAL_COINS,
                isGameStarted = true,
                pipes = listOf(
                    PipeState(x = 600f, gapTop = 300f), PipeState(x = 1000f, gapTop = 500f)
                )
            ),
            animalSkinRes = SkinData.getIDLEAnimalSkinResource(""),
            rotation = -10f,
            density = Density(3f),
            onUsePowerUp = {},
            onQuickBuy = {},
            onHome = {},
            onPlayAgain = {},
            onNextLevel = {},
            onShopClick = {})
    }
}

@Preview(showBackground = true, showSystemUi = false, name = "Game Screen – win")
@Composable
fun GameScreenWinPreview() {
    AppTheme {
        GameContent(
            gameState = GameState(
                score = 50,
                targetScore = 50,
                level = 5,
                coins = AppConstants.DEFAULT_INITIAL_COINS,
                isWin = true,
                rewardCoins = 25
            ),
            animalSkinRes = SkinData.getIDLEAnimalSkinResource(""),
            rotation = 0f,
            density = Density(3f),
            onUsePowerUp = {},
            onQuickBuy = {},
            onHome = {},
            onPlayAgain = {},
            onNextLevel = {},
            onShopClick = {})
    }
}

@Preview(showBackground = true, showSystemUi = false, name = "Game Screen – game over")
@Composable
fun GameScreenGameOverPreview() {
    AppTheme {
        GameContent(
            gameState = GameState(
                score = 15,
                level = 1,
                coins = AppConstants.DEFAULT_INITIAL_COINS,
                highScore = 42,
                isGameOver = true
            ),
            animalSkinRes = SkinData.getIDLEAnimalSkinResource(""),
            rotation = 90f,
            density = Density(3f),
            onUsePowerUp = {},
            onQuickBuy = {},
            onHome = {},
            onPlayAgain = {},
            onNextLevel = {},
            onShopClick = {})
    }
}
