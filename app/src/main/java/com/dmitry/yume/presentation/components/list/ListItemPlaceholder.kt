package com.dmitry.yume.presentation.components.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.components.SkeletonBlock

@Composable
fun ListItemPlaceholder(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        SkeletonBlock(
            Modifier.width(108.dp).aspectRatio(2f / 3f),
            cornerRadius = 8.dp,
        )

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            SkeletonBlock(Modifier.fillMaxWidth(0.85f).height(18.dp))
            Spacer(Modifier.height(8.dp))
            SkeletonBlock(Modifier.fillMaxWidth(0.55f).height(14.dp), subdued = true)
        }
    }
}
