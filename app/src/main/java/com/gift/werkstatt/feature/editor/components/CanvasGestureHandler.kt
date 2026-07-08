package com.gift.werkstatt.feature.editor.components

import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import com.gift.werkstatt.domain.canvas.model.StrokePoint
import com.gift.werkstatt.feature.editor.CanvasEditorUiState
import com.gift.werkstatt.rendering.transform.CanvasTransformMapper
import com.gift.werkstatt.rendering.transform.ViewportTransform
import kotlin.math.sqrt

class CanvasGestureHandler(
    private val mapper: CanvasTransformMapper = CanvasTransformMapper()
) {
    private var isMultiTouch = false
    private var lastPinchDistance = 0f
    private var lastCentroid = Offset.Zero

    fun handleMotionEvent(
        event: MotionEvent,
        canvasSize: Offset,
        state: CanvasEditorUiState,
        onStrokeStart: (StrokePoint) -> Unit,
        onStrokeMove: (List<StrokePoint>) -> Unit,
        onStrokeEnd: () -> Unit,
        onGesture: (Float, Offset, Offset) -> Unit
    ): Boolean {
        val action = event.actionMasked

        if (event.pointerCount >= 2) {
            return handleMultiTouch(event, action, onStrokeEnd, onGesture)
        }

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            if (isMultiTouch) {
                isMultiTouch = false
                lastPinchDistance = 0f
                return true
            }
        }

        if (isMultiTouch) return true

        return handleSingleTouch(event, action, canvasSize, state, onStrokeStart, onStrokeMove, onStrokeEnd)
    }

    private fun handleMultiTouch(
        event: MotionEvent,
        action: Int,
        onStrokeEnd: () -> Unit,
        onGesture: (Float, Offset, Offset) -> Unit
    ): Boolean {
        return when (action) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                onStrokeEnd()
                isMultiTouch = true
                lastPinchDistance = event.pinchDistance()
                lastCentroid = event.centroid()
                true
            }
            MotionEvent.ACTION_MOVE -> {
                val nextDistance = event.pinchDistance()
                val nextCentroid = event.centroid()
                if (lastPinchDistance > 0f) {
                    val zoomChange = nextDistance / lastPinchDistance
                    onGesture(zoomChange, nextCentroid - lastCentroid, nextCentroid)
                }
                lastPinchDistance = nextDistance
                lastCentroid = nextCentroid
                true
            }
            MotionEvent.ACTION_POINTER_UP -> true
            else -> false
        }
    }

    private fun handleSingleTouch(
        event: MotionEvent,
        action: Int,
        canvasSize: Offset,
        state: CanvasEditorUiState,
        onStrokeStart: (StrokePoint) -> Unit,
        onStrokeMove: (List<StrokePoint>) -> Unit,
        onStrokeEnd: () -> Unit
    ): Boolean {
        val point = mapper.screenToCanvas(
            screen = Offset(event.x, event.y),
            drawSize = canvasSize,
            transform = ViewportTransform(state.zoom, state.viewportOffset)
        )
        val pressure = event.pressure.coerceIn(0.1f, 1f)

        return when (action) {
            MotionEvent.ACTION_DOWN -> {
                onStrokeStart(StrokePoint(point.x, point.y, pressure))
                true
            }
            MotionEvent.ACTION_MOVE -> {
                val points = buildHistoricalPoints(event, canvasSize, state, point, pressure)
                onStrokeMove(points)
                true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                onStrokeEnd()
                true
            }
            else -> false
        }
    }

    private fun buildHistoricalPoints(
        event: MotionEvent,
        canvasSize: Offset,
        state: CanvasEditorUiState,
        currentPoint: Offset,
        currentPressure: Float
    ): List<StrokePoint> {
        val transform = ViewportTransform(state.zoom, state.viewportOffset)
        return buildList {
            for (index in 0 until event.historySize) {
                val historical = mapper.screenToCanvas(
                    screen = Offset(event.getHistoricalX(index), event.getHistoricalY(index)),
                    drawSize = canvasSize,
                    transform = transform
                )
                add(
                    StrokePoint(
                        historical.x,
                        historical.y,
                        event.getHistoricalPressure(index).coerceIn(0.1f, 1f)
                    )
                )
            }
            add(StrokePoint(currentPoint.x, currentPoint.y, currentPressure))
        }
    }

    internal companion object {
        fun centroid(event: MotionEvent): Offset = Offset(
            x = (event.getX(0) + event.getX(1)) * 0.5f,
            y = (event.getY(0) + event.getY(1)) * 0.5f
        )

        fun pinchDistance(event: MotionEvent): Float {
            val dx = event.getX(0) - event.getX(1)
            val dy = event.getY(0) - event.getY(1)
            return sqrt(dx * dx + dy * dy)
        }
    }
}

private fun MotionEvent.centroid(): Offset = CanvasGestureHandler.centroid(this)
private fun MotionEvent.pinchDistance(): Float = CanvasGestureHandler.pinchDistance(this)
