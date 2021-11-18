package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.UseCase
import kr.khs.oneboard.data.Lecture
import kr.khs.oneboard.repository.LectureRepository
import javax.inject.Inject

@HiltViewModel
class LectureDetailViewModel @Inject constructor(private val repository: LectureRepository) :
    BaseViewModel() {
    private val _lectureInfo = MutableLiveData<Lecture>()
    val lectureInfo: LiveData<Lecture>
        get() = _lectureInfo

    fun setLectureInfo(lectureId: Int) {
        viewModelScope.launch {
            val response = repository.getDetailLecture(lectureId)
            if (response.status == UseCase.Status.SUCCESS)
                _lectureInfo.value = response.data!!
        }
    }
}