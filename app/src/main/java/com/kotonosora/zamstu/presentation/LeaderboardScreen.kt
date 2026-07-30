package com.kotonosora.zamstu.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kotonosora.zamstu.domain.model.ScoreEntry
import com.kotonosora.zamstu.presentation.components.GameBackground
import com.kotonosora.zamstu.presentation.components.GameHeader
import com.kotonosora.zamstu.presentation.theme.AppTheme
import com.kotonosora.zamstu.presentation.theme.NeonCyan
import com.kotonosora.zamstu.presentation.theme.NeonYellow
import com.kotonosora.zamstu.viewmodel.LeaderboardViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel, onBack: () -> Unit
) {
    val scoreHistory by viewModel.scoreHistory.collectAsState()
    val coins by viewModel.coins.collectAsState()
    LeaderboardScreenContent(scoreHistory = scoreHistory, coins = coins, onBack = onBack)
}

@Composable
fun LeaderboardScreenContent(
    scoreHistory: List<ScoreEntry>, coins: Int, onBack: () -> Unit
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
                title = "HISTORY",
                coins = coins,
                onBackClick = onBack
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (scoreHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No games played yet!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(scoreHistory) { entry ->
                        HistoryRow(entry = entry)
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(entry: ScoreEntry) {
    val dateFormat = remember { SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()) }
    val dateStr = remember(entry.timestamp) { dateFormat.format(Date(entry.timestamp)) }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.Black.copy(alpha = 0.4f),
        border = BorderStroke(2.dp, NeonCyan.copy(alpha = 0.8f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "SCORE: ${entry.score}",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeonCyan
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "REWARD",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "+${entry.reward}",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeonYellow
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Leaderboard – with scores")
@Composable
fun LeaderboardScreenPreview() {
    AppTheme {
        LeaderboardScreenContent(
            scoreHistory = listOf(
                ScoreEntry(150, 20, System.currentTimeMillis()),
                ScoreEntry(85, 10, System.currentTimeMillis() - 3600000)
            ),
            coins = 500,
            onBack = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Leaderboard – empty")
@Composable
fun LeaderboardEmptyPreview() {
    AppTheme {
        LeaderboardScreenContent(
            scoreHistory = emptyList(),
            coins = 50,
            onBack = {}
        )
    }
}
