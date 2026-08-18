package com.dmitry.yume.presentation.screens.detail.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun Divider(

){
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth(),
        thickness = 1.dp,
        color = YumeTheme.colors.line
    )
}