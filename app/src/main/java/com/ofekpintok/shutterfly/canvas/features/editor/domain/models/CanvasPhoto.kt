package com.ofekpintok.shutterfly.canvas.features.editor.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class CanvasPhoto(
    val sourceId: String,
    val url: String,
    val id: String = UUID.randomUUID().toString(),
    val attributes: CanvasPhotoAttributes = CanvasPhotoAttributes()
) : Parcelable

@Parcelize
data class CanvasPhotoAttributes(
    val x: Float = 0f,
    val y: Float = 0f,
    val scale: Float = 1f,
    val rotation: Float = 0f
) : Parcelable