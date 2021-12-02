package kr.khs.oneboard.repository

import kr.khs.oneboard.api.ApiService
import javax.inject.Inject
import javax.inject.Named

class SessionRepositoryImpl @Inject constructor(
    @Named("withJWT") private val apiService: ApiService,
): SessionRepository {
}