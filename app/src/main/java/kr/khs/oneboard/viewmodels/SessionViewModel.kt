package kr.khs.oneboard.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.khs.oneboard.repository.SessionRepository
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(private val repository: SessionRepository) :
    ViewModel() {

}