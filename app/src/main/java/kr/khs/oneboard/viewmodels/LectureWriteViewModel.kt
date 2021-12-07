package kr.khs.oneboard.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.request.AssignmentUpdateRequestDto
import kr.khs.oneboard.data.request.NoticeUpdateRequestDto
import kr.khs.oneboard.repository.LectureRepository
import kr.khs.oneboard.utils.TYPE_ASSIGNMENT
import kr.khs.oneboard.utils.TYPE_NOTICE
import okhttp3.MultipartBody
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LectureWriteViewModel @Inject constructor(private val repository: LectureRepository) :
    BaseViewModel() {
    val status = MutableLiveData<Boolean>()

    fun editContent(
        lectureId: Int,
        contentId: Int,
        type: Boolean,
        notice: NoticeUpdateRequestDto? = null,
        assignment: AssignmentUpdateRequestDto? = null,
        file: MultipartBody.Part? = null
    ) {
        if (contentId == -1) {
            setErrorMessage("올바르지 않은 접근입니다.")
            return
        }

        var result: NetworkResult<Boolean>
        viewModelScope.launch {
            result = when (type) {
                TYPE_NOTICE -> {
                    repository.putNotice(lectureId, contentId, notice!!)
                }
                TYPE_ASSIGNMENT -> {
                    repository.putAssignment(lectureId, contentId, assignment!!, file)
                }
                else -> throw Exception("Unknown Type")
            }

            status.value =
                if (result.status == NetworkResult.Status.SUCCESS) result.data!! else false
        }
    }

    fun writeContent(
        lectureId: Int,
        type: Boolean,
        notice: NoticeUpdateRequestDto? = null,
        assignment: AssignmentUpdateRequestDto? = null,
        file: MultipartBody.Part? = null
    ) {
        var result: NetworkResult<Boolean>
        Timber.tag("WriteAssignment").d("file is null : ${file == null}")
        viewModelScope.launch {
            showProgress()
            result = when (type) {
                TYPE_NOTICE -> {
                    Timber.tag("Write").d("$notice")
                    repository.postNotice(lectureId, notice!!)
                }
                TYPE_ASSIGNMENT -> {
                    Timber.tag("Write").d("$assignment")
                    repository.postAssignment(lectureId, assignment!!, file)
                }
                else -> throw Exception("Unknown Type")
            }

            status.value =
                if (result.status == NetworkResult.Status.SUCCESS) result.data!! else false
            hideProgress()
        }
    }
}