package com.kotonosora.skyboundhopper.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.kotonosora.skyboundhopper.viewmodel.GameViewModel
import com.kotonosora.skyboundhopper.model.GameState
import com.kotonosora.skyboundhopper.view.components.*
import com.kotonosora.skyboundhopper.model.SkinData

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