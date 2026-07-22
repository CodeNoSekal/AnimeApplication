package com.dmitry.test.animeapplication.presentation.screens.collections

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeColors
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeTheme
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeType
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun CollectionsTabRow(
    tabs: List<CollectionTab>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val targetTabColor =
        when (tabs[selectedIndex]) {
            CollectionTab.FAVORITES -> YumeTheme.colors.accent
            CollectionTab.WATCHING -> YumeTheme.colors.statusWatching
            CollectionTab.PLANNED -> YumeTheme.colors.statusPlanned
            CollectionTab.COMPLETED -> YumeTheme.colors.statusCompleted
            CollectionTab.DROPPED -> YumeTheme.colors.statusDropped
        }

    val currentTabColor = animateColorAsState(
        targetValue = targetTabColor,
        animationSpec = tween(durationMillis = 250),
        label = "collectionTabColor"
    ).value

    PrimaryScrollableTabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier,
        edgePadding = 0.dp,
        containerColor = MaterialTheme.colorScheme.background,
        divider = {},
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(
                    selectedTabIndex = selectedIndex,
                    matchContentSize = true
                ),
                width = Dp.Unspecified,
                color = currentTabColor
            )
        }
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = tab.title,
                        style = YumeType.bodyMedium
                    )
                },
                selectedContentColor = currentTabColor,
                unselectedContentColor = YumeTheme.colors.textPrimary
            )
        }
    }
}