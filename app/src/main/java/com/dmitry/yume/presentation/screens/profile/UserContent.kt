package com.dmitry.yume.presentation.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.User
import com.dmitry.yume.presentation.screens.profile.components.Avatar
import com.dmitry.yume.presentation.screens.profile.components.DisplayName
import com.dmitry.yume.presentation.screens.profile.components.ExitButton
import com.dmitry.yume.presentation.screens.profile.components.VerifyEmailButton
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun UserContent(
    userData: User,
    onVerificationClick: () -> Unit,
    onExitClick: () -> Unit,
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Avatar(userData.avatarUrl)

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            DisplayName(userData.displayName)

            val meta = listOfNotNull(userData.username, userData.email).joinToString(" · ")
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

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (!userData.emailVerified) {
                VerifyEmailButton(
                    onVerificationClick = onVerificationClick
                )
            }

            ExitButton(
                onExitClick = onExitClick
            )
        }
    }
}