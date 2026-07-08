package com.gift.werkstatt.feature.editor.components

import android.graphics.Paint
import android.graphics.Path
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import com.gift.werkstatt.core.design.LocalWerkstattColors
import com.gift.werkstatt.domain.canvas.model.CANVAS_HEIGHT
import com.gift.werkstatt.domain.canvas.model.CANVAS_WIDTH
import com.gift.werkstatt.domain.canvas.model.GridMode
import com.gift.werkstatt.domain.canvas.model.Stroke
import com.gift.werkstatt.domain.canvas.model.StrokePoint
import com.gift.werkstatt.domain.canvas.model.isFastPath
import com.gift.werkstatt.feature.editor.CanvasEditorUiState
import com.gift.werkstatt.rendering.brush.StrokeRenderer
import kotlin.math.ceil

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawingCanvas(
    state: CanvasEditorUiState,
    onStrokeStart: (StrokePoint) -> Unit,
    onStrokeMove: (List<StrokePoint>) -> Unit,
    onStrokeEnd: () -> Unit,
    onGesture: (Float, Offset, Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalWerkstattColors.current
    val renderer = remember { StrokeRenderer() }
    val gestureHandler = remember { CanvasGestureHandler() }
    val androidPaint = remember { Paint(Paint.ANTI_ALIAS_FLAG) }
    val androidPath = remember { Path() }
    var canvasSize by remember { mutableStateOf(Offset.Zero) }
    val dotGridPoints = remember(canvasSize, state.gridMode, state.gridSize, state.zoom) {
        buildDotGridPoints(
            mode = state.gridMode,
            gridSize = state.gridSize,
            zoom = state.zoom,
            canvasSize = canvasSize
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(colors.canvas)
            .clipToBounds()
            .onSizeChanged { canvasSize = Offset(it.width.toFloat(), it.height.toFloat()) }
            .pointerInteropFilter { event ->
                gestureHandler.handleMotionEvent(
                    event = event,
                    canvasSize = canvasSize,
                    state = state,
                    onStrokeStart = onStrokeStart,
                    onStrokeMove = onStrokeMove,
                    onStrokeEnd = onStrokeEnd,
                    onGesture = onGesture
                )
            }
    ) {
        withTransform({
            translate(left = state.viewportOffset.x, top = state.viewportOffset.y)
            scale(scaleX = state.zoom, scaleY = state.zoom, pivot = Offset.Zero)
        }) {
            val scaleX = size.width / CANVAS_WIDTH
            val scaleY = size.height / CANVAS_HEIGHT
            drawGrid(state.gridMode, state.gridSize, scaleX, scaleY, colors.grid, dotGridPoints)

            state.strokes.forEach { stroke: Stroke ->
                if (stroke.brushType.isFastPath) {
                    drawIntoCanvas { canvas ->
                        renderer.drawAndroidStroke(canvas.nativeCanvas, stroke, scaleX, scaleY, androidPaint, androidPath)
                    }
                } else {
                    with(renderer) {
                        drawStroke(stroke, scaleX, scaleY)
                    }
                }
            }

            if (state.currentStroke.isNotEmpty()) {
                if (state.brushType.isFastPath) {
                    drawIntoCanvas { canvas ->
                        renderer.drawAndroidStroke(
                            canvas = canvas.nativeCanvas,
                            points = state.currentStroke,
                            color = state.color,
                            width = state.brushSize,
                            brushType = state.brushType,
                            opacity = state.brushOpacity,
                            scaleX = scaleX,
                            scaleY = scaleY,
                            paint = androidPaint,
                            path = androidPath
                        )
                    }
                } else {
                    with(renderer) {
                        drawStroke(
                            points = state.currentStroke,
                            color = Color(state.color),
                            width = state.brushSize,
                            brushType = state.brushType,
                            opacity = state.brushOpacity,
                            scaleX = scaleX,
                            scaleY = scaleY
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawGrid(
    mode: GridMode,
    gridSize: Float,
    scaleX: Float,
    scaleY: Float,
    color: Color,
    dotGridPoints: List<Offset>
) {
    when (mode) {
        GridMode.None -> Unit
        GridMode.Lines -> {
            var x = 0f
            while (x <= CANVAS_WIDTH) {
                drawLine(color, Offset(x * scaleX, 0f), Offset(x * scaleX, CANVAS_HEIGHT * scaleY), 0.7f)
                x += gridSize
            }
            var y = 0f
            while (y <= CANVAS_HEIGHT) {
                drawLine(color, Offset(0f, y * scaleY), Offset(CANVAS_WIDTH * scaleX, y * scaleY), 0.7f)
                y += gridSize
            }
        }
        GridMode.Dots -> {
            drawPoints(
                points = dotGridPoints,
                pointMode = PointMode.Points,
                color = color,
                strokeWidth = 2.4f,
                cap = StrokeCap.Square
            )
        }
    }
}

private fun buildDotGridPoints(
    mode: GridMode,
    gridSize: Float,
    zoom: Float,
    canvasSize: Offset
): List<Offset> {
    if (mode != GridMode.Dots || canvasSize.x <= 0f || canvasSize.y <= 0f) return emptyList()
    val scaleX = canvasSize.x / CANVAS_WIDTH
    val scaleY = canvasSize.y / CANVAS_HEIGHT
    val effectiveGrid = gridSize * gridStepMultiplier(gridSize, zoom, scaleX, scaleY)
    val points = ArrayList<Offset>(500)
    var x = 0f
    while (x <= CANVAS_WIDTH) {
        var y = 0f
        while (y <= CANVAS_HEIGHT) {
            points.add(Offset(x * scaleX, y * scaleY))
            y += effectiveGrid
        }
        x += effectiveGrid
    }
    return points
}

private fun gridStepMultiplier(gridSize: Float, zoom: Float, scaleX: Float, scaleY: Float): Float {
    val screenStep = gridSize * minOf(scaleX, scaleY) * zoom
    return ceil(56f / screenStep.coerceAtLeast(1f)).coerceAtLeast(1f)
}
