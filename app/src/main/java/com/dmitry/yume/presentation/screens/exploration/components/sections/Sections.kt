package com.dmitry.yume.presentation.screens.exploration.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.Section
import com.dmitry.yume.presentation.preview.PreviewData
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.screens.exploration.ExplorationContent
import com.dmitry.yume.presentation.ui.theme.YumeTheme

@Composable
fun Sections(
    modifier: Modifier = Modifier,
    sections: List<Section>,
    onSectionClick: (Section) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        for (group in sections.chunked(3)){
            when (group.size) {
                3 -> {
                    MosaicBlock(
                        group,
                        onSectionClick = onSectionClick
                    )
                }

                2 -> {
                    PairBlock(
                        group,
                        onSectionClick = onSectionClick
                    )
                }

                1 -> {
                    SectionCard(
                        modifier = Modifier
                            .fillMaxWidth(),
                        section = group[0],
                        size = SectionCardSize.Wide,
                        onSectionClick = onSectionClick
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun SectionsPreview() {
    YumeTheme {
        PreviewImages {
            ExplorationContent(
                exploreData = PreviewData.explore,
                modifier = Modifier,
                onSeasonClick = {},
                onSectionClick = {},
            )
        }
    }
}