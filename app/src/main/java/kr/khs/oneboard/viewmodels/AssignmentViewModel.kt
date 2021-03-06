package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.Assignment
import kr.khs.oneboard.repository.LectureRepository
import javax.inject.Inject

@HiltViewModel
class AssignmentViewModel @Inject constructor(private val lectureRepository: LectureRepository) :
    BaseViewModel() {

    private val _list = MutableLiveData<List<Assignment>>()
    val list: LiveData<List<Assignment>>
        get() = _list

    fun getList(lectureId: Int) {
        viewModelScope.launch {
            showProgress()
            _list.value = listOf()
            val response = lectureRepository.getAssignmentList(lectureId)
            if (response.status == NetworkResult.Status.SUCCESS) {
                _list.value = response.data!!
            } else {
                setToastMessage("과제 목록을 불러오지 못했습니다.")
            }
            hideProgress()
        }
    }

    fun deleteItem(lectureId: Int, item: Assignment) {
        viewModelScope.launch {
            showProgress()
            val success = lectureRepository.deleteAssignment(lectureId, item.id)
            if (success.status == NetworkResult.Status.SUCCESS)
                _list.value = _list.value!!.filter { it != item }
            else
                setToastMessage("삭제가 올바르게 되지 않았습니다.")
            hideProgress()
        }
    }
}