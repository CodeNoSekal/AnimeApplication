package com.dmitry.yume.presentation.screens.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.dmitry.yume.presentation.components.SkeletonBlock
import com.dmitry.yume.presentation.ui.theme.YumeTheme

@Composable
internal fun HomePlaceholder(innerPadding: PaddingValues) {
    Column(
        Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState())
            .clearAndSetSemantics { contentDescription = "Загрузка домашнего экрана" }
    ) {
        Column(
            Modifier.padding(12.dp).fillMaxWidth().heightIn(min = 280.dp)
                .border(1.dp, YumeTheme.colors.lineStrong, RoundedCornerShape(20.dp)).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SkeletonBlock(Modifier.fillMaxWidth(0.6f).height(12.dp), subdued = true)
                    SkeletonBlock(Modifier.fillMaxWidth(0.8f).height(12.dp), subdued = true)
                    SkeletonBlock(Modifier.fillMaxWidth().height(24.dp))
                    SkeletonBlock(Modifier.fillMaxWidth(0.75f).height(24.dp))
                }
                SkeletonBlock(Modifier.width(132.dp).aspectRatio(2f / 3f), cornerRadius = 10.dp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SkeletonBlock(Modifier.weight(1f).height(48.dp), cornerRadius = 12.dp)
                repeat(2) { SkeletonBlock(Modifier.size(48.dp), cornerRadius = 12.dp) }
            }
        }
        ContinuePlaceholder()
    }
}

@Composable
internal fun ContinuePlaceholder() {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)
            .clearAndSetSemantics { contentDescription = "Загрузка продолжения просмотра" },
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SkeletonBlock(Modifier.fillMaxWidth(0.6f).height(24.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), userScrollEnabled = false) {
            items(2) {
                Row(Modifier.width(270.dp).height(120.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SkeletonBlock(Modifier.width(80.dp).height(120.dp), cornerRadius = 8.dp)
                    Column(Modifier.weight(1f).padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SkeletonBlock(Modifier.fillMaxWidth(0.9f).height(16.dp))
                        SkeletonBlock(Modifier.fillMaxWidth(0.6f).height(12.dp), subdued = true)
                    }
                }
            }
        }
    }
}
