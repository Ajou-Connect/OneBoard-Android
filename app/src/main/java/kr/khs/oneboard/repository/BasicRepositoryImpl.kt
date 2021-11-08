package kr.khs.oneboard.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.data.LoginBody
import kr.khs.oneboard.data.LoginResponse
import kr.khs.oneboard.data.api.BasicResponse
import kr.khs.oneboard.data.api.BasicResponseImpl
import kr.khs.oneboard.data.api.Response
import kr.khs.oneboard.utils.FAIL
import kr.khs.oneboard.utils.SUCCESS
import javax.inject.Inject
import javax.inject.Named

class BasicRepositoryImpl @Inject constructor(
    @Named("withoutJWT") private val apiService: ApiService
) : BasicRepository {
    override suspend fun loginCheck(token: String): Boolean {
        val response: BasicResponse
        withContext(Dispatchers.IO) {
            response = try {
                apiService.loginCheck(token)
            } catch (e: Exception) {
                BasicResponseImpl(FAIL)
            }
        }
        return response.result == SUCCESS
    }

    override suspend fun healthCheck(): Boolean {
        val response: Response<Boolean>
        withContext(Dispatchers.IO) {
//            response = apiService.healthCheck()
            response = Response(SUCCESS, true)
        }
        return response.data
    }

    override suspend fun login(body: LoginBody): Response<LoginResponse> {
        val response: Response<LoginResponse>
        withContext(Dispatchers.IO) {
            response = apiService.login(body)
        }
        return response
    }
}