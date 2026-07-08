package com.gift.werkstatt.data.repository

import com.gift.werkstatt.data.local.dao.CanvasDao
import com.gift.werkstatt.data.local.mapper.CanvasEntityMapper
import com.gift.werkstatt.domain.canvas.model.CanvasEntry
import com.gift.werkstatt.domain.canvas.repository.CanvasRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CanvasRepositoryImpl @Inject constructor(
    private val dao: CanvasDao,
    private val mapper: CanvasEntityMapper
) : CanvasRepository {
    override fun observeEntries(): Flow<List<CanvasEntry>> {
        return dao.observeEntries().map { entries -> entries.map(mapper::toDomain) }
    }

    override suspend fun getEntry(id: String): CanvasEntry? {
        return dao.getEntry(id)?.let(mapper::toDomain)
    }

    override suspend fun upsert(entry: CanvasEntry) {
        dao.upsert(mapper.toEntity(entry))
    }

    override suspend fun delete(id: String) {
        dao.delete(id)
    }
}

