package com.example.drawingapp

import android.Manifest
import android.content.ContentUris
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import com.example.drawingapp.ui.theme.DrawingAppTheme
import java.io.InputStream

class MainActivity : ComponentActivity() {

    private lateinit var drawingSurfaceView: DrawingSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Runtime permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                100
            )
        }

        setContent {
            DrawingAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DrawingScreen()
                }
            }
        }
    }

    @Composable
    fun DrawingScreen() {
        val context = LocalContext.current

        var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }
        var scale by remember { mutableStateOf(1f) }
        var rotation by remember { mutableStateOf(0f) }
        var offset by remember { mutableStateOf(Offset.Zero) }

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AndroidView(
                    factory = {
                        DrawingSurfaceView(it).also { view ->
                            drawingSurfaceView = view
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                previewBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                rotationZ = rotation,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, zoom, rotate ->
                                    scale *= zoom
                                    rotation += rotate
                                    offset += pan
                                }
                            }
                    )
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    val bitmap = getRandomImageFromStorage()
                    if (bitmap != null) {
                        previewBitmap = bitmap
                        scale = 1f
                        rotation = 0f
                        offset = Offset.Zero
                        Toast.makeText(context, "Image loaded successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "No image found! Please add an image.", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Pick Image")
                }

                Button(onClick = {
                    previewBitmap?.let { bitmap ->
                        val matrix = Matrix().apply {
                            postTranslate(-bitmap.width / 2f, -bitmap.height / 2f)
                            postScale(scale, scale)
                            postRotate(rotation)
                            postTranslate(
                                offset.x + bitmap.width / 2f,
                                offset.y + bitmap.height / 2f
                            )
                        }
                        drawingSurfaceView.drawImageOnBitmap(bitmap, matrix)
                        previewBitmap = null
                        Toast.makeText(context, "Image inserted onto canvas!", Toast.LENGTH_SHORT).show()
                    } ?: run {
                        Toast.makeText(context, "No image to insert!", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Insert")
                }
            }
        }
    }

    private fun getRandomImageFromStorage(): Bitmap? {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.count > 0) {
                val randomIndex = (0 until it.count).random()
                it.moveToPosition(randomIndex)

                val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val imageUri = ContentUris.withAppendedId(uri, id)

                contentResolver.openInputStream(imageUri)?.use { inputStream ->
                    return BitmapFactory.decodeStream(inputStream)
                }
            }
        }

        return null
    }
}