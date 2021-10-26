package kr.khs.oneboard.api

import kr.khs.oneboard.data.Lecture
import kr.khs.oneboard.data.api.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("healthCheck")
    suspend fun healthCheck(): Response<Any>

    @POST("auth/login")
    suspend fun login(): Response<Any>

    @GET("auth/logout")
    suspend fun logout(): Response<Any>

    @POST("auth/check")
    suspend fun loginCheck(): Response<Any>

    @GET("/user")
    suspend fun getUserInfo(): Response<Any>

    @GET("/user/lectures")
    suspend fun getUserLectures(): Response<List<Lecture>>

    // todo /lectures 부터
}