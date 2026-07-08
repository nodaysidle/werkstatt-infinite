package com.gift.werkstatt.rendering.brush

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke as ComposeStroke
import com.gift.werkstatt.domain.canvas.model.BrushType
import com.gift.werkstatt.domain.canvas.model.Stroke
import com.gift.werkstatt.domain.canvas.model.StrokePoint
import javax.inject.Inject
import kotlin.math.sqrt

class StrokeRenderer @Inject constructor() {
    fun DrawScope.drawStroke(
        stroke: Stroke,
        scaleX: Float,
        scaleY: Float
    ) {
        drawStroke(
            points = stroke.points,
            color = Color(stroke.color),
            width = stroke.width,
            brushType = stroke.brushType,
            opacity = stroke.opacity,
            scaleX = scaleX,
            scaleY = scaleY
        )
    }

    fun DrawScope.drawStroke(
        points: List<StrokePoint>,
        color: Color,
        width: Float,
        brushType: BrushType,
        opacity: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        if (points.isEmpty()) return
        val scaledWidth = (width * scaleX).coerceAtLeast(1f)
        val strokeColor = color.copy(alpha = opacity)
        if (drawSinglePoint(points, strokeColor, scaledWidth, scaleX, scaleY)) return

        when (brushType) {
            BrushType.Pen,
            BrushType.Fine,
            BrushType.Ballpoint -> drawPathStroke(points, strokeColor, scaledWidth, scaleX, scaleY)
            BrushType.Pencil -> {
                drawPathStroke(points, strokeColor.copy(alpha = strokeColor.alpha * 0.68f), scaledWidth, scaleX, scaleY)
                drawPathStroke(points, strokeColor.copy(alpha = strokeColor.alpha * 0.22f), scaledWidth * 0.62f, scaleX, scaleY)
            }
            BrushType.Marker -> drawPathStroke(
                points = points,
                color = strokeColor,
                width = scaledWidth * 1.45f,
                scaleX = scaleX,
                scaleY = scaleY,
                cap = StrokeCap.Square,
                join = StrokeJoin.Bevel
            )
            BrushType.Watercolor -> {
                drawPathStroke(points, strokeColor.copy(alpha = strokeColor.alpha * 0.22f), scaledWidth * 1.9f, scaleX, scaleY)
                drawPathStroke(points, strokeColor, scaledWidth, scaleX, scaleY)
            }
            BrushType.Ink -> drawVariableStroke(points, strokeColor, scaledWidth, scaleX, scaleY)
            BrushType.Brush -> drawTaperedStroke(points, strokeColor, scaledWidth, scaleX, scaleY)
        }
    }

