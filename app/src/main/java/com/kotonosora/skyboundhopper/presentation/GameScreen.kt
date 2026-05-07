package com.kotonosora.skyboundhopper.view

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
import com.kotonosora.skyboundhopper.model.GameState
import com.kotonosora.skyboundhopper.model.PipeState
import com.kotonosora.skyboundhopper.model.SkinData
import com.kotonosora.skyboundhopper.view.components.Bird
import com.kotonosora.skyboundhopper.view.components.GameBackground
import com.kotonosora.skyboundhopper.view.components.GameHUD
import com.kotonosora.skyboundhopper.view.components.GameOverShopOverlay
import com.kotonosora.skyboundhopper.view.components.PipesCanvas
import com.kotonosora.skyboundhopper.view.theme.SkyHopTheme
import com.kotonosora.skyboundhopper.viewmodel.GameViewModel

@Composable
fun GameScreen(viewModel: GameViewModel, onBackToHome: () -> Unit) {
    val gameState by viewModel.gameState.collectAsState()
    val selectedSkinId by viewModel.selectedSkinId.collectAsState()
    val density = LocalDensity.current

    val birdSkinRes = remember(selectedSkinId) { SkinData.getBirdSkinResource(selectedSkinId) }

    val rotation by animateFloatAsState(
        targetValue = (gameState.bird.velocity * 3f).coerceIn(-30f, 90f),
        animationSpec = tween(durationMillis = 100),
        label = "BirdRotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                viewModel.onScreenSizeChanged(size.width.toFloat(), size.height.toFloat())
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = !gameState.isGameOver
            ) {
                viewModel.jump()
            }
    ) {
        GameContent(
            gameState = gameState,
            birdSkinRes = birdSkinRes,
            rotation = rotation,
            density = density,
            onUsePowerUp = { viewModel.usePowerUp(it) },
            onHome = onBackToHome,
            onPlayAgain = { viewModel.startGame() }
        )
    }
}

@Composable
fun GameContent(
    gameState: GameState,
    birdSkinRes: Int,
    rotation: Float,
    density: Density,
    onUsePowerUp: (String) -> Unit,
    onHome: () -> Unit,
    onPlayAgain: () -> Unit
) {
    GameBackground(opacity = 0.3f)

    PipesCanvas(gameState.pipes)

    Bird(
        density = density,
        position = gameState.bird.position,
        size = gameState.bird.size,
        rotation = rotation,
        skinRes = birdSkinRes,
        shieldActive = gameState.shieldActive
    )

    GameHUD(
        gameState = gameState,
        onUsePowerUp = onUsePowerUp
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
            onPlayAgain = onPlayAgain
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Game Screen – playing")
@Composable
private fun GameScreenPlayingPreview() {
    SkyHopTheme(dynamicColor = false) {
        GameContent(
            gameState = GameState(
                score = 42,
                level = 2,
                isGameStarted = true,
                pipes = listOf(
                    PipeState(x = 600f, gapTop = 300f),
                    PipeState(x = 1000f, gapTop = 500f)
                )
            ),
            birdSkinRes = SkinData.getBirdSkinResource(""),
            rotation = -10f,
            density = Density(3f),
            onUsePowerUp = {},
            onHome = {},
            onPlayAgain = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Game Screen – game over")
@Composable
private fun GameScreenGameOverPreview() {
    SkyHopTheme(dynamicColor = false) {
        GameContent(
            gameState = GameState(
                score = 15,
                level = 1,
                highScore = 42,
                isGameOver = true
            ),
            birdSkinRes = SkinData.getBirdSkinResource(""),
            rotation = 90f,
            density = Density(3f),
            onUsePowerUp = {},
            onHome = {},
            onPlayAgain = {}
        )
    }
}

