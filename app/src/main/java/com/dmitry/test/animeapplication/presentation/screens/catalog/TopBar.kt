package com.dmitry.test.animeapplication.presentation.screens.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.test.animeapplication.R
import com.dmitry.test.animeapplication.presentation.components.BaseButton
import com.dmitry.test.animeapplication.presentation.components.SearchButton
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeTheme


@Composable
fun CatalogTopBar(
    onSearchClicked: () -> Unit,
    modifier: Modifier
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SearchButton(onSearchClicked)

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BaseButton(
                    onClick = {},
                    modifier = Modifier
                        .weight(0.5f),
                    text = "Сортировка",
                    icon = R.drawable.bars_sort_18
                )

                BaseButton(
                    onClick = {},
                    modifier = Modifier
                        .weight(0.5f),
                    text = "Фильтры",
                    icon = R.drawable.filter_18
                )
            }
        }
        HorizontalDivider(color = YumeTheme.colors.line, thickness = 2.dp)
    }
}