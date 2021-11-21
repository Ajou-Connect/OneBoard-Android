package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.UseCase
import kr.khs.oneboard.data.AttendanceStudent
import kr.khs.oneboard.repository.LectureRepository
import kr.khs.oneboard.utils.toUpdateRequestDTO
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(private val lectureRepository: LectureRepository) :
    BaseViewModel() {

    private val _attendanceList = MutableLiveData<List<AttendanceStudent>>()
    val attendanceList: LiveData<List<AttendanceStudent>>
        get() = _attendanceList

    fun getAttendanceList(lectureId: Int) {
        viewModelScope.launch {
            showProgress()
            val response = lectureRepository.getAttendanceList(lectureId)
            if (response.status == UseCase.Status.SUCCESS) {
                _attendanceList.value = response.data!!
            } else {
                setErrorMessage("출석 목록을 불러오지 못했습니다.")
            }
            hideProgress()
        }
    }

    fun updateAttendance(item: AttendanceStudent) {
        _attendanceList.value ?: return

        out@ for (student in _attendanceList.value!!) {
            if (student.studentId == item.studentId) {
                for (idx in student.attendanceList.indices) {
                    if (student.attendanceList[idx].lessonId == item.attendanceList[idx].lessonId) {
                        student.attendanceList[idx].status = item.attendanceList[idx].status
                        break@out
                    }
                }
            }
        }

        val temp = _attendanceList.value!!
        _attendanceList.value = temp
    }

    fun saveAttendanceList(lectureId: Int) {
        viewModelScope.launch {
            showProgress()
            val requestDto = _attendanceList.value!!.toUpdateRequestDTO()
            Timber.tag("CompareAttendance").d("$requestDto")
            val response = lectureRepository.postAttendanceList(lectureId, requestDto)

            if (response.status == UseCase.Status.SUCCESS && response.data!!) {
                setErrorMessage("저장되었습니다.")
            } else {
                setErrorMessage("출석 현황 업데이트를 실패했습니다.\n다시 시도해주세요.")
            }

            hideProgress()
        }
    }

    fun resetAttendanceList(lectureId: Int) {
        _attendanceList.value = listOf()
        getAttendanceList(lectureId)
    }

    fun getMyAttendance(lectureId: Int) {
        viewModelScope.launch {
            showProgress()
            val response = lectureRepository.getMyAttendance(lectureId)
            if (response.status == UseCase.Status.SUCCESS) {
                _attendanceList.value = listOf(response.data!!)
            } else {
                setErrorMessage("출석 목록을 불러오지 못했습니다.")
            }
            hideProgress()
        }
    }
}