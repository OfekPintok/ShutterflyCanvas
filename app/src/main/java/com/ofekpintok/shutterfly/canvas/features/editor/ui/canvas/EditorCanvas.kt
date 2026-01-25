package com.ofekpintok.shutterfly.canvas.features.editor.ui.canvas

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.ofekpintok.shutterfly.canvas.R
import com.ofekpintok.shutterfly.canvas.core.ui.dnd.InstantDraggableSource
import com.ofekpintok.shutterfly.canvas.features.editor.EditorDragItem
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.CanvasPhoto
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.CanvasPhotoAttributes
import com.ofekpintok.shutterfly.canvas.features.editor.ui.config.EditorConfig.BaseCanvasPhotoWidth
import com.ofekpintok.shutterfly.canvas.ui.theme.ShutterflyCanvasTheme
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun EditorCanvas(
    modifier: Modifier = Modifier,
    canvasPhotos: PersistentList<CanvasPhoto>,
    onDragStart: (EditorDragItem, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
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
    canvasPhotos: PersistentList<CanvasPhoto>,
    onDragStart: (EditorDragItem, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        canvasPhotos.forEach { photo ->
            key(photo.id) {
                InstantDraggableSource(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                photo.attributes.x.roundToInt(),
                                photo.attributes.y.roundToInt()
                            )
                        }
                        .graphicsLayer {
                            rotationZ = photo.attributes.rotation
                            scaleX = photo.attributes.scale
                            scaleY = photo.attributes.scale
                        }
                        .width(BaseCanvasPhotoWidth)
                        .wrapContentHeight(),
                    onDragStart = { offset ->
                        onDragStart(
                            EditorDragItem.FromCanvas(photo),
                            offset
                        )
                    },
                    onDrag = onDrag,
                    onDragEnd = onDragEnd,
                ) { isDragging ->
                    CanvasItem(
                        photo = photo,
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (isDragging) 0f else 1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CanvasItem(photo: CanvasPhoto, modifier: Modifier = Modifier) {
    var hasAnimated by rememberSaveable { mutableStateOf(false) }

    val scaleAnim = remember { Animatable(if (hasAnimated) 1f else 0.5f) }
    val alphaAnim = remember { Animatable(if (hasAnimated) 1f else 0f) }

    LaunchedEffect(Unit) {
        if (!hasAnimated) {
            launch {
                scaleAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            launch {
                alphaAnim.animateTo(1f, tween(300))
            }
            hasAnimated = true
        }
    }

    CanvasPhotoRenderer(
        photo = photo,
        modifier = modifier,
        scaleMultiplier = scaleAnim.value,
        alpha = alphaAnim.value
    )
}

@Preview(showBackground = true)
@Composable
private fun EmptyEditorCanvasPreview() {
    ShutterflyCanvasTheme {
        EditorCanvas(
            canvasPhotos = persistentListOf(),
            onDragStart = { _, _ -> },
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
            onDragStart = { _, _ -> },
            onDrag = { },
            onDragEnd = { }
        )
    }
}
