package com.dmitry.yume.presentation.screens.catalog.filters.parts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.screens.catalog.filters.components.YearField
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun YearPart(

) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ){
        Text(
            text = "Год релиза",
            style = YumeType.bodyMedium
        )

        Row(
            modifier = Modifier
                .height(30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            YearField(
                label = "От",
                year = null,
                onClick = {},
                modifier = Modifier.weight(1.0f)
            )

            Text(
                text = "-",
                color = colors.textMuted
            )

            YearField(
                label = "До",
                year = null,
                onClick = {},
                modifier = Modifier.weight(1.0f)
            )
        }
    }
}