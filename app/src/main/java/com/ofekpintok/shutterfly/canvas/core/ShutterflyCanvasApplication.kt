package com.ofekpintok.shutterfly.canvas.core

import android.app.Application
import com.ofekpintok.shutterfly.canvas.core.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ShutterflyCanvasApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setupDi()
    }

    private fun setupDi() {
        startKoin {
            androidContext(this@ShutterflyCanvasApplication)
            modules(appModules)
        }
    }
}