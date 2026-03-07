package com.kotonosora.skyboundhopper.ui

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotonosora.skyboundhopper.ui.components.GameButton
import com.kotonosora.skyboundhopper.ui.theme.SkyBlue

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
            // Header
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

            // Shop Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Skins Section
                item(span = { GridItemSpan(2) }) {
                    Text(
                        text = "Skins",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(skinItems) { item ->
                    ShopItemCard(
                        item = item,
                        isSelected = selectedSkinId == item.id,
                        onAction = {
                            if (item.isUnlocked) viewModel.selectSkin(item.id)
                            else viewModel.buyItem(item)
                        }
                    )
                }

                // Power-ups Section
                item(span = { GridItemSpan(2) }) {
                    Text(
                        text = "Power-ups",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                    )
                }

                items(powerUpItems) { item ->
                    ShopItemCard(
                        item = item,
                        isSelected = false,
                        onAction = { viewModel.buyItem(item) }
                    )
                }
                
                // Get More Coins Button at the bottom of the list
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
    }
}

@Composable
fun ShopItemCard(
    item: ShopItem,
    isSelected: Boolean,
    onAction: () -> Unit
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black,
                maxLines = 1
            )
            
            Text(
                text = if (item.isUnlocked) "Owned" else "${item.price} Coins",
                fontSize = 14.sp,
                color = if (item.isUnlocked) Color(0xFF4CAF50) else Color.Gray,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            val btnColor = when {
                isSelected -> Color(0xFF4CAF50)
                item.id.startsWith("skin") && item.isUnlocked -> Color(0xFF2196F3)
                item.id == "skin_space" -> Color(0xFF5C6BC0)
                item.id == "skin_golden" -> Color(0xFFFFA726)
                item.id == "skin_steampunk" -> Color(0xFF8D6E63)
                else -> Color(0xFF29B6F6)
            }

            // A helper for shadows
            val shadowColor = when {
                isSelected -> Color(0xFF388E3C)
                item.id.startsWith("skin") && item.isUnlocked -> Color(0xFF1976D2)
                item.id == "skin_space" -> Color(0xFF3F51B5)
                item.id == "skin_golden" -> Color(0xFFF57C00)
                item.id == "skin_steampunk" -> Color(0xFF6D4C41)
                else -> Color(0xFF0288D1)
            }

            GameButton(
                text = when {
                    isSelected -> "Active"
                    item.isUnlocked -> "Select"
                    else -> "Purchase"
                },
                onClick = onAction,
                modifier = Modifier.fillMaxWidth(),
                height = 44.dp,
                backgroundColor = btnColor,
                shadowColor = shadowColor,
                textColor = Color.White,
                borderWidth = 0.dp,
                icon = {
                    Icon(
                        imageVector = when {
                            isSelected -> Icons.Default.Check
                            item.isUnlocked -> Icons.Default.Check
                            else -> if (item.id.startsWith("skin")) Icons.Default.Lock else Icons.Default.MonetizationOn
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }
    }
}
