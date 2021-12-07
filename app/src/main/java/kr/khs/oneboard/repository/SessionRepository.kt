package kr.khs.oneboard.repository

import kr.khs.oneboard.core.NetworkResult

interface SessionRepository {
    suspend fun leaveLesson(
        lectureId: Int,
        lessonId: Int
    ): NetworkResult<Boolean>

    suspend fun postAttendanceProfessor(
        lectureId: Int,
        lessonId: Int
    ): NetworkResult<Boolean>

    suspend fun postAttendanceStudent(
        lectureId: Int,
        lessonId: Int
    ): NetworkResult<Boolean>

    suspend fun postUnderStanding(
        lectureId: Int,
        lessonId: Int,
        liveId: Int,
        select: String
    ): NetworkResult<Boolean>

    suspend fun getUnderStanding(
        lectureId: Int,
        lessonId: Int,
        liveId: Int,
        understandingId: Int
    ): NetworkResult<Boolean>

    suspend fun getQuiz(
        lectureId: Int,
        lessonId: Int,
        liveId: Int
    ): NetworkResult<Boolean>

    suspend fun postQuiz(
        lectureId: Int,
        lessonId: Int,
        liveId: Int,
        quizId: Int,
        answer: Int
    ): NetworkResult<Boolean>
}