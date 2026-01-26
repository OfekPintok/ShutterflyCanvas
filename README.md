# Photo Canvas Editor 🎨

An Android application built with **Jetpack Compose** that demonstrates complex gesture handling, custom layouts, and interactive canvas manipulation.

## 📱 Features

* **Drag & Drop:** Seamlessly drag photos from a carousel onto the main canvas.
* **Canvas Manipulation:**
    * **Move:** Freely position photos on the canvas.
    * **Zoom (Scale):** Infinite scaling support using custom layout logic to prevent UI bound clipping.
    * **Rotate:** Full 360-degree rotation support with two-finger gestures.
* **Multi-Touch Support:** Robust gesture locking to prevent conflicts when interacting with multiple items simultaneously.
* **Smart Deletion:** Drag items to the trash bin to remove them.

## 🛠 Tech Stack

* **Language:** Kotlin
* **UI:** Jetpack Compose (Material3)
* **Architecture:** MVVM (Model-View-ViewModel)
* **DI:** Koin
* **Async:** Coroutines & Flows
* **Image Loading:** Coil

## 🏗️ Architecture Highlights

### The "Infinite" Canvas Logic
To support large-scale zooming without breaking Android's layout boundaries, the app uses a custom `Modifier.layout` strategy:
1.  **Reported Size:** The UI reports a fixed, small size to the parent layout to maintain stability.
2.  **Touch Size:** The hit-test area scales dynamically (up to screen bounds) to capture gestures.
3.  **Visual Size:** The content is rendered at full scale using `requiredSize` to allow visual overflow.

### Gesture Handling
Implemented a custom `DragOverlayContainer` acting as a mutex to manage gesture ownership, ensuring a smooth experience even when multiple fingers are on the screen.

## 📦 How to Install

Go to the [Releases Sect/releases](https://github.com/OfekPintok/ShutterflyCanvas/releases) to download the latest APK file.

---
*Built as a home assignment task.*
