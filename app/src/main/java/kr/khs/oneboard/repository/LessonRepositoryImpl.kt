package kr.khs.oneboard.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.data.Lesson
import kr.khs.oneboard.data.api.Response
import kr.khs.oneboard.utils.SUCCESS
import kr.khs.oneboard.utils.TYPE_FACE_TO_FACE
import kr.khs.oneboard.utils.TYPE_NON_FACE_TO_FACE
import kr.khs.oneboard.utils.TYPE_RECORDING
import javax.inject.Inject
import kotlin.random.Random

class LessonRepositoryImpl @Inject constructor(val apiService: ApiService) : LessonRepository {
    override suspend fun getLessonList(id: Int): List<Lesson> {
        val response: Response<List<Lesson>>
        withContext(Dispatchers.IO) {
//            response = apiService.getLessonList(id)
            response = Response(
                SUCCESS,
                (0 until 6)
                    .map {
                        val random = (0..2).random()
                        Lesson(
                            id = it,
                            title = "수업 $it",
                            date = "2021-11-$it",
                            note = if (Random.nextBoolean()) "https://naver.com" else null,
                            type = random,
                            room = if (random == TYPE_FACE_TO_FACE) "팔달관 30${it}호" else null,
                            meetingId = if (random == TYPE_NON_FACE_TO_FACE) String.format(
                                "%6d",
                                (0 until 1_000_000).random()
                            ) else null,
                            videoUrl = if (random == TYPE_RECORDING) "https://google${it}.com" else null
                        )
                    }
            )
        }

        return response.data
    }

}