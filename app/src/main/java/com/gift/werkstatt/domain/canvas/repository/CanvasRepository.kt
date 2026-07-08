package com.gift.werkstatt.domain.canvas.repository

import com.gift.werkstatt.domain.canvas.model.CanvasEntry
import kotlinx.coroutines.flow.Flow

interface CanvasRepository {
    fun observeEntries(): Flow<List<CanvasEntry>>
    suspend fun getEntry(id: String): CanvasEntry?
    suspend fun upsert(entry: CanvasEntry)
    suspend fun delete(id: String)
}

