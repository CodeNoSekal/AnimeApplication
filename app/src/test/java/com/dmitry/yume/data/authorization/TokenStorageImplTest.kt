package com.dmitry.yume.data.authorization

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Данные хранятся в памяти, как рекомендует Google для тестов DataStore —
 * это избавляет от файлового I/O, из-за которого на Windows тесты с реальным
 * файлом DataStore нестабильны (повторная перезапись файла может упасть с
 * "Unable to rename..."; на самом Android/Linux такого поведения нет).
 */
private class InMemoryPreferencesDataStore : DataStore<Preferences> {
    private val state = MutableStateFlow(emptyPreferences())
    override val data: Flow<Preferences> = state.asStateFlow()

    override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
        val updated = transform(state.value)
        state.value = updated
        return updated
    }
}

class TokenStorageImplTest {

    private fun newStorage(): TokenStorageImpl = TokenStorageImpl(InMemoryPreferencesDataStore())

    @Test
    fun `saved tokens round-trip through encryption`() = runBlocking {
        val storage = newStorage()

        storage.saveTokens("access-1", "refresh-1")

        assertEquals(StoredTokens("access-1", "refresh-1"), storage.getTokens())
    }

    @Test
    fun `getTokens returns null when nothing was saved`() = runBlocking {
        assertNull(newStorage().getTokens())
    }

    @Test
    fun `clear removes tokens so getTokens returns null`() = runBlocking {
        val storage = newStorage()
        storage.saveTokens("access-1", "refresh-1")

        storage.clear()

        assertNull(storage.getTokens())
    }

    @Test
    fun `refreshed tokens overwrite the previous pair`() = runBlocking {
        val storage = newStorage()
        storage.saveTokens("access-1", "refresh-1")

        storage.saveTokens("access-2", "refresh-2")

        assertEquals(StoredTokens("access-2", "refresh-2"), storage.getTokens())
    }

    @Test
    fun `tokens are not stored as plaintext`() = runBlocking {
        val dataStore = InMemoryPreferencesDataStore()
        val storage = TokenStorageImpl(dataStore)

        storage.saveTokens("super-secret-access-token", "super-secret-refresh-token")

        val stored = dataStore.data.first()
        assertFalse(stored[TokenStorageImpl.ACCESS_TOKEN]!!.contains("super-secret-access-token"))
        assertFalse(stored[TokenStorageImpl.REFRESH_TOKEN]!!.contains("super-secret-refresh-token"))
    }
}
