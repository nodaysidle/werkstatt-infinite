package com.gift.werkstatt.feature.editor

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gift.werkstatt.R
import com.gift.werkstatt.core.design.LocalWerkstattColors
import com.gift.werkstatt.domain.canvas.model.BrushPresets
import com.gift.werkstatt.domain.canvas.model.BrushType
import com.gift.werkstatt.domain.canvas.model.CANVAS_HEIGHT
import com.gift.werkstatt.domain.canvas.model.CANVAS_WIDTH
import com.gift.werkstatt.domain.canvas.model.ColorPalettes
import com.gift.werkstatt.domain.canvas.model.GridMode
import com.gift.werkstatt.domain.canvas.model.PaletteFamily
import com.gift.werkstatt.feature.editor.components.DrawingCanvas
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun CanvasEditorScreen(
    state: CanvasEditorUiState,
    snackbarHostState: SnackbarHostState,
    onStrokeStart: (com.gift.werkstatt.domain.canvas.model.StrokePoint) -> Unit,
    onStrokeMove: (List<com.gift.werkstatt.domain.canvas.model.StrokePoint>) -> Unit,
    onStrokeEnd: () -> Unit,
    onGesture: (Float, Offset, Offset) -> Unit,
    onNavigateBack: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onCycleGrid: () -> Unit,
    onToggleEraser: () -> Unit,
    onBrushSelected: (BrushType) -> Unit,
    onBrushSizeChanged: (Float) -> Unit,
    onColorSelected: (Long) -> Unit,
    onEditTitle: () -> Unit,
    onDismissTitle: () -> Unit,
    onConfirmTitle: (String) -> Unit,
    onExport: () -> Unit
) {
    val colors = LocalWerkstattColors.current
    var showTools by remember { mutableStateOf(false) }

    BackHandler {
        onNavigateBack()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = colors.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(colors.background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                CanvasTopBar(
                    state = state,
                    onNavigateBack = onNavigateBack,
                    onTitleClick = onEditTitle,
                    onExport = onExport
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(8.dp),
                        color = colors.surfaceRaised.copy(alpha = 0.64f),
                        border = BorderStroke(1.dp, colors.textMuted.copy(alpha = 0.14f))
                    ) {
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val aspect = CANVAS_WIDTH / CANVAS_HEIGHT
                            val pageModifier = if (maxWidth / aspect <= maxHeight) {
                                Modifier.fillMaxWidth()
                            } else {
                                Modifier.fillMaxHeight()
                            }

                            Surface(
                                modifier = pageModifier.aspectRatio(aspect),
                                shape = RoundedCornerShape(8.dp),
                                color = colors.canvas,
                                shadowElevation = 10.dp,
                                border = BorderStroke(1.dp, colors.textMuted.copy(alpha = 0.13f))
                            ) {
                                DrawingCanvas(
                                    state = state,
                                    onStrokeStart = onStrokeStart,
                                    onStrokeMove = onStrokeMove,
                                    onStrokeEnd = onStrokeEnd,
                                    onGesture = onGesture
                                )
                            }
                        }
                    }
                }

                CanvasBottomBar(
                    state = state,
                    isToolsOpen = showTools,
                    onBrushClick = { showTools = true },
                    onUndo = onUndo,
                    onRedo = onRedo,
                    onCycleGrid = onCycleGrid,
                    onToggleEraser = onToggleEraser
                )
            }

            AnimatedVisibility(
                visible = showTools,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val interactionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(colors.background.copy(alpha = 0.52f))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { showTools = false }
                        )
                )
            }

            ToolPanel(
                visible = showTools,
                state = state,
                onDismiss = { showTools = false },
                onBrushSelected = onBrushSelected,
                onBrushSizeChanged = onBrushSizeChanged,
                onColorSelected = onColorSelected,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    state.titleEditValue?.let { title ->
        TitleDialog(
            title = title,
            onDismiss = onDismissTitle,
            onConfirm = onConfirmTitle
        )
    }
}

