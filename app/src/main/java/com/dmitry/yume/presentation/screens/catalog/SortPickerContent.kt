package com.dmitry.yume.presentation.screens.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.screens.catalog.components.OrderButton
import com.dmitry.yume.presentation.screens.catalog.components.SortOption
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun SortPickerContent(
    optionsState: Options,
    onSortSelected: (Options) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "Сортировать по",
            style = YumeType.h2,
            color = colors.textPrimary,
            modifier = Modifier
                .padding(bottom = 8.dp)
        )

        Sort.entries.forEach {
            SortOption(
                text = it.toUi(),
                selected = optionsState.sort == it,
                onClick = {
                    onSortSelected(optionsState.copy(sort = it))
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OrderButton(
                onClick = { onSortSelected(optionsState.copy(order = Order.Asc)) },
                modifier = Modifier
                    .weight(0.5f),
                order = Order.Asc,
                isActive = Order.Asc == optionsState.order
            )

            OrderButton(
                onClick = { onSortSelected(optionsState.copy(order = Order.Desc)) },
                modifier = Modifier
                    .weight(0.5f),
                order = Order.Desc,
                isActive = Order.Desc == optionsState.order
            )
        }
    }
}
