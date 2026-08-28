package com.dmitry.yume.presentation.screens.catalog.filters.components

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmitry.yume.R
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors

@Composable
fun TripleCheckbox(
    state: TripleCheckState,
) {

    val mainColor =
        when(state) {
            TripleCheckState.Checked -> colors.accent

            TripleCheckState.Rejected -> MaterialTheme.colorScheme.error

            else -> colors.textMuted
        }

    Box(
        modifier = Modifier
            .size(18.dp)
            .border(width = (1.5).dp, color = mainColor, RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(6.dp))
    ) {
        when(state) {
            TripleCheckState.Checked -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(3.dp))
                        .background(mainColor),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        painter = painterResource(R.drawable.plus_small),
                        contentDescription = null,
                    )
                }
            }

            TripleCheckState.Rejected -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(3.dp))
                        .background(mainColor),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        painter = painterResource(R.drawable.minus_small),
                        contentDescription = null,
                    )
                }
            }

            else -> {}
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun TripleCheckboxPreview() {
    YumeTheme {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TripleCheckbox(state = TripleCheckState.Unchecked)
            TripleCheckbox(state = TripleCheckState.Checked)
            TripleCheckbox(state = TripleCheckState.Rejected)
        }
    }
}
