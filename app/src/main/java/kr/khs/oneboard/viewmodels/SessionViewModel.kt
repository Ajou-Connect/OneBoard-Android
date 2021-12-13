package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.Quiz
import kr.khs.oneboard.data.StudentQuizResponse
import kr.khs.oneboard.data.request.QuizRequestDto
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
    private var understandingId by Delegates.notNull<Int>()
    private var sessionName by Delegates.notNull<String>()
    private var latestQuizId by Delegates.notNull<Int>()

    // TODO: 2021/12/04 Set live ID
    private var liveId = 0

    private val _isLeave = MutableLiveData(false)
    val isLeave: LiveData<Boolean> = _isLeave

    val studentQuizResponse = MutableLiveData<StudentQuizResponse>()

    val professorQuizResponse = MutableLiveData<Quiz>()

    fun postAttendance() {
        viewModelScope.launch {
            showProgress()
            val response =
                if (UserInfoUtil.type == TYPE_PROFESSOR)
                    repository.postAttendanceProfessor(lectureId, lessonId, sessionName)
                else
                    repository.postAttendanceStudent(lectureId, lessonId, sessionName)

            if (response.status == NetworkResult.Status.SUCCESS && response.data!!) {
                setToastMessage(
                    if (UserInfoUtil.type == TYPE_PROFESSOR)
                        "출석 체크 요청을 보냈습니다."
                    else
                        "출석이 되었습니다."
                )
            } else {
                setToastMessage("오류가 발생했습니다.")
            }
            hideProgress()
        }
    }

    fun getUnderStandingProfessor() {
        viewModelScope.launch {
            showProgress()
            val response =
                repository.getUnderStandingProfessor(lectureId, lessonId, understandingId)

            if (response.status == NetworkResult.Status.SUCCESS) {
                val data = response.data!!

                // TODO: 2021/12/04 데이터 처리
            } else {
                setToastMessage("이해도 평가 정보를 가져오지 못했습니다.")
            }
            hideProgress()
        }
    }

    fun postUnderStandingProfessor() {
        viewModelScope.launch {
            showProgress()
            val response = repository.postUnderStandingProfessor(lectureId, lessonId)

            if (response.status == NetworkResult.Status.SUCCESS) {
                setToastMessage("전송 완료")
            } else {
                setToastMessage("이해도 평가 중 오류가 발생했습니다.")
            }

            hideProgress()
        }
    }

    fun postUnderStandingStudent(select: String) {
        viewModelScope.launch {
            showProgress()
            val response =
                repository.postUnderStandingStudent(lectureId, lessonId, understandingId, select)

            if (response.status == NetworkResult.Status.SUCCESS) {
                setToastMessage("전송 완료")
            } else {
                setToastMessage("이해도 평가 중 오류가 발생했습니다.")
            }

            hideProgress()
        }
    }

    fun postQuizProfessor(quizRequestDto: QuizRequestDto) {
        viewModelScope.launch {
            showProgress()
            val response = repository.postQuizProfessor(lectureId, lessonId, quizRequestDto)

            if (response.status == NetworkResult.Status.SUCCESS)
                latestQuizId = response.data!!
            else
                setToastMessage("퀴즈 출제 중 오류가 발생하였습니다.")

            hideProgress()
        }
    }

    fun getQuizProfessor() {
        viewModelScope.launch {
            showProgress()
            val response = repository.getQuizProfessor(lectureId, lessonId, latestQuizId)

            if (response.status == NetworkResult.Status.SUCCESS) {
                professorQuizResponse.value = response.data!!
            } else {
                setToastMessage("퀴즈 정보를 가져오지 못했습니다.")
            }
        }
    }

    fun getQuizStudent(quizId: Int) {
        viewModelScope.launch {
            showProgress()
            latestQuizId = quizId
            val response = repository.getQuizStudent(lectureId, lessonId, quizId)

            if (response.status == NetworkResult.Status.SUCCESS) {
                studentQuizResponse.value = response.data!!
            } else {
                setToastMessage("이해도 평가 정보를 가져오지 못했습니다.")
            }
            hideProgress()
        }
    }

    fun postQuizStudent(quizId: Int, answer: Int) {
        viewModelScope.launch {
            showProgress()
            val response = repository.postQuizStudent(lectureId, lessonId, quizId, answer)

            if (response.status == NetworkResult.Status.SUCCESS) {
                setToastMessage(
                    if (response.data!!) {
                        "전송 완료"
                    } else {
                        "전송 실패"
                    }
                )
            } else {
                setToastMessage("퀴즈 정보를 전송하지 못했습니다.")
            }
            hideProgress()
        }
    }

    fun leaveSession() {
        viewModelScope.launch {
            showProgress()
            val response = repository.leaveLesson(lectureId, lessonId, sessionName)

            if (response.status == NetworkResult.Status.SUCCESS && response.data!!) {
                _isLeave.value = true
            } else {
                setToastMessage("다시 시도해주세요.")
            }
            hideProgress()
        }
    }

    fun setId(lectureId: Int, lessonId: Int, sessionName: String) {
        this.lectureId = lectureId
        this.lessonId = lessonId
        this.sessionName = sessionName
    }
}
