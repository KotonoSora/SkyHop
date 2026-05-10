package com.jn.flagfang.presentation

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jn.flagfang.presentation.components.GameBackground
import com.jn.flagfang.presentation.theme.GameTheme
import com.jn.flagfang.viewmodel.LeaderboardViewModel

private val Gold = Color(0xFFFFD700)
private val Silver = Color(0xFFC0C0C0)
private val Bronze = Color(0xFFCD7F32)

@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel, onBack: () -> Unit
) {
    val topScores by viewModel.topScores.collectAsState()
    LeaderboardScreenContent(topScores = topScores, onBack = onBack)
}

@Composable
fun LeaderboardScreenContent(
    topScores: List<Int>, onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        GameBackground(opacity = 0.3f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
        ) {
            // ── Top bar ──────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🏆 LEADERBOARD",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFD54F),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (topScores.isEmpty()) {
                // ── Empty state ──────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No scores yet.\nPlay a game to get on the board!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // ── Score list ───────────────────────────────────
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }
                    itemsIndexed(topScores) { index, score ->
                        LeaderboardRow(rank = index + 1, score = score)
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(rank: Int, score: Int) {
    val (rowBg, _, medalLabel) = when (rank) {
        1 -> Triple(
            Brush.horizontalGradient(listOf(Color(0xFF7B5800), Color(0xFFFFD700))), Gold, "🥇"
        )

        2 -> Triple(
            Brush.horizontalGradient(listOf(Color(0xFF555555), Color(0xFFC0C0C0))), Silver, "🥈"
        )

        3 -> Triple(
            Brush.horizontalGradient(listOf(Color(0xFF4E2A00), Color(0xFFCD7F32))), Bronze, "🥉"
        )

        else -> Triple(
            Brush.horizontalGradient(
                listOf(Color.White, Color.White)
            ), Color.White, null
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(rowBg)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            // Medal / rank number
            if (medalLabel != null) {
                Text(text = medalLabel, fontSize = 26.sp, color = Color.White)
            } else {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$rank",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Score",
                style = MaterialTheme.typography.bodyMedium,
                color = if (rank <= 3) Color.White.copy(alpha = 0.85f) else Color.Gray,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = score.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = if (rank <= 3) Color.Black else Color.Black
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Leaderboard – with scores")
@Composable
private fun LeaderboardScreenPreview() {
    GameTheme(dynamicColor = false) {
        LeaderboardScreenContent(
            topScores = listOf(9999, 7500, 5200, 3100, 1800, 900, 450), onBack = {})
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Leaderboard – empty")
@Composable
private fun LeaderboardScreenEmptyPreview() {
    GameTheme(dynamicColor = false) {
        LeaderboardScreenContent(
            topScores = emptyList(), onBack = {})
    }
}
