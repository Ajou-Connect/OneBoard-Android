package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.data.Lecture
import javax.inject.Inject

@HiltViewModel
class LectureDetailViewModel @Inject constructor() : BaseViewModel() {
    private val _lectureInfo = MutableLiveData<Lecture>()
    val lectureInfo: LiveData<Lecture>
        get() = _lectureInfo

    fun setLectureInfo(lecture: Lecture) {
        _lectureInfo.value = lecture
    }
}