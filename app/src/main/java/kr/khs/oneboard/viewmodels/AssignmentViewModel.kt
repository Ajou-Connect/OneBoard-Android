package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
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
            _list.value =
                lectureRepository.getAssignmentList(lectureId)
        }
    }

    fun deleteItem(item: Assignment) {
        viewModelScope.launch {
            val success = lectureRepository.postAssignment(item)
            if (success)
                _list.value = _list.value!!.filter { it != item }
        }
    }
}