package com.dmitry.yume.presentation.screens.profile.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dmitry.yume.domain.models.Profile
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.screens.profile.ProfilePreviewData
import com.dmitry.yume.presentation.screens.profile.components.Glowing
import com.dmitry.yume.presentation.screens.profile.main.components.ProfileActionsTopBar
import com.dmitry.yume.presentation.ui.theme.YumeTheme

@Composable
fun ProfileContent(
    profile: Profile,
    onEditClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onVerificationClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            ProfileActionsTopBar(
                onEditClick = onEditClick,
                onSettingsClick = onSettingsClick,
            )
        }
    ){ innerPadding ->
        Box(
            modifier = Modifier
                .padding(bottom = innerPadding.calculateBottomPadding())
                .fillMaxSize()
        ){
            Glowing()

            UserContent(
                profile = profile
            )

            StatisticsContent(
                profile = profile
            )
        }
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