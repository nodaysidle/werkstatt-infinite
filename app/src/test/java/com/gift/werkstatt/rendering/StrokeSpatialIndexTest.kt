package com.gift.werkstatt.rendering

import com.gift.werkstatt.domain.canvas.model.Stroke
import com.gift.werkstatt.domain.canvas.model.StrokePoint
import com.gift.werkstatt.rendering.spatial.StrokeSpatialIndex
import org.junit.Assert.assertEquals
import org.junit.Test

class StrokeSpatialIndexTest {
    @Test
    fun eraseRemovesOnlyNearbyStroke() {
        val near = Stroke(points = listOf(StrokePoint(100f, 100f), StrokePoint(120f, 120f)))
        val far = Stroke(points = listOf(StrokePoint(900f, 900f), StrokePoint(940f, 940f)))
        val index = StrokeSpatialIndex()

        index.rebuild(listOf(near, far))
        val kept = index.erase(listOf(StrokePoint(110f, 110f)), radius = 45f)

        assertEquals(listOf(far.id), kept.map { it.id })
    }

    @Test
    fun eraseSupportsNegativeCoordinates() {
        val negative = Stroke(points = listOf(StrokePoint(-200f, -200f), StrokePoint(-180f, -180f)))
        val center = Stroke(points = listOf(StrokePoint(50f, 50f), StrokePoint(70f, 70f)))
        val index = StrokeSpatialIndex()

        index.rebuild(listOf(negative, center))
        val kept = index.erase(listOf(StrokePoint(-190f, -190f)), radius = 50f)

        assertEquals(listOf(center.id), kept.map { it.id })
    }
}

