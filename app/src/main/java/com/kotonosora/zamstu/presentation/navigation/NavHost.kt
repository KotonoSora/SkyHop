package com.kotonosora.zamstu.presentation.navigation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotonosora.zamstu.GameApplication
import com.kotonosora.zamstu.di.AppViewModelFactory
import com.kotonosora.zamstu.di.LocalAppContainer
import com.kotonosora.zamstu.presentation.CoinStoreScreen
import com.kotonosora.zamstu.presentation.DailyChallengeScreen
import com.kotonosora.zamstu.presentation.GameScreen
import com.kotonosora.zamstu.presentation.HelpScreen
import com.kotonosora.zamstu.presentation.HomeScreen
import com.kotonosora.zamstu.presentation.LeaderboardScreen
import com.kotonosora.zamstu.presentation.LevelSelectionScreen
import com.kotonosora.zamstu.presentation.SettingsScreen
import com.kotonosora.zamstu.presentation.SkinShopScreen
import com.kotonosora.zamstu.viewmodel.GameIntent
import com.kotonosora.zamstu.viewmodel.GameViewModel
import com.kotonosora.zamstu.viewmodel.LeaderboardViewModel
import com.kotonosora.zamstu.viewmodel.NavigationViewModel
import com.kotonosora.zamstu.viewmodel.Screen
import com.kotonosora.zamstu.viewmodel.SettingsIntent
import com.kotonosora.zamstu.viewmodel.SettingsViewModel
import com.kotonosora.zamstu.viewmodel.ShopViewModel

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val container = LocalAppContainer.current
    val app = LocalContext.current.applicationContext as GameApplication
    val factory = remember(container) { AppViewModelFactory(container) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val navViewModel: NavigationViewModel = viewModel()
    val currentScreen by navViewModel.currentScreen.collectAsState()
    val previousScreen by navViewModel.previousScreen.collectAsState()
    val latestScreen = rememberUpdatedState(currentScreen)

    val gameViewModel: GameViewModel = viewModel(factory = factory)
    val selectedSkinId by gameViewModel.selectedSkinId.collectAsState()
    val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
    val shopViewModel: ShopViewModel = viewModel(factory = factory)
    val leaderboardViewModel: LeaderboardViewModel = viewModel(factory = factory)

    DisposableEffect(lifecycleOwner, gameViewModel, app) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                gameViewModel.onIntent(GameIntent.ScreenHidden)
                container.audioManager.stopBgm()
            } else if (event == Lifecycle.Event.ON_START && latestScreen.value == Screen.Game) {
                gameViewModel.onIntent(GameIntent.ScreenVisible)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Explicit BackHandler for Game Screen
    if (currentScreen == Screen.Game) {
        BackHandler {
            navViewModel.navigateTo(Screen.Home)
        }
    } else if (currentScreen != Screen.Home) {
        BackHandler {
            navViewModel.navigateBack()
        }
    }

    var totalDrag by remember(currentScreen) { mutableFloatStateOf(0f) }
    val swipeBackEnabled = currentScreen != Screen.Home && currentScreen != Screen.Game

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (swipeBackEnabled) {
                    Modifier.pointerInput(currentScreen) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (totalDrag < -80.dp.toPx()) navViewModel.navigateBack()
                                totalDrag = 0f
                            },
                            onDragCancel = { totalDrag = 0f },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                totalDrag += dragAmount
                            })
                    }
                } else Modifier)) {
        AnimatedContent(
            targetState = currentScreen,
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars),
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
                leaderboardViewModel = leaderboardViewModel,
                navigateTo = navViewModel::navigateTo,
                navigateBack = { navViewModel.navigateBack() },
                launchPurchaseFlow = container.billingRepository::launchPurchaseFlow
            )
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
    leaderboardViewModel: LeaderboardViewModel,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
    launchPurchaseFlow: (Activity, String) -> Unit
) {
    when (screen) {
        Screen.Home -> HomeScreen(
            onPlayClick = { navigateTo(Screen.LevelSelection) },
            onDailyChallengeClick = { navigateTo(Screen.DailyChallenge) },
            onLeaderboardClick = { navigateTo(Screen.Leaderboard) },
            onHelpClick = { navigateTo(Screen.Help) },
            onShopClick = { navigateTo(Screen.Shop) },
            onSettingsClick = { navigateTo(Screen.Settings) },
            onGetCoinsClick = { navigateTo(Screen.CoinStore) },
            onClaimRewardClick = { settingsViewModel.onIntent(SettingsIntent.ClaimReward) },
            coins = shopViewModel.coins.collectAsState().value,
            selectedSkinId = selectedSkinId,
            canClaimDailyReward = settingsViewModel.canClaimDailyReward.collectAsState().value
        )

        Screen.Game -> GameScreen(
            viewModel = gameViewModel,
            onBackToHome = { navigateTo(Screen.Home) },
            onGoToShop = { navigateTo(Screen.CoinStore) }
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
            onGoToShop = { navigateTo(Screen.Shop) },
            viewModel = shopViewModel,
            onLaunchPurchase = launchPurchaseFlow
        )

        Screen.Settings -> SettingsScreen(
            onBack = { navigateTo(Screen.Home) }, settingsViewModel = settingsViewModel
        )

        Screen.Leaderboard -> LeaderboardScreen(
            viewModel = leaderboardViewModel, onBack = { navigateTo(Screen.Home) })

        Screen.DailyChallenge -> DailyChallengeScreen(
            coins = shopViewModel.coins.collectAsState().value,
            onBack = { navigateTo(Screen.Home) },
            onPlay = {
                gameViewModel.onIntent(
                    GameIntent.StartGame(
                        level = 1,
                        isEndless = false,
                        isDailyChallenge = true
                    )
                )
                navigateTo(Screen.Game)
            }
        )

        Screen.Help -> HelpScreen(
            coins = shopViewModel.coins.collectAsState().value,
            onBack = { navigateTo(Screen.Home) }
        )

        Screen.LevelSelection -> LevelSelectionScreen(
            coins = shopViewModel.coins.collectAsState().value,
            onBack = { navigateBack() },
            onEndlessClick = {
                gameViewModel.onIntent(GameIntent.StartGame(isEndless = true))
                navigateTo(Screen.Game)
            },
            onDailyChallengeClick = { navigateTo(Screen.DailyChallenge) },
            onStoryModeClick = { level ->
                gameViewModel.onIntent(GameIntent.StartGame(level = level, isEndless = false))
                navigateTo(Screen.Game)
            }
        )
    }
}

private fun getScreenTransitionSpec(
    targetState: Screen, initialState: Screen
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
