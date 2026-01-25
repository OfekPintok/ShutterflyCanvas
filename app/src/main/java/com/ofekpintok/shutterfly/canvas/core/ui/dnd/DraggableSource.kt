package com.ofekpintok.shutterfly.canvas.core.ui.dnd

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.toSize

@Composable
fun DraggableSource(
    onDragStart: (Offset, Size) -> Unit,
    onDrag: (dragAmount: Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(isDragging: Boolean) -> Unit
) {
    BaseDraggableSource(
        modifier = modifier,
        onDragStart = onDragStart,
        onDrag = onDrag,
        onDragEnd = onDragEnd,
        detect = { start, drag, end ->
            detectDragGesturesAfterLongPress(
                onDragStart = start,
                onDrag = drag,
                onDragEnd = end,
                onDragCancel = end
            )
        },
        content = content
    )
}

@Composable
fun InstantDraggableSource(
    modifier: Modifier = Modifier,
    onDragStart: (Offset, Size) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    content: @Composable (isDragging: Boolean) -> Unit
) {
    BaseDraggableSource(
        modifier = modifier,
        onDragStart = onDragStart,
        onDrag = onDrag,
        onDragEnd = onDragEnd,
        detect = { start, drag, end ->
            detectDragGestures(
                onDragStart = start,
                onDrag = drag,
                onDragEnd = end,
                onDragCancel = end
            )
        },
        content = { isDragging -> content(isDragging) }
    )
}

@Composable
private fun BaseDraggableSource(
    onDragStart: (Offset, Size) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
    detect: suspend PointerInputScope.(
        onDragStart: (Offset) -> Unit,
        onDrag: (PointerInputChange, Offset) -> Unit,
        onDragEnd: () -> Unit
    ) -> Unit,
    content: @Composable BoxScope.(isDragging: Boolean) -> Unit
) {
    var selfGlobalPosition by remember { mutableStateOf(Offset.Zero) }
    var selfSize by remember { mutableStateOf(Size.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .onGloballyPositioned { 
                selfGlobalPosition = it.positionInRoot()
                selfSize = it.size.toSize()
            }
            .pointerInput(Unit) {
                detect(
                    { 
                        isDragging = true
                        onDragStart(selfGlobalPosition, selfSize)
                    },
                    { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    },
                    {
                        isDragging = false
                        onDragEnd()
                    }
                )
            },
        content = { content(isDragging) }
    )
}
