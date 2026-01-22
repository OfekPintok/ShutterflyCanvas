package com.ofekpintok.shutterfly.canvas.features.editor.data.datasource

import com.ofekpintok.shutterfly.canvas.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalAssetsDataSourceTest {

    private val sut = LocalAssetsDataSource()

    @Test
    fun `getAssetResources returns a non-empty list of drawables`() {
        // When
        val result = sut.getAssetResources()

        // Then
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `getAssetResources returns the expected list of animal drawables`() {
        // Given
        val expected = listOf(
            R.drawable.cat,
            R.drawable.colorful_bird,
            R.drawable.giraffe,
            R.drawable.koala,
            R.drawable.reindeer,
            R.drawable.stork,
            R.drawable.tiger
        )

        // When
        val result = sut.getAssetResources()

        // Then
        assertEquals(expected, result)
    }
}