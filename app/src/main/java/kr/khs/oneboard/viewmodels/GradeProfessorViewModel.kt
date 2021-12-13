package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.GradeRatio
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
            if (response.status == NetworkResult.Status.SUCCESS) {
                aRatio.value = response.data!!.aRatio.toString()
                bRatio.value = response.data.bRatio.toString()
            } else {
                setToastMessage("학점 비율을 올바르게 가져오지 못했습니다.")
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

    fun saveRatio(lectureId: Int, a: Int, b: Int) {
        viewModelScope.launch {
            showProgress()
            setRatio(a, b)
            val response = lectureRepository.postGradeRatio(
                lectureId,
                GradeRatio(aRatio.value!!.toInt(), bRatio.value!!.toInt())
            )

            setToastMessage(
                if (response.status == NetworkResult.Status.SUCCESS) {
                    getRatio(lectureId)
                    "저장되었습니다."
                } else
                    "오류가 발생했습니다.\n다시 시도해주세요"
            )
            hideProgress()
        }
    }

    fun getGradeList(lectureId: Int) {
        viewModelScope.launch {
            showProgress()
            val response = lectureRepository.getStudentGradeList(lectureId)
            if (response.status == NetworkResult.Status.SUCCESS) {
                _gradeList.value = response.data!!
            } else {
                setToastMessage("학생 성적 목록을 불러오지 못했습니다.")
            }
            hideProgress()
        }
    }
}
