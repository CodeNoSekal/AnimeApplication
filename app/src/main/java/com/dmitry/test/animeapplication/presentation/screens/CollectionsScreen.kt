package com.dmitry.test.animeapplication.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dmitry.test.animeapplication.R

@Composable
fun CollectionsScreen(
    onItemClicked: (Int) -> Unit
) {
    Scaffold(
        topBar = { CollectionsTopBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.nav_lists)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsTopBar() {
    TopAppBar(
        title = {
            Text(
                stringResource(R.string.nav_lists)
            )
        },
        modifier = Modifier
            .height(90.dp)
    )
}