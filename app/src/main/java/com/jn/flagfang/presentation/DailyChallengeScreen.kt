package com.jn.flagfang.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.jn.flagfang.presentation.components.GameButton
import com.jn.flagfang.presentation.components.GameHeader
import com.jn.flagfang.presentation.theme.GameTheme
import com.jn.flagfang.presentation.theme.NeonCyan
import com.jn.flagfang.presentation.theme.NeonGreen
import com.jn.flagfang.presentation.theme.NeonYellow

@Composable
fun DailyChallengeScreen(coins: Int, onBack: () -> Unit, onPlay: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Column(modifier = Modifier.fillMaxSize().safeContentPadding()) {
            GameHeader(title = "CHALLENGE", coins = coins, onBackClick = onBack)
            
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
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

@Preview(showBackground = true, showSystemUi = true, name = "Daily Challenge Screen", group = "Screens")
@Composable
fun DailyChallengePreview() {
    GameTheme {
        DailyChallengeScreen(coins = 100, onBack = {}, onPlay = {})
    }
}
