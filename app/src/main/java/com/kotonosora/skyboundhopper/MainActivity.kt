package com.kotonosora.skyboundhopper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotonosora.skyboundhopper.viewmodel.GameViewModel
import com.kotonosora.skyboundhopper.view.CoinStoreScreen
import com.kotonosora.skyboundhopper.view.GameScreen
import com.kotonosora.skyboundhopper.view.HomeScreen
import com.kotonosora.skyboundhopper.view.SettingsScreen
import com.kotonosora.skyboundhopper.view.SkinShopScreen
import com.kotonosora.skyboundhopper.view.theme.SkyHopTheme

enum class Screen {
    Home, Game, Shop, CoinStore, Settings
}

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
    var currentScreen by remember { mutableStateOf(Screen.Home) }
    var previousScreen by remember { mutableStateOf(Screen.Home) }
    val gameViewModel: GameViewModel = viewModel()

    fun navigateTo(screen: Screen) {
        previousScreen = currentScreen
        currentScreen = screen
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
                    gameViewModel = gameViewModel,
                    navigateTo = ::navigateTo
                )
            }
        }
    }
}

@Composable
private fun ScreenContent(
    screen: Screen,
    previousScreen: Screen,
    gameViewModel: GameViewModel,
    navigateTo: (Screen) -> Unit
) {
    when (screen) {
        Screen.Home -> HomeScreen(
            onPlayClick = { 
                gameViewModel.startGame()
                navigateTo(Screen.Game) 
            },
            onShopClick = { navigateTo(Screen.Shop) },
            onGetCoinsClick = { navigateTo(Screen.CoinStore) },
            onSettingsClick = { navigateTo(Screen.Settings) }
        )
        Screen.Game -> GameScreen(
            viewModel = gameViewModel,
            onBackToHome = { navigateTo(Screen.Home) },
            onGoToShop = { navigateTo(Screen.CoinStore) }
        )
        Screen.Shop -> SkinShopScreen(
            onClose = { navigateTo(Screen.Home) },
            onGoToCoinStore = { navigateTo(Screen.CoinStore) }
        )
        Screen.CoinStore -> CoinStoreScreen(
            onClose = { 
                if (previousScreen == Screen.Game) navigateTo(Screen.Home)
                else navigateTo(previousScreen)
            }
        )
        Screen.Settings -> SettingsScreen(
            onBack = { navigateTo(Screen.Home) }
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
