package com.ofekpintok.shutterfly.canvas.features.editor.ui.canvas

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.CanvasPhoto
import kotlinx.coroutines.launch

@Composable
fun CanvasPhotoRenderer(
    photo: CanvasPhoto,
    modifier: Modifier = Modifier,
    animateEntry: Boolean = true
) {
    val initialValue = if (animateEntry) 0f else 1f

    val scaleAnim = remember { Animatable(initialValue) }
    val alphaAnim = remember { Animatable(initialValue) }

    LaunchedEffect(Unit) {
        if (animateEntry) {
            launch { scaleAnim.animateTo(1f, animationSpec = tween(300, easing = LinearOutSlowInEasing)) }
            launch { alphaAnim.animateTo(1f, animationSpec = tween(300)) }
        }
    }

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(photo.url)
            .crossfade(true)
            .build(),
        contentDescription = null,
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .graphicsLayer {
                scaleX = scaleAnim.value
                scaleY = scaleAnim.value
                alpha = alphaAnim.value
            }
    )
}
