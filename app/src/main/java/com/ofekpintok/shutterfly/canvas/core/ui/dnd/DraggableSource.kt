package com.ofekpintok.shutterfly.canvas.core.ui.dnd

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.toSize
import com.ofekpintok.shutterfly.canvas.core.ui.dnd.models.DragEventDelta
import kotlin.math.PI
import kotlin.math.abs

@Composable
fun DraggableSource(
    onDragStart: (Offset, Size) -> Unit,
    onDrag: (DragEventDelta) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
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
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        isDragging = true
                        onDragStart(selfGlobalPosition, selfSize)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(DragEventDelta(pan = dragAmount))
                    },
                    onDragEnd = {
                        isDragging = false
                        onDragEnd()
                    },
                    onDragCancel = {
                        isDragging = false
                        onDragEnd()
                    }
                )
            },
        content = { content(isDragging) }
    )
}

@Composable
fun TransformableSource(
    modifier: Modifier = Modifier,
    onDragStart: (Offset, Size) -> Unit,
    onDrag: (DragEventDelta) -> Unit,
    onDragEnd: () -> Unit,
    graphicLayerScope: GraphicsLayerScope.() -> Unit = {},
    content: @Composable BoxScope.(isDragging: Boolean) -> Unit
) {
    val currentOnDragStart by rememberUpdatedState(onDragStart)
    val currentOnDrag by rememberUpdatedState(onDrag)
    val currentOnDragEnd by rememberUpdatedState(onDragEnd)

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
                detectUnifiedGestures(
                    onGestureStart = {
                        isDragging = true
                        currentOnDragStart(selfGlobalPosition, selfSize)
                    },
                    onGesture = { pan, zoom, rotation ->
                        currentOnDrag(DragEventDelta(pan, zoom, rotation))
                    },
                    onGestureEnd = {
                        isDragging = false
                        currentOnDragEnd()
                    }
                )
            }
            .graphicsLayer(graphicLayerScope),
        content = { content(isDragging) }
    )
}

suspend fun PointerInputScope.detectUnifiedGestures(
    onGestureStart: () -> Unit,
    onGesture: (pan: Offset, zoom: Float, rotation: Float) -> Unit,
    onGestureEnd: () -> Unit
) {
    awaitEachGesture {
        awaitFirstDown(requireUnconsumed = false)
        onGestureStart()

        var zoom = 1f
        var rotation = 0f
        var pan = Offset.Zero
        var pastTouchSlop = false
        val touchSlop = viewConfiguration.touchSlop

        do {
            val event = awaitPointerEvent()
            val canceled = event.changes.any { it.isConsumed }
            if (canceled) break

            val zoomChange = event.calculateZoom()
            val rotationChange = event.calculateRotation()
            val panChange = event.calculatePan()

            if (pastTouchSlop) {
                if (zoomChange != 1f || rotationChange != 0f || panChange != Offset.Zero) {
                    onGesture(panChange, zoomChange, rotationChange)
                }
                event.changes.forEach { if (it.positionChanged()) it.consume() }
            } else {
                zoom *= zoomChange
                rotation += rotationChange
                pan += panChange

                val centroidSize = event.calculateCentroidSize(useCurrent = false)
                val zoomMotion = abs(1 - zoom) * centroidSize
                val rotationMotion = abs(rotation * PI.toFloat() * centroidSize / 180f)
                val panMotion = pan.getDistance()

                if (zoomMotion > touchSlop || rotationMotion > touchSlop || panMotion > touchSlop) {
                    pastTouchSlop = true
                }
            }
        } while (event.changes.any { it.pressed })

        onGestureEnd()
    }
}