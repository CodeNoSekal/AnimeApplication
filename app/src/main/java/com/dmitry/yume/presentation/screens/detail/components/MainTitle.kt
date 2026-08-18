package com.dmitry.yume.presentation.screens.detail.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun MainTitle(
    title: String?
) {
    title?.let {
        Text(
            text = it,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            style = YumeType.h1,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}