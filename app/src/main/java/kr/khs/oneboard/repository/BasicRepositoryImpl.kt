package kr.khs.oneboard.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.LoginBody
import kr.khs.oneboard.data.LoginResponse
import kr.khs.oneboard.data.api.BasicResponse
import kr.khs.oneboard.data.api.BasicResponseImpl
import kr.khs.oneboard.data.api.Response
import kr.khs.oneboard.utils.FAIL
import kr.khs.oneboard.utils.SUCCESS
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class BasicRepositoryImpl @Inject constructor(
    @Named("withoutJWT") private val apiService: ApiService
) : BasicRepository {
    override suspend fun loginCheck(token: String): NetworkResult<Boolean> {
        val response: BasicResponse
        withContext(Dispatchers.IO) {
            response = try {
                apiService.loginCheck(token)
            } catch (e: Exception) {
                Timber.e(e)
                BasicResponseImpl(FAIL)
            }
        }
        return if (response.result == SUCCESS)
            NetworkResult.success(true)
        else
            NetworkResult.error("Invalidate Token")
    }

    override suspend fun healthCheck(): NetworkResult<Boolean> {
        val response: Response<Boolean>
        try {
            withContext(Dispatchers.IO) {
//            response = apiService.healthCheck()
                response = Response(SUCCESS, true)
            }
        } catch (e: Exception) {
            Timber.e(e)
            return NetworkResult.error("")
        }

        return NetworkResult.success(response.data)
    }

    override suspend fun login(body: LoginBody): NetworkResult<Response<LoginResponse>> {
        val response: Response<LoginResponse>
        try {
            withContext(Dispatchers.IO) {
                response = apiService.login(body)
            }
        } catch (e: Exception) {
            Timber.e(e)
            return NetworkResult.error("Invalidate email or password")
        }
        return NetworkResult.success(response)
    }
}