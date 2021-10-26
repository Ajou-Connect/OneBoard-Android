package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: UserRepository) : BaseViewModel() {

    private val _loginYN = MutableLiveData<Boolean>()
    val loginYN: LiveData<Boolean>
        get() = _loginYN

    val isEmail = MutableLiveData(false)

    val isPassword = MutableLiveData(false)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            showProgress()
            _loginYN.value = repository.login(email, password)
            hideProgress()
        }
    }
}