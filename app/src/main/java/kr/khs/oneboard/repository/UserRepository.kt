package kr.khs.oneboard.repository

import kr.khs.oneboard.data.Lecture

interface UserRepository {
    suspend fun healthCheck(): Boolean

    suspend fun login(email: String, password: String): Boolean

    suspend fun loginCheck(token: String): Boolean

    suspend fun logout()

    suspend fun getUserInfo()

    suspend fun getUserLectures(): List<Lecture>
}