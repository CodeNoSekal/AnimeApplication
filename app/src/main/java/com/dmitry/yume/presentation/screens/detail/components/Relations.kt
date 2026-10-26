package com.dmitry.yume.presentation.screens.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun Relations(
    items: List<Anime>,
    modifier: Modifier = Modifier,
    currentTitleId: Int,
    onItemClick: (Int) -> Unit,
    onShowAllClick: () -> Unit,
){
    val uniqueItems = items.distinctBy { it.id }
    if (uniqueItems.size <= 1) return

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
            ,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Связанное",
                style = YumeType.h3
            )
            TextButton(onClick = onShowAllClick) {
                Text("Показать все", style = YumeType.sm, color = colors.textMuted)
            }
        }

        val currentIndex = uniqueItems.indexOfFirst { it.id == currentTitleId }

        val maxStartIndex = (uniqueItems.size - 3).coerceAtLeast(0)

        val startIndex = (currentIndex - 1)
            .coerceIn(0, maxStartIndex)

        val relationsList = uniqueItems
            .drop(startIndex)
            .take(3)

        relationsList.forEach { item ->
            RelationItem(item, currentTitleId, onItemClick)
        }
    }
}
