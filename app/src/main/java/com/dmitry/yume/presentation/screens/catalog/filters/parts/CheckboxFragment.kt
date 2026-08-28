package com.dmitry.yume.presentation.screens.catalog.filters.parts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.ui.theme.YumeType
import kotlin.collections.chunked
import kotlin.collections.forEach

@Composable
fun <T> CheckboxFragment(
    title: String,
    items: List<T>,
    checkbox: @Composable (T) -> Unit,
    labelOf: (T) -> String,
    onItemClick: (T) -> Unit,
    columns: Int,
){
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ){
        Text(
            text = title,
            style = YumeType.bodyMedium
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items
                .chunked(columns)
                .forEach { rowParameters ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowParameters.forEach { parameter ->
                            val interactionSource = remember { MutableInteractionSource() }

                            Row(
                                modifier = Modifier
                                    .weight(1.0f)
                                    .clickable(
                                        interactionSource = interactionSource,
                                        indication = null,
                                        onClick = { onItemClick(parameter) }
                                    ),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                checkbox(parameter)

                                Text(
                                    text = labelOf(parameter),
                                    style = YumeType.body,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            if (rowParameters.size == 1) {
                                Spacer(
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
        }
    }
}