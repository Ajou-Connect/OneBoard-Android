package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {
    private val _healthCheck = MutableLiveData(false)
    val healthCheck: LiveData<Boolean>
        get() = _healthCheck

    private val _loginCheck = MutableLiveData<Boolean>()
    val loginCheck: LiveData<Boolean>
        get() = _loginCheck

    private val _animationCheck = MutableLiveData(false)
    val animationCheck: LiveData<Boolean>
        get() = _animationCheck

    fun checkHealth() {
        viewModelScope.launch {
            _healthCheck.value = repository.healthCheck()
        }
    }

    fun checkValidToken(token: String) {
        if (token == "")
            _loginCheck.value = true
        else {
            viewModelScope.launch {
                _loginCheck.value = repository.loginCheck(token)
            }
        }
    }

    fun checkAnimation() {
        _animationCheck.value = true
    }
}