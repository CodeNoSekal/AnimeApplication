package com.dmitry.yume.presentation.screens.catalog.filters

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmitry.yume.R
import com.dmitry.yume.presentation.screens.catalog.AnimeKind
import com.dmitry.yume.presentation.screens.catalog.Collection
import com.dmitry.yume.presentation.screens.catalog.FilterOptions
import com.dmitry.yume.presentation.screens.catalog.Status
import com.dmitry.yume.presentation.screens.catalog.filters.components.DoubleCheckState
import com.dmitry.yume.presentation.screens.catalog.filters.components.DoubleCheckbox
import com.dmitry.yume.presentation.screens.catalog.filters.components.TripleCheckState
import com.dmitry.yume.presentation.screens.catalog.filters.parts.CheckboxFragment
import com.dmitry.yume.presentation.screens.catalog.filters.components.FiltersBottomBar
import com.dmitry.yume.presentation.screens.catalog.filters.components.FiltersTopBar
import com.dmitry.yume.presentation.screens.catalog.filters.components.TripleCheckbox
import com.dmitry.yume.presentation.screens.catalog.filters.parts.RatingPart
import com.dmitry.yume.presentation.screens.catalog.filters.parts.YearPart
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun FiltersContent(
    onBackClick: () -> Unit,
    onGenresClick: () -> Unit,
    onDropClick: () -> Unit,
    onApplyClick: () -> Unit,
    filters: FilterOptions,
    onKindClick: (AnimeKind) -> Unit,
    onStatusClick: (Status) -> Unit,
    onCollectionClick: (Collection) -> Unit,
) {
    val scrollState = rememberScrollState()
    val interactionSource = remember { MutableInteractionSource() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            FiltersTopBar(onBackClick)
        },
        bottomBar = {
            FiltersBottomBar(
                onDropClick = onDropClick,
                onApplyClick = onApplyClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 10.dp)
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onGenresClick
                    ),

                ) {
                    Text(
                        text = "Жанры",
                        style = YumeType.bodyMedium
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.angle_small_right),
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                        )
                    }
            }

            YearPart()

            RatingPart()

            CheckboxFragment(
                title = "Тип",
                items = AnimeKind.entries,
                labelOf = { parameter ->
                    parameter.title
                },
                onItemClick = { parameter ->
                    onKindClick(parameter)
                },
                columns = 2,
                checkbox = { parameter ->
                    DoubleCheckbox(
                        state =
                            when (parameter) {
                                in filters.kinds -> DoubleCheckState.Checked
                                else -> DoubleCheckState.Unchecked
                            }
                    )
                }
            )

            CheckboxFragment(
                title = "Статус",
                items = Status.entries,
                labelOf = { parameter ->
                    parameter.title
                },
                onItemClick = { parameter ->
                    onStatusClick(parameter)
                },
                columns = 2,
                checkbox = { parameter ->
                    DoubleCheckbox(
                        state =
                            when (parameter) {
                                in filters.statuses -> DoubleCheckState.Checked
                                else -> DoubleCheckState.Unchecked
                            }
                    )
                }
            )
            CheckboxFragment(
                title = "Списки",
                items = Collection.entries,
                labelOf = { parameter ->
                    parameter.title
                },
                onItemClick = { parameter ->
                    onCollectionClick(parameter)
                },
                columns = 2,
                checkbox = { parameter ->
                    TripleCheckbox(
                        state =
                            when (parameter) {
                                in filters.collections -> TripleCheckState.Checked
                                in filters.excludeCollections -> TripleCheckState.Rejected
                                else -> TripleCheckState.Unchecked
                            }
                    )
                }
            )

        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF14131A,
)
@Composable
private fun FilterScreenPreview() {
    YumeTheme {
        FiltersContent(
            onBackClick = {},
            onGenresClick = {},
            onDropClick = {},
            onApplyClick = {},
            filters = FilterOptions(),
            onKindClick = {},
            onStatusClick = {},
            onCollectionClick = {}
        )
    }
}