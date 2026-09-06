package com.aiu.tdminsight.features.graph

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp

/**
 * ConcentrationGraph.kt
 * Owner: Benat
 *
 * Renders the vancomycin concentration-vs-time curve for a patient case,
 * with an optional shaded therapeutic range band, using plain Compose
 * Canvas (no external charting library — keeps this file dependency-free
 * so it doesn't need a build.gradle.kts change).
 *
 * This component is display-only: it takes already-calculated points
 * (from Yalda's engine) and draws them. It does NOT calculate
 * concentrations itself.
 */

/**
 * A single point on the concentration-time curve.
 *
 * @param timeHours time since the reference dose, in hours
 * @param concentrationMgL predicted or measured drug concentration, in mg/L
 */
data class ConcentrationPoint(
    val timeHours: Double,
    val concentrationMgL: Double
)

@Composable
fun ConcentrationGraph(
    points: List<ConcentrationPoint>,
    modifier: Modifier = Modifier,
    therapeuticRangeLow: Double? = null,
    therapeuticRangeHigh: Double? = null,
    title: String = "Concentration over Time"
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (points.isEmpty()) {
            Text(
                text = "No concentration data available yet.",
                style = MaterialTheme.typography.bodyMedium
            )
            return@Column
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.6f)
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().aspectRatio(1.6f)) {
                drawConcentrationChart(
                    points = points,
                    therapeuticRangeLow = therapeuticRangeLow,
                    therapeuticRangeHigh = therapeuticRangeHigh
                )
            }
        }
    }
}

private fun DrawScope.drawConcentrationChart(
    points: List<ConcentrationPoint>,
    therapeuticRangeLow: Double?,
    therapeuticRangeHigh: Double?
) {
    val leftPadding = 56f
    val bottomPadding = 40f
    val topPadding = 16f
    val rightPadding = 16f

    val chartWidth = size.width - leftPadding - rightPadding
    val chartHeight = size.height - topPadding - bottomPadding

    val minTime = points.minOf { it.timeHours }
    val maxTime = points.maxOf { it.timeHours }.let { if (it == minTime) it + 1 else it }

    val dataMaxConcentration = points.maxOf { it.concentrationMgL }
    val rangeCeiling = therapeuticRangeHigh ?: 0.0
    val maxConcentration = maxOf(dataMaxConcentration, rangeCeiling) * 1.15

    fun xFor(timeHours: Double): Float {
        val fraction = (timeHours - minTime) / (maxTime - minTime)
        return leftPadding + (fraction * chartWidth).toFloat()
    }

    fun yFor(concentration: Double): Float {
        val fraction = concentration / maxConcentration
        return topPadding + chartHeight - (fraction * chartHeight).toFloat()
    }

    // Axes
    drawLine(
        color = Color.Gray,
        start = Offset(leftPadding, topPadding),
        end = Offset(leftPadding, topPadding + chartHeight),
        strokeWidth = 2f
    )
    drawLine(
        color = Color.Gray,
        start = Offset(leftPadding, topPadding + chartHeight),
        end = Offset(leftPadding + chartWidth, topPadding + chartHeight),
        strokeWidth = 2f
    )

    // Therapeutic range band, if provided
    if (therapeuticRangeLow != null && therapeuticRangeHigh != null) {
        val yLow = yFor(therapeuticRangeLow)
        val yHigh = yFor(therapeuticRangeHigh)
        drawRect(
            color = Color(0xFF4CAF50).copy(alpha = 0.15f),
            topLeft = Offset(leftPadding, yHigh),
            size = androidx.compose.ui.geometry.Size(chartWidth, yLow - yHigh)
        )
    }

    // Y-axis labels (5 gridlines)
    val ySteps = 5
    for (i in 0..ySteps) {
        val value = (maxConcentration / ySteps) * i
        val y = yFor(value)
        drawLine(
            color = Color.LightGray,
            start = Offset(leftPadding, y),
            end = Offset(leftPadding + chartWidth, y),
            strokeWidth = 1f
        )
        drawContext.canvas.nativeCanvas.drawText(
            "%.0f".format(value),
            8f,
            y + 8f,
            android.graphics.Paint().apply {
                textSize = 24f
                color = android.graphics.Color.GRAY
            }
        )
    }

    // X-axis labels (start, middle, end)
    val xLabelValues = listOf(minTime, (minTime + maxTime) / 2, maxTime)
    xLabelValues.forEach { t ->
        val x = xFor(t)
        drawContext.canvas.nativeCanvas.drawText(
            "%.0fh".format(t),
            x - 12f,
            size.height - 12f,
            android.graphics.Paint().apply {
                textSize = 24f
                color = android.graphics.Color.GRAY
                textAlign = android.graphics.Paint.Align.CENTER
            }
        )
    }

    // The concentration curve itself
    val sortedPoints = points.sortedBy { it.timeHours }
    for (i in 0 until sortedPoints.size - 1) {
        val start = sortedPoints[i]
        val end = sortedPoints[i + 1]
        drawLine(
            color = ChartPrimaryColor,
            start = Offset(xFor(start.timeHours), yFor(start.concentrationMgL)),
            end = Offset(xFor(end.timeHours), yFor(end.concentrationMgL)),
            strokeWidth = 5f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }

    // Data point markers
    sortedPoints.forEach { point ->
        drawCircle(
            color = ChartPrimaryColor,
            radius = 8f,
            center = Offset(xFor(point.timeHours), yFor(point.concentrationMgL))
        )
    }
}

// A fixed color used for the curve/markers. Kept as a plain constant since
// DrawScope (used inside Canvas) does not have direct access to
// @Composable MaterialTheme lookups.
private val ChartPrimaryColor = Color(0xFF2962FF)