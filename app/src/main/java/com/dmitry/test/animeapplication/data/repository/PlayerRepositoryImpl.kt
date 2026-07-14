package com.dmitry.test.animeapplication.data.repository

import com.dmitry.test.animeapplication.data.api.PlayerApi
import com.dmitry.test.animeapplication.data.response.toDomain
import com.dmitry.test.animeapplication.domain.repository.PlayerRepository
import com.dmitry.test.animeapplication.domain.repository.PlayerResult
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