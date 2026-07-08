package com.gift.werkstatt.feature.templates

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gift.werkstatt.R
import com.gift.werkstatt.core.design.LocalWerkstattColors
import com.gift.werkstatt.domain.canvas.model.GridMode
import com.gift.werkstatt.domain.canvas.model.PaperTemplate

@Composable
fun TemplatePickerDialog(
    onTemplateSelected: (PaperTemplate) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.template_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PaperTemplate.entries.forEach { template ->
                    TemplateCard(
                        template = template,
                        onClick = { onTemplateSelected(template) }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}

@Composable
private fun TemplateCard(
    template: PaperTemplate,
    onClick: () -> Unit
) {
    val colors = LocalWerkstattColors.current
    val shape = RoundedCornerShape(8.dp)

    Card(
        onClick = onClick,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = colors.surfaceRaised),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .size(64.dp)
                    .background(colors.canvas, shape)
            ) {
                val accent = Color(template.accentColor).copy(alpha = 0.72f)
                when (template.gridMode) {
                    GridMode.None -> drawCircle(
                        color = accent,
                        radius = size.minDimension * 0.12f,
                        center = Offset(size.width / 2f, size.height / 2f)
                    )
                    GridMode.Lines -> {
                        var y = size.height * 0.22f
                        while (y < size.height * 0.85f) {
                            drawLine(
                                color = accent,
                                start = Offset(size.width * 0.16f, y),
                                end = Offset(size.width * 0.84f, y),
                                strokeWidth = 2f
                            )
                            y += size.height * 0.18f
                        }
                    }
                    GridMode.Dots -> {
                        var y = size.height * 0.24f
                        while (y < size.height * 0.82f) {
                            var x = size.width * 0.24f
                            while (x < size.width * 0.82f) {
                                drawCircle(color = accent, radius = 2.2f, center = Offset(x, y))
                                x += size.width * 0.18f
                            }
                            y += size.height * 0.18f
                        }
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(template.titleRes()),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.text
                )
                Text(
                    text = stringResource(template.subtitleRes()),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }
        }
    }
}
