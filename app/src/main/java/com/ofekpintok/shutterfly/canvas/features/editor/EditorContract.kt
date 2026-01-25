package com.ofekpintok.shutterfly.canvas.features.editor

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.CanvasPhoto
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.Photo
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

sealed class EditorEvent {
    data class AddPhotoToCanvas(val photo: Photo, val position: Offset) : EditorEvent()
    data class RemovePhotoFromCanvas(val instanceId: String) : EditorEvent()
    data class MoveCanvasPhoto(val instanceId: String, val newPosition: Offset) : EditorEvent()
}

@Immutable
data class EditorUiState(
    val isLoading: Boolean = false,
    val carouselPhotos: PersistentList<Photo> = persistentListOf(),
    val canvasPhotos: PersistentList<CanvasPhoto> = persistentListOf()
)

sealed class EditorDragItem {
    data class FromCarousel(val photo: Photo) : EditorDragItem()
    data class FromCanvas(val canvasPhoto: CanvasPhoto) : EditorDragItem()
}