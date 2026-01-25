package com.ofekpintok.shutterfly.canvas.features.editor.ui.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.CanvasPhoto
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.Photo
import com.ofekpintok.shutterfly.canvas.features.editor.ui.canvas.CanvasPhotoRenderer
import kotlin.math.roundToInt

@Composable
fun GhostPhotoItem(photo: Photo, offset: Offset, modifier: Modifier = Modifier) {
    AsyncImage(
        model = photo.url,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .offset {
                IntOffset(
                    offset.x.roundToInt(),
                    offset.y.roundToInt()
                )
            }
            .width(120.dp)
            .wrapContentHeight()
            .heightIn(max = 200.dp)
            .alpha(0.6f)
            .clip(RoundedCornerShape(2.dp))
            .shadow(16.dp, RoundedCornerShape(8.dp))
    )
}

@Composable
fun GhostCanvasItem(
    photo: CanvasPhoto,
    offset: Offset,
    modifier: Modifier = Modifier
) {
    CanvasPhotoRenderer(
        photo = photo,
        modifier = modifier.offset {
            IntOffset(
                offset.x.roundToInt(),
                offset.y.roundToInt()
            )
        }
    )
}