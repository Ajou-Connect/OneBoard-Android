package kr.khs.oneboard.repository

import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.LoginBody
import kr.khs.oneboard.data.LoginResponse
import kr.khs.oneboard.data.api.Response

interface BasicRepository {
    suspend fun loginCheck(token: String): NetworkResult<Boolean>

    suspend fun healthCheck(): NetworkResult<Boolean>

    suspend fun login(body: LoginBody): NetworkResult<Response<LoginResponse>>
}