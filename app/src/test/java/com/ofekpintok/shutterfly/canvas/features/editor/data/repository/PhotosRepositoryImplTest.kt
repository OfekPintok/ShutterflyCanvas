package com.ofekpintok.shutterfly.canvas.features.editor.data.repository

import com.ofekpintok.shutterfly.canvas.features.editor.data.datasource.LocalAssetsDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class PhotosRepositoryImplTest {

    private val localAssetsDataSource: LocalAssetsDataSource = mock()
    private val packageName = "com.ofekpintok.shutterfly.canvas"

    private val testDispatcher = UnconfinedTestDispatcher()

    private val sut = PhotosRepositoryImpl(
        packageName = packageName,
        localAssetsDataSource = localAssetsDataSource,
        dispatcher = testDispatcher
    )

    @Test
    fun `fetchPhotos returns list of photos based on data source resources`() = runTest {
        // Given
        val mockResources = listOf(101, 102)
        whenever(localAssetsDataSource.getAssetResources()).thenReturn(mockResources)

        // When
        val result = sut.fetchPhotos()

        // Then
        assertEquals(2, result.size)
        assertEquals("android.resource://$packageName/101", result[0].url)
        assertEquals("android.resource://$packageName/102", result[1].url)
    }

    @Test
    fun `fetchPhotos returns empty list when data source is empty`() = runTest {
        // Given
        whenever(localAssetsDataSource.getAssetResources()).thenReturn(emptyList())

        // When
        val result = sut.fetchPhotos()

        // Then
        assertTrue(result.isEmpty())
    }
}