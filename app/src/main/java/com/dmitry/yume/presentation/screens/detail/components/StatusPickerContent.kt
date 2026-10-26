package com.dmitry.yume.presentation.screens.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.Status
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun StatusPickerContent(
    status: Status,
    onStatusSelected: (String?) -> Unit,
    enabled: Boolean = true,
) {

    val options = listOf(
        null to "Не смотрю",
        "смотрю" to "Смотрю",
        "в планах" to "В планах",
        "просмотрено" to "Просмотрено",
        "брошено" to "Брошено",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "Статус просмотра",
            style = YumeType.h2,
            color = colors.textPrimary,
            modifier = Modifier
                .padding(bottom = 8.dp)
        )

        options.forEach { (value, label) ->
            StatusOption(
                text = label,
                status = value,
                selected = when (status.status) {
                    "watching" -> "смотрю"
                    "planned" -> "в планах"
                    "completed" -> "просмотрено"
                    "dropped" -> "брошено"
                    else -> status.status
                } == value,
                onClick = { onStatusSelected(value) },
                enabled = enabled
            )
        }
    }

}
