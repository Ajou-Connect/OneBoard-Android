package kr.khs.oneboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.khs.oneboard.core.BaseViewModel
import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.Lecture
import kr.khs.oneboard.data.User
import kr.khs.oneboard.repository.UserRepository
import kr.khs.oneboard.utils.getDay
import kr.khs.oneboard.utils.toDayOfWeekInt
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val userRepository: UserRepository) :
    BaseViewModel() {
    private val lecture = MutableLiveData<Lecture>()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?>
        get() = _user

    fun setLecture(lecture: Lecture) {
        this.lecture.value = lecture
    }

    fun getLecture() = lecture.value!!

    fun getLectureDayTime(): Pair<String, String>? {
        lecture.value ?: return null

        val lectureDateTime = lecture.value?.defaultDateTime?.split(",") ?: return null

        loop@ for (dateTime in lectureDateTime) {
            return try {
                val split = dateTime.split(" ")
                if (split[0].toDayOfWeekInt() < getDay().toDayOfWeekInt())
                    continue@loop
                else {
                    val date = SimpleDateFormat("MM-dd").format(
                        Calendar.getInstance().timeInMillis + 1000 * 60 * 60 * 24L * (getDay().toDayOfWeekInt() - split[0].toDayOfWeekInt())
                    )
                    Pair(date, split[1])
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        return null
    }

    fun getUserInfo() {
        viewModelScope.launch {
            showProgress()
            val response = userRepository.getUserInfo()
            _user.value = if (response.status == NetworkResult.Status.SUCCESS)
                response.data
            else
                null
            hideProgress()
        }
    }

    init {
        getUserInfo()
    }
}