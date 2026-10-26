package com.dmitry.yume.presentation.screens.profile.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import com.dmitry.yume.domain.models.Profile

@Composable
fun StatisticsContent(
    profile: Profile
){
    Column(

    ) {
        TotalCard()

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
        ) {
            items(profile.library.lists) { item ->
                ListItemCard(item)
            }
        }

        WatchTimeCard()
    }
}