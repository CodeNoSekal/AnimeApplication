package com.dmitry.yume.domain.repository

import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    val animeIds: Flow<List<Int>>
    suspend fun add(animeId: Int)
    suspend fun remove(animeId: Int)
    suspend fun clear()
}
