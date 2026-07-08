package com.gift.werkstatt.rendering.spatial

import com.gift.werkstatt.domain.canvas.model.Stroke
import com.gift.werkstatt.domain.canvas.model.StrokePoint
import javax.inject.Inject
import kotlin.math.floor

class StrokeSpatialIndex @Inject constructor() {
    private val grid = mutableMapOf<Pair<Int, Int>, MutableList<Int>>()
    private var strokes: List<Stroke> = emptyList()

    fun rebuild(nextStrokes: List<Stroke>, cellSize: Float = 96f, padding: Float = 30f) {
        grid.clear()
        strokes = nextStrokes
        nextStrokes.forEachIndexed { index, stroke ->
            if (stroke.points.isEmpty()) return@forEachIndexed
            val minX = stroke.points.minOf { it.x.toDouble() }.toFloat() - padding
            val minY = stroke.points.minOf { it.y.toDouble() }.toFloat() - padding
            val maxX = stroke.points.maxOf { it.x.toDouble() }.toFloat() + padding
            val maxY = stroke.points.maxOf { it.y.toDouble() }.toFloat() + padding
            val startX = floor(minX / cellSize).toInt()
            val endX = floor(maxX / cellSize).toInt()
            val startY = floor(minY / cellSize).toInt()
            val endY = floor(maxY / cellSize).toInt()
            for (x in startX..endX) {
                for (y in startY..endY) {
                    grid.getOrPut(x to y) { mutableListOf() }.add(index)
                }
            }
        }
    }

    fun erase(points: List<StrokePoint>, radius: Float = 30f, cellSize: Float = 96f): List<Stroke> {
        if (points.isEmpty() || strokes.isEmpty()) return strokes
        val candidateIndexes = mutableSetOf<Int>()
        points.forEach { point ->
            val minX = floor((point.x - radius) / cellSize).toInt()
            val maxX = floor((point.x + radius) / cellSize).toInt()
            val minY = floor((point.y - radius) / cellSize).toInt()
            val maxY = floor((point.y + radius) / cellSize).toInt()
            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    grid[x to y]?.let(candidateIndexes::addAll)
                }
            }
        }
        if (candidateIndexes.isEmpty()) return strokes
        val radiusSquared = radius * radius
        return strokes.filterIndexed { index, stroke ->
            if (index !in candidateIndexes) return@filterIndexed true
            points.none { erasePoint ->
                stroke.points.any { strokePoint ->
                    val dx = strokePoint.x - erasePoint.x
                    val dy = strokePoint.y - erasePoint.y
                    dx * dx + dy * dy <= radiusSquared
                }
            }
        }
    }
}

