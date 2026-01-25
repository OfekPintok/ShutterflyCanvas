package com.ofekpintok.shutterfly.canvas.features.editor.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ofekpintok.shutterfly.canvas.core.ui.dnd.DragOverlayContainer
import com.ofekpintok.shutterfly.canvas.core.ui.dnd.utils.rememberDragIntersection
import com.ofekpintok.shutterfly.canvas.core.utils.clipBottom
import com.ofekpintok.shutterfly.canvas.features.editor.EditorDragItem
import com.ofekpintok.shutterfly.canvas.features.editor.EditorEvent
import com.ofekpintok.shutterfly.canvas.features.editor.EditorUiState
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.Photo
import com.ofekpintok.shutterfly.canvas.features.editor.ui.canvas.EditorCanvas
import com.ofekpintok.shutterfly.canvas.features.editor.ui.components.GhostCanvasItem
import com.ofekpintok.shutterfly.canvas.features.editor.ui.components.GhostPhotoItem
import com.ofekpintok.shutterfly.canvas.features.editor.ui.config.EditorConfig.BaseCanvasPhotoWidth
import com.ofekpintok.shutterfly.canvas.ui.theme.ShutterflyCanvasTheme
import kotlinx.collections.immutable.persistentListOf
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditorRoute(
    viewModel: EditorViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EditorScreen(
        uiState = uiState,
        onEditorEvent = { viewModel.onEvent(it) }
    )
}

@Composable
private fun EditorScreen(
    uiState: EditorUiState,
    onEditorEvent: (EditorEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding -> EditorContent(onEditorEvent, padding, uiState) }
}

@Composable
private fun EditorContent(
    onEditorEvent: (EditorEvent) -> Unit,
    padding: PaddingValues,
    uiState: EditorUiState
) {
    var canvasBounds by remember { mutableStateOf(Rect.Zero) }
    var trashBounds by remember { mutableStateOf(Rect.Zero) }

    val haptic = LocalHapticFeedback.current

    DragOverlayContainer(
        onDrop = { dragItem, globalDropOffset, size->
            val droppedItemRect = Rect(offset = globalDropOffset, size = size)

            when {
                dragItem is EditorDragItem.FromCarousel && canvasBounds.contains(globalDropOffset) -> {
                    onEditorEvent(
                        EditorEvent.AddPhotoToCanvas(
                            photo = dragItem.photo,
                            position = globalDropOffset - canvasBounds.topLeft
                        )
                    )
                }

                dragItem is EditorDragItem.FromCanvas && trashBounds.overlaps(droppedItemRect) -> {
                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                    onEditorEvent(EditorEvent.RemovePhotoFromCanvas(dragItem.canvasPhoto.id))
                }

                dragItem is EditorDragItem.FromCanvas -> onEditorEvent(
                    EditorEvent.MoveCanvasPhoto(
                        instanceId = dragItem.canvasPhoto.id,
                        newPosition = globalDropOffset - canvasBounds.topLeft
                    )
                )

                else -> Unit
            }
        },
        dragOverlayContent = { dragItem, offset ->
            when (dragItem) {
                is EditorDragItem.FromCarousel -> GhostPhotoItem(dragItem.photo, offset)
                is EditorDragItem.FromCanvas -> GhostCanvasItem(
                    modifier = Modifier
                        .width(BaseCanvasPhotoWidth)
                        .wrapContentHeight(),
                    photo = dragItem.canvasPhoto,
                    offset = offset
                )
            }
        },
    ) {
        val isHoveringTrash = rememberDragIntersection(
            isDragging = dragState.isDragging,
            dragPosition = dragState.currentPosition,
            dragItemSize = dragState.size,
            targetBounds = trashBounds
        )

        BoxWithConstraints(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val carouselHeight = calculateCarouselHeight(maxHeight)

            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clipBottom()
                ) {
                    EditorCanvas(
                        modifier = Modifier.onGloballyPositioned {
                            canvasBounds = it.boundsInRoot()
                        },
                        canvasPhotos = uiState.canvasPhotos,
                        onDragStart = { item, offset, size -> onDragStart(item, offset, size) },
                        onDrag = { onDrag(it) },
                        onDragEnd = { onDragEnd() }
                    )
                }

                HorizontalDivider()

                EditorBottomBar(
                    modifier = Modifier
                        .height(carouselHeight)
                        .fillMaxWidth(),
                    carouselPhotos = uiState.carouselPhotos,
                    isLoading = uiState.isLoading,
                    dragState = dragState,
                    isHoveringTrash = isHoveringTrash,
                    onTrashBoundsChange = { trashBounds = it },
                    onDragStart = { item, offset, size ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDragStart(item, offset, size)
                    },
                    onDrag = { onDrag(it) },
                    onDragEnd = { onDragEnd() }
                )
            }
        }
    }
}

private fun calculateCarouselHeight(availableHeight: Dp): Dp {
    // Ensures the carousel remains usable on small screens (e.g., split-screen)
    // without taking up more than 30% of the vertical space.
    return min(120.dp, availableHeight * 0.3f)
}

@Preview(showBackground = true)
@Composable
private fun EditorScreenPreview() {
    ShutterflyCanvasTheme {
        EditorScreen(
            uiState = EditorUiState(
                isLoading = false,
                carouselPhotos = persistentListOf(
                    Photo(url = "url1", id = "1"),
                    Photo(url = "url2", id = "2"),
                    Photo(url = "url3", id = "3"),
                ),
                canvasPhotos = persistentListOf()
            ),
            onEditorEvent = {}
        )
    }
}