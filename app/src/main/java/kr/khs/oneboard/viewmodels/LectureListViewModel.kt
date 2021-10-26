package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.data.Lecture
import kr.khs.oneboard.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class LectureListViewModel @Inject constructor(private val userRepository: UserRepository) :
    BaseViewModel() {
    private val _lectures = MutableLiveData<List<Lecture>>()
    val lectures: LiveData<List<Lecture>>
        get() = _lectures

    private fun getLectureList() {
        viewModelScope.launch {
            showProgress()
            _lectures.value = userRepository.getUserLectures()
            hideProgress()
        }
    }

    init {
        getLectureList()
    }
}