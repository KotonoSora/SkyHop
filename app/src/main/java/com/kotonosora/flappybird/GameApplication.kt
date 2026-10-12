package com.kotonosora.flappybird

import android.app.Application
import local.flappybird.ui.di.AppContainer
import local.flappybird.ui.di.AppContainerHost
import local.flappybird.ui.di.DefaultAppContainer

/**
 * Application class that hosts the [AppContainer] for Dependency Injection.
 */
class GameApplication : Application(), AppContainerHost {

    /**
     * Container instance used by the rest of the app to obtain dependencies.
     */
    override lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this, BuildConfig.DEBUG)
    }

}
