package kr.khs.oneboard.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String>
        get() = _toastMessage

    protected fun showProgress() {
        _isLoading.value = true
    }

    protected fun hideProgress() {
        _isLoading.value = false
    }

    fun setToastMessage(msg: String = "") {
        _toastMessage.value = msg
    }
}
