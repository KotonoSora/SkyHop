package com.kotonosora.skyboundhopper.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class Screen {
    Home, Game, Shop, CoinStore, Settings
}

/**
 * Owns the app-level navigation state so that screen transitions are not
 * managed by mutableStateOf inside a Composable.
 */
class NavigationViewModel : ViewModel() {

    private val _currentScreen = MutableStateFlow(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _previousScreen = MutableStateFlow(Screen.Home)
    val previousScreen: StateFlow<Screen> = _previousScreen.asStateFlow()

    fun navigateTo(screen: Screen) {
        _previousScreen.value = _currentScreen.value
        _currentScreen.value = screen
    }
}
