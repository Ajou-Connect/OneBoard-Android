package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.UseCase
import kr.khs.oneboard.repository.SessionRepository
import kr.khs.oneboard.utils.TYPE_PROFESSOR
import kr.khs.oneboard.utils.UserInfoUtil
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class SessionViewModel @Inject constructor(private val repository: SessionRepository) :
    BaseViewModel() {

    private var lectureId by Delegates.notNull<Int>()
    private var lessonId by Delegates.notNull<Int>()

    private val _isLeave = MutableLiveData(false)
    val isLeave: LiveData<Boolean> = _isLeave

    fun postAttendance() {
        viewModelScope.launch {
            val response =
                if (UserInfoUtil.type == TYPE_PROFESSOR)
                    repository.postAttendanceProfessor(lectureId, lessonId)
                else
                    repository.postAttendanceStudent(lectureId, lessonId)

            if (response.status == UseCase.Status.SUCCESS && response.data!!) {
                setErrorMessage(
                    if (UserInfoUtil.type == TYPE_PROFESSOR)
                        "출석 체크 요청을 보냈습니다."
                    else
                        "출석이 되었습니다."
                )
            } else {
                setErrorMessage("오류가 발생했습니다.")
            }
        }
    }

    fun getUnderStanding() {

    }

    fun postUnderStanding() {

    }

    fun getQuiz() {

    }

    fun postQuiz() {

    }

    fun leaveSession() {
        viewModelScope.launch {
            showProgress()
            val response = repository.leaveLesson(lectureId, lessonId)

            if (response.status == UseCase.Status.SUCCESS && response.data!!) {
                _isLeave.value = true
            } else {
                setErrorMessage("다시 시도해주세요.")
            }
            hideProgress()
        }
    }

    fun setId(lectureId: Int, lessonId: Int) {
        this.lectureId = lectureId
        this.lessonId = lessonId
    }
}