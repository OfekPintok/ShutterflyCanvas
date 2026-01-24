package com.ofekpintok.shutterfly.canvas.core

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.ofekpintok.shutterfly.canvas.core.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ShutterflyCanvasApplication : Application(), ImageLoaderFactory {
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

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .crossfade(true)
            .build()
}