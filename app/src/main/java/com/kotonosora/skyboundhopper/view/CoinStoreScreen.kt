package com.kotonosora.skyboundhopper.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotonosora.skyboundhopper.BuildConfig
import com.kotonosora.skyboundhopper.R
import com.kotonosora.skyboundhopper.billing.BillingStatus
import com.kotonosora.skyboundhopper.view.components.GameButton
import com.kotonosora.skyboundhopper.view.components.CoinBadge
import com.kotonosora.skyboundhopper.view.theme.SkyBlue
import com.kotonosora.skyboundhopper.viewmodel.ShopViewModel
import com.kotonosora.skyboundhopper.model.CoinPackItem

@Composable
fun CoinStoreScreen(
    onClose: () -> Unit,
    viewModel: ShopViewModel = viewModel()
) {
    val coins by viewModel.coins.collectAsState()
    val coinPacks by viewModel.coinPacks.collectAsState()
    val billingStatus by viewModel.billingStatus.collectAsState()
    val activity = LocalContext.current.findActivity()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            CoinStoreHeader(
                onClose = onClose,
                coins = coins
            )

            Spacer(modifier = Modifier.height(32.dp))

            CoinStoreContent(
                modifier = Modifier.weight(1f),
                billingStatus = billingStatus,
                coinPacks = coinPacks,
                onRetry = { viewModel.retryConnection() },
                onBuy = { coinPackItem ->
                    if (coinPackItem.id.startsWith("mock_")) {
                        val amount = when (coinPackItem.id) {
                            "mock_100" -> 100
                            "mock_500" -> 500
                            "mock_1000" -> 1000
                            else -> 0
                        }
                        viewModel.addMockCoins(amount)
                    } else {
                        val details = coinPackItem.productDetails
                        if (details != null) {
                            activity?.let { act ->
                                viewModel.buyCoinPack(act, details)
                            }
                        }
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

@Composable
fun CoinStoreHeader(
    onClose: () -> Unit,
    coins: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "COINS",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black
            )
        }

        CoinBadge(coins = coins)
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
    // If in debug mode and list is empty, inject mock data
    val displayPacks = if (BuildConfig.DEBUG && coinPacks.isEmpty()) {
        listOf(
            CoinPackItem("mock_100", "100 COINS", "$0.99", R.drawable.img_coins_100, null),
            CoinPackItem("mock_500", "500 COINS", "$3.99", R.drawable.img_coins_500, null),
            CoinPackItem("mock_1000", "1000 COINS", "$6.99", R.drawable.img_coins_1000, null)
        )
    } else {
        coinPacks
    }

    val currentStatus = if (BuildConfig.DEBUG && coinPacks.isEmpty()) BillingStatus.CONNECTED else billingStatus

    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        val emptyMessage = when (currentStatus) {
            BillingStatus.EMPTY -> "NO COIN PACKS AVAILABLE AT THE MOMENT."
            BillingStatus.CONNECTED -> if (displayPacks.isEmpty()) "NO COIN PACKS FOUND IN THE STORE." else null
            else -> null
        }

        if (emptyMessage != null) {
            NoPacksView(message = emptyMessage)
            return@Box
        }

        when (currentStatus) {
            BillingStatus.CONNECTING, BillingStatus.IDLE -> {
                ConnectionLoadingView()
            }
            BillingStatus.ERROR -> {
                ConnectionErrorView(onRetry = onRetry)
            }
            BillingStatus.CONNECTED -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(displayPacks) { item ->
                        CoinPackCard(item) {
                            onBuy(item)
                        }
                    }
                }
            }
            BillingStatus.EMPTY -> {}
        }
    }
}

@Composable
fun ConnectionLoadingView() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "CONNECTING TO PLAY STORE...",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ConnectionErrorView(onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "FAILED TO CONNECT TO GOOGLE PLAY. PLEASE CHECK YOUR INTERNET CONNECTION.",
            color = Color.White,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        GameButton(
            text = "RETRY",
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(0.5f),
            height = 48.dp,
            backgroundColor = Color.White,
            shadowColor = Color.LightGray,
            textColor = Color.Black,
            icon = { Icon(Icons.Default.Refresh, contentDescription = null) }
        )
    }
}

@Composable
fun NoPacksView(message: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
        Text(
            message,
            color = Color.White,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )
    }
}


@Composable
fun CoinPackCard(item: CoinPackItem, onBuy: () -> Unit) {
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .size(280.dp, 400.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFFFF9C4), Color(0xFFFFECB3))
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
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = item.name.uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black
            )
            
            Text(
                text = item.price.uppercase(),
                style = MaterialTheme.typography.titleLarge,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            GameButton(
                text = "BUY",
                onClick = onBuy,
                modifier = Modifier.fillMaxWidth(),
                height = 56.dp,
                backgroundColor = Color(0xFFFFCA28),
                shadowColor = Color(0xFFFFA000),
                textColor = Color.Black,
                icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.Black) }
            )
        }
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