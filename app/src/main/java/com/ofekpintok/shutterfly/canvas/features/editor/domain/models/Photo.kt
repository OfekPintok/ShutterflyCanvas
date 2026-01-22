package com.ofekpintok.shutterfly.canvas.features.editor.domain.models

import java.util.UUID

data class Photo(
    val url: String,
    val id: String = UUID.randomUUID().toString()
)