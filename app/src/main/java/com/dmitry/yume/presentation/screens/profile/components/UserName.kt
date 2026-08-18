package com.dmitry.yume.presentation.screens.profile.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun DisplayName(
    displayName: String?
){
    Text(
        text = displayName ?: "",
        style = YumeType.display,
        color = colors.textPrimary
    )
}