package kr.khs.oneboard.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.Lecture
import kr.khs.oneboard.data.User
import kr.khs.oneboard.data.api.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class UserRepositoryImpl @Inject constructor(
    @Named("withJWT") private val apiService: ApiService,
) : UserRepository {

    override suspend fun getUserInfo(): NetworkResult<User> {
        val response: Response<User>
        try {
            withContext(Dispatchers.IO) {
                response = apiService.getUserInfo()
            }
        } catch (e: Exception) {
            Timber.e(e)
            return NetworkResult.error("API Error")
        }

        return NetworkResult.success(response.data)
    }

    override suspend fun getUserLectures(): NetworkResult<List<Lecture>> {
        val response: Response<List<Lecture>>
        try {
            withContext(Dispatchers.IO) {
                response = apiService.getUserLectures()
            }
        } catch (e: Exception) {
            Timber.e(e)
            return NetworkResult.error("")
        }
        return NetworkResult.success(response.data)
    }
}