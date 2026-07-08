package com.gift.werkstatt.data.files

import android.content.Context
import com.gift.werkstatt.domain.canvas.model.CanvasEntry
import com.gift.werkstatt.rendering.export.CanvasExportRenderer
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CanvasExportStore @Inject constructor(
    @ApplicationContext private val context: Context,
    private val renderer: CanvasExportRenderer
) {
    fun export(entry: CanvasEntry): Result<File> {
        val bitmap = renderer.render(entry.strokes)
            ?: return Result.failure(IllegalStateException("Export failed"))

        val file = File(context.cacheDir, buildExportFileName(entry.title, System.currentTimeMillis()))
        return runCatching {
            FileOutputStream(file).use { stream ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)
            }
            file
        }.also {
            bitmap.recycle()
        }
    }
}

fun buildExportFileName(title: String, timestamp: Long): String {
    val stem = title
        .trim()
        .replace(Regex("\\s+"), "_")
        .replace(Regex("[^A-Za-z0-9_\\-]"), "")
        .ifBlank { "Untitled" }
    return "${stem}_${timestamp}.png"
}

