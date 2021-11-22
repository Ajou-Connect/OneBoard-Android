package kr.khs.oneboard.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.UseCase
import kr.khs.oneboard.data.Submit
import kr.khs.oneboard.repository.LectureRepository
import javax.inject.Inject

@HiltViewModel
class LectureReadViewModel @Inject constructor(private val repository: LectureRepository) :
    BaseViewModel() {
    val assignmentData = MutableLiveData<Submit>()

    val isSubmit = MutableLiveData(false)

    fun getAssignmentSubmitInfo(lectureId: Int, assignmentId: Int) {
        viewModelScope.launch {
            showProgress()
            val response = repository.getMyAssignmentSubmitInfo(lectureId, assignmentId)
            if (response.status == UseCase.Status.SUCCESS) {
                isSubmit.value = true
                assignmentData.value = response.data!!
            } else {
                isSubmit.value = false
            }
            hideProgress()
        }
    }
}
