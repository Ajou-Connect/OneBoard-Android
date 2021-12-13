package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.Submit
import kr.khs.oneboard.repository.LectureRepository
import javax.inject.Inject

@HiltViewModel
class ContentDetailViewModel @Inject constructor(private val lectureRepository: LectureRepository) :
    BaseViewModel() {

    private val _assignmentList = MutableLiveData<List<Submit>>()
    val assignmentList: LiveData<List<Submit>>
        get() = _assignmentList

    fun getSubmitList(lectureId: Int, assignmentId: Int) {
        viewModelScope.launch {
            showProgress()
            val response = lectureRepository.getSubmitAssignmentList(lectureId, assignmentId)
            if (response.status == NetworkResult.Status.SUCCESS)
                _assignmentList.value = response.data!!
            else
                setToastMessage("제출된 과제 목록을 받아오지 못했습니다.")
            hideProgress()
        }
    }
}