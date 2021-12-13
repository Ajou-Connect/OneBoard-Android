package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.Lesson
import kr.khs.oneboard.data.QuizAnalysis
import kr.khs.oneboard.data.UnderstandingAnalysis
import kr.khs.oneboard.repository.LessonRepository
import kr.khs.oneboard.utils.TYPE_NON_FACE_TO_FACE
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(private val repository: LessonRepository) :
    BaseViewModel() {

    private val _lessonList = MutableLiveData<List<Lesson>>()
    val lessonList: LiveData<List<Lesson>>
        get() = _lessonList

    private val _understandingList = MutableLiveData<List<UnderstandingAnalysis>>()
    val understandingList: LiveData<List<UnderstandingAnalysis>>
        get() = _understandingList

    private val _quizList = MutableLiveData<List<QuizAnalysis>>()
    val quizList: LiveData<List<QuizAnalysis>>
        get() = _quizList

    fun getLessonList(lectureId: Int) {
        viewModelScope.launch {
            showProgress()

            val response = repository.getLessonList(lectureId)

            if (response.status == NetworkResult.Status.SUCCESS) {
                _lessonList.value = response.data!!.filter {
                    it.type == TYPE_NON_FACE_TO_FACE
                }
            } else
                setToastMessage("수업 목록을 불러오지 못했습니다.")

            hideProgress()
        }
    }

    fun getAnalysisInfo(lectureId: Int, lessonId: Int) {
        viewModelScope.launch {
            showProgress()
            val response = repository.getAnalysis(lectureId, lessonId)

            if (response.status == NetworkResult.Status.SUCCESS) {
                _understandingList.value = response.data!!.understandAnalysisDtoList
                _quizList.value = response.data.quizAnalysisDtoList
            } else {
                setToastMessage("분석 정보를 불러오지 못했습니다.")
            }

            hideProgress()
        }
    }
}
