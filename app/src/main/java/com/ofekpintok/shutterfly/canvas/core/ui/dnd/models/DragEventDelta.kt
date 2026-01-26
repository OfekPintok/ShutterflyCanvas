package com.ofekpintok.shutterfly.canvas.core.ui.dnd.models

import androidx.compose.ui.geometry.Offset

data class DragEventDelta(
    val pan: Offset = Offset.Zero,
    val zoom: Float = 1f,
    val rotation: Float = 1f
)