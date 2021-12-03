package kr.khs.oneboard.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.core.UseCase
import kr.khs.oneboard.utils.SUCCESS
import javax.inject.Inject
import javax.inject.Named

class SessionRepositoryImpl @Inject constructor(
    @Named("withJWT") private val apiService: ApiService,
) : SessionRepository {
    override suspend fun leaveLesson(lectureId: Int, lessonId: Int): UseCase<Boolean> {
        var returnValue: UseCase<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.leaveLesson(lectureId, lessonId)

                returnValue = UseCase.success(response.result == SUCCESS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            returnValue = UseCase.error("error")
        }

        return returnValue
    }

    override suspend fun postAttendanceProfessor(lectureId: Int, lessonId: Int): UseCase<Boolean> {
        var returnValue: UseCase<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.postAttendanceStudent(lectureId, lessonId)

                returnValue = UseCase.success(response.result == SUCCESS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            returnValue = UseCase.error("error")
        }

        return returnValue
    }

    override suspend fun postAttendanceStudent(lectureId: Int, lessonId: Int): UseCase<Boolean> {
        var returnValue: UseCase<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.postAttendanceProfessor(lectureId, lessonId)

                returnValue = UseCase.success(response.result == SUCCESS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            returnValue = UseCase.error("error")
        }

        return returnValue
    }

    override suspend fun postUnderStanding(
        lectureId: Int,
        lessonId: Int,
        liveId: Int
    ): UseCase<Boolean> {
        var returnValue: UseCase<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.postUnderStanding(lectureId, lessonId, liveId)

                returnValue = UseCase.success(response.result == SUCCESS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            returnValue = UseCase.error("error")
        }

        return returnValue
    }

    override suspend fun getUnderStanding(
        lectureId: Int,
        lessonId: Int,
        liveId: Int,
        understandingId: Int
    ): UseCase<Boolean> {
        var returnValue: UseCase<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val response =
                    apiService.getUnderStanding(lectureId, lessonId, liveId, understandingId)

                returnValue = UseCase.success(response.result == SUCCESS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            returnValue = UseCase.error("error")
        }

        return returnValue
    }

    override suspend fun getQuiz(lectureId: Int, lessonId: Int, liveId: Int): UseCase<Boolean> {
        var returnValue: UseCase<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.getQuiz(lectureId, lessonId, liveId)

                returnValue = UseCase.success(response.result == SUCCESS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            returnValue = UseCase.error("error")
        }

        return returnValue
    }

    override suspend fun postQuiz(
        lectureId: Int,
        lessonId: Int,
        liveId: Int,
        quizId: Int
    ): UseCase<Boolean> {
        var returnValue: UseCase<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.postQuiz(lectureId, lessonId, liveId, quizId)

                returnValue = UseCase.success(response.result == SUCCESS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            returnValue = UseCase.error("error")
        }

        return returnValue
    }
}