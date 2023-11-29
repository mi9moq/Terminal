package com.example.terminal.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terminal.data.Bar
import com.example.terminal.presentation.rememberTerminalState
import kotlin.math.roundToInt

private const val MIN_VISIBLE_BARS_COUNT = 20

@OptIn(ExperimentalTextApi::class)
@Composable
fun Terminal(bars: List<Bar>) {

    var terminalState by rememberTerminalState(bars = bars)

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val visibleBarsCount = (terminalState.visibleBarsCount / zoomChange).roundToInt()
            .coerceIn(MIN_VISIBLE_BARS_COUNT, bars.size)

        val scrolledBy = (terminalState.scrolledBy + panChange.x)
            .coerceAtLeast(0f)
            .coerceAtMost(bars.size * terminalState.barWidth - terminalState.terminalWidth)

        terminalState = terminalState.copy(
            visibleBarsCount = visibleBarsCount,
            scrolledBy = scrolledBy
        )
    }

    val textMeasure = rememberTextMeasurer()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .transformable(transformableState)
            .padding(
                top = 32.dp,
                bottom = 32.dp,
            )
            .onSizeChanged {
                terminalState = terminalState.copy(terminalWidth = it.width.toFloat())
            }
    ) {
        val max = terminalState.visibleBars.maxOf { it.high }
        val min = terminalState.visibleBars.minOf { it.low }
        val pxPerPoint = size.height / (max - min)

        translate(left = terminalState.scrolledBy) {
            bars.forEachIndexed { index, bar ->
                val offsetX = size.width - index * terminalState.barWidth
                drawLine(
                    color = Color.White,
                    start = Offset(offsetX, size.height - ((bar.low - min) * pxPerPoint)),
                    end = Offset(offsetX, size.height - ((bar.high - min) * pxPerPoint)),
                    strokeWidth = 1f
                )
                drawLine(
                    color = if (bar.open < bar.close) Color.Green else Color.Red,
                    start = Offset(offsetX, size.height - ((bar.open - min) * pxPerPoint)),
                    end = Offset(offsetX, size.height - ((bar.close - min) * pxPerPoint)),
                    strokeWidth = terminalState.barWidth / 2
                )
            }
        }

        bars.firstOrNull()?.let {
            drawPrices(
                max = max,
                min = min,
                last = it.close,
                pxPerPoint = pxPerPoint,
                textMeasure = textMeasure,
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawPrices(
    max: Float,
    min: Float,
    last: Float,
    pxPerPoint: Float,
    textMeasure: TextMeasurer,
) {

    drawDashedLine(
        start = Offset(0f, 0f),
        end = Offset(size.width, 0f)
    )
    drawTextPrice(
        textMeasure = textMeasure,
        price = max,
        offsetY = 0f
    )

    drawDashedLine(
        start = Offset(0f, size.height - ((last - min) * pxPerPoint)),
        end = Offset(size.width, size.height - (last - min) * pxPerPoint),
    )
    drawTextPrice(
        textMeasure = textMeasure,
        price = last,
        offsetY = size.height - ((last - min) * pxPerPoint)
    )

    drawDashedLine(
        start = Offset(0f, size.height),
        end = Offset(size.width, size.height),
    )
    drawTextPrice(
        textMeasure = textMeasure,
        price = min,
        offsetY = size.height
    )
}

private fun DrawScope.drawDashedLine(
    start: Offset,
    end: Offset,
    color: Color = Color.White,
    strokeWidth: Float = 1f
) {
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokeWidth,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(
                4.dp.toPx(), 4.dp.toPx()
            )
        ),
    )
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawTextPrice(
    textMeasure: TextMeasurer,
    price: Float,
    offsetY: Float,
) {

    val textLayoutResult = textMeasure.measure(
        text = AnnotatedString(text = price.toString()),
        style = TextStyle(
            color = Color.White,
            fontSize = 12.sp,
        )
    )
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(size.width - textLayoutResult.size.width, offsetY)
    )
}