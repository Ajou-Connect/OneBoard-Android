package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.repository.LessonRepository
import kr.khs.oneboard.utils.TYPE_FACE_TO_FACE
import javax.inject.Inject

@HiltViewModel
class LessonWriteViewModel @Inject constructor(private val lessonRepository: LessonRepository) :
    BaseViewModel() {

    private val _lessonType = MutableLiveData(TYPE_FACE_TO_FACE)
    val lessonType: LiveData<Int>
        get() = _lessonType

    fun setLessonType(type: Int) {
        if (_lessonType.value == type)
            return

        _lessonType.value = type
    }

    // todo 생성
    fun writeLesson(data: Any): Boolean {
        var response = false
        viewModelScope.launch {
            showProgress()
            response = true
            delay(1000)
            hideProgress()
        }
        return response
    }
}
