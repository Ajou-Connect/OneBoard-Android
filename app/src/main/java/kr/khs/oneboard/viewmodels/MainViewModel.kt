package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.data.User
import kr.khs.oneboard.data.api.Response
import kr.khs.oneboard.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val userRepository: UserRepository) :
    BaseViewModel() {
    private val _user = MutableLiveData<Response<User>>()
    val user: LiveData<Response<User>>
        get() = _user

    fun getUserInfo() {
        viewModelScope.launch {
            showProgress()
            _user.value = userRepository.getUserInfo()
            hideProgress()
        }
    }

    init {
        getUserInfo()
    }
}