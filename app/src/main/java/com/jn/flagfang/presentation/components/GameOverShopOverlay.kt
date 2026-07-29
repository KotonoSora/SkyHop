package com.jn.flagfang.presentation.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jn.flagfang.R
import com.jn.flagfang.presentation.theme.GameTheme
import com.jn.flagfang.presentation.theme.NeonCyan
import com.jn.flagfang.presentation.theme.NeonRed
import com.jn.flagfang.presentation.theme.NeonYellow
import java.util.Locale

@Composable
fun GameOverShopOverlay(
    score: Int,
    level: Int,
    onHome: () -> Unit,
    onPlayAgain: () -> Unit,
    onShopClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
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
                text = stringResource(R.string.label_game_over),
                color = NeonRed,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "SCORE",
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.labelSmall
            )

            Text(
                text = String.format(Locale.US, "%,d", score),
                color = NeonCyan,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            GameButton(
                text = "REPLAY",
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = NeonCyan,
                textColor = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            GameButton(
                text = "GET COINS",
                onClick = onShopClick,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = NeonYellow,
                textColor = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

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

@Preview(name = "Game Over Overlay", showBackground = true)
@Composable
private fun GameOverShopOverlayPreview() {
    GameTheme {
        GameOverShopOverlay(
            score = 125,
            level = 5,
            onHome = {},
            onPlayAgain = {},
            onShopClick = {}
        )
    }
}
