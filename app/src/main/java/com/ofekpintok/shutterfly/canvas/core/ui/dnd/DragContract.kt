package com.ofekpintok.shutterfly.canvas.core.ui.dnd

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

data class DragState<T>(
    val isDragging: Boolean = false,
    val data: T? = null,
    val currentPosition: Offset = Offset.Zero,
    val size: Size = Size.Zero
)