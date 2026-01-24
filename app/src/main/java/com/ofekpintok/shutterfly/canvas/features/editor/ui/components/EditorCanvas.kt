package com.ofekpintok.shutterfly.canvas.features.editor.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.CanvasPhoto
import kotlinx.collections.immutable.PersistentList

@Composable
fun EditorCanvas(
    modifier: Modifier = Modifier,
    canvasPhotos: PersistentList<CanvasPhoto>
) {
    Box(modifier = modifier.fillMaxSize())
}