package com.gift.werkstatt.rendering.export

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

class CanvasExportRenderer @Inject constructor(
    private val strokeRenderer: StrokeRenderer
) {
    fun render(strokes: List<Stroke>): Bitmap? {
        val width = 1600
        val height = 2400
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

