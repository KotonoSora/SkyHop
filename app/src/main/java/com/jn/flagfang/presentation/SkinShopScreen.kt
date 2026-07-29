package com.jn.flagfang.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.flagfang.R
import com.jn.flagfang.feature.shop.ShopData
import com.jn.flagfang.feature.shop.ShopItem
import com.jn.flagfang.feature.shop.SkinIds
import com.jn.flagfang.presentation.components.GameBackground
import com.jn.flagfang.presentation.components.GameButton
import com.jn.flagfang.presentation.components.GameHeader
import com.jn.flagfang.presentation.theme.AppTheme
import com.jn.flagfang.viewmodel.ShopIntent
import com.jn.flagfang.viewmodel.ShopViewModel

@Composable
fun SkinShopScreen(
    onClose: () -> Unit,
    onGoToCoinStore: () -> Unit,
    viewModel: ShopViewModel,
) {
    val coins by viewModel.coins.collectAsState()
    val skinItems by viewModel.skinItems.collectAsState()
    val powerUpItems = viewModel.powerUpItems
    val selectedSkinId by viewModel.selectedSkinId.collectAsState()

    // Collect power-up counts
    val shieldCount by viewModel.shieldCount.collectAsState()
    val multiplierCount by viewModel.multiplierCount.collectAsState()

    SkinShopScreenContent(
        onClose = onClose,
        onGoToCoinStore = onGoToCoinStore,
        coins = coins,
        skinItems = skinItems,
        powerUpItems = powerUpItems,
        selectedSkinId = selectedSkinId,
        shieldCount = shieldCount,
        multiplierCount = multiplierCount,
        onSkinSelectOrBuy = { item ->
            if (item.id.startsWith("skin")) {
                if (item.isUnlocked) viewModel.onIntent(ShopIntent.SelectSkin(item.id))
                else viewModel.onIntent(ShopIntent.BuyItem(item))
            } else {
                viewModel.onIntent(ShopIntent.BuyItem(item))
            }
        }
    )
}

@Composable
fun SkinShopScreenContent(
    onClose: () -> Unit,
    onGoToCoinStore: () -> Unit,
    coins: Int,
    skinItems: List<ShopItem>,
    powerUpItems: List<ShopItem>,
    selectedSkinId: String,
    shieldCount: Int,
    multiplierCount: Int,
    onSkinSelectOrBuy: (ShopItem) -> Unit
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
                title = "SHOP",
                coins = coins,
                onBackClick = onClose,
                onCoinsClick = onGoToCoinStore
            )

            ShopGridContent(
                skinItems = skinItems,
                powerUpItems = powerUpItems,
                selectedSkinId = selectedSkinId,
                shieldCount = shieldCount,
                multiplierCount = multiplierCount,
                onSkinSelectOrBuy = onSkinSelectOrBuy,
                onGoToCoinStore = onGoToCoinStore
            )
        }
    }
}

@Composable
fun ShopGridContent(
    skinItems: List<ShopItem>,
    powerUpItems: List<ShopItem>,
    selectedSkinId: String,
    shieldCount: Int,
    multiplierCount: Int,
    onSkinSelectOrBuy: (ShopItem) -> Unit,
    onGoToCoinStore: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(2) }) {
            SectionTitle(title = "SKINS")
        }

        items(skinItems) { item ->
            SkinShopItemCard(
                item = item, isSelected = selectedSkinId == item.id, onAction = onSkinSelectOrBuy
            )
        }

        item(span = { GridItemSpan(2) }) {
            SectionTitle(title = "POWER-UPS", modifier = Modifier.padding(top = 24.dp))
        }

        items(powerUpItems) { item ->
            val count = when (item.id) {
                "powerup_shield" -> shieldCount
                "powerup_multiplier" -> multiplierCount
                else -> 0
            }
            SkinShopItemCard(
                item = item, isSelected = false, onAction = onSkinSelectOrBuy, ownedCount = count
            )
        }

        item(span = { GridItemSpan(2) }) {
            Spacer(modifier = Modifier.height(16.dp))
            GameButton(
                text = "GET COINS",
                onClick = onGoToCoinStore,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                backgroundColor = Color(0xFFFFD54F),
                glowColor = Color(0xFFFBC02D),
                icon = {
                    Icon(
                        Icons.Default.ShoppingCart, contentDescription = null, tint = Color.Black
                    )
                })
        }
    }
}

