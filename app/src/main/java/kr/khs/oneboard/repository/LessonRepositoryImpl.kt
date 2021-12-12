package kr.khs.oneboard.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.AnalysisResponseDto
import kr.khs.oneboard.data.Lesson
import kr.khs.oneboard.data.LessonDefaultInfo
import kr.khs.oneboard.utils.SUCCESS
import kr.khs.oneboard.utils.toPlainRequestBody
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Named

class LessonRepositoryImpl @Inject constructor(@Named("withJWT") val apiService: ApiService) :
    LessonRepository {
    override suspend fun getLessonList(id: Int): NetworkResult<List<Lesson>> {
        val returnValue: NetworkResult<List<Lesson>>
        try {
            withContext(Dispatchers.IO) {
                val response = apiService.getLessonList(id)

                if (response.result == SUCCESS)
                    returnValue = NetworkResult.success(
                        response.data.map { lesson ->
                            lesson.apply {
                                date = date.substring(0, date.length - 3)
                            }
                        }
                    )
                else
                    throw Exception()
            }
        } catch (e: Exception) {
            return NetworkResult.error("Error")
        }

        return returnValue
    }

    override suspend fun getLesson(lectureId: Int, lessonId: Int): NetworkResult<Lesson> {
        val returnValue: NetworkResult<Lesson>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.getLesson(lectureId, lessonId)
                if (response.result == SUCCESS)
                    returnValue = NetworkResult.success(response.data)
                else
                    throw Exception()
            }
        } catch (e: Exception) {
            return NetworkResult.error("Error")
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
    ): NetworkResult<Boolean> {
        val returnValue: NetworkResult<Boolean>

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

                returnValue = NetworkResult.success(
                    apiService.postLesson(
                        lectureId,
                        note,
                        map
                    ).result == SUCCESS
                )
            }
        } catch (e: Exception) {
            return NetworkResult.error("Error")
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
    ): NetworkResult<Boolean> {
        val returnValue: NetworkResult<Boolean>

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

                returnValue = NetworkResult.success(
                    apiService.putLesson(
                        lectureId,
                        lessonId,
                        note,
                        map
                    ).result == SUCCESS
                )
            }
        } catch (e: Exception) {
            return NetworkResult.error("Error")
        }

        return returnValue
    }

    override suspend fun deleteLesson(lectureId: Int, lessonId: Int): NetworkResult<Boolean> {
        val returnValue: NetworkResult<Boolean>

        try {
            withContext(Dispatchers.IO) {
                returnValue = NetworkResult.success(
                    apiService.deleteLesson(lectureId, lessonId).result == SUCCESS
                )
            }
        } catch (e: Exception) {
            return NetworkResult.error("Error")
        }

        return returnValue
    }

    override suspend fun getDefaultLessonInfo(lectureId: Int): NetworkResult<LessonDefaultInfo> {
        val returnValue: NetworkResult<LessonDefaultInfo>

        try {
            withContext(Dispatchers.IO) {
                returnValue = NetworkResult.success(
                    apiService.getDefaultLessonInfo(lectureId).data.apply {
                        defaultDateTime?.let {
                            defaultDateTime = it.substring(0, it.length - 3)
                        }
                    }
                )
            }
        } catch (e: Exception) {
            return NetworkResult.error("Error")
        }

        return returnValue
    }

    override suspend fun enterLesson(
        lectureId: Int,
        lessonId: Int,
        sessionName: String
    ): NetworkResult<Boolean> {
        var returnValue: NetworkResult<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.enterLesson(lectureId, lessonId, sessionName)
                returnValue = NetworkResult.success(response.result == SUCCESS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            returnValue = NetworkResult.error("Error!")
        }

        return returnValue
    }

    override suspend fun getAnalysis(
        lectureId: Int,
        lessonId: Int
    ): NetworkResult<AnalysisResponseDto> {
        var returnValue: NetworkResult<AnalysisResponseDto>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.getLessonAnalysis(lectureId, lessonId)

                returnValue = if (response.result == SUCCESS)
                    NetworkResult.success(response.data)
                else
                    NetworkResult.error("No Data")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            returnValue = NetworkResult.error("Error")
        }

        return returnValue
    }
}