package com.ofekpintok.shutterfly.canvas.features.editor.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CanvasPhoto(
    val id: String,
    val photoId: String,
    val x: Float,
    val y: Float,
    val scale: Float,
    val rotation: Float
) : Parcelable