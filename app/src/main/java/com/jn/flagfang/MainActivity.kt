package com.jn.flagfang

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
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jn.flagfang.di.AppViewModelFactory
import com.jn.flagfang.presentation.CoinStoreScreen
import com.jn.flagfang.presentation.DailyChallengeScreen
import com.jn.flagfang.presentation.GameScreen
import com.jn.flagfang.presentation.HelpScreen
import com.jn.flagfang.presentation.HomeScreen
import com.jn.flagfang.presentation.LeaderboardScreen
import com.jn.flagfang.presentation.LevelSelectionScreen
import com.jn.flagfang.presentation.SettingsScreen
import com.jn.flagfang.presentation.SkinShopScreen
import com.jn.flagfang.presentation.theme.GameTheme
import com.jn.flagfang.viewmodel.GameViewModel
import com.jn.flagfang.viewmodel.LeaderboardViewModel
import com.jn.flagfang.viewmodel.NavigationViewModel
import com.jn.flagfang.viewmodel.Screen
import com.jn.flagfang.viewmodel.SettingsViewModel
import com.jn.flagfang.viewmodel.ShopViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Draw edge-to-edge (content behind system bars)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Ensure system bars are visible
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.show(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_DEFAULT

        setContent {
            GameTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    // Infrastructure comes from the Application-scoped singletons, not from remember {}.
    val app = LocalContext.current.applicationContext as GameApplication
    val factory = remember(app.container) { AppViewModelFactory(app.container) }
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
                // App is hidden (Home/recent apps/app switch): force-stop game loop and audio.
                gameViewModel.onGameScreenHidden()
                app.container.audioManager.stopBgm()
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

    Scaffold(contentWindowInsets = WindowInsets(0)) { paddingValues ->
        var totalDrag by remember(currentScreen) { mutableFloatStateOf(0f) }
        val swipeBackEnabled = currentScreen != Screen.Home && currentScreen != Screen.Game

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                    launchPurchaseFlow = app.container.billingRepository::launchPurchaseFlow
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
    leaderboardViewModel: LeaderboardViewModel,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
    launchPurchaseFlow: (Activity, String) -> Unit
) {
    when (screen) {
        Screen.Home -> HomeScreen(
            onPlayClick = {
                navigateTo(Screen.LevelSelection)
            },
            onDailyChallengeClick = { navigateTo(Screen.DailyChallenge) },
            onLeaderboardClick = { navigateTo(Screen.Leaderboard) },
            onHelpClick = { navigateTo(Screen.Help) },
            onShopClick = { navigateTo(Screen.Shop) },
            onSettingsClick = { navigateTo(Screen.Settings) },
            onGetCoinsClick = { navigateTo(Screen.CoinStore) },
            onClaimRewardClick = { settingsViewModel.claimReward() },
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
                gameViewModel.startGame(isEndless = false, isDailyChallenge = true)
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
                gameViewModel.startGame(isEndless = true)
                navigateTo(Screen.Game)
            },
            onDailyChallengeClick = { navigateTo(Screen.DailyChallenge) },
            onStoryModeClick = { level ->
                gameViewModel.startGame(level = level)
                navigateTo(Screen.Game)
            }
        )
    }
}

private fun AnimatedContentTransitionScope<Screen>.getScreenTransitionSpec(
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
