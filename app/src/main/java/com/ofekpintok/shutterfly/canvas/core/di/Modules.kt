package com.ofekpintok.shutterfly.canvas.core.di

import com.ofekpintok.shutterfly.canvas.features.editor.data.datasource.LocalAssetsDataSource
import com.ofekpintok.shutterfly.canvas.features.editor.data.repository.PhotosRepositoryImpl
import com.ofekpintok.shutterfly.canvas.features.editor.domain.FetchPhotosUseCase
import com.ofekpintok.shutterfly.canvas.features.editor.domain.repository.PhotosRepository
import com.ofekpintok.shutterfly.canvas.features.editor.ui.EditorViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModules = module {
    viewModelOf(::EditorViewModel)

    factoryOf(::FetchPhotosUseCase)

    factory<PhotosRepository> {
        PhotosRepositoryImpl(
            packageName = androidContext().packageName,
            localAssetsDataSource = get()
        )
    }

    factoryOf(::LocalAssetsDataSource)
}