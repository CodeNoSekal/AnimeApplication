package com.dmitry.yume.presentation.screens.home.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.offset

fun Modifier.topPadding(padding: PaddingValues) = layout { measurable, constraints ->
    val top = padding.calculateTopPadding().roundToPx()
    val placeable = measurable.measure(constraints.offset(vertical = -top))
    layout(placeable.width, placeable.height + top) {placeable.place(0, top)}
}