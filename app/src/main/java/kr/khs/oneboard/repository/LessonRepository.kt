package kr.khs.oneboard.repository

import kr.khs.oneboard.data.Lesson

interface LessonRepository {
    suspend fun getLessonList(id: Int): List<Lesson>
}