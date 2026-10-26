package com.dmitry.yume.presentation.screens.exploration.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.Section

@Composable
fun PairBlock(
    sections: List<Section>,
    onSectionClick: (Section) -> Unit
) {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            section = sections[1],
            size = SectionCardSize.Small,
            onSectionClick = onSectionClick
        )

        SectionCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            section = sections[2],
            size = SectionCardSize.Small,
            onSectionClick = onSectionClick
        )
    }
}
