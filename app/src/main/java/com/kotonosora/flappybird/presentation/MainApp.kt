package com.kotonosora.flappybird.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kotonosora.flappybird.GameApplication
import com.kotonosora.flappybird.di.LocalAppContainer
import com.kotonosora.flappybird.presentation.navigation.AppNavHost
import com.kotonosora.flappybird.presentation.theme.AppTheme

@Composable
fun MainApp() {
    val app = LocalContext.current.applicationContext as GameApplication

    CompositionLocalProvider(LocalAppContainer provides app.container) {
        AppTheme {
            Scaffold(
                contentWindowInsets = WindowInsets(0),
                modifier = Modifier.fillMaxSize()
            ) { paddingValues ->
                AppNavHost(modifier = Modifier.padding(paddingValues))
            }
        }
    }
}
