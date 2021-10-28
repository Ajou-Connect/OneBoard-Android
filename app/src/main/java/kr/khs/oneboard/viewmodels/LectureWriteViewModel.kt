package kr.khs.oneboard.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.repository.LectureRepository
import kr.khs.oneboard.utils.TYPE_ASSIGNMENT
import kr.khs.oneboard.utils.TYPE_NOTICE
import javax.inject.Inject

@HiltViewModel
class LectureWriteViewModel @Inject constructor(private val repository: LectureRepository) :
    BaseViewModel() {
    val status = MutableLiveData<Boolean>()

    fun writeContent(type: Boolean) {
        var result: Boolean
        viewModelScope.launch {
            result = when (type) {
                TYPE_NOTICE -> repository.postNotice()
                TYPE_ASSIGNMENT -> repository.postAssignment()
                else -> false
            }

            status.value = result
        }
    }
}