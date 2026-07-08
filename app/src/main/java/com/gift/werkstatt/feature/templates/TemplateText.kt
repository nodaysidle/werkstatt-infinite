package com.gift.werkstatt.feature.templates

import androidx.annotation.StringRes
import com.gift.werkstatt.R
import com.gift.werkstatt.domain.canvas.model.PaperTemplate

@StringRes
fun PaperTemplate.titleRes(): Int = when (this) {
    PaperTemplate.Blank -> R.string.template_blank
    PaperTemplate.Ruled -> R.string.template_ruled
    PaperTemplate.DotGrid -> R.string.template_dot_grid
    PaperTemplate.SmallGrid -> R.string.template_small_grid
    PaperTemplate.Storyboard -> R.string.template_storyboard
    PaperTemplate.Sketch -> R.string.template_sketch
}

@StringRes
fun PaperTemplate.subtitleRes(): Int = when (this) {
    PaperTemplate.Blank -> R.string.template_blank_subtitle
    PaperTemplate.Ruled -> R.string.template_ruled_subtitle
    PaperTemplate.DotGrid -> R.string.template_dot_grid_subtitle
    PaperTemplate.SmallGrid -> R.string.template_small_grid_subtitle
    PaperTemplate.Storyboard -> R.string.template_storyboard_subtitle
    PaperTemplate.Sketch -> R.string.template_sketch_subtitle
}

