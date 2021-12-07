package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.AttendanceStudent
import kr.khs.oneboard.data.Lesson
import kr.khs.oneboard.data.request.AttendanceUpdateRequestDto
import kr.khs.oneboard.repository.LectureRepository
import kr.khs.oneboard.repository.LessonRepository
import kr.khs.oneboard.utils.toUpdateRequestDTO
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val lectureRepository: LectureRepository,
    private val lessonRepository: LessonRepository
) :
    BaseViewModel() {

    private val _attendanceList = MutableLiveData<List<AttendanceStudent>>()
    val attendanceList: LiveData<List<AttendanceStudent>>
        get() = _attendanceList

    val lessonList = MutableLiveData<List<Lesson>>()

    fun getAttendanceList(lectureId: Int) {
        viewModelScope.launch {
            showProgress()
            val response = lectureRepository.getAttendanceList(lectureId)
            if (response.status == NetworkResult.Status.SUCCESS) {
                lessonList.value = lessonRepository.getLessonList(lectureId).data!!
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

            if (response.status == NetworkResult.Status.SUCCESS && response.data!!) {
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
            if (response.status == NetworkResult.Status.SUCCESS) {
                _attendanceList.value = listOf(response.data!!)
            } else {
                setErrorMessage("출석 목록을 불러오지 못했습니다.")
            }
            hideProgress()
        }
    }

    // isAttend - true : 2 attendance, false : 0 absent
    // if lessonId = -1 -> target : All students or specific student, else target : specific lesson
    // if studentId = -1 -> target : All students or specific lesson, else target : specific student
    fun changeAllAttendance(
        lectureId: Int,
        isAttend: Boolean,
        lessonIdx: Int = -1,
        studentIdx: Int = -1
    ) {

        viewModelScope.launch {
            showProgress()
            val requestDto =
                when {
                    lessonIdx != -1 -> {
                        val lessonId = lessonList.value!![lessonIdx].lessonId
                        AttendanceUpdateRequestDto(
                            attendanceList.value!!.map {
                                AttendanceUpdateRequestDto.AttendanceUpdate(
                                    it.studentId,
                                    lessonId,
                                    if (isAttend) 2 else 0
                                )
                            }
                        )
                    }
                    studentIdx != -1 -> {
                        val studentId = attendanceList.value!![studentIdx].studentId
                        AttendanceUpdateRequestDto(
                            attendanceList.value!!.filter { it.studentId == studentId }[0].attendanceList.map {
                                AttendanceUpdateRequestDto.AttendanceUpdate(
                                    studentId,
                                    it.lessonId,
                                    if (isAttend) 2 else 0
                                )
                            }
                        )
                    }
                    else -> {
                        AttendanceUpdateRequestDto(attendanceList.value!!.toUpdateRequestDTO().updateDataList.map {
                            it.apply { it.status = if (isAttend) 2 else 0 }
                        })
                    }
                }


            val response = lectureRepository.postAttendanceList(lectureId, requestDto)

            if (response.status == NetworkResult.Status.SUCCESS && response.data!!) {
                setErrorMessage("저장되었습니다.")
            } else {
                setErrorMessage("출석 현황 업데이트를 실패했습니다.\n다시 시도해주세요.")
            }

            hideProgress()
            getAttendanceList(lectureId)
        }
    }
}