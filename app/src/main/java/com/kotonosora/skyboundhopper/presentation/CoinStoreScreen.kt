package com.kotonosora.skyboundhopper.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.android.billingclient.api.ProductDetails
import com.kotonosora.skyboundhopper.R
import com.kotonosora.skyboundhopper.billing.BillingStatus
import com.kotonosora.skyboundhopper.model.CoinPackItem
import com.kotonosora.skyboundhopper.view.components.CoinBadge
import com.kotonosora.skyboundhopper.view.components.GameButton
import com.kotonosora.skyboundhopper.view.theme.CoinButtonPrimary
import com.kotonosora.skyboundhopper.view.theme.CoinButtonShadow
import com.kotonosora.skyboundhopper.view.theme.CoinGoldDark
import com.kotonosora.skyboundhopper.view.theme.CoinGoldLight
import com.kotonosora.skyboundhopper.view.theme.SkyBlue
import com.kotonosora.skyboundhopper.viewmodel.ShopViewModel

@Composable
fun CoinStoreScreen(
    onClose: () -> Unit,
    viewModel: ShopViewModel,
    onLaunchPurchase: (Activity, ProductDetails) -> Unit,
    onShowAd: (Activity) -> Unit
) {
    val coins by viewModel.coins.collectAsState()
    val coinPacks by viewModel.coinPacks.collectAsState()
    val billingStatus by viewModel.billingStatus.collectAsState()
    val activity = LocalContext.current.findActivity()

    LaunchedEffect(activity, viewModel) {
        val currentActivity = activity ?: return@LaunchedEffect
        viewModel.purchaseLaunchRequests.collect { productDetails ->
            onLaunchPurchase(currentActivity, productDetails)
        }
    }

    LaunchedEffect(activity, viewModel) {
        val currentActivity = activity ?: return@LaunchedEffect
        viewModel.adShowRequests.collect {
            onShowAd(currentActivity)
        }
    }

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
                canWatchAd = viewModel.canWatchAd.collectAsState().value,
                onRetry = { 
                    viewModel.retryConnection() 
                },
                onBuy = viewModel::buyCoinPack,
                onWatchAd = viewModel::watchRewardedAd
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
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.desc_back),
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = stringResource(R.string.title_coins),
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
    canWatchAd: Boolean,
    onRetry: () -> Unit,
    onBuy: (CoinPackItem) -> Unit,
    onWatchAd: () -> Unit
) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        if (billingStatus == BillingStatus.CONNECTING || billingStatus == BillingStatus.IDLE) {
            ConnectionLoadingView()
            return@Box
        }

        val emptyMessage = when (billingStatus) {
            BillingStatus.EMPTY -> stringResource(R.string.msg_no_packs_available)
            BillingStatus.CONNECTED -> if (coinPacks.isEmpty() && !canWatchAd) stringResource(R.string.msg_no_packs_found) else null
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
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        AdRewardCard(
                            canWatch = canWatchAd,
                            onWatch = onWatchAd
                        )
                    }
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
fun AdRewardCard(canWatch: Boolean, onWatch: () -> Unit) {
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .widthIn(max = 280.dp)
            .aspectRatio(0.7f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
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
                            listOf(Color(0xFFE0E0E0), Color(0xFFBDBDBD))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_coins_500),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.8f),
                    contentScale = ContentScale.Fit,
                    alpha = if (canWatch) 1f else 0.5f
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.label_daily_reward).uppercase(),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.msg_ad_reward),
                style = MaterialTheme.typography.titleSmall,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            GameButton(
                text = if (canWatch) stringResource(R.string.btn_watch) else stringResource(R.string.btn_watched),
                onClick = onWatch,
                enabled = canWatch,
                modifier = Modifier.fillMaxWidth(),
                height = 56.dp,
                backgroundColor = CoinButtonPrimary,
                shadowColor = CoinButtonShadow,
                textColor = if (canWatch) Color.Black else Color.DarkGray,
                icon = { 
                    Icon(
                        imageVector = if (canWatch) Icons.Default.PlayArrow else Icons.Default.Check,
                        contentDescription = null,
                        tint = if (canWatch) Color.Black else Color.DarkGray
                    ) 
                }
            )
        }
    }
}

@Composable
fun ConnectionLoadingView() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.msg_connecting_play_store),
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ConnectionErrorView(onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.msg_connection_error),
            color = Color.White,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        GameButton(
            text = stringResource(R.string.btn_retry),
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
            text = message,
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
            .widthIn(max = 280.dp)
            .aspectRatio(0.7f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
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
                            listOf(CoinGoldLight, CoinGoldDark)
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
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = item.price,
                style = MaterialTheme.typography.titleSmall,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            GameButton(
                text = stringResource(R.string.btn_buy),
                onClick = onBuy,
                modifier = Modifier.fillMaxWidth(),
                height = 56.dp,
                backgroundColor = CoinButtonPrimary,
                shadowColor = CoinButtonShadow,
                textColor = Color.Black,
                icon = { 
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = Color.Black
                    ) 
                }
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
