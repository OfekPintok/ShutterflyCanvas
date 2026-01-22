package com.ofekpintok.shutterfly.canvas.features.editor.domain

import com.ofekpintok.shutterfly.canvas.features.editor.domain.models.Photo
import com.ofekpintok.shutterfly.canvas.features.editor.domain.repository.PhotosRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class FetchPhotosUseCaseTest {

    private val photosRepository: PhotosRepository = mock()
    private val sut = FetchPhotosUseCase(photosRepository)

    @Test
    fun `invoke with shuffled false returns photos in original order`() = runTest {
        // Given
        val photos = listOf(
            Photo(url = "url1", id = "1"),
            Photo(url = "url2", id = "2"),
            Photo(url = "url3", id = "3")
        )
        whenever(photosRepository.fetchPhotos()).thenReturn(photos)

        // When
        val result = sut(shuffled = false)

        // Then
        assertEquals(photos, result)
    }

    @Test
    fun `invoke with shuffled true returns photos in different order`() = runTest {
        // Given
        val photos = (1..100).map { Photo(url = "url$it", id = "$it") }
        whenever(photosRepository.fetchPhotos()).thenReturn(photos)

        // When
        val result = sut(shuffled = true)

        // Then
        assertEquals(photos.size, result.size)
        assertTrue(result.containsAll(photos))
        assertNotEquals(photos, result)
    }
}