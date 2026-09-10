package my.edu.aiu.app.tdm_insight_android.features.graph

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import my.edu.aiu.app.tdm_insight_android.engine.VancomycinEngine
import my.edu.aiu.app.tdm_insight_android.model.TdmResult

/**
 * OWNER: Benat
 *
 * A custom Canvas component that plots the concentration-time curve.
 */
@Composable
fun ConcentrationGraph(
    result: TdmResult,
    intervalHours: Double,
    infusionHours: Double,
    modifier: Modifier = Modifier
) {
    val points = VancomycinEngine.curvePoints(result, intervalHours, infusionHours)
    
    // Fix the Y-axis to a stable clinical range (0 to 60+ mg/L)
    // This ensures the curve moves VISIBLY when the dose changes.
    val maxConc = 60.0.coerceAtLeast(result.peakAtEndOfInfusion * 1.1)
    val maxTime = intervalHours

    val axisColor = Color.Gray
    val curveColor = Color(0xFF00796B) // EmeraldGreen

    Box(modifier = modifier.padding(16.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Coordinate conversion helpers
            fun x(t: Double) = (t / maxTime * width).toFloat()
            fun y(c: Double) = (height - (c / maxConc * height)).toFloat()

            // 1. Draw Target Ranges (Reference Bands)
            // Trough Target (15-20 mg/L) - Green Tint
            drawRect(
                color = Color(0xFFE8F5E9),
                topLeft = Offset(0f, y(20.0)),
                size = androidx.compose.ui.geometry.Size(width, y(15.0) - y(20.0))
            )
            // Peak Target (30-40 mg/L) - Yellow Tint
            drawRect(
                color = Color(0xFFFFFDE7),
                topLeft = Offset(0f, y(40.0)),
                size = androidx.compose.ui.geometry.Size(width, y(30.0) - y(40.0))
            )

            // 2. Draw Axes
            drawLine(
                color = axisColor,
                start = Offset(0f, height),
                end = Offset(width, height),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = axisColor,
                start = Offset(0f, 0f),
                end = Offset(0f, height),
                strokeWidth = 1.dp.toPx()
            )

            // 3. Draw the Curve
            if (points.isNotEmpty()) {
                val path = Path().apply {
                    moveTo(x(points[0].first), y(points[0].second))
                    for (i in 1 until points.size) {
                        lineTo(x(points[i].first), y(points[i].second))
                    }
                }
                drawPath(
                    path = path,
                    color = curveColor,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }
    }
}
