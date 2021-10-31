package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.data.Notice
import kr.khs.oneboard.repository.LectureRepository
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(private val lectureRepository: LectureRepository) :
    BaseViewModel() {

    private val _list = MutableLiveData<List<Notice>>()
    val list: LiveData<List<Notice>>
        get() = _list

    fun getList(lectureId: Int) {
        viewModelScope.launch {
            _list.value =
                lectureRepository.getNoticeList(lectureId)
        }
    }

    fun deleteItem(item: Notice) {
        viewModelScope.launch {
            showProgress()
            val success = lectureRepository.postNotice(item)
            if (success)
                _list.value = _list.value!!.filter { it != item }
            hideProgress()
        }
    }
}