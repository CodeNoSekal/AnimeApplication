package com.dmitry.yume.presentation.screens.profile

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.User
import com.dmitry.yume.domain.models.ProfileStatistics
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
    statistics: ProfileStatistics?,
    isStatsLoading: Boolean,
    scrollState: ScrollState
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 32.dp)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(70.dp))

        Avatar(avatarUrl = userData.avatarUrl)

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

        if (userData.emailVerified) {
            ProfileStats(statistics = statistics, isLoading = isStatsLoading)
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


            if (userData.isPremium) {
                Surface(
                    color = colors.accent.copy(alpha = 0.14f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(999.dp),
                ) {
                    Text(
                        text = "Premium",
                        style = YumeType.sm,
                        color = colors.accent,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    )
                }
            }

            ExitButton(
                onExitClick = onExitClick
            )
        }
    }
}