@Composable
private fun CanvasTopBar(
    state: CanvasEditorUiState,
    onNavigateBack: () -> Unit,
    onTitleClick: () -> Unit,
    onExport: () -> Unit
) {
    val colors = LocalWerkstattColors.current
    val title = state.entry?.title ?: stringResource(R.string.canvas_untitled)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(8.dp),
        color = colors.surface.copy(alpha = 0.92f),
        shadowElevation = 6.dp,
        border = BorderStroke(1.dp, colors.textMuted.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChromeButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.canvas_back),
                onClick = onNavigateBack
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onTitleClick)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(if (state.isSaving) colors.accent else colors.textMuted, CircleShape)
                    )
                    Text(
                        text = if (state.isSaving) {
                            stringResource(R.string.canvas_saving)
                        } else {
                            stringResource(R.string.canvas_saved_hint)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (state.isSaving) colors.accent else colors.textMuted
                    )
                }
            }

            ChromeButton(
                icon = Icons.Default.Share,
                contentDescription = stringResource(R.string.canvas_export),
                onClick = onExport
            )
        }
    }
}

@Composable
private fun CanvasBottomBar(
    state: CanvasEditorUiState,
    isToolsOpen: Boolean,
    onBrushClick: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onCycleGrid: () -> Unit,
    onToggleEraser: () -> Unit
) {
    val colors = LocalWerkstattColors.current
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(8.dp),
        color = colors.surface.copy(alpha = 0.92f),
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, colors.textMuted.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BrushSummary(
                state = state,
                isToolsOpen = isToolsOpen,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onBrushClick()
                },
                modifier = Modifier.weight(1f)
            )

            ToolTextButton(
                label = stringResource(R.string.canvas_undo),
                onClick = onUndo,
                enabled = state.strokes.isNotEmpty()
            )
            ToolTextButton(
                label = stringResource(R.string.canvas_redo),
                onClick = onRedo,
                enabled = state.canRedo
            )
            ToolIconButton(
                icon = Icons.AutoMirrored.Filled.List,
                contentDescription = stringResource(R.string.canvas_grid),
                active = state.gridMode != GridMode.None,
                onClick = onCycleGrid
            )
            ToolIconButton(
                icon = if (state.eraserMode) Icons.Default.Edit else Icons.Default.Delete,
                contentDescription = if (state.eraserMode) {
                    stringResource(R.string.canvas_draw_mode)
                } else {
                    stringResource(R.string.canvas_eraser)
                },
                active = state.eraserMode,
                onClick = onToggleEraser
            )
        }
    }
}

@Composable
private fun BrushSummary(
    state: CanvasEditorUiState,
    isToolsOpen: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalWerkstattColors.current
    val background = when {
        state.eraserMode -> colors.error.copy(alpha = 0.16f)
        isToolsOpen -> colors.accent.copy(alpha = 0.14f)
        else -> colors.surfaceRaised.copy(alpha = 0.68f)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = background,
        border = BorderStroke(1.dp, colors.textMuted.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        if (state.eraserMode) colors.error.copy(alpha = 0.16f) else Color(state.color),
                        CircleShape
                    )
                    .border(2.dp, colors.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (state.eraserMode) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = colors.error)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (state.eraserMode) {
                        stringResource(R.string.canvas_eraser_active)
                    } else {
                        stringResource(state.brushType.labelRes())
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = if (state.eraserMode) colors.error else colors.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (state.eraserMode) {
                        stringResource(R.string.canvas_draw_mode)
                    } else {
                        stringResource(R.string.canvas_px, state.brushSize.toInt())
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                    maxLines = 1
                )
            }
            Icon(
                imageVector = if (state.eraserMode) Icons.Default.Edit else Icons.Default.Settings,
                contentDescription = null,
                tint = if (isToolsOpen) colors.accent else colors.textMuted
            )
        }
    }
}

@Composable
private fun ChromeButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    val colors = LocalWerkstattColors.current
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = colors.surfaceRaised,
        border = BorderStroke(1.dp, colors.textMuted.copy(alpha = 0.1f))
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(44.dp)) {
            Icon(icon, contentDescription = contentDescription, tint = colors.text)
        }
    }
}

@Composable
private fun ToolIconButton(
    icon: ImageVector,
    contentDescription: String,
    active: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalWerkstattColors.current
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (active) colors.accent.copy(alpha = 0.16f) else colors.surfaceRaised,
        border = BorderStroke(1.dp, if (active) colors.accent.copy(alpha = 0.3f) else colors.textMuted.copy(alpha = 0.12f))
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(44.dp)) {
            Icon(icon, contentDescription = contentDescription, tint = if (active) colors.accent else colors.text)
        }
    }
}

