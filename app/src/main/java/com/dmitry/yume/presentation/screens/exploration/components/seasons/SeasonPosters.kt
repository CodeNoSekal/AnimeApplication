package com.dmitry.yume.presentation.screens.exploration.components.seasons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.preview.PreviewData
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.ui.theme.YumeTheme

@Composable
fun SeasonPosters(
    modifier: Modifier = Modifier,
    posters: List<String>,
){
    Box(modifier) {
        posters.getOrNull(0)?.let {
            SeasonPoster(
                url = it,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(x = (10).dp, y = 20.dp)
                    .rotate(-8f)
            )
        }

        posters.getOrNull(1)?.let {
            SeasonPoster(
                url = it,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (10).dp, y = 20.dp)
                    .rotate(-8f)
            )
        }

        posters.getOrNull(2)?.let {
            SeasonPoster(
                url = it,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-60).dp, y = (-20).dp)
                    .rotate(8f)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun SeasonPostersPreview() {
    YumeTheme {
        PreviewImages {
            SeasonCard(
                season = PreviewData.seasons[0],
                onClick = {}
            )
        }
    }
}

// Season without posters: the card must still look fine
@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun SeasonPostersEmptyPreview() {
    YumeTheme {
        PreviewImages {
            SeasonCard(
                season = PreviewData.seasons[1],
                onClick = {}
            )
        }
    }
}
