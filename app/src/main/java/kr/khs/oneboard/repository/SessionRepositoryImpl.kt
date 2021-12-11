package kr.khs.oneboard.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.Understanding
import kr.khs.oneboard.utils.SUCCESS
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named

class SessionRepositoryImpl @Inject constructor(
    @Named("withJWT") private val apiService: ApiService,
) : SessionRepository {
    override suspend fun leaveLesson(
        lectureId: Int,
        lessonId: Int,
        sessionName: String
    ): NetworkResult<Boolean> {
        var returnValue: NetworkResult<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.leaveLesson(lectureId, lessonId, sessionName)

                returnValue = NetworkResult.success(response.result == SUCCESS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            returnValue = NetworkResult.error("error")
        }

        return returnValue
    }

    override suspend fun postAttendanceProfessor(
        lectureId: Int,
        lessonId: Int
    ): NetworkResult<Boolean> {
        var returnValue: NetworkResult<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.postAttendanceStudent(lectureId, lessonId)

                returnValue = NetworkResult.success(response.result == SUCCESS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            returnValue = NetworkResult.error("error")
        }

        return returnValue
    }

    override suspend fun postAttendanceStudent(
        lectureId: Int,
        lessonId: Int
    ): NetworkResult<Boolean> {
        var returnValue: NetworkResult<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.postAttendanceProfessor(lectureId, lessonId)

                returnValue = NetworkResult.success(response.result == SUCCESS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            returnValue = NetworkResult.error("error")
        }

        return returnValue
    }

    override suspend fun postUnderStandingProfessor(
        lectureId: Int,
        lessonId: Int,
    ): NetworkResult<Int> {
        var returnValue: NetworkResult<Int>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.professorPostUnderstanding(lectureId, lessonId)

                returnValue = if (response["result"] == SUCCESS)
                    NetworkResult.success(response.getJSONObject("data").getInt("understandId"))
                else
                    NetworkResult.error("result Fail")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            returnValue = NetworkResult.error("error")
        }

        return returnValue
    }

    override suspend fun getUnderStandingProfessor(
        lectureId: Int,
        lessonId: Int,
        understandingId: Int
    ): NetworkResult<Understanding> {
        var returnValue: NetworkResult<Understanding>

        try {
            withContext(Dispatchers.IO) {
                val response =
                    apiService.professorGetUnderStanding(
                        lectureId,
                        lessonId,
                        understandingId
                    )

                returnValue = if (response.result == SUCCESS)
                    NetworkResult.success(response.data)
                else
                    NetworkResult.error("Result Fail")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            returnValue = NetworkResult.error("error")
        }

        return returnValue
    }

    override suspend fun postUnderStandingStudent(
        lectureId: Int,
        lessonId: Int,
        understandingId: Int,
        response: String
    ): NetworkResult<Boolean> {
        var returnValue: NetworkResult<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val body = JSONObject("\"response\": ${if (response == "O") 1 else 0}")
                val response = apiService.studentPostUnderStanding(
                    lectureId,
                    lessonId,
                    understandingId,
                    body
                )

                returnValue = NetworkResult.success(response.result == SUCCESS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            returnValue = NetworkResult.error("error")
        }

        return returnValue
    }

    override suspend fun getQuiz(
        lectureId: Int,
        lessonId: Int,
        liveId: Int
    ): NetworkResult<Boolean> {
        var returnValue: NetworkResult<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.getQuiz(lectureId, lessonId, liveId)

                returnValue = NetworkResult.success(response.result == SUCCESS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            returnValue = NetworkResult.error("error")
        }

        return returnValue
    }

    override suspend fun postQuiz(
        lectureId: Int,
        lessonId: Int,
        liveId: Int,
        quizId: Int,
        answer: Int
    ): NetworkResult<Boolean> {
        var returnValue: NetworkResult<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val body = JSONObject().apply {
                    put("answer", answer)
                }
                val response = apiService.postQuiz(lectureId, lessonId, liveId, quizId, body)

                returnValue = NetworkResult.success(response.result == SUCCESS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            returnValue = NetworkResult.error("error")
        }

        return returnValue
    }
}