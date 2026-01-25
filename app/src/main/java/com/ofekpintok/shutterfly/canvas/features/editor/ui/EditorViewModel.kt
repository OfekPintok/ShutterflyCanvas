package com.ofekpintok.shutterfly.canvas.features.editor.ui

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ofekpintok.shutterfly.canvas.features.editor.EditorEvent
import com.ofekpintok.shutterfly.canvas.features.editor.EditorUiState
import com.ofekpintok.shutterfly.canvas.features.editor.domain.FetchPhotosUseCase
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.CanvasPhoto
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.CanvasPhotoAttributes
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.Photo
import com.ofekpintok.shutterfly.canvas.features.editor.ui.config.EditorConfig
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

private const val TAG = "EditorViewModel"

class EditorViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val fetchPhotosUseCase: FetchPhotosUseCase
) : ViewModel() {

    companion object {
        private const val KEY_CANVAS_PHOTOS = "canvasPhotos"
    }

    private val galleryState = flow {
        emit(InternalGalleryState(isLoading = true))
        simulateNetworkDelay()
        try {
            val photos = fetchPhotosUseCase(shuffled = EditorConfig.ENABLE_SHUFFLE_ON_LOAD)
            emit(InternalGalleryState(isLoading = false, photos = photos))
        } catch (e: Exception) {
            emit(InternalGalleryState(isLoading = false, photos = emptyList()))
            Log.d(TAG, "Error fetching photos", e)
        }
    }

    private val canvasPhotosState = savedStateHandle.getStateFlow(
        key = KEY_CANVAS_PHOTOS,
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
        started = SharingStarted.Lazily,
        initialValue = EditorUiState(isLoading = true)
    )

    fun onEvent(event: EditorEvent) = when (event) {
        is EditorEvent.AddPhotoToCanvas -> addPhotoToCanvas(event.photo, event.position)
        is EditorEvent.RemovePhotoFromCanvas -> removePhotoFromCanvas(event.instanceId)
        is EditorEvent.MoveCanvasPhoto -> moveCanvasPhoto(event.instanceId, event.newPosition)
    }

    private fun addPhotoToCanvas(photo: Photo, position: Offset) {
        val canvasPhoto = CanvasPhoto(
            sourceId = photo.id,
            url = photo.url,
            attributes = CanvasPhotoAttributes(
                x = position.x,
                y = position.y
            )
        )
        updateCanvasPhotos { it + canvasPhoto }
    }

    private fun removePhotoFromCanvas(instanceId: String) {
        val canvasPhoto = canvasPhotosState.value.firstOrNull { it.id == instanceId } ?: return
        updateCanvasPhotos { it - canvasPhoto }
    }

    private fun moveCanvasPhoto(
        instanceId: String,
        newPosition: Offset
    ) {
        updateCanvasPhotos { photos ->
            val photoToMove = photos.find { it.id == instanceId } ?: return@updateCanvasPhotos photos
            val updatedPhoto = photoToMove.copy(
                attributes = photoToMove.attributes.copy(
                    x = newPosition.x,
                    y = newPosition.y
                )
            )

            photos.minus(photoToMove).plus(updatedPhoto)
        }
    }

    private fun updateCanvasPhotos(transform: (List<CanvasPhoto>) -> List<CanvasPhoto>) {
        savedStateHandle[KEY_CANVAS_PHOTOS] = transform(canvasPhotosState.value)
    }

    private suspend fun simulateNetworkDelay() {
        val delayMs = EditorConfig.SIMULATED_LATENCY_MS
        if (delayMs > 0) delay(delayMs)
    }
}

private data class InternalGalleryState(
    val isLoading: Boolean = false,
    val photos: List<Photo> = emptyList()
)