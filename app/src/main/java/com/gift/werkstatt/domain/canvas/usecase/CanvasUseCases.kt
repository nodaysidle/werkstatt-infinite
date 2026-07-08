package com.gift.werkstatt.domain.canvas.usecase

import com.gift.werkstatt.data.files.ThumbnailStore
import com.gift.werkstatt.domain.canvas.model.CanvasEntry
import com.gift.werkstatt.domain.canvas.model.PaperTemplate
import com.gift.werkstatt.domain.canvas.repository.CanvasRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ObserveCanvasesUseCase @Inject constructor(
    private val repository: CanvasRepository
) {
    operator fun invoke() = repository.observeEntries()
}

class GetCanvasUseCase @Inject constructor(
    private val repository: CanvasRepository
) {
    suspend operator fun invoke(id: String) = repository.getEntry(id)
}

class CreateCanvasUseCase @Inject constructor(
    private val repository: CanvasRepository
) {
    suspend operator fun invoke(title: String, template: PaperTemplate): CanvasEntry {
        return withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val entry = CanvasEntry(
                title = title,
                gridMode = template.gridMode,
                gridSize = template.gridSize,
                createdAt = now,
                updatedAt = now
            )
            repository.upsert(entry)
            entry
        }
    }
}

class SaveCanvasUseCase @Inject constructor(
    private val repository: CanvasRepository,
    private val thumbnailStore: ThumbnailStore
) {
    suspend operator fun invoke(entry: CanvasEntry, refreshThumbnail: Boolean): CanvasEntry {
        return withContext(Dispatchers.IO) {
            val updated = entry.copy(updatedAt = System.currentTimeMillis())
            val thumbnail = if (refreshThumbnail) {
                thumbnailStore.render(updated.id, updated.strokes) ?: updated.thumbnailPath
            } else {
                updated.thumbnailPath
            }
            val finalEntry = updated.copy(thumbnailPath = thumbnail)
            repository.upsert(finalEntry)
            finalEntry
        }
    }
}

class DeleteCanvasUseCase @Inject constructor(
    private val repository: CanvasRepository,
    private val thumbnailStore: ThumbnailStore
) {
    suspend operator fun invoke(id: String) {
        withContext(Dispatchers.IO) {
            thumbnailStore.delete(id)
            repository.delete(id)
        }
    }
}
