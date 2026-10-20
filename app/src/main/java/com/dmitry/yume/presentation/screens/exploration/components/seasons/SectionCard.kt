package com.dmitry.yume.presentation.screens.exploration.components.seasons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.SectionModel
import com.dmitry.yume.presentation.preview.PreviewData
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors

@Composable
fun SectionCard(
    sectionModel: SectionModel,
    onSectionClicked: (SectionModel) -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable(
                onClick = {
                    onSectionClicked(sectionModel)
                }
            ),
        color = colors.surfaceCard,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, colors.line)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = sectionModel.title
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun SeasonPagerPreview() {
    YumeTheme {
        PreviewImages {
            SectionCard(
                sectionModel = PreviewData.sections[1],
                onSectionClicked = {},
            )
        }
    }
}