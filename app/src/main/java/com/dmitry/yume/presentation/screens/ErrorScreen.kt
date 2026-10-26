package com.dmitry.yume.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmitry.yume.R
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    title: String,
    message: String? = null,
    onRetry: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.exclamation),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(50.dp)
        )

        Text(
            text = title,
            style = YumeType.h2,
            color = colors.textPrimary,
            textAlign = TextAlign.Center
        )

        message?.let {
            Text(
                text = it,
                style = YumeType.bodyMedium,
                color = colors.textPrimary,
                textAlign = TextAlign.Center
            )
        }

        onRetry?.let {
            Button(
                onClick = it
            ) {
                Text(
                    text = "Попробовать ещё раз",
                    style = YumeType.bodyMedium,
                    color = colors.textPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF14131A)
@Composable
private fun ErrorScreenPreview() {
    YumeTheme {
        ErrorScreen(
            title = "Ошибка №1",
            message = "Короткое сообщение об ошибке",
            onRetry = { }
        )
    }
}
