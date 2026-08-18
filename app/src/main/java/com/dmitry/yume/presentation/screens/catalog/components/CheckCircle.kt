package com.dmitry.yume.presentation.screens.catalog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CheckCircle(
    selected: Boolean,
    color: Color,
) {

    Box (
        contentAlignment = Alignment.Center
    ){
        Box(
            modifier = Modifier
                .border(2.dp, color, CircleShape)
                .size(16.dp)
        )

        if (selected) {
            Box(
                modifier = Modifier
                    .background(color, CircleShape)
                    .size(8.dp)
            )
        }
    }
}