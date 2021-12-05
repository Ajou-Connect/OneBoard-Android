package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.UseCase
import kr.khs.oneboard.data.Assignment
import kr.khs.oneboard.data.Lecture
import kr.khs.oneboard.data.Lesson
import kr.khs.oneboard.data.Notice
import kr.khs.oneboard.repository.LectureRepository
import javax.inject.Inject

@HiltViewModel
class LectureDetailViewModel @Inject constructor(private val repository: LectureRepository) :
    BaseViewModel() {
    private val _lectureInfo = MutableLiveData<Lecture>()
    val lectureInfo: LiveData<Lecture>
        get() = _lectureInfo

    private val _latestNotice = MutableLiveData<Notice>()
    val latestNotice: LiveData<Notice>
        get() = _latestNotice

    private val _latestLesson = MutableLiveData<Lesson>()
    val latestLesson: LiveData<Lesson>
        get() = _latestLesson

    private val _latestAssignment = MutableLiveData<Assignment>()
    val latestAssignment: LiveData<Assignment>
        get() = _latestAssignment

    fun setLectureInfo(lecture: Lecture) {
        viewModelScope.launch {
            showProgress()
            _lectureInfo.value = lecture
            val response = repository.getDetailLecture(lecture.id)
            if (response.status == UseCase.Status.SUCCESS) {
                _latestNotice.value =
                    response.data!!.first ?: Notice(0, "공지사항이 없습니다.", "공지사항이 없습니다.", "", "")
                _latestLesson.value =
                    response.data.second ?: Lesson(0, 0, "다음 수업이 없습니다.", "", type = 0)
                _latestAssignment.value =
                    response.data.third ?: Assignment(0, "과제가 없습니다.", "", "", "", "", "", "", 0.0f)
            }
            hideProgress()
        }
    }
}