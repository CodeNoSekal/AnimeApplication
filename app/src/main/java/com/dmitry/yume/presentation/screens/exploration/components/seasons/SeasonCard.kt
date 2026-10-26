package com.dmitry.yume.presentation.screens.exploration.components.seasons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.dmitry.yume.R
import com.dmitry.yume.domain.models.Season
import com.dmitry.yume.presentation.preview.PreviewData
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.screens.home.components.ranking.getRankIcon
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun SeasonCard(
    season: Season,
    onClick: () -> Unit,
) {
    val colors = YumeTheme.colors

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = colors.accent,
        border = BorderStroke(1.dp, colors.lineStrong),
    ) {
        Box{
            SeasonPosters(
                modifier = Modifier.matchParentSize(),
                posters = season.posters
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = season.label.replaceFirst(' ', '\n'),
                    style = YumeType.displayLg.copy(
                        lineHeight = 32.sp,
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.Both
                        )
                    ),
                    color = MaterialTheme.colorScheme.background
                )

                val digitIcons = listOf(
                    R.drawable.ic_digit_0, R.drawable.ic_digit_1, R.drawable.ic_digit_2,
                    R.drawable.ic_digit_3, R.drawable.ic_digit_4, R.drawable.ic_digit_5,
                    R.drawable.ic_digit_6, R.drawable.ic_digit_7, R.drawable.ic_digit_8,
                    R.drawable.ic_digit_9,
                )

                Row{
                    for (digit in season.total.toString()){
                        Icon(
                            painter = painterResource(digitIcons[digit.digitToInt()]),
                            contentDescription = null,
                            modifier = Modifier
                                .height(100.dp),
                            tint = MaterialTheme.colorScheme.background
                        )
                    }
                }
            }
        }

    }
}

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun SeasonCardPreview() {
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
private fun SeasonCardEmptyPreview() {
    YumeTheme {
        PreviewImages {
            SeasonCard(
                season = PreviewData.seasons[1],
                onClick = {}
            )
        }
    }
}