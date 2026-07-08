package com.gift.werkstatt.rendering

import androidx.compose.ui.geometry.Offset
import com.gift.werkstatt.rendering.transform.CanvasTransformMapper
import com.gift.werkstatt.rendering.transform.ViewportTransform
import org.junit.Assert.assertEquals
import org.junit.Test

class CanvasTransformMapperTest {
    private val mapper = CanvasTransformMapper()

    @Test
    fun screenToCanvasMapsCenterWithoutTransform() {
        val result = mapper.screenToCanvas(
            screen = Offset(500f, 500f),
            drawSize = Offset(1000f, 1000f),
            transform = ViewportTransform()
        )

        assertEquals(1500f, result.x, 0.01f)
        assertEquals(2250f, result.y, 0.01f)
    }

    @Test
    fun screenToCanvasCompensatesForZoomAndPan() {
        val result = mapper.screenToCanvas(
            screen = Offset(1000f, 900f),
            drawSize = Offset(1000f, 1000f),
            transform = ViewportTransform(zoom = 2f, offset = Offset(100f, 50f))
        )

        assertEquals(1350f, result.x, 0.01f)
        assertEquals(1912.5f, result.y, 0.01f)
    }
}

