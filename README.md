# Jetpack Compose Drawing App

This project is developed as part of an Android developer interview task. It demonstrates how to integrate a traditional `SurfaceView` within a modern Jetpack Compose UI, allowing users to select, transform (pan, zoom, rotate), and draw images onto a canvas in a performant and responsive way.

## Features

- Jetpack Compose UI with embedded SurfaceView
- Load a random image from device storage
- Image manipulation with touch gestures (pan, zoom, rotate)
- Permanent drawing of transformed images onto the canvas
- Canvas reset functionality
- Thread-safe drawing to maintain UI responsiveness
- Runtime permission handling for Android 13+
- Toast-based user feedback
- Fixed portrait orientation

## Screenshots

*(Add screenshots of the app here)*

## Screen Recording

[View the demo recording](https://drive.google.com/file/d/1VZB4kGxY8SVXL3Gtq_Isw0dicgSp7a_d/view?usp=sharing)

## Tech Stack

- Kotlin
- Jetpack Compose
- Android SurfaceView
- Matrix transformations (Canvas and Bitmap)
- Android MediaStore
- Coroutines and threading

## Requirements

- Android device or emulator running API 23 (Marshmallow) or above
- MediaStore must contain at least one image
- Android 13 and above requires `READ_MEDIA_IMAGES` permission

## Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/sayan9191/JetpackCompose-DrawingApp.git
