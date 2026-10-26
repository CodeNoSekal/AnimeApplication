package com.dmitry.yume.presentation.screens.player.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.PlaybackSource
import com.dmitry.yume.domain.models.Provider
import com.dmitry.yume.presentation.screens.player.PlaybackViewState
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun VoiceoverPickerContent(
    sources: List<PlaybackSource>,
    selected: PlaybackViewState,
    onProviderSelected: (Provider) -> Unit,
    onVoiceoverSelected: (Int) -> Unit
) {
    val providers = sources.map { it.provider }

    val voiceovers = sources
        .firstOrNull {
            it.provider == selected.selectedSourceProvider
        }
        ?.voiceovers
        .orEmpty()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (providers.size > 1) {
            Text(
                text = "Источники",
                style = YumeType.h2,
                color = colors.textPrimary,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                providers.forEach { provider ->
                    ProviderButton(
                        onClick = {
                            if (provider != selected.selectedSourceProvider) {
                                onProviderSelected(provider)
                            }
                        },
                        provider = provider,
                        playbackState = selected,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Text(
            text = "Озвучки",
            style = YumeType.h2,
            color = colors.textPrimary
        )

        key(selected.selectedSourceProvider) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                items(
                    items = voiceovers,
                    key = { it.id }
                ) { voiceover ->
                    VoiceoverItemCard(
                        voiceoverId = voiceover.id,
                        selectedVoiceoverId = selected.selectedVoiceoverId,
                        logoUrl = voiceover.logoUrl,
                        voiceoverName = voiceover.name,
                        onClick = onVoiceoverSelected
                    )
                }
            }
        }
    }
}