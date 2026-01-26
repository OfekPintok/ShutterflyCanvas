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
import com.ofekpintok.shutterfly.canvas.core.ui.dnd.models.DragEventDelta

private const val TAG = "DragOverlayContainer"

interface DragScope<T> {
    val dragState: DragState<T>
    fun onDragStart(
        data: T,
        offset: Offset,
        size: Size,
        initialScale: Float = 1f,
        initialRotation: Float = 0f
    ): Boolean

    fun onDrag(delta: DragEventDelta)
    fun onDragEnd()
}

@Composable
fun <T> DragOverlayContainer(
    onDrop: (T, DragState<T>) -> Unit,
    dragOverlayContent: @Composable (T, DragState<T>) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable DragScope<T>.() -> Unit
) {
    var dragState by remember { mutableStateOf(DragState<T>()) }

    val dragScope = remember {
        object : DragScope<T> {
            override val dragState: DragState<T>
                get() = dragState

            override fun onDragStart(
                data: T,
                offset: Offset,
                size: Size,
                initialScale: Float,
                initialRotation: Float
            ): Boolean {
                if (dragState.isDragging) {
                    return false
                }

                Log.d(TAG, "onDragStart: data=$data, offset=$offset")
                dragState = DragState(
                    isDragging = true,
                    data = data,
                    currentPosition = offset,
                    size = size,
                    scale = initialScale,
                    rotation = initialRotation
                )

                return true
            }

            override fun onDrag(delta: DragEventDelta) {
                val newPosition = dragState.currentPosition + delta.pan
                val newScale = (dragState.scale * delta.zoom).coerceIn(0.1f, 5f)
                val newRotation = dragState.rotation + delta.rotation
                Log.d(
                    TAG,
                    "onDrag: delta=${delta}, newPosition=$newPosition, newScale=$newScale, newRotation=$newRotation"
                )
                dragState = dragState.copy(
                    currentPosition = newPosition,
                    scale = newScale,
                    rotation = newRotation
                )
            }

            override fun onDragEnd() {
                Log.d(
                    TAG,
                    "onDragEnd: data=${dragState.data}, finalPosition=${dragState.currentPosition}"
                )
                dragState.data?.let { onDrop(it, dragState) }
                dragState = DragState()
            }
        }
    }

    Box(modifier = modifier) {
        dragScope.content()

        if (dragState.isDragging) {
            dragState.data?.let { item ->
                dragOverlayContent(item, dragState)
            }
        }
    }
}