package com.ofekpintok.shutterfly.canvas.features.editor.ui.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ofekpintok.shutterfly.canvas.R
import com.ofekpintok.shutterfly.canvas.core.ui.dnd.TransformableSource
import com.ofekpintok.shutterfly.canvas.core.ui.dnd.models.DragEventDelta
import com.ofekpintok.shutterfly.canvas.core.utils.dpToPx
import com.ofekpintok.shutterfly.canvas.core.utils.pxToDp
import com.ofekpintok.shutterfly.canvas.features.editor.EditorDragItem
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.CanvasPhoto
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.CanvasPhotoAttributes
import com.ofekpintok.shutterfly.canvas.features.editor.ui.config.EditorConfig.BaseCanvasPhotoWidthDp
import com.ofekpintok.shutterfly.canvas.features.editor.ui.utils.editorPositioning
import com.ofekpintok.shutterfly.canvas.ui.theme.ShutterflyCanvasTheme
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlin.math.roundToInt

@Composable
fun EditorCanvas(
    canvasBounds: Rect,
    modifier: Modifier = Modifier,
    canvasPhotos: PersistentList<CanvasPhoto>,
    onDragStart: (EditorDragItem, Offset, Size, Float, Float) -> Unit,
    onDrag: (DragEventDelta) -> Unit,
    onDragEnd: () -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val gridLineColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .drawBehind {
                val gridSizePx = 32.dp.toPx().toInt()
                val strokeWidth = 1.dp.toPx()

                for (x in 0..size.width.toInt() step gridSizePx) {
                    drawLine(
                        color = gridLineColor,
                        start = Offset(x.toFloat(), 0f),
                        end = Offset(x.toFloat(), size.height),
                        strokeWidth = strokeWidth
                    )
                }

                for (y in 0..size.height.toInt() step gridSizePx) {
                    drawLine(
                        color = gridLineColor,
                        start = Offset(0f, y.toFloat()),
                        end = Offset(size.width, y.toFloat()),
                        strokeWidth = strokeWidth
                    )
                }
            },
        content = {
            CanvasContent(
                canvasBounds = canvasBounds,
                canvasPhotos = canvasPhotos,
                onDragStart = onDragStart,
                onDrag = onDrag,
                onDragEnd = onDragEnd
            )
        }
    )
}

@Composable
private fun CanvasContent(
    canvasBounds: Rect,
    canvasPhotos: PersistentList<CanvasPhoto>,
    onDragStart: (EditorDragItem, Offset, Size, Float, Float) -> Unit,
    onDrag: (DragEventDelta) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        val baseWidthPx = BaseCanvasPhotoWidthDp.dpToPx
        val baseWidthInt = baseWidthPx.roundToInt()

        val maxScreenDimensionPx = with(LocalConfiguration.current) {
            maxOf(screenWidthDp, screenHeightDp).dp.dpToPx.roundToInt()
        }

        canvasPhotos.forEach { photo ->
            key(photo.id) {
                CanvasPhotoItem(
                    baseWidthPx = baseWidthPx,
                    photo = photo,
                    maxScreenDimensionPx = maxScreenDimensionPx,
                    baseWidthInt = baseWidthInt,
                    canvasBounds = canvasBounds,
                    onDragStart = onDragStart,
                    onDrag = onDrag,
                    onDragEnd = onDragEnd
                )
            }
        }
    }
}

@Composable
private fun CanvasPhotoItem(
    baseWidthPx: Float,
    photo: CanvasPhoto,
    maxScreenDimensionPx: Int,
    baseWidthInt: Int,
    canvasBounds: Rect,
    onDragStart: (EditorDragItem, Offset, Size, Float, Float) -> Unit,
    onDrag: (DragEventDelta) -> Unit,
    onDragEnd: () -> Unit
) {
    val realVisualSizePx = baseWidthPx * photo.attributes.scale
    val touchSizePx = realVisualSizePx
        .coerceAtMost(maxScreenDimensionPx.toFloat())
        .roundToInt()

    TransformableSource(
        modifier = Modifier.editorPositioning(
            x = photo.attributes.x,
            y = photo.attributes.y,
            baseSize = baseWidthInt,
            touchSize = touchSizePx
        ),
        graphicLayerScope = {
            rotationZ = photo.attributes.rotation
            scaleX = 1f
            scaleY = 1f
        },
        onDragStart = { _, _ ->
            val absoluteImagePosition = canvasBounds.topLeft + Offset(
                photo.attributes.x,
                photo.attributes.y
            )

            onDragStart(
                EditorDragItem.FromCanvas(photo),
                absoluteImagePosition,
                Size(baseWidthPx, baseWidthPx),
                photo.attributes.scale,
                photo.attributes.rotation
            )
        },
        onDrag = { onDrag(it) },
        onDragEnd = onDragEnd,
    ) { isDragging ->
        CanvasPhotoRenderer(
            photo = photo,
            modifier = Modifier
                .requiredSize(realVisualSizePx.pxToDp)
                .alpha(if (isDragging) 0f else 1f),
            animateEntry = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyEditorCanvasPreview() {
    ShutterflyCanvasTheme {
        EditorCanvas(
            Rect.Zero,
            canvasPhotos = persistentListOf(),
            onDragStart = { _, _, _, _, _ -> },
            onDrag = { },
            onDragEnd = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PopulatedEditorCanvasPreview() {
    val packageName = LocalContext.current.packageName
    ShutterflyCanvasTheme {
        EditorCanvas(
            Rect.Zero,
            canvasPhotos = persistentListOf(
                CanvasPhoto(
                    sourceId = "1",
                    url = "android.resource://$packageName/${R.drawable.cat}",
                    attributes = CanvasPhotoAttributes(x = 50f, y = 50f, rotation = 10f)
                ),
                CanvasPhoto(
                    sourceId = "2",
                    url = "android.resource://$packageName/${R.drawable.koala}",
                    attributes = CanvasPhotoAttributes(
                        x = 300f,
                        y = 200f,
                        rotation = -5f,
                        scale = 0.8f
                    )
                ),
                CanvasPhoto(
                    sourceId = "3",
                    url = "android.resource://$packageName/${R.drawable.tiger}",
                    attributes = CanvasPhotoAttributes(x = 150f, y = 500f, rotation = 0f)
                )
            ),
            onDragStart = { _, _, _, _, _ -> },
            onDrag = { },
            onDragEnd = { }
        )
    }
}
