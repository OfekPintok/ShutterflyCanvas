package com.ofekpintok.shutterfly.canvas.features.editor.data.repository

import com.ofekpintok.shutterfly.canvas.features.editor.data.datasource.LocalAssetsDataSource
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.Photo
import com.ofekpintok.shutterfly.canvas.features.editor.domain.repository.PhotosRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhotosRepositoryImpl(
    private val packageName: String,
    private val localAssetsDataSource: LocalAssetsDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : PhotosRepository {
    override suspend fun fetchPhotos(): List<Photo> = withContext(dispatcher) {
        localAssetsDataSource.getAssetResources().map { resId ->
            Photo(url = "android.resource://$packageName/$resId")
        }
    }
}