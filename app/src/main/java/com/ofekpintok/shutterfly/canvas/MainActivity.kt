package com.ofekpintok.shutterfly.canvas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ofekpintok.shutterfly.canvas.features.editor.ui.EditorRoute
import com.ofekpintok.shutterfly.canvas.ui.theme.ShutterflyCanvasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShutterflyCanvasTheme {
                EditorRoute()
            }
        }
    }
}