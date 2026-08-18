package com.dmitry.yume.presentation.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.components.BaseButton
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun LoginContent(
    onLoginClick: () -> Unit,
    onRegistrationClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp)
            .padding(horizontal = 16.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
            ,
            shape = RoundedCornerShape(14.dp),
            color = YumeTheme.colors.surfaceBg,
            border = BorderStroke(
                width = 1.dp,
                color = YumeTheme.colors.line
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Какой-то приветственный текст, о том как важно зарегистрироваться",
                    style = YumeType.bodyMedium
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BaseButton(
                        onClick = onLoginClick,
                        modifier = Modifier
                            .weight(0.35f),
                        text = "Войти"
                    )

                    BaseButton(
                        onClick = onRegistrationClick,
                        modifier = Modifier
                            .weight(0.65f)
                        ,
                        text = "Зарегистрироваться"
                    )
                }
            }

        }
    }
}