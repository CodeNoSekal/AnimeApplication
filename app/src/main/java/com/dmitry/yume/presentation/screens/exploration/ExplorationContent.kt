package com.dmitry.yume.presentation.screens.exploration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.ExploreData
import com.dmitry.yume.domain.models.Season
import com.dmitry.yume.domain.models.Section
import com.dmitry.yume.presentation.preview.PreviewData
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.screens.exploration.components.seasons.SeasonPager
import com.dmitry.yume.presentation.screens.exploration.components.sections.Sections
import com.dmitry.yume.presentation.ui.theme.YumeTheme

@Composable
fun ExplorationContent(
    modifier: Modifier = Modifier,
    exploreData: ExploreData,
    onSeasonClick: (Season) -> Unit,
    onSectionClick: (Section) -> Unit,
){
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp),
 
    ) {
        SeasonPager(
            seasons = exploreData.seasons,
            onSeasonClick = onSeasonClick,
        )

        Sections(
            modifier = Modifier
                .padding(horizontal = 12.dp),
            sections = exploreData.sections,
            onSectionClick = onSectionClick,
        )
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
                onSeasonClick = {},
                onSectionClick = {},
            )
        }
    }
}