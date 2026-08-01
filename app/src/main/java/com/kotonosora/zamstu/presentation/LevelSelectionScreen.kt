package com.kotonosora.zamstu.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kotonosora.zamstu.core.AppConstants
import com.kotonosora.zamstu.presentation.components.GameBackground
import com.kotonosora.zamstu.presentation.components.GameButton
import com.kotonosora.zamstu.presentation.components.GameHeader
import com.kotonosora.zamstu.presentation.theme.AppTheme
import com.kotonosora.zamstu.presentation.theme.NeonGreen
import com.kotonosora.zamstu.presentation.theme.NeonMagenta

@Composable
fun LevelSelectionScreen(
    coins: Int,
    onBack: () -> Unit,
    onEndlessClick: () -> Unit,
    onDailyChallengeClick: () -> Unit,
    onStoryModeClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        GameBackground(opacity = 0.2f)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
        ) {
            GameHeader(
                title = "LEVELS",
                coins = coins,
                onBackClick = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameButton(
                    text = "ENDLESS MODE",
                    onClick = onEndlessClick,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFF00E5FF), // NeonCyan
                    textColor = Color.Black
                )

                Spacer(modifier = Modifier.height(20.dp))

                GameButton(
                    text = "DAILY CHALLENGE",
                    onClick = onDailyChallengeClick,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = NeonGreen,
                    textColor = Color.Black,
                    icon = { Icon(Icons.Default.Star, contentDescription = null) }
                )

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "STORY MODE",
                    style = MaterialTheme.typography.titleLarge,
                    color = NeonMagenta,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Placeholder for levels
                GameButton(
                    text = "LEVEL 1",
                    onClick = { onStoryModeClick(1) },
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = NeonMagenta,
                    textColor = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = false, name = "Level Selection", group = "Screens")
@Composable
fun LevelSelectionScreenPreview() {
    AppTheme {
        LevelSelectionScreen(
            coins = AppConstants.DEFAULT_INITIAL_COINS,
            onBack = {},
            onEndlessClick = {},
            onDailyChallengeClick = {},
            onStoryModeClick = {}
        )
    }
}
