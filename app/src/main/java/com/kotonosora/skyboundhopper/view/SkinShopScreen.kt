package com.kotonosora.skyboundhopper.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotonosora.skyboundhopper.view.components.GameButton
import com.kotonosora.skyboundhopper.view.components.CoinBadge
import com.kotonosora.skyboundhopper.view.theme.SkyBlue
import com.kotonosora.skyboundhopper.viewmodel.ShopViewModel
import com.kotonosora.skyboundhopper.model.ShopItem

@Composable
fun SkinShopScreen(
    onClose: () -> Unit,
    onGoToCoinStore: () -> Unit,
    viewModel: ShopViewModel = viewModel()
) {
    val coins by viewModel.coins.collectAsState()
    val skinItems by viewModel.skinItems.collectAsState()
    val powerUpItems = viewModel.powerUpItems
    val selectedSkinId by viewModel.selectedSkinId.collectAsState()

    // Collect power-up counts
    val shieldCount by viewModel.shieldCount.collectAsState()
    val multiplierCount by viewModel.multiplierCount.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            SkinShopHeader(
                onClose = onClose,
                coins = coins
            )

            ShopGridContent(
                skinItems = skinItems,
                powerUpItems = powerUpItems,
                selectedSkinId = selectedSkinId,
                shieldCount = shieldCount,
                multiplierCount = multiplierCount,
                onSkinSelectOrBuy = { item ->
                    if (item.id.startsWith("skin")) {
                        if (item.isUnlocked) viewModel.selectSkin(item.id)
                        else viewModel.buyItem(item)
                    } else {
                        viewModel.buyItem(item)
                    }
                },
                onGoToCoinStore = onGoToCoinStore
            )
        }
    }
}

@Composable
fun SkinShopHeader(
    onClose: () -> Unit,
    coins: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.3f))
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
        }

        Text(
            text = "SHOP",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Black
        )

        CoinBadge(coins = coins)
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
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(2) }) {
            SectionTitle(title = "SKINS")
        }

        items(skinItems) { item ->
            SkinShopItemCard(
                item = item,
                isSelected = selectedSkinId == item.id,
                onAction = onSkinSelectOrBuy
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
                item = item,
                isSelected = false,
                onAction = onSkinSelectOrBuy,
                ownedCount = count
            )
        }
        
        item(span = { GridItemSpan(2) }) {
            Spacer(modifier = Modifier.height(16.dp))
            GameButton(
                text = "GET COINS",
                onClick = onGoToCoinStore,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                backgroundColor = Color(0xFFFFD54F),
                shadowColor = Color(0xFFFBC02D),
                icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.Black) }
            )
        }
    }
}

@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = Color.Black,
        modifier = modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SkinShopItemCard(
    item: ShopItem,
    isSelected: Boolean,
    onAction: (ShopItem) -> Unit,
    ownedCount: Int = 0
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ItemImageContainer(item = item, ownedCount = ownedCount)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = item.name.uppercase(),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                maxLines = 1,
                textAlign = TextAlign.Center,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            ItemStatusText(item = item, isSelected = isSelected, ownedCount = ownedCount)

            Spacer(modifier = Modifier.height(12.dp))

            val (btnColor, shadowColor, btnText, btnIcon) = getActionVisuals(item, isSelected)

            GameButton(
                text = btnText,
                onClick = { onAction(item) },
                modifier = Modifier.fillMaxWidth(),
                height = 40.dp,
                backgroundColor = btnColor,
                shadowColor = shadowColor,
                textColor = Color.White,
                borderWidth = 0.dp,
                icon = { Icon(imageVector = btnIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp)) },
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
                    listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))
                )
            ),
        contentAlignment = Alignment.Center
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
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color
    )
}

@Composable
private fun getActionVisuals(item: ShopItem, isSelected: Boolean): ActionVisuals {
    return if (isSelected) {
        ActionVisuals(
            btnColor = Color(0xFF4CAF50),
            shadowColor = Color(0xFF388E3C),
            btnText = "ACTIVE",
            btnIcon = Icons.Default.Check
        )
    } else if (item.isUnlocked) {
        ActionVisuals(
            btnColor = Color(0xFF2196F3),
            shadowColor = Color(0xFF1976D2),
            btnText = "SELECT",
            btnIcon = Icons.Default.Check
        )
    } else {
        val (color, shadow) = when (item.id) {
            "skin_space" -> Color(0xFF5C6BC0) to Color(0xFF3F51B5)
            "skin_golden" -> Color(0xFFFFA726) to Color(0xFFF57C00)
            "skin_steampunk" -> Color(0xFF8D6E63) to Color(0xFF6D4C41)
            else -> Color(0xFF29B6F6) to Color(0xFF0288D1)
        }
        ActionVisuals(
            btnColor = color,
            shadowColor = shadow,
            btnText = "BUY",
            btnIcon = if (item.id.startsWith("skin")) Icons.Default.Lock else Icons.Default.MonetizationOn
        )
    }
}

private data class ActionVisuals(
    val btnColor: Color,
    val shadowColor: Color,
    val btnText: String,
    val btnIcon: ImageVector
)
