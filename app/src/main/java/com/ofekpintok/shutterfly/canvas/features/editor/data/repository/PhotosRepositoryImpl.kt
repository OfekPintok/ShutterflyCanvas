package com.ofekpintok.shutterfly.canvas.features.editor.data.repository

import com.ofekpintok.shutterfly.canvas.features.editor.data.datasource.LocalAssetsDataSource
import com.ofekpintok.shutterfly.canvas.features.editor.domain.repository.PhotosRepository

class PhotosRepositoryImpl(val localAssetsDataSource: LocalAssetsDataSource) : PhotosRepository {
}