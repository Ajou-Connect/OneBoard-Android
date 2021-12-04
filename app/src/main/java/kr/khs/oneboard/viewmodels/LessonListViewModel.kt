package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.UseCase
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
            val response = lessonRepository.getLessonList(id)
            if (response.status == UseCase.Status.SUCCESS) {
                _lessonList.value = response.data ?: listOf()
            } else {
                setErrorMessage(response.message!!)
            }
            hideProgress()
        }
    }

    fun deleteItem(lectureId: Int, lessonId: Int) {
        viewModelScope.launch {
            showProgress()
            val response = lessonRepository.deleteLesson(lectureId, lessonId)

            if (response.status == UseCase.Status.SUCCESS && response.data!!)
                _lessonList.value = _lessonList.value!!.filter { it.lessonId != lessonId }
            else
                setErrorMessage(response.message!!)

            hideProgress()
        }
    }
}