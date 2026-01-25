package com.ofekpintok.shutterfly.canvas.core.ui.dnd

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

private const val TAG = "DragOverlayContainer"

interface DragScope<T> {
    val dragState: DragState<T>
    fun onDragStart(data: T, offset: Offset, size: Size)
    fun onDrag(offset: Offset)
    fun onDragEnd()
}

@Composable
fun <T> DragOverlayContainer(
    onDrop: (T, Offset, Size) -> Unit,
    dragOverlayContent: @Composable (T, Offset) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable DragScope<T>.() -> Unit
) {
    var dragState by remember { mutableStateOf(DragState<T>()) }

    val dragScope = remember {
        object : DragScope<T> {
            override val dragState: DragState<T>
                get() = dragState

            override fun onDragStart(data: T, offset: Offset, size: Size) {
                Log.d(TAG, "onDragStart: data=$data, offset=$offset")
                dragState = DragState(isDragging = true, data = data, currentPosition = offset, size = size)
            }

            override fun onDrag(offset: Offset) {
                val newPosition = dragState.currentPosition + offset
                Log.d(TAG, "onDrag: delta=$offset, newPosition=$newPosition")
                dragState = dragState.copy(currentPosition = newPosition)
            }

            override fun onDragEnd() {
                Log.d(TAG, "onDragEnd: data=${dragState.data}, finalPosition=${dragState.currentPosition}")
                dragState.data?.let { onDrop(it, dragState.currentPosition, dragState.size) }
                dragState = DragState()
            }
        }
    }

    Box(modifier = modifier) {
        dragScope.content()
    }

    if (dragState.isDragging) {
        dragState.data?.let { dragOverlayContent(it, dragState.currentPosition) }
    }
}