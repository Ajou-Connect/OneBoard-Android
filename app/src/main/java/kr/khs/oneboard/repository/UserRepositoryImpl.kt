package kr.khs.oneboard.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.data.Lecture
import kr.khs.oneboard.data.api.Response
import kr.khs.oneboard.utils.SUCCESS
import javax.inject.Inject
import javax.inject.Named

class UserRepositoryImpl @Inject constructor(
    @Named("withJWT") private val apiService: ApiService,
) : UserRepository {

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