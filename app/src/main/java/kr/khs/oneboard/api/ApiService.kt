package kr.khs.oneboard.api

import kr.khs.oneboard.data.Lecture
import kr.khs.oneboard.data.LoginBody
import kr.khs.oneboard.data.LoginResponse
import kr.khs.oneboard.data.api.BasicResponseImpl
import kr.khs.oneboard.data.api.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @GET("healthCheck")
    suspend fun healthCheck(): Response<Any>

    @POST("auth/login")
    suspend fun login(@Body body: LoginBody): Response<LoginResponse>

    @GET("auth/logout")
    suspend fun logout(): Response<Any>

    @GET("auth/check")
    suspend fun loginCheck(@Header("X-AUTH-TOKEN") token: String): BasicResponseImpl

    @GET("/user")
    suspend fun getUserInfo(): Response<Any>

    @GET("/user/lectures")
    suspend fun getUserLectures(): Response<List<Lecture>>

    // todo /lectures 부터
}