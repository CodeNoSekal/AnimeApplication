package com.dmitry.yume.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dmitry.yume.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchHistoryRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SearchHistoryRepository {
    override val animeIds: Flow<List<Int>> = dataStore.data.map { preferences ->
        decode(preferences[SEARCH_HISTORY_IDS])
    }

    override suspend fun add(animeId: Int) {
        dataStore.edit { preferences ->
            val ids = decode(preferences[SEARCH_HISTORY_IDS])
            preferences[SEARCH_HISTORY_IDS] = encode(listOf(animeId) + ids.filterNot { it == animeId })
        }
    }

    override suspend fun remove(animeId: Int) {
        dataStore.edit { preferences ->
            preferences[SEARCH_HISTORY_IDS] = encode(
                decode(preferences[SEARCH_HISTORY_IDS]).filterNot { it == animeId }
            )
        }
    }

    override suspend fun clear() {
        dataStore.edit { it.remove(SEARCH_HISTORY_IDS) }
    }

    private fun decode(raw: String?): List<Int> = raw
        ?.split(',')
        ?.mapNotNull(String::toIntOrNull)
        ?.distinct()
        ?.take(MAX_HISTORY_SIZE)
        .orEmpty()

    private fun encode(ids: List<Int>): String = ids.take(MAX_HISTORY_SIZE).joinToString(",")

    private companion object {
        val SEARCH_HISTORY_IDS = stringPreferencesKey("search_history_anime_ids")
        const val MAX_HISTORY_SIZE = 20
    }
}
