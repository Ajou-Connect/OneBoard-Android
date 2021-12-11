package kr.khs.oneboard.repository

import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.Lesson
import kr.khs.oneboard.data.LessonDefaultInfo
import okhttp3.MultipartBody

interface LessonRepository {
    suspend fun getLessonList(lectureId: Int): NetworkResult<List<Lesson>>

    suspend fun getLesson(lectureId: Int, lessonId: Int): NetworkResult<Lesson>

    suspend fun postLesson(
        lectureId: Int,
        title: String,
        date: String,
        type: Int,
        note: MultipartBody.Part? = null,
        room: String? = null,
        meetingId: String? = null,
        videoUrl: String? = null
    ): NetworkResult<Boolean>

    suspend fun putLesson(
        lectureId: Int,
        lessonId: Int,
        title: String,
        date: String,
        type: Int,
        note: MultipartBody.Part? = null,
        room: String? = null,
        meetingId: String? = null,
        videoUrl: String? = null
    ): NetworkResult<Boolean>

    suspend fun deleteLesson(lectureId: Int, lessonId: Int): NetworkResult<Boolean>

    suspend fun getDefaultLessonInfo(lectureId: Int): NetworkResult<LessonDefaultInfo>

    suspend fun enterLesson(
        lectureId: Int,
        lessonId: Int,
        sessionName: String
    ): NetworkResult<Boolean>
}