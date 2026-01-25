package com.ofekpintok.shutterfly.canvas.core.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect

fun Modifier.clipBottom() = this.drawWithContent {
    val infinite = Float.MAX_VALUE

    clipRect(
        left = -infinite,
        top = -infinite,
        right = infinite,
        bottom = size.height
    ) {
        this@drawWithContent.drawContent()
    }
}