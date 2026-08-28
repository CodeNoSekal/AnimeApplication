package com.dmitry.yume.presentation.screens.catalog.filters.components

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors

@Composable
fun DoubleCheckbox(
    state: DoubleCheckState,
) {

    val mainColor =
        when(state) {
            DoubleCheckState.Checked -> colors.accent

            else -> colors.textMuted
        }

    Box(
        modifier = Modifier
            .size(18.dp)
            .border(width = (1.5).dp, color = mainColor, RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(6.dp))
    ) {
        when(state) {
            DoubleCheckState.Checked -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(3.dp))
                        .background(mainColor)
                )
            }

            DoubleCheckState.Unchecked -> {

            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun DoubleCheckboxPreview() {
    YumeTheme {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DoubleCheckbox(state = DoubleCheckState.Unchecked)
            DoubleCheckbox(state = DoubleCheckState.Checked)
        }
    }
}
