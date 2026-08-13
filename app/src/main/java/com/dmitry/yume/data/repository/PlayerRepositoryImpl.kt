package com.dmitry.yume.data.repository

import com.dmitry.yume.data.api.PlayerApi
import com.dmitry.yume.data.response.toDomain
import com.dmitry.yume.domain.repository.PlayerRepository
import com.dmitry.yume.domain.repository.PlayerResult
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val api: PlayerApi
) : PlayerRepository {
    override suspend fun getPlayerById(id: Int): PlayerResult {
        try {
            val response = api.getPlayerById(id)
            return PlayerResult.Success(response.toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception){
            return PlayerResult.Error(e.message ?: "Не удалось загрузить плеер")
        }
    }

}
