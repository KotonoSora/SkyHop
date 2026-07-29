package com.jn.flagfang

import android.app.Application
import com.jn.flagfang.di.AppContainer
import com.jn.flagfang.di.DefaultAppContainer

/**
 * Application class that hosts the [AppContainer] for Dependency Injection.
 */
class GameApplication : Application() {

    /**
     * Container instance used by the rest of the app to obtain dependencies.
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }

}
