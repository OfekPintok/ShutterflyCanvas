package com.ofekpintok.shutterfly.canvas.core.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot

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

fun Modifier.clipBelow(limitY: Float) = composed {
    var selfGlobalY by remember { mutableStateOf(0f) }

    this
        .onGloballyPositioned { coordinates -> selfGlobalY = coordinates.positionInRoot().y }
        .drawWithContent {
            val visibleHeight = limitY - selfGlobalY

            clipRect(
                left = -Float.MAX_VALUE,
                top = -Float.MAX_VALUE,
                right = Float.MAX_VALUE,
                bottom = visibleHeight
            ) {
                this@drawWithContent.drawContent()
            }
        }
}