package kr.khs.oneboard.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.khs.oneboard.data.Lecture
import kr.khs.oneboard.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    private val lecture = MutableLiveData<Lecture>()

    fun setLecture(lecture: Lecture) {
        this.lecture.value = lecture
    }

    fun getLecture() = lecture.value!!
}