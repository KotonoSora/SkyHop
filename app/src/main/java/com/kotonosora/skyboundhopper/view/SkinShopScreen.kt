package com.kotonosora.skyboundhopper.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotonosora.skyboundhopper.view.components.GameButton
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
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.3f))
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
        }

        Text(
            text = "Shop",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black
        )

        CoinDisplay(coins = coins)
    }
}

@Composable
fun CoinDisplay(coins: Int) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.3f),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = Color(0xFFFFD54F),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = coins.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun ShopGridContent(
    skinItems: List<ShopItem>,
    powerUpItems: List<ShopItem>,
    selectedSkinId: String,
    onSkinSelectOrBuy: (ShopItem) -> Unit,
    onGoToCoinStore: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(2) }) {
            SectionTitle(title = "Skins")
        }

        items(skinItems) { item ->
            SkinShopItemCard(
                item = item,
                isSelected = selectedSkinId == item.id,
                onAction = onSkinSelectOrBuy
            )
        }

        item(span = { GridItemSpan(2) }) {
            SectionTitle(title = "Power-ups", modifier = Modifier.padding(top = 24.dp))
        }

        items(powerUpItems) { item ->
            SkinShopItemCard(
                item = item,
                isSelected = false,
                onAction = onSkinSelectOrBuy
            )
        }
        
        item(span = { GridItemSpan(2) }) {
            GameButton(
                text = "GET MORE COINS",
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
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SkinShopItemCard(
    item: ShopItem,
    isSelected: Boolean,
    onAction: (ShopItem) -> Unit
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
            ItemImageContainer(item = item)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black,
                maxLines = 1
            )
            
            ItemStatusText(item = item, isSelected = isSelected)

            Spacer(modifier = Modifier.height(12.dp))

            val (btnColor, shadowColor, btnText, btnIcon) = getActionVisuals(item, isSelected)

            GameButton(
                text = btnText,
                onClick = { onAction(item) },
                modifier = Modifier.fillMaxWidth(),
                height = 44.dp,
                backgroundColor = btnColor,
                shadowColor = shadowColor,
                textColor = Color.White,
                borderWidth = 0.dp,
                icon = { Icon(imageVector = btnIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp)) }
            )
        }
    }
}

@Composable
private fun ItemImageContainer(item: ShopItem) {
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
            modifier = Modifier.fillMaxSize(0.85f),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun ItemStatusText(item: ShopItem, isSelected: Boolean) {
    val (text, color) = when {
        isSelected -> "Active" to Color(0xFF4CAF50)
        item.isUnlocked -> "Owned" to Color(0xFF2196F3)
        else -> "${item.price} Coins" to Color.Gray
    }

    Text(
        text = text,
        fontSize = 14.sp,
        color = color,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun getActionVisuals(item: ShopItem, isSelected: Boolean): ActionVisuals {
    return if (isSelected) {
        ActionVisuals(
            btnColor = Color(0xFF4CAF50),
            shadowColor = Color(0xFF388E3C),
            btnText = "Active",
            btnIcon = Icons.Default.Check
        )
    } else if (item.isUnlocked) {
        ActionVisuals(
            btnColor = Color(0xFF2196F3),
            shadowColor = Color(0xFF1976D2),
            btnText = "Select",
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
            btnText = "Purchase",
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