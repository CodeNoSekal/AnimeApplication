package com.dmitry.yume.presentation.screens.detail.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun Description(description: String?) {
    if (description.isNullOrBlank()) return
    var expanded by rememberSaveable(description) { mutableStateOf(false) }
    var canExpand by remember(description) { mutableStateOf(false) }
    Text(
        description,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            .animateContentSize()
            .semantics { stateDescription = if (expanded) "Развёрнуто" else "Свёрнуто" }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = canExpand || expanded,
                onClickLabel = if (expanded) "Свернуть описание" else "Развернуть описание",
                onClick = { expanded = !expanded }
            ),
        maxLines = if (expanded) Int.MAX_VALUE else 4,
        overflow = TextOverflow.Ellipsis,
        style = YumeType.body,
        textAlign = TextAlign.Start,
        color = colors.textPrimary,
        onTextLayout = { if (!expanded) canExpand = it.hasVisualOverflow }
    )
}
