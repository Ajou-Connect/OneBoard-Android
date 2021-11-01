package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.data.Lesson
import kr.khs.oneboard.repository.LessonRepository
import javax.inject.Inject

@HiltViewModel
class LessonListViewModel @Inject constructor(private val lessonRepository: LessonRepository) :
    BaseViewModel() {

    private val _lessonList = MutableLiveData<List<Lesson>>()
    val lessonList: LiveData<List<Lesson>>
        get() = _lessonList

    fun getLessonList(id: Int) {
        viewModelScope.launch {
            showProgress()
            _lessonList.value = lessonRepository.getLessonList(id)
            hideProgress()
        }
    }
}