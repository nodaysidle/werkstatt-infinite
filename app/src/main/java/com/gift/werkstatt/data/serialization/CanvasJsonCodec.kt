package com.gift.werkstatt.data.serialization

import com.gift.werkstatt.domain.canvas.model.BrushType
import com.gift.werkstatt.domain.canvas.model.DEFAULT_INK
import com.gift.werkstatt.domain.canvas.model.Stroke
import com.gift.werkstatt.domain.canvas.model.StrokePoint
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CanvasJsonCodec @Inject constructor(
    private val gson: Gson
) {
    private val strokeListType = object : TypeToken<List<Stroke>>() {}.type
    private val persistedStrokeListType = object : TypeToken<List<PersistedStroke?>>() {}.type

    fun encodeStrokes(strokes: List<Stroke>): String = gson.toJson(strokes, strokeListType)

    fun decodeStrokes(json: String): List<Stroke> {
        return runCatching {
            gson.fromJson<List<PersistedStroke?>>(json, persistedStrokeListType)
                .orEmpty()
                .mapNotNull { it?.toDomain() }
        }.getOrNull().orEmpty()
    }
}

private data class PersistedStroke(
    val id: String?,
    val points: List<PersistedPoint>?,
    val color: Long?,
    val width: Float?,
    val brushType: BrushType?,
    val opacity: Float?,
    val createdAt: Long?
) {
    fun toDomain(): Stroke {
        return Stroke(
            id = id ?: UUID.randomUUID().toString(),
            points = points.orEmpty().mapNotNull { it.toDomain() },
            color = color ?: DEFAULT_INK,
            width = width ?: 4f,
            brushType = brushType ?: BrushType.Pen,
            opacity = opacity ?: 1f,
            createdAt = createdAt ?: System.currentTimeMillis()
        )
    }
}

private data class PersistedPoint(
    val x: Float?,
    val y: Float?,
    val pressure: Float?
) {
    fun toDomain(): StrokePoint? {
        val pointX = x ?: return null
        val pointY = y ?: return null
        return StrokePoint(pointX, pointY, pressure ?: 1f)
    }
}
