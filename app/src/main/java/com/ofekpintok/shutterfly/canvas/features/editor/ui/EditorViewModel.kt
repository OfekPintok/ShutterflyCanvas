package com.ofekpintok.shutterfly.canvas.features.editor.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ofekpintok.shutterfly.canvas.features.editor.EditorConfig
import com.ofekpintok.shutterfly.canvas.features.editor.domain.FetchPhotosUseCase
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.CanvasPhoto
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.Photo
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class EditorViewModel(
    savedStateHandle: SavedStateHandle,
    private val fetchPhotosUseCase: FetchPhotosUseCase
) : ViewModel() {

    private val galleryState = flow {
        emit(InternalGalleryState(isLoading = true))
        simulateNetworkDelay()
        val photos = fetchPhotosUseCase(shuffled = EditorConfig.ENABLE_SHUFFLE_ON_LOAD)
        emit(InternalGalleryState(isLoading = false, photos = photos))
    }

    private val canvasPhotosState = savedStateHandle.getStateFlow(
        key = "canvasPhotos",
        initialValue = emptyList<CanvasPhoto>()
    )

    val uiState = combine(
        galleryState,
        canvasPhotosState
    ) { galleryPhotosState, canvasPhotos ->
        EditorUiState(
            isLoading = galleryPhotosState.isLoading,
            carouselPhotos = galleryPhotosState.photos.toPersistentList(),
            canvasPhotos = canvasPhotos.toPersistentList()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EditorUiState(isLoading = true)
    )

    private suspend fun simulateNetworkDelay() {
        val delayMs = EditorConfig.SIMULATED_LATENCY_MS
        if (delayMs > 0) delay(delayMs)
    }
}

private data class InternalGalleryState(
    val isLoading: Boolean = false,
    val photos: List<Photo> = emptyList()
)