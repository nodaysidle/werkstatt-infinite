package com.gift.werkstatt.data

import com.gift.werkstatt.data.serialization.CanvasJsonCodec
import com.gift.werkstatt.domain.canvas.model.BrushType
import com.gift.werkstatt.domain.canvas.model.Stroke
import com.gift.werkstatt.domain.canvas.model.StrokePoint
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test

class CanvasJsonCodecTest {
    @Test
    fun strokesRoundTripThroughJson() {
        val codec = CanvasJsonCodec(Gson())
        val strokes = listOf(
            Stroke(
                id = "stroke-1",
                points = listOf(StrokePoint(1f, 2f, 0.5f)),
                color = 0xFFC8FF00,
                width = 7f,
                brushType = BrushType.Ink,
                opacity = 0.8f
            )
        )

        assertEquals(strokes, codec.decodeStrokes(codec.encodeStrokes(strokes)))
    }

    @Test
    fun malformedJsonReturnsEmptyList() {
        val codec = CanvasJsonCodec(Gson())

        assertEquals(emptyList<Stroke>(), codec.decodeStrokes("{"))
    }

    @Test
    fun legacyStrokeJsonGetsSafeDefaults() {
        val codec = CanvasJsonCodec(Gson())
        val legacyJson = """
            [
              {
                "id": "legacy",
                "points": [{"x": 1.0, "y": 2.0}],
                "color": 4294244080,
                "width": 4.0,
                "brushType": null,
                "opacity": 1.0,
                "createdAt": 1
              }
            ]
        """.trimIndent()

        val stroke = codec.decodeStrokes(legacyJson).single()

        assertEquals(BrushType.Pen, stroke.brushType)
        assertEquals(1f, stroke.points.single().pressure)
    }
}
