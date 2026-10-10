package local.flappybird.ui.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import local.flappybird.ui.di.AppContainerHost
import local.flappybird.ui.di.LocalAppContainer
import local.flappybird.ui.presentation.navigation.AppNavHost
import local.core.ui.theme.AppTheme

@Composable
fun MainApp() {
    val app = LocalContext.current.applicationContext as AppContainerHost

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
