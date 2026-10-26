package com.dmitry.yume.presentation.screens.profile.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmitry.yume.R
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.screens.profile.ProfilePreviewData
import com.dmitry.yume.presentation.screens.profile.main.ProfileContent
import com.dmitry.yume.presentation.ui.theme.YumeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileActionsTopBar(
    onEditClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TopBarIconButton(
            onClick = onEditClick,
            icon = R.drawable.pencil,
        )

        TopBarIconButton(
            onClick = onEditClick,
            icon = R.drawable.settings_24,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun ProfileActionsTopBarPreview() {
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