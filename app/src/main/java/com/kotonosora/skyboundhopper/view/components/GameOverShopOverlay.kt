package com.kotonosora.skyboundhopper.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotonosora.skyboundhopper.R
import java.util.Locale

@Composable
fun GameOverShopOverlay(
    score: Int,
    onHome: () -> Unit,
    onPlayAgain: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            // Consume clicks to prevent interaction with the GameScreen's clickable background
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { /* No-op, just consume */ },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Top Highlight Score
            Text(
                text = stringResource(R.string.title_score),
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.titleLarge,
                letterSpacing = 4.sp
            )
            
            Text(
                text = String.format(Locale.US, "%,d", score),
                color = Color(0xFFFFD54F), // Gold
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.label_game_over),
                color = Color.White,
                style = MaterialTheme.typography.displaySmall,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Action Buttons
            GameButton(
                text = stringResource(R.string.btn_replay),
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth(),
                height = 64.dp,
                backgroundColor = Color(0xFFFBC02D),
                shadowColor = Color(0xFFF57F17),
                textColor = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            GameButton(
                text = stringResource(R.string.btn_home),
                onClick = onHome,
                modifier = Modifier.fillMaxWidth(),
                height = 56.dp,
                backgroundColor = Color(0xFF424242), // Dark grey
                shadowColor = Color(0xFF212121),
                textColor = Color.White
            )
        }
    }
}