@Composable
private fun ToolTextButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalWerkstattColors.current
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = colors.surfaceRaised,
        border = BorderStroke(1.dp, colors.textMuted.copy(alpha = 0.12f))
    ) {
        TextButton(
            onClick = onClick,
            enabled = enabled,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp),
            modifier = Modifier
                .widthIn(min = 44.dp)
                .height(44.dp),
            colors = ButtonDefaults.textButtonColors(
                contentColor = colors.text,
                disabledContentColor = colors.textMuted.copy(alpha = 0.42f)
            )
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
        }
    }
}

@Composable
private fun ToolPanel(
    visible: Boolean,
    state: CanvasEditorUiState,
    onDismiss: () -> Unit,
    onBrushSelected: (BrushType) -> Unit,
    onBrushSizeChanged: (Float) -> Unit,
    onColorSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalWerkstattColors.current
    var tab by remember { mutableIntStateOf(0) }
    val maxHeight = LocalConfiguration.current.screenHeightDp.dp * 0.72f

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .heightIn(max = maxHeight),
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
            color = colors.surface,
            shadowElevation = 24.dp,
            border = BorderStroke(1.dp, colors.textMuted.copy(alpha = 0.16f))
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(colors.textMuted.copy(alpha = 0.62f), RoundedCornerShape(2.dp))
                    )
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.canvas_tools), style = MaterialTheme.typography.titleLarge, color = colors.text)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Check, contentDescription = stringResource(R.string.dialog_save), tint = colors.accent)
                    }
                }
                Spacer(Modifier.height(12.dp))
                SegmentedTabs(
                    selectedIndex = tab,
                    onSelected = { tab = it },
                    labels = listOf(
                        stringResource(R.string.canvas_tab_brush),
                        stringResource(R.string.canvas_tab_size),
                        stringResource(R.string.canvas_tab_color)
                    )
                )
                Spacer(Modifier.height(18.dp))
                when (tab) {
                    0 -> BrushPicker(state.brushType, state.brushSize, onBrushSelected)
                    1 -> SizePicker(state.brushSize, state.color, onBrushSizeChanged)
                    2 -> ColorPicker(state.color, state.recentColors, onColorSelected)
                }
            }
        }
    }
}

