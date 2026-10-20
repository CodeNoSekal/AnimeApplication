package com.dmitry.yume.presentation.screens.exploration.components.seasons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.SeasonModel
import com.dmitry.yume.presentation.components.PagerIndicator
import com.dmitry.yume.presentation.preview.PreviewData
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.ui.theme.YumeTheme

@Composable
fun SeasonPager(
    seasons: List<SeasonModel>,
    onSeasonClicked: (SeasonModel) -> Unit
){
    val defaultPage = seasons.indexOfFirst { it.isDefault }.coerceAtLeast(0)
    val pagerState = rememberPagerState(
        initialPage = defaultPage,
        pageCount = { seasons.size }
    )


    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            key = { seasons[it].key }
        ) {
            SeasonCard(
                seasonData = seasons[it],
                onClick = {
                    onSeasonClicked(seasons[it])
                }
            )
        }

        PagerIndicator(
            pageCount = seasons.size,
            position = { pagerState.currentPage + pagerState.currentPageOffsetFraction },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun SeasonPagerPreview() {
    YumeTheme {
        PreviewImages {
            SeasonPager(
                seasons = PreviewData.seasons,
                onSeasonClicked = {},
            )
        }
    }
}