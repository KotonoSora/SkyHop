package com.kotonosora.skyboundhopper

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.billingclient.api.ProductDetails
import com.kotonosora.skyboundhopper.view.CoinStoreScreen
import com.kotonosora.skyboundhopper.view.GameScreen
import com.kotonosora.skyboundhopper.view.HomeScreen
import com.kotonosora.skyboundhopper.view.SettingsScreen
import com.kotonosora.skyboundhopper.view.SkinShopScreen
import com.kotonosora.skyboundhopper.view.theme.SkyHopTheme
import com.kotonosora.skyboundhopper.viewmodel.GameViewModel
import com.kotonosora.skyboundhopper.viewmodel.GameViewModelFactory
import com.kotonosora.skyboundhopper.viewmodel.NavigationViewModel
import com.kotonosora.skyboundhopper.viewmodel.Screen
import com.kotonosora.skyboundhopper.viewmodel.SettingsViewModel
import com.kotonosora.skyboundhopper.viewmodel.SettingsViewModelFactory
import com.kotonosora.skyboundhopper.viewmodel.ShopViewModel
import com.kotonosora.skyboundhopper.viewmodel.ShopViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SkyHopTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    // Infrastructure comes from the Application-scoped singletons, not from remember {}.
    val app = LocalContext.current.applicationContext as SkyHopApplication
    val lifecycleOwner = LocalLifecycleOwner.current

    val navViewModel: NavigationViewModel = viewModel()
    val currentScreen by navViewModel.currentScreen.collectAsState()
    val previousScreen by navViewModel.previousScreen.collectAsState()
    val latestScreen = rememberUpdatedState(currentScreen)

    val gameViewModel: GameViewModel = viewModel(
        factory = GameViewModelFactory(
            scoreRepository = app.scoreRepository,
            settingsRepository = app.settingsRepository,
            audioManager = app.audioManager
        )
    )
    val selectedSkinId by gameViewModel.selectedSkinId.collectAsState()

    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            settingsRepository = app.settingsRepository,
            versionName = app.appVersionName
        )
    )

    val shopViewModel: ShopViewModel = viewModel(
        factory = ShopViewModelFactory(
            settingsRepository = app.settingsRepository,
            billingManager = app.billingManager,
            adRewardRepository = app.adRewardRepository,
            adManager = app.adManager
        )
    )

    DisposableEffect(lifecycleOwner, gameViewModel, app) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                // App is hidden (Home/recent apps/app switch): force-stop game loop and audio.
                gameViewModel.onGameScreenHidden()
                app.audioManager.stopBgm()
            } else if (event == Lifecycle.Event.ON_START && latestScreen.value == Screen.Game) {
                // App returns to foreground while user is on Game screen: resume gameplay/audio.
                gameViewModel.onGameScreenVisible()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(currentScreen) {
        if (currentScreen != Screen.Game) {
            gameViewModel.onGameScreenHidden()
        } else {
            gameViewModel.onGameScreenVisible()
        }
    }

    Scaffold { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = currentScreen,
                modifier = Modifier.padding(if (currentScreen == Screen.Game) PaddingValues(0.dp) else innerPadding),
                transitionSpec = { getScreenTransitionSpec(targetState, initialState) },
                label = "ScreenTransition"
            ) { screen ->
                ScreenContent(
                    screen = screen,
                    previousScreen = previousScreen,
                    selectedSkinId = selectedSkinId,
                    gameViewModel = gameViewModel,
                    settingsViewModel = settingsViewModel,
                    shopViewModel = shopViewModel,
                    navigateTo = navViewModel::navigateTo,
                    launchPurchaseFlow = app.billingManager::launchPurchaseFlow,
                    showRewardedAd = app.adManager::showRewardedAd,
                    onAdRewardEarned = shopViewModel::onAdRewardEarned
                )
            }
        }
    }
}

@Composable
private fun ScreenContent(
    screen: Screen,
    previousScreen: Screen,
    selectedSkinId: String,
    gameViewModel: GameViewModel,
    settingsViewModel: SettingsViewModel,
    shopViewModel: ShopViewModel,
    navigateTo: (Screen) -> Unit,
    launchPurchaseFlow: (Activity, ProductDetails) -> Unit,
    showRewardedAd: (Activity, () -> Unit) -> Unit,
    onAdRewardEarned: () -> Unit
) {
    when (screen) {
        Screen.Home -> HomeScreen(
            onPlayClick = { 
                gameViewModel.startGame()
                navigateTo(Screen.Game) 
            },
            onShopClick = { navigateTo(Screen.Shop) },
            onGetCoinsClick = { navigateTo(Screen.CoinStore) },
            onSettingsClick = { navigateTo(Screen.Settings) },
            selectedSkinId = selectedSkinId
        )
        Screen.Game -> GameScreen(
            viewModel = gameViewModel,
            onBackToHome = { navigateTo(Screen.Home) }
        )
        Screen.Shop -> SkinShopScreen(
            onClose = { navigateTo(Screen.Home) },
            onGoToCoinStore = { navigateTo(Screen.CoinStore) },
            viewModel = shopViewModel
        )
        Screen.CoinStore -> CoinStoreScreen(
            onClose = { 
                if (previousScreen == Screen.Game) navigateTo(Screen.Home)
                else navigateTo(previousScreen)
            },
            viewModel = shopViewModel,
            onLaunchPurchase = launchPurchaseFlow,
            onShowAd = { activity ->
                showRewardedAd(activity, onAdRewardEarned)
            }
        )
        Screen.Settings -> SettingsScreen(
            onBack = { navigateTo(Screen.Home) },
            settingsViewModel = settingsViewModel
        )
    }
}

private fun AnimatedContentTransitionScope<Screen>.getScreenTransitionSpec(
    targetState: Screen,
    initialState: Screen
): ContentTransform {
    return when {
        targetState == Screen.Game -> {
            (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
        }
        initialState == Screen.Game -> {
            (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
        }
        else -> {
            fadeIn(animationSpec = tween(500)).togetherWith(fadeOut(animationSpec = tween(500)))
        }
    }
}
