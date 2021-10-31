package kr.khs.oneboard.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.data.Submit
import kr.khs.oneboard.repository.LectureRepository
import javax.inject.Inject

@HiltViewModel
class SubmitDetailViewModel @Inject constructor(private val lectureRepository: LectureRepository) :
    BaseViewModel() {
    val saveResponse = MutableLiveData(false)

    fun saveScoreFeedback(submit: Submit) {
        viewModelScope.launch {
            showProgress()
            saveResponse.value = lectureRepository.postAssignmentFeedBack(submit)
            hideProgress()
        }
    }

}
