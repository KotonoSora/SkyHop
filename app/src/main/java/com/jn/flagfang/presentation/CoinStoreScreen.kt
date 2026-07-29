package com.jn.flagfang.presentation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jn.flagfang.domain.repository.BillingStatus
import com.jn.flagfang.feature.shop.CoinPackItem
import com.jn.flagfang.feature.shop.ShopData
import com.jn.flagfang.presentation.components.GameBackground
import com.jn.flagfang.presentation.components.GameButton
import com.jn.flagfang.presentation.components.GameHeader
import com.jn.flagfang.presentation.theme.AppTheme
import com.jn.flagfang.presentation.theme.CoinButtonPrimary
import com.jn.flagfang.presentation.theme.CoinButtonShadow
import com.jn.flagfang.presentation.theme.CoinGoldDark
import com.jn.flagfang.presentation.theme.CoinGoldLight
import com.jn.flagfang.viewmodel.ShopIntent
import com.jn.flagfang.viewmodel.ShopViewModel

@Composable
fun CoinStoreScreen(
    onClose: () -> Unit,
    onGoToShop: () -> Unit,
    viewModel: ShopViewModel,
    onLaunchPurchase: (Activity, String) -> Unit,
) {
    val coins by viewModel.coins.collectAsState()
    val coinPacks by viewModel.coinPacks.collectAsState()
    val billingStatus by viewModel.billingStatus.collectAsState()
    val activity = LocalContext.current.findActivity()

    LaunchedEffect(activity, viewModel) {
        val currentActivity = activity ?: return@LaunchedEffect
        viewModel.purchaseLaunchRequests.collect { productId ->
            onLaunchPurchase(currentActivity, productId)
        }
    }

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
                title = "COINS",
                coins = coins,
                onBackClick = onClose
            )

            Spacer(modifier = Modifier.height(16.dp))

            CoinStoreContent(
                modifier = Modifier.weight(1f),
                billingStatus = billingStatus,
                coinPacks = coinPacks,
                onRetry = {
                    viewModel.onIntent(ShopIntent.RetryConnection)
                },
                onBuy = { item ->
                    viewModel.onIntent(ShopIntent.BuyCoinPack(item))
                }
            )

            GameButton(
                text = "GO TO SKIN SHOP",
                onClick = onGoToShop,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                backgroundColor = Color(0xFFAB47BC),
                glowColor = Color(0xFF7B1FA2),
                textColor = Color.White
            )
        }
    }
}

@Composable
fun CoinStoreContent(
    modifier: Modifier = Modifier,
    billingStatus: BillingStatus,
    coinPacks: List<CoinPackItem>,
    onRetry: () -> Unit,
    onBuy: (CoinPackItem) -> Unit
) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        if (billingStatus == BillingStatus.CONNECTING || billingStatus == BillingStatus.IDLE) {
            ConnectionLoadingView()
            return@Box
        }

        val emptyMessage = when (billingStatus) {
            BillingStatus.EMPTY -> "Store currently empty."
            BillingStatus.CONNECTED -> if (coinPacks.isEmpty()) "No items available." else null
            else -> null
        }

        if (emptyMessage != null) {
            NoPacksView(message = emptyMessage)
            return@Box
        }

        when (billingStatus) {
            BillingStatus.ERROR -> {
                ConnectionErrorView(onRetry = onRetry)
            }

            BillingStatus.CONNECTED -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(coinPacks) { item ->
                        CoinPackCard(item) {
                            onBuy(item)
                        }
                    }
                }
            }

            else -> {}
        }
    }
}

@Composable
fun CoinPackCard(item: CoinPackItem, onBuy: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(CoinGoldLight, CoinGoldDark)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize(0.7f),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.name.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = item.description,
                style = TextStyle(fontSize = 10.sp, lineHeight = 12.sp),
                color = Color.Gray,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            GameButton(
                text = item.price,
                onClick = onBuy,
                modifier = Modifier.fillMaxWidth(),
                height = 40.dp,
                backgroundColor = CoinButtonPrimary,
                glowColor = CoinButtonShadow,
                textColor = Color.Black,
                icon = {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                }
            )
        }
    }
}

@Composable
fun ConnectionLoadingView() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        CircularProgressIndicator(color = Color.White)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Connecting to Store...",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ConnectionErrorView(onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Store currently unavailable",
            color = Color.White,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        GameButton(
            text = "TRY AGAIN",
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(0.5f),
            height = 48.dp,
            backgroundColor = Color.White,
            glowColor = Color.LightGray,
            textColor = Color.Black,
            icon = { Icon(Icons.Default.Refresh, contentDescription = null) }
        )
    }
}

@Composable
fun NoPacksView(message: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
        Text(
            text = message,
            color = Color.White,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@Preview(showBackground = true, showSystemUi = true, name = "Coin Store – connected")
@Composable
fun CoinStoreScreenPreview() {
    AppTheme {
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
                GameHeader(title = "COINS", coins = 750, onBackClick = {})
                Spacer(modifier = Modifier.height(16.dp))
                CoinStoreContent(
                    modifier = Modifier.weight(1f),
                    billingStatus = BillingStatus.CONNECTED,
                    coinPacks = ShopData.mockProducts.map { ShopData.mapToCoinPack(it) },
                    onRetry = {},
                    onBuy = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Coin Store – loading")
@Composable
fun CoinStoreLoadingPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            GameBackground(opacity = 0.2f)
            CoinStoreContent(
                billingStatus = BillingStatus.CONNECTING,
                coinPacks = emptyList(),
                onRetry = {},
                onBuy = {}
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Coin Store – error")
@Composable
fun CoinStoreErrorPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            GameBackground(opacity = 0.2f)
            CoinStoreContent(
                billingStatus = BillingStatus.ERROR,
                coinPacks = emptyList(),
                onRetry = {},
                onBuy = {}
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Coin Store – empty")
@Composable
fun CoinStoreEmptyPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            GameBackground(opacity = 0.2f)
            CoinStoreContent(
                billingStatus = BillingStatus.EMPTY,
                coinPacks = emptyList(),
                onRetry = {},
                onBuy = {}
            )
        }
    }
}
