package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.UseCase
import kr.khs.oneboard.repository.BasicRepository
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val repository: BasicRepository) : ViewModel() {
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
            val response = repository.healthCheck()
            _healthCheck.value = when (response.status) {
                UseCase.Status.SUCCESS -> {
                    true
                }
                UseCase.Status.ERROR -> {
                    false
                }
            }
        }
    }

    fun checkValidToken(token: String) {
        if (token == "")
            _loginCheck.value = false
        else {
            viewModelScope.launch {
                val response = repository.loginCheck(token)
                _loginCheck.value = when (response.status) {
                    UseCase.Status.SUCCESS -> {
                        true
                    }
                    UseCase.Status.ERROR -> {
                        false
                    }
                }
            }
        }
    }

    fun checkAnimation() {
        _animationCheck.value = true
    }
}