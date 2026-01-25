package com.ofekpintok.shutterfly.canvas.features.editor.ui.canvas

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.CanvasPhoto

@Composable
fun CanvasPhotoRenderer(
    photo: CanvasPhoto,
    modifier: Modifier = Modifier,
    scaleMultiplier: Float = 1f,
    alpha: Float = 1f,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(photo.url)
            .size(800)
            .crossfade(true)
            .build(),
        contentDescription = null,
        modifier = modifier
            .graphicsLayer {
                rotationZ = photo.attributes.rotation
                scaleX = photo.attributes.scale * scaleMultiplier
                scaleY = photo.attributes.scale * scaleMultiplier
                this.alpha = alpha
                renderEffect = null
            }
            .clip(RoundedCornerShape(2.dp))
    )
}
