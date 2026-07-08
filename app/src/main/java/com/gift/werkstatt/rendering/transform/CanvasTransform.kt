package com.gift.werkstatt.rendering.transform

import androidx.compose.ui.geometry.Offset
import com.gift.werkstatt.domain.canvas.model.CANVAS_HEIGHT
import com.gift.werkstatt.domain.canvas.model.CANVAS_WIDTH
import javax.inject.Inject
import kotlin.math.max

data class ViewportTransform(
    val zoom: Float = 1f,
    val offset: Offset = Offset.Zero
)

class CanvasTransformMapper @Inject constructor() {
    fun screenToCanvas(
        screen: Offset,
        drawSize: Offset,
        transform: ViewportTransform
    ): Offset {
        val safeZoom = max(transform.zoom, 0.1f)
        val scaleX = if (drawSize.x > 0f) CANVAS_WIDTH / drawSize.x else 1f
        val scaleY = if (drawSize.y > 0f) CANVAS_HEIGHT / drawSize.y else 1f
        val localX = (screen.x - transform.offset.x) / safeZoom
        val localY = (screen.y - transform.offset.y) / safeZoom
        return Offset(localX * scaleX, localY * scaleY)
    }

    fun applyGesture(
        current: ViewportTransform,
        zoomChange: Float,
        pan: Offset,
        centroid: Offset
    ): ViewportTransform {
        val oldZoom = current.zoom.coerceAtLeast(0.1f)
        val newZoom = (oldZoom * zoomChange).coerceIn(0.5f, 5f)
        val ratio = newZoom / oldZoom
        val nextOffset = Offset(
            x = centroid.x - (centroid.x - current.offset.x) * ratio + pan.x,
            y = centroid.y - (centroid.y - current.offset.y) * ratio + pan.y
        )
        return ViewportTransform(zoom = newZoom, offset = nextOffset)
    }
}

