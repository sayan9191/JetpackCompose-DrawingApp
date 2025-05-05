package com.example.drawingapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

class DrawingSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    private lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        val w = width.takeIf { it > 0 } ?: 1080
        val h = height.takeIf { it > 0 } ?: 1920
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        drawToSurface()
    }

    private fun drawToSurface() {
        val surfaceCanvas = holder.lockCanvas()
        surfaceCanvas?.let {
            it.drawBitmap(bitmap, 0f, 0f, null)
            holder.unlockCanvasAndPost(it)
        }
    }

    fun drawImageOnBitmap(image: Bitmap, matrix: Matrix) {
        Thread {
            val tempCanvas = Canvas(bitmap)
            tempCanvas.drawBitmap(image, matrix, null)
            post { drawToSurface() }
        }.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) {}
}