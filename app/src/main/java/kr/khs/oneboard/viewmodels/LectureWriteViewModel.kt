package kr.khs.oneboard.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.data.Assignment
import kr.khs.oneboard.data.LectureBase
import kr.khs.oneboard.data.Notice
import kr.khs.oneboard.repository.LectureRepository
import kr.khs.oneboard.utils.TYPE_ASSIGNMENT
import kr.khs.oneboard.utils.TYPE_NOTICE
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LectureWriteViewModel @Inject constructor(private val repository: LectureRepository) :
    BaseViewModel() {
    val status = MutableLiveData<Boolean>()

    fun writeContent(type: Boolean, item: LectureBase) {
        var result: Boolean
        viewModelScope.launch {
            showProgress()
            result = when (type) {
                TYPE_NOTICE -> {
                    Timber.tag("Write").d("${item as Notice}")
                    repository.postNotice(item as Notice)
                }
                TYPE_ASSIGNMENT -> {
                    Timber.tag("Write").d("${item as Assignment}")
                    repository.postAssignment(item as Assignment)
                }
                else -> false
            }

            status.value = result
            hideProgress()
        }
    }
}