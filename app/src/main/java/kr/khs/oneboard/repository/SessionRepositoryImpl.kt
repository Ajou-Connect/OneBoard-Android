package kr.khs.oneboard.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.Quiz
import kr.khs.oneboard.data.StudentQuizResponse
import kr.khs.oneboard.data.Understanding
import kr.khs.oneboard.data.UnderstandingStudentResponseWrapper
import kr.khs.oneboard.data.request.QuizRequestDto
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
        lessonId: Int,
        sessionName: String
    ): NetworkResult<Boolean> {
        var returnValue: NetworkResult<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.postAttendanceProfessor(lectureId, lessonId, sessionName)

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
        lessonId: Int,
        sessionName: String
    ): NetworkResult<Boolean> {
        var returnValue: NetworkResult<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.postAttendanceStudent(lectureId, lessonId, sessionName)

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

                returnValue = if (response.result == SUCCESS)
                    NetworkResult.success(response.data.understandId)
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
                val body = UnderstandingStudentResponseWrapper(if (response == "O") 1 else 0)
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

    override suspend fun postQuizProfessor(
        lectureId: Int,
        lessonId: Int,
        quizRequestDto: QuizRequestDto
    ): NetworkResult<Int> {
        var returnValue: NetworkResult<Int>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.postQuizProfessor(lectureId, lessonId, quizRequestDto)

                returnValue = if (response.result == SUCCESS)
                    NetworkResult.success(response.data.quizId)
                else
                    NetworkResult.success(-1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            returnValue = NetworkResult.error("Error")
        }

        return returnValue
    }

    override suspend fun getQuizProfessor(
        lectureId: Int,
        lessonId: Int,
        quizId: Int
    ): NetworkResult<Quiz> {
        var returnValue: NetworkResult<Quiz>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.getQuizProfessor(lectureId, lessonId, quizId)

                returnValue = NetworkResult.success(response.data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            returnValue = NetworkResult.error("error")
        }

        return returnValue
    }

    override suspend fun getQuizStudent(
        lectureId: Int,
        lessonId: Int,
        quizId: Int
    ): NetworkResult<StudentQuizResponse> {
        var returnValue: NetworkResult<StudentQuizResponse>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.getQuizStudent(lectureId, lessonId, quizId)

                returnValue = NetworkResult.success(response.data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            returnValue = NetworkResult.error("error")
        }

        return returnValue
    }

    override suspend fun postQuizStudent(
        lectureId: Int,
        lessonId: Int,
        quizId: Int,
        answer: Int
    ): NetworkResult<Boolean> {
        var returnValue: NetworkResult<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val body = JSONObject().apply {
                    put("response", answer)
                }
                val response = apiService.postQuizStudent(lectureId, lessonId, quizId, body)

                returnValue = NetworkResult.success(response.result == SUCCESS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            returnValue = NetworkResult.error("error")
        }

        return returnValue
    }
}