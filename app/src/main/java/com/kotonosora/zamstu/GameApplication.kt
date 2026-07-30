package com.kotonosora.zamstu

import android.app.Application
import com.kotonosora.zamstu.di.AppContainer
import com.kotonosora.zamstu.di.DefaultAppContainer

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
