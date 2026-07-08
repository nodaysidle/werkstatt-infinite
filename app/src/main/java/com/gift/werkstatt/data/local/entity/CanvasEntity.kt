package com.gift.werkstatt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "canvas_entries")
data class CanvasEntity(
    @PrimaryKey val id: String,
    val title: String,
    val strokes: String,
    val images: String,
    val viewportX: Float,
    val viewportY: Float,
    val zoom: Float,
    val gridMode: String,
    val gridSize: Float,
    val createdAt: Long,
    val updatedAt: Long,
    val thumbnailPath: String?
)
