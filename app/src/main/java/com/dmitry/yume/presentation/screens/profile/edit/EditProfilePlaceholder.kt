package com.dmitry.yume.presentation.screens.profile.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.components.SkeletonBlock

@Composable
internal fun EditProfilePlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .clearAndSetSemantics { contentDescription = "Загрузка профиля" },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SkeletonBlock(Modifier.size(120.dp), cornerRadius = 28.dp)
        SkeletonBlock(Modifier.width(160.dp).height(16.dp), subdued = true)

        SkeletonBlock(Modifier.fillMaxWidth(0.4f).height(16.dp))
        SkeletonBlock(Modifier.fillMaxWidth().height(52.dp), cornerRadius = 12.dp)
        SkeletonBlock(Modifier.fillMaxWidth().height(52.dp), cornerRadius = 12.dp)
        SkeletonBlock(Modifier.fillMaxWidth(0.6f).height(12.dp), subdued = true)
        SkeletonBlock(Modifier.fillMaxWidth().height(48.dp), cornerRadius = 12.dp)
    }
}
