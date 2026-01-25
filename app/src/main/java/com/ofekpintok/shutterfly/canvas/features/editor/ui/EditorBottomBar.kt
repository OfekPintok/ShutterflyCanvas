package com.ofekpintok.shutterfly.canvas.features.editor.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ofekpintok.shutterfly.canvas.core.ui.dnd.DragState
import com.ofekpintok.shutterfly.canvas.features.editor.EditorDragItem
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.Photo
import com.ofekpintok.shutterfly.canvas.features.editor.ui.carousel.PhotoCarousel
import com.ofekpintok.shutterfly.canvas.ui.theme.ShutterflyCanvasTheme

@Composable
fun EditorBottomBar(
    modifier: Modifier = Modifier,
    carouselPhotos: List<Photo>,
    isLoading: Boolean,
    dragState: DragState<EditorDragItem>,
    isHoveringTrash: Boolean,
    onTrashBoundsChange: (Rect) -> Unit,
    onDragStart: (EditorDragItem, Offset, Size) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        PhotoCarousel(
            modifier = Modifier.matchParentSize(),
            isLoading = isLoading,
            photos = carouselPhotos,
            onDragStart = onDragStart,
            onDrag = onDrag,
            onDragEnd = onDragEnd
        )

        AnimatedVisibility(
            modifier = Modifier.matchParentSize(),
            visible = dragState.isDragging && dragState.data is EditorDragItem.FromCanvas,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            TrashOverlay(onTrashBoundsChange, isHoveringTrash)
        }
    }
}

@Composable
private fun BoxScope.TrashOverlay(
    onTrashBoundsChange: (Rect) -> Unit,
    isHoveringTrash: Boolean
) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .onGloballyPositioned { onTrashBoundsChange(it.boundsInRoot()) },
        contentAlignment = Alignment.Center
    ) {
        val contentColor by animateColorAsState(
            targetValue = if (isHoveringTrash) MaterialTheme.colorScheme.error else Color.White,
            label = "trashColor"
        )

        val contentScale by animateFloatAsState(
            targetValue = if (isHoveringTrash) 1.2f else 1.0f,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            label = "trashScale"
        )

        val backgroundColor by animateColorAsState(
            targetValue = if (isHoveringTrash) Color.Black.copy(alpha = 0.9f) else Color.Black.copy(
                alpha = 0.7f
            ),
            label = "bgColor"
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(backgroundColor)
                .onGloballyPositioned { onTrashBoundsChange(it.boundsInRoot()) },
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.scale(contentScale),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Drop to delete",
                    tint = contentColor,
                    modifier = Modifier.scale(contentScale)
                )
                Text(
                    text = "Drop to delete",
                    color = contentColor,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditorBottomBarPreview() {
    ShutterflyCanvasTheme {
        EditorBottomBar(
            modifier = Modifier.fillMaxWidth().height(120.dp),
            carouselPhotos = listOf(Photo(url = "", id = "1")),
            isLoading = false,
            dragState = DragState(),
            isHoveringTrash = false,
            onTrashBoundsChange = {},
            onDragStart = { _, _, _ -> },
            onDrag = {},
            onDragEnd = {}
        )
    }
}
