package com.dmitry.yume.presentation.screens.exploration

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dmitry.yume.R
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun ExplorationScreen(
    onSearchClicked: () -> Unit,
    onQuickSearchClick: (QuickSearchCategory) -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { ExplorationTopBar(onSearchClicked) }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                QuickSearchHeading(
                    title = "Подборки",
                    subtitle = "Будущие премьеры и недавние релизы",
                )
            }

            items(
                items = listOf(
                    QuickSearchCategory.Announcements,
                    QuickSearchCategory.RecentReleases,
                ),
                key = { it.route },
            ) { category ->
                QuickSearchCard(category, onQuickSearchClick)
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                QuickSearchHeading(
                    title = "По типу",
                    subtitle = "Свежие обновления в каждом формате",
                    modifier = Modifier.padding(top = 12.dp),
                )
            }

            items(
                items = QuickSearchCategory.entries.filterNot {
                    it == QuickSearchCategory.Announcements ||
                        it == QuickSearchCategory.RecentReleases
                },
                key = { it.route },
            ) { category ->
                QuickSearchCard(category, onQuickSearchClick)
            }
        }
    }
}

@Composable
private fun QuickSearchHeading(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = YumeType.h2,
            color = YumeTheme.colors.textPrimary,
        )
        Text(
            text = subtitle,
            style = YumeType.sm,
            color = YumeTheme.colors.textMuted,
            modifier = Modifier.padding(top = 2.dp, bottom = 4.dp),
        )
    }
}

@Composable
private fun QuickSearchCard(
    category: QuickSearchCategory,
    onClick: (QuickSearchCategory) -> Unit,
) {
    val colors = YumeTheme.colors

    Surface(
        onClick = { onClick(category) },
        modifier = Modifier
            .fillMaxWidth()
            .height(126.dp),
        shape = RoundedCornerShape(12.dp),
        color = colors.surfaceCard,
        border = BorderStroke(1.dp, colors.lineStrong),
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Text(
                text = category.title,
                style = YumeType.overline,
                color = colors.accent,
                modifier = Modifier.align(Alignment.TopStart),
            )
            Icon(
                painter = painterResource(R.drawable.angle_small_right),
                contentDescription = null,
                tint = colors.textMuted,
                modifier = Modifier.align(Alignment.TopEnd),
            )
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(
                    text = category.description,
                    style = YumeType.xs,
                    color = colors.textMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 3.dp),
                )
            }
        }
    }
}
