package com.gift.werkstatt.domain.canvas.usecase

import com.gift.werkstatt.domain.canvas.model.BrushType
import com.gift.werkstatt.domain.canvas.model.CanvasEntry
import com.gift.werkstatt.domain.canvas.model.Stroke
import com.gift.werkstatt.domain.canvas.model.StrokePoint
import com.gift.werkstatt.rendering.spatial.StrokeSpatialIndex
import javax.inject.Inject

class BuildStrokeUseCase @Inject constructor(
    private val spatialIndex: StrokeSpatialIndex
) {
    fun execute(
        entry: CanvasEntry,
        points: List<StrokePoint>,
        color: Long,
        width: Float,
        brushType: BrushType,
        opacity: Float
    ): CanvasEntry {
        val stroke = Stroke(
            points = points,
            color = color,
            width = width,
            brushType = brushType,
            opacity = opacity
        )
        val nextStrokes = entry.strokes + stroke
        spatialIndex.rebuild(nextStrokes)
        return entry.copy(strokes = nextStrokes)
    }
}
