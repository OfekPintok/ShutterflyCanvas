package com.ofekpintok.shutterfly.canvas.features.editor.ui.carousel

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ofekpintok.shutterfly.canvas.core.ui.dnd.DraggableSource
import com.ofekpintok.shutterfly.canvas.core.utils.rememberShimmerBrush
import com.ofekpintok.shutterfly.canvas.features.editor.EditorDragItem
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.Photo
import com.ofekpintok.shutterfly.canvas.ui.theme.ShutterflyCanvasTheme

@Composable
fun PhotoCarousel(
    isLoading: Boolean,
    photos: List<Photo>,
    onDragStart: (EditorDragItem, Offset, Size) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        when {
            isLoading -> CarouselLoader()
            photos.isEmpty() -> CarouselError()
            else -> CarouselContent(
                photos = photos,
                onDragStart = onDragStart,
                onDrag = onDrag,
                onDragEnd = onDragEnd
            )
        }
    }
}

@Composable
private fun CarouselLoader(modifier: Modifier = Modifier) {
    HorizontalCarousel(modifier = modifier) {
        items(
            count = 10,
            key = { "shimmer_$it" },
            itemContent = {
                CarouselItemContainer {
                    ShimmerItem()
                }
            }
        )
    }
}

@Composable
private fun CarouselError(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            text = "We were unable to load your photos, please try again later.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun CarouselContent(
    modifier: Modifier = Modifier,
    photos: List<Photo>,
    onDragStart: (EditorDragItem, Offset, Size) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    HorizontalCarousel(modifier = modifier) {
        items(
            items = photos,
            key = { photo -> photo.id },
            itemContent = { photo ->
                DraggableSource(
                    modifier = modifier.wrapContentSize(),
                    onDragStart = { globalOffset, size ->
                        onDragStart(
                            EditorDragItem.FromCarousel(photo),
                            globalOffset,
                            size
                        )
                    },
                    onDrag = onDrag,
                    onDragEnd = onDragEnd,
                    content = { isDragging ->
                        val alpha by animateFloatAsState(
                            targetValue = if (isDragging) 0.3f else 1f,
                            label = "drag_alpha"
                        )

                        val scale by animateFloatAsState(
                            targetValue = if (isDragging) 0.9f else 1f,
                            label = "drag_scale"
                        )

                        CarouselItem(
                            photo = photo,
                            modifier = Modifier.graphicsLayer {
                                this.alpha = alpha
                                this.scaleX = scale
                                this.scaleY = scale
                            }
                        )
                    }
                )
            }
        )
    }
}

@Composable
private fun HorizontalCarousel(
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit
) {
    LazyRow(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun CarouselItemContainer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        content = content
    )
}

@Composable
private fun ShimmerItem(modifier: Modifier = Modifier) {
    val brush = rememberShimmerBrush()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush)
    )
}

@Composable
private fun CarouselItem(
    photo: Photo,
    modifier: Modifier = Modifier
) {
    CarouselItemContainer(modifier = modifier) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.url)
                .crossfade(true)
                .build(),
            contentDescription = "Select photo",
            error = rememberVectorPainter(Icons.Default.BrokenImage),
            contentScale = ContentScale.Crop
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotoCarouselLoadingPreview() {
    ShutterflyCanvasTheme {
        PhotoCarousel(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            isLoading = true,
            photos = emptyList(),
            onDragStart = { _, _, _ -> },
            onDrag = {},
            onDragEnd = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotoCarouselErrorPreview() {
    ShutterflyCanvasTheme {
        PhotoCarousel(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            isLoading = false,
            photos = emptyList(),
            onDragStart = { _, _, _ -> },
            onDrag = {},
            onDragEnd = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotoCarouselPreview() {
    ShutterflyCanvasTheme {
        PhotoCarousel(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            isLoading = false,
            // Since those urls are not real, we should see an error handler when we run this preview.
            photos = listOf(
                Photo(url = "url1", id = "1"),
                Photo(url = "url2", id = "2")
            ),
            onDragStart = { _, _, _ -> },
            onDrag = {},
            onDragEnd = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShimmerItemPreview() {
    ShutterflyCanvasTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ShimmerItem(modifier = Modifier.size(100.dp))
        }
    }
}
