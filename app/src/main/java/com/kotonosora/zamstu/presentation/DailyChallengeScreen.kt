package com.kotonosora.zamstu.presentation

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotonosora.zamstu.presentation.components.GameButton
import com.kotonosora.zamstu.presentation.components.GameHeader
import com.kotonosora.zamstu.presentation.theme.AppTheme
import com.kotonosora.zamstu.presentation.theme.NeonCyan
import com.kotonosora.zamstu.presentation.theme.NeonGreen
import com.kotonosora.zamstu.presentation.theme.NeonYellow

@Composable
fun DailyChallengeScreen(coins: Int, onBack: () -> Unit, onPlay: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
        ) {
            GameHeader(title = "CHALLENGE", coins = coins, onBackClick = onBack)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "DAILY CHALLENGE",
                    style = MaterialTheme.typography.headlineMedium,
                    color = NeonGreen,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Surface(
                    color = Color.Black.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(2.dp, NeonGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "GUIDE RULES:",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonCyan
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "• Pass 20 pipes in one run.\n• Shield is disabled.\n• Double gravity active.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "REWARD:",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Text(
                    text = "50 COINS",
                    style = MaterialTheme.typography.headlineSmall,
                    color = NeonYellow,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(48.dp))

                GameButton(
                    text = "PLAY CHALLENGE",
                    onClick = onPlay,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = NeonGreen,
                    textColor = Color.Black
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Daily Challenge Screen",
    group = "Screens"
)
@Composable
fun DailyChallengePreview() {
    AppTheme {
        DailyChallengeScreen(coins = 100, onBack = {}, onPlay = {})
    }
}
