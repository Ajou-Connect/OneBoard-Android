package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.UseCase
import kr.khs.oneboard.data.AttendanceLesson
import kr.khs.oneboard.data.AttendanceStudent
import kr.khs.oneboard.repository.LectureRepository
import kr.khs.oneboard.utils.compareWith
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(private val lectureRepository: LectureRepository) :
    BaseViewModel() {

    private var originalList = listOf<AttendanceStudent>()

    private val _attendanceList = MutableLiveData<List<AttendanceStudent>>()
    val attendanceList: LiveData<List<AttendanceStudent>>
        get() = _attendanceList

    fun getAttendanceList(lectureId: Int) {
        viewModelScope.launch {
            showProgress()
            val response = lectureRepository.getAttendanceList(lectureId)
            if (response.status == UseCase.Status.SUCCESS) {
                originalList = response.data!!
                _attendanceList.value = originalList
            } else {
                setErrorMessage("출석 목록을 불러오지 못했습니다.")
            }
            hideProgress()
        }
    }

    fun updateAttendance(item: AttendanceLesson) {
        _attendanceList.value!! ?: return
        out@ for (student in _attendanceList.value!!) {
            for (lesson in student.attendanceList) {
                if (lesson.lessonId == item.lessonId) {
                    lesson.status = item.status
                    break@out
                }
            }
        }
    }

    fun saveAttendanceList(lectureId: Int) {
        viewModelScope.launch {
            showProgress()
            val requestDto = originalList compareWith _attendanceList.value!!
            Timber.tag("CompareAttendance").d("$requestDto")
//            val response = lectureRepository.postAttendanceList(lectureId, requestDto)
//
//            if (response.status == UseCase.Status.SUCCESS) {
//                originalList.value = _attendanceList.value!!
//            } else {
//                setErrorMessage("출석 현황 업데이트를 실패했습니다.\n다시 시도해주세요.")
//            }

            hideProgress()
        }
    }

    fun resetAttendanceList() {
        _attendanceList.value = originalList.map { it.copy() }
    }
}