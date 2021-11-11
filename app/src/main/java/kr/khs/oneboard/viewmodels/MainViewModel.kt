package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.UseCase
import kr.khs.oneboard.data.Lecture
import kr.khs.oneboard.data.User
import kr.khs.oneboard.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val userRepository: UserRepository) :
    BaseViewModel() {
    private val lecture = MutableLiveData<Lecture>()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?>
        get() = _user

    fun setLecture(lecture: Lecture) {
        this.lecture.value = lecture
    }

    fun getLecture() = lecture.value!!

    fun getUserInfo() {
        viewModelScope.launch {
            showProgress()
            val response = userRepository.getUserInfo()
            _user.value = if (response.status == UseCase.Status.SUCCESS)
                response.data
            else
                null
            hideProgress()
        }
    }

    init {
        getUserInfo()
    }
}