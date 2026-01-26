package com.ofekpintok.shutterfly.canvas.features.editor.ui.utils

import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

/**
 * Positions the element at the specified X and Y coordinates and defines a touch target (HitBox)
 * that is larger than the size reported to the layout system.
 *
 * This ensures visual stability in the layout while providing a larger interaction area
 * for the user.
 *
 * @param x The horizontal offset in pixels.
 * @param y The vertical offset in pixels.
 * @param baseSize The size (width and height) reported to the parent layout.
 * @param touchSize The actual size (width and height) of the interactive area.
 */
fun Modifier.editorPositioning(
    x: Float,
    y: Float,
    baseSize: Int,
    touchSize: Int
): Modifier = this
    .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
    .layout { measurable, _ ->
        val placeable = measurable.measure(Constraints.fixed(touchSize, touchSize))

        layout(baseSize, baseSize) {
            val offset = (touchSize - baseSize) / 2
            placeable.place(-offset, -offset)
        }
    }