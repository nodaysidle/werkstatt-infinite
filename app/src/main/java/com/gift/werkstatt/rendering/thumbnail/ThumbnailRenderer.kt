package com.gift.werkstatt.rendering.thumbnail

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import com.gift.werkstatt.domain.canvas.model.CANVAS_HEIGHT
import com.gift.werkstatt.domain.canvas.model.CANVAS_WIDTH
import com.gift.werkstatt.domain.canvas.model.Stroke
import com.gift.werkstatt.rendering.brush.StrokeRenderer
import javax.inject.Inject

class ThumbnailRenderer @Inject constructor(
    private val strokeRenderer: StrokeRenderer
) {
    fun render(strokes: List<Stroke>): Bitmap? {
        return renderBitmap(strokes, width = 320, height = 480)
    }

    private fun renderBitmap(strokes: List<Stroke>, width: Int, height: Int): Bitmap? {
        if (width <= 0 || height <= 0) return null
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.rgb(16, 19, 25))
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val path = Path()
        val scaleX = width / CANVAS_WIDTH
        val scaleY = height / CANVAS_HEIGHT
        strokes.forEach { stroke ->
            strokeRenderer.drawAndroidStroke(canvas, stroke, scaleX, scaleY, paint, path)
        }
        return bitmap
    }
}

