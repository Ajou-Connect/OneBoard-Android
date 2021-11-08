package kr.khs.oneboard.repository

import kr.khs.oneboard.core.UseCase
import kr.khs.oneboard.data.LoginBody
import kr.khs.oneboard.data.LoginResponse
import kr.khs.oneboard.data.api.Response

interface BasicRepository {
    suspend fun loginCheck(token: String): UseCase<Boolean>

    suspend fun healthCheck(): UseCase<Boolean>

    suspend fun login(body: LoginBody): UseCase<Response<LoginResponse>>
}