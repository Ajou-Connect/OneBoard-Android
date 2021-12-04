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

    // TODO: 2021/12/04 Set live ID
    private var liveId = 0

    private val _isLeave = MutableLiveData(false)
    val isLeave: LiveData<Boolean> = _isLeave

    fun postAttendance() {
        viewModelScope.launch {
            showProgress()
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
            hideProgress()
        }
    }

    fun getUnderStanding(understandingId: Int) {
        viewModelScope.launch {
            showProgress()
            val response = repository.getUnderStanding(lectureId, lessonId, liveId, understandingId)

            if (response.status == UseCase.Status.SUCCESS) {
                val data = response.data!!

                // TODO: 2021/12/04 데이터 처리
            } else {
                setErrorMessage("이해도 평가 정보를 가져오지 못했습니다.")
            }
            hideProgress()
        }
    }

    fun postUnderStanding(select: String) {
        viewModelScope.launch {
            showProgress()
            val response = repository.postUnderStanding(lectureId, lessonId, liveId, select)

            if (response.status == UseCase.Status.SUCCESS) {
                setErrorMessage(
                    if (response.data!!) {
                        "전송 완료"
                    } else {
                        "전송 실패"
                    }
                )
            } else {
                setErrorMessage("이해도 평가 중 오류가 발생했습니다.")
            }
            hideProgress()
        }
    }

    fun getQuiz() {
        viewModelScope.launch {
            showProgress()
            val response = repository.getQuiz(lectureId, lessonId, liveId)

            if (response.status == UseCase.Status.SUCCESS) {
                val data = response.data!!

                // TODO: 2021/12/04 데이터 처리
            } else {
                setErrorMessage("이해도 평가 정보를 가져오지 못했습니다.")
            }
            hideProgress()
        }
    }

    fun postQuiz(quizId: Int, answer: Int) {
        viewModelScope.launch {
            showProgress()
            val response = repository.postQuiz(lectureId, lessonId, liveId, quizId, answer)

            if (response.status == UseCase.Status.SUCCESS) {
                setErrorMessage(
                    if (response.data!!) {
                        "전송 완료"
                    } else {
                        "전송 실패"
                    }
                )
            } else {
                setErrorMessage("퀴즈 정보를 전송하지 못했습니다.")
            }
            hideProgress()
        }
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

// TODO: 2021/12/03 테스트하기 : 줌 세션 나가기(강의자, 학생) 