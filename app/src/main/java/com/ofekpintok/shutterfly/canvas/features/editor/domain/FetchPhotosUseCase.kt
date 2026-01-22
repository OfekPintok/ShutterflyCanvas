package com.ofekpintok.shutterfly.canvas.features.editor.domain

import com.ofekpintok.shutterfly.canvas.features.editor.domain.repository.PhotosRepository

class FetchPhotosUseCase(private val photosRepository: PhotosRepository) {
    suspend operator fun invoke(shuffled: Boolean = false) = photosRepository.fetchPhotos().run {
        if (shuffled) shuffled() else this
    }
}