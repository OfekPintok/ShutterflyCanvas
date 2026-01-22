package com.ofekpintok.shutterfly.canvas.features.editor.domain.repository

import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.Photo

interface PhotosRepository {
    suspend fun fetchPhotos(): List<Photo>
}