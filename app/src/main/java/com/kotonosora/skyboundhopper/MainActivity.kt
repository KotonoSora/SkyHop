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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotonosora.skyboundhopper.game.GameViewModel
import com.kotonosora.skyboundhopper.ui.CoinStoreScreen
import com.kotonosora.skyboundhopper.ui.GameScreen
import com.kotonosora.skyboundhopper.ui.HomeScreen
import com.kotonosora.skyboundhopper.ui.SettingsScreen
import com.kotonosora.skyboundhopper.ui.SkinShopScreen
import com.kotonosora.skyboundhopper.ui.theme.SkyHopTheme

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
    val gameViewModel: GameViewModel = viewModel()

    Scaffold(
        bottomBar = {
            if (currentScreen != Screen.Game) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ) {
                    NavigationBarItem(
                        selected = currentScreen == Screen.Home,
                        onClick = { currentScreen = Screen.Home },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.Shop || currentScreen == Screen.CoinStore,
                        onClick = { currentScreen = Screen.Shop },
                        icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Shop") },
                        label = { Text("Shop") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.Game,
                        onClick = { 
                            gameViewModel.startGame()
                            currentScreen = Screen.Game 
                        },
                        icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Play") },
                        label = { Text("Play") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.Settings,
                        onClick = { currentScreen = Screen.Settings },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = currentScreen,
                modifier = Modifier.padding(if (currentScreen == Screen.Game) PaddingValues(0.dp) else innerPadding),
                transitionSpec = {
                    if (targetState == Screen.Game) {
                        (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                    } else if (initialState == Screen.Game) {
                        (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                    } else {
                        fadeIn(animationSpec = tween(500)).togetherWith(fadeOut(animationSpec = tween(500)))
                    }
                },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    Screen.Home -> HomeScreen(
                        onPlayClick = { 
                            gameViewModel.startGame()
                            currentScreen = Screen.Game 
                        },
                        onShopClick = { currentScreen = Screen.Shop }
                    )
                    Screen.Game -> GameScreen(
                        viewModel = gameViewModel,
                        onBackToHome = { currentScreen = Screen.Home },
                        onGoToShop = { currentScreen = Screen.CoinStore }
                    )
                    Screen.Shop -> SkinShopScreen(
                        onClose = { currentScreen = Screen.Home },
                        onGoToCoinStore = { currentScreen = Screen.CoinStore }
                    )
                    Screen.CoinStore -> CoinStoreScreen(
                        onClose = { currentScreen = Screen.Shop }
                    )
                    Screen.Settings -> SettingsScreen()
                }
            }
        }
    }
}
