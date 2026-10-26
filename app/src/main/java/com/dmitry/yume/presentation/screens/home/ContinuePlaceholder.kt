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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.components.SkeletonBlock
import com.dmitry.yume.presentation.components.SkeletonText
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

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
