package com.gift.werkstatt.data.local.mapper

import com.gift.werkstatt.data.local.entity.CanvasEntity
import com.gift.werkstatt.data.serialization.CanvasJsonCodec
import com.gift.werkstatt.domain.canvas.model.CanvasEntry
import com.gift.werkstatt.domain.canvas.model.GridMode

class CanvasEntityMapper(
    private val codec: CanvasJsonCodec
) {
    fun toDomain(entity: CanvasEntity): CanvasEntry {
        return CanvasEntry(
            id = entity.id,
            title = entity.title,
            strokes = codec.decodeStrokes(entity.strokes),
            viewportX = entity.viewportX,
            viewportY = entity.viewportY,
            zoom = entity.zoom,
            gridMode = parseGridMode(entity.gridMode),
            gridSize = entity.gridSize,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            thumbnailPath = entity.thumbnailPath
        )
    }

    fun toEntity(entry: CanvasEntry): CanvasEntity {
        return CanvasEntity(
            id = entry.id,
            title = entry.title,
            strokes = codec.encodeStrokes(entry.strokes),
            images = "[]",
            viewportX = entry.viewportX,
            viewportY = entry.viewportY,
            zoom = entry.zoom,
            gridMode = entry.gridMode.name,
            gridSize = entry.gridSize,
            createdAt = entry.createdAt,
            updatedAt = entry.updatedAt,
            thumbnailPath = entry.thumbnailPath
        )
    }

    private fun parseGridMode(value: String): GridMode {
        return runCatching { GridMode.valueOf(value) }.getOrDefault(GridMode.None)
    }
}
