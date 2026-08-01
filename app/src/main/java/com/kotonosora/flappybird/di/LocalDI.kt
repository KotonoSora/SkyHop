package com.kotonosora.flappybird.di

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * CompositionLocal for the [AppContainer].
 * This allows any composable in the tree to access the dependency container without parameter passing.
 */
val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("No AppContainer provided.")
}
