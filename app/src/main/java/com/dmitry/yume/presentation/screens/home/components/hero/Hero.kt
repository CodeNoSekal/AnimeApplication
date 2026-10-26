package com.dmitry.yume.presentation.screens.home.components.hero

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.presentation.preview.PreviewData
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.screens.home.components.topPadding
import com.dmitry.yume.presentation.ui.theme.YumeTheme

@Composable
fun Hero(
    heroes: List<Anime>,
    contentPadding: PaddingValues,
    onItemClick: (Int) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { heroes.size })

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        HeroBackground(
            modifier = Modifier.matchParentSize(),
            position = { pagerState.currentPage + pagerState.currentPageOffsetFraction },
            posters = heroes.map { it.posterUrl }
        )

        Column(
            modifier = Modifier.topPadding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                key = { heroes[it].id }
            ) { page ->
                HeroContent(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .clickable(
                            interactionSource = null,
                            indication = null,
                            onClick = {
                                onItemClick(heroes[page].id)
                            }
                        ),
                    pageData = heroes[page]
                )
            }

            HeroPagerIndicator(
                pageCount = heroes.size,
                position = { pagerState.currentPage + pagerState.currentPageOffsetFraction },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun HeroPreview() {
    YumeTheme {
        PreviewImages {
            Hero(
                heroes = PreviewData.homeData.hero,
                contentPadding = PaddingValues(top = 24.dp),
                onItemClick = {},
            )
        }
    }
}