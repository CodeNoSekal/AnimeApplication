package com.dmitry.yume.presentation.screens.catalog.filters.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FiltersBottomBar(
    onDropClick: () -> Unit,
    onApplyClick: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FiltersClearButton(
                onClick = onDropClick,
                modifier = Modifier
                    .weight(1.0f),
            )

            FiltersApplyButton(
                onClick = onApplyClick,
                modifier = Modifier
                    .weight(1.0f),
            )
        }
    }
}