package kr.khs.oneboard.repository

import kr.khs.oneboard.core.UseCase
import kr.khs.oneboard.data.Lesson
import okhttp3.MultipartBody

interface LessonRepository {
    suspend fun getLessonList(lectureId: Int): UseCase<List<Lesson>>

    suspend fun getLesson(lectureId: Int, lessonId: Int): UseCase<Lesson>

    suspend fun postLesson(
        lectureId: Int,
        title: String,
        date: String,
        type: Int,
        note: MultipartBody.Part? = null,
        room: String? = null,
        meetingId: String? = null,
        videoUrl: String? = null
    ): UseCase<Boolean>

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
    ): UseCase<Boolean>

    suspend fun deleteLesson(lectureId: Int, lessonId: Int): UseCase<Boolean>
}