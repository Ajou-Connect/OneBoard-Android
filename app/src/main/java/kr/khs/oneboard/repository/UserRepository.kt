package kr.khs.oneboard.repository

import kr.khs.oneboard.core.UseCase
import kr.khs.oneboard.data.Lecture
import kr.khs.oneboard.data.User

interface UserRepository {

    suspend fun getUserInfo(): UseCase<User>

    suspend fun getUserLectures(): UseCase<List<Lecture>>
}