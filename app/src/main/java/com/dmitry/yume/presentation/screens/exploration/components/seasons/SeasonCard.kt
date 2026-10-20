package com.dmitry.yume.presentation.screens.exploration.components.seasons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.dmitry.yume.domain.models.SeasonModel
import com.dmitry.yume.presentation.preview.PreviewData
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.ui.theme.YumeTheme

@Composable
fun SeasonCard(
    seasonData: SeasonModel,
    onClick: () -> Unit,
) {
    val colors = YumeTheme.colors

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = colors.surfaceCard,
        border = BorderStroke(1.dp, colors.lineStrong),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = seasonData.label
            )

            Text(
                text = seasonData.total.toString()
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun SeasonCardPreview() {
    YumeTheme {
        PreviewImages {
            SeasonCard(
                seasonData = PreviewData.seasons[0],
                onClick = {}
            )
        }
    }
}

// Season without posters: the card must still look fine
@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun SeasonCardEmptyPreview() {
    YumeTheme {
        PreviewImages {
            SeasonCard(
                seasonData = PreviewData.seasons[2],
                onClick = {}
            )
        }
    }
}