package com.ofekpintok.shutterfly.canvas.core.ui.dnd.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size

@Composable
fun rememberDragIntersection(
    isDragging: Boolean,
    dragPosition: Offset,
    dragItemSize: Size,
    targetBounds: Rect
): Boolean {
    return remember(isDragging, dragPosition, targetBounds, dragItemSize) {
        derivedStateOf {
            if (!isDragging || targetBounds.isEmpty) return@derivedStateOf false

            val dragRect = Rect(
                offset = dragPosition,
                size = dragItemSize
            )

            targetBounds.overlaps(dragRect)
        }
    }.value
}