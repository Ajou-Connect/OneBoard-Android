package kr.khs.oneboard.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.core.UseCase
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
    override suspend fun loginCheck(token: String): UseCase<Boolean> {
        val response: BasicResponse
        withContext(Dispatchers.IO) {
            response = try {
                apiService.loginCheck(token)
            } catch (e: Exception) {
                BasicResponseImpl(FAIL)
            }
        }
        return if (response.result == SUCCESS)
            UseCase.success(true)
        else
            UseCase.error("Invalidate Token")
    }

    override suspend fun healthCheck(): UseCase<Boolean> {
        val response: Response<Boolean>
        try {
            withContext(Dispatchers.IO) {
//            response = apiService.healthCheck()
                response = Response(SUCCESS, true)
            }
        } catch (e: Exception) {
            return UseCase.error("")
        }

        return UseCase.success(response.data)
    }

    override suspend fun login(body: LoginBody): UseCase<Response<LoginResponse>> {
        val response: Response<LoginResponse>
        try {
            withContext(Dispatchers.IO) {
                response = apiService.login(body)
            }
        } catch (e: Exception) {
            return UseCase.error("Invalidate email or password")
        }
        return UseCase.success(response)
    }
}