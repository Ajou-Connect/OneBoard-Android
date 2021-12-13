package kr.khs.oneboard.repository

import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.Lecture
import kr.khs.oneboard.data.User

interface UserRepository {

    suspend fun getUserInfo(): NetworkResult<User>

    suspend fun getUserLectures(): NetworkResult<List<Lecture>>
}