package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.repository.LectureRepository
import javax.inject.Inject

@HiltViewModel
class GradeViewModel @Inject constructor(private val lectureRepository: LectureRepository) :
    BaseViewModel() {
    private val _aRatio = MutableLiveData<Int>()
    val aRatio: LiveData<Int>
        get() = _aRatio

    private val _bRatio = MutableLiveData<Int>()
    val bRatio: LiveData<Int>
        get() = _bRatio

    fun getRatio(lectureId: Int, callback: (Int, Int) -> Unit) {
        viewModelScope.launch {
            showProgress()
            val response = lectureRepository.getGradeRatio(lectureId)
            _aRatio.value = response["A"] ?: 30
            _bRatio.value = response["B"] ?: 70
            callback.invoke(_aRatio.value!!, _bRatio.value!!)
            hideProgress()
        }
    }

    fun setRatio(a: Int? = null, b: Int? = null) {
        a?.let { a ->
            _aRatio.value = a
        }
        b?.let { b ->
            _bRatio.value = b
        }
    }
}
