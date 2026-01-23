package com.ofekpintok.shutterfly.canvas.features.editor.ui

import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.CanvasPhoto
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.Photo
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class EditorUiState(
    val isLoading: Boolean = false,
    val carouselPhotos: PersistentList<Photo> = persistentListOf(),
    val canvasPhotos: PersistentList<CanvasPhoto> = persistentListOf()
)