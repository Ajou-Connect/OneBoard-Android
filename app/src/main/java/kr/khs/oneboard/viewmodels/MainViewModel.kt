package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.data.Lecture
import kr.khs.oneboard.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    private val _lectures = MutableLiveData<List<Lecture>>()
    val lectures : LiveData<List<Lecture>>
        get() = _lectures

    private fun getLectureList() {
        viewModelScope.launch {
            _lectures.value = userRepository.getUserLectures()
        }
    }

    init {
        getLectureList()
    }
}