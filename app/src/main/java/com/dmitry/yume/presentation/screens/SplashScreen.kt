package com.dmitry.yume.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dmitry.yume.R

@Composable
fun SplashScreen(){
    // Reading the system bar insets here starts tracking them before the first real screen,
    // otherwise that screen gets 0 insets on its first frame and jumps down by the status bar
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .systemBarsPadding()
        )

        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            Modifier.size(288.dp)
        )
    }
}
