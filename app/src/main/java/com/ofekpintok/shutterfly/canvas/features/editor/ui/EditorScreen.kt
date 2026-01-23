package com.ofekpintok.shutterfly.canvas.features.editor.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.Photo
import com.ofekpintok.shutterfly.canvas.features.editor.ui.components.EditorCanvas
import com.ofekpintok.shutterfly.canvas.features.editor.ui.components.PhotoCarousel
import com.ofekpintok.shutterfly.canvas.ui.theme.ShutterflyCanvasTheme
import kotlinx.collections.immutable.persistentListOf
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditorRoute(
    viewModel: EditorViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EditorScreen(uiState)
}

@Composable
private fun EditorScreen(
    uiState: EditorUiState,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val carouselHeight = calculateCarouselHeight(maxHeight)

            Column(modifier = Modifier.fillMaxSize()) {
                EditorCanvas(
                    modifier = Modifier.weight(1f),
                    canvasPhotos = uiState.canvasPhotos
                )
                PhotoCarousel(
                    modifier = Modifier.height(height = carouselHeight),
                    isLoading = uiState.isLoading,
                    photos = uiState.carouselPhotos
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
            )
        )
    }
}
