package kr.khs.oneboard.repository

import kr.khs.oneboard.data.Lecture
import kr.khs.oneboard.data.User
import kr.khs.oneboard.data.api.Response

interface UserRepository {

    suspend fun getUserInfo(): Response<User>

    suspend fun getUserLectures(): List<Lecture>
}