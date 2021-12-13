package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.Lesson
import kr.khs.oneboard.repository.LessonRepository
import javax.inject.Inject

@HiltViewModel
class LessonDetailViewModel @Inject constructor(private val repository: LessonRepository) :
    BaseViewModel() {
    private lateinit var lesson: Lesson

    private val _isCreateLesson = MutableLiveData<Boolean>()
    val isCreateLesson: LiveData<Boolean> = _isCreateLesson

    val sessionError = MutableLiveData<String>()

    fun setLesson(item: Lesson) {
        if (this::lesson.isInitialized.not())
            lesson = item
    }

    fun getLesson() = lesson

//    fun createLesson(lectureId: Int, lessonId: Int) {
//        viewModelScope.launch {
//            showProgress()
//            val response = repository.createLesson(lectureId, lessonId)
//
//            if (response.status == NetworkResult.Status.SUCCESS) {
//                _isCreateLesson.value = response.data!!
//
//                if (response.data.not())
//                    setSessionErrorMessage("다시 시도해주세요.")
//            } else {
//                setErrorMessage("수업을 생성하는데 오류가 생겼습니다.")
//            }
//            hideProgress()
//        }
//    }

    fun enterLesson(lectureId: Int, lessonId: Int, sessionName: String) {
        viewModelScope.launch {
            showProgress()
            val response = repository.enterLesson(lectureId, lessonId, sessionName)
            if (response.status == NetworkResult.Status.SUCCESS) {
                _isCreateLesson.value = response.data!!

                if (response.data.not())
                    setSessionErrorMessage("아직 수업이 생성되지 않았습니다.")
            } else {
                setToastMessage("수업에 입장하지 못했습니다.")
            }
            hideProgress()
        }
    }

    fun doneCreateLesson() {
        _isCreateLesson.value = false
    }

    fun setSessionErrorMessage(message: String = "") {
        sessionError.value = message
    }
}
