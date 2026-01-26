package com.ofekpintok.shutterfly.canvas.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

val Float.pxToDp: Dp
    @Composable
    get() = with(LocalDensity.current) { this@pxToDp.toDp() }

val Int.pxToDp: Dp
    @Composable
    get() = with(LocalDensity.current) { this@pxToDp.toDp() }

val Dp.dpToPx: Float
    @Composable
    get() = with(LocalDensity.current) { this@dpToPx.toPx() }