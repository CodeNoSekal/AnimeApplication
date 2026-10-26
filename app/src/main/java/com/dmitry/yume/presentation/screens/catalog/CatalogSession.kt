package com.dmitry.yume.presentation.screens.catalog

import androidx.paging.PagingData
import com.dmitry.yume.domain.models.Anime
import kotlinx.coroutines.flow.Flow

data class CatalogSession(
    val generation: Int,
    val options: CatalogOptions,
    val anime: Flow<PagingData<Anime>>
)
