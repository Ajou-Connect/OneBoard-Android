package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.UseCase
import kr.khs.oneboard.repository.LessonRepository
import kr.khs.oneboard.utils.TYPE_FACE_TO_FACE
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class LessonWriteViewModel @Inject constructor(private val lessonRepository: LessonRepository) :
    BaseViewModel() {

    private val _lessonType = MutableLiveData(TYPE_FACE_TO_FACE)
    val lessonType: LiveData<Int>
        get() = _lessonType

    private val _updateLesson = MutableLiveData(false)
    val updateLesson: LiveData<Boolean>
        get() = _updateLesson

    fun setLessonType(type: Int) {
        if (_lessonType.value == type)
            return

        _lessonType.value = type
    }

    fun writeLesson(
        lectureId: Int,
        title: String,
        date: String,
        note: MultipartBody.Part? = null,
        room: String? = null,
        meetingId: String? = null,
        videoUrl: String? = null
    ) {
        viewModelScope.launch {
            showProgress()
            val response = lessonRepository.postLesson(
                lectureId,
                title,
                date,
                lessonType.value!!,
                note,
                room,
                meetingId,
                videoUrl,
            )

            if (response.status == UseCase.Status.SUCCESS)
                _updateLesson.value = true
            else
                setErrorMessage("수업이 생성되지 못했습니다.")

            hideProgress()
        }
    }

    fun editLesson(
        lectureId: Int,
        lessonId: Int,
        title: String,
        date: String,
        note: MultipartBody.Part? = null,
        room: String? = null,
        meetingId: String? = null,
        videoUrl: String? = null
    ) {
        viewModelScope.launch {
            showProgress()
            val response = lessonRepository.putLesson(
                lectureId,
                lessonId,
                title,
                date,
                lessonType.value!!,
                note,
                room,
                meetingId,
                videoUrl,
            )

            if (response.status == UseCase.Status.SUCCESS)
                _updateLesson.value = true
            else
                setErrorMessage("수업이 생성되지 못했습니다.")

            hideProgress()
        }
    }
}
