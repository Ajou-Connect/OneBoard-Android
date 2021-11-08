package kr.khs.oneboard.repository

import kr.khs.oneboard.data.Lecture

interface UserRepository {

    suspend fun getUserInfo()

    suspend fun getUserLectures(): List<Lecture>
}