package com.kotonosora.flappybird.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotonosora.flappybird.core.AppConstants
import com.kotonosora.flappybird.presentation.components.GameBackground
import com.kotonosora.flappybird.presentation.components.GameHeader
import com.kotonosora.flappybird.presentation.theme.AppTheme
import com.kotonosora.flappybird.presentation.theme.NeonCyan
import com.kotonosora.flappybird.presentation.theme.NeonYellow

@Composable
fun HelpScreen(coins: Int, onBack: () -> Unit) {
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
            GameHeader(title = "HELP", coins = coins, onBackClick = onBack)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "HOW TO PLAY",
                    style = MaterialTheme.typography.headlineMedium,
                    color = NeonYellow,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Tap the screen to jump!",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeonCyan,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Avoid the obstacles and reach the target score to win levels and earn coins.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = false, name = "Help Screen", group = "Screens")
@Composable
fun HelpPreview() {
    AppTheme {
        HelpScreen(coins = AppConstants.DEFAULT_INITIAL_COINS, onBack = {})
    }
}
