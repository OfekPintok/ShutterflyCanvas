package com.ofekpintok.shutterfly.canvas.features.editor.ui.config

import androidx.compose.ui.unit.dp

object EditorConfig {

    /**
     * Feature Flag: Controls the initial shuffle state of the gallery.
     *
     * NOTE: In a production environment, this value should be sourced from a Remote Config provider
     * to enable dynamic updates and A/B testing without requiring a new app release.
     */
    val ENABLE_SHUFFLE_ON_LOAD: Boolean
        get() = true

    /**
     * Configuration: Simulates network latency to validate UI loading states.
     *
     * NOTE: Adjust this value to observe the transition between loading (Skeleton/Shimmer) and content states.
     * Recommended: 1000L for visual verification, 0L for instant execution.
     */
    val SIMULATED_LATENCY_MS: Long
        get() = 1_000L

    /**
     * Configuration: The base width in pixels used for calculating photo dimensions on the canvas.
     *
     * This value serves as the reference point for scaling and positioning elements
     * within the editor's workspace.
     */
    val BaseCanvasPhotoWidth = 200.dp
}