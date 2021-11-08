package kr.khs.oneboard.repository

import kr.khs.oneboard.data.LoginBody
import kr.khs.oneboard.data.LoginResponse
import kr.khs.oneboard.data.api.Response

interface BasicRepository {
    suspend fun loginCheck(token: String): Boolean

    suspend fun healthCheck(): Boolean

    suspend fun login(body: LoginBody): Response<LoginResponse>
}