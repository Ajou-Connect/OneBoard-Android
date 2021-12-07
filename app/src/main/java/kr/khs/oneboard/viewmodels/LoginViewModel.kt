package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.LoginBody
import kr.khs.oneboard.data.LoginResponse
import kr.khs.oneboard.data.api.Response
import kr.khs.oneboard.repository.BasicRepository
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: BasicRepository) :
    BaseViewModel() {
    private val _loginYN = MutableLiveData<Response<LoginResponse>>()
    val loginYN: LiveData<Response<LoginResponse>>
        get() = _loginYN

    val isEmail = MutableLiveData(false)

    val isPassword = MutableLiveData(false)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            showProgress()
            val response = repository.login(LoginBody(email, password))
            if (response.status == NetworkResult.Status.SUCCESS)
                _loginYN.value = response.data!!
            else
                setErrorMessage("알 수 없는 오류가 발생했습니다.\n다시 시도해주세요.")
            hideProgress()
        }
    }

}