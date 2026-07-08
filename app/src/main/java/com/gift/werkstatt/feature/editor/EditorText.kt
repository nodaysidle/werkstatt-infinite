package com.gift.werkstatt.feature.editor

import androidx.annotation.StringRes
import com.gift.werkstatt.R
import com.gift.werkstatt.domain.canvas.model.BrushType
import com.gift.werkstatt.domain.canvas.model.PaletteFamily

@StringRes
fun BrushType.labelRes(): Int = when (this) {
    BrushType.Pen -> R.string.brush_pen
    BrushType.Fine -> R.string.brush_fine
    BrushType.Ballpoint -> R.string.brush_ballpoint
    BrushType.Pencil -> R.string.brush_pencil
    BrushType.Marker -> R.string.brush_marker
    BrushType.Watercolor -> R.string.brush_watercolor
    BrushType.Ink -> R.string.brush_ink
    BrushType.Brush -> R.string.brush_brush
}

@StringRes
fun BrushType.descriptionRes(): Int = when (this) {
    BrushType.Pen -> R.string.brush_pen_desc
    BrushType.Fine -> R.string.brush_fine_desc
    BrushType.Ballpoint -> R.string.brush_ballpoint_desc
    BrushType.Pencil -> R.string.brush_pencil_desc
    BrushType.Marker -> R.string.brush_marker_desc
    BrushType.Watercolor -> R.string.brush_watercolor_desc
    BrushType.Ink -> R.string.brush_ink_desc
    BrushType.Brush -> R.string.brush_brush_desc
}

@StringRes
fun PaletteFamily.labelRes(): Int = when (this) {
    PaletteFamily.Bold -> R.string.palette_bold
    PaletteFamily.Pastel -> R.string.palette_pastel
    PaletteFamily.Earth -> R.string.palette_earth
    PaletteFamily.Neon -> R.string.palette_neon
    PaletteFamily.Skin -> R.string.palette_skin
    PaletteFamily.Vintage -> R.string.palette_vintage
}