    fun drawAndroidStroke(
        canvas: Canvas,
        stroke: Stroke,
        scaleX: Float,
        scaleY: Float,
        paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG),
        path: Path = Path()
    ) {
        drawAndroidStroke(
            canvas = canvas,
            points = stroke.points,
            color = stroke.color,
            width = stroke.width,
            brushType = stroke.brushType,
            opacity = stroke.opacity,
            scaleX = scaleX,
            scaleY = scaleY,
            paint = paint,
            path = path
        )
    }

    fun drawAndroidStroke(
        canvas: Canvas,
        points: List<StrokePoint>,
        color: Long,
        width: Float,
        brushType: BrushType,
        opacity: Float,
        scaleX: Float,
        scaleY: Float,
        paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG),
        path: Path = Path()
    ) {
        if (points.isEmpty()) return
        val scaledWidth = (width * scaleX).coerceAtLeast(1f)
        paint.reset()
        paint.isAntiAlias = true
        paint.color = color.toInt()
        paint.alpha = (opacity.coerceIn(0f, 1f) * 255).toInt()
        paint.style = Paint.Style.STROKE
        paint.strokeCap = if (brushType == BrushType.Marker) Paint.Cap.SQUARE else Paint.Cap.ROUND
        paint.strokeJoin = if (brushType == BrushType.Marker) Paint.Join.BEVEL else Paint.Join.ROUND
        paint.strokeWidth = if (brushType == BrushType.Marker) scaledWidth * 1.45f else scaledWidth

        if (points.size == 1) {
            paint.style = Paint.Style.FILL
            val point = points.first()
            canvas.drawCircle(point.x * scaleX, point.y * scaleY, scaledWidth / 2f, paint)
            return
        }

        path.reset()
        path.moveTo(points.first().x * scaleX, points.first().y * scaleY)
        for (index in 1 until points.size) {
            val previous = points[index - 1]
            val current = points[index]
            val midX = (previous.x + current.x) * 0.5f * scaleX
            val midY = (previous.y + current.y) * 0.5f * scaleY
            path.quadTo(previous.x * scaleX, previous.y * scaleY, midX, midY)
        }
        val last = points.last()
        path.lineTo(last.x * scaleX, last.y * scaleY)
        canvas.drawPath(path, paint)
    }

    private fun DrawScope.drawPathStroke(
        points: List<StrokePoint>,
        color: Color,
        width: Float,
        scaleX: Float,
        scaleY: Float,
        cap: StrokeCap = StrokeCap.Round,
        join: StrokeJoin = StrokeJoin.Round
    ) {
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(points.first().x * scaleX, points.first().y * scaleY)
            for (index in 1 until points.size) {
                val previous = points[index - 1]
                val current = points[index]
                quadraticTo(
                    previous.x * scaleX,
                    previous.y * scaleY,
                    (previous.x + current.x) * 0.5f * scaleX,
                    (previous.y + current.y) * 0.5f * scaleY
                )
            }
            val last = points.last()
            lineTo(last.x * scaleX, last.y * scaleY)
        }
        drawPath(
            path = path,
            color = color,
            style = ComposeStroke(width = width, cap = cap, join = join)
        )
    }

    private fun DrawScope.drawSinglePoint(
        points: List<StrokePoint>,
        color: Color,
        width: Float,
        scaleX: Float,
        scaleY: Float
    ): Boolean {
        if (points.size != 1) return false
        val point = points.first()
        drawCircle(
            color = color,
            radius = (width / 2f).coerceAtLeast(1f),
            center = Offset(point.x * scaleX, point.y * scaleY)
        )
        return true
    }

/**
     * Speed-sensitive stroke that varies width based on draw velocity.
     * Fast strokes are thinner; slow strokes are wider.
     * The 70f threshold controls speed sensitivity — lower = more responsive.
     */
    private fun DrawScope.drawVariableStroke(
        points: List<StrokePoint>,
        color: Color,
        width: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        for (index in 1 until points.size) {
            val previous = points[index - 1]
            val current = points[index]
            val dx = current.x - previous.x
            val dy = current.y - previous.y
            val distance = sqrt(dx * dx + dy * dy)
            val speedFactor = 1f - (distance / 70f).coerceIn(0f, 0.72f)
            drawLine(
                color = color,
                start = Offset(previous.x * scaleX, previous.y * scaleY),
                end = Offset(current.x * scaleX, current.y * scaleY),
                strokeWidth = width * (0.45f + speedFactor * 0.75f),
                cap = StrokeCap.Round
            )
        }
    }

    private fun DrawScope.drawTaperedStroke(
        points: List<StrokePoint>,
        color: Color,
        width: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        val total = points.size.coerceAtLeast(1)
        for (index in 1 until points.size) {
            val progress = index.toFloat() / total
            val taper = when {
                progress < 0.12f -> progress / 0.12f
                progress > 0.88f -> (1f - progress) / 0.12f
                else -> 1f
            }.coerceIn(0.32f, 1f)
            val previous = points[index - 1]
            val current = points[index]
            drawLine(
                color = color,
                start = Offset(previous.x * scaleX, previous.y * scaleY),
                end = Offset(current.x * scaleX, current.y * scaleY),
                strokeWidth = width * taper,
                cap = StrokeCap.Round
            )
        }
    }
}
