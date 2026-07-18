package com.dmitry.test.animeapplication.presentation.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dmitry.test.animeapplication.domain.models.Status
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeTheme.colors
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeType

@Composable
fun StatusPickerContent(
    status: Status,
    setStatus: (String?) -> Unit,
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
                selected = status.status == value,
                onClick = { setStatus(value) }
            )
        }
    }

}

@Composable
fun StatusOption(
    text: String,
    status: String?,
    selected: Boolean,
    onClick: () -> Unit
) {
    val contentColor =
        when (status) {
            "смотрю" -> colors.statusWatching
            "в планах" -> colors.statusPlanned
            "просмотрено" -> colors.statusCompleted
            "брошено" -> colors.statusDropped
            else -> colors.textMuted
        }

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val selectedColor =
                if (selected)
                    contentColor
                else contentColor.copy(alpha = 0.35f)

            CheckCircle(
                selected,
                selectedColor
            )

            Text(
                text = text,
                style = YumeType.bodyMedium,
                color = selectedColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

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






















