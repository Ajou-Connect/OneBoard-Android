package kr.khs.oneboard.repository

import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.Understanding

interface SessionRepository {
    suspend fun leaveLesson(
        lectureId: Int,
        lessonId: Int,
        sessionName: String
    ): NetworkResult<Boolean>

    suspend fun postAttendanceProfessor(
        lectureId: Int,
        lessonId: Int
    ): NetworkResult<Boolean>

    suspend fun postAttendanceStudent(
        lectureId: Int,
        lessonId: Int
    ): NetworkResult<Boolean>

    suspend fun postUnderStandingProfessor(
        lectureId: Int,
        lessonId: Int,
    ): NetworkResult<Int>

    suspend fun getUnderStandingProfessor(
        lectureId: Int,
        lessonId: Int,
        understandingId: Int
    ): NetworkResult<Understanding>

    suspend fun postUnderStandingStudent(
        lectureId: Int,
        lessonId: Int,
        understandingId: Int,
        response: String
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