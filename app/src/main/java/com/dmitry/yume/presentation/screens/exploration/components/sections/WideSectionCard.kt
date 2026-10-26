package com.dmitry.yume.presentation.screens.exploration.components.sections

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.Section
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun WideSectionCard(
    section: Section,
    modifier: Modifier = Modifier,
    onSectionClick: (Section) -> Unit
) {
    Surface(
        modifier = modifier
            .clickable(
                onClick = {
                    onSectionClick(section)
                }
            ),
        color = colors.surfaceCard,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, colors.line)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row{
                    for (digit in section.total.toString()){
                        Icon(
                            painter = painterResource(digitIcons[digit.digitToInt()]),
                            contentDescription = null,
                            modifier = Modifier
                                .height(50.dp),
                            tint = colors.accent
                        )
                    }
                }

                Text(
                    text = section.title,
                    style = YumeType.h2.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = colors.textPrimary,
                    modifier = Modifier,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Box(
                Modifier
                    .width(120.dp)
            ) {

                val width = 70.dp

                SectionPoster(
                    url = section.posters[1],
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(x = (20).dp, y = (5).dp)
                        .rotate(8f),
                    width = width
                )

                SectionPoster(
                    url = section.posters[0],
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(x = (-30).dp, y = 0.dp)
                        .rotate(-8f),
                    width = width
                )
            }
        }
    }
}
