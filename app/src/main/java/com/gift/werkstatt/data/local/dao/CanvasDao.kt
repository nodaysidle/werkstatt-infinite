package com.gift.werkstatt.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.gift.werkstatt.data.local.entity.CanvasEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CanvasDao {
    @Query("SELECT * FROM canvas_entries ORDER BY updatedAt DESC")
    fun observeEntries(): Flow<List<CanvasEntity>>

    @Query("SELECT * FROM canvas_entries WHERE id = :id")
    suspend fun getEntry(id: String): CanvasEntity?

    @Upsert
    suspend fun upsert(entry: CanvasEntity)

    @Query("DELETE FROM canvas_entries WHERE id = :id")
    suspend fun delete(id: String)
}

