package com.ofekpintok.shutterfly.canvas.core.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect


/**
 * Clips the drawing content to the bottom boundary of the layout node.
 *
 * This modifier ensures that any content drawn outside the height of the component
 * is not rendered, while allowing content to overflow freely on the left, top,
 * and right sides.
 *
 * @return A [Modifier] that clips content at the bottom edge of the layout.
 */
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