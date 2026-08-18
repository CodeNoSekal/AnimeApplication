package com.dmitry.yume.presentation.screens.profile.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun VerifyEmailButton(
    onVerificationClick: () -> Unit,
) {
    TextButton(
        onClick = onVerificationClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = colors.accent
        )
    ) {
        Text(
            text = "Подтвердить почту",
            color = colors.accent,
            style = YumeType.bodyMedium
        )
    }
}