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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.billingclient.api.ProductDetails
import com.jn.flagfang.R
import com.jn.flagfang.billing.BillingStatus
import com.jn.flagfang.feature.shop.CentPackItem
import com.jn.flagfang.feature.shop.ShopData
import com.jn.flagfang.presentation.components.CoinBadge
import com.jn.flagfang.presentation.components.GameButton
import com.jn.flagfang.presentation.theme.CoinButtonPrimary
import com.jn.flagfang.presentation.theme.CoinButtonShadow
import com.jn.flagfang.presentation.theme.CoinGoldDark
import com.jn.flagfang.presentation.theme.CoinGoldLight
import com.jn.flagfang.presentation.theme.GameTheme
import com.jn.flagfang.presentation.theme.SkyBlue
import com.jn.flagfang.viewmodel.ShopViewModel

@Composable
fun CentStoreScreen(
    onClose: () -> Unit,
    viewModel: ShopViewModel,
    onLaunchPurchase: (Activity, ProductDetails) -> Unit
) {
    val cents by viewModel.cents.collectAsState()
    val centPacks by viewModel.centPacks.collectAsState()
    val billingStatus by viewModel.billingStatus.collectAsState()
    val activity = LocalContext.current.findActivity()

    LaunchedEffect(activity, viewModel) {
        val currentActivity = activity ?: return@LaunchedEffect
        viewModel.purchaseLaunchRequests.collect { productDetails ->
            onLaunchPurchase(currentActivity, productDetails)
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
                .safeContentPadding()
        ) {
            CentStoreHeader(
                onClose = onClose,
                cents = cents
            )

            Spacer(modifier = Modifier.height(32.dp))

            CentStoreContent(
                modifier = Modifier.weight(1f),
                billingStatus = billingStatus,
                centPacks = centPacks,
                onRetry = {
                    viewModel.retryConnection()
                },
                onBuy = viewModel::buyCoinPack
            )

            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

@Composable
fun CentStoreHeader(
    onClose: () -> Unit,
    cents: Int
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
                text = stringResource(R.string.title_cents),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )
        }

        CoinBadge(cents = cents)
    }
}

@Composable
fun CentStoreContent(
    modifier: Modifier = Modifier,
    billingStatus: BillingStatus,
    centPacks: List<CentPackItem>,
    onRetry: () -> Unit,
    onBuy: (CentPackItem) -> Unit
) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        if (billingStatus == BillingStatus.CONNECTING || billingStatus == BillingStatus.IDLE) {
            ConnectionLoadingView()
            return@Box
        }

        val emptyMessage = when (billingStatus) {
            BillingStatus.EMPTY -> stringResource(R.string.msg_no_packs_available)
            BillingStatus.CONNECTED -> if (centPacks.isEmpty()) stringResource(R.string.msg_no_packs_found) else null
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
                    items(centPacks) { item ->
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
fun ConnectionLoadingView() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        CircularProgressIndicator(color = Color.White)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.msg_connecting_play_store),
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
fun CoinPackCard(item: CentPackItem, onBuy: () -> Unit) {
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

@Preview(showBackground = true, showSystemUi = true, name = "Coin Store – connected")
@Composable
private fun CentStoreScreenPreview() {
    GameTheme(dynamicColor = false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SkyBlue)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeContentPadding()
            ) {
                CentStoreHeader(onClose = {}, cents = 750)
                Spacer(modifier = Modifier.height(32.dp))
                CentStoreContent(
                    modifier = Modifier.weight(1f),
                    billingStatus = BillingStatus.CONNECTED,
                    centPacks = ShopData.getDebugCoinPacks(),
                    onRetry = {},
                    onBuy = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Coin Store – loading")
@Composable
private fun CentStoreLoadingPreview() {
    GameTheme(dynamicColor = false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SkyBlue)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeContentPadding()
            ) {
                CentStoreHeader(onClose = {}, cents = 0)
                Spacer(modifier = Modifier.height(32.dp))
                CentStoreContent(
                    modifier = Modifier.weight(1f),
                    billingStatus = BillingStatus.CONNECTING,
                    centPacks = emptyList(),
                    onRetry = {},
                    onBuy = {}
                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, name = "Coin Store – loading")
@Composable
private fun CentStoreErrorPreview() {
    GameTheme(dynamicColor = false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SkyBlue)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeContentPadding()
            ) {
                CentStoreHeader(onClose = {}, cents = 0)
                Spacer(modifier = Modifier.height(32.dp))
                CentStoreContent(
                    modifier = Modifier.weight(1f),
                    billingStatus = BillingStatus.ERROR,
                    centPacks = emptyList(),
                    onRetry = {},
                    onBuy = {}
                )
            }
        }
    }
}
