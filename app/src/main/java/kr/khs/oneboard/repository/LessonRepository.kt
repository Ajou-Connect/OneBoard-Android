package kr.khs.oneboard.repository

import kr.khs.oneboard.core.UseCase
import kr.khs.oneboard.data.Lesson
import kr.khs.oneboard.data.request.LessonUpdateRequestDto

interface LessonRepository {
    suspend fun getLessonList(lectureId: Int): UseCase<List<Lesson>>

    suspend fun getLesson(lectureId: Int, lessonId: Int): UseCase<Lesson>

    suspend fun postLesson(lectureId: Int, dto: LessonUpdateRequestDto): UseCase<Boolean>

    suspend fun putLesson(lectureId: Int, lessonId: Int, dto: LessonUpdateRequestDto): UseCase<Boolean>

    suspend fun deleteLesson(lectureId: Int, lessonId: Int): UseCase<Boolean>
}