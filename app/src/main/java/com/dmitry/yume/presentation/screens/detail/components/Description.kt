package com.dmitry.yume.presentation.screens.detail.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun Description(
    description: String?
){
    var maxLinesDesc by remember { mutableIntStateOf(4) }
    val interactionSource = remember { MutableInteractionSource() }

//    description?.let {
//        Box(
//            modifier = Modifier
//                .padding(horizontal = 20.dp)
//                .clip(RoundedCornerShape(14.dp))
//                .background(YumeTheme.colors.surfaceCard)
//                .animateContentSize(
//                    animationSpec = spring()
//                )
//                .clickable(onClick = {
//                    maxLinesDesc = if (maxLinesDesc == 4)
//                        Int.MAX_VALUE
//                    else
//                        4
//                })
//        ) {
//            Text(
//                text = it,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 12.dp, vertical = 8.dp),
//                maxLines = maxLinesDesc,
//                overflow = TextOverflow.Ellipsis,
//                style = YumeType.body,
//                textAlign = TextAlign.Start,
//                color = MaterialTheme.colorScheme.onBackground,
//            )
//        }
//    }

    description?.let {
        Text(
            text = it,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .animateContentSize(
                    animationSpec = spring()
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                    maxLinesDesc = if (maxLinesDesc == 4)
                        Int.MAX_VALUE
                    else
                        4
                }),
            maxLines = maxLinesDesc,
            overflow = TextOverflow.Ellipsis,
            style = YumeType.body,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}