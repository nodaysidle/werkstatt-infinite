package com.gift.werkstatt.domain.canvas.usecase

import com.gift.werkstatt.domain.canvas.model.CanvasEntry
import com.gift.werkstatt.domain.canvas.model.StrokePoint
import com.gift.werkstatt.rendering.spatial.StrokeSpatialIndex
import javax.inject.Inject

class EraseStrokesUseCase @Inject constructor(
    private val spatialIndex: StrokeSpatialIndex
) {
    fun execute(entry: CanvasEntry, points: List<StrokePoint>): CanvasEntry {
        val nextStrokes = spatialIndex.erase(points)
        spatialIndex.rebuild(nextStrokes)
        return entry.copy(strokes = nextStrokes)
    }
}
