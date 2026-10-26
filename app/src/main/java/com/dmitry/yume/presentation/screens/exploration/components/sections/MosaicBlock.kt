package com.dmitry.yume.presentation.screens.exploration.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.Section

@Composable
fun MosaicBlock(
    sections: List<Section>,
    onSectionClick: (Section) -> Unit
) {
    Row(
        modifier = Modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            section = sections[0],
            size = SectionCardSize.Tall,
            onSectionClick = onSectionClick
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .heightIn(min = 150.dp),
                section = sections[1],
                size = SectionCardSize.Small,
                onSectionClick = onSectionClick
            )

            SectionCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .heightIn(min = 150.dp),
                section = sections[2],
                size = SectionCardSize.Small,
                onSectionClick = onSectionClick
            )
        }
    }
}
