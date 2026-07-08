package com.gift.werkstatt.data.files

import android.content.Context
import com.gift.werkstatt.domain.canvas.model.Stroke
import com.gift.werkstatt.rendering.thumbnail.ThumbnailRenderer
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThumbnailStore @Inject constructor(
    @ApplicationContext private val context: Context,
    private val renderer: ThumbnailRenderer
) {
    private val directory: File by lazy {
        File(context.filesDir, "thumbnails").apply { mkdirs() }
    }

    fun render(entryId: String, strokes: List<Stroke>): String? {
        val bitmap = renderer.render(strokes) ?: return null
        val output = File(directory, "thumb_${entryId.toSafeFileStem()}.jpg")
        return runCatching {
            FileOutputStream(output).use { stream ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 86, stream)
            }
            output.absolutePath
        }.getOrNull().also {
            bitmap.recycle()
        }
    }

    fun delete(entryId: String) {
        File(directory, "thumb_${entryId.toSafeFileStem()}.jpg").delete()
    }
}

internal fun String.toSafeFileStem(): String {
    return replace(Regex("[\\\\/:.\"'<>|?*\\x00-\\x1f]"), "_")
}

