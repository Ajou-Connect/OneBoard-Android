package kr.khs.oneboard.api

import kr.khs.oneboard.data.*
import kr.khs.oneboard.data.api.BasicResponseImpl
import kr.khs.oneboard.data.api.Response
import kr.khs.oneboard.data.request.AssignmentUpdateRequestDto
import kr.khs.oneboard.data.request.NoticeUpdateRequestDto
import retrofit2.http.*

interface ApiService {
    @GET("healthCheck")
    suspend fun healthCheck(): Response<Any>

    @POST("auth/login")
    suspend fun login(@Body body: LoginBody): Response<LoginResponse>

    @GET("auth/logout")
    suspend fun logout(): Response<Any>

    @GET("auth/check")
    suspend fun loginCheck(@Header("X-AUTH-TOKEN") token: String): BasicResponseImpl

    @GET("user")
    suspend fun getUserInfo(): Response<User>

    @GET("lectures")
    suspend fun getUserLectures(): Response<List<Lecture>>

    @GET("lecture/{lectureId}")
    suspend fun getDetailLecture(@Path("lectureId") lectureId: Int): Response<Lecture>

    @GET("lecture/{lectureId}/notices")
    suspend fun getNoticeList(@Path("lectureId") lectureId: Int): Response<List<Notice>>

    @POST("lecture/{lectureId}/notice")
    suspend fun postNotice(
        @Path("lectureId") lectureId: Int,
        @Body dto: NoticeUpdateRequestDto
    ): BasicResponseImpl

    @PUT("/lecture/{lectureId}/notice/{noticeId}")
    suspend fun putNotice(
        @Path("lectureId") lectureId: Int,
        @Path("noticeId") noticeId: Int,
        @Body dto: NoticeUpdateRequestDto
    ): BasicResponseImpl

    @DELETE("/lecture/{lectureId}/notice/{noticeId}")
    suspend fun deleteNotice(
        @Path("lectureId") lectureId: Int,
        @Path("noticeId") noticeId: Int
    ): BasicResponseImpl

    @GET("lecture/attendance")
    suspend fun getAttendanceList(lectureId: Int): Response<List<AttendanceStudent>>

    @POST("lecture/attendance")
    suspend fun postAttendance(): Response<Boolean>

    @GET("lecture/{lectureId}/assignments")
    suspend fun getAssignmentList(@Path("lectureId") lectureId: Int): Response<List<Assignment>>

    @POST("lecture/{lectureId}/assignment")
    suspend fun postAssignment(
        @Path("lectureId") lectureId: Int,
        @Body dto: AssignmentUpdateRequestDto
    ): BasicResponseImpl

    @PUT("lecture/{lectureId}/assignment/{assignmentId}")
    suspend fun putAssignment(
        @Path("lectureId") lectureId: Int,
        @Path("assignmentId") assignmentId: Int,
        @Body dto: AssignmentUpdateRequestDto
    ): BasicResponseImpl

    @DELETE("lecture/{lectureId}/assignment/{assignmentId}")
    suspend fun deleteAssignment(
        @Path("lectureId") lectureId: Int,
        @Path("assignmentId") assignmentId: Int
    ): BasicResponseImpl

    // todo lecture/assignment/result부터
}