@Composable
private fun SegmentedTabs(
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    labels: List<String>
) {
    val colors = LocalWerkstattColors.current
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = colors.surfaceRaised,
        border = BorderStroke(1.dp, colors.textMuted.copy(alpha = 0.14f))
    ) {
        Row(modifier = Modifier.padding(6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            labels.forEachIndexed { index, label ->
                val active = index == selectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (active) colors.accent.copy(alpha = 0.16f) else Color.Transparent)
                        .clickable { onSelected(index) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (active) colors.accent else colors.textMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun BrushPicker(
    selected: BrushType,
    currentSize: Float,
    onSelected: (BrushType) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        BrushType.entries.forEach { brush ->
            val colors = LocalWerkstattColors.current
            val active = brush == selected
            val preset = BrushPresets.forType(brush)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = if (active) colors.accent.copy(alpha = 0.14f) else colors.surfaceRaised,
                border = BorderStroke(1.dp, if (active) colors.accent.copy(alpha = 0.45f) else colors.textMuted.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier
                        .clickable { onSelected(brush) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    BrushPreview(brush, active)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(brush.labelRes()), style = MaterialTheme.typography.titleMedium, color = if (active) colors.accent else colors.text)
                        Text(stringResource(brush.descriptionRes()), style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
                    }
                    Text(
                        text = stringResource(R.string.canvas_px, (if (active) currentSize else preset.size).toInt()),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (active) colors.accent else colors.textMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun BrushPreview(brush: BrushType, active: Boolean) {
    val colors = LocalWerkstattColors.current
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = colors.background.copy(alpha = 0.66f),
        border = BorderStroke(1.dp, colors.textMuted.copy(alpha = 0.12f))
    ) {
        Canvas(modifier = Modifier.size(54.dp).padding(12.dp)) {
            drawLine(
                color = (if (active) colors.accent else colors.text).copy(
                    alpha = if (brush == BrushType.Watercolor) 0.58f else 1f
                ),
                start = Offset(4f, size.height - 4f),
                end = Offset(size.width - 4f, 4f),
                strokeWidth = when (brush) {
                    BrushType.Fine -> 1.5f
                    BrushType.Marker -> 8f
                    BrushType.Watercolor -> 7f
                    BrushType.Brush -> 5f
                    else -> 3.2f
                },
                cap = if (brush == BrushType.Marker) StrokeCap.Square else StrokeCap.Round
            )
        }
    }
}

@Composable
private fun SizePicker(
    size: Float,
    color: Long,
    onChanged: (Float) -> Unit
) {
    val colors = LocalWerkstattColors.current
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = colors.surfaceRaised,
        border = BorderStroke(1.dp, colors.textMuted.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.canvas_stroke_size), style = MaterialTheme.typography.titleMedium, color = colors.text)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(size.coerceIn(8f, 40f).dp)
                            .background(Color(color), CircleShape)
                    )
                    Text(stringResource(R.string.canvas_px, size.toInt()), style = MaterialTheme.typography.titleSmall, color = colors.text)
                }
            }
            Spacer(Modifier.height(14.dp))
            Slider(
                value = size,
                onValueChange = onChanged,
                valueRange = 1f..50f,
                colors = SliderDefaults.colors(
                    thumbColor = colors.accent,
                    activeTrackColor = colors.accent,
                    inactiveTrackColor = colors.textMuted.copy(alpha = 0.22f)
                )
            )
        }
    }
}

@Composable
private fun ColorPicker(
    currentColor: Long,
    recentColors: List<Long>,
    onColorSelected: (Long) -> Unit
) {
    val colors = LocalWerkstattColors.current
    val hsv = remember(currentColor) {
        FloatArray(3).also { android.graphics.Color.colorToHSV(currentColor.toInt(), it) }
    }
    var hue by remember(currentColor) { mutableFloatStateOf(hsv[0]) }
    var saturation by remember(currentColor) { mutableFloatStateOf(hsv[1]) }
    var brightness by remember(currentColor) { mutableFloatStateOf(hsv[2]) }
    val colorHex = remember(currentColor) { String.format("#%06X", 0xFFFFFF and currentColor.toInt()) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = colors.surfaceRaised,
            border = BorderStroke(1.dp, colors.textMuted.copy(alpha = 0.12f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(currentColor), RoundedCornerShape(8.dp))
                        .border(1.dp, colors.textMuted.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                )
                Column {
                    Text(stringResource(R.string.color_current), style = MaterialTheme.typography.labelLarge, color = colors.text)
                    Text(colorHex, style = MaterialTheme.typography.titleSmall, color = colors.text)
                }
            }
        }

        HueWheel(
            hue = hue,
            onHueChanged = {
                hue = it
                onColorSelected(hsvToLong(hue, saturation, brightness))
            }
        )
        ValueSlider(
            label = stringResource(R.string.color_saturation),
            value = saturation,
            hue = hue,
            saturationMode = true,
            onChanged = {
                saturation = it
                onColorSelected(hsvToLong(hue, saturation, brightness))
            }
        )
        ValueSlider(
            label = stringResource(R.string.color_brightness),
            value = brightness,
            hue = hue,
            saturationMode = false,
            onChanged = {
                brightness = it
                onColorSelected(hsvToLong(hue, saturation, brightness))
            }
        )
        if (recentColors.isNotEmpty()) {
            SwatchRow(stringResource(R.string.color_recent), recentColors, currentColor, onColorSelected)
        }
        PaletteFamily.entries.forEach { family ->
            SwatchRow(stringResource(family.labelRes()), ColorPalettes.colorsFor(family), currentColor, onColorSelected)
        }
    }
}

