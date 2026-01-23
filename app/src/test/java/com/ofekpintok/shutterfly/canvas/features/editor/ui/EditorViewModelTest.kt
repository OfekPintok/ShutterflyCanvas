package com.ofekpintok.shutterfly.canvas.features.editor.ui

import androidx.lifecycle.SavedStateHandle
import com.ofekpintok.shutterfly.canvas.features.editor.domain.FetchPhotosUseCase
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.CanvasPhoto
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class EditorViewModelTest {

    private val fetchPhotosUseCase: FetchPhotosUseCase = mock()
    private val testDispatcher = UnconfinedTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        // Given
        val savedStateHandle = SavedStateHandle()
        whenever(fetchPhotosUseCase.invoke(any())).thenReturn(emptyList())

        // When
        val sut = EditorViewModel(savedStateHandle, fetchPhotosUseCase)

        // Then
        assertTrue(sut.uiState.value.isLoading)
    }

    @Test
    fun `after loading, uiState contains photos and loading is false`() = runTest {
        // Given
        val photos = listOf(Photo(url = "url1", id = "1"))
        whenever(fetchPhotosUseCase.invoke(any())).thenReturn(photos)
        val savedStateHandle = SavedStateHandle()

        // When
        val sut = EditorViewModel(savedStateHandle, fetchPhotosUseCase)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            sut.uiState.collect()
        }
        advanceUntilIdle()

        // Then
        assertFalse(sut.uiState.value.isLoading)
        assertEquals(photos, sut.uiState.value.carouselPhotos)
    }

    @Test
    fun `restores canvas photos from SavedStateHandle`() = runTest {
        // Given
        val savedPhoto = CanvasPhoto(id = "c1", photoId = "p1", x = 10f, y = 10f, rotation = 0f, scale = 1f)
        val initialSavedState = mapOf("canvasPhotos" to listOf(savedPhoto))
        val savedStateHandle = SavedStateHandle(initialSavedState)

        whenever(fetchPhotosUseCase.invoke(any())).thenReturn(emptyList())

        // When
        val sut = EditorViewModel(savedStateHandle, fetchPhotosUseCase)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            sut.uiState.collect()
        }
        advanceUntilIdle()

        // Then
        val actualCanvas = sut.uiState.value.canvasPhotos
        assertEquals(1, actualCanvas.size)
        assertEquals("c1", actualCanvas.first().id)
    }
}