package kr.khs.oneboard.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.data.Lecture
import kr.khs.oneboard.data.api.Response
import kr.khs.oneboard.utils.SUCCESS

class UserRepositoryImpl(val apiService: ApiService) : UserRepository {
    override suspend fun healthCheck(): Boolean {
        val response: Response<Boolean>
        withContext(Dispatchers.IO) {
//            response = apiService.healthCheck()
            response = Response(SUCCESS, true)
        }
        return response.data
    }

    override suspend fun login(email: String, password: String): Boolean {
        // TODO: 2021/10/20 로그인 성공 시 토큰 저장
        val response: Response<Boolean>
        withContext(Dispatchers.IO) {
//            response = apiService.login()
            response = Response(SUCCESS, true)
        }
        return response.data
    }

    override suspend fun loginCheck(token: String): Boolean {
        val response: Response<Boolean>
        withContext(Dispatchers.IO) {
//            response = apiService.loginCheck()
            response = Response(SUCCESS, false)
        }
        return response.data
    }

    override suspend fun logout() {
        TODO("Not yet implemented")
    }

    override suspend fun getUserInfo() {
        TODO("Not yet implemented")
    }

    override suspend fun getUserLectures(): List<Lecture> {
        val response: Response<List<Lecture>>
        withContext(Dispatchers.IO) {
//            response = apiService.getUserLectures()
            response = Response(
                SUCCESS,
                listOf(
                    Lecture(id = 1, title = "SW 캡스톤 디자인1", semester = "2021-2", professor = "윤대균"),
                    Lecture(id = 2, title = "SW 캡스톤 디자인2", semester = "2021-2", professor = "윤대균"),
                    Lecture(id = 3, title = "SW 캡스톤 디자인3", semester = "2021-2", professor = "윤대균"),
                    Lecture(id = 4, title = "SW 캡스톤 디자인4", semester = "2021-2", professor = "윤대균"),
                    Lecture(id = 5, title = "SW 캡스톤 디자인5", semester = "2021-2", professor = "윤대균"),
                    Lecture(id = 6, title = "SW 캡스톤 디자인6", semester = "2021-2", professor = "윤대균"),
                )
            )
        }
        return response.data
    }
}