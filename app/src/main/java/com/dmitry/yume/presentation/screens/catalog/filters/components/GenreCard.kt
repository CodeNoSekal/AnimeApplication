package com.dmitry.yume.presentation.screens.catalog.filters.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.Genre
import com.dmitry.yume.presentation.screens.catalog.filters.components.TripleCheckState
import com.dmitry.yume.presentation.screens.catalog.filters.components.TripleCheckbox
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun GenreCard(
    genre: Genre,
    included: Set<Int>,
    excluded: Set<Int>,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = (4.5).dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        TripleCheckbox(
            state =
                when (genre.id) {
                    in included -> TripleCheckState.Checked
                    in excluded -> TripleCheckState.Rejected
                    else -> TripleCheckState.Unchecked
                },
        )

        Text(
            text = genre.name,
            style = YumeType.bodyMedium
        )
    }
}
