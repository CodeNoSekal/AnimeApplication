package com.dmitry.yume.presentation.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.components.SkeletonBlock

@Composable
fun DetailPlaceholder(onBackClick: () -> Unit) {
    Scaffold(topBar = { DetailsTopBar(onBackClick, 0f) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
                .clearAndSetSemantics { contentDescription = "Загрузка тайтла" },
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SkeletonBlock(
                Modifier.padding(horizontal = 80.dp).padding(top = 50.dp)
                    .fillMaxWidth().aspectRatio(2f / 3f), cornerRadius = 8.dp
            )
            Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SkeletonBlock(Modifier.fillMaxWidth(0.85f).height(26.dp))
                SkeletonBlock(Modifier.fillMaxWidth(0.55f).height(16.dp), subdued = true)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SkeletonBlock(Modifier.size(48.dp), cornerRadius = 14.dp)
                    SkeletonBlock(Modifier.weight(1f).height(48.dp), cornerRadius = 14.dp)
                }
                SkeletonBlock(Modifier.fillMaxWidth().height(48.dp), cornerRadius = 14.dp)
                repeat(4) { index ->
                    SkeletonBlock(Modifier.fillMaxWidth(if (index == 3) 0.65f else 1f).height(14.dp), subdued = true)
                }
                Spacer(Modifier.height(4.dp))
                repeat(3) { index ->
                    SkeletonBlock(Modifier.fillMaxWidth(if (index == 0) 0.75f else 0.6f).height(14.dp), subdued = true)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) { SkeletonBlock(Modifier.weight(1f).height(32.dp), cornerRadius = 16.dp) }
                }
                Spacer(Modifier.height(4.dp))
                SkeletonBlock(Modifier.fillMaxWidth(0.3f).height(22.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    SkeletonBlock(Modifier.weight(1f).height(48.dp))
                    SkeletonBlock(Modifier.weight(1f).height(48.dp), cornerRadius = 14.dp)
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}
