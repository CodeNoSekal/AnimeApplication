package com.dmitry.yume.data.repository

import com.dmitry.yume.data.api.PlayerApi
import com.dmitry.yume.data.response.toDomain
import com.dmitry.yume.domain.repository.PlayerRepository
import com.dmitry.yume.domain.repository.PlayerResult
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val api: PlayerApi
) : PlayerRepository {
    override suspend fun getPlayerById(id: Int): PlayerResult {
        try {
            val response = api.getPlayerById(id)
            return PlayerResult.Success(response.toDomain())
        } catch (e: IOException){
            return PlayerResult.Error(e.message)
        } catch (e: HttpException){
            return PlayerResult.Error(e.message)
        }
    }

}