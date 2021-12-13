package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.GradeStudent
import kr.khs.oneboard.repository.LectureRepository
import javax.inject.Inject

@HiltViewModel
class GradeStudentViewModel @Inject constructor(private val repository: LectureRepository) :
    BaseViewModel() {

    private val _gradeInfo = MutableLiveData<GradeStudent>()
    val gradeInfo: LiveData<GradeStudent>
        get() = _gradeInfo

    val grade = MutableLiveData<String>()

    private val _gradeResult = MutableLiveData<Boolean>()
    val gradeResult: LiveData<Boolean>
        get() = _gradeResult

    fun getGradeInfo(lectureId: Int, studentId: Int? = null) {
        viewModelScope.launch {
            showProgress()
            val response = studentId?.let {
                repository.getStudentGrade(lectureId, it)
            } ?: repository.getStudentOwnGrade(lectureId)

            if (response.status == NetworkResult.Status.SUCCESS) {
                _gradeInfo.value = response.data!!
                grade.value = _gradeInfo.value!!.result
            } else
                setToastMessage("성적 정보를 불러오지 못했습니다.")
            hideProgress()
        }
    }

    fun postGradeInfo(lectureId: Int, studentId: Int, newGrade: String) {
        viewModelScope.launch {
            showProgress()
            val response = repository.postStudentGrade(
                lectureId, studentId, newGrade
            )
            if (response.status == NetworkResult.Status.SUCCESS && response.data!!) {
                _gradeResult.value =
                    (response.status == NetworkResult.Status.SUCCESS && response.data)
                _gradeInfo.value!!.result = grade.value!!
            } else {
                grade.value = _gradeInfo.value!!.result
            }
            hideProgress()
        }
    }

    fun setGrade(grade: String) {
        this.grade.value = grade
    }

    fun resetGrade() {
        grade.value = _gradeInfo.value!!.result
    }
}
