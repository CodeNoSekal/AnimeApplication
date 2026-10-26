package com.dmitry.yume.presentation.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.screens.profile.ProfilePreviewData
import com.dmitry.yume.presentation.screens.profile.main.ProfileContent
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors

@Composable
fun Avatar(
    avatarUrl: String?,
    modifier: Modifier = Modifier,
) {
    val outerShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .size(120.dp)
            .clip(outerShape)
            .background(colors.surfaceCard)
            .border(2.dp, colors.accent, outerShape),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Аватар",
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = modifier
                .fillMaxSize()
                .clip(outerShape)
                .border(4.dp, colors.surfaceCard, outerShape)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun ProfileContentPreview() {
    YumeTheme {
        PreviewImages {
            ProfileContent(
                profile = ProfilePreviewData.profile,
                onEditClick = {},
                onSettingsClick = {},
                onVerificationClick = {},
            )
        }
    }
}
