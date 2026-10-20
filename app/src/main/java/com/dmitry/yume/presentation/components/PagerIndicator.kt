package com.dmitry.yume.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import kotlin.math.abs

@Composable
fun PagerIndicator(
    pageCount: Int,
    position: () -> Float,
    modifier: Modifier = Modifier,
) {

    val activeColor = colors.textPrimary
    val inactiveColor = colors.textMuted

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            Box(
                Modifier
                    .layout { measurable, _ ->
                        val closeness = pageCloseness(position(), index)
                        val width = lerp(8.dp, 20.dp, closeness).roundToPx()
                        val height = 8.dp.roundToPx()
                        val placeable = measurable.measure(Constraints.fixed(width, height))
                        layout(width, height) { placeable.place(0, 0)}
                    }
                    .drawBehind {
                        val closeness = pageCloseness(position(), index)
                        drawRoundRect(
                            color = lerp(inactiveColor, activeColor, closeness),
                            cornerRadius = CornerRadius(size.height / 2)
                        )
                    }
            )
        }
    }
}

fun pageCloseness(position: Float, page: Int): Float =
    (1f - abs(position - page)).coerceIn(0f, 1f)


@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun PagerIndicatorPreview() {
    YumeTheme {
        PagerIndicator(
            pageCount = 5,
            position = { 1.0f }
        )
    }
}