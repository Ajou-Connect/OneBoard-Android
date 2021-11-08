package kr.khs.oneboard.viewmodels

import dagger.hilt.android.lifecycle.HiltViewModel
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.data.Lesson
import javax.inject.Inject

@HiltViewModel
class LessonDetailViewModel @Inject constructor() : BaseViewModel() {
    private lateinit var lesson: Lesson

    fun setLesson(item: Lesson) {
        if (this::lesson.isInitialized.not())
            lesson = item
    }

    fun getLesson() = lesson
}
