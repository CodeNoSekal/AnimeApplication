package com.dmitry.yume.presentation.screens.profile.main

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.User
import com.dmitry.yume.domain.models.Profile
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.screens.profile.ProfilePreviewData
import com.dmitry.yume.presentation.screens.profile.components.Avatar
import com.dmitry.yume.presentation.screens.profile.components.DisplayName
import com.dmitry.yume.presentation.screens.profile.components.ExitButton
import com.dmitry.yume.presentation.screens.profile.components.ProBadge
import com.dmitry.yume.presentation.screens.profile.components.VerifyEmailButton
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun UserContent(
    profile: Profile
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        Avatar(avatarUrl = profile.user.avatar)

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DisplayName(profile.user.displayName)
                if (profile.user.isPremium)
                    ProBadge()
            }

            val meta = listOfNotNull(profile.user.username, profile.user.email).joinToString(" · ")
            if (meta.isNotBlank()) {
                Text(
                    text = meta,
                    style = YumeType.xs,
                    color = colors.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun UserContentPreview() {
    YumeTheme {
        PreviewImages {
            UserContent(
                profile = ProfilePreviewData.profile
            )
        }
    }
}