@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
        modifier = modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SkinShopItemCard(
    item: ShopItem, isSelected: Boolean, onAction: (ShopItem) -> Unit, ownedCount: Int = 0
) {
    val borderColor = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.2f)
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
        border = BorderStroke(2.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ItemImageContainer(item = item, ownedCount = ownedCount)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = item.name.uppercase(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                maxLines = 2,
                textAlign = TextAlign.Center,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            ItemStatusText(item = item, isSelected = isSelected, ownedCount = ownedCount)

            Spacer(modifier = Modifier.height(12.dp))

            val (btnColor, glowColor, btnText, btnIcon) = getActionVisuals(item, isSelected)

            GameButton(
                text = btnText,
                onClick = { onAction(item) },
                modifier = Modifier.fillMaxWidth(),
                height = 40.dp,
                backgroundColor = btnColor,
                glowColor = glowColor,
                textColor = Color.White,
                borderWidth = 0.dp,
                icon = {
                    Icon(
                        imageVector = btnIcon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                },
                textStyle = MaterialTheme.typography.labelMedium,
                horizontalPadding = 4.dp
            )
        }
    }
}

@Composable
private fun ItemImageContainer(item: ShopItem, ownedCount: Int = 0) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color.DarkGray, Color.Black)
                )
            ), contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = item.name,
            modifier = Modifier.fillMaxSize(0.8f),
            contentScale = ContentScale.Fit
        )

        if (ownedCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .background(Color(0xFFFFD54F), CircleShape)
                    .size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ownedCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun ItemStatusText(item: ShopItem, isSelected: Boolean, ownedCount: Int = 0) {
    val (text, color) = when {
        isSelected -> "ACTIVE" to Color(0xFF4CAF50)
        item.isUnlocked -> "OWNED" to Color(0xFF2196F3)
        else -> "${item.price} COINS" to Color.Gray
    }

    Text(
        text = text, style = MaterialTheme.typography.labelSmall, color = color
    )
}

@Composable
private fun getActionVisuals(item: ShopItem, isSelected: Boolean): ActionVisuals {
    return if (isSelected) {
        ActionVisuals(
            btnColor = Color(0xFF4CAF50),
            glowColor = Color(0xFF388E3C),
            btnText = "ACTIVE",
            btnIcon = Icons.Default.Check
        )
    } else if (item.isUnlocked) {
        ActionVisuals(
            btnColor = Color(0xFF2196F3),
            glowColor = Color(0xFF1976D2),
            btnText = "SELECT",
            btnIcon = Icons.Default.Check
        )
    } else {
        val (color, shadow) = when (item.id) {
            else -> Color(0xFF29B6F6) to Color(0xFF0288D1)
        }
        ActionVisuals(
            btnColor = color,
            glowColor = shadow,
            btnText = "BUY",
            btnIcon = if (item.id.startsWith("skin")) Icons.Default.Lock else Icons.Default.MonetizationOn
        )
    }
}

private data class ActionVisuals(
    val btnColor: Color, val glowColor: Color, val btnText: String, val btnIcon: ImageVector
)

@Preview(showBackground = true, showSystemUi = true, name = "Skin Shop - Mixed States")
@Composable
fun SkinShopScreenMixedPreview() {
    AppTheme {
        SkinShopScreenContent(
            onClose = {},
            onGoToCoinStore = {},
            coins = 2000,
            skinItems = listOf(
                ShopItem(
                    SkinIds.SKIN_DEFAULT_ID,
                    "Classic Bat",
                    "Standard bat.",
                    0,
                    R.drawable.img_idle_bat_normal,
                    true
                ),
                ShopItem(
                    SkinIds.SKIN_SIR_A_LOT,
                    "Sir-A-Lot",
                    "A bat with a hat.",
                    500,
                    R.drawable.img_idle_bat_sir_a_lot,
                    true
                ),
                ShopItem(
                    SkinIds.SKIN_SONAR_MECH,
                    "Sonar Mech",
                    "Mechanical bat.",
                    1000,
                    R.drawable.img_idle_bat_sonar_mech,
                    false
                )
            ),
            powerUpItems = ShopData.getPowerUpItems(),
            selectedSkinId = SkinIds.SKIN_SIR_A_LOT,
            shieldCount = 0,
            multiplierCount = 3,
            onSkinSelectOrBuy = {}
        )
    }
}

