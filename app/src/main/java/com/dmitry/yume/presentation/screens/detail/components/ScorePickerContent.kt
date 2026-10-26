package com.dmitry.yume.presentation.screens.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.ui.theme.YumeTheme.colors
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun ScorePickerContent(
    title: String?,
    initialScore: Int?,
    isSaving: Boolean,
    error: String?,
    onSave: (Int?) -> Unit
) {
    var draft by rememberSaveable(initialScore) { mutableStateOf(initialScore) }
    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Ваша оценка", style = YumeType.h2, color = colors.textPrimary)
            title?.let { Text(it, style = YumeType.sm, color = colors.textSecondary) }
        }
        (1..10).chunked(5).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { value ->
                    val isSelected = draft == value
                    Surface(
                        onClick = { draft = value },
                        enabled = !isSaving,
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) colors.rating else colors.surfaceRaised,
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).semantics {
                            selected = isSelected
                            contentDescription = "Оценка $value из 10"
                        }
                    ) {
                        Text(
                            value.toString(),
                            modifier = Modifier.padding(vertical = 12.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = YumeType.bodyMedium,
                            color = if (isSelected) colors.surfaceBg else colors.textPrimary
                        )
                    }
                }
            }
        }
        error?.let { Text(it, style = YumeType.sm, color = colors.danger) }
        Button(
            onClick = { onSave(draft) },
            enabled = !isSaving && draft != null && draft != initialScore,
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
        ) {
            if (isSaving) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = colors.textPrimary)
            else Text("Сохранить", style = YumeType.bodyMedium)
        }
        if (initialScore != null) {
            TextButton(onClick = { onSave(null) }, enabled = !isSaving, modifier = Modifier.fillMaxWidth()) {
                Text("Удалить оценку", color = colors.textSecondary)
            }
        }
    }
}