@Composable
private fun HueWheel(
    hue: Float,
    onHueChanged: (Float) -> Unit
) {
    val colors = LocalWerkstattColors.current
    val wheelDescription = stringResource(R.string.content_color_wheel)
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = colors.surfaceRaised,
        border = BorderStroke(1.dp, colors.textMuted.copy(alpha = 0.12f))
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
            var center by remember { mutableStateOf(Offset.Zero) }
            Canvas(
                modifier = Modifier
                    .size(220.dp)
                    .semantics { contentDescription = wheelDescription }
                    .clickable { }
                    .pointerHueInput(center, onHueChanged)
            ) {
                center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension / 2f - 18f
                drawCircle(
                    brush = Brush.sweepGradient(
                        colorStops = (0..360 step 60)
                            .map { it / 360f to Color.hsv(it.toFloat(), 1f, 1f) }
                            .toTypedArray()
                    ),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 34f)
                )
                drawCircle(colors.surface, radius - 28f, center)
                val angle = Math.toRadians((hue - 90f).toDouble())
                val indicator = Offset(
                    center.x + radius * cos(angle).toFloat(),
                    center.y + radius * sin(angle).toFloat()
                )
                drawCircle(Color.White, 16f, indicator)
                drawCircle(Color.hsv(hue, 1f, 1f), 9f, indicator)
                drawCircle(colors.text.copy(alpha = 0.8f), 16f, indicator, style = Stroke(width = 2f))
            }
        }
    }
}

private fun Modifier.pointerHueInput(
    center: Offset,
    onHueChanged: (Float) -> Unit
): Modifier {
    return pointerInput(center) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                val change = event.changes.firstOrNull() ?: continue
                if (change.pressed) {
                    val dx = change.position.x - center.x
                    val dy = change.position.y - center.y
                    var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat() + 90f
                    if (angle < 0f) angle += 360f
                    onHueChanged(angle)
                    change.consume()
                }
            }
        }
    }
}

@Composable
private fun ValueSlider(
    label: String,
    value: Float,
    hue: Float,
    saturationMode: Boolean,
    onChanged: (Float) -> Unit
) {
    val colors = LocalWerkstattColors.current
    val sliderDescription = if (saturationMode) {
        stringResource(R.string.content_saturation_slider)
    } else {
        stringResource(R.string.content_brightness_slider)
    }
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = colors.text)
            Text("${(value * 100).roundToInt()}%", style = MaterialTheme.typography.labelMedium, color = colors.accent)
        }
        Spacer(Modifier.height(8.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .semantics { contentDescription = sliderDescription }
                .pointerValueInput(onChanged)
        ) {
            val gradient = if (saturationMode) {
                listOf(Color.hsv(hue, 0f, 1f), Color.hsv(hue, 1f, 1f))
            } else {
                listOf(Color.hsv(hue, 1f, 0f), Color.hsv(hue, 1f, 1f))
            }
            drawRoundRect(
                brush = Brush.horizontalGradient(gradient),
                cornerRadius = CornerRadius(16f, 16f)
            )
            val x = value * size.width
            drawCircle(Color.White, 13f, Offset(x, size.height / 2f))
            drawCircle(colors.text, 13f, Offset(x, size.height / 2f), style = Stroke(width = 2f))
        }
    }
}

private fun Modifier.pointerValueInput(onChanged: (Float) -> Unit): Modifier {
    return pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                val change = event.changes.firstOrNull() ?: continue
                if (change.pressed) {
                    onChanged((change.position.x / size.width).coerceIn(0f, 1f))
                    change.consume()
                }
            }
        }
    }
}

@Composable
private fun SwatchRow(
    title: String,
    swatches: List<Long>,
    selected: Long,
    onColorSelected: (Long) -> Unit
) {
    val colors = LocalWerkstattColors.current
    Column {
        Text(title, style = MaterialTheme.typography.labelLarge, color = colors.text)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            swatches.take(8).forEach { color ->
                val active = color == selected
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(color), RoundedCornerShape(8.dp))
                        .border(
                            width = if (active) 3.dp else 1.dp,
                            color = if (active) colors.accent else colors.textMuted.copy(alpha = 0.32f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onColorSelected(color) },
                    contentAlignment = Alignment.Center
                ) {
                    if (active) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = colors.background, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TitleDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var nextTitle by remember(title) { mutableStateOf(title) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_edit_title)) },
        text = {
            OutlinedTextField(
                value = nextTitle,
                onValueChange = { nextTitle = it },
                label = { Text(stringResource(R.string.dialog_canvas_title)) },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(nextTitle) }) {
                Text(stringResource(R.string.dialog_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}

private fun hsvToLong(hue: Float, saturation: Float, brightness: Float): Long {
    return android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, brightness)).toLong() and 0xFFFFFFFFL
}
