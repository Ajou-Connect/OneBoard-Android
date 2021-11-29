package kr.khs.oneboard.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.core.UseCase
import kr.khs.oneboard.data.Lesson
import kr.khs.oneboard.utils.SUCCESS
import kr.khs.oneboard.utils.toPlainRequestBody
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Named

class LessonRepositoryImpl @Inject constructor(@Named("withJWT") val apiService: ApiService) :
    LessonRepository {
    override suspend fun getLessonList(id: Int): UseCase<List<Lesson>> {
        val returnValue: UseCase<List<Lesson>>
        try {
            withContext(Dispatchers.IO) {
                val response = apiService.getLessonList(id)

                if (response.result == SUCCESS)
                    returnValue = UseCase.success(
                        response.data
                    )
                else
                    throw Exception()
            }
        } catch (e: Exception) {
            return UseCase.error("Error")
        }

        return returnValue
    }

    override suspend fun getLesson(lectureId: Int, lessonId: Int): UseCase<Lesson> {
        val returnValue: UseCase<Lesson>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.getLesson(lectureId, lessonId)
                if (response.result == SUCCESS)
                    returnValue = UseCase.success(response.data)
                else
                    throw Exception()
            }
        } catch (e: Exception) {
            return UseCase.error("Error")
        }

        return returnValue
    }

    override suspend fun postLesson(
        lectureId: Int,
        title: String,
        date: String,
        type: Int,
        note: MultipartBody.Part?,
        room: String?,
        meetingId: String?,
        videoUrl: String?
    ): UseCase<Boolean> {
        val returnValue: UseCase<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val map = HashMap<String, RequestBody>()
                map["title"] = title.toPlainRequestBody()
                map["date"] = date.toPlainRequestBody()
                map["type"] = type.toString().toPlainRequestBody()
                room?.let {
                    map["room"] = room.toPlainRequestBody()
                }
                meetingId?.let {
                    map["meetingId"] = meetingId.toPlainRequestBody()
                }
                videoUrl?.let {
                    map["videoUrl"] = videoUrl.toPlainRequestBody()
                }

                returnValue = UseCase.success(
                    apiService.postLesson(
                        lectureId,
                        note,
                        map
                    ).result == SUCCESS
                )
            }
        } catch (e: Exception) {
            return UseCase.error("Error")
        }

        return returnValue
    }

    override suspend fun putLesson(
        lectureId: Int,
        lessonId: Int,
        title: String,
        date: String,
        type: Int,
        note: MultipartBody.Part?,
        room: String?,
        meetingId: String?,
        videoUrl: String?
    ): UseCase<Boolean> {
        val returnValue: UseCase<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val map = HashMap<String, RequestBody>()
                map["title"] = title.toPlainRequestBody()
                map["date"] = date.toPlainRequestBody()
                map["type"] = type.toString().toPlainRequestBody()
                room?.let {
                    map["room"] = room.toPlainRequestBody()
                }
                meetingId?.let {
                    map["meetingId"] = meetingId.toPlainRequestBody()
                }
                videoUrl?.let {
                    map["videoUrl"] = videoUrl.toPlainRequestBody()
                }

                returnValue = UseCase.success(
                    apiService.putLesson(
                        lectureId,
                        lessonId,
                        note,
                        map
                    ).result == SUCCESS
                )
            }
        } catch (e: Exception) {
            return UseCase.error("Error")
        }

        return returnValue
    }

    override suspend fun deleteLesson(lectureId: Int, lessonId: Int): UseCase<Boolean> {
        val returnValue: UseCase<Boolean>

        try {
            withContext(Dispatchers.IO) {
                returnValue = UseCase.success(
                    apiService.deleteLesson(lectureId, lessonId).result == SUCCESS
                )
            }
        } catch (e: Exception) {
            return UseCase.error("Error")
        }

        return returnValue
    }

}