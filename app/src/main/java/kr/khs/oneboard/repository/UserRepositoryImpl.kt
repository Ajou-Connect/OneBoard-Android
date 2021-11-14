package kr.khs.oneboard.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.core.UseCase
import kr.khs.oneboard.data.Lecture
import kr.khs.oneboard.data.User
import kr.khs.oneboard.data.api.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class UserRepositoryImpl @Inject constructor(
    @Named("withJWT") private val apiService: ApiService,
) : UserRepository {

    override suspend fun getUserInfo(): UseCase<User> {
        val response: Response<User>
        try {
            withContext(Dispatchers.IO) {
                response = apiService.getUserInfo()
            }
        } catch (e: Exception) {
            Timber.e(e)
            return UseCase.error("API Error")
        }

        return UseCase.success(response.data)
    }

    override suspend fun getUserLectures(): UseCase<List<Lecture>> {
        val response: Response<List<Lecture>>
        try {
            withContext(Dispatchers.IO) {
                response = apiService.getUserLectures()
            }
        } catch (e: Exception) {
            Timber.e(e)
            return UseCase.error("")
        }
        return UseCase.success(response.data)
    }
}