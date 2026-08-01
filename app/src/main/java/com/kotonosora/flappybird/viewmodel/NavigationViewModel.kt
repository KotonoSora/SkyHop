package com.kotonosora.flappybird.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class Screen {
    Home, Game, Shop, CoinStore, Settings, Leaderboard, DailyChallenge, Help, LevelSelection
}

/**
 * Owns the app-level navigation state so that screen transitions are not
 * managed by mutableStateOf inside a Composable.
 *
 * Maintains a full back stack so [navigateBack] can always pop to the
 * correct previous destination regardless of how many screens deep the
 * user has navigated.
 */
class NavigationViewModel : ViewModel() {

    private val backStack: ArrayDeque<Screen> = ArrayDeque<Screen>().also { it.add(Screen.Home) }

    private val _currentScreen = MutableStateFlow(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _previousScreen = MutableStateFlow(Screen.Home)
    val previousScreen: StateFlow<Screen> = _previousScreen.asStateFlow()

    /** True when there is at least one screen to pop back to. */
    val canGoBack: Boolean get() = backStack.size > 1

    fun navigateTo(screen: Screen) {
        _previousScreen.value = _currentScreen.value
        backStack.addLast(screen)
        _currentScreen.value = screen
    }

    /** Pops the current screen and returns to the previous one.
     *  Returns true if a back navigation was performed. */
    fun navigateBack(): Boolean {
        if (backStack.size <= 1) return false
        backStack.removeLast()
        val destination = backStack.last()
        _previousScreen.value = _currentScreen.value
        _currentScreen.value = destination
        return true
    }
}
