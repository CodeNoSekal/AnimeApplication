package com.dmitry.yume.presentation.screens.exploration.components.sections

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dmitry.yume.R
import com.dmitry.yume.domain.models.Section
import com.dmitry.yume.presentation.preview.PreviewData
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.screens.exploration.ExplorationContent
import com.dmitry.yume.presentation.ui.theme.YumeTheme

enum class SectionCardSize { Tall, Small, Wide}
@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    section: Section,
    size: SectionCardSize = SectionCardSize.Small,
    onSectionClick: (Section) -> Unit
) {
    when (size) {
        SectionCardSize.Tall -> {
            TallSectionCard(
                section = section,
                modifier = modifier,
                onSectionClick = onSectionClick
            )
        }

        SectionCardSize.Small -> {
            SmallSectionCard(
                section = section,
                modifier = modifier,
                onSectionClick = onSectionClick
            )
        }

        SectionCardSize.Wide -> {
            WideSectionCard(
                section = section,
                modifier = modifier,
                onSectionClick = onSectionClick
            )
        }
    }
}

internal val digitIcons = listOf(
    R.drawable.ic_digit_0, R.drawable.ic_digit_1, R.drawable.ic_digit_2,
    R.drawable.ic_digit_3, R.drawable.ic_digit_4, R.drawable.ic_digit_5,
    R.drawable.ic_digit_6, R.drawable.ic_digit_7, R.drawable.ic_digit_8,
    R.drawable.ic_digit_9,
)

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun SectionCardPreview() {
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