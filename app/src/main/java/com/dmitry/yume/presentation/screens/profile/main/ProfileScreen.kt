package com.dmitry.yume.presentation.screens.profile.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.ui.compose.state.rememberProgressStateWithTickCount
import com.dmitry.yume.domain.models.AppError
import com.dmitry.yume.domain.models.Profile
import com.dmitry.yume.presentation.preview.PreviewImages
import com.dmitry.yume.presentation.screens.ErrorScreen
import com.dmitry.yume.presentation.screens.profile.ProfilePreviewData
import com.dmitry.yume.presentation.screens.profile.ProfileViewModel
import com.dmitry.yume.presentation.screens.profile.ProfileViewState
import com.dmitry.yume.presentation.screens.profile.components.Glowing
import com.dmitry.yume.presentation.ui.theme.YumeTheme

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onVerificationClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegistrationClick: () -> Unit,
    onEditClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val profileState by viewModel.state.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = profileState,
        contentKey = { it::class },
        transitionSpec = { fadeIn() togetherWith fadeOut() using null  },
        label = "profile"
    ) { state ->
        when (state) {
            ProfileViewState.Loading -> {
                ProfilePlaceholder()
            }

            ProfileViewState.Guest -> {
                ProfileGuest()
            }

            is ProfileViewState.Content -> {
                ProfileContent(
                    profile = state.profile,
                    onEditClick = onEditClick,
                    onSettingsClick = onSettingsClick,
                    onVerificationClick = onVerificationClick
                )
            }

            is ProfileViewState.Error -> {
                val (title, message) = when (state.error) {
                    AppError.NoConnection -> "Нет подключения" to "Проверьте интернет и попробуйте ещё раз"
                    AppError.NotFound -> "Страница не найдена" to null
                    AppError.Server -> "Сервер недоступен" to "Попробуйте немного позже"
                    AppError.Unknown -> "Не удалось загрузить домашний экран" to null
                }

                ErrorScreen(
                    title = title,
                    message = message,
                    onRetry = if (state.error == AppError.NotFound) null else viewModel::load,
                    modifier = Modifier.systemBarsPadding()
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun ProfileScreenPreview() {
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

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun ProfileScreenUnverifiedPreview() {
    YumeTheme {
        PreviewImages {
            ProfileContent(
                profile = ProfilePreviewData.unverifiedProfile,
                onEditClick = {},
                onSettingsClick = {},
                onVerificationClick = {},
            )
        }
    }
}
