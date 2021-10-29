package kr.khs.oneboard.data

import com.squareup.moshi.Json

// list가 1이면 바로 확장되도록
data class AttendanceStudent(
    @Json(name = "lesson_id")
    val lessonId: Int,
    @Json(name = "student_id")
    val studentId: Int,
    @Json(name = "student_major")
    val studentMajor: String,
    @Json(name = "student_name")
    val studentName: String,
    val lessonList: List<AttendanceLesson>,
    var isExpand: Boolean
)

data class AttendanceLesson(
    val id: Int,
    val date: String,
    val description: String,
    var check: Int
)

// A 출석, B 지각, F 결석
enum class AttendanceEnum {
    A, B, F
}