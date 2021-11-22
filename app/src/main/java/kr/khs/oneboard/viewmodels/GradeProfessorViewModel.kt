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
class GradeProfessorViewModel @Inject constructor(private val lectureRepository: LectureRepository) :
    BaseViewModel() {

    private val _gradeList = MutableLiveData<List<GradeStudent>>()
    val gradeList: LiveData<List<GradeStudent>>
        get() = _gradeList

    val aRatio = MutableLiveData<String>()

    val bRatio = MutableLiveData<String>()

    fun getRatio(lectureId: Int) {
        viewModelScope.launch {
            showProgress()
            val response = lectureRepository.getGradeRatio(lectureId)
            if (response.status == UseCase.Status.SUCCESS) {
                aRatio.value = response.data!!.aRatio.toString()
                bRatio.value = response.data.bRatio.toString()
            } else {
                setErrorMessage("학점 비율을 올바르게 가져오지 못했습니다.")
            }
            hideProgress()
        }
    }

    fun setRatio(a: Int? = null, b: Int? = null) {
        a?.let { a ->
            aRatio.value = a.toString()
        }
        b?.let { b ->
            bRatio.value = b.toString()
        }
    }

    fun getGradeList(lectureId: Int) {
        viewModelScope.launch {
            showProgress()
//            _gradeList.value = lectureRepository.get(lectureId)
            hideProgress()
        }
    }
}
