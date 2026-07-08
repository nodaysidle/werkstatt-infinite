package com.gift.werkstatt.domain.canvas.model

import java.util.UUID

const val CANVAS_WIDTH = 3000f
const val CANVAS_HEIGHT = 4500f
const val DEFAULT_INK = 0xFFF4F7F0L

data class StrokePoint(
    val x: Float,
    val y: Float,
    val pressure: Float = 1f
)

data class Stroke(
    val id: String = UUID.randomUUID().toString(),
    val points: List<StrokePoint> = emptyList(),
    val color: Long = DEFAULT_INK,
    val width: Float = 4f,
    val brushType: BrushType = BrushType.Pen,
    val opacity: Float = 1f,
    val createdAt: Long = System.currentTimeMillis()
)

data class CanvasEntry(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val strokes: List<Stroke> = emptyList(),
    val viewportX: Float = 0f,
    val viewportY: Float = 0f,
    val zoom: Float = 1f,
    val gridMode: GridMode = GridMode.None,
    val gridSize: Float = 40f,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val thumbnailPath: String? = null
)

enum class BrushType {
    Pen,
    Fine,
    Ballpoint,
    Pencil,
    Marker,
    Watercolor,
    Ink,
    Brush
}

val BrushType.isFastPath: Boolean
    get() = this == BrushType.Pen ||
        this == BrushType.Fine ||
        this == BrushType.Ballpoint ||
        this == BrushType.Marker

data class BrushPreset(
    val type: BrushType,
    val size: Float,
    val opacity: Float = 1f
)

object BrushPresets {
    val all = listOf(
        BrushPreset(BrushType.Pen, 4f),
        BrushPreset(BrushType.Fine, 2f),
        BrushPreset(BrushType.Ballpoint, 5f),
        BrushPreset(BrushType.Pencil, 8f, 0.88f),
        BrushPreset(BrushType.Marker, 18f, 0.82f),
        BrushPreset(BrushType.Watercolor, 24f, 0.58f),
        BrushPreset(BrushType.Ink, 7f),
        BrushPreset(BrushType.Brush, 14f, 0.95f)
    )

    fun forType(type: BrushType): BrushPreset = all.first { it.type == type }
}

enum class GridMode {
    None,
    Lines,
    Dots
}

enum class SortMode {
    Updated,
    Created,
    Name
}

enum class PaperTemplate(
    val gridMode: GridMode,
    val gridSize: Float,
    val accentColor: Long
) {
    Blank(GridMode.None, 40f, 0xFFC8FF00L),
    Ruled(GridMode.Lines, 56f, 0xFFC8FF00L),
    DotGrid(GridMode.Dots, 32f, 0xFF58C7F3L),
    SmallGrid(GridMode.Lines, 24f, 0xFF9AA04EL),
    Storyboard(GridMode.Lines, 150f, 0xFFFF7E7EL),
    Sketch(GridMode.Dots, 60f, 0xFFD4883AL)
}

enum class PaletteFamily {
    Bold,
    Pastel,
    Earth,
    Neon,
    Skin,
    Vintage
}

object ColorPalettes {
    fun colorsFor(family: PaletteFamily): List<Long> = when (family) {
        PaletteFamily.Bold -> listOf(
            0xFFE53935L, 0xFF1E88E5L, 0xFFFDD835L, 0xFF43A047L,
            0xFFFB8C00L, 0xFF8E24AAL, 0xFFF4F7F0L, 0xFF080A0DL
        )
        PaletteFamily.Pastel -> listOf(
            0xFFF8BBD9L, 0xFFE1BEE7L, 0xFFB2DFDBL, 0xFFFFE0B2L,
            0xFFBBDEFBL, 0xFFFFF8E1L, 0xFFD7CCC8L, 0xFFC8E6C9L
        )
        PaletteFamily.Earth -> listOf(
            0xFF6D4C41L, 0xFFD84315L, 0xFF827717L, 0xFFF9A825L,
            0xFFBF360CL, 0xFFD7CCC8L, 0xFF4E342EL, 0xFF8D6E63L
        )
        PaletteFamily.Neon -> listOf(
            0xFFFF1744L, 0xFF00E5FFL, 0xFF76FF03L, 0xFFFF9100L,
            0xFFD500F9L, 0xFFFFEA00L, 0xFF00E676L, 0xFF651FFFL
        )
        PaletteFamily.Skin -> listOf(
            0xFFFFDBACL, 0xFFF1C27DL, 0xFFE0AC69L, 0xFFC68642L,
            0xFF8D5524L, 0xFF6B4423L, 0xFF4A2912L, 0xFF2D1810L
        )
        PaletteFamily.Vintage -> listOf(
            0xFFD4A03BL, 0xFFB87D7DL, 0xFF87A878L, 0xFF8B3A3AL,
            0xFF2C3E50L, 0xFFA08887L, 0xFF6B5344L, 0xFF4A6741L
        )
    }
}
