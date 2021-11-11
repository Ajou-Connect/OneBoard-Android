package kr.khs.oneboard.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.UseCase
import kr.khs.oneboard.data.request.NoticeUpdateRequestDto
import kr.khs.oneboard.repository.LectureRepository
import kr.khs.oneboard.utils.TYPE_NOTICE
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LectureWriteViewModel @Inject constructor(private val repository: LectureRepository) :
    BaseViewModel() {
    val status = MutableLiveData<Boolean>()

    fun writeContent(lectureId: Int, type: Boolean, notice: NoticeUpdateRequestDto? = null) {
        var result: UseCase<Boolean>
        viewModelScope.launch {
            result = when (type) {
                TYPE_NOTICE -> {
                    Timber.tag("Write").d("$notice")
                    repository.postNotice(lectureId, notice!!)
                }
//                TYPE_ASSIGNMENT -> {
//                    Timber.tag("Write").d("${item as Assignment}")
//                    repository.postAssignment(item as Assignment)
//                }
                else -> throw Exception("Unknown Type")
            }

            status.value = if (result.status == UseCase.Status.SUCCESS) result.data!! else false
        }
    }
}