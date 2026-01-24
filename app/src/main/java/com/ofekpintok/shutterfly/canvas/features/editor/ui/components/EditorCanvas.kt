package com.ofekpintok.shutterfly.canvas.features.editor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.CanvasPhoto
import com.ofekpintok.shutterfly.canvas.ui.theme.ShutterflyCanvasTheme
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun EditorCanvas(
    modifier: Modifier = Modifier,
    canvasPhotos: PersistentList<CanvasPhoto>
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
            }
    )
}

@Preview(showBackground = true)
@Composable
private fun EmptyEditorCanvasPreview() {
    ShutterflyCanvasTheme {
        EditorCanvas(
            canvasPhotos = persistentListOf()
        )
    }
}
