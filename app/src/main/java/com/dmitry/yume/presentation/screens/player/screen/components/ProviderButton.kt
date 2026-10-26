package com.dmitry.yume.presentation.screens.player.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.Provider
import com.dmitry.yume.presentation.screens.player.PlaybackViewState
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun ProviderButton(
    onClick: () -> Unit,
    provider: Provider,
    modifier: Modifier,
    playbackState: PlaybackViewState,
) {

    val mainColor =
        if (provider == playbackState.selectedSourceProvider)
            colors.accent
        else
            colors.accent.copy(alpha = 0.1f)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = mainColor,
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = provider.toRaw(),
                style = YumeType.bodyMedium
            )
        }
    }
}
