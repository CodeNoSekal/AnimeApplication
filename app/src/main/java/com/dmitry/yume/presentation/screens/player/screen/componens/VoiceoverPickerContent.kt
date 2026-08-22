package com.dmitry.yume.presentation.screens.player.screen.componens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dmitry.yume.domain.models.PlaybackSource
import com.dmitry.yume.domain.models.Provider
import com.dmitry.yume.domain.models.Voiceover
import com.dmitry.yume.presentation.screens.player.PlaybackUiState
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun VoiceoverPickerContent(
    sources: List<PlaybackSource>,
    selected: PlaybackUiState,
    setProvider: (Provider) -> Unit,
    setVoiceover: (Int) -> Unit
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
                            setProvider(provider)
                        }},
                    provider = provider,
                    playbackState = selected,
                    modifier = Modifier.weight(1f)
                )
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
                        onClick = setVoiceover
                    )
                }
            }
        }
    }
}

@Composable
fun VoiceoverItemCard(
    voiceoverId: Int,
    selectedVoiceoverId: Int?,
    logoUrl: String,
    voiceoverName: String,
    onClick: (Int) -> Unit
) {
    val cardColor =
        if (voiceoverId == selectedVoiceoverId)
            colors.accent.copy(alpha = 0.3f)
        else
            colors.surfaceCard

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                onClick = {
                    if (voiceoverId != selectedVoiceoverId)
                        onClick(voiceoverId)
                }
            )
            .background(color = cardColor)
            .padding(horizontal = 5.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = logoUrl,
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
        )

        Text(
            text = voiceoverName,
            style = YumeType.bodyMedium
        )
    }
}

@Composable
fun ProviderButton(
    onClick: () -> Unit,
    provider: Provider,
    modifier: Modifier,
    playbackState: PlaybackUiState,
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