package kr.khs.oneboard.api

import kr.khs.oneboard.data.*
import kr.khs.oneboard.data.api.BasicResponseImpl
import kr.khs.oneboard.data.api.Response
import kr.khs.oneboard.data.request.AttendanceUpdateRequestDto
import kr.khs.oneboard.data.request.GradeUpdateRequestDto
import kr.khs.oneboard.data.request.NoticeUpdateRequestDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
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

    @GET("lecture/{lectureId}/attendances")
    suspend fun getAttendanceList(@Path("lectureId") lectureId: Int): Response<List<AttendanceStudent>>

    @PUT("lecture/{lectureId}/attendances")
    suspend fun putAttendance(
        @Path("lectureId") lectureId: Int,
        @Body dto: AttendanceUpdateRequestDto
    ): BasicResponseImpl

    @GET("lecture/{lectureId}/attendances/my")
    suspend fun getMyAttendanceList(@Path("lectureId") lectureId: Int): Response<AttendanceStudent>

    @GET("lecture/{lectureId}/assignments")
    suspend fun getAssignmentList(@Path("lectureId") lectureId: Int): Response<List<Assignment>>

    @Multipart
    @POST("lecture/{lectureId}/assignment")
    suspend fun postAssignment(
        @Path("lectureId") lectureId: Int,
        @Part file: MultipartBody.Part?,
        @PartMap body: HashMap<String, RequestBody>
    ): BasicResponseImpl

    @Multipart
    @PUT("lecture/{lectureId}/assignment/{assignmentId}")
    suspend fun putAssignment(
        @Path("lectureId") lectureId: Int,
        @Path("assignmentId") assignmentId: Int,
        @Part file: MultipartBody.Part?,
        @PartMap body: HashMap<String, RequestBody>
    ): BasicResponseImpl

    @DELETE("lecture/{lectureId}/assignment/{assignmentId}")
    suspend fun deleteAssignment(
        @Path("lectureId") lectureId: Int,
        @Path("assignmentId") assignmentId: Int
    ): BasicResponseImpl

    @GET("lecture/{lectureId}/assignment/{assignmentId}/submit")
    suspend fun getMyAssignmentSubmitInfo(
        @Path("lectureId") lectureId: Int,
        @Path("assignmentId") assignmentId: Int
    ): Response<Submit>

    @POST("lecture/assignment/result")
    suspend fun postAssignmentFeedBack(): Response<Boolean>

    @GET("lecture/{lectureId}/assignment/{assignmentId}/submits")
    suspend fun getSubmitAssignmentList(
        @Path("lectureId") lectureId: Int,
        @Path("assignmentId") assignmentId: Int
    ): Response<List<Submit>>

    @GET("lecture/{lectureId}/grade")
    suspend fun getStudentOwnGrade(@Path("lectureId") lectureId: Int): Response<GradeStudent>

    @GET("lecture/{lectureId}/grade/{studentId}")
    suspend fun getStudentGrade(
        @Path("lectureId") lectureId: Int,
        @Path("studentId") studentId: Int
    ): Response<GradeStudent>

    @POST("lecture/{lectureId}/grade/{studentId}")
    suspend fun postStudentGrade(
        @Path("lectureId") lectureId: Int,
        @Path("studentId") studentId: Int,
        @Body body: GradeUpdateRequestDto
    ): BasicResponseImpl

    @GET("lecture/{lectureId}/grade/list")
    suspend fun getGradeList(@Path("lectureId") lectureId: Int): Response<List<GradeStudent>>

    @GET("lecture/{lectureId}/grade/ratio")
    suspend fun getGradeRatio(@Path("lectureId") lectureId: Int): Response<GradeRatio>

    @POST("lecture/{lectureId}/grade/ratio")
    suspend fun postGradeRatio(
        @Path("lectureId") lectureId: Int,
        @Body gradeRatio: GradeRatio
    ): BasicResponseImpl

    @GET("lecture/{lectureId}/lessons")
    suspend fun getLessonList(@Path("lectureId") lectureId: Int): Response<List<Lesson>>

    @GET("lecture/{lectureId}/lesson/{lessonId}")
    suspend fun getLesson(
        @Path("lectureId") lectureId: Int,
        @Path("lessonId") lessonId: Int
    ): Response<Lesson>

    @Multipart
    @POST("lecture/{lectureId}/lesson")
    suspend fun postLesson(
        @Path("lectureId") lectureId: Int,
        @Part file: MultipartBody.Part?,
        @PartMap body: HashMap<String, RequestBody>
    ): BasicResponseImpl

    @Multipart
    @PUT("lecture/{lectureId}/lesson/{lessonId}")
    suspend fun putLesson(
        @Path("lectureId") lectureId: Int,
        @Path("lessonId") lessonId: Int,
        @Part file: MultipartBody.Part?,
        @PartMap body: HashMap<String, RequestBody>
    ): BasicResponseImpl

    @DELETE("lecture/{lectureId}/lesson/{lessonId}")
    suspend fun deleteLesson(
        @Path("lectureId") lectureId: Int,
        @Path("lessonId") lessonId: Int
    ): BasicResponseImpl

    @GET("lecture/{lectureId}/lesson/default")
    suspend fun getDefaultLessonInfo(
        @Path("lectureId") lectureId: Int
    ): Response<LessonDefaultInfo>


    // TODO: 2021/12/03 수업 분석 차트 확인
    @GET("lecture/{lectureId}/lesson/{lessonId}/analysis")
    suspend fun getLessonAnalysis(
        @Path("lectureId") lectureId: Int,
        @Path("lessonId") lessonId: Int
    )

    // TODO: 2021/12/03 비대면 수업 생성
    @GET("lecture/{lectureId}/lesson/{lessonId}/live/open")
    suspend fun openLesson(
        @Path("lectureId") lectureId: Int,
        @Path("lessonId") lessonId: Int
    ): BasicResponseImpl

    // TODO: 2021/12/03 비대면 수업 입장
    @GET("lecture/{lectureId}/lesson/{lessonId}/live/entrance")
    suspend fun enterLesson(
        @Path("lectureId") lectureId: Int,
        @Path("lessonId") lessonId: Int
    ): BasicResponseImpl

    // TODO: 2021/12/03 비대면 수업 종료
    @GET("lecture/{lectureId}/lesson/{lessonId}/live/clos")
    suspend fun leaveLesson(
        @Path("lectureId") lectureId: Int,
        @Path("lessonId") lessonId: Int
    ): BasicResponseImpl

    // TODO: 2021/12/03 출석 체크 요청 - 교수
    @GET("lecture/{lectureId}/lesson/{lessonId}/live/attendance/professor")
    suspend fun postAttendanceProfessor(
        @Path("lectureId") lectureId: Int,
        @Path("lessonId") lessonId: Int
    ): BasicResponseImpl

    // TODO: 2021/12/03 출석 체크 요청 - 학생
    @POST("lecture/{lectureId}/lesson/{lessonId}/live/attendance/student")
    suspend fun postAttendanceStudent(
        @Path("lectureId") lectureId: Int,
        @Path("lessonId") lessonId: Int
    ): BasicResponseImpl

    // TODO: 2021/12/03 이해도 평가 요청
    @POST("lecture/{lectureId}/lesson/{lessonId}/live/{liveId}/understanding/professor")
    suspend fun postUnderStanding(
        @Path("lectureId") lectureId: Int,
        @Path("lessonId") lessonId: Int,
        @Path("liveId") liveId: Int,
        @Body body: JSONObject
    ): BasicResponseImpl

    // TODO: 2021/12/03 이해도 평가 결과 확인
    @GET("lecture/{lectureId}/lesson/{lessonId}/live/{liveId}/understanding/{understandingId}/professor")
    suspend fun getUnderStanding(
        @Path("lectureId") lectureId: Int,
        @Path("lessonId") lessonId: Int,
        @Path("liveId") liveId: Int,
        @Path("understandingId") underStandingId: Int
    ): BasicResponseImpl

    // TODO: 2021/12/03 퀴즈 요청 확인
    @GET("lecture/{lectureId}/lesson/{lessonId}/live/{liveId}/quiz/student")
    suspend fun getQuiz(
        @Path("lectureId") lectureId: Int,
        @Path("lessonId") lessonId: Int,
        @Path("liveId") liveId: Int,
    ): BasicResponseImpl

    // TODO: 2021/12/03 퀴즈 요청 응답
    @POST("lecture/{lectureId}/lesson/{lessonId}/live/{liveId}/quiz/{quizId}/student")
    suspend fun postQuiz(
        @Path("lectureId") lectureId: Int,
        @Path("lessonId") lessonId: Int,
        @Path("liveId") liveId: Int,
        @Path("quizId") quizId: Int,
        @Body body: JSONObject
    ): BasicResponseImpl
}