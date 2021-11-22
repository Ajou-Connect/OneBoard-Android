package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.UseCase
import kr.khs.oneboard.data.GradeStudent
import kr.khs.oneboard.repository.LectureRepository
import javax.inject.Inject

@HiltViewModel
class GradeStudentViewModel @Inject constructor(private val repository: LectureRepository) :
    BaseViewModel() {

    private val _gradeInfo = MutableLiveData<GradeStudent>()
    val gradeInfo: LiveData<GradeStudent>
        get() = _gradeInfo

    fun getGradeInfo(lectureId: Int, studentId: Int? = null) {
        viewModelScope.launch {
            showProgress()
            val response = studentId?.let {
                repository.getStudentGrade(lectureId, it)
            } ?: repository.getStudentOwnGrade(lectureId)

            if (response.status == UseCase.Status.SUCCESS)
                _gradeInfo.value = response.data!!
            else
                setErrorMessage("성적 정보를 불러오지 못했습니다.")
            hideProgress()
        }
    }
}
