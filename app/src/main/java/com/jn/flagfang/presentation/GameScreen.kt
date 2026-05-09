package com.jn.flagfang.view

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
import com.jn.flagfang.model.GameState
import com.jn.flagfang.model.PipeState
import com.jn.flagfang.model.SkinData
import com.jn.flagfang.view.components.Animal
import com.jn.flagfang.view.components.GameBackground
import com.jn.flagfang.view.components.GameHUD
import com.jn.flagfang.view.components.GameOverShopOverlay
import com.jn.flagfang.view.components.PipesCanvas
import com.jn.flagfang.view.theme.GameTheme
import com.jn.flagfang.viewmodel.GameViewModel

@Composable
fun GameScreen(viewModel: GameViewModel, onBackToHome: () -> Unit) {
    val gameState by viewModel.gameState.collectAsState()
    val selectedSkinId by viewModel.selectedSkinId.collectAsState()
    val density = LocalDensity.current

    val animalSkinRes = remember(selectedSkinId) {
        SkinData.getFlyAnimalSkinResource(selectedSkinId)
    }

    val rotation by animateFloatAsState(
        targetValue = (gameState.Animal.velocity * 3f).coerceIn(-30f, 90f),
        animationSpec = tween(durationMillis = 100),
        label = "AnimalRotation"
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
            }) {
        GameContent(
            gameState = gameState,
            animalSkinRes = animalSkinRes,
            rotation = rotation,
            density = density,
            onUsePowerUp = { viewModel.usePowerUp(it) },
            onHome = onBackToHome,
            onPlayAgain = { viewModel.startGame() })
    }
}

@Composable
fun GameContent(
    gameState: GameState,
    animalSkinRes: Int,
    rotation: Float,
    density: Density,
    onUsePowerUp: (String) -> Unit,
    onHome: () -> Unit,
    onPlayAgain: () -> Unit
) {
    GameBackground(opacity = 0.3f)

    PipesCanvas(gameState.pipes)

    Animal(
        density = density,
        position = gameState.Animal.position,
        size = gameState.Animal.size,
        rotation = rotation,
        skinRes = animalSkinRes,
        shieldActive = gameState.shieldActive
    )

    GameHUD(
        gameState = gameState, onUsePowerUp = onUsePowerUp
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
    GameTheme(dynamicColor = false) {
        GameContent(
            gameState = GameState(
                score = 42, level = 2, isGameStarted = true, pipes = listOf(
                    PipeState(x = 600f, gapTop = 300f), PipeState(x = 1000f, gapTop = 500f)
                )
            ),
            animalSkinRes = SkinData.getIDLEAnimalSkinResource(""),
            rotation = -10f,
            density = Density(3f),
            onUsePowerUp = {},
            onHome = {},
            onPlayAgain = {})
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Game Screen – game over")
@Composable
private fun GameScreenGameOverPreview() {
    GameTheme(dynamicColor = false) {
        GameContent(
            gameState = GameState(
                score = 15, level = 1, highScore = 42, isGameOver = true
            ),
            animalSkinRes = SkinData.getIDLEAnimalSkinResource(""),
            rotation = 90f,
            density = Density(3f),
            onUsePowerUp = {},
            onHome = {},
            onPlayAgain = {})
    }
}

