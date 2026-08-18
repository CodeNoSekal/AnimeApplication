package com.dmitry.yume.presentation.screens.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dmitry.yume.R
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun SortPickerContent(
    optionsState: Options,
    onSortSelected: (Options) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "Сортировать по",
            style = YumeType.h2,
            color = colors.textPrimary,
            modifier = Modifier
                .padding(bottom = 8.dp)
        )

        Sort.entries.forEach {
            SortOption(
                text = it.toUi(),
                selected = optionsState.sort == it,
                onClick = {
                    onSortSelected(optionsState.copy(sort = it))
                }
            )
        }
    }
}

@Composable
fun SortOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val selectedColor =
                if (selected)
                    colors.textPrimary
                else colors.textMuted

            CheckCircle(
                selected,
                selectedColor
            )

            Text(
                text = text,
                style = YumeType.bodyMedium,
                color = selectedColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CheckCircle(
    selected: Boolean,
    color: Color,
) {

    Box (
        contentAlignment = Alignment.Center
    ){
        Box(
            modifier = Modifier
                .border(2.dp, color, CircleShape)
                .size(16.dp)
        )

        if (selected) {
            Box(
                modifier = Modifier
                    .background(color, CircleShape)
                    .size(8.dp)
            )
        }
    }
}
