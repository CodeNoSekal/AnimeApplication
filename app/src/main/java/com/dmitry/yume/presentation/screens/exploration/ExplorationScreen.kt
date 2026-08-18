package com.dmitry.yume.presentation.screens.exploration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ExplorationScreen(
    onSearchClicked: () -> Unit,
) {
    Scaffold(
        topBar = { ExplorationTopBar(onSearchClicked) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize(),
        ) {

        }
    }
}