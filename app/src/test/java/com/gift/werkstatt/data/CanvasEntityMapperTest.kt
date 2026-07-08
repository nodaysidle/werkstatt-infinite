package com.gift.werkstatt.data

import com.gift.werkstatt.data.local.entity.CanvasEntity
import com.gift.werkstatt.data.local.mapper.CanvasEntityMapper
import com.gift.werkstatt.data.serialization.CanvasJsonCodec
import com.gift.werkstatt.domain.canvas.model.CanvasEntry
import com.gift.werkstatt.domain.canvas.model.GridMode
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test

class CanvasEntityMapperTest {
    private val mapper = CanvasEntityMapper(CanvasJsonCodec(Gson()))

    @Test
    fun toEntityPersistsTemplateGrid() {
        val entity = mapper.toEntity(
            CanvasEntry(
                id = "canvas-1",
                title = "Storyboard",
                gridMode = GridMode.Lines,
                gridSize = 150f
            )
        )

        assertEquals(GridMode.Lines.name, entity.gridMode)
        assertEquals(150f, entity.gridSize)
    }

    @Test
    fun toDomainFallsBackForUnknownGridMode() {
        val entry = mapper.toDomain(
            CanvasEntity(
                id = "canvas-1",
                title = "Recovered",
                strokes = "[]",
                images = "[]",
                viewportX = 0f,
                viewportY = 0f,
                zoom = 1f,
                gridMode = "Unknown",
                gridSize = 40f,
                createdAt = 1L,
                updatedAt = 1L,
                thumbnailPath = null
            )
        )

        assertEquals(GridMode.None, entry.gridMode)
    }
}
