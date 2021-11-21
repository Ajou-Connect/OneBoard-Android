package kr.khs.oneboard.data

import com.squareup.moshi.Json

data class GradeStudent(
    @Json(name = "lecture_id")
    val lectureId: Int,
    @Json(name = "student_id")
    val studentId: String,
    @Json(name = "student_name")
    val studentName: String,
    val studentMajor: String,
    val assignmentList: List<Pair<String, Int>>,
    val score: Float?
)