package com.dmitry.yume.presentation.screens.exploration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.ExploreModel
import com.dmitry.yume.domain.models.SeasonModel
import com.dmitry.yume.domain.models.SectionModel
import com.dmitry.yume.presentation.preview.PreviewData
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.screens.exploration.components.seasons.SeasonPager
import com.dmitry.yume.presentation.screens.exploration.components.seasons.SectionCard
import com.dmitry.yume.presentation.ui.theme.YumeTheme

@Composable
fun ExplorationContent(
    modifier: Modifier = Modifier,
    exploreData: ExploreModel,
    onSeasonClicked: (SeasonModel) -> Unit,
    onSectionClicked: (SectionModel) -> Unit,
){
    Column(
        modifier = modifier
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SeasonPager(
            seasons = exploreData.seasons,
            onSeasonClicked = onSeasonClicked,
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(exploreData.sections) { section ->
                SectionCard(
                    sectionModel = section,
                    onSectionClicked = onSectionClicked
                )
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun ExplorationContentPreview() {
    YumeTheme {
        PreviewImages {
            ExplorationContent(
                exploreData = PreviewData.explore,
                modifier = Modifier,
                onSeasonClicked = {},
                onSectionClicked = {},
            )
        }
    }
}