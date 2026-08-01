package com.kotonosora.flappybird.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotonosora.flappybird.presentation.theme.AppTheme
import com.kotonosora.flappybird.presentation.theme.NeonGreen
import com.kotonosora.flappybird.presentation.theme.NeonYellow

@Composable
fun GameWinOverlay(
    reward: Int,
    onHome: () -> Unit,
    onNextLevel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "VICTORY!",
                color = NeonGreen,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "LEVEL COMPLETE",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "REWARD",
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.labelSmall
            )

            Text(
                text = "+$reward COINS",
                color = NeonYellow,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(64.dp))

            GameButton(
                text = "NEXT LEVEL",
                onClick = onNextLevel,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = NeonGreen,
                textColor = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            GameButton(
                text = "HOME",
                onClick = onHome,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.DarkGray,
                textColor = Color.White
            )
        }
    }
}

@Preview(name = "Game Win Overlay", showBackground = true)
@Composable
private fun GameWinOverlayPreview() {
    AppTheme {
        GameWinOverlay(
            reward = 50,
            onHome = {},
            onNextLevel = {}
        )
    }
}
