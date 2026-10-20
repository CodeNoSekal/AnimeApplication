package com.dmitry.yume.presentation.screens.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.components.SkeletonBlock
import com.dmitry.yume.presentation.components.SkeletonText
import com.dmitry.yume.presentation.preview.PreviewData
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.screens.home.components.ranking.getRankIcon
import com.dmitry.yume.presentation.screens.home.components.topPadding
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType


@Composable
internal fun HomePlaceholder(contentPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .clearAndSetSemantics { contentDescription = "Загрузка домашнего экрана" },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeroPlaceholder(contentPadding)

        ContinuePlaceholder()

        RankingPlaceholder()
    }
}

@Composable
private fun HeroPlaceholder(contentPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .topPadding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SkeletonBlock(
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(2 / 3f),
                cornerRadius = 14.dp
            )

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                SkeletonText(YumeType.bodyMedium, widthFraction = 0.45f)
                SkeletonText(YumeType.h1, widthFraction = 0.95f)
                SkeletonText(YumeType.h1, widthFraction = 0.6f)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally)
        ) {
            repeat(3) {
                SkeletonBlock(
                    modifier = Modifier
                        .size(8.dp),
                    cornerRadius = 4.dp
                )
            }
        }
    }
}

@Composable
internal fun ContinuePlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clearAndSetSemantics { contentDescription = "Загрузка продолжения просмотра" },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonText(
                style = YumeType.h2,
                widthFraction = 0.6f,
                modifier = Modifier
                    .weight(1f)
            )

            // Same height as the "Очистить" button
            Box(
                modifier = Modifier
                    .height(30.dp),
                contentAlignment = Alignment.Center
            ) {
                SkeletonText(
                    style = YumeType.body,
                    widthFraction = 1f,
                    modifier = Modifier
                        .width(64.dp),
                    subdued = true
                )
            }
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            userScrollEnabled = false
        ) {
            items(2) {
                ProgressCardPlaceholder()
            }
        }
    }
}

@Composable
private fun ProgressCardPlaceholder() {
    // No fill like the real card has: the skeleton blocks are the same color and would disappear
    Box(
        modifier = Modifier
            .height(150.dp)
            .width(320.dp)
            .border(1.dp, colors.line, RoundedCornerShape(18.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            SkeletonBlock(
                modifier = Modifier
                    .height(126.dp)
                    .aspectRatio(2f / 3f),
                cornerRadius = 12.dp
            )

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 2.dp, end = 36.dp)
                ) {
                    SkeletonText(YumeType.h3, widthFraction = 0.9f)
                    SkeletonText(YumeType.h3, widthFraction = 0.55f)
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SkeletonText(
                        style = YumeType.mono,
                        widthFraction = 0.4f,
                        modifier = Modifier
                            .padding(top = 2.dp),
                        subdued = true
                    )

                    SkeletonBlock(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.5.dp),
                        cornerRadius = 4.5.dp
                    )

                    SkeletonText(
                        style = YumeType.xs,
                        widthFraction = 0.5f,
                        modifier = Modifier
                            .padding(top = 2.dp),
                        subdued = true
                    )
                }
            }
        }
    }
}

@Composable
private fun RankingPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clearAndSetSemantics { contentDescription = "Загрузка топа" },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SkeletonText(
            style = YumeType.h2,
            widthFraction = 0.65f,
            modifier = Modifier
                .padding(horizontal = 12.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            userScrollEnabled = false
        ) {
            items(3) { index ->
                RankedCardPlaceholder(rank = index + 1)
            }
        }
    }
}

@Composable
private fun RankedCardPlaceholder(rank: Int) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.End
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy((-25).dp)
        ) {
            // The real digit tinted as a skeleton: keeps the exact width of every number
            Icon(
                painter = painterResource(getRankIcon(rank)),
                contentDescription = null,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .height(100.dp),
                tint = colors.surfaceCard
            )

            SkeletonBlock(
                modifier = Modifier
                    .width(135.dp)
                    .aspectRatio(2 / 3f),
                cornerRadius = 14.dp
            )
        }

        Column(
            modifier = Modifier
                .width(135.dp)
        ) {
            SkeletonText(YumeType.sm, widthFraction = 0.9f)
            SkeletonText(YumeType.sm, widthFraction = 0.55f)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun HomePlaceholderPreview() {
    YumeTheme {
        HomeScreenContent(
            homeData = HomeViewState.Loading,
            progressData = ProgressViewState.Loading,
            onItemClick = {},
            onSearchClicked = {},
            onPlayClick = {},
            onRetry = {}
        )
    }
}

// Real screen with the placeholder on top at half opacity: every mismatch shows up as a "double"
@Preview(showBackground = true, backgroundColor = 0xFF14131A, locale = "ru")
@Composable
private fun HomePlaceholderOverlayPreview() {
    YumeTheme {
        PreviewImages {
            Box {
                HomeScreenContent(
                    homeData = HomeViewState.Success(PreviewData.homeData),
                    progressData = ProgressViewState.Success(PreviewData.progress),
                    onItemClick = {},
                    onSearchClicked = {},
                    onPlayClick = {},
                    onRetry = {}
                )

                Box(
                    modifier = Modifier
                        .alpha(0.5f)
                ) {
                    HomeScreenContent(
                        homeData = HomeViewState.Loading,
                        progressData = ProgressViewState.Loading,
                        onItemClick = {},
                        onSearchClicked = {},
                        onPlayClick = {},
                        onRetry = {}
                    )
                }
            }
        }
    }
}
