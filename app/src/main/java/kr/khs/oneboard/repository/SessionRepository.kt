package kr.khs.oneboard.repository

import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.Quiz
import kr.khs.oneboard.data.StudentQuizResponse
import kr.khs.oneboard.data.Understanding
import kr.khs.oneboard.data.request.QuizRequestDto

interface SessionRepository {
    suspend fun leaveLesson(
        lectureId: Int,
        lessonId: Int,
        sessionName: String
    ): NetworkResult<Boolean>

    suspend fun postAttendanceProfessor(
        lectureId: Int,
        lessonId: Int,
        sessionName: String
    ): NetworkResult<Boolean>

    suspend fun postAttendanceStudent(
        lectureId: Int,
        lessonId: Int,
        sessionName: String
    ): NetworkResult<Boolean>

    suspend fun postUnderStandingProfessor(
        lectureId: Int,
        lessonId: Int,
        sessionName: String,
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
        sessionName: String,
        response: String
    ): NetworkResult<Boolean>

    suspend fun postQuizProfessor(
        lectureId: Int,
        lessonId: Int,
        quizRequestDto: QuizRequestDto,
        sessionName: String
    ): NetworkResult<Int>

    suspend fun getQuizProfessor(
        lectureId: Int,
        lessonId: Int,
        quizId: Int
    ): NetworkResult<Quiz>

    suspend fun getQuizStudent(
        lectureId: Int,
        lessonId: Int,
        quizId: Int
    ): NetworkResult<StudentQuizResponse>

    suspend fun postQuizStudent(
        lectureId: Int,
        lessonId: Int,
        quizId: Int,
        sessionName: String,
        answer: Int
    ): NetworkResult<Boolean>
}