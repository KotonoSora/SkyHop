package com.kotonosora.skyboundhopper.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Forward
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun GameOverShopOverlay(
    score: Int,
    coins: Int,
    shieldCount: Int,
    autoPlayCount: Int,
    onReviveShield: () -> Unit,
    onPowerUpBoost: () -> Unit,
    onGetMoreCoins: () -> Unit,
    onHome: () -> Unit,
    onPlayAgain: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameOverHeader(score = score)

            Spacer(modifier = Modifier.height(32.dp))

            PowerUpOptionsRow(
                shieldCount = shieldCount,
                autoPlayCount = autoPlayCount,
                onReviveShield = onReviveShield,
                onPowerUpBoost = onPowerUpBoost
            )

            Spacer(modifier = Modifier.height(24.dp))

            GameButton(
                text = "GET MORE COINS",
                onClick = onGetMoreCoins,
                modifier = Modifier.fillMaxWidth(),
                height = 56.dp,
                backgroundColor = Color(0xFFE64A19),
                shadowColor = Color(0xFFBF360C),
                textColor = Color.White,
                icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            ActionButtonsRow(onHome = onHome, onPlayAgain = onPlayAgain)
        }
    }
}

@Composable
private fun GameOverHeader(score: Int) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF1976D2),
        modifier = Modifier.fillMaxWidth().height(120.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = "GAME OVER", fontSize = 48.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(text = "FINAL SCORE", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    Text(
        text = String.format(Locale.US, "%,d", score),
        color = Color(0xFFFFCA28),
        fontSize = 64.sp,
        fontWeight = FontWeight.Black
    )
}

@Composable
private fun PowerUpOptionsRow(
    shieldCount: Int,
    autoPlayCount: Int,
    onReviveShield: () -> Unit,
    onPowerUpBoost: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        PowerUpPurchaseCard(
            modifier = Modifier.weight(1f),
            title = if (shieldCount > 0) "USE\nSHIELD" else "REVIVE WITH\nSHIELD",
            icon = Icons.Default.Shield,
            cost = if (shieldCount > 0) 0 else 50,
            color = Color(0xFF0D47A1),
            onPurchase = onReviveShield
        )
        PowerUpPurchaseCard(
            modifier = Modifier.weight(1f),
            title = if (autoPlayCount > 0) "USE\nBOOST" else "POWER-UP\nWITH BOOST",
            icon = Icons.AutoMirrored.Filled.Forward,
            cost = if (autoPlayCount > 0) 0 else 50,
            color = Color(0xFFFFA000),
            onPurchase = onPowerUpBoost
        )
    }
}

@Composable
private fun ActionButtonsRow(onHome: () -> Unit, onPlayAgain: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        GameButton(
            text = "HOME",
            onClick = onHome,
            modifier = Modifier.weight(1f),
            height = 56.dp,
            backgroundColor = Color(0xFF1565C0),
            shadowColor = Color(0xFF0D47A1),
            textColor = Color.White
        )
        GameButton(
            text = "PLAY AGAIN",
            onClick = { onPlayAgain() },
            modifier = Modifier.weight(1f),
            height = 56.dp,
            backgroundColor = Color(0xFFFBC02D),
            shadowColor = Color(0xFFF57F17),
            textColor = Color.Black
        )
    }
}

@Composable
fun PowerUpPurchaseCard(
    modifier: Modifier,
    title: String,
    icon: ImageVector,
    cost: Int,
    color: Color,
    onPurchase: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = color.copy(alpha = 0.9f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = if (cost > 0) "$cost Coins" else "FREE (Stored)", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            GameButton(
                text = if (cost > 0) "PURCHASE" else "USE NOW",
                onClick = onPurchase,
                modifier = Modifier.fillMaxWidth(),
                height = 40.dp,
                backgroundColor = Color.White.copy(alpha = 0.3f),
                shadowColor = Color.White.copy(alpha = 0.1f),
                textColor = Color.White,
                borderWidth = 0.dp
            )
        }
    }
}
