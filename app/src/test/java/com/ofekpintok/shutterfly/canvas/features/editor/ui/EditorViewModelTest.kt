package com.ofekpintok.shutterfly.canvas.features.editor.ui

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.SavedStateHandle
import com.ofekpintok.shutterfly.canvas.features.editor.EditorEvent
import com.ofekpintok.shutterfly.canvas.features.editor.domain.FetchPhotosUseCase
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.CanvasPhoto
import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.CanvasPhotoAttributes
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
    fun `on AddPhotoToCanvas uiState is updated with new photo`() = runTest {
        // Given
        whenever(fetchPhotosUseCase.invoke(any())).thenReturn(emptyList())
        val savedStateHandle = SavedStateHandle()
        val sut = EditorViewModel(savedStateHandle, fetchPhotosUseCase)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            sut.uiState.collect()
        }
        advanceUntilIdle()

        val photo = Photo(url = "url1", id = "p1")
        val position = Offset(100f, 200f)

        // When
        sut.onEvent(EditorEvent.AddPhotoToCanvas(photo, position))

        // Then
        val actualCanvas = sut.uiState.value.canvasPhotos
        assertEquals(1, actualCanvas.size)
        assertEquals("p1", actualCanvas.first().sourceId)
        assertEquals(100f, actualCanvas.first().attributes.x)
        assertEquals(200f, actualCanvas.first().attributes.y)
    }

    @Test
    fun `on MoveCanvasPhoto photo position is updated`() = runTest {
        // Given
        val initialPhoto = CanvasPhoto(
            id = "instance1",
            sourceId = "p1",
            url = "url1",
            attributes = CanvasPhotoAttributes(x = 0f, y = 0f, scale = 1f, rotation = 0f)
        )
        val savedStateHandle = SavedStateHandle(mapOf("canvasPhotos" to listOf(initialPhoto)))
        whenever(fetchPhotosUseCase.invoke(any())).thenReturn(emptyList())

        val sut = EditorViewModel(savedStateHandle, fetchPhotosUseCase)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            sut.uiState.collect()
        }

        val newPosition = Offset(500f, 600f)

        // When
        sut.onEvent(
            EditorEvent.ManipulateCanvasPhoto(
                instanceId = "instance1",
                newPosition = newPosition,
                scale = 1f,
                rotation = 0f
            )
        )
        advanceUntilIdle()

        // Then
        val actualCanvas = sut.uiState.value.canvasPhotos
        val updatedPhoto = actualCanvas.find { it.id == "instance1" }!!

        assertEquals(500f, updatedPhoto.attributes.x)
        assertEquals(600f, updatedPhoto.attributes.y)
        assertEquals(1f, updatedPhoto.attributes.scale)
    }

    @Test
    fun `on ScaleCanvasPhoto photo scale is updated`() = runTest {
        // Given
        val initialPhoto = CanvasPhoto(id = "instance1", sourceId = "p1", url = "url1")
        val savedStateHandle = SavedStateHandle(mapOf("canvasPhotos" to listOf(initialPhoto)))
        whenever(fetchPhotosUseCase.invoke(any())).thenReturn(emptyList())

        val sut = EditorViewModel(savedStateHandle, fetchPhotosUseCase)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { sut.uiState.collect() }

        val newScale = 2.5f

        // When
        sut.onEvent(
            EditorEvent.ManipulateCanvasPhoto(
                instanceId = "instance1",
                newPosition = Offset(0f, 0f),
                scale = newScale,
                rotation = 0f
            )
        )
        advanceUntilIdle()

        // Then
        val actualPhoto = sut.uiState.value.canvasPhotos.first()
        assertEquals(2.5f, actualPhoto.attributes.scale)
    }

    @Test
    fun `on RotateCanvasPhoto photo rotation is updated`() = runTest {
        // Given
        val initialPhoto = CanvasPhoto(id = "instance1", sourceId = "p1", url = "url1")
        val savedStateHandle = SavedStateHandle(mapOf("canvasPhotos" to listOf(initialPhoto)))
        whenever(fetchPhotosUseCase.invoke(any())).thenReturn(emptyList())

        val sut = EditorViewModel(savedStateHandle, fetchPhotosUseCase)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { sut.uiState.collect() }

        val newRotation = 45f

        // When
        sut.onEvent(EditorEvent.ManipulateCanvasPhoto(
            instanceId = "instance1",
            newPosition = Offset.Zero,
            scale = 1f,
            rotation = newRotation
        ))
        advanceUntilIdle()

        // Then
        val actualPhoto = sut.uiState.value.canvasPhotos.first()
        assertEquals(45f, actualPhoto.attributes.rotation)
    }

    @Test
    fun `on ManipulateCanvasPhoto all attributes are updated concurrently`() = runTest {
        // Given
        val initialPhoto = CanvasPhoto(id = "instance1", sourceId = "p1", url = "url1")
        val savedStateHandle = SavedStateHandle(mapOf("canvasPhotos" to listOf(initialPhoto)))
        whenever(fetchPhotosUseCase.invoke(any())).thenReturn(emptyList())

        val sut = EditorViewModel(savedStateHandle, fetchPhotosUseCase)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { sut.uiState.collect() }

        // When
        sut.onEvent(EditorEvent.ManipulateCanvasPhoto(
            instanceId = "instance1",
            newPosition = Offset(100f, 200f),
            scale = 1.5f,
            rotation = 90f
        ))
        advanceUntilIdle()

        // Then
        val actualPhoto = sut.uiState.value.canvasPhotos.first()
        assertEquals(100f, actualPhoto.attributes.x)
        assertEquals(200f, actualPhoto.attributes.y)
        assertEquals(1.5f, actualPhoto.attributes.scale)
        assertEquals(90f, actualPhoto.attributes.rotation)
    }
}