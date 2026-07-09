package com.dmitry.test.animeapplication.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.dmitry.test.animeapplication.R
import com.dmitry.test.animeapplication.domain.models.User
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeTheme
import com.dmitry.test.animeapplication.presentation.ui.theme.YumeType

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val state = viewModel.state.collectAsStateWithLifecycle()
    val isRefreshing = state.value.isLoading

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        ProfileContent(
            pullToRefreshState = pullToRefreshState,
            data = state.value.user,
            isRefreshing = isRefreshing,
            modifier = Modifier.padding(innerPadding),
            viewModel = viewModel)
    }
}

@Composable
fun ProfileContent(
    data: User?,
    pullToRefreshState: PullToRefreshState,
    isRefreshing: Boolean,
    modifier: Modifier,
    viewModel: ProfileViewModel
) {
    val colors = YumeTheme.colors

    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.load() },
        modifier = modifier
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(360.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.34f),
                            Color.Transparent,
                        ),
                        center = Offset(x = Float.POSITIVE_INFINITY * 0.7f, y = 0f),
                        radius = 900f
                    )
                )
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(Modifier.height(80.dp))
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceCard)
                    .border(2.dp, colors.accent, CircleShape)
            ) {
                AsyncImage(
                    model = data?.avatarUrl,
                    contentDescription = "Аватар",
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
            Spacer(Modifier.height(16.dp))

            Text(
                text = data?.displayName ?: "",
                style = YumeType.display,
                color = colors.textPrimary
            )
            Spacer(Modifier.height(8.dp))

            val meta = listOfNotNull(data?.username, data?.email).joinToString(" · ")
            if (meta.isNotBlank()) {
                Text(
                    text = meta,
                    style = YumeType.xs,
                    color = colors.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.height(20.dp))
            TextButton(
                onClick = viewModel::logout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = colors.danger
                )
            ) {
                Icon(
                    painterResource(R.drawable.exit_20),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))

                Text(
                    text = "Выйти из аккаунта",
                    color = colors.danger,
                    style = YumeType.bodyMedium)
            }


        }
    }
}