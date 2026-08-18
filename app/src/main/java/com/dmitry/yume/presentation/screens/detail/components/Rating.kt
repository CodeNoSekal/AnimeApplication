package com.dmitry.yume.presentation.screens.detail.components

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun Rating(
    rating: Double?
) {
    rating?.let {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Divider()

            Text(
                text = "Рейтинг",
                style = YumeType.h2,
                modifier = Modifier
                    .padding(start = 10.dp)
            )

            Text(
                text = rating.toString(),
                style = YumeType.rating,
                modifier = Modifier
                    .padding(start = 16.dp)
            )

            Divider()
        }

    }

}