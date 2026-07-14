package com.dmitry.test.animeapplication.data.repository

import com.dmitry.test.animeapplication.data.api.AuthApi
import com.dmitry.test.animeapplication.data.response.toDomain
import com.dmitry.test.animeapplication.domain.repository.ProfileRepository
import com.dmitry.test.animeapplication.domain.repository.ProfileResult
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: AuthApi
) : ProfileRepository {
    override suspend fun getUser(): ProfileResult {
        try {
            val response = api.getMe()
            return ProfileResult.Success(response.toDomain())
        } catch (e: IOException){
            return ProfileResult.Error(e.message)
        } catch (e: HttpException){
            return ProfileResult.Error(e.message)
        }
    }
}